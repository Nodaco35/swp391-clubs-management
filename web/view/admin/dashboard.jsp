<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Admin Dashboard - UniClub</title>

        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">

        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <!-- CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    </head>
    <body>
        <div class="dashboard">
            <jsp:include page="components/admin-sidebar.jsp" />

            <main class="main-content">
                <div class="header">
                    <h1 class="page-title">Dashboard</h1>
                    <div class="user-profile">
                        <div class="user-avatar">A</div>
                        <div class="user-info">
                            <div class="user-name">${sessionScope.user.fullName}</div>
                            <div class="user-role">Quản trị viên</div>
                        </div>
                    </div>
                </div>

                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-header">
                            <div class="stat-title">Tổng số CLB</div>
                            <div class="stat-icon blue">
                                <i class="fas fa-users"></i>
                            </div>
                        </div>
                        <div class="stat-value">${totalClubs}</div>
                        <div class="stat-description">8 CLB đang tuyển thành viên</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-header">
                            <div class="stat-title">Tổng số thành viên</div>
                            <div class="stat-icon green">
                                <i class="fas fa-user-graduate"></i>
                            </div>
                        </div>
                        <div class="stat-value">${totalMembers}</div>
                        <div class="stat-description">Tăng 15% so với kỳ trước</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-header">
                            <div class="stat-title">Đơn đề xuất mới</div>
                            <div class="stat-icon yellow">
                                <i class="fas fa-clipboard-list"></i>
                            </div>
                        </div>
                        <div class="stat-value">${pendingRequests}</div>
                        <div class="stat-description">${pendingRequests} đơn chưa được xử lý</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-header">
                            <div class="stat-title">Sự kiện sắp tới</div>
                            <div class="stat-icon red">
                                <i class="fas fa-calendar-alt"></i>
                            </div>
                        </div>
                        <div class="stat-value">${upcomingEventsCount}</div>
                        <div class="stat-description">3 sự kiện trong tuần này</div>
                    </div>
                </div>

                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">Đơn đề xuất thành lập CLB mới</h2>
                        <div class="card-actions">
                            <div class="filter-group">
                                <label class="filter-label">Lọc theo trạng thái</label>
                                <select class="filter-select" onchange="filterApplications(this.value)">
                                    <option value="all">Tất cả</option>
                                    <option value="PENDING">Chờ duyệt</option>
                                    <option value="APPROVED">Đã duyệt</option>
                                    <option value="REJECTED">Từ chối</option>
                                </select>
                            </div>
                            <button class="btn btn-outline">
                                <i class="fas fa-download"></i> Xuất
                            </button>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Tên CLB</th>
                                        <th>Người đề xuất</th>
                                        <th>Ngày đề xuất</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <!-- Đơn chờ duyệt -->
                                    <c:forEach items="${pendingClubRequests}" var="request">
                                        <tr request-status="PENDING">
                                            <td>#CLB${request.applicationID}</td>
                                            <td>${request.clubName}</td>
                                            <td>${request.userID}</td>
                                            <td>
                                                <fmt:formatDate value="${request.submitDate}" pattern="dd/MM/yyyy HH:mm" />
                                            </td>
                                            <td>
                                                <span class="badge badge-pending">
                                                    <i class="fas fa-clock"></i> Chờ duyệt
                                                </span>
                                            </td>
                                            <td>
                                                <div class="table-actions">
                                                    <button class="btn btn-icon btn-outline" onclick="openModal('viewProposalModal${request.applicationID}')">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                    <button class="btn btn-icon btn-success" onclick="approveRequest(${request.applicationID})">
                                                        <i class="fas fa-check"></i>
                                                    </button>
                                                    <button class="btn btn-icon btn-error" onclick="rejectRequest(${request.applicationID})">
                                                        <i class="fas fa-times"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                    <!-- Đơn đã duyệt -->
                                    <c:forEach items="${approvedClubRequests}" var="request">
                                        <tr request-status="APPROVED">
                                            <td>#CLB${request.applicationID}</td>
                                            <td>${request.clubName}</td>
                                            <td>${request.userID}</td>
                                            <td>
                                                <fmt:formatDate value="${request.submitDate}" pattern="dd/MM/yyyy HH:mm" />
                                            </td>
                                            <td>
                                                <span class="badge badge-approved">
                                                    <i class="fas fa-check"></i> Đã duyệt
                                                </span>
                                            </td>
                                            <td>
                                                <div class="table-actions">
                                                    <button class="btn btn-icon btn-outline" onclick="openModal('viewProposalModal${request.applicationID}')">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                    <!-- Đơn bị từ chối -->
                                    <c:forEach items="${rejectedClubRequests}" var="request">
                                        <tr request-status="REJECTED">
                                            <td>#CLB${request.applicationID}</td>
                                            <td>${request.clubName}</td>
                                            <td>${request.userID}</td>
                                            <td>
                                                <fmt:formatDate value="${request.submitDate}" pattern="dd/MM/yyyy HH:mm" />
                                            </td>
                                            <td>
                                                <span class="badge badge-rejected">
                                                    <i class="fas fa-times"></i> Từ chối
                                                </span>
                                            </td>
                                            <td>
                                                <div class="table-actions">
                                                    <button class="btn btn-icon btn-outline" onclick="openModal('viewProposalModal${request.applicationID}')">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>


                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <div class="card">
                    <div class="card-header">

                        <h2 class="card-title">Câu lạc bộ</h2>

                        <div class="card-actions">
                            <label class="filter-label">Trạng thái</label>
                            <select class="filter-select" onchange="filterClubs(this.value)">
                                <option value="all">Tất cả</option>
                                <option value="active">Hoạt động</option>
                                <option value="inactive">Ngừng hoạt động</option>
                            </select>
                            <button class="btn btn-primary">
                                <i class="fas fa-plus"></i> Thêm CLB
                            </button>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Tên CLB</th>
                                        <th>Chủ nhiệm</th>
                                        <th>Ngày thành lập</th>
                                        <th>Số thành viên</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <!-- CLB đang hoạt động -->
                                    <c:forEach items="${activeClubs}" var="club">
                                        <tr data-status="active">
                                            <td>#CLB${club.clubID}</td>
                                            <td>${club.clubName}</td>
                                            <td></td>
                                            <td>${club.establishedDate}</td>
                                            <td>${club.memberCount}</td>
                                            <td>
                                                <span class="badge badge-approved">
                                                    <i class="fas fa-check"></i> Hoạt động
                                                </span>
                                            </td>
                                            <td>
                                                <div class="table-actions">
                                                    <button class="btn btn-icon btn-outline" onclick="viewClub(${club.clubID})">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                    <button class="btn btn-icon btn-outline" onclick="editClub(${club.clubID})">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                    <!-- CLB không hoạt động -->
                                    <c:forEach items="${inactiveClubs}" var="club">
                                        <tr data-status="inactive">
                                            <td>#CLB${club.clubID}</td>
                                            <td>${club.clubName}</td>
                                            <td></td>
                                            <td>${club.establishedDate}</td>
                                            <td>${club.memberCount}</td>
                                            <td>
                                                <span class="badge badge-rejected">
                                                    <i class="fas fa-times"></i> Ngừng hoạt động
                                                </span>
                                            </td>
                                            <td>
                                                <div class="table-actions">
                                                    <button class="btn btn-icon btn-outline" onclick="viewClub(${club.clubID})">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>

                            </table>
                        </div>
                    </div>
                </div>
            </main>
        </div>

        <script>
            function filterApplications(status) {
                // Chọn tất cả các hàng có thuộc tính data-status (cả PENDING, APPROVED, REJECTED)
                const rows = document.querySelectorAll('tbody tr[request-status]');

                rows.forEach(row => {
                    const rowStatus = row.getAttribute('request-status');

                    // Hiển thị nếu trạng thái trùng hoặc đang chọn "Tất cả"
                    const shouldShow = (status === 'all' || rowStatus === status);
                    row.style.display = shouldShow ? '' : 'none';
                });
            }

            // Đóng modal khi click ra ngoài (nếu bạn có modal sử dụng class 'modal')
            window.onclick = function (event) {
                if (event.target.classList.contains('modal')) {
                    event.target.style.display = 'none';
                }
            };
        </script>
        <script>
            function filterClubs(status) {
                const rows = document.querySelectorAll('tbody tr[data-status]');
                rows.forEach(row => {
                    const rowStatus = row.getAttribute('data-status');
                    const show = (status === 'all' || rowStatus === status);
                    row.style.display = show ? '' : 'none';
                });
            }
        </script>

    </body>
</html>