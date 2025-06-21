package dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import models.ClubCreationPermissions;

public class ClubCreationPermissionDAO {
    
    // Insert a new permission request
    public boolean insertRequest(String userID) {
        if (userID == null || userID.trim().isEmpty()) {
            return false;
        }
        String query = "INSERT INTO ClubCreationPermissions (UserID, Status, RequestDate) VALUES (?, 'PENDING', NOW())";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID.trim());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Get permissions by status
    public List<ClubCreationPermissions> getPermissionsByStatus(String status) {
        List<ClubCreationPermissions> permissions = new ArrayList<>();
        String query = "SELECT p.*, u.FullName FROM ClubCreationPermissions p " +
                       "JOIN Users u ON p.UserID = u.UserID " +
                       "WHERE p.Status = ? ORDER BY p.RequestDate DESC";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ClubCreationPermissions permission = new ClubCreationPermissions();
                permission.setId(rs.getInt("ID"));
                permission.setUserID(rs.getString("UserID"));
                permission.setUserName(rs.getString("FullName"));
                permission.setStatus(rs.getString("Status"));
                Timestamp requestTimestamp = rs.getTimestamp("RequestDate");
                if (requestTimestamp != null) {
                    permission.setRequestDate(requestTimestamp.toLocalDateTime());
                }
                Timestamp processedTimestamp = rs.getTimestamp("ProcessedDate");
                if (processedTimestamp != null) {
                    permission.setProcessedDate(processedTimestamp.toLocalDateTime());
                }
                permission.setProcessedBy(rs.getString("ProcessedBy"));
                Timestamp grantedTimestamp = rs.getTimestamp("GrantedDate");
                if (grantedTimestamp != null) {
                    permission.setGrantedDate(grantedTimestamp.toLocalDateTime());
                }
                Timestamp usedTimestamp = rs.getTimestamp("UsedDate");
                if (usedTimestamp != null) {
                    permission.setUsedDate(usedTimestamp.toLocalDateTime());
                }
                permissions.add(permission);
            }
        } catch (SQLException e) {
            // Silent catch, return empty list
        }
        return permissions;
    }

    // Approve a request and transition to ACTIVE
    public boolean approveRequest(int id, String processedBy) {
        String query = "UPDATE ClubCreationPermissions SET Status = 'ACTIVE', ProcessedDate = NOW(), ProcessedBy = ?, GrantedDate = NOW() WHERE ID = ? AND Status = 'PENDING'";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, processedBy);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Reject a request
    public boolean rejectRequest(int id, String processedBy) {
        String query = "UPDATE ClubCreationPermissions SET Status = 'REJECTED', ProcessedDate = NOW(), ProcessedBy = ? WHERE ID = ? AND Status = 'PENDING'";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, processedBy);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Check for pending requests
    public boolean hasPendingRequest(String userID) {
        String query = "SELECT COUNT(*) FROM ClubCreationPermissions WHERE UserID = ? AND Status = 'PENDING'";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            // Silent catch
        }
        return false;
    }

    // Check for active permissions
    public boolean hasActivePermission(String userID) {
        String query = "SELECT COUNT(*) FROM ClubCreationPermissions WHERE UserID = ? AND Status = 'ACTIVE'";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            // Silent catch
        }
        return false;
    }

    // Mark permission as used
    public boolean markPermissionAsUsed(String userID) {
        String query = "UPDATE ClubCreationPermissions SET Status = 'USED', UsedDate = NOW() WHERE UserID = ? AND Status = 'ACTIVE'";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Revoke permission
    public boolean revokePermission(String userID) {
        String query = "UPDATE ClubCreationPermissions SET Status = 'INACTIVE' WHERE UserID = ? AND Status = 'ACTIVE'";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public int countActiveClubPermission(String userId) {
        int count = 0;
        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM ClubCreationPermissions WHERE UserID = ? AND Status = 'ACTIVE'"
            );
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public void insertClubPermission(String userId, String grantedBy) {
        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ClubCreationPermissions (UserID, Status, GrantedBy, GrantedDate) VALUES (?, 'ACTIVE', ?, NOW())");
            ps.setString(1, userId);
            ps.setString(2, grantedBy);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void revokeClubPermission(String userId) {
        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE ClubCreationPermissions SET Status = 'INACTIVE' WHERE UserID = ? AND Status = 'ACTIVE'"
            );
            ps.setString(1, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasActiveClubPermission(String userId) {
        boolean hasActive = false;
        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM ClubCreationPermissions WHERE UserID = ? AND Status = 'ACTIVE' LIMIT 1"
            );
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                hasActive = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasActive;
    }
}
