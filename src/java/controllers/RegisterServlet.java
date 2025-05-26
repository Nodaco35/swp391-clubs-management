package controllers;

import dao.UserDAO;
import models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class RegisterServlet extends HttpServlet {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@fpt\\.edu\\.vn$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/Register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName");
        String dobStr = request.getParameter("dateOfBirth");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        String error = validateInput(fullName, email, password, confirmPassword, dobStr);
        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("dateOfBirth", dobStr);
            request.getRequestDispatcher("/view/Register.jsp").forward(request, response);
            return;
        }

        if (UserDAO.getUserByEmail(email.trim()) != null) {
            request.setAttribute("error", "Email này đã được sử dụng!");
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("dateOfBirth", dobStr);
            request.getRequestDispatcher("/view/Register.jsp").forward(request, response);
            return;
        }

        // Chuyển đổi ngày sinh sang java.sql.Timestamp
        Timestamp dateOfBirth = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            long time = sdf.parse(dobStr).getTime();
            dateOfBirth = new Timestamp(time);
        } catch (ParseException e) {
            e.printStackTrace(); // Hoặc log lỗi
        }

        User newUser = new User();
        newUser.setFullName(fullName.trim());
        newUser.setEmail(email.trim().toLowerCase());
        newUser.setPassword(password.trim());
        newUser.setDateOfBirth(dateOfBirth); // <-- thêm ngày sinh
        newUser.setPermissionID(1); // Default: Student
        newUser.setStatus(true);    // Active

        boolean success = UserDAO.register(newUser);
        if (success) {
            HttpSession session = request.getSession();
            session.setAttribute("user", newUser);
            session.setAttribute("permissionID", newUser.getPermissionID());
            session.setMaxInactiveInterval(30 * 60); // 30 phút

            int permissionID = newUser.getPermissionID();
            if (permissionID == 1) {
                response.sendRedirect(request.getContextPath() + "/home");
            } else {
                response.sendRedirect(request.getContextPath() + "/view/dashboard.jsp");
            }
        } else {
            request.setAttribute("error", "Đăng ký thất bại! Vui lòng thử lại.");
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("dateOfBirth", dobStr);
            request.getRequestDispatcher("/view/Register.jsp").forward(request, response);
        }
    }

    private String validateInput(String fullName, String email, String password, String confirmPassword, String dobStr) {
        if (fullName == null || fullName.trim().length() < 2 || fullName.trim().length() > 100) {
            return "Họ tên phải từ 2 đến 100 ký tự!";
        }

        if (email == null || email.trim().isEmpty()) {
            return "Vui lòng nhập email!";
        }
        if (email.trim().length() > 255 || !pattern.matcher(email.trim()).matches()) {
            return "Vui lòng sử dụng email@fpt.edu.vn";
        }

        if (password == null || password.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự!";
        }

        if (!password.equals(confirmPassword)) {
            return "Xác nhận mật khẩu không khớp!";
        }

        if (dobStr == null || dobStr.isEmpty()) {
            return "Vui lòng nhập ngày sinh!";
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            sdf.parse(dobStr);
        } catch (ParseException e) {
            return "Ngày sinh không hợp lệ! (định dạng yyyy-MM-dd)";
        }

        return null;
    }
}
