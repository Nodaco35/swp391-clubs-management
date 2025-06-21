<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">    <title>Thêm Thành Viên Ban</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
</head>
<body>
    <!-- Include sidebar -->
    <jsp:include page="sidebar.jsp"></jsp:include>
    
    <div class="container-fluid px-4 mt-4">
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h4 class="mb-0">Thêm Thành Viên Ban</h4>
                        <a href="${pageContext.request.contextPath}/department-members?clubID=${param.clubID}" class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Quay Lại
                        </a>
                    </div>
                    <div class="card-body">
                        <!-- Thông báo -->
                        <c:if test="${not empty sessionScope.errorMessage}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                ${sessionScope.errorMessage}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                            <c:remove var="errorMessage" scope="session" />
                        </c:if>
                        
                        <form action="${pageContext.request.contextPath}/department-members" method="post">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="clubID" value="${param.clubID}">
                            
                            <div class="mb-3">
                                <label for="userId" class="form-label">Mã Sinh Viên</label>
                                <input type="text" class="form-control" id="userId" name="userId" placeholder="Nhập mã sinh viên" required>
                                <div class="form-text">Ví dụ: U001, U002, ...</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="clubDepartmentId" class="form-label">Ban</label>
                                <select class="form-select" id="clubDepartmentId" name="clubDepartmentId" required>
                                    <option value="" selected disabled>Chọn ban...</option>
                                    <!-- Load từ controller -->
                                    <c:forEach var="dept" items="${departments}">
                                        <option value="${dept.clubDepartmentId}">${dept.departmentName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            
                            <div class="mb-3">
                                <label for="roleId" class="form-label">Vai Trò</label>
                                <select class="form-select" id="roleId" name="roleId" required>
                                    <option value="" selected disabled>Chọn vai trò...</option>
                                    <option value="4">Thành viên</option>
                                </select>
                            </div>
                            
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-plus"></i> Thêm Thành Viên
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
