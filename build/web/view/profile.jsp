<%-- 
    Document   : profile
    Created on : May 19, 2025, 10:28:26 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="models.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Profile Page</title>
    </head>
    <body>
        <% 
            User user = (User) session.getAttribute("user"); 
            if (user == null) {
                response.sendRedirect("index.jsp");
                return;
            }
        %>

        <h1>Thông tin cá nhân</h1>

        <form action="home?action=update" method="POST">
            <input type="hidden" name="id" value="<%= user.getUserID()%>">
            <div>
                <label>Họ và tên:</label>
                <input type="text" name="name" value="<%= user.getFullName() %>">
            </div>
            <div>
                <label>Email:</label>
                <input type="text" name="email" value="<%= user.getEmail() %>">
            </div>
            <div>
                <label></label>
                <input type="submit"  value="Cập nhập thông tin" >
            </div>
            

        </form>
           <% String msg = (String) request.getAttribute("msg");
              if(msg != null){
           %>
           <p><%= msg%></p>
           <%}%>
           
           


    </body>
</html>