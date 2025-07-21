<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<aside class="sidebar">
    <div class="sidebar-header">
        <i class="fas fa-university"></i>
        <h2>IC Officer Portal</h2>
    </div>
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/ic/dashboard" ><i class="fas fa-file-alt"></i> Báo cáo hàng kỳ</a></li>
        <li><a href="${pageContext.request.contextPath}/ic/statistics"><i class="fas fa-chart-line"></i> Thống kê hiệu suất</a></li>
        <li><a href="${pageContext.request.contextPath}/ic?action=grantPermission"><i class="fas fa-users"></i> Danh sách đơn tạo CLB</a></li>
        <li><a href="${pageContext.request.contextPath}/ic/approval-events"><i class="fas fa-calendar-alt"></i> Duyệt sự kiện</a></li>
        <li><a href="${pageContext.request.contextPath}/ic/schedule"><i class="fas fa-calendar-check"></i> Lịch báo cáo</a></li>
        <li><a href="${pageContext.request.contextPath}/ic/notifications"><i class="fas fa-bell"></i> Thông báo</a></li>
        <li><a href="${pageContext.request.contextPath}/ic/settings"><i class="fas fa-cog"></i> Cài đặt</a></li>
        <li><a href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Đăng xuất</a></li>
    </ul>
</aside>