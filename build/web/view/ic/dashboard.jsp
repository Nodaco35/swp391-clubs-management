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
                            <div class="stat-title">Báo cáo chưa đọc</div>
                            <div class="stat-icon yellow">
                                <i class="fas fa-clock"></i>
                            </div>
                        </div>
                        <div class="stat-value">${pendingReports}</div>
                        <div class="stat-description">Cần xem xét trong tuần này</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-header">
                            <div class="stat-title">Báo cáo đã đọc</div>
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
                    <div class="reports-grid" id="reportsGrid">
                        <div class="filters" style="padding: 5px 20px">
                            <div class="filter-group">
                                <label class="filter-label">Trạng thái</label>
                                <select class="filter-select" onchange="filterReports(this.value)">
                                    <option value="all">Tất cả</option>
                                    <option value="pending">Chưa xem</option>
                                    <option value="approved">Đã xem</option>
                                </select>
                            </div>
                        </div>
                        <!-- PENDING -->
                        <c:forEach items="${pendingReportsList}" var="report">
                            <div class="report-card" data-status="pending">
                                <div class="report-header">
                                    <div class="report-meta">
                                        <div>
                                            <div class="report-title">${report.term}</div>
                                            <div class="report-club">${report.clubName}</div>
                                            <div class="report-date">Nộp ngày: ${report.submissionDate}</div>
                                        </div>
                                        <div class="report-status">
                                            <span class="badge badge-pending"><i class="fas fa-clock"></i> Chờ duyệt</span>
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
                                    </div>
                                </div>
                                <div class="report-footer">
                                    <div class="report-actions">
                                        <a class="btn btn-outline btn-sm" href="ic?action=showReport&id=${report.reportId}">
                                            <i class="fas fa-eye"></i> Xem chi tiết
                                        </a>
                                        <a class="btn btn-success btn-sm" href="ic?action=markAsRead&id=${report.reportId}">
                                            <i class="fas fa-check"></i> Đánh dấu là đã đọc
                                        </a>

                                    </div>
                                </div>
                            </div>
                        </c:forEach>

                        <!-- APPROVED: Đã đọc -->
                        <c:forEach items="${approvedReportsList}" var="report">
                            <div class="report-card" data-status="approved">
                                <div class="report-header">
                                    <div class="report-meta">
                                        <div>
                                            <div class="report-title">${report.term}</div>
                                            <div class="report-club">${report.clubName}</div>
                                            <div class="report-date">Nộp ngày: ${report.submissionDate}</div>
                                        </div>
                                        <div class="report-status">
                                            <span class="badge badge-approved"><i class="fas fa-check"></i> Đã xem</span>
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
                                    </div>
                                </div>
                                <div class="report-footer">
                                    <div class="report-actions">
                                        <a class="btn btn-outline btn-sm" href="ic?action=showReport&id=${report.reportId}">
                                            <i class="fas fa-eye"></i> Xem chi tiết
                                        </a>
                                    </div>
                                </div>          
                            </div>
                        </c:forEach>

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

        <script>
            function filterReports(status) {
                const cards = document.querySelectorAll('.report-card');

                cards.forEach(card => {
                    const cardStatus = card.dataset.status;

                    // So sánh theo status
                    const showByStatus = (status === 'all' || cardStatus === status);

                    // Nếu đúng status thì hiển thị
                    if (showByStatus) {
                        card.style.display = 'block';
                    } else {
                        card.style.display = 'none';
                    }
                });
            }

            // Đóng modal khi click bên ngoài
            window.onclick = function (event) {
                if (event.target.classList.contains('modal')) {
                    event.target.style.display = 'none';
                }
            }

            // Xử lý rating stars
            document.querySelectorAll('.rating-stars').forEach(ratingGroup => {
                const stars = ratingGroup.querySelectorAll('.star');
                stars.forEach((star, index) => {
                    star.addEventListener('click', () => {
                        stars.forEach((s, i) => {
                            s.classList.toggle('active', i <= index);
                        });
                    });
                });
            });
        </script>


    </body>
</html>