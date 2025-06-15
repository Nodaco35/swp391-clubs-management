// applicationForm.js - Xử lý biểu mẫu đăng ký

document.addEventListener('DOMContentLoaded', () => {
    console.log('ApplicationForm: DOM đã tải xong, khởi tạo biểu mẫu...');

    // Thiết lập chức năng xem trước tệp đã chọn
    setupFileInputPreviews();
    
    // Xác minh biểu mẫu trước khi gửi
    const form = document.getElementById('applicationForm');
    if (form) {
        console.log('ApplicationForm: Đã tìm thấy biểu mẫu, thiết lập xử lý gửi');
        form.addEventListener('submit', (e) => {
            e.preventDefault();
            validateForm();
        });
    } else {
        console.warn('ApplicationForm: Không tìm thấy phần tử biểu mẫu!');
    }
    
    // Thêm kiểu dáng xác nhận cho trường bắt buộc
    setupRequiredFields();
    
    console.log('ApplicationForm: Khởi tạo hoàn tất');
});

/**
 * Thiết lập chức năng xem trước cho các trường tải lên tệp
 */
function setupFileInputPreviews() {
    const fileInputs = document.querySelectorAll('.form-file-input');
    fileInputs.forEach(input => {
        input.addEventListener('change', function() {
            const preview = this.parentElement.querySelector('.file-input-preview');
            if (!preview) return;
            
            // Xóa bản xem trước trước đó
            preview.innerHTML = '';
            
            if (this.files && this.files[0]) {
                const file = this.files[0];
                console.log(`Debug: File đã chọn - ${file.name}, loại: ${file.type}, kích thước: ${file.size} bytes`);

                // Kiểm tra xem đó có phải là tệp hình ảnh không
                if (file.type.match('image.*')) {
                    const img = document.createElement('img');
                    img.src = URL.createObjectURL(file);
                    img.onload = function() {
                        URL.revokeObjectURL(this.src);
                    }
                    preview.appendChild(img);
                } else {
                    // Đối với các tệp không phải hình ảnh, chỉ hiển thị tên tệp
                    const fileInfo = document.createElement('div');
                    fileInfo.className = 'file-info';
                    fileInfo.textContent = file.name;
                    preview.appendChild(fileInfo);
                }
            }
        });
    });
}

/**
 * Thiết lập hiệu ứng trực quan cho các trường bắt buộc
 */
function setupRequiredFields() {
    // Thêm đánh dấu trường bắt buộc
    const requiredInputs = document.querySelectorAll('[required]');
    console.log(`Debug: Đã tìm thấy ${requiredInputs.length} trường bắt buộc`);

    // Clear any existing error messages on page load
    document.querySelectorAll('.error-message').forEach(msg => msg.remove());

    requiredInputs.forEach(input => {
        input.addEventListener('invalid', function() {
            this.classList.add('input-error');
            
            // Find the parent form-group
            const formGroup = this.closest('.form-group');
            if (formGroup) {
                // Add visual indication to the label as well
                const label = formGroup.querySelector('.form-label');
                if (label && !label.classList.contains('required-label')) {
                    label.classList.add('required-label');
                }
            }
        });
        
        input.addEventListener('input', function() {
            if (this.validity.valid) {
                this.classList.remove('input-error');
            }
        });
    });
}

/**
 * Kiểm tra hợp lệ biểu mẫu trước khi gửi
 */
function validateForm() {
    console.log('Debug: Đang xác minh biểu mẫu...');
    const form = document.getElementById('applicationForm');
    if (!form) return;
    
    // Xóa tất cả thông báo lỗi hiện có trước khi xác minh lại
    form.querySelectorAll('.error-message').forEach(msg => msg.remove());
    form.querySelectorAll('.input-error').forEach(field => field.classList.remove('input-error'));
    
    // Thực hiện xác minh tùy chỉnh cho đầu vào tệp vì chúng không hoạt động với xác minh HTML5
    const requiredFileInputs = form.querySelectorAll('input[type="file"][required]');
    let allFilesValid = true;
    
    requiredFileInputs.forEach(input => {
        if (!input.files || input.files.length === 0) {
            input.classList.add('input-error');
            const errorMsg = document.createElement('div');
            errorMsg.className = 'error-message';
            errorMsg.textContent = 'Vui lòng chọn một tệp';
            
            // Hiển thị thông báo lỗi bên dưới ô nhập liệu
            const container = input.closest('.file-input-container') || input.parentElement;
            container.appendChild(errorMsg);
            allFilesValid = false;
            console.log(`Debug: Lỗi trường tệp bắt buộc '${input.name}'`);
        }
    });
    
    // Xác minh bổ sung cho các nút radio có thuộc tính bắt buộc
    const radioGroups = {};
    form.querySelectorAll('input[type="radio"][required]').forEach(radio => {
        if (!radioGroups[radio.name]) {
            radioGroups[radio.name] = { elements: [], isValid: false };
        }
        radioGroups[radio.name].elements.push(radio);
        if (radio.checked) {
            radioGroups[radio.name].isValid = true;
        }
    });

    // Kiểm tra xem có nhóm radio nào không hợp lệ không
    Object.keys(radioGroups).forEach(groupName => {
        const group = radioGroups[groupName];
        if (!group.isValid) {
            group.elements[0].classList.add('input-error');
            const radioContainer = group.elements[0].closest('.radio-options');
            if (radioContainer) {
                const errorMsg = document.createElement('div');
                errorMsg.className = 'error-message';
                errorMsg.textContent = 'Vui lòng chọn một tùy chọn';
                radioContainer.appendChild(errorMsg);
                allFilesValid = false;
                console.log(`Debug: Lỗi trường radio bắt buộc '${groupName}'`);
            }
        }
    });
    
    // Xác minh cho các nhóm checkbox có ít nhất một phần tử có thuộc tính bắt buộc
    const checkboxGroups = {};
    form.querySelectorAll('input[type="checkbox"][required]').forEach(checkbox => {
        if (!checkboxGroups[checkbox.name]) {
            checkboxGroups[checkbox.name] = { elements: [], isValid: false };
        }
        checkboxGroups[checkbox.name].elements.push(checkbox);
        
        // Nếu ít nhất một checkbox được chọn, nhóm là hợp lệ
        if (checkbox.checked) {
            checkboxGroups[checkbox.name].isValid = true;
        }
    });
    
    // Kiểm tra xem có nhóm checkbox nào không hợp lệ không
    Object.keys(checkboxGroups).forEach(groupName => {
        const group = checkboxGroups[groupName];
        // Nếu không có checkbox nào được chọn và trường là bắt buộc
        if (!group.isValid) {
            group.elements[0].classList.add('input-error');
            const checkboxContainer = group.elements[0].closest('.checkbox-options');
            if (checkboxContainer) {
                const errorMsg = document.createElement('div');
                errorMsg.className = 'error-message';
                errorMsg.textContent = 'Vui lòng chọn ít nhất một tùy chọn';
                checkboxContainer.appendChild(errorMsg);
                allFilesValid = false;
                console.log(`Debug: Lỗi nhóm checkbox bắt buộc '${groupName}'`);
            }
        }
    });

    // Kiểm tra xác minh HTML5
    if (!form.checkValidity() || !allFilesValid) {
        form.reportValidity();
        console.log('Debug: Biểu mẫu không hợp lệ, không được gửi');
        return;
    }
    
    // Nếu tất cả kiểm tra đều thành công, gửi biểu mẫu
    console.log('Debug: Biểu mẫu đã qua xác minh, bắt đầu gửi dữ liệu');
    submitApplication();
}

/**
 * Thu thập và gửi dữ liệu biểu mẫu
 */
function submitApplication() {
    const form = document.getElementById('applicationForm');
    const data = {};
    let hasFileInputs = false;
    let pendingFileUploads = 0;

    console.log('Debug: Đang thu thập dữ liệu từ biểu mẫu...');

    // Hiển thị chỉ báo đang tải
    const submitBtn = document.getElementById('submitBtn');
    const loadingIndicator = document.getElementById('loadingIndicator');
    if (submitBtn) submitBtn.disabled = true;
    if (loadingIndicator) loadingIndicator.style.display = 'flex';

    // Thu thập giá trị cho mỗi câu hỏi
    Array.from(form.elements).forEach(el => {
        if (!el.name || !el.name.startsWith('ans_')) return;
        const key = el.name.substring(4);

        switch (el.type) {
            case 'checkbox':
                // Khởi tạo object cho checkbox nếu chưa có
                if (!data[key]) {
                    data[key] = {
                        type: 'checkbox',
                        values: [],
                        fieldId: key
                    };
                }
                
                // Thêm giá trị đã chọn
                if (el.checked) {
                    data[key].values.push(el.value);
                    console.log(`Debug: Checkbox '${key}' đã chọn giá trị: ${el.value}`);
                }
                break;

            case 'radio':
                if (el.checked) {
                    // Lưu trữ giá trị radio trong định dạng cấu trúc
                    data[key] = {
                        type: 'radio',
                        value: el.value,
                        fieldId: key
                    };
                    console.log(`Debug: Radio '${key}' đã chọn: ${el.value}`);
                }
                break;

            case 'file':
                // Xử lý đầu vào tệp (chuyển đổi sang Base64)
                const file = el.files[0];
                if (file) {
                    pendingFileUploads++;
                    hasFileInputs = true;
                    console.log(`Debug: Đang đọc tệp '${file.name}' cho trường '${key}'`);
                    const reader = new FileReader();
                    reader.onload = () => {
                        data[key] = {
                            type: 'file',
                            fileName: file.name,
                            fileType: file.type,
                            content: reader.result
                        };
                        console.log(`Debug: Đã đọc tệp cho trường '${key}', kích thước dữ liệu Base64: ${reader.result.length}`);
                        pendingFileUploads--;
                        // Sau khi đọc tệp, kiểm tra xem tất cả các tệp đã được xử lý chưa
                        console.log(`Debug: Còn ${pendingFileUploads} tệp đang chờ xử lý`);
                        if (pendingFileUploads === 0) {
                            finalizeFormSubmission(form, data);
                        }
                    };
                    reader.readAsDataURL(file);
                }
                break;

            case 'date':
                // Xử lý trường ngày đặc biệt - đảm bảo định dạng nhất quán
                if (el.value) {
                    // Get date constraints if they exist
                    const minDate = el.getAttribute('min');
                    const maxDate = el.getAttribute('max');
                    
                    // Create a standardized date object to store date and constraints
                    const dateObj = {
                        type: 'date',
                        value: el.value, // Định dạng đã là YYYY-MM-DD
                        constraints: {},
                        fieldId: key
                    };
                    
                    // Add constraints if they exist
                    if (minDate) dateObj.constraints.minDate = minDate;
                    if (maxDate) dateObj.constraints.maxDate = maxDate;
                    
                    // Store the structured date object
                    data[key] = dateObj;
                    console.log(`Debug: Trường ngày '${key}', giá trị: ${el.value}, min: ${minDate || 'không có'}, max: ${maxDate || 'không có'}`);
                }
                break;

            default:
                data[key] = el.value;
                console.log(`Debug: Trường '${key}', loại: ${el.type}, giá trị: ${el.value}`);
                break;
        }
    });

    // Xử lý tùy chọn hộp kiểm chưa được chọn
    document.querySelectorAll('.checkbox-options').forEach(checkboxGroup => {
        const name = checkboxGroup.querySelector('input[type="checkbox"]')?.name;
        if (name) {
            const key = name.substring(4);
            if (!data[key]) {
                // Khởi tạo object cho checkbox ngay cả khi không có lựa chọn nào
                data[key] = {
                    type: 'checkbox',
                    values: [],
                    fieldId: key
                };
                console.log(`Debug: Nhóm checkbox '${key}' không có tùy chọn nào được chọn`);
            }
        }
    });

    // Không thu thập dữ liệu từ trường info vì chỉ là thông tin hiển thị
    document.querySelectorAll('.info-field').forEach((infoField) => {
        // Tìm input ẩn cho trường thông tin này
        const hiddenInput = infoField.querySelector('input[type="hidden"][data-field-type="info"]');
        if (!hiddenInput) return;
        
        const key = hiddenInput.name.substring(4); // Bỏ tiền tố 'ans_'
        
        // Ghi log nhưng không lưu trữ dữ liệu info field
        console.log(`Debug: Bỏ qua trường thông tin chỉ đọc ${key}`);
        
        // Đảm bảo trường info không được thêm vào data
        if (data[key]) {
            delete data[key];
        }
    });

    // In toàn bộ dữ liệu thu được để debug
    console.log('Debug: Tổng quan dữ liệu thu thập được:', Object.keys(data).map(key => `${key}: ${data[key] instanceof Array ? `[${data[key].join(', ')}]` : (typeof data[key] === 'string' ? (data[key].length > 100 ? `${data[key].substring(0, 100)}... (${data[key].length} ký tự)` : data[key]) : typeof data[key])}`));

    // Nếu không có đầu vào tệp cần xử lý không đồng bộ, gửi ngay
    if (!hasFileInputs) {
        console.log('Debug: Không có tệp cần xử lý, hoàn tất gửi đơn');
        finalizeFormSubmission(form, data);
    }
}

/**
 * Hoàn tất và gửi biểu mẫu với dữ liệu đã thu thập
 */
function finalizeFormSubmission(form, data) {
    console.log('Debug: Đang hoàn thiện gửi biểu mẫu...');
    const jsonData = JSON.stringify(data);
    document.getElementById('responsesJson').value = jsonData;
    console.log(`Debug: Dữ liệu JSON đã sẵn sàng, kích thước: ${jsonData.length} ký tự`);
    console.log('Debug: Đang gửi biểu mẫu...');
    form.submit();
}
