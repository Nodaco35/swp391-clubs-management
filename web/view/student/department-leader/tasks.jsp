<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý nhiệm vụ | Trưởng ban</title>    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css">
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <jsp:include page="sidebar.jsp" />

            <!-- Main content -->
            <main class="col-md-9 ms-sm-auto col-lg-12 px-md-4 pt-4">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h2>Quản lý nhiệm vụ</h2>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <a href="${pageContext.request.contextPath}/department-task?action=add" class="btn btn-sm btn-primary">
                            <i class="bi bi-plus"></i> Thêm nhiệm vụ mới
                        </a>
                    </div>
                </div>

                <div class="alert alert-success ${successMessage == null ? 'd-none' : ''}" role="alert">
                    ${successMessage}
                </div>

                <div class="alert alert-danger ${errorMessage == null ? 'd-none' : ''}" role="alert">
                    ${errorMessage}
                </div>

                <!-- Task Filter -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="mb-0">Bộ lọc nhiệm vụ</h5>
                    </div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/department-task" method="get">
                            <input type="hidden" name="action" value="list">
                            <div class="row g-3 align-items-center">
                                <div class="col-md-3">
                                    <label for="status" class="form-label">Trạng thái</label>
                                    <select class="form-select" id="status" name="status">
                                        <option value="">Tất cả</option>
                                        <option value="pending" ${param.status == 'pending' ? 'selected' : ''}>Chưa hoàn thành</option>
                                        <option value="completed" ${param.status == 'completed' ? 'selected' : ''}>Đã hoàn thành</option>
                                    </select>
                                </div>
                                <div class="col-md-3">
                                    <label for="assignee" class="form-label">Người được giao</label>
                                    <select class="form-select" id="assignee" name="assignee">
                                        <option value="">Tất cả</option>
                                        <c:forEach items="${departmentMembers}" var="member">
                                            <option value="${member.userID}" ${param.assignee == member.userID ? 'selected' : ''}>${member.fullName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-3">
                                    <label for="startDate" class="form-label">Từ ngày</label>
                                    <input type="date" class="form-control" id="startDate" name="startDate" value="${param.startDate}">
                                </div>
                                <div class="col-md-3">
                                    <label for="endDate" class="form-label">Đến ngày</label>
                                    <input type="date" class="form-control" id="endDate" name="endDate" value="${param.endDate}">
                                </div>
                                <div class="col-md-12 text-end">
                                    <button type="submit" class="btn btn-primary">Lọc</button>
                                    <a href="${pageContext.request.contextPath}/department-task?action=list" class="btn btn-secondary">Đặt lại</a>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Task List Table -->
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Danh sách nhiệm vụ</h5>
                    </div>
                    <div class="card-body">
                        <table id="taskTable" class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>Tiêu đề</th>
                                    <th>Người được giao</th>
                                    <th>Ngày bắt đầu</th>
                                    <th>Hạn chót</th>
                                    <th>Trạng thái</th>
                                    <th>Độ ưu tiên</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${tasks}" var="task">
                                    <tr>                                        <td>${task.taskName}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${task.assignedMembers != null && !task.assignedMembers.isEmpty()}">
                                                    <c:forEach items="${task.assignedMembers}" var="member" varStatus="status">
                                                        ${member.fullName}${!status.last ? ', ' : ''}
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">Chưa gán</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${task.startDate}</td>
                                        <td>${task.deadline}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${task.status == 'completed'}">
                                                    <span class="badge bg-success">Hoàn thành</span>
                                                </c:when>
                                                <c:when test="${task.status == 'in_progress'}">
                                                    <span class="badge bg-primary">Đang thực hiện</span>
                                                </c:when>
                                                <c:when test="${task.status == 'pending'}">
                                                    <span class="badge bg-warning">Đang chờ</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">Không xác định</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${task.priority == 'high'}">
                                                    <span class="badge bg-danger">Cao</span>
                                                </c:when>
                                                <c:when test="${task.priority == 'medium'}">
                                                    <span class="badge bg-warning">Trung bình</span>
                                                </c:when>
                                                <c:when test="${task.priority == 'low'}">
                                                    <span class="badge bg-info">Thấp</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">Không xác định</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="btn-group">
                                                <a href="${pageContext.request.contextPath}/department-task?action=view&id=${task.taskID}" class="btn btn-sm btn-info">
                                                    <i class="bi bi-eye"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/department-task?action=edit&id=${task.taskID}" class="btn btn-sm btn-warning">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <button type="button" class="btn btn-sm btn-danger" data-bs-toggle="modal" data-bs-target="#deleteModal${task.taskID}">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            </div>

                                            <!-- Delete Confirmation Modal -->
                                            <div class="modal fade" id="deleteModal${task.taskID}" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
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
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.7.0.js"></script>
    <script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.6/js/dataTables.bootstrap5.min.js"></script>
    <script>
        $(document).ready(function() {
            $('#taskTable').DataTable({
                "language": {
                    "lengthMenu": "Hiển thị _MENU_ dòng mỗi trang",
                    "zeroRecords": "Không tìm thấy nhiệm vụ nào",
                    "info": "Đang hiển thị trang _PAGE_ / _PAGES_",
                    "infoEmpty": "Không có nhiệm vụ nào",
                    "infoFiltered": "(lọc từ _MAX_ nhiệm vụ)",
                    "search": "Tìm kiếm:",
                    "paginate": {
                        "first": "Đầu",
                        "last": "Cuối",
                        "next": "Tiếp",
                        "previous": "Trước"
                    }
                }
            });
        });
    </script>
</body>
</html>
