<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="models.Users" %>
<%@page import="models.Clubs" %>

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
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ic-dashboard.css">
        <!-- Bootstrap 5 CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

        <!-- Bootstrap 5 JS (ở cuối body) -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

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
                        <h2 class="card-title">Danh sách đơn xin tạo/sửa CLB</h2>
                        <div class="card-actions">

                            <div class="filter-group">
                                <label class="filter-label">Lọc theo trạng thái</label>
                                <select id="statusFilter" class="filter-select" onchange="filterRequests()">
                                    <option value="all">Tất cả</option>
                                    <option value="Pending">Chờ duyệt</option>
                                    <option value="Approved">Đã duyệt</option>
                                    <option value="Rejected">Từ chối</option>
                                </select>

                            </div>

                            <div class="search-box">
                                <i class="fas fa-search search-icon"></i>
                                <input type="text" id="userIdSearch" class="search-input" placeholder="Nhập ID người dùng..." oninput="filterRequests()">
                            </div>
                        </div>
                    </div>

                    <div class="card-body">
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
                                        <th>Người gửi</th>
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
                                        <c:if test="${request.clubRequestStatus == 'Pending'}">
                                            <tr request-status="PENDING">
                                                <td>
                                                    <div class="user-info">
                                                        <div class="user-avatar">${fn:substring(request.chairmanFullName, 0, 1)}</div>
                                                        <div class="user-details">
                                                            <div class="user-name">${fn:escapeXml(request.chairmanFullName)}</div>
                                                            <div class="user-email">${request.chairmanID}</div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>${fn:escapeXml(request.clubName)}</td>
                                                <td>${request.categoryName}</td>
                                                <td><c:out value="${request.establishedDate}"></c:out></td>
                                                    <td><span class="badge bg-primary text-white"><i class="fas fa-clock"></i> Chờ duyệt</span>
                                                    </td>
                                                    <td class="table-actions">
                                                        <button class="btn btn-outline-secondary btn-sm" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=viewClubRequest&id=${request.clubID}'">
                                                        <i class="fas fa-eye"></i> Xem chi tiết
                                                    </button>
                                                    <button class="btn btn-icon btn-success" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=approvePermissionRequest&id=${request.clubID}&userID=${request.chairmanID}'">
                                                        <i class="fas fa-check"></i> Đồng ý
                                                    </button>
                                                    <button type="button"
                                                            class="btn btn-danger btn-sm"
                                                            onclick="showRejectModal('${request.clubID}', '${request.chairmanID}')">
                                                        <i class="fas fa-times"></i> Từ chối
                                                    </button>

                                                    <button class="btn btn-danger btn-sm" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=deletePermissionRequest&id=${request.clubID}&userID=${request.chairmanID}'">
                                                        <i class="fa-solid fa-trash"></i> Xoá
                                                    </button>

                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:forEach>

                                    <!-- Đơn bị từ chối -->
                                    <c:forEach var="request" items="${requests}" varStatus="loop">
                                        <c:if test="${request.clubRequestStatus == 'Rejected'}">
                                            <tr request-status="Rejected">
                                                <td>
                                                    <div class="user-info">
                                                        <div class="user-avatar">${fn:substring(request.chairmanFullName, 0, 1)}</div>
                                                        <div class="user-details">
                                                            <div class="user-name">${fn:escapeXml(request.chairmanFullName)}</div>
                                                            <div class="user-email">${request.chairmanID}</div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>${fn:escapeXml(request.clubName)}</td>
                                                <td>${request.categoryName}</td>
                                                <td><c:out value="${request.establishedDate}"></c:out></td>
                                                    <td><span class="badge bg-danger"><i class="fas fa-times"></i> Từ chối</span></td>
                                                    <td class="table-actions">
                                                        <button class="btn btn-outline-secondary btn-sm" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=viewClubRequest&id=${request.clubID}'">
                                                        <i class="fas fa-eye"></i> Xem chi tiết
                                                    </button>
                                                    <button class="btn btn-sm btn-success" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=approvePermissionRequest&id=${request.clubID}&userID=${request.chairmanID}'">
                                                        <i class="fas fa-check"></i> Đồng ý
                                                    </button>
                                                    <button class="btn btn-danger btn-sm" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=deletePermissionRequest&id=${request.clubID}&userID=${request.chairmanID}'">
                                                        <i class="fa-solid fa-trash"></i> Xoá
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:forEach>

                                    <!-- Đơn đã xong -->
                                    <c:forEach var="request" items="${approvedRequests}" varStatus="loop">
                                        <c:if test="${request.clubRequestStatus == 'Approved'}">
                                            <tr request-status="Approved">
                                                <td>
                                                    <div class="user-info">
                                                        <div class="user-avatar">${fn:substring(request.chairmanFullName, 0, 1)}</div>
                                                        <div class="user-details">
                                                            <div class="user-name">${fn:escapeXml(request.chairmanFullName)}</div>
                                                            <div class="user-email">${request.chairmanID}</div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>${fn:escapeXml(request.clubName)}</td>
                                                <td>${request.categoryName}</td>
                                                <td><c:out value="${request.establishedDate}"></c:out></td>
                                                    <td><span class="badge bg-success"><i class="fas fa-check"></i> Đã đồng ý</span></td>
                                                    <td class="table-actions">
                                                        <!-- Nút Xem chi tiết -->
                                                        <button class="btn btn-outline-secondary btn-sm" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=viewClubRequest&id=${request.clubID}'">
                                                        <i class="fas fa-eye"></i> Xem chi tiết
                                                    </button>
                                                    <button type="button"
                                                            class="btn btn-danger btn-sm"
                                                            onclick="showRejectModal('${request.clubID}', '${request.chairmanID}')">
                                                        <i class="fas fa-times"></i> Từ chối
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:forEach>

                                    <c:if test="${empty requests and empty approvedRequests}">
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
                            
                        <div class="table-container">
                            <table id="requestsTable">
                                <thead>
                                    <tr>
                                        <th>Người gửi</th>
                                        <th>Tên CLB</th>
                                        <th>Danh mục</th>
                                        <th>Ngày gửi</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <!-- Đơn chờ duyệt -->
                                    <c:forEach var="request" items="${updateRequests}" varStatus="loop">
                                        <c:if test="${request.clubRequestStatus == 'Pending'}">
                                            <tr request-status="PENDING">
                                                <td>
                                                    <div class="user-info">
                                                        <div class="user-avatar">${fn:substring(request.chairmanFullName, 0, 1)}</div>
                                                        <div class="user-details">
                                                            <div class="user-name">${fn:escapeXml(request.chairmanFullName)}</div>
                                                            <div class="user-email">${request.chairmanID}</div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>${fn:escapeXml(request.clubName)}</td>
                                                <td>${request.categoryName}</td>
                                                <td><c:out value="${request.establishedDate}"></c:out></td>
                                                    <td><span class="badge bg-primary text-white"><i class="fas fa-clock"></i> Chờ duyệt</span>
                                                    </td>
                                                    <td class="table-actions">
                                                        <button class="btn btn-outline-secondary btn-sm" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=viewUpdateClubRequest&id=${request.clubID}'">
                                                        <i class="fas fa-eye"></i> Xem chi tiết
                                                    </button>
                                                    <button class="btn btn-icon btn-success" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=approveUpdatePermissionRequest&id=${request.clubID}&userID=${request.chairmanID}'">
                                                        <i class="fas fa-check"></i> Đồng ý
                                                    </button>
                                                    <button type="button"
                                                            class="btn btn-danger btn-sm"
                                                            onclick="showUpdateRejectModal('${request.clubID}', '${request.chairmanID}')">
                                                        <i class="fas fa-times"></i> Từ chối
                                                    </button>

                                                    <button class="btn btn-danger btn-sm" onclick="window.location.href = '${pageContext.request.contextPath}/ic?action=deleteUpdatePermissionRequest&id=${request.clubID}&userID=${request.chairmanID}'">
                                                        <i class="fa-solid fa-trash"></i> Xoá
                                                    </button>

                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:forEach>


                                    <c:if test="${empty updateRequests and empty approvedRequests}">
                                        <tr class="original-empty-state">
                                            <td colspan="7" class="empty-state">
                                                <i class="fas fa-folder-open empty-icon"></i>
                                                <div class="empty-title">Không có đơn nào</div>
                                                <div class="empty-description">Hiện tại không có đơn xin sửa CLB nào.</div>
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
                                        
                
                <!-- Modal Bootstrap: Nhập lý do từ chối -->
                <div class="modal fade" id="rejectReasonModal" tabindex="-1" aria-labelledby="rejectReasonModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <form action="${pageContext.request.contextPath}/ic" method="get" class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="rejectReasonModalLabel">Lý do từ chối</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                            </div>
                            <div class="modal-body">
                                <input type="hidden" name="action" value="rejectPermissionRequest">
                                <input type="hidden" name="id" id="rejectClubID">
                                <input type="hidden" name="userID" id="rejectUserID">

                                <div class="mb-3">
                                    <label for="reason" class="form-label">Nhập lý do từ chối:</label>
                                    <textarea class="form-control" name="reason" id="reason" rows="4" required></textarea>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-danger">Từ chối</button>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            </div>
                        </form>
                    </div>
                </div>
                            
                <!-- Modal Bootstrap: Nhập lý do từ chối -->
                <div class="modal fade" id="rejectUpdateReasonModal" tabindex="-1" aria-labelledby="rejectReasonModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <form action="${pageContext.request.contextPath}/ic" method="get" class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="rejectReasonModalLabel">Lý do từ chối</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                            </div>
                            <div class="modal-body">
                                <input type="hidden" name="action" value="rejectUpdatePermissionRequest">
                                <input type="hidden" name="id" id="rejectClubID">
                                <input type="hidden" name="userID" id="rejectUserID">

                                <div class="mb-3">
                                    <label for="reason" class="form-label">Nhập lý do từ chối:</label>
                                    <textarea class="form-control" name="reason" id="reason" rows="4" required></textarea>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-danger">Từ chối</button>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            </div>
                        </form>
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
        <script>
            function showRejectModal(clubID, userID) {
                // Gán dữ liệu vào các hidden input
                document.getElementById("rejectClubID").value = clubID;
                document.getElementById("rejectUserID").value = userID;

                // Hiện modal Bootstrap
                const modal = new bootstrap.Modal(document.getElementById('rejectReasonModal'));
                modal.show();
            }
        </script>
        <script>
            function showUpdateRejectModal(clubID, userID) {
                // Gán dữ liệu vào các hidden input
                document.getElementById("rejectClubID").value = clubID;
                document.getElementById("rejectUserID").value = userID;

                // Hiện modal Bootstrap
                const modal = new bootstrap.Modal(document.getElementById('rejectUpdateReasonModal'));
                modal.show();
            }
        </script>

    </body>
</html>