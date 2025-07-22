package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.ApplicationFormTemplate;

public class ApplicationFormTemplateDAO {

    private Connection connection;
    // Thêm mẫu form mới
    public void saveFormTemplate(ApplicationFormTemplate template) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "INSERT INTO ApplicationFormTemplates (FormID, FieldName, FieldType, IsRequired, Options, DisplayOrder) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, template.getFormId());
            stmt.setString(2, template.getFieldName());
            stmt.setString(3, template.getFieldType());
            stmt.setBoolean(4, template.isRequired());
            stmt.setString(5, template.getOptions());
            stmt.setInt(6, template.getDisplayOrder());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
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
                            rs.getInt("FormID"),
                            rs.getString("FieldName"),
                            rs.getString("FieldType"),
                            rs.getBoolean("IsRequired"),
                            rs.getString("Options"),
                            rs.getInt("DisplayOrder")
                    );
                }
            }
        }
        return null;
    }    // Lấy tất cả mẫu form của một CLB

    public List<ApplicationFormTemplate> getTemplatesByClub(int clubId) throws SQLException {
        connection = DBContext.getConnection();
        List<ApplicationFormTemplate> templates = new ArrayList<>();
        String sql = "SELECT aft.* FROM ApplicationFormTemplates aft " +
                     "JOIN ApplicationForms af ON aft.FormID = af.FormID " +
                     "WHERE af.ClubID = ? ORDER BY af.Title, aft.DisplayOrder";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    templates.add(mapRowToTemplate(rs));
                }
            }
        }
        return templates;
    }    // Lấy forms theo club và trạng thái published

    public List<ApplicationFormTemplate> getTemplatesByClubAndStatus(int clubId, boolean published) throws SQLException {
        connection = DBContext.getConnection();
        List<ApplicationFormTemplate> templates = new ArrayList<>();
        String sql = "SELECT DISTINCT TemplateID, ClubID, EventID, FormType, Title, FieldName, FieldType, IsRequired, Options, Published, DisplayOrder "
                + "FROM ApplicationFormTemplates WHERE ClubID = ? AND Published = ? "
                + "GROUP BY TemplateID, ClubID, EventID, FormType, Title, FieldName, FieldType, IsRequired, Options, Published, DisplayOrder "
                + "ORDER BY Title, DisplayOrder";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            stmt.setBoolean(2, published);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    templates.add(mapRowToTemplate(rs));
                }
            }
        }
        return templates;
    }
    
        // Cập nhật mẫu form
    public void updateTemplate(ApplicationFormTemplate template) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "UPDATE ApplicationFormTemplates SET FieldName = ?, "
                + "FieldType = ?, IsRequired = ?, Options = ?, DisplayOrder = ? WHERE TemplateID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, template.getFieldName());
            stmt.setString(2, template.getFieldType());
            stmt.setBoolean(3, template.isRequired());
            stmt.setString(4, template.getOptions());
            stmt.setInt(5, template.getDisplayOrder());
            stmt.setInt(6, template.getTemplateId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
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
        
        // First, get all form IDs with the given title
        String selectSql = "SELECT FormID FROM ApplicationForms WHERE Title = ?";
        List<Integer> formIds = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSql)) {
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    formIds.add(rs.getInt("FormID"));
                }
            }
        }
        
        // Delete templates first to maintain referential integrity
        if (!formIds.isEmpty()) {
            for (Integer formId : formIds) {
                String deleteTemplatesSql = "DELETE FROM ApplicationFormTemplates WHERE FormID = ?";
                try (PreparedStatement stmt = connection.prepareStatement(deleteTemplatesSql)) {
                    stmt.setInt(1, formId);
                    stmt.executeUpdate();
                }
            }
            
            // Then delete the forms
            String deleteFormsSql = "DELETE FROM ApplicationForms WHERE Title = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteFormsSql)) {
                stmt.setString(1, title);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        }
        
        return false;
    }

    //Lấy form templates grouped by form
    public List<Map<String, Object>> getFormsByClubAndStatus(int clubId, boolean published) throws SQLException {
        connection = DBContext.getConnection();
        List<Map<String, Object>> forms = new ArrayList<>();
        String sql = "SELECT af.FormID, af.Title, af.FormType, af.ClubID, af.EventID, af.Published, " +
                     "MIN(aft.TemplateID) as TemplateID " +
                     "FROM ApplicationForms af " +
                     "LEFT JOIN ApplicationFormTemplates aft ON af.FormID = aft.FormID " +
                     "WHERE af.ClubID = ? AND af.Published = ? " +
                     "GROUP BY af.FormID, af.Title, af.FormType, af.ClubID, af.EventID, af.Published " +
                     "ORDER BY af.FormID DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            stmt.setBoolean(2, published);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> form = new HashMap<>();
                    form.put("formId", rs.getInt("FormID"));
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
        catch(SQLException e){
            System.out.println("DEBUG SQL Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return forms;
    }
    // Thêm mẫu form mới và trả về ID được tạo
    public int saveFormTemplateAndGetId(ApplicationFormTemplate template) throws SQLException {
    connection = DBContext.getConnection();
    String sql = "INSERT INTO ApplicationFormTemplates (FormID, FieldName, FieldType, IsRequired, Options, DisplayOrder) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setInt(1, template.getFormId());
        stmt.setString(2, template.getFieldName());
        stmt.setString(3, template.getFieldType());
        stmt.setBoolean(4, template.isRequired());
        stmt.setString(5, template.getOptions());
        stmt.setInt(6, template.getDisplayOrder());
        stmt.executeUpdate();
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
    }
    throw new SQLException("Không thể tạo template mới.");
}
    private ApplicationFormTemplate mapRowToTemplate(ResultSet rs) throws SQLException {
        return new ApplicationFormTemplate(
                rs.getInt("TemplateID"),
                rs.getInt("FormID"),
                rs.getString("FieldName"),
                rs.getString("FieldType"),
                rs.getBoolean("IsRequired"),
                rs.getString("Options"),
                rs.getInt("DisplayOrder")
        );
    }


    public List<ApplicationFormTemplate> getTemplatesByFormId(int formId){
    connection = DBContext.getConnection();
    List<ApplicationFormTemplate> templates = new ArrayList<>();
    String sql = "SELECT * FROM ApplicationFormTemplates WHERE FormID = ? ORDER BY DisplayOrder ASC";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, formId);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                templates.add(new ApplicationFormTemplate(
                    rs.getInt("TemplateID"),
                    rs.getInt("FormID"),
                    rs.getString("FieldName"),
                    rs.getString("FieldType"),
                    rs.getBoolean("IsRequired"),
                    rs.getString("Options"),
                    rs.getInt("DisplayOrder")
                ));
            }
        }
    }
    catch(SQLException e){ e.printStackTrace();}
    return templates;
}

    public List<Integer> getTemplateIdsByGroup(String title, int clubId, int maxTemplateId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT aft.TemplateID FROM ApplicationFormTemplates aft " +
                     "JOIN ApplicationForms af ON aft.FormID = af.FormID " +
                     "WHERE af.Title = ? AND af.ClubID = ? AND aft.TemplateID <= ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setInt(2, clubId);
            ps.setInt(3, maxTemplateId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("TemplateID"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách templateId: " + e.getMessage());
        }
        return ids;
    }

    /**
     * Trả về TemplateID lớn nhất cho một form (dựa vào title + clubId)
     */
    public int getMaxTemplateIdForForm(String title, int clubId) {
        String sql = "SELECT MAX(aft.TemplateID) AS maxId FROM ApplicationFormTemplates aft " +
                     "JOIN ApplicationForms af ON aft.FormID = af.FormID " +
                     "WHERE af.Title = ? AND af.ClubID = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setInt(2, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("maxId");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy max templateId: " + e.getMessage());
        }
        return -1;
    }    // Lấy tất cả câu hỏi đã publish theo (title, clubId)

    public List<ApplicationFormTemplate> getPublishedTemplatesByGroup(String title, int clubId) {
        List<ApplicationFormTemplate> templates = new ArrayList<>();
        // Mở kết nối mới
        connection = DBContext.getConnection();
        String sql = "SELECT aft.* FROM ApplicationFormTemplates aft "
                + "JOIN ApplicationForms af ON aft.FormID = af.FormID "
                + "WHERE af.Title = ? AND af.ClubID = ? AND af.Published = 1 "
                + "ORDER BY aft.DisplayOrder ASC, aft.TemplateID ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setInt(2, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Sử dụng helper mapRowToTemplate để tránh lặp code
                    templates.add(mapRowToTemplate(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return templates;
    }

    /**
     * Lấy tất cả các form member (Club) đã xuất bản
     *
     * @param specificClubId Nếu muốn lọc theo clubId cụ thể, truyền vào giá trị
     * khác null
     * @return Danh sách các form đã xuất bản dạng Map với các thông tin cần
     * thiết
     */
    public List<Map<String, Object>> getPublishedMemberForms(Integer specificClubId) {
    Connection connection = DBContext.getConnection();
    List<Map<String, Object>> forms = new ArrayList<>();

    String sql = "SELECT FormID, Title, FormType, ClubID, EventID, Published " +
                 "FROM ApplicationForms " +
                 "WHERE Published = TRUE AND FormType = 'Club'";

    // Nếu có truyền clubId cụ thể thì thêm điều kiện
    if (specificClubId != null) {
        sql += " AND ClubID = ?";
    }

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        if (specificClubId != null) {
            stmt.setInt(1, specificClubId);
        }

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> form = new HashMap<>();
                form.put("formId", rs.getInt("FormID"));
                form.put("title", rs.getString("Title"));
                form.put("formType", rs.getString("FormType"));
                form.put("clubId", rs.getInt("ClubID"));
                form.put("eventId", rs.getObject("EventID")); // nullable
                form.put("published", rs.getBoolean("Published"));
                forms.add(form);
            }
        }
    } catch (SQLException e) {
        System.err.println("Lỗi khi lấy danh sách form đã xuất bản: " + e.getMessage());
        e.printStackTrace();
    }

    return forms;
}


    /**
     * Phương thức gốc để tương thích ngược với code hiện tại Lấy tất cả các
     * form member (Club) đã xuất bản không lọc theo clubId
     */
    public List<Map<String, Object>> getPublishedMemberForms() {
        return getPublishedMemberForms(null);
    }

    /**
     * Lấy danh sách tất cả các form thành viên đã xuất bản theo clubId cụ thể
     * Phương thức này lấy tất cả các form mà không thực hiện GROUP BY để xem
     * đầy đủ chi tiết
     *
     * @param clubId ID của club cần lọc
     * @return Danh sách các form thành viên đã xuất bản của club
     */
    public List<ApplicationFormTemplate> getAllMemberFormsByClubId(int clubId) {
        List<ApplicationFormTemplate> templates = new ArrayList<>();
        String sql = "SELECT aft.* FROM ApplicationFormTemplates aft " +
                     "JOIN ApplicationForms af ON aft.FormID = af.FormID " +
                     "WHERE af.Published = TRUE AND af.FormType = 'Club' AND af.ClubID = ? " +
                     "ORDER BY af.Title, aft.DisplayOrder";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    templates.add(mapRowToTemplate(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return templates;
    }








    public List<ApplicationFormTemplate> getTemplatesByFormID(int formId) {
        List<ApplicationFormTemplate> templates = new ArrayList<>();
        String sql = "SELECT * FROM ApplicationFormTemplates WHERE FormID = ? ORDER BY DisplayOrder";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, formId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ApplicationFormTemplate template = new ApplicationFormTemplate();
                template.setTemplateId(rs.getInt("TemplateID"));
                template.setFormId(rs.getInt("FormID"));
                template.setFieldName(rs.getString("FieldName"));
                template.setFieldType(rs.getString("FieldType"));
                template.setRequired(rs.getBoolean("IsRequired"));
                template.setOptions(rs.getString("Options"));
                template.setDisplayOrder(rs.getInt("DisplayOrder"));
                templates.add(template);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy templates: " + e.getMessage());
        }
        return templates;
    }


}
