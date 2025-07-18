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
                     SELECT DISTINCT cm.*, c.ClubName, c.ClubImg
                     FROM ClubMeeting cm
                     JOIN Clubs c ON cm.ClubID = c.ClubID
                     JOIN UserClubs uc ON c.ClubID = uc.ClubID
                     WHERE uc.UserID = ?
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
                findByUserID.add(cm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findByUserID;
    }

    

    public static int countByUserID(String userID) {
        String sql = """
                     SELECT count(distinct clubmeetingID) as total
                                          FROM ClubMeeting cm 
                                          JOIN UserClubs uc ON cm.ClubID = uc.ClubID 
                                          WHERE uc.UserID = ?
                                          AND cm.StartedTime > CURRENT_TIMESTAMP""";int count = 0;
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
}


