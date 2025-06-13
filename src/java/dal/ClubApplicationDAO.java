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
