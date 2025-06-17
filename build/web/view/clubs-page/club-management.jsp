<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Danh Sách Câu Lạc Bộ</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
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

        /* Floating Create Club Button */
        .create-club-btn {
            position: fixed;
            top: 50%;
            left: 20px;
            transform: translateY(-50%);
            background-color: #22c55e; /* bg-green-500 */
            color: white;
            width: 50px;
            height: 50px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: background-color 0.3s, transform 0.3s;
            z-index: 1000;
        }

        .create-club-btn:hover {
            background-color: #16a34a; /* bg-green-600 */
            transform: translateY(-50%) scale(1.1);
        }

        /* Tooltip */
        .create-club-btn .tooltip {
            visibility: hidden;
            background-color: #1f2937; /* bg-gray-800 */
            color: white;
            text-align: center;
            border-radius: 4px;
            padding: 8px 12px;
            position: absolute;
            left: 60px;
            white-space: nowrap;
            opacity: 0;
            transition: opacity 0.3s, visibility 0.3s;
            z-index: 1000;
        }

        .create-club-btn:hover .tooltip {
            visibility: visible;
            opacity: 1;
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

                <div class="filter-options flex justify-start items-center mb-4">
                    <a href="${pageContext.request.contextPath}/clubs?category=all" class="filter-option ${selectedCategory == 'all' ? 'active' : ''}" data-category="all">Tất Cả</a>
                    <a href="${pageContext.request.contextPath}/clubs?category=Thể Thao" class="filter-option ${selectedCategory == 'Thể Thao' ? 'active' : ''}" data-category="Thể Thao">Thể Thao</a>
                    <a href="${pageContext.request.contextPath}/clubs?category=Học Thuật" class="filter-option ${selectedCategory == 'Học Thuật' ? 'active' : ''}" data-category="Học Thuật">Học Thuật</a>
                    <a href="${pageContext.request.contextPath}/clubs?category=Phong Trào" class="filter-option ${selectedCategory == 'Phong Trào' ? 'active' : ''}" data-category="Phong Trào">Phong Trào</a>
                    <c:if test="${not empty sessionScope.user}">
                        <c:if test="${hasClubs}">
                            <a href="${pageContext.request.contextPath}/clubs?category=myClubs" class="filter-option ${selectedCategory == 'myClubs' ? 'active' : ''}" data-category="myClubs">Club của tôi</a>
                        </c:if>
                        <c:if test="${hasFavoriteClubs}">
                            <a href="${pageContext.request.contextPath}/clubs?category=favoriteClubs" class="filter-option ${selectedCategory == 'favoriteClubs' ? 'active' : ''}" data-category="favoriteClubs">Club yêu thích của tôi</a>
                        </c:if>
                    </c:if>
                </div>

<!--                <div class="clubs-grid">
                    <c:forEach items="${clubs}" var="club">
                        <c:set var="club" value="${club}" scope="request"/>
                        <jsp:include page="../components/club-card.jsp"/>
                    </c:forEach>
                </div>-->

                <div class="clubs-grid">
                    <c:forEach items="${clubs}" var="club">
                        <% 
                            // Lấy clubID từ club
                            int clubID = ((models.Clubs) pageContext.getAttribute("club")).getClubID();
                            // Lấy câu lạc bộ từ session nếu có
                            models.Clubs sessionClub = (models.Clubs) session.getAttribute("currentClub_" + clubID);
                            // Nếu không có trong session, sử dụng club từ loop
                            models.Clubs displayClub = sessionClub != null ? sessionClub : (models.Clubs) pageContext.getAttribute("club");
                            // Đặt displayClub vào request để sử dụng trong club-card.jsp
                            request.setAttribute("club", displayClub);
                        %>
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

        <!-- Floating Create Club Button -->
        <c:if test="${hasPermission}">
            <a href="${pageContext.request.contextPath}/create-club" class="create-club-btn">
                <i class="fa-solid fa-pencil-alt"></i>
                <span class="tooltip">Tạo câu lạc bộ mới</span>
            </a>
        </c:if>

        <jsp:include page="../components/footer.jsp" />
    </body>
</html>