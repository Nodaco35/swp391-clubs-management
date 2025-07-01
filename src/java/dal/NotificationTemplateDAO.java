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
import models.NotificationTemplate;

public class NotificationTemplateDAO {
    
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    // Create new notification template
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
    
    // Update notification template
    public boolean updateNotificationTemplate(NotificationTemplate template) {
        try {
            conn = DBContext.getConnection();
            String sql = "UPDATE NotificationTemplates SET "
                    + "TemplateName = ?, Title = ?, Content = ?, IsReusable = ? "
                    + "WHERE TemplateID = ? AND ClubID = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, template.getTemplateName());
            ps.setString(2, template.getTitle());
            ps.setString(3, template.getContent());
            ps.setBoolean(4, template.isReusable());
            ps.setInt(5, template.getTemplateID());
            ps.setInt(6, template.getClubID());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }
    
    // Get template by ID
    public NotificationTemplate getTemplateById(int templateID) {
        NotificationTemplate template = null;
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT nt.*, c.ClubName, u.FullName as CreatedByName "
                    + "FROM NotificationTemplates nt "
                    + "JOIN Clubs c ON nt.ClubID = c.ClubID "
                    + "LEFT JOIN Users u ON nt.CreatedBy = u.UserID "
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
    
    // Get all templates by club ID
    public List<NotificationTemplate> getTemplatesByClubId(int clubID) {
        List<NotificationTemplate> templates = new ArrayList<>();
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT nt.*, c.ClubName, u.FullName as CreatedByName "
                    + "FROM NotificationTemplates nt "
                    + "JOIN Clubs c ON nt.ClubID = c.ClubID "
                    + "LEFT JOIN Users u ON nt.CreatedBy = u.UserID "
                    + "WHERE nt.ClubID = ? "
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
    
    // Get all reusable templates by club ID
    public List<NotificationTemplate> getReusableTemplatesByClubId(int clubID) {
        List<NotificationTemplate> templates = new ArrayList<>();
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT nt.*, c.ClubName, u.FullName as CreatedByName "
                    + "FROM NotificationTemplates nt "
                    + "JOIN Clubs c ON nt.ClubID = c.ClubID "
                    + "LEFT JOIN Users u ON nt.CreatedBy = u.UserID "
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
    
    // Delete a notification template (usually not recommended)
    public boolean deleteNotificationTemplate(int templateID) {
        try {
            conn = DBContext.getConnection();
            String sql = "DELETE FROM NotificationTemplates WHERE TemplateID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, templateID);
            
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
