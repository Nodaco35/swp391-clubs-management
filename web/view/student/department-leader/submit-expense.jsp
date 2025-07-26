<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Yêu Cầu Chi Tiêu</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
        <style>
            .expense-table th, .expense-table td {
                vertical-align: middle;
            }
            .status-pending {
                color: #ffc107;
                font-weight: bold;
            }
            .status-approved {
                color: #28a745;
                font-weight: bold;
            }
            .status-rejected {
                color: #dc3545;
                font-weight: bold;
            }
            .pagination-container {
                display: flex;
                justify-content: center;
                margin-top: 1rem;
            }
            .sortable:hover {
                background-color: rgba(0, 0, 0, 0.05);
                cursor: pointer;
            }
            .sort-icon {
                margin-left: 5px;
                font-size: 0.8em;
            }
        </style>
    </head>
    <body>
        <div class="department-leader-container">
            <!-- Sidebar -->
            <c:set var="activePage" value="submit-expense" />
            <%@ include file="components/sidebar.jsp" %>

            <!-- Main Content -->
            <main class="main-content">
                <header class="header mb-4">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h1 class="h3 mb-1">Gửi đơn yêu cầu chi tiêu</h1>
                            <nav aria-label="breadcrumb">
                                <ol class="breadcrumb mb-0">
                                    <li class="breadcrumb-item">
                                        <a href="${pageContext.request.contextPath}/department-dashboard?clubID=${clubID}" class="text-decoration-none">Dashboard</a>
                                    </li>
                                    <li class="breadcrumb-item active" aria-current="page">Gửi Yêu Cầu Chi Tiêu</li>
                                </ol>
                        </div>
                    </div>
                </header>

                <div class="content-section">
                    <!-- Expense Submission Form -->
                    <div class="card enhanced-card mb-4">
                        <div class="card-header">
                            <h2 class="card-title">
                                <i class="fas fa-file-invoice-dollar" style="color: #8b5cf6;"></i>
                                Gửi đơn yêu cầu chi tiêu Mới (Kỳ: ${term.termName})
                            </h2>
                            <button class="btn btn-primary" type="button" data-bs-toggle="collapse" data-bs-target="#expenseFormCollapse" aria-expanded="false" aria-controls="expenseFormCollapse">
                                <i class="fas fa-plus"></i> Tạo Đơn
                            </button>
                        </div>
                        <div class="card-body collapse" id="expenseFormCollapse">
                            <form id="expenseForm" action="${pageContext.request.contextPath}/department/submit-expense" method="post">
                                <input type="hidden" name="clubID" value="${clubID}">
                                <div class="mb-3">
                                    <label for="purpose" class="form-label">Mục đích</label>
                                    <select id="purpose" name="purpose" class="form-select" required>
                                        <option value="" disabled selected-press>Chọn mục đích</option>
                                        <option value="Sự kiện">Sự kiện</option>
                                        <option value="Vật tư">Vật tư</option>
                                        <option value="Thuê địa điểm">Thuê địa điểm</option>
                                        <option value="Khác">Khác</option>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label for="amount" class="form-label">Số tiền (VND)</label>
                                    <input type="number" id="amount" name="amount" class="form-control" step="0.01" required>
                                </div>
                                <div class="mb-3">
                                    <label for="expenseDate" class="form-label">Ngày chi tiêu</label>
                                    <input type="date" id="expenseDate" name="expenseDate" class="form-control" required>
                                </div>
                                <div class="mb-3">
                                    <label for="description" class="form-label">Mô tả</label>
                                    <textarea id="description" name="description" class="form-control" rows="4"></textarea>
                                </div>
                                <div class="mb-3">
                                    <label for="attachment" class="form-label">Link Google Docs</label>
                                    <input type="text" id="attachment" name="attachment" class="form-control" placeholder="https://docs.google.com/..." pattern="https:\/\/docs\.google\.com\/.*">
                                </div>
                                <div class="d-flex justify-content-end gap-2">
                                    <button type="button" class="btn btn-secondary" data-bs-toggle="collapse" data-bs-target="#expenseFormCollapse">Đóng</button>
                                    <button type="submit" class="btn btn-primary">Gửi Đơn</button>
                                </div>

                            </form>
                        </div>
                    </div>

                    <!-- Submitted Expenses List -->
                    <div class="card enhanced-card">
                        <div class="card-header">
                            <h2 class="card-title">
                                <i class="fas fa-list" style="color: #8b5cf6;"></i>
                                Danh Sách Yêu Cầu Chi Tiêu Đã Gửi (Kỳ: ${term.termName})
                            </h2>
                        </div>
                        <div class="card-body">
                            <c:if test="${not empty message}">
                                <div class="alert alert-success">${message}</div>
                            </c:if>
                            <c:if test="${not empty error}">
                                <div class="alert alert-danger">${error}</div>
                            </c:if>
                            <!-- Search and Filter -->
                            <div class="filter-controls mb-3">
                                <div class="filter-group">
                                    <label class="filter-label">Tìm kiếm</label>
                                    <input type="text" id="searchInput" class="form-control" placeholder="Tìm theo mô tả, mục đích hoặc số tiền" value="${param.search}">
                                </div>
                                <div class="filter-group">
                                    <label class="filter-label">Trạng thái</label>
                                    <select id="statusFilter" class="form-select">
                                        <option value="" ${param.status == '' ? 'selected' : ''}>Tất cả</option>
                                        <option value="Pending" ${param.status == 'Pending' ? 'selected' : ''}>Chờ duyệt</option>
                                        <option value="Approved" ${param.status == 'Approved' ? 'selected' : ''}>Đã duyệt</option>
                                        <option value="Rejected" ${param.status == 'Rejected' ? 'selected' : ''}>Đã từ chối</option>
                                    </select>
                                </div>
                                <div class="filter-group">
                                    <label class="filter-label">Sắp xếp theo</label>
                                    <select id="sortBy" class="form-select">
                                        <option value="createdAt-desc" ${param.sortBy == 'createdAt-desc' ? 'selected' : ''}>Ngày tạo (Mới nhất)</option>
                                        <option value="createdAt-asc" ${param.sortBy == 'createdAt-asc' ? 'selected' : ''}>Ngày tạo (Cũ nhất)</option>
                                        <option value="amount-desc" ${param.sortBy == 'amount-desc' ? 'selected' : ''}>Số tiền (Cao đến thấp)</option>
                                        <option value="amount-asc" ${param.sortBy == 'amount-asc' ? 'selected' : ''}>Số tiền (Thấp đến cao)</option>
                                        <option value="expenseDate-desc" ${param.sortBy == 'expenseDate-desc' ? 'selected' : ''}>Ngày chi tiêu (Mới nhất)</option>
                                        <option value="expenseDate-asc" ${param.sortBy == 'expenseDate-asc' ? 'selected' : ''}>Ngày chi tiêu (Cũ nhất)</option>
                                    </select>
                                </div>
                            </div>
                            <c:if test="${empty submittedExpenses}">
                                <p class="text-muted">Bạn chưa gửi yêu cầu chi tiêu nào.</p>
                            </c:if>
                            <c:if test="${not empty submittedExpenses}">
                                <div class="table-responsive">
                                    <table class="table table-hover expense-table">
                                        <thead>
                                            <tr>
                                                <th>Mô tả</th>
                                                <th>Số tiền</th>
                                                <th>Ngày chi tiêu</th>
                                                <th>Mục đích</th>
                                                <th>Google Docs</th>
                                                <th>Trạng thái</th>
                                            </tr>
                                        </thead>
                                        <tbody id="expenseTableBody">
                                            <c:forEach var="expense" items="${submittedExpenses}">
                                                <tr>
                                                    <td>${expense.description}</td>
                                                    <td><fmt:formatNumber value="${expense.amount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                                    <td><fmt:formatDate value="${expense.expenseDate}" pattern="dd/MM/yyyy"/></td>
                                                    <td>${expense.purpose}</td>
                                                    <td>
                                                        <c:if test="${not empty expense.attachment}">
                                                            <a href="${expense.attachment}" target="_blank">Xem</a>
                                                        </c:if>
                                                    </td>
                                                    <td>
                                                        <span class="${expense.status == 'Pending' ? 'status-pending' : expense.status == 'Approved' ? 'status-approved' : 'status-rejected'}">
                                                            ${expense.status == 'Pending' ? 'Chờ duyệt' : expense.status == 'Approved' ? 'Đã duyệt' : 'Đã từ chối'}
                                                        </span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                <!-- Pagination -->
                                <div class="pagination-container">
                                    <nav aria-label="Page navigation">
                                        <ul class="pagination">
                                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                <a class="page-link" href="?page=${currentPage - 1}&clubID=${clubID}&status=${param.status}&search=${param.search}&sortBy=${param.sortBy}">Trước</a>
                                            </li>
                                            <c:forEach begin="1" end="${totalPages}" var="i">
                                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                    <a class="page-link" href="?page=${i}&clubID=${clubID}&status=${param.status}&search=${param.search}&sortBy=${param.sortBy}">${i}</a>
                                                </li>
                                            </c:forEach>
                                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                                <a class="page-link" href="?page=${currentPage + 1}&clubID=${clubID}&status=${param.status}&search=${param.search}&sortBy=${param.sortBy}">Sau</a>
                                            </li>
                                        </ul>
                                    </nav>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </main>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            window.addEventListener('load', function () {
                const cards = document.querySelectorAll('.enhanced-card');
                cards.forEach((card, index) => {
                    setTimeout(() => {
                        card.classList.add('fade-in');
                    }, index * 100);
                });

                // Validate form before submission
                const form = document.getElementById('expenseForm');
                form.addEventListener('submit', function (event) {
                    const purpose = document.getElementById('purpose').value;
                    const attachment = document.getElementById('attachment').value;
                    if (!purpose) {
                        event.preventDefault();
                        alert('Vui lòng chọn mục đích.');
                        return;
                    }
                    if (attachment && !attachment.match(/^https:\/\/docs\.google\.com\/.*/)) {
                        event.preventDefault();
                        alert('Link Google Docs không hợp lệ. Vui lòng nhập link bắt đầu bằng https://docs.google.com/');
                    }
                });

                // Search, Filter, and Sort
                const searchInput = document.getElementById('searchInput');
                const statusFilter = document.getElementById('statusFilter');
                const sortBy = document.getElementById('sortBy');

                function applyFilters() {
                    const search = searchInput.value.trim();
                    const status = statusFilter.value;
                    const sort = sortBy.value;
                    const url = new URL(window.location);
                    url.searchParams.set('search', search);
                    url.searchParams.set('status', status);
                    url.searchParams.set('sortBy', sort);
                    url.searchParams.set('page', '1');
                    window.location = url;
                }

                searchInput.addEventListener('input', () => {
                    clearTimeout(searchInput.timeout);
                    searchInput.timeout = setTimeout(applyFilters, 500);
                });
                statusFilter.addEventListener('change', applyFilters);
                sortBy.addEventListener('change', applyFilters);

                
                            });
        </script>
    </body>
</html>