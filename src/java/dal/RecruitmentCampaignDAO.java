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
import models.RecruitmentCampaign;

public class RecruitmentCampaignDAO {
    
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    /**
     * Create new recruitment campaign
     * @param campaign The campaign to create
     * @return The ID of the created campaign if successful, 
     *        -1 if time overlap (handled by service layer),
     *        -2 if start date is in the past
     */
    public int createRecruitmentCampaign(RecruitmentCampaign campaign) {
        int newRecruitmentId = 0;
        try {
            conn = DBContext.getConnection();
            
            // First check if start date is in the past
            String checkDateSql = "SELECT CASE WHEN ? < CURRENT_DATE THEN 1 ELSE 0 END AS is_past_date";
            ps = conn.prepareStatement(checkDateSql);
            ps.setTimestamp(1, campaign.getStartDate() != null ? new Timestamp(campaign.getStartDate().getTime()) : null);
            rs = ps.executeQuery();
            
            if (rs.next() && rs.getInt("is_past_date") == 1) {
                // Start date is in the past, return -2 to indicate this error
                return -2;
            }
            
            // Use CASE statement in SQL to determine status based on start date
            String sql = "INSERT INTO RecruitmentCampaigns "
                    + "(ClubID, Gen, TemplateID, Title, Description, StartDate, EndDate, Status, CreatedBy, CreatedAt) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, "
                    + "CASE "
                    + "  WHEN ? = CURRENT_DATE THEN 'ONGOING' "
                    + "  WHEN ? > CURRENT_DATE THEN 'UPCOMING' "
                    + "END, "
                    + "?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, campaign.getClubID());
            ps.setInt(2, campaign.getGen());
            ps.setInt(3, campaign.getTemplateID());
            ps.setString(4, campaign.getTitle());
            ps.setString(5, campaign.getDescription());
            ps.setTimestamp(6, campaign.getStartDate() != null ? new Timestamp(campaign.getStartDate().getTime()) : null);
            ps.setTimestamp(7, campaign.getEndDate() != null ? new Timestamp(campaign.getEndDate().getTime()) : null);
            // For the CASE condition, we need the date part only
            ps.setTimestamp(8, campaign.getStartDate() != null ? new Timestamp(campaign.getStartDate().getTime()) : null);
            ps.setTimestamp(9, campaign.getStartDate() != null ? new Timestamp(campaign.getStartDate().getTime()) : null);
            ps.setString(10, campaign.getCreatedBy());
            ps.setTimestamp(11, new Timestamp(new Date().getTime())); // Current timestamp
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    newRecruitmentId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return newRecruitmentId;
    }
    
    // Update recruitment campaign
    public boolean updateRecruitmentCampaign(RecruitmentCampaign campaign) {
        try {
            conn = DBContext.getConnection();
            
            // First check if start date is in the past (for new campaigns only)
            String checkSql = "SELECT Status FROM RecruitmentCampaigns WHERE RecruitmentID = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setInt(1, campaign.getRecruitmentID());
            rs = ps.executeQuery();
            
            boolean isNewCampaign = false;
            if (rs.next()) {
                String currentStatus = rs.getString("Status");
                isNewCampaign = "UPCOMING".equals(currentStatus);
                
                // If it's a new campaign and the start date is in the past, don't allow the update
                if (isNewCampaign) {
                    String checkDateSql = "SELECT CASE WHEN ? < CURRENT_DATE THEN 1 ELSE 0 END AS is_past_date";
                    ps = conn.prepareStatement(checkDateSql);
                    ps.setTimestamp(1, campaign.getStartDate() != null ? new Timestamp(campaign.getStartDate().getTime()) : null);
                    rs = ps.executeQuery();
                    
                    if (rs.next() && rs.getInt("is_past_date") == 1) {
                        // Start date is in the past for a new campaign
                        return false;
                    }
                }
            }
            
            // Use CASE statement to update Status based on the start date for UPCOMING campaigns
            String sql;
            if (isNewCampaign) {
                sql = "UPDATE RecruitmentCampaigns SET "
                      + "Gen = ?, TemplateID = ?, Title = ?, Description = ?, "
                      + "StartDate = ?, EndDate = ?, "
                      + "Status = CASE "
                      + "  WHEN ? = CURRENT_DATE THEN 'ONGOING' "
                      + "  WHEN ? > CURRENT_DATE THEN 'UPCOMING' "
                      + "  ELSE Status "  // Keep existing status for other cases
                      + "END "
                      + "WHERE RecruitmentID = ? AND ClubID = ?";
            } else {
                sql = "UPDATE RecruitmentCampaigns SET "
                      + "Gen = ?, TemplateID = ?, Title = ?, Description = ?, "
                      + "StartDate = ?, EndDate = ?, Status = ? "
                      + "WHERE RecruitmentID = ? AND ClubID = ?";
            }
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, campaign.getGen());
            ps.setInt(2, campaign.getTemplateID());
            ps.setString(3, campaign.getTitle());
            ps.setString(4, campaign.getDescription());
            ps.setTimestamp(5, campaign.getStartDate() != null ? new Timestamp(campaign.getStartDate().getTime()) : null);
            ps.setTimestamp(6, campaign.getEndDate() != null ? new Timestamp(campaign.getEndDate().getTime()) : null);
            
            if (isNewCampaign) {
                // For the CASE condition, we need the date part for comparison
                ps.setTimestamp(7, campaign.getStartDate() != null ? new Timestamp(campaign.getStartDate().getTime()) : null);
                ps.setTimestamp(8, campaign.getStartDate() != null ? new Timestamp(campaign.getStartDate().getTime()) : null);
                ps.setInt(9, campaign.getRecruitmentID());
                ps.setInt(10, campaign.getClubID());
            } else {
                ps.setString(7, campaign.getStatus());
                ps.setInt(8, campaign.getRecruitmentID());
                ps.setInt(9, campaign.getClubID());
            }
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }
    
    // Update recruitment campaign status only
    public boolean updateRecruitmentStatus(int recruitmentID, String status) {
        try {
            conn = DBContext.getConnection();
            String sql = "UPDATE RecruitmentCampaigns SET Status = ? WHERE RecruitmentID = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, recruitmentID);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }
    
    // Get recruitment campaign by ID
    public RecruitmentCampaign getRecruitmentCampaignById(int recruitmentID) {
        RecruitmentCampaign campaign = null;
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT rc.*, c.ClubName, ft.Title as TemplateName, u.FullName as CreatedByName "
                    + "FROM RecruitmentCampaigns rc "
                    + "JOIN Clubs c ON rc.ClubID = c.ClubID "
                    + "JOIN ApplicationFormTemplates ft ON rc.TemplateID = ft.TemplateID "
                    + "JOIN Users u ON rc.CreatedBy = u.UserID "
                    + "WHERE rc.RecruitmentID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, recruitmentID);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                campaign = new RecruitmentCampaign(
                        rs.getInt("RecruitmentID"),
                        rs.getInt("ClubID"),
                        rs.getInt("Gen"),
                        rs.getInt("TemplateID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getTimestamp("StartDate"),
                        rs.getTimestamp("EndDate"),
                        rs.getString("Status"),
                        rs.getString("CreatedBy"),
                        rs.getTimestamp("CreatedAt"),
                        rs.getString("ClubName"),
                        rs.getString("TemplateName"),
                        rs.getString("CreatedByName")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return campaign;
    }
    
    // Get all recruitment campaigns for a club
    public List<RecruitmentCampaign> getRecruitmentCampaignsByClub(int clubID) {
        List<RecruitmentCampaign> campaigns = new ArrayList<>();
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT rc.*, c.ClubName, ft.Title as TemplateName, u.FullName as CreatedByName "
                    + "FROM RecruitmentCampaigns rc "
                    + "JOIN Clubs c ON rc.ClubID = c.ClubID "
                    + "JOIN ApplicationFormTemplates ft ON rc.TemplateID = ft.TemplateID "
                    + "JOIN Users u ON rc.CreatedBy = u.UserID "
                    + "WHERE rc.ClubID = ? "
                    + "ORDER BY rc.CreatedAt DESC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, clubID);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                RecruitmentCampaign campaign = new RecruitmentCampaign(
                        rs.getInt("RecruitmentID"),
                        rs.getInt("ClubID"),
                        rs.getInt("Gen"),
                        rs.getInt("TemplateID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getTimestamp("StartDate"),
                        rs.getTimestamp("EndDate"),
                        rs.getString("Status"),
                        rs.getString("CreatedBy"),
                        rs.getTimestamp("CreatedAt"),
                        rs.getString("ClubName"),
                        rs.getString("TemplateName"),
                        rs.getString("CreatedByName")
                );
                campaigns.add(campaign);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return campaigns;
    }
    
    // Get all active recruitment campaigns (for student to apply)
    public List<RecruitmentCampaign> getActiveRecruitmentCampaigns() {
        List<RecruitmentCampaign> campaigns = new ArrayList<>();
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT rc.*, c.ClubName, ft.Title as TemplateName, u.FullName as CreatedByName "
                    + "FROM RecruitmentCampaigns rc "
                    + "JOIN Clubs c ON rc.ClubID = c.ClubID "
                    + "JOIN ApplicationFormTemplates ft ON rc.TemplateID = ft.TemplateID "
                    + "JOIN Users u ON rc.CreatedBy = u.UserID "
                    + "WHERE rc.Status = 'ONGOING' AND rc.EndDate >= CURRENT_TIMESTAMP "
                    + "ORDER BY rc.StartDate ASC";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                RecruitmentCampaign campaign = new RecruitmentCampaign(
                        rs.getInt("RecruitmentID"),
                        rs.getInt("ClubID"),
                        rs.getInt("Gen"),
                        rs.getInt("TemplateID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getTimestamp("StartDate"),
                        rs.getTimestamp("EndDate"),
                        rs.getString("Status"),
                        rs.getString("CreatedBy"),
                        rs.getTimestamp("CreatedAt"),
                        rs.getString("ClubName"),
                        rs.getString("TemplateName"),
                        rs.getString("CreatedByName")
                );
                campaigns.add(campaign);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return campaigns;
    }
    
    // Check for campaign time overlap within the same club
    public boolean hasCampaignTimeOverlap(int clubID, Date startDate, Date endDate, Integer excludeCampaignId) {
        try {
            conn = DBContext.getConnection();
            
            // SQL to check for time overlaps excluding the current campaign if updating
            String sql = "SELECT COUNT(*) as count FROM RecruitmentCampaigns "
                    + "WHERE ClubID = ? AND "
                    + "((? BETWEEN StartDate AND EndDate) OR (? BETWEEN StartDate AND EndDate) OR "
                    + "(StartDate BETWEEN ? AND ?) OR (EndDate BETWEEN ? AND ?))";
            
            // If we're updating an existing campaign, exclude it from the check
            if (excludeCampaignId != null) {
                sql += " AND RecruitmentID != ?";
            }
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, clubID);
            ps.setTimestamp(2, new Timestamp(startDate.getTime()));
            ps.setTimestamp(3, new Timestamp(endDate.getTime()));
            ps.setTimestamp(4, new Timestamp(startDate.getTime()));
            ps.setTimestamp(5, new Timestamp(endDate.getTime()));
            ps.setTimestamp(6, new Timestamp(startDate.getTime()));
            ps.setTimestamp(7, new Timestamp(endDate.getTime()));
            
            if (excludeCampaignId != null) {
                ps.setInt(8, excludeCampaignId);
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
    
    
    // Delete a recruitment campaign and all related data
    public boolean deleteRecruitmentCampaign(int recruitmentID) {
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);
            
            // Check if campaign is ongoing
            String checkSql = "SELECT Status FROM RecruitmentCampaigns WHERE RecruitmentID = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setInt(1, recruitmentID);
            rs = ps.executeQuery();
            
            if (rs.next() && "ONGOING".equals(rs.getString("Status"))) {
                // Can't delete an ongoing campaign
                conn.rollback();
                return false;
            }
            
            // Delete related stage notifications
            String deleteNotificationsSql = "DELETE FROM StageNotifications "
                    + "WHERE StageID IN (SELECT StageID FROM RecruitmentStages WHERE RecruitmentID = ?)";
            ps = conn.prepareStatement(deleteNotificationsSql);
            ps.setInt(1, recruitmentID);
            ps.executeUpdate();
            
            // Delete related application stages
            String deleteApplicationStagesSql = "DELETE FROM ApplicationStages "
                    + "WHERE StageID IN (SELECT StageID FROM RecruitmentStages WHERE RecruitmentID = ?)";
            ps = conn.prepareStatement(deleteApplicationStagesSql);
            ps.setInt(1, recruitmentID);
            ps.executeUpdate();
            
            // Delete related stages
            String deleteStagesSql = "DELETE FROM RecruitmentStages WHERE RecruitmentID = ?";
            ps = conn.prepareStatement(deleteStagesSql);
            ps.setInt(1, recruitmentID);
            ps.executeUpdate();
            
            // Finally delete the campaign
            String deleteCampaignSql = "DELETE FROM RecruitmentCampaigns WHERE RecruitmentID = ?";
            ps = conn.prepareStatement(deleteCampaignSql);
            ps.setInt(1, recruitmentID);
            int affectedRows = ps.executeUpdate();
            
            conn.commit();
            return affectedRows > 0;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            closeResources();
        }
    }
    
    // Get all recruitment campaigns
    public List<RecruitmentCampaign> getAllRecruitmentCampaigns() {
        List<RecruitmentCampaign> campaignList = new ArrayList<>();
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT rc.*, u.FullName AS CreatorName "
                    + "FROM RecruitmentCampaigns rc "
                    + "LEFT JOIN Users u ON rc.CreatedBy = u.UserID "
                    + "ORDER BY rc.CreatedAt DESC";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                RecruitmentCampaign campaign = new RecruitmentCampaign();
                campaign.setRecruitmentID(rs.getInt("RecruitmentID"));
                campaign.setClubID(rs.getInt("ClubID"));
                campaign.setGen(rs.getInt("Gen"));
                campaign.setTemplateID(rs.getInt("TemplateID"));
                campaign.setTitle(rs.getString("Title"));
                campaign.setDescription(rs.getString("Description"));
                campaign.setStartDate(rs.getTimestamp("StartDate"));
                campaign.setEndDate(rs.getTimestamp("EndDate"));
                campaign.setStatus(rs.getString("Status"));
                campaign.setCreatedBy(rs.getString("CreatedBy"));
                campaign.setCreatedAt(rs.getTimestamp("CreatedAt"));
                
                campaignList.add(campaign);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return campaignList;
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
