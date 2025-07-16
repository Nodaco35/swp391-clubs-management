<%-- 
    Document   : change-password
    Created on : Jun 2, 2025, 10:00:00 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="models.Users" %>
<!DOCTYPE html>
<html>    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đổi mật khẩu</title>
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Font Awesome for icons -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <!-- Custom CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eventsPage.css"/>
    </head>
    <body>
        <jsp:include page="./events-page/header.jsp" />
        <%
            Users user = (Users) session.getAttribute("user");
            if (user == null) {
                response.sendRedirect("login");
                return;
            }
        %>
        
        <div class="password-container">
            <h1>Đổi mật khẩu</h1>
            
            <form id="passwordChangeForm" action="change-password" method="POST">
                <div class="mb-3">
                    <label for="currentPassword" class="form-label">Mật khẩu hiện tại</label>
                    <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
                </div>
                <div class="mb-3">
                    <label for="newPassword" class="form-label">Mật khẩu mới</label>
                    <input type="password" class="form-control" id="newPassword" name="newPassword" required minlength="6">
                    <div class="form-text">Mật khẩu tối thiểu 6 ký tự</div>
                </div>
                <div class="mb-3">
                    <label for="confirmPassword" class="form-label">Xác nhận mật khẩu mới</label>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                    <div id="passwordMatchError" class="text-danger" style="display: none;">Mật khẩu không khớp</div>
                </div>
                <div class="mb-3">
                    <button type="submit" class="btn btn-primary">Đổi mật khẩu</button>
                </div>
            </form>
            
            <a href="profile?action=myProfile" class="btn btn-secondary btn-back">
                <i class="fas fa-arrow-left"></i> Quay lại trang cá nhân
            </a>
            
            <% String msg = (String) request.getAttribute("msg");
               String msgType = (String) request.getAttribute("msgType");
               if (msg != null) { 
                   String alertClass = "alert-info";
                   if (msgType != null && msgType.equals("error")) {
                       alertClass = "alert-danger";
                   } else if (msgType != null && msgType.equals("success")) {
                       alertClass = "alert-success";
                   }
            %>
            <div class="alert <%= alertClass %> mt-3"><%= msg %></div>
            <% } %>
        </div>
        
        <jsp:include page="./components/footer.jsp" />
        
        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <!-- JavaScript -->
        <script>
            // Kiểm tra mật khẩu khớp nhau
            document.getElementById('passwordChangeForm').addEventListener('submit', function(event) {
                const newPassword = document.getElementById('newPassword').value;
                const confirmPassword = document.getElementById('confirmPassword').value;
                const passwordMatchError = document.getElementById('passwordMatchError');
                
                if (newPassword !== confirmPassword) {
                    passwordMatchError.style.display = 'block';
                    event.preventDefault();
                } else {
                    passwordMatchError.style.display = 'none';
                }
                
                // Kiểm tra độ dài mật khẩu
                if (newPassword.length < 6) {
                    alert('Mật khẩu phải có ít nhất 6 ký tự');
                    event.preventDefault();
                }
            });
            
            // Hiển thị thông báo lỗi ngay khi gõ
            document.getElementById('confirmPassword').addEventListener('input', function() {
                const newPassword = document.getElementById('newPassword').value;
                const confirmPassword = document.getElementById('confirmPassword').value;
                const passwordMatchError = document.getElementById('passwordMatchError');
                
                if (newPassword !== confirmPassword) {
                    passwordMatchError.style.display = 'block';
                } else {
                    passwordMatchError.style.display = 'none';
                }
            });
        </script>
    </body>
</html>
