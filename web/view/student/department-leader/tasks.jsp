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
        <!-- Select2 CSS -->
        <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
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
            .select2-container .select2-selection--single {
                height: 38px;
                border: 1px solid #ced4da;
                border-radius: 0.375rem;
            }
            .select2-container--default .select2-selection--single .select2-selection__rendered {
                line-height: 36px;
                padding-left: 12px;
            }
            .select2-container--default .select2-selection--single .select2-selection__arrow {
                height: 36px;
            }
            .member-item {
                display: flex;
                align-items: center;
                padding: 8px 0;
            }
            .member-avatar {
                width: 32px;
                height: 32px;
                border-radius: 50%;
                margin-right: 10px;
                object-fit: cover;
            }

            /* Autocomplete dropdown styling */
            #memberDropdown {
                border: 1px solid #ced4da;
                border-radius: 0.375rem;
                box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
                background: white;
            }

            #memberDropdown .dropdown-item {
                padding: 8px 12px;
                border-bottom: 1px solid #f8f9fa;
            }

            #memberDropdown .dropdown-item:hover {
                background-color: #f8f9fa;
            }

            #memberDropdown .dropdown-item:last-child {
                border-bottom: none;
            }

            #selectedMemberDisplay {
                animation: fadeIn 0.3s ease-in-out;
            }

            @keyframes fadeIn {
                from {
                    opacity: 0;
                    transform: translateY(-5px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
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
                            <i class="fas fa-plus"></i> Tạo nhiệm vụ mới
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
                                                            <c:choose>
                                                                <c:when test="${task.status == 'Review'}">
                                                                    <button type="button"
                                                                            class="btn btn-danger btn-sm"
                                                                            onclick="showRejectModal('${task.taskID}')">
                                                                        <i class="fas fa-times"></i> Từ chối
                                                                    </button>
                                                                    <button type="button"
                                                                            class="btn btn-success btn-sm"
                                                                            onclick="showApproveModal('${task.taskID}')">
                                                                        <i class="fas fa-times"></i> Duyệt
                                                                    </button>

                                                                </c:when>
                                                            </c:choose>
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

        <!-- Modal Bootstrap: Nhập lý do từ chối -->
        <div class="modal fade" id="rejectReasonModal" tabindex="-1" aria-labelledby="rejectReasonModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <form action="${pageContext.request.contextPath}/department-tasks" method="get" class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="rejectReasonModalLabel">Lý do từ chối</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" name="clubID" value="${param.clubID}">
                        <input type="hidden" name="action" value="rejectTask">
                        <input type="hidden" name="taskID" id="rejectClubID">

                        <div class="mb-3">
                            <label for="reason" class="form-label">Nhập lý do từ chối:</label>
                            <textarea class="form-control" name="reason" id="reason" rows="4" required></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-danger">Từ chối</button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    </div>
                </form>
            </div>
        </div>

        <div class="modal fade" id="approveTaskModal" tabindex="-1" aria-labelledby="approveTaskModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <form action="${pageContext.request.contextPath}/department-tasks" method="get" class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="approveTaskModalLabel">Duyệt Task</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                    </div>

                    <div class="modal-body">
                        <input type="hidden" name="action" value="approveTask">
                        <input type="hidden" name="taskID" id="approveTaskID">

                        <div class="mb-3">
                            <label for="rating" class="form-label">Chọn chất lượng:</label>
                            <select class="form-select form-select-sm" name="rating" id="rating" required>
                                <option value="">-- Chọn --</option>
                                <option value="Positive">👍 Tốt</option>
                                <option value="Neutral">😐 Trung bình</option>
                                <option value="Negative">👎 Kém</option>
                            </select>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="submit" class="btn btn-success">Duyệt</button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    </div>
                </form>
            </div>
        </div>


        <!-- Create Task Modal -->
        <div class="modal fade" id="createTaskModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">
                            <i class="fas fa-tasks me-2"></i>Tạo nhiệm vụ mới
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <form id="createTaskForm">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="createTask">
                            <input type="hidden" name="clubID" value="${param.clubID}">
                            <input type="hidden" name="assigneeType" value="User">

                            <div class="row">
                                <div class="col-md-12 mb-3">
                                    <label for="taskTitle" class="form-label">Tiêu đề nhiệm vụ <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="taskTitle" name="title" required maxlength="200" 
                                           placeholder="Nhập tiêu đề nhiệm vụ...">
                                </div>

                                <div class="col-md-12 mb-3">
                                    <label for="taskDescription" class="form-label">Mô tả nhiệm vụ <span class="text-danger">*</span></label>
                                    <textarea class="form-control" id="taskDescription" name="description" rows="4" required 
                                              maxlength="1000" placeholder="Mô tả chi tiết nhiệm vụ cần thực hiện..."></textarea>
                                    <div class="form-text">Tối đa 1000 ký tự</div>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="startDate" class="form-label">Ngày bắt đầu <span class="text-danger">*</span></label>
                                    <input type="date" class="form-control" id="startDate" name="startDate" required>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="endDate" class="form-label">Ngày kết thúc <span class="text-danger">*</span></label>
                                    <input type="date" class="form-control" id="endDate" name="endDate" required>
                                </div>

                                <div class="col-md-12 mb-3">
                                    <label for="eventId" class="form-label">Sự kiện liên quan</label>
                                    <select class="form-select" id="eventId" name="eventId" onchange="loadDepartmentTasks()">
                                        <option value="">-- Không liên quan đến sự kiện cụ thể --</option>
                                        <c:forEach var="event" items="${clubEvents}">
                                            <option value="${event.eventID}">${event.eventName}</option>
                                        </c:forEach>
                                    </select>
                                    <div class="form-text">Có thể để trống nếu công việc không thuộc về sự kiện cụ thể nào</div>
                                </div>

                                <div class="col-md-12 mb-3">
                                    <label for="parentTaskId" class="form-label">Nhiệm vụ Ban</label>
                                    <select class="form-select" id="parentTaskId" name="parentTaskId" disabled>
                                        <option value="">-- Chọn sự kiện trước để xem nhiệm vụ ban --</option>
                                    </select>
                                    <div class="form-text">Chọn nhiệm vụ ban để tạo nhiệm vụ con cho thành viên thực hiện</div>
                                </div>

                                <div class="col-md-12 mb-3">
                                    <label for="assignedTo" class="form-label">Người phụ trách <span class="text-danger">*</span></label>
                                    <div class="position-relative">
                                        <input type="text" class="form-control" id="memberSearchInput" 
                                               placeholder="Tìm kiếm thành viên theo tên hoặc email..."
                                               autocomplete="off" required>
                                        <input type="hidden" id="assignedTo" name="assigneeId" required>
                                        <div id="memberDropdown" class="dropdown-menu position-absolute w-100" style="display: none; max-height: 250px; overflow-y: auto; z-index: 1050;">
                                            <!-- Search results will appear here -->
                                        </div>
                                        <div id="selectedMemberDisplay" class="mt-2 p-2 border rounded bg-light" style="display: none;">
                                            <div class="d-flex align-items-center">
                                                <img id="selectedAvatar" src="" class="rounded-circle me-2" style="width: 32px; height: 32px; object-fit: cover;">
                                                <div class="flex-grow-1">
                                                    <div class="fw-semibold" id="selectedName"></div>
                                                    <small class="text-muted" id="selectedEmail"></small>
                                                </div>
                                                <button type="button" class="btn btn-sm btn-outline-danger" onclick="clearSelectedMember()">
                                                    <i class="fas fa-times"></i>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="form-text">Tìm kiếm và chọn thành viên trong ban để giao nhiệm vụ</div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> Tạo nhiệm vụ
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
        <script>
            function showRejectModal(clubID) {
            // Gán dữ liệu vào các hidden input
            document.getElementById("rejectClubID").value = clubID;
            // Hiện modal Bootstrap
            const modal = new bootstrap.Modal(document.getElementById('rejectReasonModal'));
            modal.show();
            }
        </script>
        <script>
            function showApproveModal(taskID) {
            // Gán dữ liệu vào hidden input
            document.getElementById("approveTaskID").value = taskID;
            // Hiện modal Bootstrap
            const modal = new bootstrap.Modal(document.getElementById('approveTaskModal'));
            modal.show();
            }
        </script>


        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <!-- jQuery -->
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <!-- Select2 JS -->
        <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>

        <script>
            $(document).ready(function() {
            // Debug: Check if members are available
            console.log('Initializing Select2...');
            var memberOptions = $('.member-select option').length;
            console.log('Found ' + memberOptions + ' member options');
            // Khởi tạo Select2 cho dropdown thành viên
            $('.member-select').select2({
            placeholder: "Tìm kiếm thành viên...",
                    allowClear: true,
                    dropdownParent: $('#createTaskModal'),
                    templateResult: function (member) {
                    if (!member.id) {
                    return member.text;
                    }

                    var avatar = $(member.element).data('avatar');
                    var email = $(member.element).data('email');
                    var $member = $(
                            '<div class="member-item">' +
                            '<img class="member-avatar" src="' + (avatar || 'img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg') + '" alt="Avatar">' +
                            '<div>' +
                            '<div style="font-weight: 500;">' + member.text.split(' (')[0] + '</div>' +
                            '<div style="font-size: 0.85em; color: #6c757d;">' + email + '</div>' +
                            '</div>' +
                            '</div>'
                            );
                    return $member;
                    },
                    templateSelection: function (member) {
                    if (!member.id) {
                    return member.text;
                    }
                    return member.text.split(' (')[0];
                    }
            });
            // Set ngày tối thiểu là hôm nay
            var today = new Date().toISOString().split('T')[0];
            $('#startDate').attr('min', today);
            $('#startDate').on('change', function() {
            $('#endDate').attr('min', this.value);
            });
            // Validation ngày
            $('#startDate, #endDate').on('change', function() {
            var startDate = new Date($('#startDate').val());
            var endDate = new Date($('#endDate').val());
            if (startDate && endDate && endDate < startDate) {
            $('#endDate')[0].setCustomValidity('Ngày kết thúc phải sau ngày bắt đầu');
            } else {
            $('#endDate')[0].setCustomValidity('');
            }
            });
            });
            // Show create task modal
            function showCreateTaskModal() {
            console.log('Opening modal...');
            var eventOptions = $('#eventId option').length;
            console.log('Event options: ' + eventOptions);
            // Initialize member autocomplete
            initializeMemberAutocomplete();
            console.log('Member autocomplete initialized');
            
            // Reset form
            document.getElementById('createTaskForm').reset();
            clearSelectedMember();
            
            // Reset department tasks dropdown
            const parentTaskSelect = document.getElementById('parentTaskId');
            parentTaskSelect.innerHTML = '<option value="">-- Chọn sự kiện trước để xem nhiệm vụ ban --</option>';
            parentTaskSelect.disabled = true;
            
            new bootstrap.Modal(document.getElementById('createTaskModal')).show();
            }

            // Load department tasks based on selected event
            function loadDepartmentTasks() {
            const eventId = document.getElementById('eventId').value;
            const parentTaskSelect = document.getElementById('parentTaskId');
            const clubID = new URLSearchParams(window.location.search).get('clubID');
            
            if (!eventId) {
            parentTaskSelect.innerHTML = '<option value="">-- Chọn sự kiện trước để xem nhiệm vụ ban --</option>';
            parentTaskSelect.disabled = true;
            return;
            }
            
            // Show loading
            parentTaskSelect.innerHTML = '<option value="">-- Đang tải nhiệm vụ ban... --</option>';
            parentTaskSelect.disabled = true;
            
            // Fetch department tasks
            fetch('${pageContext.request.contextPath}/department-tasks?action=getDepartmentTasks&clubID=' + clubID + '&eventId=' + eventId)
            .then(response => response.json())
            .then(data => {
            if (data.error) {
            parentTaskSelect.innerHTML = '<option value="">-- Lỗi: ' + data.error + ' --</option>';
            return;
            }
            
            // Populate options
            let html = '<option value="">-- Không chọn nhiệm vụ ban (tạo nhiệm vụ độc lập) --</option>';
            
            if (data.tasks && data.tasks.length > 0) {
            data.tasks.forEach(task => {
            const statusText = getStatusText(task.status);
            const termInfo = task.termName ? ' (' + task.termName + ')' : '';
            const deptInfo = task.departmentName ? ' - ' + task.departmentName : '';
            
            html += `<option value="\${task.taskID}">
            \${task.title} [\${statusText}]\${termInfo}\${deptInfo}
            </option>`;
            });
            } else {
            html += '<option value="" disabled>-- Chưa có nhiệm vụ ban nào cho sự kiện này --</option>';
            }
            
            parentTaskSelect.innerHTML = html;
            parentTaskSelect.disabled = false;
            })
            .catch(error => {
            console.error('Error loading department tasks:', error);
            parentTaskSelect.innerHTML = '<option value="">-- Lỗi kết nối --</option>';
            });
            }

            // Helper function to get status text
            function getStatusText(status) {
            switch(status) {
            case 'pending': return 'Chờ xử lý';
            case 'in_progress': return 'Đang thực hiện';
            case 'completed': return 'Hoàn thành';
            case 'cancelled': return 'Đã hủy';
            default: return 'Không xác định';
            }
            }

            // Initialize member autocomplete functionality
            function initializeMemberAutocomplete() {
            const searchInput = $('#memberSearchInput');
            const dropdown = $('#memberDropdown');
            const hiddenInput = $('#assignedTo');
            let searchTimeout;
            // Clear previous selection
            clearSelectedMember();
            searchInput.on('input', function() {
            const query = $(this).val().trim();
            clearTimeout(searchTimeout);
            if (query.length < 2) {
            dropdown.hide();
            return;
            }

            // Debounce search
            searchTimeout = setTimeout(() => {
            searchMembers(query);
            }, 300);
            });
            // Hide dropdown when clicking outside
            $(document).on('click', function(e) {
            if (!$(e.target).closest('#memberSearchInput, #memberDropdown').length) {
            dropdown.hide();
            }
            });
            // Show dropdown when input is focused and has value
            searchInput.on('focus', function() {
            if ($(this).val().trim().length >= 2 && dropdown.children().length > 0) {
            dropdown.show();
            }
            });
            }

            // Search members via Ajax
            function searchMembers(query) {
            const dropdown = $('#memberDropdown');
            const clubID = new URLSearchParams(window.location.search).get('clubID');
            // Show loading
            dropdown.html('<div class="dropdown-item text-center"><i class="fas fa-spinner fa-spin"></i> Đang tìm kiếm...</div>').show();
            $.ajax({
            url: '${pageContext.request.contextPath}/department-tasks',
                    method: 'GET',
                    data: {
                    action: 'searchMembers',
                            clubID: clubID,
                            q: query
                    },
                    dataType: 'json',
                    success: function(response) {
                    if (response.error) {
                    dropdown.html('<div class="dropdown-item text-danger"><i class="fas fa-exclamation-triangle"></i> ' + response.error + '</div>');
                    return;
                    }

                    if (response.results && response.results.length > 0) {
                    let html = '';
                    response.results.forEach(function(member) {
                    const avatar = member.avatar ?
                            '${pageContext.request.contextPath}/img/' + member.avatar :
                            '${pageContext.request.contextPath}/img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg';
                    html += '<div class="dropdown-item member-option" style="cursor: pointer;" ' +
                            'data-id="' + member.id + '" ' +
                            'data-name="' + member.fullName + '" ' +
                            'data-email="' + member.email + '" ' +
                            'data-avatar="' + avatar + '">' +
                            '<div class="d-flex align-items-center">' +
                            '<img src="' + avatar + '" class="rounded-circle me-2" style="width: 32px; height: 32px; object-fit: cover;">' +
                            '<div>' +
                            '<div class="fw-semibold">' + member.fullName + '</div>' +
                            '<small class="text-muted">' + member.email + '</small>' +
                            '</div>' +
                            '</div>' +
                            '</div>';
                    });
                    dropdown.html(html);
                    // Add click handlers for member options
                    $('.member-option').on('click', function() {
                    const memberId = $(this).data('id');
                    const memberName = $(this).data('name');
                    const memberEmail = $(this).data('email');
                    const memberAvatar = $(this).data('avatar');
                    selectMember(memberId, memberName, memberEmail, memberAvatar);
                    });
                    } else {
                    dropdown.html('<div class="dropdown-item text-muted"><i class="fas fa-search"></i> Không tìm thấy thành viên nào</div>');
                    }
                    },
                    error: function() {
                    dropdown.html('<div class="dropdown-item text-danger"><i class="fas fa-exclamation-triangle"></i> Lỗi kết nối</div>');
                    }
            });
            }

            // Select a member
            function selectMember(id, name, email, avatar) {
            $('#assignedTo').val(id);
            $('#memberSearchInput').val(name);
            $('#memberDropdown').hide();
            // Show selected member display
            $('#selectedAvatar').attr('src', avatar);
            $('#selectedName').text(name);
            $('#selectedEmail').text(email);
            $('#selectedMemberDisplay').show();
            $('#memberSearchInput').hide();
            }

            // Clear selected member
            function clearSelectedMember() {
            $('#assignedTo').val('');
            $('#memberSearchInput').val('').show();
            $('#selectedMemberDisplay').hide();
            $('#memberDropdown').hide();
            }

            // Handle create task form submission
            document.getElementById('createTaskForm').addEventListener('submit', function(e) {
            e.preventDefault();
            // Validation
            var title = document.getElementById('taskTitle').value.trim();
            var description = document.getElementById('taskDescription').value.trim();
            var startDate = document.getElementById('startDate').value;
            var endDate = document.getElementById('endDate').value;
            var eventId = document.getElementById('eventId').value; // Event is now optional
            var assignedTo = document.getElementById('assignedTo').value;
            var parentTaskId = document.getElementById('parentTaskId').value; // Get parent task ID
            
            // Debug log
            console.log('Form submission data:', {
                title, description, startDate, endDate, eventId, assignedTo, parentTaskId
            });
            
            if (!title || !description || !startDate || !endDate || !assignedTo) {
            showNotification('Vui lòng điền đầy đủ thông tin bắt buộc!', 'error');
            return false;
            }

            if (new Date(endDate) < new Date(startDate)) {
            showNotification('Ngày kết thúc phải sau ngày bắt đầu!', 'error');
            return false;
            }

            // Use traditional form submission instead of fetch
            const form = this;
            const submitBtn = form.querySelector('button[type="submit"]');
            // Show loading
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang tạo...';
            submitBtn.disabled = true;
            // Submit form traditionally
            form.action = '${pageContext.request.contextPath}/department-tasks';
            form.method = 'POST';
            form.submit();
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

            // View task detail function
            function viewTaskDetail(taskId) {
            console.log('Viewing task detail for ID:', taskId);
            // Show loading in modal
            const modal = new bootstrap.Modal(document.getElementById('taskDetailModal'));
            const content = document.getElementById('taskDetailContent');
            content.innerHTML = '<div class="text-center py-4"><i class="fas fa-spinner fa-spin fa-2x"></i><br>Đang tải...</div>';
            modal.show();
            // Load task details via AJAX
            const clubID = new URLSearchParams(window.location.search).get('clubID');
            $.ajax({
            url: '${pageContext.request.contextPath}/department-tasks',
                    method: 'GET',
                    data: {
                    action: 'getTaskDetail',
                            taskId: taskId,
                            clubID: clubID
                    },
                    dataType: 'json',
                    success: function(response) {
                    console.log('Task detail response:', response); // Debug log

                    if (response.error) {
                    content.innerHTML = `
                                <div class="alert alert-danger">
                                    <i class="fas fa-exclamation-triangle"></i>
                                    Lỗi: \${response.error}
                                </div>
                            `;
                    return;
                    }

                    // Build task detail content
                    const statusBadgeClass = getStatusBadgeClass(response.status);
                    // Check assignee info with proper null/undefined handling
                    let assigneeInfo;
                    if (response.assignee && response.assignee.fullName) {
                    const avatarSrc = response.assignee.avatar ?
                            '${pageContext.request.contextPath}/img/' + response.assignee.avatar :
                            '${pageContext.request.contextPath}/img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg';
                    assigneeInfo = `
                                <div class="d-flex align-items-center">
                                    <img src="\${avatarSrc}" 
                                         class="rounded-circle me-2" style="width: 32px; height: 32px; object-fit: cover;">
                                    <div>
                                        <div class="fw-semibold">\${response.assignee.fullName}</div>
                                        <small class="text-muted">\${response.assignee.email || ''}</small>
                                    </div>
                                </div>
                            `;
                    } else {
                    assigneeInfo = '<span class="text-muted">Chưa giao</span>';
                    }

                    content.innerHTML = `
                            <div class="row">
                                <div class="col-md-8">
                                    <h5 class="fw-bold mb-3">\${response.title}</h5>
                                    
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Mô tả:</label>
                                        <div class="p-3 bg-light rounded">
                                            \${response.description || '<span class="text-muted">Không có mô tả</span>'}
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Người phụ trách:</label>
                                        <div>
                                            \${assigneeInfo}
                                        </div>
                                    </div>
                                    
                                    \${response.event ? `
                            <div class="mb-3">
                                                            <label class="form-label fw-semibold">Sự kiện liên quan:</label>
                                                                <div class="p-2 bg-info bg-opacity-10 rounded">
                                                                    <i class="fas fa-calendar-alt me-2"></i>
                                                                    \${response.event.eventName}
        </div>
                                                                    </div>
                            ` : ''}
                                </div>
                                
                                <div class="col-md-4">
                                    <div class="card">
                                        <div class="card-header">
                                            <h6 class="mb-0">Thông tin chi tiết</h6>
                                        </div>
                                        <div class="card-body">
                                            <div class="mb-3">
                                                <label class="form-label fw-semibold">Trạng thái:</label>
                                                <div>
                                                    <span class="badge \${statusBadgeClass}">\${response.statusText}</span>
                                                </div>
                                            </div>
                                            
                                            \${response.startDate ? `
                            <div class="mb-3">
                                                                             <label class="form-label fw-semibold">Ngày bắt đầu:</label>
                                                                             <div class="text-muted">
                                                                             <i class="fas fa-calendar-start me-2"></i>
                                                                  \${response.startDate}
                                                                  </div>
                                                                  </div>
                            ` : ''}
                                            
                                            \${response.endDate ? `
                            <div class="mb-3">
                                                                  <label class="form-label fw-semibold">Ngày kết thúc:</label>
                                                                  <div class="text-muted">
                                                                  <i class="fas fa-calendar-end me-2"></i>
                                                              \${response.endDate}
                                                        </div>
                                                    </div>
                            ` : ''}
                                            
                                            \${response.creator ? `
                            <div class="mb-3">
                                                            <label class="form-label fw-semibold">Người tạo:</label>
                                                        <div class="text-muted">
                                                            <i class="fas fa-user me-2"></i>
                                                                \${response.creator.fullName}
                                                                </div>
                                                            </div>
                            ` : ''}
                                            
                                            \${response.createdAt ? `
                            <div class="mb-3">
        <label class="form-label fw-semibold">Ngày tạo:</label>
        <div class="text-muted">
                                                                    <i class="fas fa-clock me-2"></i>
                                                                    \${response.createdAt}
        </div>
                                                            </div>
                            ` : ''}
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="mt-4 text-center">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                                <button type="button" class="btn btn-primary ms-2" onclick="editTask(\${taskId})">
                                    <i class="fas fa-edit"></i> Chỉnh sửa
                                </button>
                            </div>
                        `;
                    },
                    error: function() {
                    content.innerHTML = `
                            <div class="alert alert-danger">
                                <i class="fas fa-exclamation-triangle"></i>
                                Không thể tải thông tin chi tiết. Vui lòng thử lại sau.
                            </div>
                        `;
                    }
            });
            }

            // Helper function to get status badge class
            function getStatusBadgeClass(status) {
            switch (status) {
            case 'Done': return 'bg-success';
            case 'InProgress': return 'bg-primary';
            case 'Review': return 'bg-warning';
            case 'Rejected': return 'bg-danger';
            case 'ToDo':
                    default: return 'bg-secondary';
            }
            }

            // Edit task function
            function editTask(taskId) {
            console.log('Editing task ID:', taskId);
            showNotification('Chức năng chỉnh sửa nhiệm vụ đang được phát triển.', 'info');
            // TODO: Implement edit task functionality
            // This would typically:
            // 1. Load task data via AJAX
            // 2. Populate edit form
            // 3. Show edit modal
            // 4. Handle form submission for updates
            }
        </script>
        
        <script>
            function showApprove(taskId) {
            document.getElementById(`approve-${taskId}`).classList.add('show');
            document.getElementById(`reject-${taskId}`).classList.remove('show');
            }

            function showReject(taskId) {
            document.getElementById(`reject-${taskId}`).classList.add('show');
            document.getElementById(`approve-${taskId}`).classList.remove('show');
            }
</script>

</body>
</html>
