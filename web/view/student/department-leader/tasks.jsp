<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Công việc - CLB</title>

        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <!-- Custom CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
        <style>
            .table th a {
                color: inherit;
                text-decoration: none;
            }
            .table th a:hover {
                color: #0d6efd;
            }
            .table th .fa-sort,
            .table th .fa-sort-up,
            .table th .fa-sort-down {
                font-size: 0.8em;
                margin-left: 5px;
                opacity: 0.6;
            }
            .table th .fa-sort-up,
            .table th .fa-sort-down {
                opacity: 1;
                color: #0d6efd;
            }
            .search-highlight {
                background-color: yellow;
                font-weight: bold;
            }
            .card-header .badge {
                font-size: 0.7em;
            }
        </style>
    </head>
    <body>
        <!-- Mobile Menu Toggle -->
        <%@ include file="components/mobile-menu.jsp" %>
        
        <!-- Sidebar -->
        <div class="department-leader-container">
            <c:set var="activePage" value="tasks" />
            <%@ include file="components/sidebar.jsp" %>

            <!-- Main Content -->
            <main class="main-content">
                <!-- Header -->
                <header class="header">
                    <div class="header-left">
                        <h1>Quản lý Công việc</h1>
                        <p class="breadcrumb">
                            <span>Câu lạc bộ</span> 
                            <i class="fas fa-chevron-right"></i> 
                            <span>Công việc</span>
                        </p>
                    </div>
                    <div class="header-right">
                        <button class="btn btn-primary" onclick="showCreateTaskModal()">
                            <i class="fas fa-plus"></i> Tạo công việc mới
                        </button>
                    </div>
                </header>

                <!-- Filters -->
                <div class="card mb-4">
                    <div class="card-body">
                        <form method="GET" class="row g-3" id="filterForm">
                            <input type="hidden" name="clubID" value="${clubID}">
                            
                            <!-- Search Box -->
                            <div class="col-md-5">
                                <label for="searchKeyword" class="form-label">Tìm kiếm:</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-search"></i></span>
                                    <input type="text" class="form-control" id="searchKeyword" name="search" 
                                           value="${param.search}" placeholder="Tìm theo tên công việc hoặc người phụ trách...">
                                </div>
                            </div>
                            
                            <div class="col-md-2">
                                <label for="statusFilter" class="form-label">Trạng thái:</label>
                                <select class="form-select" id="statusFilter" name="status">
                                    <option value="">Tất cả trạng thái</option>
                                    <option value="ToDo" ${param.status == 'ToDo' ? 'selected' : ''}>Chưa bắt đầu</option>
                                    <option value="InProgress" ${param.status == 'InProgress' ? 'selected' : ''}>Đang thực hiện</option>
                                    <option value="Review" ${param.status == 'Review' ? 'selected' : ''}>Chờ duyệt</option>
                                    <option value="Done" ${param.status == 'Done' ? 'selected' : ''}>Hoàn thành</option>
                                    <option value="Rejected" ${param.status == 'Rejected' ? 'selected' : ''}>Từ chối</option>
                                </select>
                            </div>
                            
                            <div class="col-md-2">
                                <label for="sortBy" class="form-label">Sắp xếp theo:</label>
                                <select class="form-select" id="sortBy" name="sortBy">
                                    <option value="createdAt" ${param.sortBy == 'createdAt' ? 'selected' : ''}>Ngày tạo</option>
                                    <option value="startDate" ${param.sortBy == 'startDate' ? 'selected' : ''}>Ngày bắt đầu</option>
                                    <option value="endDate" ${param.sortBy == 'endDate' ? 'selected' : ''}>Ngày kết thúc</option>
                                    <option value="title" ${param.sortBy == 'title' ? 'selected' : ''}>Tên công việc</option>
                                    <option value="status" ${param.sortBy == 'status' ? 'selected' : ''}>Trạng thái</option>
                                </select>
                            </div>
                            
                            <div class="col-md-2">
                                <label for="sortOrder" class="form-label">Thứ tự:</label>
                                <select class="form-select" id="sortOrder" name="sortOrder">
                                    <option value="desc" ${param.sortOrder == 'desc' ? 'selected' : ''}>Mới nhất</option>
                                    <option value="asc" ${param.sortOrder == 'asc' ? 'selected' : ''}>Cũ nhất</option>
                                </select>
                            </div>
                            
                            <div class="col-12">
                                <div class="d-flex gap-2">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-filter"></i> Lọc & Tìm kiếm
                                    </button>
                                    <button type="button" class="btn btn-outline-secondary" onclick="clearFilters()">
                                        <i class="fas fa-times"></i> Xóa bộ lọc
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Tasks List -->
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">
                            <i class="fas fa-tasks"></i> 
                            Danh sách công việc (${assignedTasks.size()} công việc)
                        </h5>
                        
                        <!-- Current filters display -->
                        <div class="text-end">
                            <c:if test="${not empty param.search}">
                                <span class="badge bg-info me-1">Tìm kiếm: "${param.search}"</span>
                            </c:if>
                            <c:if test="${not empty param.status}">
                                <span class="badge bg-secondary me-1">Trạng thái: 
                                    <c:choose>
                                        <c:when test="${param.status == 'ToDo'}">Chưa bắt đầu</c:when>
                                        <c:when test="${param.status == 'InProgress'}">Đang thực hiện</c:when>
                                        <c:when test="${param.status == 'Review'}">Chờ duyệt</c:when>
                                        <c:when test="${param.status == 'Done'}">Hoàn thành</c:when>
                                        <c:when test="${param.status == 'Rejected'}">Từ chối</c:when>
                                        <c:otherwise>${param.status}</c:otherwise>
                                    </c:choose>
                                </span>
                            </c:if>
                            <c:if test="${not empty param.sortBy}">
                                <span class="badge bg-success me-1">Sắp xếp: 
                                    <c:choose>
                                        <c:when test="${param.sortBy == 'title'}">Tên công việc</c:when>
                                        <c:when test="${param.sortBy == 'startDate'}">Ngày bắt đầu</c:when>
                                        <c:when test="${param.sortBy == 'endDate'}">Ngày kết thúc</c:when>
                                        <c:when test="${param.sortBy == 'status'}">Trạng thái</c:when>
                                        <c:otherwise>Ngày tạo</c:otherwise>
                                    </c:choose>
                                    (${param.sortOrder == 'asc' ? 'Cũ → Mới' : 'Mới → Cũ'})
                                </span>
                            </c:if>
                        </div>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${empty assignedTasks}">
                                <div class="text-center py-5">
                                    <i class="fas fa-clipboard-list fa-3x text-muted mb-3"></i>
                                    <h5 class="text-muted">Chưa có công việc nào</h5>
                                    <p class="text-muted">Hãy tạo công việc mới để bắt đầu quản lý</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead class="table-light">
                                            <tr>
                                                <th>
                                                    <a href="javascript:void(0)" onclick="sortBy('title')" class="text-decoration-none text-dark">
                                                        Tên công việc 
                                                        <i class="fas fa-sort ${param.sortBy == 'title' ? (param.sortOrder == 'asc' ? 'fa-sort-up' : 'fa-sort-down') : ''}"></i>
                                                    </a>
                                                </th>
                                                <th>Phụ trách</th>
                                                <th>
                                                    <a href="javascript:void(0)" onclick="sortBy('startDate')" class="text-decoration-none text-dark">
                                                        Thời gian 
                                                        <i class="fas fa-sort ${param.sortBy == 'startDate' ? (param.sortOrder == 'asc' ? 'fa-sort-up' : 'fa-sort-down') : ''}"></i>
                                                    </a>
                                                </th>
                                                <th>
                                                    <a href="javascript:void(0)" onclick="sortBy('status')" class="text-decoration-none text-dark">
                                                        Trạng thái 
                                                        <i class="fas fa-sort ${param.sortBy == 'status' ? (param.sortOrder == 'asc' ? 'fa-sort-up' : 'fa-sort-down') : ''}"></i>
                                                    </a>
                                                </th>
                                                <th>
                                                    <a href="javascript:void(0)" onclick="sortBy('createdAt')" class="text-decoration-none text-dark">
                                                        Người tạo 
                                                        <i class="fas fa-sort ${param.sortBy == 'createdAt' ? (param.sortOrder == 'asc' ? 'fa-sort-up' : 'fa-sort-down') : ''}"></i>
                                                    </a>
                                                </th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="task" items="${assignedTasks}">
                                                <tr>
                                                    <td>
                                                        <div class="fw-bold">${task.title}</div>
                                                        <small class="text-muted">
                                                            <c:if test="${not empty task.description}">
                                                                ${task.description.length() > 50 ? task.description.substring(0, 50).concat('...') : task.description}
                                                            </c:if>
                                                        </small>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty task.userAssignee}">
                                                                <div class="d-flex align-items-center">
                                                                    <i class="fas fa-user-circle me-2 text-primary"></i>
                                                                    <span>${task.userAssignee.fullName}</span>
                                                                </div>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-muted">Chưa giao</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <div>
                                                            <strong>Bắt đầu:</strong> 
                                                            <c:choose>
                                                                <c:when test="${not empty task.startDate}">
                                                                    <fmt:formatDate value="${task.startDate}" pattern="dd/MM/yyyy HH:mm"/>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="text-muted">Chưa xác định</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </div>
                                                        <div>
                                                            <strong>Kết thúc:</strong> 
                                                            <c:choose>
                                                                <c:when test="${not empty task.endDate}">
                                                                    <fmt:formatDate value="${task.endDate}" pattern="dd/MM/yyyy HH:mm"/>
                                                                    <c:set var="now" value="<%= new java.util.Date() %>" />
                                                                    <c:if test="${task.endDate.time < now.time && task.status != 'Done'}">
                                                                        <br><small class="text-danger"><i class="fas fa-exclamation-triangle"></i> Quá hạn</small>
                                                                    </c:if>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="text-muted">Chưa xác định</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <span class="badge ${task.status == 'Done' ? 'bg-success' : 
                                                                            task.status == 'InProgress' ? 'bg-primary' : 
                                                                            task.status == 'Review' ? 'bg-warning' : 
                                                                            task.status == 'Rejected' ? 'bg-danger' : 'bg-secondary'}">
                                                            <c:choose>
                                                                <c:when test="${task.status == 'ToDo'}">Chưa bắt đầu</c:when>
                                                                <c:when test="${task.status == 'InProgress'}">Đang thực hiện</c:when>
                                                                <c:when test="${task.status == 'Review'}">Chờ duyệt</c:when>
                                                                <c:when test="${task.status == 'Done'}">Hoàn thành</c:when>
                                                                <c:when test="${task.status == 'Rejected'}">Từ chối</c:when>
                                                                <c:otherwise>${task.status}</c:otherwise>
                                                            </c:choose>
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <c:if test="${not empty task.createdBy}">
                                                            ${task.createdBy.fullName}
                                                        </c:if>
                                                        <c:if test="${not empty task.createdAt}">
                                                            <br><small class="text-muted">
                                                                <fmt:formatDate value="${task.createdAt}" pattern="dd/MM/yyyy"/>
                                                            </small>
                                                        </c:if>
                                                    </td>
                                                    <td>
                                                        <div class="btn-group btn-group-sm" role="group">
                                                            <button type="button" class="btn btn-outline-primary" 
                                                                    onclick="viewTaskDetail(${task.taskID})" 
                                                                    title="Xem chi tiết">
                                                                <i class="fas fa-eye"></i>
                                                            </button>
                                                            <button type="button" class="btn btn-outline-success" 
                                                                    onclick="editTask(${task.taskID})" 
                                                                    title="Chỉnh sửa">
                                                                <i class="fas fa-edit"></i>
                                                            </button>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </main>
        </div>

        <!-- Create Task Modal -->
        <div class="modal fade" id="createTaskModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Tạo công việc mới</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <form id="createTaskForm">
                        <div class="modal-body">
                            <input type="hidden" name="clubID" value="${clubID}">
                            <input type="hidden" name="createdBy" value="${currentUser.userID}">
                            
                            <div class="row">
                                <div class="col-md-12 mb-3">
                                    <label for="taskTitle" class="form-label">Tên công việc <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="taskTitle" name="title" required>
                                </div>
                                
                                <div class="col-md-12 mb-3">
                                    <label for="taskDescription" class="form-label">Mô tả công việc</label>
                                    <textarea class="form-control" id="taskDescription" name="description" rows="3"></textarea>
                                </div>
                                
                                <div class="col-md-6 mb-3">
                                    <label for="assigneeType" class="form-label">Giao việc cho <span class="text-danger">*</span></label>
                                    <select class="form-select" id="assigneeType" name="assigneeType" required onchange="toggleAssigneeFields()">
                                        <option value="">Chọn...</option>
                                        <option value="Department">Ban</option>
                                        <option value="User">Cá nhân</option>
                                    </select>
                                </div>
                                
                                <div class="col-md-6 mb-3" id="departmentField" style="display: none;">
                                    <label for="departmentID" class="form-label">Chọn ban</label>
                                    <select class="form-select" id="departmentID" name="departmentID">
                                        <option value="">Chọn ban...</option>
                                        <c:forEach var="dept" items="${clubDepartments}">
                                            <option value="${dept.departmentID}">${dept.departmentName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                
                                <div class="col-md-6 mb-3" id="userField" style="display: none;">
                                    <label for="userID" class="form-label">Chọn thành viên</label>
                                    <select class="form-select" id="userID" name="userID">
                                        <option value="">Chọn thành viên...</option>
                                        <!-- This will be populated via AJAX based on selected department -->
                                    </select>
                                </div>
                                
                                <div class="col-md-6 mb-3">
                                    <label for="startDate" class="form-label">Ngày bắt đầu</label>
                                    <input type="datetime-local" class="form-control" id="startDate" name="startDate">
                                </div>
                                
                                <div class="col-md-6 mb-3">
                                    <label for="endDate" class="form-label">Ngày kết thúc</label>
                                    <input type="datetime-local" class="form-control" id="endDate" name="endDate">
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> Tạo công việc
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Task Detail Modal -->
        <div class="modal fade" id="taskDetailModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Chi tiết công việc</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body" id="taskDetailContent">
                        <!-- Task detail content will be loaded here -->
                    </div>
                </div>
            </div>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        
        <script>
            // Show create task modal
            function showCreateTaskModal() {
                new bootstrap.Modal(document.getElementById('createTaskModal')).show();
            }

            // Toggle assignee fields based on type
            function toggleAssigneeFields() {
                const assigneeType = document.getElementById('assigneeType').value;
                const departmentField = document.getElementById('departmentField');
                const userField = document.getElementById('userField');
                
                if (assigneeType === 'Department') {
                    departmentField.style.display = 'block';
                    userField.style.display = 'none';
                    document.getElementById('departmentID').required = true;
                    document.getElementById('userID').required = false;
                } else if (assigneeType === 'User') {
                    departmentField.style.display = 'none';
                    userField.style.display = 'block';
                    document.getElementById('departmentID').required = false;
                    document.getElementById('userID').required = true;
                } else {
                    departmentField.style.display = 'none';
                    userField.style.display = 'none';
                    document.getElementById('departmentID').required = false;
                    document.getElementById('userID').required = false;
                }
            }

            // View task detail
            function viewTaskDetail(taskId) {
                fetch('${pageContext.request.contextPath}/task-detail?taskId=' + taskId)
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            displayTaskDetail(data.task);
                            new bootstrap.Modal(document.getElementById('taskDetailModal')).show();
                        } else {
                            showNotification(data.message || 'Không tìm thấy thông tin công việc', 'error');
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        showNotification('Có lỗi xảy ra khi tải chi tiết công việc', 'error');
                    });
            }

            // Display task detail in modal
            function displayTaskDetail(task) {
                const content = document.getElementById('taskDetailContent');
                content.innerHTML = `
                    <div class="row">
                        <div class="col-md-12 mb-3">
                            <h6>Thông tin cơ bản</h6>
                            <p><strong>Tên công việc:</strong> \${task.title}</p>
                            <p><strong>Mô tả:</strong> \${task.description || 'Không có mô tả'}</p>
                            <p><strong>Trạng thái:</strong> 
                                <span class="badge bg-primary">\${task.status}</span>
                            </p>
                        </div>
                        <div class="col-md-6">
                            <h6>Thời gian</h6>
                            <p><strong>Bắt đầu:</strong> \${task.startDate ? new Date(task.startDate).toLocaleString('vi-VN') : 'Chưa xác định'}</p>
                            <p><strong>Kết thúc:</strong> \${task.endDate ? new Date(task.endDate).toLocaleString('vi-VN') : 'Chưa xác định'}</p>
                            <p><strong>Ngày tạo:</strong> \${task.createdAt ? new Date(task.createdAt).toLocaleString('vi-VN') : 'N/A'}</p>
                        </div>
                        <div class="col-md-6">
                            <h6>Phân công</h6>
                            <p><strong>Người tạo:</strong> \${task.createdBy ? task.createdBy.fullName : 'N/A'}</p>
                            <p><strong>Phụ trách:</strong> 
                                \${task.assigneeType === 'Department' ? 
                                    (task.departmentAssignee ? task.departmentAssignee.departmentName : 'Chưa giao') :
                                    (task.userAssignee ? task.userAssignee.fullName : 'Chưa giao')
                                }
                            </p>
                        </div>
                    </div>
                `;
            }

            // Edit task
            function editTask(taskId) {
                // Implementation for edit task functionality
                showNotification('Chức năng chỉnh sửa đang được phát triển', 'info');
            }

            // Handle create task form submission
            document.getElementById('createTaskForm').addEventListener('submit', function(e) {
                e.preventDefault();
                
                const formData = new FormData(this);
                const submitBtn = this.querySelector('button[type="submit"]');
                
                // Show loading
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang tạo...';
                submitBtn.disabled = true;
                
                fetch('${pageContext.request.contextPath}/create-task', {
                    method: 'POST',
                    body: formData
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        showNotification('Tạo công việc thành công!', 'success');
                        bootstrap.Modal.getInstance(document.getElementById('createTaskModal')).hide();
                        setTimeout(() => location.reload(), 1500);
                    } else {
                        showNotification(data.message || 'Tạo công việc thất bại', 'error');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showNotification('Có lỗi xảy ra khi tạo công việc', 'error');
                })
                .finally(() => {
                    // Reset button
                    submitBtn.innerHTML = '<i class="fas fa-save"></i> Tạo công việc';
                    submitBtn.disabled = false;
                });
            });

            // Sorting function
            function sortBy(column) {
                const urlParams = new URLSearchParams(window.location.search);
                const currentSort = urlParams.get('sortBy');
                const currentOrder = urlParams.get('sortOrder') || 'desc';
                
                // Toggle order if same column, otherwise default to desc
                const newOrder = (currentSort === column && currentOrder === 'desc') ? 'asc' : 'desc';
                
                urlParams.set('sortBy', column);
                urlParams.set('sortOrder', newOrder);
                
                window.location.search = urlParams.toString();
            }

            // Clear all filters
            function clearFilters() {
                const clubID = new URLSearchParams(window.location.search).get('clubID');
                window.location.href = window.location.pathname + '?clubID=' + clubID;
            }

            // Real-time search functionality
            let searchTimeout;
            document.getElementById('searchKeyword').addEventListener('input', function() {
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(() => {
                    document.getElementById('filterForm').submit();
                }, 500); // Delay 500ms after user stops typing
            });

            // Auto-submit form when filter dropdowns change
            document.getElementById('statusFilter').addEventListener('change', function() {
                document.getElementById('filterForm').submit();
            });
            
            document.getElementById('sortBy').addEventListener('change', function() {
                document.getElementById('filterForm').submit();
            });
            
            document.getElementById('sortOrder').addEventListener('change', function() {
                document.getElementById('filterForm').submit();
            });

            // Notification function
            function showNotification(message, type = 'info') {
                const notification = document.createElement('div');
                notification.className = `alert alert-\${type === 'error' ? 'danger' : type} alert-dismissible fade show position-fixed`;
                notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
                notification.innerHTML = `
                    \${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                `;
                
                document.body.appendChild(notification);
                
                setTimeout(() => {
                    if (notification.parentNode) {
                        notification.remove();
                    }
                }, 5000);
            }
        </script>
    </body>
</html>
