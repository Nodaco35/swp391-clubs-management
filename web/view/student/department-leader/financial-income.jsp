<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý nguồn thu</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/financial.css">
        <style>
            .income-list {
                margin-top: 20px;
            }
            .income-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 1rem;
                border: 1px solid #e2e8f0;
                border-radius: 12px;
                transition: all 0.2s ease;
                margin-bottom: 1rem;
            }
            .income-item:hover {
                border-color: #cbd5e1;
                background: #f8fafc;
                transform: translateX(4px);
            }
            .income-info {
                display: flex;
                align-items: center;
                gap: 0.75rem;
                flex: 2;
            }
            .income-details h4 {
                font-size: 0.875rem;
                font-weight: 600;
                color: #1e293b;
                margin-bottom: 0.125rem;
            }
            .income-meta {
                font-size: 0.75rem;
                color: #64748b;
            }
            .income-status {
                font-weight: 600;
            }
            .income-status.dadanhan {
                color: #16a34a;
            }
            .income-status.dangcho {
                color: #eab308;
            }
            .income-status.quahan {
                color: #dc2626;
            }
            .filter-form {
                margin-bottom: 20px;
                display: flex;
                gap: 10px;
                align-items: center;
            }
            .create-form {
                display: none;
                margin-bottom: 20px;
                padding: 1rem;
                border: 1px solid #e2e8f0;
                border-radius: 12px;
                background: #f8fafc;
                transition: all 0.3s ease;
            }
            .create-form.active {
                display: block;
                opacity: 1;
                transform: translateY(0);
            }
            .pagination {
                justify-content: center;
                margin-top: 20px;
            }
            html {
                scroll-behavior: smooth;
            }
            @media print {
                .sidebar, .filter-form, .pagination, .create-form {
                    display: none !important;
                }
                .main-content {
                    margin-left: 0;
                    padding: 0;
                }
                .income-item {
                    border: 1px solid #000;
                    margin-bottom: 10px;
                }
                .income-details h4, .income-meta, .income-status {
                    color: #000 !important;
                }
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
                            <h1 class="h3 mb-1">Quản lý nguồn thu của kỳ ${term.termID}</h1>
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
                                     
                                    <li class="breadcrumb-item active" aria-current="page">Nguồn thu</li>
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
                                <i class="fas fa-dollar-sign" style="color: #8b5cf6;"></i>
                                Danh sách nguồn thu
                            </h2>
                            <button id="add-income-btn" class="btn btn-primary">Thêm mới</button>
                        </div>
                        <div class="card-body">
                            <c:if test="${not empty message}">
                                <div class="alert alert-success">${message}</div>
                            </c:if>
                            <c:if test="${not empty error}">
                                <div class="alert alert-danger">${error}</div>
                            </c:if>
                            <form class="filter-form" action="${pageContext.request.contextPath}/department/financial/income" method="get">
                                <input type="hidden" name="clubID" value="${clubID}">
                                <input type="hidden" name="termID" value="${termID}">
                                <div class="form-group">
                                    <label for="status">Trạng thái:</label>
                                    <select name="status" id="status" class="form-select">
                                        <option value="" ${status == '' ? 'selected' : ''}>Tất cả</option>
                                        <option value="Đã nhận" ${status == 'Đã nhận' ? 'selected' : ''}>Đã nhận</option>
                                        <option value="Đang chờ" ${status == 'Đang chờ' ? 'selected' : ''}>Đang chờ</option>
                                        <option value="Quá hạn" ${status == 'Quá hạn' ? 'selected' : ''}>Quá hạn</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="source">Nguồn:</label>
                                    <select name="source" id="source" class="form-select">
                                        <option value="" ${source == '' ? 'selected' : ''}>Tất cả</option>
                                        <option value="Phí thành viên" ${source == 'Phí thành viên' ? 'selected' : ''}>Phí thành viên</option>
                                        <option value="Tài trợ" ${source == 'Tài trợ' ? 'selected' : ''}>Tài trợ</option>
                                        <option value="Doanh thu sự kiện" ${source == 'Doanh thu sự kiện' ? 'selected' : ''}>Doanh thu sự kiện</option>
                                        <option value="Khác" ${source == 'Khác' ? 'selected' : ''}>Khác</option>
                                    </select>
                                </div>
                                <button type="submit" class="btn btn-primary">Lọc</button>
                            </form>
                            
                            <form class="create-form" id="create-income-form" action="${pageContext.request.contextPath}/department/financial/income?action=insert" method="post" style="display: hidden">
                                <input type="hidden" name="clubID" value="${clubID}">
                                <input type="hidden" name="termID" value="${termID}">
                                <div class="form-group">
                                    <label for="source">Nguồn:</label>
                                    <select name="source" id="source-insert" class="form-select" required>
                                        <option value="Phí thành viên">Phí thành viên</option>
                                        <option value="Tài trợ">Tài trợ</option>
                                        <option value="Doanh thu sự kiện">Doanh thu sự kiện</option>
                                        <option value="Khác">Khác</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="amount">Số tiền:</label>
                                    <input type="number" name="amount" id="amount" class="form-control" step="0.01" required>
                                </div>
                                
                                <div class="form-group" id="dueDateGroup" style="display: none;">
                                    <label for="dueDate">Hạn chót nộp:</label>
                                    <input type="datetime-local" name="dueDate" id="dueDate" class="form-control">
                                </div>
                                <div class="form-group">
                                    <label for="description">Mô tả:</label>
                                    <textarea name="description" id="description" class="form-control"></textarea>
                                </div>
                                <div class="form-group" id="statusGroup">
                                    <label for="status">Trạng thái:</label>
                                    <select name="status" id="status-insert" class="form-select" required>
                                        <c:choose>
                                            <c:when test="${param.source == 'Phí thành viên'}">
                                                <option value="Pending" selected>Pending</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="Đang chờ">Đang chờ</option>
                                                <option value="Đã nhận">Đã nhận</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </select>
                                </div>
                                <button type="submit" class="btn btn-primary">Thêm mới</button>
                                <button type="button" id="cancel-form-btn" class="btn btn-secondary">Hủy</button>
                            </form>

                            <c:choose>
                                <c:when test="${not empty incomes}">
                                    <div class="income-list">
                                        <c:forEach var="income" items="${incomes}">
                                            <div class="income-item">
                                                <div class="income-info">
                                                    <div class="income-details">
                                                        <h4>Nguồn thu #${income.incomeID} - ${income.source}</h4>
                                                        <div class="income-meta">Ngày: <fmt:formatDate value="${income.incomeDate}" pattern="dd/MM/yyyy HH:mm"/></div>
                                                        <c:if test="${not empty income.description}">
                                                            <div class="income-meta">Mô tả: ${income.description}</div></c:if>
                                                        <c:if test="${not empty income.attachment}">
                                                            <div class="income-meta">Tệp đính kèm: <a href="${pageContext.request.contextPath}/${income.attachment}" target="_blank">Xem</a></div>
                                                        </c:if>
                                                    </div>
                                                </div>
                                                <div class="invoice-payment">
                                                    <div class="payment-info">
                                                        <div class="payment-amount">
                                                            <fmt:formatNumber value="${income.amount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                        </div>
                                                    </div>
                                                    <div class="income-status ${fn:toLowerCase(income.status)}">${income.status}</div>
                                                    <c:if test="${income.status != 'Đã nhận' && income.source != 'Phí thành viên'}">
                                                        <div class="action-buttons">
                                                            <form action="${pageContext.request.contextPath}/department/financial/income?action=paid" method="post">
                                                                <input type="hidden" name="incomeID" value="${income.incomeID}">
                                                                <button type="submit" class="btn btn-success btn-sm">
                                                                    <i class="fas fa-check"></i> Đã nhận
                                                                </button>
                                                            </form>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                    <nav class="pagination">
                                        <ul class="pagination">
                                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                <a class="page-link" href="${pageContext.request.contextPath}/department/financial/income?clubID=${clubID}&termID=${termID}&status=${status}&source=${source}&page=${currentPage - 1}">Trước</a>
                                            </li>
                                            <c:forEach begin="1" end="${totalPages}" var="i">
                                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                    <a class="page-link" href="${pageContext.request.contextPath}/department/financial/income?clubID=${clubID}&termID=${termID}&status=${status}&source=${source}&page=${i}">${i}</a>
                                                </li>
                                            </c:forEach>
                                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                                <a class="page-link" href="${pageContext.request.contextPath}/department/financial/income?clubID=${clubID}&termID=${termID}&status=${status}&source=${source}&page=${currentPage + 1}">Sau</a>
                                            </li>
                                        </ul>
                                    </nav>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert alert-info text-center">
                                        Không có nguồn thu nào để hiển thị.
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </main>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script>
            $(document).ready(function () {
                function toggleFields() {
                    const sourceVal = $('#source-insert').val();
                    if (sourceVal === 'Phí thành viên') {
                        $('#statusGroup').hide();
                        $('#dueDateGroup').show();
                    } else {
                        $('#statusGroup').show();
                        $('#dueDateGroup').hide();
                    }
                }

                $('#source-insert').on('change', toggleFields);
                toggleFields(); // Initialize on page load

                // Toggle form and smooth scroll when clicking "Thêm mới"
                const addButton = $('#add-income-btn');
                const createForm = $('.create-form');
                const cancelButton = $('#cancel-form-btn');

                addButton.on('click', function () {
                    createForm.addClass('active');
                    // Smooth scroll to the form
                    $('html, body').animate({
                        scrollTop: createForm.offset().top - 100 // Adjust offset for better visibility
                    }, 500); // 500ms for smooth scrolling
                });

                cancelButton.on('click', function () {
                    createForm.removeClass('active');
                    createForm[0].reset(); // Reset form fields
                    $('#source-insert').trigger('change'); // Reset status dropdown
                });
            });
        </script>
    </body>
</html>