package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.DepartmentDashboard;

/**
 * Data Access Object for Department Dashboard
 * Handles all database operations for Department Leader dashboard statistics
 * @author Department Management Module
 */
public class DepartmentDashboardDAO {    /**
     * Get department information for a department leader
     * @param userId ID of the department leader
     * @return DepartmentDashboard object with basic department info
     */    public DepartmentDashboard getDepartmentInfo(String userId) {
        String sql = """
            SELECT cd.DepartmentID, d.DepartmentName, 
                   cd.ClubID, c.ClubName, cd.ClubDepartmentID
            FROM UserClubs uc
            JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID  
            JOIN Departments d ON cd.DepartmentID = d.DepartmentID
            JOIN Clubs c ON cd.ClubID = c.ClubID
            WHERE uc.UserID = ? AND uc.RoleID = 3 AND uc.IsActive = 1
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new DepartmentDashboard(
                    rs.getInt("ClubDepartmentID"),
                    rs.getInt("DepartmentID"),
                    rs.getString("DepartmentName"),
                    rs.getInt("ClubID"),
                    rs.getString("ClubName")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get member statistics for a department
     * @param clubDepartmentId ID of the club department
     * @return DepartmentDashboard with member statistics filled
     */    public void fillMemberStatistics(DepartmentDashboard dashboard, int clubDepartmentId) {
        String sql = """
            SELECT 
                COUNT(DISTINCT uc.UserID) as totalMembers,
                COUNT(DISTINCT CASE WHEN uc.IsActive = 1 THEN uc.UserID END) as activeMembers
            FROM UserClubs uc
            WHERE uc.ClubDepartmentID = ?
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, clubDepartmentId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int totalMembers = rs.getInt("totalMembers");
                int activeMembers = rs.getInt("activeMembers");
                
                dashboard.setTotalMembers(totalMembers);
                dashboard.setActiveMembers(activeMembers);
                
                // Calculate activity rate
                if (totalMembers > 0) {
                    double activityRate = (double) activeMembers / totalMembers * 100;
                    dashboard.setMemberActivityRate(Math.round(activityRate * 100.0) / 100.0);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }/**
     * Get task statistics for a department (sửa để dùng clubDepartmentId)
     * @param clubDepartmentId ID của club department (khóa chính)
     * @return DepartmentDashboard with task statistics filled
     */
    public void fillTaskStatistics(DepartmentDashboard dashboard, int clubDepartmentId) {
        String sql = """
            SELECT 
                COUNT(*) as totalTasks,
                SUM(CASE WHEN t.Status = 'ToDo' THEN 1 ELSE 0 END) as todoTasks,
                SUM(CASE WHEN t.Status = 'InProgress' THEN 1 ELSE 0 END) as inProgressTasks,
                SUM(CASE WHEN t.Status = 'Review' THEN 1 ELSE 0 END) as reviewTasks,
                SUM(CASE WHEN t.Status = 'Done' THEN 1 ELSE 0 END) as doneTasks,
                AVG(t.ProgressPercent) as averageProgress
            FROM Tasks t
            JOIN TaskAssignees ta ON t.TaskID = ta.TaskID
            JOIN ClubDepartments cd ON ta.DepartmentID = cd.DepartmentID
            WHERE ta.AssigneeType = 'Department' AND cd.ClubDepartmentID = ?
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, clubDepartmentId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                dashboard.setTotalTasks(rs.getInt("totalTasks"));
                dashboard.setTodoTasks(rs.getInt("todoTasks"));
                dashboard.setInProgressTasks(rs.getInt("inProgressTasks"));
                dashboard.setReviewTasks(rs.getInt("reviewTasks"));
                dashboard.setDoneTasks(rs.getInt("doneTasks"));
                
                // Handle null average (when no tasks exist)
                double avgProgress = rs.getDouble("averageProgress");
                if (!rs.wasNull()) {
                    dashboard.setAverageProgress(Math.round(avgProgress * 100.0) / 100.0);
                } else {
                    dashboard.setAverageProgress(0.0);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    /**
     * Get event statistics for a department (sửa để dùng clubDepartmentId)
     * @param clubDepartmentId ID của club department (khóa chính)
     * @return DepartmentDashboard with event statistics filled
     */
    public void fillEventStatistics(DepartmentDashboard dashboard, int clubDepartmentId) {
        String sql = """
            SELECT 
                COUNT(DISTINCT t.EventID) as totalEvents,
                SUM(CASE WHEN e.EventDate > NOW() THEN 1 ELSE 0 END) as upcomingEvents,
                SUM(CASE WHEN e.Status = 'Completed' THEN 1 ELSE 0 END) as completedEvents
            FROM Tasks t
            JOIN TaskAssignees ta ON t.TaskID = ta.TaskID
            JOIN Events e ON t.EventID = e.EventID
            JOIN ClubDepartments cd ON ta.DepartmentID = cd.DepartmentID
            WHERE ta.AssigneeType = 'Department' AND cd.ClubDepartmentID = ?
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, clubDepartmentId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                dashboard.setTotalEvents(rs.getInt("totalEvents"));
                dashboard.setUpcomingEvents(rs.getInt("upcomingEvents"));
                dashboard.setCompletedEvents(rs.getInt("completedEvents"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    /**
     * Get complete dashboard statistics for a department leader
     * @param userId ID of the department leader
     * @return Complete DepartmentDashboard with all statistics
     */
    public DepartmentDashboard getCompleteDashboard(String userId) {
        // First get department info
        DepartmentDashboard dashboard = getDepartmentInfo(userId);
        
        if (dashboard != null) {            // Fill member statistics
            fillMemberStatistics(dashboard, dashboard.getClubDepartmentId());
            
            // Fill task và event statistics (đã sửa để dùng clubDepartmentId)
            fillTaskStatistics(dashboard, dashboard.getClubDepartmentId());
            fillEventStatistics(dashboard, dashboard.getClubDepartmentId());
            
            // Set weekly meetings (placeholder)
            dashboard.setWeeklyMeetings(0);
        }
        
        return dashboard;
    }    /**
     * Check if user is a department leader (sử dụng logic giống DepartmentMemberDAO)
     * @param userId ID of the user
     * @return true if user is department leader, false otherwise
     */    
    public boolean isDepartmentLeader(String userId) {
        String sql = """
            SELECT uc.ClubDepartmentID
            FROM UserClubs uc
            WHERE uc.UserID = ? AND uc.RoleID = 3 AND uc.IsActive = 1
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            
            return rs.next(); // Trả về true nếu tìm thấy ít nhất 1 record
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * Lấy Club Department ID của trưởng ban (tái sử dụng logic từ DepartmentMemberDAO)
     * @param userId ID của user
     * @return clubDepartmentID nếu user là trưởng ban, 0 nếu không phải
     */
    public int getClubDepartmentIdByLeader(String userId) {
        String sql = """
            SELECT uc.ClubDepartmentID
            FROM UserClubs uc
            WHERE uc.UserID = ? AND uc.RoleID = 3 AND uc.IsActive = 1
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("ClubDepartmentID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
     public boolean isDepartmentLeaderIndoingoai(String userId, int clubID) {
        String sql = """
                     SELECT  uc.ClubDepartmentID
                     FROM UserClubs uc
                     WHERE uc.UserID = ? AND uc.RoleID = 3 AND uc.IsActive = 1 AND uc.ClubDepartmentID=6 and uc.ClubID = ?""";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            ps.setObject(2, clubID);
            ResultSet rs = ps.executeQuery();
            
            return rs.next(); // Trả về true nếu tìm thấy ít nhất 1 record
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
