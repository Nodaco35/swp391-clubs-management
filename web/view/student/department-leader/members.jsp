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
                        <a href="${pageContext.request.contextPath}/department-dashboard?clubID=${clubID}" class="menu-link">
                            <i class="fas fa-chart-pie"></i>
                            <span>Dashboard</span>
                        </a>
                    </li>
                    <li class="menu-item active">
                        <a href="${pageContext.request.contextPath}/department-members?clubID=${clubID}" class="menu-link">
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
                        <a href="${pageContext.request.contextPath}/department-meeting" class="menu-link">
                            <i class="fas fa-calendar-alt"></i>
                            <span>Quản lý cuộc họp</span>
                        </a>
                    </li>
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/department/financial" class="menu-link">
                            <i class="fa-dollar-sign"></i>
                            <span>Tài chính</span>
                        </a>
                    </li>
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/" class="menu-link">
                            <i class="fas fa-home"></i>
                            <span>Về trang chủ</span>
                        </a>
                    </li>
                </ul>

                <div class="sidebar-footer">
                    <div class="user-info">                    <div class="user-avatar">
                            <img src="${pageContext.request.contextPath}/img/${not empty currentUser.avatar ? currentUser.avatar : 'Hinh-anh-dai-dien-mac-dinh-Facebook.jpg'}" alt="Avatar">
                        </div>
                        <div class="user-details">
                            <div class="user-name">${currentUser.fullName}</div>
                            <div class="user-role">Trưởng ban ${departmentName}</div>
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
                                    <span id="memberDetailStatus" class="badge bg-success mb-2">Hoạt động</span>
                                </div>
                                <div class="col-md-9">
                                    <h4 id="memberDetailName" class="mb-1 fw-bold">Tên thành viên</h4>
                                    <div id="memberDetailRole" class="mb-2 role-badge"></div>

                                    <div class="member-info">
                                        <div class="row mb-2">
                                            <div class="col-md-6">
                                                <div class="d-flex align-items-center mb-2">
                                                    <i class="fas fa-envelope text-primary me-2"></i>
                                                    <span id="memberDetailEmail"></span>
                                                </div>
                                                <div class="d-flex align-items-center mb-2">
                                                    <i class="fas fa-phone text-primary me-2"></i>
                                                    <span id="memberDetailPhone">-</span>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="d-flex align-items-center mb-2">
                                                    <i class="fas fa-id-card text-primary me-2"></i>
                                                    <span id="memberDetailStudentCode">-</span>
                                                </div>
                                                <div class="d-flex align-items-center mb-2">
                                                    <i class="fas fa-graduation-cap text-primary me-2"></i>
                                                    <span id="memberDetailMajor">-</span>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="mb-2">
                                            <i class="fas fa-calendar-alt text-primary me-2"></i>
                                            Tham gia ban từ: <strong id="memberDetailJoinDate"></strong>
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

                                            <div class="progress mt-3">
                                                <div id="memberDetailProgress" class="progress-bar bg-success" style="width: 0%; transition: width 1s ease-in-out;"></div>
                                            </div>
                                            <div class="d-flex justify-content-between mt-2">
                                                <small class="text-muted">Tiến độ hoàn thành công việc</small>
                                                <small class="fw-bold" id="memberDetailProgressText">0% hoàn thành</small>
                                            </div>

                                            <div class="mt-3 text-center">
                                                <small id="memberDetailLastActivity" class="d-inline-block">
                                                    <i class="fas fa-history me-1"></i>
                                                    Hoạt động gần nhất: -
                                                </small>
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
                                                            <th>Ưu tiên</th>
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
