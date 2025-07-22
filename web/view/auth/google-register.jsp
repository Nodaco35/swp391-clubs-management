<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hoàn tất đăng ký Google - Quản lý Câu lạc bộ</title>
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
        <a href="${pageContext.request.contextPath}/login" class="auth-btn signin-inactive">Sign In</a>
        <button class="auth-btn register-active">Complete Registration</button>
    </div>
</nav>

<!-- Main Content -->
<div class="main-container">
    <!-- Left Banner Section -->
    <div class="banner-section">
        <img src="${pageContext.request.contextPath}/img/banner.jpg" alt="Banner" class="banner-image">
    </div>

    <!-- Right Registration Section -->
    <div class="form-section">
        <div class="form-header">
            <h2 class="form-title">Hoàn tất</h2>
            <h3 class="form-subtitle">Đăng ký Google.</h3>
        </div>

        <!-- Google User Info Display -->
        <% if (request.getAttribute("googleEmail") != null) { %>
        <div class="google-user-info">
            <div class="google-avatar">
                <% if (request.getAttribute("googlePicture") != null && !request.getAttribute("googlePicture").toString().isEmpty()) { %>
                    <img src="<%= request.getAttribute("googlePicture") %>" alt="Google Avatar" class="avatar-img">
                <% } else { %>
                    <i class="fas fa-user-circle"></i>
                <% } %>
            </div>
            <div class="google-details">
                <p><strong>Email Google:</strong> <%= request.getAttribute("googleEmail") %></p>
                <% if (request.getAttribute("googleName") != null) { %>
                    <p><strong>Tên Google:</strong> <%= request.getAttribute("googleName") %></p>
                <% } %>
            </div>
        </div>
        <% } %>

        <!-- Error Alert -->
        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error">
            <%= request.getAttribute("error") %>
        </div>
        <% } %>

        <!-- Success Alert -->
        <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success">
            <%= request.getAttribute("success") %>
        </div>
        <% } %>

        <p class="form-description">
            Vui lòng cung cấp thông tin bổ sung để hoàn tất việc tạo tài khoản.
        </p>

        <!-- Google Registration Form -->
        <form action="${pageContext.request.contextPath}/auth/google/register" method="post" class="auth-form" id="googleRegisterForm">
            <div class="form-group">
                <input 
                    type="text" 
                    name="fullName" 
                    class="form-input"
                    placeholder="Họ và tên"
                    value="<%= request.getAttribute("fullName") != null ? request.getAttribute("fullName") : 
                           (request.getAttribute("googleName") != null ? request.getAttribute("googleName") : "") %>"
                    required
                >
            </div>

            <div class="form-group">
                <input 
                    type="password" 
                    name="password" 
                    class="form-input"
                    placeholder="Mật khẩu (tối thiểu 6 ký tự)"
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
                    placeholder="Xác nhận mật khẩu"
                    id="confirmPassword"
                    required
                >
                <i class="fas fa-eye password-toggle" id="confirmPassword-icon" onclick="togglePassword('confirmPassword')"></i>
            </div>

            <div class="form-group">
                <input 
                    type="date" 
                    name="dateOfBirth" 
                    class="form-input"
                    value="<%= request.getAttribute("dateOfBirth") != null ? request.getAttribute("dateOfBirth") : "" %>"
                    required
                >
                <label for="dateOfBirth" class="form-label">Ngày sinh</label>
            </div>

            <div class="form-info">
                <p><i class="fas fa-info-circle"></i> Tài khoản sẽ được tạo với quyền Sinh viên.</p>
                <p><i class="fas fa-shield-alt"></i> Tài khoản Google đã được xác minh tự động.</p>
                <p><i class="fas fa-key"></i> Bạn có thể đăng nhập bằng email/password hoặc Google.</p>
            </div>

            <button type="submit" class="submit-btn">
                <i class="fas fa-user-plus"></i>
                Hoàn tất đăng ký
            </button>
        </form>

        <!-- Footer Link -->
        <p class="footer-link">
            Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập ngay!</a>
        </p>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/auth.js"></script>
<script>
    // Form validation
    document.getElementById('googleRegisterForm').addEventListener('submit', function(e) {
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        
        if (password !== confirmPassword) {
            e.preventDefault();
            alert('Mật khẩu xác nhận không khớp!');
            return false;
        }
        
        if (password.length < 6) {
            e.preventDefault();
            alert('Mật khẩu phải có ít nhất 6 ký tự!');
            return false;
        }
    });
</script>

<style>
    .google-user-info {
        background: #f8f9fa;
        border: 1px solid #dee2e6;
        border-radius: 8px;
        padding: 15px;
        margin: 15px 0;
        display: flex;
        align-items: center;
        gap: 15px;
    }
    
    .google-avatar .avatar-img {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        object-fit: cover;
    }
    
    .google-avatar .fas {
        font-size: 60px;
        color: #6c757d;
    }
    
    .google-details p {
        margin: 5px 0;
        font-size: 14px;
        color: #495057;
    }
    
    .form-description {
        color: #6c757d;
        font-size: 14px;
        margin-bottom: 20px;
        text-align: center;
    }
    
    .form-info {
        background: #e7f3ff;
        border: 1px solid #b3d9ff;
        border-radius: 6px;
        padding: 12px;
        margin: 15px 0;
    }
    
    .form-info p {
        margin: 5px 0;
        font-size: 13px;
        color: #0056b3;
    }
    
    .form-info .fas {
        margin-right: 8px;
        width: 16px;
    }
    
    .form-label {
        position: absolute;
        top: 10px;
        right: 15px;
        font-size: 12px;
        color: #6c757d;
        pointer-events: none;
    }
    
    input[type="date"] + .form-label {
        display: block;
    }
    
    input[type="date"]:focus + .form-label,
    input[type="date"]:not(:placeholder-shown) + .form-label {
        display: none;
    }
</style>
</body>
</html>
