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
                            <button class="btn btn-outline">
                                <i class="fas fa-filter"></i> Lọc
                            </button>
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
                                    <c:forEach items="${pendingClubRequests}" var="request">
                                        <tr>
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

                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title">CLB mới thành lập</h2>
                        <div class="card-actions">
                            <button class="btn btn-outline">
                                <i class="fas fa-filter"></i> Lọc
                            </button>
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
                                    <c:forEach items="${recentlyApprovedClubs}" var="club">
                                        <tr>
                                            <td>#CLB${club.clubId}</td>
                                            <td>${club.clubName}</td>
                                            <td>${club.presidentName}</td>
                                            <td>${club.formattedFoundingDate}</td>
                                            <td>${club.memberCount}</td>
                                            <td>
                                                <span class="badge badge-approved">
                                                    <i class="fas fa-check"></i> Hoạt động
                                                </span>
                                            </td>
                                            <td>
                                                <div class="table-actions">
                                                    <button class="btn btn-icon btn-outline" onclick="viewClub(${club.clubId})">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                    <button class="btn btn-icon btn-outline" onclick="editClub(${club.clubId})">
                                                        <i class="fas fa-edit"></i>
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

        <!-- Modal xem đơn đề xuất -->
        <c:forEach items="${pendingClubRequests}" var="request">
            <div id="viewProposalModal${request.requestId}" class="modal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2 class="modal-title">Đơn đề xuất thành lập CLB</h2>
                        <button class="modal-close" onclick="closeModal('viewProposalModal${request.requestId}')">&times;</button>
                    </div>
                    <div class="modal-body">
                        <!-- Chi tiết đơn đề xuất -->
                        <div class="form-group">
                            <label class="form-label">ID đơn</label>
                            <input type="text" class="form-control" value="#CLB${request.requestId}" readonly>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Tên CLB</label>
                            <input type="text" class="form-control" value="${request.clubName}" readonly>
                        </div>
                        <!-- Các trường khác -->
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-outline" onclick="closeModal('viewProposalModal${request.requestId}')">Đóng</button>
                        <button class="btn btn-error" onclick="rejectRequest(${request.requestId})">Từ chối</button>
                        <button class="btn btn-success" onclick="approveRequest(${request.requestId})">Phê duyệt</button>
                    </div>
                </div>
            </div>
        </c:forEach>

        <!-- JavaScript -->
        <script src="${pageContext.request.contextPath}/js/admin-dashboard.js"></script>
    </body>
</html>

