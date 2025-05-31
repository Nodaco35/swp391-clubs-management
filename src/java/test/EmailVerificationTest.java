package test;

import util.EmailService;
import models.Users;
import dal.UserDAO;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lớp test để kiểm tra quy trình xác minh email
 * Chạy class này để kiểm tra quy trình xác minh email hoạt động đúng
 */
public class EmailVerificationTest {
    
    private static final Logger LOGGER = Logger.getLogger(EmailVerificationTest.class.getName());
    
    public static void main(String[] args) {
        // Thay đổi email để kiểm tra
        String testEmail = "huycvhe180308@fpt.edu.vn";
        
        // Test 1: Kiểm tra kết nối SMTP
        LOGGER.log(Level.INFO, "Test 1: Kiểm tra kết nối SMTP");
        boolean smtpConnected = EmailService.testSMTPConnection();
        if (!smtpConnected) {
            LOGGER.log(Level.SEVERE, "Kết nối SMTP thất bại! Kiểm tra cấu hình SMTP.");
            return;
        }
        
        // Test 2: Kiểm tra tạo token và thời gian hết hạn
        LOGGER.log(Level.INFO, "Test 2: Kiểm tra tạo token và thời gian hết hạn");
        String token = EmailService.generateVerificationToken();
        LOGGER.log(Level.INFO, "Token được tạo: {0}", token);
        Date expiryTime = EmailService.generateTokenExpiryTime();
        LOGGER.log(Level.INFO, "Thời gian hết hạn: {0}", expiryTime);
        
        // Test 3: Tạo User giả cho việc thử nghiệm
        LOGGER.log(Level.INFO, "Test 3: Kiểm tra tạo User mô phỏng");
        Users testUser = createTestUser(testEmail);
        LOGGER.log(Level.INFO, "Đã tạo người dùng test: {0}", testUser.getEmail());
        
        // Test 4: Gửi email xác minh
        LOGGER.log(Level.INFO, "Test 4: Kiểm tra gửi email xác minh");
        boolean emailSent = EmailService.sendRegistrationEmail(
            testUser.getEmail(), 
            testUser.getFullName(), 
            token, 
            "/swp391-clubs-management"
        );
        
        if (emailSent) {
            LOGGER.log(Level.INFO, "Email xác minh đã được gửi thành công đến {0}", testUser.getEmail());
            LOGGER.log(Level.INFO, "URL xác minh sẽ chứa token: {0}", token);
        } else {
            LOGGER.log(Level.SEVERE, "Không thể gửi email xác minh đến {0}", testUser.getEmail());
        }
        
        // Hướng dẫn kiểm tra
        LOGGER.log(Level.INFO, "=========== HƯỚNG DẪN KIỂM TRA ===========");
        LOGGER.log(Level.INFO, "1. Kiểm tra email của bạn để xem đã nhận được email xác minh chưa");
        LOGGER.log(Level.INFO, "2. Kiểm tra URL xác minh trong email có chứa token đúng không");
        LOGGER.log(Level.INFO, "3. Nhấp vào URL để xác minh tài khoản (hoặc chạy một câu truy vấn SQL mô phỏng xác minh)");
        LOGGER.log(Level.INFO, "4. Sau khi xác minh, đăng nhập với email và mật khẩu để xác nhận tài khoản đã được kích hoạt");
    }
    
    private static Users createTestUser(String email) {
        Users user = new Users();
        user.setFullName("Người Dùng Test");
        user.setEmail(email);
        user.setPassword("password123");
        user.setPermissionID(1); // Student
        user.setStatus(false); // Chưa xác minh
        return user;
    }
}
