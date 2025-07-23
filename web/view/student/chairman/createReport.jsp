<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

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
        <h4 class="fw-bold">Tạo báo cáo kỳ: <span class="text-primary">${semesterID}</span></h4>
    </div>

    <form method="get" action="${pageContext.request.contextPath}/chairman-page/reports?action=submitReport">
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
                            <th>Ban</th>
                            <th>Điểm rèn luyện</th>
                            <th>Hành động</th> <!-- Thêm cột nút Lưu -->
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="m" items="${members}" varStatus="i">
                            <tr>
                        <form action="${pageContext.request.contextPath}/chairman-page/reports" method="get">
                            <input type="hidden" name="action" value="editPointFromReport" />
                            <input type="hidden" name="userID" value="${m.userID}" />
                            <input type="hidden" name="termID" value="${semesterID}" />
                            <td>${m.fullName}</td>
                            <td>${m.studentCode}</td>
                            <td>${m.role}</td>
                            <td>${m.department}</td>
                            <td>
                                <input type="number"
                                       name="points"
                                       min="0"
                                       max="100"
                                       pattern="[0-9]*"
                                       class="form-control"
                                       value="${m.progressPoint}" required>
                            </td>
                            <td>
                                <button type="submit" class="btn btn-success btn-sm">
                                    <i class="fas fa-check"></i> Lưu
                                </button>
                            </td>
                        </form>
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
                <table class="table align-middle" style="border: 1px">
                    <thead class="table-light">
                        <tr>
                            <th>Tên sự kiện</th>
                            <th>Ngày tổ chức</th>
                            <th>Số người tham gia</th>
                            <th>Ngày</th>
                            <th>Thời gian bắt đầu</th>
                            <th>Thời gian kết thúc</th>
                            <th>Địa điểm</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="e" items="${publicEvents}">
                            <c:forEach var="schedule" items="${e.scheduleList}" varStatus="status">
                                <tr>
                                    <c:if test="${status.first}">
                                        <td rowspan="${fn:length(e.scheduleList)}">${e.eventName}</td>
                                    </c:if>

                                    <td><fmt:formatDate value="${schedule.eventDate}" pattern="dd/MM/yyyy"/></td>

                                    <c:if test="${status.first}">
                                        <td rowspan="${fn:length(e.scheduleList)}">${e.participantCount}</td>
                                    </c:if>

                                    <td>${schedule.eventDate}</td>
                                    <td>${schedule.startTime}</td>
                                    <td>${schedule.endTime}</td>
                                    <td>${schedule.locationName}</td>

                                </tr>
                            </c:forEach>
                        </c:forEach>
                    </tbody>

                </table>


            </div>


        </div>

        <!-- Nút hành động -->
        <div class="mt-4 mb-5">
            <a href="${pageContext.request.contextPath}/chairman-page/reports" class="btn btn-secondary me-2">
                <i class="fas fa-arrow-left me-1"></i> Quay lại
            </a>
            <a href="${pageContext.request.contextPath}/chairman-page/reports?action=submitReport" 
               class="btn btn-success"><i class="fas fa-paper-plane me-1"></i> Nộp báo cáo
            </a>

        </div>
    </form>
</div>
