<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>IC Officer - Báo cáo hàng kỳ CLB</title>
    
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ic-dashboard.css">
</head>
<body>
<div class="dashboard">
    <jsp:include page="components/ic-sidebar.jsp" />

    <main class="main-content">
        <div class="header">
            <h1 class="page-title">Báo cáo hàng kỳ CLB</h1>
            <div class="user-profile">
                <div class="user-avatar">IC</div>
                <div class="user-info">
                    <div class="user-name">${sessionScope.user.fullName}</div>
                    <div class="user-role">IC Officer</div>
                </div>
            </div>
        </div>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-header">
                    <div class="stat-title">Báo cáo chờ duyệt</div>
                    <div class="stat-icon yellow">
                        <i class="fas fa-clock"></i>
                    </div>
                </div>
                <div class="stat-value">${pendingReports}</div>
                <div class="stat-description">Cần xem xét trong tuần này</div>
            </div>
            <div class="stat-card">
                <div class="stat-header">
                    <div class="stat-title">Báo cáo đã duyệt</div>
                    <div class="stat-icon green">
                        <i class="fas fa-check-circle"></i>
                    </div>
                </div>
                <div class="stat-value">${approvedReports}</div>
                <div class="stat-description">Trong học kỳ này</div>
            </div>
            <div class="stat-card">
                <div class="stat-header">
                    <div class="stat-title">CLB quá hạn</div>
                    <div class="stat-icon red">
                        <i class="fas fa-exclamation-triangle"></i>
                    </div>
                </div>
                <div class="stat-value">${overdueClubs}</div>
                <div class="stat-description">Chưa nộp báo cáo</div>
            </div>
            <div class="stat-card">
                <div class="stat-header">
                    <div class="stat-title">Tổng số CLB</div>
                    <div class="stat-icon blue">
                        <i class="fas fa-users"></i>
                    </div>
                </div>
                <div class="stat-value">${totalActiveClubs}</div>
                <div class="stat-description">Đang hoạt động</div>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Danh sách báo cáo hàng kỳ</h2>
                <div class="card-actions">
                    <button class="btn btn-outline">
                        <i class="fas fa-download"></i> Xuất báo cáo
                    </button>
                    <button class="btn btn-primary">
                        <i class="fas fa-plus"></i> Tạo nhắc nhở
                    </button>
                </div>
            </div>
            <div class="card-body">
                <div class="filters">
                    <div class="filter-group">
                        <label class="filter-label">Trạng thái</label>
                        <select class="filter-select" onchange="filterReports(this.value)">
                            <option value="all">Tất cả</option>
                            <option value="pending">Chờ duyệt</option>
                            <option value="approved">Đã duyệt</option>
                            <option value="rejected">Từ chối</option>
                            <option value="overdue">Quá hạn</option>
                        </select>
                    </div>
                    <div class="filter-group">
                        <label class="filter-label">Học kỳ</label>
                        <select class="filter-select">
                            <option value="2023-2024-1">HK1 2023-2024</option>
                            <option value="2022-2023-2">HK2 2022-2023</option>
                            <option value="2022-2023-1">HK1 2022-2023</option>
                        </select>
                    </div>
                    <div class="filter-group">
                        <label class="filter-label">Loại CLB</label>
                        <select class="filter-select">
                            <option value="all">Tất cả</option>
                            <option value="academic">Học thuật</option>
                            <option value="sports">Thể thao</option>
                            <option value="arts">Nghệ thuật</option>
                            <option value="volunteer">Thiện nguyện</option>
                        </select>
                    </div>
                </div>

                <div class="reports-grid" id="reportsGrid">
                    <!-- Báo cáo chờ duyệt -->
                    <c:forEach items="${pendingReportsList}" var="report">
                        <div class="report-card" data-status="pending">
                            <div class="report-header">
                                <div class="report-meta">
                                    <div>
                                        <div class="report-title">${report.title}</div>
                                        <div class="report-club">${report.clubName}</div>
                                        <div class="report-date">Nộp ngày: ${report.formattedSubmissionDate}</div>
                                    </div>
                                    <div class="report-status">
                                        <span class="badge badge-pending">
                                            <i class="fas fa-clock"></i> Chờ duyệt
                                        </span>
                                    </div>
                                </div>
                            </div>
                            <div class="report-body">
                                <div class="report-summary">
                                    <h4>Tóm tắt báo cáo</h4>
                                    <p>${report.overview}</p>
                                </div>
                                <div class="report-metrics">
                                    <div class="metric">
                                        <div class="metric-value">${report.memberCount}</div>
                                        <div class="metric-label">Thành viên</div>
                                    </div>
                                    <div class="metric">
                                        <div class="metric-value">${report.eventCount}</div>
                                        <div class="metric-label">Sự kiện</div>
                                    </div>
                                    <div class="metric">
                                        <div class="metric-value">${report.formattedBudget}</div>
                                        <div class="metric-label">Ngân sách (VND)</div>
                                    </div>
                                    <div class="metric">
                                        <div class="metric-value">${report.participationRate}%</div>
                                        <div class="metric-label">Tham gia</div>
                                    </div>
                                </div>
                            </div>
                            <div class="report-footer">
                                <div class="report-actions">
                                    <button class="btn btn-outline btn-sm" onclick="openReportModal(${report.reportId})">
                                        <i class="fas fa-eye"></i> Xem chi tiết
                                    </button>
                                    <button class="btn btn-success btn-sm" onclick="approveReport(${report.reportId})">
                                        <i class="fas fa-check"></i> Phê duyệt
                                    </button>
                                    <button class="btn btn-error btn-sm" onclick="rejectReport(${report.reportId})">
                                        <i class="fas fa-times"></i> Từ chối
                                    </button>
                                </div>
                            </div>
                        </div>
                    </c:forEach>

                    <!-- Hiển thị các loại báo cáo khác tương tự -->
                </div>
            </div>
        </div>
    </main>
</div>

<!-- Modal xem chi tiết báo cáo -->
<div id="reportModal" class="modal">
    <div class="modal-content">
        <!-- Nội dung modal -->
    </div>
</div>

<!-- JavaScript -->
<script src="${pageContext.request.contextPath}/js/ic-dashboard.js"></script>
</body>
</html>