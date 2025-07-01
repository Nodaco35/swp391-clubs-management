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
import models.RecruitmentStage;

public class RecruitmentStageDAO {
    
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    // Create new recruitment stage
    public int createRecruitmentStage(RecruitmentStage stage) {
        int newStageId = 0;
        try {
            conn = DBContext.getConnection();
            String sql = "INSERT INTO RecruitmentStages "
                    + "(RecruitmentID, StageName, Status, StartDate, EndDate, LocationID, Description, CreatedAt) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, stage.getRecruitmentID());
            ps.setString(2, stage.getStageName());
            ps.setString(3, stage.getStatus());
            ps.setTimestamp(4, stage.getStartDate() != null ? new Timestamp(stage.getStartDate().getTime()) : null);
            ps.setTimestamp(5, stage.getEndDate() != null ? new Timestamp(stage.getEndDate().getTime()) : null);
            ps.setInt(6, stage.getLocationID());
            ps.setString(7, stage.getDescription());
            ps.setTimestamp(8, new Timestamp(new Date().getTime())); // Current timestamp
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    newStageId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return newStageId;
    }
    
    // Update recruitment stage
    public boolean updateRecruitmentStage(RecruitmentStage stage) {
        try {
            conn = DBContext.getConnection();
            String sql = "UPDATE RecruitmentStages SET "
                    + "StageName = ?, Status = ?, StartDate = ?, EndDate = ?, "
                    + "LocationID = ?, Description = ? "
                    + "WHERE StageID = ? AND RecruitmentID = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, stage.getStageName());
            ps.setString(2, stage.getStatus());
            ps.setTimestamp(3, stage.getStartDate() != null ? new Timestamp(stage.getStartDate().getTime()) : null);
            ps.setTimestamp(4, stage.getEndDate() != null ? new Timestamp(stage.getEndDate().getTime()) : null);
            ps.setInt(5, stage.getLocationID());
            ps.setString(6, stage.getDescription());
            ps.setInt(7, stage.getStageID());
            ps.setInt(8, stage.getRecruitmentID());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }
    
    // Update recruitment stage status only
    public boolean updateStageStatus(int stageID, String status) {
        try {
            conn = DBContext.getConnection();
            String sql = "UPDATE RecruitmentStages SET Status = ? WHERE StageID = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, stageID);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }
    
    // Get stage by ID
    public RecruitmentStage getStageById(int stageID) {
        RecruitmentStage stage = null;
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT rs.*, l.LocationName, rc.Title as RecruitmentTitle "
                    + "FROM RecruitmentStages rs "
                    + "LEFT JOIN Locations l ON rs.LocationID = l.LocationID "
                    + "JOIN RecruitmentCampaigns rc ON rs.RecruitmentID = rc.RecruitmentID "
                    + "WHERE rs.StageID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, stageID);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                stage = new RecruitmentStage(
                        rs.getInt("StageID"),
                        rs.getInt("RecruitmentID"),
                        rs.getString("StageName"),
                        rs.getString("Description"),
                        rs.getTimestamp("StartDate"),
                        rs.getTimestamp("EndDate"),
                        rs.getString("Status"),
                        rs.getInt("LocationID"),
                        null, // createdBy not stored in this table
                        rs.getTimestamp("CreatedAt"),
                        rs.getString("LocationName"),
                        null, // locationAddress not stored directly
                        rs.getString("RecruitmentTitle"),
                        null // createdByName not available
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return stage;
    }
    
    // Get all stages for a recruitment campaign
    public List<RecruitmentStage> getStagesByRecruitmentId(int recruitmentID) {
        List<RecruitmentStage> stages = new ArrayList<>();
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT rs.*, l.LocationName, rc.Title as RecruitmentTitle "
                    + "FROM RecruitmentStages rs "
                    + "LEFT JOIN Locations l ON rs.LocationID = l.LocationID "
                    + "JOIN RecruitmentCampaigns rc ON rs.RecruitmentID = rc.RecruitmentID "
                    + "WHERE rs.RecruitmentID = ? "
                    + "ORDER BY rs.StartDate";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, recruitmentID);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                RecruitmentStage stage = new RecruitmentStage(
                        rs.getInt("StageID"),
                        rs.getInt("RecruitmentID"),
                        rs.getString("StageName"),
                        rs.getString("Description"),
                        rs.getTimestamp("StartDate"),
                        rs.getTimestamp("EndDate"),
                        rs.getString("Status"),
                        rs.getInt("LocationID"),
                        null, // createdBy not stored in this table
                        rs.getTimestamp("CreatedAt"),
                        rs.getString("LocationName"),
                        null, // locationAddress not stored directly
                        rs.getString("RecruitmentTitle"),
                        null // createdByName not available
                );
                stages.add(stage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return stages;
    }
    
    // Check for stage time overlap within the same recruitment campaign
    public boolean hasStageTimeOverlap(int recruitmentID, Date startDate, Date endDate, Integer excludeStageId) {
        try {
            conn = DBContext.getConnection();
            
            // SQL to check for time overlaps excluding the current stage if updating
            String sql = "SELECT COUNT(*) as count FROM RecruitmentStages "
                    + "WHERE RecruitmentID = ? AND "
                    + "((? BETWEEN StartDate AND EndDate) OR (? BETWEEN StartDate AND EndDate) OR "
                    + "(StartDate BETWEEN ? AND ?) OR (EndDate BETWEEN ? AND ?))";
            
            // If we're updating an existing stage, exclude it from the check
            if (excludeStageId != null) {
                sql += " AND StageID != ?";
            }
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, recruitmentID);
            ps.setTimestamp(2, new Timestamp(startDate.getTime()));
            ps.setTimestamp(3, new Timestamp(endDate.getTime()));
            ps.setTimestamp(4, new Timestamp(startDate.getTime()));
            ps.setTimestamp(5, new Timestamp(endDate.getTime()));
            ps.setTimestamp(6, new Timestamp(startDate.getTime()));
            ps.setTimestamp(7, new Timestamp(endDate.getTime()));
            
            if (excludeStageId != null) {
                ps.setInt(8, excludeStageId);
            }
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return false;
    }
    
    // Delete a recruitment stage (usually not recommended)
    public boolean deleteRecruitmentStage(int stageID) {
        try {
            conn = DBContext.getConnection();
            String sql = "DELETE FROM RecruitmentStages WHERE StageID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, stageID);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
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
