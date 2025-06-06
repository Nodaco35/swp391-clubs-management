<%-- 
    Document   : eventsPage
    Created on : May 20, 2025, 12:47:15 PM
    Author     : LE VAN THUAN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="../../css/eventsPage.css"/>
        <title>JSP Page</title>
    </head>
    <body>

    <footer class="footer">
        <div class="container footer-container">
            <div class="footer-logo">
                <i class="fas fa-users"></i>
                <span>UniClub</span>
            </div>
            <p class="copyright">
                © 2023 UniClub. Tất cả các quyền được bảo lưu.
            </p>
            <div class="footer-links">
                <a href="${pageContext.request.contextPath}/terms">Điều Khoản</a>
                <a href="${pageContext.request.contextPath}/privacy">Chính Sách</a>
                <a href="${pageContext.request.contextPath}/contact">Liên Hệ</a>
            </div>
        </div>
    </footer>
    </body>
</html>
