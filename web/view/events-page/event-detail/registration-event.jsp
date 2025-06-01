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
					<button type="reset" class="search-btn">
						<i class="fas fa-search"></i>
					</button>
				</form>
			</div>
		</div>

		<nav class="main-nav">
			<ul>
				<li>
					<a href="${pageContext.request.contextPath}/">Trang Chủ</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/clubs">Câu Lạc Bộ</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/events-page">Sự Kiện</a>
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
					<c:if test="${not empty message}">
						<div class="form-message ${messageType}">
								${message}
						</div>
					</c:if>
					<form id="registrationForm" class="registration-form" action="registration-event" method="post">
						<input type="hidden" name="eventID" value="${event.eventID}"/>
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
									       placeholder="Nhập họ và tên đầy đủ" value="${userDetails.fullName}" readonly>
								</div>
								<div class="form-group">
									<label for="studentId">Mã sinh viên <span class="required">*</span></label>
									<input type="text" id="studentId" name="studentId" required
									       placeholder="Ví dụ: 2021001234" value="${userDetails.userID}" readonly>
								</div>
							</div>

							<div class="form-row">
								<div class="form-group">
									<label for="email">Email <span class="required">*</span></label>
									<input type="email" id="email" name="email" required
									       placeholder="example@student.edu.vn" value="${userDetails.email}" readonly>
								</div>
								<%--								<div class="form-group">--%>
								<%--									<label for="phone">Số điện thoại <span class="required">*</span></label>--%>
								<%--									<input type="tel" id="phone" name="phone" required placeholder="0123456789">--%>
								<%--								</div>--%>
							</div>
						</div>

						<div class="form-section">
							<h4 class="section-title">
								<i class="fas fa-info-circle"></i>
								Một số câu hỏi
							</h4>
							<div class="form-group">
								<label for="faculty">Bạn biết đến sự kiện này qua đâu <span
										class="required">*</span></label>
								<select id="faculty" name="faculty" required>
									<option value="mxh">Mạng xã hội</option>
									<option value="fr">Bạn bè</option>
									<option value="web">Truyền thông</option>
									<option value="other">Khác</option>
								</select>
							</div>
							<div class="form-group">
								<label for="reasonJoin">Lý do bạn muốn tham gia sự kiện này là gì?</label>
								<textarea id="reasonJoin" name="reasonJoin" rows="3"
								          placeholder="Chia sẻ lý do bạn quan tâm đến sự kiện này..."></textarea>
							</div>

							<div class="form-group">
								<label for="experienceLevel">Bạn đã có kinh nghiệm gì liên quan đến chủ đề sự kiện
									chưa?</label>
								<select id="experienceLevel" name="experienceLevel">
									<option value="none">Chưa có</option>
									<option value="basic">Có biết sơ qua</option>
									<option value="intermediate">Có một chút kinh nghiệm</option>
									<option value="advanced">Đã có nhiều kinh nghiệm</option>
								</select>
							</div>

							<div class="form-group">
								<label for="suggestions">Bạn có đề xuất hoặc mong muốn nào cho các sự kiện trong tương
									lai không?</label>
								<textarea id="suggestions" name="suggestions" rows="3"
								          placeholder="Ý tưởng, chủ đề, diễn giả bạn muốn..."></textarea>
							</div>

							<div class="form-group">
								<label for="expectations">Bạn mong muốn học được gì từ sự kiện này?</label>
								<textarea id="expectations" name="expectations" rows="4"
								          placeholder="Chia sẻ mong muốn và mục tiêu của bạn..."></textarea>
							</div>
						</div>

						<!-- Terms and Conditions -->
						<div class="form-section">
							<div class="terms-section">
								<label class="checkbox-item terms-checkbox">
									<input type="checkbox" name="agreeTerms" required>
									<span class="checkmark"></span>
									<span class="terms-text">Tôi đồng ý với <a href="#" class="terms-link" onclick="openTermsModal(event)">điều khoản và điều kiện</a> của sự kiện và cam kết tham gia đầy đủ.</span>
								</label>
							</div>
						</div>

						<!-- Modal for Terms -->
						<div id="termsModal" class="modal">
							<div class="modal-content">
								<span class="close" onclick="closeTermsModal()">&times;</span>
								<h3>Điều khoản và Điều kiện</h3>
								<p>
									Khi tham gia sự kiện, bạn đồng ý tuân thủ nội quy và cam kết tham gia đầy đủ, đúng
									giờ...
								</p>
								<ul>
									<li>Tham gia đầy đủ các buổi hoạt động và không bỏ lỡ lịch trình đã đăng ký.</li>
									<li>Tuân thủ quy định về trang phục và an toàn trong suốt thời gian diễn ra sự kiện.</li>
									<li>Tôn trọng các thành viên khác và ban tổ chức, không gây mất trật tự.</li>
									<li>Không được phép mang theo các vật dụng gây nguy hiểm hoặc cấm tại địa điểm tổ chức.</li>
									<li>Ban tổ chức có quyền từ chối hoặc loại bỏ người tham gia nếu vi phạm các quy định.</li>
									<li>Mọi thay đổi hoặc hủy bỏ sự kiện sẽ được thông báo trước qua email hoặc thông báo chính thức.</li>
									<li>Bạn cam kết tham gia đầy đủ và chịu trách nhiệm về việc đăng ký của mình.</li>
								</ul>
								<p>
									Nếu có bất kỳ câu hỏi hoặc yêu cầu hỗ trợ, vui lòng liên hệ với ban tổ chức qua email hoặc số điện thoại được cung cấp.
								</p>
								<label style="display: flex; align-items: center; margin-top: 15px; font-size: 0.95rem; cursor: pointer;">
									<input type="checkbox" id="confirmRead" style="margin-right: 8px;">
									Tôi đã đọc và đồng ý với điều khoản và điều kiện
								</label>
							</div>
						</div>

						<!-- Submit Buttons -->
						<div class="form-actions">
							<button type="button" class="btn-secondary">
								<a href="${pageContext.request.contextPath}/event-detail?id=${e.eventID}"><i
										class="fas fa-arrow-left"></i> Quay lại</a>
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
<script>
    function openTermsModal(event) {
        event.preventDefault();
        document.getElementById("termsModal").style.display = "block";
    }

    function closeTermsModal() {
        document.getElementById("termsModal").style.display = "none";
    }

    window.onclick = function(event) {
        const modal = document.getElementById("termsModal");
        if (event.target === modal) {
            modal.style.display = "none";
        }
    }
</script>
</body>
</html>
