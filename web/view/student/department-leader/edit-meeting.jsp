<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa cuộc họp | Trưởng ban</title>    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
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
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/department-meeting?action=view&id=${meeting.meetingID}&departmentID=${departmentID}">Chi tiết cuộc họp</a></li>
                        <li class="breadcrumb-item active" aria-current="page">Chỉnh sửa</li>
                    </ol>
                </nav>

                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h2>Chỉnh sửa cuộc họp</h2>
                </div>

                <div class="alert alert-danger ${errorMessage == null ? 'd-none' : ''}" role="alert">
                    ${errorMessage}
                </div>

                <div class="row">
                    <div class="col-md-8">
                        <div class="card">
                            <div class="card-body">
                                <form action="${pageContext.request.contextPath}/department-meeting" method="post" class="needs-validation" novalidate>
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="meetingID" value="${meeting.meetingID}">
                                    <input type="hidden" name="departmentID" value="${departmentID}">
                                    
                                    <div class="mb-3">
                                        <label for="meetingDateTime" class="form-label">Thời gian <span class="text-danger">*</span></label>
                                        <fmt:formatDate value="${meeting.startedTime}" pattern="yyyy-MM-dd'T'HH:mm" var="formattedDateTime" />
                                        <input type="datetime-local" class="form-control" id="meetingDateTime" name="meetingDateTime" value="${formattedDateTime}" required>
                                        <div class="invalid-feedback">
                                            Vui lòng chọn thời gian cuộc họp
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="urlMeeting" class="form-label">URL Cuộc họp <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="urlMeeting" name="urlMeeting" value="${meeting.urlMeeting}" required>
                                        <div class="invalid-feedback">
                                            Vui lòng nhập URL cuộc họp
                                        </div>
                                        <small class="form-text text-muted">URL của cuộc họp online (Google Meet, Zoom, Microsoft Teams, etc.)</small>
                                    </div>
                                    
                                    <div class="mt-4 d-flex justify-content-end">
                                        <a href="${pageContext.request.contextPath}/department-meeting?action=view&id=${meeting.meetingID}&departmentID=${departmentID}" class="btn btn-secondary me-2">Hủy</a>
                                        <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-4">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="card-title mb-0">Người tham gia (${meeting.participantCount})</h5>
                            </div>
                            <div class="card-body p-0">
                                <ul class="list-group list-group-flush">
                                    <c:forEach items="${participants}" var="participant">
                                        <li class="list-group-item">
                                            <div class="d-flex align-items-center">
                                                <img src="${empty participant.userAvatar ? 'https://via.placeholder.com/40' : pageContext.request.contextPath.concat('/img/').concat(participant.userAvatar)}" 
                                                    class="rounded-circle me-2" width="40" height="40" alt="Avatar">
                                                <div>
                                                    <div class="fw-bold">${participant.userName}</div>
                                                    <small class="text-muted">${participant.userEmail}</small>
                                                </div>
                                            </div>
                                        </li>
                                    </c:forEach>
                                    <c:if test="${empty participants}">
                                        <li class="list-group-item text-center text-muted">Chưa có người tham gia nào</li>
                                    </c:if>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.7.0.js"></script>
    <script>
        $(document).ready(function() {
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
