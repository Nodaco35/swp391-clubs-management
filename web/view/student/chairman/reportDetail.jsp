<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">

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

    <!-- Kỳ báo cáo -->
    <div class="mb-4">
        <h4 class="fw-bold">Kỳ báo cáo: <span class="text-primary">${term}</span></h4>
    </div>

    <!-- Báo cáo thành viên -->
    <div class="mb-5">
        <h5 class="mb-3">Báo cáo thành viên</h5>
        <div class="table-responsive">
            <table class="table table-borderless align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Họ và tên</th>
                        <th>Mã sinh viên</th>
                        <th>Vai trò</th>
                        <th>Điểm rèn luyện</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="m" items="${members}">
                        <tr>
                            <td>${m.fullName}</td>
                            <td>${m.studentCode}</td>
                            <td>${m.role}</td>
                            <td>${m.progressPoint}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Báo cáo sự kiện -->
    <div>
        <h5 class="mb-3">Báo cáo sự kiện</h5>
        <div class="table-responsive">
            <table class="table table-borderless align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Tên sự kiện</th>
                        <th>Ngày tổ chức</th>
                        <th>Loại sự kiện</th>
                        <th>Số người tham gia</th>
                        <th>Minh chứng</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="e" items="${events}">
                        <tr>
                            <td>${e.eventName}</td>
                            <td>
                                <fmt:formatDate value="${e.eventDate}" pattern="dd/MM/yyyy" />
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${e.eventType eq 'Internal'}">Nội bộ</c:when>
                                    <c:otherwise>Công khai</c:otherwise>
                                </c:choose>
                            </td>
                            <td>${e.participantCount}</td>
                            <td>
                                <c:if test="${not empty e.proofLink}">
                                    <a href="${e.proofLink}" target="_blank">Xem</a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Nút quay lại -->
    <div style="margin-bottom: 30px">
        <a href="${pageContext.request.contextPath}/chairman-page/reports" class="btn btn-secondary">
            <i class="fas fa-arrow-left me-1"></i> Quay lại
        </a>
    </div>
</div>
