package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
import models.Users;
import models.UserClub;
import service.RecruitmentService;
import service.NotificationService;
import dal.LocationDAO;
import dal.UserClubDAO;
import dal.ApplicationFormTemplateDAO;

@WebServlet(name = "RecruitmentCampaignServlet", urlPatterns = {"/recruitment/*"})
public class RecruitmentCampaignServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(RecruitmentCampaignServlet.class.getName());
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
        
        // Đảm bảo charset UTF-8 cho request và response
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // Kiểm tra người dùng đã đăng nhập
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        // Kiểm tra null trước khi gọi equals() để tránh NullPointerException
        if (pathInfo == null) {
            // Lấy clubId từ request parameter
            String redirectClubId = request.getParameter("clubId");
            if (redirectClubId == null || redirectClubId.isEmpty()) {
                // Nếu không có clubId, chuyển hướng về myclub với thông báo lỗi
                response.sendRedirect(request.getContextPath() + "/myclub?error=missing_parameter&message=" + 
                    URLEncoder.encode("Thiếu thông tin câu lạc bộ", StandardCharsets.UTF_8.name()));
            } else {
                // Chuyển hướng đến /list với clubId
                response.sendRedirect(request.getContextPath() + "/recruitment/list?clubId=" + redirectClubId);
            }
            return;
        }

        // Xử lý theo đường dẫn
        try {
            // Các đường dẫn hiện có
            if ("/list".equals(pathInfo)) {
                // Lấy clubId từ request parameter
                String clubIdParam = request.getParameter("clubId");
                
                // Kiểm tra và chuyển đổi clubId
                if (clubIdParam == null || clubIdParam.trim().isEmpty()) {
                    // Nếu không có clubId, chuyển hướng về myclub với thông báo lỗi
                    response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + 
                        URLEncoder.encode("Thiếu thông tin câu lạc bộ", StandardCharsets.UTF_8.name()));
                    return;
                }
                
                try {
                    int clubId = Integer.parseInt(clubIdParam);
                    
                    // Kiểm tra quyền truy cập - chỉ cho phép chủ nhiệm CLB (roleId = 1)
                    UserClub userClub = userClubDAO.getUserClubManagementRole(currentUser.getUserID(), clubId);
                    
                    if (userClub == null || userClub.getRoleID() != 1) {
                        // Không có quyền, chuyển hướng về myclub với thông báo lỗi
                        response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + 
                            URLEncoder.encode("Bạn không có quyền quản lý hoạt động tuyển quân trong câu lạc bộ này", StandardCharsets.UTF_8.name()));
                        return;
                    }
                    
                    // Có quyền, tiếp tục hiển thị danh sách hoạt động
                    // Lấy tất cả chiến dịch và lọc theo clubId
                    List<RecruitmentCampaign> allCampaigns = recruitmentService.getAllCampaigns();
                    List<RecruitmentCampaign> campaigns = allCampaigns.stream()
                            .filter(c -> c.getClubID() == clubId)
                            .collect(Collectors.toList());
                    
                    request.setAttribute("campaigns", campaigns);
                    request.setAttribute("clubId", clubId);
                    request.getRequestDispatcher("/view/student/chairman/recruitmentActivitiesManagement.jsp").forward(request, response);
                    
                } catch (NumberFormatException e) {
                    // Xử lý lỗi clubId không hợp lệ
                    response.sendRedirect(request.getContextPath() + "/myclub?error=invalid_parameter&message=" + 
                        URLEncoder.encode("ID câu lạc bộ không hợp lệ", StandardCharsets.UTF_8.name()));
                    return;
                } catch (Exception e) {
                    // Xử lý các lỗi khác
                    logger.log(Level.SEVERE, "Lỗi khi kiểm tra quyền truy cập: ", e);
                    response.sendRedirect(request.getContextPath() + "/myclub?error=system_error&message=" + 
                        URLEncoder.encode("Lỗi hệ thống: " + e.getMessage(), StandardCharsets.UTF_8.name()));
                    return;
                }
            } else if ("/create".equals(pathInfo)) {
                // Chuyển hướng sang servlet mới để xử lý việc tạo hoạt động tuyển quân
                String clubIdParam = request.getParameter("clubId");
                
                // Nếu không có clubId trong request, thử tìm từ session
                if ((clubIdParam == null || clubIdParam.isEmpty()) && session.getAttribute("userManagementRole") != null) {
                    try {
                        UserClub userRole = (UserClub) session.getAttribute("userManagementRole");
                        clubIdParam = String.valueOf(userRole.getClubID());
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Không thể lấy clubId từ session: {0}", e.getMessage());
                    }
                }
                
                String redirectUrl = request.getContextPath() + "/recruitmentForm/new";
                
                if (clubIdParam != null && !clubIdParam.isEmpty()) {
                    redirectUrl += "?clubId=" + clubIdParam;
                }
                
                // Log để debug
                logger.log(Level.INFO, "Chuyển hướng tạo mới từ /recruitment/create sang {0} với clubId={1}", 
                          new Object[]{redirectUrl, clubIdParam});
                
                response.sendRedirect(redirectUrl);
                return;
            } else if ("/view".equals(pathInfo)) {
                // Chuyển hướng đến servlet mới để xử lý việc xem chi tiết hoạt động tuyển quân
                String idParam = request.getParameter("id");
                
                if (idParam == null || idParam.trim().isEmpty()) {
                    // Nếu không có id, chuyển hướng về myclub với thông báo lỗi
                    response.sendRedirect(request.getContextPath() + "/myclub?error=missing_parameter&message=" + 
                        URLEncoder.encode("Thiếu ID hoạt động tuyển quân", StandardCharsets.UTF_8.name()));
                    return;
                }
                
                String redirectUrl = request.getContextPath() + "/recruitment/recruitmentDetail?recruitmentId=" + idParam;
                
                // Log để debug luồng chuyển hướng
                logger.log(Level.INFO, "Chuyển hướng từ /recruitment/view?id={0} sang {1}", 
                        new Object[]{idParam, redirectUrl});
                
                response.sendRedirect(redirectUrl);
                return;
            } else if ("/form".equals(pathInfo)) {
                // Chuyển hướng sang servlet mới để xử lý việc tạo/chỉnh sửa hoạt động tuyển quân
                String redirectUrl = request.getContextPath() + "/recruitmentForm";
                String idParam = request.getParameter("id");
                String clubIdParam = request.getParameter("clubId");
                
                logger.log(Level.INFO, "[DEBUG] Chuyển hướng sang RecruitmentFormServlet với tham số: id={0}, clubId={1}", 
                          new Object[]{idParam, clubIdParam});
                
                if (idParam != null && !idParam.isEmpty()) {
                    // Chế độ chỉnh sửa
                    redirectUrl += "/edit?id=" + idParam;
                    
                    // Thêm clubId vào URL nếu có
                    if (clubIdParam != null && !clubIdParam.isEmpty()) {
                        redirectUrl += "&clubId=" + clubIdParam;
                        logger.log(Level.INFO, "[DEBUG] Thêm clubId từ param: {0}", clubIdParam);
                    } else {
                        // Nếu không có clubId, cố gắng lấy từ campaign
                        try {
                            RecruitmentCampaign campaign = recruitmentService.getCampaignById(Integer.parseInt(idParam));
                            if (campaign != null) {
                                clubIdParam = String.valueOf(campaign.getClubID());
                                redirectUrl += "&clubId=" + clubIdParam;
                                logger.log(Level.INFO, "[DEBUG] Thêm clubId={0} từ campaign vào URL chỉnh sửa", clubIdParam);
                            } else {
                                logger.log(Level.SEVERE, "[DEBUG] CAMPAIGN KHÔNG TỒN TẠI HOẶC NULL: {0}", idParam);
                            }
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "[DEBUG] Không thể lấy clubId từ campaign để thêm vào URL: {0}", e.getMessage());
                        }
                    }
                    
                    // Log để debug luồng chuyển hướng
                    logger.log(Level.INFO, "[DEBUG] LUỒNG CHUYỂN HƯỚNG: /recruitment/form?id={0}&clubId={1} -> {2} (RecruitmentFormServlet)", 
                              new Object[]{idParam, clubIdParam != null ? clubIdParam : "null", redirectUrl});
                } else {
                    // Chế độ tạo mới
                    redirectUrl += "/new";
                    if (clubIdParam != null && !clubIdParam.isEmpty()) {
                        redirectUrl += "?clubId=" + clubIdParam;
                    }
                    
                    // Log để debug luồng chuyển hướng
                    logger.log(Level.INFO, "[DEBUG] LUỒNG CHUYỂN HƯỚNG: /recruitment/form{0} -> {1} (RecruitmentFormServlet)", 
                              new Object[]{clubIdParam != null ? "?clubId=" + clubIdParam : "", redirectUrl});
                }
                
                response.sendRedirect(redirectUrl);
                return;
            } else if ("/edit".equals(pathInfo)) {
                // Chuyển hướng sang endpoint form với id
                String id = request.getParameter("id");
                String clubIdParam = request.getParameter("clubId");
                
                if (id != null && !id.isEmpty()) {
                    // Nếu không có clubId, cần lấy nó từ bản ghi chiến dịch
                    if (clubIdParam == null || clubIdParam.isEmpty()) {
                        try {
                            RecruitmentCampaign campaign = recruitmentService.getCampaignById(Integer.parseInt(id));
                            if (campaign != null) {
                                clubIdParam = String.valueOf(campaign.getClubID());
                                logger.log(Level.INFO, "[DEBUG] Đã lấy clubId={0} từ campaign ID={1}", 
                                          new Object[]{clubIdParam, id});
                            }
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "[DEBUG] Không thể lấy clubId từ campaign: {0}", e.getMessage());
                        }
                    }
                    
                    String redirectUrl = request.getContextPath() + "/recruitment/form/edit?recruitmentId=" + id;
                    if (clubIdParam != null && !clubIdParam.isEmpty()) {
                        redirectUrl += "&clubId=" + clubIdParam;
                    }
                    
                    // Log để debug luồng chuyển hướng
                    logger.log(Level.INFO, "[DEBUG] LUỒNG CHUYỂN HƯỚNG: /recruitment/edit?id={0} -> {1} -> /recruitmentForm/edit?recruitmentId={0}&clubId={2}", 
                              new Object[]{id, redirectUrl, clubIdParam != null ? clubIdParam : "null"});
                    
                    response.sendRedirect(redirectUrl);
                } else {
                    logger.log(Level.WARNING, "[DEBUG] LUỒNG CHUYỂN HƯỚNG: /recruitment/edit -> /recruitment (thiếu ID)");
                    response.sendRedirect(request.getContextPath() + "/recruitment");
                }
                return;
            } else if (pathInfo.startsWith("/stage/")) {
                // Hiển thị chi tiết giai đoạn tuyển quân với danh sách ứng viên
                String[] parts = pathInfo.split("/");
                if (parts.length >= 3) {
                    try {
                        int stageId = Integer.parseInt(parts[2]);
                        RecruitmentStage stage = recruitmentService.getStageById(stageId);
                        
                        if (stage == null) {
                            // Giai đoạn không tồn tại
                            response.sendRedirect(request.getContextPath() + "/myclub?error=not_found&message=" + 
                                URLEncoder.encode("Không tìm thấy giai đoạn tuyển quân", StandardCharsets.UTF_8.name()));
                            return;
                        }
                        
                        // Lấy thông tin chiến dịch từ giai đoạn
                        RecruitmentCampaign campaign = recruitmentService.getCampaignById(stage.getRecruitmentID());
                        
                        if (campaign == null) {
                            // Hoạt động không tồn tại
                            response.sendRedirect(request.getContextPath() + "/myclub?error=not_found&message=" + 
                                URLEncoder.encode("Không tìm thấy hoạt động tuyển quân liên quan", StandardCharsets.UTF_8.name()));
                            return;
                        }
                        
                        // Kiểm tra quyền truy cập - chỉ cho phép chủ nhiệm CLB (roleId = 1)
                        UserClub userClub = userClubDAO.getUserClubManagementRole(currentUser.getUserID(), campaign.getClubID());
                        
                        if (userClub == null || userClub.getRoleID() != 1) {
                            // Không có quyền, chuyển hướng về myclub với thông báo lỗi
                            response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + 
                                URLEncoder.encode("Bạn không có quyền xem giai đoạn tuyển quân này", StandardCharsets.UTF_8.name()));
                            return;
                        }
                        
                        // Có quyền, lấy dữ liệu và hiển thị
                        List<ApplicationStage> applications = recruitmentService.getApplicationStagesByStage(stageId);
                        List<StageNotification> notifications = notificationService.getNotificationsByStage(stageId);
                        
                        request.setAttribute("stage", stage);
                        request.setAttribute("campaign", campaign);
                        request.setAttribute("applications", applications);
                        request.setAttribute("notifications", notifications);
                        request.getRequestDispatcher("/view/student/chairman/recruitmentStageDetail.jsp").forward(request, response);
                    } catch (NumberFormatException e) {
                        // Xử lý lỗi id không hợp lệ
                        response.sendRedirect(request.getContextPath() + "/myclub?error=invalid_parameter&message=" + 
                            URLEncoder.encode("ID giai đoạn tuyển quân không hợp lệ", StandardCharsets.UTF_8.name()));
                        return;
                    } catch (Exception e) {
                        // Xử lý các lỗi khác
                        logger.log(Level.SEVERE, "Lỗi khi hiển thị chi tiết giai đoạn tuyển quân: ", e);
                        response.sendRedirect(request.getContextPath() + "/myclub?error=system_error&message=" + 
                            URLEncoder.encode("Lỗi hệ thống: " + e.getMessage(), StandardCharsets.UTF_8.name()));
                        return;
                    }
                } else {
                    response.sendRedirect(request.getContextPath() + "/myclub?error=invalid_request&message=" + 
                        URLEncoder.encode("Yêu cầu không hợp lệ", StandardCharsets.UTF_8.name()));
                }
            } else if ("/templates".equals(pathInfo)) {
                // Lấy clubId từ request parameter
                String clubIdParam = request.getParameter("clubId");
                
                // Kiểm tra và chuyển đổi clubId
                if (clubIdParam == null || clubIdParam.trim().isEmpty()) {
                    // Nếu không có clubId, chuyển hướng về myclub với thông báo lỗi
                    response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + 
                        URLEncoder.encode("Thiếu thông tin câu lạc bộ", StandardCharsets.UTF_8.name()));
                    return;
                }
                
                try {
                    int clubId = Integer.parseInt(clubIdParam);
                    
                    // Kiểm tra quyền truy cập - chỉ cho phép chủ nhiệm CLB (roleId = 1)
                    UserClub userClub = userClubDAO.getUserClubManagementRole(currentUser.getUserID(), clubId);
                    
                    if (userClub == null || userClub.getRoleID() != 1) {
                        // Không có quyền, chuyển hướng về myclub với thông báo lỗi
                        response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + 
                            URLEncoder.encode("Bạn không có quyền quản lý mẫu thông báo trong câu lạc bộ này", StandardCharsets.UTF_8.name()));
                        return;
                    }
                    
                    // Có quyền, lấy dữ liệu và hiển thị
                    List<NotificationTemplate> templates = notificationService.getTemplatesByClub(clubId);
                    
                    request.setAttribute("templates", templates);
                    request.setAttribute("clubId", clubId);
                    request.getRequestDispatcher("/view/student/chairman/notificationTemplates.jsp").forward(request, response);
                } catch (NumberFormatException e) {
                    // Xử lý lỗi clubId không hợp lệ
                    response.sendRedirect(request.getContextPath() + "/myclub?error=invalid_parameter&message=" + 
                        URLEncoder.encode("ID câu lạc bộ không hợp lệ", StandardCharsets.UTF_8.name()));
                    return;
                } catch (Exception e) {
                    // Xử lý các lỗi khác
                    logger.log(Level.SEVERE, "Lỗi khi hiển thị quản lý mẫu thông báo: ", e);
                    response.sendRedirect(request.getContextPath() + "/myclub?error=system_error&message=" + 
                        URLEncoder.encode("Lỗi hệ thống: " + e.getMessage(), StandardCharsets.UTF_8.name()));
                    return;
                }
            } else if ("/stages".equals(pathInfo)) {
                // API endpoint để lấy danh sách các vòng tuyển theo recruitmentId
                String recruitmentIdParam = request.getParameter("recruitmentId");
                if (recruitmentIdParam == null || recruitmentIdParam.trim().isEmpty()) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"success\":false,\"message\":\"Thiếu recruitmentId\"}");
                    return;
                }
                
                try {
                    int recruitmentId = Integer.parseInt(recruitmentIdParam);
                    RecruitmentCampaign campaign = recruitmentService.getCampaignById(recruitmentId);
                    
                    if (campaign == null) {
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write("{\"success\":false,\"message\":\"Không tìm thấy chiến dịch\"}");
                        return;
                    }
                    
                    // Kiểm tra quyền truy cập
                    UserClub userClub = userClubDAO.getUserClubManagementRole(currentUser.getUserID(), campaign.getClubID());
                    if (userClub == null || userClub.getRoleID() != 1) {
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write("{\"success\":false,\"message\":\"Bạn không có quyền truy cập dữ liệu này\"}");
                        return;
                    }
                    
                    // Lấy danh sách các vòng tuyển
                    List<RecruitmentStage> stages = recruitmentService.getStagesByCampaign(recruitmentId);
                    
                    // DEBUG: Log chi tiết về từng vòng tuyển
                    logger.log(Level.INFO, "API /stages - Đã tìm thấy {0} vòng tuyển cho chiến dịch ID {1}", 
                              new Object[]{stages.size(), recruitmentId});
                    
                    for (RecruitmentStage stage : stages) {
                        logger.log(Level.INFO, "API /stages - Vòng tuyển #{0}: {1}, Thời gian: {2} -> {3}, Vị trí: {4}, Mô tả: {5}, Trạng thái: {6}", 
                                  new Object[]{
                                      stage.getStageID(), 
                                      stage.getStageName(), 
                                      stage.getStartDate(), 
                                      stage.getEndDate(), 
                                      stage.getLocationID() > 0 ? "ID: " + stage.getLocationID() : "Không có",
                                      stage.getDescription() != null && !stage.getDescription().isEmpty() ? "Có" : "Không",
                                      stage.getStatus()
                                  });
                    }
                    
                    // Chuyển đổi danh sách thành JSON và trả về
                    String stagesJson = gson.toJson(stages);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"success\":true,\"stages\":" + stagesJson + "}");
                    
                    // Log JSON trả về để debug
                    logger.log(Level.INFO, "API /stages - JSON trả về: {0}", stagesJson.substring(0, Math.min(stagesJson.length(), 300)) + 
                              (stagesJson.length() > 300 ? "... (còn nữa)" : ""));
                    
                    // Log full JSON để debug chi tiết (có thể bật/tắt khi cần)
                    logger.log(Level.FINE, "API /stages - Full JSON: {0}", stagesJson);
                    
                } catch (NumberFormatException e) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"success\":false,\"message\":\"ID chiến dịch không hợp lệ\"}");
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Lỗi khi lấy danh sách vòng tuyển: ", e);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"success\":false,\"message\":\"Lỗi hệ thống: " + e.getMessage() + "\"}");
                }
                return;
            } else if ("/active-campaigns".equals(pathInfo)) {
                // API trả về danh sách các hoạt động đang diễn ra
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                
                try {
                    List<RecruitmentCampaign> activeCampaigns = recruitmentService.getActiveCampaigns();
                    
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("success", true);
                    
                    com.google.gson.JsonArray campaignsArray = new com.google.gson.JsonArray();
                    for (RecruitmentCampaign campaign : activeCampaigns) {
                        JsonObject campaignJson = new JsonObject();
                        campaignJson.addProperty("recruitmentId", campaign.getRecruitmentID());
                        campaignJson.addProperty("clubId", campaign.getClubID());
                        campaignJson.addProperty("title", campaign.getTitle());
                        campaignJson.addProperty("description", campaign.getDescription());
                        campaignJson.addProperty("status", campaign.getStatus());
                        campaignJson.addProperty("startDate", campaign.getStartDate().toString());
                        campaignJson.addProperty("endDate", campaign.getEndDate().toString());
                        campaignJson.addProperty("templateId", campaign.getTemplateID());
                        campaignsArray.add(campaignJson);
                    }
                    
                    jsonResponse.add("campaigns", campaignsArray);
                    out.print(jsonResponse.toString());
                } catch (Exception e) {
                    JsonObject errorResponse = new JsonObject();
                    errorResponse.addProperty("success", false);
                    errorResponse.addProperty("message", "Lỗi khi lấy danh sách hoạt động tuyển quân: " + e.getMessage());
                    out.print(errorResponse.toString());
                    logger.log(Level.SEVERE, "Lỗi khi lấy danh sách hoạt động tuyển quân", e);
                }
                return;
            } else if ("/club-campaigns".equals(pathInfo)) {
                // API trả về danh sách hoạt động tuyển quân của một câu lạc bộ
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                
                String clubIdParam = request.getParameter("clubId");
                if (clubIdParam == null || clubIdParam.isEmpty()) {
                    JsonObject errorResponse = new JsonObject();
                    errorResponse.addProperty("success", false);
                    errorResponse.addProperty("message", "Thiếu tham số clubId");
                    out.print(errorResponse.toString());
                    return;
                }
                
                try {
                    int clubId = Integer.parseInt(clubIdParam);
                    List<RecruitmentCampaign> clubCampaigns = recruitmentService.getCampaignsByClub(clubId);
                    
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("success", true);
                    
                    com.google.gson.JsonArray campaignsArray = new com.google.gson.JsonArray();
                    for (RecruitmentCampaign campaign : clubCampaigns) {
                        JsonObject campaignJson = new JsonObject();
                        campaignJson.addProperty("recruitmentId", campaign.getRecruitmentID());
                        campaignJson.addProperty("clubId", campaign.getClubID());
                        campaignJson.addProperty("title", campaign.getTitle());
                        campaignJson.addProperty("description", campaign.getDescription());
                        campaignJson.addProperty("status", campaign.getStatus());
                        campaignJson.addProperty("startDate", campaign.getStartDate().toString());
                        campaignJson.addProperty("endDate", campaign.getEndDate().toString());
                        campaignJson.addProperty("templateId", campaign.getTemplateID());
                        campaignsArray.add(campaignJson);
                    }
                    
                    jsonResponse.add("campaigns", campaignsArray);
                    out.print(jsonResponse.toString());
                } catch (NumberFormatException e) {
                    JsonObject errorResponse = new JsonObject();
                    errorResponse.addProperty("success", false);
                    errorResponse.addProperty("message", "Tham số clubId không hợp lệ");
                    out.print(errorResponse.toString());
                } catch (Exception e) {
                    JsonObject errorResponse = new JsonObject();
                    errorResponse.addProperty("success", false);
                    errorResponse.addProperty("message", "Lỗi khi lấy danh sách hoạt động tuyển quân: " + e.getMessage());
                    out.print(errorResponse.toString());
                    logger.log(Level.SEVERE, "Lỗi khi lấy danh sách hoạt động tuyển quân của câu lạc bộ", e);
                }
                return;
            } else if ("/checkExpired".equals(pathInfo)) {
                // API endpoint to check and close expired recruitment campaigns
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                
                try {
                    // Get all active campaigns
                    List<RecruitmentCampaign> activeCampaigns = recruitmentService.getActiveCampaigns();
                    
                    // Current date for comparison
                    java.util.Date currentDate = new java.util.Date();
                    
                    // Count of campaigns closed in this check
                    int closedCount = 0;
                    JsonObject jsonResponse = new JsonObject();
                    com.google.gson.JsonArray closedCampaignsArray = new com.google.gson.JsonArray();
                    
                    // Loop through active campaigns and check if any have passed their end date
                    for (RecruitmentCampaign campaign : activeCampaigns) {
                        java.util.Date endDate = campaign.getEndDate();
                        
                        // If campaign's end date has passed, close it
                        if (endDate != null && endDate.before(currentDate)) {
                            // Update campaign status to CLOSED
                            campaign.setStatus("CLOSED");
                            
                            // Update the campaign
                            boolean updateSuccess = recruitmentService.updateCampaign(campaign);
                            
                            if (updateSuccess) {
                                // Update the club's recruiting status to false
                                dal.ClubDAO clubDAO = new dal.ClubDAO();
                                boolean clubUpdated = clubDAO.updateIsRecruiting(campaign.getClubID(), false);
                                
                                // Add to closed campaigns list
                                JsonObject closedCampaign = new JsonObject();
                                closedCampaign.addProperty("recruitmentId", campaign.getRecruitmentID());
                                closedCampaign.addProperty("clubId", campaign.getClubID());
                                closedCampaign.addProperty("title", campaign.getTitle());
                                closedCampaign.addProperty("clubUpdated", clubUpdated);
                                closedCampaignsArray.add(closedCampaign);
                                
                                closedCount++;
                                logger.log(Level.INFO, 
                                    "Auto-closed campaign ID {0} for club ID {1}. IsRecruiting updated: {2}", 
                                    new Object[]{campaign.getRecruitmentID(), campaign.getClubID(), clubUpdated});
                            }
                        }
                    }
                    
                    // Build response
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", closedCount + " expired campaigns automatically closed");
                    jsonResponse.addProperty("closedCount", closedCount);
                    jsonResponse.add("closedCampaigns", closedCampaignsArray);
                    
                    out.print(jsonResponse.toString());
                } catch (Exception e) {
                    JsonObject errorResponse = new JsonObject();
                    errorResponse.addProperty("success", false);
                    errorResponse.addProperty("message", "Error checking expired campaigns: " + e.getMessage());
                    out.print(errorResponse.toString());
                    logger.log(Level.SEVERE, "Error checking expired campaigns", e);
                }
                return;
            } else { // Endpoint "/form" đã được xử lý ở trên
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            // Xử lý các ngoại lệ từ phương thức parse
            logger.log(Level.WARNING, "Dữ liệu không hợp lệ: {0}", e.getMessage());
            
            // Tạo thông báo lỗi rõ ràng bằng tiếng Việt cho người dùng
            String errorMessage = e.getMessage() != null ? encodeMessage(e.getMessage()) : "Dữ liệu không hợp lệ";
            
            // Chuyển hướng với thông báo lỗi đã được mã hóa UTF-8
            try {
                response.sendRedirect(request.getContextPath() + "/myclub?error=validation_error&message=" + 
                    URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException ex) {
                // UTF-8 luôn được hỗ trợ nhưng vẫn cần xử lý ngoại lệ
                response.sendRedirect(request.getContextPath() + "/myclub?error=validation_error");
            }
        } catch (Exception e) {
            // Xử lý các ngoại lệ chung và chuyển hướng với thông báo lỗi
            logger.log(Level.SEVERE, "Lỗi xử lý request: " + pathInfo, e);
            
            // Tạo thông báo lỗi rõ ràng bằng tiếng Việt cho người dùng
            String errorMessage = "Lỗi hệ thống";
            if (e.getMessage() != null) {
                errorMessage += ": " + e.getMessage().replace("\n", " ").replace("\r", "");
            }
            
            // Ghi log chi tiết về vị trí lỗi
            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace.length > 0) {
                String errorLocation = stackTrace[0].toString();
                logger.log(Level.SEVERE, "Chi tiết lỗi: {0}", errorLocation);
            }
            
            e.printStackTrace(); // In chi tiết stack trace để debug
            
            // Chuyển hướng với thông báo lỗi đã được mã hóa UTF-8
            try {
                response.sendRedirect(request.getContextPath() + "/myclub?error=system_error&message=" + 
                    URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException ex) {
                // UTF-8 luôn được hỗ trợ nhưng vẫn cần xử lý ngoại lệ
                response.sendRedirect(request.getContextPath() + "/myclub?error=system_error");
            }
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Đảm bảo charset UTF-8 cho request và response
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        // Xử lý trường hợp pathInfo null
        // Sử dụng cú pháp "/xyz".equals(pathInfo) thay vì pathInfo.equals("/xyz") để tránh NullPointerException
        if (pathInfo == null) {
            pathInfo = ""; // Đặt giá trị mặc định để tránh NullPointerException
        }
        
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
        if ("/create".equals(pathInfo)) {
            String clubIdParam = request.getParameter("clubId");
            if (clubIdParam != null && !clubIdParam.isEmpty()) {
                clubId = Integer.parseInt(clubIdParam);
            }
        } else if ("/update".equals(pathInfo)) {
            // Lấy clubId trực tiếp từ tham số request
            String clubIdParam = request.getParameter("clubId");
            if (clubIdParam != null && !clubIdParam.isEmpty()) {
                clubId = Integer.parseInt(clubIdParam);
            }
        } else if ("/delete".equals(pathInfo)) {
            // Khi xóa, cần lấy clubId từ recruitmentId
            try {
                int recruitmentId = Integer.parseInt(request.getParameter("recruitmentId"));
                RecruitmentCampaign campaign = recruitmentService.getCampaignById(recruitmentId);
                if (campaign != null) {
                    clubId = campaign.getClubID();
                }
            } catch (Exception e) {
                // Xử lý ngoại lệ khi lấy thông tin hoạt động
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Không thể xác định hoạt động để kiểm tra quyền: " + e.getMessage());
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
                    jsonResponse.addProperty("message", "Bạn không có quyền quản lý hoạt động tuyển quân trong clb này");
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
            if ("/create".equals(pathInfo)) {
                // Chuyển hướng POST request cho việc tạo hoạt động tuyển quân sang servlet mới
                String redirectUrl = request.getContextPath() + "/recruitmentForm/create";
                
                // Log để debug
                logger.log(Level.INFO, "Chuyển hướng request tạo mới từ /recruitment/create sang {0}", redirectUrl);
                
                response.setContentType("application/json");
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("redirect", redirectUrl);
                jsonResponse.addProperty("message", "API đã chuyển sang đường dẫn mới. Vui lòng sử dụng " + redirectUrl);
                out.print(jsonResponse.toString());
                return;
            } else if ("/update".equals(pathInfo)) {
                // Chuyển hướng POST request cho việc cập nhật hoạt động tuyển quân sang servlet mới
                String redirectUrl = request.getContextPath() + "/recruitmentForm/update";
                
                // Log để debug
                logger.log(Level.INFO, "Chuyển hướng request cập nhật từ /recruitment/update sang {0}", redirectUrl);
                
                response.setContentType("application/json");
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("redirect", redirectUrl);
                jsonResponse.addProperty("message", "API đã chuyển sang đường dẫn mới. Vui lòng sử dụng " + redirectUrl);
                out.print(jsonResponse.toString());
                return;
            } else if ("/stage/create".equals(pathInfo)) {
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
                    jsonResponse.addProperty("message", "Thời gian giai đoạn phải nằm trong thời gian của hoạt động");
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Không thể tạo giai đoạn");
                }
            } else if ("/stage/update".equals(pathInfo)) {
                // Cập nhật giai đoạn tuyển quân
                RecruitmentStage stage = parseRecruitmentStage(request, false);
                
                boolean result = recruitmentService.updateStage(stage);
                if (result) {
                    jsonResponse.addProperty("success", true);
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Không thể cập nhật giai đoạn. Vui lòng kiểm tra trùng lịch hoặc thời gian nằm ngoài hoạt động.");
                }
            } else if ("/application/update".equals(pathInfo)) {
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
            } else if ("/notification/create".equals(pathInfo)) {
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
            } else if ("/notification/fromTemplate".equals(pathInfo)) {
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
            } else if ("/template/create".equals(pathInfo)) {
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
            } else if ("/template/update".equals(pathInfo)) {
                // Cập nhật mẫu thông báo
                NotificationTemplate template = parseNotificationTemplate(request);
                
                boolean result = notificationService.updateTemplate(template);
                if (result) {
                    jsonResponse.addProperty("success", true);
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Không thể cập nhật mẫu thông báo");
                }
            } else if ("/delete".equals(pathInfo)) {
                // Xóa hoạt động tuyển quân
                int recruitmentId = Integer.parseInt(request.getParameter("recruitmentId"));
                
                boolean result = recruitmentService.deleteCampaign(recruitmentId);
                if (result) {
                    jsonResponse.addProperty("success", true);
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Không thể xóa hoạt động. Chiến dịch có thể đang diễn ra hoặc có dữ liệu liên quan.");
                }
            } else if ("/close".equals(pathInfo)) {
                // Kết thúc hoạt động tuyển quân
                String recruitmentIdParam = request.getParameter("recruitmentId");
                if (recruitmentIdParam == null || recruitmentIdParam.isEmpty()) {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Thiếu ID hoạt động tuyển quân");
                    logger.log(Level.WARNING, "ERROR: Missing recruitmentId parameter in close request");
                    return;
                }
                
                try {
                    int recruitmentId = Integer.parseInt(recruitmentIdParam);
                    logger.log(Level.INFO, "Processing close request for recruitmentId: {0}", recruitmentId);
                    
                    // Lấy thông tin hoạt động hiện tại
                    RecruitmentCampaign campaign = recruitmentService.getCampaignById(recruitmentId);
                    
                    if (campaign != null) {
                        // Cập nhật trạng thái thành CLOSED
                        campaign.setStatus("CLOSED");
                        
                        boolean result = recruitmentService.updateCampaign(campaign);
                        if (result) {
                            jsonResponse.addProperty("success", true);
                            jsonResponse.addProperty("message", "Đã kết thúc hoạt động thành công");
                            logger.log(Level.INFO, "Campaign {0} successfully closed", recruitmentId);
                            
                            // Cập nhật trạng thái tuyển quân của câu lạc bộ
                            dal.ClubDAO clubDAO = new dal.ClubDAO();
                            boolean isUpdated = clubDAO.updateIsRecruiting(campaign.getClubID(), false);
                            logger.log(Level.INFO, "Cập nhật IsRecruitting=false cho CLB ID {0}: {1}", 
                                      new Object[]{campaign.getClubID(), isUpdated ? "thành công" : "thất bại"});
                        } else {
                            jsonResponse.addProperty("success", false);
                            jsonResponse.addProperty("message", "Không thể kết thúc hoạt động. Vui lòng thử lại sau.");
                            logger.log(Level.WARNING, "Failed to close campaign {0}", recruitmentId);
                        }
                    } else {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Không tìm thấy hoạt động tuyển quân.");
                        logger.log(Level.WARNING, "Campaign not found: {0}", recruitmentId);
                    }
                } catch (NumberFormatException e) {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "ID hoạt động tuyển quân không hợp lệ");
                    logger.log(Level.WARNING, "ERROR: Invalid recruitmentId parameter: {0}", recruitmentIdParam);
                }
            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Đường dẫn yêu cầu không hợp lệ");
            }
        } catch (IllegalArgumentException e) {
            jsonResponse.addProperty("success", false);
            
            // Đảm bảo thông báo lỗi người dùng được hiển thị đúng UTF-8
            String errorMessage = e.getMessage() != null ? encodeMessage(e.getMessage()) : "Dữ liệu không hợp lệ";
            jsonResponse.addProperty("message", errorMessage);
            logger.log(Level.WARNING, "Lỗi dữ liệu đầu vào: {0}", errorMessage);
            
        } catch (Exception e) {
            jsonResponse.addProperty("success", false);
            
            // Tạo thông báo lỗi dễ hiểu cho người dùng bằng tiếng Việt
            String errorMessage = "Lỗi hệ thống";
            if (e.getMessage() != null) {
                errorMessage += ": " + e.getMessage().replace("\n", " ").replace("\r", "");
            }
            
            jsonResponse.addProperty("message", errorMessage);
            logger.log(Level.SEVERE, "Lỗi xử lý POST request: " + pathInfo, e);
            e.printStackTrace(); // In chi tiết lỗi để debug
        }
        
        out.print(jsonResponse.toString());
    }
    
    // Các phương thức hỗ trợ để chuyển đổi tham số request thành các đối tượng model
    
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
    
    /**
     * Phương thức này đảm bảo các thông báo lỗi tiếng Việt được hiển thị đúng
     * @param message Thông báo cần xử lý
     * @return Thông báo đã được xử lý để hiển thị đúng UTF-8
     */
    private String encodeMessage(String message) {
        if (message == null) {
            return "Lỗi không xác định";
        }
        
        // Thay thế các thông báo lỗi có thể bị lỗi encoding
        if (message.contains("KhÃ´ng cÃ³ thÃ´ng tin clubId")) {
            return "Không có thông tin clubId";
        } else if (message.contains("Vui lÃ²ng nhÃ¡ÂºÂ­p sá»â")) {
            return "Vui lòng nhập số thế hệ (Gen)";
        } else if (message.contains("Vui lÃ²ng chá»n form ÄÄng kÃ½")) {
            return "Vui lòng chọn form đăng ký";
        } else if (message.contains("Vui lÃ²ng chá»n ngÃ y bá»t Äáº§u")) {
            return "Vui lòng chọn ngày bắt đầu";
        } else if (message.contains("Vui lÃ²ng chá»n ngÃ y káº¿t thÃºc")) {
            return "Vui lòng chọn ngày kết thúc";
        }
        
        return message;
    }
}
