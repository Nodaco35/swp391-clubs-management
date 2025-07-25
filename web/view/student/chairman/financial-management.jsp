
<%-- 
    Document   : financial-management
    Created on : Jul 22, 2025, 12:35:00 AM
    Author     : Grok
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
    <head>
        <title>Quản lý dòng tiền</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <style>
        /* Add or modify this in the chairmanPage.css */
        .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
            margin-top: 20px;
        }

        .pagination .btn {
            padding: 8px 15px; /* Reduce padding to make buttons more compact */
            width: auto; /* Allow buttons to size naturally */
            min-width: 100px; /* Set a minimum width if needed */
            text-align: center;
            border-radius: 6px;
            font-weight: 500;
            border: none;
            cursor: pointer;
            transition: all 0.3s ease;
            background: var(--primary);
            color: var(--primary-foreground);
        }

        .pagination .btn:hover {
            background: #0089a0;
        }

        .pagination .btn:disabled {
            background: #ccc;
            cursor: not-allowed;
        }
    </style>
    <body>
        <jsp:include page="components/sidebar.jsp" />

        <main class="dashboard-content">
            <div class="modal-content">
                <c:if test="${not empty sessionScope.errorMessage}">
                    <div class="error-message" style="color: red">${sessionScope.errorMessage}</div>
                    <c:remove var="errorMessage" scope="session" />
                </c:if>
                <c:if test="${not empty sessionScope.successMsg}">
                    <div class="error-message" style="color: green">${sessionScope.successMsg}</div>
                    <c:remove var="successMsg" scope="session" />
                </c:if>

                <!-- Financial Overview -->
                <div class="stats-section">
                    <h2>Tổng quan tài chính Kì: ${termID}</h2>
                    <div class="stats-grid">
                        <div class="stat-card">
                            <div class="stat-icon total">
                                <i class="fas fa-wallet"></i>
                            </div>
                            <div class="stat-content">
                                <h3><fmt:formatNumber value="${totalIncome}" type="currency" currencySymbol="₫"/></h3>
                                <p>Tổng thu nhập</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon completed">
                                <i class="fas fa-money-bill"></i>
                            </div>
                            <div class="stat-content">
                                <h3><fmt:formatNumber value="${totalExpense}" type="currency" currencySymbol="₫"/></h3>
                                <p>Tổng chi phí</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon ongoing">
                                <i class="fas fa-balance-scale"></i>
                            </div>
                            <div class="stat-content">
                                <h3><fmt:formatNumber value="${currentBalance}" type="currency" currencySymbol="₫"/></h3>
                                <p>Số dư hiện tại</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon upcoming">
                                <i class="fas fa-money-check"></i>
                            </div>
                            <div class="stat-content">
                                <h3><fmt:formatNumber value="${totalPendingIncome}" type="currency" currencySymbol="₫"/></h3>
                                <p>Phí thành viên chưa thu</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Transactions Management -->
                <div class="events-management">
                    <div class="section-header">
                        <div class="section-title">
                            <h2><i class="fas fa-exchange-alt"></i> Danh sách giao dịch</h2>
                        </div>
                        <div class="section-actions">
                            <form action="${pageContext.request.contextPath}/chairman-page/financial-management" method="get" class="search-container">
                                <i class="fas fa-search search-icon"></i>
                                <input type="text" name="search"  placeholder="Tìm kiếm giao dịch..." value="${search}">
                                <input type="hidden" name="type" value="${type}">
                                <input type="hidden" name="termID" value="${termID}">
                            </form>
                            <form id="filterForm" action="${pageContext.request.contextPath}/chairman-page/financial-management" method="get">
                                <select class="filter-select" name="type" onchange="this.form.submit()">
                                    <option value="all" ${type == 'all' || empty type ? 'selected' : ''}>Tất cả</option>
                                    <option value="Income" ${type == 'Income' ? 'selected' : ''}>Thu nhập</option>
                                    <option value="Expense" ${type == 'Expense' ? 'selected' : ''}>Chi phí</option>
                                </select>
                                <input type="hidden" name="search" value="${search}">
                            </form>
                        </div>
                    </div>

                    <div class="events-table-container">
                        <table class="events-table">
                            <thead>
                                <tr>
                                    <th>Loại</th>
                                    <th>Mô tả</th>
                                    <th>Số tiền</th>
                                    <th>Ngày giao dịch</th>
                                    <th>Người tạo</th>
                                    <th>Trạng thái</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="transaction" items="${transactions}">
                                    <tr>
                                        <td>
                                            <c:choose>
                                                <c:when test="${transaction.type == 'Income'}">
                                                    <span class="status Approved">Thu nhập</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status Approved">Chi phí</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${transaction.description}</td>
                                        <td><fmt:formatNumber value="${transaction.amount}" type="currency" currencySymbol="₫"/></td>
                                        <td><fmt:formatDate value="${transaction.transactionDate}" pattern="dd-MM-yyyy HH:mm"/></td>
                                        <td>${transaction.createdName}</td>
                                        <td><span class="status Approved">Đã duyệt</span></td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty transactions}">
                                    <tr>
                                        <td colspan="6" class="no-events">Không có giao dịch nào</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <div class="pagination" style="padding: 20px; text-align: center;">
                            <c:if test="${currentPage > 1}">
                                <a href="${pageContext.request.contextPath}/chairman-page/financial-management?page=${currentPage - 1}&search=${search}&type=${type}&termID=${termID}" class="btn-secondary">
                                    <i class="fas fa-chevron-left"></i> Trang trước
                                </a>
                            </c:if>
                            <span style="margin: 0 10px;">Trang ${currentPage} / ${totalPages}</span>
                            <c:if test="${currentPage < totalPages}">
                                <a href="${pageContext.request.contextPath}/chairman-page/financial-management?page=${currentPage + 1}&search=${search}&type=${type}&termID=${termID}" class="btn-secondary">
                                    Trang sau <i class="fas fa-chevron-right"></i>
                                </a>
                            </c:if>
                        </div>
                    </c:if>
                </div>
        </main>

        <script>
            function toggleMobileMenu() {
                const mobileMenu = document.getElementById('mobileMenu');
                mobileMenu.style.display = mobileMenu.style.display === 'block' ? 'none' : 'block';
            }
        </script>
    </body>
</html>
