package dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClubCreationPermissionDAO {

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

    public void markPermissionAsUsed(String userId) {
        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE ClubCreationPermissions SET Status = 'USED', UsedDate = NOW() WHERE UserID = ? AND Status = 'ACTIVE'"
            );
            ps.setString(1, userId);
            ps.executeUpdate();
            ps.close();
            DBContext.closeConnection(conn);
        } catch (SQLException e) {
            System.out.println("Error marking permission as used: " + e.getMessage());
        }
    }
}
