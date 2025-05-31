package controllers;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.User;
import util.EmailService;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResendVerificationServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(ResendVerificationServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Email không được để trống!");
            request.getRequestDispatcher("/view/login.jsp").forward(request, response);
            return;
        }        User user = UserDAO.getUserByEmail(email.trim());
        
        if (user == null) {
            request.setAttribute("error", "Email không tồn tại trong hệ thống!");
            request.getRequestDispatcher("/view/login.jsp").forward(request, response);
            return;
        }
        
        // Nếu tài khoản đã được kích hoạt
        if (user.isStatus()) {
            request.setAttribute("info", "Tài khoản của bạn đã được kích hoạt. Bạn có thể đăng nhập bình thường.");
            request.getRequestDispatcher("/view/login.jsp").forward(request, response);
            return;
        }
          // Tạo token mới
        String verificationToken = EmailService.generateVerificationToken();
        Date tokenExpiry = EmailService.generateTokenExpiryTime();
        
        // Cập nhật token vào database
        boolean tokenUpdated = UserDAO.updateVerificationToken(email.trim(), verificationToken, tokenExpiry);
        
        if (tokenUpdated) {
            // Gửi lại email xác minh
            boolean emailSent = EmailService.sendRegistrationEmail(
                email.trim(), 
                user.getFullName(), 
                verificationToken, 
                request.getContextPath()
            );
            
            if (emailSent) {
                LOGGER.log(Level.INFO, "Đã gửi lại email xác nhận đến: {0}", email);
                request.setAttribute("success", "Chúng tôi đã gửi lại email xác nhận đến " + email + ". Vui lòng kiểm tra hộp thư của bạn.");
            } else {
                LOGGER.log(Level.WARNING, "Không thể gửi lại email xác nhận đến: {0}", email);
                request.setAttribute("error", "Không thể gửi lại email xác nhận. Vui lòng thử lại sau hoặc liên hệ quản trị viên.");
            }
        } else {
            request.setAttribute("error", "Không thể tạo mới token xác minh. Vui lòng thử lại sau.");
        }
        
        request.getRequestDispatcher("/view/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
