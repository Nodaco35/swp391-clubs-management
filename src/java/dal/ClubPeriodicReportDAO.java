package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.ClubInfo;
import models.Clubs;
import models.Department;
import models.ClubPeriodicReport;

public class ClubPeriodicReportDAO {

    public ClubPeriodicReportDAO() {
    }

    public static List<ClubPeriodicReport> getReportsByClubID(int clubID) {
        List<ClubPeriodicReport> list = new ArrayList<>();
        String sql = """
                     SELECT * FROM PeriodicClubReport WHERE ClubID = ? ORDER BY SubmissionDate DESC;
                     """;

        try {

            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ClubPeriodicReport report = new ClubPeriodicReport();
                report.setReportID(rs.getInt("ReportID"));
                report.setClubID(rs.getInt("ClubID"));
                report.setTerm(rs.getString("Term"));
                report.setSubmissionDate(rs.getTimestamp("SubmissionDate"));
                list.add(report);
            }
            return list;

        } catch (Exception e) {

        }

        return list;
    }

    public List<ClubPeriodicReport> getAllReportsSortedByDate() {
        List<ClubPeriodicReport> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT * FROM PeriodicClubReport pr 
                         			join clubs c on c.ClubID = pr.ClubID
                         ORDER BY SubmissionDate DESC;
                         """;
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ClubPeriodicReport report = new ClubPeriodicReport();
                report.setReportID(rs.getInt("ReportID"));
                report.setClubID(rs.getInt("ClubID"));
                report.setClubName(rs.getString("ClubName"));
                report.setTerm(rs.getString("Term"));
                report.setSubmissionDate(rs.getTimestamp("SubmissionDate"));
                list.add(report);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countMembersByClubAndTerm(int clubId, String termId) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM ActivedMemberClubs WHERE ClubID = ? AND TermID = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            ps.setString(2, termId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public int countEventsByClubAndTerm(int clubId, String termId) {
        int count = 0;
        String sql = """
        SELECT COUNT(*)
                FROM events e
                JOIN semesters s ON e.SemesterID = s.TermID
                WHERE e.ClubID = ? AND s.TermID = ? and e.Status = 'Completed' and IsPublic = 1;
    """;
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            ps.setString(2, termId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

}
