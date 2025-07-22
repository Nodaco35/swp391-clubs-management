package controller;

import dal.UserDAO;
import models.GoogleUserInfo;
import models.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet to handle Google user registration completion
 */
@WebServlet(name = "GoogleRegistrationServlet", urlPatterns = {"/auth/google/register"})
public class GoogleRegistrationServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(GoogleRegistrationServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        GoogleUserInfo googleUserInfo = (GoogleUserInfo) session.getAttribute("googleUserInfo");
        
        // Check if we have Google user info in session
        if (googleUserInfo == null) {
            LOGGER.log(Level.WARNING, "No Google user info in session");
            request.setAttribute("error", "Phiên đăng nhập Google đã hết hạn. Vui lòng thử lại.");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Pre-fill form with Google user info
        request.setAttribute("googleEmail", googleUserInfo.getEmail());
        request.setAttribute("googleName", googleUserInfo.getName());
        request.setAttribute("googlePicture", googleUserInfo.getPicture());
        
        // Forward to Google registration completion form
        request.getRequestDispatcher("/view/auth/google-register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        GoogleUserInfo googleUserInfo = (GoogleUserInfo) session.getAttribute("googleUserInfo");
        
        // Check if we have Google user info in session
        if (googleUserInfo == null) {
            LOGGER.log(Level.WARNING, "No Google user info in session during registration");
            request.setAttribute("error", "Phiên đăng nhập Google đã hết hạn. Vui lòng thử lại.");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Get form data
        String fullName = request.getParameter("fullName");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String dateOfBirthStr = request.getParameter("dateOfBirth");
        
        // Validate input
        String validationError = validateRegistrationInput(fullName, password, confirmPassword, dateOfBirthStr);
        if (validationError != null) {
            request.setAttribute("error", validationError);
            request.setAttribute("fullName", fullName);
            request.setAttribute("dateOfBirth", dateOfBirthStr);
            request.setAttribute("googleEmail", googleUserInfo.getEmail());
            request.setAttribute("googleName", googleUserInfo.getName());
            request.setAttribute("googlePicture", googleUserInfo.getPicture());
            request.getRequestDispatcher("/view/auth/google-register.jsp").forward(request, response);
            return;
        }
        
        // Double-check email doesn't exist (race condition protection)
        Users existingUser = UserDAO.getUserByEmail(googleUserInfo.getEmail());
        if (existingUser != null) {
            LOGGER.log(Level.WARNING, "User already exists during Google registration: {0}", googleUserInfo.getEmail());
            request.setAttribute("error", "Tài khoản với email này đã tồn tại!");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Create new user
        Users newUser = new Users();
        newUser.setFullName(fullName.trim());
        newUser.setEmail(googleUserInfo.getEmail());
        newUser.setPassword(password.trim());
        
        // Parse date of birth
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date dateOfBirth = sdf.parse(dateOfBirthStr);
            newUser.setDateOfBirth(dateOfBirth);
        } catch (ParseException e) {
            request.setAttribute("error", "Ngày sinh không hợp lệ!");
            request.setAttribute("fullName", fullName);
            request.setAttribute("dateOfBirth", dateOfBirthStr);
            request.setAttribute("googleEmail", googleUserInfo.getEmail());
            request.setAttribute("googleName", googleUserInfo.getName());
            request.setAttribute("googlePicture", googleUserInfo.getPicture());
            request.getRequestDispatcher("/view/auth/google-register.jsp").forward(request, response);
            return;
        }
        
        // Set default user properties
        newUser.setPermissionID(1); // Default: Student
        newUser.setStatus(true);    // Google users are automatically verified
        
        // Set avatar from Google if available
        if (googleUserInfo.getPicture() != null && !googleUserInfo.getPicture().isEmpty()) {
            newUser.setAvatar(googleUserInfo.getPicture());
        }
        
        LOGGER.log(Level.INFO, "Creating new Google user: {0}", newUser.getEmail());
        
        // Save to database
        boolean success = userDAO.register(newUser);
        if (success) {
            // Registration successful - clean up session and login
            session.removeAttribute("googleUserInfo");
            
            // Get the created user from database to get UserID
            Users createdUser = UserDAO.getUserByEmail(newUser.getEmail());
            if (createdUser != null) {
                // Create login session (same logic as LoginServlet)
                session.setAttribute("user", createdUser);
                session.setAttribute("fullName", createdUser.getFullName());
                session.setAttribute("permissionID", createdUser.getPermissionID());
                session.setAttribute("userID", createdUser.getUserID());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                
                // Get additional user info for session
                List<Integer> myEventIDs = userDAO.getEventIDsOfChairman(createdUser.getUserID());
                int myClubID = userDAO.getClubIdIfChairman(createdUser.getUserID());
                boolean isChairman = userDAO.isChairman(createdUser.getUserID(), myClubID);
                session.setAttribute("myClubID", myClubID);
                session.setAttribute("myEventIDs", myEventIDs);
                session.setAttribute("isChairman", isChairman);
                
                LOGGER.log(Level.INFO, "Google registration and login successful: {0}", createdUser.getEmail());
                
                // Redirect to homepage (default for new students)
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }
        }
        
        // Registration failed
        LOGGER.log(Level.SEVERE, "Failed to register Google user: {0}", googleUserInfo.getEmail());
        request.setAttribute("error", "Đăng ký thất bại! Vui lòng thử lại.");
        request.setAttribute("fullName", fullName);
        request.setAttribute("dateOfBirth", dateOfBirthStr);
        request.setAttribute("googleEmail", googleUserInfo.getEmail());
        request.setAttribute("googleName", googleUserInfo.getName());
        request.setAttribute("googlePicture", googleUserInfo.getPicture());
        request.getRequestDispatcher("/view/auth/google-register.jsp").forward(request, response);
    }
    
    /**
     * Validate Google registration form input
     */
    private String validateRegistrationInput(String fullName, String password, String confirmPassword, String dateOfBirth) {
        if (fullName == null || fullName.trim().length() < 2 || fullName.trim().length() > 100) {
            return "Họ tên phải từ 2 đến 100 ký tự!";
        }
        
        if (password == null || password.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự!";
        }
        
        if (!password.equals(confirmPassword)) {
            return "Xác nhận mật khẩu không khớp!";
        }
        
        if (dateOfBirth == null || dateOfBirth.trim().isEmpty()) {
            return "Vui lòng nhập ngày sinh!";
        }
        
        // Validate date format
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(dateOfBirth);
        } catch (ParseException e) {
            return "Ngày sinh không hợp lệ! (định dạng yyyy-MM-dd)";
        }
        
        return null; // No errors
    }
}
