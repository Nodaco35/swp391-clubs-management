package dao;

import java.sql.*;
import models.User;

public class UserDAO {

    // Đăng nhập đơn giản (kiểm tra email + password)
    public static User login(String email, String password) {
        String sql = "SELECT * FROM Users WHERE Email = ? AND Password = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                return null;
            }

            ps.setString(1, email);
            ps.setString(2, password); // Bạn có thể thay bằng mã hóa nếu muốn
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    // Đăng ký người dùng mới
    public static boolean register(User user) {
        String newUserId = generateNextUserId(); // Tạo ID mới

        String sql = "INSERT INTO Users (UserID, FullName, Email, Password, PermissionID) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                return false;
            }

            ps.setString(1, newUserId);
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword()); // Hash nếu cần
            ps.setInt(5, user.getPermissionID());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static User getUserByEmail(String email) {
        User user = null;
        String query = "SELECT * FROM Users WHERE email = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setUserID(rs.getString("userID"));
                    user.setFullName(rs.getString("fullName"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setPermissionID(rs.getInt("permissionID"));
                    user.setStatus(rs.getBoolean("status"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace(); // Hoặc dùng logging
        }

        return user;
    }

    private static User extractUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserID(rs.getString("UserID"));
        u.setFullName(rs.getString("FullName"));
        u.setEmail(rs.getString("Email"));
        u.setPassword(rs.getString("Password"));
        return u;
    }
}
