
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
        <header class="header">
            <div class="container header-container">
                <div class="logo">
                    <i class="fas fa-users"></i>
                    <span>UniClub</span>
                </div>

                <!-- Search Bar -->
                <div class="search-container">
                    <div class="search-box">
                        <form action="${pageContext.request.contextPath}/events-page" method="get">
                            <i class="fas fa-search search-icon"></i>
                            <input type="text" id="searchInput" name="key" placeholder="Tìm kiếm sự kiện..." class="search-input">
                            <button type="submit" class="search-btn">
                                <i class="fas fa-search"></i>
                            </button>
                        </form>
                    </div>
                </div>

                <nav class="main-nav">
                    <ul>
                        <li>
                            <a href="${pageContext.request.contextPath}/"
                               class="${pageContext.request.servletPath == '/' ? 'active' : ''}">
                                Trang Chủ
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/clubs"
                               class="${fn:contains(pageContext.request.servletPath, '/clubs') ? 'active' : ''}">
                                Câu Lạc Bộ
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/events-page"
                               class="${fn:contains(pageContext.request.servletPath, '/events-page') ? 'active' : ''}">
                                Sự Kiện
                            </a>
                        </li>

                    </ul>
                </nav>

                <div class="auth-buttons">
                    <c:choose>
                        <c:when test="${sessionScope.user != null}">
                            <div class="user-menu" id="userMenu">
                                <a href="${pageContext.request.contextPath}/notification" class="btn btn-outline">
                                    <i class="fa-solid fa-bell"></i>
                                </a>
                                <a href="${pageContext.request.contextPath}/profile?action=myProfile" class="btn btn-outline">
                                    <i class="fa-solid fa-user"></i>
                                </a>
                                <form action="${pageContext.request.contextPath}/logout" method="post">
                                    <input class="btn btn-primary" type="submit" value="Logout">
                                </form>
                                <a href="${pageContext.request.contextPath}/myclub" class="btn btn-primary">MyClub</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="guest-menu" id="guestMenu">
                                <a href="${pageContext.request.contextPath}/login" class="btn btn-outline">Đăng Nhập</a>
                                <a href="${pageContext.request.contextPath}/register" class="btn btn-primary">Đăng Ký</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <button class="mobile-menu-btn" onclick="toggleMobileMenu()">
                        <i class="fas fa-bars"></i>
                    </button>
                </div>
            </div>

            <!-- Mobile Menu -->
            <div class="mobile-menu" id="mobileMenu">
                <nav class="mobile-nav">
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/"
                               class="${pageContext.request.servletPath == '/' ? 'active' : ''}">Trang Chủ</a></li>
                        <li><a href="${pageContext.request.contextPath}/clubs"
                               class="${pageContext.request.servletPath == '/clubs' ? 'active' : ''}">Câu Lạc Bộ</a></li>
                        <li><a href="${pageContext.request.contextPath}/events-page"
                               class="${pageContext.request.servletPath == '/events-page' ? 'active' : ''}">Sự Kiện</a></li>
                    </ul>
                </nav>
            </div>
            <c:if test="${not empty club}">
                <div class="club-header">
                    <div class="club-info">
                        <div class="club-avatar">
                            <img src="${pageContext.request.contextPath}${club.clubImg}" alt="${club.clubName}"
                                 style="width: 60px; height: 60px; border-radius: 50%;">
                        </div>
                        <div class="club-details">
                            <h1>${club.clubName}</h1>
                            <p>Chủ nhiệm: ${club.clubChairmanName}</p>
                        </div>
                    </div>
                </div>
            </c:if>
            <nav class="dashboard-nav">
                <ul>
                    <li>
                        <a href="${pageContext.request.contextPath}/chairman-page/overview"
                           class="nav-item ${currentPath == '/chairman-page/overview' ? 'active' : ''}">
                            <i class="fas fa-tachometer-alt"></i> Tổng quan
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/chairman-page/myclub-events"
                           class="nav-item ${currentPath == '/chairman-page/myclub-events' ? 'active' : ''}">
                            <i class="fas fa-calendar-alt"></i> Sự kiện
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/chairman-page/tasks"
                           class="nav-item ${currentPath == '/chairman-page/tasks' ? 'active' : ''}">
                            <i class="fas fa-clock"></i> Timeline & Công việc
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/chairman-page/clubmeeting"
                           class="nav-item ${currentPath == '/chairman-page/clubmeeting' ? 'active' : ''}">
                            <i class="fas fa-clock"></i> Cuộc họp
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/chairman-page/reports"
                           class="nav-item ${currentPath == '/chairman-page/tasks' ? 'active' : ''}">
                            <i class="fas fa-file-alt"></i> Báo cáo
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/chairman-page/financial-management"
                           class="nav-item ${currentPath == '/chairman-page/financial-management' ? 'active' : ''}">
                            <i class="fas fa-money-bill-wave"></i> Quản lý dòng tiền
                        </a>
                    </li>
                </ul>
            </nav>
        </header>

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
                    <h2>Tổng quan tài chính</h2>
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
                                <select class="filter-select" name="termID" onchange="this.form.submit()">

                                    <c:forEach var="term" items="${termIDs}">
                                        <option value="${term}" ${termID == term ? 'selected' : ''}>${term}</option>
                                    </c:forEach>
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
