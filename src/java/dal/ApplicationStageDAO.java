package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import models.ApplicationStage;
import models.RecruitmentStage;

public class ApplicationStageDAO {
    
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    // Create new application stage status
    public int createApplicationStage(ApplicationStage appStage) {
        int newAppStageId = 0;
        try {
            conn = DBContext.getConnection();
            String sql = "INSERT INTO ApplicationStages "
                    + "(ApplicationID, StageID, Status, UpdatedAt, UpdatedBy) "
                    + "VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, appStage.getApplicationID());
            ps.setInt(2, appStage.getStageID());
            ps.setString(3, appStage.getStatus());
            ps.setTimestamp(4, new Timestamp(new Date().getTime())); // Current timestamp
            ps.setString(5, appStage.getUpdatedBy());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    newAppStageId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return newAppStageId;
    }
    
    // Update application stage status
    public boolean updateApplicationStageStatus(int applicationStageID, String status, String updatedBy) {
        try {
            conn = DBContext.getConnection();
            String sql = "UPDATE ApplicationStages SET "
                    + "Status = ?, UpdatedAt = ?, UpdatedBy = ? "
                    + "WHERE ApplicationStageID = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setTimestamp(2, new Timestamp(new Date().getTime())); // Current timestamp
            ps.setString(3, updatedBy);
            ps.setInt(4, applicationStageID);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }
    
    // Get application stage by ID
    public ApplicationStage getApplicationStageById(int applicationStageID) {
        ApplicationStage appStage = null;
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT ast.*, rs.StageName, ca.UserID, u1.FullName as ApplicationUserName, "
                    + "ca.Email as ApplicationEmail, u2.FullName as UpdatedByName "
                    + "FROM ApplicationStages ast "
                    + "JOIN RecruitmentStages rs ON ast.StageID = rs.StageID "
                    + "JOIN ClubApplications ca ON ast.ApplicationID = ca.ApplicationID "
                    + "LEFT JOIN Users u1 ON ca.UserID = u1.UserID "
                    + "LEFT JOIN Users u2 ON ast.UpdatedBy = u2.UserID "
                    + "WHERE ast.ApplicationStageID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, applicationStageID);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                appStage = new ApplicationStage(
                        rs.getInt("ApplicationStageID"),
                        rs.getInt("ApplicationID"),
                        rs.getInt("StageID"),
                        rs.getString("Status"),
                        rs.getTimestamp("UpdatedAt"),
                        null, // notes not stored in this table
                        rs.getString("UpdatedBy"),
                        rs.getString("StageName"),
                        rs.getString("ApplicationUserName"),
                        rs.getString("ApplicationEmail"),
                        rs.getString("UpdatedByName")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return appStage;
    }
    
    // Get all application stages for a specific stage
    public List<ApplicationStage> getApplicationStagesByStageId(int stageID) {
        List<ApplicationStage> appStages = new ArrayList<>();
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT ast.*, rs.StageName, ca.UserID, u1.FullName as ApplicationUserName, "
                    + "ca.Email as ApplicationEmail, u2.FullName as UpdatedByName "
                    + "FROM ApplicationStages ast "
                    + "JOIN RecruitmentStages rs ON ast.StageID = rs.StageID "
                    + "JOIN ClubApplications ca ON ast.ApplicationID = ca.ApplicationID "
                    + "LEFT JOIN Users u1 ON ca.UserID = u1.UserID "
                    + "LEFT JOIN Users u2 ON ast.UpdatedBy = u2.UserID "
                    + "WHERE ast.StageID = ? "
                    + "ORDER BY ast.UpdatedAt DESC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, stageID);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                ApplicationStage appStage = new ApplicationStage(
                        rs.getInt("ApplicationStageID"),
                        rs.getInt("ApplicationID"),
                        rs.getInt("StageID"),
                        rs.getString("Status"),
                        rs.getTimestamp("UpdatedAt"),
                        null, // notes not stored in this table
                        rs.getString("UpdatedBy"),
                        rs.getString("StageName"),
                        rs.getString("ApplicationUserName"),
                        rs.getString("ApplicationEmail"),
                        rs.getString("UpdatedByName")
                );
                appStages.add(appStage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return appStages;
    }
    
    // Get all application stages for a specific application
    public List<ApplicationStage> getApplicationStagesByApplicationId(int applicationID) {
        List<ApplicationStage> appStages = new ArrayList<>();
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT ast.*, rs.StageName, ca.UserID, u1.FullName as ApplicationUserName, "
                    + "ca.Email as ApplicationEmail, u2.FullName as UpdatedByName "
                    + "FROM ApplicationStages ast "
                    + "JOIN RecruitmentStages rs ON ast.StageID = rs.StageID "
                    + "JOIN ClubApplications ca ON ast.ApplicationID = ca.ApplicationID "
                    + "LEFT JOIN Users u1 ON ca.UserID = u1.UserID "
                    + "LEFT JOIN Users u2 ON ast.UpdatedBy = u2.UserID "
                    + "WHERE ast.ApplicationID = ? "
                    + "ORDER BY rs.StartDate";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, applicationID);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                ApplicationStage appStage = new ApplicationStage(
                        rs.getInt("ApplicationStageID"),
                        rs.getInt("ApplicationID"),
                        rs.getInt("StageID"),
                        rs.getString("Status"),
                        rs.getTimestamp("UpdatedAt"),
                        null, // notes not stored in this table
                        rs.getString("UpdatedBy"),
                        rs.getString("StageName"),
                        rs.getString("ApplicationUserName"),
                        rs.getString("ApplicationEmail"),
                        rs.getString("UpdatedByName")
                );
                appStages.add(appStage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return appStages;
    }
    
    // Get application stages by stage ID and status
    public List<ApplicationStage> getApplicationStagesByStageAndStatus(int stageID, String status) {
        List<ApplicationStage> appStages = new ArrayList<>();
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT ast.*, rs.StageName, ca.UserID, u1.FullName as ApplicationUserName, "
                    + "ca.Email as ApplicationEmail, u2.FullName as UpdatedByName "
                    + "FROM ApplicationStages ast "
                    + "JOIN RecruitmentStages rs ON ast.StageID = rs.StageID "
                    + "JOIN ClubApplications ca ON ast.ApplicationID = ca.ApplicationID "
                    + "LEFT JOIN Users u1 ON ca.UserID = u1.UserID "
                    + "LEFT JOIN Users u2 ON ast.UpdatedBy = u2.UserID "
                    + "WHERE ast.StageID = ? AND ast.Status = ? "
                    + "ORDER BY ast.UpdatedAt DESC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, stageID);
            ps.setString(2, status);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                ApplicationStage appStage = new ApplicationStage(
                        rs.getInt("ApplicationStageID"),
                        rs.getInt("ApplicationID"),
                        rs.getInt("StageID"),
                        rs.getString("Status"),
                        rs.getTimestamp("UpdatedAt"),
                        null, // notes not stored in this table
                        rs.getString("UpdatedBy"),
                        rs.getString("StageName"),
                        rs.getString("ApplicationUserName"),
                        rs.getString("ApplicationEmail"),
                        rs.getString("UpdatedByName")
                );
                appStages.add(appStage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return appStages;
    }
    
    // Get statistics for candidates by stage and status for a recruitment campaign
    public java.util.Map<Integer, java.util.Map<String, Integer>> getStageStats(int recruitmentId) {
        java.util.Map<Integer, java.util.Map<String, Integer>> stageStats = new java.util.HashMap<>();
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT rs.StageID, " +
                        "ast.Status, " +
                        "COUNT(*) as StatusCount " +
                        "FROM RecruitmentStages rs " +
                        "LEFT JOIN ApplicationStages ast ON rs.StageID = ast.StageID " +
                        "LEFT JOIN ClubApplications ca ON ast.ApplicationID = ca.ApplicationID " +
                        "WHERE rs.RecruitmentID = ? " +
                        "GROUP BY rs.StageID, ast.Status " +
                        "ORDER BY rs.StageID";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, recruitmentId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                int stageId = rs.getInt("StageID");
                String status = rs.getString("Status");
                int count = rs.getInt("StatusCount");
                
                if (!stageStats.containsKey(stageId)) {
                    stageStats.put(stageId, new java.util.HashMap<>());
                }
                
                if (status != null) {
                    stageStats.get(stageId).put(status.toUpperCase(), count);
                }
            }
            
            // Calculate totals and fill missing statuses
            for (Integer stageId : stageStats.keySet()) {
                java.util.Map<String, Integer> statMap = stageStats.get(stageId);
                
                // Ensure all status types exist
                statMap.putIfAbsent("PENDING", 0);
                statMap.putIfAbsent("APPROVED", 0);
                statMap.putIfAbsent("REJECTED", 0);
                
                // Calculate total
                int total = statMap.get("PENDING") + statMap.get("APPROVED") + statMap.get("REJECTED");
                statMap.put("TOTAL", total);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        
        return stageStats;
    }

    // Debug method to test database connection and stage stats
    public void testStageStats() {
        System.out.println("=== Testing ApplicationStageDAO.getStageStats ===");
        
        try {
            conn = DBContext.getConnection();
            System.out.println("Database connection: SUCCESS");
            
            // Test với recruitmentId = 1
            java.util.Map<Integer, java.util.Map<String, Integer>> testResult = getStageStats(1);
            System.out.println("Stage stats for recruitment ID 1: " + testResult.size() + " stages found");
            
            for (Integer stageId : testResult.keySet()) {
                java.util.Map<String, Integer> stats = testResult.get(stageId);
                System.out.println("Stage " + stageId + " stats: " + stats);
            }
            
        } catch (Exception e) {
            System.err.println("Database connection ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Lấy vòng tuyển đầu tiên (APPLICATION) của chiến dịch tuyển quân hiện tại của CLB
    public RecruitmentStage getFirstRecruitmentStage(int clubId) {
        RecruitmentStage stage = null;
        try {
            conn = DBContext.getConnection();
            // Truy vấn vòng APPLICATION của chiến dịch hiện tại (ONGOING hoặc UPCOMING gần nhất) của CLB
            String sql = """
                SELECT rs.* 
                FROM RecruitmentStages rs
                JOIN RecruitmentCampaigns rc ON rs.RecruitmentID = rc.RecruitmentID
                WHERE rc.ClubID = ? 
                AND rs.StageName = 'APPLICATION'
                AND (rc.Status = 'ONGOING' OR rc.Status = 'UPCOMING')
                ORDER BY rc.StartDate DESC, rs.StartDate ASC
                LIMIT 1
                """;
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, clubId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                stage = new RecruitmentStage();
                stage.setStageID(rs.getInt("StageID"));
                stage.setRecruitmentID(rs.getInt("RecruitmentID"));
                stage.setStageName(rs.getString("StageName"));
                stage.setStatus(rs.getString("Status"));
                stage.setStartDate(rs.getDate("StartDate"));
                stage.setEndDate(rs.getDate("EndDate"));
                stage.setLocationID(rs.getInt("LocationID"));
                stage.setDescription(rs.getString("Description"));
                stage.setCreatedAt(rs.getTimestamp("CreatedAt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return stage;
    }

    // Helper method to close database resources
    private void closeResources() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
