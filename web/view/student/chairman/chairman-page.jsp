<%-- 
    Document   : chairman-page
    Created on : Jun 14, 2025, 8:42:59 PM
    Author     : LE VAN THUAN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>Chairman Page</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
    <body>
    <header class="header">
        <div class="container header-container">
            <div class="logo">
                <i class="fas fa-users"></i>
                <span>UniClub</span>
            </div>

            <!-- Search Bar -->
            <div class="search-container">
                <div class="search-box">
                    <form action="events-page" method="get">
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
                           class="${currentPath == '/' ? 'active' : ''}">
                            Trang Chủ
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/clubs"
                           class="${fn:contains(currentPath, '/clubs') ? 'active' : ''}">
                            Câu Lạc Bộ
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/events-page"
                           class="${fn:contains(currentPath, '/events-page') ? 'active' : ''}">
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
                            <form action="logout" method="post">
                                <input class="btn btn-primary" type="submit" value="Logout">
                            </form>
                            <a href="${pageContext.request.contextPath}/my-club" class="btn btn-primary">MyClub</a>
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
                    <li><a href="${pageContext.request.contextPath}/" class="${pageContext.request.servletPath == '/index.jsp' ? 'active' : ''}">Trang Chủ</a></li>
                    <li><a href="${pageContext.request.contextPath}/clubs" class="${pageContext.request.servletPath == '/clubs.jsp' ? 'active' : ''}">Câu Lạc Bộ</a></li>
                    <li><a href="${pageContext.request.contextPath}/events-page" class="${pageContext.request.servletPath == '/events-page.jsp' ? 'active' : ''}">Sự Kiện</a></li>
                </ul>
            </nav>
        </div>
        <c:if test="${not empty club}">
            <div class="club-header">
                <div class="club-info">
                    <div class="club-avatar">
                        <img src="${pageContext.request.contextPath}${club.clubImg}" alt="${club.clubName}" style="width: 60px; height: 60px; border-radius: 50%;">
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
                <li><a href="chairman-page?action=overview" class="nav-item ${action == 'overview' ? 'active' : ''}"><i class="fas fa-tachometer-alt"></i> Tổng quan</a></li>
                <li><a href="chairman-page?action=myclub-events" class="nav-item ${action == 'myclub-events' ? 'active' : ''}"><i class="fas fa-calendar-check"></i> Quản lý sự kiện</a></li>
                <li><a href="chairman-page?action=tasks" class="nav-item ${action == 'tasks' ? 'active' : ''}"><i class="fas fa-tasks"></i> Công việc</a></li>
            </ul>
        </nav>


    </header>
    <main class="dashboard-content">
<%--        <!-- Overview Tab -->--%>
<%--        <div id="overview-tab" class="tab-content active">--%>
<%--            <!-- Club Statistics -->--%>
<%--            <section class="stats-section">--%>
<%--                <h2>Thống kê tổng quan</h2>--%>
<%--                <div class="stats-grid">--%>
<%--                    <div class="stat-card">--%>
<%--                        <div class="stat-icon departments">--%>
<%--                            <i class="fas fa-sitemap"></i>--%>
<%--                        </div>--%>
<%--                        <div class="stat-content">--%>
<%--                            <h3 id="totalDepartments">${totalDepartments}</h3>--%>
<%--                            <p>Các ban</p>--%>
<%--                        </div>--%>
<%--                    </div>--%>
<%--                    <div class="stat-card">--%>
<%--                        <div class="stat-icon members">--%>
<%--                            <i class="fas fa-users"></i>--%>
<%--                        </div>--%>
<%--                        <div class="stat-content">--%>
<%--                            <h3 id="totalMembers">${totalMembers}</h3>--%>
<%--                            <p>Thành viên</p>--%>
<%--                        </div>--%>
<%--                    </div>--%>
<%--                    <div class="stat-card">--%>
<%--                        <div class="stat-icon events">--%>
<%--                            <i class="fas fa-calendar"></i>--%>
<%--                        </div>--%>
<%--                        <div class="stat-content">--%>
<%--                            <h3 id="totalEvents">${totalEvents}</h3>--%>
<%--                            <p>Sự kiện</p>--%>
<%--                        </div>--%>
<%--                    </div>--%>
<%--                    <div class="stat-card">--%>
<%--                        <div class="stat-icon active-tasks">--%>
<%--                            <i class="fas fa-tasks"></i>--%>
<%--                        </div>--%>
<%--                        <div class="stat-content">--%>
<%--                            <h3 id="activeTasks">8</h3>--%>
<%--                            <p>Nhiệm vụ đang thực hiện</p>--%>
<%--                        </div>--%>
<%--                    </div>--%>
<%--                </div>--%>
<%--            </section>--%>

<%--&lt;%&ndash;            <!-- Departments Overview -->&ndash;%&gt;--%>
<%--&lt;%&ndash;            <section class="departments-overview">&ndash;%&gt;--%>
<%--&lt;%&ndash;                <h2>Các ban trong CLB</h2>&ndash;%&gt;--%>
<%--&lt;%&ndash;                <div class="departments-grid" id="departmentsGrid">&ndash;%&gt;--%>
<%--&lt;%&ndash;                    <!-- Departments will be populated by JavaScript -->&ndash;%&gt;--%>
<%--&lt;%&ndash;                </div>&ndash;%&gt;--%>
<%--&lt;%&ndash;            </section>&ndash;%&gt;--%>

<%--&lt;%&ndash;            <!-- Recent Activities -->&ndash;%&gt;--%>
<%--&lt;%&ndash;            <section class="recent-activities">&ndash;%&gt;--%>
<%--&lt;%&ndash;                <h2>Hoạt động gần đây</h2>&ndash;%&gt;--%>
<%--&lt;%&ndash;                <div class="activities-list" id="activitiesList">&ndash;%&gt;--%>
<%--&lt;%&ndash;                    <!-- Recent activities will be populated by JavaScript -->&ndash;%&gt;--%>
<%--&lt;%&ndash;                </div>&ndash;%&gt;--%>
<%--&lt;%&ndash;            </section>&ndash;%&gt;--%>
<%--        </div>--%>

    <c:choose>
        <c:when test="${action == 'overview'}">
            <jsp:include page="view/student/chairman/overview.jsp" />
        </c:when>
        <c:when test="${action == 'myclub-events'}">
            <jsp:include page="view/student/chairman/myclub-events.jsp" />
        </c:when>
        <c:when test="${action == 'tasks'}">
            <jsp:include page="view/student/chairman/tasks.jsp" />
        </c:when>
        <c:otherwise>
            <jsp:include page="view/student/chairman/overview.jsp" />
        </c:otherwise>
    </c:choose>


    </main>
    </body>
</html>
