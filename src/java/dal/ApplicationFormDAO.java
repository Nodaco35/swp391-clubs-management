package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.ApplicationForm;
import models.ApplicationFormTemplate;

/**
 * DAO class for handling ApplicationForm and ApplicationFormTemplate database operations
 */
public class ApplicationFormDAO {
    private Connection connection;
    
    public int createForm(ApplicationForm form) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "INSERT INTO ApplicationForms (ClubID, EventID, FormType, Title, Published) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, form.getClubId());
            
            // Carefully handle EventID which might be null
            if (form.getEventId() != null) {
                stmt.setInt(2, form.getEventId());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            
            stmt.setString(3, form.getFormType());
            stmt.setString(4, form.getTitle());
            stmt.setBoolean(5, form.isPublished());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Tạo form thất bại, không có dòng nào được thêm vào.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Tạo form thất bại, không lấy được ID.");
                }
            }
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Lỗi đóng kết nối: " + e.getMessage());
            }
        }
    }
    public boolean updateForm(ApplicationForm form) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "UPDATE ApplicationForms SET ClubID = ?, EventID = ?, FormType = ?, Title = ?, Published = ? WHERE FormID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, form.getClubId());
            
            // Carefully handle EventID which might be null
            if (form.getEventId() != null) {
                stmt.setInt(2, form.getEventId());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            
            stmt.setString(3, form.getFormType());
            stmt.setString(4, form.getTitle());
            stmt.setBoolean(5, form.isPublished());
            stmt.setInt(6, form.getFormId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Lỗi đóng kết nối: " + e.getMessage());
            }
        }
    }
    
    public ApplicationForm getFormById(int formId) {
        connection = DBContext.getConnection();
        ApplicationForm form = null;
        String sql = "SELECT * FROM ApplicationForms WHERE FormID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, formId); 
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("DEBUG: Form found in database, extracting data");
                    form = new ApplicationForm(
                        rs.getInt("FormID"),
                        rs.getInt("ClubID"),
                        rs.getInt("EventID"),
                        rs.getString("FormType"),
                        rs.getString("Title"),
                        rs.getBoolean("Published")
                    );
                    
                    // Check if EventID was NULL in database
                    if (rs.wasNull()) {
                        form.setEventId(null);
                    }
                } else {
                    System.out.println("DEBUG: No form found with ID " + formId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Lỗi đóng kết nối: " + e.getMessage());
            }
        }
        return form;
    }
    public ApplicationForm findFormByTitleAndClubId(String title, int clubId) {
        connection = DBContext.getConnection();
        ApplicationForm form = null;
        String sql = "SELECT * FROM ApplicationForms WHERE Title = ? AND ClubID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setInt(2, clubId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    form = new ApplicationForm(
                        rs.getInt("FormID"),
                        rs.getInt("ClubID"),
                        rs.getInt("EventID"),
                        rs.getString("FormType"),
                        rs.getString("Title"),
                        rs.getBoolean("Published")
                    );
                    
                    // Check if EventID was NULL in database
                    if (rs.wasNull()) {
                        form.setEventId(null);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm form theo title và clubId: " + e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Lỗi đóng kết nối: " + e.getMessage());
            }
        }
        return form;
    }
    
    /**
     * Get all forms for a specific club
     * 
     * @param clubId Club ID
     * @return List of ApplicationForm objects
     */
    public List<ApplicationForm> getFormsByClubId(int clubId) {
        connection = DBContext.getConnection();
        List<ApplicationForm> forms = new ArrayList<>();
        String sql = "SELECT * FROM ApplicationForms WHERE ClubID = ? ORDER BY FormID DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ApplicationForm form = new ApplicationForm(
                        rs.getInt("FormID"),
                        rs.getInt("ClubID"),
                        rs.getInt("EventID"),
                        rs.getString("FormType"),
                        rs.getString("Title"),
                        rs.getBoolean("Published")
                    );
                    
                    // Check if EventID was NULL in database
                    if (rs.wasNull()) {
                        form.setEventId(null);
                    }
                    
                    forms.add(form);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách form theo clubId: " + e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Lỗi đóng kết nối: " + e.getMessage());
            }
        }
        return forms;
    }

    public List<ApplicationForm> getPublishedFormsByClubId(int clubId) {
        connection = DBContext.getConnection();
        List<ApplicationForm> forms = new ArrayList<>();
        String sql = "SELECT * FROM ApplicationForms WHERE ClubID = ? AND Published = TRUE ORDER BY FormID DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ApplicationForm form = new ApplicationForm(
                        rs.getInt("FormID"),
                        rs.getInt("ClubID"),
                        rs.getInt("EventID"),
                        rs.getString("FormType"),
                        rs.getString("Title"),
                        rs.getBoolean("Published")
                    );
                    
                    // Check if EventID was NULL in database
                    if (rs.wasNull()) {
                        form.setEventId(null);
                    }
                    
                    forms.add(form);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách form đã published theo clubId: " + e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Lỗi đóng kết nối: " + e.getMessage());
            }
        }
        return forms;
    }
    
    /**
     * Publish a form (set Published = TRUE)
     * 
     * @param formId Form ID
     * @return True if update was successful
     */
    public boolean publishFormById(int formId) {
        connection = DBContext.getConnection();
        String sql = "UPDATE ApplicationForms SET Published = TRUE WHERE FormID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, formId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi publish form: " + e.getMessage());
            return false;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean unpublishFormById(int formId) {
        connection = DBContext.getConnection();
        String sql = "UPDATE ApplicationForms SET Published = FALSE WHERE FormID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, formId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi unpublish form: " + e.getMessage());
            return false;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean deleteFormById(int formId) throws SQLException {
    connection = DBContext.getConnection();
    // Xóa các template liên quan trước để tránh vi phạm khóa ngoại
    String deleteTemplatesSql = "DELETE FROM ApplicationFormTemplates WHERE FormID = ?";
    try (PreparedStatement stmt = connection.prepareStatement(deleteTemplatesSql)) {
        stmt.setInt(1, formId);
        stmt.executeUpdate();
    }
    // Sau đó xóa form
    String deleteFormSql = "DELETE FROM ApplicationForms WHERE FormID = ?";
    try (PreparedStatement stmt = connection.prepareStatement(deleteFormSql)) {
        stmt.setInt(1, formId);
        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0;
    }
}
    
}
