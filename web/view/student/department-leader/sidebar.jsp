<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="sidebar">
    <div class="logo-details">
        <i class='fas fa-users-cog'></i>
        <span class="logo_name">Quản Lý Ban</span>
    </div>
    <ul class="nav-links">
        <li>
            <a href="${pageContext.request.contextPath}/department-dashboard?clubID=${param.clubID}" class="${param.active == 'dashboard' ? 'active' : ''}">
                <i class='fas fa-tachometer-alt'></i>
                <span class="link_name">Dashboard</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/department-members?clubID=${param.clubID}" class="${param.active == 'members' ? 'active' : ''}">
                <i class='fas fa-users'></i>
                <span class="link_name">Thành Viên</span>
            </a>
        </li>        <li>
            <a href="${pageContext.request.contextPath}/department-task?action=list&clubID=${param.clubID}" class="${param.active == 'tasks' ? 'active' : ''}">
                <i class='fas fa-tasks'></i>
                <span class="link_name">Nhiệm Vụ</span>
            </a>
        </li>        <li>
            <a href="${pageContext.request.contextPath}/department-meeting?action=list&clubID=${param.clubID}" class="${param.active == 'meetings' ? 'active' : ''}">
                <i class='fas fa-calendar-alt'></i>
                <span class="link_name">Cuộc Họp</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/department-notifications?clubID=${param.clubID}" class="${param.active == 'notifications' ? 'active' : ''}">
                <i class='fas fa-bell'></i>
                <span class="link_name">Thông Báo</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/myclub?clubId=${param.clubID}">
                <i class='fas fa-arrow-left'></i>
                <span class="link_name">Quay Lại CLB</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/logout">
                <i class='fas fa-sign-out-alt'></i>
                <span class="link_name">Đăng Xuất</span>
            </a>
        </li>
    </ul>
</div>

<style>
    .sidebar {
        position: fixed;
        height: 100%;
        width: 240px;
        background: #0a3d62;
        transition: all 0.5s ease;
        z-index: 100;
    }
    .sidebar .logo-details {
        height: 60px;
        display: flex;
        align-items: center;
        padding: 0 15px;
    }
    .sidebar .logo-details i {
        font-size: 28px;
        color: #fff;
        margin-right: 10px;
    }
    .sidebar .logo-details .logo_name {
        color: #fff;
        font-size: 18px;
        font-weight: 600;
    }
    .sidebar .nav-links {
        margin-top: 10px;
        padding-left: 0;
    }
    .sidebar .nav-links li {
        position: relative;
        list-style: none;
        height: 50px;
    }
    .sidebar .nav-links li a {
        height: 100%;
        width: 100%;
        display: flex;
        align-items: center;
        text-decoration: none;
        transition: all 0.4s ease;
        padding: 0 15px;
    }
    .sidebar .nav-links li a.active {
        background: #1a5276;
    }
    .sidebar .nav-links li a:hover {
        background: #1a5276;
    }
    .sidebar .nav-links li i {
        min-width: 30px;
        text-align: center;
        font-size: 18px;
        color: #fff;
    }
    .sidebar .nav-links li a .link_name {
        color: #fff;
        font-size: 15px;
        font-weight: 400;
    }
    
    /* Main Content Styling */
    body {
        padding-left: 240px;
        background-color: #f5f5f5;
    }
</style>
