<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<section class="section" style="background-color: #f5f5f5;">
    <div class="container">
        <div class="section-header">
            <h2 class="section-title">Câu Lạc Bộ Nổi Bật</h2>
            <p class="section-description">Khám phá các câu lạc bộ đang tuyển thành viên mới</p>
        </div>
        
        <div class="filter-options">
            <a href="#" class="filter-option active" data-category="all">Tất Cả</a>
            <a href="#" class="filter-option" data-category="Thể Thao">Thể Thao</a>
            <a href="#" class="filter-option" data-category="Học Thuật">Học Thuật</a>
            <a href="#" class="filter-option" data-category="Phong Trào">Phong Trào</a>
        </div>
        
        <div class="clubs-grid">
            <c:forEach items="${featuredClubs}" var="club">
                <c:set var="club" value="${club}" scope="request"/>
                <jsp:include page="../components/club-card.jsp"/>
            </c:forEach>
        </div>
        
        <div class="view-all">
            <a href="${pageContext.request.contextPath}/clubs" class="btn btn-outline">Xem Tất Cả Câu Lạc Bộ</a>
        </div>
    </div>
</section>