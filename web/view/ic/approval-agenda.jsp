<%-- 
    Document   : approval-agenda
    Created on : Jul 6, 2025, 12:07:46 PM
    Author     : LE VAN THUAN
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
	<title>Duyệt Agenda</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/ic-approval.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
<header>


	<aside class="sidebar">
		<div class="sidebar-header">
			<i class="fas fa-university"></i>
			<h2>IC Officer Portal</h2>
		</div>
		<ul class="sidebar-menu">
			<li>
				<a class="${fn:contains(currentPath, '/ic/dashboard') ? 'active' : ''}"
				   href="${pageContext.request.contextPath}/ic/dashboard">
					<i class="fas fa-file-alt"></i> Báo cáo hàng kỳ
				</a>
			</li>
			<li>
				<a class="${fn:contains(currentPath, '/ic/statistics') ? 'active' : ''}"
				   href="${pageContext.request.contextPath}/ic/statistics">
					<i class="fas fa-chart-line"></i> Thống kê hiệu suất
				</a>
			</li>
			<li>
				<a class="${fn:contains(currentPath, '/ic') and param.action == 'grantPermission' ? 'active' : ''}"
				   href="${pageContext.request.contextPath}/ic?action=grantPermission">
					<i class="fas fa-users"></i> Danh sách đơn tạo CLB
				</a>
			</li>
			<li>
				<a class="${fn:contains(currentPath, '/ic/approval-events') ? 'active' : ''}"
				   href="${pageContext.request.contextPath}/ic/approval-events">
					<i class="fas fa-calendar-alt"></i> Duyệt sự kiện
				</a>
			</li>
			<li>
				<a class="${fn:contains(currentPath, '/ic/approval-agenda') ? 'active' : ''}"
				   href="${pageContext.request.contextPath}/ic/approval-agenda">
					<i class="fas fa-clipboard-list"></i> Duyệt Agenda
				</a>
			</li>
			<li>
				<a class="${fn:contains(currentPath, '/ic/schedule') ? 'active' : ''}"
				   href="${pageContext.request.contextPath}/ic/schedule">
					<i class="fas fa-calendar-check"></i> Lịch báo cáo
				</a>
			</li>
			<li>
				<a class="${fn:contains(currentPath, '/ic/notifications') ? 'active' : ''}"
				   href="${pageContext.request.contextPath}/ic/notifications">
					<i class="fas fa-bell"></i> Thông báo
				</a>
			</li>
			<li>
				<a class="${fn:contains(currentPath, '/ic/settings') ? 'active' : ''}"
				   href="${pageContext.request.contextPath}/ic/settings">
					<i class="fas fa-cog"></i> Cài đặt
				</a>
			</li>
			<li>
				<a href="${pageContext.request.contextPath}/logout">
					<i class="fas fa-sign-out-alt"></i> Đăng xuất
				</a>
			</li>
		</ul>
	</aside>
</header>
<main class="main-content">
	<div class="header">
		<h1>Quản lý Agenda</h1>
		<div class="user-info">
			<div class="user-avatar">IC</div>
			<div class="user-details">
				<div class="user-name">${sessionScope.user.fullName}</div>
				<div class="user-role">IC Officer</div>
			</div>
		</div>
	</div>
	<div class="content-wrapper">

		<section id="agenda-section" class="content-section">
			<div class="section-header">
				<h2>Danh sách Agenda cần duyệt</h2>
				<div class="filters">
					<span class="filter-label">Lọc theo trạng thái</span>
					<select class="filter-select">
						<option value="all">Tất cả</option>
						<option value="pending">Chờ duyệt</option>
						<option value="approved">Đã duyệt</option>
						<option value="rejected">Từ chối</option>
					</select>
				</div>
			</div>

			<div class="stats-summary">
				<div class="stat-card">
					<i class="fas fa-clock"></i>
					<div class="stat-info">
						<span class="stat-number">4</span>
						<span class="stat-label">Chờ duyệt</span>
					</div>
				</div>
				<div class="stat-card approved">
					<i class="fas fa-check-circle"></i>
					<div class="stat-info">
						<span class="stat-number">0</span>
						<span class="stat-label">Đã duyệt</span>
					</div>
				</div>
				<div class="stat-card rejected">
					<i class="fas fa-times-circle"></i>
					<div class="stat-info">
						<span class="stat-number">0</span>
						<span class="stat-label">Từ chối</span>
					</div>
				</div>
			</div>

			<div class="table-container">
				<table class="data-table">
					<thead>
					<tr>
						<th>ID</th>
						<th>Tên sự kiện</th>
						<th>Số lượng mục</th>
						<th>Ngày diễn ra</th>
						<th>Trạng thái</th>
						<th>Thao tác</th>
					</tr>
					</thead>
					<tbody id="agenda-table-body">
					<tr>
						<td>#A001</td>
						<td>
							<div class="agenda-info">
								<strong>Thử Thách Lập Trình FPTU 2025</strong>
								<small>CLB Lập trình</small>
							</div>
						</td>
						<td>6 mục</td>
						<td>2025-06-21</td>
						<td><span class="status-badge pending">Chờ duyệt</span></td>
						<td>
							<div class="action-buttons">
								<button class="btn-details" onclick="viewAgendaDetails('A001')">
									<i class="fas fa-eye"></i>
								</button>
							</div>
						</td>
					</tr>
					<tr>
						<td>#A002</td>
						<td>
							<div class="agenda-info">
								<strong>Hackathon Đổi Mới AI</strong>
								<small>CLB Lập trình</small>
							</div>
						</td>
						<td>6 mục</td>
						<td>2025-07-15</td>
						<td><span class="status-badge pending">Chờ duyệt</span></td>
						<td>
							<div class="action-buttons">
								<button class="btn-details" onclick="viewAgendaDetails('A002')">
									<i class="fas fa-eye"></i>
								</button>
							</div>
						</td>
					</tr>
					<tr>
						<td>#A003</td>
						<td>
							<div class="agenda-info">
								<strong>Workshop: Xây dựng Website với Spring Boot</strong>
								<small>CLB Lập trình</small>
							</div>
						</td>
						<td>9 mục</td>
						<td>2025-04-20</td>
						<td><span class="status-badge pending">Chờ duyệt</span></td>
						<td>
							<div class="action-buttons">
								<button class="btn-details" onclick="viewAgendaDetails('A003')">
									<i class="fas fa-eye"></i>
								</button>
							</div>
						</td>
					</tr>
					<tr>
						<td>#A004</td>
						<td>
							<div class="agenda-info">
								<strong>Giải Bóng Đá Sinh Viên FPTU 2025</strong>
								<small>CLB Thể thao</small>
							</div>
						</td>
						<td>6 mục</td>
						<td>2025-08-05</td>
						<td><span class="status-badge pending">Chờ duyệt</span></td>
						<td>
							<div class="action-buttons">
								<button class="btn-details" onclick="viewAgendaDetails('A004')">
									<i class="fas fa-eye"></i>
								</button>
							</div>
						</td>
					</tr>
					</tbody>
				</table>
			</div>
		</section>
	</div>
</main>
</body>
</html>
