<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" pageEncoding="UTF-8" %>

<!-- Department Leader Sidebar -->
<nav class="sidebar" id="sidebar">
    <div class="sidebar-header">
        <div class="logo">
            <i class="fas fa-users-gear"></i>
            <span>Quản lý Ban</span>
        </div>
    </div>

    <ul class="sidebar-menu">
        <li class="menu-item ${activePage == 'dashboard' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/department-dashboard?clubID=${clubID}" class="menu-link">
                <i class="fas fa-chart-pie"></i>
                <span>Dashboard</span>
            </a>
        </li>
        <li class="menu-item ${activePage == 'members' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/department-members?clubID=${clubID}" class="menu-link">
                <i class="fas fa-users"></i>
                <span>Quản lý thành viên</span>
            </a>
        </li>
        <li class="menu-item ${activePage == 'tasks' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/department-tasks?clubID=${clubID}" class="menu-link">
                <i class="fas fa-tasks"></i>
                <span>Quản lý công việc</span>
            </a>
        </li>
        <li class="menu-item ${activePage == 'meeting' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/department-meeting?clubID=${clubID}" class="menu-link">
                <i class="fas fa-calendar-alt"></i>
                <span>Quản lý cuộc họp</span>
            </a>
        </li>
        <li class="menu-item ${activePage == 'plan-events' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/department-plan-events?clubID=${clubID}" class="menu-link">
                <i class="fas fa-calendar-check"></i>
                <span>Xem kế hoạch & sự kiện</span>
            </a>
        </li>

        <!-- Financial Management - Only for specific departments -->
        <c:if test="${isAccess}">
            <li class="menu-item ${activePage == 'financial' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/department/financial" class="menu-link">
                    <i class="fas fa-dollar-sign"></i>
                    <span>Tài chính</span>
                </a>
            </li>
        </c:if>
        
        <!-- Expense Reporting - Only for Logistics department -->
        <c:if test="${isHauCan}">
            <li class="menu-item ${activePage == 'submit-expense' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/department/submit-expense" class="menu-link">
                    <i class="fas fa-receipt"></i>
                    <span>Xin chi phí</span>
                </a>
            </li>
        </c:if>
        
        <!-- Return to Homepage -->
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/" class="menu-link">
                <i class="fas fa-home"></i>
                <span>Về trang chủ</span>
            </a>
        </li>
    </ul>
    
    <div class="sidebar-footer">
        <div class="user-info">
            <div class="user-avatar">
                <c:choose>
                    <c:when test="${not empty sessionScope.user.avatar}">
                        <img src="${pageContext.request.contextPath}/${sessionScope.user.avatar}" alt="Avatar">
                    </c:when>
                    <c:when test="${not empty currentUser.avatar}">
                        <img src="${pageContext.request.contextPath}/${currentUser.avatar}" alt="Avatar">
                    </c:when>
                    <c:otherwise>
                        <img src="${pageContext.request.contextPath}/img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg" alt="Avatar">
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="user-details">
                <div class="user-name">
                    <c:choose>
                        <c:when test="${not empty sessionScope.user.fullName}">
                            ${sessionScope.user.fullName}
                        </c:when>
                        <c:otherwise>
                            ${currentUser.fullName}
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="user-role">
                    <c:choose>
                        <c:when test="${isHauCan}">Trưởng ban Hậu Cần</c:when>
                        <c:when test="${isAccess}">Trưởng ban Đối Ngoại</c:when>
                        <c:when test="${not empty departmentName}">Trưởng ban ${departmentName}</c:when>
                        <c:otherwise>Trưởng ban</c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</nav>
