<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa nhiệm vụ | Trưởng ban</title>    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
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
                    <h2>Chỉnh sửa nhiệm vụ</h2>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <a href="${pageContext.request.contextPath}/department-task?action=view&id=${task.taskID}" class="btn btn-sm btn-secondary">
                            <i class="bi bi-arrow-left"></i> Quay lại chi tiết
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
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="taskID" value="${task.taskID}">
                            
                            <div class="row mb-3">
                                <div class="col-md-12">
                                    <label for="title" class="form-label">Tiêu đề <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="title" name="title" value="${task.taskName}" required>
                                </div>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-12">
                                    <label for="description" class="form-label">Mô tả</label>
                                    <textarea class="form-control" id="description" name="description" rows="5">${task.description}</textarea>
                                </div>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="startDate" class="form-label">Ngày bắt đầu <span class="text-danger">*</span></label>
                                    <input type="date" class="form-control" id="startDate" name="startDate" value="${task.startDate}" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="deadline" class="form-label">Hạn chót <span class="text-danger">*</span></label>
                                    <input type="date" class="form-control" id="deadline" name="deadline" value="${task.deadline}" required>
                                </div>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="priority" class="form-label">Độ ưu tiên <span class="text-danger">*</span></label>
                                    <select class="form-select" id="priority" name="priority" required>
                                        <option value="low" ${task.priority == 'low' ? 'selected' : ''}>Thấp</option>
                                        <option value="medium" ${task.priority == 'medium' ? 'selected' : ''}>Trung bình</option>
                                        <option value="high" ${task.priority == 'high' ? 'selected' : ''}>Cao</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label for="status" class="form-label">Trạng thái <span class="text-danger">*</span></label>
                                    <select class="form-select" id="status" name="status" required>
                                        <option value="pending" ${task.status == 'pending' ? 'selected' : ''}>Chưa bắt đầu</option>
                                        <option value="in_progress" ${task.status == 'in_progress' ? 'selected' : ''}>Đang thực hiện</option>
                                        <option value="completed" ${task.status == 'completed' ? 'selected' : ''}>Đã hoàn thành</option>
                                    </select>
                                </div>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-12">
                                    <label for="assignedMembers" class="form-label">Gán cho thành viên</label>
                                    <select class="form-select" id="assignedMembers" name="assignedMembers" multiple style="width: 100%;">
                                        <c:forEach items="${departmentMembers}" var="member">
                                            <option value="${member.userID}" 
                                                <c:forEach items="${task.assignedMembers}" var="assigned">
                                                    ${assigned.userID == member.userID ? 'selected' : ''}
                                                </c:forEach>
                                            >${member.fullName} - ${member.position}</option>
                                        </c:forEach>
                                    </select>
                                    <div class="form-text">Bạn có thể chọn nhiều thành viên</div>
                                </div>
                            </div>

                            <!-- Current attachments -->
                            <c:if test="${not empty task.attachments}">
                                <div class="row mb-3">
                                    <div class="col-md-12">
                                        <label class="form-label">Tệp đính kèm hiện tại</label>
                                        <ul class="list-group">
                                            <c:forEach items="${task.attachments}" var="attachment">
                                                <li class="list-group-item d-flex justify-content-between align-items-center">
                                                    <div>
                                                        <i class="bi bi-paperclip"></i> ${attachment.fileName}
                                                    </div>
                                                    <div>
                                                        <a href="${pageContext.request.contextPath}/files/${attachment.filePath}" class="btn btn-sm btn-info me-2" target="_blank">
                                                            <i class="bi bi-eye"></i>
                                                        </a>
                                                        <button type="button" class="btn btn-sm btn-danger" 
                                                            onclick="if(confirm('Bạn có chắc chắn muốn xóa tệp đính kèm này?')) 
                                                                window.location.href='${pageContext.request.contextPath}/department-task?action=deleteAttachment&id=${attachment.id}&taskID=${task.taskID}'">
                                                            <i class="bi bi-trash"></i>
                                                        </button>
                                                    </div>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </div>
                            </c:if>

                            <!-- New attachments -->
                            <div class="row mb-3">
                                <div class="col-md-12">
                                    <label for="attachments" class="form-label">Thêm tệp đính kèm mới</label>
                                    <input class="form-control" type="file" id="attachments" name="attachments" multiple>
                                    <div class="form-text">Bạn có thể chọn nhiều tệp đính kèm (tối đa 5MB mỗi tệp)</div>
                                </div>
                            </div>

                            <div class="text-end">
                                <button type="reset" class="btn btn-secondary me-2">Đặt lại</button>
                                <button type="submit" class="btn btn-primary">Cập nhật nhiệm vụ</button>
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
    <script>
        $(document).ready(function() {
            // Initialize Select2
            $('#assignedMembers').select2({
                placeholder: "Chọn thành viên",
                allowClear: true
            });
            
            // Ensure deadline is after startDate
            document.getElementById('startDate').addEventListener('change', function() {
                if (document.getElementById('deadline').value < this.value) {
                    document.getElementById('deadline').value = this.value;
                }
            });
        });
    </script>
</body>
</html>
