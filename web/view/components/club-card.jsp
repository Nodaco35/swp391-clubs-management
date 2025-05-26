<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="club-card" data-category="${club.category}">
    <div class="club-image">
        <img src="${pageContext.request.contextPath}${club.clubImg != null ? club.clubImg : '/images/default-club.jpg'}" 
             alt="${club.clubName}">
        <div class="club-badge">
            <c:if test="${club.isRecruiting}">
                <span class="badge badge-primary">Đang Tuyển</span>
            </c:if>
            <c:if test="${!club.isRecruiting}">
                <span class="badge badge-secondary">Đã Đủ</span>
            </c:if>
        </div>
    </div>
    <div class="club-header">
        <h3 class="club-title">${club.clubName}</h3>
        <p class="club-category">${club.category}</p>
    </div>
    <div class="club-content">
        <p class="club-description">${club.description}</p>
    </div>
    <div class="club-footer">
        <div class="club-members">
            <i class="fas fa-users"></i>
            <span>${club.memberCount} thành viên</span>
        </div>
        <a href="${pageContext.request.contextPath}/club-detail?id=${club.clubID}" class="btn btn-primary">Chi Tiết</a>
    </div>
</div>