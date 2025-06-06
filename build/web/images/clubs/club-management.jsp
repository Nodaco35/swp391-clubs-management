<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Danh Sách Câu Lạc Bộ</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body>
        <jsp:include page="../components/header.jsp" />
        <section class="section" style="background-color: #f5f5f5;">
            <div class="container">
                <div class="section-header">
                    <h2 class="section-title">Danh Sách Câu Lạc Bộ</h2>
                    <p class="section-description">Khám phá tất cả các câu lạc bộ</p>
                </div>

                <div class="filter-options">
                    <a href="${pageContext.request.contextPath}/clubs?category=all" class="filter-option ${selectedCategory == 'all' ? 'active' : ''}" data-category="all">Tất Cả</a>
                    <a href="${pageContext.request.contextPath}/clubs?category=Thể Thao" class="filter-option ${selectedCategory == 'Thể Thao' ? 'active' : ''}" data-category="Thể Thao">Thể Thao</a>
                    <a href="${pageContext.request.contextPath}/clubs?category=Học Thuật" class="filter-option ${selectedCategory == 'Học Thuật' ? 'active' : ''}" data-category="Học Thuật">Học Thuật</a>
                    <a href="${pageContext.request.contextPath}/clubs?category=Phong Trào" class="filter-option ${selectedCategory == 'Phong Trào' ? 'active' : ''}" data-category="Phong Trào">Phong Trào</a>
                </div>

                <div class="clubs-grid">
                    <c:forEach items="${clubs}" var="club">
                        <c:set var="club" value="${club}" scope="request"/>
                        <jsp:include page="../components/club-card.jsp"/>
                    </c:forEach>
                </div>
            </div>
        </section>

        <jsp:include page="../components/footer.jsp" />
    </body>
</html>