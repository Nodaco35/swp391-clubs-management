<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">    <title>Quản Lý Thành Viên Ban</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <style>
        .member-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            object-fit: cover;
        }
        .status-active {
            color: green;
            font-weight: bold;
        }
        .status-inactive {
            color: red;
            font-weight: bold;
        }
        .role-leader {
            background-color: #ffc107;
            padding: 3px 8px;
            border-radius: 10px;
            font-size: 12px;
            font-weight: bold;
        }
        .role-member {
            background-color: #17a2b8;
            color: white;
            padding: 3px 8px;
            border-radius: 10px;
            font-size: 12px;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <!-- Include sidebar -->
    <jsp:include page="sidebar.jsp"></jsp:include>
    
    <div class="container-fluid px-4 mt-4">
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h4 class="mb-0">Danh Sách Thành Viên Ban</h4>
                        <a href="${pageContext.request.contextPath}/department-members?action=add&clubID=${param.clubID}" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Thêm Thành Viên
                        </a>
                    </div>
                    <div class="card-body">
                        <!-- Thông báo -->
                        <c:if test="${not empty sessionScope.successMessage}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                ${sessionScope.successMessage}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                            <c:remove var="successMessage" scope="session" />
                        </c:if>                        <c:if test="${not empty sessionScope.errorMessage}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                ${sessionScope.errorMessage}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                            <c:remove var="errorMessage" scope="session" />
                        </c:if>
                        
                        <!-- Thống kê thành viên -->
                        <div class="row mb-4">
                            <div class="col-md-6">
                                <div class="card border-primary">
                                    <div class="card-body text-center">
                                        <h3 class="text-primary">${totalMembers}</h3>
                                        <p class="mb-0">Tổng Số Thành Viên</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="card border-success">
                                    <div class="card-body text-center">
                                        <h3 class="text-success">${activeMembers}</h3>
                                        <p class="mb-0">Thành Viên Đang Hoạt Động</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Bảng thành viên -->
                        <div class="table-responsive">
                            <table class="table table-striped table-hover">
                                <thead>
                                    <tr>
                                        <th>Ảnh</th>
                                        <th>Họ Tên</th>
                                        <th>Email</th>
                                        <th>Vai Trò</th>
                                        <th>Ngày Tham Gia</th>
                                        <th>Trạng Thái</th>
                                        <th>Hành Động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="member" items="${members}">
                                        <tr>
                                            <td>
                                                <img src="${pageContext.request.contextPath}/${member.avatarUrl}" alt="${member.fullName}" class="member-avatar">
                                            </td>
                                            <td>${member.fullName}</td>
                                            <td>${member.email}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${member.roleID == 3}">
                                                        <span class="role-leader">Trưởng Ban</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="role-member">Thành Viên</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td><fmt:formatDate value="${member.joinDate}" pattern="dd/MM/yyyy" /></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${member.active}">
                                                        <span class="status-active">Đang hoạt động</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-inactive">Ngưng hoạt động</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/department-members?action=view&id=${member.userClubID}&clubID=${param.clubID}" class="btn btn-sm btn-info">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/department-members?action=edit&id=${member.userClubID}&clubID=${param.clubID}" class="btn btn-sm btn-warning">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <c:if test="${member.roleID != 3}">
                                                    <button onclick="confirmDelete(${member.userClubID})" class="btn btn-sm btn-danger">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty members}">
                                        <tr>
                                            <td colspan="7" class="text-center">Không có thành viên nào trong ban này</td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Modal Xác nhận xóa -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Xác nhận xóa thành viên</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    Bạn có chắc chắn muốn xóa thành viên này khỏi ban?
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <a id="deleteLink" href="#" class="btn btn-danger">Xóa</a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function confirmDelete(id) {
            const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
            document.getElementById('deleteLink').href = '${pageContext.request.contextPath}/department-members?action=delete&id=' + id + '&clubID=${param.clubID}';
            deleteModal.show();
        }
    </script>
</body>
</html>
