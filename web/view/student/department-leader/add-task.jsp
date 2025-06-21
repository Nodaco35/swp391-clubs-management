<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm nhiệm vụ mới | Trưởng ban</title>    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <jsp:include page="sidebar.jsp" />

            <!-- Main content -->
            <main class="col-md-9 ms-sm-auto col-lg-12 px-md-4 pt-4">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h2>Thêm nhiệm vụ mới</h2>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <a href="${pageContext.request.contextPath}/department-task?action=list" class="btn btn-sm btn-secondary">
                            <i class="bi bi-arrow-left"></i> Quay lại danh sách
                        </a>
                    </div>
                </div>

                <div class="alert alert-success ${successMessage == null ? 'd-none' : ''}" role="alert">
                    ${successMessage}
                </div>

                <div class="alert alert-danger ${errorMessage == null ? 'd-none' : ''}" role="alert">
                    ${errorMessage}
                </div>

                <div class="card">
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/department-task" method="post" enctype="multipart/form-data">
                            <input type="hidden" name="action" value="create">
                            
                            <div class="row mb-3">
                                <div class="col-md-12">
                                    <label for="title" class="form-label">Tiêu đề <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="title" name="title" required>
                                </div>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-12">
                                    <label for="description" class="form-label">Mô tả</label>
                                    <textarea class="form-control" id="description" name="description" rows="5"></textarea>
                                </div>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="startDate" class="form-label">Ngày bắt đầu <span class="text-danger">*</span></label>
                                    <input type="date" class="form-control" id="startDate" name="startDate" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="deadline" class="form-label">Hạn chót <span class="text-danger">*</span></label>
                                    <input type="date" class="form-control" id="deadline" name="deadline" required>
                                </div>
                            </div>

                            <div class="row mb-3">                                <div class="col-md-12">
                                    <label for="priority" class="form-label">Độ ưu tiên <span class="text-danger">*</span></label>
                                    <select class="form-select" id="priority" name="priority" required>
                                        <option value="low">Thấp</option>
                                        <option value="medium" selected>Trung bình</option>
                                        <option value="high">Cao</option>
                                    </select>
                                    <!-- Hidden input để set status mặc định là ToDo -->
                                    <input type="hidden" name="status" value="ToDo">
                                </div>
                            </div>                            <div class="row mb-3">                                <div class="col-md-12">
                                    <label for="memberID" class="form-label">Gán cho thành viên</label>
                                    <select class="form-select" id="memberID" name="memberID" multiple style="width: 100%;">
                                        <c:forEach items="${departmentMembers}" var="member">
                                            <option value="${member.userClubID}">${member.fullName} - ${member.roleName}</option>
                                        </c:forEach>
                                    </select>
                                    <div class="form-text">Chọn thành viên để gán nhiệm vụ này</div>
                                </div>
                            </div>

                            <div class="text-end">
                                <button type="reset" class="btn btn-secondary me-2">Làm mới</button>
                                <button type="submit" class="btn btn-primary">Tạo nhiệm vụ</button>
                            </div>
                        </form>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.7.0.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
    <script>        $(document).ready(function() {
            // Initialize Select2
            $('#memberID').select2({
                placeholder: "Chọn thành viên",
                allowClear: true
            });
            
            // Set min date for startDate and deadline (today)
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('startDate').min = today;
            document.getElementById('deadline').min = today;
            
            // Ensure deadline is after startDate
            document.getElementById('startDate').addEventListener('change', function() {
                document.getElementById('deadline').min = this.value;
                
                // If current deadline is before new startDate, update it
                if (document.getElementById('deadline').value < this.value) {
                    document.getElementById('deadline').value = this.value;
                }
            });
        });
    </script>
</body>
</html>
