<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="event-card">
    <div class="event-image">
        <img src="${pageContext.request.contextPath}/${event.eventImg}" alt="${event.eventName}" />
    </div>
    <div class="event-content">
        <h3 class="event-title">${event.eventName}</h3>
        <div class="event-meta">
            <div class="event-date">
                <i class="fas fa-calendar"></i>
                <c:choose>
                    <c:when test="${not empty event.schedules}">
                        <fmt:formatDate value="${event.schedules[0].eventDate}" pattern="dd/MM/yyyy" />
                    </c:when>
                    <c:otherwise>
                        Chưa có lịch trình
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="event-time">
                <i class="fas fa-clock"></i>
                <c:choose>
                    <c:when test="${not empty event.schedules}">
                        <fmt:formatDate value="${event.schedules[0].startTime}" pattern="HH:mm" /> -
                        <fmt:formatDate value="${event.schedules[0].endTime}" pattern="HH:mm" />
                    </c:when>
                    <c:otherwise>
                        Không xác định
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="event-location">
                <i class="fas fa-map-marker-alt"></i>
                <c:choose>
                    <c:when test="${not empty event.schedules}">
                        ${event.schedules[0].location.locationName}
                    </c:when>
                    <c:otherwise>
                        Không xác định
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <div class="event-footer">
            <div class="event-club">
                <div class="event-club-image">
                    <img src="${pageContext.request.contextPath}${event.clubImg != null ? event.clubImg : '/images/default-club.jpg'}"
                         alt="${event.clubName}">
                </div>
                <span class="event-club-name">${event.clubName}</span>
            </div>
            <a href="${pageContext.request.contextPath}/event-detail?id=${event.eventID}" class="btn btn-primary">Chi Tiết</a>
        </div>
    </div>
</div>