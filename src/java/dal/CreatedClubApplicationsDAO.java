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

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<CreatedClubApplications> getRequestsByStatus(String status, int limit) {
        List<CreatedClubApplications> list = new ArrayList<>();
        String sql = "SELECT cca.*, c.ClubName FROM CreatedClubApplications cca " +
                     "JOIN Clubs c ON cca.ClubID = c.ClubID " +
                     "WHERE cca.Status = ? ORDER BY cca.SubmitDate DESC LIMIT ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CreatedClubApplications c = new CreatedClubApplications();
                c.setApplicationID(rs.getInt("ApplicationID"));
                c.setUserID(rs.getString("UserID"));
                c.setClubID(rs.getInt("ClubID"));
                c.setEmail(rs.getString("Email"));
                c.setStatus(rs.getString("Status"));
                c.setSubmitDate(rs.getTimestamp("SubmitDate"));
                c.setClubName(rs.getString("ClubName"));
                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<CreatedClubApplications> getRequestsByStatus(String status) {
        return getRequestsByStatus(status, Integer.MAX_VALUE);
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

    public List<CreatedClubApplications> getPendingRequests(int limit) {
        List<CreatedClubApplications> list = new ArrayList<>();
        String sql = "SELECT cca.*, c.ClubName FROM CreatedClubApplications cca " +
                     "JOIN Clubs c ON cca.ClubID = c.ClubID " +
                     "WHERE cca.Status = 'PENDING' ORDER BY cca.SubmitDate DESC LIMIT ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CreatedClubApplications c = new CreatedClubApplications();
                c.setApplicationID(rs.getInt("ApplicationID"));
                c.setUserID(rs.getString("UserID"));
                c.setClubID(rs.getInt("ClubID"));
                c.setEmail(rs.getString("Email"));
                c.setStatus(rs.getString("Status"));
                c.setSubmitDate(rs.getTimestamp("SubmitDate"));
                c.setClubName(rs.getString("ClubName"));
                list.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
