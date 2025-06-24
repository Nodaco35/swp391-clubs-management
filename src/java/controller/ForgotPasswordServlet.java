package controller;

import dal.UserDAO;
import models.Users;
import util.EmailService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password", "/reset-password"})
public class ForgotPasswordServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.endsWith("/forgot-password")) {
            request.getRequestDispatcher("/view/auth/forgot-password.jsp").forward(request, response);
        } else if (uri.endsWith("/reset-password")) {
            String token = request.getParameter("token");
            if (token == null || token.isEmpty()) {
                request.setAttribute("error", "Token không hợp lệ!");
                request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
                return;
            }
            UserDAO userDAO = new UserDAO();
            Users user = userDAO.getUserByToken(token);
            if (user == null || user.getTokenExpiry() == null || new Date().after(user.getTokenExpiry())) {
                request.setAttribute("error", "Liên kết đặt lại mật khẩu đã hết hạn hoặc không hợp lệ!");
            } else {
                request.setAttribute("token", token);
            }
            request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.endsWith("/forgot-password")) {
            String email = request.getParameter("email");
            UserDAO userDAO = new UserDAO();
            Users user = UserDAO.getUserByEmail(email);
            if (user == null) {
                request.setAttribute("error", "Email không tồn tại trong hệ thống!");
                request.getRequestDispatcher("/view/auth/forgot-password.jsp").forward(request, response);
                return;
            }
            String token = EmailService.generateVerificationToken();
            Date expiry = EmailService.generateTokenExpiryTime();
            // Lưu token và thời gian hết hạn vào DB
            String sql = "UPDATE Users SET ResetToken = ?, TokenExpiry = ? WHERE Email = ?";
            try {
                java.sql.Connection conn = dal.DBContext.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, token);
                ps.setTimestamp(2, new java.sql.Timestamp(expiry.getTime()));
                ps.setString(3, email);
                ps.executeUpdate();
                ps.close();
                conn.close();
            } catch (Exception e) {
                request.setAttribute("error", "Lỗi hệ thống, vui lòng thử lại sau!");
                request.getRequestDispatcher("/view/auth/forgot-password.jsp").forward(request, response);
                return;
            }
            // Gửi email
            boolean sent = EmailService.sendResetPasswordEmail(user.getEmail(), user.getFullName(), token, request.getContextPath());
            if (sent) {
                request.setAttribute("message", "Đã gửi email hướng dẫn đặt lại mật khẩu. Vui lòng kiểm tra hộp thư!");
            } else {
                request.setAttribute("error", "Không thể gửi email. Vui lòng thử lại sau!");
            }
            request.getRequestDispatcher("/view/auth/forgot-password.jsp").forward(request, response);
        } else if (uri.endsWith("/reset-password")) {
            String token = request.getParameter("token");
            String password = request.getParameter("password");
            String confirm = request.getParameter("confirm");
            if (token == null || token.isEmpty()) {
                request.setAttribute("error", "Token không hợp lệ!");
                request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
                return;
            }
            if (password == null || password.length() < 6 || !password.equals(confirm)) {
                request.setAttribute("error", "Mật khẩu phải từ 6 ký tự và trùng khớp!");
                request.setAttribute("token", token);
                request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
                return;
            }
            UserDAO userDAO = new UserDAO();
            Users user = userDAO.getUserByToken(token);
            if (user == null || user.getTokenExpiry() == null || new Date().after(user.getTokenExpiry())) {
                request.setAttribute("error", "Liên kết đặt lại mật khẩu đã hết hạn hoặc không hợp lệ!");
                request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
                return;
            }
            // Đổi mật khẩu và xóa token
            boolean changed = UserDAO.changePassword(user.getUserID(), password);
            if (changed) {
                // Xóa token
                String sql = "UPDATE Users SET ResetToken = NULL, TokenExpiry = NULL WHERE UserID = ?";
                try {
                    java.sql.Connection conn = dal.DBContext.getConnection();
                    java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, user.getUserID());
                    ps.executeUpdate();
                    ps.close();
                    conn.close();
                } catch (Exception e) { /* ignore */ }
                request.setAttribute("message", "Đặt lại mật khẩu thành công! Bạn có thể đăng nhập với mật khẩu mới.");
            } else {
                request.setAttribute("error", "Không thể đặt lại mật khẩu. Vui lòng thử lại!");
                request.setAttribute("token", token);
            }
            request.getRequestDispatcher("/view/auth/reset-password.jsp").forward(request, response);
        }
    }
}
