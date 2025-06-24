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

    
    //thay đổi câu lệnh sql
    public static List<TaskAssignment> findByUserID(String userID) {
        List<TaskAssignment> findByUserID = new ArrayList<>();
        String sql = """
                     SELECT distinct(uc.UserID),
                      tm.*, 
                           d.DepartmentName, c.ClubName, c.ClubID, e.EventName, cd.DepartmentID
                     FROM taskassignmentdepartment tm
                     JOIN Events e ON tm.EventID = e.EventID
                     JOIN ClubDepartments cd ON e.ClubID = cd.ClubID
                     Join clubs c on cd.clubID = c.ClubID
                     JOIN departments d on d.DepartmentID = cd.DepartmentID
                     JOIN userclubs uc on uc.ClubDepartmentID = cd.ClubDepartmentID
                     where uc.UserID = ?
                     AND tm.DueDate > now()
                     ORDER BY tm.DueDate ASC
                     ;""";
        try {
           PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, userID);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TaskAssignment ta = new TaskAssignment();
                ta.setTaskAssignmentID(rs.getInt("TaskAssignmentDepartmentID"));
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
                      SELECT COUNT(distinct tm.TaskAssignmentDepartmentID) as total 
                                                               FROM taskassignmentdepartment tm
                                                                                    JOIN Events e ON tm.EventID = e.EventID
                                                                                    JOIN ClubDepartments cd ON e.ClubID = cd.ClubID
                                                                                    Join clubs c on cd.clubID = c.ClubID
                                                                                    JOIN departments d on d.DepartmentID = cd.DepartmentID
                                                                                    JOIN userclubs uc on uc.ClubDepartmentID = cd.ClubDepartmentID
                                                                                    where uc.UserID = ?
                                                                                    AND tm.DueDate > now();""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, userID);
           
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
    
}
