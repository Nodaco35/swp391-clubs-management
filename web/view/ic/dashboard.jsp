<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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

                <!-- Hiển thị thông báo nếu có -->
                <c:if test="${not empty message}">
                    <div class="alert alert-success" style="margin-bottom: 15px;">
                        <i class="fas fa-check-circle"></i> ${message}
                    </div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-warning" style="margin-bottom: 15px;">
                        <i class="fas fa-exclamation-circle"></i> ${error}
                    </div>
                </c:if>

                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">Danh sách báo cáo</h2>
                    </div>

                    <div class="reports-grid" id="reportsGrid">
                        <c:forEach items="${reportList}" var="report">
                            <div class="report-card">
                                <div class="report-header">
                                    <div class="report-meta">
                                        <div>
                                            <div class="report-title">${report.term}</div>
                                            <div class="report-club">${report.clubName}</div>
                                            <div class="report-date">Nộp ngày: ${report.submissionDate}</div>
                                        </div>
                                    </div>
                                </div>

                                <div class="report-body">
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
                                        <a class="btn btn-outline btn-sm" href="ic?action=showReport&id=${report.reportID}">
                                            <i class="fas fa-eye"></i> Xem chi tiết
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                        <div class="text-center mt-3">
                            <a href="ic?action=periodicReport" class="btn btn-primary">Xem tất cả báo cáo</a>
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
