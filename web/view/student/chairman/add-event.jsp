<%--
    Document   : add-event
    Created on : Jun 16, 2025, 12:19:52 AM
    Author     : LE VAN THUAN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
	<title>Thêm Sự Kiện Mới</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">
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
				<form action="${pageContext.request.contextPath}/events-page" method="get">
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
					   class="${pageContext.request.servletPath == '/' ? 'active' : ''}">
						Trang Chủ
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/clubs"
					   class="${fn:contains(pageContext.request.servletPath, '/clubs') ? 'active' : ''}">
						Câu Lạc Bộ
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/events-page"
					   class="${fn:contains(pageContext.request.servletPath, '/events-page') ? 'active' : ''}">
						Sự Kiện
					</a>
				</li>
			</ul>
		</nav>

		<div class="auth-buttons">
			<c:choose>
				<c:when test="${sessionScope.user != null}">
					<div class="user-menu" id="userMenu">
						<a href="${pageContext.request.contextPath}/notification" class="btn btn-outline">
							<i class="fa-solid fa-bell"></i>
						</a>
						<a href="${pageContext.request.contextPath}/profile?action=myProfile" class="btn btn-outline">
							<i class="fa-solid fa-user"></i>
						</a>
						<form action="${pageContext.request.contextPath}/logout" method="post">
							<input class="btn btn-primary" type="submit" value="Logout">
						</form>
						<a href="${pageContext.request.contextPath}/my-club" class="btn btn-primary">MyClub</a>
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
				       class="${pageContext.request.servletPath == '/' ? 'active' : ''}">Trang Chủ</a></li>
				<li><a href="${pageContext.request.contextPath}/clubs"
				       class="${pageContext.request.servletPath == '/clubs' ? 'active' : ''}">Câu Lạc Bộ</a></li>
				<li><a href="${pageContext.request.contextPath}/events-page"
				       class="${pageContext.request.servletPath == '/events-page' ? 'active' : ''}">Sự Kiện</a></li>
			</ul>
		</nav>
	</div>
	<c:if test="${not empty club}">
		<div class="club-header">
			<div class="club-info">
				<div class="club-avatar">
					<img src="${pageContext.request.contextPath}${club.clubImg}" alt="${club.clubName}"
					     style="width: 60px; height: 60px; border-radius: 50%;">
				</div>
				<div class="club-details">
					<h1>${club.clubName}</h1>
					<p>Chủ nhiệm: ${club.clubChairmanName}</p>
				</div>
			</div>
		</div>
	</c:if>

	<nav class="dashboard-nav">
		<ul>
			<li>
				<a href="${pageContext.request.contextPath}/chairman-page/overview"
				   class="nav-item ${currentPath == '/chairman-page/overview' ? 'active' : ''}">
					<i class="fas fa-tachometer-alt"></i> Tổng quan
				</a>
			</li>
			<li>
				<a href="${pageContext.request.contextPath}/chairman-page/myclub-events"
				   class="nav-item ${currentPath == '/chairman-page/myclub-events' ? 'active' : ''}">
					<i class="fas fa-calendar-alt"></i> Sự kiện
				</a>
			</li>
			<li>
				<a href="${pageContext.request.contextPath}/chairman-page/tasks"
				   class="nav-item ${currentPath == '/chairman-page/tasks' ? 'active' : ''}">
					<i class="fas fa-clock"></i> Timeline & Công việc
				</a>
			</li>
		</ul>
	</nav>

</header>

<main class="dashboard-content">
	<div class="page-header">
		<div class="page-title">
			<h1><i class="fas fa-plus"></i> Thêm sự kiện mới</h1>
			<p>Tạo sự kiện mới</p>
		</div>
		<div class="page-actions">
			<a href="${pageContext.request.contextPath}/chairman-page/myclub-events" class="btn-secondary">
				<i class="fas fa-arrow-left"></i> Quay lại
			</a>
		</div>
	</div>
	<div class="form-container">
		<c:if test="${not empty errorMessage}">
		<div class="error-message" style="color: red">${errorMessage}</div>
		</c:if>

		<form id="eventForm" action="${pageContext.request.contextPath}/chairman-page/myclub-events/add-event"
		      method="post">
			<div class="form-section">
				<h3>Thông tin cơ bản</h3>
				<div class="form-grid">
					<div class="form-group">
						<label for="eventName">Tên sự kiện *</label>
						<input type="text" id="eventName" name="eventName" value="${param.eventName}" required>
					</div>
					<div class="form-group">
						<label for="eventType">Loại sự kiện</label>
						<select id="eventType" name="eventType">
							<option value="public" ${param.eventType == 'public' ? 'selected' : ''}>Công khai</option>
							<option value="private" ${param.eventType == 'private' ? 'selected' : ''}>Riêng tư</option>
						</select>
					</div>

					<div class="form-group full-width">
						<label for="eventDescription">Mô tả</label>
						<textarea id="eventDescription" name="eventDescription"
						          rows="4">${param.eventDescription}</textarea>
					</div>
				</div>
			</div>

			<div class="form-section">
				<h3>Thời gian và địa điểm</h3>
				<div class="form-grid">
					<div class="form-group">
						<label for="eventDate">Ngày tổ chức *</label>
						<input type="date" id="eventDate" name="eventDate" value="${param.eventDate}" required>
					</div>
					<div class="form-group">
						<label for="maxParticipants">Số lượng tối đa *</label>
						<input type="number" id="maxParticipants" name="maxParticipants"
						       value="${param.maxParticipants}" required>
					</div>
				</div>
				<div class="form-grid">
					<div class="form-group">
						<label for="eventTime">Giờ bắt đầu *</label>
						<input type="time" id="eventTime" name="eventTime" value="${param.eventTime}" required>
					</div>
					<div class="form-group">
						<label for="eventEndTime">Giờ kết thúc *</label>
						<input type="time" id="eventEndTime" name="eventEndTime" value="${param.eventEndTime}" required>
					</div>
				</div>
				<div class="form-grid-2">
					<div class="form-group">
						<label for="locationType">Loại địa điểm (Trong hay ngoài trường) *</label>
						<select id="locationType" name="locationType" onchange="this.form.submit()" required>
							<option value="OnCampus" ${param.locationType == 'OnCampus' ? 'selected' : ''}>Trong trường
							</option>
							<option value="OffCampus" ${param.locationType == 'OffCampus' ? 'selected' : ''}>Ngoài
								trường
							</option>
						</select>
					</div>
				</div>
				<div class="form-grid-2">
					<div class="form-group">
						<label for="eventLocation">Địa điểm *</label>
						<select id="eventLocation" name="eventLocation" required>
							<option value="">Chọn địa điểm...</option>
							<c:forEach var="location" items="${locations}">
								<option value="${location.locationID}" ${param.eventLocation == location.locationID ? 'selected' : ''}>
										${location.locationName}
								</option>
							</c:forEach>
						</select>
					</div>
				</div>
			</div>

			<div class="form-actions">
				<button type="button" class="btn-cancel"
				        onclick="window.location.href='${pageContext.request.contextPath}/chairman-page/myclub-events'">
					<i class="fas fa-times"></i> Hủy
				</button>
				<button type="submit" class="btn-submit">
					<i class="fas fa-save"></i> Tạo sự kiện
				</button>
			</div>
		</form>

		<div class="form-grid-2">
			<div id="newLocationGroup"
			     style="display: ${param.locationType == 'OffCampus' ? 'block' : 'none'}; margin-top: 20px;">
				<form action="${pageContext.request.contextPath}/add-location" method="post">
					<div class="form-group">
						<label for="newLocationName">Địa điểm mới</label>
						<input type="text" id="newLocationName" name="newLocationName"
						       placeholder="Nhập địa điểm ngoài trường..." value="${param.newLocationName}">
					</div>
					<div class="form-actions">
						<button type="submit" class="btn-submit">
							<i class="fas fa-plus"></i> Thêm địa điểm
						</button>
					</div>
				</form>
			</div>
		</div>
</main>
<script>
    function toggleNewLocation() {
        var locationType = document.getElementById('locationType').value;
        var newLocationGroup = document.getElementById('newLocationGroup');
        newLocationGroup.style.display = locationType === 'OffCampus' ? 'block' : 'none';
    }

    window.onload = function () {
        toggleNewLocation();
    }

    // // Gửi form eventForm khi locationType thay đổi
    // function submitLocationType() {
    //     var form = document.getElementById('eventForm');
    //     // Tạm thời bỏ required để gửi locationType
    //     form.querySelectorAll('input[required], select[required]').forEach(function (element) {
    //         if (element.id !== 'locationType') {
    //             element.removeAttribute('required');
    //         }
    //     });
    //     form.submit();
    //     // Khôi phục required
    //     setTimeout(function () {
    //         form.querySelectorAll('input:not([id="locationType"]), select:not([id="locationType"])').forEach(function (element) {
    //             if (element.dataset.required === 'true') {
    //                 element.setAttribute('required', '');
    //             }
    //         });
    //     }, 0);
    // }
    //
    // // Khởi tạo trạng thái khi tải trang
    // window.onload = function () {
    //     toggleNewLocation();
    //     // Lưu trạng thái required ban đầu
    //     document.querySelectorAll('input[required], select[required]').forEach(function (element) {
    //         element.dataset.required = 'true';
    //     });
    // };
</script>
</body>
</html>