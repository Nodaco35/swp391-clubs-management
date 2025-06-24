/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.util.ArrayList;
import java.util.List;
import models.DepartmentMeeting;
import java.sql.*;

/**
 *
 * @author he181
 */
public class DepartmentMeetingDAO {

        public static List<DepartmentMeeting> findByUserID(String userID) {
            String sql = """
                         SELECT dm.*, 
                                d.DepartmentName, c.ClubName, c.ClubID, c.ClubImg
                         FROM DepartmentMeeting dm
                         JOIN ClubDepartments cd ON dm.ClubDepartmentID = cd.ClubDepartmentID
                         JOIN Clubs c ON cd.ClubID = c.ClubID
                         JOIN Departments d ON cd.DepartmentID = d.DepartmentID
                         JOIN UserClubs uc ON dm.ClubDepartmentID = uc.ClubDepartmentID
                         WHERE uc.UserID = ?
                         AND dm.StartedTime >= now() - INTERVAL 1 HOUR
                         ORDER BY dm.StartedTime ASC;""";
            List<DepartmentMeeting> findByUserID = new ArrayList<>();
            try {
                PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
                ps.setObject(1, userID);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    DepartmentMeeting dm = new DepartmentMeeting();
                    dm.setDepartmentMeetingID(rs.getInt("DepartmentMeetingID"));
                    dm.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                    dm.setURLMeeting(rs.getString("URLMeeting"));
                    dm.setStartedTime(rs.getTimestamp("StartedTime"));
                    dm.setDepartmentName(rs.getString("DepartmentName"));
                    dm.setClubName(rs.getString("ClubName"));
                    dm.setClubImg(rs.getString("ClubImg"));
                    findByUserID.add(dm);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return findByUserID;
    }

    public static int countByUID(String userID) {
        String sql = """
                     SELECT COUNT(*) as total_meetings
                     FROM DepartmentMeeting dm
                     JOIN ClubDepartments cd ON dm.ClubDepartmentID = cd.ClubDepartmentID
                     JOIN Clubs c ON cd.ClubID = c.ClubID
                     JOIN Departments d ON cd.DepartmentID = d.DepartmentID
                     JOIN UserClubs uc ON dm.ClubDepartmentID = uc.ClubDepartmentID
                     WHERE uc.UserID = ?
                     AND dm.StartedTime >= now() - INTERVAL 1 HOUR;""";
        int cnt = 0;
        try {
           PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, userID);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cnt = rs.getInt("total_meetings");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cnt;
    }
    
    public static void createMeeting(int clubDepartmentID, String startedTime, String URLMeeting) {
        String sql = """
            INSERT INTO DepartmentMeeting (ClubDepartmentID, URLMeeting, StartedTime)
            VALUES (?, ?, ?)
            """;
        
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubDepartmentID);
            ps.setObject(2, URLMeeting);
            ps.setObject(3, startedTime);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
       
    }
    

    // Update an existing meeting
    public boolean updateMeeting(DepartmentMeeting meeting) {
        String sql = """
            UPDATE DepartmentMeeting 
            SET URLMeeting = ?, StartedTime = ?
            WHERE DepartmentMeetingID = ? 
            AND ClubDepartmentID = ?
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, meeting.getURLMeeting());
            ps.setTimestamp(2, meeting.getStartedTime());
            ps.setInt(3, meeting.getDepartmentMeetingID());
            ps.setInt(4, meeting.getClubDepartmentID());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a meeting
    public boolean deleteMeeting(int meetingID, int clubDepartmentID) {
        String sql = """
            DELETE FROM DepartmentMeeting 
            WHERE DepartmentMeetingID = ? 
            AND ClubDepartmentID = ?
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, meetingID);
            ps.setInt(2, clubDepartmentID);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get meetings for a department (visible to all members)
    public List<DepartmentMeeting> getDepartmentMeetings(int clubDepartmentID, int page, int pageSize) {
        List<DepartmentMeeting> meetings = new ArrayList<>();
        String sql = """
            SELECT dm.*, 
                   d.DepartmentName, c.ClubName, c.ClubID, c.ClubImg
            FROM DepartmentMeeting dm
            JOIN ClubDepartments cd ON dm.ClubDepartmentID = cd.ClubDepartmentID
            JOIN Clubs c ON cd.ClubID = c.ClubID
            JOIN Departments d ON cd.DepartmentID = d.DepartmentID
            WHERE dm.ClubDepartmentID = ?
            ORDER BY dm.StartedTime DESC
            LIMIT ? OFFSET ?
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, clubDepartmentID);
            ps.setInt(2, pageSize);
            ps.setInt(3, (page - 1) * pageSize);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DepartmentMeeting dm = new DepartmentMeeting();
                dm.setDepartmentMeetingID(rs.getInt("DepartmentMeetingID"));
                dm.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                dm.setURLMeeting(rs.getString("URLMeeting"));
                dm.setStartedTime(rs.getTimestamp("StartedTime"));
                dm.setDepartmentName(rs.getString("DepartmentName"));
                dm.setClubName(rs.getString("ClubName"));
                dm.setClubImg(rs.getString("ClubImg"));
                meetings.add(dm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meetings;
    }

    // Get total count of meetings for pagination
    public int getTotalMeetingsCount(int clubDepartmentID) {
        String sql = """
            SELECT COUNT(*) as total
            FROM DepartmentMeeting
            WHERE ClubDepartmentID = ?
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, clubDepartmentID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get upcoming meetings for notification (within 24 hours)
    public List<DepartmentMeeting> getUpcomingMeetings(int clubDepartmentID) {
        List<DepartmentMeeting> meetings = new ArrayList<>();
        String sql = """
            SELECT dm.*, 
                   d.DepartmentName, c.ClubName, c.ClubID, c.ClubImg
            FROM DepartmentMeeting dm
            JOIN ClubDepartments cd ON dm.ClubDepartmentID = cd.ClubDepartmentID
            JOIN Clubs c ON cd.ClubID = c.ClubID
            JOIN Departments d ON cd.DepartmentID = d.DepartmentID
            WHERE dm.ClubDepartmentID = ?
            AND dm.StartedTime BETWEEN NOW() AND NOW() + INTERVAL 24 HOUR
            ORDER BY dm.StartedTime ASC
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, clubDepartmentID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DepartmentMeeting dm = new DepartmentMeeting();
                dm.setDepartmentMeetingID(rs.getInt("DepartmentMeetingID"));
                dm.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                dm.setURLMeeting(rs.getString("URLMeeting"));
                dm.setStartedTime(rs.getTimestamp("StartedTime"));
                dm.setDepartmentName(rs.getString("DepartmentName"));
                dm.setClubName(rs.getString("ClubName"));
                dm.setClubImg(rs.getString("ClubImg"));
                meetings.add(dm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meetings;
    }
    
    // Check if user is a leader for a specific ClubDepartmentID
    public boolean isDepartmentLeaderForClubDepartment(String userId, int clubDepartmentID) {
        String sql = """
            SELECT COUNT(*) as count
            FROM UserClubs uc
            WHERE uc.UserID = ? 
            AND uc.ClubDepartmentID = ? 
            AND uc.RoleID = 3 
            AND uc.IsActive = 1
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            ps.setInt(2, clubDepartmentID);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<DepartmentMeeting> searchMeetings(int clubDepartmentID, String keyword, String status, int page, int pageSize) {
        List<DepartmentMeeting> meetings = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT dm.*, 
                   d.DepartmentName, c.ClubName, c.ClubID, c.ClubImg
            FROM DepartmentMeeting dm
            JOIN ClubDepartments cd ON dm.ClubDepartmentID = cd.ClubDepartmentID
            JOIN Clubs c ON cd.ClubID = c.ClubID
            JOIN Departments d ON cd.DepartmentID = d.DepartmentID
            WHERE dm.ClubDepartmentID = ?
        """);
        
        if (!keyword.isEmpty()) {
            sql.append(" AND (dm.URLMeeting LIKE ? OR CAST(dm.StartedTime AS CHAR) LIKE ?)");
        }
        
        if (!status.isEmpty()) {
            if (status.equals("upcoming")) {
                sql.append(" AND dm.StartedTime > NOW()");
            } else if (status.equals("past")) {
                sql.append(" AND dm.StartedTime <= NOW()");
            }
        }
        
        sql.append(" ORDER BY dm.StartedTime DESC LIMIT ? OFFSET ?");
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            ps.setInt(paramIndex++, clubDepartmentID);
            
            if (!keyword.isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex, (page - 1) * pageSize);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DepartmentMeeting dm = new DepartmentMeeting();
                dm.setDepartmentMeetingID(rs.getInt("DepartmentMeetingID"));
                dm.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                dm.setURLMeeting(rs.getString("URLMeeting"));
                dm.setStartedTime(rs.getTimestamp("StartedTime"));
                dm.setDepartmentName(rs.getString("DepartmentName"));
                dm.setClubName(rs.getString("ClubName"));
                dm.setClubImg(rs.getString("ClubImg"));
                meetings.add(dm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meetings;
    }

    public int getTotalMeetingsCountWithFilter(int clubDepartmentID, String keyword, String status) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) as total
            FROM DepartmentMeeting
            WHERE ClubDepartmentID = ?
        """);
        
        if (!keyword.isEmpty()) {
            sql.append(" AND (URLMeeting LIKE ? OR CAST(StartedTime AS CHAR) LIKE ?)");
        }
        
        if (!status.isEmpty()) {
            if (status.equals("upcoming")) {
                sql.append(" AND StartedTime > NOW()");
            } else if (status.equals("past")) {
                sql.append(" AND StartedTime <= NOW()");
            }
        }
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            ps.setInt(paramIndex++, clubDepartmentID);
            
            if (!keyword.isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}