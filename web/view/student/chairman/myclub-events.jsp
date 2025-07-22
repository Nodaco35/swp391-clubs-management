
<%-- 
    Document   : myclub-events
    Created on : Jun 15, 2025, 11:26:48 AM
    Author     : LE VAN THUAN
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
    <head>
        <title>MyClub Dashboard</title>
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
                        <li><a href="${pageContext.request.contextPath}/" class="${pageContext.request.servletPath == '/' ? 'active' : ''}">Trang Chủ</a></li>
                        <li><a href="${pageContext.request.contextPath}/clubs" class="${pageContext.request.servletPath == '/clubs' ? 'active' : ''}">Câu Lạc Bộ</a></li>
                        <li><a href="${pageContext.request.contextPath}/events-page" class="${pageContext.request.servletPath == '/events-page' ? 'active' : ''}">Sự Kiện</a></li>
                    </ul>
                </nav>
            </div>
            <div class="club-header">
                <c:if test="${not empty club}">
                    <div class="club-info">
                        <div class="club-avatar">
                            <img src="${pageContext.request.contextPath}${club.clubImg}" alt="${club.clubName}" style="width: 60px; height: 60px; border-radius: 50%;">
                        </div>
                        <div class="club-details">
                            <h1>${club.clubName}</h1>
                            <p>Chủ nhiệm: ${club.clubChairmanName}</p>
                        </div>
                    </div>
                </c:if>
            </div>

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
        <main class="dashboard-content">
            <div id="events-tab" class="tab-content">
                <section class="stats-section">
                    <h2>Tổng quan sự kiện</h2>
                    <div class="stats-grid">
                        <div class="stat-card">
                            <div class="stat-icon upcoming">
                                <i class="fas fa-calendar-plus"></i>
                            </div>
                            <div class="stat-content">
                                <h3>${totalUpcoming}</h3>
                                <p>Sự kiện sắp tới</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon ongoing">
                                <i class="fas fa-calendar-check"></i>
                            </div>
                            <div class="stat-content">
                                <h3>${totalOngoing}</h3>
                                <p>Sự kiện đang diễn ra</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon completed">
                                <i class="fas fa-calendar-times"></i>
                            </div>
                            <div class="stat-content">
                                <h3>${totalPast}</h3>
                                <p>Sự kiện đã kết thúc</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon total">
                                <i class="fas fa-calendar"></i>
                            </div>
                            <div class="stat-content">
                                <h3>${totalEvents}</h3>
                                <p>Tổng sự kiện</p>
                            </div>
                        </div>
                    </div>
                </section>
                <section class="events-management">
                    <div class="section-header">
                        <div class="section-title">
                            <h2>Quản lý sự kiện</h2>
                        </div>
                        <div class="section-actions">
                            <select id="eventStatusFilter" class="filter-select">
                                <option value="">Tất cả trạng thái</option>
                                <option value="upcoming">Sắp tới</option>
                                <option value="ongoing">Đang diễn ra</option>
                                <option value="completed">Đã kết thúc</option>
                            </select>
                            <a href="${pageContext.request.contextPath}/chairman-page/myclub-events/add-event" class="btn-add-event">
                                <i class="fas fa-plus"></i> Thêm sự kiện
                            </a>
                        </div>
                    </div>

                    <div class="events-table-container">
                        <table class="events-table">
                            <thead>
                                <tr>
                                    <th>Tên sự kiện</th>
                                    <th>Ngày tổ chức</th>
                                    <th>Địa điểm</th>
                                    <th>Số người tham gia</th>
                                    <th>Trạng thái</th>
                                    <th>Duyệt</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody id="clubEventsTableBody">
                                <c:forEach var="event" items="${myClubEvents}">
                                    <c:choose>
                                        <c:when test="${event.status == 'PENDING' || event.status == 'Pending'}">
                                            <c:set var="statusKey" value="upcoming"/>
                                        </c:when>
                                        <c:when test="${event.status == 'PROCESSING' || event.status == 'Processing'}">
                                            <c:set var="statusKey" value="ongoing"/>
                                        </c:when>
                                        <c:when test="${event.status == 'COMPLETED' || event.status == 'Completed'}">
                                            <c:set var="statusKey" value="completed"/>
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="statusKey" value="unknown"/>
                                        </c:otherwise>
                                    </c:choose>

                                </span>
                            </td>
                            <td>
                                <span class="status ${event.approvalStatus}">
                                    <c:if test="${event.isPublic() == true}">
                                        <c:choose>
                                            <c:when test="${event.approvalStatus == 'PENDING'}">Chờ duyệt</c:when>
                                            <c:when test="${event.approvalStatus == 'APPROVED'}">Đã duyệt</c:when>
                                            <c:when test="${event.approvalStatus == 'REJECTED'}">Từ chối</c:when>
                                            <c:otherwise>Không xác định</c:otherwise>
                                        </c:choose>
                                    </c:if>
                                </span>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/event-detail?id=${event.eventID}" class="btn-action form" title="Xem chi tiết">
                                    <i class="fa-regular fa-rectangle-list"></i>
                                </a>
                                <a href="${pageContext.request.contextPath}/chairman-page/myclub-events/edit-event?eventID=${event.eventID}" class="btn-action edit" title="Sửa">
                                    <i class="fas fa-edit"></i>
                                </a>
                                <c:if test="${(event.status == 'PENDING' || event.status == 'Pending' ) && event.approvalStatus == 'APPROVED'}">
                                    <a href="#" class="btn-action assign" data-event-id="${event.eventID}" title="Giao việc">
                                        <i class="fas fa-tasks"></i>
                                    </a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </section>
    </div>

    <div class="modal" id="eventTermModal">
        <div class="modal-content">
            <span class="close">×</span>
            <h2>Thêm Giai Đoạn Sự Kiện</h2>
            <c:if test="${not empty termError}">
                <div class="error-message" style="color: red; margin-bottom: 10px;">
                        ${termError}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/chairman-page/myclub-events" method="post" id="addEventTermForm">
                <input type="hidden" name="action" value="addTerm">
                <input type="hidden" name="eventID" id="eventID">
                <div class="form-group">
                    <label for="termName">Tên Giai Đoạn:</label>
                    <select name="termName" id="termName" required>
                        <option value="Trước sự kiện">Trước sự kiện</option>
                        <option value="Trong sự kiện">Trong sự kiện</option>
                        <option value="Sau sự kiện">Sau sự kiện</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="termStart">Ngày Bắt Đầu:</label>
                    <input type="date" name="termStart" id="termStart" required>
                </div>
                <div class="form-group">
                    <label for="termEnd">Ngày Kết Thúc:</label>
                    <input type="date" name="termEnd" id="termEnd" required>
                </div>
                <button type="submit" class="btn btn-primary">Thêm Giai Đoạn</button>
            </form>
        </div>
    </div>
</main>

        <script>
            // Lọc theo status
            window.addEventListener("DOMContentLoaded", function () {
                document.getElementById("eventStatusFilter").value = "upcoming";
                document.getElementById("eventStatusFilter").dispatchEvent(new Event("change"));
            });

            document.getElementById("eventStatusFilter").addEventListener("change", function () {
                const selectedStatus = this.value;
                const rows = document.querySelectorAll("#clubEventsTableBody tr");

        rows.forEach(row => {
            const status = row.getAttribute("data-status").toLowerCase();
            if (selectedStatus === "" || status.includes(selectedStatus.toLowerCase())) {
                row.style.display = "";
            } else {
                row.style.display = "none";
            }
        });
    });

    // Xử lý hiển thị modal khi nhấn nút "Giao việc"
    document.querySelectorAll(".btn-action.assign").forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault();
            const eventID = this.getAttribute("data-event-id");
            const modal = document.getElementById("eventTermModal");
            const eventIDInput = document.getElementById("eventID");
            eventIDInput.value = eventID;
            modal.style.display = "flex";
        });
    });

    // Xử lý đóng modal
    document.querySelector(".close").addEventListener("click", function () {
        document.getElementById("eventTermModal").style.display = "none";
    });

    // Đóng modal khi nhấn ra ngoài
    window.addEventListener("click", function (event) {
        const modal = document.getElementById("eventTermModal");
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });
    <% if (request.getAttribute("showTermModal") != null) { %>
    window.addEventListener("load", function () {
        document.getElementById("eventTermModal").style.display = "flex";
    });
    <% } %>
</script>
</body>

</html>
