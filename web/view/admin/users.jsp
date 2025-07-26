<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="models.Permission"%>
<%@page import="dal.PermissionDAO"%>

<%@page import="java.util.*"%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Manage Members - UniClub</title>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
        <style>
            /* Modal Styles */
            .modal {

                display: none;
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0, 0, 0, 0.5);
                z-index: 1000;
                overflow-y: auto;
            }

            .modal-content {
                background-color: #fff;
                margin: 20px auto;
                padding: 20px;
                border-radius: 8px;
                width: 90%;
                max-width: 600px;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
                position: relative;
            }

            .close {
                position: absolute;
                top: 10px;
                right: 15px;
                font-size: 1.5rem;
                cursor: pointer;
                color: #333;
            }

            .close:hover {
                color: #e74c3c;
            }

            .modal-content h2 {
                margin-bottom: 20px;
                font-size: 1.5rem;
                font-weight: 500;

            }

            .form-group {
                margin-bottom: 15px;
            }

            .form-group label {
                display: block;
                font-weight: 500;
                margin-bottom: 5px;
            }

            .form-group input,
            .form-group select {
                width: 100%;
                padding: 8px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 1rem;
            }

            .form-group input[type="file"] {
                padding: 3px;
            }

            .form-actions {
                display: flex;
                gap: 10px;
                justify-content: flex-end;
                margin-top: 20px;
            }

            /* Table Styles */
            .card-body {
                background-color: #fff;
                border-radius: 8px;
                padding: 20px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            }

            .table-header-2 {
                margin-bottom: 20px;
                justify-content: left;
                display: flex;
            }

            .table-container {
                overflow-x: auto;
            }

            table {
                margin-top: 10px;
                width: 100%;
                border-collapse: collapse;
                min-width: 800px;
            }

            thead {
                background-color: #283b48;
                color: white;
            }


            th, td {
                padding: 12px;
                text-align: left;
                border-bottom: 1px solid #ddd;
            }

            th {
                font-weight: 500;
            }

            tbody tr:hover {
                background-color: #f9f9f9;
            }

            .badge {
                padding: 5px 10px;
                border-radius: 12px;
                font-size: 0.9rem;
            }

            .badge-approved {
                background-color: #2ecc71;
                color: #fff;
            }

            .badge-rejected {
                background-color: #e74c3c;
                color: #fff;
            }

            .table-actions {
                display: flex;
                gap: 10px;
            }

            /* Button Styles */
            .btn {
                padding: 8px 12px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 1rem;
                transition: background 0.3s;
            }

            .btn-primary {
                background-color: #3498db;
                color: #fff;
            }

            .btn-primary:hover {
                background-color: #2980b9;
            }

            .btn-secondary {
                background-color: #7f8c8d;
                color: #fff;
            }

            .btn-secondary:hover {
                background-color: #6c7a89;
            }

            .btn-outline {
                background-color: transparent;
                border: 1px solid #3498db;
                color: #3498db;
            }

            .btn-outline:hover {
                background-color: #3498db;
                color: #fff;
            }

            .btn-success {
                background-color: #e74c3c;
                color: #fff;
            }

            .btn-success:hover {
                background-color: #c0392b;
            }

            .btn-icon {
                padding: 6px;
                display: flex;
                align-items: center;
                justify-content: center;
            }

            /* Pagination Styles */
            .pagination {
                display: flex;
                justify-content: center;
                gap: 10px;
                margin-top: 20px;
            }

            .pagination a {
                padding: 8px 12px;
                border: 1px solid #ddd;
                border-radius: 4px;
                text-decoration: none;
                color: #333;
            }

            .pagination a.active {
                background-color: #3498db;
                color: #fff;
                border-color: #3498db;
            }

            .pagination a:hover {
                background-color: #f0f0f0;
            }
            .table-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 10px 0;
                flex-wrap: wrap;
                gap: 10px;
            }
            .filter-form {
                display: flex;
                align-items: center;
                gap: 20px;
            }
            .filter-group {
                display: flex;
                align-items: center;
                margin: 10px;
            }
            .filter-group label {
                margin-right: 8px;
                font-weight: bold;
            }
            .filter-group select {
                padding: 5px;
                border: 1px solid #ccc;
                border-radius: 4px;
                font-size: 14px;
            }
            .filter-group input {
                padding: 5px;
                border: 1px solid #ccc;
                border-radius: 4px;
                font-size: 14px;
                margin-right: 2px;
            }
            .add-member-btn {
                padding: 8px 16px;
                background-color: #28a745;
                color: white;
                text-decoration: none;
                border-radius: 4px;
                font-size: 14px;
                transition: background-color 0.3s;
            }
            .add-member-btn:hover {
                background-color: #218838;
            }
        </style>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                let errorMessage = "<%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %>";
                if (errorMessage !== "") {
                    const modal = document.getElementById('userModal');
                    const form = document.getElementById('userForm');
                    const modalTitle = document.getElementById('modalTitle');
                    let type = "<%= request.getAttribute("type") != null ? request.getAttribute("type") : "" %>";
                    if (type === "insert") {
                        modalTitle.textContent = 'Thêm thành viên';
                        form.action = 'admin?action=insert';
                    } else if (type === "update") {
                        modalTitle.textContent = 'Chỉnh sửa thành viên';
                        form.action = 'admin?action=update';
                    }
                    document.getElementById('userID').value = "<%= request.getAttribute("formUserID") != null ? request.getAttribute("formUserID") : "" %>";
                    document.getElementById('fullName').value = "<%= request.getAttribute("formFullName") != null ? request.getAttribute("formFullName") : "" %>";
                    document.getElementById('email').value = "<%= request.getAttribute("formEmail") != null ? request.getAttribute("formEmail") : "" %>";
                    document.getElementById('password').value = "<%= request.getAttribute("formPassword") != null ? request.getAttribute("formPassword") : "" %>";
                    document.getElementById('dob').value = "<%= request.getAttribute("formDateOfBirth") != null ? request.getAttribute("formDateOfBirth") : "" %>";
                    document.getElementById('permissionID').value = "<%= request.getAttribute("formPermissionID") != null ? request.getAttribute("formPermissionID") : "" %>";
                    document.getElementById('status').value = "<%= request.getAttribute("formStatus") != null ? request.getAttribute("formStatus") : "true" %>";
                    modal.style.display = 'block';
                }
            });
        </script>
    </head>
    <div id="errorModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="this.parentElement.parentElement.style.display = 'none'">×</span>
            <h2>Lỗi</h2>
            <p id="errorMessage"></p>
            <div class="form-actions">
                <button class="btn btn-primary" onclick="document.getElementById('errorModal').style.display = 'none'">Đóng</button>
            </div>
        </div>
    </div>

    <body>


        <div class="dashboard">
            <jsp:include page="components/admin-sidebar.jsp" />
            <main class="main-content">
                <div class="header">
                    <h1 class="page-title">Quản lý thành viên</h1>
                    <div class="user-profile">
                        <div><img src="${sessionScope.user.avatar}" alt="Avatar" style="width: 60px; height: 60px; border-radius: 50%;"></div>
                        <div class="user-info">
                            <div class="user-name">${sessionScope.user.fullName}</div>
                            <div class="user-role">Quản trị viên</div>
                        </div>
                    </div>
                </div>

                <!-- Reusable Modal for Add/Edit -->
                <div id="userModal" class="modal">
                    <div class="modal-content">
                        <span class="close" onclick="closeModal('userModal')">×</span>
                        <h2 id="modalTitle">Thông tin thành viên</h2>

                        <form id="userForm" action="" method="POST">
                            <input type="hidden" id ="userID" name="userID">
                            <div class="form-group">
                                <label for="fullName">Họ và tên:</label>
                                <input type="text" id="fullName" name="fullName" required>
                            </div>
                            <div class="form-group">
                                <label for="email">Email:</label>
                                <input type="email" id="email" name="email" required>
                            </div>
                            <div class="form-group">
                                <label for="password">Mật khẩu:</label>
                                <input type="password" id="password" name="password">
                                <small id="passwordHint" style="display: none;">Để trống nếu không muốn thay đổi mật khẩu.</small>
                            </div>
                            <div class="form-group">
                                <label for="dob">Ngày sinh:</label>
                                <input type="date" id="dob" name="dateOfBirth">
                            </div>
                            <div class="form-group">
                                <label for="permissionID">Quyền:</label>
                                <select id="permissionID" name="permissionID" required>

                                    <% List<Permission> permissions = PermissionDAO.findAll(); 
                                    for(Permission permission : permissions){%>
                                    <option value="<%=permission.getPermissionID()%>"><%=permission.getPermissionName()%></option>
                                    <%}%>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="status">Trạng thái:</label>
                                <select id="status" name="status" required>
                                    <option value="true">Hoạt động</option>
                                    <option value="false">Không hoạt động</option>
                                </select>
                            </div>
                            <div class="form-group" style="display: none">
                                <label for="avatar">Avatar:</label>
                                <input type="file" id="avatar" name="avatar" accept="image/*">
                                <img id="avatarPreview" src="" alt="Avatar Preview" style="display: none; width: 50px; height: 50px; border-radius: 50%;">
                            </div>

                            <div class="form-group">

                                <%String error = (String) request.getAttribute("error");
                                if(error != null){%>
                                <div style="color: red"><%= error%></div>
                                <%}%>

                            </div>

                            <div class="form-actions">
                                <button type="submit" class="btn btn-primary">Lưu</button>
                                <button type="button" class="btn btn-secondary" onclick="closeModal('userModal')">Hủy</button>
                            </div>
                        </form>
                    </div>
                </div>

                <div class="card-body">
                    <!-- Add Member Button -->
                    <div class="table-header-2">
                        <button class="btn btn-primary" onclick="openAddUserModal()">Thêm thành viên</button>
                        <form action="admin" method="GET" class="filter-form">
                            <input type="hidden" name="action" value="filter">
                            <div class="filter-group">
                                <label for="permissionID">Quyền:</label>
                                <select name="permissionID" id="permissionID" onchange="this.form.submit()">
                                    <option value="0" <%= request.getAttribute("permissionID") != null && ((Integer)request.getAttribute("permissionID")) == 0 ? "selected" : "" %>>Tất cả</option>
                                    <% for(Permission permission : permissions){ %>
                                    <option value="<%=permission.getPermissionID()%>" <%= request.getAttribute("permissionID") != null && permission.getPermissionID() == ((Integer)request.getAttribute("permissionID")) ? "selected" : "" %>><%=permission.getPermissionName()%></option>
                                    <% } %>
                                </select>
                            </div>
                        </form>
                        <form action="admin" method="GET" class="filter-form">
                            <input type="hidden" name="action" value="search">
                            <div class="filter-group">
                                <label for="query">Tìm kiếm:</label>
                                <input type="text" name="query" id="query" value="${query != null ? query : ''}" placeholder="Tìm theo tên, email, ID">
                                <button class="btn btn-icon btn-outline"><i class="fa-solid fa-magnifying-glass"></i></button>
                            </div>
                        </form>

                    </div>
                               
                               
                    <div class="table-container">
                        <c:if test="${not empty msg}">
                            <div class="modal-content">${msg}</div>
                        </c:if>
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Họ và tên</th>
                                    <th>Email</th>
                                    <th>Mật khẩu</th>
                                    <th>Quyền</th>
                                    <th>Trạng thái</th>
                                    <th>Ngày sinh</th>
                                    <th>Avatar</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${list}" var="user">
                                    <tr>
                                        <td name="userID">${user.userID}</td>
                                        <td name="fullName">${user.fullName}</td>
                                        <td name="email">${user.email}</td>
                                        <td name="password">${user.password}</td>
                                        <td name="permissionName">${user.perName}</td>
                                        <td name="status">
                                            <span class="badge ${user.status == true ? 'badge-approved' : 'badge-rejected'}">
                                                <i class="fas ${user.status == true ? 'fa-check' : 'fa-times'}"></i>
                                                ${user.status == true ? 'Hoạt động' : 'Không hoạt động'}
                                            </span>
                                        </td>
                                        <td name="dateOfBirth">${user.dateOfBirth!= null ? user.dateOfBirth : 'Không có'}</td>
                                        <td name="avatarSrc">
                                            <img src="${user.avatar}" alt="Avatar" style="width: 30px; height: 30px; border-radius: 50%;">

                                        </td>
                                        <td>
                                            <div class="table-actions">
                                                <button class="btn btn-icon btn-outline" onclick='openEditUserModal(this)'>
                                                    <i class="fas fa-pencil"></i>
                                                </button>
                                                <form action="admin?action=deleteAccounts&id=${user.userID}" method="POST" onsubmit="return confirm('Bạn có chắc chắn muốn vô hiệu hóa thành viên này?')">
                                                    <button class="btn btn-icon btn-success" type="submit">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>

                    </div>

                </div>
                <div class="pagination">
                    <c:if test="${totalPages > 0}">
                        <!-- Trang đầu -->
                        <a href="admin?action=${param.action == 'filter' ? 'filter' : param.action == 'search' ? 'search' : 'manageAccounts'}&page=1${param.action == 'filter' ? '&permissionID=' += permissionID : ''}${param.action == 'search' ? '&query=' += query : ''}" class="${currentPage == 1 ? 'active' : ''}">1</a>

                        <!-- Dấu "..." nếu currentPage > 3 -->
                        <c:if test="${currentPage > 3}">
                            <span>...</span>
                        </c:if>

                        <!-- Các trang xung quanh currentPage -->
                        <c:forEach begin="${currentPage - 1 > 1 ? currentPage - 1 : 2}" end="${currentPage + 1 < totalPages ? currentPage + 1 : totalPages - 1}" var="i">
                            <a href="admin?action=${param.action == 'filter' ? 'filter' : param.action == 'search' ? 'search' : 'manageAccounts'}&page=${i}${param.action == 'filter' ? '&permissionID=' += permissionID : ''}${param.action == 'search' ? '&query=' += query : ''}" class="${i == currentPage ? 'active' : ''}">${i}</a>
                        </c:forEach>

                        <!-- Dấu "..." nếu currentPage < totalPages - 2 -->
                        <c:if test="${currentPage < totalPages - 2}">
                            <span>...</span>
                        </c:if>

                        <!-- Trang cuối nếu totalPages > 1 -->
                        <c:if test="${totalPages > 1}">
                            <a href="admin?action=${param.action == 'filter' ? 'filter' : param.action == 'search' ? 'search' : 'manageAccounts'}&page=${totalPages}${param.action == 'filter' ? '&permissionID=' += permissionID : ''}${param.action == 'search' ? '&query=' += query : ''}" class="${currentPage == totalPages ? 'active' : ''}">${totalPages}</a>
                        </c:if>
                    </c:if>
                </div>

            </main>
        </div>

        <script>

            function openAddUserModal() {
                const modal = document.getElementById('userModal');
                const form = document.getElementById('userForm');
                const modalTitle = document.getElementById('modalTitle');
                const passwordInput = document.getElementById('password');
                const passwordHint = document.getElementById('passwordHint');
                // Reset form and set for adding new user
                form.reset();
                modalTitle.textContent = 'Thêm thành viên';
                form.action = 'admin?action=insert';
                passwordInput.required = true;
                passwordHint.style.display = 'none';
                document.getElementById('avatarPreview').style.display = 'none';
                modal.style.display = 'block';
            }

            function openEditUserModal(button) {
                const modal = document.getElementById('userModal');
                const form = document.getElementById('userForm');
                const modalTitle = document.getElementById('modalTitle');
                const passwordInput = document.getElementById('password');
                const passwordHint = document.getElementById('passwordHint');
                // Get user data from table row
                const row = button.closest('tr');
                const userID = row.querySelector('td[name="userID"]').textContent.trim();
                const fullName = row.querySelector('td[name="fullName"]').textContent.trim();
                const email = row.querySelector('td[name="email"]').textContent.trim();
                const dateOfBirth = row.querySelector('td[name="dateOfBirth"]').textContent.trim();
                const status = row.querySelector('td[name="status"] span').textContent.includes('Hoạt động') ? 'true' : 'false';
                const avatarSrc = row.querySelector('td[name="avatarSrc"] img').src;
                // Fill form with user data
                document.getElementById('userID').value = userID;
                document.getElementById('fullName').value = fullName;
                document.getElementById('email').value = email;
                document.getElementById('dob').value = dateOfBirth === 'Không có' ? '' : dateOfBirth;
                document.getElementById('status').value = status;
                // Set avatar preview
                const avatarPreview = document.getElementById('avatarPreview');
                avatarPreview.src = avatarSrc;
                avatarPreview.style.display = 'block';
                // Set form for editing
                modalTitle.textContent = 'Chỉnh sửa thành viên';
                form.action = 'admin?action=update';
                passwordInput.required = false;
                passwordHint.style.display = 'block';
                modal.style.display = 'block';
            }




            function closeModal(modalId) {
                document.getElementById(modalId).style.display = 'none';
            }


            document.getElementById('avatar').addEventListener('change', function (e) {
                const avatarPreview = document.getElementById('avatarPreview');
                if (e.target.files && e.target.files[0]) {
                    avatarPreview.src = URL.createObjectURL(e.target.files[0]);
                    avatarPreview.style.display = 'block';
                } else {
                    avatarPreview.style.display = 'none';
                }
            });


            window.onclick = function (event) {
                const modal = document.getElementById('userModal');
                if (event.target === modal) {
                    modal.style.display = 'none';
                }
            };
        </script>
    </body>
</html>