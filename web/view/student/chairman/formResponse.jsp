<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Form Response - UniClub</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;500;600;700&family=Poppins:wght@300;400;500;600;700&family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/formResponse.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/formResponseSearch.css">
    </head>
    <body>
        <jsp:include page="/view/events-page/header.jsp" />
        
        <!-- Hidden form type information -->
        <input type="hidden" id="formTypeInput" value="${formType}">
        <input type="hidden" id="clubIdInput" value="${clubId}">
        <input type="hidden" id="formIdInput" value="${formId}">

        <!-- Hidden server data -->
        <c:if test="${not empty applicationsJson}">
            <input type="hidden" id="applicationsData" value='${applicationsJson}'>
        </c:if>
        
        <!-- Error message display -->
        <c:if test="${not empty errorMessage}">
            <input type="hidden" id="errorMessageData" value="${errorMessage}">
        </c:if>
        
        <div class="container">
            <div style="text-align: center; background-color: aliceblue; padding: 20px; border-radius: 10px; margin-bottom: 20px;">
                <h1>Quản lý đăng ký ${formType eq 'club' ? 'CLB' : 'sự kiện'}</h1>
                <p>Xem và duyệt các đơn đăng ký tham gia ${formType eq 'club' ? 'câu lạc bộ' : 'sự kiện'}</p>
            </div>
            
            <div class="dashboard">
                <div class="stat-card waiting">
                    <div class="card-icon"><i class="fas fa-clock"></i></div>
                    <div class="stat-number" id="waitingCount">0</div>
                    <div class="stat-label">Chờ duyệt</div>
                </div>
                
                <div class="stat-card approved">
                    <div class="card-icon"><i class="fas fa-check-circle"></i></div>
                    <div class="stat-number" id="approvedCount">0</div>
                    <div class="stat-label">Đã duyệt</div>
                </div>
                
                <div class="stat-card rejected">
                    <div class="card-icon"><i class="fas fa-times-circle"></i></div>
                    <div class="stat-number" id="rejectedCount">0</div>
                    <div class="stat-label">Từ chối</div>
                </div>
                
                <c:if test="${formType eq 'club'}">
                    <div class="stat-card candidate">
                        <div class="card-icon"><i class="fas fa-user-graduate"></i></div>
                        <div class="stat-number" id="candidateCount">0</div>
                        <div class="stat-label">Ứng viên</div>
                    </div>
                    
                    <div class="stat-card collaborator">
                        <div class="card-icon"><i class="fas fa-user-friends"></i></div>
                        <div class="stat-number" id="collaboratorCount">0</div>
                        <div class="stat-label">Cộng tác viên</div>
                    </div>
                </c:if>
                
                <div class="stat-card total">
                    <div class="card-icon"><i class="fas fa-file-alt"></i></div>
                    <div class="stat-number" id="totalCount">0</div>
                    <div class="stat-label">Tổng cộng</div>
                </div>
            </div>              <div class="form-response-search-container">
                <div class="form-response-search-box">
                    <i class="fas fa-search form-response-search-icon"></i>
                    <input type="text" id="responseSearchInput" class="form-response-search-input" placeholder="Tìm kiếm theo tên, email hoặc mã người dùng...">
                    <!-- Clear button sẽ được thêm tự động từ JS -->
                </div>
            </div>
            
            <div class="filter-tabs">
                <button class="tab-btn active" data-filter="all">Tất cả (<span id="allCount">0</span>)</button>
                <button class="tab-btn" data-filter="waiting">Chờ duyệt (<span id="waitingTabCount">0</span>)</button>
                <c:if test="${formType eq 'club'}">
                    <button class="tab-btn" data-filter="candidate">Ứng viên (<span id="candidateTabCount">0</span>)</button>
                    <button class="tab-btn" data-filter="collaborator">Cộng tác viên (<span id="collaboratorTabCount">0</span>)</button>
                </c:if>
                <button class="tab-btn" data-filter="approved">Đã duyệt (<span id="approvedTabCount">0</span>)</button>
                <button class="tab-btn" data-filter="rejected">Từ chối (<span id="rejectedTabCount">0</span>)</button>
            </div>
            
            <div class="responses-container">
                <div class="responses-list" id="responsesList">
                    <!-- Danh sách các phản hồi sẽ được render từ JavaScript -->
                    <div class="no-responses-message">
                        <i class="fas fa-inbox"></i>
                        <p>Chưa có phản hồi nào cho form này</p>
                    </div>
                </div>
            </div>
            
            <!-- Modal cho chi tiết phản hồi -->
            <div id="responseModal" class="modal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2 id="responseModalTitle">Chi tiết đơn đăng ký</h2>
                        <span class="close-modal">&times;</span>
                    </div>
                    <div id="responseModalContent">
                        <!-- Nội dung chi tiết sẽ được render từ JavaScript -->
                    </div>
                    <div class="review-section">
                        <h3>Đánh giá ứng viên</h3>
                        <textarea id="reviewNoteInput" class="review-note-input" placeholder="Nhập đánh giá hoặc lý do duyệt/từ chối..."></textarea>
                        <div class="review-actions">
                            <button id="saveReviewBtn" class="btn btn-primary">Lưu đánh giá</button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Toast notifications -->
            <div id="toast-container">
                <!-- Toast thông báo sẽ được thêm vào đây từ JavaScript -->
            </div>
        </div>
        
    <script src="${pageContext.request.contextPath}/js/formResponse.js?v=<%= System.currentTimeMillis() %>"></script>
    </body>
</html>
