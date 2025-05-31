package dal;

import models.PeriodicReport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeriodicReportDAO extends DBContext {

    // Đếm báo cáo theo trạng thái
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM PeriodicClubReport WHERE Status = ?";
        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement state = conn.prepareStatement(sql);
            state.setString(1, status);
            ResultSet rs = state.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // Đếm số CLB chưa nộp báo cáo trong kỳ gần nhất
    public int countOverdueClubs() {
        String sql = """
            SELECT COUNT(*) FROM Clubs c
            WHERE c.ClubID NOT IN (
                SELECT DISTINCT ClubID FROM PeriodicClubReport
                WHERE Term = (SELECT MAX(Term) FROM PeriodicClubReport)
            )
        """;
        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement state = conn.prepareStatement(sql);
            ResultSet rs = state.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // Lấy danh sách báo cáo theo trạng thái
    public List<PeriodicReport> getReportsByStatus(String status) {
        List<PeriodicReport> list = new ArrayList<>();
        String sql = """
        SELECT r.ReportID, r.Term, c.ClubName, u.FullName AS SubmittedBy, 
               r.Status, r.SubmissionDate,
               (SELECT COUNT(*) FROM PeriodicReportMemberStats m WHERE m.ReportID = r.ReportID) AS MemberCount,
               (SELECT COUNT(*) FROM PeriodicReportEvents e WHERE e.ReportID = r.ReportID) AS EventCount
        FROM PeriodicClubReport r
        JOIN Clubs c ON r.ClubID = c.ClubID
        JOIN Users u ON r.SubmittedBy = u.UserID
        WHERE r.Status = ?
        ORDER BY r.SubmissionDate DESC
    """;

        try {
            Connection conn = DBContext.getConnection();
            PreparedStatement state = conn.prepareStatement(sql);
            state.setString(1, status);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                PeriodicReport pr = new PeriodicReport();
                pr.setReportId(rs.getInt("ReportID"));
                pr.setTerm(rs.getString("Term"));
                pr.setClubName(rs.getString("ClubName"));
                pr.setSubmittedBy(rs.getString("SubmittedBy"));
                pr.setStatus(rs.getString("Status"));
                pr.setSubmissionDate(rs.getTimestamp("SubmissionDate"));
                pr.setMemberCount(rs.getInt("MemberCount"));
                pr.setEventCount(rs.getInt("EventCount"));
                list.add(pr);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

}
