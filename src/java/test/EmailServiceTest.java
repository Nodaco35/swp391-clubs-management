package test;

import util.EmailService;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple test class for EmailService
 * Run this class to test if email sending is working correctly
 */
public class EmailServiceTest {
    
    private static final Logger LOGGER = Logger.getLogger(EmailServiceTest.class.getName());
      public static void main(String[] args) {
        // Test SMTP connection first
        LOGGER.log(Level.INFO, "Kiểm tra kết nối SMTP...");
        boolean smtpConnected = EmailService.testSMTPConnection();
        
        if (!smtpConnected) {
            LOGGER.log(Level.SEVERE, "Không thể kết nối đến máy chủ SMTP. Các bài kiểm tra khác sẽ không chạy.");
            return;
        }
        
        // Sử dụng email người dùng để kiểm tra
        String testEmail = "huycvhe180308@fpt.edu.vn"; // Email được chỉ định để kiểm tra
        
        // Test token generation
        String token = EmailService.generateVerificationToken();
        LOGGER.log(Level.INFO, "Generated verification token: {0}", token);
        
        // Test token expiry time generation
        Date expiryTime = EmailService.generateTokenExpiryTime();
        LOGGER.log(Level.INFO, "Generated token expiry time: {0}", expiryTime);
          // Test email sending
        boolean emailSent = EmailService.sendRegistrationEmail(
            testEmail, 
            "Chau Van Huy", 
            token, 
            "/swp391-clubs-management"
        );
        
        if (emailSent) {
            LOGGER.log(Level.INFO, "Test email sent successfully to {0}", testEmail);
            LOGGER.log(Level.INFO, "Verification URL should contain token: {0}", token);
        } else {
            LOGGER.log(Level.SEVERE, "Failed to send test email to {0}", testEmail);
        }
    }
}
