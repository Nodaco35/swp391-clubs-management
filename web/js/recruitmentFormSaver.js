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
        
        if (!this.form) {
            console.error(`FormSaver: Không tìm thấy form với ID "${formId}"`);
            return;
        }
        
        this.storageKey = `${this.keyPrefix}_${window.location.pathname}`;
        
        // Tự động lưu khi thay đổi giá trị các trường
        this.setupAutoSave();
        
        // Kiểm tra và khôi phục dữ liệu đã lưu nếu có
        this.restoreFormData();
    }
    
    /**
     * Thiết lập tự động lưu khi thay đổi giá trị các trường
     */
    setupAutoSave() {
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
    }
    
    /**
     * Lưu dữ liệu form vào localStorage
     */
    saveFormData() {
        if (!this.form) return;
        
        const formData = {};
        const inputs = this.form.querySelectorAll('input, textarea, select');
        
        // Nếu là form chỉnh sửa, lưu ID để phân biệt
        const idField = this.form.querySelector('input[name="recruitmentId"]');
        const formMode = this.form.getAttribute('data-mode') || 'create';
        
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
        formData['_mode'] = formMode;
        if (idField && idField.value) {
            formData['_id'] = idField.value;
            this.storageKey = `${this.keyPrefix}_${formMode}_${idField.value}`;
        }
        
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
        if (!this.form) return false;
        
        try {
            // Nếu là form chỉnh sửa, kiểm tra ID để lấy dữ liệu đúng
            const idField = this.form.querySelector('input[name="recruitmentId"]');
            const formMode = this.form.getAttribute('data-mode') || 'create';
            
            if (idField && idField.value) {
                // Nếu có ID, sử dụng key riêng cho form edit
                const editStorageKey = `${this.keyPrefix}_${formMode}_${idField.value}`;
                const savedDataStr = localStorage.getItem(editStorageKey);
                if (savedDataStr) {
                    this.storageKey = editStorageKey;
                    return this._applyStoredData(savedDataStr);
                }
            }
            
            // Nếu không có ID hoặc không tìm thấy dữ liệu với ID, sử dụng key mặc định
            const savedDataStr = localStorage.getItem(this.storageKey);
            if (!savedDataStr) return false;
            
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
            
            // Kiểm tra mode form
            const currentFormMode = this.form.getAttribute('data-mode') || 'create';
            const savedFormMode = savedData['_mode'] || 'create';
            const currentFormId = this.form.querySelector('input[name="recruitmentId"]')?.value;
            const savedFormId = savedData['_id'];
            
            // Nếu mode khác nhau và có ID, không khôi phục
            if (currentFormMode === 'edit' && currentFormId && 
                (savedFormMode !== currentFormMode || savedFormId !== currentFormId)) {
                console.log('FormSaver: Mode hoặc ID form đã thay đổi, không khôi phục dữ liệu');
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
}
