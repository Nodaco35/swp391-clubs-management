    <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="event-card">
    <div class="event-image">
        <img src="${pageContext.request.contextPath}/images/events/${event.eventID}.jpg" 
             alt="${event.eventName}" 
             onerror="this.src='${pageContext.request.contextPath}/images/default-event.jpg'">
    </div>
    <div class="event-content">
        <h3 class="event-title">${event.eventName}</h3>
        <div class="event-meta">
            <div class="event-date">
                <i class="fas fa-calendar"></i>
                <fmt:formatDate value="${event.eventDate}" pattern="dd/MM/yyyy" />
            </div>
            <div class="event-time">
                <i class="fas fa-clock"></i>
                <fmt:formatDate value="${event.eventDate}" pattern="HH:mm" />
            </div>
            <div class="event-location">
                <i class="fas fa-map-marker-alt"></i>
                ${event.location}
            </div>
        </div>
        <p class="event-description">${event.description}</p>
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