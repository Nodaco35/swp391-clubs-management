package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.DepartmentDashboard;

/**
 * Data Access Object for Department Dashboard Handles all database operations
 * for Department Leader dashboard statistics
 *
 * @author Department Management Module
 */
public class DepartmentDashboardDAO {

    /**
     * Get department information for a department leader
     *
     * @param userId ID of the department leader
     * @return DepartmentDashboard object with basic department info
     */
    public DepartmentDashboard getDepartmentInfo(String userId) {
        String sql = """
            SELECT cd.DepartmentID, d.DepartmentName, 
                   cd.ClubID, c.ClubName, cd.ClubDepartmentID
            FROM UserClubs uc
            JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID  
            JOIN Departments d ON cd.DepartmentID = d.DepartmentID
            JOIN Clubs c ON cd.ClubID = c.ClubID
            WHERE uc.UserID = ? AND uc.RoleID = 3 AND uc.IsActive = 1
            """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            System.out.println("DEBUG DepartmentDashboardDAO - Checking user: " + userId);
            System.out.println("DEBUG DepartmentDashboardDAO - SQL: " + sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                DepartmentDashboard result = new DepartmentDashboard(
                        rs.getInt("ClubDepartmentID"),
                        rs.getInt("DepartmentID"),
                        rs.getString("DepartmentName"),
                        rs.getInt("ClubID"),
                        rs.getString("ClubName")
                );
                System.out.println("DEBUG DepartmentDashboardDAO - Found department: " + result.getDepartmentName() + 
                                 ", ClubID: " + result.getClubId() + ", DepartmentID: " + result.getDepartmentId());
                return result;
            } else {
                System.out.println("DEBUG DepartmentDashboardDAO - No department found for user: " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get member statistics for a department
     *
     * @param clubDepartmentId ID of the club department
     * @return DepartmentDashboard with member statistics filled
     */
    public void fillMemberStatistics(DepartmentDashboard dashboard, int clubDepartmentId) {
        String sql = """
            SELECT 
                COUNT(DISTINCT uc.UserID) as totalMembers,
                COUNT(DISTINCT CASE WHEN uc.IsActive = 1 THEN uc.UserID END) as activeMembers
            FROM UserClubs uc
            WHERE uc.ClubDepartmentID = ?
            """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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
    }

    /**
     * Get task statistics for a department (sửa để dùng clubDepartmentId)
     *
     * @param clubDepartmentId ID của club department (khóa chính)
     * @return DepartmentDashboard with task statistics filled
     */
    public void fillTaskStatistics(DepartmentDashboard dashboard, int clubDepartmentId) {
        // Lấy ClubID và DepartmentID từ ClubDepartmentID trước
        String getInfoSql = """
            SELECT cd.ClubID, cd.DepartmentID 
            FROM ClubDepartments cd 
            WHERE cd.ClubDepartmentID = ?
            """;
        
        try (Connection conn = DBContext.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(getInfoSql)) {
            
            ps.setInt(1, clubDepartmentId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int clubId = rs.getInt("ClubID");
                int departmentId = rs.getInt("DepartmentID");
                
                // Bây giờ query Tasks với ClubID và DepartmentID
                String taskSql = """
                    SELECT 
                        COUNT(*) as totalTasks,
                        SUM(CASE WHEN Status = 'ToDo' THEN 1 ELSE 0 END) as todoTasks,
                        SUM(CASE WHEN Status = 'InProgress' THEN 1 ELSE 0 END) as inProgressTasks,
                        SUM(CASE WHEN Status = 'Review' THEN 1 ELSE 0 END) as reviewTasks,
                        SUM(CASE WHEN Status = 'Done' THEN 1 ELSE 0 END) as doneTasks
                    FROM Tasks 
                    WHERE AssigneeType = 'Department' AND ClubID = ? AND DepartmentID = ?
                    """;
                
                try (PreparedStatement taskPs = conn.prepareStatement(taskSql)) {
                    taskPs.setInt(1, clubId);
                    taskPs.setInt(2, departmentId);
                    ResultSet taskRs = taskPs.executeQuery();
                    
                    if (taskRs.next()) {
                        dashboard.setTotalTasks(taskRs.getInt("totalTasks"));
                        dashboard.setTodoTasks(taskRs.getInt("todoTasks"));
                        dashboard.setInProgressTasks(taskRs.getInt("inProgressTasks"));
                        dashboard.setReviewTasks(taskRs.getInt("reviewTasks"));
                        dashboard.setDoneTasks(taskRs.getInt("doneTasks"));
                        dashboard.setAverageProgress(0.0);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get event statistics for a department (sửa để dùng clubDepartmentId)
     *
     * @param clubDepartmentId ID của club department (khóa chính)
     * @return DepartmentDashboard with event statistics filled
     */
    public void fillEventStatistics(DepartmentDashboard dashboard, int clubDepartmentId) {
        // Lấy ClubID và DepartmentID từ ClubDepartmentID trước
        String getInfoSql = """
            SELECT cd.ClubID, cd.DepartmentID 
            FROM ClubDepartments cd 
            WHERE cd.ClubDepartmentID = ?
            """;
        
        try (Connection conn = DBContext.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(getInfoSql)) {
            
            ps.setInt(1, clubDepartmentId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int clubId = rs.getInt("ClubID");
                int departmentId = rs.getInt("DepartmentID");
                
                // Query Events thông qua Tasks
                String eventSql = """
                    SELECT 
                        COUNT(DISTINCT t.EventID) as totalEvents,
                        SUM(CASE WHEN es.EventDate > CURDATE() THEN 1 ELSE 0 END) as upcomingEvents,
                        SUM(CASE WHEN e.Status = 'Completed' THEN 1 ELSE 0 END) as completedEvents
                    FROM Tasks t
                    LEFT JOIN Events e ON t.EventID = e.EventID
                    LEFT JOIN EventSchedules es ON e.EventID = es.EventID
                    WHERE t.AssigneeType = 'Department' AND t.ClubID = ? AND t.DepartmentID = ? 
                      AND t.EventID IS NOT NULL
                    """;
                
                try (PreparedStatement eventPs = conn.prepareStatement(eventSql)) {
                    eventPs.setInt(1, clubId);
                    eventPs.setInt(2, departmentId);
                    ResultSet eventRs = eventPs.executeQuery();
                    
                    if (eventRs.next()) {
                        dashboard.setTotalEvents(eventRs.getInt("totalEvents"));
                        dashboard.setUpcomingEvents(eventRs.getInt("upcomingEvents"));
                        dashboard.setCompletedEvents(eventRs.getInt("completedEvents"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get complete dashboard statistics for a department leader
     *
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
    }

    /**
     * Check if user is a department leader (sử dụng logic giống
     * DepartmentMemberDAO)
     *
     * @param userId ID of the user
     * @return true if user is department leader, false otherwise
     */
    public boolean isDepartmentLeader(String userId) {
        String sql = """
            SELECT uc.ClubDepartmentID
            FROM UserClubs uc
            WHERE uc.UserID = ? AND uc.RoleID = 3 AND uc.IsActive = 1
            """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            return rs.next(); // Trả về true nếu tìm thấy ít nhất 1 record
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy Club Department ID của trưởng ban (tái sử dụng logic từ
     * DepartmentMemberDAO)
     *
     * @param userId ID của user
     * @return clubDepartmentID nếu user là trưởng ban, 0 nếu không phải
     */
    public int getClubDepartmentIdByLeader(String userId) {
        String sql = """
            SELECT uc.ClubDepartmentID
            FROM UserClubs uc
            WHERE uc.UserID = ? AND uc.RoleID = 3 AND uc.IsActive = 1
            """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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
                     SELECT  *
                     FROM UserClubs uc
                     Join ClubDepartments cd on uc.ClubDepartmentID = cd.ClubDepartmentID
                     WHERE uc.UserID = ? AND uc.RoleID = 3 AND uc.IsActive = 1 AND cd.DepartmentID = 6 and uc.ClubID = ?""";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setObject(2, clubID);
            ResultSet rs = ps.executeQuery();

            return rs.next(); // Trả về true nếu tìm thấy ít nhất 1 record
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getClubMemberCount(int clubID) {
        String sql = "SELECT COUNT(UserID) AS membercount FROM userclubs WHERE clubID = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("membercount");
            }
        } catch (Exception e) {
              e.printStackTrace();
        }
        return 0;
    }
    
    public int findClubDepartmentId(int clubId, String userId) {
        String sql = """
            SELECT uc.ClubDepartmentID
            FROM UserClubs uc
            JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
            WHERE uc.UserID = ? AND cd.ClubID = ? AND uc.IsActive = 1
            """;
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            ps.setInt(2, clubId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("ClubDepartmentID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public boolean isDepartmentLeaderHauCan(String userId, int clubID) {
        String sql = """
                     SELECT uc.ClubDepartmentID
                     FROM UserClubs uc
                     JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
                     WHERE uc.UserID = ? AND uc.RoleID = 3 AND uc.IsActive = 1 AND cd.DepartmentID=5 AND cd.ClubID = ?""";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            ps.setInt(2, clubID);
            ResultSet rs = ps.executeQuery();
            
            return rs.next(); // Trả về true nếu tìm thấy ít nhất 1 record
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public String getExternalLeaderID(int clubID) {
        String sql = """
                     SELECT uc.UserID
                     FROM UserClubs uc
                     JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
                     WHERE cd.ClubID = ? AND cd.DepartmentID = 6 AND uc.RoleID = 3 AND uc.IsActive = 1
                     """;
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("UserID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
