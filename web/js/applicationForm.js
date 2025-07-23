// applicationForm.js - Xử lý biểu mẫu đăng ký

document.addEventListener('DOMContentLoaded', () => {
    console.log('ApplicationForm: DOM đã tải xong, khởi tạo biểu mẫu...');

    // Thiết lập chức năng xem trước tệp đã chọn
    setupFileInputPreviews();
    
    // Xác minh biểu mẫu trước khi gửi
    const form = document.getElementById('applicationForm');
    if (form) {
        form.addEventListener('submit', (e) => {
            e.preventDefault();
            validateForm();
        });
    } else {
        console.warn('ApplicationForm: Không tìm thấy phần tử biểu mẫu!');
    }
    
    // Thêm kiểu dáng xác nhận cho trường bắt buộc
    setupRequiredFields();
    
    // Thiết lập bộ đếm ký tự cho trường text và textarea
    setupCharacterCounter();
    
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
    
    // Thiết lập xử lý cho các nhóm checkbox bắt buộc
    setupCheckboxGroupValidation();
}

/**
 * Thiết lập xác thực cho nhóm checkbox bắt buộc
 * Nhóm checkbox bắt buộc yêu cầu ít nhất một tùy chọn được chọn
 */
function setupCheckboxGroupValidation() {
    // Nhóm các checkbox theo name attribute
    const checkboxGroups = {};
    
    // Tìm tất cả các checkbox có thuộc tính data-is-required="true"
    document.querySelectorAll('input[type="checkbox"][data-is-required="true"]').forEach(checkbox => {
        const name = checkbox.name;
        const templateId = name.substring(4); // Bỏ tiền tố "ans_"
        
        if (!checkboxGroups[name]) {
            checkboxGroups[name] = {
                checkboxes: [],
                validator: document.getElementById(`validator_${templateId}`),
                errorMessage: document.getElementById(`error-msg-${templateId}`),
                container: document.getElementById(`checkbox-group-${templateId}`)
            };
        }
        
        checkboxGroups[name].checkboxes.push(checkbox);
        
        // Thêm sự kiện xử lý khi checkbox được click
        checkbox.addEventListener('change', function() {
            const group = checkboxGroups[this.name];
            const isAnyChecked = group.checkboxes.some(cb => cb.checked);
            
            // Update validator
            if (group.validator) {
                if (isAnyChecked) {
                    group.validator.value = "valid";
                    group.validator.setCustomValidity('');
                    
                    // Xóa các lớp và thông báo lỗi
                    if (group.errorMessage) {
                        group.errorMessage.style.display = 'none';
                    }
                    if (group.container) {
                        group.container.classList.remove('has-error');
                    }
                } else {
                    group.validator.value = "";
                    group.validator.setCustomValidity('Vui lòng chọn ít nhất một tùy chọn');
                    
                    // Hiển thị lỗi
                    if (group.errorMessage) {
                        group.errorMessage.style.display = 'block';
                    }
                    if (group.container) {
                        group.container.classList.add('has-error');
                    }
                }
            }
            
            // Cập nhật trạng thái hiển thị của tất cả checkbox trong nhóm
            group.checkboxes.forEach(cb => {
                if (isAnyChecked) {
                    // Xóa style lỗi
                    cb.classList.remove('input-error');
                    const label = cb.nextElementSibling;
                    if (label && label.tagName === 'LABEL') {
                        label.style.color = ''; // Đặt lại màu mặc định
                    }
                } else {
                    // Thêm style lỗi
                    cb.classList.add('input-error');
                    const label = cb.nextElementSibling;
                    if (label && label.tagName === 'LABEL') {
                        label.style.color = 'var(--error-color)';
                    }
                }
            });
            
        });
    });
    
    // Khởi tạo trạng thái ban đầu cho tất cả nhóm checkbox
    Object.values(checkboxGroups).forEach(group => {
        const isAnyChecked = group.checkboxes.some(cb => cb.checked);
        if (group.validator) {
            if (isAnyChecked) {
                group.validator.value = "valid";
                group.validator.setCustomValidity('');
            } else {
                group.validator.value = "";
                group.validator.setCustomValidity('Vui lòng chọn ít nhất một tùy chọn');
            }
        }
    });
    
}

/**
 * Kiểm tra hợp lệ biểu mẫu trước khi gửi
 */
function validateForm() {
    const form = document.getElementById('applicationForm');
    if (!form) return;
    
    // Xóa tất cả thông báo lỗi hiện có trước khi xác minh lại
    form.querySelectorAll('.error-message').forEach(msg => {
        if (msg.style) msg.style.display = 'none';
    });
    form.querySelectorAll('.input-error').forEach(field => field.classList.remove('input-error'));
    form.querySelectorAll('.checkbox-group.has-error').forEach(group => group.classList.remove('has-error'));
    
    // Đặt lại màu cho các nhãn checkbox
    form.querySelectorAll('input[type="checkbox"] + label').forEach(label => {
        label.style.color = '';
    });
    
    // Kiểm tra loại form để quyết định việc xác thực
    const formType = document.querySelector('input[name="formType"]')?.value || 
                    (document.location.search.includes('event') ? 'event' : 'member');
    const isEventForm = formType === 'event';
    
    // Tìm câu hỏi "Chọn ban"
    const departmentQuestions = Array.from(document.querySelectorAll('.form-group')).filter(group => {
        const label = group.querySelector('.form-label');
        return label && label.textContent.includes('Chọn ban');
    });
    
    // Nếu là form event, bỏ qua xác thực bắt buộc cho câu hỏi chọn ban
    if (isEventForm && departmentQuestions.length > 0) {
        departmentQuestions.forEach(group => {
            const inputs = group.querySelectorAll('input[required]');
            inputs.forEach(input => input.required = false);
        });
    }
    
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
            }
        }
    });      // Xác minh cho các nhóm checkbox cần ít nhất một lựa chọn khi nhóm đó là required
    const checkboxGroups = {};
    
    // Tìm tất cả các checkbox và nhóm chúng theo name
    form.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
        const name = checkbox.name;
        if (!checkboxGroups[name]) {
            // Xác định xem nhóm này có là required hay không bằng thuộc tính data-is-required
            const isRequired = checkbox.getAttribute('data-is-required') === 'true';
            checkboxGroups[name] = { 
                elements: [], 
                isValid: !isRequired, // Nếu không phải required thì mặc định là valid
                isRequired: isRequired 
            };
        }
        
        checkboxGroups[name].elements.push(checkbox);
        
        // Nếu có ít nhất một checkbox được chọn, nhóm được coi là hợp lệ
        if (checkbox.checked) {
            checkboxGroups[name].isValid = true;
        }
    });
    
    // Kiểm tra từng nhóm checkbox
    Object.keys(checkboxGroups).forEach(groupName => {
        const group = checkboxGroups[groupName];
          // Chỉ xác thực các nhóm có thuộc tính required
        if (group.isRequired && !group.isValid) {
            // Đánh dấu nhóm không hợp lệ bằng cách thêm lớp error cho tất cả checkbox trong nhóm
            group.elements.forEach(cb => {
                cb.classList.add('input-error');
                // Thêm style cho label đi kèm
                const label = cb.nextElementSibling;
                if (label && label.tagName === 'LABEL') {
                    label.style.color = 'var(--error-color)';
                }
            });
            
            // Tìm container gần nhất và thêm thông báo lỗi
            const checkboxContainer = group.elements[0].closest('.checkbox-group');
            if (checkboxContainer) {
                // Thêm lớp has-error cho container
                checkboxContainer.classList.add('has-error');
                
                // Tìm hoặc hiển thị thông báo lỗi có sẵn
                let errorMsg = checkboxContainer.querySelector('.error-message');
                if (errorMsg) {
                    errorMsg.style.display = 'block';
                } else {
                    // Nếu không có, tạo mới
                    errorMsg = document.createElement('div');
                    errorMsg.className = 'error-message';
                    errorMsg.textContent = 'Vui lòng chọn ít nhất một tùy chọn';
                    checkboxContainer.appendChild(errorMsg);
                }
                
                // Tìm validator hidden field và đánh dấu là invalid
                const templateId = groupName.substring(4); // Bỏ "ans_"
                const validator = document.getElementById(`validator_${templateId}`);
                if (validator) {
                    validator.setCustomValidity('Vui lòng chọn ít nhất một tùy chọn');
                }
                
                allFilesValid = false;
            }
        }
    });

    // Kiểm tra xác minh HTML5
    if (!form.checkValidity() || !allFilesValid) {
        form.reportValidity();
        return;
    }
    
    // Nếu tất cả kiểm tra đều thành công, gửi biểu mẫu
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


    // Kiểm tra loại form
    const formType = document.querySelector('input[name="formType"]')?.value || 
                    (document.location.search.includes('event') ? 'event' : 'member');
    const isEventForm = formType === 'event';

    // Hiển thị chỉ báo đang tải
    const submitBtn = document.getElementById('submitBtn');
    const loadingIndicator = document.getElementById('loadingIndicator');
    if (submitBtn) submitBtn.disabled = true;
    if (loadingIndicator) loadingIndicator.style.display = 'flex';

    // Thu thập giá trị cho mỗi câu hỏi
    Array.from(form.elements).forEach(el => {
        if (!el.name || !el.name.startsWith('ans_')) return;
        const key = el.name.substring(4);

        // Kiểm tra xem có phải câu hỏi chọn ban không
        const isSelectDepartment = el.closest('.form-group') && 
            el.closest('.form-group').querySelector('label') && 
            el.closest('.form-group').querySelector('label').textContent.includes('Chọn ban');
        
        // Bỏ qua câu hỏi chọn ban nếu đây là form sự kiện
        if (isEventForm && isSelectDepartment) {
            return;
        }

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
                }
                break;

            case 'file':
                // Xử lý đầu vào tệp (chuyển đổi sang Base64)
                const file = el.files[0];
                if (file) {
                    pendingFileUploads++;
                    hasFileInputs = true;
                    const reader = new FileReader();
                    reader.onload = () => {
                        data[key] = {
                            type: 'file',
                            fileName: file.name,
                            fileType: file.type,
                            content: reader.result
                        };
                        pendingFileUploads--;
                        // Sau khi đọc tệp, kiểm tra xem tất cả các tệp đã được xử lý chưa
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
                    
                    data[key] = dateObj;
                
                }
                break;

            default:
                data[key] = el.value;
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
            }
        }
    });

    // Không thu thập dữ liệu từ trường info vì chỉ là thông tin hiển thị
    document.querySelectorAll('.info-field').forEach((infoField) => {
        // Tìm input ẩn cho trường thông tin này
        const hiddenInput = infoField.querySelector('input[type="hidden"][data-field-type="info"]');
        if (!hiddenInput) return;
        
        const key = hiddenInput.name.substring(4); // Bỏ tiền tố 'ans_'
        
        
        // Đảm bảo trường info không được thêm vào data
        if (data[key]) {
            delete data[key];
        }
    });


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
    form.submit();
}

/**
 * Thiết lập bộ đếm ký tự cho các trường văn bản và văn bản nhiều dòng
 */
function setupCharacterCounter() {
    // Lấy tất cả các trường input và textarea có bộ đếm ký tự
    const textInputs = document.querySelectorAll('input[type="text"][maxlength], textarea[maxlength]');
    
    // Thêm sự kiện lắng nghe cho mỗi trường
    textInputs.forEach(input => {
        const counterId = 'counter_' + input.id.split('_')[1];
        const counter = document.getElementById(counterId);
        const maxLength = input.getAttribute('maxlength');
        
        // Cập nhật bộ đếm ban đầu
        if (counter) {
            counter.textContent = '0/' + maxLength;
            
            // Thêm sự kiện input để cập nhật bộ đếm khi người dùng nhập liệu
            input.addEventListener('input', function() {
                const currentLength = this.value.length;
                counter.textContent = currentLength + '/' + maxLength;
                
                // Hiển thị cảnh báo nếu gần đạt đến giới hạn
                if (currentLength > maxLength * 0.9) {
                    counter.classList.add('char-counter-warning');
                } else {
                    counter.classList.remove('char-counter-warning');
                }
            });
        }
    });
}
