package dal;

import java.sql.Connection;
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

    public List<ClubApplication> getPendingRequests(int limit) {
    List<ClubApplication> list = new ArrayList<>();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
        conn = DBContext.getConnection();
        String sql = "SELECT * FROM ClubApplications WHERE Status = 'PENDING' ORDER BY SubmitDate DESC LIMIT ?";
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, limit);
        rs = stmt.executeQuery();

        while (rs.next()) {
            ClubApplication app = new ClubApplication();
            app.setApplicationID(rs.getInt("ApplicationID"));
            app.setUserID(rs.getString("UserID"));
            app.setClubName(rs.getString("ClubName"));
            app.setDescription(rs.getString("Description"));
            app.setEmail(rs.getString("Email"));
            app.setPhone(rs.getString("Phone"));
            app.setStatus(rs.getString("Status"));
            app.setSubmitDate(rs.getTimestamp("SubmitDate"));

            list.add(app);
        }
    } catch (SQLException e) {
        System.out.println("Error getting pending club applications: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DBContext.closeConnection(conn);
        } catch (SQLException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }

    return list;
}

}
