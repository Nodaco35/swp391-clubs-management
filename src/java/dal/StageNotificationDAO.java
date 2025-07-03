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
import models.StageNotification;
import models.NotificationTemplate;

public class StageNotificationDAO {
    
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    // Create new stage notification
    public int createStageNotification(StageNotification notification) {
        int newNotificationId = 0;
        try {
            conn = DBContext.getConnection();
            String sql = "INSERT INTO StageNotifications "
                    + "(StageID, Title, Content, CreatedAt, CreatedBy) "
                    + "VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, notification.getStageID());
            ps.setString(2, notification.getTitle());
            ps.setString(3, notification.getContent());
            ps.setTimestamp(4, new Timestamp(new Date().getTime())); // Current timestamp
            ps.setString(5, notification.getCreatedBy());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    newNotificationId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return newNotificationId;
    }
    
    // Update stage notification
    public boolean updateStageNotification(StageNotification notification) {
        try {
            conn = DBContext.getConnection();
            String sql = "UPDATE StageNotifications SET "
                    + "Title = ?, Content = ? "
                    + "WHERE StageNotificationID = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, notification.getTitle());
            ps.setString(2, notification.getContent());
            ps.setInt(3, notification.getNotificationID());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }
    
    // Get notification by ID
    public StageNotification getNotificationById(int notificationID) {
        StageNotification notification = null;
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT sn.*, rs.StageName, u.FullName as CreatedByName "
                    + "FROM StageNotifications sn "
                    + "JOIN RecruitmentStages rs ON sn.StageID = rs.StageID "
                    + "JOIN Users u ON sn.CreatedBy = u.UserID "
                    + "WHERE sn.StageNotificationID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, notificationID);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                notification = new StageNotification(
                        rs.getInt("StageNotificationID"),
                        rs.getInt("StageID"),
                        0, // templateID not directly stored in this table
                        rs.getString("Title"),
                        rs.getString("Content"),
                        false, // isSent not directly stored in this table
                        null, // scheduledDate not directly stored in this table
                        null, // sentDate not directly stored in this table
                        rs.getString("CreatedBy"),
                        rs.getTimestamp("CreatedAt"),
                        rs.getString("StageName"),
                        null, // templateName not available
                        rs.getString("CreatedByName")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return notification;
    }
    
    // Get all notifications for a stage
    public List<StageNotification> getNotificationsByStageId(int stageID) {
        List<StageNotification> notifications = new ArrayList<>();
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT sn.*, rs.StageName, u.FullName as CreatedByName "
                    + "FROM StageNotifications sn "
                    + "JOIN RecruitmentStages rs ON sn.StageID = rs.StageID "
                    + "JOIN Users u ON sn.CreatedBy = u.UserID "
                    + "WHERE sn.StageID = ? "
                    + "ORDER BY sn.CreatedAt DESC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, stageID);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                StageNotification notification = new StageNotification(
                        rs.getInt("StageNotificationID"),
                        rs.getInt("StageID"),
                        0, // templateID not directly stored in this table
                        rs.getString("Title"),
                        rs.getString("Content"),
                        false, // isSent not directly stored in this table
                        null, // scheduledDate not directly stored in this table
                        null, // sentDate not directly stored in this table
                        rs.getString("CreatedBy"),
                        rs.getTimestamp("CreatedAt"),
                        rs.getString("StageName"),
                        null, // templateName not available
                        rs.getString("CreatedByName")
                );
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return notifications;
    }
    
    // Create notification template (reusable)
    public int createNotificationTemplate(NotificationTemplate template) {
        int newTemplateId = 0;
        try {
            conn = DBContext.getConnection();
            String sql = "INSERT INTO NotificationTemplates "
                    + "(ClubID, TemplateName, Title, Content, CreatedBy, CreatedAt, IsReusable) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, template.getClubID());
            ps.setString(2, template.getTemplateName());
            ps.setString(3, template.getTitle());
            ps.setString(4, template.getContent());
            ps.setString(5, template.getCreatedBy());
            ps.setTimestamp(6, new Timestamp(new Date().getTime())); // Current timestamp
            ps.setBoolean(7, template.isReusable());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    newTemplateId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return newTemplateId;
    }
    
    // Get all notification templates for a club
    public List<NotificationTemplate> getNotificationTemplatesByClub(int clubID) {
        List<NotificationTemplate> templates = new ArrayList<>();
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT nt.*, c.ClubName, u.FullName as CreatedByName "
                    + "FROM NotificationTemplates nt "
                    + "JOIN Clubs c ON nt.ClubID = c.ClubID "
                    + "JOIN Users u ON nt.CreatedBy = u.UserID "
                    + "WHERE nt.ClubID = ? AND nt.IsReusable = true "
                    + "ORDER BY nt.CreatedAt DESC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, clubID);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                NotificationTemplate template = new NotificationTemplate(
                        rs.getInt("TemplateID"),
                        rs.getInt("ClubID"),
                        rs.getString("TemplateName"),
                        rs.getString("Title"),
                        rs.getString("Content"),
                        rs.getString("CreatedBy"),
                        rs.getTimestamp("CreatedAt"),
                        rs.getBoolean("IsReusable"),
                        rs.getString("ClubName"),
                        rs.getString("CreatedByName")
                );
                templates.add(template);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return templates;
    }
    
    // Get a notification template by ID
    public NotificationTemplate getNotificationTemplateById(int templateID) {
        NotificationTemplate template = null;
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT nt.*, c.ClubName, u.FullName as CreatedByName "
                    + "FROM NotificationTemplates nt "
                    + "JOIN Clubs c ON nt.ClubID = c.ClubID "
                    + "JOIN Users u ON nt.CreatedBy = u.UserID "
                    + "WHERE nt.TemplateID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, templateID);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                template = new NotificationTemplate(
                        rs.getInt("TemplateID"),
                        rs.getInt("ClubID"),
                        rs.getString("TemplateName"),
                        rs.getString("Title"),
                        rs.getString("Content"),
                        rs.getString("CreatedBy"),
                        rs.getTimestamp("CreatedAt"),
                        rs.getBoolean("IsReusable"),
                        rs.getString("ClubName"),
                        rs.getString("CreatedByName")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return template;
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
