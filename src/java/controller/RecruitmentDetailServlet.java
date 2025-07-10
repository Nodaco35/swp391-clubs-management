package controller;

import dal.ApplicationStageDAO;
import dal.ClubApplicationDAO;
import dal.NotificationDAO;
import dal.UserClubDAO;
import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.*;
import service.NotificationService;
import service.RecruitmentService;

/**
 * Servlet để xử lý việc xem chi tiết hoạt động tuyển quân và các API endpoints
 */
public class RecruitmentDetailServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(RecruitmentDetailServlet.class.getName());
    private final RecruitmentService recruitmentService = new RecruitmentService();
    private final UserClubDAO userClubDAO = new UserClubDAO();
    private final NotificationService notificationService = new NotificationService();
    private final ApplicationStageDAO applicationStageDAO = new ApplicationStageDAO();
    private final ClubApplicationDAO clubApplicationDAO = new ClubApplicationDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Đảm bảo charset UTF-8 cho request và response
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String servletPath = request.getServletPath();
        
        // Xử lý API endpoints
        if (servletPath.startsWith("/api/")) {
            handleAPIRequest(request, response, servletPath);
            return;
        }
        
        // Xử lý trang chi tiết hoạt động tuyển quân
        handleDetailPageRequest(request, response);
    }
    
    /**
     * Xử lý các API requests
     */
    private void handleAPIRequest(HttpServletRequest request, HttpServletResponse response, String servletPath)
            throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Kiểm tra người dùng đã đăng nhập
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            sendErrorResponse(response, "Unauthorized", 401);
            return;
        }
        
        try {
            switch (servletPath) {
                case "/api/notificationTemplates":
                    handleGetNotificationTemplates(request, response, currentUser);
                    break;
                case "/api/stageCandidates":
                    handleGetStageCandidates(request, response, currentUser);
                    break;
                default:
                    sendErrorResponse(response, "API endpoint not found", 404);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in API request: " + servletPath, e);
            sendErrorResponse(response, "Server error: " + e.getMessage(), 500);
        }
    }
    
    /**
     * Xử lý request trang chi tiết hoạt động tuyển quân
     */
    private void handleDetailPageRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Kiểm tra người dùng đã đăng nhập
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            logger.log(Level.INFO, "Người dùng chưa đăng nhập, chuyển hướng đến trang đăng nhập");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        // Xử lý endpoint mặc định
        if (pathInfo == null || pathInfo.equals("/")) {
            // Lấy recruitmentId từ request parameter
            String idParam = request.getParameter("recruitmentId");
            if (idParam == null || idParam.trim().isEmpty()) {
                // Nếu không có recruitmentId, chuyển hướng về myclub với thông báo lỗi
                logger.log(Level.WARNING, "RecruitmentID not found");
                response.sendRedirect(request.getContextPath() + "/myclub?error=missing_parameter&message=" + 
                    URLEncoder.encode("Thiếu ID hoạt động tuyển quân", StandardCharsets.UTF_8.name()));
                return;
            }
            
            try {
                // Hiển thị chi tiết hoạt động tuyển quân
                int recruitmentId = Integer.parseInt(idParam);
                logger.log(Level.INFO, "Đang lấy thông tin hoạt động tuyển quân với ID: {0}", recruitmentId);
                
                RecruitmentCampaign campaign = recruitmentService.getCampaignById(recruitmentId);
                
                if (campaign == null) {
                    // Hoạt động không tồn tại
                    logger.log(Level.WARNING, "Không tìm thấy hoạt động tuyển quân với ID: {0}", recruitmentId);
                    response.sendRedirect(request.getContextPath() + "/myclub?error=not_found&message=" + 
                        URLEncoder.encode("Không tìm thấy hoạt động tuyển quân", StandardCharsets.UTF_8.name()));
                    return;
                }
                
                // Kiểm tra quyền truy cập - chỉ cho phép chủ nhiệm CLB (roleId = 1) xem hoạt động
                UserClub userClub = userClubDAO.getUserClubManagementRole(currentUser.getUserID(), campaign.getClubID());
                
                logger.log(Level.INFO, "Kiểm tra quyền truy cập của người dùng ID: {0} với câu lạc bộ ID: {1}", 
                        new Object[]{currentUser.getUserID(), campaign.getClubID()});
                
                if (userClub == null || userClub.getRoleID() != 1) {
                    // Không có quyền, chuyển hướng về myclub với thông báo lỗi
                    logger.log(Level.WARNING, "Người dùng ID: {0} không có quyền xem hoạt động tuyển quân ID: {1}", 
                            new Object[]{currentUser.getUserID(), recruitmentId});
                    response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + 
                        URLEncoder.encode("Bạn không có quyền xem hoạt động tuyển quân này", StandardCharsets.UTF_8.name()));
                    return;
                }
                
                // Có quyền, lấy dữ liệu và hiển thị
                logger.log(Level.INFO, "Đang lấy thông tin các giai đoạn của hoạt động tuyển quân ID: {0}", recruitmentId);
                List<RecruitmentStage> stages = recruitmentService.getStagesByCampaign(recruitmentId);
                
                logger.log(Level.INFO, "Đã tìm thấy {0} giai đoạn cho hoạt động tuyển quân ID: {1}", 
                        new Object[]{stages.size(), recruitmentId});
                
                // Lấy thống kê ứng viên theo từng giai đoạn
                logger.log(Level.INFO, "Đang lấy thống kê ứng viên theo giai đoạn cho hoạt động tuyển quân ID: {0}", recruitmentId);
                java.util.Map<Integer, java.util.Map<String, Integer>> stageStats = applicationStageDAO.getStageStats(recruitmentId);
                
                request.setAttribute("campaign", campaign);
                request.setAttribute("stages", stages);
                request.setAttribute("stageStats", stageStats);
                
                // Check if debug mode is requested
                String debugParam = request.getParameter("debug");
                if ("true".equals(debugParam)) {
                    logger.log(Level.INFO, "Debug mode requested, forwarding to debug page");
                    request.getRequestDispatcher("/debug-stage.jsp").forward(request, response);
                    return;
                }
                
                logger.log(Level.INFO, "Chuyển hướng đến trang xem chi tiết hoạt động tuyển quân");
                request.getRequestDispatcher("/view/student/chairman/viewRecruitmentCampaign.jsp").forward(request, response);
                
            } catch (NumberFormatException e) {
                // Xử lý lỗi id không hợp lệ
                logger.log(Level.WARNING, "ID hoạt động tuyển quân không hợp lệ: {0}", e.getMessage());
                response.sendRedirect(request.getContextPath() + "/myclub?error=invalid_parameter&message=" + 
                    URLEncoder.encode("ID hoạt động tuyển quân không hợp lệ", StandardCharsets.UTF_8.name()));
                return;
            } catch (Exception e) {
                // Xử lý các lỗi khác
                logger.log(Level.SEVERE, "Lỗi khi hiển thị chi tiết hoạt động tuyển quân: ", e);
                response.sendRedirect(request.getContextPath() + "/myclub?error=system_error&message=" + 
                    URLEncoder.encode("Lỗi hệ thống: " + e.getMessage(), StandardCharsets.UTF_8.name()));
                return;
            }
        } else {
            // Endpoint không hợp lệ
            logger.log(Level.WARNING, "Endpoint không hợp lệ: {0}", pathInfo);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String servletPath = request.getServletPath();
        
        // Chỉ xử lý API endpoint sendStageNotification
        if ("/api/sendStageNotification".equals(servletPath)) {
            handleSendStageNotification(request, response);
        } else {
            // Hiện tại chưa cần xử lý method POST khác
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"success\":false,\"message\":\"Method not supported\"}");
        }
    }

    /**
     * API: Lấy danh sách notification templates theo clubId
     */
    private void handleGetNotificationTemplates(HttpServletRequest request, HttpServletResponse response, Users currentUser)
            throws IOException {
        
        String clubIdStr = request.getParameter("clubId");
        if (clubIdStr == null || clubIdStr.trim().isEmpty()) {
            sendErrorResponse(response, "Missing clubId parameter", 400);
            return;
        }
        
        try {
            int clubId = Integer.parseInt(clubIdStr);
            
            // Lấy danh sách template có thể tái sử dụng
            List<NotificationTemplate> templates = notificationService.getReusableTemplatesByClub(clubId);
            
            // Chuyển đổi sang JSON format đơn giản
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
            
            PrintWriter out = response.getWriter();
            out.print(jsonBuilder.toString());
            
            logger.log(Level.INFO, "Loaded {0} notification templates for club {1}", 
                    new Object[]{templates.size(), clubId});
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "Invalid clubId format", 400);
        }
    }

    /**
     * API: Lấy danh sách ứng viên theo campaign và stage type
     */
    private void handleGetStageCandidates(HttpServletRequest request, HttpServletResponse response, Users currentUser)
            throws IOException {
        
        String campaignIdStr = request.getParameter("campaignId");
        String stageType = request.getParameter("stageType");
        
        if (campaignIdStr == null || stageType == null) {
            sendErrorResponse(response, "Missing campaignId or stageType parameter", 400);
            return;
        }
        
        try {
            int campaignId = Integer.parseInt(campaignIdStr);
            
            // Lấy thông tin stage từ campaign và stageType
            List<RecruitmentStage> stages = recruitmentService.getStagesByCampaign(campaignId);
            RecruitmentStage targetStage = null;
            
            for (RecruitmentStage stage : stages) {
                if (stage.getStageName().equals(stageType)) {
                    targetStage = stage;
                    break;
                }
            }
            
            if (targetStage == null) {
                sendErrorResponse(response, "Stage not found", 404);
                return;
            }
            
            // Lấy danh sách ApplicationStage của vòng này
            List<ApplicationStage> applicationStages = applicationStageDAO.getApplicationStagesByStageId(targetStage.getStageID());
            
            // Chuyển đổi sang JSON format đơn giản
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");
            
            for (int i = 0; i < applicationStages.size(); i++) {
                ApplicationStage appStage = applicationStages.get(i);
                if (i > 0) jsonBuilder.append(",");
                
                // Lấy thông tin application chi tiết
                ClubApplication application = getApplicationById(appStage.getApplicationID());
                
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
            
            PrintWriter out = response.getWriter();
            out.print(jsonBuilder.toString());
            
            logger.log(Level.INFO, "Loaded {0} candidates for campaign {1}, stage {2}", 
                    new Object[]{applicationStages.size(), campaignId, stageType});
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "Invalid campaignId format", 400);
        }
    }

    /**
     * API: Gửi thông báo stage notification
     */
    private void handleSendStageNotification(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            sendErrorResponse(response, "Unauthorized", 401);
            return;
        }
        
        // Đọc JSON data từ request body (cách đơn giản)
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try {
            while ((line = request.getReader().readLine()) != null) {
                jsonBuffer.append(line);
            }
        } catch (IOException e) {
            sendErrorResponse(response, "Error reading request", 400);
            return;
        }
        
        // Parse JSON đơn giản (không dùng Gson để tránh dependency)
        String jsonString = jsonBuffer.toString();
        
        try {
            // Extract values từ JSON string đơn giản
            int stageId = extractIntFromJson(jsonString, "stageId");
            String status = extractStringFromJson(jsonString, "status");
            String title = extractStringFromJson(jsonString, "title");
            String content = extractStringFromJson(jsonString, "content");
            boolean saveAsTemplate = extractBooleanFromJson(jsonString, "saveAsTemplate");
            String templateName = extractStringFromJson(jsonString, "templateName");
            
            // Tạo StageNotification
            StageNotification stageNotification = new StageNotification();
            stageNotification.setStageID(stageId);
            stageNotification.setTitle(title);
            stageNotification.setContent(content);
            stageNotification.setCreatedBy(currentUser.getUserID());
            
            // Lưu template mới nếu được yêu cầu
            if (saveAsTemplate && templateName != null && !templateName.trim().isEmpty()) {
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
                    ClubApplication application = getApplicationById(appStage.getApplicationID());
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
     * Helper methods
     */
    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        out.print("{\"success\": false, \"message\": \"" + escapeJson(message) + "\"}");
    }
    
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    // Các method extract JSON đơn giản (không dùng library)
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
    
    private ClubApplication getApplicationById(int applicationId) {
        try {
            return clubApplicationDAO.getApplicationById(applicationId);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error getting application by id: " + applicationId, e);
            return null;
        }
    }
    
    private String getUserName(String userId) {
        try {
            return userDAO.getUserName(userId);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error getting user name for userId: " + userId, e);
            return null;
        }
    }
    
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
}
