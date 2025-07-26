package dal;

import java.util.ArrayList;
import java.util.List;
import models.ClubApprovalHistory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ApprovalHistoryDAO {

    public ApprovalHistoryDAO() {
    }

    public boolean insertApprovalRecord(int clubId, String actionType, String reason, String requestType) {
        String sql = "INSERT INTO ClubApprovalHistory (ClubID, ActionType, Reason, RequestType) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setInt(1, clubId);
            ps.setString(2, actionType);     // "Approved", "Rejected", "Pending", "Deleted" (nếu bạn mở rộng enum)
            ps.setString(3, reason);         // Có thể null nếu không cần lý do
            ps.setString(4, requestType);    // "Create" hoặc "Update" hoặc null
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean approveRequest(int clubId) {
        String sql = "UPDATE Clubs "
                + "SET ClubRequestStatus = 'Approved', "
                + "    ClubStatus = 1, "
                + "    LastRejectReason = NULL "
                + "WHERE ClubID = ? ";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setInt(1, clubId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean rejectRequest(int clubId, String reason) {
        String sql = """
                     UPDATE Clubs SET ClubRequestStatus = 'Rejected',
                                         LastRejectReason = ?,
                                         ClubStatus = 0
                                     WHERE ClubID = ?
                     """;
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setString(1, reason);
            ps.setInt(2, clubId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteClubRequest(int clubId) {
        String sql = "DELETE FROM Clubs WHERE ClubID = ?";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setInt(1, clubId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ClubApprovalHistory> getHistoryByClubId(int clubId) {
        List<ClubApprovalHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM ClubApprovalHistory WHERE ClubID = ? ORDER BY ActionAt ASC";

        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setInt(1, clubId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ClubApprovalHistory history = new ClubApprovalHistory();
                history.setId(rs.getInt("HistoryID"));
                history.setClubID(rs.getInt("ClubID"));
                history.setActionType(rs.getString("ActionType"));
                history.setReason(rs.getString("Reason"));
                history.setRequestType(rs.getString("RequestType"));
                history.setActionAt(rs.getTimestamp("ActionAt"));

                list.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void approveTask(int taskId, String rating) {
        String sql = "UPDATE Tasks SET Status = 'Done', Rating = ? WHERE TaskID = ?";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setString(1, rating);
            ps.setInt(2, taskId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rejectTask(int taskId, String rejectReason) {
        String sql = "UPDATE Tasks SET Status = 'Rejected', LastRejectReason = ? WHERE TaskID = ?";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setString(1, rejectReason);
            ps.setInt(2, taskId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
