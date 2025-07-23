<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Kế hoạch & Sự kiện CLB</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
    </head>
    <body>
        <div class="department-leader-container">
            <!-- Sidebar -->
            <c:set var="activePage" value="plan-events" />
            <%@ include file="components/sidebar.jsp" %>

            <!-- Main Content -->
            <main class="main-content">
                <div class="plan-events-container">
                    <div class="container-fluid">
                        <!-- Header -->
                        <div class="content-header">
                            <div class="d-flex justify-content-between align-items-center">
                                <div>
                                    <h1 class="h3 mb-1">
                                        <i class="fas fa-calendar-check text-primary"></i>
                                        Kế hoạch & Sự kiện CLB
                                    </h1>
                                    <p class="text-muted mb-0">
                                        Xem lộ trình sự kiện và nhiệm vụ của ban
                                        <c:if test="${not empty departmentName}">
                                            - Ban ${departmentName}
                                        </c:if>
                                    </p>
                                </div>
                                <div class="text-end">
                                    <div class="small text-muted">
                                        <i class="fas fa-clock"></i>
                                        <span id="currentTime"></span>
                                    </div>
                                    <div class="d-flex gap-2 mt-2">
                                        <button class="btn btn-outline-primary btn-sm" onclick="location.reload()">
                                            <i class="fas fa-sync-alt"></i> Làm mới
                                        </button>
                                        <button class="btn btn-primary btn-sm" onclick="exportData()">
                                            <i class="fas fa-download"></i> Xuất file
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Tab Navigation -->
                        <div class="tab-navigation">
                            <ul class="nav nav-tabs" id="planEventsTabs" role="tablist">
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link ${activeTab == 'timeline' ? 'active' : ''}" 
                                            id="timeline-tab" data-bs-toggle="tab" data-bs-target="#timeline" 
                                            type="button" role="tab" aria-controls="timeline" 
                                            aria-selected="${activeTab == 'timeline' ? 'true' : 'false'}">
                                        <i class="fas fa-timeline"></i>
                                        Timeline
                                    </button>
                                </li>
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link ${activeTab == 'upcoming-events' ? 'active' : ''}" 
                                            id="upcoming-events-tab" data-bs-toggle="tab" data-bs-target="#upcoming-events" 
                                            type="button" role="tab" aria-controls="upcoming-events" 
                                            aria-selected="${activeTab == 'upcoming-events' ? 'true' : 'false'}">
                                        <i class="fas fa-calendar-alt"></i>
                                        Sự kiện sắp tới
                                    </button>
                                </li>
                            </ul>
                        </div>

                        <!-- Tab Content -->
                        <div class="tab-content" id="planEventsTabContent">
                            <!-- Timeline Tab -->
                            <div class="tab-pane fade ${activeTab == 'timeline' ? 'show active' : ''}" 
                                 id="timeline" role="tabpanel" aria-labelledby="timeline-tab">
                                
                                <div class="filter-controls">
                                    <div class="row align-items-center">
                                        <div class="col-md-6">
                                            <h5 class="mb-0">
                                                <i class="fas fa-stream text-primary"></i>
                                                Lộ trình hoạt động tổng quan
                                            </h5>
                                            <small class="text-muted">Xem nhiệm vụ được giao cho ban và lộ trình thực hiện</small>
                                        </div>
                                        <div class="col-md-6 text-end">
                                            <div class="btn-group" role="group">
                                                <button type="button" class="btn btn-outline-primary btn-sm" onclick="filterTimeline('all')">
                                                    <i class="fas fa-list"></i> Tất cả
                                                </button>
                                                <button type="button" class="btn btn-outline-success btn-sm" onclick="filterTimeline('confirmed')">
                                                    <i class="fas fa-check-circle"></i> Đã xác nhận
                                                </button>
                                                <button type="button" class="btn btn-outline-warning btn-sm" onclick="filterTimeline('planning')">
                                                    <i class="fas fa-clock"></i> Đang lên kế hoạch
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="timeline-container">
                                    <div class="timeline">
                                        <!-- Display actual tasks from database -->
                                        <c:choose>
                                            <c:when test="${not empty departmentTasks}">
                                                <c:forEach var="task" items="${departmentTasks}" varStatus="status">
                                                    <div class="timeline-item confirmed" data-status="confirmed">
                                                        <div class="timeline-number">${status.index + 1}</div>
                                                        <div class="d-flex justify-content-between align-items-start">
                                                            <div class="flex-grow-1">
                                                                <h5 class="mb-2">
                                                                    <i class="fas fa-tasks text-primary"></i>
                                                                    ${task.title}
                                                                </h5>
                                                                <div class="mb-2">
                                                                    <c:choose>
                                                                        <c:when test="${task.status == 'Done'}">
                                                                            <span class="event-status status-confirmed">Hoàn thành</span>
                                                                        </c:when>
                                                                        <c:when test="${task.status == 'InProgress'}">
                                                                            <span class="event-status status-planning">Đang thực hiện</span>
                                                                        </c:when>
                                                                        <c:when test="${task.status == 'Review'}">
                                                                            <span class="event-status status-planning">Đang đánh giá</span>
                                                                        </c:when>
                                                                        <c:when test="${task.status == 'Rejected'}">
                                                                            <span class="event-status status-draft">Bị từ chối</span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="event-status status-draft">Chưa bắt đầu</span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                                <c:if test="${not empty task.startDate and not empty task.endDate}">
                                                                    <p class="text-muted mb-2">
                                                                        <i class="fas fa-calendar"></i> 
                                                                        <fmt:formatDate value="${task.startDate}" pattern="dd/MM/yyyy HH:mm" />
                                                                        - 
                                                                        <fmt:formatDate value="${task.endDate}" pattern="dd/MM/yyyy HH:mm" />
                                                                    </p>
                                                                </c:if>
                                                                <c:if test="${not empty task.description}">
                                                                    <p class="mb-3">${task.description}</p>
                                                                </c:if>
                                                                <c:if test="${not empty task.event}">
                                                                    <div class="mt-3">
                                                                        <h6 class="text-info">Sự kiện liên quan:</h6>
                                                                        <p class="mb-0">
                                                                            <i class="fas fa-calendar-alt"></i> ${task.event.eventName}
                                                                        </p>
                                                                    </div>
                                                                </c:if>
                                                                <c:if test="${not empty task.createdBy}">
                                                                    <div class="mt-2">
                                                                        <small class="text-muted">
                                                                            <i class="fas fa-user"></i> Giao bởi: ${task.createdBy.fullName}
                                                                            <c:if test="${not empty task.createdAt}">
                                                                                - <fmt:formatDate value="${task.createdAt}" pattern="dd/MM/yyyy" />
                                                                            </c:if>
                                                                        </small>
                                                                    </div>
                                                                </c:if>
                                                            </div>
                                                            <div class="text-end">
                                                                <div class="dropdown">
                                                                    <button class="btn btn-outline-secondary btn-sm dropdown-toggle" type="button" data-bs-toggle="dropdown">
                                                                        <i class="fas fa-ellipsis-v"></i>
                                                                    </button>
                                                                    <ul class="dropdown-menu">
                                                                        <li><a class="dropdown-item" href="#" onclick="viewTaskDetail(${task.taskID})"><i class="fas fa-eye"></i> Xem chi tiết</a></li>
                                                                        <li><a class="dropdown-item" href="#" onclick="openDiscussion(${task.taskID})"><i class="fas fa-comments"></i> Thảo luận</a></li>
                                                                        <c:if test="${task.status != 'Done'}">
                                                                            <li><hr class="dropdown-divider"></li>
                                                                        <li><a class="dropdown-item" href="#" onclick="updateProgress(${task.taskID})"><i class="fas fa-edit"></i> Cập nhật trạng thái</a></li>
                                                                        </c:if>
                                                                    </ul>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="text-center py-5">
                                                    <i class="fas fa-tasks fa-3x text-muted mb-3"></i>
                                                    <h5 class="text-muted">Chưa có nhiệm vụ nào</h5>
                                                    <p class="text-muted">Ban ${departmentName} chưa được giao nhiệm vụ nào từ Chủ tích.</p>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>

                            <!-- Upcoming Events Tab -->
                            <div class="tab-pane fade ${activeTab == 'upcoming-events' ? 'show active' : ''}" 
                                 id="upcoming-events" role="tabpanel" aria-labelledby="upcoming-events-tab">
                                
                                <div class="filter-controls">
                                    <h5 class="mb-3">
                                        <i class="fas fa-calendar-alt text-primary"></i>
                                        Sự kiện sắp tới
                                    </h5>
                                </div>

                                <div class="row">
                                    <c:forEach var="event" items="${upcomingEvents}">
                                        <div class="col-md-6 mb-4">
                                            <div class="event-card">
                                                <h5 class="mb-2">
                                                    <i class="fas fa-star text-warning"></i>
                                                    ${event.eventName}
                                                </h5>
                                                <p class="text-muted mb-3">${event.description}</p>
                                                <div class="d-flex justify-content-between align-items-center">
                                                    <span class="event-status status-confirmed">confirmed</span>
                                                    <small class="text-muted">
                                                        <i class="fas fa-users"></i> ${event.capacity} người
                                                    </small>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                    
                                    <c:if test="${empty upcomingEvents}">
                                        <div class="col-12">
                                            <div class="text-center py-5">
                                                <i class="fas fa-calendar-times fa-3x text-muted mb-3"></i>
                                                <h5 class="text-muted">Chưa có sự kiện nào sắp tới</h5>
                                                <p class="text-muted">Liên hệ với Chủ tích để biết thêm thông tin về kế hoạch sự kiện.</p>
                                            </div>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>

        <!-- Task Detail Modal -->
        <div class="modal fade" id="taskDetailModal" tabindex="-1" aria-labelledby="taskDetailModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="taskDetailModalLabel">
                            <i class="fas fa-tasks text-primary"></i> Chi tiết nhiệm vụ
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body" id="taskDetailContent">
                        <div class="text-center py-4">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Đang tải...</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Update Progress Modal -->
        <div class="modal fade" id="updateProgressModal" tabindex="-1" aria-labelledby="updateProgressModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="updateProgressModalLabel">
                            <i class="fas fa-edit text-warning"></i> Cập nhật trạng thái
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form id="updateProgressForm">
                        <div class="modal-body">
                            <input type="hidden" id="updateTaskId" name="taskId">
                            
                            <div class="mb-3">
                                <label for="taskStatus" class="form-label">Trạng thái <span class="text-danger">*</span></label>
                                <select class="form-select" id="taskStatus" name="status" required>
                                    <option value="">-- Chọn trạng thái --</option>
                                    <option value="ToDo">Chưa bắt đầu</option>
                                    <option value="InProgress">Đang thực hiện</option>
                                    <option value="Review">Đang đánh giá</option>
                                    <option value="Done">Hoàn thành</option>
                                    <option value="Rejected">Bị từ chối</option>
                                </select>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> Cập nhật trạng thái
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Update current time
            function updateTime() {
                const now = new Date();
                const timeString = now.toLocaleString('vi-VN', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit'
                });
                document.getElementById('currentTime').textContent = timeString;
            }
            
            // Filter timeline items
            function filterTimeline(status) {
                const items = document.querySelectorAll('.timeline-item');
                items.forEach(item => {
                    if (status === 'all') {
                        item.style.display = 'block';
                    } else {
                        const statusBadge = item.querySelector('.event-status');
                        if (statusBadge) {
                            const itemStatus = statusBadge.textContent.trim().toLowerCase();
                            let shouldShow = false;
                            
                            if (status === 'confirmed' && (itemStatus.includes('hoàn thành') || itemStatus.includes('confirmed'))) {
                                shouldShow = true;
                            } else if (status === 'planning' && (itemStatus.includes('đang') || itemStatus.includes('planning'))) {
                                shouldShow = true;
                            }
                            
                            item.style.display = shouldShow ? 'block' : 'none';
                        }
                    }
                });
            }
            
            // Export data functionality
            function exportData() {
                alert('Tính năng xuất file đang được phát triển!');
            }

            // View task detail
            function viewTaskDetail(taskId) {
                const modal = new bootstrap.Modal(document.getElementById('taskDetailModal'));
                
                // Show loading
                document.getElementById('taskDetailContent').innerHTML = `
                    <div class="text-center py-4">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Đang tải...</span>
                        </div>
                    </div>
                `;
                
                modal.show();
                
                // Fetch task details
                fetch('${pageContext.request.contextPath}/task-detail?taskId=' + taskId)
                    .then(response => response.json())
                    .then(data => {
                        if (data.success && data.task) {
                            displayTaskDetail(data.task);
                        } else {
                            document.getElementById('taskDetailContent').innerHTML = 
                                `<div class="alert alert-warning">${data.message || 'Không thể tải thông tin nhiệm vụ.'}</div>`;
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        document.getElementById('taskDetailContent').innerHTML = 
                            '<div class="alert alert-danger">Có lỗi xảy ra khi tải dữ liệu.</div>';
                    });
            }

            // Display task detail in modal
            function displayTaskDetail(task) {
                const content = `
                    <div class="row">
                        <div class="col-md-12">
                            <h6 class="text-primary mb-3"><i class="fas fa-info-circle"></i> Thông tin cơ bản</h6>
                            <table class="table table-borderless">
                                <tr>
                                    <td class="fw-bold" width="30%">Tên nhiệm vụ:</td>
                                    <td>\${task.title}</td>
                                </tr>
                                <tr>
                                    <td class="fw-bold">Mô tả:</td>
                                    <td>\${task.description || 'Không có mô tả'}</td>
                                </tr>
                                <tr>
                                    <td class="fw-bold">Trạng thái:</td>
                                    <td><span class="badge \${getStatusBadgeClass(task.status)}">\${getStatusText(task.status)}</span></td>
                                </tr>
                                <tr>
                                    <td class="fw-bold">Thời gian:</td>
                                    <td>
                                        <i class="fas fa-calendar"></i> 
                                        \${formatDate(task.startDate)} - \${formatDate(task.endDate)}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="fw-bold">Giao bởi:</td>
                                    <td><i class="fas fa-user"></i> \${task.createdBy && task.createdBy.fullName ? task.createdBy.fullName : 'N/A'}</td>
                                </tr>
                                <tr>
                                    <td class="fw-bold">Sự kiện:</td>
                                    <td><i class="fas fa-calendar-alt"></i> \${task.event && task.event.eventName ? task.event.eventName : 'Không có'}</td>
                                </tr>
                            </table>
                        </div>
                    </div>
                `;
                
                document.getElementById('taskDetailContent').innerHTML = content;
            }

            // Update task progress
            function updateProgress(taskId) {
                document.getElementById('updateTaskId').value = taskId;
                
                // Reset form
                document.getElementById('updateProgressForm').reset();
                document.getElementById('updateTaskId').value = taskId;
                
                // Fetch current task data to pre-fill form
                fetch('${pageContext.request.contextPath}/task-detail?taskId=' + taskId)
                    .then(response => response.json())
                    .then(data => {
                        if (data.success && data.task) {
                            document.getElementById('taskStatus').value = data.task.status || '';
                        }
                    })
                    .catch(error => console.error('Error:', error));
                
                const modal = new bootstrap.Modal(document.getElementById('updateProgressModal'));
                modal.show();
            }

            // Handle progress update form submission
            document.getElementById('updateProgressForm').addEventListener('submit', function(e) {
                e.preventDefault();
                
                const formData = new FormData(this);
                const submitBtn = this.querySelector('button[type="submit"]');
                
                // Show loading
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang cập nhật...';
                submitBtn.disabled = true;
                
                fetch('${pageContext.request.contextPath}/update-task-status', {
                    method: 'POST',
                    body: formData
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Close modal
                        bootstrap.Modal.getInstance(document.getElementById('updateProgressModal')).hide();
                        
                        // Show success message
                        showNotification('Cập nhật trạng thái thành công!', 'success');
                        
                        // Reload page to show updates
                        setTimeout(() => {
                            location.reload();
                        }, 1500);
                    } else {
                        showNotification('Có lỗi xảy ra: ' + (data.message || 'Unknown error'), 'error');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showNotification('Có lỗi xảy ra khi cập nhật trạng thái.', 'error');
                })
                .finally(() => {
                    // Reset button
                    submitBtn.innerHTML = '<i class="fas fa-save"></i> Cập nhật trạng thái';
                    submitBtn.disabled = false;
                });
            });

            // Open discussion (placeholder)
            function openDiscussion(taskId) {
                showNotification('Tính năng thảo luận đang được phát triển! Task ID: ' + taskId, 'info');
            }

            // Helper functions
            function getStatusBadgeClass(status) {
                switch(status) {
                    case 'Done': return 'bg-success';
                    case 'InProgress': return 'bg-warning';
                    case 'Review': return 'bg-info';
                    case 'Rejected': return 'bg-danger';
                    default: return 'bg-secondary';
                }
            }

            function getStatusText(status) {
                switch(status) {
                    case 'Done': return 'Hoàn thành';
                    case 'InProgress': return 'Đang thực hiện';
                    case 'Review': return 'Đang đánh giá';
                    case 'Rejected': return 'Bị từ chối';
                    case 'ToDo': return 'Chưa bắt đầu';
                    default: return 'Không xác định';
                }
            }

            function formatDate(dateString) {
                if (!dateString) return 'N/A';
                return new Date(dateString).toLocaleDateString('vi-VN');
            }

            function formatDateTime(dateString) {
                if (!dateString) return 'N/A';
                return new Date(dateString).toLocaleString('vi-VN');
            }

            function showNotification(message, type) {
                let alertClass;
                switch(type) {
                    case 'success': alertClass = 'alert-success'; break;
                    case 'info': alertClass = 'alert-info'; break;
                    case 'warning': alertClass = 'alert-warning'; break;
                    default: alertClass = 'alert-danger';
                }
                
                const notification = `
                    <div class="alert \${alertClass} alert-dismissible fade show position-fixed" 
                         style="top: 20px; right: 20px; z-index: 9999; min-width: 300px;" role="alert">
                        \${message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                `;
                document.body.insertAdjacentHTML('beforeend', notification);
                
                // Auto remove after 3 seconds
                setTimeout(() => {
                    const alerts = document.querySelectorAll('.alert');
                    if (alerts.length > 0) alerts[alerts.length - 1].remove();
                }, 3000);
            }
            
            // Initialize
            document.addEventListener('DOMContentLoaded', function() {
                updateTime();
                setInterval(updateTime, 60000); // Update every minute
            });
        </script>
    </body>
</html>
