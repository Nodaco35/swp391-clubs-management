<%-- 
    Document   : userClub
    Created on : May 21, 2025, 1:34:27 PM
    Author     : FPT Shop
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="./css/userClub.css">
    <title>Danh sách thành viên</title>
    
</head>
<body>
    <div class="header">
        <div class="logo">LOGO</div>
        <div class="user-info">
            <span>Xin chào, ${sessionScope.fullName} (Club ID: ${clubId})</span>
            <form action="logout" method="post" style="display:inline;">
                <button type="submit">Đăng xuất</button>
            </form>
        </div>
    </div>
    <div class="nav">
        <a href="#">Home</a>
        <a href="#">Club</a>
        <a href="#">Event</a>
        <a href="#">...</a>
    </div>
    <div class="search-filter">
        <div>
            <form action="members" method="get">
                <input type="text" name="search" placeholder="tìm kiếm tên thành viên" value="${param.search}">
                <button type="submit">Tìm kiếm</button>
            </form>
        </div>
        <div>
            <button onclick="openAddModal()">thêm + lọc thông tin thành viên</button>
        </div>
    </div>
    <div class="content">
        <h2>DANH SÁCH THÀNH VIÊN CỦA CLUB</h2>
        <table>
            <tr>
                <th>Mã SV</th>
                <th>Tên</th>
                <th>Gmail</th>
                <th>...</th>
            </tr>
            <c:forEach var="member" items="${members}">
                <tr>
                    <td>${member.userID}</td>
                    <td>${member.fullName}</td>
                    <td>${member.email}</td>
                    <td class="actions">
                        <a href="#" onclick="openEditModal(${member.userID}, '${member.fullName}', '${member.email}', '${member.departmentID}')">Sửa</a>
                        <a href="members?action=delete&userId=${member.userID}&clubId=${clubId}">Xóa</a>
                        <a href="#">Giao NV</a>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <div class="pagination">
            <c:forEach begin="1" end="${totalPages}" var="i">
                <a href="members?page=${i}&search=${param.search}">${i}</a>
            </c:forEach>
        </div>
    </div>

    <!-- Add Member Modal -->
    <div id="addModal" class="modal">
        <div class="modal-content">
            <h3>Thêm Thành Viên</h3>
            <form action="members" method="post">
                <input type="hidden" name="action" value="add">
                <input type="hidden" name="clubId" value="${clubId}">
                <label>Tên:</label>
                <input type="text" name="fullName" required>
                <label>Email:</label>
                <input type="email" name="email" required>
                <label>Mật khẩu:</label>
                <input type="password" name="password" required>
                <label>Department ID:</label>
                <input type="text" name="departmentId" required>
                <label>Quyền (Role ID):</label>
                <input type="text" name="permissions" required>
                <button type="submit">Thêm</button>
                <button type="button" onclick="closeAddModal()">Hủy</button>
            </form>
        </div>
    </div>

    <!-- Edit Member Modal -->
    <div id="editModal" class="modal">
        <div class="modal-content">
            <h3>Sửa Thành Viên</h3>
            <form action="members" method="post">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="userId" id="editUserId">
                <input type="hidden" name="clubId" value="${clubId}">
                <label>Tên:</label>
                <input type="text" name="fullName" id="editFullName" required>
                <label>Email:</label>
                <input type="email" name="email" id="editEmail" required>
                <label>Department ID:</label>
                <input type="text" name="departmentId" id="editDepartmentId" required>
                <button type="submit">Lưu</button>
                <button type="button" onclick="closeEditModal()">Hủy</button>
            </form>
        </div>
    </div>

    <script>
        function openAddModal() {
            document.getElementById('addModal').style.display = 'flex';
        }
        function closeAddModal() {
            document.getElementById('addModal').style.display = 'none';
        }
        function openEditModal(userId, fullName, email, departmentId) {
            document.getElementById('editUserId').value = userId;
            document.getElementById('editFullName').value = fullName;
            document.getElementById('editEmail').value = email;
            document.getElementById('editDepartmentId').value = departmentId;
            document.getElementById('editModal').style.display = 'flex';
        }
        function closeEditModal() {
            document.getElementById('editModal').style.display = 'none';
        }
    </script>
</body>
</html>