<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác minh tài khoản - Quản lý Câu lạc bộ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body class="verify-result-body">
    <div class="verify-container">
        <div class="verify-header">
            <h1>Xác minh tài khoản</h1>
        </div>
        <div class="content">
            <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i>
                    <%= request.getAttribute("success") %>
                </div>
                <div class="button-container">
                    <% if (request.getAttribute("email") != null) { %>
                        <a href="${pageContext.request.contextPath}/login?email=<%= request.getAttribute("email") %>" class="verify-btn">
                            <i class="fas fa-sign-in-alt"></i> Đăng nhập ngay
                        </a>
                    <% } else { %>
                        <a href="${pageContext.request.contextPath}/login" class="verify-btn">
                            <i class="fas fa-sign-in-alt"></i> Đăng nhập ngay
                        </a>
                    <% } %>
                </div>
            <% } %>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">
                    <i class="fas fa-times-circle"></i>
                    <%= request.getAttribute("error") %>
                </div>

                <div class="button-container">
                    <% if (request.getAttribute("email") != null) { %>
                        <a href="${pageContext.request.contextPath}/resend-verification?email=<%= request.getAttribute("email") %>" class="verify-btn">
                            <i class="fas fa-paper-plane"></i> Gửi lại email xác minh
                        </a>
                        <a href="${pageContext.request.contextPath}/register" class="verify-btn secondary">
                            <i class="fas fa-user-plus"></i> Đăng ký lại
                        </a>
                    <% } else { %>
                        <a href="${pageContext.request.contextPath}/register" class="verify-btn">
                            <i class="fas fa-user-plus"></i> Đăng ký lại
                        </a>
                    <% } %>
                </div>
            <% } %>

            <div class="footer-link">
                <a href="${pageContext.request.contextPath}/">
                    <i class="fas fa-home"></i> Quay lại trang chủ
                </a>
            </div>
        </div>
    </div>
</body>
</html>
