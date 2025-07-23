<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Tổng quan Báo cáo CLB - IC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ic-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="dashboard">
        <jsp:include page="components/ic-sidebar.jsp" />

        <main class="main-content">
            <div class="header">
                <h1 class="page-title">Tổng quan báo cáo các CLB</h1>
                <div class="user-profile">
                    <div class="user-avatar">IC</div>
                    <div class="user-info">
                        <div class="user-name">${sessionScope.user.fullName}</div>
                        <div class="user-role">IC Officer</div>
                    </div>
                </div>
            </div>

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
                </div>
            </div>
        </main>
    </div>
</body>
</html>
