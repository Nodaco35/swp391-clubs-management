<%-- 
    Document   : homePage
    Created on : May 19, 2025, 9:54:32 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page  import="models.User"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <% 
        User user = (User) session.getAttribute("user");
        if (user != null) {
        %>
        <div>
            <%= user.getFullName() %>
        </div>
        <% 
            } else {
        %>
        <div>
            No user found.
        </div>
        <% 
            }
        %>

        <form action="home" method="GET">
            <input type="hidden" name="action" value="profile">
            <input type="submit" value="Show Profile">
        </form>
    </body>
</html>
