<%-- 
    Document   : profileSlidebar
    Created on : Jul 9, 2025, 8:55:48 PM
    Author     : NC PC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <div class="sidebar">
            <div style="padding-top: 70px"></div>
            <ul class="nav-list" id="sidebar-nav">
                <li><a href="${pageContext.request.contextPath}/profile?action=myProfile">Tài khoản</a></li>
                <li><a href="${pageContext.request.contextPath}/student?action=history">Lịch sử hoạt động</a></li>
            </ul>
        </div>

        <script>
            window.addEventListener("DOMContentLoaded", function () {
                const currentUrl = window.location.href;
                const navLinks = document.querySelectorAll("#sidebar-nav a");

                navLinks.forEach(link => {
                    const href = link.href;

                    // Nếu URL hiện tại chứa đường dẫn của link
                    if (currentUrl.includes(href)) {
                        link.parentElement.classList.add("active");
                    }
                });
            });
        </script>

    </body>
</html>
