<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi Tiết Chiến Dịch Tuyển Quân - UniClub</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;500;600;700&family=Poppins:wght@300;400;500;600;700&family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewRecruitmentCampaign.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body style="background-color: #d8d7ce;">
    <jsp:include page="/view/events-page/header.jsp" />
    
    <!-- Toast Container -->
    <div class="recruitment-toast-container" id="toastContainer"></div>
    
    <div class="recruitment-container">
        <div class="page-header">
            <a href="${pageContext.request.contextPath}/recruitment/list?clubId=${campaign.clubID}" class="back-btn">
                <i class="fas fa-arrow-left mr-2"></i> Quay lại
            </a>
            <h1 class="page-title">Chi Tiết Chiến Dịch Tuyển Quân</h1>
        </div>
        <div class="campaign-info">
                <div class="campaign-main">
                    <div class="campaign-title-section">
                        <h1 class="campaign-title">${campaign.title}</h1>
                        <span class="status-badge ${campaign.status.toLowerCase()}">
                            <c:choose>
                                <c:when test="${campaign.status == 'ONGOING'}">
                                    <i class="fas fa-play-circle"></i> Đang diễn ra
                                </c:when>
                                <c:when test="${campaign.status == 'CLOSED'}">
                                    <i class="fas fa-stop-circle"></i> Đã đóng
                                </c:when>
                                <c:otherwise>
                                    <i class="fas fa-clock"></i> Chờ bắt đầu
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <p class="campaign-description">${campaign.description}</p>
                    <div class="campaign-meta">
                        <div class="meta-item">
                            <i class="fas fa-calendar"></i>
                            <span>
                                <fmt:formatDate value="${campaign.startDate}" pattern="dd/MM/yyyy" /> - 
                                <fmt:formatDate value="${campaign.endDate}" pattern="dd/MM/yyyy" />
                            </span>
                        </div>
                        <div class="meta-item">
                            <i class="fas fa-users"></i>
                            <span>Gen: ${campaign.gen}</span>
                        </div>
                        <div class="meta-item">
                            <i class="fas fa-user-friends"></i>
                            <span>${totalCandidates} ứng viên</span>
                        </div>
                    </div>
                </div>
                
                <div class="campaign-actions">
                    <a href="${pageContext.request.contextPath}/recruitment/form/edit?recruitmentId=${campaign.recruitmentID}" 
                       class="recruitment-btn btn-outline">
                        <i class="fas fa-edit"></i> Chỉnh sửa
                    </a>
                </div>
            </div>
        </div>

        <!-- Tabs -->
        <div class="tabs-container">
            <div class="tabs-nav">
                <button class="tab-btn active" data-tab="overview">
                    <i class="fas fa-chart-bar"></i> Tổng quan
                </button>
                <button class="tab-btn" data-tab="application">
                    <i class="fas fa-file-alt"></i> Vòng nộp đơn
                </button>
                <button class="tab-btn" data-tab="interview">
                    <i class="fas fa-users"></i> Vòng phỏng vấn
                </button>
                <button class="tab-btn" data-tab="challenge">
                    <i class="fas fa-trophy"></i> Vòng thử thách
                </button>
            </div>
        </div>

        <!-- Tab Contents -->
        <div class="tab-content active" id="overview">
            <!-- Chart Section -->
            <div class="chart-section">
                <div class="chart-card">
                    <div class="chart-header">
                        <h3><i class="fas fa-chart-column"></i> Biểu đồ tiến độ các vòng</h3>
                    </div>
                    <div class="chart-container">
                        <canvas id="stageProgressChart"></canvas>
                    </div>
                    <div class="chart-legend">
                        <div class="legend-item">
                            <div class="legend-color" style="background-color: #3b82f6;"></div>
                            <span>Tổng ứng viên</span>
                        </div>
                        <div class="legend-item">
                            <div class="legend-color" style="background-color: #f59e0b;"></div>
                            <span>Chờ xử lý</span>
                        </div>
                        <div class="legend-item">
                            <div class="legend-color" style="background-color: #10b981;"></div>
                            <span>Đã duyệt</span>
                        </div>
                        <div class="legend-item">
                            <div class="legend-color" style="background-color: #ef4444;"></div>
                            <span>Từ chối</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Stages Overview Cards -->
            <div class="stages-cards-grid">
                <c:forEach var="stage" items="${stages}" varStatus="status">
                    <div class="stage-overview-card">
                        <div class="stage-card-header">
                            <h3 class="stage-card-title">
                                <c:choose>
                                    <c:when test="${stage.stageName == 'APPLICATION'}">Vòng nộp đơn</c:when>
                                    <c:when test="${stage.stageName == 'INTERVIEW'}">Vòng phỏng vấn</c:when>
                                    <c:when test="${stage.stageName == 'CHALLENGE'}">Vòng thử thách</c:when>
                                    <c:otherwise>${stage.stageName}</c:otherwise>
                                </c:choose>
                            </h3>
                            <div class="stage-card-status">
                                <span class="status-badge ${stage.status.toLowerCase()}">
                                    <c:choose>
                                        <c:when test="${stage.status == 'ONGOING'}">
                                            <i class="fas fa-play-circle"></i> Đang diễn ra
                                        </c:when>
                                        <c:when test="${stage.status == 'CLOSED'}">
                                            <i class="fas fa-check-circle"></i> Đã kết thúc
                                        </c:when>
                                        <c:otherwise>
                                            <i class="fas fa-clock"></i> Chờ bắt đầu
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                        </div>
                        
                        <div class="stage-card-dates">
                            <p><i class="fas fa-calendar-start"></i> 
                                <fmt:formatDate value="${stage.startDate}" pattern="dd/MM/yyyy" /> - 
                                <fmt:formatDate value="${stage.endDate}" pattern="dd/MM/yyyy" />
                            </p>
                            <c:if test="${stage.stageName == 'INTERVIEW'}">
                                <p><i class="fas fa-map-marker-alt"></i> ${stage.location != null ? stage.location : 'Chưa xác định'}</p>
                            </c:if>
                        </div>
                        
                        <div class="stage-card-stats">
                            <div class="stat-row">
                                <div class="stat-item total">
                                    <div class="stat-number" data-stage-id="${stage.stageID}" data-stat="total">
                                        ${stageStats[stage.stageID]['TOTAL'] != null ? stageStats[stage.stageID]['TOTAL'] : 0}
                                    </div>
                                    <div class="stat-label">Tổng</div>
                                </div>
                                <div class="stat-item pending">
                                    <div class="stat-number" data-stage-id="${stage.stageID}" data-stat="pending">
                                        ${stageStats[stage.stageID]['PENDING'] != null ? stageStats[stage.stageID]['PENDING'] : 0}
                                    </div>
                                    <div class="stat-label">Đang Chờ</div>
                                </div>
                            </div>
                            <div class="stat-row">
                                <div class="stat-item approved">
                                    <div class="stat-number" data-stage-id="${stage.stageID}" data-stat="approved">
                                        ${stageStats[stage.stageID]['APPROVED'] != null ? stageStats[stage.stageID]['APPROVED'] : 0}
                                    </div>
                                    <div class="stat-label">Đã Duyệt</div>
                                </div>
                                <div class="stat-item rejected">
                                    <div class="stat-number" data-stage-id="${stage.stageID}" data-stat="rejected">
                                        ${stageStats[stage.stageID]['REJECTED'] != null ? stageStats[stage.stageID]['REJECTED'] : 0}
                                    </div>
                                    <div class="stat-label">Từ chối</div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="stage-card-actions">
                            <div class="notification-buttons">
                                <button class="recruitment-btn btn-primary btn-sm show-notification-modal-btn" 
                                        data-stage-id="${stage.stageID}" 
                                        data-stage-name="${stage.stageName}">
                                    <i class="fas fa-bell"></i> Gửi thông báo
                                </button>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>

        <div class="tab-content" id="application">
            <div class="stage-detail-content">
                <div class="stage-detail-header">
                    <h3><i class="fas fa-file-alt"></i> Vòng nộp đơn</h3>
                    <div class="header-actions">
                        <button class="recruiment-btn btn-primary" onclick="viewFormResponses('APPLICATION')">
                            <i class="fas fa-eye"></i> Xem chi tiết đơn
                        </button>
                    </div>
                </div>
                <div class="candidates-section" data-stage-type="APPLICATION">
                    <div class="candidates-list">
                        <div class="table-container">
                            <table class="candidates-table">
                                <thead>
                                    <tr>
                                        <th>STT</th>
                                        <th>Tên ứng viên</th>
                                        <th>Email</th>
                                        <th>Ngày nộp</th>
                                        <th>Trạng thái</th>
                                    </tr>
                                </thead>
                                <tbody id="application-candidates">
                                    <!-- Candidates will be loaded here -->
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="tab-content" id="interview">
            <div class="stage-detail-content">
                <div class="stage-detail-header">
                    <h3><i class="fas fa-users"></i> Vòng phỏng vấn</h3>
                    <div class="header-actions">
                        <c:forEach var="stage" items="${stages}">
                            <c:if test="${stage.stageName == 'INTERVIEW'}">
                                <div class="location-display">
                                    <i class="fas fa-map-marker-alt"></i>
                                    <span>Địa điểm: ${stage.location != null ? stage.location : 'Chưa xác định'}</span>
                                </div>
                            </c:if>
                        </c:forEach>
                        <button class="recruitment-btn btn-primary" onclick="viewFormResponses('INTERVIEW')">
                            <i class="fas fa-eye"></i> Xem chi tiết đơn
                        </button>
                    </div>
                </div>
                <div class="candidates-section" data-stage-type="INTERVIEW">
                    <div class="candidates-list">
                        <div class="table-container">
                            <table class="candidates-table">
                                <thead>
                                    <tr>
                                        <th>STT</th>
                                        <th>Tên ứng viên</th>
                                        <th>Email</th>
                                        <th>Ngày nộp đơn</th>
                                        <th>Trạng thái</th>
                                    </tr>
                                </thead>
                                <tbody id="interview-candidates">
                                    <!-- Candidates will be loaded here -->
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="tab-content" id="challenge">
            <div class="stage-detail-content">
                <div class="stage-detail-header">
                    <h3><i class="fas fa-trophy"></i> Vòng thử thách</h3>
                    <div class="header-actions">
                        <button class="recruitment-btn btn-primary" onclick="viewFormResponses('CHALLENGE')">
                            <i class="fas fa-eye"></i> Xem chi tiết đơn
                        </button>
                    </div>
                </div>
                <div class="candidates-section" data-stage-type="CHALLENGE">
                    <div class="candidates-list">
                        <div class="table-container">
                            <table class="candidates-table">
                                <thead>
                                    <tr>
                                        <th>STT</th>
                                        <th>Tên ứng viên</th>
                                        <th>Email</th>
                                        <th>Ngày hoàn thành</th>
                                        <th>Trạng thái</th>
                                    </tr>
                                </thead>
                                <tbody id="challenge-candidates">
                                    <!-- Candidates will be loaded here -->
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Notification Modal -->
    <div id="notificationModal" class="modal">
        <div class="modal-content large">
            <div class="modal-header">
                <h3>Gửi thông báo</h3>
                <span class="close">&times;</span>
            </div>
            <div class="modal-body">
                <!-- Layout dạng cột dọc -->
                <div class="notification-layout">
                    <!-- 1. Soạn thông báo -->
                    <div class="notification-compose-column">
                        <h4 class="section-title">Soạn thông báo</h4>
                        <form id="notificationForm">
                            <div id="notificationFormError" class="form-error" style="display:none; color: #dc3545; margin-bottom: 15px; padding: 8px; border-radius: 4px; background-color: #f8d7da; border: 1px solid #f5c6cb;">
                            </div>
                            <div class="form-group">
                                <label for="notificationTitle">Tiêu đề thông báo:</label>
                                <input type="text" id="notificationTitle" class="form-control" placeholder="Nhập tiêu đề thông báo" required>
                            </div>
                            
                            <div class="form-group">
                                <label for="notificationContent">Nội dung thông báo:</label>
                                <textarea id="notificationContent" class="form-control" rows="8" 
                                      placeholder="Bạn có thể sử dụng các biến: {Tên ứng viên}, {Tên vòng}, {Địa điểm(Vòng phỏng vấn)}" required></textarea>
                            </div>
                        </form>
                        
                        <div class="preview-container">
                            <h4>Xem trước thông báo</h4>
                            <div class="notification-preview" id="notificationPreview">
                                <h5>Tiêu đề thông báo sẽ hiển thị ở đây</h5>
                                <p>Nội dung thông báo sẽ hiển thị ở đây...</p>
                            </div>
                        </div>
                    </div>
                    
                    <!-- 2. Danh sách người nhận -->
                    <div class="notification-recipients-column">
                        <h4 class="section-title">Danh sách người nhận</h4>
                        <div class="recipients-header">
                            <div class="form-group">
                                <label for="recipientFilter">Lọc theo trạng thái:</label>
                                <select id="recipientFilter" class="form-control" onchange="filterRecipients(this.value)">
                                    <option value="">Tất cả ứng viên</option>
                                    <option value="approved">Đã duyệt</option>
                                    <option value="rejected">Đã từ chối</option>
                                    <option value="pending">Đang chờ xử lý</option>
                                </select>
                            </div>
                            <div class="selection-controls">
                                <div class="checkbox-group">
                                    <input type="checkbox" id="selectAllRecipients">
                                    <label for="selectAllRecipients">Chọn tất cả</label>
                                </div>
                                <span class="selection-count">
                                    <span id="selectedCount">0</span> người đã chọn</span>
                            </div>
                        </div>
                        <div class="recipients-list" id="recipientsList">
                            <!-- Recipients will be loaded here -->
                            <div class="loading">
                                <i class="fas fa-spinner fa-spin"></i> Đang tải danh sách người nhận...
                            </div>
                        </div>
                    </div>
                    
                    <!-- 3. Mẫu thông báo -->
                    <div class="templates-section">
                        <h4 class="section-title">Mẫu thông báo</h4>
                        <div class="form-group">
                            <label for="templateSelect">Chọn mẫu thông báo:</label>
                            <div class="template-select-container">
                                <select id="templateSelector" class="form-control">
                                    <option value="">-- Chọn mẫu thông báo --</option>
                                </select>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <div class="checkbox-group">
                                <input type="checkbox" id="saveAsTemplate">
                                <label for="saveAsTemplate">Lưu làm mẫu thông báo</label>
                            </div>
                        </div>
                        
                        <div class="form-group template-name-group" style="display: none;">
                            <label for="templateName">Tên mẫu thông báo:</label>
                            <input type="text" id="templateName" class="form-control" placeholder="Nhập tên mẫu thông báo">
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="recruitment-btn btn-outline cancel-btn">Hủy</button>
                <button type="button" class="recruitment-btn btn-primary" id="sendNotificationBtn" onclick="confirmSendNotification()">
                    <i class="fas fa-paper-plane"></i> Gửi thông báo
                </button>
            </div>
        </div>
    </div>

    <!-- Confirmation Modal -->
    <div id="confirmationModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Xác nhận gửi thông báo</h3>
                <span class="close">&times;</span>
            </div>
            <div class="modal-body">
                <p class="text-center mb-4">Bạn có chắc chắn muốn gửi thông báo đến ứng viên đã chọn không?</p>
                <div class="notification-summary">
                    <div class="summary-item">
                        <strong>Vòng tuyển:</strong> <span id="confirmStage"></span>
                    </div>
                    <div class="summary-item">
                        <strong>Tiêu đề:</strong> <span id="confirmTitle"></span>
                    </div>
                    <div class="summary-item">
                        <strong>Số người nhận:</strong> <span id="confirmCount"></span>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="recruitment-btn btn-outline cancel-btn">Hủy</button>
                <button type="button" class="recruitment-btn btn-primary" id="confirmSendBtn">
                    <i class="fas fa-check"></i> Xác nhận gửi
                </button>
            </div>
        </div>
    </div>
    
    <!-- Toast Notifications -->
    <div id="successToast" class="toast success">
        <i class="fas fa-check-circle"></i>
        <span class="toast-message"></span>
    </div>

    <div id="errorToast" class="toast error">
        <i class="fas fa-exclamation-circle"></i>
        <span class="toast-message"></span>
    </div>

    <script>
        console.log("JSP campaign data:", {
            recruitmentID: '${campaign.recruitmentID}',
            templateID: '${campaign.templateID}',
            clubID: '${campaign.clubID}'
        });
        
        // Pass data to JavaScript
        window.campaignData = {
            campaignId: parseInt('${campaign.recruitmentID}') || 0,
            templateId: parseInt('${campaign.templateID}') || 0,
            clubId: parseInt('${campaign.clubID}') || 0,  // Chỉ sử dụng tên biến nhất quán là "clubId"
            stages: []
        };
        console.log("Initialized window.campaignData:", window.campaignData);
        
        <c:forEach var="stage" items="${stages}" varStatus="status">
        window.campaignData.stages.push({
            stageId: ${stage.stageID},
            stageName: '${stage.stageName}',
            status: '${stage.status}',
            stats: {
                // Get stats from the stageStats object passed from the server
                total: ${stageStats[stage.stageID]['TOTAL'] != null ? stageStats[stage.stageID]['TOTAL'] : 0},
                pending: ${stageStats[stage.stageID]['PENDING'] != null ? stageStats[stage.stageID]['PENDING'] : 0},
                approved: ${stageStats[stage.stageID]['APPROVED'] != null ? stageStats[stage.stageID]['APPROVED'] : 0},
                rejected: ${stageStats[stage.stageID]['REJECTED'] != null ? stageStats[stage.stageID]['REJECTED'] : 0}
            }
        });
        </c:forEach>
    </script>
    
    <script src="${pageContext.request.contextPath}/js/recruitmentCommon.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="${pageContext.request.contextPath}/js/viewRecruitmentCampaign.js?v=<%= System.currentTimeMillis() %>"></script>
</body>
</html>
