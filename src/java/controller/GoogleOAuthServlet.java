package controller;

import dal.UserDAO;
import models.GoogleUserInfo;
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
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet to handle Google OAuth2 authentication flow
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
            // Step 1: Initiate OAuth flow - redirect to Google
            initiateGoogleAuth(request, response);
        } else if ("/oauth2callback".equals(servletPath)) {
            // Step 2: Handle callback from Google
            handleGoogleCallback(request, response);
        }
    }
    
    /**
     * Initiate Google OAuth2 flow by redirecting to Google authorization server
     */
    private void initiateGoogleAuth(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession();
        
        // Generate state parameter for CSRF protection
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth_state", state);
        
        // Build Google authorization URL
        String authorizationUrl = GoogleOAuthConfig.buildAuthorizationUrl(state);
        
        LOGGER.log(Level.INFO, "Redirecting to Google OAuth: {0}", authorizationUrl);
        
        // Redirect user to Google
        response.sendRedirect(authorizationUrl);
    }
    
    /**
     * Handle callback from Google OAuth2 authorization server
     */
    private void handleGoogleCallback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Step 1: Validate state parameter (CSRF protection)
        String returnedState = request.getParameter("state");
        String sessionState = (String) session.getAttribute("oauth_state");
        
        if (returnedState == null || !returnedState.equals(sessionState)) {
            LOGGER.log(Level.WARNING, "Invalid state parameter. Possible CSRF attack.");
            request.setAttribute("error", "Phiên đăng nhập không hợp lệ. Vui lòng thử lại.");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Clean up state from session
        session.removeAttribute("oauth_state");
        
        // Step 2: Check for authorization code or error
        String authorizationCode = request.getParameter("code");
        String error = request.getParameter("error");
        
        if (error != null) {
            LOGGER.log(Level.WARNING, "Google OAuth error: {0}", error);
            request.setAttribute("error", "Đăng nhập Google thất bại: " + error);
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        if (authorizationCode == null) {
            LOGGER.log(Level.WARNING, "No authorization code received from Google");
            request.setAttribute("error", "Không nhận được mã xác thực từ Google. Vui lòng thử lại.");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Step 3: Process OAuth callback (exchange code for token and get user info)
        GoogleUserInfo googleUser = GoogleOAuthService.processOAuthCallback(authorizationCode);
        
        if (googleUser == null) {
            LOGGER.log(Level.SEVERE, "Failed to process Google OAuth callback");
            request.setAttribute("error", "Lỗi xử lý đăng nhập Google. Vui lòng thử lại.");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Step 4: Validate email domain (already checked in service, but double-check)
        if (!googleUser.isValidDomain()) {
            LOGGER.log(Level.WARNING, "Invalid email domain: {0}", googleUser.getEmail());
            request.setAttribute("error", "Chỉ cho phép đăng nhập bằng email @fpt.edu.vn");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Step 5: Check if user already exists in database
        Users existingUser = UserDAO.getUserByEmail(googleUser.getEmail());
        
        if (existingUser != null) {
            // User exists - login directly
            loginExistingUser(existingUser, request, response);
        } else {
            // New user - redirect to complete registration
            redirectToCompleteRegistration(googleUser, request, response);
        }
    }
    
    /**
     * Login existing user who has Google account
     */
    private void loginExistingUser(Users user, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession();
        
        // Create session attributes (similar to LoginServlet logic)
        session.setAttribute("user", user);
        session.setAttribute("fullName", user.getFullName());
        session.setAttribute("permissionID", user.getPermissionID());
        session.setAttribute("userID", user.getUserID());
        session.setMaxInactiveInterval(30 * 60); // 30 minutes
        
        // Get additional user info for session
        List<Integer> myEventIDs = userDAO.getEventIDsOfChairman(user.getUserID());
        int myClubID = userDAO.getClubIdIfChairman(user.getUserID());
        boolean isChairman = userDAO.isChairman(user.getUserID(), myClubID);
        session.setAttribute("myClubID", myClubID);
        session.setAttribute("myEventIDs", myEventIDs);
        session.setAttribute("isChairman", isChairman);
        
        LOGGER.log(Level.INFO, "Google login successful for existing user: {0}", user.getEmail());
        
        // Redirect based on permission level (same logic as LoginServlet)
        if (user.getPermissionID() == 1) {
            // Student
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
     * Redirect new Google user to complete registration form
     */
    private void redirectToCompleteRegistration(GoogleUserInfo googleUser, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Store Google user info in session for the registration form
        session.setAttribute("googleUserInfo", googleUser);
        
        LOGGER.log(Level.INFO, "New Google user, redirecting to registration: {0}", googleUser.getEmail());
        
        // Redirect to Google registration completion form
        response.sendRedirect(request.getContextPath() + "/auth/google/register");
    }
}
