package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import models.RecruitmentCampaign;
import models.RecruitmentStage;
import models.ApplicationStage;
import models.StageNotification;
import models.NotificationTemplate;
import models.Locations;
import models.Users;
import models.UserClub;
import service.RecruitmentService;
import service.NotificationService;
import dal.LocationDAO;
import dal.UserClubDAO;
import dal.ApplicationFormTemplateDAO;
import java.sql.SQLException;

@WebServlet(name = "RecruitmentCampaignServlet", urlPatterns = {"/recruitment/*"})
public class RecruitmentCampaignServlet extends HttpServlet {

    private final RecruitmentService recruitmentService = new RecruitmentService();
    private final NotificationService notificationService = new NotificationService();
    private final LocationDAO locationDAO = new LocationDAO();
    private final ApplicationFormTemplateDAO formTemplateDAO = new ApplicationFormTemplateDAO();
    private final UserClubDAO userClubDAO = new UserClubDAO();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Khai báo clubId ở đây để có thể dùng chung cho tất cả các endpoint
        Integer clubId = null;
        String clubIdParam = request.getParameter("clubId");
        
        // Kiểm tra và chuyển đổi clubId từ request
        if (clubIdParam != null && !clubIdParam.isEmpty()) {
            try {
                clubId = Integer.parseInt(clubIdParam);
                
                // Kiểm tra quyền chủ nhiệm (roleID = 1)
                UserClub userClub = userClubDAO.getUserClubManagementRole(currentUser.getUserID(), clubId);
                
                // Nếu người dùng không phải là chủ nhiệm CLB (roleID = 1), chuyển về trang myclub với thông báo lỗi
                if (userClub == null || userClub.getRoleID() != 1) {
                    response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + 
                        URLEncoder.encode("Bạn không có quyền quản lý chiến dịch tuyển quân trong clb này", StandardCharsets.UTF_8.name()));
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/myclub?error=invalid_club&message=" + 
                    URLEncoder.encode("ID CLB không hợp lệ", StandardCharsets.UTF_8.name()));
                return;
            }
        } else if (pathInfo == null || pathInfo.equals("/")) {
            // Nếu không có clubId trong request khi truy cập trang chính
            response.sendRedirect(request.getContextPath() + "/myclub?error=invalid_club&message=" + 
                URLEncoder.encode("Vui lòng chọn câu lạc bộ để quản lý chiến dịch tuyển quân", StandardCharsets.UTF_8.name()));
            return;
        }
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Hiển thị tất cả chiến dịch tuyển quân cho chủ nhiệm CLB
                List<RecruitmentCampaign> campaigns = recruitmentService.getCampaignsByClub(clubId);
                request.setAttribute("campaigns", campaigns);
                request.getRequestDispatcher("/view/student/chairman/recruitmentActivitiesManagement.jsp").forward(request, response);
            } else if (pathInfo.equals("/create")) {
                // Hiển thị form tạo chiến dịch tuyển quân
                List<Map<String, Object>> publishedTemplates = formTemplateDAO.getPublishedMemberForms();
                List<Locations> locations = locationDAO.getAllLocations();
                
                request.setAttribute("clubId", clubId);
                request.setAttribute("publishedTemplates", publishedTemplates);
                request.setAttribute("locations", locations);
                request.getRequestDispatcher("/view/student/chairman/createRecruitmentCampaign.jsp").forward(request, response);
            } else if (pathInfo.equals("/edit")) {
                // Hiển thị form chỉnh sửa chiến dịch tuyển quân
                int recruitmentId = Integer.parseInt(request.getParameter("id"));
                RecruitmentCampaign campaign = recruitmentService.getCampaignById(recruitmentId);
                List<RecruitmentStage> stages = recruitmentService.getStagesByCampaign(recruitmentId);
                List<Map<String, Object>> publishedTemplates = formTemplateDAO.getPublishedMemberForms();
                List<Locations> locations = locationDAO.getAllLocations();
                
                request.setAttribute("campaign", campaign);
                request.setAttribute("stages", stages);
                request.setAttribute("publishedTemplates", publishedTemplates);
                request.setAttribute("locations", locations);
                request.getRequestDispatcher("/view/student/chairman/editRecruitmentCampaign.jsp").forward(request, response);
            } else if (pathInfo.startsWith("/stage/")) {
                // Hiển thị chi tiết giai đoạn tuyển quân với danh sách ứng viên
                String[] parts = pathInfo.split("/");
                if (parts.length >= 3) {
                    int stageId = Integer.parseInt(parts[2]);
                    RecruitmentStage stage = recruitmentService.getStageById(stageId);
                    List<ApplicationStage> applications = recruitmentService.getApplicationStagesByStage(stageId);
                    List<StageNotification> notifications = notificationService.getNotificationsByStage(stageId);
                    
                    request.setAttribute("stage", stage);
                    request.setAttribute("applications", applications);
                    request.setAttribute("notifications", notifications);
                    request.getRequestDispatcher("/view/student/chairman/recruitmentStageDetail.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else if (pathInfo.equals("/templates")) {
                // Hiển thị quản lý mẫu thông báo
                List<NotificationTemplate> templates = notificationService.getTemplatesByClub(clubId);
                
                request.setAttribute("templates", templates);
                request.setAttribute("clubId", clubId);
                request.getRequestDispatcher("/view/student/chairman/notificationTemplates.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }catch (Exception e) {
            // Xử lý các ngoại lệ chung và chuyển hướng với thông báo lỗi
            response.sendRedirect(request.getContextPath() + "/myclub?error=system_error&message=" + 
                URLEncoder.encode("Lỗi hệ thống: " + e.getMessage(), StandardCharsets.UTF_8.name()));
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();
        
        if (currentUser == null) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "User not authenticated");
            out.print(jsonResponse.toString());
            return;
        }
        
        // Kiểm tra quyền chủ nhiệm khi gửi dữ liệu POST
        Integer clubId = null;
        
        // Lấy clubId từ các tham số khác nhau tùy thuộc vào endpoint
        if (pathInfo.equals("/create")) {
            String clubIdParam = request.getParameter("clubId");
            if (clubIdParam != null && !clubIdParam.isEmpty()) {
                clubId = Integer.parseInt(clubIdParam);
            }
        } else if (pathInfo.equals("/update")) {
            // Lấy clubId trực tiếp từ tham số request
            String clubIdParam = request.getParameter("clubId");
            if (clubIdParam != null && !clubIdParam.isEmpty()) {
                clubId = Integer.parseInt(clubIdParam);
            }
        } else if (pathInfo.equals("/delete")) {
            // Khi xóa, cần lấy clubId từ recruitmentId
            try {
                int recruitmentId = Integer.parseInt(request.getParameter("recruitmentId"));
                RecruitmentCampaign campaign = recruitmentService.getCampaignById(recruitmentId);
                if (campaign != null) {
                    clubId = campaign.getClubID();
                }
            } catch (Exception e) {
                // Xử lý ngoại lệ khi lấy thông tin chiến dịch
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Không thể xác định chiến dịch để kiểm tra quyền: " + e.getMessage());
                out.print(jsonResponse.toString());
                return;
            }
        }
        
        // Nếu có clubId, kiểm tra quyền chủ nhiệm
        if (clubId != null) {
            try {
                UserClub userClub = userClubDAO.getUserClubManagementRole(currentUser.getUserID(), clubId);
                if (userClub == null || userClub.getRoleID() != 1) {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Bạn không có quyền quản lý chiến dịch tuyển quân trong clb này");
                    out.print(jsonResponse.toString());
                    return;
                }
            } catch (Exception e) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Lỗi hệ thống khi kiểm tra quyền: " + e.getMessage());
                out.print(jsonResponse.toString());
                return;
            }
        }
        
        try {
            if (pathInfo.equals("/create")) {
                // Tạo chiến dịch tuyển quân mới
                RecruitmentCampaign campaign = parseRecruitmentCampaign(request, true);
                campaign.setCreatedBy(currentUser.getUserID());
                campaign.setStatus("ONGOING");
                
                int result = recruitmentService.createCampaign(campaign);
                if (result > 0) {
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("recruitmentId", result);
                } else if (result == -1) {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Thời gian bị trùng với chiến dịch khác");
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Không thể tạo chiến dịch");
                }
            } else if (pathInfo.equals("/update")) {
                // Cập nhật chiến dịch tuyển quân
                RecruitmentCampaign campaign = parseRecruitmentCampaign(request, false);
                
                boolean result = recruitmentService.updateCampaign(campaign);
                if (result) {
                    jsonResponse.addProperty("success", true);
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Không thể cập nhật chiến dịch. Vui lòng kiểm tra trùng lịch hoặc chiến dịch đang diễn ra.");
                }
            } else if (pathInfo.equals("/stage/create")) {
                // Tạo giai đoạn tuyển quân mới
                RecruitmentStage stage = parseRecruitmentStage(request, true);
                
                int result = recruitmentService.createStage(stage);
                if (result > 0) {
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("stageId", result);
                } else if (result == -1) {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Thời gian bị trùng với giai đoạn khác");
                } else if (result == -2) {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Thời gian giai đoạn phải nằm trong thời gian của chiến dịch");
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Không thể tạo giai đoạn");
                }
            } else if (pathInfo.equals("/stage/update")) {
                // Cập nhật giai đoạn tuyển quân
                RecruitmentStage stage = parseRecruitmentStage(request, false);
                
                boolean result = recruitmentService.updateStage(stage);
                if (result) {
                    jsonResponse.addProperty("success", true);
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Không thể cập nhật giai đoạn. Vui lòng kiểm tra trùng lịch hoặc thời gian nằm ngoài chiến dịch.");
                }
            } else if (pathInfo.equals("/application/update")) {
                // Cập nhật trạng thái ứng viên
                int applicationStageId = Integer.parseInt(request.getParameter("applicationStageId"));
                String status = request.getParameter("status");
                
                boolean result = recruitmentService.updateApplicationStatus(applicationStageId, status, currentUser.getUserID());
                if (result) {
                    jsonResponse.addProperty("success", true);
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Không thể cập nhật trạng thái ứng viên");
                }
            } else if (pathInfo.equals("/notification/create")) {
                // Tạo thông báo cho giai đoạn
                StageNotification notification = parseStageNotification(request);
                notification.setCreatedBy(currentUser.getUserID());
                
                int result = notificationService.createStageNotification(notification);
                if (result > 0) {
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("notificationId", result);
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Không thể tạo thông báo");
                }
            } else if (pathInfo.equals("/notification/fromTemplate")) {
                // Tạo thông báo từ mẫu
                int stageId = Integer.parseInt(request.getParameter("stageId"));
                int templateId = Integer.parseInt(request.getParameter("templateId"));
                
                // Phân tích dữ liệu động từ request
                Map<String, String> dynamicData = new HashMap<>();
                String[] dataKeys = {"candidate_name", "stage_name", "club_name", "interview_date"};
                for (String key : dataKeys) {
                    String value = request.getParameter(key);
                    if (value != null && !value.trim().isEmpty()) {
                        dynamicData.put(key, value);
                    }
                }
                
                StageNotification notification = notificationService.createNotificationFromTemplate(
                        stageId, templateId, dynamicData, currentUser.getUserID());
                
                if (notification != null) {
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("notificationId", notification.getNotificationID());
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Không thể tạo thông báo từ mẫu");
                }
            } else if (pathInfo.equals("/template/create")) {
                // Tạo mẫu thông báo mới
                NotificationTemplate template = parseNotificationTemplate(request);
                template.setCreatedBy(currentUser.getUserID());
                
                int result = notificationService.createTemplate(template);
                if (result > 0) {
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("templateId", result);
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Không thể tạo mẫu thông báo");
                }
            } else if (pathInfo.equals("/template/update")) {
                // Cập nhật mẫu thông báo
                NotificationTemplate template = parseNotificationTemplate(request);
                
                boolean result = notificationService.updateTemplate(template);
                if (result) {
                    jsonResponse.addProperty("success", true);
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Không thể cập nhật mẫu thông báo");
                }
            } else if (pathInfo.equals("/delete")) {
                // Xóa chiến dịch tuyển quân
                int recruitmentId = Integer.parseInt(request.getParameter("recruitmentId"));
                
                boolean result = recruitmentService.deleteCampaign(recruitmentId);
                if (result) {
                    jsonResponse.addProperty("success", true);
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Không thể xóa chiến dịch. Chiến dịch có thể đang diễn ra hoặc có dữ liệu liên quan.");
                }
            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Đường dẫn yêu cầu không hợp lệ");
            }
        } catch (Exception e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        
        out.print(jsonResponse.toString());
    }
    
    // Các phương thức hỗ trợ để chuyển đổi tham số request thành các đối tượng model
    
    private RecruitmentCampaign parseRecruitmentCampaign(HttpServletRequest request, boolean isNew) throws ParseException {
        RecruitmentCampaign campaign = new RecruitmentCampaign();
        
        if (!isNew) {
            campaign.setRecruitmentID(Integer.parseInt(request.getParameter("recruitmentId")));
        }
        
        campaign.setClubID(Integer.parseInt(request.getParameter("clubId")));
        campaign.setGen(Integer.parseInt(request.getParameter("gen")));
        campaign.setTemplateID(Integer.parseInt(request.getParameter("templateId")));
        campaign.setTitle(request.getParameter("title"));
        campaign.setDescription(request.getParameter("description"));
        campaign.setStartDate(dateFormat.parse(request.getParameter("startDate")));
        campaign.setEndDate(dateFormat.parse(request.getParameter("endDate")));
        
        if (!isNew) {
            campaign.setStatus(request.getParameter("status"));
        }
        
        return campaign;
    }
    
    private RecruitmentStage parseRecruitmentStage(HttpServletRequest request, boolean isNew) throws ParseException {
        RecruitmentStage stage = new RecruitmentStage();
        
        if (!isNew) {
            stage.setStageID(Integer.parseInt(request.getParameter("stageId")));
        }
        
        stage.setRecruitmentID(Integer.parseInt(request.getParameter("recruitmentId")));
        stage.setStageName(request.getParameter("stageName"));
        stage.setDescription(request.getParameter("description"));
        stage.setStartDate(dateFormat.parse(request.getParameter("startDate")));
        stage.setEndDate(dateFormat.parse(request.getParameter("endDate")));
        
        if (isNew) {
            stage.setStatus("UPCOMING");
        } else {
            stage.setStatus(request.getParameter("status"));
        }
        
        String locationIdParam = request.getParameter("locationId");
        if (locationIdParam != null && !locationIdParam.trim().isEmpty()) {
            stage.setLocationID(Integer.parseInt(locationIdParam));
        }
        
        return stage;
    }
    
    private StageNotification parseStageNotification(HttpServletRequest request) {
        StageNotification notification = new StageNotification();
        
        String notificationIdParam = request.getParameter("notificationId");
        if (notificationIdParam != null && !notificationIdParam.trim().isEmpty()) {
            notification.setNotificationID(Integer.parseInt(notificationIdParam));
        }
        
        notification.setStageID(Integer.parseInt(request.getParameter("stageId")));
        notification.setTitle(request.getParameter("title"));
        notification.setContent(request.getParameter("content"));
        
        String templateIdParam = request.getParameter("templateId");
        if (templateIdParam != null && !templateIdParam.trim().isEmpty()) {
            notification.setTemplateID(Integer.parseInt(templateIdParam));
        }
        
        return notification;
    }
    
    private NotificationTemplate parseNotificationTemplate(HttpServletRequest request) {
        NotificationTemplate template = new NotificationTemplate();
        
        String templateIdParam = request.getParameter("templateId");
        if (templateIdParam != null && !templateIdParam.trim().isEmpty()) {
            template.setTemplateID(Integer.parseInt(templateIdParam));
        }
        
        template.setClubID(Integer.parseInt(request.getParameter("clubId")));
        template.setTemplateName(request.getParameter("templateName"));
        template.setTitle(request.getParameter("title"));
        template.setContent(request.getParameter("content"));
        template.setReusable(Boolean.parseBoolean(request.getParameter("isReusable")));
        
        return template;
    }
}
