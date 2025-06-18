package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.ClubApplication;

public class ClubApplicationDAO {

    public static List<ClubApplication> pendingApplicationsFindByClub(String userID) {
        String sql = """
                     SELECT ca.*, u.FullName, c.ClubName
                     FROM ClubApplications ca
                     JOIN Users u ON ca.UserID = u.UserID
                     JOIN UserClubs uc ON ca.ClubID = uc.ClubID
                     JOIN clubs c on ca.ClubID = c.ClubID
                     WHERE uc.UserID = ? AND uc.RoleID = 1 AND ca.Status = 'PENDING'
                     ORDER BY ca.SubmitDate DESC;""";
        List<ClubApplication> list = new ArrayList<>();
        try {
            PreparedStatement ps = DBContext_Duc.getInstance().connection.prepareStatement(sql);
            ps.setObject(1, userID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                ClubApplication app = new ClubApplication();
            app.setApplicationID(rs.getInt("ApplicationID"));
            app.setUserID(rs.getString("UserID"));
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
            
            PreparedStatement ps = DBContext_Duc.getInstance().connection.prepareStatement(sql);
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
