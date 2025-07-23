<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<% 
            int clubID = ((models.Clubs) request.getAttribute("club")).getClubID();
            models.Clubs sessionClub = (models.Clubs) session.getAttribute("currentClub_" + clubID);
            models.Clubs displayClub = sessionClub != null ? sessionClub : (models.Clubs) request.getAttribute("club");
            request.setAttribute("displayClub", displayClub);
%>

<div class="club-card" data-category="${displayClub.categoryID}">
    <div class="club-image">
        <img src="${pageContext.request.contextPath}/${displayClub.clubImg != null && not empty displayClub.clubImg ? displayClub.clubImg : 'images/default-club.jpg'}?t=<%= System.currentTimeMillis() %>" 
             alt="${displayClub.clubName}">
        <div class="club-badge">
            <c:if test="${displayClub.isRecruiting}">
                <span class="badge badge-primary">Đang Tuyển Thành Viên</span>
            </c:if>
            <c:if test="${!displayClub.isRecruiting}">
                <span class="badge badge-secondary">Không Tuyển Thành Viên</span>
            </c:if>
        </div>
    </div>
    <div class="club-header">
        <h3 class="club-title">${displayClub.clubName}</h3>
        <p class="club-category">${displayClub.categoryID == 1 ? "Học thuật" : displayClub.categoryID == 2 ? "Phong trào" : displayClub.categoryID == 3 ? "Thể thao" : ""}</p>
    </div>
    <div class="club-content">
        <p class="club-description">${displayClub.description}</p>
    </div>
    <div class="club-footer">
        <div class="club-members">
            <i class="fas fa-users"></i>
            <span>${displayClub.memberCount} thành viên</span>
        </div>
        <div class="club-actions">
            <a href="${pageContext.request.contextPath}/club-detail?id=${displayClub.clubID}" class="btn btn-primary">Chi Tiết</a>
            <c:if test="${not empty sessionScope.user}">
                <c:choose>
                    <c:when test="${displayClub.favorite}">
                        <a href="${pageContext.request.contextPath}/clubs?action=removeFavorite&clubID=${displayClub.clubID}&category=${selectedCategory}" 
                           class="btn btn-secondary" title="Bỏ yêu thích">
                            <i class="fas fa-heart"></i>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/clubs?action=addFavorite&clubID=${displayClub.clubID}&category=${selectedCategory}" 
                           class="btn btn-secondary" title="Thêm vào yêu thích">
                            <i class="far fa-heart"></i>
                        </a>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </div>
    </div>
</div>