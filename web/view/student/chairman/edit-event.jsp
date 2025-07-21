
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
	<title>Chỉnh Sửa Sự Kiện</title>
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

		<div class="search-container">
			<div class="search-box">
				<form action="${pageContext.request.contextPath}/events-page" method="get">
					<i class="fas fa-search search-icon"></i>
					<input type="text" id="searchInput" name="key" placeholder="Tìm kiếm sự kiện..." class="search-input">
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
                                <li>
				<a href="${pageContext.request.contextPath}/chairman-page/clubmeeting"
				   class="nav-item ${currentPath == '/chairman-page/clubmeeting' ? 'active' : ''}">
					<i class="fas fa-clock"></i> Cuộc họp
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
			<li>
				<a href="${pageContext.request.contextPath}/chairman-page/clubmeeting"
				   class="nav-item ${currentPath == '/chairman-page/clubmeeting' ? 'active' : ''}">
					<i class="fas fa-clock"></i> Cuộc họp
				</a>
			</li>
		</ul>
	</nav>
</header>

<main class="dashboard-content">
	<div class="page-header">
		<div class="page-title">
			<h1><i class="fas fa-edit"></i> Chỉnh sửa sự kiện</h1>
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
			<c:remove var="errorMessage" scope="session"/>
		</c:if>
		<c:if test="${not empty requestScope.errorMessage}">
			<div class="error-message" style="color: red">${requestScope.errorMessage}</div>
		</c:if>
		<c:if test="${not empty sessionScope.successMsg}">
			<div class="error-message" style="color: green">${sessionScope.successMsg}</div>
			<c:remove var="successMsg" scope="session"/>
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
			<div class="form-section">
				<h3>Thông tin cơ bản</h3>
				<div class="form-grid">
					<div class="form-group">
						<label for="eventName"><i class="fas fa-calendar-alt"></i> Tên sự kiện *</label>
						<input type="text" id="eventName" name="eventName" required value="${event.eventName}"/>
					</div>
					<div class="form-group">
						<label><i class="fas fa-eye"></i> Loại sự kiện</label>
						<div>
							<label for="publicEvent" style="margin-right: 20px">
								<input type="radio" id="publicEvent" name="eventType" value="public" ${event.isPublic() ? 'checked' : ''}> Công khai
							</label>
							<label for="privateEvent">
								<input type="radio" id="privateEvent" name="eventType" value="private" ${!event.isPublic() ? 'checked' : ''}> Riêng tư (CLB)
							</label>
						</div>
					</div>
					<div class="form-group">
						<label for="maxParticipants"><i class="fas fa-users"></i> Số người tham gia tối đa *</label>
						<input type="number" id="maxParticipants" name="maxParticipants" min="1" required value="${event.capacity}"/>
					</div>
					<div class="form-group full-width">
						<label for="eventImage"><i class="fas fa-image"></i> Ảnh sự kiện</label>
						<c:if test="${not empty event.eventImg}">
							<div class="current-image-container" style="margin-bottom: 10px;">
								<p>Ảnh hiện tại:</p>
								<img src="${pageContext.request.contextPath}/${event.eventImg}" alt="Current event image"
								     style="max-width: 200px; max-height: 200px; object-fit: cover; border: 1px solid #ddd; border-radius: 4px;">
							</div>
						</c:if>
						<input type="file" id="eventImage" name="eventImage" accept="image/*">
						<small class="form-text text-muted">Chọn ảnh mới nếu muốn thay đổi. Để trống nếu giữ nguyên ảnh hiện tại.</small>
					</div>
					<div class="form-group full-width">
						<label for="eventDescription"><i class="fas fa-align-left"></i> Mô tả sự kiện</label>
						<textarea id="eventDescription" name="eventDescription" rows="4">${event.description}</textarea>
					</div>
				</div>
			</div>

			<div class="form-section">
				<h3>Lịch trình sự kiện</h3>
				<div id="scheduleContainer">
					<c:forEach var="schedule" items="${event.schedules}">
						<div class="schedule-item">
							<div class="form-grid">
								<div class="form-group">
									<%--@declare id="eventdate[]"--%><label for="eventDate[]">Ngày tổ chức *</label>
									<input type="date" name="eventDate[]" required value="<fmt:formatDate value='${schedule.eventDate}' pattern='yyyy-MM-dd' />"/>
								</div>
								<div class="form-group">
									<%--@declare id="eventlocation[]"--%><label for="eventLocation[]">Địa điểm *</label>
									<select name="eventLocation[]" required>
										<option value="">Chọn địa điểm...</option>
										<c:forEach var="location" items="${locations}">
											<option value="${location.locationID}" ${schedule.locationID == location.locationID ? 'selected' : ''}>
													${location.locationName}
											</option>
										</c:forEach>
									</select>
								</div>
								<div class="form-group">
									<%--@declare id="eventtime[]"--%><label for="eventTime[]">Giờ bắt đầu *</label>
									<input type="time" name="eventTime[]" required value="<fmt:formatDate value='${schedule.startTime}' pattern='HH:mm' />"/>
								</div>
								<div class="form-group">
									<%--@declare id="eventendtime[]"--%><label for="eventEndTime[]">Giờ kết thúc *</label>
									<input type="time" name="eventEndTime[]" required value="<fmt:formatDate value='${schedule.endTime}' pattern='HH:mm' />"/>

								</div>
								<button type="button" class="btn-remove-agenda" onclick="removeScheduleItem(this)" style="margin-left: 10px;">
									<i class="fas fa-times"></i>
								</button>
							</div>
						</div>
					</c:forEach>
				</div>
				<button type="button" class="btn-add-agenda" onclick="addScheduleItem()">
					<i class="fas fa-plus"></i> Thêm lịch trình
				</button>
			</div>

			<div class="form-actions">
				<button type="button" class="btn-cancel" onclick="window.location.href='${pageContext.request.contextPath}/chairman-page/myclub-events'">
					<i class="fas fa-times"></i> Đóng
				</button>
				<c:choose>
					<c:when test="${event.approvalStatus == 'APPROVED'}">
						<button type="submit" class="btn-submit" disabled style="background-color: #ccc; cursor: not-allowed;">
							<i class="fas fa-save"></i> Lưu sự kiện
						</button>
					</c:when>
					<c:otherwise>
						<button type="submit" class="btn-submit">
							<i class="fas fa-save"></i> Lưu sự kiện
						</button>
					</c:otherwise>
				</c:choose>
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
								<select name="scheduleID[]" required>
									<option value="">Chọn lịch trình...</option>
									<c:forEach var="schedule" items="${event.schedules}">
										<option value="${schedule.scheduleID}" ${agenda.scheduleID == schedule.scheduleID ? 'selected' : ''}>
											<fmt:formatDate value="${schedule.eventDate}" pattern="yyyy-MM-dd"/> - ${schedule.location.locationName}
										</option>
									</c:forEach>
								</select>
								<input type="time" name="agendaStartTime[]" value="<fmt:formatDate value='${agenda.startTime}' pattern='HH:mm' />" required/>
								<input type="time" name="agendaEndTime[]" value="<fmt:formatDate value='${agenda.endTime}' pattern='HH:mm' />" required/>
								<input type="text" name="agendaActivity[]" value="${agenda.title}" placeholder="Hoạt động" required/>
								<input type="text" name="agendaDescription[]" value="${agenda.description}" placeholder="Mô tả ngắn gọn hoạt động" required/>
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
						<c:choose>
							<c:when test="${event.approvalStatus == 'APPROVED'}">
								<button type="submit" class="btn-submit" disabled style="background-color: #ccc; cursor: not-allowed;">
									<i class="fas fa-save"></i> Lưu chương trình sự kiện
								</button>
							</c:when>
							<c:otherwise>
								<button type="submit" class="btn-submit">
									<i class="fas fa-save"></i> Lưu chương trình sự kiện
								</button>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</form>
		</div>
		<div class="form-grid-2">
			<c:if test="${event.approvalStatus == 'REJECTED' && not empty event.rejectionReason}">
				<div class="form-group full-width">
					<label><i class="fas fa-exclamation-circle"></i> Lý do từ chối</label>
					<div class="error-message-reject" style="color: red">${event.rejectionReason}</div>
				</div>
			</c:if>
		</div>

		<div class="form-grid-2">
			<form id="assignForm" action="${pageContext.request.contextPath}/assign-form" method="post">
				<input type="hidden" name="eventID" value="${event.eventID}"/>
				<div class="form-group">
					<label><i class="fas fa-file-alt"></i> Form đăng ký sự kiện</label>
					<select id="formId" name="formId" class="form-control" required>
						<option value="">-- Chọn form đăng ký sự kiện --</option>
						<c:forEach items="${requestScope.applicationForms}" var="form">
							<option value="${form.formId}" ${event.formID == form.formId ? 'selected' : ''}>
									${form.title}
							</option>
						</c:forEach>
					</select>
				</div>
				<div class="form-actions">
					<c:choose>
						<c:when test="${event.approvalStatus == 'APPROVED'}">
							<button type="submit" class="btn-submit">
								<i class="fas fa-save"></i> Gán form
							</button>
						</c:when>
						<c:otherwise>
							<button type="submit" class="btn-submit" disabled style="background-color: #ccc; cursor: not-allowed;">
								<i class="fas fa-save"></i> Gán form
							</button>
						</c:otherwise>
					</c:choose>
				</div>
			</form>
		</div>
	</div>
</main>
<script>
    const schedules = [
        <c:forEach var="schedule" items="${event.schedules}" varStatus="status">
        {
            scheduleID: "${schedule.scheduleID}",
            startTime: "<fmt:formatDate value='${schedule.startTime}' pattern='HH:mm'/>",
            endTime: "<fmt:formatDate value='${schedule.endTime}' pattern='HH:mm'/>"
        }${status.last ? '' : ','}
        </c:forEach>
    ];

    window.addEventListener('DOMContentLoaded', function () {
        const scheduleContainer = document.getElementById('scheduleContainer');
        const today = new Date();
        today.setDate(today.getDate() + 7);
        const year = today.getFullYear();
        const month = String(today.getMonth() + 1).padStart(2, '0');
        const day = String(today.getDate()).padStart(2, '0');
        const minDate = `${year}-${month}-${day}`;
        scheduleContainer.querySelectorAll('input[type="date"]').forEach(input => input.min = minDate);
    });

    document.getElementById("editEventForm").addEventListener("submit", function (e) {
        const scheduleItems = document.getElementsByClassName('schedule-item');
        for (let i = 0; i < scheduleItems.length; i++) {
            const eventDate = new Date(scheduleItems[i].querySelector('input[name="eventDate[]"]').value);
            const now = new Date();
            now.setHours(0, 0, 0, 0);
            now.setDate(now.getDate() + 7);
            if (eventDate < now) {
                alert(`Ngày tổ chức của lịch trình ${i + 1} phải sau ngày hiện tại ít nhất 7 ngày.`);
                e.preventDefault();
                return;
            }

            const startTime = scheduleItems[i].querySelector('input[name="eventTime[]"]').value;
            const endTime = scheduleItems[i].querySelector('input[name="eventEndTime[]"]').value;
            if (!startTime || !endTime) continue;

            const [startHour, startMin] = startTime.split(':').map(Number);
            const [endHour, endMin] = endTime.split(':').map(Number);
            const startTotalMin = startHour * 60 + startMin;
            const endTotalMin = endHour * 60 + endMin;

            if (endTotalMin - startTotalMin < 30) {
                alert(`Giờ kết thúc của lịch trình ${i + 1} phải cách giờ bắt đầu ít nhất 30 phút.`);
                e.preventDefault();
                return;
            }
        }
    });

    document.getElementById("agendaForm").addEventListener("submit", function (e) {
        const agendaItems = document.getElementsByClassName('agenda-item');
        for (let i = 0; i < agendaItems.length; i++) {
            const scheduleID = agendaItems[i].querySelector('select[name="scheduleID[]"]').value;
            const startTime = agendaItems[i].querySelector('input[name="agendaStartTime[]"]').value;
            const endTime = agendaItems[i].querySelector('input[name="agendaEndTime[]"]').value;

            if (!scheduleID) {
                alert(`Vui lòng chọn lịch trình cho hoạt động ${i + 1}.`);
                e.preventDefault();
                return;
            }

            if (!startTime || !endTime) continue;

            const [startHour, startMin] = startTime.split(':').map(Number);
            const [endHour, endMin] = endTime.split(':').map(Number);
            const startTotalMin = startHour * 60 + startMin;
            const endTotalMin = endHour * 60 + endMin;

            if (endTotalMin <= startTotalMin) {
                alert(`Thời gian kết thúc của hoạt động ${i + 1} phải sau thời gian bắt đầu.`);
                e.preventDefault();
                return;
            }

            // Validate agenda time within schedule time
            const schedule = schedules.find(s => s.scheduleID === scheduleID);
            if (schedule) {
                const [schedStartHour, schedStartMin] = schedule.startTime.split(':').map(Number);
                const [schedEndHour, schedEndMin] = schedule.endTime.split(':').map(Number);
                const schedStartTotalMin = schedStartHour * 60 + schedStartMin;
                const schedEndTotalMin = schedEndHour * 60 + schedEndMin;

                if (startTotalMin < schedStartTotalMin || endTotalMin > schedEndTotalMin) {
                    alert(`Thời gian của hoạt động ${i + 1} phải nằm trong khoảng thời gian của lịch trình (${schedule.startTime} - ${schedule.endTime}).`);
                    e.preventDefault();
                    return;
                }
            } else {
                alert(`Không tìm thấy thông tin lịch trình cho hoạt động ${i + 1}.`);
                e.preventDefault();
                return;
            }
        }
    });

    function addScheduleItem() {
        const container = document.getElementById('scheduleContainer');
        const item = document.createElement('div');
        item.classList.add('schedule-item');
        item.innerHTML = `
            <div class="form-grid">
                <div class="form-group">
                    <label for="eventDate[]">Ngày tổ chức *</label>
                    <input type="date" name="eventDate[]" required>
                </div>
                <div class="form-group">
                    <label for="eventLocation[]">Địa điểm *</label>
                    <select name="eventLocation[]" required>
                        <option value="">Chọn địa điểm...</option>
                        <c:forEach var="location" items="${locations}">
                            <option value="${location.locationID}">${location.locationName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="eventTime[]">Giờ bắt đầu *</label>
                    <input type="time" name="eventTime[]" required>
                </div>
                <div class="form-group">
                    <label for="eventEndTime[]">Giờ kết thúc *</label>
                    <input type="time" name="eventEndTime[]" required>
                    <button type="button" class="btn-remove-schedule" onclick="removeScheduleItem(this)" style="margin-left: 10px;">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            </div>
        `;
        container.appendChild(item);
        const today = new Date();
        today.setDate(today.getDate() + 7);
        const year = today.getFullYear();
        const month = String(today.getMonth() + 1).padStart(2, '0');
        const day = String(today.getDate()).padStart(2, '0');
        item.querySelector('input[type="date"]').min = `${year}-${month}-${day}`;
    }

    function removeScheduleItem(button) {
        const scheduleItems = document.getElementsByClassName('schedule-item');
        if (scheduleItems.length > 1) {
            button.parentElement.parentElement.parentElement.remove();
        } else {
            alert('Phải có ít nhất một lịch trình.');
        }
    }

    function addAgendaItem() {
        const container = document.getElementById('agendaContainer');
        const item = document.createElement('div');
        item.classList.add('agenda-item');
        item.innerHTML = `
            <select name="scheduleID[]" required>
                <option value="">Chọn lịch trình...</option>
                <c:forEach var="schedule" items="${event.schedules}">
                    <option value="${schedule.scheduleID}">
                        <fmt:formatDate value="${schedule.eventDate}" pattern="yyyy-MM-dd"/> - ${schedule.location.locationName}
                    </option>
                </c:forEach>
            </select>
            <input type="time" name="agendaStartTime[]" required />
            <input type="time" name="agendaEndTime[]" required />
            <input type="text" name="agendaActivity[]" placeholder="Hoạt động" required />
            <input type="text" name="agendaDescription[]" placeholder="Mô tả ngắn gọn hoạt động" required />
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
