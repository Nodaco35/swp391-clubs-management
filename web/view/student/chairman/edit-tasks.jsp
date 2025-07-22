<%--
  Created by IntelliJ IDEA.
  User: thuan
  Date: 7/23/2025
  Time: 4:18 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
	<title>MyClub Dashboard</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
	<style>
        .task-details {
            padding: 20px;
        }
        .task-detail-view {
            margin-bottom: 20px;
        }
        .task-detail-item {
            margin-bottom: 15px;
        }
        .task-detail-item label {
            font-weight: bold;
            display: inline-block;
            width: 150px;
        }
        .task-detail-item span {
            display: inline-block;
        }
        .form-group {
            margin-bottom: 10px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
        }
        .form-group input, .form-group select, .form-group textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .btn-primary {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            margin-right: 10px;
        }
        .btn-primary:hover {
            background-color: #0056b3;
        }
        .btn-outline {
            background-color: transparent;
            color: #007bff;
            border: 1px solid #007bff;
            padding: 10px 20px;
            border-radius: 4px;
            cursor: pointer;
        }
        .btn-outline:hover {
            background-color: #f8f9fa;
        }
        .error-message {
            color: red;
            margin-bottom: 10px;
        }
        #newDocumentFields {
            display: none;
        }
	</style>
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
	<div class="mobile-menu" id="mobileMenu">
		<nav class="mobile-nav">
			<ul>
				<li><a href="${pageContext.request.contextPath}/" class="${pageContext.request.servletPath == '/' ? 'active' : ''}">Trang Chủ</a></li>
				<li><a href="${pageContext.request.contextPath}/clubs" class="${pageContext.request.servletPath == '/clubs' ? 'active' : ''}">Câu Lạc Bộ</a></li>
				<li><a href="${pageContext.request.contextPath}/events-page" class="${pageContext.request.servletPath == '/events-page' ? 'active' : ''}">Sự Kiện</a></li>
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
			<li>
				<a href="${pageContext.request.contextPath}/chairman-page/reports"
				   class="nav-item ${currentPath == '/chairman-page/reports' ? 'active' : ''}">
					<i class="fas fa-file-alt"></i> Báo cáo
				</a>
			</li>
			<li>
				<a href="${pageContext.request.contextPath}/chairman-page/financial-management"
				   class="nav-item ${currentPath == '/chairman-page/financial-management' ? 'active' : ''}">
					<i class="fas fa-money-bill-wave"></i> Quản lý dòng tiền
				</a>
			</li>
		</ul>
	</nav>
</header>
<main class="dashboard-content">
	<c:if test="${not empty errorMessage}">
		<div class="error-message">${errorMessage}</div>
	</c:if>
	<section class="task-details">
		<div class="section-header">
			<div class="section-title">
				<h2>Chi tiết công việc</h2>
			</div>
			<div class="section-actions">
				<button id="editTaskButton" class="btn btn-primary"><i class="fas fa-edit"></i> Chỉnh sửa công việc</button>
				<a href="${pageContext.request.contextPath}/chairman-page/tasks?eventID=${task.event.eventID}" class="btn btn-outline">Quay lại</a>
			</div>
		</div>
		<div id="taskDetailView" class="task-detail-view">
			<div class="task-detail-item">
				<label>Tiêu đề:</label>
				<span>${task.title}</span>
			</div>
			<div class="task-detail-item">
				<label>Mô tả:</label>
				<span>${task.description}</span>
			</div>
			<div class="task-detail-item">
				<label>Phòng ban:</label>
				<span>${task.departmentAssignee.departmentName}</span>
			</div>
			<div class="task-detail-item">
				<label>Tài liệu:</label>
				<span>
                    <c:choose>
	                    <c:when test="${not empty task.document}">
		                    <a href="${task.document.documentURL}" target="_blank">${task.document.documentName}</a>
	                    </c:when>
	                    <c:otherwise>Không có tài liệu</c:otherwise>
                    </c:choose>
                </span>
			</div>
			<div class="task-detail-item">
				<label>Ngày bắt đầu:</label>
				<span><fmt:formatDate value="${task.startDate}" pattern="dd/MM/yyyy"/></span>
			</div>
			<div class="task-detail-item">
				<label>Ngày kết thúc:</label>
				<span><fmt:formatDate value="${task.endDate}" pattern="dd/MM/yyyy"/></span>
			</div>
			<div class="task-detail-item">
				<label>Trạng thái:</label>
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
		<div id="taskEditForm" style="display: none;">
			<h2>Chỉnh sửa công việc</h2>
			<form action="${pageContext.request.contextPath}/edit-tasks" method="post">
				<input type="hidden" name="action" value="updateTask">
				<input type="hidden" name="taskID" value="${task.taskID}">
				<input type="hidden" name="termID" value="${task.term.termID}">
				<input type="hidden" name="eventID" value="${task.event.eventID}">
				<input type="hidden" name="clubID" value="${club.clubID}">
				<input type="hidden" name="assigneeType" value="Department">
				<input type="hidden" name="createdBy" value="${sessionScope.user.userID}">
				<div class="form-group">
					<label for="title">Tiêu đề công việc:</label>
					<input type="text" name="title" id="title" value="${task.title}" required maxlength="100">
				</div>
				<div class="form-group">
					<label for="description">Mô tả:</label>
					<textarea name="description" id="description" rows="4">${task.description}</textarea>
				</div>
				<div class="form-group">
					<label for="departmentID">Phòng ban:</label>
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
					<label for="documentSelect">Tài liệu:</label>
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
				<div id="newDocumentFields" style="display: none;">
					<div class="form-group">
						<label for="documentName">Tên tài liệu:</label>
						<input type="text" name="documentName" id="documentName" maxlength="100">
					</div>
					<div class="form-group">
						<label for="documentURL">Liên kết tài liệu:</label>
						<input type="url" name="documentURL" id="documentURL" maxlength="255">
					</div>
				</div>
				<div class="form-group">
					<label for="startDate">Ngày bắt đầu:</label>
					<input type="date" name="startDate" id="startDate" value="${task.startDate}" required>
				</div>
				<div class="form-group">
					<label for="endDate">Ngày kết thúc:</label>
					<input type="date" name="endDate" id="endDate" value="${task.endDate}" required>
				</div>
				<div class="form-group">
					<label for="status">Trạng thái:</label>
					<select name="status" id="status" required>
						<option value="ToDo" ${task.status == 'ToDo' ? 'selected' : ''}>Chưa bắt đầu</option>
						<option value="InProgress" ${task.status == 'InProgress' ? 'selected' : ''}>Đang thực hiện</option>
						<option value="Review" ${task.status == 'Review' ? 'selected' : ''}>Chờ duyệt</option>
						<option value="Rejected" ${task.status == 'Rejected' ? 'selected' : ''}>Bị từ chối</option>
						<option value="Done" ${task.status == 'Done' ? 'selected' : ''}>Hoàn thành</option>
					</select>
				</div>
				<button type="submit" class="btn btn-primary">Cập nhật công việc</button>
				<button type="button" id="cancelEditButton" class="btn btn-outline">Hủy</button>
			</form>
		</div>
	</section>



	<script>
        // Hiển thị/ẩn form chỉnh sửa
        document.getElementById("editTaskButton").addEventListener("click", function () {
            document.getElementById("taskDetailView").style.display = "none";
            document.getElementById("taskEditForm").style.display = "block";
            toggleDocumentFields(); // Kiểm tra trạng thái tài liệu ban đầu
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

        // Client-side validation cho DocumentName và DocumentURL
        document.querySelector("form").addEventListener("submit", function (e) {
            const documentSelect = document.getElementById("documentSelect").value;
            const documentName = document.getElementById("documentName").value;
            const documentURL = document.getElementById("documentURL").value;
            if (documentSelect === "new" && documentURL && !documentName) {
                e.preventDefault();
                alert("Vui lòng nhập tên tài liệu khi cung cấp liên kết tài liệu.");
            }
        });
	</script>
</main>
</body>
</html>
