package dal;

import models.PeriodicReport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.ActivedMemberClubs;
import models.ClubEvent;
import models.EventScheduleDetail;
import models.PeriodicReportEvents;
import models.PeriodicReport_MemberAchievements;
import models.Semesters;

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

    public String getTermIDByReportID(int reportID) {
        String termName = null;

        String sql = """
        SELECT s.TermID
        FROM PeriodicClubReport p
        JOIN Semesters s ON p.Term = s.TermID
        WHERE p.ReportID = ?
    """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reportID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    termName = rs.getString("TermID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return termName;
    }

    public String getActiveTermID() {
        String termName = null;

        String sql = """
        SELECT TermID FROM Semesters where Status = 'ACTIVE';
    """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    termName = rs.getString("TermID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return termName;
    }

    public Semesters getCurrentTerm() {
        String sql = "SELECT * FROM Semesters WHERE Status = 'ACTIVE'";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Semesters(
                        rs.getString("TermID"),
                        rs.getString("TermName"),
                        rs.getDate("StartDate"),
                        rs.getDate("EndDate"),
                        rs.getString("Status")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ActivedMemberClubs> getActiveMembersForReport(String termID, int clubID) {
        List<ActivedMemberClubs> list = new ArrayList<>();
        String sql = """
        SELECT a.ActiveID, a.UserID, u.FullName, u.UserID, a.ProgressPoint, r.RoleName, d.DepartmentName
                FROM ActivedMemberClubs a
                JOIN Users u ON a.UserID = u.UserID
                join userclubs uc on u.UserID = uc.UserID
                join clubdepartments cd on uc.ClubDepartmentID = cd.ClubDepartmentID
                join departments d on d.DepartmentID = cd.DepartmentID
                join roles r on r.RoleID = uc.RoleID
                WHERE a.ClubID = ? AND a.TermID = ? AND a.IsActive = TRUE
                order by RoleName desc, d.DepartmentName asc;
    """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clubID);
            ps.setString(2, termID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ActivedMemberClubs am = new ActivedMemberClubs();
                am.setActiveID(rs.getInt("ActiveID"));
                am.setUserID(rs.getString("UserID"));
                am.setFullName(rs.getString("FullName"));
                am.setStudentCode(rs.getString("UserID"));
                am.setProgressPoint(rs.getInt("ProgressPoint"));
                am.setRole(rs.getString("RoleName"));
                am.setDepartment(rs.getString("DepartmentName"));
                list.add(am);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getClubIdByChairmanId(String chairmanId) {
        Integer clubId = null;
        String sql = "SELECT ClubID FROM UserClubs WHERE UserID = ? AND RoleID = 1";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, chairmanId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                clubId = rs.getInt("ClubID");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return clubId;
    }

    public List<ClubEvent> getPublicEventsWithSchedules(String semesterId, int clubID) {
        List<ClubEvent> events = new ArrayList<>();
        String eventQuery = """
        SELECT e.EventID, e.EventName, COUNT(ep.UserID) AS ParticipantCount
        FROM Events e
        LEFT JOIN EventParticipants ep ON e.EventID = ep.EventID 
        WHERE e.SemesterID = ? AND e.IsPublic = 1 AND e.Status = 'COMPLETED' and e.ClubID = ?
        GROUP BY e.EventID, e.EventName
    """;

        String scheduleQuery = """
        SELECT EventID, EventDate, StartTime, EndTime, l.LocationName
        FROM EventSchedules es
        JOIN Locations l ON es.LocationID = l.LocationID
        WHERE EventID = ?
        ORDER BY EventDate, StartTime
    """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(eventQuery)) {

            ps.setString(1, semesterId);
            ps.setInt(2, clubID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ClubEvent event = new ClubEvent();
                event.setEventID(rs.getInt("EventID"));
                event.setEventName(rs.getString("EventName"));
                event.setParticipantCount(rs.getInt("ParticipantCount"));

                // Lấy danh sách lịch
                try (PreparedStatement psSchedule = conn.prepareStatement(scheduleQuery)) {
                    psSchedule.setInt(1, event.getEventID());
                    ResultSet rsSchedule = psSchedule.executeQuery();
                    List<EventScheduleDetail> scheduleList = new ArrayList<>();

                    while (rsSchedule.next()) {
                        EventScheduleDetail detail = new EventScheduleDetail();
                        detail.setEventDate(rsSchedule.getDate("EventDate"));
                        detail.setStartTime(rsSchedule.getString("StartTime"));
                        detail.setEndTime(rsSchedule.getString("EndTime"));
                        detail.setLocationName(rsSchedule.getString("LocationName"));
                        scheduleList.add(detail);
                    }
                    event.setScheduleList(scheduleList);
                }

                events.add(event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }

    public void updateProgressPoint(String userId, int clubId, String termId, int point) {
        String sql = "UPDATE ActivedMemberClubs SET ProgressPoint = ? "
                + "WHERE UserID = ? AND ClubID = ? AND TermID = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, point);
            ps.setString(2, userId);
            ps.setInt(3, clubId);
            ps.setString(4, termId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int insertClubReport(int clubID, String termID) {
        int generatedID = -1;
        String sql = "INSERT INTO PeriodicClubReport (ClubID, Term) VALUES (?, ?)";

        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, clubID);
            ps.setString(2, termID);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedID = rs.getInt(1); // Lấy ReportID vừa tạo
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedID;
    }

    public Integer getReportIdByClubAndTerm(int clubId, String termId) {
        String sql = "SELECT ReportID FROM PeriodicClubReport WHERE ClubID = ? AND Term = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clubId);
            stmt.setString(2, termId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ReportID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Không tìm thấy hoặc lỗi
    }

    public boolean updateProgressPointFromReport(String userID, String termID, int point) {
        String sql = "UPDATE ActivedMemberClubs "
                + "SET ProgressPoint = ? "
                + "WHERE UserID = ? AND TermID = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, point);
            stmt.setString(2, userID);
            stmt.setString(3, termID);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateReportLastUpdatedByClubAndTerm(int clubID, String termID) {
    String sql = "UPDATE PeriodicClubReport SET LastUpdated = CURRENT_DATE WHERE ClubID = ? AND Term = ?";
    try (Connection conn = DBContext.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, clubID);
        ps.setString(2, termID);
        int rows = ps.executeUpdate();
        return rows > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


}
