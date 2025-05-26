package dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.User;

public class UserDAO {

    public static User getUserByEmail(String email) {
        User user = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT * FROM Users WHERE Email = ? AND Status = 1";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User();
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
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error getting user by email: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return user;
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
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error validating user: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
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
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error getting total active users: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return count;
    }

    public List<User> getAllUsers() throws ClassNotFoundException {
        List<User> userList = new ArrayList<>();

        String sql = "SELECT * FROM Users";


        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
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

        if (userList.isEmpty()) return null;
        return userList;
    }

    private String generateNextUserId() throws SQLException {
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

    public boolean register(User user) {
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

    public User getUserByEmailAndPassword(String email, String password) throws ClassNotFoundException {
        String sql = "SELECT * FROM Users WHERE Email = ? AND Password = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserID(rs.getString("UserID"));
                    user.setFullName(rs.getString("FullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setPassword(rs.getString("Password"));
                    user.setPermissionID(rs.getInt("PermissionID"));
                    user.setStatus(rs.getBoolean("Status"));
                    user.setResetToken(rs.getString("ResetToken"));
                    user.setTokenExpiry(rs.getTimestamp("TokenExpiry"));

                    return user;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
     

