<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${club.clubName} - Chi Tiết Câu Lạc Bộ - UniClub</title>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/club-detail.css">
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
                        <img src="${pageContext.request.contextPath}/${displayClub.clubImg != null && not empty displayClub.clubImg ? displayClub.clubImg : 'images/default-club.jpg'}?t=<%= System.currentTimeMillis() %>" 
                             alt="${displayClub.clubName}" class="club-image">

                        <div class="club-info">
                            <h1 class="club-title">${displayClub.clubName}</h1>
                            <p class="club-category">${displayClub.category}</p>
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
                                <c:if test="${isPresident}">
                                    <a href="${pageContext.request.contextPath}/create-club?action=editClub&id=${displayClub.clubID}"
                                       class="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition">Chỉnh sửa</a>
                                </c:if>
                                <c:if test="${isPresident}">
                                    <a href="${pageContext.request.contextPath}/club-members?clubID=${displayClub.clubID}" 
                                       class="btn btn-primary left-btn">
                                        <i class="fas fa-users"></i> Quản lý thành viên
                                    </a>
                                </c:if>
                                
                                <c:if test="${isDepartmentLeader}">
                                    <a href="${pageContext.request.contextPath}/department-dashboard" 
                                       class="btn btn-success left-btn">
                                        <i class="fas fa-users-gear"></i> Quản lý Ban
                                    </a>
                                </c:if>

                                <c:if test="${!isMember && displayClub.isRecruiting && sessionScope.user != null}">
                                    <a href="${pageContext.request.contextPath}/club-apply?clubID=${displayClub.clubID}" 
                                       class="btn btn-primary right-btn">
                                        <i class="fas fa-user-plus"></i> Tham gia câu lạc bộ
                                    </a>
                                </c:if>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </section>

        <jsp:include page="../components/footer.jsp" />
        <script src="${pageContext.request.contextPath}/js/scripts.js"></script>
    </body>
</html>