/**
 * JavaScript để kiểm tra xem form có phản hồi nào hay không
 */
document.addEventListener('DOMContentLoaded', function() {
    // Lấy clubId từ URL parameter
    const urlParams = new URLSearchParams(window.location.search);
    const clubId = urlParams.get('clubId');
    
    if (!clubId) {
        console.error('clubId không tìm thấy trong URL');
        return;
    }

    // Context path cho URL
    const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 2)) || '';
      // Gửi yêu cầu AJAX để lấy danh sách form có phản hồi
    fetch(`${contextPath}/formResponses`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `action=checkResponses&clubId=${clubId}`
    })    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        // Kiểm tra nếu phản hồi có định dạng ApiResponse
        let templateIdsWithResponses = data;
        if (data && data.hasOwnProperty('success') && data.hasOwnProperty('message')) {
            // Nếu không thành công, hiển thị thông báo lỗi
            if (!data.success) {
                console.error('API error:', data.message);
                return;
            }
            // Nếu có data, sử dụng nó
            if (data.hasOwnProperty('data')) {
                templateIdsWithResponses = data.data;
            } else {
                return; // Không có dữ liệu
            }
        }
        
        if (!Array.isArray(templateIdsWithResponses)) {
            console.error('Invalid response format:', templateIdsWithResponses);
            return;
        }
        
        console.log('Form có phản hồi:', templateIdsWithResponses);
        
        // Lấy tất cả các form card trong tab saved
        const savedTabFormCards = document.querySelectorAll('#saved-tab .form-card');
        
        // Duyệt qua từng form card
        savedTabFormCards.forEach(card => {
            // Lấy templateId của form
            const templateId = card.querySelector('[data-template-id]')?.getAttribute('data-template-id');
            
            if (templateId) {
                // Nếu templateId có trong danh sách form có phản hồi
                if (templateIdsWithResponses.includes(parseInt(templateId))) {
                    // Tạo nút "Xem phản hồi" mới
                    const viewResponsesBtn = document.createElement('button');
                    viewResponsesBtn.className = 'btn btn-info btn-sm view-responses';
                    viewResponsesBtn.setAttribute('data-template-id', templateId);
                    viewResponsesBtn.setAttribute('data-club-id', clubId);
                    viewResponsesBtn.setAttribute('data-form-type', card.getAttribute('data-form-type') === 'Event' ? 'event' : 'member');
                    viewResponsesBtn.innerHTML = '<i class="fas fa-chart-bar"></i> Xem phản hồi';
                    
                    // Thêm sự kiện click cho nút
                    viewResponsesBtn.addEventListener('click', function() {
                        const templateId = this.getAttribute('data-template-id');
                        const clubId = this.getAttribute('data-club-id');
                        const formType = this.getAttribute('data-form-type');
                        window.location.href = `${contextPath}/formResponses?templateId=${templateId}&clubId=${clubId}&formType=${formType}`;
                    });
                    
                    // Thêm nút vào div form-actions
                    const formActions = card.querySelector('.form-actions');
                    if (formActions) {
                        // Thêm nút vào đầu danh sách các nút hành động
                        formActions.insertBefore(viewResponsesBtn, formActions.firstChild);
                    }
                }
            }
        });
    })
    .catch(error => {
        console.error('Error checking form responses:', error);
    });
});
