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
        String sql = "SELECT *\n"
                + "FROM `clubmanagementsystem`.`users`\n"
                + "WHERE `users`.`UserID` = ?;";
        DBContext_Duc db = DBContext_Duc.getInstance();
        try {
            PreparedStatement ps = db.connection.prepareStatement(sql);
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

    private String generateNextUserId() {
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
    }    public boolean register(Users user) {
        String sql = "INSERT INTO Users (UserID, FullName, Email, Password, DateOfBirth, PermissionID, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
     * @param email     Email của tài khoản
     * @param token     Token xác minh
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
     * @return true nếu xác minh thành công, false nếu thất bại hoặc token không hợp lệ
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
}
