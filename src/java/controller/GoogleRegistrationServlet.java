package controller;

import dal.UserDAO;
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
 * Servlet xử lý hoàn tất đăng ký cho user Google mới
 * B. Redirect về form bổ sung thông tin (những thông tin này ở phần đăng ký)
 * C. Cho bổ sung cả password luôn
 */
@WebServlet(name = "GoogleRegistrationServlet", urlPatterns = {"/auth/google/register"})
public class GoogleRegistrationServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(GoogleRegistrationServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Users googleUserProfile = (Users) session.getAttribute("googleUserProfile");
        
        // Kiểm tra có thông tin Google user trong session không
        if (googleUserProfile == null) {
            LOGGER.log(Level.WARNING, "Không có thông tin Google user trong session");
            request.setAttribute("error", "Phiên đăng nhập Google đã hết hạn. Vui lòng thử lại.");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Pre-fill form với thông tin từ Google
        request.setAttribute("googleEmail", googleUserProfile.getEmail());
        request.setAttribute("googleName", googleUserProfile.getFullName());
        request.setAttribute("googlePicture", googleUserProfile.getAvatar());
        
        // Forward tới form hoàn tất đăng ký Google
        request.getRequestDispatcher("/view/auth/google-register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Users googleUserProfile = (Users) session.getAttribute("googleUserProfile");
        
        // Kiểm tra có thông tin Google user trong session không
        if (googleUserProfile == null) {
            LOGGER.log(Level.WARNING, "Không có thông tin Google user trong session khi đăng ký");
            request.setAttribute("error", "Phiên đăng nhập Google đã hết hạn. Vui lòng thử lại.");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Lấy dữ liệu từ form
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
            request.setAttribute("googleEmail", googleUserProfile.getEmail());
            request.setAttribute("googleName", googleUserProfile.getFullName());
            request.setAttribute("googlePicture", googleUserProfile.getAvatar());
            request.getRequestDispatcher("/view/auth/google-register.jsp").forward(request, response);
            return;
        }
        
        // D. Kiểm tra lại email có tồn tại không (race condition protection)
        Users existingUser = UserDAO.getUserByEmail(googleUserProfile.getEmail());
        if (existingUser != null) {
            LOGGER.log(Level.WARNING, "User đã tồn tại trong quá trình đăng ký Google: {0}", googleUserProfile.getEmail());
            request.setAttribute("error", "Tài khoản với email này đã tồn tại!");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Tạo user mới
        Users newUser = new Users();
        newUser.setFullName(fullName.trim());
        newUser.setEmail(googleUserProfile.getEmail());
        newUser.setPassword(password.trim()); // C. Cho bổ sung cả password luôn
        
        // Parse ngày sinh
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date dateOfBirth = sdf.parse(dateOfBirthStr);
            newUser.setDateOfBirth(dateOfBirth);
        } catch (ParseException e) {
            request.setAttribute("error", "Ngày sinh không hợp lệ!");
            request.setAttribute("fullName", fullName);
            request.setAttribute("dateOfBirth", dateOfBirthStr);
            request.setAttribute("googleEmail", googleUserProfile.getEmail());
            request.setAttribute("googleName", googleUserProfile.getFullName());
            request.setAttribute("googlePicture", googleUserProfile.getAvatar());
            request.getRequestDispatcher("/view/auth/google-register.jsp").forward(request, response);
            return;
        }
        
        // E. Set default user properties - dùng chung phân quyền như hiện tại
        newUser.setPermissionID(1); // Default: Student
        newUser.setStatus(true);    // Google users đã được verify tự động
        
        // Set avatar từ Google nếu có
        if (googleUserProfile.getAvatar() != null && !googleUserProfile.getAvatar().isEmpty()) {
            newUser.setAvatar(googleUserProfile.getAvatar());
        }
        
        LOGGER.log(Level.INFO, "Tạo user Google mới: {0}", newUser.getEmail());
        
        // Lưu vào database
        boolean success = userDAO.register(newUser);
        if (success) {
            // Đăng ký thành công, lấy lại user từ DB để có đầy đủ thông tin (bao gồm UserID)
            Users registeredUser = UserDAO.getUserByEmail(newUser.getEmail());
            if (registeredUser != null) {
                LOGGER.log(Level.INFO, "Đăng ký Google thành công: {0} với UserID: {1}", 
                          new Object[]{registeredUser.getEmail(), registeredUser.getUserID()});
                
                // Xóa thông tin Google user từ session
                session.removeAttribute("googleUserProfile");
                
                // E. Tạo session và đăng nhập (dùng chung session như logic hiện tại)
                loginNewGoogleUser(registeredUser, request, response);
                return;
            } else {
                LOGGER.log(Level.SEVERE, "Không thể lấy lại thông tin user sau khi đăng ký: {0}", newUser.getEmail());
            }
        }
        
        // Đăng ký thất bại
        LOGGER.log(Level.SEVERE, "Đăng ký Google thất bại: {0}", googleUserProfile.getEmail());
        request.setAttribute("error", "Đăng ký thất bại! Vui lòng thử lại.");
        request.setAttribute("fullName", fullName);
        request.setAttribute("dateOfBirth", dateOfBirthStr);
        request.setAttribute("googleEmail", googleUserProfile.getEmail());
        request.setAttribute("googleName", googleUserProfile.getFullName());
        request.setAttribute("googlePicture", googleUserProfile.getAvatar());
        request.getRequestDispatcher("/view/auth/google-register.jsp").forward(request, response);
    }
    
    /**
     * E. Đăng nhập user Google mới đã đăng ký thành công - dùng chung session như logic hiện tại
     */
    private void loginNewGoogleUser(Users user, HttpServletRequest request, HttpServletResponse response)
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
        
        LOGGER.log(Level.INFO, "Đăng nhập thành công user Google mới: {0}", user.getEmail());
        
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
     * Validate form input cho Google registration
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
        
        // Validate định dạng ngày
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(dateOfBirth);
        } catch (ParseException e) {
            return "Ngày sinh không hợp lệ! (định dạng yyyy-MM-dd)";
        }
        
        return null; // Không có lỗi
    }
}
