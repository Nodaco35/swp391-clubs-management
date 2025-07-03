/**
 * CreateEditRecruitmentCampaign - Module xử lý tạo mới và chỉnh sửa hoạt động tuyển quân
 * Phụ thuộc: recruitmentCommon.js
 */
// Khai báo biến global cho script
let isEdit = false;
let contextPath = '';

// Hàm hiển thị thông báo lỗi khi cần thiết
function showDebugInfo(message, data = null) {
    const debugPanel = document.getElementById('debugPanel');
    const debugContent = document.getElementById('debugContent');
    
    if (debugPanel && debugContent) {
        let content = `<p><strong>${message}</strong></p>`;
        
        if (data) {
            if (typeof data === 'object') {
                content += '<pre>' + JSON.stringify(data, null, 2) + '</pre>';
            } else {
                content += `<p>${data}</p>`;
            }
        }
        
        debugContent.innerHTML = content;
        debugPanel.style.display = 'block';
        
        // Tạo style cho debug panel khi cần hiển thị
        debugPanel.style.marginBottom = '15px';
        debugPanel.style.padding = '10px';
        debugPanel.style.border = '1px solid #f5c6cb';
        debugPanel.style.backgroundColor = '#f8d7da';
        debugPanel.style.color = '#721c24';
        debugPanel.style.borderRadius = '5px';
    }
}

document.addEventListener('DOMContentLoaded', function() {
    // Xác định mode tạo mới hay chỉnh sửa từ form data
    const form = document.getElementById('recruitmentForm');
    
    // Xác định mode chính xác dựa trên data-mode và kiểm tra thêm input recruitmentId
    if (form) {
        const hasValidId = document.querySelector('input[name="recruitmentId"]');
        isEdit = form.dataset.mode === 'edit' && hasValidId !== null;
    } else {
        isEdit = false;
    }
    
    contextPath = getContextPath();
    
    // Hiển thị mode hiện tại
    showDebugInfo('Thông tin mode', {
        isEdit: isEdit,
        mode: isEdit ? 'Chỉnh sửa' : 'Tạo mới',
        contextPath: contextPath
    });
    
    // Khởi tạo các bước trong wizard
    initializeWizard();
    
    // Khởi tạo datepickers và validation
    initializeDateConstraints();
    
    // Thiết lập form submission
    setupFormSubmission();
    
    // Tự động chọn form đăng ký nếu chỉ có một lựa chọn
    autoSelectSingleTemplate();
});

/**
 * Khởi tạo wizard với các bước
 */
function initializeWizard() {
    // Lấy các elements
    const step1 = document.getElementById('step1');
    const step2 = document.getElementById('step2');
    const step3 = document.getElementById('step3');
    
    const step1NextBtn = document.getElementById('step1NextBtn');
    const step2PrevBtn = document.getElementById('step2PrevBtn');
    const step2NextBtn = document.getElementById('step2NextBtn');
    const step3PrevBtn = document.getElementById('step3PrevBtn');
    
    // Stepper indicators
    const step1_2Line = document.getElementById('step1-2-line');
    const step2_3Line = document.getElementById('step2-3-line');
    const step2Circle = document.getElementById('step2-circle');
    const step3Circle = document.getElementById('step3-circle');
    const step2Text = document.getElementById('step2-text');
    const step3Text = document.getElementById('step3-text');
    
    // Button events
    if (step1NextBtn) {
        step1NextBtn.addEventListener('click', function() {
            if (validateStep1()) {
                step1.classList.remove('active');
                step2.classList.add('active');
                
                // Update stepper
                step1_2Line.classList.remove('inactive');
                step1_2Line.classList.add('active');
                step2Circle.classList.remove('inactive');
                step2Circle.classList.add('active');
                step2Text.classList.remove('inactive');
                step2Text.classList.add('active');
            }
        });
    }
    
    if (step2PrevBtn) {
        step2PrevBtn.addEventListener('click', function() {
            step2.classList.remove('active');
            step1.classList.add('active');
            
            // Update stepper
            step1_2Line.classList.remove('active');
            step1_2Line.classList.add('inactive');
            step2Circle.classList.remove('active');
            step2Circle.classList.add('inactive');
            step2Text.classList.remove('active');
            step2Text.classList.add('inactive');
        });
    }
    
    if (step2NextBtn) {
        step2NextBtn.addEventListener('click', function() {
            if (validateStep2()) {
                step2.classList.remove('active');
                step3.classList.add('active');
                
                // Update stepper
                step2_3Line.classList.remove('inactive');
                step2_3Line.classList.add('active');
                step3Circle.classList.remove('inactive');
                step3Circle.classList.add('active');
                step3Text.classList.remove('inactive');
                step3Text.classList.add('active');
                
                // Populate confirmation step
                populateConfirmationStep();
            }
        });
    }
    
    if (step3PrevBtn) {
        step3PrevBtn.addEventListener('click', function() {
            step3.classList.remove('active');
            step2.classList.add('active');
            
            // Update stepper
            step2_3Line.classList.remove('active');
            step2_3Line.classList.add('inactive');
            step3Circle.classList.remove('active');
            step3Circle.classList.add('inactive');
            step3Text.classList.remove('active');
            step3Text.classList.add('inactive');
        });
    }
    
    // Prefill dates nếu đang ở chế độ edit
    const form = document.getElementById('recruitmentForm');
    if (form && form.dataset.mode === 'edit') {
        prefillStageData();
    }
}

/**
 * Validate thông tin ở step 1
 */
function validateStep1() {
    const requiredFields = document.querySelectorAll('#step1 [required]');
    let isValid = true;
    
    // Kiểm tra tất cả các trường required
    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            isValid = false;
            markFieldInvalid(field, 'Trường này là bắt buộc');
        } else {
            markFieldValid(field);
        }
    });
    
    // Kiểm tra cụ thể trường templateId (form đăng ký)
    const templateIdField = document.getElementById('templateId');
    if (!templateIdField.value || templateIdField.value.trim() === '') {
        isValid = false;
        markFieldInvalid(templateIdField, 'Vui lòng chọn form đăng ký');
    }
    
    // Kiểm tra ngày bắt đầu < ngày kết thúc
    const startDate = new Date(document.getElementById('startDate').value);
    const endDate = new Date(document.getElementById('endDate').value);
    
    if (isNaN(startDate.getTime())) {
        isValid = false;
        markFieldInvalid(document.getElementById('startDate'), 'Vui lòng chọn ngày bắt đầu');
    }
    
    if (isNaN(endDate.getTime())) {
        isValid = false;
        markFieldInvalid(document.getElementById('endDate'), 'Vui lòng chọn ngày kết thúc');
    }
    
    if (!isNaN(startDate.getTime()) && !isNaN(endDate.getTime()) && startDate >= endDate) {
        isValid = false;
        markFieldInvalid(document.getElementById('endDate'), 'Ngày kết thúc phải sau ngày bắt đầu');
    }
    
    return isValid;
}

/**
 * Validate thông tin ở step 2
 */
function validateStep2() {
    const requiredFields = document.querySelectorAll('#step2 [required]');
    let isValid = true;
    
    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            isValid = false;
            markFieldInvalid(field, 'Trường này là bắt buộc');
        } else {
            markFieldValid(field);
        }
    });
    
    // Lấy thông tin các ngày
    const campaignStartDate = new Date(document.getElementById('startDate').value);
    const campaignEndDate = new Date(document.getElementById('endDate').value);
    
    const applicationStartDate = new Date(document.getElementById('applicationStageStart').value);
    const applicationEndDate = new Date(document.getElementById('applicationStageEnd').value);
    
    const interviewStartDate = new Date(document.getElementById('interviewStageStart').value);
    const interviewEndDate = new Date(document.getElementById('interviewStageEnd').value);
    
    const challengeStartDate = new Date(document.getElementById('challengeStageStart').value);
    const challengeEndDate = new Date(document.getElementById('challengeStageEnd').value);
    
    // Kiểm tra các ngày có hợp lệ không
    if (isNaN(applicationStartDate.getTime()) || isNaN(applicationEndDate.getTime()) || 
        isNaN(interviewStartDate.getTime()) || isNaN(interviewEndDate.getTime()) || 
        isNaN(challengeStartDate.getTime()) || isNaN(challengeEndDate.getTime())) {
        isValid = false;
        showToast('Vui lòng điền đầy đủ ngày tháng cho các giai đoạn', 'error');
        return isValid;
    }
    
    // 1. Kiểm tra từng giai đoạn - ngày bắt đầu phải trước ngày kết thúc
    if (applicationStartDate >= applicationEndDate) {
        isValid = false;
        markFieldInvalid(document.getElementById('applicationStageEnd'), 'Ngày kết thúc phải sau ngày bắt đầu');
    }
    
    if (interviewStartDate >= interviewEndDate) {
        isValid = false;
        markFieldInvalid(document.getElementById('interviewStageEnd'), 'Ngày kết thúc phải sau ngày bắt đầu');
    }
    
    if (challengeStartDate >= challengeEndDate) {
        isValid = false;
        markFieldInvalid(document.getElementById('challengeStageEnd'), 'Ngày kết thúc phải sau ngày bắt đầu');
    }
    
    // 2. Kiểm tra các giai đoạn nằm trong thời gian hoạt động
    if (applicationStartDate < campaignStartDate || applicationEndDate > campaignEndDate) {
        isValid = false;
        showToast('Thời gian vòng nộp đơn phải nằm trong thời gian hoạt động', 'error');
    }
    
    if (interviewStartDate < campaignStartDate || interviewEndDate > campaignEndDate) {
        isValid = false;
        showToast('Thời gian vòng phỏng vấn phải nằm trong thời gian hoạt động', 'error');
    }
    
    if (challengeStartDate < campaignStartDate || challengeEndDate > campaignEndDate) {
        isValid = false;
        showToast('Thời gian vòng thử thách phải nằm trong thời gian hoạt động', 'error');
    }
    
    // 3. Kiểm tra chồng lấn giữa các giai đoạn
    if (doPeriodsOverlap(applicationStartDate, applicationEndDate, interviewStartDate, interviewEndDate)) {
        isValid = false;
        showToast('Thời gian vòng nộp đơn và vòng phỏng vấn không được chồng lấn nhau', 'error');
    }
    
    if (doPeriodsOverlap(applicationStartDate, applicationEndDate, challengeStartDate, challengeEndDate)) {
        isValid = false;
        showToast('Thời gian vòng nộp đơn và vòng thử thách không được chồng lấn nhau', 'error');
    }
    
    if (doPeriodsOverlap(interviewStartDate, interviewEndDate, challengeStartDate, challengeEndDate)) {
        isValid = false;
        showToast('Thời gian vòng phỏng vấn và vòng thử thách không được chồng lấn nhau', 'error');
    }
    
//Kiểm tra thứ tự các giai đoạn - nếu muốn bắt buộc theo thứ tự
    if (applicationEndDate > interviewStartDate) {
        isValid = false;
        showToast('Vòng nộp đơn phải kết thúc trước khi vòng phỏng vấn bắt đầu', 'error');
    }
    
    if (interviewEndDate > challengeStartDate) {
        isValid = false;
        showToast('Vòng phỏng vấn phải kết thúc trước khi vòng thử thách bắt đầu', 'error');
    }
    
    return isValid;
}

/**
 * Kiểm tra hai khoảng thời gian có chồng lấn nhau không
 * @param {Date} start1 - Ngày bắt đầu khoảng 1
 * @param {Date} end1 - Ngày kết thúc khoảng 1
 * @param {Date} start2 - Ngày bắt đầu khoảng 2
 * @param {Date} end2 - Ngày kết thúc khoảng 2
 * @returns {boolean} - true nếu chồng lấn, false nếu không
 */
function doPeriodsOverlap(start1, end1, start2, end2) {
    // Trường hợp không chồng lấn:
    // 1. Khoảng 1 kết thúc trước khi khoảng 2 bắt đầu HOẶC
    // 2. Khoảng 2 kết thúc trước khi khoảng 1 bắt đầu
    if (end1 < start2 || end2 < start1) {
        return false;
    }
    // Các trường hợp còn lại là chồng lấn
    return true;
}

/**
 * Đánh dấu trường không hợp lệ
 */
function markFieldInvalid(field, message) {
    field.classList.add('field-error');
    
    // Tạo thông báo lỗi nếu chưa có
    const errorId = `error-${field.id}`;
    if (!document.getElementById(errorId)) {
        const errorMsg = document.createElement('p');
        errorMsg.id = errorId;
        errorMsg.className = 'form-error-message';
        errorMsg.textContent = message;
        field.parentNode.appendChild(errorMsg);
    }
}

/**
 * Đánh dấu trường hợp lệ
 */
function markFieldValid(field) {
    field.classList.remove('field-error');
    
    const errorId = `error-${field.id}`;
    const errorElement = document.getElementById(errorId);
    if (errorElement) {
        errorElement.remove();
    }
}

/**
 * Thiết lập ràng buộc về ngày tháng
 */
function initializeDateConstraints() {
    // Set min date cho tất cả date inputs là ngày hiện tại
    const today = new Date();
    const todayString = today.toISOString().split('T')[0]; // Format YYYY-MM-DD
    
    // Campaign dates
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    
    if (startDateInput && endDateInput) {
        // Nếu không phải ở chế độ edit, set min date
        if (document.getElementById('recruitmentForm').dataset.mode !== 'edit') {
            startDateInput.min = todayString;
            endDateInput.min = todayString;
        }
        
        // Khi thay đổi ngày bắt đầu, cập nhật min của ngày kết thúc
        startDateInput.addEventListener('change', function() {
            endDateInput.min = this.value;
            
            if (endDateInput.value && new Date(endDateInput.value) < new Date(this.value)) {
                endDateInput.value = this.value;
            }
            
            // Cập nhật min cho các date inputs của các giai đoạn
            updateStagesDateConstraints();
        });
        
        // Khi thay đổi ngày kết thúc, cập nhật max của ngày bắt đầu
        endDateInput.addEventListener('change', function() {
            // Không cần cập nhật max cho startDate vì có thể hoạt động đã bắt đầu
            
            // Cập nhật max cho các date inputs của các giai đoạn
            updateStagesDateConstraints();
        });
    }
    
    // Stage dates
    updateStagesDateConstraints();
}

/**
 * Cập nhật ràng buộc ngày tháng cho các giai đoạn
 */
function updateStagesDateConstraints() {
    const campaignStartDate = document.getElementById('startDate').value;
    const campaignEndDate = document.getElementById('endDate').value;
    
    if (!campaignStartDate || !campaignEndDate) return;
    
    // Application stage
    const applicationStageStart = document.getElementById('applicationStageStart');
    const applicationStageEnd = document.getElementById('applicationStageEnd');
    
    if (applicationStageStart && applicationStageEnd) {
        applicationStageStart.min = campaignStartDate;
        applicationStageStart.max = campaignEndDate;
        applicationStageEnd.min = applicationStageStart.value || campaignStartDate;
        applicationStageEnd.max = campaignEndDate;
        
        applicationStageStart.addEventListener('change', function() {
            applicationStageEnd.min = this.value;
            
            if (applicationStageEnd.value && new Date(applicationStageEnd.value) < new Date(this.value)) {
                applicationStageEnd.value = this.value;
            }
        });
    }
    
    // Interview stage
    const interviewStageStart = document.getElementById('interviewStageStart');
    const interviewStageEnd = document.getElementById('interviewStageEnd');
    
    if (interviewStageStart && interviewStageEnd) {
        interviewStageStart.min = campaignStartDate;
        interviewStageStart.max = campaignEndDate;
        interviewStageEnd.min = interviewStageStart.value || campaignStartDate;
        interviewStageEnd.max = campaignEndDate;
        
        interviewStageStart.addEventListener('change', function() {
            interviewStageEnd.min = this.value;
            
            if (interviewStageEnd.value && new Date(interviewStageEnd.value) < new Date(this.value)) {
                interviewStageEnd.value = this.value;
            }
        });
    }
    
    // Challenge stage
    const challengeStageStart = document.getElementById('challengeStageStart');
    const challengeStageEnd = document.getElementById('challengeStageEnd');
    
    if (challengeStageStart && challengeStageEnd) {
        challengeStageStart.min = campaignStartDate;
        challengeStageStart.max = campaignEndDate;
        challengeStageEnd.min = challengeStageStart.value || campaignStartDate;
        challengeStageEnd.max = campaignEndDate;
        
        challengeStageStart.addEventListener('change', function() {
            challengeStageEnd.min = this.value;
            
            if (challengeStageEnd.value && new Date(challengeStageEnd.value) < new Date(this.value)) {
                challengeStageEnd.value = this.value;
            }
        });
    }
}

/**
 * Điền sẵn dữ liệu giai đoạn nếu ở chế độ chỉnh sửa
 */
function prefillStageData() {
    const recruitmentId = document.querySelector('input[name="recruitmentId"]')?.value;
    
    if (!recruitmentId) {
        console.error('Không tìm thấy ID hoạt động tuyển quân để lấy dữ liệu giai đoạn');
        return;
    }
    
    // Lấy dữ liệu các giai đoạn từ server
    fetch(`${getContextPath()}/recruitment/stages?recruitmentId=${recruitmentId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Lỗi khi lấy dữ liệu giai đoạn');
            }
            return response.json();
        })
        .then(data => {
            if (data && data.stages && Array.isArray(data.stages)) {
                // Điền dữ liệu giai đoạn vào form
                data.stages.forEach(stage => {
                    if (stage.stageName.includes('Nộp đơn')) {
                        document.getElementById('applicationStageStart').value = formatDateForInput(new Date(stage.startDate));
                        document.getElementById('applicationStageEnd').value = formatDateForInput(new Date(stage.endDate));
                    } else if (stage.stageName.includes('Phỏng vấn')) {
                        document.getElementById('interviewStageStart').value = formatDateForInput(new Date(stage.startDate));
                        document.getElementById('interviewStageEnd').value = formatDateForInput(new Date(stage.endDate));
                        if (stage.locationID) {
                            document.getElementById('interviewLocationId').value = stage.locationID;
                        }
                    } else if (stage.stageName.includes('Thử thách')) {
                        document.getElementById('challengeStageStart').value = formatDateForInput(new Date(stage.startDate));
                        document.getElementById('challengeStageEnd').value = formatDateForInput(new Date(stage.endDate));
                        if (stage.description) {
                            document.getElementById('challengeDescription').value = stage.description;
                        }
                    }
                });
            } else {
                console.error('Dữ liệu giai đoạn không hợp lệ:', data);
            }
        })
        .catch(error => {
            console.error('Lỗi khi lấy dữ liệu giai đoạn:', error);
        });
}

/**
 * Format date để hiển thị trong input type="date"
 */
function formatDateForInput(date) {
    if (!date || isNaN(date.getTime())) return '';
    return date.toISOString().split('T')[0]; // Format YYYY-MM-DD
}

/**
 * Điền thông tin vào màn hình xác nhận
 */
function populateConfirmationStep() {
    // Thông tin cơ bản
    document.getElementById('confirmGen').textContent = document.getElementById('gen').value;
    
    const startDate = new Date(document.getElementById('startDate').value);
    const endDate = new Date(document.getElementById('endDate').value);
    document.getElementById('confirmTime').textContent = `${formatDate(startDate)} - ${formatDate(endDate)}`;
    
    const templateSelect = document.getElementById('templateId');
    const selectedTemplate = templateSelect.options[templateSelect.selectedIndex].text;
    document.getElementById('confirmForm').textContent = selectedTemplate;
    
    // Vòng nộp đơn
    const appStartDate = new Date(document.getElementById('applicationStageStart').value);
    const appEndDate = new Date(document.getElementById('applicationStageEnd').value);
    document.getElementById('confirmApplicationStage').textContent = `${formatDate(appStartDate)} - ${formatDate(appEndDate)}`;
    
    // Vòng phỏng vấn
    const intStartDate = new Date(document.getElementById('interviewStageStart').value);
    const intEndDate = new Date(document.getElementById('interviewStageEnd').value);
    const locationSelect = document.getElementById('interviewLocationId');
    let locationText = '';
    if (locationSelect.selectedIndex > 0) {
        locationText = ` (${locationSelect.options[locationSelect.selectedIndex].text})`;
    }
    document.getElementById('confirmInterviewStage').textContent = `${formatDate(intStartDate)} - ${formatDate(intEndDate)}${locationText}`;
    
    // Vòng thử thách
    const chalStartDate = new Date(document.getElementById('challengeStageStart').value);
    const chalEndDate = new Date(document.getElementById('challengeStageEnd').value);
    const chalDesc = document.getElementById('challengeDescription').value;
    let challengeText = `${formatDate(chalStartDate)} - ${formatDate(chalEndDate)}`;
    if (chalDesc) {
        challengeText += ` (${chalDesc})`;
    }
    document.getElementById('confirmChallengeStage').textContent = challengeText;
}

/**
 * Format date từ ISO string sang định dạng ngày/tháng/năm
 */
function formatDate(date) {
    return `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getFullYear()}`;
}

/**
 * Thiết lập xử lý khi submit form
 */
function setupFormSubmission() {
    const form = document.getElementById('recruitmentForm');
    
    if (!form) return;
    
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Xác thực form trước khi gửi
        if (!validateEntireForm()) {
            showToast('Vui lòng điền đầy đủ thông tin cần thiết', 'error');
            return;
        }
        
        const formData = new FormData(form);
        // Dùng biến global isEdit và contextPath đã khởi tạo
        
        // Kiểm tra các field quan trọng
        const requiredParams = ['clubId', 'gen', 'templateId', 'startDate', 'endDate', 'title', 
                               'applicationStageStart', 'applicationStageEnd', 
                               'interviewStageStart', 'interviewStageEnd',
                               'challengeStageStart', 'challengeStageEnd'];
        let missingParams = [];
        
        requiredParams.forEach(param => {
            const value = formData.get(param);
            if (!value || value.trim() === '') {
                missingParams.push(param);
            }
        });
        
        if (missingParams.length > 0) {
            const errorMsg = 'Thiếu thông tin bắt buộc: ' + missingParams.join(', ');
            showToast(errorMsg, 'error');
            
            // Hiển thị thông tin lỗi khi thiếu dữ liệu
            if (missingParams.includes('clubId')) {
                showDebugInfo('Lỗi thiếu thông tin CLB', {
                    missingParams: missingParams
                });
            }
            return;
        }
        
        // URL endpoint tùy thuộc vào mode tạo mới hay chỉnh sửa
        const url = isEdit 
            ? `${contextPath}/recruitmentForm/update` 
            : `${contextPath}/recruitmentForm/create`;
        
        // Nếu đang chỉnh sửa, kiểm tra ID
        if (isEdit) {
            const recruitmentId = formData.get('recruitmentId');
            console.log("ID hoạt động cần cập nhật:", recruitmentId);
            if (!recruitmentId || recruitmentId === '0') {
                showToast('Thiếu ID hoạt động tuyển quân hợp lệ khi cập nhật', 'error');
                document.getElementById('submitBtn').disabled = false;
                document.getElementById('submitBtn').innerHTML = 'Cập nhật hoạt động';
                
                // Hiển thị thông tin debug
                showDebugInfo('Lỗi: ID không hợp lệ khi cập nhật', {
                    recruitmentId: recruitmentId,
                    formMode: isEdit ? 'edit' : 'create'
                });
                return;
            }
        }
        
        // Double check - đảm bảo URL endpoint chính xác và tham số
        const hasValidId = formData.get('recruitmentId') && formData.get('recruitmentId') !== '0';
        const finalUrl = isEdit && hasValidId
            ? `${contextPath}/recruitmentForm/update` 
            : `${contextPath}/recruitmentForm/create`;
        
        // Đảm bảo clubId tồn tại và hợp lệ
        const clubId = formData.get('clubId');
        
        // Nếu không có clubId hoặc clubId không hợp lệ, hiển thị thông báo lỗi
        if (!clubId || clubId === '0' || clubId === '') {
            showToast('Thiếu thông tin CLB', 'error');
            document.getElementById('submitBtn').disabled = false;
            document.getElementById('submitBtn').innerHTML = isEdit ? 'Cập nhật hoạt động' : 'Tạo hoạt động';
            
            // Hiển thị thông tin lỗi
            showDebugInfo('Lỗi: Thiếu thông tin CLB', {
                clubId: clubId
            });
            return;
        }
        
        // Nếu đang tạo mới, xóa các trường không cần thiết để tránh lỗi
        if (finalUrl.includes('/create')) {
            formData.delete('recruitmentId');
            formData.delete('status');
        }
        
        // Đảm bảo clubId luôn được gửi đi với nhiều cách khác nhau
        const clubIdValue = formData.get('clubId');
        if (clubIdValue) {
            // Thêm các biến thể của clubId với tên khác nhau để đảm bảo server nhận được
            formData.append('clubID', clubIdValue);
            formData.append('club_id', clubIdValue);
            formData.append('club-id', clubIdValue);
        } else {
            // Nếu không tìm thấy clubId trong form, thử tìm từ các nguồn khác
            const hiddenClubId = document.querySelector('input[name="clubId"]');
            if (hiddenClubId && hiddenClubId.value) {
                const value = hiddenClubId.value;
                formData.append('clubId', value);
                formData.append('clubID', value);
                formData.append('club_id', value);
                formData.append('club-id', value);
            } else {
                // Thử tìm từ URL hoặc data attribute
                const urlParams = new URLSearchParams(window.location.search);
                const urlClubId = urlParams.get('clubId');
                if (urlClubId) {
                    formData.append('clubId', urlClubId);
                    formData.append('clubID', urlClubId);
                } else {
                    showDebugInfo('Không tìm thấy clubId từ bất kỳ nguồn nào', {
                        fromURL: urlParams.get('clubId')
                    });
                }
            }
        }
        
        // Hiển thị loading
        document.getElementById('submitBtn').disabled = true;
        document.getElementById('submitBtn').innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang xử lý...';
        
        // Tạo một đối tượng URLSearchParams để đảm bảo tham số được gửi đúng
        const urlParams = new URLSearchParams();
        for (const pair of formData.entries()) {
            urlParams.append(pair[0], pair[1]);
        }
        
        // Đảm bảo clubId luôn được gửi đi
        if (!urlParams.has('clubId') || urlParams.get('clubId') === '') {
            const hiddenClubId = document.querySelector('input[name="clubId"]');
            if (hiddenClubId && hiddenClubId.value) {
                urlParams.set('clubId', hiddenClubId.value);
                console.log("Đã thêm clubId từ input hidden:", hiddenClubId.value);
            }
        }
        
        console.log("URLSearchParams:", urlParams.toString());
        
        // Submit form data bằng fetch API với timeout
        const fetchPromise = fetch(finalUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: urlParams.toString()
        });
        
        // Thêm timeout để tránh trường hợp request bị treo
        const timeoutPromise = new Promise((_, reject) => 
            setTimeout(() => reject(new Error('Request timeout sau 10 giây')), 10000)
        );
        
        Promise.race([fetchPromise, timeoutPromise])
        .then(response => {
            // Kiểm tra response status trước khi parse JSON
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            
            // Thêm kiểm tra content-type
            const contentType = response.headers.get('content-type');
            if (!contentType || !contentType.includes('application/json')) {
                throw new Error('Phản hồi không phải dạng JSON');
            }
            
            // Parse JSON
            return response.json();
        })
        .then(data => {
            if (data.success) {
                showToast(isEdit ? 'Cập nhật hoạt động thành công' : 'Tạo hoạt động mới thành công', 'success');
                
                // Redirect sau khi tạo/cập nhật thành công
                setTimeout(() => {
                    const redirectUrl = isEdit 
                        ? `${contextPath}/recruitment/view?id=${data.recruitmentId || formData.get('recruitmentId')}` 
                        : `${contextPath}/recruitment?clubId=${formData.get('clubId')}`;
                    window.location.href = redirectUrl;
                }, 1500);
            } else {
                showToast(data.message || 'Có lỗi xảy ra khi xử lý yêu cầu', 'error');
                document.getElementById('submitBtn').disabled = false;
                document.getElementById('submitBtn').innerHTML = isEdit ? 'Cập nhật hoạt động' : 'Tạo hoạt động';
                
                // Hiển thị thông tin lỗi
                showDebugInfo('Lỗi từ server', {
                    message: data.message,
                    url: finalUrl,
                    requestedMode: isEdit ? 'UPDATE' : 'CREATE',
                    formData: {
                        clubId: formData.get('clubId'),
                        gen: formData.get('gen'),
                        templateId: formData.get('templateId'),
                        title: formData.get('title')
                    }
                });
            }
        })
        .catch(error => {
            // Hiển thị thông báo lỗi 
            let errorMessage = 'Đã xảy ra lỗi: ' + (error.message || 'Lỗi không xác định');
            
            if (error.message && error.message.includes('Thiếu ID hoạt động tuyển quân')) {
                errorMessage = 'Thiếu ID hoạt động tuyển quân. Đây có thể là lỗi từ server khi xử lý request.';
                
                // Hiển thị thông tin lỗi cho trường hợp thiếu ID
                showDebugInfo('Lỗi: Thiếu ID hoạt động tuyển quân', {
                    mode: isEdit ? 'edit' : 'create',
                    clubId: formData.get('clubId')
                });
            }
            
            showToast(errorMessage, 'error');
            document.getElementById('submitBtn').disabled = false;
            document.getElementById('submitBtn').innerHTML = isEdit ? 'Cập nhật hoạt động' : 'Tạo hoạt động';
            
            // Hiển thị chi tiết dữ liệu form để debug
            console.error('Form mode:', isEdit ? "edit" : "create");
            console.error('Form URL đang được gọi:', url);
            
            // In ra tất cả các trường trong form data
            console.error('Chi tiết form data:');
            for (let pair of formData.entries()) {
                console.error(pair[0] + ': ' + pair[1]);
            }
        });
    });
    
    // Reset form validation khi nhập lại
    const inputs = form.querySelectorAll('input, textarea, select');
    inputs.forEach(input => {
        input.addEventListener('input', function() {
            markFieldValid(this);
        });
    });
}

/**
 * Kiểm tra tất cả các trường dữ liệu trong form
 * @returns {boolean} true nếu form hợp lệ
 */
function validateEntireForm() {
    return validateStep1() && validateStep2();
}

/**
 * Tự động chọn form đăng ký nếu chỉ có một lựa chọn
 */
function autoSelectSingleTemplate() {
    const templateSelect = document.getElementById('templateId');
    if (templateSelect && templateSelect.options.length === 2) {
        // Có 1 option thật + 1 option placeholder "Chọn form đăng ký"
        templateSelect.selectedIndex = 1; // Chọn option thứ 2 (index = 1)
    }
}
