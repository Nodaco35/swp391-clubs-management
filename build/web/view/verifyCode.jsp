<%-- 
    Document   : verifyCode.jsp
    Created on : May 23, 2025, 1:13:03 AM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="models.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Verify Code</title>
    </head>
    <body>

        <%String type = (String) session.getAttribute("type");
        String email = (String) session.getAttribute("otpEmail");
        User user = (User) session.getAttribute("user");
        if(type.equals("Verify current email")){
        %>
        <div>Mã xác minh được gửi đến gmail: <%= email%></div>
        <form action="verifyCode?action=confirmOtp" method="POST">
            <input type="text" name="otp" required>
            <input type="submit" value="Comfirm OTP">
        </form>


        <% }else if(type.equals("Verify new email")) {%>

        <div>Mã xác minh được gửi đến gmail mới: <%= email%></div>
        <form action="verifyCode?action=confirmOtp" method="POST">
            <input type="hidden" name="id" value="<%= user.getUserID()%>">
            <input type="hidden" name="email" value="<%= email%>">
            <input type="text" name="otp" required>
            <input type="submit" value="Confirm OTP">
        </form>
        <%}%>
        <!-- Nút gửi lại mã OTP và đếm ngược -->
        <form id="resendForm" action="verifyCode?action=resendOtp" method="POST">
            <input type="submit" id="resendBtn" value="Gửi lại mã" disabled>
        </form>

    </body>
    <% String msg = (String) request.getAttribute("msg");
        if(msg != null){
    %>
    <div><%= msg%></div>
    <%
    }
    %>
    <script>
        let countdown = 59;
        const resendBtn = document.getElementById('resendBtn');

        const timer = setInterval(() => {
            if (countdown <= 0) {
                resendBtn.disabled = false;
                resendBtn.value = "Gửi lại mã";
                clearInterval(timer);
            } else {
                resendBtn.value = `Gửi lại mã (${countdown}s)`;
                countdown--;
            }
        }, 1000);
    </script>
</html>
