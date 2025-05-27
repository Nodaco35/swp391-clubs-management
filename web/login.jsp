<%--
  Created by IntelliJ IDEA.
  User: thuan
  Date: 5/22/2025
  Time: 4:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
	<title>Login</title>
	<meta charset="UTF-8">
</head>
<body>
<form action="login" method="post">
	<h3 style="color: red">${requestScope.error}</h3>
	Username: <input type="text" name="email">
	<br>
	Password: <input type="password" name="password">
	<br>
	<input type="submit" value="Login">
</form>
</body>
</html>
