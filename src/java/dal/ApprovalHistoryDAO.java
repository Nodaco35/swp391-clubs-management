package dal;

import java.util.ArrayList;
import java.util.List;
import models.ClubApprovalHistory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
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

    public boolean approveUpdateRequest(int clubId) {
        String sql = "UPDATE Clubs "
                + "SET ClubRequestStatus = 'Approved', "
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

    public boolean updateOriginalClubFromPendingUpdate(int clubId) {
        Connection conn = null;
        PreparedStatement psGetUpdate = null;
        PreparedStatement psApplyUpdate = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();

            String sqlGetUpdate = "SELECT * FROM Clubs WHERE ParentClubID = ?";
            psGetUpdate = conn.prepareStatement(sqlGetUpdate);
            psGetUpdate.setInt(1, clubId);
            rs = psGetUpdate.executeQuery();

            if (rs.next()) {
                String sqlApplyUpdate = "UPDATE Clubs SET ClubImg = ?, IsRecruiting = ?, ClubName = ?, Description = ?, "
                        + "CategoryID = ?, EstablishedDate = ?, ContactPhone = ?, ContactGmail = ?, "
                        + "ContactURL = ?, ClubStatus = ? ,"
                        + "ClubRequestStatus = 'Approved' ,"
                        + "LastRejectReason = NULL "
                        + "WHERE ClubID = ?";

                psApplyUpdate = conn.prepareStatement(sqlApplyUpdate);
                psApplyUpdate.setString(1, rs.getString("ClubImg"));
                psApplyUpdate.setBoolean(2, rs.getBoolean("IsRecruiting"));
                psApplyUpdate.setString(3, rs.getString("ClubName"));
                psApplyUpdate.setString(4, rs.getString("Description"));
                psApplyUpdate.setInt(5, rs.getInt("CategoryID"));
                psApplyUpdate.setDate(6, rs.getDate("EstablishedDate"));
                psApplyUpdate.setString(7, rs.getString("ContactPhone"));
                psApplyUpdate.setString(8, rs.getString("ContactGmail"));
                psApplyUpdate.setString(9, rs.getString("ContactURL"));
                psApplyUpdate.setBoolean(10, rs.getBoolean("ClubStatus"));
                psApplyUpdate.setInt(11, clubId);

                return psApplyUpdate.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (psGetUpdate != null) {
                    psGetUpdate.close();
                }
                if (psApplyUpdate != null) {
                    psApplyUpdate.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public boolean deleteCreateClubRequest(int clubId) {
        String sqlUserClubs = "DELETE FROM UserClubs WHERE ClubID = ?";
        String sqlClubs = "DELETE FROM Clubs WHERE ClubID = ?";

        try {
            Connection conn = DBContext.getConnection();

            // Xóa trong bảng UserClubs trước
            PreparedStatement psUserClubs = conn.prepareStatement(sqlUserClubs);
            psUserClubs.setInt(1, clubId);
            psUserClubs.executeUpdate();

            // Xóa trong bảng Clubs sau
            PreparedStatement psClubs = conn.prepareStatement(sqlClubs);
            psClubs.setInt(1, clubId);
            int result = psClubs.executeUpdate();

            return result > 0; // Chỉ cần check bảng chính, hoặc thêm logic tùy bạn
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteUpdateClubRequest(int clubId) {
        String sqlClubs = "DELETE FROM Clubs WHERE ParentClubID = ?";

        try {
            Connection conn = DBContext.getConnection();


            // Xóa trong bảng Clubs sau
            PreparedStatement psClubs = conn.prepareStatement(sqlClubs);
            psClubs.setInt(1, clubId);
            int result = psClubs.executeUpdate();

            return result > 0; // Chỉ cần check bảng chính, hoặc thêm logic tùy bạn
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
