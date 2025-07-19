package dal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.ApplicationResponse;

public class ApplicationResponseDAO {
    private Connection connection;
    // Thêm response mới
    public int saveResponse(ApplicationResponse response) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "INSERT INTO ApplicationResponses (FormID, UserID, ClubID, EventID, Responses, Status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, response.getFormID());
            ps.setString(2, response.getUserID());
            ps.setInt(3, response.getClubID());
            
            if (response.getEventID() != null) {
                ps.setInt(4, response.getEventID());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            
            ps.setString(5, response.getResponses());
            ps.setString(6, response.getStatus());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Return generated ResponseID
                    }
                }
            }
        }
        return -1;
    }
    
    // Lấy số lượng responses theo templateID
    public int getResponseCountByTemplateId(int templateId) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "SELECT COUNT(*) as count FROM ApplicationResponses WHERE TemplateID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, templateId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }
    
    // Lấy tất cả responses theo templateID
    public List<ApplicationResponse> getResponsesByTemplateId(int templateId) throws SQLException {
        connection = DBContext.getConnection();
        List<ApplicationResponse> responses = new ArrayList<>();
        String sql = "SELECT * FROM ApplicationResponses WHERE TemplateID = ? ORDER BY SubmitDate DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, templateId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ApplicationResponse response = new ApplicationResponse();
                    response.setResponseID(rs.getInt("ResponseID"));
                    response.setFormID(rs.getInt("TemplateID"));
                    response.setUserID(rs.getString("UserID"));
                    response.setClubID(rs.getInt("ClubID"));
                    
                    // Handle nullable EventID
                    int eventId = rs.getInt("EventID");
                    if (!rs.wasNull()) {
                        response.setEventID(eventId);
                    }
                    
                    response.setResponses(rs.getString("Responses"));
                    response.setStatus(rs.getString("Status"));
                    response.setSubmitDate(rs.getTimestamp("SubmitDate"));
                    
                    responses.add(response);
                }
            }
        }
        return responses;
    }
    
    // Lấy responses theo clubID
    public List<ApplicationResponse> getResponsesByClubId(int clubId) throws SQLException {
        connection = DBContext.getConnection();
        List<ApplicationResponse> responses = new ArrayList<>();
        String sql = "SELECT * FROM ApplicationResponses WHERE ClubID = ? ORDER BY SubmitDate DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ApplicationResponse response = new ApplicationResponse();
                    response.setResponseID(rs.getInt("ResponseID"));
                    response.setFormID(rs.getInt("TemplateID"));
                    response.setUserID(rs.getString("UserID"));
                    response.setClubID(rs.getInt("ClubID"));
                    
                    // Handle nullable EventID
                    int eventId = rs.getInt("EventID");
                    if (!rs.wasNull()) {
                        response.setEventID(eventId);
                    }
                    
                    response.setResponses(rs.getString("Responses"));
                    response.setStatus(rs.getString("Status"));
                    response.setSubmitDate(rs.getTimestamp("SubmitDate"));
                    
                    responses.add(response);
                }
            }
        }
        return responses;
    }
    
    // Lấy responses theo userID
    public List<ApplicationResponse> getResponsesByUserId(String userId) throws SQLException {
        connection = DBContext.getConnection();
        List<ApplicationResponse> responses = new ArrayList<>();
        String sql = "SELECT * FROM ApplicationResponses WHERE UserID = ? ORDER BY SubmitDate DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ApplicationResponse response = new ApplicationResponse();
                    response.setResponseID(rs.getInt("ResponseID"));
                    response.setFormID(rs.getInt("TemplateID"));
                    response.setUserID(rs.getString("UserID"));
                    response.setClubID(rs.getInt("ClubID"));
                    
                    // Handle nullable EventID
                    int eventId = rs.getInt("EventID");
                    if (!rs.wasNull()) {
                        response.setEventID(eventId);
                    }
                    
                    response.setResponses(rs.getString("Responses"));
                    response.setStatus(rs.getString("Status"));
                    response.setSubmitDate(rs.getTimestamp("SubmitDate"));
                    
                    responses.add(response);
                }
            }
        }
        return responses;
    }
    
    // Lấy response theo ID
    public ApplicationResponse getResponseById(int responseId) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "SELECT * FROM ApplicationResponses WHERE ResponseID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, responseId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ApplicationResponse response = new ApplicationResponse();
                    response.setResponseID(rs.getInt("ResponseID"));
                    response.setFormID(rs.getInt("TemplateID"));
                    response.setUserID(rs.getString("UserID"));
                    response.setClubID(rs.getInt("ClubID"));
                    
                    // Handle nullable EventID
                    int eventId = rs.getInt("EventID");
                    if (!rs.wasNull()) {
                        response.setEventID(eventId);
                    }
                    
                    response.setResponses(rs.getString("Responses"));
                    response.setStatus(rs.getString("Status"));
                    response.setSubmitDate(rs.getTimestamp("SubmitDate"));
                    
                    return response;
                }
            }
        }
        return null;
    }
    
    // Cập nhật status của response
    public boolean updateResponseStatus(int responseId, String status) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "UPDATE ApplicationResponses SET Status = ? WHERE ResponseID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, responseId);
            
            return ps.executeUpdate() > 0;
        }
    }
    
    // Xóa response theo ID
    public boolean deleteResponse(int responseId) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "DELETE FROM ApplicationResponses WHERE ResponseID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, responseId);
            return ps.executeUpdate() > 0;
        }
    }
    
    // Xóa tất cả responses của một template (khi xóa form)
    public boolean deleteResponsesByTemplateId(int templateId) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "DELETE FROM ApplicationResponses WHERE TemplateID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, templateId);
            return ps.executeUpdate() > 0;
        }
    }
    
    // Lấy thống kê responses theo status
    public int getResponseCountByStatus(int templateId, String status) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "SELECT COUNT(*) as count FROM ApplicationResponses WHERE TemplateID = ? AND Status = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, templateId);
            ps.setString(2, status);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }
    
    // Lấy responses với phân trang
    public List<ApplicationResponse> getResponsesByTemplateIdWithPaging(int templateId, int offset, int limit) throws SQLException {
        connection = DBContext.getConnection();
        List<ApplicationResponse> responses = new ArrayList<>();
        String sql = "SELECT * FROM ApplicationResponses WHERE TemplateID = ? ORDER BY SubmitDate DESC LIMIT ? OFFSET ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, templateId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ApplicationResponse response = new ApplicationResponse();
                    response.setResponseID(rs.getInt("ResponseID"));
                    response.setFormID(rs.getInt("TemplateID"));
                    response.setUserID(rs.getString("UserID"));
                    response.setClubID(rs.getInt("ClubID"));
                    
                    // Handle nullable EventID
                    int eventId = rs.getInt("EventID");
                    if (!rs.wasNull()) {
                        response.setEventID(eventId);
                    }
                    
                    response.setResponses(rs.getString("Responses"));
                    response.setStatus(rs.getString("Status"));
                    response.setSubmitDate(rs.getTimestamp("SubmitDate"));
                    
                    responses.add(response);
                }
            }
        }
        return responses;
    }
    
    // Kiểm tra user đã submit form chưa
    public boolean hasUserSubmitted(int templateId, String userId) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "SELECT COUNT(*) as count FROM ApplicationResponses WHERE TemplateID = ? AND UserID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, templateId);
            ps.setString(2, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * Kiểm tra xem form có phản hồi nào không theo form ID
     * 
     * @param formId ID của form cần kiểm tra
     * @return true nếu form có ít nhất một phản hồi, ngược lại là false
     * @throws SQLException 
     */
    public boolean hasResponsesByFormId(int formId) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "SELECT COUNT(*) FROM ApplicationResponses ar " +
                "JOIN ApplicationForms aft ON ar.formID = aft.formID " +
                "WHERE aft.FormID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, formId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("DEBUG hasResponsesByFormId SQL Error: " + e.getMessage());
            throw e;
        }
        
        return false;
    }
    
    /**
     * Kiểm tra xem một form có phản hồi nào từ người dùng hay không
     * @param formTitle Tên của form
     * @param clubId ID của câu lạc bộ
     * @return true nếu form đã có ít nhất một phản hồi, false nếu chưa có phản hồi
     * @throws SQLException 
     */
    public boolean hasResponsesByFormTitle(String formTitle, int clubId) throws SQLException {
        connection = DBContext.getConnection();
        String sql = "SELECT COUNT(*) FROM ApplicationResponses r " +
                     "INNER JOIN ApplicationFormTemplates t ON r.TemplateID = t.TemplateID " +
                     "INNER JOIN ApplicationForms f ON t.FormID = f.FormID " +
                     "WHERE f.Title = ? AND f.ClubID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, formTitle);
            ps.setInt(2, clubId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
