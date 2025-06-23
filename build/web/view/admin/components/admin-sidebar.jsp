<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<aside class="sidebar">
    <div class="sidebar-header">
        <i class="fas fa-users"></i>
        <h2>UniClub Admin</h2>
    </div>
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/admin/dashboard" class="active"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/club-requests"><i class="fas fa-clipboard-list"></i> Đơn đề xuất CLB</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/clubs"><i class="fas fa-users"></i> Quản lý CLB</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/events-admin"><i class="fas fa-calendar-alt"></i> Quản lý sự kiện</a></li>
        <li><a href="${pageContext.request.contextPath}/admin?action=manageAccounts"><i class="fas fa-user"></i> Quản lý người dùng</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/reports"><i class="fas fa-file-alt"></i> Báo cáo</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/settings"><i class="fas fa-cog"></i> Cài đặt</a></li>
        <li><a href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Đăng xuất</a></li>
    </ul>
</aside>