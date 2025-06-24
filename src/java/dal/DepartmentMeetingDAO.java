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
    
}