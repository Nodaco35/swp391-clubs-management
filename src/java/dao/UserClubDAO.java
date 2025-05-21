package dao;

import models.UserClub;
import dao.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import models.UserClub;

public class UserClubDAO {

    // Get members for a specific club with pagination and search
    public List<UserClub> findMembersByClubId(String search, int page, int clubId, int pageSize) {
        List<UserClub> members = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        String sql = "SELECT uc.UserClubID, uc.UserID, uc.ClubID, uc.DepartmentID, uc.RoleID, uc.JoinDate, uc.IsActive, " +
                    "u.FullName, u.Email " +
                    "FROM UserClubs uc " +
                    "JOIN Users u ON uc.UserID = u.UserID " +
                    "WHERE uc.IsActive = 1 AND uc.ClubID = ?" +
                    (search != null && !search.isEmpty() ? " AND u.FullName LIKE ?" : "") +
                    " ORDER BY uc.UserID LIMIT ? OFFSET ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clubId);
            int paramIndex = 2;
            if (search != null && !search.isEmpty()) {
                ps.setString(paramIndex++, "%" + search + "%");
            }
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserClub member = new UserClub();
                    member.setUserClubID(rs.getInt("UserClubID"));
                    member.setUserID(rs.getInt("UserID"));
                    member.setClubID(rs.getInt("ClubID"));
                    member.setDepartmentID(rs.getInt("DepartmentID"));
                    member.setRoleID(rs.getInt("RoleID"));
                    member.setJoinDate(rs.getTimestamp("JoinDate"));
                    member.setIsActive(rs.getBoolean("IsActive"));
                    member.setFullName(rs.getString("FullName"));
                    member.setEmail(rs.getString("Email"));
                    members.add(member);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }

    // Get total number of members for pagination
    public int getTotalMembers(String search, int clubId) {
        int total = 0;

        String sql = "SELECT COUNT(*) " +
                    "FROM UserClubs uc " +
                    "JOIN Users u ON uc.UserID = u.UserID " +
                    "WHERE uc.IsActive = 1 AND uc.ClubID = ?" +
                    (search != null && !search.isEmpty() ? " AND u.FullName LIKE ?" : "");

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clubId);
            if (search != null && !search.isEmpty()) {
                ps.setString(2, "%" + search + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    // Add a new member
    public void addMember(String fullName, String email, String password, String departmentID, String permissions, int clubId) {
        String sqlUser = "INSERT INTO Users (FullName, Email, Password, Permissions) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlUser, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, password); // In practice, hash the password
            ps.setString(4, permissions);
            ps.executeUpdate();

            int userID;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                userID = rs.getInt(1);
            }

            String sqlUserClub = "INSERT INTO UserClubs (UserID, ClubID, DepartmentID, RoleID, JoinDate, IsActive) " +
                                "VALUES (?, ?, ?, ?, NOW(), 1)";
            try (PreparedStatement ps2 = conn.prepareStatement(sqlUserClub)) {
                ps2.setInt(1, userID);
                ps2.setInt(2, clubId);
                ps2.setInt(3, Integer.parseInt(departmentID));
                ps2.setInt(4, Integer.parseInt(permissions)); // RoleID same as permissions for simplicity
                ps2.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Edit an existing member
    public void editMember(int userId, String fullName, String email, String departmentID, int clubId) {
        String sqlUser = "UPDATE Users SET FullName = ?, Email = ? WHERE UserID = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlUser)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setInt(3, userId);
            ps.executeUpdate();

            String sqlUserClub = "UPDATE UserClubs SET DepartmentID = ? WHERE UserID = ? AND ClubID = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(sqlUserClub)) {
                ps2.setInt(1, Integer.parseInt(departmentID));
                ps2.setInt(2, userId);
                ps2.setInt(3, clubId);
                ps2.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete a member (set IsActive to false)
    public void deleteMember(int userId, int clubId) {
        String sql = "UPDATE UserClubs SET IsActive = 0 WHERE UserID = ? AND ClubID = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, clubId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

