<%--
    Document   : profile
    Created on : May 19, 2025, 10:28:26 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="models.Users" %>
<%@page import="models.Permission" %>
<%@page import="dal.PermissionDAO" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thông tin cá nhân</title>
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Custom CSS -->

    </head>
    <link rel="stylesheet" href="./css/profileStyle.css"/>
    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <body>
        <jsp:include page="./events-page/header.jsp" />
        <%
    Users user = (Users) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login");
        return;
    }
    String contextPath = request.getContextPath();
    String avatarPath = user.getAvatar() != null ? user.getAvatar() : "img/Hinh-anh-dai-dien-mac-dinh-Facebook.png";
    
        %>

        <div class="profile-container">
            <h1>Thông tin cá nhân</h1>

            <form action="profile?action=update" method="POST" enctype="multipart/form-data">
                <div class="row">
                    <!-- Avatar Section -->
                    <div class="col-md-4">
                        <div class="avatar-box">
                            <img src="<%= contextPath + "/" + avatarPath %>" alt="Avatar" class="avatar-img" />
                            <div class="avatar-upload">
                                <input type="file" class="form-control" id="avatar" name="avatar" accept="image/*">
                            </div>
                        </div>
                    </div>

                    <!-- Info Section -->
                    <div class="col-md-8 info-section">
<!--                        <input type="hidden" name="id" value="<%= user.getUserID()%>">
                        <input type="hidden" name="email" value="<%= user.getEmail()%>">-->
                        <div class="mb-3">
                            <label for="userId" class="form-label">Mã người dùng</label>
                            <input type="text" class="form-control" id="userId"  value="<%= user.getUserID() %>" readonly>
                        </div>

                        <div class="mb-3">
                            <label for="name" class="form-label">Họ và tên</label>
                            <input type="text" class="form-control" id="name" name="name" value="<%= user.getFullName() %>" required>
                        </div>
                        <div class="mb-3">
                            <label for="dob" class="form-label">Ngày sinh</label>
                            <input type="date" class="form-control" id="dob" name="dob" 
                                   value="<%=(user.getDateOfBirth() != null) ? user.getDateOfBirth() : ""%>" required>
                        </div>

                        <%  int perId = user.getPermissionID();
                            Permission per = PermissionDAO.findByPerId(perId);%>
                        <div class="mb-3">
                            <label for="PermissionName" class="form-label">Quyền truy cập</label>
                            <input type="text" class="form-control" id="permissionName" name="PermissionName" value="<%= (per != null) ? per.getPermissionName() : "" %>" readonly>
                        </div>
                        <div class="mb-3">
                            <label for="gmail" class="form-label">Email</label>
                            <input type="gmail" class="form-control" id="gmail" value="<%= user.getEmail() %>" readonly>

                        </div>

                        <div class="mb-3">
                            <button type="submit" class="btn btn-primary">Cập nhật thông tin</button>
                        </div>
                    </div>
                </div>
            </form>

            <form action="verifyCode?action=sendOtp" method="POST">
                <input type="hidden" name="type" value="Verify current email">
                <input type="hidden" name="email" value="<%= user.getEmail() %>">
                <div class="mb-3">
                    <input type="submit" value="Thay đổi email" class="btn btn-secondary">
                </div>
            </form>
            

            <% String msg = (String) request.getAttribute("msg");
       if (msg != null) { %>
            <div class="alert alert-info"><%= msg %></div>
            <% } %>
        </div>


        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <!-- Custom JS -->
        <script>
            // Preview avatar before upload
            document.getElementById('avatar').addEventListener('change', function (event) {
                const file = event.target.files[0];
                if (file) {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        document.querySelector('.avatar-img').src = e.target.result;
                    };
                    reader.readAsDataURL(file);
                }
            });
        </script>
        <jsp:include page="./components/footer.jsp" />

        <!-- JavaScript -->
        <script src="${pageContext.request.contextPath}/js/script.js"></script>
    </body>
</html>




