<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Lịch sử giao dịch của CLB</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/financial.css">
    <style>
        .transaction-list {
            margin-top: 20px;
        }
        .transaction-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1rem;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            transition: all 0.2s ease;
            margin-bottom: 1rem;
        }
        .transaction-item:hover {
            border-color: #cbd5e1;
            background: #f8fafc;
            transform: translateX(4px);
        }
        .transaction-info {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            flex: 2;
        }
        .transaction-details h4 {
            font-size: 0.875rem;
            font-weight: 600;
            color: #1e293b;
            margin-bottom: 0.125rem;
        }
        .transaction-meta {
            font-size: 0.75rem;
            color: #64748b;
        }
        .transaction-status {
            font-weight: 600;
        }
        .transaction-status.pending {
            color: #eab308;
        }
        .transaction-status.approved {
            color: #16a34a;
        }
        .transaction-status.rejected {
            color: #dc2626;
        }
        .filter-form {
            margin-bottom: 20px;
            display: flex;
            gap: 10px;
            align-items: center;
        }
        .pagination {
            justify-content: center;
            margin-top: 20px;
        }
        @media print {
            .sidebar, .filter-form, .pagination { display: none !important; }
            .main-content { margin-left: 0; padding: 0; }
            .transaction-item { border: 1px solid #000; margin-bottom: 10px; }
            .transaction-details h4, .transaction-meta, .transaction-status { color: #000 !important; }
        }
    </style>
</head>
<body>
    <div class="department-leader-container">
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
                <li class="menu-item active">
                    <a href="${pageContext.request.contextPath}/department/financial" class="menu-link">
                        <i class="fas fa-dollar-sign"></i>
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
                <div class="user-info">
                    <div class="user-avatar">
                        <img src="${pageContext.request.contextPath}/${sessionScope.user.avatar != null ? sessionScope.user.avatar : 'Hinh-anh-dai-dien-mac-dinh-Facebook.jpg'}" alt="Avatar">
                    </div>
                    <div class="user-details">
                        <div class="user-name">${sessionScope.user.fullName}</div>
                        <div class="user-role">Trưởng ban</div>
                    </div>
                </div>
            </div>
        </nav>

        <main class="main-content">
            <header class="header mb-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1 class="h3 mb-1">Lịch sử giao dịch của CLB</h1>
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb mb-0">
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/department-dashboard?clubID=${clubID}" class="text-decoration-none">Dashboard</a>
                                </li>
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/department/financial" class="text-decoration-none">Tài chính</a>
                                </li>
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/department/financial/income.member" class="text-decoration-none">
                                        Phí thành viên
                                    </a>
                                </li>
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/department/financial/income" class="text-decoration-none">
                                        Quản lý nguồn thu
                                    </a>
                                </li>
                                <li class="breadcrumb-item active" aria-current="page">
                                    Lịch sử giao dịch của CLB
                                </li>
                            </ol>
                        </nav>
                    </div>
                    <button class="btn btn-primary" onclick="window.print()">In danh sách</button>
                </div>
            </header>

            <div class="content-section">
                <div class="card enhanced-card">
                    <div class="card-header">
                        <h2 class="card-title">
                            <i class="fas fa-history" style="color: #8b5cf6;"></i>
                            Lịch sử giao dịch
                        </h2>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty message}">
                            <div class="alert alert-success">${message}</div>
                        </c:if>
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger">${error}</div>
                        </c:if>
                        <form class="filter-form" action="${pageContext.request.contextPath}/department/financial/club-transaction" method="get">
                            <input type="hidden" name="clubID" value="${clubID}">
                            <input type="hidden" name="termID" value="${termID}">
                            <div class="form-group">
                                <label for="type">Loại:</label>
                                <select name="type" id="type" class="form-select">
                                    <option value="" ${type == '' ? 'selected' : ''}>Tất cả</option>
                                    <option value="Income" ${type == 'Income' ? 'selected' : ''}>Thu</option>
                                    <option value="Expense" ${type == 'Expense' ? 'selected' : ''}>Chi</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="status">Trạng thái:</label>
                                <select name="status" id="status" class="form-select">
                                    <option value="" ${status == '' ? 'selected' : ''}>Tất cả</option>
                                    <option value="Pending" ${status == 'Pending' ? 'selected' : ''}>Pending</option>
                                    <option value="Approved" ${status == 'Approved' ? 'selected' : ''}>Approved</option>
                                    <option value="Rejected" ${status == 'Rejected' ? 'selected' : ''}>Rejected</option>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-primary">Lọc</button>
                        </form>

                        <c:choose>
                            <c:when test="${not empty transactions}">
                                <div class="transaction-list">
                                    <c:forEach var="transaction" items="${transactions}">
                                        <div class="transaction-item">
                                            <div class="transaction-info">
                                                <div class="transaction-details">
                                                    <h4>Giao dịch #${transaction.transactionID} - ${transaction.type}</h4>
                                                    <div class="transaction-meta">Ngày: ${transaction.transactionDate}</div>
                                                    <c:if test="${not empty transaction.description}">
                                                        <div class="transaction-meta">Mô tả: ${transaction.description}</div>
                                                    </c:if>
                                                    
                                                    <div class="transaction-meta">Người tạo: ${transaction.createBy}</div>
                                                    <c:if test="${transaction.referenceID != null}">
                                                        <div class="transaction-meta">Tham chiếu: ${transaction.referenceID}</div>
                                                    </c:if>
                                                </div>
                                            </div>
                                            <div class="invoice-payment">
                                                <div class="payment-info">
                                                    <div class="payment-amount">
                                                        <fmt:formatNumber value="${transaction.amount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                    </div>
                                                </div>
                                                <div class="transaction-status ${fn:toLowerCase(transaction.status)}">${transaction.status}</div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                                <nav class="pagination">
                                    <ul class="pagination">
                                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/department/financial/club-transaction?type=${type}&status=${status}&page=${currentPage - 1}">Trước</a>
                                        </li>
                                        <c:forEach begin="1" end="${totalPages}" var="i">
                                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                <a class="page-link" href="${pageContext.request.contextPath}/department/financial/club-transaction?type=${type}&status=${status}&page=${i}">${i}</a>
                                            </li>
                                        </c:forEach>
                                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/department/financial/club-transaction?type=${type}&status=${status}&page=${currentPage + 1}">Sau</a>
                                        </li>
                                    </ul>
                                </nav>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-info text-center">
                                    Không có giao dịch nào để hiển thị.
                                </div>
                            </c:otherwise>
                        </c:choose>
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
        });
    </script>
</body>
</html>