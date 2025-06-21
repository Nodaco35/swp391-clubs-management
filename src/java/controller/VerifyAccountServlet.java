package controller;

import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Users;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VerifyAccountServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(VerifyAccountServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        
        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("error", "Token xác minh không hợp lệ!");
            request.getRequestDispatcher("/view/verifyResult.jsp").forward(request, response);
            return;
        }
        
        UserDAO userDAO = new UserDAO();
        Users user = userDAO.getUserByToken(token);
        
        if (user == null) {
            request.setAttribute("error", "Token xác minh không tồn tại hoặc đã hết hạn!");
            request.getRequestDispatcher("/view/verifyResult.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra thời hạn của token
        if (user.getTokenExpiry() == null || new java.util.Date().after(user.getTokenExpiry())) {
            request.setAttribute("error", "Token xác minh đã hết hạn! Vui lòng yêu cầu gửi lại email xác minh.");
            request.setAttribute("email", user.getEmail());
            request.getRequestDispatcher("/view/verifyResult.jsp").forward(request, response);
            return;
        }
          // Kiểm tra xem tài khoản đã được xác minh chưa
        if (user.isStatus()) {
            request.setAttribute("success", "Tài khoản của bạn đã được xác minh trước đó! Bạn có thể đăng nhập ngay bây giờ.");
            request.getRequestDispatcher("/view/verifyResult.jsp").forward(request, response);
            return;
        }

        // Kích hoạt tài khoản
        boolean verified = userDAO.verifyAccount(token);
        if (verified) {
            LOGGER.log(Level.INFO, "Xác minh thành công cho người dùng: {0}", user.getEmail());
            request.setAttribute("success", "Xác minh tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.");
            request.setAttribute("email", user.getEmail());
        } else {
            LOGGER.log(Level.SEVERE, "Xác minh thất bại cho người dùng: {0}", user.getEmail());
            request.setAttribute("error", "Xác minh tài khoản thất bại! Vui lòng thử lại hoặc liên hệ quản trị viên.");
            request.setAttribute("email", user.getEmail());
        }
        
        request.getRequestDispatcher("/view/verifyResult.jsp").forward(request, response);
    }
}
