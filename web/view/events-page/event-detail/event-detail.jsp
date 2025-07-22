
<%--
    Document   : event-detail
    Created on : May 30, 2025, 11:26:29 PM
    Author     : LE VAN THUAN
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eventDetail.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/typeFeedback.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewFeedback.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <title>Event Detail</title>
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
                            <a href="${pageContext.request.contextPath}/">Trang Chủ</a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/clubs">Câu Lạc Bộ</a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/events-page">Sự Kiện</a>
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
                               class="${pageContext.request.servletPath == '/index.jsp' ? 'active' : ''}">Trang Chủ</a></li>
                        <li><a href="${pageContext.request.contextPath}/clubs"
                               class="${pageContext.request.servletPath == '/clubs.jsp' ? 'active' : ''}">Câu Lạc Bộ</a></li>
                        <li><a href="${pageContext.request.contextPath}/events-page"
                               class="${pageContext.request.servletPath == '/events-page.jsp' ? 'active' : ''}">Sự Kiện</a></li>
                    </ul>
                </nav>
            </div>
        </header>

        <main>
            <!-- Breadcrumb -->
            <section class="breadcrumb-section">
                <div class="container">
                    <nav class="breadcrumb">
                        <a href="${pageContext.request.contextPath}/events-page" class="breadcrumb-link">
                            <i class="fas fa-calendar"></i>
                            Danh sách sự kiện
                        </a>
                        <span class="breadcrumb-separator">/</span>
                        <a href="#" class="breadcrumb-link">
                            <span class="breadcrumb-current"><i class="fas fa-info-circle"></i> Chi tiết sự kiện</span>
                        </a>
                    </nav>
                </div>
            </section>

            <!-- Event Detail Section -->
            <section class="event-detail-section">
                <div class="container">
                    <div class="event-detail-container">
                        <c:set var="e" value="${requestScope.event}"/>
                        <!-- Event Header -->
                        <div class="event-detail-header"
                             style="background-image: url('${pageContext.request.contextPath}/${e.eventImg}');">
                            <div class="event-status-badges">
                                <div class="event-badge ${e.isPublic() ? 'badge-public' : 'badge-private'}">
                                    ${e.isPublic() ? 'Công khai' : 'Riêng tư'}
                                </div>
                            </div>
                        </div>

                        <!-- Event Content -->
                        <div class="event-detail-content">
                            <h1 class="event-detail-title">${e.eventName}</h1>

                            <!-- Event Meta Information -->
                            <div class="event-meta">
                                <c:if test="${not empty e.schedules}">
                                    <div class="meta-item">
                                        <div class="meta-icon">
                                            <i class="fas fa-calendar-alt"></i>
                                        </div>
                                        <div class="meta-content">
                                            <h4>Ngày tổ chức</h4>
                                            <p><fmt:formatDate value="${e.schedules[0].eventDate}" pattern="dd/MM/yyyy"/></p>
                                        </div>
                                    </div>
                                    <div class="meta-item">
                                        <div class="meta-icon">
                                            <i class="fas fa-clock"></i>
                                        </div>
                                        <div class="meta-content">
                                            <h4>Thời gian</h4>
                                            <p>
                                                <fmt:formatDate value="${e.schedules[0].startTime}" pattern="HH:mm"/> -
                                                <fmt:formatDate value="${e.schedules[0].endTime}" pattern="HH:mm"/>
                                            </p>
                                        </div>
                                    </div>
                                    <div class="meta-item">
                                        <div class="meta-icon">
                                            <i class="fas fa-map-marker-alt"></i>
                                        </div>
                                        <div class="meta-content">
                                            <h4>Địa điểm</h4>
                                            <p>${e.schedules[0].location.locationName}</p>
                                        </div>
                                    </div>
                                </c:if>
                                <div class="meta-item">
                                    <div class="meta-icon">
                                        <i class="fas fa-users"></i>
                                    </div>
                                    <div class="meta-content">
                                        <h4>Số lượng</h4>
                                        <p>${e.capacity}</p>
                                    </div>
                                </div>
                            </div>

                            <!-- Event Schedules -->
                            <c:if test="${fn:length(e.schedules) > 1}">
                                <div class="event-schedules-section">
                                    <h3 class="section-title">
                                        <i class="fas fa-calendar-alt"></i> Các lịch trình khác trong sự kiện
                                    </h3>
                                    <div class="schedules-list">
                                        <c:forEach var="schedule" items="${e.schedules}" varStatus="loop">
                                            <c:if test="${loop.index > 0}">
                                                <div class="schedule-item">
                                                    <p>
                                                        <strong>Ngày:</strong> <fmt:formatDate value="${schedule.eventDate}" pattern="dd/MM/yyyy"/>
                                                        <br>
                                                        <strong>Thời gian:</strong> <fmt:formatDate value="${schedule.startTime}" pattern="HH:mm"/> - 
                                                        <fmt:formatDate value="${schedule.endTime}" pattern="HH:mm"/>
                                                        <br>
                                                        <strong>Địa điểm:</strong> ${schedule.location.locationName}
                                                    </p>
                                                </div>
                                            </c:if>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:if>

                            <!-- Event Description -->
                            <div class="event-description-section">
                                <h3 class="section-title">
                                    <i class="fas fa-info-circle"></i>
                                    Mô tả chi tiết
                                </h3>
                                <div class="event-description">
                                    ${e.description}
                                </div>
                            </div>

                            <!-- Agenda Section -->
                            <h3 class="section-title">
                                <i class="fas fa-clock"></i> Chi tiết Agenda
                            </h3>

                            <c:choose>
                                <c:when test="${not empty requestScope.agendaDetails}">
                                    <div class="form-group d-flex" style="gap: 16px;">
                                        <div style="flex: 1;">
                                            <label for="locationFilter">Lọc theo địa điểm:</label>
                                            <select id="locationFilter" class="form-control" onchange="filterAgenda()">
                                                <option value="all">Tất cả địa điểm</option>
                                                <c:forEach var="schedule" items="${e.schedules}">
                                                    <option value="${schedule.location.locationName}">${schedule.location.locationName}</option>
                                                </c:forEach>
                                            </select>
                                        </div>

                                        <div style="flex: 1;">
                                            <label for="dateFilter">Lọc theo ngày:</label>
                                            <select id="dateFilter" class="form-control" onchange="filterAgenda()">
                                                <option value="all">Tất cả ngày</option>
                                                <c:forEach var="schedule" items="${e.schedules}">
                                                    <fmt:formatDate var="formattedDate" value="${schedule.eventDate}" pattern="dd/MM/yyyy" />
                                                    <option value="${formattedDate}">${formattedDate}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>

                                    <div class="agenda-timeline">
                                        <c:forEach var="schedule" items="${e.schedules}">
                                            <fmt:formatDate var="formattedDate" value="${schedule.eventDate}" pattern="dd/MM/yyyy" />
                                            <div class="agenda-group" data-location="${schedule.location.locationName}" data-date="${formattedDate}">
                                                <h4 class="location-header">
                                                    <i class="fas fa-map-marker-alt"></i> ${schedule.location.locationName}
                                                    <span style="font-weight: normal;">| Ngày: ${formattedDate}</span>
                                                </h4>
                                                <c:set var="foundAgenda" value="false" />
                                                <c:forEach var="agenda" items="${agendaDetails}">
                                                    <c:if test="${agenda.scheduleID == schedule.scheduleID}">
                                                        <c:set var="foundAgenda" value="true" />
                                                        <div class="agenda-item">
                                                            <div class="agenda-time">
                                                                <fmt:formatDate value="${agenda.startTime}" pattern="HH:mm"/> -
                                                                <fmt:formatDate value="${agenda.endTime}" pattern="HH:mm"/>
                                                            </div>
                                                            <div class="agenda-content">
                                                                <h5>${agenda.title}</h5>
                                                                <p>${agenda.description}</p>
                                                                <p><strong>Ngày:</strong> ${formattedDate}</p>
                                                            </div>
                                                        </div>
                                                    </c:if>
                                                </c:forEach>
                                                <c:if test="${not foundAgenda}">
                                                    <p class="no-events">Không có Agenda nào cho địa điểm này.</p>
                                                </c:if>
                                            </div>
                                        </c:forEach>

                                        <!-- Thông báo khi không có agenda sau khi lọc -->
                                        <p id="noAgendaFound" class="no-events" style="display: none; margin-top: 20px; font-style: italic;">Không tìm thấy agenda nào phù hợp với bộ lọc.</p>
                                    </div>

                                    <!-- JavaScript filter -->
                                    <script>
                                        function filterAgenda() {
                                            const selectedLocation = document.getElementById("locationFilter").value;
                                            const selectedDate = document.getElementById("dateFilter").value;
                                            const groups = document.querySelectorAll(".agenda-group");
                                            let visibleCount = 0;

                                            groups.forEach(group => {
                                                const location = group.getAttribute("data-location");
                                                const date = group.getAttribute("data-date");

                                                const matchLocation = (selectedLocation === "all" || selectedLocation === location);
                                                const matchDate = (selectedDate === "all" || selectedDate === date);

                                                const shouldShow = matchLocation && matchDate;
                                                group.style.display = shouldShow ? "block" : "none";

                                                if (shouldShow)
                                                    visibleCount++;
                                            });

                                            const noAgendaText = document.getElementById("noAgendaFound");
                                            noAgendaText.style.display = visibleCount === 0 ? "block" : "none";
                                        }
                                    </script>
                                </c:when>
                                <c:otherwise>
                                    <p class="no-events">Agenda của sự kiện đang được cập nhật.</p>
                                </c:otherwise>
                            </c:choose>




                            <!-- Organizer Info -->
                            <div class="organizer-info">
                                <c:set var="ownerInfo" value="${requestScope.ownerInfo}"/>
                                <div class="organizer-avatar">
                                    <i class="fas fa-user"></i>
                                </div>
                                <div class="organizer-details">
                                    <h4>${ownerInfo.fullName}</h4>
                                    <p>Người tổ chức • ${ownerInfo.clubName}</p>
                                </div>
                                <a href="mailto:${ownerInfo.email}" class="contact-btn">
                                    <i class="fas fa-envelope"></i>
                                    Liên hệ: ${ownerInfo.email}
                                </a>
                            </div>

                            <!-- Related Events -->
                            <h3 class="section-title">
                                <i class="fas fa-info-circle"></i>
                                Sự kiện liên quan
                            </h3>
                            <div class="events-grid" id="eventsGrid">
                                <c:forEach var="e" items="${requestScope.relatedEvents}">
                                    <div class="event-card" onclick="redirectToDetail('${e.eventID}')">
                                        <div class="event-image">
                                            <img src="${pageContext.request.contextPath}/${e.eventImg}" alt="${e.eventName}"/>
                                            <span class="event-badge ${e.isPublic() ? 'badge-public' : 'badge-private'}">
                                                ${e.isPublic() ? 'Công khai' : 'Riêng tư'}
                                            </span>
                                        </div>
                                        <div class="event-content">
                                            <h3 class="event-title">${e.eventName}</h3>
                                            <div class="event-details">
                                                <c:if test="${not empty e.schedules}">
                                                    <div class="event-detail">
                                                        <i class="fas fa-calendar-alt"></i>
                                                        <span>
                                                            <fmt:formatDate value="${e.schedules[0].eventDate}" pattern="dd/MM/yyyy"/>
                                                            <fmt:formatDate value="${e.schedules[0].startTime}" pattern="HH:mm"/>
                                                        </span>
                                                    </div>
                                                    <div class="event-detail">
                                                        <i class="fas fa-map-marker-alt"></i>
                                                        <span>${e.schedules[0].location.locationName}</span>
                                                    </div>
                                                </c:if>
                                                <div class="event-detail">
                                                    <i class="fas fa-users"></i>
                                                    <span>Sức chứa: ${e.capacity}</span>
                                                </div>
                                            </div>
                                            <div class="event-footer">
                                                <c:choose>
                                                    <c:when test="${e.status == 'PENDING' || e.status == 'Pending'}">
                                                        <button type="button" class="register-btn"
                                                                onclick="registerEvent(${e.eventID})">
                                                            Đăng ký
                                                        </button>
                                                    </c:when>
                                                    <c:when test="${e.status == 'Processing' || e.status == 'PROCESSING'}">
                                                        <button type="button" class="register-btn disabled" disabled>
                                                            Đang diễn ra
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <button type="button" class="register-btn disabled" disabled>
                                                            Đã kết thúc
                                                        </button>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                            <c:if test="${empty requestScope.relatedEvents}">
                                <p class="no-events"><i class="fas fa-search search-icon"></i> Không có sự kiện liên quan nào để hiển thị.</p>
                            </c:if>
                        </div>

                        <!-- Registration Section -->
                        <div class="registration-section">
                            <h3 class="registration-title">Đăng ký tham gia</h3>
                            <c:set var="e" value="${requestScope.event}"/>
                            <div class="registration-info">
                                <div class="attendee-count">
                                    <i class="fas fa-users"></i>
                                    <span>Số lượng: ${e.capacity}</span>
                                </div>
                                <p>Còn <strong>${spotsLeft}</strong> chỗ trống</p>
                            </div>

                            <div class="registration-buttons">
                                <a href="${pageContext.request.contextPath}/events-page" class="register-btn-secondary">
                                    <i class="fas fa-arrow-left"></i>
                                    Xem sự kiện khác
                                </a>
                                <%@ page import="dal.UserClubDAO" %>
                                <c:if test="${sessionScope.user != null}">
                                    <c:set var="clubId" value="${event.clubID}"/>
                                    <c:set var="userId" value="${sessionScope.user.userID}"/>
                                    <c:set var="eventId" value="${event.eventID}"/>
                                    <%
                                        String userIdStr = (String) pageContext.findAttribute("userId");
                                        Integer clubIdInt = (Integer) pageContext.findAttribute("clubId");
                                        Integer eventIdInt = (Integer) pageContext.findAttribute("eventId");

                                        UserClubDAO userClubDAO = new UserClubDAO();
                                        boolean isClubPresident = false;

                                        if (userIdStr != null && clubIdInt != null) {
                                            isClubPresident = userClubDAO.isClubPresident(userIdStr, clubIdInt);
                                        }

                                        pageContext.setAttribute("isClubPresident", isClubPresident);
                                    %>
                                </c:if>

                                <c:choose>
                                    <c:when test="${sessionScope.isChairman == true && e.clubID == sessionScope.myClubID}">
                                        <a href="${pageContext.request.contextPath}/chairman-page/overview"
                                           class="btn btn-primary">
                                            <i class="fas fa-crown"></i> MyClub
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <c:choose>
                                            <c:when test="${!isLoggedIn}">
                                                <span class="register-btn-finish disabled">
                                                    <i class="fas fa-exclamation-circle" style="color: red;"></i> Bạn cần
                                                    <a href="${pageContext.request.contextPath}/login">đăng nhập</a> để có thể đăng ký tham gia sự kiện.
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <c:choose>
                                                    <c:when test="${e.status == 'PENDING' || e.status == 'Pending'}">
                                                        <c:choose>
                                                            <c:when test="${e.isPublic() || isMember}">
                                                                <c:choose>
                                                                    <c:when test="${spotsLeft > 0}">
                                                                        <a href="${pageContext.request.contextPath}/registration-event?id=${e.eventID}"
                                                                           class="register-btn-primary">
                                                                            <i class="fas fa-user-plus"></i> Đăng ký ngay
                                                                        </a>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <button type="button"
                                                                                class="register-btn-finish disabled" disabled>
                                                                            Sự kiện đã đủ số lượng đăng ký
                                                                        </button>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <button type="button" class="register-btn-finish disabled"
                                                                        disabled>
                                                                    Sự kiện nội bộ - Chỉ dành cho thành viên CLB
                                                                </button>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div style="display: flex; gap: 10px;">
                                                            <button type="button" class="register-btn-finish disabled" disabled>
                                                                Đã kết thúc
                                                            </button>

                                                            <c:if test="${sessionScope.user != null}">
                                                                <%@ page import="dal.EventParticipantDAO" %>
                                                                <%@ page import="dal.FeedbackDAO" %>
                                                                <%
                                                                    String userIdStr = (String) pageContext.findAttribute("userId");
                                                                    Integer eventIdInt = (Integer) pageContext.findAttribute("eventId");

                                                                    EventParticipantDAO participantDAO = new EventParticipantDAO();
                                                                    FeedbackDAO feedbackDAO = new FeedbackDAO();

                                                                    boolean hasParticipated = false;
                                                                    boolean hasFeedback = false;

                                                                    if (userIdStr != null && eventIdInt != null) {
                                                                        hasParticipated = participantDAO.hasUserParticipatedInEvent(userIdStr, eventIdInt);
                                                                        if (hasParticipated) {
                                                                            hasFeedback = feedbackDAO.hasFeedbackForEvent(eventIdInt, userIdStr);
                                                                        }
                                                                    }

                                                                    pageContext.setAttribute("hasParticipated", hasParticipated);
                                                                    pageContext.setAttribute("hasFeedback", hasFeedback);
                                                                %>

                                                                <c:choose>
                                                                    <c:when test="${hasParticipated && !hasFeedback}">
                                                                        <a href="${pageContext.request.contextPath}/typefeedback?eventId=${event.eventID}"
                                                                           class="feedback-btn">
                                                                            <i class="fas fa-star"></i> Gửi feedback
                                                                        </a>
                                                                    </c:when>
                                                                    <c:when test="${hasParticipated && hasFeedback}">
                                                                        <button type="button" class="feedback-btn disabled"
                                                                                disabled>
                                                                            <i class="fas fa-check"></i> Đã gửi feedback
                                                                        </button>
                                                                    </c:when>
                                                                </c:choose>
                                                            </c:if>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:otherwise>
                                </c:choose>

                                <c:if test="${sessionScope.user != null && e.status != 'PENDING' && e.status != 'Pending' && e.status != 'Processing' && e.status != 'PROCESSING'}">
                                    <c:if test="${isClubPresident}">
                                        <a href="${pageContext.request.contextPath}/viewFeedback?eventId=${event.eventID}"
                                           class="view-feedback-btn">
                                            <i class="fas fa-chart-bar"></i> Xem feedback
                                        </a>
                                    </c:if>
                                </c:if>
                            </div>
                        </div>
                    </div>
            </section>
        </main>

        <footer class="footer">
            <div class="container footer-container">
                <div class="footer-logo">
                    <i class="fas fa-users"></i>
                    <span>UniClub</span>
                </div>
                <p class="copyright">
                    © 2023 UniClub. Tất cả các quyền được bảo lưu.
                </p>
                <div class="footer-links">
                    <a href="/terms">Điều Khoản</a>
                    <a href="/privacy">Chính Sách</a>
                    <a href="/contact">Liên Hệ</a>
                </div>
            </div>
        </footer>
        <script>
            function redirectToDetail(eventID) {
                window.location.href = "event-detail?id=" + eventID;
            }

            function registerEvent(eventID) {
                window.location.href = "${pageContext.request.contextPath}/registration-event?id=" + eventID;
            }
        </script>
    </body>
</html>