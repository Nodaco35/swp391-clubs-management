
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
    
    public static List<ClubPeriodicReport> getReportsByClubID(int clubID){
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
    
    
}
