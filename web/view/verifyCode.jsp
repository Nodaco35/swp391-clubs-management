<%-- 
    Document   : verifyCode.jsp
    Created on : May 23, 2025, 1:13:03 AM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Verify Code</title>
    </head>
    <body>
        <form action="verifyCode?action=comfirmOtp" method="POST">
            <input type="text" name="otp" required>
            <input type="submit" value="Comfirm OTP">
        </form>
        
        <form action="verifyCode?action=resendOtp" method="POST">
            
            
        </form>
        
    </body>
</html>
