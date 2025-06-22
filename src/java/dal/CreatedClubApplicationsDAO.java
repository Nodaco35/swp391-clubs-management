package dal;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;
import models.CreatedClubApplications;

public class CreatedClubApplicationsDAO {

    public int countRequestsByStatus(String status) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM CreatedClubApplications WHERE Status = ?";

        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement state = conn.prepareStatement(sql);
            state.setString(1, status);
            ResultSet rs = state.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            state.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    private Connection conn;

    public CreatedClubApplicationsDAO() {
        try {
            conn = DBContext.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int countPendingRequests() {
        String sql = "SELECT COUNT(*) FROM ClubCreationPermissions WHERE Status = 'PENDING'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public boolean isClubNameTaken(String clubName) {
        String query = "SELECT COUNT(*) FROM Clubs WHERE ClubName = ? " +
                      "UNION ALL " +
                      "SELECT COUNT(*) FROM ClubCreationPermissions WHERE ClubName = ? AND Status IN ('PENDING', 'APPROVED')";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, clubName.trim());
            stmt.setString(2, clubName.trim());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertRequest(String userID, String clubName, String category) {
        if (userID == null || userID.trim().isEmpty() || clubName == null || clubName.trim().isEmpty() || category == null || category.trim().isEmpty()) {
            return false;
        }
        if (isClubNameTaken(clubName)) {
            return false;
        }
        String query = "INSERT INTO ClubCreationPermissions (UserID, ClubName, Category, Status, RequestDate) VALUES (?, ?, ?, 'PENDING', NOW())";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID.trim());
            stmt.setString(2, clubName.trim());
            stmt.setString(3, category.trim());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CreatedClubApplications> getApplicationsByStatus(String status) {
        List<CreatedClubApplications> applications = new ArrayList<>();
        String query = "SELECT p.*, u.FullName FROM ClubCreationPermissions p " +
                      "JOIN Users u ON p.UserID = u.UserID " +
                      "WHERE p.Status = ? ORDER BY p.RequestDate DESC";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CreatedClubApplications application = new CreatedClubApplications();
                application.setId(rs.getInt("ID"));
                application.setUserID(rs.getString("UserID"));
                application.setUserName(rs.getString("FullName"));
                application.setClubName(rs.getString("ClubName"));
                application.setCategory(rs.getString("Category"));
                application.setStatus(rs.getString("Status"));
                Timestamp requestTimestamp = rs.getTimestamp("RequestDate");
                if (requestTimestamp != null) {
                    application.setRequestDate(requestTimestamp.toLocalDateTime());
                }
                Timestamp processedTimestamp = rs.getTimestamp("ProcessedDate");
                if (processedTimestamp != null) {
                    application.setProcessedDate(processedTimestamp.toLocalDateTime());
                }
                application.setProcessedBy(rs.getString("ProcessedBy"));
                Timestamp grantedTimestamp = rs.getTimestamp("GrantedDate");
                if (grantedTimestamp != null) {
                    application.setGrantedDate(grantedTimestamp.toLocalDateTime());
                }
                Timestamp usedTimestamp = rs.getTimestamp("UsedDate");
                if (usedTimestamp != null) {
                    application.setUsedDate(usedTimestamp.toLocalDateTime());
                }
                applications.add(application);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    public boolean approveRequest(int id, String processedBy) {
        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            String approveQuery = "UPDATE ClubCreationPermissions SET Status = 'APPROVED', ProcessedDate = NOW(), ProcessedBy = ?, GrantedDate = NOW() WHERE ID = ? AND Status = 'PENDING'";
            try (PreparedStatement stmt = conn.prepareStatement(approveQuery)) {
                stmt.setString(1, processedBy);
                stmt.setInt(2, id);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated == 0) {
                    conn.rollback();
                    return false;
                }
            }

            String rejectQuery = "UPDATE ClubCreationPermissions SET Status = 'REJECTED', ProcessedDate = NOW(), ProcessedBy = ? WHERE UserID = (SELECT UserID FROM ClubCreationPermissions WHERE ID = ?) AND ID != ? AND Status = 'PENDING'";
            try (PreparedStatement stmt = conn.prepareStatement(rejectQuery)) {
                stmt.setString(1, processedBy);
                stmt.setInt(2, id);
                stmt.setInt(3, id);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    public boolean rejectRequest(int id, String processedBy) {
        String query = "UPDATE ClubCreationPermissions SET Status = 'REJECTED', ProcessedDate = NOW(), ProcessedBy = ? WHERE ID = ? AND Status = 'PENDING'";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, processedBy);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasPendingRequest(String userID) {
        String query = "SELECT COUNT(*) FROM ClubCreationPermissions WHERE UserID = ? AND Status = 'PENDING'";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasActiveApplication(String userID) {
        String query = "SELECT COUNT(*) FROM ClubCreationPermissions WHERE UserID = ? AND Status = 'APPROVED'";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public CreatedClubApplications getActiveApplication(String userID) {
        String query = "SELECT ID, ClubName, Category FROM ClubCreationPermissions WHERE UserID = ? AND Status = 'APPROVED' ORDER BY GrantedDate DESC LIMIT 1";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                CreatedClubApplications application = new CreatedClubApplications();
                application.setId(rs.getInt("ID"));
                application.setClubName(rs.getString("ClubName"));
                application.setCategory(rs.getString("Category"));
                return application;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean markApplicationAsUsed(String userID) {
        String query = "UPDATE ClubCreationPermissions SET Status = 'USED', UsedDate = NOW() WHERE UserID = ? AND Status = 'APPROVED'";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean revokeApplication(String userID) {
        String query = "UPDATE ClubCreationPermissions SET Status = 'INACTIVE' WHERE UserID = ? AND Status = 'APPROVED'";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int countActiveClubApplication(String userId) {
        int count = 0;
        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM ClubCreationPermissions WHERE UserID = ? AND Status = 'APPROVED'"
            );
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void insertClubApplication(String userId, String grantedBy) {
        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ClubCreationPermissions (UserID, Status, ProcessedBy, GrantedDate) VALUES (?, 'APPROVED', ?, NOW())");
            ps.setString(1, userId);
            ps.setString(2, grantedBy);
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void revokeClubApplication(String userId) {
        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE ClubCreationPermissions SET Status = 'INACTIVE' WHERE UserID = ? AND Status = 'APPROVED'"
            );
            ps.setString(1, userId);
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     public boolean hasActiveClubPermission(String userId) {
        boolean hasActive = false;
        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM ClubCreationPermissions WHERE UserID = ? AND Status = 'APPROVED' LIMIT 1"
            );
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                hasActive = true;
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hasActive;
    }
}
