<%--
  Created by IntelliJ IDEA.
  User: thuan
  Date: 5/25/2025
  Time: 10:50 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
	<title>Events Page</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/eventsPage.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

<header class="header">
	<div class="container header-container">
		<div class="logo">
			<i class="fas fa-users"></i>
			<span>UniClub</span>
		</div>

		<!-- Search Bar -->
		<div class="search-container">
			<div class="search-box">
				<form action="events-page" method="get">
					<i class="fas fa-search search-icon"></i>
					<input type="text" id="searchInput" name="key" placeholder="Tìm kiếm sự kiện..."
					       class="search-input">
					<button type="submit" class="search-btn">
						<i class="fas fa-search"></i>
					</button>
				</form>
			</div>
		</div>

		<nav class="main-nav">
			<ul>
				<li><a href="/">Trang Chủ</a></li>
				<li><a href="/clubs">Câu Lạc Bộ</a></li>
				<li><a href="/events-page" class="active">Sự Kiện</a></li>
			</ul>
		</nav>

		<div class="auth-buttons">
			<c:choose>
				<c:when test="${sessionScope.user != null}">
					<div class="user-menu" id="userMenu">
						<span id="userName">Hi, ${sessionScope.user.fullName}</span>
						<a href="${pageContext.request.contextPath}/profile" class="btn btn-outline">
							<i class="fa-solid fa-user"></i>
						</a>
						<form action="logout" method="post">
							<input class="btn btn-primary" type="submit" value="Logout">
						</form>
					</div>
				</c:when>
				<c:otherwise>
					<div class="guest-menu" id="guestMenu">
						<a href="${pageContext.request.contextPath}/login" class="btn btn-outline">Đăng Nhập</a>
						<a href="${pageContext.request.contextPath}/register" class="btn btn-primary">Đăng Ký</a>
					</div>
				</c:otherwise>
			</c:choose>
			<button class="mobile-menu-btn" onclick="toggleMobileMenu()">
				<i class="fas fa-bars"></i>
			</button>
		</div>
	</div>

	<!-- Mobile Menu -->
	<div class="mobile-menu" id="mobileMenu">
		<!-- <div class="mobile-search">
			<div class="search-box">
				<i class="fas fa-search search-icon"></i>
				<input type="text" placeholder="Tìm kiếm sự kiện..." class="search-input">
				<button class="search-btn">
					<i class="fas fa-search"></i>
				</button>
			</div>
		</div> -->
		<nav class="mobile-nav">
			<a href="/">Trang Chủ</a>
			<a href="/clubs">Câu Lạc Bộ</a>
			<a href="/events-page" class="active">Sự Kiện</a>
		</nav>
		<!-- <div class="mobile-auth">
			<a href="/login" class="btn btn-outline">Đăng Nhập</a>
			<a href="/register" class="btn btn-primary">Đăng Ký</a>
		</div> -->
	</div>
</header>
<main>
	<c:if test="${sessionScope.user == null}">
		<jsp:include page="banner.jsp"/>
	</c:if>
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
						<option value="all">Tất Cả Trạng Thái</option>
						<option value="newest">Mới Nhất</option>
						<option value="oldest">Cũ Nhất</option>
					</select>
					<select id="statusSelect" class="sort-select" onchange="filterByStatus(this.value)">
						<option value="all">Tất Cả Trạng Thái</option>
						<option value="PENDING">Đang Chờ</option>
						<option value="COMPLETED">Hoàn Thành</option>
					</select>
				</div>
			</div>
			<!-- Events Grid -->
			<div class="events-grid" id="eventsGrid">
				<c:forEach var="e" items="${requestScope.events}">
					<div class="event-card" data-filter="${e.isPublic() ? 'public' : 'private'}">
						<div class="event-image">
							<!-- Placeholder icon for event image -->
							<i class="fas fa-calendar-day"></i>
							<span class="event-badge ${e.isPublic() ? 'badge-public' : 'badge-private'}">
									${e.isPublic() ? 'Công khai' : 'Riêng tư'}
							</span>
						</div>
						<div class="event-content">
							<h3 class="event-title">${e.eventName}</h3>
							<p class="event-description">${e.description}</p>
							<div class="event-details">
								<div class="event-detail">
									<i class="fas fa-calendar-alt"></i>
									<span><fmt:formatDate value="${e.eventDate}" pattern="dd/MM/yyyy HH:mm"/></span>
								</div>
								<div class="event-detail">
									<i class="fas fa-map-marker-alt"></i>
									<span>${e.location}</span>
								</div>
								<div class="event-detail">
									<i class="fas fa-users"></i>
									<span>Sức chứa: ${e.capacity}</span>
								</div>
							</div>
							<div class="event-club">
								<strong>Club ID:</strong> ${e.clubID}
							</div>
							<div class="event-footer">
								<span class="attendees">${e.status}</span>
								<c:if test="${not empty e.urlGGForm}">
									<a href="${e.urlGGForm}" class="register-btn" target="_blank">Đăng ký</a>
								</c:if>
								<c:if test="${empty e.urlGGForm}">
									<button class="register-btn" disabled>Đăng ký</button>
								</c:if>
							</div>
						</div>
					</div>
				</c:forEach>
				<c:if test="${empty requestScope.events}">
					<p class="no-events"><i class="fas fa-search search-icon">Không có sự kiện nào để hiển thị.</i></p>
				</c:if>
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
</main>
<jsp:include page="footer.jsp"/>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
