<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý đơn xin tạo CLB - UniClub Admin</title>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&family=Montserrat:wght@600&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-LN+7fdVzj6u52u30Kp6M/trliBMCMKTyK833zpbD+pXdCLuTusPj697FH4R/5mcr" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    </head>
    <body>
        <div class="dashboard">
            <jsp:include page="components/ic-sidebar.jsp" />

            <main class="main-content">
                <div class="page-header">
                    <h1 class="page-title">Quản lý đơn xin tạo CLB</h1>
                    <div class="user-profile">
                        <div class="user-avatar">${fn:substring(sessionScope.user.fullName, 0, 1)}</div>
                        <div class="user-info">
                            <div class="user-name">${fn:escapeXml(sessionScope.user.fullName)}</div>
                            <div class="user-role">IC Officer</div>
                        </div>
                    </div>
                </div>

                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">Danh sách đơn xin tạo CLB</h2>
                        <div class="card-actions">

                            <div class="filter-group">
                                <label class="filter-label">Lọc theo trạng thái</label>
                                <select id="statusFilter" class="filter-select" onchange="filterRequests()">
                                    <option value="all">Tất cả</option>
                                    <option value="PENDING">Chờ duyệt</option>
                                    <option value="APPROVED">Đã duyệt</option>
                                    <option value="REJECTED">Từ chối</option>
                                    <option value="USED">Đã dùng</option>
                                </select>
                            </div>

                            <div class="search-box">
                                <i class="fas fa-search search-icon"></i>
                                <input type="text" id="userIdSearch" class="search-input" placeholder="Nhập ID người dùng..." oninput="filterRequests()">
                            </div>
                        </div>
                    </div>

                    <div class="card-body">
                        <h4>Có ${numberRequest} đơn đang chờ duyệt</h4>
                        <c:if test="${not empty successMessage}">
                            <div class="alert alert-success" id="successMessage">
                                <i class="fas fa-check-circle"></i> ${successMessage}
                            </div>
                            <script>
                                setTimeout(() => {
                                    const message = document.getElementById('successMessage');
                                    if (message) {
                                        message.style.display = 'none';
                                    }
                                }, 4000);
                            </script>
                        </c:if>

                        <c:if test="${not empty rejectedMessage}">
                            <div class="alert alert-warning" id="rejectedMessage">
                                <i class="fas fa-check-circle"></i> ${rejectedMessage}
                            </div>
                            <script>
                                setTimeout(() => {
                                    const message = document.getElementById('rejectedMessage');
                                    if (message) {
                                        message.style.display = 'none';
                                    }
                                }, 4000);
                            </script>
                        </c:if>

                        <c:if test="${not empty deletedMessage}">
                            <div class="alert alert-danger" id="deletedMessage">
                                <i class="fas fa-check-circle"></i> ${deletedMessage}
                            </div>
                            <script>
                                setTimeout(() => {
                                    const message = document.getElementById('deletedMessage');
                                    if (message) {
                                        message.style.display = 'none';
                                    }
                                }, 4000);
                            </script>
                        </c:if>

                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-warning" id="errorMessage">
                                <i class="fas fa-exclamation-circle"></i> ${errorMessage}
                            </div>
                            <script>
                                setTimeout(() => {
                                    const message = document.getElementById('errorMessage');
                                    if (message) {
                                        message.style.display = 'none';
                                    }
                                }, 4000);
                            </script>
                        </c:if>

                        <div class="table-container">
                            <table id="requestsTable">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Người dùng</th>
                                        <th>Tên CLB</th>
                                        <th>Danh mục</th>
                                        <th>Ngày gửi</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <!-- Đơn chờ duyệt -->
                                    <c:forEach var="request" items="${requests}" varStatus="loop">
                                        <c:if test="${request.status == 'PENDING'}">
                                            <tr request-status="PENDING">
                                                <td>#${request.id}</td>
                                                <td>
                                                    <div class="user-info">
                                                        <div class="user-avatar">${fn:substring(request.userName, 0, 1)}</div>
                                                        <div class="user-details">
                                                            <div class="user-name">${fn:escapeXml(request.userName)}</div>
                                                            <div class="user-email">${request.userID}</div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>${fn:escapeXml(request.clubName)}</td>
                                                <td>${request.category}</td>
                                                <td><c:out value="${request.requestDate}"></c:out></td>
                                                    <td><span class="badge badge-pending"><i class="fas fa-clock"></i> Chờ duyệt</span></td>
                                                    <td class="table-actions">

                                                        <button class="btn btn-icon btn-success" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=approvePermissionRequest&id=${request.id}&userID=${request.userID}'">
                                                        <i class="fas fa-check"></i>
                                                    </button>
                                                    <button class="btn btn-icon btn-error" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=rejectPermissionRequest&id=${request.id}&userID=${request.userID}'">
                                                        <i class="fas fa-times"></i>
                                                    </button>
                                                    <button class="btn btn-icon btn-error" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=deletePermissionRequest&id=${request.id}&userID=${request.userID}'">
                                                        <i class="fa-solid fa-trash"></i>
                                                    </button>

                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:forEach>

                                    <!-- Đơn đã duyệt -->
                                    <c:forEach var="request" items="${requests}" varStatus="loop">
                                        <c:if test="${request.status == 'APPROVED'}">
                                            <tr request-status="APPROVED">
                                                <td>#${request.id}</td>
                                                <td>
                                                    <div class="user-info">
                                                        <div class="user-avatar">${fn:substring(request.userName, 0, 1)}</div>
                                                        <div class="user-details">
                                                            <div class="user-name">${fn:escapeXml(request.userName)}</div>
                                                            <div class="user-email">${request.userID}</div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>${fn:escapeXml(request.clubName)}</td>
                                                <td>${request.category}</td>
                                                <td><c:out value="${request.requestDate}"></c:out></td>
                                                    <td><span class="badge badge-approved"><i class="fas fa-check"></i> Đã duyệt</span></td>

                                                    <td class="table-actions">
                                                        <button class="btn btn-icon btn-error" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=rejectPermissionRequest&id=${request.id}&userID=${request.userID}'">
                                                        <i class="fas fa-times"></i>
                                                    </button>
                                                    <button class="btn btn-icon btn-error" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=deletePermissionRequest&id=${request.id}&userID=${request.userID}'">
                                                        <i class="fa-solid fa-trash"></i>
                                                    </button>

                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:forEach>

                                    <!-- Đơn bị từ chối -->
                                    <c:forEach var="request" items="${requests}" varStatus="loop">
                                        <c:if test="${request.status == 'REJECTED'}">
                                            <tr request-status="REJECTED">
                                                <td>#${request.id}</td>
                                                <td>
                                                    <div class="user-info">
                                                        <div class="user-avatar">${fn:substring(request.userName, 0, 1)}</div>
                                                        <div class="user-details">
                                                            <div class="user-name">${fn:escapeXml(request.userName)}</div>
                                                            <div class="user-email">${request.userID}</div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>${fn:escapeXml(request.clubName)}</td>
                                                <td>${request.category}</td>
                                                <td><c:out value="${request.requestDate}"></c:out></td>
                                                    <td><span class="badge badge-rejected"><i class="fas fa-times"></i> Từ chối</span></td>
                                                    <td class="table-actions">

                                                        <button class="btn btn-icon btn-success" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=approvePermissionRequest&id=${request.id}&userID=${request.userID}'">
                                                        <i class="fas fa-check"></i>
                                                    </button>
                                                    <button class="btn btn-icon btn-error" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=deletePermissionRequest&id=${request.id}&userID=${request.userID}'">
                                                        <i class="fa-solid fa-trash"></i>
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:forEach>

                                    <!-- Đơn đã dùng -->
                                    <c:forEach var="request" items="${requests}" varStatus="loop">
                                        <c:if test="${request.status == 'USED'}">
                                            <tr request-status="USED">
                                                <td>#${request.id}</td>
                                                <td>
                                                    <div class="user-info">
                                                        <div class="user-avatar">${fn:substring(request.userName, 0, 1)}</div>
                                                        <div class="user-details">
                                                            <div class="user-name">${fn:escapeXml(request.userName)}</div>
                                                            <div class="user-email">${request.userID}</div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>${fn:escapeXml(request.clubName)}</td>
                                                <td>${request.category}</td>
                                                <td><c:out value="${request.requestDate}"></c:out></td>
                                                    <td><span class="badge badge-info"><i class="fas fa-check-circle"></i> Đã dùng</span></td>
                                                    <td class="table-actions"></td>
                                                </tr>
                                        </c:if>
                                    </c:forEach>

                                    <c:if test="${empty requests}">
                                        <tr class="original-empty-state">
                                            <td colspan="7" class="empty-state">
                                                <i class="fas fa-folder-open empty-icon"></i>
                                                <div class="empty-title">Không có đơn nào</div>
                                                <div class="empty-description">Hiện tại không có đơn xin tạo CLB nào.</div>
                                            </td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>

                        <!-- Modal xác nhận -->
                        <div id="confirmModal" class="modal">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h3 class="modal-title">Xác nhận hành động</h3>
                                    <button class="modal-close" onclick="closeConfirmModal()">×</button>
                                </div>
                                <div class="modal-body">
                                    <p id="modalText"></p>
                                </div>
                                <div class="modal-footer">
                                    <form id="confirmForm" action="${pageContext.request.contextPath}/ic" method="post">
                                        <input type="hidden" name="action" value="processRequest">
                                        <input type="hidden" name="requestId" id="requestId">
                                        <input type="hidden" name="status" id="modalStatus">
                                        <button type="submit" class="btn btn-primary">Xác nhận</button>
                                        <button type="button" class="btn btn-outline" onclick="closeConfirmModal()">Hủy</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>

        <script>
            function filterRequests() {
                const status = document.getElementById('statusFilter').value;
                const userId = document.getElementById('userIdSearch').value.toLowerCase().trim();
                const rows = document.querySelectorAll('#requestsTable tbody tr[request-status]');
                const tbody = document.querySelector('#requestsTable tbody');

                let visibleCount = 0;

                rows.forEach(row => {
                    const rowStatus = row.getAttribute('request-status');
                    // Lấy userID từ .user-email element
                    const userEmailElement = row.querySelector('.user-email');
                    const rowUserId = userEmailElement ? userEmailElement.textContent.toLowerCase().trim() : '';

                    const statusMatch = status === 'all' || rowStatus === status;
                    const userIdMatch = userId === '' || rowUserId.includes(userId);

                    if (statusMatch && userIdMatch) {
                        row.style.display = '';
                        visibleCount++;
                    } else {
                        row.style.display = 'none';
                    }
                });

                // Xử lý empty state
                let originalEmptyRow = tbody.querySelector('.original-empty-state');
                let filterEmptyRow = tbody.querySelector('.filter-empty-state');

                if (visibleCount === 0) {
                    // Ẩn empty state gốc nếu có
                    if (originalEmptyRow) {
                        originalEmptyRow.style.display = 'none';
                    }

                    // Tạo empty state mới cho filter nếu chưa có
                    if (!filterEmptyRow) {
                        filterEmptyRow = document.createElement('tr');
                        filterEmptyRow.className = 'filter-empty-state';
                        filterEmptyRow.innerHTML = `
                            <td colspan="7" class="empty-state">
                                <i class="fas fa-folder-open empty-icon"></i>
                                <div class="empty-title">Không có đơn nào</div>
                                <div class="empty-description">Không tìm thấy đơn phù hợp với bộ lọc.</div>
                            </td>
                        `;
                        tbody.appendChild(filterEmptyRow);
                    }
                    filterEmptyRow.style.display = '';
                } else {
                    // Hiện empty state gốc nếu có và không có data
                    if (originalEmptyRow) {
                        originalEmptyRow.style.display = '';
                    }

                    // Ẩn empty state của filter
                    if (filterEmptyRow) {
                        filterEmptyRow.style.display = 'none';
                    }
                }
            }


            // Khởi tạo filter khi trang load
            document.addEventListener('DOMContentLoaded', function () {
                filterRequests();
            });

            // Hàm tự động ẩn thông báo sau một khoảng thời gian (mặc định là 10 giây)
            function hideNotificationAfterTimeout(elementId, timeout = 10000) {
                const notification = document.getElementById(elementId);
                if (notification) {
                    setTimeout(() => {
                        notification.style.display = 'none';  // Ẩn thông báo sau 10 giây
                    }, timeout);
            }
            }

            // Khi DOM đã sẵn sàng, kiểm tra và ẩn thông báo success/error
            window.onload = function () {
                hideNotificationAfterTimeout('successMessage');  // Ẩn success message sau 10 giây
                hideNotificationAfterTimeout('errorMessage');    // Ẩn error message sau 10 giây
            };
        </script>
    </body>
</html>