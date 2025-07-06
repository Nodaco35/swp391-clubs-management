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
    // Hiển thị debug về mode hiện tại
    showDebugInfo('Đang kiểm tra dữ liệu vòng tuyển', {
        isEdit: isEdit,
        mode: isEdit ? 'Chỉnh sửa' : 'Tạo mới',
        hasServerData: (typeof stagesFromServer !== 'undefined' && Array.isArray(stagesFromServer) && stagesFromServer.length > 0)
    });

    // Kiểm tra nếu có biến stagesFromServer đã được định nghĩa từ JSP
    if (typeof stagesFromServer !== 'undefined' && Array.isArray(stagesFromServer) && stagesFromServer.length > 0) {
        console.log('[DEBUG] Dùng dữ liệu vòng tuyển từ server:', stagesFromServer.length + ' vòng');
        
        // Log chi tiết từng vòng tuyển
        stagesFromServer.forEach((stage, index) => {
            console.log(`[DEBUG] Vòng tuyển #${index+1}: ${stage.stageName}, ID: ${stage.stageId}, Thời gian: ${stage.startDate} -> ${stage.endDate}`);
        });
        
        // Log toàn bộ dữ liệu nhận được để debug
        console.log('[DEBUG] Chi tiết tất cả vòng tuyển từ server:', JSON.stringify(stagesFromServer));
        
        // Điền dữ liệu giai đoạn vào form - Sử dụng ENUM values
        stagesFromServer.forEach(stage => {
            // Log chi tiết gồm ID và stageName để debug
            console.log('[DEBUG] Đang xử lý vòng tuyển:', {
                stageName: stage.stageName, 
                stageId: stage.stageId, 
                startDate: stage.startDate,
                endDate: stage.endDate,
                locationId: stage.locationId,
                description: stage.description ? stage.description.substring(0, 30) + '...' : 'không có'
            });
            
            const stageName = String(stage.stageName).toUpperCase().trim();
            
            // Hiển thị loại vòng tuyển đã chuyển đổi
            console.log('[DEBUG] Loại vòng sau khi chuyển đổi:', stageName);
            
            if (stageName === "APPLICATION" || stageName.includes('APPLICATION') || stageName.includes('NỘP ĐƠN')) {
                document.getElementById('applicationStageStart').value = formatDateForInput(new Date(stage.startDate));
                document.getElementById('applicationStageEnd').value = formatDateForInput(new Date(stage.endDate));
                console.log('[DEBUG] Đã điền dữ liệu vòng APPLICATION:', formatDateForInput(new Date(stage.startDate)), formatDateForInput(new Date(stage.endDate)));
            } else if (stageName === "INTERVIEW" || stageName.includes('INTERVIEW') || stageName.includes('PHỎNG VẤN')) {
                document.getElementById('interviewStageStart').value = formatDateForInput(new Date(stage.startDate));
                document.getElementById('interviewStageEnd').value = formatDateForInput(new Date(stage.endDate));
                if (stage.locationId) {
                    document.getElementById('interviewLocationId').value = stage.locationId;
                    console.log('[DEBUG] Đã điền vị trí phỏng vấn:', stage.locationId);
                }
                console.log('[DEBUG] Đã điền dữ liệu vòng INTERVIEW:', formatDateForInput(new Date(stage.startDate)), formatDateForInput(new Date(stage.endDate)));
            } else if (stageName === "CHALLENGE" || stageName.includes('CHALLENGE') || stageName.includes('THỬ THÁCH')) {
                document.getElementById('challengeStageStart').value = formatDateForInput(new Date(stage.startDate));
                document.getElementById('challengeStageEnd').value = formatDateForInput(new Date(stage.endDate));
                if (stage.description) {
                    document.getElementById('challengeDescription').value = stage.description;
                    console.log('[DEBUG] Đã điền mô tả thử thách:', stage.description.substring(0, 30) + (stage.description.length > 30 ? '...' : ''));
                }
                console.log('[DEBUG] Đã điền dữ liệu vòng CHALLENGE:', formatDateForInput(new Date(stage.startDate)), formatDateForInput(new Date(stage.endDate)));
            } else {
                console.warn('[DEBUG] Không nhận diện được loại vòng tuyển:', stage.stageName, '(đã chuyển đổi thành:', stageName, ')');
            }
        });
        
        // Kiểm tra thứ tự thời gian và chồng lấp
        checkStageTimeOverlap();
        
        // Hiển thị thông tin debug về dữ liệu đã nhận
        showDebugInfo('Đã điền dữ liệu từ ' + stagesFromServer.length + ' vòng tuyển', stagesFromServer);
        return;
    }
    
    // Phương án dự phòng nếu không có dữ liệu từ server
    const recruitmentId = document.querySelector('input[name="recruitmentId"]')?.value;
    
    if (!recruitmentId) {
        console.error('[DEBUG] Không tìm thấy ID hoạt động tuyển quân để lấy dữ liệu giai đoạn');
        showDebugInfo('Lỗi', 'Không tìm thấy ID hoạt động tuyển quân để lấy dữ liệu giai đoạn');
        return;
    }
    
    console.log('[DEBUG] Tải dữ liệu vòng tuyển qua API cho recruitmentId:', recruitmentId);
    showDebugInfo('Đang tải dữ liệu vòng tuyển qua API...', { recruitmentId: recruitmentId });
    
    // Lấy dữ liệu các giai đoạn từ server qua API
    fetch(`${getContextPath()}/recruitment/stages?recruitmentId=${recruitmentId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Lỗi khi lấy dữ liệu giai đoạn');
            }
            return response.json();
        })
        .then(data => {
            if (data && data.success && data.stages && Array.isArray(data.stages)) {
                console.log('[DEBUG] Nhận được dữ liệu từ API:', data);
                
                // Log toàn bộ dữ liệu nhận được từ API để debug
                console.log('[DEBUG] Chi tiết tất cả vòng tuyển từ API:', JSON.stringify(data.stages));
                
                // Điền dữ liệu giai đoạn vào form - Sử dụng ENUM values
                data.stages.forEach(stage => {
                    // Log chi tiết gồm ID và stageName để debug
                    console.log('[DEBUG] API - Đang xử lý vòng tuyển:', {
                        stageName: stage.stageName, 
                        stageId: stage.stageId || stage.stageID, 
                        startDate: stage.startDate,
                        endDate: stage.endDate,
                        locationId: stage.locationId || stage.locationID,
                        description: stage.description ? stage.description.substring(0, 30) + '...' : 'không có'
                    });
                    
                    const stageName = String(stage.stageName).toUpperCase().trim();
                    
                    // Hiển thị loại vòng tuyển đã chuyển đổi
                    console.log('[DEBUG] API - Loại vòng sau khi chuyển đổi:', stageName);
                    
                    if (stageName === "APPLICATION" || stageName.includes('APPLICATION') || stageName.includes('NỘP ĐƠN')) {
                        document.getElementById('applicationStageStart').value = formatDateForInput(new Date(stage.startDate));
                        document.getElementById('applicationStageEnd').value = formatDateForInput(new Date(stage.endDate));
                        console.log('[DEBUG] Đã điền dữ liệu vòng APPLICATION từ API:', formatDateForInput(new Date(stage.startDate)), formatDateForInput(new Date(stage.endDate)));
                    } else if (stageName === "INTERVIEW" || stageName.includes('INTERVIEW') || stageName.includes('PHỎNG VẤN')) {
                        document.getElementById('interviewStageStart').value = formatDateForInput(new Date(stage.startDate));
                        document.getElementById('interviewStageEnd').value = formatDateForInput(new Date(stage.endDate));
                        if (stage.locationID || stage.locationId) {
                            document.getElementById('interviewLocationId').value = stage.locationID || stage.locationId;
                            console.log('[DEBUG] Đã điền vị trí phỏng vấn từ API:', stage.locationID || stage.locationId);
                        }
                        console.log('[DEBUG] Đã điền dữ liệu vòng INTERVIEW từ API:', formatDateForInput(new Date(stage.startDate)), formatDateForInput(new Date(stage.endDate)));
                    } else if (stageName === "CHALLENGE" || stageName.includes('CHALLENGE') || stageName.includes('THỬ THÁCH')) {
                        document.getElementById('challengeStageStart').value = formatDateForInput(new Date(stage.startDate));
                        document.getElementById('challengeStageEnd').value = formatDateForInput(new Date(stage.endDate));
                        if (stage.description) {
                            document.getElementById('challengeDescription').value = stage.description;
                            console.log('[DEBUG] Đã điền mô tả thử thách từ API:', stage.description.substring(0, 30) + (stage.description.length > 30 ? '...' : ''));
                        }
                        console.log('[DEBUG] Đã điền dữ liệu vòng CHALLENGE từ API:', formatDateForInput(new Date(stage.startDate)), formatDateForInput(new Date(stage.endDate)));
                    } else {
                        console.warn('[DEBUG] API - Không nhận diện được loại vòng tuyển:', stage.stageName, '(đã chuyển đổi thành:', stageName, ')');
                    }
                });
                
                // Kiểm tra thứ tự thời gian và chồng lấp
                checkStageTimeOverlap();
                
                // Hiển thị thông tin debug
                showDebugInfo('Đã lấy dữ liệu từ API: ' + data.stages.length + ' vòng tuyển', data);
            } else {
                console.error('Dữ liệu giai đoạn không hợp lệ:', data);
                showDebugInfo('Lỗi dữ liệu: Không lấy được dữ liệu vòng tuyển từ API', data);
            }
        })
        .catch(error => {
            console.error('Lỗi khi lấy dữ liệu giai đoạn:', error);
            showDebugInfo('Lỗi khi lấy dữ liệu giai đoạn: ' + error.message);
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
    console.log("Đang điền thông tin vào màn hình xác nhận...");
    try {
        // Hàm bổ trợ kiểm tra phần tử an toàn
        function setConfirmText(elementId, value) {
            const element = document.getElementById(elementId);
            if (element) {
                element.textContent = value || '';
            } else {
                console.error(`Không tìm thấy phần tử '${elementId}' để hiển thị kết quả`);
            }
        }
        
        // Hàm lấy giá trị an toàn từ phần tử input
        function getSafeElementValue(id) {
            const element = document.getElementById(id);
            return element && element.value ? element.value : null;
        }
        
        // Debug thông tin vòng tuyển để kiểm tra dữ liệu
        const stageData = collectStageData();
        console.log("DEBUG - Thông tin vòng tuyển gửi lên server:", stageData);
        
        // Thông tin cơ bản
        const genValue = getSafeElementValue('gen');
        if (genValue) {
            setConfirmText('confirmGen', genValue);
        } else {
            console.error("Không tìm thấy phần tử 'gen' hoặc không có giá trị");
            setConfirmText('confirmGen', 'Không có thông tin');
        }
        
        const startDateValue = getSafeElementValue('startDate');
        const endDateValue = getSafeElementValue('endDate');
        if (startDateValue && endDateValue) {
            const startDate = new Date(startDateValue);
            const endDate = new Date(endDateValue);
            if (!isNaN(startDate.getTime()) && !isNaN(endDate.getTime())) {
                setConfirmText('confirmTime', `${formatDate(startDate)} - ${formatDate(endDate)}`);
            } else {
                setConfirmText('confirmTime', 'Ngày không hợp lệ');
            }
        } else {
            console.error("Không tìm thấy phần tử 'startDate' hoặc 'endDate', hoặc chúng không có giá trị");
            setConfirmText('confirmTime', 'Không có thông tin');
        }
        
        const templateSelect = document.getElementById('templateId');
        if (templateSelect && templateSelect.selectedIndex >= 0 && 
            templateSelect.options && templateSelect.selectedIndex < templateSelect.options.length) {
            const selectedTemplate = templateSelect.options[templateSelect.selectedIndex].text;
            setConfirmText('confirmForm', selectedTemplate);
        } else {
            console.error("Không tìm thấy phần tử 'templateId' hoặc chưa chọn mẫu đơn");
            setConfirmText('confirmForm', 'Không có thông tin');
        }
        
        // Vòng nộp đơn - APPLICATION
        const appStartValue = getSafeElementValue('applicationStageStart');
        const appEndValue = getSafeElementValue('applicationStageEnd');
        if (appStartValue && appEndValue) {
            const appStartDate = new Date(appStartValue);
            const appEndDate = new Date(appEndValue);
            if (!isNaN(appStartDate.getTime()) && !isNaN(appEndDate.getTime())) {
                setConfirmText('confirmApplicationStage', `${formatDate(appStartDate)} - ${formatDate(appEndDate)}`);
            } else {
                setConfirmText('confirmApplicationStage', 'Ngày không hợp lệ');
            }
        } else {
            console.error("Không tìm thấy phần tử 'applicationStageStart' hoặc 'applicationStageEnd', hoặc chúng không có giá trị");
            setConfirmText('confirmApplicationStage', 'Không có thông tin');
        }
        
        // Vòng phỏng vấn - INTERVIEW
        const intStartValue = getSafeElementValue('interviewStageStart');
        const intEndValue = getSafeElementValue('interviewStageEnd');
        const locationSelect = document.getElementById('interviewLocationId');
        
        if (intStartValue && intEndValue) {
            const intStartDate = new Date(intStartValue);
            const intEndDate = new Date(intEndValue);
            if (!isNaN(intStartDate.getTime()) && !isNaN(intEndDate.getTime())) {
                let locationText = '';
                
                if (locationSelect && locationSelect.selectedIndex > 0 && 
                    locationSelect.options && locationSelect.selectedIndex < locationSelect.options.length) {
                    locationText = ` (${locationSelect.options[locationSelect.selectedIndex].text})`;
                }
                
                setConfirmText('confirmInterviewStage', `${formatDate(intStartDate)} - ${formatDate(intEndDate)}${locationText}`);
            } else {
                setConfirmText('confirmInterviewStage', 'Ngày không hợp lệ');
            }
        } else {
            console.error("Không tìm thấy phần tử 'interviewStageStart' hoặc 'interviewStageEnd', hoặc chúng không có giá trị");
            setConfirmText('confirmInterviewStage', 'Không có thông tin');
        }
        
        // Vòng thử thách - CHALLENGE
        const chalStartValue = getSafeElementValue('challengeStageStart');
        const chalEndValue = getSafeElementValue('challengeStageEnd');
        const chalDescValue = getSafeElementValue('challengeDescription');
        
        if (chalStartValue && chalEndValue) {
            const chalStartDate = new Date(chalStartValue);
            const chalEndDate = new Date(chalEndValue);
            if (!isNaN(chalStartDate.getTime()) && !isNaN(chalEndDate.getTime())) {
                let challengeText = `${formatDate(chalStartDate)} - ${formatDate(chalEndDate)}`;
                
                if (chalDescValue) {
                    challengeText += ` (${chalDescValue})`;
                }
                
                setConfirmText('confirmChallengeStage', challengeText);
            } else {
                setConfirmText('confirmChallengeStage', 'Ngày không hợp lệ');
            }
        } else {
            console.error("Không tìm thấy phần tử 'challengeStageStart' hoặc 'challengeStageEnd', hoặc chúng không có giá trị");
            setConfirmText('confirmChallengeStage', 'Không có thông tin (tùy chọn)');
        }
        
        console.log("Đã điền thông tin vào màn hình xác nhận thành công");
    } catch (error) {
        console.error("Lỗi khi điền thông tin vào màn hình xác nhận:", error);
        
        // Hiển thị debug panel với thông tin lỗi
        showDebugInfo('Lỗi khi điền thông tin xác nhận', {
            error: error.message,
            stack: error.stack,
            stageData: collectStageData()
        });
    }
}

// Hàm bổ sung để thu thập dữ liệu vòng tuyển để debug
function collectStageData() {
    const stageData = {
        application: {
            start: document.getElementById('applicationStageStart')?.value || null,
            end: document.getElementById('applicationStageEnd')?.value || null,
            stageName: "APPLICATION" // Đảm bảo sử dụng đúng ENUM value
        },
        interview: {
            start: document.getElementById('interviewStageStart')?.value || null,
            end: document.getElementById('interviewStageEnd')?.value || null,
            locationId: document.getElementById('interviewLocationId')?.value || null,
            stageName: "INTERVIEW" // Đảm bảo sử dụng đúng ENUM value
        },
        challenge: {
            start: document.getElementById('challengeStageStart')?.value || null,
            end: document.getElementById('challengeStageEnd')?.value || null,
            description: document.getElementById('challengeDescription')?.value || null,
            stageName: "CHALLENGE" // Đảm bảo sử dụng đúng ENUM value
        }
    };
    return stageData;
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
    
    if (!form) {
        console.error("Không tìm thấy form với id 'recruitmentForm'");
        return;
    }
    
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        console.log("Đang gửi form...");
        
        // Xác thực form trước khi gửi
        if (!validateEntireForm()) {
            showToast('Vui lòng điền đầy đủ thông tin cần thiết', 'error');
            console.error("Validation thất bại");
            return;
        }
        
        const formData = new FormData(form);
        
        // Debug: Log tất cả dữ liệu form
        console.log("Dữ liệu form sẽ gửi:");
        for (let pair of formData.entries()) {
            console.log(pair[0] + ": " + pair[1]);
        }
        
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
        
        // Debug: Log dữ liệu gửi đi
        console.log("Đang gửi request đến:", finalUrl);
        console.log("Dữ liệu gửi đi:", urlParams.toString());
        
        // Log các thông tin về vòng tuyển
        console.log("Thông tin vòng tuyển:");
        console.log("- Vòng nộp đơn:", urlParams.get('applicationStageStart'), "đến", urlParams.get('applicationStageEnd'));
        console.log("- Vòng phỏng vấn:", urlParams.get('interviewStageStart'), "đến", urlParams.get('interviewStageEnd'));
        console.log("- Vòng thử thách:", urlParams.get('challengeStageStart'), "đến", urlParams.get('challengeStageEnd'));
        console.log("- Mô tả thử thách:", urlParams.get('challengeDescription') || '(Không có)');
        
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
            console.log("Response từ server:", data);
            
            if (data.success) {
                showToast(isEdit ? 'Cập nhật hoạt động thành công' : 'Tạo hoạt động mới thành công', 'success');
                
                // Debug: Log các thông tin về stages trả về (nếu có)
                if (data.stagesCreated) {
                    console.log("Số vòng tuyển đã tạo:", data.stagesCreated);
                }
                
                if (data.stages) {
                    console.log("Thông tin các vòng tuyển:", data.stages);
                }
                
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
            
            // Log chi tiết về các vòng tuyển để debug
            console.error('Thông tin các vòng tuyển:');
            console.error('- Vòng nộp đơn:', formData.get('applicationStageStart'), 'đến', formData.get('applicationStageEnd'));
            console.error('- Vòng phỏng vấn:', formData.get('interviewStageStart'), 'đến', formData.get('interviewStageEnd'), 
                         'tại địa điểm ID:', formData.get('interviewLocationId') || '(Không có)');
            console.error('- Vòng thử thách:', formData.get('challengeStageStart'), 'đến', formData.get('challengeStageEnd'),
                         'mô tả:', formData.get('challengeDescription') || '(Không có)');
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

/**
 * Kiểm tra chồng lấn thời gian giữa các vòng tuyển
 * @returns {Object} Kết quả kiểm tra với các thuộc tính: valid, message
 */
function checkStageOverlap() {
    // Thu thập dữ liệu thời gian của các vòng
    const stages = [
        {
            name: 'Nộp đơn',
            startDate: new Date(document.getElementById('applicationStageStart').value),
            endDate: new Date(document.getElementById('applicationStageEnd').value)
        },
        {
            name: 'Phỏng vấn',
            startDate: new Date(document.getElementById('interviewStageStart').value),
            endDate: new Date(document.getElementById('interviewStageEnd').value)
        },
        {
            name: 'Thử thách',
            startDate: new Date(document.getElementById('challengeStageStart').value),
            endDate: new Date(document.getElementById('challengeStageEnd').value)
        }
    ];
    
    // Lọc bỏ các vòng không có dữ liệu
    const validStages = stages.filter(stage => 
        !isNaN(stage.startDate) && !isNaN(stage.endDate)
    );
    
    // Sắp xếp theo ngày bắt đầu
    validStages.sort((a, b) => a.startDate - b.startDate);
    
    // Kiểm tra chồng lấn
    for (let i = 0; i < validStages.length - 1; i++) {
        const current = validStages[i];
        const next = validStages[i + 1];
        
        // Kiểm tra nếu ngày kết thúc của vòng hiện tại > ngày bắt đầu của vòng tiếp theo
        if (current.endDate > next.startDate) {
            console.error('Phát hiện chồng lấn thời gian:', {
                stage1: current,
                stage2: next
            });
            return {
                valid: false,
                message: `Thời gian chồng lấn giữa vòng ${current.name} và vòng ${next.name}. Vòng ${current.name} kết thúc sau khi vòng ${next.name} bắt đầu.`
            };
        }
    }
    
    return { valid: true };
}

/**
 * Kiểm tra chồng lấn thời gian các vòng tuyển
 */
function checkStageTimeOverlap() {
    // Lấy các trường ngày tháng
    const appStart = document.getElementById('applicationStageStart').value;
    const appEnd = document.getElementById('applicationStageEnd').value;
    const interviewStart = document.getElementById('interviewStageStart').value;
    const interviewEnd = document.getElementById('interviewStageEnd').value;
    const challengeStart = document.getElementById('challengeStageStart').value;
    const challengeEnd = document.getElementById('challengeStageEnd').value;
    
    // Kiểm tra các trường có dữ liệu không
    if (!appStart || !appEnd || !interviewStart || !interviewEnd) {
        console.log("[DEBUG] Thiếu dữ liệu cho một số vòng tuyển, không thể kiểm tra chồng lấn thời gian");
        return;
    }
    
    // Chuyển thành đối tượng Date
    const appStartDate = new Date(appStart);
    const appEndDate = new Date(appEnd);
    const interviewStartDate = new Date(interviewStart);
    const interviewEndDate = new Date(interviewEnd);
    const challengeStartDate = challengeStart ? new Date(challengeStart) : null;
    const challengeEndDate = challengeEnd ? new Date(challengeEnd) : null;
    
    const warnings = [];
    
    // Kiểm tra thứ tự thời gian của từng vòng
    if (appStartDate > appEndDate) {
        warnings.push("Ngày bắt đầu vòng Nộp đơn phải trước ngày kết thúc");
    }
    
    if (interviewStartDate > interviewEndDate) {
        warnings.push("Ngày bắt đầu vòng Phỏng vấn phải trước ngày kết thúc");
    }
    
    if (challengeStartDate && challengeEndDate && challengeStartDate > challengeEndDate) {
        warnings.push("Ngày bắt đầu vòng Thử thách phải trước ngày kết thúc");
    }
    
    // Kiểm tra thứ tự giữa các vòng
    if (interviewStartDate < appEndDate) {
        warnings.push("Vòng Phỏng vấn nên bắt đầu sau khi vòng Nộp đơn kết thúc");
    }
    
    if (challengeStartDate && challengeStartDate < interviewEndDate) {
        warnings.push("Vòng Thử thách nên bắt đầu sau khi vòng Phỏng vấn kết thúc");
    }
    
    // Hiển thị kết quả debug
    if (warnings.length > 0) {
        console.log("[DEBUG] Phát hiện vấn đề thời gian vòng tuyển:", warnings);
        showDebugInfo("Cảnh báo về thời gian vòng tuyển", warnings);
    } else {
        console.log("[DEBUG] Thứ tự thời gian các vòng tuyển OK");
    }
}
