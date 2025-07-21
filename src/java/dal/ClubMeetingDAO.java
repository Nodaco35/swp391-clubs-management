/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.ClubMeeting;

/**
 *
 * @author he181
 */
public class ClubMeetingDAO {

    public static List<ClubMeeting> findByUserID(String userID) {
        String sql = """
                     SELECT distinct  cm.*, cmp.*, c.ClubName, c.ClubImg
                                          FROM ClubMeeting cm
                                           Join ClubmeetingParticipants cmp on cm.ClubMeetingID = cmp.ClubMeetingID
                                          JOIN Clubs c ON cm.ClubID = c.ClubID
                                          JOIN UserClubs uc ON c.ClubID = uc.ClubID
                                          WHERE uc.UserID = ? and uc.ClubDepartmentID = cmp.ClubDepartmentID
                                            AND cm.StartedTime >= NOW() - INTERVAL 1 HOUR;""";
        List<ClubMeeting> findByUserID = new ArrayList<>();
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ClubMeeting cm = new ClubMeeting();
                cm.setClubMeetingID(rs.getInt("ClubMeetingID"));
                cm.setClubID(rs.getInt("ClubID"));
                cm.setClubName(rs.getString("ClubName"));
                cm.setURLMeeting(rs.getString("URLMeeting"));
                cm.setStartedTime(rs.getTimestamp("StartedTime"));
                cm.setClubImg(rs.getString("ClubImg"));
                cm.setMeetingTitle(rs.getString("MeetingTitle"));
                cm.setEndTime(rs.getTimestamp("EndTime"));
                cm.setDocument(rs.getString("Document"));
                cm.setDescription(rs.getString("Description"));
                findByUserID.add(cm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findByUserID;
    }
 
    public static int countByUserID(String userID) {
        String sql = """
                     SELECT count(distinct cm.ClubMeetingID) as total
                                                               FROM ClubMeeting cm 
                                                               Join ClubmeetingParticipants cmp on cm.ClubMeetingID = cmp.ClubMeetingID
                                                                JOIN UserClubs uc ON cm.ClubID = uc.ClubID
                                                               WHERE uc.UserID = ?  and uc.ClubDepartmentID = cmp.ClubDepartmentID
                                                               AND cm.StartedTime >= NOW() - INTERVAL 1 HOUR;""";
        int count = 0;
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, userID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public static void insert(int clubID, String startedTime, String URLMeeting) {
        String sql = """
                     INSERT INTO ClubMeeting (ClubID, URLMeeting, StartedTime)
                     VALUES (?, ?, ?);""";

        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubID);
            ps.setObject(2, URLMeeting);
            ps.setObject(3, startedTime);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void update(int clubID, String startedTime, String URLMeeting, int clubMeetingID) {
        String sql = """
                     UPDATE `clubmanagementsystem`.`clubmeeting`
                     SET
                     
                     `ClubID` = ?,
                     `URLMeeting` = ?,
                     `StartedTime` = ?
                     WHERE `ClubMeetingID` = ?;""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubID);
            ps.setObject(3, startedTime);
            ps.setObject(2, URLMeeting);
            ps.setObject(4, clubMeetingID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void delete(int clubMeetingID) {
        String sql = """
                     DELETE FROM `clubmanagementsystem`.`clubmeeting`
                     WHERE ClubMeetingID = ?;""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubMeetingID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static List<ClubMeeting> findByClubID(int clubID, String search, int page, int pageSize) {
        String sql = """
                 SELECT cm.*, c.ClubName, c.ClubImg
                 FROM ClubMeeting cm
                 JOIN Clubs c ON cm.ClubID = c.ClubID
                 WHERE cm.ClubID = ?
                 AND (cm.MeetingTitle LIKE ? OR cm.URLMeeting LIKE ? OR CAST(cm.StartedTime AS CHAR) LIKE ?)
                 ORDER BY cm.StartedTime DESC
                 LIMIT ? OFFSET ?;""";
        List<ClubMeeting> meetings = new ArrayList<>();
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            String searchParam = "%" + (search != null ? search : "") + "%";
            ps.setInt(1, clubID);
            ps.setString(2, searchParam);
            ps.setString(3, searchParam);
            ps.setString(4, searchParam);
            ps.setInt(5, pageSize);
            ps.setInt(6, (page - 1) * pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ClubMeeting cm = new ClubMeeting();
                cm.setClubMeetingID(rs.getInt("ClubMeetingID"));
                cm.setClubID(rs.getInt("ClubID"));
                cm.setClubName(rs.getString("ClubName"));
                cm.setURLMeeting(rs.getString("URLMeeting"));
                cm.setStartedTime(rs.getTimestamp("StartedTime"));
                cm.setEndTime(rs.getTimestamp("EndTime"));
                cm.setMeetingTitle(rs.getString("MeetingTitle"));
                cm.setDescription(rs.getString("Description"));
                cm.setDocument(rs.getString("Document"));
                cm.setClubImg(rs.getString("ClubImg"));
                meetings.add(cm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meetings;
    }

    public static int countByClubID(int clubID, String search) {
        String sql = """
                 SELECT COUNT(*) as total
                 FROM ClubMeeting cm
                 WHERE cm.ClubID = ?
                 AND (cm.MeetingTitle LIKE ? OR cm.URLMeeting LIKE ? OR CAST(cm.StartedTime AS CHAR) LIKE ?);""";
        int total = 0;
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            String searchParam = "%" + (search != null ? search : "") + "%";
            ps.setInt(1, clubID);
            ps.setString(2, searchParam);
            ps.setString(3, searchParam);
            ps.setString(4, searchParam);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public static List<String> getClubDepartmentID(int meetingId) {
        List<String> getClubDepartmentID = new ArrayList();
        String sql = """
                     SELECT cmp.ClubMeetingID,
                                             cmp.ClubDepartmentID, cd.DepartmentID, d.DepartmentName
                                          FROM `clubmanagementsystem`.`clubmeetingparticipants` cmp
                                          join clubdepartments cd on cmp.ClubDepartmentID = cd.ClubDepartmentID
                                          join departments d on cd.DepartmentID = d.DepartmentID
                                          where cmp.ClubMeetingID =  ?""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, meetingId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                getClubDepartmentID.add(rs.getString("ClubDepartmentID"));

            }
        } catch (Exception e) {
        }
        return getClubDepartmentID;
    }

    public static boolean createMeeting(int clubId, String title, String urlMeeting, String documentLink, String formattedStartedTime) {
        String sql = """
                     INSERT INTO ClubMeeting (
                         ClubID,
                         MeetingTitle,
                         URLMeeting,
                         StartedTime,
                         Document
                     ) VALUES (
                         ?,?,?,?,?
                     );""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubId);
            ps.setObject(2, title);
            ps.setObject(3, urlMeeting);
            ps.setObject(4, formattedStartedTime);
            ps.setObject(5, documentLink);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static ClubMeeting getNewest(int clubID) {
        ClubMeeting cm = new ClubMeeting();
        String sql = """
                     SELECT * 
                                          FROM clubmanagementsystem.clubmeeting 
                                          Where ClubID = ?
                                          ORDER BY ClubMeetingID DESC 
                                          LIMIT 1;""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cm.setClubMeetingID(rs.getInt("ClubMeetingID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cm;
    }

    public static void insertParticipants(int clubMeetingId, int parseInt) {
        String sql = """
                     INSERT INTO `clubmanagementsystem`.`clubmeetingparticipants`
                     (`ClubMeetingID`,
                     `ClubDepartmentID`)
                     VALUES
                     (?,
                     ?);""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubMeetingId);
            ps.setObject(2, parseInt);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static boolean updateMeeting(ClubMeeting meeting, List<String> participants) {
        String sqlUpdatemeeting = """
                                  UPDATE `clubmanagementsystem`.`clubmeeting`
                                  SET
                                  
                                  
                                  `MeetingTitle` =?,
                                  
                                  `URLMeeting` = ?,
                                  `StartedTime` = ?,
                                  `Document` = ?
                                  WHERE `ClubMeetingID` = ?;""";
        String sqlDlOldPar = """
                             DELETE FROM `clubmanagementsystem`.`clubmeetingparticipants`
                             WHERE ClubMeetingID=?;""";
        String sqlNewPar = """
                           INSERT INTO `clubmanagementsystem`.`clubmeetingparticipants`
                                                (`ClubMeetingID`,
                                                `ClubDepartmentID`)
                                                VALUES
                                                (?,
                                                ?);""";

        try {
            PreparedStatement ps1 = DBContext.getConnection().prepareStatement(sqlUpdatemeeting);
            PreparedStatement ps2 = DBContext.getConnection().prepareStatement(sqlDlOldPar);
            PreparedStatement ps3 = DBContext.getConnection().prepareStatement(sqlNewPar);

            ps1.setObject(1, meeting.getMeetingTitle());
            ps1.setObject(2, meeting.getURLMeeting());
            ps1.setObject(3, meeting.getStartedTime());
            ps1.setObject(4, meeting.getDocument());
            ps1.setObject(5, meeting.getClubMeetingID());
            boolean updated = ps1.executeUpdate() > 0;
            if (updated) {
                ps2.setObject(1, meeting.getClubMeetingID());
                ps2.executeUpdate();
                if (!participants.isEmpty()) {
                    for (String clubmeetingid : participants) {
                        ps3.setObject(1, meeting.getClubMeetingID());
                        ps3.setObject(2, String.valueOf(clubmeetingid));
                        ps3.executeUpdate();
                    }

                }
            }
            return true;
        } catch (SQLException e) {
            return false;
        }

    }

    public static boolean deleteMeeting(int meetingId) {
        String sql1 = """
                      DELETE FROM `clubmanagementsystem`.`clubmeeting`
                      WHERE ClubMeetingID = ?;""";
        String sql2 = """
                      DELETE FROM `clubmanagementsystem`.`clubmeetingparticipants`
                      WHERE ClubMeetingID = ?;""";

        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql1);
            PreparedStatement ps2 = DBContext.getConnection().prepareStatement(sql2);
            ps.setObject(1, meetingId);
            ps2.setObject(1, meetingId);

            ps.executeUpdate();
            ps2.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }

    }

}
