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

        .pagination {
            display: flex;
            justify-content: center;
            margin-top: 20px;
        }

        .pagination a {
            margin: 0 5px;
            padding: 8px 12px;
            text-decoration: none;
            color: #333;
            border: 1px solid #ddd;
            border-radius: 4px;
        }

        .pagination a.active {
            background-color: #007bff;
            color: white;
            border-color: #007bff;
        }

        .pagination a:hover:not(.disabled) {
            background-color: #f0f0f0;
        }

        .pagination a.disabled {
            color: #ccc;
            cursor: not-allowed;
            border-color: #ccc;
        }
    </style>
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
                    <c:if test="${not empty sessionScope.user}">
                        <a href="${pageContext.request.contextPath}/clubs?category=myClubs" class="filter-option ${selectedCategory == 'myClubs' ? 'active' : ''}" data-category="myClubs">Club của tôi</a>
                    </c:if>
                </div>

                <div class="clubs-grid">
                    <c:forEach items="${clubs}" var="club">
                        <c:set var="club" value="${club}" scope="request"/>
                        <jsp:include page="../components/club-card.jsp"/>
                    </c:forEach>
                </div>

                <!-- Pagination Controls -->
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/clubs?category=${selectedCategory}&page=${currentPage - 1}">« Trước</a>
                    </c:if>
                    <c:if test="${currentPage <= 1}">
                        <a class="disabled">« Trước</a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="${pageContext.request.contextPath}/clubs?category=${selectedCategory}&page=${i}" class="${currentPage == i ? 'active' : ''}">${i}</a>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <a href="${pageContext.request.contextPath}/clubs?category=${selectedCategory}&page=${currentPage + 1}">Tiếp »</a>
                    </c:if>
                    <c:if test="${currentPage >= totalPages}">
                        <a class="disabled">Tiếp »</a>
                    </c:if>
                </div>
            </div>
        </section>

        <jsp:include page="../components/footer.jsp" />
    </body>
</html>