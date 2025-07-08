<%-- 
    Document   : edit-event
    Created on : Jun 16, 2025, 12:20:06 AM
    Author     : LE VAN THUAN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
	<title>Xem Sự Kiện </title>
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
			<h1><i class="fas fa-edit"></i>Xem sự kiện</h1>
			<p>Chỉnh sửa và xem chi tiết sự kiện</p>
		</div>
		<div class="page-actions">
			<a href="${pageContext.request.contextPath}/chairman-page/myclub-events" class="btn-secondary">
				<i class="fas fa-arrow-left"></i> Quay lại
			</a>
		</div>
	</div>
	<div class="modal-content">
		<c:if test="${not empty sessionScope.errorMessage}">
			<div class="error-message" style="color: red">${sessionScope.errorMessage}</div>
			<c:remove var="successMsg" scope="session" />

		</c:if>
		<c:if test="${not empty requestScope.errorMessage}">
			<div class="error-message" style="color: red">${requestScope.errorMessage}</div>

		</c:if>
		<c:if test="${not empty sessionScope.successMsg}">
			<div class="error-message" style="color: green">${sessionScope.successMsg}</div>
			<c:remove var="successMsg" scope="session" />
		</c:if>
		<div class="modal-header">
			<h3 id="modalTitle">Chi tiết sự kiện</h3>
		</div>

		<form method="get" action="edit-event" style="margin-bottom: 1em;">
			<input type="hidden" name="eventID" value="${event.eventID}"/>
			<div class="form-grid-2">
				<div class="form-group">
					<label for="locationType"><i class="fas fa-map-marker-alt"></i> Loại địa điểm *</label>
					<select id="locationType" name="locationType" onchange="this.form.submit()">
						<option value="OnCampus" ${locationType == 'OnCampus' ? 'selected' : ''}>Trong trường</option>
						<option value="OffCampus" ${locationType == 'OffCampus' ? 'selected' : ''}>Ngoài trường</option>
					</select>
				</div>
			</div>
		</form>

		<form id="editEventForm" action="edit-event" method="post" enctype="multipart/form-data">
			<input type="hidden" id="eventID" name="eventID" value="${event.eventID}"/>

			<div class="form-grid">
				<div class="form-group">
					<label for="eventName"><i class="fas fa-calendar-alt"></i> Tên sự kiện *</label>
					<input type="text" id="eventName" name="eventName" required value="${event.eventName}"/>
				</div>

				<div class="form-group">
					<label for="eventDate"><i class="fas fa-calendar-day"></i> Ngày tổ chức *</label>
					<input type="date" id="eventDate" name="eventDate" required
					       value="<fmt:formatDate value='${event.eventDate}' pattern='yyyy-MM-dd' />"/>
				</div>

				<div class="form-group">
					<label for="eventTime"><i class="fas fa-clock"></i> Thời gian bắt đầu *</label>
					<input type="time" id="eventTime" name="eventTime" required
					       value="<fmt:formatDate value='${event.eventDate}' pattern='HH:mm' />"/>
				</div>

				<div class="form-group">
					<label for="eventEndTime"><i class="fas fa-hourglass-end"></i> Thời gian kết thúc *</label>
					<input type="time" id="eventEndTime" name="eventEndTime" required
					       value="<fmt:formatDate value='${event.endTime}' pattern='HH:mm' />"/>
				</div>

				<div class="form-group">
					<label for="maxParticipants"><i class="fas fa-users"></i> Số người tham gia tối đa *</label>
					<input type="number" id="maxParticipants" name="maxParticipants" min="1" required
					       value="${event.capacity}"/>
				</div>

				<div class="form-group">
					<label for="eventType"><i class="fas fa-eye"></i> Loại sự kiện</label>
					<select id="eventType" name="eventType">
						<option value="public" ${event.isPublic() ? 'selected' : ''}>Công khai</option>
						<option value="private" ${!event.isPublic() ? 'selected' : ''}>Riêng tư (CLB)</option>
					</select>
				</div>

				<div class="form-group">
					<label for="eventLocation"><i class="fas fa-building"></i> Địa điểm *</label>
					<select id="eventLocation" name="eventLocation" required>
						<option value="">Chọn địa điểm...</option>
						<c:forEach var="location" items="${locations}">
							<option value="${location.locationID}"
								${event.location != null && event.location.locationID == location.locationID ? 'selected' : ''}>
									${location.locationName}
							</option>
						</c:forEach>
					</select>
				</div>

				<!-- Thêm phần upload ảnh -->
				<div class="form-group full-width">
					<label for="eventImage"><i class="fas fa-image"></i> Ảnh sự kiện</label>

					<!-- Hiển thị ảnh hiện tại nếu có -->
					<c:if test="${not empty event.eventImg}">
						<div class="current-image-container" style="margin-bottom: 10px;">
							<p>Ảnh hiện tại:</p>
							<img src="${pageContext.request.contextPath}/${event.eventImg}"
							     alt="Current event image"
							     style="max-width: 200px; max-height: 200px; object-fit: cover; border: 1px solid #ddd; border-radius: 4px;">
						</div>
					</c:if>

					<!-- Input để chọn ảnh mới -->
					<input type="file" id="eventImage" name="eventImage" accept="image/*">
					<small class="form-text text-muted">
						Chọn ảnh mới nếu muốn thay đổi. Để trống nếu giữ nguyên ảnh hiện tại.
					</small>
				</div>

				<div class="form-group full-width">
					<label for="eventDescription"><i class="fas fa-align-left"></i> Mô tả sự kiện</label>
					<textarea id="eventDescription" name="eventDescription" rows="4">${event.description}</textarea>
				</div>
			</div>

			<div class="form-actions">
				<button type="button" class="btn-cancel"
				        onclick="window.location.href='${pageContext.request.contextPath}/chairman-page/myclub-events'">
					<i class="fas fa-times"></i> Đóng
				</button>
				<button type="submit" class="btn-submit">
					<i class="fas fa-save"></i> Lưu sự kiện
				</button>
			</div>
		</form>

		<div class="form-grid-2">
			<form id="agendaForm" action="${pageContext.request.contextPath}/agenda" method="post">
				<input type="hidden" name="eventID" value="${event.eventID}"/>
				<div class="form-group full-width">
					<label><i class="fas fa-list-alt"></i> Chương trình sự kiện (Agenda)</label>
					<div id="agendaContainer">
						<c:forEach var="agenda" items="${agendas}">
							<div class="agenda-item">
								<input type="time" name="agendaStartTime[]"
								       value="<fmt:formatDate value='${agenda.startTime}' pattern='HH:mm' />"
								       required/>
								<input type="time" name="agendaEndTime[]"
								       value="<fmt:formatDate value='${agenda.endTime}' pattern='HH:mm' />"
								       required/>
								<input type="text" name="agendaActivity[]" value="${agenda.title}"
								       placeholder="Hoạt động" required/>
								<input type="text" name="agendaDescription[]" value="${agenda.description}"
								       placeholder="Mô tả ngắn gọn hoạt động" required/>
								<button type="button" class="btn-remove-agenda" onclick="removeAgendaItem(this)">
									<i class="fas fa-times"></i>
								</button>
							</div>
						</c:forEach>
					</div>
					<button type="button" class="btn-add-agenda" onclick="addAgendaItem()">
						<i class="fas fa-plus"></i> Thêm hoạt động
					</button>

					<div class="form-actions">
						<button type="submit" class="btn-submit">
							<i class="fas fa-save"></i> Lưu chương trình sự kiện
						</button>
					</div>
				</div>
			</form>
		</div>
	</div>

</main>
<script>
    // Gắn kiểm tra cho form chính
    document.getElementById("editEventForm").addEventListener("submit", function (e) {

        const eventDateInput = document.getElementById("eventDate");
        const eventTimeInput = document.getElementById("eventTime");
        const eventEndTimeInput = document.getElementById("eventEndTime");

        if (!eventDateInput.value || !eventTimeInput.value || !eventEndTimeInput.value) {
            return;
        }

        const eventDate = new Date(eventDateInput.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        const minDate = new Date(today);
        minDate.setDate(minDate.getDate() + 7);

        if (eventDate < minDate) {
            alert("Ngày tổ chức phải sau hôm nay ít nhất 7 ngày.");
            e.preventDefault();
            return;
        }

        const [startHour, startMin] = eventTimeInput.value.split(':').map(Number);
        const [endHour, endMin] = eventEndTimeInput.value.split(':').map(Number);

        const startTotalMin = startHour * 60 + startMin;
        const endTotalMin = endHour * 60 + endMin;

        if (endTotalMin - startTotalMin < 30) {
            alert("Giờ kết thúc phải cách giờ bắt đầu ít nhất 30 phút.");
            e.preventDefault();
            return;
        }

    });

    document.getElementById("agendaForm").addEventListener("submit", function (e) {

        const eventTimeInput = document.getElementById("eventTime");
        const eventEndTimeInput = document.getElementById("eventEndTime");

        if (!eventTimeInput.value || !eventEndTimeInput.value) {
            alert("Vui lòng điền giờ bắt đầu và kết thúc của sự kiện trước.");
            e.preventDefault();
            return;
        }

        const [startHour, startMin] = eventTimeInput.value.split(':').map(Number);
        const [endHour, endMin] = eventEndTimeInput.value.split(':').map(Number);

        const startTotalMin = startHour * 60 + startMin;
        const endTotalMin = endHour * 60 + endMin;

        const agendaStartTimes = document.getElementsByName("agendaStartTime[]");
        const agendaEndTimes = document.getElementsByName("agendaEndTime[]");

        for (let i = 0; i < agendaStartTimes.length; i++) {
            const startValue = agendaStartTimes[i].value;
            const endValue = agendaEndTimes[i].value;

            if (!startValue || !endValue) continue;

            const [aStartHour, aStartMin] = startValue.split(':').map(Number);
            const [aEndHour, aEndMin] = endValue.split(':').map(Number);

            const aStartTotalMin = aStartHour * 60 + aStartMin;
            const aEndTotalMin = aEndHour * 60 + aEndMin;


            if (aEndTotalMin <= aStartTotalMin) {
                alert(`Thời gian kết thúc phải sau thời gian bắt đầu.`);
                e.preventDefault();
                return;
            }

            if (aStartTotalMin < startTotalMin || aEndTotalMin > endTotalMin) {
                alert(`Thời gian phải nằm trong khung giờ sự kiện.`);
                e.preventDefault();
                return;
            }
        }

    });
</script>

<script>
    function addAgendaItem() {
        const container = document.getElementById('agendaContainer');
        const item = document.createElement('div');
        item.classList.add('agenda-item');
        item.innerHTML = `
        <input type="time" name="agendaStartTime[]" required />
        <input type="time" name="agendaEndTime[]" required />
        <input type="text" name="agendaActivity[]" placeholder="Hoạt động" required />
		<input type="text" name="agendaDescription[]" placeholder="Mô tả ngắn gọn hoạt động" required/>
        <button type="button" class="btn-remove-agenda" onclick="removeAgendaItem(this)">
            <i class="fas fa-times"></i>
        </button>
    `;
        container.appendChild(item);
    }

    function removeAgendaItem(button) {
        button.parentElement.remove();
    }
</script>

</body>
</html>
