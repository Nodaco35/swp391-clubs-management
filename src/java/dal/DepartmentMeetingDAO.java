package dal;

import java.util.ArrayList;
import java.util.List;
import models.DepartmentMeeting;
import java.sql.*;
import models.Users;

public class DepartmentMeetingDAO {

    public static List<DepartmentMeeting> findByUserID(String userID) {
        String sql = """
                     SELECT dm.*, 
                            d.DepartmentName, c.ClubName, c.ClubID, c.ClubImg
                     FROM DepartmentMeeting dm
                     JOIN ClubDepartments cd ON dm.ClubDepartmentID = cd.ClubDepartmentID
                     JOIN Clubs c ON cd.ClubID = c.ClubID
                     JOIN Departments d ON cd.DepartmentID = d.DepartmentID
                     JOIN DepartmentMeetingParticipants dmp ON dm.DepartmentMeetingID = dmp.DepartmentMeetingID
                     WHERE dmp.UserID = ?
                     AND dm.StartedTime >= now() - INTERVAL 1 HOUR
                     ORDER BY dm.StartedTime ASC;""";
        List<DepartmentMeeting> findByUserID = new ArrayList<>();
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DepartmentMeeting dm = new DepartmentMeeting();
                dm.setDepartmentMeetingID(rs.getInt("DepartmentMeetingID"));
                dm.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                dm.setTitle(rs.getString("Title"));
                dm.setURLMeeting(rs.getString("URLMeeting"));
                dm.setDocumentLink(rs.getString("DocumentLink"));
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
                     JOIN DepartmentMeetingParticipants dmp ON dm.DepartmentMeetingID = dmp.DepartmentMeetingID
                     WHERE dmp.UserID = ?
                     AND dm.StartedTime >= now() - INTERVAL 1 HOUR;""";
        int cnt = 0;
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cnt = rs.getInt("total_meetings");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cnt;
    }

    public void createMeeting(int clubDepartmentID, String title, String urlMeeting, String documentLink, String startedTime, List<String> participantUserIds) {
        String sqlMeeting = """
            INSERT INTO DepartmentMeeting (ClubDepartmentID, Title, URLMeeting, DocumentLink, StartedTime)
            VALUES (?, ?, ?, ?, ?)
            """;
        String sqlParticipants = """
            INSERT INTO DepartmentMeetingParticipants (DepartmentMeetingID, UserID)
            VALUES (?, ?)
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement psMeeting = conn.prepareStatement(sqlMeeting, Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);
            psMeeting.setInt(1, clubDepartmentID);
            psMeeting.setString(2, title);
            psMeeting.setString(3, urlMeeting);
            psMeeting.setString(4, documentLink);
            psMeeting.setString(5, startedTime);
            psMeeting.executeUpdate();

            ResultSet rs = psMeeting.getGeneratedKeys();
            int meetingId = 0;
            if (rs.next()) {
                meetingId = rs.getInt(1);
            }

            if (participantUserIds != null && !participantUserIds.isEmpty()) {
                try (PreparedStatement psParticipants = conn.prepareStatement(sqlParticipants)) {
                    for (String userId : participantUserIds) {
                        psParticipants.setInt(1, meetingId);
                        psParticipants.setString(2, userId);
                        psParticipants.addBatch();
                    }
                    psParticipants.executeBatch();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateMeeting(DepartmentMeeting meeting, List<String> participantUserIds) {
        String sqlMeeting = """
            UPDATE DepartmentMeeting 
            SET Title = ?, URLMeeting = ?, DocumentLink = ?, StartedTime = ?
            WHERE DepartmentMeetingID = ? 
            AND ClubDepartmentID = ?
            """;
        String sqlDeleteParticipants = """
            DELETE FROM DepartmentMeetingParticipants 
            WHERE DepartmentMeetingID = ?
            """;
        String sqlInsertParticipants = """
            INSERT INTO DepartmentMeetingParticipants (DepartmentMeetingID, UserID)
            VALUES (?, ?)
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement psMeeting = conn.prepareStatement(sqlMeeting);
             PreparedStatement psDelete = conn.prepareStatement(sqlDeleteParticipants);
             PreparedStatement psInsert = conn.prepareStatement(sqlInsertParticipants)) {
            conn.setAutoCommit(false);

            psMeeting.setString(1, meeting.getTitle());
            psMeeting.setString(2, meeting.getURLMeeting());
            psMeeting.setString(3, meeting.getDocumentLink());
            psMeeting.setTimestamp(4, meeting.getStartedTime());
            psMeeting.setInt(5, meeting.getDepartmentMeetingID());
            psMeeting.setInt(6, meeting.getClubDepartmentID());
            boolean updated = psMeeting.executeUpdate() > 0;

            if (updated) {
                psDelete.setInt(1, meeting.getDepartmentMeetingID());
                psDelete.executeUpdate();

                if (participantUserIds != null && !participantUserIds.isEmpty()) {
                    for (String userId : participantUserIds) {
                        psInsert.setInt(1, meeting.getDepartmentMeetingID());
                        psInsert.setString(2, userId);
                        psInsert.addBatch();
                    }
                    psInsert.executeBatch();
                }
            }
            conn.commit();
            return updated;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteMeeting(int meetingID, int clubDepartmentID) {
        String sqlDeleteParticipants = """
            DELETE FROM DepartmentMeetingParticipants 
            WHERE DepartmentMeetingID = ?
            """;
        String sqlDeleteMeeting = """
            DELETE FROM DepartmentMeeting 
            WHERE DepartmentMeetingID = ? 
            AND ClubDepartmentID = ?
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement psDeleteParticipants = conn.prepareStatement(sqlDeleteParticipants);
             PreparedStatement psDeleteMeeting = conn.prepareStatement(sqlDeleteMeeting)) {
            conn.setAutoCommit(false);
            psDeleteParticipants.setInt(1, meetingID);
            psDeleteParticipants.executeUpdate();

            psDeleteMeeting.setInt(1, meetingID);
            psDeleteMeeting.setInt(2, clubDepartmentID);
            boolean deleted = psDeleteMeeting.executeUpdate() > 0;

            conn.commit();
            return deleted;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

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
                dm.setTitle(rs.getString("Title"));
                dm.setURLMeeting(rs.getString("URLMeeting"));
                dm.setDocumentLink(rs.getString("DocumentLink"));
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
            ORDER BY dm

.StartedTime ASC
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubDepartmentID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DepartmentMeeting dm = new DepartmentMeeting();
                dm.setDepartmentMeetingID(rs.getInt("DepartmentMeetingID"));
                dm.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                dm.setTitle(rs.getString("Title"));
                dm.setURLMeeting(rs.getString("URLMeeting"));
                dm.setDocumentLink(rs.getString("DocumentLink"));
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

    public boolean isDepartmentLeaderForClubDepartment(String userId, int clubDepartmentID) {
        String sql = """
            SELECT COUNT(*) as count
            FROM UserClubs uc
            WHERE uc.UserID concessions
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
                return  rs.getInt("count") > 0;
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
            sql.append(" AND (dm.Title LIKE ? OR dm.URLMeeting LIKE ? OR CAST(dm.StartedTime AS CHAR) LIKE ?)");
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
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex, (page - 1) * pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DepartmentMeeting dm = new DepartmentMeeting();
                dm.setDepartmentMeetingID(rs.getInt("DepartmentMeetingID"));
                dm.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                dm.setTitle(rs.getString("Title"));
                dm.setURLMeeting(rs.getString("URLMeeting"));
                dm.setDocumentLink(rs.getString("DocumentLink"));
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
            sql.append(" AND (Title LIKE ? OR URLMeeting LIKE ? OR CAST(StartedTime AS CHAR) LIKE ?)");
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

    public List<Users> getDepartmentMembers(int clubDepartmentID) {
        List<Users> members = new ArrayList<>();
        String sql = """
            SELECT u.*
            FROM Users u
            JOIN UserClubs uc ON u.UserID = uc.UserID
            WHERE uc.ClubDepartmentID = ?
            AND uc.IsActive = 1
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubDepartmentID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Users user = new Users();
                user.setUserID(rs.getString("UserID"));
                user.setFullName(rs.getString("FullName"));
                members.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public List<String> getMeetingParticipants(int meetingID) {
        List<String> participants = new ArrayList<>();
        String sql = """
            SELECT UserID
            FROM DepartmentMeetingParticipants
            WHERE DepartmentMeetingID = ?
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, meetingID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                participants.add(rs.getString("UserID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }
}