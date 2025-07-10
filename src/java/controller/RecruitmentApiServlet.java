package controller;

import dal.ApplicationStageDAO;
import dal.ClubApplicationDAO;
import dal.NotificationDAO;
import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.*;
import service.NotificationService;
import service.RecruitmentService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * API Servlet xử lý các endpoint cho trang xem chi tiết hoạt động tuyển quân
 */
public class RecruitmentApiServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(RecruitmentApiServlet.class.getName());
    
    // Các service đã có sẵn
    private final NotificationService notificationService = new NotificationService();
    private final RecruitmentService recruitmentService = new RecruitmentService();
    
    // Các DAO để lấy dữ liệu chi tiết
    private final ApplicationStageDAO applicationStageDAO = new ApplicationStageDAO();
    private final ClubApplicationDAO clubApplicationDAO = new ClubApplicationDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Thiết lập response UTF-8 và JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        String queryString = request.getQueryString();
        logger.log(Level.INFO, "RecruitmentApiServlet - doGet: {0}?{1}", new Object[]{pathInfo, queryString});
        PrintWriter out = response.getWriter();
        
        try {
            // Kiểm tra đăng nhập
            if (!isUserAuthenticated(request)) {
                sendErrorResponse(response, "Unauthorized", 401);
                return;
            }
            
            // Route các endpoint
            switch (pathInfo) {
                case "/notificationTemplates":
                    handleGetNotificationTemplates(request, response);
                    break;
                case "/stageCandidates":
                    handleGetStageCandidates(request, response);
                    break;
                default:
                    sendErrorResponse(response, "Endpoint not found", 404);
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in API request: " + pathInfo, e);
            sendErrorResponse(response, "Server error: " + e.getMessage(), 500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Thiết lập response UTF-8 và JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        logger.log(Level.INFO, "RecruitmentApiServlet - doPost: {0}", pathInfo);
        
        try {
            // Log request headers để debug
            java.util.Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                logger.log(Level.FINE, "Header: {0} = {1}", new Object[]{headerName, request.getHeader(headerName)});
            }
            // Kiểm tra đăng nhập
            if (!isUserAuthenticated(request)) {
                sendErrorResponse(response, "Unauthorized", 401);
                return;
            }
            
            // Route các endpoint POST
            switch (pathInfo) {
                case "/sendStageNotification":
                    handleSendStageNotification(request, response);
                    break;
                default:
                    sendErrorResponse(response, "Endpoint not found", 404);
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in API POST request: " + pathInfo, e);
            sendErrorResponse(response, "Server error: " + e.getMessage(), 500);
        }
    }

    /**
     * Xử lý endpoint GET /api/notificationTemplates?clubId=X
     * Trả về danh sách template thông báo có thể tái sử dụng của club
     */
    private void handleGetNotificationTemplates(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        logger.log(Level.INFO, "=== Xử lý GET notificationTemplates ===");
        
        String clubIdStr = request.getParameter("clubId");
        logger.log(Level.INFO, "Parameter clubId: {0}", clubIdStr);
        
        if (clubIdStr == null || clubIdStr.trim().isEmpty()) {
            logger.log(Level.WARNING, "Thiếu tham số clubId");
            sendErrorResponse(response, "Missing clubId parameter", 400);
            return;
        }
        
        try {
            int clubId = Integer.parseInt(clubIdStr);
            
            // Sử dụng NotificationService có sẵn để lấy template
            List<NotificationTemplate> templates = notificationService.getReusableTemplatesByClub(clubId);
            
            // Chuyển đổi dữ liệu cho frontend
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");
            
            for (int i = 0; i < templates.size(); i++) {
                NotificationTemplate template = templates.get(i);
                if (i > 0) jsonBuilder.append(",");
                
                jsonBuilder.append("{")
                    .append("\"templateId\":").append(template.getTemplateID()).append(",")
                    .append("\"templateName\":\"").append(escapeJson(template.getTemplateName())).append("\",")
                    .append("\"title\":\"").append(escapeJson(template.getTitle())).append("\",")
                    .append("\"content\":\"").append(escapeJson(template.getContent())).append("\"")
                    .append("}");
            }
            
            jsonBuilder.append("]");
            
            // Gửi response
            PrintWriter out = response.getWriter();
            out.print(jsonBuilder.toString());
            
            logger.log(Level.INFO, "Loaded {0} notification templates for club {1}", 
                    new Object[]{templates.size(), clubId});
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "Invalid clubId format", 400);
        }
    }

    /**
     * Xử lý endpoint GET /api/stageCandidates?campaignId=X&stageType=Y
     * Trả về danh sách ứng viên của một vòng cụ thể
     */
    private void handleGetStageCandidates(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        String campaignIdStr = request.getParameter("campaignId");
        String stageType = request.getParameter("stageType");
        
        logger.log(Level.INFO, "API REQUEST: stageCandidates - campaignId={0}, stageType={1}", new Object[]{campaignIdStr, stageType});
        
        if (campaignIdStr == null || stageType == null) {
            logger.warning("Missing campaignId or stageType parameter");
            sendErrorResponse(response, "Missing campaignId or stageType parameter", 400);
            return;
        }
        
        try {
            int campaignId = Integer.parseInt(campaignIdStr);
            
            // Lấy thông tin stage từ campaign và stageType
            logger.log(Level.INFO, "Fetching stages for campaign {0}", campaignId);
            List<RecruitmentStage> stages = recruitmentService.getStagesByCampaign(campaignId);
            RecruitmentStage targetStage = null;
            
            for (RecruitmentStage stage : stages) {
                if (stage.getStageName().equalsIgnoreCase(stageType)) {
                    targetStage = stage;
                    break;
                }
            }
            
            if (targetStage == null) {
                logger.log(Level.WARNING, "Stage not found for campaign {0} and type {1}", new Object[]{campaignId, stageType});
                sendErrorResponse(response, "Stage not found", 404);
                return;
            }
            
            // Lấy danh sách ApplicationStage của vòng này
            logger.log(Level.INFO, "Fetching application stages for stageId {0}", targetStage.getStageID());
            List<ApplicationStage> applicationStages = applicationStageDAO.getApplicationStagesByStageId(targetStage.getStageID());
            
            // Chuyển đổi dữ liệu cho frontend
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");
            
            for (int i = 0; i < applicationStages.size(); i++) {
                ApplicationStage appStage = applicationStages.get(i);
                if (i > 0) jsonBuilder.append(",");
                
                // Lấy thông tin application chi tiết
                ClubApplication application = clubApplicationDAO.getApplicationById(appStage.getApplicationID());
                
                jsonBuilder.append("{")
                    .append("\"applicationId\":").append(appStage.getApplicationID()).append(",")
                    .append("\"applicationStageId\":").append(appStage.getApplicationStageID()).append(",")
                    .append("\"userName\":\"").append(escapeJson(application != null ? getUserName(application.getUserId()) : "")).append("\",")
                    .append("\"email\":\"").append(escapeJson(application != null ? application.getEmail() : "")).append("\",")
                    .append("\"status\":\"").append(appStage.getStatus()).append("\",")
                    .append("\"submitDate\":\"").append(application != null && application.getSubmitDate() != null ? 
                        application.getSubmitDate().toString() : "").append("\"")
                    .append("}");
            }
            
            jsonBuilder.append("]");
            
            // Gửi response
            PrintWriter out = response.getWriter();
            out.print(jsonBuilder.toString());
            
            logger.log(Level.INFO, "Loaded {0} candidates for campaign {1}, stage {2}", 
                    new Object[]{applicationStages.size(), campaignId, stageType});
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "Invalid campaignId format", 400);
        }
    }

    /**
     * Xử lý endpoint POST /api/sendStageNotification
     * Gửi thông báo hàng loạt đến ứng viên đã duyệt/từ chối của một vòng
     */
    private void handleSendStageNotification(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        logger.log(Level.INFO, "=== Bắt đầu xử lý sendStageNotification ===");
        
        // Đọc JSON data từ request body
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            jsonBuffer.append(line);
        }
        
        try {
            // Parse JSON request đơn giản
            String requestBody = jsonBuffer.toString();
            logger.log(Level.INFO, "Request body: {0}", requestBody);
            
            int stageId = extractIntFromJson(requestBody, "stageId");
            String status = extractStringFromJson(requestBody, "status"); // "APPROVED" hoặc "REJECTED"
            String title = extractStringFromJson(requestBody, "title");
            String content = extractStringFromJson(requestBody, "content");
            boolean saveAsTemplate = extractBooleanFromJson(requestBody, "saveAsTemplate");
            String templateName = extractStringFromJson(requestBody, "templateName");
            
            logger.log(Level.INFO, "Thông tin thông báo: stageId={0}, status={1}, title={2}, saveAsTemplate={3}, templateName={4}", 
                    new Object[]{stageId, status, title, saveAsTemplate, templateName});
            
            Users currentUser = getCurrentUser(request);
            if (currentUser == null) {
                sendErrorResponse(response, "User not authenticated", 401);
                return;
            }
            
            // Tạo StageNotification
            StageNotification stageNotification = new StageNotification();
            stageNotification.setStageID(stageId);
            stageNotification.setTitle(title);
            stageNotification.setContent(content);
            stageNotification.setCreatedBy(currentUser.getUserID());
            
            // Lưu template mới nếu được yêu cầu
            if (saveAsTemplate && templateName != null && !templateName.trim().isEmpty()) {
                // Lấy thông tin stage để biết clubId
                RecruitmentStage stage = recruitmentService.getStageById(stageId);
                if (stage != null) {
                    RecruitmentCampaign campaign = recruitmentService.getCampaignById(stage.getRecruitmentID());
                    if (campaign != null) {
                        NotificationTemplate template = new NotificationTemplate();
                        template.setClubID(campaign.getClubID());
                        template.setTemplateName(templateName);
                        template.setTitle(title);
                        template.setContent(content);
                        template.setCreatedBy(currentUser.getUserID());
                        template.setReusable(true);
                        
                        int templateId = notificationService.createTemplate(template);
                        if (templateId > 0) {
                            stageNotification.setTemplateID(templateId);
                        }
                    }
                }
            }
            
            // Tạo stage notification
            int notificationId = notificationService.createStageNotification(stageNotification);
            
            if (notificationId > 0) {
                // Gửi thông báo hàng loạt đến ứng viên có status tương ứng
                List<ApplicationStage> targetApplications = applicationStageDAO.getApplicationStagesByStageAndStatus(stageId, status);
                
                int sentCount = 0;
                for (ApplicationStage appStage : targetApplications) {
                    ClubApplication application = clubApplicationDAO.getApplicationById(appStage.getApplicationID());
                    if (application != null && application.getUserId() != null) {
                        
                        // Thay thế các biến động trong nội dung
                        String personalizedTitle = replaceVariables(title, application, stageId);
                        String personalizedContent = replaceVariables(content, application, stageId);
                        
                        // Gửi thông báo (sử dụng method có sẵn)
                        NotificationDAO.sentToPerson(currentUser.getUserID(), application.getUserId(), 
                                personalizedTitle, personalizedContent);
                        sentCount++;
                    }
                }
                
                // Cập nhật thống kê gửi thông báo (nếu StageNotification model hỗ trợ)
                // stageNotification.setTotalSent(sentCount);
                // stageNotification.setLastSentAt(new java.util.Date());
                // notificationService.updateStageNotification(stageNotification);
                
                // Gửi response thành công
                PrintWriter out = response.getWriter();
                out.print("{\"success\": true, \"message\": \"Đã gửi thông báo tới " + sentCount + " ứng viên\", \"sentCount\": " + sentCount + "}");
                
                logger.log(Level.INFO, "Sent stage notification to {0} candidates for stage {1}, status {2}", 
                        new Object[]{sentCount, stageId, status});
                
            } else {
                sendErrorResponse(response, "Failed to create stage notification", 500);
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending stage notification", e);
            sendErrorResponse(response, "Error: " + e.getMessage(), 500);
        }
    }

    /**
     * Thay thế các biến động trong template thông báo
     */
    private String replaceVariables(String template, ClubApplication application, int stageId) {
        String result = template;
        
        // Lấy tên ứng viên
        String candidateName = getUserName(application.getUserId());
        if (candidateName == null || candidateName.trim().isEmpty()) {
            candidateName = application.getEmail().split("@")[0]; // Dùng phần trước @ của email
        }
        
        // Lấy tên vòng
        String stageName = getStageName(stageId);
        
        // Lấy địa điểm (nếu là vòng phỏng vấn)
        String location = getStageLocation(stageId);
        
        // Thay thế các biến
        result = result.replace("{Tên ứng viên}", candidateName);
        result = result.replace("{Tên vòng}", stageName);
        result = result.replace("{Địa điểm}", location != null ? location : "Sẽ thông báo sau");
        result = result.replace("{Thời gian}", "Sẽ thông báo chi tiết qua email");
        
        return result;
    }

    /**
     * Các helper methods
     */
    private boolean isUserAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }
    
    private Users getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null ? (Users) session.getAttribute("user") : null;
    }
    
    private String getUserName(String userId) {
        try {
            return userDAO.getUserName(userId);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error getting user name for userId: " + userId, e);
            return null;
        }
    }
    
    private String getStageName(int stageId) {
        try {
            RecruitmentStage stage = recruitmentService.getStageById(stageId);
            if (stage != null) {
                String stageName = stage.getStageName();
                switch (stageName) {
                    case "APPLICATION": return "Vòng nộp đơn";
                    case "INTERVIEW": return "Vòng phỏng vấn";
                    case "CHALLENGE": return "Vòng thử thách";
                    default: return stageName;
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error getting stage name for stageId: " + stageId, e);
        }
        return "Vòng tuyển";
    }
    
    private String getStageLocation(int stageId) {
        try {
            RecruitmentStage stage = recruitmentService.getStageById(stageId);
            return stage != null ? stage.getLocation() : null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error getting stage location for stageId: " + stageId, e);
            return null;
        }
    }
    
    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setStatus(statusCode);
        
        PrintWriter out = response.getWriter();
        out.print("{\"success\":false,\"message\":\"" + escapeJson(message) + "\"}");
    }

    /**
     * Helper method để escape JSON string
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    // Các method extract JSON đơn giản
    private int extractIntFromJson(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }
    
    private String extractStringFromJson(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"|\"" + key + "\"\\s*:\\s*'([^']*)'";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1) != null ? m.group(1) : m.group(2);
        }
        return "";
    }
    
    private boolean extractBooleanFromJson(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(true|false)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Boolean.parseBoolean(m.group(1));
        }
        return false;
    }
}
