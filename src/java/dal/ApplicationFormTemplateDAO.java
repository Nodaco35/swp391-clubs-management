package dal;

import models.ApplicationFormTemplate;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        } catch (SQLException e) {
            throw e;
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
    // Lấy forms theo club và trạng thái published
    public List<ApplicationFormTemplate> getTemplatesByClubAndStatus(int clubId, boolean published) throws SQLException {
        connection = DBContext.getConnection();
        List<ApplicationFormTemplate> templates = new ArrayList<>();
        String sql = "SELECT DISTINCT TemplateID, ClubID, EventID, FormType, Title, FieldName, FieldType, IsRequired, Options, Published " +
                "FROM ApplicationFormTemplates WHERE ClubID = ? AND Published = ? " +
                "GROUP BY TemplateID, ClubID, EventID, FormType, Title, FieldName, FieldType, IsRequired, Options, Published " +
                "ORDER BY TemplateID DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            stmt.setBoolean(2, published);
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
    public boolean publishFormsByTitle(String title) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "UPDATE ApplicationFormTemplates SET Published = 1 WHERE Title = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Hủy xuất bản form (đổi Published từ 1 thành 0)
    public boolean unpublishFormsByTitle(String title) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "UPDATE ApplicationFormTemplates SET Published = 0 WHERE Title = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Xóa template theo ID
    public boolean deleteTemplate(int templateId) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "DELETE FROM ApplicationFormTemplates WHERE TemplateID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, templateId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    public boolean deleteFormsByTitle(String title) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "DELETE FROM ApplicationFormTemplates WHERE Title = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    //Lấy form templates grouped by form
    public List<Map<String, Object>> getFormsByClubAndStatus(int clubId, boolean published) throws SQLException {
        connection = DBContext.getConnection();
        List<Map<String, Object>> forms = new ArrayList<>();
        String sql = "SELECT DISTINCT Title, FormType, ClubID, EventID, Published, MAX(TemplateID) as TemplateID " +
                "FROM ApplicationFormTemplates " +
                "WHERE ClubID = ? AND Published = ? " +
                "GROUP BY Title, FormType, ClubID, EventID, Published " +
                "ORDER BY MAX(TemplateID) DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            stmt.setBoolean(2, published);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> form = new HashMap<>();
                    form.put("templateId", rs.getInt("TemplateID"));
                    form.put("title", rs.getString("Title"));
                    form.put("formType", rs.getString("FormType"));
                    form.put("clubId", rs.getInt("ClubID"));
                    form.put("eventId", rs.getObject("EventID"));
                    form.put("published", rs.getBoolean("Published"));
                    forms.add(form);
                }
            }
        }
        return forms;
    }
    public List<ApplicationFormTemplate> getTemplatesByTitle(String title) throws SQLException {
        List<ApplicationFormTemplate> templates = new ArrayList<>();
        Connection connection = DBContext.getConnection(); // Ensure connection is established
        String sql = "SELECT * FROM ApplicationFormTemplates WHERE Title = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ApplicationFormTemplate template = new ApplicationFormTemplate();
                    template.setTemplateId(rs.getInt("TemplateID"));
                    template.setClubId(rs.getInt("ClubID"));
                    template.setEventId(rs.getObject("EventID") != null ? rs.getInt("EventID") : null);
                    template.setFormType(rs.getString("FormType"));
                    template.setTitle(rs.getString("Title"));
                    template.setFieldName(rs.getString("FieldName"));
                    template.setFieldType(rs.getString("FieldType"));
                    template.setIsRequired(rs.getBoolean("IsRequired"));
                    template.setOptions(rs.getString("Options"));
                    template.setPublished(rs.getBoolean("Published"));
                    templates.add(template);
                }
            }
        }
        return templates;
    }


    // Thêm mẫu form mới và trả về ID được tạo
    public int saveFormTemplateAndGetId(ApplicationFormTemplate template) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "INSERT INTO ApplicationFormTemplates (ClubID, EventID, FormType, Title, FieldName, FieldType, IsRequired, Options, Published) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating template failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    return newId;
                } else {
                    throw new SQLException("Creating template failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw e;
        }
    }
    private ApplicationFormTemplate mapRowToTemplate(ResultSet rs) throws SQLException {
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
    public List<ApplicationFormTemplate> getTemplatesByGroup(String title, int clubId, int maxTemplateId) {
        List<ApplicationFormTemplate> templates = new ArrayList<>();
        String sql = "SELECT * FROM ApplicationFormTemplates WHERE Title = ? AND ClubID = ? AND TemplateID <= ? ORDER BY TemplateID ASC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setInt(2, clubId);
            ps.setInt(3, maxTemplateId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ApplicationFormTemplate template = new ApplicationFormTemplate();
                    template.setTemplateId(rs.getInt("TemplateID"));
                    template.setClubId(rs.getInt("ClubID"));
                    template.setEventId(rs.getInt("EventID"));
                    template.setFormType(rs.getString("FormType"));
                    template.setTitle(rs.getString("Title"));
                    template.setFieldName(rs.getString("FieldName"));
                    template.setFieldType(rs.getString("FieldType"));
                    template.setIsRequired(rs.getBoolean("IsRequired"));
                    template.setOptions(rs.getString("Options"));
                    template.setPublished(rs.getBoolean("Published"));
                    templates.add(template);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return templates;
    }

    public List<Integer> getTemplateIdsByGroup(String title, int clubId, int maxTemplateId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT TemplateID FROM ApplicationFormTemplates WHERE Title = ? AND ClubID = ? AND TemplateID <= ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setInt(2, clubId);
            ps.setInt(3, maxTemplateId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("TemplateID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
    /**
     * Trả về TemplateID lớn nhất cho một form (dựa vào title + clubId)
     */
    public int getMaxTemplateIdForForm(String title, int clubId) {
        String sql = "SELECT MAX(TemplateID) AS maxId FROM ApplicationFormTemplates "
                + "WHERE Title = ? AND ClubID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setInt(2, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("maxId");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}