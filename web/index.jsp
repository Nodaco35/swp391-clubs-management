<%-- 
    Document   : index
    Created on : Mar 1, 2025, 6:04:34 PM
    Author     : he181
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <% response.sendRedirect(request.getContextPath() + "/login"); %>
    </body>
</html>
