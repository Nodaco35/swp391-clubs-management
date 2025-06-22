

// Hàm hiển thị thông báo toast
function showToast(message, type, duration) {
    const toastContainer = document.getElementById('toastContainer');
    
    // Tạo phần tử toast
    const toast = document.createElement('div');
    toast.className = "toast toast-" + type;
    
    // Tạo nội dung thông báo và nút đóng
    const toastContent = document.createElement('span');
    toastContent.textContent = message;
    
    const closeBtn = document.createElement('button');
    closeBtn.className = 'toast-close';
    closeBtn.innerHTML = '&times;';
    closeBtn.onclick = function() {
        toast.classList.add('fade-out');
        setTimeout(function() { toast.remove(); }, 300);
    };
    

    toast.appendChild(toastContent);
    toast.appendChild(closeBtn);
    toastContainer.appendChild(toast);
    
    // Hiệu ứng hiện toast
    setTimeout(function() {
        toast.style.opacity = '1';
    }, 10);
    

    setTimeout(function() {
        toast.classList.add('fade-out');
        setTimeout(function() { toast.remove(); }, 300);
    }, duration);
    
    return toast;
}

document.addEventListener('DOMContentLoaded', function() {
    // Kiểm tra thông báo từ server
    if (typeof success !== 'undefined' && success) {
        const toast = showToast(success, "success", 3000);
        
        // Tự động chuyển hướng sau khi hiển thị toast
        setTimeout(function() {
            window.location.href = contextPath + "/event-detail?id=" + eventId;
        }, 3500);
    }
    
    if (typeof error !== 'undefined' && error) {
        showToast(error, "error", 5000);
    }
    
    const ratingInputs = document.querySelectorAll('input[name="rating"]');
    const detailedRatingGroups = document.querySelectorAll('.rating-options');
    
    // Kích hoạt hiệu ứng hover cho đánh giá sao
    const stars = document.querySelectorAll('.rating-group label');
    stars.forEach(star => {
        star.addEventListener('mouseenter', () => {
            const starValue = star.getAttribute('for').split('-')[1];
            highlightStars(starValue);
        });
        
        star.addEventListener('mouseleave', () => {
            resetStars();
            const checkedStar = document.querySelector('input[name="rating"]:checked');
            if (checkedStar) {
                const starValue = checkedStar.id.split('-')[1];
                highlightStars(starValue);
            }
        });
    });
    
    function highlightStars(value) {
        stars.forEach(s => {
            const starVal = s.getAttribute('for').split('-')[1];
            if (starVal <= value) {
                s.style.color = '#ffca08';
            } else {
                s.style.color = '#ddd';
            }
        });
    }
    
    function resetStars() {
        stars.forEach(s => {
            s.style.color = '#ddd'; // Đặt lại màu của tất cả các sao về xám
        });
    }

    // Xử lý kiểm tra hợp lệ khi gửi form
    const form = document.querySelector('form');
    
    // Kiểm tra xem đã có container lỗi chưa
    let errorContainer = document.querySelector('.feedback-error');
    if (!errorContainer) {
        // Tạo container cho thông báo lỗi
        errorContainer = document.createElement('div');
        errorContainer.className = 'feedback-error';
        errorContainer.style.display = 'none';
        
        
        form.insertBefore(errorContainer, form.firstChild);
    }
    

    const detailedRatings = [
        'q1_organization', 'q2_communication', 'q3_support',
        'q4_relevance', 'q5_welcoming', 'q6_value',
        'q7_timing', 'q8_participation', 'q9_willingnessToReturn'
    ];
    
    // Xử lý sự kiện khi form được submit
    form.addEventListener('submit', function(e) {
        e.preventDefault(); // Ngăn chặn hành vi submit mặc định
        
        // Xóa thông báo lỗi cũ
        errorContainer.textContent = '';
        errorContainer.style.display = 'none';
        
        
        const errors = [];
        
        // Kiểm tra xem đánh giá tổng quan đã được chọn chưa
        if (!document.querySelector('input[name="rating"]:checked')) {
            errors.push("Vui lòng chọn đánh giá tổng quan");
        }
        
        // Kiểm tra các đánh giá chi tiết
        let missingRatings = 0;
        let firstMissingElement = null;
        
        detailedRatings.forEach(rating => {
            if (!document.querySelector(`input[name="${rating}"]:checked`)) {
                missingRatings++;
                if (!firstMissingElement) {
                    firstMissingElement = document.querySelector(`input[name="${rating}"]`).closest('.rating-item');
                }
            }
        });
        
        if (missingRatings > 0) {
            errors.push(`Vui lòng hoàn thành tất cả ${missingRatings} câu hỏi đánh giá chi tiết`);
        }
        
        // Nếu có lỗi
        if (errors.length > 0) {
            // Hiển thị thông báo lỗi
            errorContainer.textContent = errors.join('. ');
            errorContainer.style.display = 'block';
            
            // Cuộn lên trên để người dùng thấy lỗi
            errorContainer.scrollIntoView({ behavior: 'smooth' });
            
            
            if (firstMissingElement) {
                firstMissingElement.classList.add('highlight-missing');
                setTimeout(() => {
                    firstMissingElement.classList.remove('highlight-missing');
                }, 2000);
            }
        } else {
            // Nếu không có lỗi, tiếp tục gửi form
            form.submit();
        }
    });
    
    // Xử lý sự kiện khi người dùng chọn radio button
    document.querySelectorAll('input[type="radio"]').forEach(radio => {
        radio.addEventListener('change', function() {
            // Nếu có thông báo lỗi và người dùng đã chọn tất cả các trường, ẩn thông báo lỗi
            if (errorContainer.style.display === 'block') {
                const missingRatings = detailedRatings.filter(
                    rating => !document.querySelector(`input[name="${rating}"]:checked`)
                ).length;
                
                const overallRating = document.querySelector('input[name="rating"]:checked');
                
                if (missingRatings === 0 && overallRating) {
                    errorContainer.style.display = 'none';
                }
            }
        });
    });
});