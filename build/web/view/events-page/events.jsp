<%--
  Created by IntelliJ IDEA.
  User: thuan
  Date: 5/25/2025
  Time: 11:52 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<title>Title</title>
</head>
<body>
<section class="events-section">
	<div class="container">
		<div class="section-header">
			<h2>Sự Kiện Sắp Tới</h2>
			<p>Khám phá các sự kiện thú vị và bổ ích đang chờ đón bạn</p>
		</div>
		<div class="event-filters-wrapper">
			<!-- Event Filter Buttons -->
			<div class="event-filters">
				<button class="filter-btn active" onclick="filterEvents('all')" data-filter="all">
					<i class="fas fa-globe"></i>
					Tất Cả Sự Kiện
				</button>
				<button class="filter-btn" onclick="filterEvents('public')" data-filter="public">
					<i class="fas fa-users"></i>
					Sự Kiện Công Khai
				</button>
				<button class="filter-btn" onclick="filterEvents('private')" data-filter="private">
					<i class="fas fa-lock"></i>
					Sự Kiện Riêng Tư (Club)
				</button>
			</div>
			<div class="event-sort">
				<select id="sortSelect" class="sort-select" onchange="sortEvents(this.value)">
					<option value="newest">Mới Nhất</option>
					<option value="oldest">Cũ Nhất</option>
				</select>
			</div>
		</div>
		<!-- Events Grid -->
		<div class="events-grid" id="eventsGrid">
			<c:forEach var="e" items="${requestScope.events}">
				<div class="event-card ${e.isPublic ? 'public' : 'private'}">
					<h3>${e.eventName}</h3>
					<p><strong>Thời gian:</strong> ${e.eventDate}</p>
					<p><strong>Địa điểm:</strong> ${e.location}</p>
					<p>${e.description}</p>
					<c:if test="${not empty e.urlGGForm}">
						<a href="${e.urlGGForm}" target="_blank">Đăng ký ngay</a>
					</c:if>
					<p><strong>Sức chứa:</strong> ${e.capacity}</p>
					<p><strong>Trạng thái:</strong> ${e.status}</p>
				</div>
			</c:forEach>
		</div>

		<!-- Pagination Controls -->
		<div class="pagination">
			<a href="/events?page=1" class="page-link active">1</a>
			<a href="/events?page=2" class="page-link">2</a>
			<a href="/events?page=3" class="page-link">3</a>
			<a href="/events?page=4" class="page-link">4</a>
			<a href="/events?page=5" class="page-link">5</a>
		</div>

	</div>
</section>
</body>
</html>
