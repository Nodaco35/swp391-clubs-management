<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Danh Sách Câu Lạc Bộ</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/club-management.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    
    <body>
        <jsp:include page="../components/header.jsp" />
        <section class="section" style="background-color: #f5f5f5;">
            <div class="container">
                <div class="section-header">
                    <h2 class="section-title">Danh Sách Câu Lạc Bộ</h2>
                    <p class="section-description">Khám phá tất cả các câu lạc bộ</p>
                </div>

                <!-- Display Messages -->
                <c:if test="${not empty sessionScope.message}">
                    <div class="message success">${sessionScope.message}</div>
                    <c:remove var="message" scope="session"/>
                    <script>
                        setTimeout(() => {
                            document.querySelector('.message.success').style.display = 'none';
                        }, 5000);
                    </script>
                </c:if>
                <c:if test="${not empty sessionScope.error}">
                    <div class="message error">${sessionScope.error}</div>
                    <c:remove var="error" scope="session"/>
                    <script>
                        setTimeout(() => {
                            document.querySelector('.message.error').style.display = 'none';
                        }, 5000);
                    </script>
                </c:if>

                <div class="filter-options flex justify-start items-center mb-4">
                    <a href="${pageContext.request.contextPath}/clubs?category=0" class="filter-option ${selectedCategory == 'all' ? 'active' : ''}" data-category="all">Tất Cả</a>
                    <c:forEach items="${categories}" var="category">
                        <a href="${pageContext.request.contextPath}/clubs?category=${category.categoryID}" class="filter-option ${selectedCategoryID == category.categoryID ? 'active' : ''}" data-category-id="${category.categoryID}">${category.categoryName}</a>
                    </c:forEach>
                    <c:if test="${not empty sessionScope.user}">
                        <c:if test="${hasClubs}">
                            <a href="${pageContext.request.contextPath}/clubs?category=-1" class="filter-option ${selectedCategory == 'myClubs' ? 'active' : ''}" data-category-id="-1">Câu lạc bộ của tôi</a>
                        </c:if>
                        <c:if test="${hasFavoriteClubs}">
                            <a href="${pageContext.request.contextPath}/clubs?category=-2" class="filter-option ${selectedCategory == 'favoriteClubs' ? 'active' : ''}" data-category-id="-2">Câu lạc bộ yêu thích của tôi</a>
                        </c:if>
                    </c:if>
                </div>

                <div class="clubs-grid">
                    <c:forEach items="${clubs}" var="club">
                        <% 
                            int clubID = ((models.Clubs) pageContext.getAttribute("club")).getClubID();
                            models.Clubs sessionClub = (models.Clubs) session.getAttribute("currentClub_" + clubID);
                            models.Clubs displayClub = sessionClub != null ? sessionClub : (models.Clubs) pageContext.getAttribute("club");
                            request.setAttribute("club", displayClub);
                        %>
                        <jsp:include page="../components/club-card.jsp"/>
                    </c:forEach>
                </div>

                <!-- Pagination Controls -->
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/clubs?category=${selectedCategoryID}&page=${currentPage - 1}">« Trước</a>
                    </c:if>
                    <c:if test="${currentPage <= 1}">
                        <a class="disabled">« Trước</a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="${pageContext.request.contextPath}/clubs?category=${selectedCategoryID}&page=${i}" class="${currentPage == i ? 'active' : ''}">${i}</a>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <a href="${pageContext.request.contextPath}/clubs?category=${selectedCategoryID}&page=${currentPage + 1}">Tiếp »</a>
                    </c:if>
                    <c:if test="${currentPage >= totalPages}">
                        <a class="disabled">Tiếp »</a>
                    </c:if>
                </div>
            </div>
        </section>

        <!-- Floating Create Club Button -->
        <c:if test="${not empty sessionScope.user}">
            <a href="${pageContext.request.contextPath}/create-club" class="create-club-btn">
                <i class="fa-solid fa-pencil-alt"></i>
                <span class="tooltip">Tạo câu lạc bộ mới</span>
            </a>
        </c:if>

        

        <jsp:include page="../components/footer.jsp" />
    </body>
</html>