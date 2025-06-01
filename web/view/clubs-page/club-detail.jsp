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
</head>
<style>
    html, body {
    height: 100%;
    margin: 0;
    padding: 0;
    display: flex;
    flex-direction: column;
}

main {
    flex: 1;
    background-color: #f5f5f5;
    padding: 10px;
}

footer {
    margin-top: auto;
}
footer {
    padding: 0;
    background-color: #1d1d1d;
    color: white;
    text-align: center;
}
</style>
<body>
    <jsp:include page="../components/header.jsp" />

    <section class="section">
        <div class="container">
            <div class="club-detail">
                <!-- Thông tin câu lạc bộ -->
                <div class="club-header">
                    <img src="${pageContext.request.contextPath}${club.clubImg != null ? club.clubImg : '/images/default-club.jpg'}" 
                         alt="${club.clubName}" class="club-image">
                    <div class="club-info">
                        <h1 class="club-title">${club.clubName}</h1>
                        <p class="club-category">${club.category}</p>
                        <p class="club-description">${club.description}</p>
                        <div class="club-meta">
                            <p><i class="fas fa-users"></i> ${club.memberCount} thành viên</p>
                            <p><i class="fas fa-calendar-alt"></i> Thành lập: 
                                <fmt:formatDate value="${club.establishedDate}" pattern="dd/MM/yyyy" />
                            </p>
                            <p><i class="fas fa-envelope"></i> ${club.contactGmail}</p>
                            <c:if test="${club.contactPhone != null}">
                                <p><i class="fas fa-phone"></i> ${club.contactPhone}</p>
                            </c:if>
                            <c:if test="${club.contactURL != null}">
                                <p><i class="fas fa-link"></i> <a href="${club.contactURL}" target="_blank">Website</a></p>
                            </c:if>
                            <p>
                                <i class="fas fa-bullhorn"></i> 
                                <c:choose>
                                    <c:when test="${club.isRecruiting}">
                                        <span class="badge badge-primary">Đang tuyển thành viên</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-secondary">Đã đủ thành viên</span>
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </div>
                    </div>
                </div>

                <!-- Nút quản lý thành viên (chỉ hiển thị nếu là chủ nhiệm) -->
                <c:if test="${isPresident}">
                    <div class="club-actions">
                        <a href="${pageContext.request.contextPath}/club-members?clubID=${club.clubID}" 
                           class="btn btn-primary">
                            <i class="fas fa-users"></i> Quản lý thành viên
                        </a>
                    </div>
                </c:if>

                <!-- Nút tham gia (nếu không phải thành viên và đang tuyển) -->
                <c:if test="${!isMember && club.isRecruiting && sessionScope.user != null}">
                    <div class="join-club">
                        <a href="${pageContext.request.contextPath}/club-apply?clubID=${club.clubID}" 
                           class="btn btn-primary btn-lg">
                            <i class="fas fa-user-plus"></i> Tham gia câu lạc bộ
                        </a>
                    </div>
                </c:if>
            </div>
        </div>
    </section>

    <jsp:include page="../components/footer.jsp" />
    <script src="${pageContext.request.contextPath}/js/scripts.js"></script>
</body>
</html>