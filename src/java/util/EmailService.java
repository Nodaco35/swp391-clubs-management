package util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.*;
import java.util.Properties;
import java.util.UUID;
import java.util.Date;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dịch vụ gửi email cho hệ thống, hỗ trợ xác minh tài khoản
 */
public class EmailService {
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    
    // Các thông tin cấu hình
    private static String smtpHost = "smtp.gmail.com";
    private static String smtpPort = "587";
    private static String fromEmail = "fptcms@gmail.com";
    private static String password = "qthj sgph dnkf swvz";  // Sẽ được cập nhật từ file cấu hình hoặc biến môi trường
    private static String baseUrl = "http://localhost:8080";
    
    static {
        // Load cấu hình khi class được tải
        loadConfig();
    }
    
    /**
     * Tải cấu hình từ file hoặc biến môi trường
     */
    private static void loadConfig() {
        // Thử tải từ biến môi trường trước
        String envEmail = System.getenv("EMAIL_USERNAME");
        String envPassword = System.getenv("EMAIL_PASSWORD");
        String envBaseUrl = System.getenv("APP_BASE_URL");
        
        if (envEmail != null && !envEmail.isEmpty()) fromEmail = envEmail;
        if (envPassword != null && !envPassword.isEmpty()) password = envPassword;
        if (envBaseUrl != null && !envBaseUrl.isEmpty()) baseUrl = envBaseUrl;
        
        // Nếu không có trong biến môi trường, thử tải từ file cấu hình
        String configPath = System.getProperty("catalina.base") + "/conf/email_config.properties";
        Properties props = new Properties();
        
        try (FileInputStream fis = new FileInputStream(configPath)) {
            props.load(fis);
            
            fromEmail = props.getProperty("email.username", fromEmail);
            password = props.getProperty("email.password", password);
            baseUrl = props.getProperty("app.base_url", baseUrl);
            smtpHost = props.getProperty("email.smtp.host", smtpHost);
            smtpPort = props.getProperty("email.smtp.port", smtpPort);
            
            LOGGER.log(Level.INFO, "Đã tải cấu hình email từ file: {0}", configPath);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Không thể tải file cấu hình email, sử dụng giá trị mặc định: {0}", e.getMessage());
        }
        
        // Nếu vẫn chưa có mật khẩu, sử dụng giá trị mặc định cuối cùng
        if (password == null || password.isEmpty()) {
            password = "qthj sgph dnkf swvz";  // Mật khẩu ứng dụng Gmail mặc định
        }
    }
    
    /**
     * Tạo token xác nhận email và trả về token đó
     * @return token UUID đã được tạo
     */
    public static String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Tạo thời điểm hết hạn cho token, mặc định là 24 giờ sau thời điểm hiện tại
     * @return Thời điểm hết hạn của token
     */
    public static Date generateTokenExpiryTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24); // Token có hiệu lực trong 24 giờ
        return calendar.getTime();
    }
    
    /**
     * Gửi email xác nhận đăng ký với token xác minh
     * @param toEmail Email người nhận
     * @param fullName Họ tên người nhận
     * @param verificationToken Token xác minh
     * @param contextPath ContextPath của ứng dụng web
     * @return true nếu gửi thành công, false nếu thất bại
     */
    public static boolean sendRegistrationEmail(String toEmail, String fullName, String verificationToken, String contextPath) {
        try {
            LOGGER.log(Level.INFO, "Chuẩn bị gửi email xác nhận đến: {0}", toEmail);
            
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );
            message.setSubject("Xác nhận đăng ký tài khoản - Hệ thống Quản lý Câu lạc bộ FPT");
            
            // Tạo URL xác nhận
            String verificationUrl = baseUrl + contextPath + "/verify?token=" + verificationToken;
            LOGGER.log(Level.INFO, "URL xác nhận: {0}", verificationUrl);
            
            String htmlContent = ""
                    + "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;'>"
                    + "<h2 style='color: #4285f4;'>Chào mừng đến với Hệ thống Quản lý Câu lạc bộ FPT!</h2>"
                    + "<p>Xin chào <strong>" + fullName + "</strong>,</p>"
                    + "<p>Cảm ơn bạn đã đăng ký tài khoản trên hệ thống của chúng tôi.</p>"
                    + "<p>Để hoàn tất việc đăng ký và kích hoạt tài khoản của bạn, vui lòng nhấp vào nút bên dưới:</p>"
                    + "<div style='text-align: center; margin: 30px 0;'>"
                    + "<a href='" + verificationUrl + "' style='background-color: #4285f4; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; font-weight: bold;'>Xác nhận Email</a>"
                    + "</div>"
                    + "<p>Hoặc bạn có thể sao chép và dán đường dẫn sau vào trình duyệt:</p>"
                    + "<p style='background-color: #f5f5f5; padding: 10px; border-radius: 4px;'>" + verificationUrl + "</p>"
                    + "<p>Lưu ý: Liên kết xác nhận này sẽ hết hạn sau 24 giờ.</p>"
                    + "<p>Nếu bạn không thực hiện yêu cầu đăng ký này, vui lòng bỏ qua email.</p>"
                    + "<p>Thân mến,<br>Ban quản trị CLB FPT</p>"
                    + "</div>";
            
            message.setContent(htmlContent, "text/html; charset=UTF-8");
            Transport.send(message);
            LOGGER.log(Level.INFO, "Đã gửi email xác nhận đến: {0}", toEmail);
            return true;
            
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi gửi email đến {0}: {1}", new Object[]{toEmail, e.getMessage()});
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Kiểm tra kết nối đến máy chủ SMTP và xác thực tài khoản
     * @return true nếu kết nối thành công, false nếu thất bại
     */
    public static boolean testSMTPConnection() {
        try {
            LOGGER.log(Level.INFO, "Kiểm tra kết nối SMTP với email: {0}", fromEmail);
            
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.connectiontimeout", "5000");
            props.put("mail.smtp.timeout", "5000");
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });
            
            Transport transport = session.getTransport("smtp");
            transport.connect(smtpHost, fromEmail, password);
            transport.close();
            
            LOGGER.log(Level.INFO, "Kết nối SMTP thành công!");
            return true;
            
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Lỗi kết nối SMTP: {0}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Kiểm tra cấu hình email và hiển thị thông tin chi tiết về lỗi nếu có
     * @return Thông báo chi tiết về trạng thái kết nối
     */
    public static String checkEmailConfigurationDetails() {
        StringBuilder result = new StringBuilder();
        try {
            LOGGER.log(Level.INFO, "Đang kiểm tra cấu hình email chi tiết...");
            result.append("Kiểm tra cấu hình email:\n");
            result.append("- Email gửi: ").append(fromEmail).append("\n");
            result.append("- Máy chủ SMTP: ").append(smtpHost).append(":").append(smtpPort).append("\n");
            result.append("- Mật khẩu: ").append(password.isEmpty() ? "Chưa cấu hình" : "Đã cấu hình").append("\n");
            result.append("- URL cơ sở: ").append(baseUrl).append("\n");
            
            // Kiểm tra kết nối Internet
            try {
                Properties props = new Properties();
                props.put("mail.smtp.connectiontimeout", "3000");
                props.put("mail.smtp.timeout", "3000");
                
                Session session = Session.getInstance(props);
                Transport transport = session.getTransport("smtp");
                transport.connect(smtpHost, Integer.parseInt(smtpPort), "", "");
                transport.close();
                result.append("- Kết nối Internet: OK\n");
            } catch (MessagingException e) {
                if (e.getMessage().contains("couldn't connect to host")) {
                    result.append("- Kết nối Internet: LỖI - Không thể kết nối đến máy chủ SMTP\n");
                } else {
                    result.append("- Kết nối Internet: OK (Có thể kết nối đến máy chủ SMTP)\n");
                }
            }
            
            // Kiểm tra file cấu hình
            String configPath = System.getProperty("catalina.base") + "/conf/email_config.properties";
            try {
                new FileInputStream(configPath).close();
                result.append("- File cấu hình: TỒN TẠI (").append(configPath).append(")\n");
            } catch (IOException e) {
                result.append("- File cấu hình: KHÔNG TỒN TẠI (").append(configPath).append(")\n");
                result.append("  Gợi ý: Tạo file với nội dung:\n");
                result.append("  email.username=your_email@gmail.com\n");
                result.append("  email.password=your_app_password\n");
                result.append("  app.base_url=http://localhost:8080\n");
                result.append("  email.smtp.host=smtp.gmail.com\n");
                result.append("  email.smtp.port=587\n");
            }
            
            return result.toString();
            
        } catch (Exception e) {
            result.append("Lỗi kiểm tra cấu hình: ").append(e.getMessage());
            return result.toString();
        }
    }
}
