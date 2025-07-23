<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
    <head>
        <title>Báo cáo định kỳ CLB</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">
        <style>
            .report-card {
                transition: box-shadow 0.3s ease;
            }

            .report-card:hover {
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
            }

            .badge-term {
                font-size: 0.9rem;
                background-color: #0d6efd;
            }
        </style>
    </head>
    <body>
        <header class="header">
            <div class="container header-container">
                <div class="logo">
                    <i class="fas fa-users"></i>
                    <span>UniClub</span>
                </div>

                <div class="search-container">
                    <div class="search-box">
                        <form action="${pageContext.request.contextPath}/events-page" method="get">
                            <i class="fas fa-search search-icon"></i>
                            <input type="text" id="searchInput" name="key" placeholder="Tìm kiếm sự kiện..."
                                   class="search-input">
                            <button type="submit" class="search-btn">
                                <i class="fas fa-search"></i>
                            </button>
                        </form>
                    </div>
                </div>

                <nav class="main-nav">
                    <ul>
                        <li>
                            <a href="${pageContext.request.contextPath}/"
                               class="${pageContext.request.servletPath == '/' ? 'active' : ''}">
                                Trang Chủ
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/clubs"
                               class="${fn:contains(pageContext.request.servletPath, '/clubs') ? 'active' : ''}">
                                Câu Lạc Bộ
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/events-page"
                               class="${fn:contains(pageContext.request.servletPath, '/events-page') ? 'active' : ''}">
                                Sự Kiện
                            </a>
                        </li>
                    </ul>
                </nav>

                <div class="auth-buttons">
                    <c:choose>
                        <c:when test="${sessionScope.user != null}">
                            <div class="user-menu" id="userMenu">
                                <a href="${pageContext.request.contextPath}/notification" class="btn btn-outline">
                                    <i class="fa-solid fa-bell"></i>
                                </a>
                                <a href="${pageContext.request.contextPath}/profile?action=myProfile" class="btn btn-outline">
                                    <i class="fa-solid fa-user"></i>
                                </a>
                                <form action="${pageContext.request.contextPath}/logout" method="post">
                                    <input class="btn btn-primary" type="submit" value="Logout">
                                </form>
                                <a href="${pageContext.request.contextPath}/myclub" class="btn btn-primary">MyClub</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="guest-menu" id="guestMenu">
                                <a href="${pageContext.request.contextPath}/login" class="btn btn-outline">Đăng Nhập</a>
                                <a href="${pageContext.request.contextPath}/register" class="btn btn-primary">Đăng Ký</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <button class="mobile-menu-btn" onclick="toggleMobileMenu()">
                        <i class="fas fa-bars"></i>
                    </button>
                </div>
            </div>

            <!-- Mobile Menu -->
            <div class="mobile-menu" id="mobileMenu">
                <nav class="mobile-nav">
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/"
                               class="${pageContext.request.servletPath == '/' ? 'active' : ''}">Trang Chủ</a></li>
                        <li><a href="${pageContext.request.contextPath}/clubs"
                               class="${pageContext.request.servletPath == '/clubs' ? 'active' : ''}">Câu Lạc Bộ</a></li>
                        <li><a href="${pageContext.request.contextPath}/events-page"
                               class="${pageContext.request.servletPath == '/events-page' ? 'active' : ''}">Sự Kiện</a></li>
                    </ul>
                </nav>
            </div>
            <c:if test="${not empty club}">
                <div class="club-header">
                    <div class="club-info">
                        <div class="club-avatar">
                            <img src="${pageContext.request.contextPath}${club.clubImg}" alt="${club.clubName}"
                                 style="width: 60px; height: 60px; border-radius: 50%;">
                        </div>
                        <div class="club-details">
                            <h1>${club.clubName}</h1>
                            <p>Chủ nhiệm: ${club.clubChairmanName}</p>
                        </div>
                    </div>
                </div>
            </c:if>

            <nav class="dashboard-nav">
                <ul>
                    <li>
                        <a href="${pageContext.request.contextPath}/chairman-page/overview"
                           class="nav-item ${currentPath == '/chairman-page/overview' ? 'active' : ''}">
                            <i class="fas fa-tachometer-alt"></i> Tổng quan
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/chairman-page/myclub-events"
                           class="nav-item ${currentPath == '/chairman-page/myclub-events' ? 'active' : ''}">
                            <i class="fas fa-calendar-alt"></i> Sự kiện
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/chairman-page/tasks"
                           class="nav-item ${currentPath == '/chairman-page/tasks' ? 'active' : ''}">
                            <i class="fas fa-clock"></i> Timeline & Công việc
                        </a>
                    </li>

                    <li>
                        <a href="${pageContext.request.contextPath}/chairman-page/clubmeeting"
                           class="nav-item ${currentPath == '/chairman-page/clubmeeting' ? 'active' : ''}">
                            <i class="fas fa-clock"></i> Cuộc họp
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/chairman-page/reports"
                           class="nav-item ${currentPath == '/chairman-page/tasks' ? 'active' : ''}">
                            <i class="fas fa-file-alt"></i> Báo cáo
                        </a>
                    </li>
                </ul>
            </nav>

        </header>
        <div class="container mt-4">
            <h4 class="mb-4" style="color: #2c5aa0;margin-bottom: 20px; font-size: 24px">Báo cáo định kỳ</h4>

            <c:if test="${empty reports}">
                <div class="alert alert-warning text-center">
                    <i class="fas fa-exclamation-circle me-2"></i>Không có báo cáo nào được tìm thấy.
                </div>
            </c:if>

            <div class="row">
                <c:forEach var="report" items="${reports}">
                    <div class="col-md-6 mb-4">
                        <div class="card report-card">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-center">
                                    <div>
                                        <h5 class="card-title mb-1">Báo cáo kỳ <span class="badge badge-term">${report.term}</span></h5>
                                        <small class="text-muted">
                                            Mã báo cáo: <strong>${report.reportID}</strong><br/>
                                        </small>
                                    </div>
                                    <i class="fas fa-file-alt fa-2x text-secondary"></i>
                                </div>
                                <hr>
                                <p class="mb-1">
                                    Ngày nộp: 
                                    <fmt:formatDate value="${report.submissionDate}" pattern="dd/MM/yyyy HH:mm" />
                                </p>
                                <div class="d-flex justify-content-end">
                                    <a href="${pageContext.request.contextPath}/chairman-page/reports?action=viewDetail&reportId=${report.reportID}" 
                                       class="btn btn-sm btn-outline-primary">
                                        <i class="fas fa-eye me-1"></i>Xem chi tiết
                                    </a>
                                </div>
                                    <c:if test="${report.reportID == reportNowID}">
                                    <div class="d-flex justify-content-end">
                                        <a href="${pageContext.request.contextPath}/chairman-page/reports?action=fixDetailReport" 
                                           class="btn btn-sm btn-outline-secondary">
                                            <i class="fas fa-wrench me-1"></i>Sửa
                                        </a>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <c:if test="${!createReportNow}">
                <div style="margin-bottom: 30px">
                    <a href="${pageContext.request.contextPath}/chairman-page/reports?action=createReports" class="btn btn-success">
                        <i class="fas fa-file-text"></i> Tạo báo cáo kỳ ${termNow}
                    </a>
                </div>
            </c:if>
            <c:if test="${createReportNow}">
                <div style="margin-bottom: 30px">
                    <h6 style="color: green">Đã nộp báo cáo</h6>
                    <a href="${pageContext.request.contextPath}/chairman-page/reports?action=viewDetail&reportId=${reportNowID}" class="btn btn-primary">
                        <i class="fas fa-file-text"></i> Xem báo cáo đã nộp
                    </a>
                </div>
            </c:if>
        </div>
    </body>
</html>
