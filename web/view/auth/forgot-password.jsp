<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Forgot password</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/auth.css">
    <script src="<%=request.getContextPath()%>/js/auth.js"></script>
</head>
<body>
<div style="min-height: 80vh; display: flex; align-items: center; justify-content: center;">
    <div class="fp-container">
        <div class="fp-title">Forgot password</div>
        <div class="fp-sub">Remember your password? <a href='<%=request.getContextPath()%>/login'>Login here</a></div>
        <form method="post" action="<%=request.getContextPath()%>/forgot-password">
            <label for="email" class="fp-label">Email address</label>
            <input type="email" id="email" name="email" class="fp-input" required autocomplete="email">
            <button type="submit" class="fp-btn">Reset password</button>
        </form>
        <% if (request.getAttribute("error") != null) { %>
            <div class="fp-error"><%= request.getAttribute("error") %></div>
        <% } %>
        <% if (request.getAttribute("message") != null) { %>
            <div class="fp-message"><%= request.getAttribute("message") %></div>
        <% } %>
    </div>
</div>
</body>
</html>
