<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<header class="header">
    <div class="container header-container">
        <a href="${pageContext.request.contextPath}/">
            <div class="logo">
                <i class="fas fa-users"></i>
                <span>UniClub</span>
            </div>
        </a>
        <nav class="main-nav">
            <ul>
                <li></li>
                <li></li>
                <li><a href="${pageContext.request.contextPath}/" class="filter-option active">Trang Chủ</a></li>
                <li><a href="${pageContext.request.contextPath}/clubs" class="filter-option">Câu Lạc Bộ</a></li>
                <li><a href="${pageContext.request.contextPath}/events-page" class="filter-option">Sự Kiện</a></li>
                
            </ul>
        </nav>
        <div class="auth-buttons">
            <c:choose>
                <c:when test="${sessionScope.user != null}">
                    <div class="user-menu">
                        <span>Xin chào, ${sessionScope.user.fullName}</span>
                        <a href="${pageContext.request.contextPath}/profile?action=myProfile" class="btn btn-outline">Tài Khoản</a>
                        <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline">Đăng Xuất</a>
                        <a href="${pageContext.request.contextPath}/myclub" class="btn btn-outline">Câu lạc bộ của tôi</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-outline">Đăng Nhập</a>
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-primary">Đăng Ký</a>
                </c:otherwise>
            </c:choose>
            <button class="mobile-menu-btn">
                <i class="fas fa-bars"></i>
            </button>
        </div>
    </div>
</header>