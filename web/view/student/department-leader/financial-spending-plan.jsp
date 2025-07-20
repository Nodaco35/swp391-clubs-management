<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý kế hoạch chi tiêu - Kỳ ${term.termID} - ${club.ClubName}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
</head>
<body>
    <div class="department-leader-container">
        <!-- Sidebar (same as financial-expense.jsp) -->
        <nav class="sidebar">
            <div class="sidebar-header">
                <div class="logo">
                    <i class="fas fa-users-gear"></i>
                    <span>Quản lý Ban</span>
                </div>
            </div>
            <ul class="sidebar-menu">
                <li class="menu-item ${activeMenu == 'dashboard' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/department-dashboard?clubID=${clubID}" class="menu-link">
                        <i class="fas fa-chart-pie"></i>
                        <span>Dashboard</span>
                    </a>
                </li>
                <li class="menu-item ${activeMenu == 'members' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/department-members?clubID=${clubID}" class="menu-link">
                        <i class="fas fa-users"></i>
                        <span>Quản lý thành viên</span>
                    </a>
                </li>
                <li class="menu-item ${activeMenu == 'tasks' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/department-tasks" class="menu-link">
                        <i class="fas fa-tasks"></i>
                        <span>Quản lý công việc</span>
                    </a>
                </li>
                <li class="menu-item ${activeMenu == 'meeting' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/department-meeting" class="menu-link">
                        <i class="fas fa-calendar-alt"></i>
                        <span>Quản lý cuộc họp</span>
                    </a>
                </li>
                <c:if test="${isAccess}">
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/department/financial?clubID=${clubID}" class="menu-link">
                            <i class="fas fa-dollar-sign"></i>
                            <span>Tài chính</span>
                        </a>
                    </li>
                </c:if>
                <c:if test="${isAccessSpending}">
                    <li class="menu-item ${activeMenu == 'financial' ? 'active' : ''}">
                        <a href="${pageContext.request.contextPath}/department/spending-plan?clubID=${clubID}" class="menu-link">
                            <i class="fas fa-dollar-sign"></i>
                            <span>Chi tiêu chi tiết</span>
                        </a>
                    </li>
                </c:if>
                <li class="menu-item ${activeMenu == 'home' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/" class="menu-link">
                        <i class="fas fa-home"></i>
                        <span>Về trang chủ</span>
                    </a>
                </li>
            </ul>
            <div class="sidebar-footer">
                <div class="user-info">
                    <div class="user-avatar">
                        <img src="${sessionScope.user.avatar != null ? sessionScope.user.avatar : 'https://via.placeholder.com/50'}" alt="Avatar">
                    </div>
                    <div class="user-details">
                        <div class="user-name">${sessionScope.user.fullName}</div>
                        <div class="user-role">Trưởng ban</div>
                    </div>
                </div>
            </div>
        </nav>

        <!-- Mobile Menu Toggle (same) -->
        <button class="mobile-menu-toggle" onclick="toggleSidebar()">
            <i class="fas fa-bars"></i>
        </button>

        <!-- Main Content -->
        <main class="main-content">
            <header class="header mb-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1 class="h3 mb-1">Quản lý kế hoạch chi tiêu - Kỳ ${term.termID}</h1>
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb mb-0">
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/department-dashboard?clubID=${clubID}" class="text-decoration-none">Dashboard</a>
                                </li>
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/department/financial" class="text-decoration-none">Tài chính</a>
                                </li>
                                <li class="breadcrumb-item active" aria-current="page">Kế hoạch chi tiêu</li>
                            </ol>
                        </nav>
                    </div>
                </div>
            </header>

            <c:if test="${not empty message}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="modal fade show" id="errorModal" tabindex="-1" style="display: block;" aria-labelledby="errorModalLabel" aria-modal="true" role="dialog">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header bg-danger text-white">
                                <h5 class="modal-title" id="errorModalLabel">Lỗi</h5>
                                <button type="button" class="btn-close btn-close-white" onclick="closeErrorModal()" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                ${error}
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-danger" onclick="closeErrorModal()">Đóng</button>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>

            <div class="row mb-4">
                <div class="col-12">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <div class="row align-items-center">
                                <div class="col-md-6">
                                    <form action="${pageContext.request.contextPath}/department/spending-plan?clubID=${clubID}" method="get">
                                        <div class="input-group">
                                            <span class="input-group-text">
                                                <i class="fas fa-search"></i>
                                            </span>
                                            <input type="text" name="keyword" class="form-control" 
                                                   placeholder="Tìm kiếm theo tên kế hoạch hoặc sự kiện..." 
                                                   value="${keyword}">
                                            <select name="status" class="form-select">
                                                <option value="all" ${status == 'all' ? 'selected' : ''}>Tất cả</option>
                                                <option value="Chưa bắt đầu" ${status == 'Chưa bắt đầu' ? 'selected' : ''}>Chưa bắt đầu</option>
                                                <option value="Đang thực hiện" ${status == 'Đang thực hiện' ? 'selected' : ''}>Đang thực hiện</option>
                                                <option value="Hoàn thành" ${status == 'Hoàn thành' ? 'selected' : ''}>Hoàn thành</option>
                                            </select>
                                            <button class="btn btn-outline-primary" type="submit">Lọc</button>
                                        </div>
                                    </form>
                                </div>
                                <div class="col-md-6 text-end">
                                    <button class="btn btn-primary" onclick="toggleAddPlanForm()">
                                        <i class="fas fa-plus me-2"></i>Tạo kế hoạch
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row mb-4 ${showAddForm ? 'd-block' : 'd-none'}" id="addPlanForm">
                <div class="col-12">
                    <div class="card shadow-sm">
                        <div class="card-header bg-white">
                            <h5 class="mb-0">
                                <i class="fas fa-plus me-2"></i>
                                Tạo kế hoạch chi tiêu mới
                            </h5>
                        </div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/department/spending-plan?clubID=${clubID}" method="post" id="formData">
                                <input type="hidden" name="action" value="addPlan">
                                <div class="mb-3">
                                    <label for="planName" class="form-label">Tên kế hoạch</label>
                                    <input type="text" name="planName" id="planName" class="form-control" required>
                                </div>
                     

                                <div class="mb-3">
                                    <label for="eventID" class="form-label">Sự kiện liên quan (tùy chọn)</label>
                                    <select name="eventID" id="eventID" class="form-select">
                                        <option value="">Không liên kết</option>
                                        <c:forEach var="event" items="${eventsList}">
                                            <option value="${event.eventID}">${event.eventName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label for="totalPlannedBudget" class="form-label">Tổng ngân sách kế hoạch</label>
                                    <input type="number" name="totalPlannedBudget" id="totalPlannedBudget" step="0.01" class="form-control" required>
                                </div>
                                <div id="itemsContainer">
                                    <div class="mb-3">
                                        <label class="form-label">Hạng mục chi tiêu</label>
                                        <div class="input-group mb-2">
                                            <input type="text" name="category[]" class="form-control" placeholder="Tên hạng mục" required>
                                            <input type="number" name="plannedAmount[]" class="form-control" placeholder="Số tiền dự kiến" step="0.01" required>
                                            <textarea name="description[]" class="form-control" placeholder="Mô tả"></textarea>
                                        </div>
                                    </div>
                                </div>
                                <button type="button" class="btn btn-secondary mb-3" onclick="addItemField()">Thêm hạng mục</button>
                                <div class="d-flex justify-content-end">
                                    <button type="button" class="btn btn-secondary me-2" onclick="toggleAddPlanForm()">Hủy</button>
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-save me-2"></i>
                                        Thêm
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <div class="card shadow-sm">
                        <div class="card-header bg-white">
                            <div class="d-flex justify-content-between align-items-center">
                                <h5 class="mb-0">
                                    <i class="fas fa-list me-2"></i>Danh sách kế hoạch chi tiêu
                                </h5>
                                <span class="badge bg-primary">${totalRecords} kế hoạch</span>
                            </div>
                        </div>
                        <div class="card-body p-0">
                            <c:if test="${not empty plansList}">
                                <div class="table-responsive">
                                    <table class="table table-hover mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th scope="col" class="ps-4">ID</th>
                                                <th scope="col">Tên kế hoạch</th>
                                                <th scope="col">Sự kiện</th>
                                                <th scope="col">Tổng kế hoạch</th>
                                                <th scope="col">Tổng thực tế</th>
                                                <th scope="col">Trạng thái</th>
                                                <th scope="col" class="text-center">Hành động</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="plan" items="${plansList}">
                                                <tr>
                                                    <td class="ps-4">${plan.planID}</td>
                                                    <td>${plan.planName}</td>
                                                    <td>${plan.eventID != null ? 'Event ' + plan.eventID : 'Không liên kết'}</td>
                                                    <td><fmt:formatNumber value="${plan.totalPlannedBudget}" type="currency" currencySymbol="₫" groupingUsed="true"/></td>
                                                    <td><fmt:formatNumber value="${plan.totalActual}" type="currency" currencySymbol="₫" groupingUsed="true"/></td> <!-- Assume totalActual from query -->
                                                    <td>
                                                        <span class="badge ${plan.status == 'Hoàn thành' ? 'bg-success' : (plan.status == 'Đang thực hiện' ? 'bg-warning' : 'bg-secondary')}">
                                                            ${plan.status}
                                                        </span>
                                                    </td>
                                                    <td class="text-center">
                                                        <div class="btn-group" role="group">
                                                            <button class="btn btn-sm btn-outline-primary" onclick="viewItems(${plan.planID})" title="Xem hạng mục">
                                                                <i class="fas fa-eye"></i>
                                                            </button>
                                                            <!-- Additional actions: edit, delete, update status -->
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:if>
                            <c:if test="${empty plansList}">
                                <div class="text-center py-5">
                                    <div class="mb-3">
                                        <i class="fas fa-box-open fa-3x text-muted"></i>
                                    </div>
                                    <h5 class="text-muted">Không có kế hoạch nào</h5>
                                    <p class="text-muted">Chưa có kế hoạch nào được tạo hoặc không tìm thấy kết quả phù hợp.</p>
                                    <button class="btn btn-primary" onclick="toggleAddPlanForm()">
                                        <i class="fas fa-plus me-2"></i>Tạo kế hoạch đầu tiên
                                    </button>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>

            <c:if test="${totalPages > 1}">
                <div class="row mt-4">
                    <div class="col-12">
                        <nav aria-label="Expenses pagination">
                            <ul class="pagination justify-content-center">
                                <c:if test="${currentPage > 1}">
                                    <li class="page-item">
                                        <a class="page-link" href="?page=${currentPage - 1}&keyword=${keyword}&status=${status}&clubID=${clubID}" aria-label="Previous">
                                            <span aria-hidden="true">«</span>
                                        </a>
                                    </li>
                                </c:if>
                                <c:set var="startPage" value="${currentPage - 2 > 0 ? currentPage - 2 : 1}" />
                                <c:set var="endPage" value="${currentPage + 2 <= totalPages ? currentPage + 2 : totalPages}" />
                                <c:if test="${startPage > 1}">
                                    <li class="page-item">
                                        <a class="page-link" href="?page=1&keyword=${keyword}&status=${status}&clubID=${clubID}">1</a>
                                    </li>
                                    <c:if test="${startPage > 2}">
                                        <li class="page-item disabled">
                                            <span class="page-link">...</span>
                                        </li>
                                    </c:if>
                                </c:if>
                                <c:forEach begin="${startPage}" end="${endPage}" var="i">
                                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                                        <c:choose>
                                            <c:when test="${i == currentPage}">
                                                <span class="page-link">${i}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <a class="page-link" href="?page=${i}&keyword=${keyword}&status=${status}&clubID=${clubID}">${i}</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </li>
                                </c:forEach>
                                <c:if test="${endPage < totalPages}">
                                    <c:if test="${endPage < totalPages - 1}">
                                        <li class="page-item disabled">
                                            <span class="page-link">...</span>
                                        </li>
                                    </c:if>
                                    <li class="page-item">
                                        <a class="page-link" href="?page=${totalPages}&keyword=${keyword}&status=${status}&clubID=${clubID}">${totalPages}</a>
                                    </li>
                                </c:if>
                                <c:if test="${currentPage < totalPages}">
                                    <li class="page-item">
                                        <a class="page-link" href="?page=${currentPage + 1}&keyword=${keyword}&status=${status}&clubID=${clubID}" aria-label="Next">
                                            <span aria-hidden="true">»</span>
                                        </a>
                                    </li>
                                </c:if>
                            </ul>
                        </nav>
                        <div class="text-center mt-2">
                            <small class="text-muted">
                                Hiển thị trang ${currentPage} / ${totalPages} - Tổng ${totalRecords} chi tiêu
                            </small>
                        </div>
                    </div>
                </div>
            </c:if>
        </main>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function toggleSidebar() {
            document.querySelector('.sidebar').classList.toggle('active');
        }

        function toggleAddPlanForm() {
            const form = document.getElementById('addPlanForm');
            form.classList.toggle('d-none');
            form.classList.toggle('d-block');
        }

        function addItemField() {
            const container = document.getElementById('itemsContainer');
            const newItem = document.createElement('div');
            newItem.classList.add('mb-3');
            newItem.innerHTML = `
                <div class="input-group mb-2">
                    <input type="text" name="category[]" class="form-control" placeholder="Tên hạng mục" required>
                    <input type="number" name="plannedAmount[]" class="form-control" placeholder="Số tiền dự kiến" step="0.01" required>
                    <textarea name="description[]" class="form-control" placeholder="Mô tả"></textarea>
                    <button type="button" class="btn btn-danger" onclick="this.parentElement.parentElement.remove()">Xóa</button>
                </div>
            `;
            container.appendChild(newItem);
        }

        function closeErrorModal() {
            document.getElementById('errorModal').style.display = 'none';
        }

        // Function to view plan items (could be a modal fetch via AJAX)
        function viewItems(planID) {
            // Implement AJAX to fetch and display items
            console.log('View items for plan ' + planID);
        }

        document.addEventListener('DOMContentLoaded', function () {
            <c:if test="${not empty error}">
            document.getElementById('errorModal').style.display = 'block';
            </c:if>
        });
    </script>
</body>
</html>