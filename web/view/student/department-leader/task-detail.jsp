<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết nhiệm vụ | Trưởng ban</title>    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
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
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h2>Chi tiết nhiệm vụ</h2>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <a href="${pageContext.request.contextPath}/department-task?action=list" class="btn btn-sm btn-secondary me-2">
                            <i class="bi bi-arrow-left"></i> Quay lại danh sách
                        </a>
                        <a href="${pageContext.request.contextPath}/department-task?action=edit&id=${task.taskID}" class="btn btn-sm btn-warning me-2">
                            <i class="bi bi-pencil"></i> Chỉnh sửa
                        </a>
                        <button type="button" class="btn btn-sm btn-danger" data-bs-toggle="modal" data-bs-target="#deleteModal">
                            <i class="bi bi-trash"></i> Xóa
                        </button>
                    </div>
                </div>

                <div class="alert alert-success ${successMessage == null ? 'd-none' : ''}" role="alert">
                    ${successMessage}
                </div>

                <div class="alert alert-danger ${errorMessage == null ? 'd-none' : ''}" role="alert">
                    ${errorMessage}
                </div>

                <!-- Task details -->
                <div class="row">
                    <div class="col-lg-8">
                        <!-- Main task information -->
                        <div class="card mb-4">
                            <div class="card-header d-flex justify-content-between align-items-center">
                                <h5 class="mb-0">Thông tin nhiệm vụ</h5>
                                <span class="badge ${task.status == 'completed' ? 'bg-success' : task.status == 'in_progress' ? 'bg-primary' : 'bg-warning'}">
                                    ${task.status == 'completed' ? 'Hoàn thành' : task.status == 'in_progress' ? 'Đang thực hiện' : 'Đang chờ'}
                                </span>
                            </div>
                            <div class="card-body">
                                <h4 class="card-title">${task.taskName}</h4>
                                <div class="mb-3">
                                    <span class="badge bg-${task.priority == 'high' ? 'danger' : task.priority == 'medium' ? 'warning' : 'info'} me-2">
                                        Ưu tiên: ${task.priority == 'high' ? 'Cao' : task.priority == 'medium' ? 'Trung bình' : 'Thấp'}
                                    </span>
                                </div>
                                <div class="mb-3">
                                    <strong>Ngày bắt đầu:</strong> ${task.startDate}
                                </div>
                                <div class="mb-3">
                                    <strong>Hạn chót:</strong> ${task.deadline}
                                </div>
                                <div class="mb-4">
                                    <h6>Mô tả</h6>
                                    <p>${task.description}</p>
                                </div>
                                
                                <h6>Tệp đính kèm</h6>
                                <div class="mb-4">
                                    <c:if test="${not empty task.attachments}">
                                        <ul class="list-group">
                                            <c:forEach items="${task.attachments}" var="attachment">
                                                <li class="list-group-item">
                                                    <a href="${pageContext.request.contextPath}/files/${attachment.filePath}" target="_blank">
                                                        <i class="bi bi-paperclip"></i> ${attachment.fileName}
                                                    </a>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </c:if>
                                    <c:if test="${empty task.attachments}">
                                        <p class="text-muted">Không có tệp đính kèm</p>
                                    </c:if>
                                </div>
                            </div>
                        </div>

                        <!-- Task comments -->
                        <div class="card mb-4">
                            <div class="card-header">
                                <h5 class="mb-0">Bình luận</h5>
                            </div>
                            <div class="card-body">
                                <c:if test="${empty task.comments}">
                                    <p class="text-muted">Chưa có bình luận nào</p>
                                </c:if>
                                <c:if test="${not empty task.comments}">
                                    <ul class="list-unstyled">
                                        <c:forEach items="${task.comments}" var="comment">
                                            <li class="mb-3 pb-3 border-bottom">
                                                <div class="d-flex">
                                                    <img src="${pageContext.request.contextPath}/img/${comment.user.avatar != null ? comment.user.avatar : 'Hinh-anh-dai-dien-mac-dinh-Facebook.jpg'}" 
                                                        class="rounded-circle me-2" width="40" height="40" alt="User avatar">
                                                    <div>
                                                        <div class="fw-bold">${comment.user.fullName}</div>
                                                        <div class="text-muted small">${comment.createdDate}</div>
                                                        <div class="mt-2">${comment.content}</div>
                                                    </div>
                                                </div>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:if>

                                <!-- Comment form -->
                                <form action="${pageContext.request.contextPath}/department-task" method="post">
                                    <input type="hidden" name="action" value="comment">
                                    <input type="hidden" name="taskID" value="${task.taskID}">
                                    <div class="mb-3">
                                        <label for="comment" class="form-label">Thêm bình luận</label>
                                        <textarea class="form-control" id="comment" name="comment" rows="3" required></textarea>
                                    </div>
                                    <button type="submit" class="btn btn-primary">Gửi</button>
                                </form>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-4">
                        <!-- Assigned members -->
                        <div class="card mb-4">
                            <div class="card-header">
                                <h5 class="mb-0">Người được giao nhiệm vụ</h5>
                            </div>
                            <div class="card-body">
                                <c:if test="${empty task.assignedMembers}">
                                    <p class="text-muted">Chưa có thành viên nào được gán</p>
                                </c:if>
                                <c:if test="${not empty task.assignedMembers}">
                                    <ul class="list-group">
                                        <c:forEach items="${task.assignedMembers}" var="member">
                                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                                <div class="d-flex align-items-center">
                                                    <img src="${pageContext.request.contextPath}/img/${member.avatar != null ? member.avatar : 'Hinh-anh-dai-dien-mac-dinh-Facebook.jpg'}" 
                                                        class="rounded-circle me-2" width="40" height="40" alt="Member avatar">
                                                    <span>${member.fullName}</span>
                                                </div>
                                                <span class="badge bg-${member.status == 'completed' ? 'success' : member.status == 'in_progress' ? 'primary' : 'warning'} rounded-pill">
                                                    ${member.status == 'completed' ? 'Hoàn thành' : member.status == 'in_progress' ? 'Đang thực hiện' : 'Đang chờ'}
                                                </span>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:if>
                            </div>
                        </div>

                        <!-- Progress tracking -->
                        <div class="card mb-4">
                            <div class="card-header">
                                <h5 class="mb-0">Tiến độ</h5>
                            </div>
                            <div class="card-body">
                                <div class="mb-3">
                                    <label class="form-label">Hoàn thành: ${task.progress}%</label>
                                    <div class="progress">
                                        <div class="progress-bar progress-bar-striped bg-success" role="progressbar" 
                                            style="width: ${task.progress}%" 
                                            aria-valuenow="${task.progress}" 
                                            aria-valuemin="0" 
                                            aria-valuemax="100">
                                        </div>
                                    </div>
                                </div>
                                <c:if test="${userRole == 'leader'}">
                                    <form action="${pageContext.request.contextPath}/department-task" method="post">
                                        <input type="hidden" name="action" value="updateProgress">
                                        <input type="hidden" name="taskID" value="${task.taskID}">
                                        <div class="mb-3">
                                            <label for="progress" class="form-label">Cập nhật tiến độ</label>
                                            <input type="range" class="form-range" min="0" max="100" step="5" id="progress" name="progress" value="${task.progress}">
                                            <div class="d-flex justify-content-between">
                                                <span>0%</span>
                                                <span>50%</span>
                                                <span>100%</span>
                                            </div>
                                        </div>
                                        <button type="submit" class="btn btn-primary">Cập nhật</button>
                                    </form>
                                </c:if>
                            </div>
                        </div>

                        <!-- Activity log -->
                        <div class="card">
                            <div class="card-header">
                                <h5 class="mb-0">Lịch sử hoạt động</h5>
                            </div>
                            <div class="card-body">
                                <c:if test="${empty task.activityLogs}">
                                    <p class="text-muted">Không có hoạt động nào được ghi lại</p>
                                </c:if>
                                <c:if test="${not empty task.activityLogs}">
                                    <ul class="list-group">
                                        <c:forEach items="${task.activityLogs}" var="log">
                                            <li class="list-group-item small">
                                                <div class="d-flex justify-content-between">
                                                    <span>${log.action}</span>
                                                    <span class="text-muted">${log.timestamp}</span>
                                                </div>
                                                <div class="text-muted small">Bởi: ${log.user.fullName}</div>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteModalLabel">Xác nhận xóa</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    Bạn có chắc chắn muốn xóa nhiệm vụ "${task.taskName}" không?
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <a href="${pageContext.request.contextPath}/department-task?action=delete&id=${task.taskID}" class="btn btn-danger">Xóa</a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
