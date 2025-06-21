<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo cuộc họp mới | Trưởng ban</title>    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
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
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/department-meeting?action=list&departmentID=${departmentID}">Cuộc họp</a></li>
                        <li class="breadcrumb-item active" aria-current="page">Tạo cuộc họp mới</li>
                    </ol>
                </nav>

                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h2>Tạo cuộc họp mới</h2>
                </div>

                <div class="alert alert-danger ${errorMessage == null ? 'd-none' : ''}" role="alert">
                    ${errorMessage}
                </div>

                <div class="row">
                    <div class="col-md-8">
                        <div class="card">
                            <div class="card-body">
                                <form action="${pageContext.request.contextPath}/department-meeting" method="post" class="needs-validation" novalidate>
                                    <input type="hidden" name="action" value="create">
                                    <input type="hidden" name="departmentID" value="${departmentID}">
                                    
                                    <div class="mb-3">
                                        <label for="meetingDateTime" class="form-label">Thời gian <span class="text-danger">*</span></label>
                                        <input type="datetime-local" class="form-control" id="meetingDateTime" name="meetingDateTime" required>
                                        <div class="invalid-feedback">
                                            Vui lòng chọn thời gian cuộc họp
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="urlMeeting" class="form-label">URL Cuộc họp <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="urlMeeting" name="urlMeeting" required
                                               placeholder="https://meet.google.com/xxx-xxxx-xxx hoặc https://zoom.us/j/xxxxxxxxxx">
                                        <div class="invalid-feedback">
                                            Vui lòng nhập URL cuộc họp
                                        </div>
                                        <small class="form-text text-muted">URL của cuộc họp online (Google Meet, Zoom, Microsoft Teams, etc.)</small>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="participants" class="form-label">Người tham gia</label>
                                        <select class="form-select select2-multiple" id="participants" name="participants" multiple>
                                            <c:forEach items="${departmentMembers}" var="member">
                                                <option value="${member.userID}">${member.userName}</option>
                                            </c:forEach>
                                        </select>
                                        <small class="form-text text-muted">Bạn có thể thêm người tham gia sau</small>
                                    </div>
                                    
                                    <div class="mt-4 d-flex justify-content-end">
                                        <a href="${pageContext.request.contextPath}/department-meeting?action=list&departmentID=${departmentID}" class="btn btn-secondary me-2">Hủy</a>
                                        <button type="submit" class="btn btn-primary">Tạo cuộc họp</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-4">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="card-title mb-0">Hướng dẫn</h5>
                            </div>
                            <div class="card-body">
                                <h6>Cách tạo cuộc họp trực tuyến</h6>
                                <ol>
                                    <li>Tạo cuộc họp trong Google Meet, Zoom hoặc Microsoft Teams</li>
                                    <li>Sao chép URL cuộc họp</li>
                                    <li>Dán URL vào trường "URL Cuộc họp"</li>
                                    <li>Chọn thời gian và người tham gia</li>
                                    <li>Nhấn "Tạo cuộc họp" để hoàn tất</li>
                                </ol>
                                <p class="text-muted">Người tham gia sẽ nhận được thông báo về cuộc họp sau khi bạn tạo.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.7.0.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
    <script>
        $(document).ready(function() {
            // Initialize Select2
            $('.select2-multiple').select2({
                placeholder: "Chọn người tham gia",
                allowClear: true
            });
            
            // Form validation
            (function() {
                'use strict';
                var forms = document.querySelectorAll('.needs-validation');
                Array.prototype.slice.call(forms).forEach(function(form) {
                    form.addEventListener('submit', function(event) {
                        if (!form.checkValidity()) {
                            event.preventDefault();
                            event.stopPropagation();
                        }
                        form.classList.add('was-validated');
                    }, false);
                });
            })();
        });
    </script>
</body>
</html>
