<%--
    Document   : myclub-events
    Created on : Jun 15, 2025, 11:26:48 AM
    Author     : LE VAN THUAN
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
	<title>MyClub Dashboard</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
<jsp:include page="components/sidebar.jsp"/>
<main class="dashboard-content">
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
				</div>
				<div class="section-actions">
					<select id="eventStatusFilter" class="filter-select">
						<option value="">Tất cả trạng thái</option>
						<option value="upcoming">Sắp tới</option>
						<option value="ongoing">Đang diễn ra</option>
						<option value="completed">Đã kết thúc</option>
					</select>
					<a href="${pageContext.request.contextPath}/chairman-page/myclub-events/add-event"
					   class="btn-add-event">
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
						<th>Duyệt</th>
						<th>Thao tác</th>
					</tr>
					</thead>
					<tbody id="clubEventsTableBody">
					<c:forEach var="event" items="${myClubEvents}">
						<c:choose>
							<c:when test="${event.status == 'PENDING' || event.status == 'Pending'}">
								<c:set var="statusKey" value="upcoming"/>
							</c:when>
							<c:when test="${event.status == 'PROCESSING' || event.status == 'Processing'}">
								<c:set var="statusKey" value="ongoing"/>
							</c:when>
							<c:when test="${event.status == 'COMPLETED' || event.status == 'Completed'}">
								<c:set var="statusKey" value="completed"/>
							</c:when>
							<c:otherwise>
								<c:set var="statusKey" value="unknown"/>
							</c:otherwise>
						</c:choose>
						<tr data-status="${statusKey}">
							<td>
								<div class="event-info">
									<strong>${event.eventName}</strong>
									<small>
											${event.isPublic() ? 'Công khai' : 'Riêng tư'}
									</small>
								</div>
							</td>
							<td>
								<c:choose>
									<c:when test="${not empty event.schedules}">
										<fmt:formatDate value="${event.schedules[0].eventDate}" pattern="dd/MM/yyyy"/>
										<c:if test="${event.schedules.size() > 1}">
											- <fmt:formatDate
												value="${event.schedules[event.schedules.size()-1].eventDate}"
												pattern="dd/MM/yyyy"/>
										</c:if>
										<fmt:formatDate value="${event.schedules[0].startTime}" pattern="HH:mm"/>
									</c:when>
									<c:otherwise>
										Chưa có lịch trình
									</c:otherwise>
								</c:choose>
								<!-- Lưu ngày bắt đầu và kết thúc trong thuộc tính data -->
								<c:if test="${not empty event.schedules}">
                                    <span class="event-dates"
                                                        data-start-date="<fmt:formatDate value="${event.schedules[0].eventDate}" pattern="yyyy-MM-dd"/>"
                                          data-end-date="<fmt:formatDate value="${event.schedules[event.schedules.size()-1].eventDate}" pattern="yyyy-MM-dd"/>"></span>
								</c:if>
							</td>
							<td class="location-cell">
								<c:choose>
									<c:when test="${not empty event.schedules}">
										${event.schedules[0].location.locationName}
									</c:when>
									<c:otherwise>
										Không xác định
									</c:otherwise>
								</c:choose>
							</td>
							<td>
								<div class="event-info">
									<strong>${event.registered}/${event.capacity}</strong>
									<small>
										(Còn ${event.spotsLeft} chỗ trống)
									</small>
								</div>
							</td>
							<td>
                                            <span class="status ${event.status}">
                                                <c:choose>
	                                                <c:when test="${event.status == 'PENDING' || event.status == 'Pending'}">Sắp diễn ra</c:when>
	                                                <c:when test="${event.status == 'PROCESSING' || event.status == 'Processing'}">Đang diễn ra</c:when>
	                                                <c:when test="${event.status == 'COMPLETED' || event.status == 'Completed'}">Đã kết thúc</c:when>
	                                                <c:otherwise>Không xác định</c:otherwise>
                                                </c:choose>
                                            </span>
							</td>
							<td>
                                            <span class="status ${event.approvalStatus}">
                                                <c:if test="${event.isPublic() == true}">
	                                                <c:choose>
		                                                <c:when test="${event.approvalStatus == 'PENDING'}">Chờ duyệt</c:when>
		                                                <c:when test="${event.approvalStatus == 'APPROVED'}">Đã duyệt</c:when>
		                                                <c:when test="${event.approvalStatus == 'REJECTED'}">Từ chối</c:when>
		                                                <c:otherwise>Không xác định</c:otherwise>
	                                                </c:choose>
                                                </c:if>
                                            </span>
							</td>
							<td>
								<a href="${pageContext.request.contextPath}/event-detail?id=${event.eventID}"
								   class="btn-action form" title="Xem chi tiết">
									<i class="fa-regular fa-rectangle-list"></i>
								</a>
								<a href="${pageContext.request.contextPath}/chairman-page/myclub-events/edit-event?eventID=${event.eventID}"
								   class="btn-action edit" title="Sửa">
									<i class="fas fa-edit"></i>
								</a>
								<c:if test="${(event.status == 'PENDING' || event.status == 'Pending' ) && event.approvalStatus == 'APPROVED'}">
									<a href="#" class="btn-action assign" data-event-id="${event.eventID}"
									   title="Giao việc">
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

	<div class="modal-term" id="eventTermModal">
		<div class="modal-content-term">
			<span class="close-term">×</span>
			<h2>Thêm Giai Đoạn Sự Kiện</h2>
			<c:if test="${not empty termError}">
				<div class="error-message-term" style="color: red; margin-bottom: 10px;">
						${termError}
				</div>
			</c:if>

			<form action="${pageContext.request.contextPath}/chairman-page/myclub-events" method="post" id="addEventTermForm">
				<input type="hidden" name="action" value="addTerm">
				<input type="hidden" name="eventID" id="eventID">
				<input type="hidden" name="eventStartDate" id="eventStartDate">
				<input type="hidden" name="eventEndDate" id="eventEndDate">
				<div class="form-group-term">
					<label for="termName">Tên Giai Đoạn:</label>
					<select name="termName" id="termName" required>
						<option value="Trước sự kiện">Trước sự kiện</option>
						<option value="Trong sự kiện">Trong sự kiện</option>
						<option value="Sau sự kiện">Sau sự kiện</option>
					</select>
				</div>
				<div class="form-group-term">
					<label for="termStart">Ngày Bắt Đầu:</label>
					<input type="date" name="termStart" id="termStart" required>
				</div>
				<div class="form-group-term">
					<label for="termEnd">Ngày Kết Thúc:</label>
					<input type="date" name="termEnd" id="termEnd" required>
				</div>
				<div id="error-message" style="color: red; margin-bottom: 10px; display: none;"></div>
				<button type="submit" class="btn btn-primary">Thêm Giai Đoạn</button>
			</form>
		</div>
	</div>
</main>

<script>
    window.addEventListener("DOMContentLoaded", function () {
        document.getElementById("eventStatusFilter").value = "upcoming";
        document.getElementById("eventStatusFilter").dispatchEvent(new Event("change"));
    });

    document.getElementById("eventStatusFilter").addEventListener("change", function () {
        const selectedStatus = this.value;
        const rows = document.querySelectorAll("#clubEventsTableBody tr");

        rows.forEach(row => {
            const status = row.getAttribute("data-status").toLowerCase();
            if (selectedStatus === "" || status.includes(selectedStatus.toLowerCase())) {
                row.style.display = "";
            } else {
                row.style.display = "none";
            }
        });
    });

    // Hàm để gán eventID, eventStartDate, và eventEndDate vào form
    function setModalData(eventID, row) {
        const modal = document.getElementById("eventTermModal");
        const eventIDInput = document.getElementById("eventID");
        const eventStartDateInput = document.getElementById("eventStartDate");
        const eventEndDateInput = document.getElementById("eventEndDate");

        if (!eventID || eventID.trim() === "") {
            alert("Không thể mở modal: ID sự kiện không hợp lệ.");
            return false;
        }

        const dateSpan = row.querySelector(".event-dates");
        if (!dateSpan) {
            alert("Không thể thêm giai đoạn: Sự kiện chưa có lịch trình.");
            return false;
        }

        const startDate = dateSpan.getAttribute("data-start-date");
        const endDate = dateSpan.getAttribute("data-end-date");
        if (!startDate) {
            alert("Không thể lấy ngày bắt đầu sự kiện. Vui lòng kiểm tra dữ liệu.");
            return false;
        }

        // Reset form và gán lại các giá trị cần thiết
        document.getElementById("addEventTermForm").reset();
        eventIDInput.value = eventID;
        eventStartDateInput.value = startDate;
        eventEndDateInput.value = endDate || startDate; // Nếu chỉ có 1 ngày, dùng startDate
        modal.style.display = "flex";
        return true;
    }

    document.querySelectorAll(".btn-action.assign").forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault();
            const eventID = this.getAttribute("data-event-id");
            const row = this.closest("tr");
            setModalData(eventID, row);
        });
    });

    document.querySelector(".close-term").addEventListener("click", function () {
        document.getElementById("eventTermModal").style.display = "none";
        // Reset form khi đóng modal
        document.getElementById("addEventTermForm").reset();
    });

    window.addEventListener("click", function (event) {
        const modal = document.getElementById("eventTermModal");
        if (event.target === modal) {
            modal.style.display = "none";
            // Reset form khi đóng modal
            document.getElementById("addEventTermForm").reset();
        }
    });

    document.getElementById("addEventTermForm").addEventListener("submit", function (e) {
        e.preventDefault(); // Ngăn gửi form ngay lập tức
        const termName = document.getElementById("termName").value;
        const termStart = new Date(document.getElementById("termStart").value);
        const termEnd = new Date(document.getElementById("termEnd").value);
        const eventStartDate = new Date(document.getElementById("eventStartDate").value);
        const eventEndDate = new Date(document.getElementById("eventEndDate").value);
        const eventID = document.getElementById("eventID").value;
        const errorMessage = document.getElementById("error-message");

        // Kiểm tra cơ bản
        if (!eventID || eventID.trim() === "") {
            errorMessage.textContent = "ID sự kiện không hợp lệ.";
            errorMessage.style.display = "block";
            return;
        }
        if (!termName || !document.getElementById("termStart").value || !document.getElementById("termEnd").value) {
            errorMessage.textContent = "Vui lòng điền đầy đủ thông tin.";
            errorMessage.style.display = "block";
            return;
        }
        if (termEnd < termStart) {
            errorMessage.textContent = "Ngày kết thúc không được sớm hơn ngày bắt đầu.";
            errorMessage.style.display = "block";
            return;
        }

        // Kiểm tra theo loại giai đoạn
        if (termName === "Trước sự kiện") {
            if (termEnd >= eventStartDate) {
                errorMessage.textContent = "Giai đoạn 'Trước sự kiện' phải kết thúc trước ngày bắt đầu sự kiện.";
                errorMessage.style.display = "block";
                return;
            }
        } else if (termName === "Trong sự kiện") {
            if (termStart < eventStartDate || termEnd > eventEndDate) {
                errorMessage.textContent = "Giai đoạn 'Trong sự kiện' phải nằm trong khoảng thời gian diễn ra sự kiện.";
                errorMessage.style.display = "block";
                return;
            }
        } else if (termName === "Sau sự kiện") {
            if (termStart <= eventEndDate) {
                errorMessage.textContent = "Giai đoạn 'Sau sự kiện' phải bắt đầu sau ngày kết thúc sự kiện.";
                errorMessage.style.display = "block";
                return;
            }
        }

        // Nếu không có lỗi, gửi form
        errorMessage.style.display = "none";
        this.submit();
    });

    <% if (request.getAttribute("showTermModal") != null) { %>
    window.addEventListener("load", function () {
        // Lấy eventID từ form hoặc từ server nếu có
        const eventIDInput = document.getElementById("eventID");
        const eventID = eventIDInput.value;
        if (eventID && eventID.trim() !== "") {
            // Tìm hàng trong bảng tương ứng với eventID
            const row = document.querySelector(`tr[data-status] a[data-event-id="${eventID}"]`)?.closest("tr");
            if (row) {
                if (setModalData(eventID, row)) {
                    document.getElementById("eventTermModal").style.display = "flex";
                }
            } else {
                alert("Không tìm thấy sự kiện tương ứng. Vui lòng thử lại.");
            }
        } else {
            alert("ID sự kiện không hợp lệ. Vui lòng thử lại.");
        }
    });
    <% } %>
</script>
</body>
</html>
