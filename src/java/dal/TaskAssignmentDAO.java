/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;
import com.mysql.cj.xdevapi.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.TaskAssignment;
/**
 *
 * @author he181
 */
public class TaskAssignmentDAO {

    public static List<TaskAssignment> findByUserID(String userID) {
        List<TaskAssignment> findByUserID = new ArrayList<>();
        String sql = """
                     SELECT ta.*, e.EventName, cd.DepartmentName, c.ClubName, c.ClubID 
                     FROM TaskAssignment ta 
                     JOIN Events e ON ta.EventID = e.EventID 
                     JOIN ClubDepartments cd ON ta.DepartmentID = cd.DepartmentID 
                     JOIN Clubs c ON cd.ClubID = c.ClubID
                     JOIN UserClubs uc ON cd.ClubID = uc.ClubID
                     WHERE uc.UserID = ?
                     AND ta.DepartmentID IN (
                         SELECT DepartmentID 
                         FROM UserClubs 
                         WHERE UserID = ?
                     ) And ta.DueDate >= NOW() 
                     ORDER BY ta.DueDate ASC;""";
        try {
            PreparedStatement ps = DBContext_Duc.getInstance().connection.prepareStatement(sql);
            ps.setObject(1, userID);
            ps.setObject(2, userID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                TaskAssignment ta = new TaskAssignment();
                ta.setTaskAssignmentID(rs.getInt("TaskAssignmentID"));
                ta.setEventID(rs.getInt("EventID"));
                ta.setDescription(rs.getString("Description"));
                ta.setDepartmentID(rs.getInt("DepartmentID"));
                ta.setTaskName(rs.getString("TaskName"));
                ta.setDueDate(rs.getTimestamp("DueDate"));
                ta.setStatus(rs.getString("Status"));
                ta.setEventName(rs.getString("EventName"));
                ta.setDepartmentName(rs.getString("DepartmentName"));
                ta.setClubName(rs.getString("ClubName"));
                ta.setClubID(rs.getInt("ClubID"));
                findByUserID.add(ta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findByUserID;
    }

    public static int countByUserID(String userID) {
        int count = 0;
        String sql = """
                     SELECT COUNT(*) as total 
                                          FROM TaskAssignment ta 
                                          JOIN Events e ON ta.EventID = e.EventID 
                                          JOIN ClubDepartments cd ON ta.DepartmentID = cd.DepartmentID 
                                          JOIN Clubs c ON cd.ClubID = c.ClubID
                                          JOIN UserClubs uc ON cd.ClubID = uc.ClubID
                                          WHERE uc.UserID = ?
                                          AND ta.DepartmentID IN (
                                              SELECT DepartmentID 
                                              FROM UserClubs 
                                              WHERE UserID = ?
                                          ) And ta.DueDate >= NOW();""";
        try {
            PreparedStatement ps = DBContext_Duc.getInstance().connection.prepareStatement(sql);
            ps.setObject(1, userID);
            ps.setObject(2, userID);
            ResultSet  rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
    
    
}
