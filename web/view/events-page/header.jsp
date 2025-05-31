<%--
  Created by IntelliJ IDEA.
  User: thuan
  Date: 5/21/2025
  Time: 4:05 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
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
                        <form action="events-page" method="get">
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
                               class="${currentPath == '/' ? 'active' : ''}">
                                Trang Chủ
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/clubs"
                               class="${fn:contains(currentPath, '/clubs') ? 'active' : ''}">
                                Câu Lạc Bộ
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/events-page"
                               class="${fn:contains(currentPath, '/events-page') ? 'active' : ''}">
                                Sự Kiện
                            </a>
                        </li>
                    </ul>
                </nav>

                <div class="auth-buttons">
                    <c:choose>
                        <c:when test="${sessionScope.user != null}">
                            <div class="user-menu" id="userMenu">
                                <span id="userName">Hi, ${sessionScope.user.fullName}</span>
                                <a href="${pageContext.request.contextPath}/profile?action=myProfile" class="btn btn-outline">
                                    <i class="fa-solid fa-user"></i>
                                </a>
                                <form action="logout" method="post">
                                    <input class="btn btn-primary" type="submit" value="Logout">
                                </form>
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

            <!-- Mobile Menu -->
            <div class="mobile-menu" id="mobileMenu">
                <!-- <div class="mobile-search">
                        <div class="search-box">
                                <i class="fas fa-search search-icon"></i>
                                <input type="text" placeholder="Tìm kiếm sự kiện..." class="search-input">
                                <button class="search-btn">
                                        <i class="fas fa-search"></i>
                                </button>
                        </div>
                </div> -->
                <nav class="mobile-nav">
                    <a href="/">Trang Chủ</a>
                    <a href="/clubs">Câu Lạc Bộ</a>
                    <a href="/events-page" class="active">Sự Kiện</a>
                </nav>
                <!-- <div class="mobile-auth">
                        <a href="/login" class="btn btn-outline">Đăng Nhập</a>
                        <a href="/register" class="btn btn-primary">Đăng Ký</a>
                </div> -->
            </div>
        </header>
    </body>
</html>
