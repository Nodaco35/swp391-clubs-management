<%-- 
    Document   : approval-events
    Created on : Jul 6, 2025, 12:07:30 PM
    Author     : LE VAN THUAN
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
	<title>Duyệt Sự Kiện</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/ic-approval.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
<header>



	<aside class="sidebar">
		<div class="sidebar-header">
			<i class="fas fa-university"></i>
			<h2>IC Officer Portal</h2>
		</div>
		<ul class="sidebar-menu">
			<li><a href="${pageContext.request.contextPath}/ic/dashboard"><i class="fas fa-file-alt"></i> Báo cáo hàng
				kỳ</a></li>
			<li><a href="${pageContext.request.contextPath}/ic/statistics"><i class="fas fa-chart-line"></i> Thống kê
				hiệu suất</a></li>
			<li><a href="${pageContext.request.contextPath}/ic?action=grantPermission"><i class="fas fa-users"></i> Danh
				sách đơn tạo CLB</a></li>
			<li><a href="${pageContext.request.contextPath}/ic/approval-events"><i class="fas fa-calendar-alt"></i>
				Duyệt sự kiện</a></li>
			<li><a href="${pageContext.request.contextPath}/ic/approval-agenda"><i class="fas fa-clipboard-list"></i>
				Duyệt Agenda</a></li>
			<li><a href="${pageContext.request.contextPath}/ic/schedule"><i class="fas fa-calendar-check"></i> Lịch báo
				cáo</a></li>
			<li><a href="${pageContext.request.contextPath}/ic/notifications"><i class="fas fa-bell"></i> Thông báo</a>
			</li>
			<li><a href="${pageContext.request.contextPath}/ic/settings"><i class="fas fa-cog"></i> Cài đặt</a></li>
			<li><a href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Đăng xuất</a>
			</li>
		</ul>
	</aside>
</header>
<main class="main-content">
	<div class="header">
		<h1>Quản lý Event & Agenda</h1>
		<div class="user-info">
			<div class="user-avatar">IC</div>
			<div class="user-details">
				<div class="user-name">${sessionScope.user.fullName}</div>
				<div class="user-role">IC Officer</div>
			</div>
		</div>
	</div>
</main>
</body>
</html>
