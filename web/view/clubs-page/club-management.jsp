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
                            <a href="${pageContext.request.contextPath}/clubs?category=-1" class="filter-option ${selectedCategory == 'myClubs' ? 'active' : ''}" data-category-id="-1">Club của tôi</a>
                        </c:if>
                        <c:if test="${hasFavoriteClubs}">
                            <a href="${pageContext.request.contextPath}/clubs?category=-2" class="filter-option ${selectedCategory == 'favoriteClubs' ? 'active' : ''}" data-category-id="-2">Club yêu thích của tôi</a>
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
        <c:if test="${hasPermission}">
            <a href="${pageContext.request.contextPath}/create-club" class="create-club-btn">
                <i class="fa-solid fa-pencil-alt"></i>
                <span class="tooltip">Tạo câu lạc bộ mới</span>
            </a>
        </c:if>

        <!-- Floating Request Permission Button -->
        <c:if test="${not empty sessionScope.user && !hasPermission && !hasPendingRequest}">
            <button class="request-permission-btn" onclick="showPermissionForm()">
                <i class="fa-solid fa-plus"></i>
                <span class="tooltip">Gửi đơn xin quyền tạo câu lạc bộ</span>
            </button>
        </c:if>

        <!-- Overlay -->
        <div class="overlay" onclick="hidePermissionForm()"></div>

        <!-- Permission Request Popup Form -->
        <div class="permission-request-popup" id="permissionRequestPopup">
            <h2>Gửi đơn xin quyền tạo câu lạc bộ</h2>
            <p>Điền thông tin câu lạc bộ bạn muốn tạo. Đơn sẽ được IC xem xét và phản hồi sớm.</p>
            <form id="permissionRequestForm" action="${pageContext.request.contextPath}/student?action=sendPermissionRequest" method="POST">
                <input type="hidden" name="userID" value="${sessionScope.user.userID}">
                <div class="mb-4">
                    <label for="clubName" class="block text-sm font-medium text-gray-700">Tên Câu Lạc Bộ: <span class="text-red-500">*</span></label>
                    <input type="text" name="clubName" id="clubName" required
                           class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                           placeholder="Nhập tên câu lạc bộ">
                </div>
                <div class="mb-4">
                    <label for="category" class="block text-sm font-medium text-gray-700">Danh Mục: <span class="text-red-500">*</span></label>
                    <select name="category" id="category" required
                            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                        <option value="" disabled selected>Chọn danh mục</option>
                        <c:forEach items="${categories}" var="category">
                            <option value="${category.categoryID}">${category.categoryName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="buttons">
                    <button type="button" class="btn cancel" onclick="hidePermissionForm()">Hủy</button>
                    <button type="submit" class="btn submit" id="submitRequestBtn">Gửi đơn</button>
                </div>
            </form>
        </div>

        <script>
            function showPermissionForm() {
                document.getElementById('permissionRequestPopup').style.display = 'block';
                document.querySelector('.overlay').style.display = 'block';
            }

            function hidePermissionForm() {
                document.getElementById('permissionRequestPopup').style.display = 'none';
                document.querySelector('.overlay').style.display = 'none';
            }

            document.getElementById('permissionRequestForm').addEventListener('submit', function (e) {
                const clubName = document.getElementById('clubName').value.trim();
                const category = document.getElementById('category').value;
                if (!clubName || !category) {
                    e.preventDefault();
                    alert('Vui lòng nhập đầy đủ tên câu lạc bộ và chọn danh mục.');
                }
            });
        </script>

        <jsp:include page="../components/footer.jsp" />
    </body>
</html>