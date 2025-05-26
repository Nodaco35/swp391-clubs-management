package controllers;

import dao.UserDAO;
import models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.regex.Pattern;

public class LoginServlet extends HttpServlet {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@fpt\\.edu\\.vn$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/Login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Kiểm tra input
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ email và mật khẩu!");
            request.getRequestDispatcher("/view/Login.jsp").forward(request, response);
            return;
        }

        if (!pattern.matcher(email).matches()) {
            request.setAttribute("error", "Email không hợp lệ (phải dùng @fpt.edu.vn)!");
            request.getRequestDispatcher("/view/Login.jsp").forward(request, response);
            return;
        }

        // Xác thực người dùng
        User user = UserDAO.login(email.trim(), password.trim());
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("fullName", user.getFullName());
            session.setAttribute("permissionID", user.getPermissionID());
            session.setMaxInactiveInterval(30 * 60); // 30 phút

            response.sendRedirect("home"); // Hoặc tùy theo giao diện chính của bạn
        } else {
            request.setAttribute("error", "Email hoặc mật khẩu không đúng!");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/view/Login.jsp").forward(request, response);
        }
    }
}
