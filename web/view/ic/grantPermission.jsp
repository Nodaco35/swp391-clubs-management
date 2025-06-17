<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Phân quyền người dùng - UniClub Admin</title>

        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">

        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <!-- CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    </head>
    <body>
        <div class="dashboard">
            <jsp:include page="components/ic-sidebar.jsp" />

            <main class="main-content">
                <!-- Header giống Admin Dashboard -->
                <div class="header">
                    <h1 class="page-title">Phân quyền người dùng</h1>
                    <div class="user-profile">
                        <div class="user-avatar">
                            ${fn:substring(sessionScope.user.fullName, 0, 1)}
                        </div>
                        <div class="user-info">
                            <div class="user-name">${sessionScope.user.fullName}</div>
                            <div class="user-role">Quản trị viên</div>
                        </div>
                    </div>
                </div>

                <!-- Card chứa Search + Button + Table -->
                <div class="card">
                    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px;">
                        <h2 class="card-title">Quản lý phân quyền người dùng</h2>

                        <div style="display: flex; align-items: center; gap: 10px; flex-wrap: wrap;">
                            <!-- Form search -->
                            <form action="ic" method="get" class="search-form" style="display: flex; align-items: center; gap: 10px;">
                                <input type="hidden" name="action" value="findUserById">
                                <input type="text" name="userSearchID" value="${requestScope.userSearchID}" class="search-input" placeholder="Nhập ID người dùng...">
                                <button class="btn btn-primary" type="submit">
                                    <i class="fas fa-search"></i> Tìm người dùng
                                </button>
                            </form>


                        </div>
                    </div>

                    <div class="card-body">
                        <c:if test="${not empty successMessage}">
                            <div class="alert alert-success" id="successMessage" style="margin-bottom: 15px;">
                                <i class="fas fa-check-circle"></i> ${successMessage}
                            </div>
                            <script>
                                // Tự ẩn thông báo sau 10s
                                setTimeout(function () {
                                    var msg = document.getElementById('successMessage');
                                    if (msg) {
                                        msg.style.display = 'none';
                                    }
                                }, 10000); // 10 giây = 10000 ms
                            </script>
                        </c:if>

                        <!-- Nếu không tìm thấy user -->
                        <c:if test="${userFind == null && not empty userSearchID}">
                            <div class="alert alert-warning">
                                Không tìm thấy người dùng với ID "${userSearchID}".
                            </div>
                        </c:if>

                        <!-- Nếu tìm thấy user -->
                        <c:if test="${userFind != null}">
                            <div class="table-container">
                                <table id="usersTable">
                                    <thead>
                                        <tr>
                                            <th>Người dùng</th>
                                            <th>MSSV</th>

                                            <th>Vai trò</th>
                                            <th>Ngày sinh</th>
                                                <c:if test="${userFind.permissionID == 1}">
                                                <th>Số lượng được cấp</th>
                                                </c:if>
                                                <c:if test="${userFind.permissionID == 1 && activePermissionCount!=0}">
                                                <th>Thao tác</th>
                                                </c:if>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr data-role="${userFind.permissionID == 2 ? 'admin' : (userFind.permissionID == 3 ? 'ic' : 'user')}">
                                            <td>
                                                <div class="user-info">
                                                    <div class="user-avatar">
                                                        <c:if test="${not empty userFind.avatar}">
                                                            <img src="${userFind.avatar}" alt="Avatar" style="width: 40px; height: 40px; border-radius: 50%;" />
                                                        </c:if>
                                                        <c:if test="${empty userFind.avatar}">
                                                            ${fn:substring(userFind.fullName, 0, 1)}
                                                        </c:if>
                                                    </div>
                                                    <div class="user-details">
                                                        <div class="user-name">${userFind.fullName}</div>
                                                        <div class="user-email">${userFind.email}</div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>${userFind.userID}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${userFind.permissionID == 2}">
                                                        <span class="badge badge-admin"><i class="fas fa-user-shield"></i> Admin</span>
                                                    </c:when>
                                                    <c:when test="${userFind.permissionID == 3}">
                                                        <span class="badge badge-ic"><i class="fas fa-user-tie"></i> IC Officer</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-user"><i class="fas fa-user"></i>Sinh viên</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${userFind.dateOfBirth}" pattern="dd/MM/yyyy" />
                                            </td>
                                            <c:if test="${userFind.permissionID == 1}">
                                                <td>

                                                    <c:choose>
                                                        <c:when test="${activePermissionCount == 0}">
                                                            <span class="badge badge-falure">
                                                                <i class="fas fa-info-circle"></i> Chưa có quyền tạo câu lạc bộ
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge badge-success">
                                                                <i class="fas fa-check-circle"></i> Đã có ${activePermissionCount} quyền tạo câu lạc bộ
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>

                                                </td>
                                            </c:if>
                                            <c:if test="${userFind.permissionID == 1 && activePermissionCount!=0}">
                                                <th>
                                                    <a href="${pageContext.request.contextPath}/ic?action=DeleteByUserId&id=${userFind.userID}" 
                                                       class="btn btn-error" 
                                                       onclick="return confirm('Bạn có chắc chắn muốn huỷ quyền tạo CLB của người dùng này không?');">
                                                        <i class="fas fa-trash-alt"></i> Huỷ quyền
                                                    </a>

                                                </th>
                                            </c:if>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </c:if>
                    </div>
                    <!-- Form Thêm quyền (nếu có user hợp lệ) -->
                    <div style="display: flex; align-items: center; gap: 10px; margin-left: 10px;">
                        <c:if test="${userFind != null}">
                            <c:if test="${userFind.permissionID == 1}">
                                <button class="btn btn-primary" type="button"
                                        onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=grantPermisstionForUser&id=${userFind.userID}'"
                                        >
                                    <i class="fa-solid fa-check"></i> Thêm quyền tạo câu lạc bộ
                                </button>
                            </c:if>
                        </c:if>
                    </div>
                </div>
            </main>
        </div>
    </body>

    <style>

        .search-box {
            flex: 1;
            min-width: 300px;
            position: relative;
        }

        .search-input {
            flex: 1;
            padding: 0.75rem 1rem 0.75rem 2.5rem;
            border: 1px solid var(--border);
            border-radius: var(--radius);
            font-size: 0.875rem;
            transition: box-shadow 0.2s, border-color 0.2s;
            min-width: 200px;
        }


        .search-input:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 2px rgba(14, 165, 233, 0.2);
        }
        
        .card{
            padding-bottom: 20px;
            margin-top: 20px;
        }

    </style>
</html>
