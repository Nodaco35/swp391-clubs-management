package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import models.TaskAssignmentDepartmentResponser;

/**
 * DAO for managing TaskAssignmentDepartmentResponser entities
 * Used to handle the many-to-many relationship between tasks and departments
 */
public class TaskAssignmentDepartmentResponserDAO {
    
    /**
     * Get all department responders for a specific task
     * @param taskID TaskAssignmentDepartmentID
     * @return List of TaskAssignmentDepartmentResponser objects
     */
    public List<TaskAssignmentDepartmentResponser> getByTaskID(int taskID) {
        List<TaskAssignmentDepartmentResponser> responders = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT r.*, u.FullName AS ResponderName " +
                         "FROM TaskAssignmentDepartmentResponser r " +
                         "LEFT JOIN ClubDepartments cd ON r.ClubDepartmentID = cd.ClubDepartmentID " +
                         "LEFT JOIN Departments d ON cd.DepartmentID = d.DepartmentID " +
                         "WHERE r.TaskAssignmentDepartmentID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, taskID);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                TaskAssignmentDepartmentResponser responder = new TaskAssignmentDepartmentResponser();
                responder.setResponserID(rs.getInt("TaskAssignmentDepartmentResponserID"));
                responder.setTaskAssignmentID(rs.getInt("TaskAssignmentDepartmentID"));
                responder.setResponderID(rs.getInt("ClubDepartmentID"));
                if (rs.getTimestamp("RespondedAt") != null) {
                    responder.setRespondedAt(rs.getTimestamp("RespondedAt"));
                }
                responder.setResponderName(rs.getString("ResponderName"));
                responders.add(responder);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching task responders: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return responders;
    }
    
    /**
     * Get all tasks assigned to a specific department
     * @param clubDepartmentID The department ID
     * @return List of task IDs assigned to the department
     */
    public List<Integer> getTasksForDepartment(int clubDepartmentID) {
        List<Integer> taskIDs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT TaskAssignmentDepartmentID FROM TaskAssignmentDepartmentResponser " +
                         "WHERE ClubDepartmentID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, clubDepartmentID);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                taskIDs.add(rs.getInt("TaskAssignmentDepartmentID"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching department tasks: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return taskIDs;
    }
    
    /**
     * Add a responder to a task
     * @param taskID The task ID
     * @param clubDepartmentID The department ID
     * @return The ID of the new responder, or 0 if failed
     */
    public int addResponder(int taskID, int clubDepartmentID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int newResponderID = 0;
        
        try {
            conn = DBContext.getConnection();
            String sql = "INSERT INTO TaskAssignmentDepartmentResponser " +
                         "(TaskAssignmentDepartmentID, ClubDepartmentID) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, taskID);
            stmt.setInt(2, clubDepartmentID);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    newResponderID = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding task responder: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return newResponderID;
    }
    
    /**
     * Remove a responder from a task
     * @param taskID The task ID
     * @param clubDepartmentID The department ID
     * @return true if successful, false otherwise
     */
    public boolean removeResponder(int taskID, int clubDepartmentID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBContext.getConnection();
            String sql = "DELETE FROM TaskAssignmentDepartmentResponser " +
                         "WHERE TaskAssignmentDepartmentID = ? AND ClubDepartmentID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, taskID);
            stmt.setInt(2, clubDepartmentID);
            
            int affectedRows = stmt.executeUpdate();
            success = affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error removing task responder: " + e.getMessage());
        } finally {
            closeResources(null, stmt, conn);
        }
        
        return success;
    }
    
    /**
     * Close database resources
     */
    private void closeResources(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DBContext.closeConnection(conn);
        } catch (SQLException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }
}
