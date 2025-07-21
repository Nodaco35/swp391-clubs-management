<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gửi Báo Cáo Chi Phí</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
    <style>
        .expense-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }
        .expense-details {
            flex: 1;
        }
        .status-pending { color: #ffc107; font-weight: bold; }
        .status-approved { color: #28a745; font-weight: bold; }
        .status-rejected { color: #dc3545; font-weight: bold; }
    </style>
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
                <li class="menu-item">
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
                <c:if test="${isAccess}">
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/department/financial/approve-expense" class="menu-link">
                            <i class="fas fa-dollar-sign"></i>
                            <span>Duyệt Báo Cáo Chi Phí</span>
                        </a>
                    </li>
                </c:if>
                <c:if test="${isHauCan}">
                    <li class="menu-item active">
                        <a href="${pageContext.request.contextPath}/department/submit-expense" class="menu-link">
                            <i class="fas fa-dollar-sign"></i>
                            <span>Báo Cáo Chi Tiêu</span>
                        </a>
                    </li>
                </c:if>
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/" class="menu-link">
                        <i class="fas fa-home"></i>
                        <span>Về trang chủ</span>
                    </a>
                </li>
            </ul>
            <div class="sidebar-footer">
                <div class="user-info">
                    <div class="user-avatar">
                        <img src="${pageContext.request.contextPath}/${sessionScope.user.avatar != null ? sessionScope.user.avatar : 'img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg'}" alt="Avatar">
                    </div>
                    <div class="user-details">
                        <div class="user-name">${sessionScope.user.fullName}</div>
                        <div class="user-role">${isHauCan ? 'Trưởng ban Hậu Cần' : isAccess ? 'Trưởng ban Đối Ngoại' : 'Trưởng ban'}</div>
                    </div>
                </div>
            </div>
        </nav>

        <!-- Main Content -->
        <main class="main-content">
            <header class="header mb-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1 class="h3 mb-1">Gửi Báo Cáo Chi Phí</h1>
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb mb-0">
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/department-dashboard?clubID=${clubID}" class="text-decoration-none">Dashboard</a>
                                </li>
                                <li class="breadcrumb-item active" aria-current="page">Gửi Báo Cáo Chi Phí</li>
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
                                Gửi Báo Cáo Chi Phí Mới (Kỳ: ${term.termName})
                            </h2>
                        </div>
                        <div class="card-body">
                            <c:if test="${not empty message}">
                                <div class="alert alert-success">${message}</div>
                            </c:if>
                            <c:if test="${not empty error}">
                                <div class="alert alert-danger">${error}</div>
                            </c:if>
                            <form id="expenseForm" action="${pageContext.request.contextPath}/department/submit-expense" method="post">
                                <input type="hidden" name="clubID" value="${clubID}">
                                <div class="mb-3">
                                    <label for="purpose" class="form-label">Mục đích</label>
                                    <select id="purpose" name="purpose" class="form-select" required>
                                        <option value="" disabled>Chọn mục đích</option>
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
                                    <button type="button" class="btn btn-secondary" onclick="window.history.back()">Hủy</button>
                                    <button type="submit" class="btn btn-primary">Gửi Báo Cáo</button>
                                </div>
                            </form>
                        </div>
                    </div>

                    <!-- Submitted Expenses List -->
                    <div class="card enhanced-card">
                        <div class="card-header">
                            <h2 class="card-title">
                                <i class="fas fa-list" style="color: #8b5cf6;"></i>
                                Danh Sách Báo Cáo Chi Phí Đã Gửi (Kỳ: ${term.termName})
                            </h2>
                        </div>
                        <div class="card-body">
                            <c:if test="${empty submittedExpenses}">
                                <p class="text-muted">Bạn chưa gửi báo cáo chi phí nào.</p>
                            </c:if>
                            <c:if test="${not empty submittedExpenses}">
                                <div class="expense-list">
                                    <c:forEach var="expense" items="${submittedExpenses}">
                                        <div class="expense-item">
                                            <div class="expense-details">
                                                <h4>${expense.description}</h4>
                                                <div>Số tiền: <fmt:formatNumber value="${expense.amount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></div>
                                                <div>Ngày chi tiêu: <fmt:formatDate value="${expense.expenseDate}" pattern="dd/MM/yyyy"/></div>
                                                <div>Mục đích: ${expense.purpose}</div>
                                                <div>Trạng thái: 
                                                    <span class="${expense.status == 'Pending' ? 'status-pending' : expense.status == 'Approved' ? 'status-approved' : 'status-rejected'}">
                                                        ${expense.status == 'Pending' ? 'Chờ duyệt' : expense.status == 'Approved' ? 'Đã duyệt' : 'Đã từ chối'}
                                                    </span>
                                                </div>
                                                <c:if test="${not empty expense.attachment}">
                                                    <div><a href="${expense.attachment}" target="_blank">Xem Google Docs</a></div>
                                                </c:if>
                                            </div>
                                        </div>
                                    </c:forEach>
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
            });
        </script>
    </body>
</html>