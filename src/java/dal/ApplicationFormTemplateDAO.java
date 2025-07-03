package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.ApplicationFormTemplate;

public class ApplicationFormTemplateDAO {
    private Connection connection;    // Thêm mẫu form mới
    public void saveFormTemplate(ApplicationFormTemplate template) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "INSERT INTO ApplicationFormTemplates (ClubID, EventID, FormType, Title, FieldName, FieldType, IsRequired, Options, Published, DisplayOrder) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            stmt.setInt(10, template.getDisplayOrder());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }    // Cập nhật mẫu form
    public void updateTemplate(ApplicationFormTemplate template) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "UPDATE ApplicationFormTemplates SET ClubID = ?, EventID = ?, FormType = ?, Title = ?, FieldName = ?, " +
                "FieldType = ?, IsRequired = ?, Options = ?, Published = ?, DisplayOrder = ? WHERE TemplateID = ?";
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
            stmt.setInt(10, template.getDisplayOrder());
            stmt.setInt(11, template.getTemplateId());
            stmt.executeUpdate();
        }
    }    // Lấy mẫu form theo ID
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
                            rs.getBoolean("Published"),
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
        String sql = "SELECT * FROM ApplicationFormTemplates WHERE ClubID = ? ORDER BY Title, DisplayOrder";
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
        String sql = "SELECT DISTINCT TemplateID, ClubID, EventID, FormType, Title, FieldName, FieldType, IsRequired, Options, Published, DisplayOrder " +
                "FROM ApplicationFormTemplates WHERE ClubID = ? AND Published = ? " +
                "GROUP BY TemplateID, ClubID, EventID, FormType, Title, FieldName, FieldType, IsRequired, Options, Published, DisplayOrder " +
                "ORDER BY Title, DisplayOrder";
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
        String sql = "INSERT INTO ApplicationFormTemplates (ClubID, EventID, FormType, Title, FieldName, FieldType, IsRequired, Options, Published, DisplayOrder) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            stmt.setInt(10, template.getDisplayOrder());

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
    }    private ApplicationFormTemplate mapRowToTemplate(ResultSet rs) throws SQLException {
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
                rs.getBoolean("Published"),
                rs.getInt("DisplayOrder")
        );
    }    public List<ApplicationFormTemplate> getTemplatesByGroup(String title, int clubId, int maxTemplateId) {
        List<ApplicationFormTemplate> templates = new ArrayList<>();
        String sql = "SELECT * FROM ApplicationFormTemplates WHERE Title = ? AND ClubID = ? AND TemplateID <= ? ORDER BY DisplayOrder ASC, TemplateID ASC";

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
                    template.setDisplayOrder(rs.getInt("DisplayOrder"));
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
    }    // Lấy tất cả câu hỏi đã publish theo (title, clubId)
    public List<ApplicationFormTemplate> getPublishedTemplatesByGroup(String title, int clubId){
        List<ApplicationFormTemplate> templates = new ArrayList<>();
        // Mở kết nối mới
        connection = DBContext.getConnection();
        String sql = "SELECT * FROM ApplicationFormTemplates " +
                "WHERE Title = ? AND ClubID = ? AND Published = 1 " +
                "ORDER BY DisplayOrder ASC, TemplateID ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setInt(2, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Sử dụng helper mapRowToTemplate để tránh lặp code
                    templates.add(mapRowToTemplate(rs));
                }
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return templates;
    }

    /**
     * Lấy tất cả các form member (Club) đã xuất bản
     * 
     * @param specificClubId Nếu muốn lọc theo clubId cụ thể, truyền vào giá trị khác null
     * @return Danh sách các form đã xuất bản dạng Map với các thông tin cần thiết
     */
    public List<Map<String, Object>> getPublishedMemberForms(Integer specificClubId){
        connection = DBContext.getConnection();
        List<Map<String, Object>> forms = new ArrayList<>();
        
        // Ưu tiên 1: Tìm form cho club hiện tại
        String sql = "SELECT DISTINCT Title, FormType, ClubID, EventID, Published, MAX(TemplateID) as TemplateID " +
                "FROM ApplicationFormTemplates " +
                "WHERE Published = TRUE AND FormType = 'Club' ";
                
        if (specificClubId != null) {
            sql += "AND ClubID = ? ";
        }
        
        sql += "GROUP BY Title, FormType, ClubID, EventID, Published " +
               "ORDER BY MAX(TemplateID) DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Nếu có clubId cụ thể thì thêm vào điều kiện query
            if (specificClubId != null) {
                stmt.setInt(1, specificClubId);
                System.out.println("DEBUG: Tìm form cho ClubID cụ thể: " + specificClubId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> form = new HashMap<>();
                    int templateId = rs.getInt("TemplateID");
                    String title = rs.getString("Title");
                    int clubId = rs.getInt("ClubID");
                    
                    form.put("templateId", templateId);
                    form.put("title", title);
                    form.put("formType", rs.getString("FormType"));
                    form.put("clubId", clubId);
                    form.put("eventId", rs.getObject("EventID"));
                    form.put("published", rs.getBoolean("Published"));
                    forms.add(form);
                    
                }
            }
        }
        catch(SQLException e) {
            System.err.println("Lỗi khi lấy danh sách form đã xuất bản: " + e.getMessage());
            e.printStackTrace(); // In stack trace để debug trong quá trình phát triển
        }
        return forms;
    }
    
    /**
     * Phương thức gốc để tương thích ngược với code hiện tại
     * Lấy tất cả các form member (Club) đã xuất bản không lọc theo clubId
     */
    public List<Map<String, Object>> getPublishedMemberForms() {
        return getPublishedMemberForms(null);
    }

    /**
     * Lấy danh sách tất cả các form thành viên đã xuất bản theo clubId cụ thể
     * Phương thức này lấy tất cả các form mà không thực hiện GROUP BY để xem đầy đủ chi tiết
     * 
     * @param clubId ID của club cần lọc
     * @return Danh sách các form thành viên đã xuất bản của club
     */
    public List<ApplicationFormTemplate> getAllMemberFormsByClubId(int clubId) {
        List<ApplicationFormTemplate> templates = new ArrayList<>();
        String sql = "SELECT * FROM ApplicationFormTemplates WHERE Published = TRUE AND FormType = 'Club' AND ClubID = ? ORDER BY Title, DisplayOrder";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    templates.add(mapRowToTemplate(rs));
                    count++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return templates;
    }
}