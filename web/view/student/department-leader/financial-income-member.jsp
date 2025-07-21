<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý phí thành viên</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <!-- Custom CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/financial.css">
        <style>
            .filter-form {
                display: flex;
                gap: 15px;
                align-items: center;
                margin-bottom: 20px;
            }
            .filter-form .form-control, .filter-form .form-select {
                border-radius: 5px;
                border: 1px solid #ced4da;
                padding: 8px 12px;
                font-size: 0.95rem;
            }
            .filter-form .btn-primary {
                border-radius: 5px;
                padding: 8px 20px;
                font-weight: 500;
                transition: background-color 0.3s ease;
            }
            .filter-form .btn-primary:hover {
                background-color: #0056b3;
            }
            .member-list {
                margin-top: 20px;
            }
            .member-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 10px;
                border-bottom: 1px solid #ddd;
            }
            .member-info {
                display: flex;
                align-items: center;
            }

            .member-details {
                margin-left: 10px;
            }
            .member-payment {
                display: flex;
                align-items: center;
            }
            .payment-info {
                margin-right: 10px;
            }
            .payment-qr img {
                border: 1px solid #ccc;
            }
            .filter-form {
                margin-bottom: 20px;
            }

            .pagination {
                margin-top: 20px;
            }
            .pagination a {
                padding: 6px 12px;
                margin: 0 2px;
                border: 1px solid #ccc;
                text-decoration: none;
                color: black;
            }
            .pagination a.active {
                background-color: #007bff;
                color: white;
                border-color: #007bff;
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
                            <h1 class="h3 mb-1">Quản lý phí thành viên của kỳ ${term.termID}</h1>
                            <nav aria-label="breadcrumb">
                                <ol class="breadcrumb mb-0">
                                    <li class="breadcrumb-item">
                                        <a href="${pageContext.request.contextPath}/department-dashboard?clubID=${clubID}" class="text-decoration-none">Dashboard</a>
                                    </li>
                                    <li class="breadcrumb-item">
                                        <a href="${pageContext.request.contextPath}/department/financial" class="text-decoration-none">Tài chính</a>
                                    </li>
                                    <li class="breadcrumb-item active" aria-current="page">Phí thành viên</li>
                                    <li class="breadcrumb-item">
                                        <a href="${pageContext.request.contextPath}/department/financial/income" class="text-decoration-none">
                                            Nguồn thu
                                        </a>
                                    </li>
                                     <li class="breadcrumb-item">
                                        <a href="${pageContext.request.contextPath}/department/financial/club-transaction?type=&status=" class="text-decoration-none">
                                           Lịch sử giao dịch của clb
                                        </a>
                                    </li>
                                </ol>
                            </nav>
                        </div>
                    </div>
                </header>

                <div class="content-section">
                    <div class="card enhanced-card">
                        <div class="card-header">
                            <h2 class="card-title">
                                <i class="fas fa-users" style="color: #8b5cf6;"></i>
                                Danh sách phí thành viên
                            </h2>
                            <div class="action-buttons">
                                <form action="${pageContext.request.contextPath}/department/financial/income.member" method="post" style="display: inline;">
                                    <input type="hidden" name="action" value="remindAll"/>
                                    <input type="hidden" name="incomeID" value="${incomeID}"/>
                                    <input type="hidden" name="keyword" value="${param.keyword}"/>
                                    <input type="hidden" name="status" value="${param.status}"/>
                                    <input type="hidden" name="page" value="${currentPage}"/>
                                    <button type="submit" class="btn btn-warning" onclick="return confirm('Gửi thông báo nhắc nhở tới tất cả thành viên chưa đóng phí?')" ${hasPending ? '' : 'disabled'}>Nhắc nhở tất cả</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/department/financial/income.member" method="post" style="display: inline;">
                                    <input type="hidden" name="action" value="complete"/>
                                    <input type="hidden" name="incomeID" value="${incomeID}"/>
                                    <input type="hidden" name="keyword" value="${param.keyword}"/>
                                    <input type="hidden" name="status" value="${param.status}"/>
                                    <input type="hidden" name="page" value="${currentPage}"/>
                                    <button type="submit" class="btn btn-success" onclick="return confirm('Xác nhận hoàn thành khoản phí thành viên này?')" ${allPaid ? '' : 'disabled'}>Hoàn thành</button>
                                </form>
                            </div>
                        </div>
                        <div class="card-body">
                            <c:if test="${not empty message}">
                                <div class="alert alert-success">${message}</div>
                            </c:if>
                            <c:if test="${not empty error}">
                                <div class="alert alert-danger">${error}</div>
                            </c:if>
                            <form class="filter-form" action="${pageContext.request.contextPath}/department/financial/income.member" method="get">

                                <input type="text" name="keyword" value="${param.keyword}" placeholder="Tìm theo tên hoặc email..." class="form-control"/>
                                <select name="status" class="form-select">
                                    <option value="all" ${param.status == 'all' || empty param.status ? 'selected' : ''}>Tất cả</option>
                                    <option value="Paid" ${param.status == 'Paid' ? 'selected' : ''}>Đã nộp</option>
                                    <option value="Pending" ${param.status == 'Pending' ? 'selected' : ''}>Chưa nộp</option>
                                </select>
                                <select name="incomeID" class="form-select">
                                    <c:forEach var="id" items="${incomeIDs}">
                                        <option value="${id.incomeID}" ${param.incomeID == id.incomeID ? 'selected' : ''}>${id.description}</option>
                                    </c:forEach>
                                </select>
                                <button type="submit" class="btn btn-primary">Lọc</button>
                            </form>
                            <c:if test="${not empty IncomeMemberSrc}">
                                <div class="member-list">
                                    <c:forEach var="memberIncome" items="${IncomeMemberSrc}">
                                        <div class="member-item">
                                            <div class="member-info">
                                                <div class="user-avatar">
                                                    <img src="${pageContext.request.contextPath}/${memberIncome.avtSrc != null ? memberIncome.avtSrc : 'img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg'}" alt="Avatar">
                                                </div>
                                                <div class="member-details">
                                                    <h4>${memberIncome.userName} (${memberIncome.userID})</h4>
                                                    <div class="member-email">${memberIncome.email}</div>
                                                </div>
                                            </div>
                                            <div class="member-payment">
                                                <div class="payment-info">
                                                    <div class="payment-amount">
                                                        <fmt:formatNumber value="${memberIncome.amount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                    </div>
                                                    <div class="payment-date">
                                                        <c:choose>
                                                            <c:when test="${not empty memberIncome.paidDate}">
                                                                ${memberIncome.paidDate}
                                                            </c:when>
                                                            <c:otherwise>Chưa nộp, hạn cuối: <strong>${memberIncome.dueDate}</strong></c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                                <i class="fas ${memberIncome.contributionStatus == 'Paid' ? 'fa-check-circle payment-status paid' : 'fa-times-circle payment-status unpaid'}"></i>
                                                <div class="member-actions">
                                                    <c:if test="${memberIncome.contributionStatus == 'Pending'}">
                                                        <form action="${pageContext.request.contextPath}/department/financial/income.member" method="post" style="display: inline;">
                                                            <input type="hidden" name="action" value="markPaid"/>
                                                            <input type="hidden" name="contributionID" value="${memberIncome.contributionID}"/>
                                                            <input type="hidden" name="incomeID" value="${incomeID}"/>
                                                            <input type="hidden" name="keyword" value="${param.keyword}"/>
                                                            <input type="hidden" name="status" value="${param.status}"/>
                                                            <input type="hidden" name="page" value="${currentPage}"/>
                                                            <button type="submit" class="btn btn-success btn-sm" onclick="return confirm('Xác nhận đã nhận phí từ thành viên này?')">Đã nhận</button>
                                                        </form>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                    <div class="pagination">
                                        <c:if test="${currentPage > 1}">
                                            <a href="?incomeID=${param.incomeID}&page=${currentPage - 1}&keyword=${param.keyword}&status=${param.status}">« Trước</a>
                                        </c:if>
                                        <c:if test="${totalPages > 1}">
                                            <c:choose>
                                                <c:when test="${totalPages <= 5}">
                                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                                        <a href="?incomeID=${incomeID}&page=${i}&keyword=${param.keyword}&status=${param.status}"
                                                           class="${i == currentPage ? 'active' : ''}">${i}</a>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="startPage" value="${currentPage - 2}"/>
                                                    <c:set var="endPage" value="${currentPage + 2}"/>
                                                    <c:if test="${startPage < 1}">
                                                        <c:set var="startPage" value="1"/>
                                                        <c:set var="endPage" value="5"/>
                                                    </c:if>
                                                    <c:if test="${endPage > totalPages}">
                                                        <c:set var="endPage" value="${totalPages}"/>
                                                        <c:set var="startPage" value="${totalPages - 4}"/>
                                                    </c:if>
                                                    <c:if test="${startPage > 1}">
                                                        <a href="?incomeID=${incomeID}&page=1&keyword=${param.keyword}&status=${param.status}">1</a>
                                                        <span class="ellipsis">...</span>
                                                    </c:if>
                                                    <c:forEach begin="${startPage}" end="${endPage}" var="i">
                                                        <a href="?incomeID=${incomeID}&page=${i}&keyword=${param.keyword}&status=${param.status}"
                                                           class="${i == currentPage ? 'active' : ''}">${i}</a>
                                                    </c:forEach>
                                                    <c:if test="${endPage < totalPages}">
                                                        <span class="ellipsis">...</span>
                                                        <a href="?incomeID=${incomeID}&page=${totalPages}&keyword=${param.keyword}&status=${param.status}">${totalPages}</a>
                                                    </c:if>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
                                        <c:if test="${currentPage < totalPages}">
                                            <a href="?incomeID=${incomeID}&page=${currentPage + 1}&keyword=${param.keyword}&status=${param.status}">Sau »</a>
                                        </c:if>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${empty IncomeMemberSrc}">
                                <p class="text-muted">Không có dữ liệu phí thành viên phù hợp.</p>
                            </c:if>
                        </div>
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

                                                                function confirmMarkPaid(contributionID, incomeID) {
                                                                    if (confirm('Xác nhận đã nhận phí từ thành viên này?')) {
                                                                        window.location.href = '${pageContext.request.contextPath}/department/financial/income.member?action=markPaid&contributionID=' + contributionID + '&incomeID=' + incomeID;
                                                                    }
                                                                }



                                                                function confirmRemindAll(incomeID) {
                                                                    if (confirm('Gửi thông báo nhắc nhở tới tất cả thành viên chưa đóng phí?')) {
                                                                        window.location.href = '${pageContext.request.contextPath}/department/financial/income.member?action=remindAll&incomeID=' + incomeID;
                                                                    }
                                                                }

                                                                function confirmComplete(incomeID) {
                                                                    if (confirm('Xác nhận hoàn thành khoản phí thành viên này?')) {
                                                                        window.location.href = '${pageContext.request.contextPath}/department/financial/income.member?action=complete&incomeID=' + incomeID;
                                                                    }
                                                                }
</script>
</body>
</html>