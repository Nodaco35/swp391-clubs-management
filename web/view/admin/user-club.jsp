<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý thành viên câu lạc bộ</title>
    <style>
        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            margin: 30px;
            background-color: #f5f7fa;
            color: #333;
        }
        h2 {
            color: #2c3e50;
            text-align: center;
            margin-bottom: 20px;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        }
        /* Header controls */
        .header-controls {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 15px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }
        /* Back button */
        .back-btn {
            background-color: #6c757d;
            color: #fff;
            padding: 10px 20px;
            border-radius: 5px;
            text-decoration: none;
            font-size: 14px;
            transition: background-color 0.3s ease;
        }
        .back-btn:hover {
            background-color: #5a6268;
        }
        /* Search form styles */
        .search-form {
            display: flex;
            flex: 1;
            gap: 10px;
            max-width: 600px;
        }
        .search-form input[type="text"] {
            flex: 1;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 14px;
        }
        .search-form button {
            background-color: #3498db;
            color: #fff;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.3s ease;
        }
        .search-form button:hover {
            background-color: #2980b9;
        }
        /* Add member button */
        .add-member-btn {
            background-color: #27ae60;
            color: #fff;
            padding: 10px 20px;
            border-radius: 5px;
            text-decoration: none;
            font-size: 14px;
            transition: background-color 0.3s ease;
        }
        .add-member-btn:hover {
            background-color: #219653;
        }
        /* Table styles */
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            background-color: #fff;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #e0e0e0;
        }
        th {
            background-color: #3498db;
            color: #fff;
            font-weight: 600;
        }
        tr:hover {
            background-color: #f1f5f9;
        }
        /* Pagination styles */
        .pagination {
            display: flex;
            justify-content: center;
            margin: 20px 0;
        }
        .pagination a {
            margin: 0 5px;
            padding: 8px 12px;
            text-decoration: none;
            border: 1px solid #3498db;
            color: #3498db;
            border-radius: 5px;
            transition: all 0.3s ease;
        }
        .pagination a:hover {
            background-color: #3498db;
            color: #fff;
        }
        .pagination a.active {
            background-color: #3498db;
            color: #fff;
            border-color: #3498db;
        }
        /* Message and error styles */
        .message {
            color: #27ae60;
            background-color: #e8f4f8;
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 15px;
            text-align: center;
        }
        .error {
            color: #c0392b;
            background-color: #f8e1e1;
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 15px;
            text-align: center;
        }
        /* Form styles */
        .form-container {
            display: none;
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            margin-top: 20px;
            opacity: 0;
            transform: translateY(20px);
            transition: opacity 0.3s ease, transform 0.3s ease;
        }
        .form-container.active {
            display: block;
            opacity: 1;
            transform: translateY(0);
        }
        .form-container h3 {
            margin-top: 0;
            color: #2c3e50;
        }
        .form-container label {
            display: block;
            margin: 10px 0 5px;
            font-weight: 500;
            color: #34495e;
        }
        .form-container input[type="text"],
        .form-container select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 14px;
        }
        .form-container input[type="checkbox"] {
            margin-right: 5px;
        }
        .form-container button {
            background-color: #3498db;
            color: #fff;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.3s ease;
            margin-top: 10px;
            margin-right: 8px;
        }
        .form-container button:hover {
            background-color: #2980b9;
        }
        .form-container button.cancel {
            background-color: #6c757d;
            color: #fff;
        }
        .form-container button.cancel:hover {
            background-color: #5a6268;
        }
        /* Action buttons */
        .action-buttons a, .action-buttons button {
            padding: 6px 12px;
            border-radius: 5px;
            text-decoration: none;
            font-size: 14px;
            transition: all 0.3s ease;
        }
        .action-buttons a {
            background-color: #3498db;
            color: #fff;
            margin-right: 5px;
        }
        .action-buttons a:hover {
            background-color: #2980b9;
        }
        .action-buttons button {
            background-color: #e74c3c;
            color: #fff;
            border: none;
            cursor: pointer;
        }
        .action-buttons button:hover {
            background-color: #c0392b;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Quản lý thành viên của ${clubName}</h2>

        <!-- Hiển thị thông báo -->
        <c:if test="${not empty message}">
            <div class="message">${message}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <!-- Header controls: Nút quay lại, Form tìm kiếm và nút thêm -->
        <div class="header-controls">
            <a href="${pageContext.request.contextPath}/club-detail?id=${clubID}" class="back-btn">
                <i class="fas fa-arrow-left"></i> Quay lại
            </a>
            <form class="search-form" action="${pageContext.request.contextPath}/club-members" method="post">
                <input type="hidden" name="clubID" value="${clubID}">
                <input type="hidden" name="action" value="search">
                <input type="text" name="search" value="${search}" placeholder="Tìm kiếm...">
                <button type="submit">Tìm kiếm</button>
            </form>
            <a href="#" class="add-member-btn" onclick="toggleAddForm()">Thêm thành viên</a>
        </div>

        <!-- Bảng danh sách thành viên -->
        <table>
            <thead>
                <tr>
                    <th>Mã SV</th>
                    <th>Họ tên</th>
                    <th>Ban</th>
                    <th>Vai trò</th>
                    <th>Ngày tham gia</th>
                    <th>Trạng thái</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="uc" items="${userClubs}">
                    <tr>
                        <td>${uc.userID}</td>
                        <td>${uc.fullName}</td>
                        <td>${uc.departmentName}</td>
                        <td>${uc.roleName}</td>
                        <td>${uc.joinDate}</td>
                        <td>${uc.isActive ? 'Hoạt động' : 'Không hoạt động'}</td>
                        <td class="action-buttons">
                            <a href="#" onclick="toggleEditForm(${uc.userClubID}, '${uc.userID}', ${uc.departmentID}, ${uc.roleID}, ${uc.isActive})">Sửa</a>
                            <form action="${pageContext.request.contextPath}/club-members" method="post" style="display:inline;">
                                <input type="hidden" name="clubID" value="${clubID}">
                                <input type="hidden" name="userClubID" value="${uc.userClubID}">
                                <input type="hidden" name="action" value="delete">
                                <button type="submit" onclick="return confirm('Bạn có chắc muốn xóa?')">Xóa</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <!-- Phân trang -->
        <div class="pagination">
            <c:if test="${currentPage > 1}">
                <a href="${pageContext.request.contextPath}/club-members?clubID=${clubID}&page=${currentPage - 1}&search=${search}">« Trước</a>
            </c:if>
            <c:forEach begin="1" end="${totalPages}" var="i">
                <a href="${pageContext.request.contextPath}/club-members?clubID=${clubID}&page=${i}&search=${search}" class="${i == currentPage ? 'active' : ''}">${i}</a>
            </c:forEach>
            <c:if test="${currentPage < totalPages}">
                <a href="${pageContext.request.contextPath}/club-members?clubID=${clubID}&page=${currentPage + 1}&search=${search}">Tiếp »</a>
            </c:if>
        </div>

        <!-- Form thêm/sửa thành viên -->
        <div class="form-container" id="memberForm">
            <h3 id="formTitle">Thêm thành viên</h3>
            <form action="${pageContext.request.contextPath}/club-members" method="post" id="memberFormData">
                <input type="hidden" name="clubID" value="${clubID}">
                <input type="hidden" name="action" id="formAction" value="add">
                <input type="hidden" name="userClubID" id="userClubID">

                <label for="userID">Mã thành viên:</label>
                <input type="text" name="userID" id="userID" required><br>

                <label for="departmentID">Ban:</label>
                <select name="departmentID" id="departmentID" required>
                    <c:forEach var="dept" items="${departments}">
                        <option value="${dept.departmentID}">${dept.departmentName}</option>
                    </c:forEach>
                </select><br>

                <label for="roleID">Vai trò:</label>
                <select name="roleID" id="roleID" required>
                    <c:forEach var="role" items="${roles}">
                        <option value="${role.roleID}">${role.roleName}</option>
                    </c:forEach>
                </select><br>

                <label for="isActive">Trạng thái:</label>
                <input type="checkbox" name="isActive" id="isActive" value="true"> Hoạt động<br>

                <button type="submit" id="submitButton">Thêm</button>
                <button type="button" class="cancel" onclick="hideForm()">Hủy</button>
            </form>
        </div>
    </div>

    <script>
        let currentEditUserClubID = null;

        // Ẩn form khi tải trang
        document.addEventListener('DOMContentLoaded', function() {
            hideForm();
            // Nếu có editUserClub (từ servlet), hiển thị form sửa
            <c:if test="${editUserClub != null}">
                showEditForm(
                    ${editUserClub.userClubID},
                    '${editUserClub.userID}',
                    ${editUserClub.departmentID},
                    ${editUserClub.roleID},
                    ${editUserClub.isActive}
                );
                currentEditUserClubID = ${editUserClub.userClubID};
            </c:if>
        });

        // Toggle form thêm
        function toggleAddForm() {
            const form = document.getElementById('memberForm');
            if (form.classList.contains('active') && document.getElementById('formAction').value === 'add') {
                hideForm();
            } else {
                showAddForm();
            }
        }

        // Toggle form sửa
        function toggleEditForm(userClubID, userID, departmentID, roleID, isActive) {
            const form = document.getElementById('memberForm');
            if (form.classList.contains('active') && currentEditUserClubID === userClubID) {
                hideForm();
                currentEditUserClubID = null;
            } else {
                showEditForm(userClubID, userID, departmentID, roleID, isActive);
                currentEditUserClubID = userClubID;
            }
        }

        // Hiển thị form thêm
        function showAddForm() {
            const form = document.getElementById('memberForm');
            const formTitle = document.getElementById('formTitle');
            const formAction = document.getElementById('formAction');
            const submitButton = document.getElementById('submitButton');
            const userIDInput = document.getElementById('userID');

            form.classList.add('active');
            formTitle.textContent = 'Thêm thành viên';
            formAction.value = 'add';
            submitButton.textContent = 'Thêm';
            userIDInput.removeAttribute('readonly');
            document.getElementById('memberFormData').reset();
            document.getElementById('userClubID').value = '';
            document.getElementById('isActive').checked = false;
            currentEditUserClubID = null;
        }

        // Hiển thị form sửa
        function showEditForm(userClubID, userID, departmentID, roleID, isActive) {
            const form = document.getElementById('memberForm');
            const formTitle = document.getElementById('formTitle');
            const formAction = document.getElementById('formAction');
            const submitButton = document.getElementById('submitButton');
            const userIDInput = document.getElementById('userID');

            form.classList.add('active');
            formTitle.textContent = 'Sửa thành viên';
            formAction.value = 'update';
            submitButton.textContent = 'Cập nhật';
            userIDInput.setAttribute('readonly', 'readonly');

            document.getElementById('userClubID').value = userClubID;
            document.getElementById('userID').value = userID;
            document.getElementById('departmentID').value = departmentID;
            document.getElementById('roleID').value = roleID;
            document.getElementById('isActive').checked = isActive;
        }

        // Ẩn form
        function hideForm() {
            document.getElementById('memberForm').classList.remove('active');
            document.getElementById('memberFormData').reset();
            currentEditUserClubID = null;
        }
    </script>
</body>
</html>
