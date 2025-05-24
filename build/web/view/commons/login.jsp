<%-- 
    Document   : login.jsp
    Created on : May 24, 2025, 5:32:59 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>TODO supply a title</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <%String msg = (String) request.getAttribute("msg");%>
        <form action="login?action=login" method="POST">
            Username: <input type="text" name="user">
            Password: <input type="password" name="password">
            <input type="submit" value="LOGIN">
        </form>
        <%if(msg!= null) {%>
        <div><%= msg%></div>
        <%}%>
    </body>
</html>
