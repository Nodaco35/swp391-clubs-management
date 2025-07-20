
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Hóa Đơn Phí Thành Viên - UniClub</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/financial.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/myClub.css">

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eventsPage.css">


        <style>
            .main-container, main, #main-content {
                max-width: 1200px;
                margin: 0 auto;
                padding: 1rem;

            }




            .sidebar {
                top: 70px; /* nếu header cao 70px */
            }

            .invoice-list {
                margin-top: 20px;
            }
            .invoice-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 1rem;
                border: 1px solid #e2e8f0;
                border-radius: 12px;
                transition: all 0.2s ease;
            }
            .invoice-item:hover {
                border-color: #cbd5e1;
                background: #f8fafc;
                transform: translateX(4px);
            }
            .invoice-info {
                display: flex;
                align-items: center;
                gap: 0.75rem;
                flex: 2;
            }
            .invoice-details h4 {
                font-size: 0.875rem;
                font-weight: 600;
                color: #1e293b;
                margin-bottom: 0.125rem;
            }
            .invoice-meta {
                font-size: 0.75rem;
                color: #64748b;
            }
            .invoice-payment {
                display: flex;
                align-items: center;
                gap: 0.75rem;
                flex: 1;
            }
            .payment-info {
                text-align: right;
            }
            .payment-amount {
                font-size: 0.875rem;
                font-weight: 600;
                color: #1e293b;
                margin-bottom: 0.125rem;
            }
            .payment-date {
                font-size: 0.75rem;
                color: #64748b;
            }
            .payment-status.paid {
                color: #16a34a;
            }
            .payment-status.unpaid {
                color: #dc2626;
            }
            .action-buttons {
                display: flex;
                gap: 0.5rem;
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

        </style>
    </head>
    <body>
        <jsp:include page="/view/events-page/header.jsp" />


        <main>
            <header >
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1 class="h3 mb-1">Hóa đơn phí thành viên của ${sessionScope.user.fullName}</h1>
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb mb-0">
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/myclub" class="text-decoration-none">Trang chủ</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Hóa đơn</li>
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/financial/transaction-history" class="text-decoration-none">Lịch sử giao dịch</a></li>
                            </ol>
                        </nav>
                    </div>

                </div>
            </header>

            <div class="content-section">
                <div class="card enhanced-card">
                    <div class="card-header">
                        <h2 class="card-title">
                            <i class="fas fa-file-invoice" style="color: #8b5cf6;"></i>
                            Danh sách hóa đơn
                        </h2>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty message}">
                            <div class="alert alert-success">${message}</div>
                        </c:if>
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger">${error}</div>
                        </c:if>
                        <form class="filter-form" action="${pageContext.request.contextPath}/financial/cart-member-contribution" method="get">
                            <input type="hidden" name="userID" value="${userID}">
                            <div class="form-group">
                                <label for="status">Trạng thái:</label>
                                <select name="status" id="status" class="form-select">
                                    <option value="" ${status == '' ? 'selected' : ''}>Tất cả</option>
                                    <option value="Pending" ${status == 'Pending' ? 'selected' : ''}>Chưa thanh toán</option>
                                    <option value="Paid" ${status == 'Paid' ? 'selected' : ''}>Đã thanh toán</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="termID">Kỳ học:</label>
                                <select name="termID" id="termID" class="form-select">
                                    <option value="" ${termID == '' ? 'selected' : ''}>Tất cả</option>
                                    <c:forEach var="term" items="${termIDs}">
                                        <option value="${term}" ${termID == term ? 'selected' : ''}>${term}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-primary">Lọc</button>
                        </form>

                        <c:choose>
                            <c:when test="${not empty invoices}">
                                <div class="invoice-list">
                                    <c:forEach var="invoice" items="${invoices}">
                                        <div class="invoice-item">
                                            <div class="invoice-info">

                                                <div class="invoice-details">
                                                    <h4>Hóa đơn #${invoice.contributionID} - ${invoice.clubName} - ${invoice.description}</h4>
                                                    <div class="invoice-meta">Kỳ: ${invoice.termID}</div>
                                                    <div class="invoice-meta">Nguồn thu: ${invoice.source}</div>
                                                    <div class="invoice-meta">Ngày tạo: <fmt:formatDate value="${invoice.createdAt}" pattern="dd/MM/yyyy HH:mm"/></div>
                                                    <div class="invoice-meta">Hạn nộp:<strong><fmt:formatDate value="${invoice.dueDate}" pattern="dd/MM/yyyy HH:mm"/> </strong> </div>
                                                </div>
                                            </div>
                                            <div class="invoice-payment"></div>
                                            <div class="invoice-payment">
                                                <div class="payment-info">
                                                    <div class="payment-amount">
                                                        <fmt:formatNumber value="${invoice.amount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                    </div>
                                                    <div class="payment-date">
                                                        <c:choose>
                                                            <c:when test="${not empty invoice.paidDate}">
                                                                Đã nộp: <fmt:formatDate value="${invoice.paidDate}" pattern="dd/MM/yyyy HH:mm"/>
                                                            </c:when>
                                                            <c:otherwise>Chưa nộp</c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>

                                                <i class="fas fa-${invoice.contributionStatus == 'Paid' ? 'check-circle payment-status paid' : 'times-circle payment-status unpaid'}"></i>


                                                <div class="action-buttons">
                                                    <c:if test="${invoice.contributionStatus == 'Pending'}">
                                                        <form action="payment" method="POST">
                                                            <input type="hidden" value="${invoice.contributionID}" name="contributionID">
                                                            <input type="hidden" name="type" value="income">
                                                            <button type="submit" class="btn btn-primary">
                                                                <i class="fas fa-money-bill-wave"></i> Thanh toán
                                                            </button>

                                                        </form>



                                                    </c:if>

                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                                <nav class="pagination">
                                    <ul class="pagination">
                                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/invoices?userID=${userID}&status=${status}&termID=${termID}&page=${currentPage - 1}">Trước</a>
                                        </li>
                                        <c:forEach begin="1" end="${totalPages}" var="i">
                                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                <a class="page-link" href="${pageContext.request.contextPath}/invoices?userID=${userID}&status=${status}&termID=${termID}&page=${i}">${i}</a>
                                            </li>
                                        </c:forEach>
                                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/invoices?userID=${userID}&status=${status}&termID=${termID}&page=${currentPage + 1}">Sau</a>
                                        </li>
                                    </ul>
                                </nav>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-info text-center">
                                    Không có hóa đơn nào để hiển thị.
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
