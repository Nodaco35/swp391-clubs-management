package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import models.TaskAssignmentMember;

public class TaskAssignmentMemberDAO {
    
    /**
     * Lấy tất cả nhiệm vụ của thành viên trong một nhiệm vụ của ban
     * @param departmentTaskID ID của nhiệm vụ ban
     * @return Danh sách các nhiệm vụ của thành viên
     */
    public List<TaskAssignmentMember> getTasksByDepartmentTask(int departmentTaskID) {
        List<TaskAssignmentMember> tasks = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT tm.*, " +
                         "assigner.FullName AS AssignerName, " +
                         "assignee.FullName AS AssigneeName, " +
                         "d.DepartmentName, e.EventName " +
                         "FROM TaskAssignmentMember tm " +
                         "JOIN Users assigner ON tm.AssignerID = assigner.UserID " +
                         "JOIN Users assignee ON tm.AssigneeID = assignee.UserID " +
                         "JOIN TaskAssignmentDepartment td ON tm.DepartmentTaskID = td.TaskAssignmentDepartmentID " +
                         "JOIN ClubDepartments cd ON td.ClubDepartmentID = cd.ClubDepartmentID " +
                         "JOIN Departments d ON cd.DepartmentID = d.DepartmentID " +
                         "JOIN Events e ON td.EventID = e.EventID " +
                         "WHERE tm.DepartmentTaskID = ? " +
                         "ORDER BY tm.Priority DESC, tm.DueDate ASC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departmentTaskID);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                TaskAssignmentMember task = extractTaskFromResultSet(rs);
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching member tasks: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return tasks;
    }
    
    /**
     * Lấy tất cả nhiệm vụ của một thành viên
     * @param assigneeID ID của thành viên được giao nhiệm vụ
     * @return Danh sách các nhiệm vụ
     */
    public List<TaskAssignmentMember> getTasksByAssignee(String assigneeID) {
        List<TaskAssignmentMember> tasks = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT tm.*, " +
                         "assigner.FullName AS AssignerName, " +
                         "assignee.FullName AS AssigneeName, " +
                         "d.DepartmentName, e.EventName " +
                         "FROM TaskAssignmentMember tm " +
                         "JOIN Users assigner ON tm.AssignerID = assigner.UserID " +
                         "JOIN Users assignee ON tm.AssigneeID = assignee.UserID " +
                         "JOIN TaskAssignmentDepartment td ON tm.DepartmentTaskID = td.TaskAssignmentDepartmentID " +
                         "JOIN ClubDepartments cd ON td.ClubDepartmentID = cd.ClubDepartmentID " +
                         "JOIN Departments d ON cd.DepartmentID = d.DepartmentID " +
                         "JOIN Events e ON td.EventID = e.EventID " +
                         "WHERE tm.AssigneeID = ? " +
                         "ORDER BY tm.Priority DESC, tm.DueDate ASC";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, assigneeID);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                TaskAssignmentMember task = extractTaskFromResultSet(rs);
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching assignee tasks: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return tasks;
    }
    
    /**
     * Lấy thông tin chi tiết của một nhiệm vụ thành viên
     * @param taskID ID của nhiệm vụ
     * @return Thông tin chi tiết của nhiệm vụ
     */
    public TaskAssignmentMember getTaskById(int taskID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        TaskAssignmentMember task = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT tm.*, " +
                         "assigner.FullName AS AssignerName, " +
                         "assignee.FullName AS AssigneeName, " +
                         "d.DepartmentName, e.EventName " +
                         "FROM TaskAssignmentMember tm " +
                         "JOIN Users assigner ON tm.AssignerID = assigner.UserID " +
                         "JOIN Users assignee ON tm.AssigneeID = assignee.UserID " +
                         "JOIN TaskAssignmentDepartment td ON tm.DepartmentTaskID = td.TaskAssignmentDepartmentID " +
                         "JOIN ClubDepartments cd ON td.ClubDepartmentID = cd.ClubDepartmentID " +
                         "JOIN Departments d ON cd.DepartmentID = d.DepartmentID " +
                         "JOIN Events e ON td.EventID = e.EventID " +
                         "WHERE tm.TaskMemberID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, taskID);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                task = extractTaskFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching member task details: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return task;
    }
    
    /**
     * Tạo một nhiệm vụ mới cho thành viên
     * @param task Thông tin nhiệm vụ cần tạo
     * @return ID của nhiệm vụ mới tạo, 0 nếu tạo thất bại
     */
    public int createTask(TaskAssignmentMember task) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int newTaskId = 0;
        
        try {
            conn = DBContext.getConnection();
            String sql = "INSERT INTO TaskAssignmentMember (DepartmentTaskID, TaskName, Description, " +
                         "AssignerID, AssigneeID, DueDate, Priority, Status, CreatedAt, UpdatedAt) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
            stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, task.getDepartmentTaskID());
            stmt.setString(2, task.getTaskName());
            stmt.setString(3, task.getDescription());
            stmt.setInt(4, task.getAssignerID());
            stmt.setInt(5, task.getAssigneeID());
            stmt.setTimestamp(6, task.getDueDate());
            stmt.setInt(7, task.getPriority());
            stmt.setString(8, task.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    newTaskId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating member task: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return newTaskId;
    }
    
    /**
     * Cập nhật thông tin của một nhiệm vụ thành viên
     * @param task Thông tin nhiệm vụ cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateTask(TaskAssignmentMember task) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBContext.getConnection();
            String sql = "UPDATE TaskAssignmentMember SET TaskName = ?, Description = ?, " +
                         "AssigneeID = ?, DueDate = ?, Priority = ?, Status = ?, UpdatedAt = NOW() " +
                         "WHERE TaskMemberID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, task.getTaskName());
            stmt.setString(2, task.getDescription());
            stmt.setInt(3, task.getAssigneeID());
            stmt.setTimestamp(4, task.getDueDate());
            stmt.setInt(5, task.getPriority());
            stmt.setString(6, task.getStatus());
            stmt.setInt(7, task.getTaskMemberID());
            
            int affectedRows = stmt.executeUpdate();
            success = affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error updating member task: " + e.getMessage());
        } finally {
            closeResources(null, stmt, conn);
        }
        
        return success;
    }
    
    /**
     * Cập nhật trạng thái của một nhiệm vụ thành viên
     * @param taskID ID của nhiệm vụ
     * @param status Trạng thái mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateTaskStatus(int taskID, String status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBContext.getConnection();
            String sql = "UPDATE TaskAssignmentMember SET Status = ?, UpdatedAt = NOW() " +
                         "WHERE TaskMemberID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, taskID);
            
            int affectedRows = stmt.executeUpdate();
            success = affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error updating member task status: " + e.getMessage());
        } finally {
            closeResources(null, stmt, conn);
        }
        
        return success;
    }
    
    /**
     * Xóa một nhiệm vụ thành viên
     * @param taskID ID của nhiệm vụ cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteTask(int taskID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBContext.getConnection();
            String sql = "DELETE FROM TaskAssignmentMember WHERE TaskMemberID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, taskID);
            
            int affectedRows = stmt.executeUpdate();
            success = affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting member task: " + e.getMessage());
        } finally {
            closeResources(null, stmt, conn);
        }
        
        return success;
    }
    
    /**
     * Xóa tất cả nhiệm vụ thành viên thuộc về một nhiệm vụ ban cụ thể
     * @param departmentTaskID ID của nhiệm vụ ban
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteAllTaskMembersByDepartmentTaskID(int departmentTaskID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBContext.getConnection();
            String sql = "DELETE FROM TaskAssignmentMember WHERE DepartmentTaskID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departmentTaskID);
            
            int affectedRows = stmt.executeUpdate();
            success = true; // Xóa được coi là thành công ngay cả khi không có hàng nào bị ảnh hưởng
        } catch (SQLException e) {
            System.out.println("Error deleting member tasks by department task ID: " + e.getMessage());
        } finally {
            closeResources(null, stmt, conn);
        }
        
        return success;
    }
    
    /**
     * Kiểm tra xem một người dùng có phải là người thực hiện của một nhiệm vụ không
     * @param taskMemberID ID của nhiệm vụ thành viên
     * @param userID ID của người dùng
     * @return true nếu người dùng là người thực hiện nhiệm vụ, false nếu không
     */
    public boolean isUserTaskResponser(int taskMemberID, String userID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean isResponser = false;
        
        try {
            conn = DBContext.getConnection();
            
            // Kiểm tra trong bảng TaskAssignmentMemberResponser hoặc trực tiếp từ TaskAssignmentMember
            // Tùy thuộc vào thiết kế cơ sở dữ liệu, có thể thay đổi truy vấn này
            String sql = "SELECT COUNT(*) AS Count FROM TaskAssignmentMemberResponser " +
                         "WHERE TaskAssignmentMemberID = ? AND MemberID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, taskMemberID);
            stmt.setString(2, userID);
            rs = stmt.executeQuery();
            
            if (rs.next() && rs.getInt("Count") > 0) {
                isResponser = true;
            }
            
            // Nếu không tìm thấy trong bảng responder, kiểm tra trực tiếp từ assigneeID
            if (!isResponser) {
                rs.close();
                stmt.close();
                
                sql = "SELECT COUNT(*) AS Count FROM TaskAssignmentMember " +
                      "WHERE TaskMemberID = ? AND AssigneeID = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, taskMemberID);
                stmt.setString(2, userID);
                rs = stmt.executeQuery();
                
                if (rs.next() && rs.getInt("Count") > 0) {
                    isResponser = true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking task responder: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return isResponser;
    }
    
    /**
     * Extract task data from ResultSet
     */
    private TaskAssignmentMember extractTaskFromResultSet(ResultSet rs) throws SQLException {
        TaskAssignmentMember task = new TaskAssignmentMember();
        task.setTaskMemberID(rs.getInt("TaskMemberID"));
        task.setDepartmentTaskID(rs.getInt("DepartmentTaskID"));
        task.setTaskName(rs.getString("TaskName"));
        task.setDescription(rs.getString("Description"));
        task.setAssignerID(rs.getInt("AssignerID"));
        task.setAssigneeID(rs.getInt("AssigneeID"));
        task.setDueDate(rs.getTimestamp("DueDate"));
        task.setPriority(rs.getInt("Priority"));
        task.setStatus(rs.getString("Status"));
        task.setCreatedAt(rs.getTimestamp("CreatedAt"));
        task.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
        
        // Set view properties
        task.setAssignerName(rs.getString("AssignerName"));
        task.setAssigneeName(rs.getString("AssigneeName"));
        task.setDepartmentName(rs.getString("DepartmentName"));
        task.setEventName(rs.getString("EventName"));
        
        return task;
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
