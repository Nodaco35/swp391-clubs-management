<%-- 
    Document   : overview
    Created on : Jun 15, 2025, 11:26:39 AM
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
<div id="overview-tab" class="tab-content active">
<section class="stats-section">
	<h2>Thống kê tổng quan</h2>
	<div class="stats-grid">
		<div class="stat-card">
			<div class="stat-icon departments">
				<i class="fas fa-sitemap"></i>
			</div>
			<div class="stat-content">
				<h3 id="totalDepartments">${totalDepartments}</h3>
				<p>Các ban</p>
			</div>
		</div>
		<div class="stat-card">
			<div class="stat-icon members">
				<i class="fas fa-users"></i>
			</div>
			<div class="stat-content">
				<h3 id="totalMembers">${totalMembers}</h3>
				<p>Thành viên</p>
			</div>
		</div>
		<div class="stat-card">
			<div class="stat-icon events">
				<i class="fas fa-calendar"></i>
			</div>
			<div class="stat-content">
				<h3 id="totalEvents">${totalEvents}</h3>
				<p>Sự kiện</p>
			</div>
		</div>
		<div class="stat-card">
			<div class="stat-icon active-tasks">
				<i class="fas fa-tasks"></i>
			</div>
			<div class="stat-content">
				<h3 id="activeTasks">8</h3>
				<p>Nhiệm vụ đang thực hiện</p>
			</div>
		</div>
	</div>
</section>
<section class="departments-overview">
	<h2>Các phòng ban trong CLB</h2>
	<div class="departments-grid" id="departmentsGrid">
		<div class="department-card">
			<h3><i class="fas fa-graduation-cap"></i> Ban Chuyên Môn</h3>
			<p>Phụ trách nội dung chuyên môn và kỹ thuật</p>
		</div>
		<div class="department-card">
			<h3><i class="fas fa-handshake"></i> Ban Đối Ngoại</h3>
			<p>Liên hệ đối tác, tài trợ và quan hệ công chúng</p>
		</div>
		<div class="department-card">
			<h3><i class="fas fa-file-alt"></i> Ban Nội Dung</h3>
			<p>Phụ trách nội dung, chương trình và tài liệu sự kiện</p>
		</div>
		<div class="department-card">
			<h3><i class="fas fa-bullhorn"></i> Ban Truyền Thông</h3>
			<p>Quảng bá, truyền thông và thiết kế nội dung</p>
		</div>
		<div class="department-card">
			<h3><i class="fas fa-tools"></i> Ban Hậu Cần</h3>
			<p>Hỗ trợ logistics, tài chính và cơ sở vật chất</p>
		</div>
	</div>
</section>
</div>
</body>
</html>
