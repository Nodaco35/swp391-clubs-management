<%-- 
    Document   : verifyCode.jsp
    Created on : May 23, 2025, 1:13:03 AM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="models.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Xác minh OTP</title>
    <!-- Tailwind CSS CDN -->
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 flex items-center justify-center min-h-screen">
    <div class="bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
        <h1 class="text-2xl font-bold text-center mb-6">Xác minh OTP</h1>

        <% String msg = (String) request.getAttribute("msg");
           if (msg != null) { %>
            <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                <%= msg %>
            </div>
        <% } %>

        <% String type = (String) session.getAttribute("type");
           String email = (String) session.getAttribute("otpEmail");
           User user = (User) session.getAttribute("user");
           if (type != null && type.equals("Verify current email")) { %>
            <p class="text-center text-gray-600 mb-4">Mã xác minh đã được gửi đến: <strong><%= email %></strong></p>
            <form action="verifyCode?action=confirmOtp" method="POST" class="space-y-4">
                <div>
                    <label for="otp" class="block text-sm font-medium text-gray-700">Mã OTP:</label>
                    <input type="text" id="otp" name="otp" required
                           class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                           placeholder="Nhập mã OTP">
                </div>
                <button type="submit"
                        class="w-full bg-indigo-600 text-white py-2 px-4 rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500">
                    Xác nhận OTP
                </button>
            </form>

        <% } else if (type != null && type.equals("Verify new email")) { %>
            <p class="text-center text-gray-600 mb-4">Mã xác minh đã được gửi đến email mới: <strong><%= email %></strong></p>
            <form action="verifyCode?action=confirmOtp" method="POST" class="space-y-4">
                <input type="hidden" name="id" value="<%= user.getUserID() %>">
                <input type="hidden" name="email" value="<%= email %>">
                <div>
                    <label for="otp" class="block text-sm font-medium text-gray-700">Mã OTP:</label>
                    <input type="text" id="otp" name="otp" required
                           class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                           placeholder="Nhập mã OTP">
                </div>
                <button type="submit"
                        class="w-full bg-indigo-600 text-white py-2 px-4 rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500">
                    Xác nhận OTP
                </button>
            </form>
        <% } %>

        <!-- Nút gửi lại mã OTP -->
        <form id="resendForm" action="verifyCode?action=resendOtp" method="POST" class="mt-4">
            <button type="submit" id="resendBtn" disabled
                    class="w-full bg-gray-400 text-white py-2 px-4 rounded-md hover:bg-gray-500 focus:outline-none focus:ring-2 focus:ring-gray-400">
                Gửi lại mã
            </button>
        </form>

        <!-- Liên kết hủy -->
        <p class="mt-4 text-center text-sm text-gray-600">
            <a href="${pageContext.request.contextPath}/" class="text-indigo-600 hover:underline">Hủy</a>
        </p>
    </div>

    <!-- Script đếm ngược -->
    <script>
        let countdown = 59;
        const resendBtn = document.getElementById('resendBtn');

        const timer = setInterval(() => {
            if (countdown <= 0) {
                resendBtn.disabled = false;
                resendBtn.classList.remove('bg-gray-400');
                resendBtn.classList.add('bg-indigo-600', 'hover:bg-indigo-700');
                resendBtn.value = 'Gửi lại mã';
                clearInterval(timer);
            } else {
                resendBtn.value = `Gửi lại mã (${countdown}s)`;
                countdown--;
            }
        }, 1000);
    </script>
</body>
</html>