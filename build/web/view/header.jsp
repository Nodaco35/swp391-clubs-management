
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="models.User" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" href="../style/homepage.css"/>
    </head>
    <body>
        <div class="logo">LOGO</div>
        <input type="text" class="search" placeholder="Tìm tên câu lạc bộ">
        <div class="user-controls">
            <span class="username">Name</span>
            <button class="logout">Log out</button>
            <%
                User user = (User) session.getAttribute("user");
            %>
            <%
                if (user == null) {
            %>
            <!-- Chưa đăng nhập -->
            <a href="login" class="username">Đăng nhập</a>
            <a href="register" class="logout">Đăng ký</a>
            <%
                } else {
            %>
            <!-- Đã đăng nhập -->
            <span class="username" class="username"><%= user.getFullName() %></span>
            <a href="logout" class="logout">Log out</a>
            <%
                }
            %>
        </div>
    </body>
</html>
