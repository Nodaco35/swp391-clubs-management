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
                        <img src="${pageContext.request.contextPath}${club.clubImg != null ? club.clubImg : '/images/default-club.jpg'}" 
                             alt="${club.clubName}" class="club-image">

                        <div class="club-info">
                            <h1 class="club-title">${club.clubName}</h1>
                            <p class="club-category">${club.category}</p>
                            <p class="club-description">${club.description}</p>


                            <div class="club-meta-grid">
                                <div><i class="fas fa-users"></i> ${club.memberCount} thành viên</div>
                                <div><i class="fas fa-calendar-alt"></i> Thành lập: 
                                    <fmt:formatDate value="${club.establishedDate}" pattern="dd/MM/yyyy" />
                                </div>

                                <div><i class="fas fa-envelope"></i> ${club.contactGmail}</div>
                                <c:if test="${club.contactPhone != null}">
                                    <div><i class="fas fa-phone"></i> ${club.contactPhone}</div>
                                </c:if>

                                <c:if test="${club.contactURL != null}">
                                    <div><i class="fas fa-link"></i> 
                                        <a href="${club.contactURL}" target="_blank">Website</a>
                                    </div>
                                </c:if>
                                <div>
                                    <i class="fas fa-bullhorn"></i> 
                                    <c:choose>
                                        <c:when test="${club.isRecruiting}">
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
                                    <a href="${pageContext.request.contextPath}/club-members?clubID=${club.clubID}" 
                                       class="btn btn-primary left-btn">
                                        <i class="fas fa-users"></i> Quản lý thành viên
                                    </a>
                                </c:if>

                                <c:if test="${!isMember && club.isRecruiting && sessionScope.user != null}">
                                    <a href="${pageContext.request.contextPath}/club-apply?clubID=${club.clubID}" 
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