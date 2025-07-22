<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Đặt lại mật khẩu</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/auth.css">
    <script src="<%=request.getContextPath()%>/js/auth.js"></script>
</head>
<body>
<div class="fp-container">
    <div class="fp-title">Reset password</div>
    <form method="post" action="<%=request.getContextPath()%>/reset-password">
        <input type="hidden" name="token" value="<%= request.getAttribute("token") != null ? request.getAttribute("token") : request.getParameter("token") %>">
        <label for="password" class="fp-label">New password</label>
        <input type="password" id="password" name="password" class="fp-input" required minlength="6">
        <label for="confirm" class="fp-label">Confirm password</label>
        <input type="password" id="confirm" name="confirm" class="fp-input" required minlength="6">
        <button type="submit" class="fp-btn">Reset password</button>
    </form>
    <% if (request.getAttribute("error") != null) { %>
        <div class="fp-error"><%= request.getAttribute("error") %></div>
    <% } %>
    <% if (request.getAttribute("message") != null) { %>
        <div class="fp-message"><%= request.getAttribute("message") %></div>
    <% } %>
    <a href="<%=request.getContextPath()%>/login" class="fp-link">Back to login</a>
</div>
</body>
</html>
