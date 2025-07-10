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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eventsPage.css"/>
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

        <jsp:include page="./components/profileSlidebar.jsp" />
        <div class="main-content">
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

                <div class="row mt-4">
                    <div class="col-md-6">
                        <form action="verifyCode?action=sendOtp" method="POST">
                            <input type="hidden" name="type" value="Verify current email">
                            <input type="hidden" name="email" value="<%= user.getEmail() %>">
                            <div class="mb-3">
                                <button type="submit" class="btn btn-secondary w-100">
                                    <i class="fas fa-envelope"></i> Thay đổi email
                                </button>
                            </div>
                        </form>
                    </div>
                    <div class="col-md-6">
                        <div class="mb-3">
                            <a href="change-password" class="btn btn-primary w-100">
                                <i class="fas fa-key"></i> Đổi mật khẩu
                            </a>
                        </div>
                    </div>
                </div>            <% 
                   // Kiểm tra thông báo từ request
                   String msg = (String) request.getAttribute("msg");
                   String msgType = (String) request.getAttribute("msgType");
               
                   // Nếu không có trong request, kiểm tra trong session
                   if (msg == null) {
                       msg = (String) session.getAttribute("msg");
                       msgType = (String) session.getAttribute("msgType");
                       // Xóa thông báo khỏi session sau khi đã lấy ra
                       if (msg != null) {
                           session.removeAttribute("msg");
                           session.removeAttribute("msgType");
                       }
                   }
               
                   // Hiển thị thông báo nếu có
                   if (msg != null) { 
                       String alertClass = "alert-info";
                       if (msgType != null && msgType.equals("error")) {
                           alertClass = "alert-danger";
                       } else if (msgType != null && msgType.equals("success")) {
                           alertClass = "alert-success";
                       }
                %>
                <div id="alertMessage" class="alert <%= alertClass %>">
                    <%= msg %>
                    <button type="button" class="btn-close float-end" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <% } %>
            </div>
        </div>


        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>        <!-- Custom JS -->        <script>
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

            // Tự động ẩn thông báo sau 5 giây
            const alertMessage = document.getElementById('alertMessage');
            if (alertMessage) {
                setTimeout(function () {
                    // Tạo hiệu ứng fade out
                    alertMessage.style.transition = 'opacity 1s';
                    alertMessage.style.opacity = '0';

                    // Sau khi fade out hoàn thành, ẩn hoàn toàn phần tử
                    setTimeout(function () {
                        alertMessage.style.display = 'none';
                    }, 1000);
                }, 5000); // Hiển thị trong 5 giây
            }
        </script>
        <jsp:include page="./components/footer.jsp" />

        <!-- JavaScript -->
        <script src="${pageContext.request.contextPath}/js/script.js"></script>
    </body>
</html>




