/**
 * Recruitment Common Module - Chứa các chức năng dùng chung cho các trang liên quan đến tuyển quân
 */

/**
 * Hiển thị thông báo toast
 * @param {string} message - Nội dung thông báo
 * @param {string} type - Loại thông báo (success, error, warning, info)
 */
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
    
    // Animation hiển thị
    setTimeout(() => {
        toast.classList.add('fade-in');
    }, 10);
    
    // Tự động đóng sau 3 giây
    const autoCloseTimeout = setTimeout(() => {
        closeToast(toast);
    }, 3000);
    
    // Xử lý sự kiện đóng toast
    const closeButton = toast.querySelector('.toast-close');
    closeButton.addEventListener('click', () => {
        clearTimeout(autoCloseTimeout);
        closeToast(toast);
    });
}

/**
 * Đóng thông báo toast
 * @param {HTMLElement} toast - Element toast cần đóng
 */
function closeToast(toast) {
    toast.classList.add('fade-out');
    setTimeout(() => {
        toast.remove();
    }, 300);
}

/**
 * Khởi tạo toast container nếu chưa tồn tại
 */
function initializeToastContainer() {
    if (!document.getElementById('toastContainer')) {
        const toastContainer = document.createElement('div');
        toastContainer.id = 'toastContainer';
        toastContainer.className = 'toast-container';
        document.body.appendChild(toastContainer);
    }
}

/**
 * Format date từ ISO string sang định dạng ngày/tháng/năm
 * @param {string} dateString - Chuỗi ngày tháng cần format
 * @returns {string} Ngày tháng đã được format
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getFullYear()}`;
}

/**
 * Lấy context path của ứng dụng
 * @returns {string} Context path
 */
function getContextPath() {
    return window.location.pathname.split('/recruitment')[0];
}


