<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">    <title>Chi Tiết Thành Viên</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <style>
        .member-avatar {
            width: 150px;
            height: 150px;
            border-radius: 50%;
            object-fit: cover;
            margin-bottom: 20px;
        }
        .member-info {
            margin-top: 20px;
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
                        <h4 class="mb-0">Chi Tiết Thành Viên</h4>
                        <a href="${pageContext.request.contextPath}/department-members?clubID=${param.clubID}" class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Quay Lại
                        </a>
                    </div>
                    <div class="card-body">
                        <c:if test="${empty member}">
                            <div class="alert alert-danger" role="alert">
                                Không tìm thấy thông tin thành viên.
                            </div>
                        </c:if>
                        
                        <c:if test="${not empty member}">
                            <div class="row">
                                <div class="col-md-4 text-center">
                                    <img src="${pageContext.request.contextPath}/${member.avatarUrl}" alt="${member.fullName}" class="member-avatar">
                                    <h4>${member.fullName}</h4>
                                    <p class="badge bg-primary">${member.roleName}</p>
                                    <p class="badge ${member.active ? 'bg-success' : 'bg-danger'}">
                                        ${member.active ? 'Đang hoạt động' : 'Ngưng hoạt động'}
                                    </p>
                                </div>
                                <div class="col-md-8">
                                    <form action="${pageContext.request.contextPath}/department-members" method="post">
                                        <input type="hidden" name="action" value="update">
                                        <input type="hidden" name="userClubId" value="${member.userClubID}">
                                        <input type="hidden" name="clubID" value="${param.clubID}">
                                        
                                        <div class="member-info">
                                            <div class="row mb-3">
                                                <div class="col-md-3">
                                                    <label for="fullName" class="form-label">Họ và tên:</label>
                                                </div>
                                                <div class="col-md-9">
                                                    <input type="text" id="fullName" name="fullName" class="form-control" value="${member.fullName}" required>
                                                </div>
                                            </div>
                                            
                                            <div class="row mb-3">
                                                <div class="col-md-3">
                                                    <label for="email" class="form-label">Email:</label>
                                                </div>
                                                <div class="col-md-9">
                                                    <input type="email" id="email" name="email" class="form-control" value="${member.email}" required>
                                                </div>
                                            </div>
                                            
                                            <div class="row mb-3">
                                                <div class="col-md-3">
                                                    <label for="departmentName" class="form-label">Ban:</label>
                                                </div>
                                                <div class="col-md-9">
                                                    <input type="text" id="departmentName" class="form-control" value="${member.departmentName}" readonly>
                                                </div>
                                            </div>
                                            
                                            <div class="row mb-3">
                                                <div class="col-md-3">
                                                    <label for="joinDate" class="form-label">Ngày tham gia:</label>
                                                </div>
                                                <div class="col-md-9">
                                                    <input type="text" id="joinDate" class="form-control" value="<fmt:formatDate value="${member.joinDate}" pattern="dd/MM/yyyy" />" readonly>
                                                </div>
                                            </div>
                                            
                                            <div class="row mb-3">
                                                <div class="col-md-3">
                                                    <label for="isActive" class="form-label">Trạng thái:</label>
                                                </div>
                                                <div class="col-md-9">
                                                    <div class="form-check form-switch">
                                                        <input class="form-check-input" type="checkbox" id="isActive" name="isActive" ${member.active ? 'checked' : ''}>
                                                        <label class="form-check-label" for="isActive">Đang hoạt động</label>
                                                    </div>
                                                </div>
                                            </div>
                                            
                                            <div class="d-grid gap-2 mt-4">
                                                <button type="submit" class="btn btn-primary">
                                                    <i class="fas fa-save"></i> Cập Nhật Thông Tin
                                                </button>
                                                <c:if test="${member.roleID != 3}">
                                                    <button type="button" class="btn btn-danger" onclick="confirmDelete(${member.userClubID})">
                                                        <i class="fas fa-trash"></i> Xóa Thành Viên
                                                    </button>
                                                </c:if>
                                            </div>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </c:if>
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
