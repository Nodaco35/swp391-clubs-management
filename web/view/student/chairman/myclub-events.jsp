<%-- 
    Document   : myclub-events
    Created on : Jun 15, 2025, 11:26:48 AM
    Author     : LE VAN THUAN
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
	<title>Chairman Page</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
<div id="events-tab" class="tab-content">
	<!-- Statistics Cards -->
	<section class="stats-section">
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

</div>
</body>
</html>
