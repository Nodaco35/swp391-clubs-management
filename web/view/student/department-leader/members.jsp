<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">    <title>Quản lý thành viên - Ban ${departmentName}</title>

        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

        <!-- Font Aw        <!-- Scripts -->    
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/department-members.js"></script>
        
        <!-- ✅ ĐƠN GIẢN HÓA: Chỉ giữ những JS cần thiết -->
        <script>
            // ✅ 1. Basic search functionality (cần thiết)
            function searchMembers() {
                const keyword = document.getElementById('searchInput').value.trim();
                if (keyword) {
                    window.location.href = '${pageContext.request.contextPath}/department-members?action=search&keyword=' + encodeURIComponent(keyword) + '&clubID=${clubID}';
                }
            }
            
            function clearSearch() {
                window.location.href = '${pageContext.request.contextPath}/department-members?action=list&clubID=${clubID}';
            }
            
            // ✅ 2. Enter key support cho search (UX improvement)
            document.addEventListener('DOMContentLoaded', function() {
                const searchInput = document.getElementById('searchInput');
                if (searchInput) {
                    searchInput.addEventListener('keypress', function(e) {
                        if (e.key === 'Enter') {
                            searchMembers();
                        }
                    });
                }
            });
            
            // ✅ 3. Simple modal functions (cần thiết cho member detail)
            function viewMemberDetail(userID) {
                // Load member detail modal
                const modal = new bootstrap.Modal(document.getElementById('memberDetailModal'));
                modal.show();
                
                // Basic loading state
                const content = document.getElementById('memberDetailContent');
                const loading = document.getElementById('memberDetailLoading');
                content.style.display = 'none';
                loading.style.display = 'block';
                
                // Simple AJAX call - no complex caching
                fetch('${pageContext.request.contextPath}/department-members?action=getMemberDetail&userID=' + userID + '&clubDepartmentID=${clubDepartmentID}')
                    .then(response => response.json())
                    .then(data => {
                        // Hide loading, show content
                        loading.style.display = 'none';
                        content.style.display = 'block';
                        
                        // Simple data binding - no complex processing
                        const nameElement = document.getElementById('memberDetailName');
                        const emailElement = document.getElementById('memberDetailEmail');
                        
                        if (nameElement) nameElement.textContent = data.member.fullName || '';
                        if (emailElement) emailElement.textContent = data.member.email || '-';
                        
                        const avatar = document.getElementById('memberDetailAvatar');
                        
                        // Handle avatar path correctly
                        if (avatar) {
                            let avatarPath = data.member.avatar;
                            if (avatarPath && avatarPath.trim() !== '') {
                                // If avatar path already includes 'img/', use it directly
                                if (avatarPath.startsWith('img/')) {
                                    avatar.src = '${pageContext.request.contextPath}/' + avatarPath;
                                } else {
                                    // If avatar path doesn't include 'img/', add it
                                    avatar.src = '${pageContext.request.contextPath}/img/' + avatarPath;
                                }
                            } else {
                                // Use default avatar
                                avatar.src = '${pageContext.request.contextPath}/img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg';
                            }
                        }
                        
                        // Update member information
                        const genElement = document.getElementById('memberDetailGen');
                        if (genElement) genElement.textContent = data.member.gen || '-';
                        
                        // Format join date
                        const joinDateElement = document.getElementById('memberDetailJoinDate');
                        if (joinDateElement) {
                            if (data.member.joinedDate) {
                                const joinDate = new Date(data.member.joinedDate);
                                joinDateElement.textContent = joinDate.toLocaleDateString('vi-VN');
                            } else {
                                joinDateElement.textContent = '-';
                            }
                        }
                        
                        // Format date of birth
                        const birthDateElement = document.getElementById('memberDetailDateOfBirth');
                        if (birthDateElement) {
                            if (data.member.dateOfBirth) {
                                const birthDate = new Date(data.member.dateOfBirth);
                                birthDateElement.textContent = birthDate.toLocaleDateString('vi-VN');
                            } else {
                                birthDateElement.textContent = '-';
                            }
                        }
                        
                        // Update status - kiểm tra cả active và isActive để đảm bảo tương thích
                        const statusElement = document.getElementById('memberDetailStatusText');
                        const isActive = data.member.active || data.member.isActive;
                        
                        if (statusElement) {
                            if (isActive) {
                                statusElement.textContent = 'Hoạt động';
                                statusElement.className = 'badge bg-success';
                            } else {
                                statusElement.textContent = 'Không hoạt động';
                                statusElement.className = 'badge bg-secondary';
                            }
                        }
                        
                        // Update task statistics
                        const assignedTasksElement = document.getElementById('memberDetailAssignedTasks');
                        const completedTasksElement = document.getElementById('memberDetailCompletedTasks');
                        
                        if (assignedTasksElement) assignedTasksElement.textContent = data.member.assignedTasks || 0;
                        if (completedTasksElement) completedTasksElement.textContent = data.member.completedTasks || 0;
                        
                        // Update task list
                        const tasksBody = document.getElementById('memberDetailTasksBody');
                        const emptyTasks = document.getElementById('memberDetailEmptyTasks');
                        const taskList = document.getElementById('memberDetailTaskList');
                        
                        if (data.tasks && data.tasks.length > 0) {
                            // Show task list, hide empty message
                            taskList.style.display = 'block';
                            emptyTasks.style.display = 'none';
                            
                            // Clear and populate task list
                            tasksBody.innerHTML = '';
                            data.tasks.forEach(function(task) {
                                const row = document.createElement('tr');
                                
                                // Status badge
                                let statusBadge = '';
                                if (task.status === 'Done') {
                                    statusBadge = '<span class="badge bg-success">Hoàn thành</span>';
                                } else if (task.status === 'In Progress') {
                                    statusBadge = '<span class="badge bg-warning text-dark">Đang thực hiện</span>';
                                } else {
                                    statusBadge = '<span class="badge bg-secondary">Chưa bắt đầu</span>';
                                }
                                
                                // Deadline formatting
                                let deadlineText = '-';
                                if (task.endDate) {
                                    const deadline = new Date(task.endDate);
                                    deadlineText = deadline.toLocaleDateString('vi-VN');
                                }
                                
                                row.innerHTML = 
                                    '<td>' + (task.title || '') + '</td>' +
                                    '<td>' + statusBadge + '</td>' +
                                    '<td>' + deadlineText + '</td>';
                                
                                tasksBody.appendChild(row);
                            });
                        } else {
                            // Hide task list, show empty message
                            taskList.style.display = 'none';
                            emptyTasks.style.display = 'block';
                        }
                    })
                    .catch(error => {
                        loading.innerHTML = '<div class="alert alert-danger">Có lỗi xảy ra khi tải thông tin thành viên.</div>';
                    });
            }
            
            // ✅ 4. Basic confirm dialog (cần thiết cho delete)
            function confirmRemoveMember(userID, fullName) {
                if (confirm('Bạn có chắc chắn muốn xóa thành viên "' + fullName + '" khỏi ban không?')) {
                    window.location.href = '${pageContext.request.contextPath}/department-members?action=remove&userID=' + userID + '&clubDepartmentID=${clubDepartmentID}';
                }
            }
            
            // ✅ 5. Simple add member modal (cần thiết)
            function showAddMemberModal() {
                const modal = new bootstrap.Modal(document.getElementById('addMemberModal'));
                modal.show();
            }
        </script>me -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <!-- Custom CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
    </head>
    <body>
        <div class="department-leader-container">
            <!-- Sidebar -->
            <c:set var="activePage" value="members" />
            <%@ include file="components/sidebar.jsp" %>

            <!-- Main Content -->
            <main class="main-content">            <!-- Header -->
                <header class="header mb-4">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h1 class="h3 mb-1">Quản lý thành viên</h1>
                            <nav aria-label="breadcrumb">
                                <ol class="breadcrumb mb-0">
                                    <li class="breadcrumb-item">
                                        <a href="${pageContext.request.contextPath}/department-dashboard?clubID=${clubID}" class="text-decoration-none">
                                            Dashboard
                                        </a>
                                    </li>
                                    <li class="breadcrumb-item active" aria-current="page">Thành viên</li>
                                </ol>
                            </nav>
                        </div>
                    </div>
                </header>
                <!-- Stats Cards -->
                <div class="row mb-4">
                    <div class="col-md-4">
                        <div class="card bg-primary text-white">
                            <div class="card-body">
                                <div class="d-flex align-items-center">
                                    <div class="flex-grow-1">
                                        <h5 class="card-title mb-1">Tổng thành viên</h5>
                                        <h3 class="mb-0">${totalMembers}</h3>
                                    </div>
                                    <div class="ms-3">
                                        <i class="fas fa-users fa-2x opacity-75"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card bg-success text-white">
                            <div class="card-body">
                                <div class="d-flex align-items-center">                                <div class="flex-grow-1">
                                        <h5 class="card-title mb-1">Hoạt động</h5>
                                        <h3 class="mb-0" id="activeCount">${activeMembers != null ? activeMembers : 0}</h3>
                                    </div>
                                    <div class="ms-3">
                                        <i class="fas fa-user-check fa-2x opacity-75"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card bg-warning text-white">
                            <div class="card-body">
                                <div class="d-flex align-items-center">                                <div class="flex-grow-1">
                                        <h5 class="card-title mb-1">Không hoạt động</h5>
                                        <h3 class="mb-0" id="inactiveCount">${inactiveMembers != null ? inactiveMembers : 0}</h3>
                                    </div>
                                    <div class="ms-3">
                                        <i class="fas fa-user-clock fa-2x opacity-75"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Search and Filter Section -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card shadow-sm">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col-md-6">
                                        <div class="input-group">
                                            <span class="input-group-text">
                                                <i class="fas fa-search"></i>
                                            </span>
                                            <input type="text" id="searchInput" class="form-control" 
                                                   placeholder="Tìm kiếm theo tên hoặc email..." 
                                                   value="${keyword}">                                        <button class="btn btn-outline-primary" type="button" onclick="searchMembers()">
                                                Tìm kiếm
                                            </button>
                                            <c:if test="${not empty keyword}">
                                                <button class="btn btn-outline-secondary" type="button" onclick="clearSearch()">
                                                    <i class="fas fa-times"></i>
                                                </button>
                                            </c:if>
                                        </div>
                                    </div>                                <div class="col-md-2">
                                        <label class="filter-label">Trạng thái</label>
                                        <select id="statusFilter" class="form-select" onchange="filterMembers()">
                                            <option value="">Tất cả trạng thái</option>
                                            <option value="active">Hoạt động</option>
                                            <option value="inactive">Không hoạt động</option>
                                        </select>
                                    </div>                                <div class="col-md-1">
                                        <label class="filter-label">&nbsp;</label>
                                        <button class="btn btn-outline-secondary w-100" type="button" onclick="resetFilters()" title="Đặt lại bộ lọc">
                                            <i class="fas fa-refresh"></i>
                                        </button>
                                    </div>
                                    <div class="col-md-3">
                                        <button class="btn btn-primary w-100" onclick="showAddMemberModal()">
                                            <i class="fas fa-plus me-2"></i>Thêm thành viên
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>  

                <!-- Members List -->
                <div class="row">
                    <div class="col-12">
                        <div class="card shadow-sm">                        <div class="card-header bg-white">
                                <div class="d-flex justify-content-between align-items-center">
                                    <h5 class="mb-0">
                                        <i class="fas fa-users me-2"></i>Danh sách thành viên
                                        <c:if test="${not empty keyword}">
                                            <small class="text-muted ms-2">- Kết quả tìm kiếm cho: "${keyword}"</small>
                                        </c:if>
                                    </h5>
                                </div>
                            </div>

                            <div class="card-body p-0">
                                <c:if test="${not empty members}">
                                    <div class="table-responsive">
                                        <table class="table table-hover mb-0">                                        <thead class="table-light">
                                                <tr>
                                                    <th scope="col" class="ps-4 sortable" data-sort="name" onclick="sortByColumn(this)">
                                                        Thành viên <i class="fas fa-sort ms-1"></i>
                                                    </th>
                                                    <th scope="col" class="sortable" data-sort="role" onclick="sortByColumn(this)">
                                                        Vai trò <i class="fas fa-sort ms-1"></i>
                                                    </th>
                                                    <th scope="col" class="sortable" data-sort="date" onclick="sortByColumn(this)">
                                                        Ngày tham gia <i class="fas fa-sort ms-1"></i>
                                                    </th>
                                                    <th scope="col">Công việc</th>
                                                    <th scope="col" class="sortable" data-sort="status" onclick="sortByColumn(this)">
                                                        Trạng thái <i class="fas fa-sort ms-1"></i>
                                                    </th>
                                                    <th scope="col" class="text-center">Hành động</th>
                                                </tr>
                                            </thead>
                                            <tbody>                                            <c:forEach var="member" items="${members}">
                                                    <tr data-member-id="${member.userID}" 
                                                        class="member-row ${member.active ? 'table-light' : 'table-secondary'}"
                                                        data-active="${member.active}">
                                                        <td class="ps-4">
                                                            <div class="d-flex align-items-center">
                                                                <div class="avatar-container me-3">
                                                                    <c:choose>
                                                                        <c:when test="${not empty member.avatar}">
                                                                            <c:choose>
                                                                                <c:when test="${member.avatar.startsWith('img/')}">
                                                                                    <img src="${pageContext.request.contextPath}/${member.avatar}" 
                                                                                         alt="Avatar" class="rounded-circle" style="width: 45px; height: 45px; object-fit: cover;">
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <img src="${pageContext.request.contextPath}/img/${member.avatar}" 
                                                                                         alt="Avatar" class="rounded-circle" style="width: 45px; height: 45px; object-fit: cover;">
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <img src="${pageContext.request.contextPath}/img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg" 
                                                                                 alt="Avatar" class="rounded-circle" style="width: 45px; height: 45px; object-fit: cover;">
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                                <div>
                                                                    <h6 class="mb-1 fw-semibold">${member.fullName}</h6>
                                                                    <small class="text-muted">${member.email}</small>
                                                                    <c:if test="${not empty member.phone}">
                                                                        <br><small class="text-muted"><i class="fas fa-phone fa-xs me-1"></i>${member.phone}</small>
                                                                        </c:if>
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>                                                        <c:choose>
                                                                <c:when test="${member.roleName == 'Trưởng ban'}">
                                                                    <span class="badge bg-danger"><i class="fas fa-crown me-1"></i>Trưởng ban</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="badge bg-secondary"><i class="fas fa-user me-1"></i>Thành viên</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <div>
                                                                <fmt:formatDate value="${member.joinedDate}" pattern="dd/MM/yyyy" />
                                                            </div>
                                                            <small class="text-muted">
                                                                <fmt:formatDate value="${member.joinedDate}" pattern="HH:mm" />
                                                            </small>
                                                        </td>
                                                        <td>
                                                            <div class="d-flex align-items-center">
                                                                <small class="me-2">
                                                                    <i class="fas fa-tasks me-1"></i>
                                                                    ${member.completedTasks}/${member.assignedTasks}
                                                                </small>
                                                                <c:if test="${member.assignedTasks > 0}">
                                                                    <div class="progress" style="width: 60px; height: 6px;">
                                                                        <div class="progress-bar bg-success" 
                                                                             style="width: ${(member.completedTasks * 100) / member.assignedTasks}%"></div>
                                                                    </div>
                                                                </c:if>
                                                            </div>
                                                        </td>                                                    <td>
                                                            <span class="badge ${member.active ? 'bg-success' : 'bg-secondary'}">
                                                                ${member.active ? 'Hoạt động' : 'Không hoạt động'}
                                                            </span>
                                                        </td>
                                                        <td class="text-center">
                                                            <div class="btn-group" role="group">
                                                                <button type="button" class="btn btn-sm btn-outline-info" 
                                                                        onclick="viewMemberDetail('${member.userID}')" 
                                                                        title="Xem chi tiết">
                                                                    <i class="fas fa-eye"></i>
                                                                </button>
                                                                <c:if test="${member.roleName != 'Trưởng ban'}">
                                                                    <button type="button" class="btn btn-sm btn-outline-danger" 
                                                                            onclick="confirmRemoveMember('${member.userID}', '${member.fullName}')" 
                                                                            title="Xóa khỏi ban">
                                                                        <i class="fas fa-trash"></i>
                                                                    </button>
                                                                </c:if>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:if>                              <c:if test="${empty members}">
                                    <div class="text-center py-5 empty-state">
                                        <div class="mb-3">
                                            <i class="fas fa-users-slash fa-3x text-muted"></i>
                                        </div>
                                        <c:choose>
                                            <c:when test="${not empty keyword}">
                                                <h5 class="text-muted">Không tìm thấy thành viên nào</h5>
                                                <p class="text-muted">Không có thành viên nào phù hợp với từ khóa "<strong>${keyword}</strong>".</p>
                                                <button class="btn btn-outline-primary" onclick="clearSearch()">
                                                    <i class="fas fa-arrow-left me-2"></i>Xem tất cả thành viên
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <h5 class="text-muted">Không có thành viên nào</h5>
                                                <p class="text-muted">Chưa có thành viên nào trong ban.</p>
                                                <button class="btn btn-primary" onclick="showAddMemberModal()">
                                                    <i class="fas fa-plus me-2"></i>Thêm thành viên đầu tiên
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>            <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <!-- Set pagination URL base -->
                    <c:choose>
                        <c:when test="${not empty keyword}">
                            <c:set var="pageUrl" value="?action=search&keyword=${keyword}&page=" />
                        </c:when>
                        <c:otherwise>
                            <c:set var="pageUrl" value="?page=" />
                        </c:otherwise>
                    </c:choose>

                    <div class="row mt-4">
                        <div class="col-12">
                            <nav aria-label="Members pagination">
                                <ul class="pagination justify-content-center">                                <!-- Previous button -->                                <c:if test="${currentPage > 1}">
                                        <li class="page-item">
                                            <a class="page-link" href="${pageUrl}${currentPage - 1}" 
                                               aria-label="Previous">
                                                <span aria-hidden="true">&laquo;</span>
                                            </a>
                                        </li>
                                    </c:if>

                                    <!-- Page numbers -->
                                    <c:set var="startPage" value="${currentPage - 2 > 0 ? currentPage - 2 : 1}" />
                                    <c:set var="endPage" value="${currentPage + 2 <= totalPages ? currentPage + 2 : totalPages}" />                                <!-- First page if needed -->
                                    <c:if test="${startPage > 1}">
                                        <li class="page-item">
                                            <a class="page-link" href="${pageUrl}1">1</a>
                                        </li>
                                        <c:if test="${startPage > 2}">
                                            <li class="page-item disabled">
                                                <span class="page-link">...</span>
                                            </li>
                                        </c:if>
                                    </c:if>

                                    <!-- Page range -->
                                    <c:forEach begin="${startPage}" end="${endPage}" var="page">
                                        <li class="page-item ${page == currentPage ? 'active' : ''}">
                                            <c:choose>
                                                <c:when test="${page == currentPage}">
                                                    <span class="page-link">${page}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <a class="page-link" href="${pageUrl}${page}">${page}</a>
                                                </c:otherwise>
                                            </c:choose>
                                        </li>
                                    </c:forEach>                                <!-- Last page if needed -->
                                    <c:if test="${endPage < totalPages}">
                                        <c:if test="${endPage < totalPages - 1}">
                                            <li class="page-item disabled">
                                                <span class="page-link">...</span>
                                            </li>
                                        </c:if>
                                        <li class="page-item">
                                            <a class="page-link" href="${pageUrl}${totalPages}">${totalPages}</a>
                                        </li>
                                    </c:if>

                                    <!-- Next button -->
                                    <c:if test="${currentPage < totalPages}">
                                        <li class="page-item">
                                            <a class="page-link" href="${pageUrl}${currentPage + 1}" 
                                               aria-label="Next">
                                                <span aria-hidden="true">&raquo;</span>
                                            </a>
                                        </li>
                                    </c:if>
                                </ul>
                            </nav>

                            <!-- Pagination info -->
                            <div class="text-center mt-2">
                                <small class="text-muted">
                                    Hiển thị trang ${currentPage} / ${totalPages} - Tổng ${totalMembers} thành viên
                                </small>
                            </div>
                        </div>
                    </div>
                </c:if>
            </main>
        </div>    <!-- Add Member Modal -->
        <div class="modal fade" id="addMemberModal" tabindex="-1" aria-labelledby="addMemberModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="addMemberModalLabel">
                            <i class="fas fa-user-plus me-2"></i>Thêm thành viên mới
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-12">
                                <label for="studentSearchInput" class="form-label">Tìm kiếm sinh viên</label>
                                <div class="input-group mb-3">
                                    <span class="input-group-text">
                                        <i class="fas fa-search"></i>
                                    </span>
                                    <input type="text" id="studentSearchInput" class="form-control" 
                                           placeholder="Nhập tên hoặc email sinh viên...">
                                </div>
                                <div id="studentSearchResults" class="border rounded p-2 mb-3" style="max-height: 200px; overflow-y: auto;"></div>
                            </div>

                            <div class="col-12" id="selectedStudent" style="display: none;">
                                <hr>
                                <h6>Sinh viên được chọn:</h6>
                                <div class="card bg-light">
                                    <div class="card-body">
                                        <div class="student-info"></div>

                                        <div class="mt-3">
                                            <label for="memberRole" class="form-label">Vai trò trong ban:</label>
                                            <select id="memberRole" class="form-select">
                                                <option value="4">Thành viên</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="button" class="btn btn-primary" onclick="addMember()" id="addMemberBtn" disabled>
                            <i class="fas fa-plus me-2"></i>Thêm thành viên
                        </button>
                    </div>
                </div>
            </div>    
        </div>    <!-- Member Detail Modal -->

        <div class="col-md-3" style="margin-left: 300px;padding: 30px">
            <button class="btn btn-warning w-100" onclick="window.location.href = '${pageContext.request.contextPath}/department-members?action=evaluatePoint&clubDepartmentID=${clubDepartmentID}'">
                <i class="fa-solid fa-user-graduate"></i>Chấm điểm thành viên trong ban
            </button>
        </div>

        <div class="modal fade" id="memberDetailModal" tabindex="-1" aria-labelledby="memberDetailModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header bg-light">
                        <h5 class="modal-title" id="memberDetailModalLabel">
                            <i class="fas fa-user-circle me-2"></i>Thông tin chi tiết thành viên
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="text-center mb-4 mt-2" id="memberDetailLoading">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Đang tải...</span>
                            </div>
                            <p class="mt-2">Đang tải thông tin...</p>
                        </div>

                        <div id="memberDetailContent" style="display: none; transition: opacity 0.3s ease;">
                            <!-- Member Profile -->
                            <div class="row mb-4">
                                <div class="col-md-3 text-center">
                                    <div class="avatar-container mb-3">
                                        <img id="memberDetailAvatar" src="" alt="Avatar" 
                                             class="rounded-circle img-thumbnail shadow" style="width: 120px; height: 120px; object-fit: cover;">
                                    </div>
                                </div>
                                <div class="col-md-9">
                                    <h4 id="memberDetailName" class="mb-3 fw-bold">Tên thành viên</h4>

                                    <div class="member-info">
                                        <div class="row mb-2">
                                            <div class="col-md-6">
                                                <div class="d-flex align-items-center mb-2">
                                                    <i class="fas fa-envelope text-primary me-2"></i>
                                                    <small class="fw-semibold me-2">Email:</small>
                                                    <span id="memberDetailEmail">-</span>
                                                </div>
                                                <div class="d-flex align-items-center mb-2">
                                                    <i class="fas fa-users text-primary me-2"></i>
                                                    <small class="fw-semibold me-2">Gen:</small>
                                                    <span id="memberDetailGen">-</span>
                                                </div>
                                                <div class="d-flex align-items-center mb-2">
                                                    <i class="fas fa-calendar-alt text-primary me-2"></i>
                                                    <small class="fw-semibold me-2">Ngày tham gia:</small>
                                                    <span id="memberDetailJoinDate">-</span>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="d-flex align-items-center mb-2">
                                                    <i class="fas fa-birthday-cake text-primary me-2"></i>
                                                    <small class="fw-semibold me-2">Ngày sinh:</small>
                                                    <span id="memberDetailDateOfBirth">-</span>
                                                </div>
                                                <div class="d-flex align-items-center mb-2">
                                                    <i class="fas fa-info-circle text-primary me-2"></i>
                                                    <small class="fw-semibold me-2">Trạng thái:</small>
                                                    <span id="memberDetailStatusText" class="badge bg-success">Hoạt động</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Progress -->
                            <div class="row mb-4">
                                <div class="col-12">
                                    <div class="card shadow-sm border-0">
                                        <div class="card-body">
                                            <h5 class="card-title d-flex align-items-center">
                                                <i class="fas fa-chart-bar text-primary me-2"></i>Thống kê công việc
                                            </h5>
                                            <div class="row">
                                                <div class="col-md-6">
                                                    <div class="d-flex align-items-center mb-3 p-3 bg-light rounded task-stats">
                                                        <div class="me-3 text-primary">
                                                            <i class="fas fa-clipboard-list fa-2x"></i>
                                                        </div>
                                                        <div>
                                                            <h6 class="mb-0 text-muted">Công việc được giao</h6>
                                                            <h3 class="mb-0 fw-bold" id="memberDetailAssignedTasks">0</h3>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="col-md-6">
                                                    <div class="d-flex align-items-center mb-3 p-3 bg-light rounded task-stats">
                                                        <div class="me-3 text-success">
                                                            <i class="fas fa-clipboard-check fa-2x"></i>
                                                        </div>
                                                        <div>
                                                            <h6 class="mb-0 text-muted">Công việc hoàn thành</h6>
                                                            <h3 class="mb-0 fw-bold" id="memberDetailCompletedTasks">0</h3>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Tasks -->
                            <div class="row">
                                <div class="col-12">
                                    <div class="card shadow-sm border-0">
                                        <div class="card-header bg-transparent border-0">
                                            <h5 class="mb-0">
                                                <i class="fas fa-list-check text-primary me-2"></i>Danh sách công việc
                                            </h5>
                                        </div>
                                        <div class="card-body p-0">
                                            <div id="memberDetailEmptyTasks" class="text-center py-4 bg-light rounded m-3" style="display: none;">
                                                <i class="fas fa-clipboard text-muted fa-2x mb-2"></i>
                                                <p class="mb-1 fw-semibold">Không có công việc nào được giao</p>
                                                <p class="text-muted small">Thành viên này chưa có công việc nào.</p>
                                            </div>

                                            <div id="memberDetailTaskList" class="table-responsive">
                                                <table class="table table-hover mb-0">
                                                    <thead class="table-light">
                                                        <tr>
                                                            <th>Tiêu đề công việc</th>
                                                            <th>Trạng thái</th>
                                                            <th>Hạn hoàn thành</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody id="memberDetailTasksBody">
                                                        <!-- Tasks will be inserted here by JavaScript -->
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer bg-light">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times me-1"></i>Đóng
                        </button>
                    </div>
                </div>
            </div>
        </div><!-- Scripts -->    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/department-members.js"></script>
    </body>
</html>
