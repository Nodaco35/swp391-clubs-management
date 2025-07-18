

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý tài chính</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <!-- Custom CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/financial.css">
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
                    <c:if test="${isAccess}">
                        <li class="menu-item active">
                            <a href="${pageContext.request.contextPath}/department/financial" class="menu-link">
                                <i class="fa-dollar-sign"></i>
                                <span>Tài chính</span>
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
                            <h1 class="h3 mb-1">Quản lý tài chính của kì ${term.termID}</h1>
                            <nav aria-label="breadcrumb">
                                <ol class="breadcrumb mb-0">
                                    <li class="breadcrumb-item">
                                        <a href="${pageContext.request.contextPath}/department-dashboard?clubID=${clubID}" class="text-decoration-none">
                                            Dashboard
                                        </a>
                                    </li>
                                    <li class="breadcrumb-item active" aria-current="page">Tài chính</li>
                                </ol>
                            </nav>
                        </div>
                    </div>
                </header> 

                <!-- Financial Overview Cards -->
                <div class="stats-grid">
                    <!-- Total Income -->
                    <div class="stat-card income-card enhanced-card">
                        <div class="stat-header">
                            <div class="stat-icon income">
                                <i class="fas fa-arrow-trend-up"></i>
                            </div>
                            <div class="stat-change positive">${compIncomeWithPreTerm}</div>
                        </div>
                        <div class="stat-content">
                            <h3>Tổng thu nhập</h3>
                            <div class="stat-value">
                                <fmt:formatNumber value="${totalIncome}" type="currency" currencySymbol="₫" groupingUsed="true"/>
                            </div>
                        </div>
                    </div>

                    <!-- Total Expenses -->
                    <div class="stat-card expense-card enhanced-card">
                        <div class="stat-header">
                            <div class="stat-icon expense">
                                <i class="fas fa-arrow-trend-down"></i>
                            </div>
                            <div class="stat-change negative">${compExpensesWithPreTerm}</div>
                        </div>
                        <div class="stat-content">
                            <h3>Tổng chi phí</h3>
                            <div class="stat-value">
                                <fmt:formatNumber value="${totalExpenses}" type="currency" currencySymbol="₫" groupingUsed="true"/>
                            </div>
                        </div>
                    </div>

                    <!-- Balance -->
                    <div class="stat-card balance-card enhanced-card">
                        <div class="stat-header">
                            <div class="stat-icon balance">
                                <i class="fas fa-wallet"></i>
                            </div>
                            <div class="stat-change positive">${compBalanceWithPreTerm}</div>
                        </div>
                        <div class="stat-content">
                            <h3>Số dư hiện tại</h3>
                            <div class="stat-value">
                                <fmt:formatNumber value="${balance}" type="currency" currencySymbol="₫" groupingUsed="true"/>
                            </div>
                        </div>
                    </div>

                    <!-- Pending Dues -->
                    <div class="stat-card pending-card enhanced-card">
                        <div class="stat-header">
                            <div class="stat-icon pending">
                                <i class="fas fa-users"></i>
                            </div>
                            <div class="stat-change neutral">${memberPendingIncome} thành viên</div>
                        </div>
                        <div class="stat-content">
                            <h3>Phí chưa thu</h3>
                             <div class="stat-value">
                                <fmt:formatNumber value="${incomeMemberPending}" type="currency" currencySymbol="₫" groupingUsed="true"/>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="main-grid">
                    <!-- Recent Transactions -->
                    <div class="content-section">
                        <div class="card enhanced-card">
                            <div class="card-header">
                                <h2 class="card-title">
                                    <i class="fas fa-history" style="color: #3b82f6;"></i>
                                    Giao dịch gần đây
                                </h2>
                                <a href="${pageContext.request.contextPath}/transaction" 
                                   class="btn btn-primary">
                                    Xem tất cả
                                </a>
                            </div>

                            <div class="card-body">
                                <div class="transaction-list">
                                    <c:forEach var="transaction" items="${recentTransactions}">
                                        <div class="transaction-item fade-in">
                                            <div class="transaction-info">
                                                <div class="transaction-icon ${transaction.type}">
                                                    <i class="fas ${transaction.type == 'income' ? 'fa-arrow-up' : 'fa-arrow-down'}"></i>
                                                </div>
                                                <div class="transaction-details">
                                                    <h4>${transaction.description}</h4>
                                                    <div class="transaction-meta">
                                                        ${transaction.category} • 
                                                        <fmt:formatDate value="${transaction.createdDate}" pattern="dd/MM/yyyy"/>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="transaction-amount">
                                                <div class="amount-value ${transaction.type}">
                                                    ${transaction.type == 'income' ? '+' : '-'}
                                                    <fmt:formatNumber value="${transaction.amount}" type="currency" currencySymbol="₫" groupingUsed="true"/>
                                                </div>
                                                <div class="amount-creator">${transaction.createdBy}</div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Sidebar -->
                    <div class="sidebar-section">
                        <!-- Quick Actions -->
                        <div class="card enhanced-card">
                            <div class="card-header">
                                <h2 class="card-title">
                                    <i class="fas fa-bolt" style="color: #f59e0b;"></i>
                                    Thao tác nhanh
                                </h2>
                            </div>
                            <div class="card-body">
                                <div class="quick-actions-grid">
                                    <form action="${pageContext.request.contextPath}/department/financial">
                                        <button  
                                            class="action-button income">
                                            <i class="fas fa-plus"></i>
                                            Thêm thu nhập
                                        </button>
                                    </form>
                                    <!-- phuc day -->
                                    <form action="${pageContext.request.contextPath}/department/financial" >
                                        <button 
                                            class="action-button expense">
                                            <i class="fa-regular fa-credit-card"></i>
                                            Quản lý chi tiêu                   
                                        </button>
                                    </form>
                                    <!-- đến đây-->

                                </div>
                            </div>
                        </div>

                        <!-- Member Dues Summary -->
                        <div class="card enhanced-card">
                            <div class="card-header">
                                <h2 class="card-title">
                                    <i class="fas fa-users" style="color: #8b5cf6;"></i>
                                    Phí thành viên
                                </h2>
                                <a href="${pageContext.request.contextPath}/member" 
                                   class="btn btn-primary">
                                    Quản lý
                                </a>
                            </div>
                            <div class="card-body">
                                <div class="member-stats">
                                    <div class="member-stat">
                                        <div class="member-stat-value total">12</div>
                                        <div class="member-stat-label">Tổng thành viên</div>
                                    </div>
                                    <div class="member-stat">
                                        <div class="member-stat-value paid">10</div>
                                        <div class="member-stat-label">Đã đóng</div>
                                    </div>
                                    <div class="member-stat">
                                        <div class="member-stat-value unpaid">2</div>
                                        <div class="member-stat-label">Chưa đóng</div>
                                    </div>
                                </div>

                                <div class="member-list">
                                    <div class="member-item">
                                        <div class="member-info">
                                            <div class="member-avatar">N</div>
                                            <div class="member-details">
                                                <h4>Nguyễn Văn An</h4>
                                                <div class="member-email">an.nguyen@email.com</div>
                                            </div>
                                        </div>
                                        <div class="member-payment">
                                            <div class="payment-info">
                                                <div class="payment-amount">100.000₫</div>
                                                <div class="payment-date">01/12/2024</div>
                                            </div>
                                            <i class="fas fa-check-circle payment-status paid"></i>
                                        </div>
                                    </div>

                                    <div class="member-item">
                                        <div class="member-info">
                                            <div class="member-avatar">T</div>
                                            <div class="member-details">
                                                <h4>Trần Thị Bình</h4>
                                                <div class="member-email">binh.tran@email.com</div>
                                            </div>
                                        </div>
                                        <div class="member-payment">
                                            <div class="payment-info">
                                                <div class="payment-amount">100.000₫</div>
                                                <div class="payment-date">01/11/2024</div>
                                            </div>
                                            <i class="fas fa-times-circle payment-status unpaid"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </main>

            <!-- Add Transaction Modal -->
            <div id="addTransactionModal" class="modal hidden">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3 class="modal-title">Thêm giao dịch mới</h3>
                    </div>

                    <form action="${pageContext.request.contextPath}/transaction" method="post">
                        <div class="form-group">
                            <label class="form-label">Loại giao dịch</label>
                            <select name="type" id="transactionType" class="form-select">
                                <option value="income">Thu nhập</option>
                                <option value="expense">Chi phí</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Số tiền</label>
                            <input type="number" name="amount" required class="form-input" placeholder="0">
                        </div>

                        <div class="form-group">
                            <label class="form-label">Mô tả</label>
                            <input type="text" name="description" required class="form-input" placeholder="Nhập mô tả giao dịch">
                        </div>

                        <div class="form-group">
                            <label class="form-label">Danh mục</label>
                            <select name="category" required class="form-select">
                                <option value="">Chọn danh mục</option>
                                <option value="Phí thành viên">Phí thành viên</option>
                                <option value="Sự kiện">Sự kiện</option>
                                <option value="Cơ sở vật chất">Cơ sở vật chất</option>
                                <option value="Văn phòng phẩm">Văn phòng phẩm</option>
                                <option value="Marketing">Marketing</option>
                            </select>
                        </div>

                        <input type="hidden" name="createdBy" value="Admin">

                        <div class="modal-footer">
                            <button type="button" onclick="closeAddTransactionModal()" class="btn btn-secondary">
                                Hủy
                            </button>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-plus"></i>
                                Thêm
                            </button>
                        </div>
                    </form>
                </div>
            </div>


            <script>
                function openAddTransactionModal(type) {
                    document.getElementById('transactionType').value = type;
                    document.getElementById('addTransactionModal').classList.remove('hidden');

                    // Add fade-in animation
                    setTimeout(() => {
                        document.querySelector('.modal-content').classList.add('fade-in');
                    }, 10);
                }

                function closeAddTransactionModal() {
                    document.getElementById('addTransactionModal').classList.add('hidden');
                    document.querySelector('.modal-content').classList.remove('fade-in');
                }

                // Close modal when clicking outside
                document.getElementById('addTransactionModal').addEventListener('click', function (e) {
                    if (e.target === this) {
                        closeAddTransactionModal();
                    }
                });

                // Add loading state to buttons
                document.querySelectorAll('form').forEach(form => {
                    form.addEventListener('submit', function () {
                        const submitBtn = this.querySelector('button[type="submit"]');
                        if (submitBtn) {
                            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang xử lý...';
                            submitBtn.disabled = true;
                        }
                    });
                });

                // Add smooth scroll to navigation links
                document.querySelectorAll('a[href^="#"]').forEach(anchor => {
                    anchor.addEventListener('click', function (e) {
                        e.preventDefault();
                        const target = document.querySelector(this.getAttribute('href'));
                        if (target) {
                            target.scrollIntoView({
                                behavior: 'smooth',
                                block: 'start'
                            });
                        }
                    });
                });

                // Add entrance animations
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
