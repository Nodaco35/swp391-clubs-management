package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.ClubApplicationExtended;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FormResponseDAO extends DBContext {
    private static final Logger LOGGER = Logger.getLogger(FormResponseDAO.class.getName());    
    //Lấy tất cả phản hồi cho một biểu mẫu cụ thể
   
    public List<ClubApplicationExtended> getApplicationsByTemplateId(int templateId) {
        List<ClubApplicationExtended> applications = new ArrayList<>();
        String sql = """
                     SELECT ca.*, u.FullName, ar.Responses, ar.Status as ResponseStatus, 
                            ar.TemplateID, aft.FormType, ar.SubmitDate as ResponseSubmitDate
                     FROM ClubApplications ca
                     JOIN ApplicationResponses ar ON ca.ResponseId = ar.ResponseID
                     JOIN ApplicationFormTemplates aft ON ar.TemplateID = aft.TemplateID
                     JOIN Users u ON ca.UserId = u.UserID
                     WHERE ar.TemplateID = ?
                     AND ar.ResponseID IN (
                        SELECT MAX(ar2.ResponseID) 
                        FROM ApplicationResponses ar2 
                        WHERE ar2.TemplateID = ? 
                        GROUP BY ar2.UserID
                     )
                     ORDER BY ar.SubmitDate DESC
                     """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, templateId);
            ps.setInt(2, templateId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ClubApplicationExtended application = new ClubApplicationExtended();
                    application.setApplicationId(rs.getInt("ApplicationId"));
                    application.setUserId(rs.getString("UserId"));
                    application.setClubId(rs.getInt("ClubId"));
                    application.setEmail(rs.getString("Email"));
                    application.setEventId(rs.getObject("EventId") != null ? rs.getInt("EventId") : null);
                    application.setResponseId(rs.getInt("ResponseId"));
                    application.setStatus(rs.getString("Status"));
                    application.setSubmitDate(rs.getTimestamp("SubmitDate"));
                    application.setFullName(rs.getString("FullName"));
                    application.setResponses(rs.getString("Responses"));
                    application.setResponseStatus(rs.getString("ResponseStatus"));
                    application.setTemplateId(rs.getInt("TemplateID"));
                    application.setFormType(rs.getString("FormType"));
                    applications.add(application);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting applications by template ID", e);
        }
        return applications;
    }    
    //Lấy tất cả phản hồi cho một câu lạc bộ cụ thể     
    public List<ClubApplicationExtended> getApplicationsByClubId(int clubId) {
        List<ClubApplicationExtended> applications = new ArrayList<>();
        String sql = """
                     SELECT ca.*, u.FullName, ar.Responses, ar.Status as ResponseStatus, 
                            ar.TemplateID, aft.FormType, ar.SubmitDate as ResponseSubmitDate
                     FROM ClubApplications ca
                     JOIN ApplicationResponses ar ON ca.ResponseId = ar.ResponseID
                     JOIN ApplicationFormTemplates aft ON ar.TemplateID = aft.TemplateID
                     JOIN Users u ON ca.UserId = u.UserID
                     WHERE ca.ClubId = ?
                     AND ar.ResponseID IN (
                        SELECT MAX(ar2.ResponseID) 
                        FROM ApplicationResponses ar2 
                        JOIN ClubApplications ca2 ON ar2.ResponseID = ca2.ResponseId 
                        WHERE ca2.ClubId = ? 
                        GROUP BY ar2.UserID
                     )
                     ORDER BY ar.SubmitDate DESC
                     """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            ps.setInt(2, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ClubApplicationExtended application = new ClubApplicationExtended();
                    application.setApplicationId(rs.getInt("ApplicationId"));
                    application.setUserId(rs.getString("UserId"));
                    application.setClubId(rs.getInt("ClubId"));
                    application.setEmail(rs.getString("Email"));
                    application.setEventId(rs.getObject("EventId") != null ? rs.getInt("EventId") : null);
                    application.setResponseId(rs.getInt("ResponseId"));
                    application.setStatus(rs.getString("Status"));
                    application.setSubmitDate(rs.getTimestamp("SubmitDate"));
                    application.setFullName(rs.getString("FullName"));
                    application.setResponses(rs.getString("Responses"));
                    application.setResponseStatus(rs.getString("ResponseStatus"));
                    application.setTemplateId(rs.getInt("TemplateID"));
                    application.setFormType(rs.getString("FormType"));
                    applications.add(application);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting applications by club ID", e);
        }
        return applications;
    }
     // Lấy tất cả phản hồi cho một biểu mẫu và câu lạc bộ cụ thể
    public List<ClubApplicationExtended> getApplicationsByTemplateAndClub(int templateId, int clubId) {
        List<ClubApplicationExtended> applications = new ArrayList<>();
        String sql = """
                     SELECT ca.*, u.FullName, ar.Responses, ar.Status as ResponseStatus, 
                            ar.TemplateID, aft.FormType, ar.SubmitDate as ResponseSubmitDate
                     FROM ClubApplications ca
                     JOIN ApplicationResponses ar ON ca.ResponseId = ar.ResponseID
                     JOIN ApplicationFormTemplates aft ON ar.TemplateID = aft.TemplateID
                     JOIN Users u ON ca.UserId = u.UserID
                     WHERE ar.TemplateID = ? AND ca.ClubId = ? 
                     AND ar.ResponseID IN (
                        SELECT MAX(ar2.ResponseID) 
                        FROM ApplicationResponses ar2 
                        JOIN ClubApplications ca2 ON ar2.ResponseID = ca2.ResponseId 
                        WHERE ar2.TemplateID = ? AND ca2.ClubId = ? 
                        GROUP BY ar2.UserID
                     )
                     ORDER BY ar.SubmitDate DESC
                     """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, templateId);
            ps.setInt(2, clubId);
            ps.setInt(3, templateId);
            ps.setInt(4, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ClubApplicationExtended application = new ClubApplicationExtended();
                    application.setApplicationId(rs.getInt("ApplicationId"));
                    application.setUserId(rs.getString("UserId"));
                    application.setClubId(rs.getInt("ClubId"));
                    application.setEmail(rs.getString("Email"));
                    application.setEventId(rs.getObject("EventId") != null ? rs.getInt("EventId") : null);
                    application.setResponseId(rs.getInt("ResponseId"));
                    application.setStatus(rs.getString("Status"));
                    application.setSubmitDate(rs.getTimestamp("SubmitDate"));
                    application.setFullName(rs.getString("FullName"));
                    application.setResponses(rs.getString("Responses"));
                    application.setResponseStatus(rs.getString("ResponseStatus"));
                    application.setTemplateId(rs.getInt("TemplateID"));
                    application.setFormType(rs.getString("FormType"));
                    applications.add(application);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting applications by template and club ID", e);
        }
        return applications;
    }    
    //Cập nhật trạng thái của một phản hồi đơn đăng ký     
    public boolean updateApplicationResponseStatus(int responseId, String newStatus) {
        String sql = "UPDATE ApplicationResponses SET Status = ? WHERE ResponseID = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, responseId);
            int rowsAffected = ps.executeUpdate();
            
            // Cập nhật status trong ClubApplications
            if (rowsAffected > 0) {
                updateClubApplicationStatus(responseId, newStatus);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating application response status", e);
        }
        return false;
    }
    //Cập nhật trạng thái của một đơn đăng ký câu lạc bộ
    private boolean updateClubApplicationStatus(int responseId, String newStatus) {
        String sql = "UPDATE ClubApplications SET Status = ? WHERE ResponseId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, responseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating club application status", e);
            return false;
        }
    }
    

    public boolean addUserToClub(String userId, int clubId, int departmentId) {
        String sql = """
                    INSERT INTO UserClubs (UserID, ClubID, ClubDepartmentID, RoleID, JoinDate, Gen)
                    SELECT ?, ?, ?, ?, ?, YEAR(CURRENT_DATE()) - YEAR(c.EstablishedDate)
                    FROM Clubs c WHERE c.ClubID = ?
                    """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, clubId);
            ps.setInt(3, departmentId); 
            ps.setInt(4, 4); // Role 4 là member
            ps.setTimestamp(5, new Timestamp(new Date().getTime()));
            ps.setInt(6, clubId); // ClubID lần thứ hai cho việc tính Gen
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding user to club", e);
            return false;
        }
    }
    
    public boolean registerUserForEvent(String userId, int eventId) {
        String sql = "INSERT INTO EventParticipants (EventID, UserID, Status) VALUES (?, ?, 'REGISTERED')";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error registering user for event", e);
            return false;
        }
    }
    

    public boolean isUserRegisteredForEvent(String userId, int eventId) {
        String sql = "SELECT COUNT(*) FROM EventParticipants WHERE EventID = ? AND UserID = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setString(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if user is registered for event", e);
        }
        return false;
    }
    
    //Kiểm tra xem người dùng đã là thành viên của câu lạc bộ hay chưa
    public boolean isUserMemberOfClub(String userId, int clubId) {
        String sql = "SELECT COUNT(*) FROM UserClubs WHERE ClubID = ? AND UserID = ? AND IsActive = 1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            ps.setString(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if user is member of club", e);
        }
        return false;
    }
      //Lấy thông tin đầy đủ về phản hồi đơn đăng ký theo ID
    public ClubApplicationExtended getApplicationById(int responseId) {
        LOGGER.info("Getting application by responseId: " + responseId);
        
        String sql = """
                     SELECT ca.*, u.FullName, ar.Responses, ar.Status as ResponseStatus, ar.TemplateID,
                     aft.FormType, ar.SubmitDate as ResponseSubmitDate
                     FROM ClubApplications ca
                     JOIN ApplicationResponses ar ON ca.ResponseId = ar.ResponseID
                     JOIN ApplicationFormTemplates aft ON ar.TemplateID = aft.TemplateID
                     JOIN Users u ON ca.UserId = u.UserID
                     WHERE ar.ResponseID = ?
                     """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, responseId);
            LOGGER.info("Executing query: " + sql.replace("\n", " ") + " with parameter: " + responseId);
            
            try (ResultSet rs = ps.executeQuery()) {                if (rs.next()) {
                    ClubApplicationExtended application = new ClubApplicationExtended();
                    application.setApplicationId(rs.getInt("ApplicationId"));
                    application.setUserId(rs.getString("UserId"));
                    application.setClubId(rs.getInt("ClubId"));
                    application.setEmail(rs.getString("Email"));
                    application.setEventId(rs.getObject("EventId") != null ? rs.getInt("EventId") : null);
                    application.setResponseId(rs.getInt("ResponseId"));
                    application.setStatus(rs.getString("Status"));
                    application.setSubmitDate(rs.getTimestamp("SubmitDate"));
                    application.setFullName(rs.getString("FullName"));
                    
                    // Lấy và log responses
                    String responses = rs.getString("Responses");
                    LOGGER.info("Raw responses for ID " + responseId + ": " + 
                                (responses != null && responses.length() > 200 ? 
                                    responses.substring(0, 200) + "..." : responses));
                    
                    application.setResponses(responses);
                    application.setResponseStatus(rs.getString("ResponseStatus"));
                    application.setTemplateId(rs.getInt("TemplateID"));
                    application.setFormType(rs.getString("FormType"));
                    
                    LOGGER.info("Found application: " + application);
                    return application;
                } else {
                    LOGGER.warning("No application found for responseId: " + responseId);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting application by ID", e);
        }
        return null;
    }
    // Xử lý duyệt đơn đăng ký
    public boolean approveApplication(int responseId) {
        ClubApplicationExtended application = getApplicationById(responseId);
        if (application == null) {
            return false;
        }
        
        // Xử lý khác nhau dựa trên loại form (member hoặc event)
        if ("event".equalsIgnoreCase(application.getFormType())) {
            // Đối với đăng ký sự kiện
            Integer eventId = application.getEventId();
            if (eventId == null) {
                LOGGER.warning("Cannot approve event application - eventId is null for responseId: " + responseId);
                return false;
            }
            
            // Cập nhật trạng thái đơn đăng ký trước
            boolean updateSuccess = updateApplicationResponseStatus(responseId, "Approved");
            if (!updateSuccess) {
                LOGGER.warning("Failed to update application status for responseId: " + responseId);
                return false;
            }
            
            // Sử dụng EventParticipantDAO để thêm người dùng vào danh sách tham gia
            EventParticipantDAO eventParticipantDAO = new EventParticipantDAO();
            boolean registerSuccess = eventParticipantDAO.registerUserForEvent(eventId, application.getUserId());
            
            if (registerSuccess) {
                LOGGER.info("Successfully registered user " + application.getUserId() + 
                           " for event " + eventId + " with status REGISTERED");
            } else {
                LOGGER.warning("Failed to register user " + application.getUserId() + 
                              " for event " + eventId);
            }
            
            return registerSuccess;
        } else {
            // Đối với đăng ký thành viên - phụ thuộc vào trạng thái hiện tại
            String currentStatus = application.getResponseStatus();
            String newStatus;
            
            switch (currentStatus.toLowerCase()) {
                case "pending":
                    newStatus = "Candidate";
                    break;
                case "candidate":
                    newStatus = "Collaborator";
                    break;
                case "collaborator":
                    newStatus = "Approved";
                    // Nếu duyệt cuối cùng, thêm vào UserClubs nếu chưa là thành viên
                    if (!isUserMemberOfClub(application.getUserId(), application.getClubId())) {
                        // Lấy departmentId từ responses JSON
                        int departmentId = extractDepartmentId(application.getResponses(), application.getClubId());
                        LOGGER.info("Final processing: Adding user " + application.getUserId() + 
                                    " to club " + application.getClubId() + 
                                    " with department " + departmentId);
                        addUserToClub(application.getUserId(), application.getClubId(), departmentId);
                    }
                    break;
                default:
                    return false;
            }
            
            return updateApplicationResponseStatus(responseId, newStatus);
        }
    }
    //Trích xuất ID phòng ban từ JSON responses
     
    private int extractDepartmentId(String responsesJson, int clubId) {
        try {
            // Sử dụng Gson để phân tích JSON
            Gson gson = new Gson();
            
            // Kiểm tra xem JSON có phải là object không
            JsonElement jsonElement = gson.fromJson(responsesJson, JsonElement.class);
            
            // Trường hợp 1: JSON là object (các field ID là key)
            if (jsonElement != null && jsonElement.isJsonObject()) {
                LOGGER.info("Processing JSON as object structure");
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                
                // Duyệt qua các trường trong JSON object    
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    if (entry.getValue().isJsonObject()) {
                        JsonObject fieldObj = entry.getValue().getAsJsonObject();
                        if (fieldObj.has("value")) {
                            String value = fieldObj.get("value").getAsString();
                            if (value.toLowerCase().contains("ban") || value.toLowerCase().contains("phòng")) {
                                LOGGER.info("Found department answer in field " + entry.getKey() + ": " + value);
                                try {
                                    return Integer.parseInt(value.trim());
                                } catch (NumberFormatException e) {
                                    int departmentId = getDepartmentIdByName(value, clubId);
                                    if (departmentId > 0) {
                                        return departmentId;
                                    }
                                }
                            }
                        }
                    }
                }
            } 
            // Trường hợp 2: JSON là array (cấu trúc cũ)
            else if (jsonElement != null && jsonElement.isJsonArray()) {
                LOGGER.info("Processing JSON as array structure");
                JsonArray answersArray = jsonElement.getAsJsonArray();
                
                // Tìm câu hỏi liên quan đến ban/phòng ban
                for (JsonElement element : answersArray) {
                    JsonObject answerObj = element.getAsJsonObject();
                    String question = "";
                    String answer = "";
                    
                    if (answerObj.has("question")) {
                        question = answerObj.get("question").getAsString().toLowerCase();
                    }
                    
                    if (answerObj.has("answer")) {
                        answer = answerObj.get("answer").getAsString();
                    }
                    
                    // Kiểm tra nếu đây là câu hỏi về phòng ban/ban
                    if ((question.contains("ban") || question.contains("department") || 
                         question.contains("phòng ban") || question.contains("bộ phận")) && !answer.isEmpty()) {
                        
                        // Thử trích xuất department ID từ câu trả lời
                        try {
                            // Trích xuất số trực tiếp
                            return Integer.parseInt(answer.trim());
                        } catch (NumberFormatException e) {
                            // Nếu không phải số trực tiếp, tìm department ID theo tên
                            int departmentId = getDepartmentIdByName(answer, clubId);
                            if (departmentId > 0) {
                                return departmentId;
                            }
                        }
                    }
                }
            }
              // Nếu không tìm thấy trong JSON, lấy department mặc định cho club
            LOGGER.info("Could not find department info in responses, using default department");
            return getDefaultDepartmentForClub(clubId);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not extract department ID, using default", e);
            return getDefaultDepartmentForClub(clubId);
        }
    }
    
    //Lấy department ID theo tên và club ID
    private int getDepartmentIdByName(String departmentName, int clubId) {
        String sql = "SELECT ClubDepartmentID FROM ClubDepartments WHERE ClubID = ? AND DepartmentName LIKE ? AND IsActive = 1";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            ps.setString(2, "%" + departmentName.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ClubDepartmentID");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error getting department ID by name", e);
        }
        return 0;
    }
    
    //Lấy department mặc định cho một club (ban đầu tiên và đang active)
    private int getDefaultDepartmentForClub(int clubId) {
        String sql = "SELECT ClubDepartmentID FROM ClubDepartments WHERE ClubID = ? AND IsActive = 1 LIMIT 1";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ClubDepartmentID");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error getting default department for club", e);
        }
        return 1; // Mặc định là department ID 1 nếu không tìm thấy
    }

    public boolean rejectApplication(int responseId) {
        return updateApplicationResponseStatus(responseId, "Rejected");
    }

    // Phương thức để kiểm tra xem một form có bất kỳ phản hồi nào hay không
    public boolean hasResponses(int templateId) {
        String sql = "SELECT COUNT(*) FROM ApplicationResponses WHERE TemplateID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, templateId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error checking if template has responses", ex);
        }
        return false;
    }
    
    // Phương thức lấy danh sách templateId có phản hồi
    public List<Integer> getTemplateIdsWithResponses(int clubId) {
        List<Integer> templateIdsWithResponses = new ArrayList<>();
        String sql = "SELECT DISTINCT ar.TemplateID " +
                     "FROM ApplicationResponses ar " +
                     "JOIN ApplicationFormTemplates aft ON ar.TemplateID = aft.TemplateID " +
                     "WHERE aft.ClubID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    templateIdsWithResponses.add(rs.getInt("TemplateID"));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error getting template IDs with responses", ex);
        }
        return templateIdsWithResponses;
    }
    
    /**
     * Kiểm tra xem một chiến dịch tuyển quân đã có đơn đăng ký hay chưa
     * @param campaignId ID của chiến dịch cần kiểm tra
     * @return true nếu đã có đơn đăng ký, false nếu chưa có
     */
    public boolean hasCampaignApplications(int campaignId) {
        String sql = """
                     SELECT COUNT(*) as count
                     FROM ClubApplications ca
                     JOIN RecruitmentCampaigns rc ON ca.ClubId = rc.ClubID
                     WHERE rc.RecruitmentID = ?
                     """;
                     
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, campaignId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if campaign has applications", e);
        }
        return false;
    }
    
    /**
     * Kiểm tra số lượng đơn đã nộp cho một mẫu đơn cụ thể trong một chiến dịch
     * @param templateId ID của mẫu đơn
     * @param campaignId ID của chiến dịch tuyển quân
     * @return Số lượng đơn đã nộp
     */
    public int countApplicationsForTemplate(int templateId, int campaignId) {
        String sql = """
                     SELECT COUNT(*) as count
                     FROM ClubApplications ca
                     JOIN ApplicationResponses ar ON ca.ResponseId = ar.ResponseID
                     JOIN RecruitmentCampaigns rc ON ca.ClubId = rc.ClubID
                     WHERE ar.TemplateID = ? AND rc.RecruitmentID = ?
                     """;
                     
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, templateId);
            ps.setInt(2, campaignId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting applications for template in campaign", e);
        }
        return 0;
    }
}
