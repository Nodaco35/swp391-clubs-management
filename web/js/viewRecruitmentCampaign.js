document.addEventListener('DOMContentLoaded', function() {
    // Khởi tạo các yếu tố trong trang chi tiết
    initializeStageDetails();
    initializeNotificationForms();
    initializeApplicantActions();
    
    // Khởi tạo toast container nếu chưa có
    initializeToastContainer();
});

// Toast notification - copy từ module chung
function showToast(message, type = 'info') {
    const toastContainer = document.getElementById('toastContainer');
    if (!toastContainer) {
        initializeToastContainer();
    }
    
    // Tạo toast element
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <div class="toast-content">${message}</div>
        <button class="toast-close">&times;</button>
    `;
    
    // Thêm toast vào container
    toastContainer.appendChild(toast);
    

    setTimeout(() => {
        toast.classList.add('fade-in');
    }, 10);
    

    const autoCloseTimeout = setTimeout(() => {
        closeToast(toast);
    }, 3000);
    

    const closeButton = toast.querySelector('.toast-close');
    closeButton.addEventListener('click', () => {
        clearTimeout(autoCloseTimeout);
        closeToast(toast);
    });
}

function closeToast(toast) {
    toast.classList.add('fade-out');
    setTimeout(() => {
        toast.remove();
    }, 300);
}

function initializeToastContainer() {
    if (!document.getElementById('toastContainer')) {
        const toastContainer = document.createElement('div');
        toastContainer.id = 'toastContainer';
        toastContainer.className = 'toast-container';
        document.body.appendChild(toastContainer);
    }
}

// Khởi tạo chi tiết các giai đoạn
function initializeStageDetails() {
    // Xử lý các nút xem chi tiết giai đoạn
    document.querySelectorAll('.view-stage-details').forEach(button => {
        button.addEventListener('click', function() {
            const stageId = this.dataset.id;
            const stageDetailsSection = document.getElementById(`stageDetails-${stageId}`);
            
            if (stageDetailsSection) {
                // Toggle hiển thị
                if (stageDetailsSection.classList.contains('hidden')) {
                    // Đóng tất cả các section khác
                    document.querySelectorAll('.stage-details-section').forEach(section => {
                        if (section !== stageDetailsSection) {
                            section.classList.add('hidden');
                        }
                    });
                    
                    stageDetailsSection.classList.remove('hidden');
                    this.innerHTML = '<i class="fas fa-chevron-up mr-2"></i> Ẩn chi tiết';
                } else {
                    stageDetailsSection.classList.add('hidden');
                    this.innerHTML = '<i class="fas fa-chevron-down mr-2"></i> Xem chi tiết';
                }
            }
        });
    });
}

// Khởi tạo form gửi thông báo
function initializeNotificationForms() {
    // Form gửi thông báo trực tiếp
    const directNotificationForm = document.getElementById('directNotificationForm');
    
    if (directNotificationForm) {
        directNotificationForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            sendNotification(formData, '/recruitment/notification/create');
        });
    }
    
    // Form gửi thông báo từ template
    const templateNotificationForm = document.getElementById('templateNotificationForm');
    
    if (templateNotificationForm) {
        templateNotificationForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            sendNotification(formData, '/recruitment/notification/fromTemplate');
        });
    }
    
    // Xử lý chọn template
    const templateSelect = document.getElementById('templateSelect');
    
    if (templateSelect) {
        templateSelect.addEventListener('change', function() {
            const selectedTemplateId = this.value;
            
            if (selectedTemplateId) {
                fetchTemplateDetails(selectedTemplateId);
            }
        });
    }
}

// Fetch template details
function fetchTemplateDetails(templateId) {
    const contextPath = window.location.pathname.split('/recruitment')[0];
    
    fetch(`${contextPath}/api/templates/${templateId}`)
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Hiển thị preview của template
            const previewContainer = document.getElementById('templatePreview');
            if (previewContainer) {
                previewContainer.innerHTML = `
                    <div class="bg-gray-100 p-4 rounded-lg">
                        <h3 class="font-medium text-lg mb-2">${data.template.title}</h3>
                        <p class="text-gray-700">${data.template.content}</p>
                    </div>
                `;
            }
        } else {
            showToast(data.message || 'Không thể tải thông tin mẫu thông báo', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('Đã xảy ra lỗi khi kết nối với máy chủ', 'error');
    });
}

// Gửi thông báo
function sendNotification(formData, endpoint) {
    const contextPath = window.location.pathname.split('/recruitment')[0];
    
    fetch(`${contextPath}${endpoint}`, {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showToast('Gửi thông báo thành công', 'success');
            
            // Làm mới phần thông báo
            setTimeout(() => {
                window.location.reload();
            }, 1500);
        } else {
            showToast(data.message || 'Không thể gửi thông báo', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('Đã xảy ra lỗi khi kết nối với máy chủ', 'error');
    });
}

// Khởi tạo xử lý ứng viên
function initializeApplicantActions() {
    // Xử lý các nút cập nhật trạng thái ứng viên
    document.querySelectorAll('.update-applicant-status').forEach(button => {
        button.addEventListener('click', function() {
            const applicationStageId = this.dataset.id;
            const status = this.dataset.status;
            
            updateApplicantStatus(applicationStageId, status);
        });
    });
}

// Cập nhật trạng thái ứng viên
function updateApplicantStatus(applicationStageId, status) {
    const contextPath = window.location.pathname.split('/recruitment')[0];
    const formData = new FormData();
    
    formData.append('applicationStageId', applicationStageId);
    formData.append('status', status);
    
    fetch(`${contextPath}/recruitment/application/update`, {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showToast('Cập nhật trạng thái ứng viên thành công', 'success');
            
            // Cập nhật UI
            const applicantRow = document.querySelector(`tr[data-application-id="${applicationStageId}"]`);
            if (applicantRow) {
                const statusCell = applicantRow.querySelector('.applicant-status');
                
                if (statusCell) {
                    // Xóa tất cả các class status hiện tại
                    statusCell.classList.remove('bg-yellow-100', 'text-yellow-800', 'bg-green-100', 'text-green-800', 'bg-red-100', 'text-red-800');
                    
                    // Cập nhật class và text theo trạng thái mới
                    if (status === 'APPROVED') {
                        statusCell.classList.add('bg-green-100', 'text-green-800');
                        statusCell.textContent = 'Đã duyệt';
                    } else if (status === 'REJECTED') {
                        statusCell.classList.add('bg-red-100', 'text-red-800');
                        statusCell.textContent = 'Từ chối';
                    } else if (status === 'PENDING') {
                        statusCell.classList.add('bg-yellow-100', 'text-yellow-800');
                        statusCell.textContent = 'Đang chờ';
                    }
                }
                
                // Cập nhật các nút hành động
                const actionButtons = applicantRow.querySelectorAll('.update-applicant-status');
                actionButtons.forEach(button => {
                    // Disable nút vừa được chọn
                    if (button.dataset.status === status) {
                        button.disabled = true;
                        button.classList.add('opacity-50', 'cursor-not-allowed');
                    } else {
                        button.disabled = false;
                        button.classList.remove('opacity-50', 'cursor-not-allowed');
                    }
                });
            }
        } else {
            showToast(data.message || 'Không thể cập nhật trạng thái', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('Đã xảy ra lỗi khi kết nối với máy chủ', 'error');
    });
}
