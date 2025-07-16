<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="contextPath" content="${pageContext.request.contextPath}">
        <title>${club.clubName} - Chi Tiết Câu Lạc Bộ - UniClub</title>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/club-detail.css?v=<%= System.currentTimeMillis() %>">
    </head>
    <body>
        <jsp:include page="../components/header.jsp" />

        <section class="section">
            <div class="containerr">
                <div class="club-detail">
                    <!-- Thông tin câu lạc bộ -->
                    <div class="club-header">
                        <%
                            // Lấy clubID từ request
                            String clubID = request.getParameter("id");
                            // Lấy câu lạc bộ từ session nếu có
                            models.Clubs sessionClub = (models.Clubs) session.getAttribute("currentClub_" + clubID);
                            // Nếu không có trong session, sử dụng club từ request
                            models.Clubs displayClub = sessionClub != null ? sessionClub : (models.Clubs) request.getAttribute("club");
                            // Đặt displayClub vào request để sử dụng trong JSP
                            request.setAttribute("displayClub", displayClub);
                        %>
                        <img src="${pageContext.request.contextPath}/${displayClub.clubImg != null && not empty displayClub.clubImg ? displayClub.clubImg : 'images/default-club.jpg'}?t=<%= System.currentTimeMillis()%>" 
                             alt="${displayClub.clubName}" class="club-image">

                        <div class="club-info">
                            <h1 class="club-title">${displayClub.clubName}</h1>
                            <p class="club-category">${displayClub.categoryName}</p>
                            <p class="club-description">${displayClub.description}</p>

                            <div class="club-meta-grid">
                                <div><i class="fas fa-users"></i> ${displayClub.memberCount} thành viên</div>
                                <div><i class="fas fa-calendar-alt"></i> Thành lập: 
                                    <fmt:formatDate value="${displayClub.establishedDate}" pattern="dd/MM/yyyy" />
                                </div>
                                <div><i class="fas fa-envelope"></i> ${displayClub.contactGmail}</div>
                                <c:if test="${displayClub.contactPhone != null}">
                                    <div><i class="fas fa-phone"></i> ${displayClub.contactPhone}</div>
                                </c:if>
                                <c:if test="${displayClub.contactURL != null}">
                                    <div><i class="fas fa-link"></i> 
                                        <a href="${displayClub.contactURL}" target="_blank">Website</a>
                                    </div>
                                </c:if>
                                <div>
                                    <i class="fas fa-bullhorn"></i> 
                                    <c:choose>
                                        <c:when test="${displayClub.isRecruiting}">
                                            <span class="badge badge-primary">Đang tuyển thành viên</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-secondary">Đã đủ thành viên</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>                            
                            <div class="club-buttons-row">
                                <div class="left-buttons">
                                    <c:if test="${isPresident}">
                                        <a href="${pageContext.request.contextPath}/create-club?action=editClub&id=${displayClub.clubID}"
                                           class="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition btn-edit">
                                            <i class="fas fa-edit me-2"></i>Chỉnh sửa
                                        </a>
                                    </c:if>
                                    <c:if test="${isPresident}">
                                        <a href="${pageContext.request.contextPath}/club-members?clubID=${displayClub.clubID}" 
                                           class="btn btn-primary">
                                            <i class="fas fa-users"></i> Quản lý thành viên
                                        </a>
                                    </c:if>
                                    <c:if test="${isDepartmentLeader}">
                                        <a href="${pageContext.request.contextPath}/department-dashboard?clubID=${displayClub.clubID}" 
                                           class="btn btn-primary">
                                            <i class="fas fa-users-gear"></i> Quản lý Ban
                                        </a>
                                    </c:if>
                                </div>

                                <c:if test="${!isMember && sessionScope.user != null}">
                                    <a href="#" id="joinClubButton"
                                       class="btn btn-primary right-btn">
                                        <i class="fas fa-user-plus"></i> Tham gia câu lạc bộ
                                    </a>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Club President -->
                <%
                    dal.UserClubDAO userClubDAO = new dal.UserClubDAO();
                    int clubIdInt = Integer.parseInt(clubID);
                    models.Users presidentUser = userClubDAO.getClubLeader(clubIdInt);
                    request.setAttribute("presidentUser", presidentUser);
                %>
                <h2 class="president-title">Chủ nhiệm Câu Lạc Bộ</h2>
                <div class="president-card">
                    <c:choose>
                        <c:when test="${presidentUser != null}">
                            <!-- ✅ Avatar đưa lên trước -->
                           <!-- <img src="${pageContext.request.contextPath}/images/default-user.jpg?t=<%= System.currentTimeMillis() %>" 
                                 alt="${presidentUser.fullName}" class="president-image"> -->

                            <!-- ✅ Thông tin căn trái -->
                            <div class="president-info">
                                <p class="president-name">${presidentUser.fullName}</p>
                                <p class="president-role">Chủ nhiệm</p>
                                <div class="president-email">
                                    <i class="fas fa-envelope text-primary mr-2"></i>
                                    <span>${presidentUser.email}</span>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="no-president">Không có thông tin chủ nhiệm.</p>
                        </c:otherwise>
                    </c:choose>
                </div>


                <!-- Events Section -->
                <h2 class="text-2xl font-bold text-gray-900 mb-6">Sự Kiện Câu Lạc Bộ</h2>
                <div class="bg-white rounded-2xl shadow-lg p-8">

                    <%
                        java.util.List<models.Events> events = (java.util.List<models.Events>) request.getAttribute("clubEvents");
                        int eventsPerPage = 3;
                        int currentPage = 1;
                        try {
                            if (request.getParameter("page") != null) {
                                currentPage = Integer.parseInt(request.getParameter("page"));
                            }
                        } catch (NumberFormatException e) {
                            currentPage = 1;
                        }

                        int totalEvents = (events != null) ? events.size() : 0;
                        int totalPages = (int) Math.ceil((double) totalEvents / eventsPerPage);
                        int startIndex = (currentPage - 1) * eventsPerPage;
                        int endIndex = Math.min(startIndex + eventsPerPage, totalEvents);
                    %>

                    <c:choose>
                        <c:when test="${not empty clubEvents}">
                            <div class="events-grid grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                                <%
                                    for (int i = startIndex; i < endIndex; i++) {
                                        request.setAttribute("event", events.get(i));
                                %>
                                <jsp:include page="../components/event-card.jsp" />
                                <%
                                    }
                                %>
                            </div>

                            <div class="pagination">
                                <c:if test="<%= currentPage > 1 %>">
                                    <a href="?id=${displayClub.clubID}&page=<%= currentPage - 1 %>" class="pagination-btn">Trước</a>
                                </c:if>
                                <%
                                    for (int pageNum = 1; pageNum <= totalPages; pageNum++) {
                                        String activeClass = (pageNum == currentPage) ? "active" : "";
                                %>
                                <a href="?id=${displayClub.clubID}&page=<%= pageNum %>" class="pagination-btn <%= activeClass %>"><%= pageNum %></a>
                                <%
                                    }
                                %>
                                <c:if test="<%= currentPage < totalPages %>">
                                    <a href="?id=${displayClub.clubID}&page=<%= currentPage + 1 %>" class="pagination-btn">Sau</a>
                                </c:if>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="no-events">Hiện tại không có sự kiện nào.</p>
                        </c:otherwise>
                    </c:choose>
                </div>


        </section>

        <jsp:include page="../components/footer.jsp" />
        <script src="${pageContext.request.contextPath}/js/scripts.js"></script>
        <script src="${pageContext.request.contextPath}/js/recruitment-status-checker.js"></script>

        <!-- Đánh dấu trang là club-detail-page để script nhận biết -->
        <script>
            // Thêm class vào body để script nhận biết
            document.body.classList.add('club-detail-page');

            // Tạo data attribute để lưu clubId
            document.addEventListener('DOMContentLoaded', function () {
                const joinButton = document.getElementById('joinClubButton');
                if (joinButton) {
                    joinButton.setAttribute('data-club-id', '${displayClub.clubID}');

                    // Khởi tạo trạng thái nút khi trang tải xong
                    if (typeof initializeJoinClubButton === 'function') {
                        initializeJoinClubButton();
                    }
                }
            });
        </script>

        <style>
            /* CSS cho nút tham gia */
            .join-club-btn {
                transition: all 0.3s ease;
            }
            .join-club-btn.disabled {
                opacity: 0.65;
                cursor: not-allowed;
                pointer-events: none;
            }
            /* Animation cho khi nút cập nhật */
            @keyframes buttonUpdate {
                0% {
                    transform: scale(1);
                }
                50% {
                    transform: scale(1.05);
                }
                100% {
                    transform: scale(1);
                }
            }
            .button-updated {
                animation: buttonUpdate 0.5s ease;
            }
        </style>
    </body>
</html>