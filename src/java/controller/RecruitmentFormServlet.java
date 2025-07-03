package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import models.Locations;
import models.Users;
import models.UserClub;
import models.ApplicationFormTemplate;
import service.RecruitmentService;
import dal.LocationDAO;
import dal.UserClubDAO;
import dal.ApplicationFormTemplateDAO;
import java.sql.SQLException;

/**
 * Servlet này xử lý việc tạo và chỉnh sửa hoạt động tuyển quân
 * Chỉ chủ nhiệm CLB (roleId = 1) mới có quyền truy cập các chức năng này
 */
@WebServlet(name = "RecruitmentFormServlet", urlPatterns = {"/recruitmentForm/*"})
public class RecruitmentFormServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(RecruitmentFormServlet.class.getName());
    private final RecruitmentService recruitmentService = new RecruitmentService();
    private final LocationDAO locationDAO = new LocationDAO();
    private final ApplicationFormTemplateDAO formTemplateDAO = new ApplicationFormTemplateDAO();
    private final UserClubDAO userClubDAO = new UserClubDAO();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final Gson gson = new Gson();
    
    /**
     * Kiểm tra người dùng có phải là chủ nhiệm của CLB không
     * @param userId ID của người dùng cần kiểm tra
     * @param clubId ID của CLB cần kiểm tra
     * @return true nếu người dùng là chủ nhiệm, ngược lại false
     */
    private boolean isClubChairman(String userId, Integer clubId) throws SQLException {
        if (clubId == null) {
            return false;
        }
        UserClub userClub = userClubDAO.getUserClubManagementRole(userId, clubId);
        return userClub != null && userClub.getRoleID() == 1;
    }
    
    /**
     * Lấy CLB mà người dùng là chủ nhiệm
     * @param userId ID của người dùng
     * @return UserClub nếu người dùng là chủ nhiệm của bất kỳ CLB nào, ngược lại null
     */
    private UserClub getChairmanClub(String userId) throws SQLException {
        UserClub userClub = userClubDAO.getUserClubManagementRole(userId, null);
        if (userClub != null && userClub.getRoleID() == 1) {
            return userClub;
        }
        return null;
    }
    
    /**
     * Gửi chuyển hướng với thông báo lỗi
     * @param request HttpServletRequest hiện tại
     * @param response HttpServletResponse để gửi chuyển hướng
     * @param errorCode Mã lỗi ngắn
     * @param message Thông báo lỗi chi tiết
     * @param redirectPath Đường dẫn chuyển hướng (mặc định là /myclub)
     */
    private void sendErrorRedirect(HttpServletRequest request, HttpServletResponse response, 
                                 String errorCode, String message, String redirectPath) throws IOException {
        if (redirectPath == null) {
            redirectPath = "/myclub";
        }
        
        try {
            response.sendRedirect(request.getContextPath() + redirectPath + "?error=" + errorCode + "&message=" + 
                URLEncoder.encode(message, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            // UTF-8 luôn được hỗ trợ nhưng vẫn cần xử lý ngoại lệ
            response.sendRedirect(request.getContextPath() + redirectPath + "?error=" + errorCode);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Đảm bảo charset UTF-8 cho request và response
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        // Xử lý pathInfo null
        if (pathInfo == null) {
            pathInfo = "/";
        }
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            if ("/new".equals(pathInfo) || "/".equals(pathInfo)) {
                // Xử lý hiển thị form tạo mới
                handleNewForm(request, response, currentUser);
            } else if ("/edit".equals(pathInfo)) {
                // Xử lý hiển thị form chỉnh sửa
                handleEditForm(request, response, currentUser);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi SQL: {0}", e.getMessage());
            sendErrorRedirect(request, response, "database_error", 
                "Lỗi khi truy cập cơ sở dữ liệu: " + e.getMessage(), "/recruitment");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi xử lý request: {0}", e.getMessage());
            sendErrorRedirect(request, response, "system_error", 
                "Lỗi hệ thống: " + e.getMessage(), "/recruitment");
        }
    }

    /**
     * Xử lý hiển thị form tạo mới hoạt động tuyển quân
     */
    private void handleNewForm(HttpServletRequest request, HttpServletResponse response, Users currentUser) 
            throws ServletException, IOException, SQLException {
        
        // Lấy thông tin CLB từ tham số hoặc từ vai trò của người dùng
        String clubIdParam = request.getParameter("clubId");
        Integer clubId = null;
        RecruitmentCampaign campaign = new RecruitmentCampaign();
        
        if (clubIdParam != null && !clubIdParam.isEmpty()) {
            try {
                clubId = Integer.parseInt(clubIdParam);
            } catch (NumberFormatException e) {
                sendErrorRedirect(request, response, "invalid_club", "ID CLB không hợp lệ", "/recruitment");
                return;
            }
            
            // Kiểm tra quyền chủ nhiệm
            if (!isClubChairman(currentUser.getUserID(), clubId)) {
                logger.log(Level.WARNING, "User {0} không phải chủ nhiệm của CLB {1}", 
                          new Object[]{currentUser.getUserID(), clubId});
                sendErrorRedirect(request, response, "unauthorized", 
                    "Chỉ chủ nhiệm CLB mới có quyền tạo hoạt động tuyển quân.", "/recruitment");
                return;
            }
            
            campaign.setClubID(clubId);
        } else {
            // Tìm CLB mà người dùng là chủ nhiệm
            UserClub userClub = getChairmanClub(currentUser.getUserID());
            
            if (userClub == null) {
                logger.log(Level.WARNING, "User {0} không phải chủ nhiệm CLB nào", 
                          new Object[]{currentUser.getUserID()});
                sendErrorRedirect(request, response, "unauthorized", 
                    "Bạn không phải là chủ nhiệm của bất kỳ CLB nào. Chỉ chủ nhiệm CLB mới có quyền tạo hoạt động tuyển quân.", "/recruitment");
                return;
            }
            
            clubId = userClub.getClubID();
            campaign.setClubID(clubId);
            logger.log(Level.INFO, "User {0} tạo chiến dịch với vai trò chủ nhiệm CLB {1}", 
                      new Object[]{currentUser.getUserID(), clubId});
        }
        
        // Lấy danh sách form đăng ký đã publish của CLB
        List<Map<String, Object>> publishedTemplates = formTemplateDAO.getPublishedMemberForms(clubId);
        
        // Debug: Ghi log thông tin về templates đã tìm thấy
        logger.log(Level.INFO, "Tìm thấy {0} templates cho CLB ID: {1}", 
                  new Object[]{publishedTemplates.size(), clubId});
        
        // Nếu không tìm thấy form cho club này, lấy tất cả các form (hiển thị warning)
        if (publishedTemplates.isEmpty()) {
            logger.log(Level.WARNING, "Không tìm thấy form nào cho ClubID={0}, lấy tất cả các form", clubId);
            
            // Lấy tất cả các form đã xuất bản từ tất cả các clubs
            publishedTemplates = formTemplateDAO.getPublishedMemberForms(null);
            
            // Thêm cảnh báo để hiển thị cho người dùng
            request.setAttribute("formWarning", 
                "Không tìm thấy form nào dành riêng cho CLB #" + clubId + ". Hiển thị tất cả form có sẵn.");
        }
        
        // Lấy danh sách địa điểm
        List<Locations> locations = locationDAO.getAllLocations();
        
        // Truyền dữ liệu vào request
        request.setAttribute("campaign", campaign);
        request.setAttribute("stages", null);
        request.setAttribute("publishedTemplates", publishedTemplates);
        request.setAttribute("locations", locations);
        request.setAttribute("mode", "create");
        request.setAttribute("selectedClubId", clubId);
        
        // Chuyển đến trang form
        request.getRequestDispatcher("/view/student/chairman/recruitmentCampaignForm.jsp").forward(request, response);
    }

    /**
     * Xử lý hiển thị form chỉnh sửa hoạt động tuyển quân
     */
    private void handleEditForm(HttpServletRequest request, HttpServletResponse response, Users currentUser) 
            throws ServletException, IOException, SQLException {
        
        // Lấy ID hoạt động tuyển quân từ tham số
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            sendErrorRedirect(request, response, "missing_id", "Thiếu ID hoạt động tuyển quân", "/recruitment");
            return;
        }
        
        int recruitmentId;
        try {
            recruitmentId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            sendErrorRedirect(request, response, "invalid_format", "ID hoạt động không hợp lệ", "/recruitment");
            return;
        }
        
        // Lấy thông tin hoạt động
        RecruitmentCampaign campaign = recruitmentService.getCampaignById(recruitmentId);
        if (campaign == null) {
            sendErrorRedirect(request, response, "not_found", "Không tìm thấy hoạt động tuyển quân", "/recruitment");
            return;
        }
        
        // Kiểm tra quyền chủ nhiệm
        int clubId = campaign.getClubID();
        if (!isClubChairman(currentUser.getUserID(), clubId)) {
            logger.log(Level.WARNING, "User {0} không có quyền chỉnh sửa chiến dịch của CLB {1}", 
                      new Object[]{currentUser.getUserID(), clubId});
            sendErrorRedirect(request, response, "unauthorized", 
                "Bạn không có quyền chỉnh sửa hoạt động tuyển quân. Chỉ chủ nhiệm CLB mới có quyền này.", "/recruitment");
            return;
        }
        
        // Lấy danh sách giai đoạn
        List<RecruitmentStage> stages = recruitmentService.getStagesByCampaign(recruitmentId);
        
        // Lấy danh sách form đăng ký đã publish của CLB
        List<Map<String, Object>> publishedTemplates = formTemplateDAO.getPublishedMemberForms(clubId);
        
        // Nếu không tìm thấy form cho club này, lấy tất cả các form
        if (publishedTemplates.isEmpty()) {
            publishedTemplates = formTemplateDAO.getPublishedMemberForms(null);
            request.setAttribute("formWarning", 
                "Không tìm thấy form nào dành riêng cho CLB #" + clubId + ". Hiển thị tất cả form có sẵn.");
        }
        
        // Lấy danh sách địa điểm
        List<Locations> locations = locationDAO.getAllLocations();
        
        // Truyền dữ liệu vào request
        request.setAttribute("campaign", campaign);
        request.setAttribute("stages", stages);
        request.setAttribute("publishedTemplates", publishedTemplates);
        request.setAttribute("locations", locations);
        request.setAttribute("mode", "edit");
        request.setAttribute("selectedClubId", clubId);
        
        logger.log(Level.INFO, "Đang chỉnh sửa chiến dịch ID: {0}, Tiêu đề: {1}", 
                  new Object[]{recruitmentId, campaign.getTitle()});
                  
        // Chuyển đến trang form
        request.getRequestDispatcher("/view/student/chairman/recruitmentCampaignForm.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Đảm bảo charset UTF-8 cho request và response
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
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
        
        try {
            if ("/create".equals(pathInfo) || "/".equals(pathInfo)) {
                // Xử lý tạo mới hoạt động
                handleCreateCampaign(request, response, currentUser, jsonResponse);
            } else if ("/update".equals(pathInfo)) {
                // Xử lý cập nhật hoạt động
                handleUpdateCampaign(request, response, currentUser, jsonResponse);
            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Endpoint không được hỗ trợ");
            }
        } catch (IllegalArgumentException e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", e.getMessage() != null ? e.getMessage() : "Dữ liệu không hợp lệ");
            logger.log(Level.WARNING, "Dữ liệu không hợp lệ: {0}", e.getMessage());
        } catch (Exception e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Lỗi hệ thống: " + e.getMessage());
            logger.log(Level.SEVERE, "Lỗi xử lý POST request: {0}", e.getMessage());
        }
        
        out.print(jsonResponse.toString());
    }

    /**
     * Xử lý tạo mới hoạt động tuyển quân
     */
    private void handleCreateCampaign(HttpServletRequest request, HttpServletResponse response, 
                                     Users currentUser, JsonObject jsonResponse) throws Exception {
        
        // Lấy thông tin clubId từ request
        String clubIdParam = request.getParameter("clubId");
        logger.log(Level.FINE, "clubIdParam từ request.getParameter: {0}", clubIdParam);
        Integer clubId = null;
        
        if (clubIdParam != null && !clubIdParam.isEmpty()) {
            clubId = Integer.parseInt(clubIdParam);
        } else {
            // Tìm CLB mà người dùng là chủ nhiệm
            UserClub userClub = getChairmanClub(currentUser.getUserID());
            if (userClub != null) {
                clubId = userClub.getClubID();
            } else {
                throw new IllegalArgumentException("Không xác định được CLB. Vui lòng chọn CLB.");
            }
        }
        
        // Kiểm tra quyền chủ nhiệm
        if (!isClubChairman(currentUser.getUserID(), clubId)) {
            throw new IllegalArgumentException("Bạn không có quyền tạo hoạt động tuyển quân cho CLB này. Chỉ chủ nhiệm CLB mới có quyền này.");
        }
        
        // Lấy thông tin từ form và tạo đối tượng RecruitmentCampaign
        RecruitmentCampaign campaign = parseRecruitmentCampaign(request, true);
        campaign.setCreatedBy(currentUser.getUserID());
        // Status will be set by the database based on the start date
        
        // Kiểm tra lại clubId một lần nữa
        if (campaign.getClubID() <= 0) {
            logger.log(Level.SEVERE, "ClubID không hợp lệ ({0}) sau quá trình xử lý!", campaign.getClubID());
            throw new IllegalArgumentException("Thiếu thông tin CLB hoặc ID CLB không hợp lệ");
        }
        
        // Ghi log thông tin cơ bản
        logger.log(Level.INFO, "Tạo chiến dịch mới - ClubID: {0}, Tiêu đề: {1}", new Object[]{campaign.getClubID(), campaign.getTitle()});
        
        // Gọi service để tạo chiến dịch
        int result = recruitmentService.createCampaign(campaign);
        
        if (result > 0) {
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("recruitmentId", result);
            jsonResponse.addProperty("message", "Đã tạo hoạt động tuyển quân thành công");
        } else if (result == -1) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Thời gian bị trùng với hoạt động khác");
        } else if (result == -2) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Không thể tạo hoạt động với ngày bắt đầu trong quá khứ");
        } else {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Không thể tạo hoạt động tuyển quân");
        }
    }

    /**
     * Xử lý cập nhật hoạt động tuyển quân
     */
    private void handleUpdateCampaign(HttpServletRequest request, HttpServletResponse response, 
                                     Users currentUser, JsonObject jsonResponse) throws Exception {
        
        // Lấy ID hoạt động tuyển quân
        String recruitmentIdParam = request.getParameter("recruitmentId");
        if (recruitmentIdParam == null || recruitmentIdParam.isEmpty()) {
            throw new IllegalArgumentException("Thiếu ID hoạt động tuyển quân");
        }
        
        int recruitmentId = Integer.parseInt(recruitmentIdParam);
        
        // Lấy thông tin hoạt động hiện tại
        RecruitmentCampaign currentCampaign = recruitmentService.getCampaignById(recruitmentId);
        if (currentCampaign == null) {
            throw new IllegalArgumentException("Không tìm thấy hoạt động tuyển quân");
        }
        
        // Kiểm tra quyền chủ nhiệm
        int clubId = currentCampaign.getClubID();
        if (!isClubChairman(currentUser.getUserID(), clubId)) {
            throw new IllegalArgumentException("Bạn không có quyền cập nhật hoạt động tuyển quân này. Chỉ chủ nhiệm CLB mới có quyền này.");
        }
        
        // Lấy thông tin từ form và cập nhật đối tượng RecruitmentCampaign
        RecruitmentCampaign updatedCampaign = parseRecruitmentCampaign(request, false);
        
        // Gọi service để cập nhật chiến dịch
        boolean result = recruitmentService.updateCampaign(updatedCampaign);
        
        if (result) {
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Đã cập nhật hoạt động tuyển quân thành công");
        } else {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Không thể cập nhật hoạt động tuyển quân. Vui lòng kiểm tra trùng lịch hoặc hoạt động đang diễn ra.");
        }
    }
    
    /**
     * Phân tích dữ liệu từ request và tạo đối tượng RecruitmentCampaign
     */
    private RecruitmentCampaign parseRecruitmentCampaign(HttpServletRequest request, boolean isNew) throws ParseException, SQLException {
        RecruitmentCampaign campaign = new RecruitmentCampaign();
        
        // Nếu là cập nhật, lấy ID hoạt động
        if (!isNew) {
            String recruitmentId = request.getParameter("recruitmentId");
            if (recruitmentId != null && !recruitmentId.isEmpty()) {
                campaign.setRecruitmentID(Integer.parseInt(recruitmentId));
            } else {
                throw new IllegalArgumentException("Thiếu ID hoạt động tuyển quân");
            }
        } // Không cần ID khi tạo mới
        
        // Thực hiện kiểm tra clubId từ nhiều nguồn khác nhau
        String clubIdParam = null;
        Integer clubId = null;
        
        // Thử tất cả các nguồn có thể cho clubId
        String[] possibleParams = {"clubId", "clubID", "club_id", "club-id"};
        
        for (String param : possibleParams) {
            String value = request.getParameter(param);
            if (value != null && !value.isEmpty()) {
                clubIdParam = value;
                break;
            }
        }
        
        // Nếu vẫn không tìm thấy, thử từ form campaign hoặc request attribute
        if (clubIdParam == null || clubIdParam.isEmpty()) {
            // Nếu cập nhật chiến dịch, lấy từ chiến dịch hiện tại
            if (!isNew && request.getAttribute("campaign") != null) {
                RecruitmentCampaign existingCampaign = (RecruitmentCampaign) request.getAttribute("campaign");
                clubId = existingCampaign.getClubID();
            }
            
            // Thử lấy từ request attribute
            if (clubId == null && request.getAttribute("selectedClubId") != null) {
                clubId = (Integer) request.getAttribute("selectedClubId");
            }
        } else {
            try {
                clubId = Integer.parseInt(clubIdParam);
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Không thể chuyển đổi clubId: {0}", e.getMessage());
                clubId = null;
            }
        }
        
        // Nếu vẫn không tìm thấy và đây là request cập nhật, lấy từ campaign ID
        if (clubId == null && !isNew) {
            try {
                String recruitmentId = request.getParameter("recruitmentId");
                if (recruitmentId != null && !recruitmentId.isEmpty()) {
                    int recId = Integer.parseInt(recruitmentId);
                    RecruitmentCampaign existingCampaign = recruitmentService.getCampaignById(recId);
                    if (existingCampaign != null) {
                        clubId = existingCampaign.getClubID();
                    }
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Lỗi khi tìm campaign: {0}", e.getMessage());
            }
        }
        
        // Kiểm tra cuối cùng và thiết lập cho campaign
        if (clubId != null && clubId > 0) {
            campaign.setClubID(clubId);
        } else {
            logger.log(Level.SEVERE, "Không tìm thấy clubId hợp lệ trong request!");
            throw new IllegalArgumentException("Thiếu thông tin CLB");
        }
        
        // Lấy các thông tin khác
        String gen = request.getParameter("gen");
        if (gen != null && !gen.isEmpty()) {
            campaign.setGen(Integer.parseInt(gen));
        } else {
            throw new IllegalArgumentException("Vui lòng nhập số thế hệ (Gen)");
        }
        
        String templateId = request.getParameter("templateId");
        if (templateId != null && !templateId.isEmpty()) {
            campaign.setTemplateID(Integer.parseInt(templateId));
        } else {
            throw new IllegalArgumentException("Vui lòng chọn form đăng ký");
        }
        
        String title = request.getParameter("title");
        if (title != null && !title.isEmpty()) {
            campaign.setTitle(title);
        } else {
            throw new IllegalArgumentException("Vui lòng nhập tiêu đề hoạt động");
        }
        
        campaign.setDescription(request.getParameter("description"));
        
        String startDate = request.getParameter("startDate");
        if (startDate != null && !startDate.isEmpty()) {
            campaign.setStartDate(dateFormat.parse(startDate));
        } else {
            throw new IllegalArgumentException("Vui lòng chọn ngày bắt đầu");
        }
        
        String endDate = request.getParameter("endDate");
        if (endDate != null && !endDate.isEmpty()) {
            campaign.setEndDate(dateFormat.parse(endDate));
        } else {
            throw new IllegalArgumentException("Vui lòng chọn ngày kết thúc");
        }
        
        if (!isNew) {
            String status = request.getParameter("status");
            if (status != null && !status.isEmpty()) {
                campaign.setStatus(status);
            }
        }
        
        return campaign;
    }
}
