<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session != null && session.getAttribute("user") != null) {
        int permissionID = (int) session.getAttribute("permissionID");
        String redirectUrl;
        switch (permissionID) {
            case 1:
                redirectUrl = "/view/Student/homepage.jsp";
                break;
            case 2:
                redirectUrl = "/view/Admin/dashboard.jsp";
                break;
            case 3:
                redirectUrl = "/view/IC_Officer/dashboard.jsp";
                break;
            default:
                redirectUrl = "/view/login.jsp";
        }
        response.sendRedirect(request.getContextPath() + redirectUrl);
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng ký - Quản lý Câu lạc bộ</title>
        <!-- CSS với context path -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    </head>
    <body>
        <!-- Navigation Header -->
        <nav class="navbar">
            <div class="nav-left">
                <div class="logo">logo</div>
                <button class="menu-toggle" onclick="toggleMobileMenu()">☰</button>
                <ul class="nav-menu">
                    <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                    <li><a href="${pageContext.request.contextPath}/clubs">Clubs</a></li>
                    <li><a href="${pageContext.request.contextPath}/events-page">Events</a></li>
                    <li><a href="${pageContext.request.contextPath}/contact">Contact</a></li>
                </ul>
            </div>
            <div class="nav-right">
                <!-- Inactive Sign In Button -->
                <a href="${pageContext.request.contextPath}/login" class="auth-btn signin-inactive">Sign In</a>
                <!-- Active Register Button -->
                <button class="auth-btn register-active">Register</button>
            </div>
        </nav>

        <!-- Main Content -->
        <div class="main-container">
            <!-- Left Banner Section -->
            <div class="banner-section">
        <img src="${pageContext.request.contextPath}/img/banner.jpg" alt="Banner" class="banner-image">
    </div>
            <!-- Right Register Section -->
            <div class="form-section">
                <div class="form-header">
                    <h2 class="form-title">Create</h2>
                    <h3 class="form-subtitle">Account.</h3>
                </div>

                <!-- Error Alert -->
                <% if (request.getAttribute("error") != null) {%>
                <div class="alert alert-error">
                    <%= request.getAttribute("error")%>
                </div>
                <% } %>                <!-- Success Alert -->
                <% if (request.getAttribute("success") != null) {%>
                <div class="alert alert-success">
                    <%= request.getAttribute("success")%>
                </div>
                <% }%>

                <!-- Email Notification Alert -->
                <% if (request.getAttribute("emailNotification") != null) {%>
                <div class="alert alert-info">
                    <%= request.getAttribute("emailNotification")%>
                </div>
                <% }%>

                <!-- Register Form -->
                <form action="${pageContext.request.contextPath}/register" method="post" class="auth-form" id="registerForm">
                    <div class="form-group">
                        <input 
                            type="text" 
                            name="fullName" 
                            class="form-input"
                            placeholder="Full Name"
                            value="<%= request.getAttribute("fullName") != null ? request.getAttribute("fullName") : ""%>"
                            required
                            >
                    </div>                    <div class="form-group">
                        <input 
                            type="email" 
                            name="email" 
                            class="form-input"
                            placeholder="Enter Email"
                            value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : ""%>"
                            required
                            >
                    </div>

                    <div class="form-group">
                        <input 
                            type="date" 
                            name="dateOfBirth" 
                            class="form-input"
                            placeholder="Date of Birth"
                            value="<%= request.getAttribute("dateOfBirth") != null ? request.getAttribute("dateOfBirth") : ""%>"
                            required
                            >
                    </div>

                    <div class="form-group">
                        <input 
                            type="password" 
                            name="password" 
                            class="form-input"
                            placeholder="Password"
                            id="password"
                            required
                            >
                        <i class="fas fa-eye password-toggle" id="password-icon" onclick="togglePassword('password')"></i>
                    </div>

                    <div class="form-group">
                        <input 
                            type="password" 
                            name="confirmPassword" 
                            class="form-input"
                            placeholder="Confirm Password"
                            id="confirmPassword"
                            required
                            >
                        <i class="fas fa-eye password-toggle" id="password-icon" onclick="togglePassword('password')"></i>
                    </div>

                    <button type="submit" class="submit-btn" id="submitBtn">
                        Create Account
                    </button>
                </form>

                <!-- Divider -->
                <div class="divider">
                    <div class="divider-line"></div>
                    <span class="divider-text">Or continue with</span>
                    <div class="divider-line"></div>
                </div>

                <!-- Google Sign In -->
                <a href="${pageContext.request.contextPath}/auth/google" class="google-btn">
                    <svg width="20" height="20" viewBox="0 0 24 24">
                    <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                    <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                    <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                    <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                    </svg>
                    <span>Google</span>
                </a>

                <!-- Footer Link -->
                <p class="footer-link">
                    Already have an account ? <a href="${pageContext.request.contextPath}/login">Sign In!</a>
                </p>
            </div>
        </div>

        <!-- Error Alert -->
        <% if (request.getAttribute("error") != null) {%>
        <div class="alert alert-error">
            <%= request.getAttribute("error")%>
        </div>
        <% } %>

        <!-- Success Alert -->
        <% if (request.getAttribute("success") != null) {%>
        <div class="alert alert-success">
            <%= request.getAttribute("success")%>
        </div>
        <% }%>
    </div>

</div>

<script src="${pageContext.request.contextPath}/js/auth.js"></script>
</body>
</html>