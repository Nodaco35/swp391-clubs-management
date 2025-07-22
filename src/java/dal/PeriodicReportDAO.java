package dal;

import models.PeriodicReport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.PeriodicReportEvents;
import models.PeriodicReport_MemberAchievements;

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

    public List<PeriodicReport_MemberAchievements> getMemberAchievementsByReportID(int reportID) {
        List<PeriodicReport_MemberAchievements> list = new ArrayList<>();

        String sql = """
                SELECT r.AchievementID, m.ReportID, r.MemberID, u.FullName, u.UserID, r.Role, r.ProgressPoint
                FROM PeriodicReport_MemberAchievements r
                JOIN Users u ON r.MemberID = u.UserID
                JOIN PeriodicClubReport m ON m.ReportID = r.ReportID
                WHERE r.ReportID = ?
    """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reportID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PeriodicReport_MemberAchievements a = new PeriodicReport_MemberAchievements();
                    a.setAchievementID(rs.getInt("AchievementID"));
                    a.setReportID(rs.getInt("ReportID"));
                    a.setMemberID(rs.getString("MemberID"));
                    a.setFullName(rs.getString("FullName"));       // cần có trong model
                    a.setStudentCode(rs.getString("UserID")); // cần có trong model
                    a.setRole(rs.getString("Role"));
                    a.setProgressPoint(rs.getInt("ProgressPoint"));
                    list.add(a);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<PeriodicReportEvents> getEventReportsByReportID(int reportID) {
        List<PeriodicReportEvents> list = new ArrayList<>();

        String sql = """
        SELECT ReportEventID, ReportID, EventName, EventDate, EventType, ParticipantCount, ProofLink
        FROM PeriodicReportEvents
        WHERE ReportID = ?
    """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reportID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PeriodicReportEvents e = new PeriodicReportEvents();
                    e.setReportEventID(rs.getInt("ReportEventID"));
                    e.setReportID(rs.getInt("ReportID"));
                    e.setEventName(rs.getString("EventName"));
                    e.setEventDate(rs.getDate("EventDate"));
                    e.setEventType(rs.getString("EventType"));
                    e.setParticipantCount(rs.getInt("ParticipantCount"));
                    e.setProofLink(rs.getString("ProofLink"));
                    list.add(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public String getTermNameByReportID(int reportID) {
        String termName = null;

        String sql = """
        SELECT s.TermName
        FROM PeriodicClubReport p
        JOIN Semesters s ON p.Term = s.TermID
        WHERE p.ReportID = ?
    """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reportID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    termName = rs.getString("TermName");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return termName;
    }

}
