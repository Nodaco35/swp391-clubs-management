<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>IC Officer - Quản lý kỳ học</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ic-dashboard.css">
    <style>
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
        }
        .modal-content {
            background-color: #fff;
            margin: 10% auto;
            padding: 20px;
            border-radius: 8px;
            width: 90%;
            max-width: 600px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
        }
        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid #e0e0e0;
            padding-bottom: 10px;
            margin-bottom: 20px;
        }
        .modal-header h2 {
            margin: 0;
            font-size: 1.5rem;
        }
        .close {
            font-size: 1.5rem;
            cursor: pointer;
            color: #333;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: 500;
        }
        .form-group input, .form-group select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 1rem;
        }
        .form-actions {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            margin-top: 20px;
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 1rem;
        }
        .btn-primary {
            background-color: #007bff;
            color: #fff;
        }
        .btn-secondary {
            background-color: #6c757d;
            color: #fff;
        }
        .semester-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        .semester-table th, .semester-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #e0e0e0;
        }
        .semester-table th {
            background-color: #f8f9fa;
            font-weight: 500;
        }
        .semester-table tr:hover {
            background-color: #f1f1f1;
        }
        .action-buttons a, .action-buttons button {
            margin-right: 10px;
            text-decoration: none;
            color: #007bff;
        }
        .action-buttons .delete-btn {
            color: #dc3545;
        }
        .alert-success, .alert-warning {
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
        }
        .alert-success {
            background-color: rgba(16, 185, 129, 0.1);
            color: #10b981;
            border: 1px solid rgba(16, 185, 129, 0.2);
        }
        .alert-warning {
            background-color: rgba(245, 158, 11, 0.1);
            color: #f59e0b;
            border: 1px solid rgba(245, 158, 11, 0.2);
        }
    </style>
</head>
<body>
    <div class="dashboard">
        <jsp:include page="components/ic-sidebar.jsp" />
        <main class="main-content">
            <div class="header">
                <h1 class="page-title">Quản lý kỳ học</h1>
                <div class="user-profile">
                    <div class="user-avatar">IC</div>
                    <div class="user-info">
                        <div class="user-name">${sessionScope.user.fullName}</div>
                        <div class="user-role">IC Officer</div>
                    </div>
                </div>
            </div>

            <c:if test="${not empty message}">
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i> ${message}
                </div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-warning">
                    <i class="fas fa-exclamation-circle"></i> ${error}
                </div>
            </c:if>

            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Danh sách kỳ học</h2>
                    <div class="card-actions">
                        <button class="btn btn-primary" onclick="openAddModal()">
                            <i class="fas fa-plus"></i> Thêm kỳ học
                        </button>
                    </div>
                </div>
                <table class="semester-table">
                    <thead>
                        <tr>
                            <th>Mã kỳ</th>
                            <th>Tên kỳ</th>
                            <th>Ngày bắt đầu</th>
                            <th>Ngày kết thúc</th>
                            <th>Trạng thái</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${semesters}" var="semester">
                            <tr>
                                <td>${semester.termID}</td>
                                <td>${semester.termName}</td>
                                <td>${semester.startDate}</td>
                                <td>${semester.endDate}</td>
                                <td>${semester.status}</td>
                                <td class="action-buttons">
                                    <a href="javascript:void(0)" onclick="openEditModal('${semester.termID}', '${semester.termName}', '${semester.startDate}', '${semester.endDate}', '${semester.status}')">
                                        <i class="fas fa-edit"></i> Sửa
                                    </a>
                                    <a href="${pageContext.request.contextPath}/ic/semester?action=deleteSemester&id=${semester.termID}" class="delete-btn" onclick="return confirm('Bạn có chắc chắn muốn xóa kỳ ${semester.termName}?')">
                                        <i class="fas fa-trash"></i> Xóa
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </main>
    </div>

    <!-- Modal thêm kỳ học -->
    <div id="addSemesterModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2>Thêm kỳ học</h2>
                <span class="close" onclick="closeModal('addSemesterModal')">×</span>
            </div>
            <form id="addSemesterForm" action="${pageContext.request.contextPath}/ic/semester?action=addSemester" method="post">
                <div class="form-group">
                    <label for="termID">Mã kỳ (VD: SP25, SU25, FA24)</label>
                    <input type="text" id="termID" name="termID" required pattern="(FA|SP|SU)[0-9]{2}" title="Mã kỳ phải có định dạng như SP25, SU25, FA24">
                </div>
                <div class="form-group">
                    <label for="termName">Tên kỳ (VD: Spring 2025, Summer 2025)</label>
                    <input type="text" id="termName" name="termName" required pattern="(Spring|Summer|Fall) [0-9]{4}" title="Tên kỳ phải có định dạng như Spring 2025, Summer 2025, Fall 2024">
                </div>
                <div class="form-group">
                    <label for="startDate">Ngày bắt đầu</label>
                    <input type="date" id="startDate" name="startDate" required>
                </div>
                <div class="form-group">
                    <label for="endDate">Ngày kết thúc</label>
                    <input type="date" id="endDate" name="endDate" required>
                </div>
                <div class="form-group">
                    <label for="status">Trạng thái</label>
                    <select id="status" name="status">
                        <option value="ACTIVE">ACTIVE</option>
                        <option value="INACTIVE">INACTIVE</option>
                    </select>
                </div>
                <div class="form-actions">
                    <button type="button" class="btn btn-secondary" onclick="closeModal('addSemesterModal')">Hủy</button>
                    <button type="submit" class="btn btn-primary">Thêm</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal sửa kỳ học -->
    <div id="editSemesterModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2>Sửa kỳ học</h2>
                <span class="close" onclick="closeModal('editSemesterModal')">×</span>
            </div>
            <form id="editSemesterForm" action="${pageContext.request.contextPath}/ic/semester?action=editSemester" method="post">
                <input type="hidden" id="editTermID" name="termID">
                <div class="form-group">
                    <label for="editTermIDDisplay">Mã kỳ</label>
                    <input type="text" id="editTermIDDisplay" disabled>
                </div>
                <div class="form-group">
                    <label for="editTermName">Tên kỳ</label>
                    <input type="text" id="editTermName" name="termName" required pattern="(Spring|Summer|Fall) [0-9]{4}" title="Tên kỳ phải có định dạng như Spring 2025, Summer 2025, Fall 2024">
                </div>
                <div class="form-group">
                    <label for="editStartDate">Ngày bắt đầu</label>
                    <input type="date" id="editStartDate" name="startDate" required>
                </div>
                <div class="form-group">
                    <label for="editEndDate">Ngày kết thúc</label>
                    <input type="date" id="editEndDate" name="endDate" required>
                </div>
                <div class="form-group">
                    <label for="editStatus">Trạng thái</label>
                    <select id="editStatus" name="status">
                        <option value="ACTIVE">ACTIVE</option>
                        <option value="INACTIVE">INACTIVE</option>
                    </select>
                </div>
                <div class="form-actions">
                    <button type="button" class="btn btn-secondary" onclick="closeModal('editSemesterModal')">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        function openAddModal() {
            document.getElementById('addSemesterModal').style.display = 'block';
        }

        function openEditModal(termID, termName, startDate, endDate, status) {
            document.getElementById('editTermID').value = termID;
            document.getElementById('editTermIDDisplay').value = termID;
            document.getElementById('editTermName').value = termName;
            document.getElementById('editStartDate').value = startDate;
            document.getElementById('editEndDate').value = endDate;
            document.getElementById('editStatus').value = status;
            document.getElementById('editSemesterModal').style.display = 'block';
        }

        function closeModal(modalId) {
            document.getElementById(modalId).style.display = 'none';
        }

        window.onclick = function(event) {
            if (event.target.classList.contains('modal')) {
                event.target.style.display = 'none';
            }
        }

        // Trim inputs before form submission
        document.getElementById('addSemesterForm').addEventListener('submit', function(e) {
            document.getElementById('termID').value = document.getElementById('termID').value.trim();
            document.getElementById('termName').value = document.getElementById('termName').value.trim();
        });

        document.getElementById('editSemesterForm').addEventListener('submit', function(e) {
            document.getElementById('editTermName').value = document.getElementById('editTermName').value.trim();
        });
    </script>
</body>
</html>