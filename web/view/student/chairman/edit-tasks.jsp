<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
	<title>MyClub Dashboard</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
	<style>
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        .page-title h1 {
            margin: 0;
            font-size: 24px;
            display: flex;
            align-items: center;
        }
        .page-title p {
            margin: 5px 0 0;
            color: #666;
        }
        .page-actions .btn-secondary {
            background-color: #6c757d;
            color: white;
            padding: 10px 20px;
            border-radius: 4px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
        }
        .page-actions .btn-secondary:hover {
            background-color: #5a6268;
        }
        .modal-content {
            background-color: #fff;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
        }
        .modal-header h3 {
            font-size: 20px;
        }
        .form-section {
            margin-bottom: 20px;
        }
        .form-section h3 {
            margin: 0 0 15px;
            font-size: 18px;
        }
        .form-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group.full-width {
            grid-column: 1 / -1;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        .form-group input, .form-group select, .form-group textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .form-group span {
            display: block;
            padding: 8px;
        }
        .form-actions {
            display: flex;
            gap: 10px;
            margin-top: 20px;
        }
        .btn-submit {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            display: flex;
            align-items: center;
        }
        .btn-submit:hover {
            background-color: #0056b3;
        }
        .btn-cancel {
            background-color: #6c757d;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            display: flex;
            align-items: center;
        }
        .btn-cancel:hover {
            background-color: #5a6268;
        }
        .error-message {
            margin-bottom: 15px;
        }
        #newDocumentFields {
            display: none;
        }
	</style>
</head>
<body>
<jsp:include page="components/sidebar.jsp" />
<main class="dashboard-content">
	<div class="page-header">
		<div class="page-title">
			<h1><i class="fas fa-edit"></i> Chỉnh sửa công việc</h1>
			<p>Chỉnh sửa và xem chi tiết công việc</p>
		</div>
		<div class="page-actions">
			<a href="${pageContext.request.contextPath}/chairman-page/tasks?eventID=${task.event.eventID}" class="btn-secondary">
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
			<div class="form-actions">
				<button type="button" class="btn-submit" id="editTaskButton">
					<h3 id="modalTitle"><i class="fas fa-edit"></i> Chỉnh sửa công việc</h3>
				</button>
			</div>
		</div>

		<div id="taskDetailView" class="form-section">
			<h3>Thông tin công việc</h3>
			<div class="form-grid">
				<div class="form-group">
					<label><i class="fas fa-tasks"></i> Tiêu đề:</label>
					<span>${task.title}</span>
				</div>
				<div class="form-group">
					<label><i class="fas fa-align-left"></i> Mô tả:</label>
					<span>${task.description}</span>
				</div>
				<div class="form-group">
					<label><i class="fas fa-users"></i> Phòng ban:</label>
					<span>${task.departmentAssignee.departmentName}</span>
				</div>
				<div class="form-group">
					<label><i class="fas fa-file-alt"></i> Tài liệu:</label>
					<span>
                        <c:choose>
	                        <c:when test="${task.document != null}">
		                        <a href="${task.document.documentURL}" target="_blank">${task.document.documentName}</a>
	                        </c:when>
	                        <c:otherwise>Không có tài liệu</c:otherwise>
                        </c:choose>
                    </span>
				</div>
				<div class="form-group">
					<label><i class="fas fa-calendar-alt"></i> Ngày bắt đầu:</label>
					<span><fmt:formatDate value="${task.startDate}" pattern="dd/MM/yyyy"/></span>
				</div>
				<div class="form-group">
					<label><i class="fas fa-calendar-alt"></i> Ngày kết thúc:</label>
					<span><fmt:formatDate value="${task.endDate}" pattern="dd/MM/yyyy"/></span>
				</div>
				<div class="form-group">
					<label><i class="fas fa-info-circle"></i> Trạng thái:</label>
					<span>
                        <c:choose>
	                        <c:when test="${task.status == 'ToDo'}">Chưa bắt đầu</c:when>
	                        <c:when test="${task.status == 'InProgress'}">Đang thực hiện</c:when>
	                        <c:when test="${task.status == 'Review'}">Chờ duyệt</c:when>
	                        <c:when test="${task.status == 'Rejected'}">Bị từ chối</c:when>
	                        <c:when test="${task.status == 'Done'}">Hoàn thành</c:when>
	                        <c:otherwise>Không xác định</c:otherwise>
                        </c:choose>
                    </span>
				</div>
			</div>

		</div>

		<div id="taskEditForm" class="form-section" style="display: none;">
			<h3>Chỉnh sửa công việc</h3>
			<form id="editTaskForm" action="${pageContext.request.contextPath}/chairman-page/tasks/edit-tasks" method="post">
				<input type="hidden" name="action" value="updateTask">
				<input type="hidden" name="taskID" value="${task.taskID}">
				<input type="hidden" name="termID" value="${task.term.termID}">
				<input type="hidden" name="eventID" value="${task.event.eventID}">
				<input type="hidden" name="clubID" value="${club.clubID}">
				<input type="hidden" name="assigneeType" value="Department">
				<input type="hidden" name="createdBy" value="${sessionScope.user.userID}">
				<div class="form-grid">
					<div class="form-group">
						<label for="title"><i class="fas fa-tasks"></i> Tiêu đề công việc *</label>
						<input type="text" name="title" id="title" value="${task.title}" required maxlength="100">
					</div>
					<div class="form-group full-width">
						<label for="description"><i class="fas fa-align-left"></i> Mô tả</label>
						<textarea name="description" id="description" rows="4">${task.description}</textarea>
					</div>
					<div class="form-group">
						<label for="departmentID"><i class="fas fa-users"></i> Phòng ban *</label>
						<select name="departmentID" id="departmentID" required>
							<option value="">Chọn phòng ban</option>
							<c:forEach var="dept" items="${departmentList}">
								<option value="${dept.departmentID}" ${dept.departmentID == task.departmentAssignee.departmentID ? 'selected' : ''}>
										${dept.departmentName}
								</option>
							</c:forEach>
						</select>
					</div>
					<div class="form-group">
						<label for="documentSelect"><i class="fas fa-file-alt"></i> Tài liệu</label>
						<select name="existingDocumentID" id="documentSelect" onchange="toggleDocumentFields()">
							<option value="">Không có tài liệu</option>
							<option value="new">Tạo tài liệu mới</option>
							<c:forEach var="doc" items="${documentsList}">
								<option value="${doc.documentID}" ${doc.documentID == task.document.documentID ? 'selected' : ''}>
										${doc.documentName} (${doc.department.departmentName})
								</option>
							</c:forEach>
						</select>
					</div>
					<div id="newDocumentFields" class="form-group full-width" style="display: none;">
						<div class="form-group">
							<label for="documentName"><i class="fas fa-file"></i> Tên tài liệu</label>
							<input type="text" name="documentName" id="documentName" maxlength="100">
						</div>
						<div class="form-group">
							<label for="documentURL"><i class="fas fa-link"></i> Liên kết tài liệu</label>
							<input type="url" name="documentURL" id="documentURL" maxlength="255">
						</div>
					</div>
					<div class="form-group">
						<label for="startDate"><i class="fas fa-calendar-alt"></i> Ngày bắt đầu *</label>
						<input type="date" name="startDate" id="startDate" value="<fmt:formatDate value='${task.startDate}' pattern='yyyy-MM-dd' />" required>
					</div>
					<div class="form-group">
						<label for="endDate"><i class="fas fa-calendar-alt"></i> Ngày kết thúc *</label>
						<input type="date" name="endDate" id="endDate" value="<fmt:formatDate value='${task.endDate}' pattern='yyyy-MM-dd' />" required>
					</div>
					<div class="form-group">
						<label for="status"><i class="fas fa-info-circle"></i> Trạng thái *</label>
						<select name="status" id="status" required>
							<option value="ToDo" ${task.status == 'ToDo' ? 'selected' : ''}>Chưa bắt đầu</option>
							<option value="InProgress" ${task.status == 'InProgress' ? 'selected' : ''}>Đang thực hiện</option>
							<option value="Review" ${task.status == 'Review' ? 'selected' : ''}>Chờ duyệt</option>
							<option value="Rejected" ${task.status == 'Rejected' ? 'selected' : ''}>Bị từ chối</option>
							<option value="Done" ${task.status == 'Done' ? 'selected' : ''}>Hoàn thành</option>
						</select>
					</div>
				</div>
				<div class="form-actions">
					<button type="button" class="btn-cancel" id="cancelEditButton">
						<i class="fas fa-times"></i> Hủy
					</button>
					<button type="submit" class="btn-submit">
						<i class="fas fa-save"></i> Lưu công việc
					</button>
				</div>
			</form>
		</div>
	</div>
</main>
<script>
    // Hiển thị/ẩn form chỉnh sửa
    document.getElementById("editTaskButton").addEventListener("click", function () {
        document.getElementById("taskDetailView").style.display = "none";
        document.getElementById("taskEditForm").style.display = "block";
        toggleDocumentFields();
    });

    // Hủy chỉnh sửa
    document.getElementById("cancelEditButton").addEventListener("click", function () {
        document.getElementById("taskDetailView").style.display = "block";
        document.getElementById("taskEditForm").style.display = "none";
    });

    // Hiển thị/ẩn các trường nhập tài liệu mới
    function toggleDocumentFields() {
        const documentSelect = document.getElementById("documentSelect");
        const newDocumentFields = document.getElementById("newDocumentFields");
        if (documentSelect.value === "new") {
            newDocumentFields.style.display = "block";
        } else {
            newDocumentFields.style.display = "none";
            document.getElementById("documentName").value = "";
            document.getElementById("documentURL").value = "";
        }
    }

    // Client-side validation
    document.getElementById("editTaskForm").addEventListener("submit", function (e) {
        const documentSelect = document.getElementById("documentSelect").value;
        const documentName = document.getElementById("documentName").value;
        const documentURL = document.getElementById("documentURL").value;
        if (documentSelect === "new" && documentURL && !documentName) {
            e.preventDefault();
            alert("Vui lòng nhập tên tài liệu khi cung cấp liên kết tài liệu.");
        }
        const startDate = document.getElementById("startDate").value;
        const endDate = document.getElementById("endDate").value;
        if (startDate && endDate && new Date(endDate) < new Date(startDate)) {
            e.preventDefault();
            alert("Ngày kết thúc phải sau ngày bắt đầu.");
        }
    });

    // Kích hoạt toggleDocumentFields khi tải trang
    window.onload = function() {
        toggleDocumentFields();
    };
</script>
</body>
</html>