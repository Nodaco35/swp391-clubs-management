document.addEventListener('DOMContentLoaded', function() {
    // Lấy context path từ URL
    const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 2));
    
    // Kiểm tra xem có lỗi từ server không
    const errorMessageElem = document.getElementById('errorMessageData');
    if (errorMessageElem && errorMessageElem.value) {
        showToast('error', 'Lỗi', errorMessageElem.value);
    }
    
    const urlParams = new URLSearchParams(window.location.search);
    const templateId = urlParams.get('templateId');
    const urlClubId = urlParams.get('clubId');
    const urlFormType = urlParams.get('formType');
    
    // Lấy formType từ URL hoặc từ trường ẩn trong HTML 
    const formTypeInput = document.getElementById('formTypeInput');
    const inputFormType = formTypeInput ? formTypeInput.value : 'not found';

    let formType = (urlFormType || inputFormType || 'member').toLowerCase();
    

    if (formType !== 'event' && formType !== 'member') {
        formType = 'member'; // Mặc định là member nếu giá trị không hợp lệ
    }
    
    // Lấy clubId từ input hidden hoặc URL
    const clubIdInput = document.getElementById('clubIdInput');
    const clubId = clubIdInput && clubIdInput.value ? clubIdInput.value : urlClubId;
    
    console.log("DEBUG parameters:");
    console.log("- URL formType parameter:", urlFormType);
    console.log("- Hidden input formType value:", inputFormType);
    console.log("- Final formType being used:", formType);
    console.log("- clubId:", clubId);
    console.log("- templateId:", templateId);
    console.log("- URL parameters:", window.location.search);
    console.log("- Complete URL:", window.location.href);
    
    if (!templateId || !clubId) {
        console.error('Không tìm thấy templateId hoặc clubId');
        showToast('error', 'Lỗi', 'Không tìm thấy thông tin form');
        return;
    }
    

    const responsesList = document.getElementById('responsesList');
    const searchInput = document.getElementById('searchInput');
    const modal = document.getElementById('responseModal');
    const closeModal = document.querySelector('.close-modal');
    const filterTabs = document.querySelectorAll('.tab-btn');
    
    
    // Kiểm tra xem có dữ liệu từ server không
    let serverData = null;
    const applicationsDataElem = document.getElementById('applicationsData');
    
    if (applicationsDataElem && applicationsDataElem.value) {
        try {
            serverData = JSON.parse(applicationsDataElem.value);
            console.log("Loaded data from server:", serverData);
        } catch (e) {
            console.error("Error parsing server data:", e);
        }
    }
    
    // Chuyển đổi dữ liệu từ server thành định dạng cho frontend
    let responses = [];
    if (serverData && Array.isArray(serverData)) {
        responses = serverData.map(app => {
            // Parse responses JSON nếu chưa được parse
            let answers = [];
            console.log("Processing app response:", app.responseId, "Responses type:", typeof app.responses);
            
            if (app.responses && typeof app.responses === 'string') {
                try {
                    console.log("Parsing response string:", app.responses);
                    answers = JSON.parse(app.responses);
                    console.log("Successfully parsed answers:", answers);
                } catch (e) {
                    console.error("Error parsing responses JSON:", e);
                    // Try to handle empty or partial JSON
                    if (app.responses === "[]" || app.responses === "{}") {
                        answers = [];
                    }
                }
            } else if (Array.isArray(app.responses)) {
                answers = app.responses;
                console.log("Responses was already an array:", answers);
            } else {
                console.log("No valid responses found for ID:", app.responseId);
            }
            
            // Convert status từ database thành frontend format
            let status = app.responseStatus ? app.responseStatus.toLowerCase() : 'waiting';
            if (status === 'pending') status = 'waiting';
            
            // Đảm bảo các giá trị không bị null/undefined và có thể tìm kiếm được
            const responseObj = {
                id: app.responseId ? app.responseId.toString() : '',
                name: app.fullName || 'Người dùng',
                email: app.email || '',
                userId: app.userId || '',
                submittedAt: app.submitDate || new Date().toISOString(),
                status: status,
                answers: answers
            };
            
            if (!responseObj.name || !responseObj.email || !responseObj.userId) {
                console.log("Chú ý: Dữ liệu không đầy đủ cho response ID:", responseObj.id);
            }
            
            return responseObj;
        });
        
        console.log("Converted server data to frontend format:", responses);
    } else {
        console.log("Không có dữ liệu từ server");
    }
    let currentFilter = 'all';
    let searchTerm = '';
    

    updateStatistics(responses);
    
    // Hiển thị responses ban đầu
    renderResponses(responses);
    
    // Xử lý tìm kiếm
    if (!searchInput) {
        console.error("Không tìm thấy phần tử searchInput. Kiểm tra ID trong HTML.");
    } else {
        console.log("Đã tìm thấy phần tử searchInput, đang thiết lập sự kiện.");
        
        // Thêm cả hai cách gán sự kiện để đảm bảo hoạt động trên nhiều trình duyệt
        searchInput.oninput = function() {
            console.log("Tìm kiếm với từ khóa: " + this.value);
            searchTerm = this.value.toLowerCase().trim();
            filterAndRenderResponses();
        };
        
        searchInput.addEventListener('input', function() {
            searchTerm = this.value.toLowerCase().trim();
            filterAndRenderResponses();
        });
    }
    
    // Xử lý filter tabs
    filterTabs.forEach(tab => {
        tab.addEventListener('click', function() {
            filterTabs.forEach(t => t.classList.remove('active'));
            this.classList.add('active');
            currentFilter = this.dataset.filter;
            filterAndRenderResponses();
        });
    });
    

    closeModal.addEventListener('click', function() {
        modal.style.display = 'none';
    });
    
    // Đóng modal khi click bên ngoài modal
    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
    
    //chuyển đổi status code thành text hiển thị
    function getStatusText(status) {
        switch(status) {
            case 'waiting': return 'Chờ duyệt';
            case 'approved': return 'Đã duyệt';
            case 'rejected': return 'Từ chối';
            case 'candidate': return 'Ứng viên';
            case 'collaborator': return 'Cộng tác viên';
            case 'all': return 'Tất cả';
            default: return status;
        }
    }
    
    // Xử lý lọc và render responses
    function filterAndRenderResponses() {
        console.log("Bắt đầu lọc dữ liệu. Từ khóa tìm kiếm:", searchTerm);
        let filteredResponses = responses;
        
        // Lọc theo tab
        if (currentFilter !== 'all') {
            filteredResponses = responses.filter(response => response.status === currentFilter);
            console.log(`Đã lọc theo tab ${currentFilter}: ${filteredResponses.length} kết quả`);
        }
        
        // Lọc theo tìm kiếm
        if (searchTerm) {
            try {
                filteredResponses = filteredResponses.filter(response => {
                    const name = (response.name || '').toLowerCase();
                    const email = (response.email || '').toLowerCase();
                    const userId = (response.userId || '').toLowerCase();
                
                    const nameMatch = name.includes(searchTerm);
                    const emailMatch = email.includes(searchTerm);
                    const userIdMatch = userId.includes(searchTerm);
                    
                    const isMatch = nameMatch || emailMatch || userIdMatch;
                    
                    if (isMatch) {
                        console.log(`Đã tìm thấy kết quả khớp: ${response.name} (${response.email})`);
                    }
                    
                    return isMatch;
                });
                
                console.log(`Sau khi tìm kiếm với từ khóa "${searchTerm}": ${filteredResponses.length} kết quả`);
            } catch (error) {
                console.error("Lỗi khi thực hiện tìm kiếm:", error);
                filteredResponses = responses;
            }
        }
        
        // Render kết quả ra giao diện
        renderResponses(filteredResponses);
    }
    
    // Render danh sách responses
    function renderResponses(dataToRender) {
        if (!dataToRender || dataToRender.length === 0) {
            let message = '';
            
            // Nếu đang tìm kiếm, hiển thị thông báo không tìm thấy kết quả tìm kiếm
            if (searchTerm) {
                message = `Không tìm thấy đơn đăng ký nào phù hợp với từ khóa "${searchTerm}"`;
                if (currentFilter !== 'all') {
                    message += ` trong danh mục "${getStatusText(currentFilter)}"`;
                }
            } else {
                // Nếu không tìm kiếm, hiển thị thông báo không có đơn trong danh mục
                message = `Không có đơn đăng ký nào ${currentFilter !== 'all' ? 'trong danh mục này' : ''}`;
            }
            
            responsesList.innerHTML = `
                <div class="no-responses-message">
                    <i class="fas fa-inbox"></i>
                    <p>${message}</p>
                    ${searchTerm ? '<p style="font-size:0.9em;opacity:0.8;margin-top:10px;">Vui lòng thử từ khóa khác hoặc xóa bộ lọc tìm kiếm</p>' : ''}
                </div>
            `;
            return;
        }
        
        responsesList.innerHTML = '';
        
        dataToRender.forEach(response => {
            const responseItem = document.createElement('div');
            responseItem.className = 'response-item';
            responseItem.dataset.id = response.id;
            
            // Xác định CSS class cho trạng thái
            let statusClass = '';
            let statusText = '';
            
            switch (response.status) {
                case 'waiting':
                    statusClass = 'status-waiting';
                    statusText = 'Chờ duyệt';
                    break;
                case 'approved':
                    statusClass = 'status-approved';
                    statusText = 'Đã duyệt';
                    break;
                case 'rejected':
                    statusClass = 'status-rejected';
                    statusText = 'Từ chối';
                    break;
                case 'candidate':
                    statusClass = 'status-candidate';
                    statusText = 'Ứng viên';
                    break;
                case 'collaborator':
                    statusClass = 'status-collaborator';
                    statusText = 'Cộng tác viên';
                    break;
            }
            
            let actionButtons = '';
            
            // Tạo nút hành động dựa trên trạng thái
            actionButtons = '';
            
            // Nút từ chối xuất hiện trong mọi trạng thái trừ "từ chối" và "đã duyệt"
            if (response.status !== 'rejected' && response.status !== 'approved') {
                actionButtons += `
                    <button class="action-btn btn-reject" data-action="reject" data-id="${response.id}">
                        <i class="fas fa-times"></i> Từ chối
                    </button>
                `;
            }
            
            // Phân biệt xử lý dựa trên loại form
            if (formType === 'event') {
                // Đơn sự kiện: chỉ có nút duyệt ở trạng thái "chờ duyệt"
                if (response.status === 'waiting') {
                    actionButtons += `
                        <button class="action-btn btn-approve" data-action="approve" data-id="${response.id}">
                            <i class="fas fa-check"></i> Duyệt
                        </button>
                    `;
                }
            } else {
                // Đơn member: có các nút duyệt theo trình tự
                if (response.status === 'waiting') {
                    actionButtons += `
                        <button class="action-btn btn-approve" data-action="approve" data-id="${response.id}">
                            <i class="fas fa-check"></i> Duyệt
                        </button>
                    `;
                } else if (response.status === 'candidate') {
                    actionButtons += `
                        <button class="action-btn btn-promote" data-action="approve" data-id="${response.id}">
                            <i class="fas fa-user-plus"></i> Nâng cấp
                        </button>
                    `;
                } else if (response.status === 'collaborator') {
                    actionButtons += `
                        <button class="action-btn btn-promote" data-action="approve" data-id="${response.id}">
                            <i class="fas fa-user-plus"></i> Nâng cấp
                        </button>
                    `;
                }
            }
            
            actionButtons += `
                <button class="action-btn btn-details" data-action="details" data-id="${response.id}">
                    <i class="fas fa-eye"></i> Chi tiết
                </button>
            `;
            
            responseItem.innerHTML = `
                <div class="response-header">
                    <div class="user-info">
                        <div class="user-avatar">
                            <img src="${contextPath}/img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg" alt="User Avatar">
                        </div>
                        <div class="user-details">
                            <div class="user-name">${response.name}</div>
                            <div class="user-email">${response.email}</div>
                            <div class="user-id">ID: ${response.userId}</div>
                        </div>
                    </div>
                    <div class="response-date">
                        Ngày gửi: ${formatDate(response.submittedAt)}
                    </div>
                </div>
                <div class="response-body">
                    <div class="response-content">
                        ${response.shortContent || 'Sinh viên đã nộp đơn đăng ký tham gia ' + (formType === 'member' ? 'câu lạc bộ' : 'sự kiện')}
                    </div>
                </div>
                <div class="response-footer">
                    <span class="response-status ${statusClass}">${statusText}</span>
                    <div class="response-actions">
                        ${actionButtons}
                    </div>
                </div>
            `;
            
            responsesList.appendChild(responseItem);
        });
        
        // Thêm event listeners cho các nút hành động
        document.querySelectorAll('.action-btn').forEach(button => {
            button.addEventListener('click', function(event) {
                event.stopPropagation();
                const action = this.dataset.action;
                const id = this.dataset.id;
                
                switch(action) {
                    case 'details':
                        showResponseDetails(id);
                        break;
                    case 'approve':
                        approveResponse(id);
                        break;
                    case 'reject':
                        rejectResponse(id);
                        break;
                }
            });
        });
        
        // Thêm sự kiện click vào response item để mở modal chi tiết
        document.querySelectorAll('.response-item').forEach(item => {
            item.addEventListener('click', function() {
                const id = this.dataset.id;
                showResponseDetails(id);
            });
        });
    }
    
    // Hàm cập nhật thống kê
    function updateStatistics(data) {
        if (!data) return;
        
        // Tính toán số lượng theo từng trạng thái
        const stats = {
            total: data.length,
            waiting: data.filter(r => r.status === 'waiting').length,
            approved: data.filter(r => r.status === 'approved').length,
            rejected: data.filter(r => r.status === 'rejected').length,
            candidate: data.filter(r => r.status === 'candidate').length,
            collaborator: data.filter(r => r.status === 'collaborator').length
        };
        
        // Cập nhật các con số thống kê
        document.getElementById('totalCount').textContent = stats.total;
        document.getElementById('waitingCount').textContent = stats.waiting;
        document.getElementById('approvedCount').textContent = stats.approved;
        document.getElementById('rejectedCount').textContent = stats.rejected;
        
        // Cập nhật tabs
        document.getElementById('allCount').textContent = stats.total;
        document.getElementById('waitingTabCount').textContent = stats.waiting;
        document.getElementById('approvedTabCount').textContent = stats.approved;
        document.getElementById('rejectedTabCount').textContent = stats.rejected;
        
        // Cập nhật thêm cho loại member
        if (formType === 'member') {
            if (document.getElementById('candidateCount')) {
                document.getElementById('candidateCount').textContent = stats.candidate;
            }
            if (document.getElementById('collaboratorCount')) {
                document.getElementById('collaboratorCount').textContent = stats.collaborator;
            }
            if (document.getElementById('candidateTabCount')) {
                document.getElementById('candidateTabCount').textContent = stats.candidate;
            }
            if (document.getElementById('collaboratorTabCount')) {
                document.getElementById('collaboratorTabCount').textContent = stats.collaborator;
            }
        }
    }
    
    // Hiển thị chi tiết phản hồi
    function showResponseDetails(id) {
        console.log("Showing response details for ID:", id);
        const response = responses.find(r => r.id === id);
        
        if (!response) {
            showToast('error', 'Lỗi', 'Không tìm thấy thông tin đơn đăng ký');
            return;
        }
        
        console.log("Found response:", response);
        console.log("Response answers type:", typeof response.answers);
        console.log("Response answers:", response.answers);
        
        // Đảm bảo answers luôn là một array
        if (!response.answers) {
            response.answers = [];
        } else if (typeof response.answers === 'string' && response.answers.trim() === '') {
            response.answers = [];
        }
        
        document.getElementById('responseModalTitle').textContent = `Chi tiết đơn đăng ký - ${response.name}`;
        
        // Tạo nội dung cho modal
        let modalContent = `
            <div class="modal-section">
                <div class="user-header">
                    <div class="user-avatar-large">
                        <img src="${contextPath}/img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg" alt="User Avatar">
                    </div>
                    <div class="user-info-large">
                        <div class="user-name-large">${response.name}</div>
                        <div class="user-meta">
                            <div class="meta-item">
                                <i class="fas fa-envelope"></i>
                                ${response.email}
                            </div>
                            <div class="meta-item">
                                <i class="fas fa-id-card"></i>
                                ID: ${response.userId}
                            </div>
                            <div class="meta-item">
                                <i class="fas fa-clock"></i>
                                Thời gian gửi: ${formatDate(response.submittedAt)}
                            </div>
                            <div class="meta-item">
                                <i class="fas fa-tag"></i>
                                Trạng thái: ${getStatusText(response.status)}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="modal-section">
                <h3 class="section-title">Các câu trả lời</h3>
                <div id="questionAnswersContainer">                <p class="form-description">Những câu trả lời dưới đây được nộp bởi người dùng. Câu hỏi về ban/phòng ban được <span class="highlight-text">đánh dấu màu cam</span>.</p>
                ${renderQuestionAnswers(response.answers)}
            </div>
            
            <script>
                // Kiểm tra xem có nội dung trong phần câu trả lời không
                const qaContainer = document.getElementById('questionAnswersContainer');
                if (qaContainer && (
                    qaContainer.innerHTML.trim() === '<p>Không có câu trả lời nào.</p>' || 
                    qaContainer.innerHTML.includes('Không có câu trả lời nào hoặc dữ liệu không đúng định dạng')
                )) {
                    // Thêm thông báo chi tiết hơn
                    qaContainer.innerHTML = 
                        '<div class="no-answers-message">' +
                            '<i class="fas fa-exclamation-circle"></i>' +
                            '<p>Không tìm thấy câu trả lời nào hoặc dữ liệu câu trả lời không đúng định dạng.</p>' +
                            '<p>Có thể form không được thiết kế đúng hoặc người dùng chưa trả lời câu hỏi nào.</p>' +
                        '</div>';
                }
            </script>
            </div>
        `;
        
        // Thêm phần footer với các nút hành động
        let modalFooter = '<div class="modal-footer">';
        
        console.log("Rendering modal footer. Form type:", formType, "Status:", response.status);
        
        // Thêm nút từ chối cho tất cả các trạng thái trừ đã từ chối
        if (response.status !== 'rejected') {
            modalFooter += `
                <button class="action-btn btn-reject" id="modalRejectBtn" data-id="${response.id}">
                    <i class="fas fa-times"></i> Từ chối
                </button>
            `;
        }
        
        // Phân biệt xử lý dựa trên loại form
        if (formType === 'event') {
            // Form sự kiện: chỉ có nút duyệt ở trạng thái chờ duyệt
            if (response.status === 'waiting') {
                modalFooter += `
                    <button class="action-btn btn-approve" id="modalApproveBtn" data-id="${response.id}">
                        <i class="fas fa-check"></i> Duyệt đăng ký
                    </button>
                `;
            }
        } else {
            // Form member: có các nút theo quy trình thăng cấp
            if (response.status === 'waiting') {
                modalFooter += `
                    <button class="action-btn btn-approve" id="modalApproveBtn" data-id="${response.id}">
                        <i class="fas fa-check"></i> Duyệt thành ứng viên
                    </button>
                `;
            } else if (response.status === 'candidate') {
                modalFooter += `
                    <button class="action-btn btn-promote" id="modalApproveBtn" data-id="${response.id}">
                        <i class="fas fa-user-plus"></i> Nâng cấp thành cộng tác viên
                    </button>
                `;
            } else if (response.status === 'collaborator') {
                modalFooter += `
                    <button class="action-btn btn-promote" id="modalApproveBtn" data-id="${response.id}">
                        <i class="fas fa-user-plus"></i> Nâng cấp thành thành viên chính thức
                    </button>
                `;
            }
        }
        
        modalFooter += `</div>`;
        modalContent += modalFooter;
        
        document.getElementById('responseModalContent').innerHTML = modalContent;
        
        // Thêm event listeners cho các nút trong modal
        const modalApproveBtn = document.getElementById('modalApproveBtn');
        const modalRejectBtn = document.getElementById('modalRejectBtn');
        
        if (modalApproveBtn) {
            modalApproveBtn.addEventListener('click', function() {
                approveResponse(this.dataset.id);
                modal.style.display = 'none';
            });
        }
        
        if (modalRejectBtn) {
            modalRejectBtn.addEventListener('click', function() {
                rejectResponse(this.dataset.id);
                modal.style.display = 'none';
            });
        }
        
        modal.style.display = 'block';
    }
    
    // Render câu hỏi và câu trả lời
    function renderQuestionAnswers(answers) {
        console.log("Rendering answers:", answers);
        
        if (!answers) {
            return '<p>Không có câu trả lời nào.</p>';
        }
        
        // Nếu answers là string JSON, parse nó
        if (typeof answers === 'string') {
            try {
                answers = JSON.parse(answers);
                console.log("Parsed string answers to:", answers);
            } catch (e) {
                console.error("Error parsing answers JSON in renderQuestionAnswers:", e);
                return '<div class="no-answers">Lỗi hiển thị câu trả lời</div>';
            }
        }
        
        let html = '';
        
        // Xử lý trường hợp answers là object (không phải array)
        if (!Array.isArray(answers) && typeof answers === 'object' && answers !== null) {
            console.log("Answers is an object, converting to array format");
            

            const answersArray = [];
            
            // Duyệt qua tất cả các thuộc tính của object
            for (const key in answers) {
                if (answers.hasOwnProperty(key)) {
                    const item = answers[key];
                    
                    // Trường hợp giá trị đơn giản (string, number)
                    if (typeof item === 'string' || typeof item === 'number') {
                        answersArray.push({
                            question: getQuestionNameFromId(key),
                            answer: item
                        });
                    }
                    // Trường hợp là object
                    else if (typeof item === 'object' && item !== null) {
                        let answer = '';
                        
                        if (item.type === 'radio' || item.type === 'text') {
                            answer = item.value || '';
                        } else if (item.type === 'checkbox' && Array.isArray(item.values)) {
                            answer = item.values.join(', ');
                        } else if (item.type === 'date') {
                            answer = item.value || '';
                        } else if (item.value !== undefined) {
                            answer = item.value;
                        } else {
                            // Nếu không xác định được cấu trúc, hiển thị dưới dạng JSON
                            try {
                                answer = JSON.stringify(item);
                            } catch (e) {
                                answer = 'Không thể hiển thị giá trị';
                            }
                        }
                        
                        answersArray.push({
                            question: getQuestionNameFromId(item.fieldId || key),
                            answer: answer,
                            fieldId: item.fieldId || key
                        });
                    }
                }
            }
            
            answers = answersArray;
            console.log("Converted to array:", answers);
        }
        
        if (!Array.isArray(answers)) {
            console.error("Answers could not be converted to an array:", answers);
            return '<div class="no-answers">Không có câu trả lời nào hoặc dữ liệu không đúng định dạng.</div>';
        }
        
        if (answers.length === 0) {
            return '<p>Không có câu trả lời nào.</p>';
        }
        
        answers.forEach((qa, index) => {
            console.log("Processing QA item:", qa, "Type:", typeof qa);
            
            let question = '';
            let answer = '';
            
            if (typeof qa === 'object' && qa !== null) {
                // Object với cặp key-value
                if (qa.question !== undefined) {
                    question = qa.question;
                } else if (qa.questionText !== undefined) {
                    question = qa.questionText;
                } else if (qa.label !== undefined) {
                    question = qa.label;
                }
                
                if (qa.answer !== undefined) {
                    answer = qa.answer;
                } else if (qa.answerText !== undefined) {
                    answer = qa.answerText;
                } else if (qa.value !== undefined) {
                    answer = qa.value;
                }
                
                // Nếu không tìm thấy thông tin theo cấu trúc chuẩn, thử duyệt các key
                if (question === '' && answer === '') {
                    for (let key in qa) {
                        if (key.toLowerCase().includes('question') || key.toLowerCase().includes('label')) {
                            question = qa[key];
                        } else if (key.toLowerCase().includes('answer') || key.toLowerCase().includes('value')) {
                            answer = qa[key];
                        }
                    }
                }
            }
            
            // Nếu vẫn không tìm được câu hỏi và câu trả lời, hiển thị thông tin raw
            if (question === '' && answer === '') {
                if (typeof qa === 'string') {
                    // Nếu là string, hiển thị như câu trả lời với câu hỏi mặc định
                    question = "Câu trả lời";
                    answer = qa;
                } else if (qa !== null) {
                    question = "Dữ liệu câu hỏi";
                    try {
                        answer = JSON.stringify(qa);
                    } catch (e) {
                        answer = "Không thể hiển thị";
                    }
                }
            }
            
            // Kiểm tra nếu đây là câu hỏi về department/ban
            let highlightClass = '';
            if (formType === 'member' && 
                ((question && (
                  question.toLowerCase().includes('ban') || 
                  question.toLowerCase().includes('department') || 
                  question.toLowerCase().includes('phòng ban'))) ||
                 (qa.fieldId === '40') ||  // ID cho câu hỏi ban
                 (answer && typeof answer === 'string' && 
                  (answer.toLowerCase().includes('ban') || 
                   answer.toLowerCase().includes('phòng')))
                )) {
                highlightClass = 'department-question';
            }
            
            // Chỉ hiển thị nếu có câu hỏi hoặc câu trả lời
            if (question || answer) {
                html += `
                    <div class="question-item ${highlightClass}">
                        <div class="question-label">${question || 'Câu hỏi không xác định'}</div>
                        <div class="question-answer ${answer && answer.length > 100 ? 'multiline-answer' : ''}">${answer || 'Không có câu trả lời'}</div>
                    </div>
                `;
            }
        });
        
        return html;
    }
    
    // Hàm xử lý duyệt đơn
    function approveResponse(id) {
        console.log('Duyệt đơn:', id, 'Form type:', formType, 'clubId:', clubId)
        
        // Gửi AJAX request đến server
        fetch(`${contextPath}/formResponses`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `action=approve&responseId=${id}&clubId=${clubId}`
        })
        .then(response => response.json())
        .then(data => {
            console.log('Server response:', data);
            
            if (data.success) {
                // Cập nhật trạng thái local
                const index = responses.findIndex(r => r.id === id);
                if (index !== -1) {
                    responses[index].status = data.status || 'approved';
                    showToast('success', 'Thành công', data.message || 'Đã duyệt đơn thành công');
                    updateStatistics(responses);
                    filterAndRenderResponses();
                } else {
                    // Nếu không tìm thấy trong mảng responses, reload trang để lấy dữ liệu mới
                    showToast('success', 'Thành công', data.message || 'Đã duyệt đơn thành công');
                    setTimeout(() => location.reload(), 1500);
                }
            } else {
                showToast('error', 'Lỗi', data.message || 'Không thể duyệt đơn đăng ký');
            }
        })
        .catch(error => {
            console.error('Error approving response:', error);
            showToast('error', 'Lỗi kết nối', 'Không thể kết nối với máy chủ. Vui lòng thử lại sau.');
        });
    }
    
    // Hàm xử lý từ chối đơn
    function rejectResponse(id) {
        console.log('Từ chối đơn:', id, 'clubId:', clubId);
        
        // Hiển thị thông báo đang xử lý
        showToast('info', 'Đang xử lý', 'Đang gửi yêu cầu từ chối đơn...');
        
        // Gửi AJAX request đến server
        fetch(`${contextPath}/formResponses`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `action=reject&responseId=${id}&clubId=${clubId}`
        })
        .then(response => response.json())
        .then(data => {
            console.log('Server response:', data);
            
            if (data.success) {
                // Cập nhật trạng thái local
                const index = responses.findIndex(r => r.id === id);
                if (index !== -1) {
                    responses[index].status = 'rejected';
                    showToast('error', 'Đã từ chối', data.message || 'Đơn đăng ký đã bị từ chối');
                    updateStatistics(responses);
                    filterAndRenderResponses();
                } else {
                    // Nếu không tìm thấy trong mảng responses, reload trang để lấy dữ liệu mới
                    showToast('error', 'Đã từ chối', data.message || 'Đơn đăng ký đã bị từ chối');
                    setTimeout(() => location.reload(), 1500);
                }
            } else {
                showToast('error', 'Lỗi', data.message || 'Không thể từ chối đơn đăng ký');
            }
        })
        .catch(error => {
            console.error('Error rejecting response:', error);
            showToast('error', 'Lỗi kết nối', 'Không thể kết nối với máy chủ. Vui lòng thử lại sau.');
        });
    }
    
    
    // Hiển thị toast thông báo
    function showToast(type, title, message) {
        const toastContainer = document.getElementById('toast-container');
        const toastId = 'toast-' + Date.now();
        
        const toast = document.createElement('div');
        toast.id = toastId;
        toast.className = `toast toast-${type}`;
        
        let icon = '';
        switch(type) {
            case 'success':
                icon = 'check-circle';
                break;
            case 'error':
                icon = 'times-circle';
                break;
            case 'info':
                icon = 'info-circle';
                break;
            case 'warning':
                icon = 'exclamation-circle';
                break;
        }
        
        toast.innerHTML = `
            <div class="toast-icon">
                <i class="fas fa-${icon}"></i>
            </div>
            <div class="toast-content">
                <div class="toast-title">${title}</div>
                <div class="toast-message">${message}</div>
            </div>
            <div class="toast-close" onclick="document.getElementById('${toastId}').remove()">
                <i class="fas fa-times"></i>
            </div>
        `;
        
        toastContainer.appendChild(toast);
        
        // Tự động tắt sau 5 giây
        setTimeout(() => {
            if (document.getElementById(toastId)) {
                document.getElementById(toastId).classList.add('hide');
                
                setTimeout(() => {
                    if (document.getElementById(toastId)) {
                        document.getElementById(toastId).remove();
                    }
                }, 300);
            }
        }, 5000);
    }
    
    // Hàm định dạng ngày tháng
    function formatDate(dateString) {
        if (!dateString) return 'N/A';
        
        const date = new Date(dateString);
        return date.toLocaleString('vi-VN', { 
            day: '2-digit', 
            month: '2-digit', 
            year: 'numeric',
            hour: '2-digit', 
            minute: '2-digit'
        });
    }
    
    // Lấy text cho trạng thái
    function getStatusText(status) {
        switch(status) {
            case 'waiting': return 'Chờ duyệt';
            case 'approved': return 'Đã duyệt';
            case 'rejected': return 'Từ chối';
            case 'candidate': return 'Ứng viên';
            case 'collaborator': return 'Cộng tác viên';
            default: return status;
        }
    }
    
    // Hàm cố gắng lấy tên câu hỏi từ fieldId
    function getQuestionNameFromId(fieldId) {
        // Đây là mapping giữa fieldId và tên câu hỏi thường gặp
        const commonQuestions = {
            '1': 'Bạn muốn tham gia ban nào?',
        };
        
        return commonQuestions[fieldId] || `Câu hỏi ${fieldId}`;
    }
});
