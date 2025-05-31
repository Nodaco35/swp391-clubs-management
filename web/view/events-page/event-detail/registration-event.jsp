<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
    Document   : registration-event
    Created on : May 31, 2025, 10:04:24 AM
    Author     : LE VAN THUAN
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/registrationEvent.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
	<title>Registration Event</title>
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
				<li>
					<a href="${pageContext.request.contextPath}/"
					   class="${currentPath == '/' ? 'active' : ''}">
						Trang Chủ
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/clubs"
					   class="${fn:contains(currentPath, '/clubs') ? 'active' : ''}">
						Câu Lạc Bộ
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/events-page"
					   class="${fn:contains(currentPath, '/events-page') ? 'active' : ''}">
						Sự Kiện
					</a>
				</li>
			</ul>
		</nav>

		<div class="auth-buttons">
			<c:choose>
				<c:when test="${sessionScope.user != null}">
					<div class="user-menu" id="userMenu">
						<span id="userName">Hi, ${sessionScope.user.fullName}</span>
						<a href="${pageContext.request.contextPath}/profile?action=myProfile" class="btn btn-outline">
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
		<nav class="mobile-nav">
			<ul>
				<li><a href="${pageContext.request.contextPath}/"
				       class="${pageContext.request.servletPath == '/index.jsp' ? 'active' : ''}">Trang Chủ</a></li>
				<li><a href="${pageContext.request.contextPath}/clubs"
				       class="${pageContext.request.servletPath == '/clubs.jsp' ? 'active' : ''}">Câu Lạc Bộ</a></li>
				<li><a href="${pageContext.request.contextPath}/events-page"
				       class="${pageContext.request.servletPath == '/events-page.jsp' ? 'active' : ''}">Sự Kiện</a></li>
			</ul>
		</nav>
	</div>
</header>

<main>
	<!-- Breadcrumb -->
	<section class="breadcrumb-section">
		<div class="container">
			<nav class="breadcrumb">
				<a href="${pageContext.request.contextPath}/events-page" class="breadcrumb-link">
					<i class="fas fa-calendar"></i>
					Danh sách sự kiện
				</a>
				<span class="breadcrumb-separator">/</span>
				<c:set var="e" value="${requestScope.event}"/>
				<a href="${pageContext.request.contextPath}/event-detail?id=${e.eventID}" class="breadcrumb-link">
					<i class="fas fa-info-circle"></i>
					Chi tiết sự kiện
				</a>
				<span class="breadcrumb-separator">/</span>
				<span class="breadcrumb-current">Đăng ký</span>
			</nav>
		</div>
	</section>

	<!-- Registration Form Section -->
	<section class="registration-form-section">
		<div class="container">
			<div class="registration-container">
				<!-- Event Summary -->
				<div class="event-summary">
					<div class="event-summary-header">
						<c:set var="e" value="${requestScope.event}"/>
						<div class="event-icon" id="eventIcon">
							<i class="fas fa-calendar-alt"></i>
						</div>
						<div class="event-info">
							<h2 id="eventTitle">${e.eventName}</h2>
							<div class="event-details">
								<span><i class="fas fa-calendar"></i> <span id="eventDate"><fmt:formatDate
										value="${e.eventDate}" pattern="dd/MM/yyyy"/></span></span>
								<span><i class="fas fa-clock"></i> <span id="eventTime"><fmt:formatDate
										value="${e.eventDate}" pattern="HH:mm"/></span></span>
								<span><i class="fas fa-map-marker-alt"></i> <span
										id="eventLocation">${e.location}</span></span>
							</div>
						</div>
					</div>
					<div class="event-status">
						<div class="status-item">
							<span class="status-label">Đã đăng ký:</span>
							<span class="status-value" id="attendeeCount">${registeredCount}/${e.capacity} người</span>
						</div>
						<div class="status-item">
							<span class="status-label">Còn lại:</span>
							<span class="status-value highlight" id="spotsLeft">${spotsLeft} chỗ</span>
						</div>
					</div>
				</div>

				<!-- Registration Form -->
				<div class="registration-form-container">
					<div class="form-header">
						<h3><i class="fas fa-user-plus"></i> Thông tin đăng ký</h3>
						<p>Vui lòng điền đầy đủ thông tin để hoàn tất đăng ký</p>
					</div>

					<form id="registrationForm" class="registration-form" action="registration-event" method="post">
						<input type="hidden" name="eventID" value="${event.eventID}" />
						<!-- Personal Information -->
						<div class="form-section">
							<h4 class="section-title">
								<i class="fas fa-user"></i>
								Thông tin cá nhân
							</h4>
							<c:set var="userDetails" value="${requestScope.userDetails}"/>
							<div class="form-row">
								<div class="form-group">
									<label for="fullName">Họ và tên <span class="required">*</span></label>
									<input type="text" id="fullName" name="fullName" required
									       placeholder="Nhập họ và tên đầy đủ" value="${userDetails.fullName}">
								</div>
								<div class="form-group">
									<label for="studentId">Mã sinh viên <span class="required">*</span></label>
									<input type="text" id="studentId" name="studentId" required
									       placeholder="Ví dụ: 2021001234" value="${userDetails.userID}" >
								</div>
							</div>

							<div class="form-row">
								<div class="form-group">
									<label for="email">Email <span class="required">*</span></label>
									<input type="email" id="email" name="email" required
									       placeholder="example@student.edu.vn" value="${userDetails.email}">
								</div>
								<div class="form-group">
									<label for="phone">Số điện thoại <span class="required">*</span></label>
									<input type="tel" id="phone" name="phone" required placeholder="0123456789">
								</div>
							</div>
						</div>

						<!-- Terms and Conditions -->
						<div class="form-section">
							<div class="terms-section">
								<label class="checkbox-item terms-checkbox">
									<input type="checkbox" name="agreeTerms" required>
									<span class="checkmark"></span>
									<span class="terms-text">
                                            Tôi đồng ý với <a href="#" class="terms-link">điều khoản và điều kiện</a>
                                            của sự kiện và cam kết tham gia đầy đủ.
                                        </span>
								</label>
							</div>
						</div>
						<c:if test="${not empty message}">
							<div class="form-message ${messageType}">
									${message}
							</div>
						</c:if>

						<!-- Submit Buttons -->
						<div class="form-actions">
							<button type="button" class="btn-secondary">
								<a href="${pageContext.request.contextPath}/event-detail?id=${e.eventID}"><i class="fas fa-arrow-left"></i> Quay lại</a>
							</button>
							<button type="submit" class="btn-primary">
								<i class="fas fa-check"></i>
								Hoàn tất đăng ký
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</section>
</main>

<footer class="footer">
	<div class="container footer-container">
		<div class="footer-logo">
			<i class="fas fa-users"></i>
			<span>UniClub</span>
		</div>
		<p class="copyright">
			© 2023 UniClub. Tất cả các quyền được bảo lưu.
		</p>
		<div class="footer-links">
			<a href="/terms">Điều Khoản</a>
			<a href="/privacy">Chính Sách</a>
			<a href="/contact">Liên Hệ</a>
		</div>
	</div>
</footer>
</body>
</html>
