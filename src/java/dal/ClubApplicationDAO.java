package dal;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.ClubApplication;

public class ClubApplicationDAO {
    //DAO
    public List<ClubApplication> getClubApplicationsByUser(String userID) {
        List<ClubApplication> list = new ArrayList<>();
        String sql = """
            SELECT ca.ApplicationID, ca.UserID, ca.ClubID, ca.Email, ca.Status, ca.SubmitDate, c.ClubName
            FROM ClubApplications ca
            JOIN Clubs c ON ca.ClubID = c.ClubID
            WHERE ca.UserID = ?
            ORDER BY ca.SubmitDate DESC
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ClubApplication ca = new ClubApplication();
                ca.setApplicationId(rs.getInt("ApplicationID"));
                ca.setUserId(rs.getString("UserID"));
                ca.setClubId(rs.getInt("ClubID"));
                ca.setEmail(rs.getString("Email"));
                ca.setStatus(rs.getString("Status"));
                ca.setSubmitDate(rs.getTimestamp("SubmitDate"));
                ca.setClubName(rs.getString("ClubName"));

                list.add(ca);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
   public static List<ClubApplication> pendingApplicationsFindByClub(String userID) {
        String sql = """
                     SELECT ca.*, u.FullName, c.ClubName
                     FROM ClubApplications ca
                     JOIN Users u ON ca.UserID = u.UserID
                     JOIN UserClubs uc ON ca.ClubID = uc.ClubID
                     JOIN Clubs c on ca.ClubID = c.ClubID
                     WHERE uc.UserID = ? AND uc.RoleID = 1 AND ca.Status = 'PENDING'
                     ORDER BY ca.SubmitDate DESC;""";
        List<ClubApplication> list = new ArrayList<>();
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, userID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                ClubApplication app = new ClubApplication();
            app.setApplicationId(rs.getInt("ApplicationID"));
            app.setUserId(rs.getString("UserID"));
            app.setClubID( rs.getInt("ClubID"));
            app.setClubName(rs.getString("ClubName"));
            app.setEmail(rs.getString("Email"));
            app.setStatus(rs.getString("Status"));
            app.setSubmitDate(rs.getTimestamp("SubmitDate"));
            app.setUserName(rs.getString("FullName"));
            list.add(app);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int countpendingApplicationsFindByClub(String userID) {
        int count = 0;
        String sql = """
                    SELECT COUNT(*) as total 
                    FROM ClubApplications ca 
                    JOIN UserClubs uc ON ca.ClubID = uc.ClubID
                    WHERE uc.UserID = ? AND uc.RoleID = 1 AND ca.Status = 'PENDING';""";
        try {
            
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    
 

    public int countPendingRequests() {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String sql = "SELECT COUNT(*) FROM ClubApplications WHERE Status = 'PENDING'";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error counting pending club applications: " + e.getMessage());
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
    public void saveClubApplication(ClubApplication app) throws SQLException {
        Connection conn = DBContext.getConnection();
        String sql = "INSERT INTO ClubApplications (UserID, ClubID, Email, EventID, ResponseID, Status, SubmitDate) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, app.getUserId());
            ps.setInt(2, app.getClubId());
            ps.setString(3, app.getEmail());
            if (app.getEventId() != null) {
                ps.setInt(4, app.getEventId());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setInt(5, app.getResponseId());
            ps.setString(6, app.getStatus());
            if (app.getSubmitDate() != null) {
                ps.setTimestamp(7, (java.sql.Timestamp) app.getSubmitDate());
            } else {
                ps.setNull(7, java.sql.Types.TIMESTAMP);
            }
            ps.executeUpdate();
        }
    }

    

}
