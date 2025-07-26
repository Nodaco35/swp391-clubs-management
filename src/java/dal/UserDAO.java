package dal;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Users;

public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    public List<Integer> getEventIDsOfChairman(String userID) {
        List<Integer> eventIDs = new ArrayList<>();

        String sql = "SELECT e.EventID " +
                "FROM Events e " +
                "JOIN Clubs c ON e.ClubID = c.ClubID " +
                "JOIN UserClubs uc ON c.ClubID = uc.ClubID " +
                "WHERE uc.UserID = ? AND uc.RoleID = 1";

        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    eventIDs.add(rs.getInt("EventID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return eventIDs;
    }

    public boolean isChairman(String userID, int clubID) {
        String sql = "SELECT 1 FROM UserClubs WHERE UserID = ? AND ClubID = ? AND RoleID = 1 AND IsActive = 1 LIMIT 1";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, userID);
            ps.setInt(2, clubID);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getClubIdIfChairman(String userID) {
        String sql = "SELECT ClubID " +
                "FROM UserClubs " +
                "WHERE UserID = ? AND RoleID = 1 AND IsActive = 1 " +
                "LIMIT 1";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("ClubID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy danh sách người dùng theo phân trang
    public static List<Users> findUsersByPage(int offset, int noOfRecords) {
        List<Users> list = new ArrayList<>();
        String sql = """
        SELECT u.*, p.PermissionName
        FROM Users u
        JOIN Permissions p ON u.PermissionID = p.PermissionID
        ORDER BY u.UserID ASC
        LIMIT ? OFFSET ?
    """;

        try (PreparedStatement ps = DBContext.getConnection().prepareStatement(sql)) {
            ps.setInt(1, noOfRecords);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Users user = new Users();
                user.setUserID(rs.getString("UserID"));
                user.setFullName(rs.getString("FullName"));
                user.setEmail(rs.getString("Email"));
                user.setPassword(rs.getString("Password"));
                user.setDateOfBirth(rs.getDate("DateOfBirth"));
                user.setPermissionID(rs.getInt("PermissionID"));
                user.setStatus(rs.getBoolean("Status"));
                user.setResetToken(rs.getString("ResetToken"));
                user.setTokenExpiry(rs.getTimestamp("TokenExpiry"));
                user.setAvatar(rs.getString("AvatarSrc"));
                user.setPerName(rs.getString("PermissionName"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

// Đếm tổng số người dùng
    public static int countAllUsers() {
        String sql = "SELECT COUNT(*) FROM Users";
        try (PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void insertByAdmin(String fullName, String email, String password, String dateOfBirth, int permissionID, String status) {
        
        String sql = """
                     INSERT INTO `clubmanagementsystem`.`users`
                     (`UserID`,
                     `FullName`,
                     `Email`,
                     `Password`,
                     `AvatarSrc`,
                     `DateOfBirth`,
                     `PermissionID`,
                     `Status`)
                     VALUES
                     (?,
                     ?,
                     ?,
                     ?,
                     ?,
                     ?,
                     ?,
                     ?);""";
        try {
            if (dateOfBirth.equals("")) {
                dateOfBirth = null;
            }
            int status_raw;
            if (status.equals("true")) {
                status_raw = 1;

            } else {
                status_raw = 0;
            }
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            
            // Sử dụng cùng logic như register() - ưu tiên mã sinh viên cho email FPT
            String userID;
            if (email != null && email.endsWith("@fpt.edu.vn")) {
                userID = generateUserIdForGoogleUser(email);
            } else {
                userID = generateNextUserId();
            }
            
            ps.setObject(1, userID);
            ps.setObject(2, fullName);
            ps.setObject(3, email);
            ps.setObject(4, password);
            ps.setObject(5, "img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg");
            ps.setObject(6, dateOfBirth);
            ps.setObject(7, permissionID);
            ps.setObject(8, status_raw);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean updateAccountByAdmin(String userID, String new_name, String new_email, String new_password, String new_dob, int new_permissionID, int status) {
        String sql = """
                     UPDATE `clubmanagementsystem`.`users`
                     SET
                     
                     `FullName` = ?,
                     `Email` = ?,
                     `Password` = ?,
                     
                     `DateOfBirth` = ?,
                     `PermissionID` = ?,
                     `Status` = ?   
                     
                     WHERE `UserID` = ?;""";
        
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, new_name);
            ps.setObject(2, new_email);
            ps.setObject(3, new_password);
            ps.setObject(4, new_dob);
            ps.setObject(5, new_permissionID);
            ps.setObject(6, status);
            ps.setObject(7, userID);
            int rs = ps.executeUpdate();
            return rs > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void changeActiveAccountByAdmin(String userID) {
        String sql = """
                     UPDATE `clubmanagementsystem`.`users`
                     SET
                     
                     `Status` = 0
                     
                     WHERE `UserID` = ?;""";
        
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, userID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Users> findUsersByPageAndPerID(int perID, int offset, int noOfRecords) {
        List<Users> list = new ArrayList<>();
        String sql = """
        SELECT u.*, p.PermissionName
        FROM Users u
        JOIN Permissions p ON u.PermissionID = p.PermissionID
        WHERE u.PermissionID = ?
        ORDER BY u.UserID ASC
        LIMIT ? OFFSET ?
    """;

        try (PreparedStatement ps = DBContext.getConnection().prepareStatement(sql)) {
            ps.setInt(1, perID);
            ps.setInt(2, noOfRecords);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Users user = new Users();
                user.setUserID(rs.getString("UserID"));
                user.setFullName(rs.getString("FullName"));
                user.setEmail(rs.getString("Email"));
                user.setPassword(rs.getString("Password"));
                user.setDateOfBirth(rs.getDate("DateOfBirth"));
                user.setPermissionID(rs.getInt("PermissionID"));
                user.setStatus(rs.getBoolean("Status"));
                user.setResetToken(rs.getString("ResetToken"));
                user.setTokenExpiry(rs.getTimestamp("TokenExpiry"));
                user.setAvatar(rs.getString("AvatarSrc"));
                user.setPerName(rs.getString("PermissionName"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int countUsersByPermissionID(int permissionID) {
        String sql = "SELECT COUNT(*) FROM Users WHERE PermissionID = ?";
        try (PreparedStatement ps = DBContext.getConnection().prepareStatement(sql)) {
            ps.setInt(1, permissionID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    // Tìm kiếm người dùng theo query với phân trang

    public static List<Users> searchUsersByQuery(String query, int offset, int noOfRecords) {
        List<Users> list = new ArrayList<>();
        String sql = """
        SELECT u.*, p.PermissionName
        FROM Users u
        JOIN Permissions p ON u.PermissionID = p.PermissionID
        WHERE u.FullName LIKE ? OR u.Email LIKE ? OR u.UserID LIKE ?
        ORDER BY u.UserID ASC
        LIMIT ? OFFSET ?
    """;

        try (PreparedStatement ps = DBContext.getConnection().prepareStatement(sql)) {
            String searchPattern = "%" + query + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setInt(4, noOfRecords);
            ps.setInt(5, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Users user = new Users();
                user.setUserID(rs.getString("UserID"));
                user.setFullName(rs.getString("FullName"));
                user.setEmail(rs.getString("Email"));
                user.setPassword(rs.getString("Password"));
                user.setDateOfBirth(rs.getDate("DateOfBirth"));
                user.setPermissionID(rs.getInt("PermissionID"));
                user.setStatus(rs.getBoolean("Status"));
                user.setResetToken(rs.getString("ResetToken"));
                user.setTokenExpiry(rs.getTimestamp("TokenExpiry"));
                user.setAvatar(rs.getString("AvatarSrc"));
                user.setPerName(rs.getString("PermissionName"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

// Đếm số người dùng khớp với query
    public static int countUsersByQuery(String query) {
        String sql = """
        SELECT COUNT(*)
        FROM Users
        WHERE FullName LIKE ? OR Email LIKE ? OR UserID LIKE ?
    """;
        try (PreparedStatement ps = DBContext.getConnection().prepareStatement(sql)) {
            String searchPattern = "%" + query + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Users getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE Email = ?";
        
        try (Connection conn = DBContext.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Users user = new Users();
                    user.setUserID(rs.getString("UserID"));
                    user.setFullName(rs.getString("FullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setPassword(rs.getString("Password"));
                    user.setDateOfBirth(rs.getDate("DateOfBirth"));
                    user.setPermissionID(rs.getInt("PermissionID"));
                    user.setStatus(rs.getBoolean("Status"));
                    user.setResetToken(rs.getString("ResetToken"));
                    user.setTokenExpiry(rs.getTimestamp("TokenExpiry"));
                    user.setAvatar(rs.getString("AvatarSrc"));
                    return user;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by email: " + email, e);
        }
        
        return null;
    }

    public static boolean update(String newName, String avatarPath, String dob, String id) {
        String sql = "UPDATE `clubmanagementsystem`.`users`\n"
                + "SET\n"
                + "  `FullName` = ?,\n"
                + "  `AvatarSrc` = ? , `DateOfBirth` = ?\n"
                + "WHERE `UserID` = ?;";
        

        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, newName);
            ps.setObject(2, avatarPath);
            ps.setObject(3, dob);
            ps.setObject(4, id);
            int row = ps.executeUpdate();
            return row>0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Users getUserById(String id) {
        String sql = "SELECT * FROM Users WHERE UserID = ?";
        
        try (Connection conn = DBContext.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Users user = new Users();
                    user.setUserID(rs.getString("UserID"));
                    user.setFullName(rs.getString("FullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setPassword(rs.getString("Password"));
                    user.setPermissionID(rs.getInt("PermissionID"));
                    user.setStatus(rs.getBoolean("Status"));
                    user.setResetToken(rs.getString("ResetToken"));
                    user.setTokenExpiry(rs.getTimestamp("TokenExpiry"));
                    user.setDateOfBirth(rs.getDate("DateOfBirth"));
                    user.setAvatar(rs.getString("AvatarSrc"));
                    return user;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by ID: " + id, e);
        }
        
        return null;
    }

    public static void updateEmail(String otpEmail, String id) {
        String sql = "UPDATE Users SET Email = ? WHERE UserID = ?";
        
        try (Connection conn = DBContext.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, otpEmail);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating email for user: " + id, e);
        }
    }

    public int getTotalActiveUsers() {
        String sql = "SELECT COUNT(*) FROM Users WHERE Status = 1";
        
        try (Connection conn = DBContext.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total active users", e);
        }
        
        return 0;
    }

    public List<Users> getAllUsers() {
        List<Users> userList = new ArrayList<>();

        String sql = "SELECT * FROM Users";

        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Users user = new Users();
                user.setUserID(rs.getString("UserID"));
                user.setFullName(rs.getString("FullName"));
                user.setEmail(rs.getString("Email"));
                user.setPassword(rs.getString("Password"));
                user.setPermissionID(rs.getInt("PermissionID"));
                user.setStatus(rs.getBoolean("Status"));
                user.setResetToken(rs.getString("ResetToken"));
                user.setTokenExpiry(rs.getTimestamp("TokenExpiry"));

                userList.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (userList.isEmpty()) {
            return null;
        }
        return userList;
    }

    /**
     * Extract mã sinh viên từ email FPT 
     * Ví dụ: huycvhe180308@fpt.edu.vn -> HE180308
     * @param email Email FPT của sinh viên
     * @return Mã sinh viên (VD: HE180308) hoặc null nếu không hợp lệ
     */
    private static String extractStudentCodeFromEmail(String email) {
        if (email == null || !email.endsWith("@fpt.edu.vn")) {
            return null;
        }
        
        String localPart = email.substring(0, email.indexOf("@"));
        
        // Pattern: tên + mã sinh viên (VD: huycv + he180308)
        // Tìm phần có pattern: 2 chữ cái + 6 số ở cuối
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("([a-zA-Z]{2}\\d{6})$", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(localPart);
        
        if (matcher.find()) {
            return matcher.group(1).toUpperCase(); // Trả về HE180308
        }
        
        return null;
    }

    /**
     * Generate UserID cho Google user dựa trên email FPT
     * Ví dụ: huycvhe180308@fpt.edu.vn -> HE180308
     * @param email Email FPT của user
     * @return Mã sinh viên nếu email hợp lệ, hoặc fallback về U### nếu không extract được
     */
    public static String generateUserIdForGoogleUser(String email) {
        // Thử extract mã sinh viên từ email FPT
        String studentCode = extractStudentCodeFromEmail(email);
        if (studentCode != null && !isUserIdExists(studentCode)) {
            return studentCode;
        }
        
        // Fallback về method generate cũ
        return generateNextUserId();
    }

    /**
     * Kiểm tra UserID đã tồn tại chưa
     */
    private static boolean isUserIdExists(String userID) {
        String sql = "SELECT 1 FROM Users WHERE UserID = ? LIMIT 1";
        try (Connection conn = DBContext.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return true; // Safe side: assume exists nếu có lỗi
        }
    }

    private static String generateNextUserId() {
        String sql = "SELECT UserID FROM Users ORDER BY UserID DESC LIMIT 1";

        try (Connection conn = DBContext.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString("UserID"); // Ví dụ: "U005"
                int numericPart = Integer.parseInt(lastId.substring(1)); // Cắt bỏ chữ 'U'
                return String.format("U%03d", numericPart + 1); // Tăng và định dạng lại
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating next user ID", e);
        }

        return "U001"; // Nếu chưa có ai trong DB
    }

    public boolean register(Users user) {
        String sql = "INSERT INTO Users (UserID, FullName, Email, Password, AvatarSrc, DateOfBirth, PermissionID, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            // Tạo UserID - ưu tiên mã sinh viên cho email FPT
            String userID;
            if (user.getEmail() != null && user.getEmail().endsWith("@fpt.edu.vn")) {
                userID = generateUserIdForGoogleUser(user.getEmail());
            } else {
                userID = generateNextUserId();
            }
            
            ps.setString(1, userID);
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getAvatar() != null ? user.getAvatar() : "img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg");
            
            if (user.getDateOfBirth() != null) {
                ps.setDate(6, new Date(user.getDateOfBirth().getTime()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            ps.setInt(7, user.getPermissionID());
            ps.setBoolean(8, user.isStatus());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error registering user: " + user.getEmail(), ex);
            return false;
        }
    }

    public Users getUserByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM Users WHERE Email = ? AND Password = ?";

        try (Connection conn = DBContext.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Users user = new Users();
                    user.setUserID(rs.getString("UserID"));
                    user.setFullName(rs.getString("FullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setPassword(rs.getString("Password"));
                    user.setPermissionID(rs.getInt("PermissionID"));
                    user.setStatus(rs.getBoolean("Status"));
                    user.setResetToken(rs.getString("ResetToken"));
                    user.setTokenExpiry(rs.getTimestamp("TokenExpiry"));
                    user.setAvatar(rs.getString("AvatarSrc"));
                    user.setDateOfBirth(rs.getDate("DateOfBirth"));
                    return user;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by email and password", e);
        }

        return null;
    }

    /**
     * Cập nhật token xác minh và thời gian hết hạn cho một tài khoản
     *
     * @param email Email của tài khoản
     * @param token Token xác minh
     * @param expiryDate Thời gian hết hạn của token
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public static boolean updateVerificationToken(String email, String token, java.util.Date expiryDate) {
        String sql = "UPDATE Users SET ResetToken = ?, TokenExpiry = ?, Status = ? WHERE Email = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setTimestamp(2, new Timestamp(expiryDate.getTime()));
            ps.setBoolean(3, false); // Tài khoản chưa kích hoạt
            ps.setString(4, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating verification token for: " + email, e);
        }
        return false;
    }

    /**
     * Xác minh tài khoản dựa trên token
     *
     * @param token Token xác minh
     * @return true nếu xác minh thành công, false nếu thất bại hoặc token không
     * hợp lệ
     */
    public boolean verifyAccount(String token) {
        // Đầu tiên kiểm tra token có tồn tại và chưa hết hạn
        Users user = getUserByToken(token);

        if (user == null) {
            return false; // Token không tồn tại
        }

        if (user.getTokenExpiry() == null || new java.util.Date().after(user.getTokenExpiry())) {
            return false; // Token đã hết hạn
        }

        // Nếu token hợp lệ, kích hoạt tài khoản
        String sql = "UPDATE Users SET Status = ?, ResetToken = NULL, TokenExpiry = NULL WHERE ResetToken = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, true); // Kích hoạt tài khoản
            ps.setString(2, token);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error verifying account with token: " + token, e);
        }
        return false;
    }

    /**
     * Lấy thông tin người dùng dựa trên token
     *
     * @param token Token cần tìm
     * @return Đối tượng User nếu tìm thấy, null nếu không tìm thấy
     */
    public Users getUserByToken(String token) {
        String sql = "SELECT * FROM Users WHERE ResetToken = ?";
        
        try (Connection conn = DBContext.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Users user = new Users();
                    user.setUserID(rs.getString("UserID"));
                    user.setFullName(rs.getString("FullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setPassword(rs.getString("Password"));
                    user.setDateOfBirth(rs.getDate("DateOfBirth"));
                    user.setPermissionID(rs.getInt("PermissionID"));
                    user.setStatus(rs.getBoolean("Status"));
                    user.setResetToken(rs.getString("ResetToken"));
                    user.setTokenExpiry(rs.getTimestamp("TokenExpiry"));
                    return user;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by token: " + token, e);
        }
        
        return null;
    }

    /**
     * Cập nhật mật khẩu cho người dùng
     *
     * @param userID ID của người dùng
     * @param currentPassword Mật khẩu hiện tại
     * @param newPassword Mật khẩu mới
     * @return true nếu cập nhật thành công, false nếu thất bại hoặc mật khẩu
     * hiện tại không đúng
     */
    public static boolean updatePassword(String userID, String currentPassword, String newPassword) {
        // Kiểm tra mật khẩu hiện tại
        Users user = getUserById(userID);
        if (user == null || !user.getPassword().equals(currentPassword)) {
            return false; // Mật khẩu hiện tại không đúng
        }

        return changePassword(userID, newPassword);
    }

    /**
     * Thay đổi mật khẩu người dùng
     *
     * @param userID ID người dùng
     * @param newPassword Mật khẩu mới
     * @return true nếu thay đổi thành công, false nếu thất bại
     */
    public static boolean changePassword(String userID, String newPassword) {
        String sql = "UPDATE Users SET Password = ? WHERE UserID = ?";
        
        try (Connection conn = DBContext.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, userID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating password for user: " + userID, e);
            return false;
        }
    }

    /**
     * Lấy tên người dùng theo UserID
     * @param userId ID của người dùng
     * @return Tên đầy đủ của người dùng, hoặc null nếu không tìm thấy
     */
    public String getUserName(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }
        
        String sql = "SELECT FullName FROM Users WHERE UserID = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("FullName");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, 
                "Error getting user name for userId: " + userId, e);
        }
        
        return null;
    }
}
