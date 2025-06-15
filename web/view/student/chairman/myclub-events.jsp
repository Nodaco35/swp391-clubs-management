<%-- 
    Document   : myclub-events
    Created on : Jun 15, 2025, 11:26:48 AM
    Author     : LE VAN THUAN
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
	<title>Chairman Page</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
<div id="events-tab" class="tab-content">
	<section class="stats-section">
		<h2>Tổng quan sự kiện</h2>
		<div class="stats-grid">
			<div class="stat-card">
				<div class="stat-icon upcoming">
					<i class="fas fa-calendar-plus"></i>
				</div>
				<div class="stat-content">
					<h3>${totalUpcoming}</h3>
					<p>Sự kiện sắp tới</p>
				</div>
			</div>
			<div class="stat-card">
				<div class="stat-icon ongoing">
					<i class="fas fa-calendar-check"></i>
				</div>
				<div class="stat-content">
					<h3>${totalOngoing}</h3>
					<p>Sự kiện đang diễn ra</p>
				</div>
			</div>
			<div class="stat-card">
				<div class="stat-icon completed">
					<i class="fas fa-calendar-times"></i>
				</div>
				<div class="stat-content">
					<h3>${totalPast}</h3>
					<p>Sự kiện đã kết thúc</p>
				</div>
			</div>
			<div class="stat-card">
				<div class="stat-icon total">
					<i class="fas fa-calendar"></i>
				</div>
				<div class="stat-content">
					<h3>${totalEvents}</h3>
					<p>Tổng sự kiện</p>
				</div>
			</div>
		</div>
	</section>
	<section class="events-management">
		<div class="section-header">
			<div class="section-title">
				<h2>Quản lý sự kiện</h2>
				<div class="search-container">
					<input type="text" id="eventSearchInput" placeholder="Tìm kiếm sự kiện..."
					       class="search-input">
					<i class="fas fa-search search-icon"></i>
				</div>
			</div>
			<div class="section-actions">
				<select id="eventStatusFilter" class="filter-select">
					<option value="">Tất cả trạng thái</option>
					<option value="upcoming">Sắp tới</option>
					<option value="ongoing">Đang diễn ra</option>
					<option value="completed">Đã kết thúc</option>
				</select>
				<a href="chairman-page?action=myclub-events&subaction=add" class="btn-add-event">
					<i class="fas fa-plus"></i> Thêm sự kiện
				</a>
			</div>
		</div>

		<div class="events-table-container">
			<table class="events-table">
				<thead>
				<tr>
					<th>Tên sự kiện</th>
					<th>Ngày tổ chức</th>
					<th>Địa điểm</th>
					<th>Số người tham gia</th>
					<th>Trạng thái</th>
					<th>Thao tác</th>
				</tr>
				</thead>
				<tbody id="clubEventsTableBody">
				<c:forEach var="event" items="${myClubEvents}">
					<tr>
						<td>${event.eventName}</td>
						<td>
							<fmt:formatDate value="${event.eventDate}" pattern="dd/MM/yyyy HH:mm" />
						</td>
						<td>${event.location}</td>
						<td>${event.registered}/${event.capacity} (Còn ${event.spotsLeft} chỗ trống)</td>
						<td>
                            <span class="status ${event.status}">
                                <c:choose>
	                                <c:when test="${event.status == 'Pending'}">Sắp diễn ra</c:when>
	                                <c:when test="${event.status == 'Processing'}">Đang diễn ra</c:when>
	                                <c:when test="${event.status == 'Completed'}">Đã kết thúc</c:when>
	                                <c:otherwise>Không xác định</c:otherwise>
                                </c:choose>
                            </span>
						</td>
						<td>
							<a href="chairman-page?action=myclub-events&subaction=edit&eventID=${event.eventID}" class="btn-action edit" title="Sửa">
								<i class="fas fa-edit"></i>
							</a>
							<c:if test="${event.status == 'Pending'}">
								<a href="chairman-page?action=myclub-events&subaction=assign-task&eventID=${event.eventID}" class="btn-action assign" title="Giao việc">
									<i class="fas fa-tasks"></i>
								</a>
							</c:if>
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</div>
	</section>
</div>
</div>
</body>
</html>
