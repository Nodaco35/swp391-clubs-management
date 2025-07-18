<%-- 
    Document   : tasks
    Created on : Jun 15, 2025, 11:27:00 AM
    Author     : LE VAN THUAN
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
	<section class="timeline-management">
		<div class="section-header">
			<div class="section-title">
				<h2>Timeline quản lý sự kiện</h2>
			</div>
			<div class="section-actions">
				<form method="get" action="${pageContext.request.contextPath}/chairman-page/tasks">
					<select name="eventID" id="timelineEventFilter" class="filter-select" onchange="this.form.submit()">
						<option value="">Tất cả sự kiện</option>
						<c:forEach var="event" items="${eventList}">
							<option value="${event.eventID}" ${param.eventID == event.eventID ? 'selected' : ''}>
									${event.eventName}
							</option>
						</c:forEach>
					</select>
				</form>
			</div>
		</div>

		<div class="timeline-container" id="timelineContainer">
			<c:if test="${not empty timelineMap}">
				<c:forEach var="entry" items="${timelineMap}">
					<c:set var="event" value="${entry.key}"/>
					<c:set var="termMap" value="${entry.value}"/>

					<div class="timeline-item">
						<div class="timeline-header">
							<h3><i class="fas fa-calendar-alt"></i> ${event.eventName}</h3>
							<div class="timeline-actions">
								<button class="btn-edit-timeline"><i class="fas fa-edit"></i> Chỉnh sửa</button>
									<%--								        <button class="btn-delete-timeline"><i class="fas fa-trash"></i> Xóa</button>--%>
							</div>
						</div>

						<div class="timeline-phases">
							<c:forEach var="termEntry" items="${termMap}">
								<c:set var="term" value="${termEntry.key}"/>
								<c:set var="tasks" value="${termEntry.value}"/>

								<div class="phase-item">
									<div class="phase-connector"></div>
									<div class="phase-content">
										<div class="phase-header">
											<h4>${term}</h4>
											<span class="phase-status">Tổng số công việc: ${fn:length(tasks)}</span>
										</div>

										<!-- Hiển thị thời gian term -->
										<c:set var="termInfo"
										       value="${requestScope['termInfoMap_'.concat(event.eventID)][term]}"/>
										<c:choose>
											<c:when test="${not empty termInfo}">
												<p class="phase-description">Thời gian:
													<fmt:formatDate value="${termInfo.termStart}" pattern="dd/MM/yyyy"/>
													-
													<fmt:formatDate value="${termInfo.termEnd}" pattern="dd/MM/yyyy"/>
												</p>
											</c:when>
											<c:when test="${not empty tasks}">
												<c:set var="firstTask" value="${tasks[0]}"/>
												<p class="phase-description">Thời gian:
													<fmt:formatDate value="${firstTask.term.termStart}"
													                pattern="dd/MM/yyyy"/> -
													<fmt:formatDate value="${firstTask.term.termEnd}"
													                pattern="dd/MM/yyyy"/>
												</p>
											</c:when>
											<c:otherwise>
												<p class="phase-description">Chưa có thông tin thời gian</p>
											</c:otherwise>
										</c:choose>

										<!-- Departments info -->
										<c:if test="${not empty tasks}">
											<div class="phase-info">
												<div class="phase-departments">
													<i class="fas fa-users"></i>
													<c:set var="deptSet" value=""/>
													<c:forEach var="task" items="${tasks}">
														<c:set var="deptName" value="${task.departmentAssignee.departmentName}" />
														<c:if test="${not empty deptName && not fn:contains(deptSet, deptName)}">
															<c:choose>
																<c:when test="${empty deptSet}">
																	<c:set var="deptSet" value="${deptName}"/>
																</c:when>
																<c:otherwise>
																	<c:set var="deptSet" value="${deptSet}, ${deptName}"/>
																</c:otherwise>
															</c:choose>
														</c:if>
													</c:forEach>
														${deptSet}

												</div>

											</div>
										</c:if>

										<!-- Task info -->
										<div class="phase-tasks">
											<div class="phase-tasks-header">
												<h5><i class="fas fa-tasks"></i> Công việc</h5>
												<button class="btn-add-task-to-phase">
													<i class="fas fa-plus"></i> Giao công việc
												</button>
											</div>

											<div class="phase-task-list">
												<c:choose>
													<c:when test="${not empty tasks}">
														<c:forEach var="task" items="${tasks}">
															<div class="phase-task-item">
																<div class="phase-task-info">
																	<div class="phase-task-title">${task.title}</div>
																	<div class="phase-task-meta">
																		<div class="phase-task-department">
																			<i class="fas fa-user"></i>
																				${task.departmentAssignee.departmentName}
																		</div>
																		<div class="phase-task-deadline">
																			<i class="fas fa-calendar-alt"></i>
																			<fmt:formatDate value="${task.startDate}"
																			                pattern="dd/MM/yyyy"/> -
																			<fmt:formatDate value="${task.endDate}"
																			                pattern="dd/MM/yyyy"/>
																		</div>
																	</div>
																</div>
																<div class="phase-task-status">
																	<c:choose>
																		<c:when test="${task.status == 'ToDo'}">
																			<span class="badge badge-secondary">Chưa bắt đầu</span>
																		</c:when>
																		<c:when test="${task.status == 'InProgress'}">
																			<span class="badge badge-warning">Đang thực hiện</span>
																		</c:when>
																		<c:when test="${task.status == 'Review'}">
																			<span class="badge badge-info">Chờ duyệt</span>
																		</c:when>
																		<c:when test="${task.status == 'Rejected'}">
																			<span class="badge badge-danger">Bị từ chối</span>
																		</c:when>
																		<c:when test="${task.status == 'Done'}">
																			<span class="badge badge-success">Hoàn thành</span>
																		</c:when>
																		<c:otherwise>
																			<span class="badge badge-dark">Không xác định</span>
																		</c:otherwise>
																	</c:choose>
																</div>

																<div class="phase-task-actions">
																	<button class="btn-task-action-small"
																	        title="Xem chi tiết">
																		<i class="fas fa-eye"></i>
																	</button>
																	<button class="btn-task-action-small"
																	        title="Xóa công việc">
																		<i class="fas fa-trash"></i>
																	</button>
																</div>
															</div>
														</c:forEach>
													</c:when>
													<c:otherwise>
														<div class="phase-task-empty">
															<p style="color: #666; font-style: italic; text-align: center; padding: 20px;">
																Chưa có công việc nào được giao trong giai đoạn này
															</p>
														</div>
													</c:otherwise>
												</c:choose>
											</div>
										</div>
									</div>
								</div>
							</c:forEach>
						</div>
					</div>
				</c:forEach>
			</c:if>
		</div>

		<!-- Empty State -->
		<c:if test="${empty timelineMap}">
			<div style="text-align: center; padding: 40px; color: #666;">
				<i class="fas fa-clock" style="font-size: 48px; margin-bottom: 16px; opacity: 0.5;"></i>
				<p>Chưa có timeline nào được tạo.</p>
			</div>
		</c:if>
	</section>
</main>

</body>
</html>
