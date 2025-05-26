<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Đổi Email</title>
    <!-- Tailwind CSS CDN -->
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 flex items-center justify-center min-h-screen">
    <div class="bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
        <h1 class="text-2xl font-bold text-center mb-6">Đổi Email</h1>
        
        
        <% if (request.getAttribute("msg") != null) { %>
            <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                <%= request.getAttribute("msg") %>
            </div>
        <% } %>
        
        <
        <form action="verifyCode?action=sendOtp" method="POST" class="space-y-4">
            <input type="hidden" name="type" value="Verify new email">
            <div>
                <label for="email" class="block text-sm font-medium text-gray-700">Email mới:</label>
                <input type="email" id="email" name="email" required
                       class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                       placeholder="Nhập email mới">
            </div>
            <input type="hidden" name="action" value="sendOtp">
            <button type="submit"
                    class="w-full bg-indigo-600 text-white py-2 px-4 rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500">
                Gửi OTP
            </button>
        </form>
        <br>
        <form action="dashboard" method="GET">
           <button type="submit"
                    class="w-full bg-indigo-600 text-white py-2 px-4 rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500">
                Hủy
            </button>
        </form>
        <p class="mt-4 text-center text-sm text-gray-600">
            <a href="/home" class="text-indigo-600 hover:underline">Quay lại trang chủ</a>
        </p>
    </div>
</body>
</html>