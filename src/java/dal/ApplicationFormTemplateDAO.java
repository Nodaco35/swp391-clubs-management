package dal;

import models.ApplicationFormTemplate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationFormTemplateDAO {
    private Connection connection;
    // Thêm mẫu form mới
    public void saveFormTemplate(ApplicationFormTemplate template) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "INSERT INTO ApplicationFormTemplates (ClubID, EventID, FormType, Title, FieldName, FieldType, IsRequired, Options, Published) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, template.getClubId());
            if (template.getEventId() != null) {
                stmt.setInt(2, template.getEventId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, template.getFormType());
            stmt.setString(4, template.getTitle());
            stmt.setString(5, template.getFieldName());
            stmt.setString(6, template.getFieldType());
            stmt.setBoolean(7, template.isRequired());
            stmt.setString(8, template.getOptions());
            stmt.setBoolean(9, template.isPublished());
            stmt.executeUpdate();
        }
    }

    // Cập nhật mẫu form
    public void updateTemplate(ApplicationFormTemplate template) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "UPDATE ApplicationFormTemplates SET ClubID = ?, EventID = ?, FormType = ?, Title = ?, FieldName = ?, " +
                "FieldType = ?, IsRequired = ?, Options = ?, Published = ? WHERE TemplateID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, template.getClubId());
            if (template.getEventId() != null) {
                stmt.setInt(2, template.getEventId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, template.getFormType());
            stmt.setString(4, template.getTitle());
            stmt.setString(5, template.getFieldName());
            stmt.setString(6, template.getFieldType());
            stmt.setBoolean(7, template.isRequired());
            stmt.setString(8, template.getOptions());
            stmt.setBoolean(9, template.isPublished());
            stmt.setInt(10, template.getTemplateId());
            stmt.executeUpdate();
        }
    }

    // Lấy mẫu form theo ID
    public ApplicationFormTemplate getTemplateById(int templateId) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "SELECT * FROM ApplicationFormTemplates WHERE TemplateID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, templateId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ApplicationFormTemplate(
                            rs.getInt("TemplateID"),
                            rs.getInt("ClubID"),
                            rs.getObject("EventID") != null ? rs.getInt("EventID") : null,
                            rs.getString("FormType"),
                            rs.getString("Title"),
                            rs.getString("FieldName"),
                            rs.getString("FieldType"),
                            rs.getBoolean("IsRequired"),
                            rs.getString("Options"),
                            rs.getBoolean("Published")
                    );
                }
            }
        }
        return null;
    }

    // Lấy tất cả mẫu form của một CLB
    public List<ApplicationFormTemplate> getTemplatesByClub(int clubId) throws SQLException {
        connection = DBContext.getConnection();
        List<ApplicationFormTemplate> templates = new ArrayList<>();
        String sql = "SELECT * FROM ApplicationFormTemplates WHERE ClubID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    templates.add(new ApplicationFormTemplate(
                            rs.getInt("TemplateID"),
                            rs.getInt("ClubID"),
                            rs.getObject("EventID") != null ? rs.getInt("EventID") : null,
                            rs.getString("FormType"),
                            rs.getString("Title"),
                            rs.getString("FieldName"),
                            rs.getString("FieldType"),
                            rs.getBoolean("IsRequired"),
                            rs.getString("Options"),
                            rs.getBoolean("Published")
                    ));
                }
            }
        }
        return templates;
    }
}