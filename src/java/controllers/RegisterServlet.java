package controllers;

import dao.UserDAO;
import models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import util.EmailService;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterServlet extends HttpServlet {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@fpt\\.edu\\.vn$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/register.jsp").forward(request, response);
    }    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String dateOfBirthStr = request.getParameter("dateOfBirth");
        String error = validateInput(fullName, email, password, confirmPassword, dateOfBirthStr);
        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("dateOfBirth", dateOfBirthStr);
            request.getRequestDispatcher("/view/register.jsp").forward(request, response);
            return;
        }
        UserDAO ud = new UserDAO();
        if (ud.getUserByEmail(email.trim()) != null) {
            request.setAttribute("error", "Email này đã được sử dụng!");
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("dateOfBirth", dateOfBirthStr);
            request.getRequestDispatcher("/view/register.jsp").forward(request, response);
            return;
        }

        User newUser = new User();
        newUser.setFullName(fullName.trim());
        newUser.setEmail(email.trim().toLowerCase());
        newUser.setPassword(password.trim());      
        // Xử lý ngày sinh
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date dateOfBirth = sdf.parse(dateOfBirthStr);
            newUser.setDateOfBirth(dateOfBirth);
        } catch (ParseException e) {
            request.setAttribute("error", "Ngày sinh không hợp lệ!");
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("dateOfBirth", dateOfBirthStr);
            request.getRequestDispatcher("/view/register.jsp").forward(request, response);
            return;
        }        newUser.setPermissionID(1); // Default: Student
        newUser.setStatus(false);   // Inactive until email verification
        LOGGER.log(Level.INFO, "Đang cố gắng đăng ký user: {0}", newUser.getEmail());
        boolean success = ud.register(newUser);
        LOGGER.log(Level.INFO, "Kết quả đăng ký: {0}", success ? "Thành công" : "Thất bại");
        if (success) {
            // Tạo token xác minh và thời gian hết hạn
            String verificationToken = EmailService.generateVerificationToken();
            java.util.Date tokenExpiry = EmailService.generateTokenExpiryTime();
            // Lưu token vào database và đánh dấu tài khoản chưa kích hoạt
            boolean tokenUpdated = ud.updateVerificationToken(email.trim(), verificationToken, tokenExpiry);
            
            if (tokenUpdated) {
                // Gửi email xác minh với token
                boolean emailSent = EmailService.sendRegistrationEmail(
                    email.trim(), 
                    fullName.trim(), 
                    verificationToken, 
                    request.getContextPath()
                );
                  if (emailSent) {
                    request.setAttribute("success", "Đăng ký thành công! Chúng tôi đã gửi một email xác nhận đến " + email + ". Vui lòng kiểm tra hộp thư và nhấp vào liên kết để kích hoạt tài khoản của bạn.");
                    LOGGER.log(Level.INFO, "Đã gửi email xác nhận đến: {0}", email);
                } else {
                    request.setAttribute("warning", "Tài khoản đã được tạo nhưng không thể gửi email xác nhận. Vui lòng liên hệ quản trị viên.");
                    LOGGER.log(Level.WARNING, "Không thể gửi email xác nhận đến: {0}", email);
                }
            } else {
                request.setAttribute("warning", "Đăng ký thành công nhưng không thể tạo token xác minh. Vui lòng liên hệ quản trị viên.");
            }
                  // Chuyển đến trang đăng nhập thay vì tự động đăng nhập
            request.getRequestDispatcher("/view/login.jsp").forward(request, response);
        } else {
            LOGGER.log(Level.SEVERE, "Đăng ký thất bại cho email: {0}", email);
            request.setAttribute("error", "Đăng ký thất bại! Vui lòng thử lại.");
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("dateOfBirth", dateOfBirthStr);
            request.getRequestDispatcher("/view/register.jsp").forward(request, response);
        }
    }    private String validateInput(String fullName, String email, String password, String confirmPassword, String dateOfBirth) {
        if (fullName == null || fullName.trim().length() < 2 || fullName.trim().length() > 100) {
            return "Họ tên phải từ 2 đến 100 ký tự!";
        }
        if (email == null || email.trim().isEmpty()) {
            return "Vui lòng nhập email!";
        }
        if (email.trim().length() > 255 || !pattern.matcher(email.trim()).matches()) {
            return "Vui lòng sử dụng email@fpt.edu.vn";
        }
        if (dateOfBirth == null || dateOfBirth.trim().isEmpty()) {
            return "Vui lòng nhập ngày sinh!";
        }
        // Kiểm tra định dạng ngày sinh yyyy-MM-dd
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(dateOfBirth);
        } catch (ParseException e) {
            return "Ngày sinh không hợp lệ! (định dạng yyyy-MM-dd)";
        }
        if (password == null || password.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự!";
        }
        if (!password.equals(confirmPassword)) {
            return "Xác nhận mật khẩu không khớp!";
        }
        return null;
    }
}
