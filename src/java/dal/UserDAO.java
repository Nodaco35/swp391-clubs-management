package dal;

import dal.DBContext;
import models.Users;

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

public class UserDAO {
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

        try (PreparedStatement ps = DBContext_Duc.getInstance().connection.prepareStatement(sql)) {
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
        try (PreparedStatement ps = DBContext_Duc.getInstance().connection.prepareStatement(sql)) {
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
        DBContext_Duc db = DBContext_Duc.getInstance();

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
            PreparedStatement ps = db.connection.prepareStatement(sql);
            String userID = generateNextUserId();
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
        DBContext_Duc db = DBContext_Duc.getInstance();
        try {
            PreparedStatement ps = db.connection.prepareStatement(sql);
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
        DBContext_Duc db = DBContext_Duc.getInstance();
        try {
            PreparedStatement ps = db.connection.prepareStatement(sql);
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

        try (PreparedStatement ps = DBContext_Duc.getInstance().connection.prepareStatement(sql)) {
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
        try (PreparedStatement ps = DBContext_Duc.getInstance().connection.prepareStatement(sql)) {
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

        try (PreparedStatement ps = DBContext_Duc.getInstance().connection.prepareStatement(sql)) {
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
        try (PreparedStatement ps = DBContext_Duc.getInstance().connection.prepareStatement(sql)) {
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

    public Users getUserByID(String userID) {
        String sql = "SELECT * FROM Users WHERE UserID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    public static Users getUserByEmail(String email) {
        Users user = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT * FROM Users WHERE Email = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                user = new Users();
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
            }
        } catch (SQLException e) {
            System.out.println("Error getting user by email: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    DBContext.closeConnection(conn);
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return user;
    }

    public static void update(String newName, String avatarPath, String dob, String id) {
        String sql = "UPDATE `clubmanagementsystem`.`users`\n"
                + "SET\n"
                + "  `FullName` = ?,\n"
                + "  `AvatarSrc` = ? , `DateOfBirth` = ?\n"
                + "WHERE `UserID` = ?;";
        DBContext_Duc db = DBContext_Duc.getInstance();

        try {
            PreparedStatement ps = db.connection.prepareStatement(sql);
            ps.setObject(1, newName);
            ps.setObject(2, avatarPath);
            ps.setObject(3, dob);
            ps.setObject(4, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Users getUserById(String id) {
        String sql = "SELECT * FROM users WHERE UserID = ?";

        // Thử kết nối với DBContext_Duc
        try {
            DBContext_Duc db = DBContext_Duc.getInstance();

            // Kiểm tra kết nối không null
            if (db.connection != null) {
                try (PreparedStatement ps = db.connection.prepareStatement(sql)) {
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
                    e.printStackTrace();
                }
            } else {
                // Nếu kết nối từ DBContext_Duc null, thử dùng DBContext thay thế
                try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void updateEmail(String otpEmail, String id) {
        String sql = """
                         UPDATE `clubmanagementsystem`.`users`
                         SET
                         
                         `Email` = ?
                         
                         WHERE `UserID` = ?;""";
        DBContext_Duc db = DBContext_Duc.getInstance();

        try {

            PreparedStatement ps = db.connection.prepareStatement(sql);
            ps.setObject(1, otpEmail);
            ps.setObject(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean validateUser(String email, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean isValid = false;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT * FROM Users WHERE Email = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("Password");
                // Trong thực tế, bạn nên sử dụng thư viện mã hóa như BCrypt để kiểm tra mật khẩu
                // Ví dụ: isValid = BCrypt.checkpw(password, storedPassword);
                isValid = password.equals(storedPassword);
            }
        } catch (SQLException e) {
            System.out.println("Error validating user: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    DBContext.closeConnection(conn);
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return isValid;
    }

    public int getTotalActiveUsers() {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT COUNT(*) FROM Users WHERE Status = 1";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting total active users: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    DBContext.closeConnection(conn);
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return count;
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

    private static String generateNextUserId() {
        String sql = "SELECT UserID FROM Users ORDER BY UserID DESC LIMIT 1";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("UserID"); // Ví dụ: "U005"
                int numericPart = Integer.parseInt(lastId.substring(1)); // Cắt bỏ chữ 'U'
                return String.format("U%03d", numericPart + 1); // Tăng và định dạng lại
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "U001"; // Nếu chưa có ai trong DB
    }

    public boolean register(Users user) {
        String sql = "INSERT INTO Users (UserID, FullName, Email, Password, DateOfBirth, PermissionID, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            LOGGER.log(Level.INFO, "Bắt đầu đăng ký cho người dùng: {0}", user.getEmail());

            // Tạo UserID
            String userID = generateNextUserId();
            ps.setString(1, userID);
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            // Convert java.util.Date to java.sql.Date
            if (user.getDateOfBirth() != null) {
                ps.setDate(5, new Date(user.getDateOfBirth().getTime()));
                LOGGER.log(Level.INFO, "Ngày sinh được đặt: {0}", user.getDateOfBirth());
            } else {
                ps.setNull(5, java.sql.Types.DATE);
                LOGGER.log(Level.WARNING, "Ngày sinh là null");
            }
            ps.setInt(6, user.getPermissionID());
            ps.setBoolean(7, user.isStatus());
            LOGGER.log(Level.INFO, "SQL Statement: {0} với UserID={1}", new Object[]{sql, userID});

            int rowsAffected = ps.executeUpdate();
            LOGGER.log(Level.INFO, "Kết quả đăng ký: {0} dòng bị ảnh hưởng", rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi SQL khi đăng ký người dùng: {0}", ex.getMessage());
            ex.printStackTrace();
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi không xác định khi đăng ký người dùng: {0}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Users getUserByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM Users WHERE Email = ? AND Password = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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
            e.printStackTrace();
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

            if (conn == null) {
                return false;
            }

            ps.setString(1, token);
            ps.setTimestamp(2, new Timestamp(expiryDate.getTime()));
            ps.setBoolean(3, false); // Tài khoản chưa kích hoạt
            ps.setString(4, email);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
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

            if (conn == null) {
                return false;
            }

            ps.setBoolean(1, true); // Kích hoạt tài khoản
            ps.setString(2, token);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
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
        Users user = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT * FROM Users WHERE ResetToken = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, token);
            rs = stmt.executeQuery();

            if (rs.next()) {
                user = new Users();
                user.setUserID(rs.getString("UserID"));
                user.setFullName(rs.getString("FullName"));
                user.setEmail(rs.getString("Email"));
                user.setPassword(rs.getString("Password"));
                user.setDateOfBirth(rs.getDate("DateOfBirth"));
                user.setPermissionID(rs.getInt("PermissionID"));
                user.setStatus(rs.getBoolean("Status"));
                user.setResetToken(rs.getString("ResetToken"));
                user.setTokenExpiry(rs.getTimestamp("TokenExpiry"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting user by token: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    DBContext.closeConnection(conn);
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return user;
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

        // Sử dụng phương thức changePassword đã được cải tiến để cập nhật mật khẩu
        return changePassword(userID, newPassword);
    }

    /**
     * Kiểm tra mật khẩu hiện tại của người dùng
     *
     * @param userID ID người dùng
     * @param password Mật khẩu cần kiểm tra
     * @return true nếu mật khẩu đúng, false nếu mật khẩu sai
     */
    public static boolean checkPassword(String userID, String password) {
        Users user = getUserById(userID);
        if (user == null) {
            return false;
        }
        return user.getPassword().equals(password);
    }

    /**
     * Thay đổi mật khẩu người dùng
     *
     * @param userID ID người dùng
     * @param newPassword Mật khẩu mới
     * @return true nếu thay đổi thành công, false nếu thất bại
     */
    public static boolean changePassword(String userID, String newPassword) {
        String sql = "UPDATE users SET Password = ? WHERE UserID = ?";

        // Thử với DBContext_Duc
        DBContext_Duc db = DBContext_Duc.getInstance();
        if (db.connection != null) {
            try {
                PreparedStatement ps = db.connection.prepareStatement(sql);
                ps.setString(1, newPassword);
                ps.setString(2, userID);
                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật mật khẩu với DBContext_Duc: {0}", e.getMessage());
                e.printStackTrace();
            }
        }

        // Nếu kết nối DBContext_Duc thất bại, thử với DBContext
        try (Connection conn = DBContext.getConnection()) {
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, newPassword);
                    ps.setString(2, userID);
                    int rowsAffected = ps.executeUpdate();
                    return rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật mật khẩu với DBContext: {0}", e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

}
