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
import service.StageNotificationService;
import service.RecruitmentService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * API Servlet xử lý các endpoint cho trang xem chi tiết hoạt động tuyển quân
 */
public class RecruitmentApiServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(RecruitmentApiServlet.class.getName());

    private final StageNotificationService notificationService = new StageNotificationService();
    private final RecruitmentService recruitmentService = new RecruitmentService();
    private final ApplicationStageDAO applicationStageDAO = new ApplicationStageDAO();
    private final ClubApplicationDAO clubApplicationDAO = new ClubApplicationDAO();
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
        try {
            // Kiểm tra đăng nhập
            if (!isUserAuthenticated(request)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Route các endpoint
            // Loại bỏ dấu "/" phía trước nếu có
            if (pathInfo == null) {
                sendErrorResponse(response, "Invalid path", 400);
                return;
            }

            String path = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;

            switch (path) {
                case "notificationTemplates":
                    handleGetNotificationTemplates(request, response);
                    break;
                case "stageCandidates":
                    handleGetStageCandidates(request, response);
                    break;
                default:
                    sendErrorResponse(response, "Endpoint not found: " + pathInfo, 404);
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
            java.util.Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                logger.log(Level.FINE, "Header: {0} = {1}", new Object[]{headerName, request.getHeader(headerName)});
            }
            // Kiểm tra đăng nhập
            if (!isUserAuthenticated(request)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Route các endpoint POST
            if (pathInfo == null) {
                sendErrorResponse(response, "Invalid path", 400);
                return;
            }

            String path = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
            switch (path) {
                case "sendBulkNotification":
                    handleSendBulkNotification(request, response);
                    break;
                default:
                    sendErrorResponse(response, "Endpoint not found: " + pathInfo, 404);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in API POST request: " + pathInfo, e);
            sendErrorResponse(response, "Server error: " + e.getMessage(), 500);
        }
    }

    /**
     * Xử lý endpoint GET /api/notificationTemplates?clubId=X Trả về danh sách
     * template thông báo có thể tái sử dụng của club
     */
    private void handleGetNotificationTemplates(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String clubIdStr = request.getParameter("clubId");
        if (clubIdStr == null || clubIdStr.trim().isEmpty()) {
            logger.log(Level.WARNING, "Thiếu tham số clubId");
            sendErrorResponse(response, "Missing clubId parameter", 400);
            return;
        }

        try {
            int clubId = Integer.parseInt(clubIdStr);
            // Sử dụng NotificationService có sẵn để lấy template
            List<NotificationTemplate> templates = notificationService.getReusableTemplatesByClub(clubId);
            logger.log(Level.INFO, "Retrieved {0} templates from database for clubId={1}", new Object[]{templates.size(), clubId});

            // Chuyển đổi dữ liệu cho frontend
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");

            for (int i = 0; i < templates.size(); i++) {
                NotificationTemplate template = templates.get(i);
                if (i > 0) {
                    jsonBuilder.append(",");
                }

                jsonBuilder.append("{")
                        .append("\"templateId\":").append(template.getTemplateID()).append(",")
                        .append("\"templateName\":\"").append(escapeJson(template.getTemplateName())).append("\",")
                        .append("\"title\":\"").append(escapeJson(template.getTitle())).append("\",")
                        .append("\"content\":\"").append(escapeJson(template.getContent())).append("\"")
                        .append("}");

            }

            jsonBuilder.append("]");
            String jsonResponse = jsonBuilder.toString();

            // Gửi response
            PrintWriter out = response.getWriter();
            out.print(jsonResponse);

            logger.log(Level.INFO, "Loaded {0} notification templates for club {1}",
                    new Object[]{templates.size(), clubId});

        } catch (NumberFormatException e) {
            sendErrorResponse(response, "Invalid clubId format", 400);
        }
    }

    /**
     * Xử lý endpoint GET /api/stageCandidates?campaignId=X&stageType=Y Trả về
     * danh sách ứng viên của một vòng cụ thể
     */
    private void handleGetStageCandidates(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String campaignIdStr = request.getParameter("campaignId");
        String stageType = request.getParameter("stageType");
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
                if (i > 0) {
                    jsonBuilder.append(",");
                }

                // Lấy thông tin application chi tiết
                ClubApplication application = clubApplicationDAO.getApplicationById(appStage.getApplicationID());

                jsonBuilder.append("{")
                        .append("\"applicationId\":").append(appStage.getApplicationID()).append(",")
                        .append("\"applicationStageId\":").append(appStage.getApplicationStageID()).append(",")
                        .append("\"userId\":\"").append(escapeJson(application != null ? application.getUserId() : "")).append("\",")
                        .append("\"userName\":\"").append(escapeJson(application != null ? getUserName(application.getUserId()) : "")).append("\",")
                        .append("\"email\":\"").append(escapeJson(application != null ? application.getEmail() : "")).append("\",")
                        .append("\"status\":\"").append(appStage.getStatus()).append("\",")
                        .append("\"submitDate\":\"").append(application != null && application.getSubmitDate() != null
                        ? application.getSubmitDate().toString() : "").append("\"")
                        .append("}");
            }

            jsonBuilder.append("]");

            // Gửi response
            PrintWriter out = response.getWriter();
            out.print(jsonBuilder.toString());

        } catch (NumberFormatException e) {
            sendErrorResponse(response, "Invalid campaignId format", 400);
        }
    }

    /**
     * Xử lý endpoint POST /api/sendBulkNotification Gửi thông báo hàng loạt đến
     * danh sách ứng viên đã chọn
     */
    private void handleSendBulkNotification(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        logger.log(Level.INFO, "=== Bắt đầu xử lý sendBulkNotification ===");

        // Đọc JSON data từ request body
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            jsonBuffer.append(line);
        }

        try {
            // Parse JSON request
            String requestBody = jsonBuffer.toString();
            logger.log(Level.INFO, "Request body: {0}", requestBody);

            // Extract các trường chính
            String title = extractStringFromJson(requestBody, "title");
            String content = extractStringFromJson(requestBody, "content");
            boolean saveAsTemplate = extractBooleanFromJson(requestBody, "saveAsTemplate");
            String templateName = extractStringFromJson(requestBody, "templateName");
            int stageId = extractIntFromJson(requestBody, "stageId");
            int clubId = extractIntFromJson(requestBody, "clubId");

            Users currentUser = getCurrentUser(request);
            if (currentUser == null) {
                sendErrorResponse(response, "User not authenticated", 401);
                return;
            }

            // Lưu template nếu được yêu cầu
            int templateId = 0;
            if (saveAsTemplate && templateName != null && !templateName.trim().isEmpty()) {
                NotificationTemplate template = new NotificationTemplate();
                template.setClubID(clubId);
                template.setTemplateName(templateName);
                template.setTitle(title);
                template.setContent(content);
                template.setCreatedBy(currentUser.getUserID());
                template.setReusable(true);

                templateId = notificationService.createTemplate(template);
                logger.log(Level.INFO, "Template created with ID: {0}", templateId);
            }

            // Lấy danh sách người nhận từ JSON
            String recipientsJson = extractJsonArrayFromJson(requestBody, "recipients");
            List<RecipientInfo> recipients = parseRecipients(recipientsJson);

            // Gửi thông báo đến từng người nhận
            int sentCount = 0;
            for (RecipientInfo recipient : recipients) {
                try {
                    // Lấy thông tin application
                    ClubApplication application = clubApplicationDAO.getApplicationById(recipient.applicationId);
                    logger.log(Level.INFO, "Processing applicationId={0}, found: {1}",
                            new Object[]{recipient.applicationId, (application != null)});

                    if (application != null && application.getUserId() != null) {
                        String personalizedTitle = replaceVariables(title, application, stageId);
                        String personalizedContent = replaceVariables(content, application, stageId);

                        NotificationDAO.sentToPerson(currentUser.getUserID(), application.getUserId(),
                                personalizedTitle, personalizedContent);
                        sentCount++;
                    } else {
                        logger.log(Level.WARNING, "Skipping invalid application or missing userId: {0}", recipient.applicationId);
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error sending notification to recipient: " + recipient.userId, e);
                }
            }

            // Gửi response thành công
            PrintWriter out = response.getWriter();
            out.print("{\"success\": true, \"message\": \"Đã gửi thông báo thành công tới " + sentCount
                    + " ứng viên\", \"sentCount\": " + sentCount + "}");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending bulk notification", e);
            sendErrorResponse(response, "Error: " + e.getMessage(), 500);
        }
    }

    /**
     * Parse danh sách người nhận từ JSON string
     */
    private List<RecipientInfo> parseRecipients(String recipientsJson) {
        List<RecipientInfo> recipients = new ArrayList<>();

        try {
            // Log dữ liệu JSON đầu vào để debug
            logger.log(Level.INFO, "Parsing recipients JSON: {0}", recipientsJson);

            String pattern = "\\{[^\\{\\}]*\\}";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(recipientsJson);

            int count = 0;
            while (m.find()) {
                count++;
                String recipientJson = m.group();
                logger.log(Level.INFO, "Found recipient JSON #{0}: {1}", new Object[]{count, recipientJson});

                String userIdPattern = "\"userId\"\\s*:\\s*\"([^\"]*)\"";
                java.util.regex.Pattern userIdP = java.util.regex.Pattern.compile(userIdPattern);
                java.util.regex.Matcher userIdM = userIdP.matcher(recipientJson);
                if (userIdM.find()) {
                    logger.log(Level.INFO, "Direct regex found userId: {0}", userIdM.group(1));
                } else {
                    logger.log(Level.WARNING, "Direct regex could NOT find userId in: {0}", recipientJson);
                }

                int applicationId = extractIntFromJson(recipientJson, "applicationId");
                String userId = extractStringFromJson(recipientJson, "userId");
                int stageId = extractIntFromJson(recipientJson, "stageId");

                if (applicationId > 0) {  // Chỉ thêm nếu có applicationId hợp lệ
                    RecipientInfo info = new RecipientInfo();
                    info.applicationId = applicationId;
                    info.userId = userId;
                    info.stageId = stageId;

                    recipients.add(info);
                    logger.log(Level.INFO, "Added recipient #{0} to list: appId={1}, userId={2}",
                            new Object[]{count, applicationId, userId});
                } else {
                    logger.log(Level.WARNING, "Skipped recipient #{0} due to invalid applicationId: {1}",
                            new Object[]{count, applicationId});
                }
            }

            logger.log(Level.INFO, "Parsed {0} recipients from {1} JSON objects", new Object[]{recipients.size(), count});
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception details: {0}", e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) {
                logger.log(Level.SEVERE, element.toString());
            }
        }

        return recipients;
    }

    /**
     * Lớp để lưu thông tin người nhận
     */
    private static class RecipientInfo {

        int applicationId;
        String userId;
        int stageId;
    }

    /**
     * Helper method để trích xuất JSON array từ JSON string
     */
    private String extractJsonArrayFromJson(String json, String key) {
        // Log dữ liệu đang xử lý
        logger.log(Level.INFO, "Extracting JSON array for key: {0}", key);
        logger.log(Level.FINE, "From JSON: {0}", json);

        String pattern = "\"" + key + "\"\\s*:\\s*(\\[.*?\\])(,|\\})";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            String result = m.group(1);
            logger.log(Level.INFO, "Extracted JSON array: {0}", result);
            return result;
        }

        // Thử pattern khác nếu không tìm thấy
        pattern = "\"" + key + "\"\\s*:\\s*(\\[.*\\])";
        p = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.DOTALL);
        m = p.matcher(json);
        if (m.find()) {
            String result = m.group(1);
            logger.log(Level.INFO, "Extracted JSON array (alternate pattern): {0}", result);
            return result;
        }

        logger.log(Level.WARNING, "No JSON array found for key: {0}", key);
        return "[]";
    }

    /**
     * Thay thế các biến động trong template thông báo
     */
    private String replaceVariables(String template, ClubApplication application, int stageId) {
        String result = template;

        String candidateName = getUserName(application.getUserId());
        if (candidateName == null || candidateName.trim().isEmpty()) {
            candidateName = application.getEmail().split("@")[0]; // Dùng phần trước @ của email
        }

        String stageName = getStageName(stageId);

        String location = getStageLocation(stageId);

        result = result.replace("{Tên ứng viên}", candidateName);
        result = result.replace("{Tên vòng}", stageName);
        result = result.replace("{Địa điểm}", location != null ? location : "Sẽ thông báo sau");
        return result;
    }

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
                    case "APPLICATION":
                        return "Vòng nộp đơn";
                    case "INTERVIEW":
                        return "Vòng phỏng vấn";
                    case "CHALLENGE":
                        return "Vòng thử thách";
                    default:
                        return stageName;
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
        if (str == null) {
            return "";
        }
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
        // Log input for debugging
        logger.log(Level.FINE, "Extracting string for key '{0}' from JSON: {1}", new Object[]{key, json});

        // First pattern: standard JSON string with double quotes
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            String result = m.group(1);
            logger.log(Level.FINE, "Found value for key '{0}': '{1}'", new Object[]{key, result});
            return result;
        }

        // Second pattern: alternative with single quotes
        pattern = "\"" + key + "\"\\s*:\\s*'([^']*)'";
        p = java.util.regex.Pattern.compile(pattern);
        m = p.matcher(json);
        if (m.find()) {
            String result = m.group(1);
            logger.log(Level.FINE, "Found value (alt pattern) for key '{0}': '{1}'", new Object[]{key, result});
            return result;
        }

        logger.log(Level.WARNING, "No string value found for key: {0}", key);
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
