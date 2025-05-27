<%-- 
    Document   : notification
    Created on : May 27, 2025, 3:10:11 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <% String dob = (String) request.getAttribute("date");%>
        <p><%= dob%></p>
    </body>
</html>
