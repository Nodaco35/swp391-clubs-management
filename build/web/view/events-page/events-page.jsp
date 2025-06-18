<%--
  Created by IntelliJ IDEA.
  User: thuan
  Date: 5/25/2025
  Time: 10:50 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>Events Page</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/eventsPage.css">
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
                        <a href="${pageContext.request.contextPath}/chairman-page" class="btn btn-primary">MyClub</a>
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
    <c:if test="${sessionScope.user == null}">
        <jsp:include page="banner.jsp"/>
    </c:if>
    <section class="breadcrumb-section">
        <div class="container">
            <nav class="breadcrumb">
                <a href="${pageContext.request.contextPath}/events-page" class="breadcrumb-link">
                    <i class="fas fa-calendar"></i>
                    Danh sách sự kiện
                </a>
            </nav>
        </div>
    </section>
    <section class="events-section">
        <div class="container">
            <div class="section-header">
                <h2>Sự Kiện Sắp Tới</h2>
                <p>Khám phá các sự kiện thú vị và bổ ích đang chờ đón bạn</p>
            </div>
            <div class="event-filters-wrapper">
                <!-- Event Filter Buttons -->
                <div class="event-filters">
                    <a href="${pageContext.request.contextPath}/events-page" class="filter-btn"><i
                            class="fa-solid fa-arrow-rotate-right"></i></a>
                    <a href="events-page?key=${currentKeyword}&publicFilter=all&sortByDate=${currentSortByDate}"
                       class="filter-btn ${currentPublicFilter == 'all' ? 'active' : ''}">
                        <i class="fas fa-globe"></i>
                        Tất Cả Sự Kiện
                    </a>
                    <a href="events-page?key=${currentKeyword}&publicFilter=public&sortByDate=${currentSortByDate}"
                       class="filter-btn ${currentPublicFilter == 'public' ? 'active' : ''}">
                        <i class="fas fa-users"></i>
                        Sự Kiện Công Khai
                    </a>
                    <a href="events-page?key=${currentKeyword}&publicFilter=private&sortByDate=${currentSortByDate}"
                       class="filter-btn ${currentPublicFilter == 'private' ? 'active' : ''}">
                        <i class="fas fa-lock"></i>
                        Sự Kiện Riêng Tư (Club)
                    </a>
                </div>
                <div class="event-sort">
                    <select id="sortSelect" class="sort-select" onchange="changeSort(this.value)">
                        <option value="newest" ${currentSortByDate == 'newest' ? 'selected' : ''}>Mới Nhất</option>
                        <option value="oldest" ${currentSortByDate == 'oldest' ? 'selected' : ''}>Cũ Nhất</option>
                    </select>
                </div>
            </div>

            <!-- Events Info -->
            <div class="events-info">
                <p>Hiển thị ${fn:length(events)} trên tổng ${requestScope.totalEvents} sự kiện
                    <c:if test="${currentPage > 0 && totalPages > 0}">
                        - Trang ${currentPage}/${totalPages}
                    </c:if>
                </p>
            </div>

            <!-- Events Grid -->
            <div class="events-grid" id="eventsGrid">
                <c:forEach var="e" items="${requestScope.events}">
                    <div class="event-card" onclick="redirectToDetail('${e.eventID}')">
                        <div class="event-image">
                            <i class="fas fa-calendar-day"></i>
                            <span class="event-badge ${e.isPublic() ? 'badge-public' : 'badge-private'}">
                                    ${e.isPublic() ? 'Công khai' : 'Riêng tư'}
                            </span>
                        </div>
                        <div class="event-content">
                            <h3 class="event-title">${e.eventName}</h3>
                                <%--                                    <p class="event-description">${e.description}</p>--%>
                            <div class="event-details">
                                <div class="event-detail">
                                    <i class="fas fa-calendar-alt"></i>
                                    <span><fmt:formatDate value="${e.eventDate}" pattern="dd/MM/yyyy HH:mm"/></span>
                                </div>

                                <div class="event-detail">
                                    <i class="fas fa-map-marker-alt"></i>
                                    <span>${e.location}</span>
                                </div>
                                <div class="event-detail">
                                    <i class="fas fa-users"></i>
                                    <span>Sức chứa: ${e.capacity}</span>
                                </div>
                            </div>
                            <div class="event-club">
                                <strong>Club Name:</strong> ${e.clubName}
                            </div>
                            <div class="event-footer">
                                <span class="attendees status-${fn:toLowerCase(e.status)}">${e.status}</span>
                                <c:set var="isMyEvent" value="false" />
                                <c:forEach var="id" items="${sessionScope.myEventIDs}">
                                    <c:if test="${id == e.eventID}">
                                        <c:set var="isMyEvent" value="true" />
                                    </c:if>
                                </c:forEach>

                                <c:choose>
                                    <c:when test="${isMyEvent}">
                                        <button type="button" class="register-btn">
                                            <a href="${pageContext.request.contextPath}/chairman-page">MyClub</a>
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <c:choose>
                                            <c:when test="${e.status == 'PENDING' || e.status == 'Pending'}">
                                                <button type="button" class="register-btn">
                                                    Đăng ký
                                                </button>
                                            </c:when>
                                            <c:when test="${e.status == 'Processing' || e.status == 'PROCESSING'}">
                                                <button type="button" class="register-btn" disabled>
                                                    Đang diễn ra
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="button" class="register-btn disabled" disabled>
                                                    Đã kết thúc
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <c:if test="${empty requestScope.events}">
                <p class="no-events"><i class="fas fa-search search-icon"></i> Không có sự kiện nào để hiển thị.</p>
            </c:if>
            <!-- Pagination Controls -->
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a class="page-link"
                       href="events-page?key=${currentKeyword}&publicFilter=${currentPublicFilter}&sortByDate=${currentSortByDate}&page=${currentPage - 1}">Previous</a>
                </c:if>
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <a href="events-page?key=${currentKeyword}&publicFilter=${currentPublicFilter}&sortByDate=${currentSortByDate}&page=${i}"
                       class="${i == currentPage ? 'active' : ''} page-link">${i}</a>
                </c:forEach>
                <c:if test="${currentPage < totalPages}">
                    <a class="page-link"
                       href="events-page?key=${currentKeyword}&publicFilter=${currentPublicFilter}&sortByDate=${currentSortByDate}&page=${currentPage + 1}">Next</a>
                </c:if>
            </div>

        </div>
    </section>
</main>
<jsp:include page="footer.jsp"/>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
<script>
    function redirectToDetail(eventID) {
        window.location.href = "event-detail?id=" + eventID;
    }
</script>
<script>
    const currentKeyword = '${fn:escapeXml(currentKeyword)}';
    const currentPublicFilter = '${currentPublicFilter}';
</script>

</body>
</html>
