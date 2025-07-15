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
						<a href="${pageContext.request.contextPath}/myclub" class="btn btn-primary">MyClub</a>
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
					     style="width: 72px; height: 72px; border-radius: 50%;">
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
		<c:if test="${not empty sessionScope.successMsg}">
		<div class="error-message" style="color: green">${sessionScope.successMsg}</div>
			<c:remove var="successMsg" scope="session"/>
		</c:if>

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
		<form id="eventForm" action="${pageContext.request.contextPath}/chairman-page/myclub-events/add-event"
		      method="post" enctype="multipart/form-data">
			<div class="form-section">
				<h3>Thông tin cơ bản</h3>
				<div class="form-grid-2">
					<div class="form-group">
						<label for="locationType">Loại địa điểm (Trong hay ngoài trường) *</label>
						<select id="locationType" name="locationType" onchange="this.form.submit()" required>
							<option value="OnCampus" ${param.locationType == 'OnCampus' ? 'selected' : ''}>Trong
								trường
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
				<div class="form-grid">
					<div class="form-group">
						<label for="eventName">Tên sự kiện *</label>
						<input type="text" id="eventName" name="eventName" value="${param.eventName}" required>
					</div>
					<div class="form-group">
						<label>Loại sự kiện *</label>
						<div>
							<input type="radio" id="public" name="eventType" value="public" ${param.eventType == 'public' ? 'checked' : ''}>
							<label for="public" style="margin-right: 20px">Công khai</label>

							<input type="radio" id="private" name="eventType" value="private" ${param.eventType == 'private' ? 'checked' : ''}>
							<label for="private">Riêng tư</label>
						</div>
					</div>

					<!-- THÊM PHẦN UPLOAD ẢNH -->
					<div class="form-group">
						<label for="eventImg">Ảnh sự kiện</label>
						<input type="file" id="eventImg" name="eventImg" accept="image/*" onchange="previewImage(this)">
						<div id="imagePreview" class="image-preview" style="display: none; margin-top: 10px;">
							<img id="previewImg" src="" alt="Preview"
							     style="max-width: 200px; max-height: 200px; border-radius: 8px;">
						</div>
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
						<label for="maxParticipants">Số người tham gia tối đa *</label>
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
		<div class="form-grid-2" id="agendaSection"
		     style="display: ${not empty sessionScope.newEventID ? 'block' : 'none'};">
			<form id="agendaForm" action="${pageContext.request.contextPath}/agenda" method="post">
				<input type="hidden" name="eventID" value="${sessionScope.newEventID}"/>
				<input type="hidden" name="sourcePage" value="add-event"/>
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

		<%--agenda khi tạo xong cùng sự kiện thì đẩy sang cho IC duyệt--%>
		<%--			(trong trang duyệt đó thì sẽ có 2 nút để duyệt, đầu tiên là duyện sự kiện, tiếp theo là duyệt agenda)--%>
</main>
<script>
    window.addEventListener('DOMContentLoaded', function () {
        const eventDateInput = document.getElementById('eventDate');
        const today = new Date();
        today.setDate(today.getDate() + 7);

        const year = today.getFullYear();
        const month = String(today.getMonth() + 1).padStart(2, '0');
        const day = String(today.getDate()).padStart(2, '0');

        const minDate = `${year}-${month}-${day}`;
        eventDateInput.min = minDate;
    });

    document.getElementById("eventForm").addEventListener("submit", function (e) {
        const eventDate = new Date(document.getElementById("eventDate").value);
        const now = new Date();
        now.setHours(0, 0, 0, 0);
        now.setDate(now.getDate() + 7);

        if (eventDate < now) {
            alert("Ngày tổ chức phải sau ngày hiện tại ít nhất 7 ngày.");
            e.preventDefault();
        }

        const startTime = document.getElementById("eventTime").value;
        const endTime = document.getElementById("eventEndTime").value;

        if (!startTime || !endTime) return;

        const [startHour, startMin] = startTime.split(':').map(Number);
        const [endHour, endMin] = endTime.split(':').map(Number);

        const startTotalMin = startHour * 60 + startMin;
        const endTotalMin = endHour * 60 + endMin;

        if (endTotalMin - startTotalMin < 30) {
            alert("Giờ kết thúc phải cách giờ bắt đầu ít nhất 30 phút.");
            e.preventDefault();
            return;
        }
    });

    document.getElementById("agendaForm")?.addEventListener("submit", function (e) {
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

    function previewImage(input) {
        const preview = document.getElementById('imagePreview');
        const previewImg = document.getElementById('previewImg');

        if (input.files && input.files[0]) {
            const reader = new FileReader();

            reader.onload = function(e) {
                previewImg.src = e.target.result;
                preview.style.display = 'block';
            }

            reader.readAsDataURL(input.files[0]);
        } else {
            preview.style.display = 'none';
        }
    }

    function toggleNewLocation() {
        var locationType = document.getElementById('locationType').value;
        var newLocationGroup = document.getElementById('newLocationGroup');
        newLocationGroup.style.display = locationType === 'OffCampus' ? 'block' : 'none';
    }

    function addAgendaItem() {
        const container = document.getElementById('agendaContainer');
        const item = document.createElement('div');
        item.classList.add('agenda-item');
        item.innerHTML = `
            <input type="time" name="agendaStartTime[]" required />
            <input type="time" name="agendaEndTime[]" required />
            <input type="text" name="agendaActivity[]" placeholder="Hoạt động" required />
            <button type="button" class="btn-remove-agenda" onclick="removeAgendaItem(this)">
                <i class="fas fa-times"></i>
            </button>
        `;
        container.appendChild(item);
    }

    function removeAgendaItem(button) {
        button.parentElement.remove();
    }

    window.onload = function () {
        toggleNewLocation();
    }
</script>
</body>
</html>