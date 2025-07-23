package controller;

import dal.UserDAO;
import models.Users;
import service.GoogleOAuthService;
import util.GoogleOAuthConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet xử lý Google OAuth2 authentication flow
 * Hỗ trợ:
 * A. Chỉ cho phép email @fpt.edu.vn
 * B. Redirect tới form bổ sung thông tin cho user mới
 * C. Cho bổ sung password 
 * D. Gộp account nếu email đã tồn tại
 * E. Dùng chung session và phân quyền như hiện tại
 */
@WebServlet(name = "GoogleOAuthServlet", urlPatterns = {"/auth/google", "/oauth2callback"})
public class GoogleOAuthServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(GoogleOAuthServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String servletPath = request.getServletPath();
        
        if ("/auth/google".equals(servletPath)) {
            // Bước 1: Bắt đầu OAuth flow - chuyển hướng tới Google
            initiateGoogleAuth(request, response);
        } else if ("/oauth2callback".equals(servletPath)) {
            // Bước 2: Xử lý callback từ Google
            handleGoogleCallback(request, response);
        }
    }
    
    /**
     * Bắt đầu Google OAuth2 flow bằng cách redirect tới Google authorization server
     */
    private void initiateGoogleAuth(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        
        // Kiểm tra configuration trước khi bắt đầu OAuth
        if (!GoogleOAuthConfig.isConfigured()) {
            LOGGER.log(Level.SEVERE, "Google OAuth not configured properly!");
            request.setAttribute("error", "Tính năng đăng nhập Google chưa được cấu hình. Vui lòng liên hệ quản trị viên.");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        HttpSession session = request.getSession();
        
        // Tạo state parameter để bảo vệ CSRF
        String state = GoogleOAuthService.generateState();
        session.setAttribute("oauth_state", state);
        
        // Tạo Google authorization URL
        String authorizationUrl = GoogleOAuthConfig.buildAuthUrl(state);
        
        LOGGER.log(Level.INFO, "Chuyển hướng tới Google OAuth: {0}", authorizationUrl);
        
        // Redirect user tới Google
        response.sendRedirect(authorizationUrl);
    }
    
    /**
     * Xử lý callback từ Google OAuth2 authorization server
     */
    private void handleGoogleCallback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Bước 1: Validate state parameter (bảo vệ CSRF)
        String returnedState = request.getParameter("state");
        String sessionState = (String) session.getAttribute("oauth_state");
        
        if (returnedState == null || !returnedState.equals(sessionState)) {
            LOGGER.log(Level.WARNING, "State parameter không hợp lệ. Có thể bị tấn công CSRF.");
            request.setAttribute("error", "Phiên đăng nhập không hợp lệ. Vui lòng thử lại.");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Dọn dẹp state từ session
        session.removeAttribute("oauth_state");
        
        // Bước 2: Kiểm tra authorization code hoặc error
        String authorizationCode = request.getParameter("code");
        String error = request.getParameter("error");
        
        if (error != null) {
            LOGGER.log(Level.WARNING, "Google OAuth error: {0}", error);
            request.setAttribute("error", "Đăng nhập Google thất bại: " + error);
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        if (authorizationCode == null) {
            LOGGER.log(Level.WARNING, "Không nhận được authorization code từ Google");
            request.setAttribute("error", "Không nhận được mã xác thực từ Google. Vui lòng thử lại.");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Bước 3: Xử lý OAuth callback (đổi code lấy token và thông tin user)
        Users googleUser = GoogleOAuthService.processCallback(authorizationCode);
        
        if (googleUser == null) {
            LOGGER.log(Level.SEVERE, "Lỗi xử lý Google OAuth callback");
            request.setAttribute("error", "Lỗi xử lý đăng nhập Google. Vui lòng thử lại.");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Bước 4: Validate email domain (A. Chỉ cho phép email @fpt.edu.vn)
        if (!googleUser.getEmail().endsWith("@fpt.edu.vn")) {
            LOGGER.log(Level.WARNING, "Email domain không hợp lệ: {0}", googleUser.getEmail());
            request.setAttribute("error", "Chỉ được phép sử dụng email @fpt.edu.vn để đăng nhập.");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Bước 5: Kiểm tra user đã tồn tại trong database chưa
        Users existingUser = UserDAO.getUserByEmail(googleUser.getEmail());
        
        if (existingUser != null) {
            // D. Gộp account - user đã tồn tại, đăng nhập luôn
            loginExistingUser(existingUser, request, response);
        } else {
            // B. User mới - redirect tới form bổ sung thông tin
            redirectToCompleteRegistration(googleUser, request, response);
        }
    }
    
    /**
     * E. Đăng nhập user hiện có với Google account - dùng chung session như logic hiện tại
     */
    private void loginExistingUser(Users user, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession();
        
        // Tạo session attributes (giống như trong LoginServlet)
        session.setAttribute("user", user);
        session.setAttribute("fullName", user.getFullName());
        session.setAttribute("permissionID", user.getPermissionID());
        session.setAttribute("userID", user.getUserID());
        
        session.setMaxInactiveInterval(30 * 60); // 30 phút
        
        // Lấy thông tin bổ sung cho session
        List<Integer> myEventIDs = userDAO.getEventIDsOfChairman(user.getUserID());
        int myClubID = userDAO.getClubIdIfChairman(user.getUserID());
        boolean isChairman = userDAO.isChairman(user.getUserID(), myClubID);
        session.setAttribute("myClubID", myClubID);
        session.setAttribute("myEventIDs", myEventIDs);
        session.setAttribute("isChairman", isChairman);
        
        LOGGER.log(Level.INFO, "Đăng nhập Google thành công cho user hiện có: {0}", user.getEmail());
        
        // E. Redirect theo phân quyền (giống như trong LoginServlet)
        if (user.getPermissionID() == 1) {
            // Student - chuyển về trang chủ
            response.sendRedirect(request.getContextPath() + "/");
        } else if (user.getPermissionID() == 2) {
            // Admin
            response.sendRedirect(request.getContextPath() + "/admin");
        } else if (user.getPermissionID() == 3) {
            // IC Officer
            response.sendRedirect(request.getContextPath() + "/ic");
        } else {
            // Default
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
    
    /**
     * B. Redirect user Google mới tới form hoàn tất đăng ký
     */
    private void redirectToCompleteRegistration(Users googleUser, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Lưu thông tin Google user vào session cho form đăng ký
        session.setAttribute("googleUserProfile", googleUser);
        
        LOGGER.log(Level.INFO, "User Google mới, chuyển hướng tới form đăng ký: {0}", googleUser.getEmail());
        
        // Redirect tới servlet xử lý form đăng ký Google
        response.sendRedirect(request.getContextPath() + "/auth/google/register");
    }
}
