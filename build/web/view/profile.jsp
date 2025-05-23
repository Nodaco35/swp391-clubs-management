<%-- 
    Document   : profile
    Created on : May 19, 2025, 10:28:26 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="models.User" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thông tin cá nhân</title>
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Custom CSS -->
        <style>
            body {
                background-color: #f5f5f5;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }
            .profile-container {
                max-width: 600px;
                margin: 50px auto;
                background-color: #fff;
                padding: 30px;
                border-radius: 10px;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            }
            .profile-container h1 {
                text-align: center;
                color: #333;
                margin-bottom: 30px;
            }
            .form-label {
                font-weight: 500;
                color: #555;
            }
            .form-control {
                border-radius: 5px;
                border: 1px solid #ced4da;
            }
            .form-control:focus {
                border-color: #007bff;
                box-shadow: 0 0 5px rgba(0, 123, 255, 0.3);
            }
            .btn-primary, .btn-secondary {
                width: 100%;
                padding: 10px;
                border-radius: 5px;
                font-size: 16px;
            }
            .alert {
                margin-top: 20px;
                text-align: center;
            }
        </style>
    </head>
    <body>
        <%
            User user = (User) session.getAttribute("user");
            if (user == null) {
                response.sendRedirect("index.html");
                return;
            }
        %>

        <div class="profile-container">
            <h1>Thông tin cá nhân</h1>
            <form action="home?action=update" method="POST" id="profileForm">
                <!-- User ID -->
                <div class="mb-3">
                    <label for="id" class="form-label">Mã người dùng</label>
                    <input type="text" class="form-control" id="id" name="id" value="<%= user.getUserID() %>" readonly>
                </div>
                <!-- Full Name -->
                <div class="mb-3">
                    <label for="name" class="form-label">Họ và tên</label>
                    <input type="text" class="form-control" id="name" name="name" value="<%= user.getFullName() %>" required>
                </div>
                <!--         Date of Birth 
                        <div class="mb-3">
                            <label for="dob" class="form-label">Ngày sinh</label>
                            <input type="date" class="form-control" id="dob" name="dob" value="<%= user.getDob() %>" required>
                        </div>-->
                <!-- Email -->
                <div class="mb-3">
                    <label for="email" class="form-label">Email</label>
                    <input type="email" class="form-control" id="email" name="email" value="<%= user.getEmail() %>" required readonly>
                </div>

                <!-- Submit Button -->
                <div class="mb-3">
                    <button type="submit" class="btn btn-primary">Cập nhật thông tin</button>
                </div>
            </form>

            <form action="verifyCode?action=sendOtp" method="POST">
                <input type="hidden" name="type" value="Verify current email">
                <div class="mb-3">
                    
                    <input type="hidden"  name="email" value="<%= user.getEmail() %>" readonly>
                </div>

                <!-- Send OTP Button -->
                <div class="mb-3">
                    <input type="submit" value="Thay đổi email">
                </div>
            </form>
            <!-- Hiển thị thông báo -->
            <% String msg = (String) request.getAttribute("msg");
       if (msg != null) { %>
            <div class="alert alert-info"><%= msg %></div>
            <% } %>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <!-- Custom JS -->

    </body>
</html>