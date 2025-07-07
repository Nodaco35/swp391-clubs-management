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
					<select id="status-filter" class="filter-select">
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
						<span class="stat-number">${pendingCount}</span>
						<span class="stat-label">Chờ duyệt</span>
					</div>
				</div>
				<div class="stat-card approved">
					<i class="fas fa-check-circle"></i>
					<div class="stat-info">
						<span class="stat-number">${approvedCount}</span>
						<span class="stat-label">Đã duyệt</span>
					</div>
				</div>
				<div class="stat-card rejected">
					<i class="fas fa-times-circle"></i>
					<div class="stat-info">
						<span class="stat-number">${rejectedCount}</span>
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
					<c:forEach var="a" items="${agendaList}" varStatus="loop">
						<tr class="event-row status-${a.status}">
							<td>#E${a.event.eventID}</td>
							<td>
								<div class="agenda-info">
									<strong>${a.event.eventName}</strong>
									<small>${a.event.clubName}</small>
								</div>
							</td>
							<td>${a.event.agendaCount} mục</td>
							<td><fmt:formatDate value="${a.event.eventDate}" pattern="yyyy-MM-dd HH:mm"/></td>
							<td>
								<c:choose>
									<c:when test="${a.status == 'PENDING'}">
										<span class="status-badge pending">Chờ duyệt</span>
									</c:when>
									<c:when test="${a.status == 'APPROVED'}">
										<span class="status-badge approved">Đã duyệt</span>
									</c:when>
									<c:when test="${a.status == 'REJECTED'}">
										<span class="status-badge rejected">Từ chối</span>
									</c:when>
								</c:choose>
							</td>
							<td>
								<div class="action-buttons">
									<button class="btn-details"
									        onclick="viewAgendaDetailsByEvent('${a.event.eventID}')">
										<i class="fas fa-eye"></i>
									</button>
								</div>
							</td>
						</tr>
					</c:forEach>
					</tbody>


				</table>
			</div>
			<div id="pagination-controls" class="pagination-controls"></div>
		</section>
	</div>
	<div id="detailsModal" class="modal">
		<div class="modal-content">
			<a href="${pageContext.request.contextPath}/ic/approval-agenda" class="close">×</a>
			<div class="modal-header">
				<h3>${agendaDetails[0].event.eventName}</h3>
				<div class="event-organization">
					<span>CLB ${agendaDetails[0].event.clubName}</span>
				</div>
			</div>
			<div class="modal-body">
				<c:if test="${not empty message}">
					<div class="message">${message}</div>
				</c:if>
				<c:if test="${not empty errorMessage}">
					<div class="error-message">${errorMessage}</div>
				</c:if>
				<div class="detail-card">
					<h4><i class="fas fa-info-circle"></i> Thông tin cơ bản</h4>
					<div class="detail-item">
						<span class="detail-label">Trạng thái</span>
						<span class="detail-value ${agendaDetails[0].status}">${agendaDetails[0].status}</span>
					</div>
					<div class="detail-item">
						<span class="detail-label">Ngày diễn ra</span>
						<span class="detail-value">
                        <fmt:formatDate value="${agendaDetails[0].event.eventDate}" pattern="yyyy-MM-dd"/>
                    </span>
					</div>
					<div class="detail-item">
						<span class="detail-label">Giờ diễn ra</span>
						<span class="detail-value">
                        <fmt:formatDate value="${agendaDetails[0].event.eventDate}" pattern="HH:mm"/>
                    </span>
					</div>
					<div class="detail-item">
						<span class="detail-label">Tổng số mục</span>
						<span class="detail-value">${agendaDetails.size()} mục</span>
					</div>
				</div>
				<div class="agenda-timeline">
					<h4><i class="fas fa-clock"></i> Chi tiết Agenda</h4>
					<c:forEach var="agenda" items="${agendaDetails}" varStatus="loop">
						<div class="agenda-item">
							<div class="agenda-time">
								<fmt:formatDate value="${agenda.startTime}" pattern="HH:mm"/> -
								<fmt:formatDate value="${agenda.endTime}" pattern="HH:mm"/>
							</div>
							<div class="agenda-content">
								<h5>${agenda.title}</h5>
								<p>${agenda.description}</p>
							</div>
						</div>
					</c:forEach>
				</div>
				<div class="rejection-section" style="display: none;">
					<h4><i class="fas fa-exclamation-triangle"></i> Lý do từ chối</h4>
					<form method="post" action="${pageContext.request.contextPath}/ic/approval-agenda">
						<input type="hidden" name="eventID" value="${agendaDetails[0].event.eventID}">
						<input type="hidden" name="status" value="rejected">
						<label for="rejectionReason">Vui lòng nhập lý do từ chối agenda này:</label>
						<textarea id="rejectionReason" name="reason" placeholder="Nhập lý do từ chối chi tiết..."
						          rows="4" required></textarea>
						<div class="modal-actions">
							<button type="submit" class="modal-btn confirm-reject">
								<i class="fas fa-ban"></i> Xác nhận từ chối
							</button>
						</div>
					</form>
				</div>
			</div>
			<div class="modal-actions">
				<a href="${pageContext.request.contextPath}/ic/approval-agenda" class="modal-btn cancel">
					<i class="fas fa-times"></i> Đóng
				</a>
				<form method="post" action="${pageContext.request.contextPath}/ic/approval-agenda"
				      style="display: inline;">
					<input type="hidden" name="eventID" value="${agendaDetails[0].event.eventID}">
					<input type="hidden" name="status" value="approved">
					<button type="submit" class="modal-btn approve">
						<i class="fas fa-check-circle"></i> Duyệt
					</button>
				</form>
				<button class="modal-btn reject" onclick="showRejectionSection()">
					<i class="fas fa-times-circle"></i> Từ chối
				</button>
			</div>
		</div>
	</div>
</main>
<script>
    function viewAgendaDetailsByEvent(eventId) {
        window.location.href = '${pageContext.request.contextPath}/ic/approval-agenda?eventID=' + eventId;
    }

    function showRejectionSection() {
        const rejectionSection = document.querySelector('.rejection-section');
        if (rejectionSection) {
            rejectionSection.style.display = 'block';
        }
    }

    <c:if test="${not empty agendaDetails}">
    document.getElementById('detailsModal').style.display = 'block';
    </c:if>
</script>

<script>
    const rowsPerPage = 3;
    let currentPage = 1;

    function filterAndPaginate() {
        const selected = document.getElementById("status-filter").value;
        const allRows = Array.from(document.querySelectorAll(".event-row"));

        // Hide all rows first
        allRows.forEach(row => row.style.display = "none");

        // Filter by status
        let filteredRows = selected === "all"
            ? allRows
            : allRows.filter(row => row.classList.contains("status-" + selected.toUpperCase()));

        // Pagination
        const totalPages = Math.ceil(filteredRows.length / rowsPerPage);
        const startIndex = (currentPage - 1) * rowsPerPage;
        const endIndex = startIndex + rowsPerPage;

        filteredRows.slice(startIndex, endIndex).forEach(row => row.style.display = "");

        renderPaginationControls(totalPages);
    }

    function renderPaginationControls(totalPages) {
        const container = document.getElementById("pagination-controls");
        if (!container) return;

        container.innerHTML = "";
        for (let i = 1; i <= totalPages; i++) {
            const btn = document.createElement("button");
            btn.textContent = i;
            btn.className = i === currentPage ? "active" : "";
            btn.onclick = function () {
                currentPage = i;
                filterAndPaginate();
            };
            container.appendChild(btn);
        }
    }

    document.getElementById("status-filter").addEventListener("change", () => {
        currentPage = 1;
        filterAndPaginate();
    });

    window.onload = () => filterAndPaginate();
</script>
</body>
</html>
