package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import models.User;
import dao.DBConnection;

public class UserDAO {

    public static List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();

        String sql = "SELECT * FROM Users";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("UserID"));
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

        return userList;
    }

    public static User getUserByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM Users WHERE Email = ? AND Password = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserID(rs.getInt("UserID"));
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

    public static void update(String newName, String email, String id) {
        String sql = "UPDATE `managerclub`.`users`\n"
                + "SET\n"
                + "  `FullName` = ?,\n"
                + "  `Email` = ?\n"
                + "WHERE `UserID` = ?;";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newName);
            ps.setObject(2, email);
            ps.setObject(3, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
