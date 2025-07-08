/**
 * FormSaver - Tiện ích lưu dữ liệu form vào localStorage và khôi phục khi cần
 * Sử dụng để lưu dữ liệu đã điền trong trường hợp chưa lưu hoặc trang bị reload
 */
 
class FormSaver {
    /**
     * Khởi tạo với ID form và prefix key để lưu trong localStorage
     * @param {string} formId - ID của form cần lưu dữ liệu
     * @param {string} keyPrefix - Tiền tố cho key trong localStorage
     * @param {number} expirationMinutes - Thời gian hết hạn (phút), mặc định là 30 phút
     */
    constructor(formId, keyPrefix, expirationMinutes = 30) {
        this.formId = formId;
        this.keyPrefix = keyPrefix;
        this.expirationMinutes = expirationMinutes;
        this.form = document.getElementById(formId);
        this.isActive = false;
        
        if (!this.form) {
            console.error(`FormSaver: Không tìm thấy form với ID "${formId}"`);
            return;
        }
        
        // Kiểm tra mode của form - chỉ hoạt động với create mode
        const formMode = this.form.getAttribute('data-mode');
        if (formMode !== 'create') {
            console.log(`FormSaver: Form đang ở mode "${formMode}" - FormSaver sẽ không hoạt động`);
            return;
        }
        
        this.isActive = true;
        this.storageKey = `${this.keyPrefix}_${window.location.pathname}`;
        
        console.log(`FormSaver: Khởi tạo thành công cho form ${formId} ở mode create`);
        
        // Tự động lưu khi thay đổi giá trị các trường
        this.setupAutoSave();
        
        // KHÔNG tự động khôi phục dữ liệu trong constructor
        // Để người dùng quyết định thông qua showRestorePrompt
    }
    
    /**
     * Thiết lập tự động lưu khi thay đổi giá trị các trường
     */
    setupAutoSave() {
        if (!this.isActive) {
            console.log('FormSaver: Không thiết lập auto-save vì FormSaver không active');
            return;
        }
        
        const inputs = this.form.querySelectorAll('input, textarea, select');
        
        inputs.forEach(input => {
            input.addEventListener('change', () => this.saveFormData());
            
            // Đối với textarea và text input, lưu khi blur (rời khỏi trường)
            if (input.tagName === 'TEXTAREA' || 
                (input.tagName === 'INPUT' && 
                 (input.type === 'text' || input.type === 'number' || input.type === 'date'))) {
                input.addEventListener('blur', () => this.saveFormData());
            }
        });
        
        console.log(`FormSaver: Đã thiết lập auto-save cho ${inputs.length} trường input`);
    }
    
    /**
     * Lưu dữ liệu form vào localStorage
     */
    saveFormData() {
        if (!this.isActive || !this.form) {
            return;
        }
        
        const formData = {};
        const inputs = this.form.querySelectorAll('input, textarea, select');
        
        // Lấy giá trị của các trường
        inputs.forEach(input => {
            if (input.name && !input.name.includes('[]')) {
                if (input.type === 'checkbox' || input.type === 'radio') {
                    if (input.checked) {
                        formData[input.name] = input.value;
                    }
                } else {
                    formData[input.name] = input.value;
                }
            }
        });
        
        // Thêm thông tin thêm
        formData['_timestamp'] = new Date().getTime();
        formData['_mode'] = 'create'; // Chỉ lưu cho create mode
        
        try {
            localStorage.setItem(this.storageKey, JSON.stringify(formData));
            console.log(`FormSaver: Đã lưu dữ liệu form ${this.formId} vào localStorage`);
        } catch (e) {
            console.error('FormSaver: Lỗi khi lưu dữ liệu form', e);
        }
    }
    
    /**
     * Khôi phục dữ liệu form từ localStorage
     * @returns {boolean} true nếu đã khôi phục dữ liệu thành công
     */
    restoreFormData() {
        if (!this.isActive || !this.form) {
            console.log('FormSaver: Không khôi phục dữ liệu vì FormSaver không active');
            return false;
        }
        
        try {
            const savedDataStr = localStorage.getItem(this.storageKey);
            if (!savedDataStr) {
                console.log('FormSaver: Không có dữ liệu đã lưu để khôi phục');
                return false;
            }
            
            return this._applyStoredData(savedDataStr);
            
        } catch (e) {
            console.error('FormSaver: Lỗi khi khôi phục dữ liệu form', e);
            return false;
        }
    }
    
    /**
     * Áp dụng dữ liệu đã lưu vào form
     * @param {string} savedDataStr - Chuỗi JSON dữ liệu đã lưu
     * @returns {boolean} true nếu đã áp dụng dữ liệu thành công
     */
    _applyStoredData(savedDataStr) {
        try {
            const savedData = JSON.parse(savedDataStr);
            
            // Kiểm tra thời gian hết hạn
            const now = new Date().getTime();
            const savedTime = savedData['_timestamp'] || 0;
            const elapsedMinutes = (now - savedTime) / (60 * 1000);
            
            if (elapsedMinutes > this.expirationMinutes) {
                console.log(`FormSaver: Dữ liệu đã lưu đã hết hạn (${elapsedMinutes.toFixed(1)} phút)`);
                localStorage.removeItem(this.storageKey);
                return false;
            }
            
            // Kiểm tra mode form - chỉ khôi phục cho create mode
            const savedFormMode = savedData['_mode'] || 'create';
            if (savedFormMode !== 'create') {
                console.log('FormSaver: Dữ liệu đã lưu không phải từ create mode, không khôi phục');
                return false;
            }
            
            // Điền dữ liệu vào form
            Object.keys(savedData).forEach(key => {
                // Bỏ qua các trường đặc biệt bắt đầu bằng _
                if (key.startsWith('_')) return;
                
                const input = this.form.querySelector(`[name="${key}"]`);
                if (input) {
                    if (input.type === 'checkbox' || input.type === 'radio') {
                        input.checked = input.value === savedData[key];
                    } else {
                        input.value = savedData[key];
                    }
                }
            });
            
            console.log(`FormSaver: Đã khôi phục dữ liệu form ${this.formId} từ localStorage`);
            return true;
            
        } catch (e) {
            console.error('FormSaver: Lỗi khi áp dụng dữ liệu đã lưu', e);
            return false;
        }
    }
    
    /**
     * Xóa dữ liệu đã lưu trong localStorage
     */
    clearSavedData() {
        if (!this.isActive) {
            console.log('FormSaver: Không xóa dữ liệu vì FormSaver không active');
            return;
        }
        
        try {
            localStorage.removeItem(this.storageKey);
            console.log(`FormSaver: Đã xóa dữ liệu form ${this.formId} khỏi localStorage`);
        } catch (e) {
            console.error('FormSaver: Lỗi khi xóa dữ liệu form', e);
        }
    }
    
    /**
     * Hiển thị thông báo khôi phục dữ liệu
     * @param {string} message - Thông báo hiển thị
     * @param {function} onRestore - Callback khi người dùng chọn khôi phục
     * @param {function} onDiscard - Callback khi người dùng chọn bỏ qua
     */
    showRestorePrompt(message, onRestore, onDiscard) {
        const promptId = 'form-restore-prompt';
        let promptElement = document.getElementById(promptId);
        
        if (!promptElement) {
            promptElement = document.createElement('div');
            promptElement.id = promptId;
            promptElement.className = 'form-restore-prompt';
            promptElement.style.position = 'fixed';
            promptElement.style.top = '20px';
            promptElement.style.right = '20px';
            promptElement.style.backgroundColor = '#fff3cd';
            promptElement.style.color = '#856404';
            promptElement.style.border = '1px solid #ffeeba';
            promptElement.style.padding = '15px';
            promptElement.style.borderRadius = '5px';
            promptElement.style.boxShadow = '0 2px 5px rgba(0, 0, 0, 0.2)';
            promptElement.style.zIndex = '1000';
            
            document.body.appendChild(promptElement);
        }
        
        promptElement.innerHTML = `
            <div style="margin-bottom: 10px;">${message}</div>
            <div style="display: flex; justify-content: space-between;">
                <button id="${promptId}-restore" style="background-color: #28a745; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">Khôi phục</button>
                <button id="${promptId}-discard" style="background-color: #dc3545; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer; margin-left: 10px;">Bỏ qua</button>
            </div>
        `;
        
        // Xử lý sự kiện cho các nút
        document.getElementById(`${promptId}-restore`).addEventListener('click', () => {
            if (typeof onRestore === 'function') onRestore();
            promptElement.remove();
        });
        
        document.getElementById(`${promptId}-discard`).addEventListener('click', () => {
            if (typeof onDiscard === 'function') onDiscard();
            promptElement.remove();
        });
    }
    
    /**
     * Utility method để xóa dữ liệu FormSaver cũ từ localStorage (static method)
     * @param {string} keyPrefix - Tiền tố key cần xóa
     * @param {string} pathname - Đường dẫn hiện tại
     */
    static clearOldFormData(keyPrefix, pathname = null) {
        try {
            const targetPath = pathname || window.location.pathname;
            const possibleKeys = [
                `${keyPrefix}_${targetPath}`,
                `${keyPrefix}_create`,
                `${keyPrefix}_edit`
            ];
            
            let cleared = 0;
            possibleKeys.forEach(key => {
                if (localStorage.getItem(key)) {
                    localStorage.removeItem(key);
                    console.log(`FormSaver: Đã xóa dữ liệu cũ với key: ${key}`);
                    cleared++;
                }
            });
            
            if (cleared > 0) {
                console.log(`FormSaver: Đã xóa ${cleared} dữ liệu FormSaver cũ`);
            } else {
                console.log('FormSaver: Không có dữ liệu FormSaver cũ cần xóa');
            }
        } catch (e) {
            console.error('FormSaver: Lỗi khi xóa dữ liệu FormSaver cũ', e);
        }
    }
}
