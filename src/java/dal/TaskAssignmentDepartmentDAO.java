package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import models.TaskAssignmentDepartment;
import models.TaskAssignmentDepartmentResponser;

public class TaskAssignmentDepartmentDAO {
      /**
     * Lấy tất cả nhiệm vụ của một ban trong một sự kiện
     * @param clubDepartmentID ID của ban trong CLB
     * @return Danh sách các nhiệm vụ của ban
     */    public List<TaskAssignmentDepartment> getTasksByDepartment(int clubDepartmentID) {
        List<TaskAssignmentDepartment> tasks = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        System.out.println("DEBUG TaskAssignmentDepartmentDAO.getTasksByDepartment: Querying for clubDepartmentID=" + clubDepartmentID);
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT t.*, e.EventName, d.DepartmentName, c.ClubName, c.ClubID, r.ClubDepartmentID " +
                         "FROM TaskAssignmentDepartment t " +
                         "JOIN TaskAssignmentDepartmentResponser r ON t.TaskAssignmentDepartmentID = r.TaskAssignmentDepartmentID " +
                         "JOIN Events e ON t.EventID = e.EventID " +
                         "JOIN ClubDepartments cd ON r.ClubDepartmentID = cd.ClubDepartmentID " +
                         "JOIN Departments d ON cd.DepartmentID = d.DepartmentID " +
                         "JOIN Clubs c ON cd.ClubID = c.ClubID " +
                         "WHERE r.ClubDepartmentID = ? " +
                         "ORDER BY t.DueDate ASC";
            
            System.out.println("DEBUG TaskAssignmentDepartmentDAO: SQL = " + sql);
            
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, clubDepartmentID);
            rs = stmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                TaskAssignmentDepartment task = extractTaskFromResultSet(rs);
                
                System.out.println("DEBUG TaskAssignmentDepartmentDAO: Found task " + count + " - ID=" + task.getTaskAssignmentDepartmentID() + ", Name=" + task.getTaskName());
                
                // Add additional responders for each task
                loadTaskResponders(task);
                
                tasks.add(task);
            }
            
            System.out.println("DEBUG TaskAssignmentDepartmentDAO: Total tasks found = " + tasks.size());
            
        } catch (SQLException e) {
            System.out.println("Error fetching department tasks: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return tasks;
    }
    
    /**
     * Load all responders for a task
     * @param task The task to load responders for
     */
    private void loadTaskResponders(TaskAssignmentDepartment task) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT r.*, cd.ClubDepartmentID, d.DepartmentName " +
                         "FROM TaskAssignmentDepartmentResponser r " +
                         "JOIN ClubDepartments cd ON r.ClubDepartmentID = cd.ClubDepartmentID " +
                         "JOIN Departments d ON cd.DepartmentID = d.DepartmentID " +
                         "WHERE r.TaskAssignmentDepartmentID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, task.getTaskAssignmentDepartmentID());
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                TaskAssignmentDepartmentResponser responder = new TaskAssignmentDepartmentResponser();
                responder.setResponserID(rs.getInt("TaskAssignmentDepartmentResponserID"));
                responder.setTaskAssignmentID(rs.getInt("TaskAssignmentDepartmentID"));
                responder.setResponderID(rs.getInt("ClubDepartmentID"));
                responder.setResponderName(rs.getString("DepartmentName"));
                
                task.addDepartmentResponser(responder);
            }
        } catch (SQLException e) {
            System.out.println("Error loading task responders: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
      /**
     * Lấy tất cả nhiệm vụ của một sự kiện
     * @param eventID ID của sự kiện
     * @return Danh sách các nhiệm vụ cho sự kiện
     */
    public List<TaskAssignmentDepartment> getTasksByEvent(int eventID) {
        List<TaskAssignmentDepartment> tasks = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            
            // We need to join with TaskAssignmentDepartmentResponser to get department info
            // but there might be multiple departments per task, so we need a distinct list of tasks first
            String sql = "SELECT DISTINCT t.* FROM TaskAssignmentDepartment t WHERE t.EventID = ? ORDER BY t.DueDate ASC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, eventID);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                TaskAssignmentDepartment task = new TaskAssignmentDepartment();
                task.setTaskAssignmentDepartmentID(rs.getInt("TaskAssignmentDepartmentID"));
                task.setEventID(rs.getInt("EventID"));
                task.setTerm(rs.getString("Term"));
                task.setTermStart(rs.getTimestamp("TermStart"));
                task.setTermEnd(rs.getTimestamp("TermEnd"));
                task.setDescription(rs.getString("Description"));
                task.setTaskName(rs.getString("TaskName"));
                task.setStatus(rs.getString("Status"));
                task.setStartedDate(rs.getTimestamp("StartedDate"));
                task.setDueDate(rs.getTimestamp("DueDate"));
                
                // Load event name
                getTaskEventDetails(task);
                
                // Load all department responders
                loadTaskResponders(task);
                
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching event tasks: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return tasks;
    }
    
    /**
     * Get event details for a task
     * @param task The task to load event details for
     */
    private void getTaskEventDetails(TaskAssignmentDepartment task) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT e.EventName FROM Events e WHERE e.EventID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, task.getEventID());
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                task.setEventName(rs.getString("EventName"));
            }
        } catch (SQLException e) {
            System.out.println("Error loading event details: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
      /**
     * Lấy thông tin chi tiết của một nhiệm vụ
     * @param taskID ID của nhiệm vụ
     * @return Thông tin chi tiết của nhiệm vụ
     */
    public TaskAssignmentDepartment getTaskById(int taskID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        TaskAssignmentDepartment task = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT t.* FROM TaskAssignmentDepartment t WHERE t.TaskAssignmentDepartmentID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, taskID);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                task = new TaskAssignmentDepartment();
                task.setTaskAssignmentDepartmentID(rs.getInt("TaskAssignmentDepartmentID"));
                task.setEventID(rs.getInt("EventID"));
                task.setTerm(rs.getString("Term"));
                task.setTermStart(rs.getTimestamp("TermStart"));
                task.setTermEnd(rs.getTimestamp("TermEnd"));
                task.setDescription(rs.getString("Description"));
                task.setTaskName(rs.getString("TaskName"));
                task.setStatus(rs.getString("Status"));
                task.setStartedDate(rs.getTimestamp("StartedDate"));
                task.setDueDate(rs.getTimestamp("DueDate"));
                
                // Load event details
                getTaskEventDetails(task);
                
                // Load all department responders
                loadTaskResponders(task);
                
                // For backward compatibility, load the first department's details
                if (!task.getDepartmentResponsers().isEmpty()) {
                    getFirstDepartmentDetails(task);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching task details: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return task;
    }
    
    /**
     * Get details for the first department in the task's responders list
     * @param task The task to load first department details for
     */
    private void getFirstDepartmentDetails(TaskAssignmentDepartment task) {
        if (task.getDepartmentResponsers().isEmpty()) {
            return;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT d.DepartmentName, c.ClubName, c.ClubID " +
                         "FROM ClubDepartments cd " +
                         "JOIN Departments d ON cd.DepartmentID = d.DepartmentID " +
                         "JOIN Clubs c ON cd.ClubID = c.ClubID " +
                         "WHERE cd.ClubDepartmentID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, task.getDepartmentResponsers().get(0).getResponderID());
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                task.setDepartmentName(rs.getString("DepartmentName"));
                task.setClubName(rs.getString("ClubName"));
                task.setClubID(rs.getInt("ClubID"));
            }
        } catch (SQLException e) {
            System.out.println("Error loading department details: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
      /**
     * Tạo một nhiệm vụ mới cho ban
     * @param task Thông tin nhiệm vụ cần tạo
     * @return ID của nhiệm vụ mới tạo, 0 nếu tạo thất bại
     */
    public int createTask(TaskAssignmentDepartment task) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int newTaskId = 0;
        
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // First create the task without ClubDepartmentID
            String sql = "INSERT INTO TaskAssignmentDepartment (EventID, Term, TermStart, TermEnd, " +
                         "Description, TaskName, Status, StartedDate, DueDate) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, task.getEventID());
            stmt.setString(2, task.getTerm());
            stmt.setTimestamp(3, new Timestamp(task.getTermStart().getTime()));
            stmt.setTimestamp(4, new Timestamp(task.getTermEnd().getTime()));
            stmt.setString(5, task.getDescription());
            stmt.setString(6, task.getTaskName());
            stmt.setString(7, task.getStatus());
            stmt.setTimestamp(8, new Timestamp(task.getStartedDate().getTime()));
            stmt.setTimestamp(9, new Timestamp(task.getDueDate().getTime()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    newTaskId = rs.getInt(1);
                    task.setTaskAssignmentDepartmentID(newTaskId);
                    
                    // Now, if there are department responders, add them to the TaskAssignmentDepartmentResponser table
                    if (!task.getDepartmentResponsers().isEmpty() || task.getClubDepartmentID() > 0) {
                        // For backward compatibility - check if there's a single clubDepartmentID
                        if (task.getClubDepartmentID() > 0) {
                            String responderSql = "INSERT INTO TaskAssignmentDepartmentResponser " +
                                                "(TaskAssignmentDepartmentID, ClubDepartmentID) VALUES (?, ?)";
                            PreparedStatement respStmt = conn.prepareStatement(responderSql);
                            respStmt.setInt(1, newTaskId);
                            respStmt.setInt(2, task.getClubDepartmentID());
                            respStmt.executeUpdate();
                            respStmt.close();
                        }
                        
                        // Add all other department responders
                        for (TaskAssignmentDepartmentResponser responder : task.getDepartmentResponsers()) {
                            // Skip if it's the same as the already added one
                            if (responder.getResponderID() == task.getClubDepartmentID()) {
                                continue;
                            }
                            
                            String responderSql = "INSERT INTO TaskAssignmentDepartmentResponser " +
                                                "(TaskAssignmentDepartmentID, ClubDepartmentID) VALUES (?, ?)";
                            PreparedStatement respStmt = conn.prepareStatement(responderSql);
                            respStmt.setInt(1, newTaskId);
                            respStmt.setInt(2, responder.getResponderID());
                            respStmt.executeUpdate();
                            respStmt.close();
                        }
                    }
                }
            }
            
            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback transaction on error
                }
            } catch (SQLException ex) {
                System.out.println("Error rolling back: " + ex.getMessage());
            }
            System.out.println("Error creating task: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                }
            } catch (SQLException e) {
                System.out.println("Error resetting auto-commit: " + e.getMessage());
            }
            closeResources(rs, stmt, conn);
        }
        
        return newTaskId;
    }
      /**
     * Cập nhật thông tin của một nhiệm vụ
     * @param task Thông tin nhiệm vụ cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateTask(TaskAssignmentDepartment task) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // First update the task basic information
            String sql = "UPDATE TaskAssignmentDepartment SET EventID = ?, Term = ?, TermStart = ?, " +
                         "TermEnd = ?, Description = ?, TaskName = ?, Status = ?, StartedDate = ?, " +
                         "DueDate = ? WHERE TaskAssignmentDepartmentID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, task.getEventID());
            stmt.setString(2, task.getTerm());
            stmt.setTimestamp(3, new Timestamp(task.getTermStart().getTime()));
            stmt.setTimestamp(4, new Timestamp(task.getTermEnd().getTime()));
            stmt.setString(5, task.getDescription());
            stmt.setString(6, task.getTaskName());
            stmt.setString(7, task.getStatus());
            stmt.setTimestamp(8, new Timestamp(task.getStartedDate().getTime()));
            stmt.setTimestamp(9, new Timestamp(task.getDueDate().getTime()));
            stmt.setInt(10, task.getTaskAssignmentDepartmentID());
            
            int affectedRows = stmt.executeUpdate();
            success = affectedRows > 0;
            
            // If task was updated successfully and we want to manage responders
            if (success && (task.getDepartmentResponsers().size() > 0 || task.getClubDepartmentID() > 0)) {
                // For simplicity, delete all existing responders and add new ones
                String deleteRespondersSql = "DELETE FROM TaskAssignmentDepartmentResponser WHERE TaskAssignmentDepartmentID = ?";
                PreparedStatement deleteStmt = conn.prepareStatement(deleteRespondersSql);
                deleteStmt.setInt(1, task.getTaskAssignmentDepartmentID());
                deleteStmt.executeUpdate();
                deleteStmt.close();
                
                // Add new responders
                // For backward compatibility - check if there's a single clubDepartmentID
                if (task.getClubDepartmentID() > 0) {
                    String responderSql = "INSERT INTO TaskAssignmentDepartmentResponser " +
                                        "(TaskAssignmentDepartmentID, ClubDepartmentID) VALUES (?, ?)";
                    PreparedStatement respStmt = conn.prepareStatement(responderSql);
                    respStmt.setInt(1, task.getTaskAssignmentDepartmentID());
                    respStmt.setInt(2, task.getClubDepartmentID());
                    respStmt.executeUpdate();
                    respStmt.close();
                }
                
                // Add all other department responders
                for (TaskAssignmentDepartmentResponser responder : task.getDepartmentResponsers()) {
                    // Skip if it's the same as the already added one
                    if (responder.getResponderID() == task.getClubDepartmentID()) {
                        continue;
                    }
                    
                    String responderSql = "INSERT INTO TaskAssignmentDepartmentResponser " +
                                        "(TaskAssignmentDepartmentID, ClubDepartmentID) VALUES (?, ?)";
                    PreparedStatement respStmt = conn.prepareStatement(responderSql);
                    respStmt.setInt(1, task.getTaskAssignmentDepartmentID());
                    respStmt.setInt(2, responder.getResponderID());
                    respStmt.executeUpdate();
                    respStmt.close();
                }
            }
            
            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback transaction on error
                }
            } catch (SQLException ex) {
                System.out.println("Error rolling back: " + ex.getMessage());
            }
            System.out.println("Error updating task: " + e.getMessage());
            success = false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                }
            } catch (SQLException e) {
                System.out.println("Error resetting auto-commit: " + e.getMessage());
            }
            closeResources(null, stmt, conn);
        }
        
        return success;
    }
    
    /**
     * Cập nhật trạng thái của một nhiệm vụ
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
            String sql = "UPDATE TaskAssignmentDepartment SET Status = ? WHERE TaskAssignmentDepartmentID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, taskID);
            
            int affectedRows = stmt.executeUpdate();
            success = affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error updating task status: " + e.getMessage());
        } finally {
            closeResources(null, stmt, conn);
        }
        
        return success;
    }
      /**
     * Xóa một nhiệm vụ
     * @param taskID ID của nhiệm vụ cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteTask(int taskID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // 1. Delete task member assignments
            String deleteMemberTasksSql = "DELETE FROM TaskAssignmentMember WHERE TaskAssignmentDepartmentResponserID IN " +
                                        "(SELECT TaskAssignmentDepartmentResponserID FROM TaskAssignmentDepartmentResponser " +
                                        "WHERE TaskAssignmentDepartmentID = ?)";
            stmt = conn.prepareStatement(deleteMemberTasksSql);
            stmt.setInt(1, taskID);
            stmt.executeUpdate();
            stmt.close();
            
            // 2. Delete task department responders
            String deleteRespondersSql = "DELETE FROM TaskAssignmentDepartmentResponser WHERE TaskAssignmentDepartmentID = ?";
            stmt = conn.prepareStatement(deleteRespondersSql);
            stmt.setInt(1, taskID);
            stmt.executeUpdate();
            stmt.close();
            
            // 3. Delete main task
            String sql = "DELETE FROM TaskAssignmentDepartment WHERE TaskAssignmentDepartmentID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, taskID);
            
            int affectedRows = stmt.executeUpdate();
            success = affectedRows > 0;
            
            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback transaction on error
                }
            } catch (SQLException ex) {
                System.out.println("Error rolling back: " + ex.getMessage());
            }
            System.out.println("Error deleting task: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                }
            } catch (SQLException e) {
                System.out.println("Error resetting auto-commit: " + e.getMessage());
            }
            closeResources(null, stmt, conn);
        }
        
        return success;
    }
    
    /**
     * Extract task data from ResultSet
     */    private TaskAssignmentDepartment extractTaskFromResultSet(ResultSet rs) throws SQLException {
        TaskAssignmentDepartment task = new TaskAssignmentDepartment();
        task.setTaskAssignmentDepartmentID(rs.getInt("TaskAssignmentDepartmentID"));
        task.setEventID(rs.getInt("EventID"));
        task.setTerm(rs.getString("Term"));
        task.setTermStart(rs.getTimestamp("TermStart"));
        task.setTermEnd(rs.getTimestamp("TermEnd"));
        task.setDescription(rs.getString("Description"));
        task.setTaskName(rs.getString("TaskName"));
        task.setStatus(rs.getString("Status"));
        task.setStartedDate(rs.getTimestamp("StartedDate"));
        task.setDueDate(rs.getTimestamp("DueDate"));
        
        // Set view properties
        task.setEventName(rs.getString("EventName"));
        task.setDepartmentName(rs.getString("DepartmentName"));
        task.setClubName(rs.getString("ClubName"));
        task.setClubID(rs.getInt("ClubID"));
        
        // For backward compatibility with existing code
        try {
            int clubDepartmentID = rs.getInt("ClubDepartmentID");
            if (!rs.wasNull()) {
                // Create a responder for the department
                TaskAssignmentDepartmentResponser responder = new TaskAssignmentDepartmentResponser();
                responder.setTaskAssignmentID(task.getTaskAssignmentDepartmentID());
                responder.setResponderID(clubDepartmentID);
                task.addDepartmentResponser(responder);
            }
        } catch (SQLException e) {
            // ClubDepartmentID column might not exist in some queries, which is fine
            // Just continue without setting it
        }
        
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

    /**
     * Test method to check what clubDepartmentIDs exist in the database
     */
    public void checkAvailableClubDepartmentIDs() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            System.out.println("DEBUG: Checking available ClubDepartmentIDs...");
            
            // Check ClubDepartments table
            String sql1 = "SELECT ClubDepartmentID, ClubID, DepartmentID FROM ClubDepartments";
            stmt = conn.prepareStatement(sql1);
            rs = stmt.executeQuery();
            
            System.out.println("DEBUG: ClubDepartments table:");
            while (rs.next()) {
                System.out.println("  ClubDepartmentID=" + rs.getInt("ClubDepartmentID") + 
                                   ", ClubID=" + rs.getInt("ClubID") + 
                                   ", DepartmentID=" + rs.getInt("DepartmentID"));
            }
            rs.close();
            stmt.close();
            
            // Check TaskAssignmentDepartment table
            String sql2 = "SELECT TaskAssignmentDepartmentID, TaskName, EventID FROM TaskAssignmentDepartment";
            stmt = conn.prepareStatement(sql2);
            rs = stmt.executeQuery();
            
            System.out.println("DEBUG: TaskAssignmentDepartment table:");
            while (rs.next()) {
                System.out.println("  TaskAssignmentDepartmentID=" + rs.getInt("TaskAssignmentDepartmentID") + 
                                   ", TaskName=" + rs.getString("TaskName") + 
                                   ", EventID=" + rs.getInt("EventID"));
            }
            rs.close();
            stmt.close();
              // Check TaskAssignmentDepartmentResponser table
            String sql3 = "SELECT TaskAssignmentDepartmentResponserID, TaskAssignmentDepartmentID, ClubDepartmentID FROM TaskAssignmentDepartmentResponser";
            stmt = conn.prepareStatement(sql3);
            rs = stmt.executeQuery();
            
            System.out.println("DEBUG: TaskAssignmentDepartmentResponser table:");
            while (rs.next()) {
                System.out.println("  TaskAssignmentDepartmentResponserID=" + rs.getInt("TaskAssignmentDepartmentResponserID") + 
                                   ", TaskAssignmentDepartmentID=" + rs.getInt("TaskAssignmentDepartmentID") + 
                                   ", ClubDepartmentID=" + rs.getInt("ClubDepartmentID"));
            }
            
        } catch (SQLException e) {
            System.out.println("Error checking database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }
    }
    
    /**
     * Method to create some test data for debugging
     */
    public void createTestData() {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBContext.getConnection();
            
            System.out.println("DEBUG: Creating test data...");
            
            // Insert a test task
            String sql1 = "INSERT INTO TaskAssignmentDepartment (EventID, Term, TermStart, TermEnd, Description, TaskName, Status, StartedDate, DueDate) " +
                         "VALUES (1, 'Trước sự kiện', '2025-01-01', '2025-01-31', 'Test task description', 'Test Task 1', 'ToDo', '2025-01-01', '2025-01-15')";
            stmt = conn.prepareStatement(sql1);
            stmt.executeUpdate();
            System.out.println("DEBUG: Inserted test task");
            stmt.close();
            
            // Get the last inserted task ID
            String sql2 = "SELECT LAST_INSERT_ID() as taskID";
            stmt = conn.prepareStatement(sql2);
            ResultSet rs = stmt.executeQuery();
            int taskID = 0;
            if (rs.next()) {
                taskID = rs.getInt("taskID");
                System.out.println("DEBUG: New task ID = " + taskID);
            }
            rs.close();
            stmt.close();
              // Insert responder with clubDepartmentID = 1
            if (taskID > 0) {
                String sql3 = "INSERT INTO TaskAssignmentDepartmentResponser (TaskAssignmentDepartmentID, ClubDepartmentID) VALUES (?, 1)";
                stmt = conn.prepareStatement(sql3);
                stmt.setInt(1, taskID);
                stmt.executeUpdate();
                System.out.println("DEBUG: Inserted responder for clubDepartmentID = 1");
                stmt.close();
            }
            
        } catch (SQLException e) {
            System.out.println("Error creating test data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
