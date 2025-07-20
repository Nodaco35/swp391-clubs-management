<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Lịch Sử Giao Dịch - UniClub</title>
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
            .filter-search-container {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 20px;
            }
            .filter-search-container select, .filter-search-container input {
                padding: 8px;
                border-radius: 5px;
                border: 1px solid #ccc;
            }
            .filter-search-container input {
                width: 300px;
            }
            .table th, .table td {
                vertical-align: middle;
                text-align: center;
            }
            .status-success {
                color: green;
                font-weight: bold;
            }
            .status-failed {
                color: red;
                font-weight: bold;
            }
            .status-pending {
                color: orange;
                font-weight: bold;
            }
            .no-data {
                text-align: center;
                font-style: italic;
                color: #666;
            }
            .pagination {
                justify-content: center;
                margin-top: 20px;
            }
            .pagination .page-item.disabled .page-link {
                cursor: not-allowed;
            }
        </style>
    </head>
    <body>
        <jsp:include page="/view/events-page/header.jsp" />
        <main>
            <header >
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1 class="h3 mb-1">Lịch sử giao dịch thành viên của ${sessionScope.user.fullName}</h1>
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb mb-0">
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/myclub" class="text-decoration-none">Trang chủ</a></li>
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/financial/cart-member-contribution" class="text-decoration-none">Hóa đơn</a></li>

                                <li class="breadcrumb-item active" aria-current="page">Lịch sử giao dịch</li>
                            </ol>
                        </nav>
                    </div>

                </div>
            </header>
            <div class="main-container">
                <h2 class="text-center mb-4">Lịch Sử Giao Dịch</h2>
                <div class="filter-search-container">
                    <div>
                        <form action="${pageContext.request.contextPath}/financial/transaction-history" method="get">
                            <label for="transResultFilter">Lọc theo trạng thái:</label>
                            <select id="transResultFilter" name="transResult" onchange="this.form.submit()">
                                <option value="" ${param.transResult == '' ? 'selected' : ''}>Tất cả</option>
                                <option value="Approved" ${param.transResult == 'Approved' ? 'selected' : ''}>Thành công</option>
                                <option value="Rejected" ${param.transResult == 'Rejected' ? 'selected' : ''}>Thất bại</option>
                                <option value="Pending" ${param.transResult == 'Pending' ? 'selected' : ''}>Đang xử lý</option>
                            </select>
                        </form>
                    </div>
                </div>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                <table class="table table-bordered table-striped">
                    <thead>
                        <tr>
                            <th>Mã giao dịch</th>
                            <th>Thông tin</th>
                            <th>Số tiền (VND)</th>
                            <th>Ngày giao dịch</th>
                            <th>Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody id="transactionTableBody">
                        <c:choose>
                            <c:when test="${empty transactions}">
                                <tr><td colspan="5" class="no-data">Không có giao dịch nào</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="transaction" items="${transactions}">
                                    <tr>
                                        <td>${transaction.transactionID}</td>  
                                        <td>${transaction.description}</td>
                                        <td><fmt:formatNumber value="${transaction.amount}" pattern="#,##0.00"/></td>
                                        <td>
                                            <fmt:formatDate value="${transaction.status == 'Pending' ? transaction.createdDate : transaction.transactionDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                        </td>
                                        <td class="status-${fn:toLowerCase(transaction.status)}">${transaction.status}</td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
                <nav aria-label="Page navigation">
                    <ul class="pagination">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/financial/transaction-history?page=${currentPage - 1}&transResult=${param.transResult}">Trước</a>
                        </li>
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/financial/transaction-history?page=${i}&transResult=${param.transResult}">${i}</a>
                            </li>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/financial/transaction-history?page=${currentPage + 1}&transResult=${param.transResult}">Sau</a>
                        </li>
                    </ul>
                </nav>
            </div>
        </main>
    </body>
</html>