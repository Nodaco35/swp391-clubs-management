<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">    <title>Quản lý thành viên - Ban ${sessionScope.departmentName}</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
</head>
<body>
    <div class="department-leader-container">
        <!-- Sidebar -->
        <nav class="sidebar">
            <div class="sidebar-header">
                <div class="logo">
                    <i class="fas fa-users-gear"></i>
                    <span>Quản lý Ban</span>
                </div>
            </div>
            
            <ul class="sidebar-menu">
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/department-dashboard" class="menu-link">
                        <i class="fas fa-chart-pie"></i>
                        <span>Dashboard</span>
                    </a>
                </li>
                <li class="menu-item active">
                    <a href="${pageContext.request.contextPath}/department-members" class="menu-link">
                        <i class="fas fa-users"></i>
                        <span>Quản lý thành viên</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/department-tasks" class="menu-link">
                        <i class="fas fa-tasks"></i>
                        <span>Quản lý công việc</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/homepage" class="menu-link">
                        <i class="fas fa-home"></i>
                        <span>Về trang chủ</span>
                    </a>
                </li>
            </ul>
            
            <div class="sidebar-footer">
                <div class="user-info">
                    <div class="user-avatar">
                        <img src="${pageContext.request.contextPath}/img/${sessionScope.user.avatar != null ? sessionScope.user.avatar : 'Hinh-anh-dai-dien-mac-dinh-Facebook.jpg'}" alt="Avatar">
                    </div>
                    <div class="user-details">
                        <div class="user-name">${sessionScope.user.fullName}</div>
                        <div class="user-role">Trưởng ban</div>
                    </div>
                </div>
            </div>
        </nav>

        <!-- Main Content -->
        <main class="main-content">            <!-- Header -->
            <header class="header mb-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1 class="h3 mb-1">Quản lý thành viên</h1>
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb mb-0">
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/department-dashboard" class="text-decoration-none">
                                        Dashboard
                                    </a>
                                </li>
                                <li class="breadcrumb-item active" aria-current="page">Thành viên</li>
                            </ol>
                        </nav>
                    </div>
                </div>
            </header><!-- Search and Filter Section -->
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
                                               value="${keyword}">
                                        <button class="btn btn-outline-primary" type="button" onclick="searchMembers()">
                                            Tìm kiếm
                                        </button>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <select id="statusFilter" class="form-select" onchange="filterMembers()">
                                        <option value="">Tất cả trạng thái</option>
                                        <option value="active">Hoạt động</option>
                                        <option value="inactive">Không hoạt động</option>
                                    </select>
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
            </div>            <!-- Stats Cards -->
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
                            <div class="d-flex align-items-center">
                                <div class="flex-grow-1">
                                    <h5 class="card-title mb-1">Hoạt động</h5>
                                    <h3 class="mb-0" id="activeCount">0</h3>
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
                            <div class="d-flex align-items-center">
                                <div class="flex-grow-1">
                                    <h5 class="card-title mb-1">Không hoạt động</h5>
                                    <h3 class="mb-0" id="inactiveCount">0</h3>
                                </div>
                                <div class="ms-3">
                                    <i class="fas fa-user-clock fa-2x opacity-75"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Members List -->
            <div class="row">
                <div class="col-12">
                    <div class="card shadow-sm">
                        <div class="card-header bg-white">
                            <div class="d-flex justify-content-between align-items-center">
                                <h5 class="mb-0">
                                    <i class="fas fa-users me-2"></i>Danh sách thành viên
                                </h5>
                                <span class="badge bg-primary">${totalMembers} thành viên</span>
                            </div>
                        </div>
                        
                        <div class="card-body p-0">
                            <c:if test="${not empty members}">
                                <div class="table-responsive">
                                    <table class="table table-hover mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th scope="col" class="ps-4">Thành viên</th>
                                                <th scope="col">Vai trò</th>
                                                <th scope="col">Ngày tham gia</th>
                                                <th scope="col">Công việc</th>
                                                <th scope="col">Trạng thái</th>
                                                <th scope="col" class="text-center">Hành động</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="member" items="${members}">
                                                <tr data-member-id="${member.userID}" class="${member.active ? 'table-light' : 'table-secondary'}">
                                                    <td class="ps-4">
                                                        <div class="d-flex align-items-center">
                                                            <div class="avatar-container me-3">
                                                                <img src="${pageContext.request.contextPath}/img/${member.avatar != null ? member.avatar : 'Hinh-anh-dai-dien-mac-dinh-Facebook.jpg'}" 
                                                                     alt="Avatar" class="rounded-circle" style="width: 45px; height: 45px; object-fit: cover;">
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
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${member.roleName == 'Trưởng ban'}">
                                                                <span class="badge bg-danger">${member.roleName}</span>
                                                            </c:when>
                                                            <c:when test="${member.roleName == 'Phó ban'}">
                                                                <span class="badge bg-warning">${member.roleName}</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-secondary">${member.roleName != null ? member.roleName : 'Thành viên'}</span>
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
                                                    </td>
                                                    <td>
                                                        <div class="form-check form-switch">
                                                            <input class="form-check-input" type="checkbox" 
                                                                   ${member.active ? 'checked' : ''} 
                                                                   onchange="updateMemberStatus('${member.userID}', this.checked)">
                                                            <label class="form-check-label">
                                                                <span class="badge ${member.active ? 'bg-success' : 'bg-secondary'}">
                                                                    ${member.active ? 'Hoạt động' : 'Không hoạt động'}
                                                                </span>
                                                            </label>
                                                        </div>
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
                            </c:if>
                            
                            <c:if test="${empty members}">
                                <div class="text-center py-5">
                                    <div class="mb-3">
                                        <i class="fas fa-users-slash fa-3x text-muted"></i>
                                    </div>
                                    <h5 class="text-muted">Không có thành viên nào</h5>
                                    <p class="text-muted">Chưa có thành viên nào trong ban hoặc không tìm thấy kết quả phù hợp.</p>
                                    <button class="btn btn-primary" onclick="showAddMemberModal()">
                                        <i class="fas fa-plus me-2"></i>Thêm thành viên đầu tiên
                                    </button>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <div class="row mt-4">
                    <div class="col-12">
                        <nav aria-label="Members pagination">
                            <ul class="pagination justify-content-center">
                                <!-- Previous button -->
                                <c:if test="${currentPage > 1}">
                                    <li class="page-item">
                                        <a class="page-link" href="?page=${currentPage - 1}&keyword=${keyword}" 
                                           aria-label="Previous">
                                            <span aria-hidden="true">&laquo;</span>
                                        </a>
                                    </li>
                                </c:if>
                                
                                <!-- Page numbers -->
                                <c:set var="startPage" value="${currentPage - 2 > 0 ? currentPage - 2 : 1}" />
                                <c:set var="endPage" value="${currentPage + 2 <= totalPages ? currentPage + 2 : totalPages}" />
                                
                                <!-- First page if needed -->
                                <c:if test="${startPage > 1}">
                                    <li class="page-item">
                                        <a class="page-link" href="?page=1&keyword=${keyword}">1</a>
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
                                                <a class="page-link" href="?page=${page}&keyword=${keyword}">${page}</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </li>
                                </c:forEach>
                                
                                <!-- Last page if needed -->
                                <c:if test="${endPage < totalPages}">
                                    <c:if test="${endPage < totalPages - 1}">
                                        <li class="page-item disabled">
                                            <span class="page-link">...</span>
                                        </li>
                                    </c:if>
                                    <li class="page-item">
                                        <a class="page-link" href="?page=${totalPages}&keyword=${keyword}">${totalPages}</a>
                                    </li>
                                </c:if>
                                
                                <!-- Next button -->
                                <c:if test="${currentPage < totalPages}">
                                    <li class="page-item">
                                        <a class="page-link" href="?page=${currentPage + 1}&keyword=${keyword}" 
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
                                            <option value="5">Phó ban</option>
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
    </div>    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        let selectedUserId = null;
        let searchTimeout = null;
        let addMemberModal = null;

        // Initialize modal
        $(document).ready(function() {
            addMemberModal = new bootstrap.Modal(document.getElementById('addMemberModal'));
            updateMemberCounts();
        });

        // Search members
        function searchMembers() {
            const keyword = document.getElementById('searchInput').value;
            const url = new URL(window.location);
            url.searchParams.set('keyword', keyword);
            url.searchParams.set('page', '1');
            window.location.href = url.toString();
        }

        // Enter key search
        document.getElementById('searchInput').addEventListener('keypress', function(e) {
            if (e.key == 'Enter') {
                searchMembers();
            }
        });

        // Update member status
        function updateMemberStatus(userId, isActive) {
            $.ajax({
                url: 'department-members',
                type: 'POST',
                data: {
                    action: 'updateStatus',
                    userID: userId,
                    isActive: isActive,
                    status: isActive ? 'Active' : 'Inactive'
                },
                success: function(response) {
                    if (response.success) {
                        // Update UI
                        const row = $(`tr[data-member-id="${userId}"]`);
                        const statusBadge = row.find('.badge');
                        
                        if (isActive) {
                            row.removeClass('table-secondary').addClass('table-light');
                            statusBadge.removeClass('bg-secondary').addClass('bg-success').text('Hoạt động');
                        } else {
                            row.removeClass('table-light').addClass('table-secondary');
                            statusBadge.removeClass('bg-success').addClass('bg-secondary').text('Không hoạt động');
                        }
                        
                        showNotification('Cập nhật trạng thái thành công!', 'success');
                        updateMemberCounts();
                    } else {
                        showNotification('Không thể cập nhật trạng thái!', 'error');
                        // Revert checkbox
                        const checkbox = $(`tr[data-member-id="${userId}"] input[type="checkbox"]`);
                        checkbox.prop('checked', !isActive);
                    }
                },
                error: function() {
                    showNotification('Có lỗi xảy ra!', 'error');
                    // Revert checkbox
                    const checkbox = $(`tr[data-member-id="${userId}"] input[type="checkbox"]`);
                    checkbox.prop('checked', !isActive);
                }
            });
        }

        // Remove member
        function confirmRemoveMember(userId, fullName) {
            if (confirm(`Bạn có chắc chắn muốn xóa "${fullName}" khỏi ban?`)) {
                removeMember(userId);
            }
        }

        function removeMember(userId) {
            $.ajax({
                url: 'department-members',
                type: 'POST',
                data: {
                    action: 'removeMember',
                    userID: userId
                },
                success: function(response) {
                    if (response.success) {
                        $(`tr[data-member-id="${userId}"]`).fadeOut(300, function() {
                            $(this).remove();
                            updateMemberCounts();
                        });
                        showNotification('Xóa thành viên thành công!', 'success');
                    } else {
                        showNotification('Không thể xóa thành viên!', 'error');
                    }
                },
                error: function() {
                    showNotification('Có lỗi xảy ra!', 'error');
                }
            });
        }

        // Add member modal
        function showAddMemberModal() {
            addMemberModal.show();
            document.getElementById('studentSearchInput').focus();
        }

        function closeAddMemberModal() {
            addMemberModal.hide();
            resetModalForm();
        }

        function resetModalForm() {
            document.getElementById('studentSearchInput').value = '';
            document.getElementById('studentSearchResults').innerHTML = '';
            document.getElementById('selectedStudent').style.display = 'none';
            selectedUserId = null;
            document.getElementById('addMemberBtn').disabled = true;
        }

        // Search students
        document.getElementById('studentSearchInput').addEventListener('input', function() {
            const keyword = this.value.trim();
            
            if (searchTimeout) {
                clearTimeout(searchTimeout);
            }
            
            if (keyword.length < 2) {
                document.getElementById('studentSearchResults').innerHTML = '';
                return;
            }
            
            searchTimeout = setTimeout(() => {
                $.ajax({
                    url: 'department-members',
                    type: 'GET',
                    data: {
                        action: 'searchStudents',
                        keyword: keyword
                    },
                    success: function(students) {
                        displaySearchResults(students);
                    },
                    error: function() {
                        document.getElementById('studentSearchResults').innerHTML = 
                            '<div class="alert alert-danger">Có lỗi khi tìm kiếm sinh viên</div>';
                    }
                });
            }, 300);
        });

        function displaySearchResults(students) {
            const resultsContainer = document.getElementById('studentSearchResults');
            
            if (students.length == 0) {
                resultsContainer.innerHTML = '<div class="text-muted text-center py-2">Không tìm thấy sinh viên nào</div>';
                return;
            }
            
            let html = '<div class="list-group">';
            students.forEach(student => {
                html += `
                    <a href="#" class="list-group-item list-group-item-action" 
                       onclick="selectStudent('${student.userID}', '${student.fullName}', '${student.email}', '${student.avatar || ''}')">
                        <div class="d-flex align-items-center">
                            <img src="${pageContext.request.contextPath}/img/${student.avatar || 'Hinh-anh-dai-dien-mac-dinh-Facebook.jpg'}" 
                                 alt="Avatar" class="rounded-circle me-3" style="width: 40px; height: 40px; object-fit: cover;">
                            <div>
                                <div class="fw-semibold">${student.fullName}</div>
                                <small class="text-muted">${student.email}</small>
                            </div>
                        </div>
                    </a>
                `;
            });
            html += '</div>';
            
            resultsContainer.innerHTML = html;
        }

        function selectStudent(userId, fullName, email, avatar) {
            selectedUserId = userId;
            
            const selectedStudentDiv = document.getElementById('selectedStudent');
            selectedStudentDiv.querySelector('.student-info').innerHTML = `
                <div class="d-flex align-items-center">
                    <img src="${pageContext.request.contextPath}/img/${avatar || 'Hinh-anh-dai-dien-mac-dinh-Facebook.jpg'}" 
                         alt="Avatar" class="rounded-circle me-3" style="width: 50px; height: 50px; object-fit: cover;">
                    <div>
                        <div class="fw-semibold">${fullName}</div>
                        <small class="text-muted">${email}</small>
                    </div>
                </div>
            `;
            
            selectedStudentDiv.style.display = 'block';
            document.getElementById('studentSearchResults').innerHTML = '';
            document.getElementById('studentSearchInput').value = fullName;
            document.getElementById('addMemberBtn').disabled = false;
        }

        function addMember() {
            if (!selectedUserId) {
                showNotification('Vui lòng chọn sinh viên!', 'error');
                return;
            }
            
            const roleId = document.getElementById('memberRole').value;
            const clubId = ${sessionScope.clubID || 1}; // Get from session or default
            
            $.ajax({
                url: 'department-members',
                type: 'POST',
                data: {
                    action: 'addMember',
                    userID: selectedUserId,
                    clubID: clubId,
                    roleID: roleId
                },
                success: function(response) {
                    if (response.success) {
                        showNotification('Thêm thành viên thành công!', 'success');
                        closeAddMemberModal();
                        setTimeout(() => {
                            window.location.reload();
                        }, 1000);
                    } else {
                        showNotification('Không thể thêm thành viên!', 'error');
                    }
                },
                error: function() {
                    showNotification('Có lỗi xảy ra!', 'error');
                }
            });
        }

        // Utility functions
        function updateMemberCounts() {
            const activeMembers = $('.table-light').length;
            const inactiveMembers = $('.table-secondary').length;
            
            $('#activeCount').text(activeMembers);
            $('#inactiveCount').text(inactiveMembers);
        }

        function showNotification(message, type) {
            // Create notification with Bootstrap toast
            const toastHtml = `
                <div class="toast align-items-center text-white bg-${type == 'success' ? 'success' : 'danger'} border-0" 
                     role="alert" aria-live="assertive" aria-atomic="true">
                    <div class="d-flex">
                        <div class="toast-body">
                            <i class="fas ${getNotificationIcon(type)} me-2"></i>${message}
                        </div>
                        <button type="button" class="btn-close btn-close-white me-2 m-auto" 
                                data-bs-dismiss="toast" aria-label="Close"></button>
                    </div>
                </div>
            `;
            
            // Create toast container if not exists
            if (!$('.toast-container').length) {
                $('body').append('<div class="toast-container position-fixed top-0 end-0 p-3"></div>');
            }
            
            const $toast = $(toastHtml);
            $('.toast-container').append($toast);
            
            const toast = new bootstrap.Toast($toast[0]);
            toast.show();
            
            // Remove toast element after it's hidden
            $toast.on('hidden.bs.toast', function() {
                $(this).remove();
            });
        }
        
        function getNotificationIcon(type) {
            switch(type) {
                case 'success': return 'fa-check-circle';
                case 'error': return 'fa-exclamation-circle';
                case 'warning': return 'fa-exclamation-triangle';
                case 'info': return 'fa-info-circle';
                default: return 'fa-bell';
            }
        }

        function viewMemberDetail(userId) {
            // TODO: Implement member detail view
            showNotification('Tính năng đang phát triển!', 'info');
        }

        function filterMembers() {
            // TODO: Implement status filter
            showNotification('Tính năng đang phát triển!', 'info');
        }

        // Reset form when modal is hidden
        document.getElementById('addMemberModal').addEventListener('hidden.bs.modal', function() {
            resetModalForm();
        });
    </script>
</body>
</html>
