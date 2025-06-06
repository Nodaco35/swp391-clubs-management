<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<section class="section">
    <div class="container">
        <div class="section-header">
            <h2 class="section-title">Sự Kiện Sắp Diễn Ra</h2>
            <p class="section-description">Đừng bỏ lỡ những sự kiện thú vị sắp tới</p>
        </div>
        
        <div class="events-grid">
            <c:forEach items="${upcomingEvents}" var="event">
                <c:set var="event" value="${event}" scope="request"/>
                <jsp:include page="../components/event-card.jsp"/>
            </c:forEach>
        </div>
        
        <div class="view-all">
            <a href="${pageContext.request.contextPath}/events-page" class="btn btn-outline">Xem Tất Cả Sự Kiện</a>
        </div>
    </div>
</section>