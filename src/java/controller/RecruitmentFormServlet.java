package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import models.RecruitmentCampaign;
import models.RecruitmentStage;
import models.Locations;
import models.Users;
import models.UserClub;
import service.RecruitmentService;
import dal.LocationDAO;
import dal.UserClubDAO;
import dal.ApplicationFormTemplateDAO;
import java.sql.SQLException;

/**
 * Xử lý việc tạo và chỉnh sửa hoạt động tuyển quân
 */
@WebServlet(name = "RecruitmentFormServlet", urlPatterns = {"/recruitmentForm/*", "/recruitment/form/*"})
public class RecruitmentFormServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(RecruitmentFormServlet.class.getName());
    private final RecruitmentService recruitmentService = new RecruitmentService();
    private final LocationDAO locationDAO = new LocationDAO();
    private final ApplicationFormTemplateDAO formTemplateDAO = new ApplicationFormTemplateDAO();
    private final UserClubDAO userClubDAO = new UserClubDAO();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final Gson gson = new Gson();

    private boolean isClubChairman(String userId, Integer clubId) throws SQLException {
        if (clubId == null) {
            return false;
        }
        UserClub userClub = userClubDAO.getUserClubManagementRole(userId, clubId);
        return userClub != null && userClub.getRoleID() == 1;
    }

    /**
     * Lấy CLB mà người dùng là chủ nhiệm
     *
     * @param userId ID của người dùng
     * @return UserClub nếu người dùng là chủ nhiệm của bất kỳ CLB nào, ngược
     * lại null
     */
    private UserClub getChairmanClub(String userId) throws SQLException {
        UserClub userClub = userClubDAO.getUserClubManagementRole(userId, null);
        if (userClub != null && userClub.getRoleID() == 1) {
            return userClub;
        }
        return null;
    }

    /**
     * Hàm gửi thông báo lỗi và chuyển hướng
     */
    private void sendErrorRedirect(HttpServletRequest request, HttpServletResponse response,
            String errorCode, String message, String redirectPath) throws IOException {
        if (redirectPath == null) {
            redirectPath = "/myclub";
        }

        // Log thông tin lỗi để debug
        logger.log(Level.WARNING, "Chuyển hướng với lỗi: {0} - {1}, path: {2}",
                new Object[]{errorCode, message, redirectPath});

        // Log thêm thông tin debug về request parameters để giúp xác định nguyên nhân
        String debugInfo = "";
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            debugInfo += paramName + "=" + paramValue + ", ";
        }

        logger.log(Level.INFO, "Request parameters: {0}", debugInfo);

        try {
            // Thêm thông tin debug vào thông báo lỗi
            String enhancedMessage = message;
            if (errorCode.equals("missing_parameter")) {
                enhancedMessage += " [Debug: " + debugInfo + "]";
            }

            response.sendRedirect(request.getContextPath() + redirectPath + "?error=" + errorCode + "&message="
                    + URLEncoder.encode(enhancedMessage, StandardCharsets.UTF_8.name()));
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
            logger.log(Level.INFO, "DEBUG - doGet với pathInfo: {0}", pathInfo);

            if ("/new".equals(pathInfo) || "/".equals(pathInfo)) {
                // Xử lý hiển thị form tạo mới
                handleNewForm(request, response, currentUser);
            } else if ("/edit".equals(pathInfo) || "/form/edit".equals(pathInfo)) {
                // Xử lý hiển thị form chỉnh sửa - sử dụng tham số recruitmentId thay vì id
                handleEditForm(request, response, currentUser);
            } else {
                logger.log(Level.WARNING, "DEBUG - Không tìm thấy endpoint cho pathInfo: {0}", pathInfo);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại: " + pathInfo);
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
        List<Locations> locations = locationDAO.getAllLocationsOnCampus();

        // Tạo JSON rỗng cho stages khi tạo mới
        String stagesJson = "[]";

        // Truyền dữ liệu vào request
        request.setAttribute("campaign", campaign);
        request.setAttribute("stages", null);
        request.setAttribute("stagesJson", stagesJson); // Thêm JSON string cho stages rỗng
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

        // Lấy ID hoạt động tuyển quân từ tham số - ưu tiên recruitmentId, hỗ trợ ngược với id cho các URL cũ
        String recruitmentIdParam = request.getParameter("recruitmentId");
        if (recruitmentIdParam == null || recruitmentIdParam.isEmpty()) {
            // Kiểm tra tham số id cho tương thích ngược với các URL cũ
            recruitmentIdParam = request.getParameter("id");
            if (recruitmentIdParam == null || recruitmentIdParam.isEmpty()) {
                logger.log(Level.WARNING, "Thiếu cả recruitmentId và id trong request");
                sendErrorRedirect(request, response, "missing_id", "Thiếu ID hoạt động tuyển quân", "/recruitment");
                return;
            }
            logger.log(Level.INFO, "Sử dụng tham số id thay vì recruitmentId cho tương thích ngược: {0}", recruitmentIdParam);
        }

        int recruitmentId;
        try {
            recruitmentId = Integer.parseInt(recruitmentIdParam);
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

        // Lấy số lượng đơn đăng ký
        int applicationCount = recruitmentService.countApplicationResponses(recruitmentId);
        boolean hasApplications = applicationCount > 0;
        // Kiểm tra quyền chủ nhiệm
        int clubId = campaign.getClubID();

        // Lấy clubId từ parameter nếu có
        String clubIdParam = request.getParameter("clubId");
        if (clubIdParam != null && !clubIdParam.isEmpty()) {
            try {
                // Debug: Log thông tin clubId từ parameter và từ campaign
                logger.log(Level.INFO, "[DEBUG] clubId từ parameter: {0}, clubId từ campaign: {1}",
                        new Object[]{clubIdParam, clubId});

                int paramClubId = Integer.parseInt(clubIdParam);
                if (paramClubId != clubId) {
                    logger.log(Level.WARNING, "[DEBUG] clubId từ parameter ({0}) khác với clubId từ campaign ({1})",
                            new Object[]{paramClubId, clubId});
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "[DEBUG] Không thể parse clubId từ parameter: {0}", clubIdParam);
            }
        } else {
            logger.log(Level.INFO, "[DEBUG] Không có clubId trong parameter, sử dụng clubId từ campaign: {0}", clubId);
        }

        if (!isClubChairman(currentUser.getUserID(), clubId)) {
            logger.log(Level.WARNING, "User {0} không có quyền chỉnh sửa chiến dịch của CLB {1}",
                    new Object[]{currentUser.getUserID(), clubId});
            sendErrorRedirect(request, response, "unauthorized",
                    "Bạn không có quyền chỉnh sửa hoạt động tuyển quân. Chỉ chủ nhiệm CLB mới có quyền này.", "/recruitment");
            return;
        }

        // Lấy danh sách giai đoạn
        List<RecruitmentStage> stages = recruitmentService.getStagesByCampaign(recruitmentId);
        // Log JSON để debug truyền dữ liệu sang frontend
        try {
            logger.log(Level.INFO, "RecruitmentFormServlet - JSON vòng tuyển: {0}",
                    new Gson().toJson(stages));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Không thể chuyển đổi stages sang JSON: {0}", e.getMessage());
        }

        // Lấy danh sách form đăng ký đã publish của CLB
        List<Map<String, Object>> publishedTemplates = formTemplateDAO.getPublishedMemberForms(clubId);

        // Nếu không tìm thấy form cho club này, lấy tất cả các form
        if (publishedTemplates.isEmpty()) {
            publishedTemplates = formTemplateDAO.getPublishedMemberForms(null);
            request.setAttribute("formWarning",
                    "Không tìm thấy form nào dành riêng cho CLB #" + clubId + ". Hiển thị tất cả form có sẵn.");
        }

        // Lấy danh sách địa điểm
        List<Locations> locations = locationDAO.getAllLocationsOnCampus();

        // Tạo JSON cho stages sử dụng Gson với định dạng ngày tương thích với JavaScript
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        // Đồng bộ các trường camelCase với trường snake_case
        for (RecruitmentStage stage : stages) {
            stage.setStageId(stage.getStageID());
            stage.setLocationId(stage.getLocationID());
        }

        // Chuyển thành JSON
        String stagesJson = gson.toJson(stages);

        // Truyền dữ liệu vào request
        request.setAttribute("campaign", campaign);
        request.setAttribute("stages", stages);
        request.setAttribute("applicationCount", applicationCount);
        request.setAttribute("hasApplications", hasApplications);
        request.setAttribute("stagesJson", stagesJson); // Thêm JSON string cho stages
        request.setAttribute("publishedTemplates", publishedTemplates);
        request.setAttribute("locations", locations);
        request.setAttribute("mode", "edit");
        request.setAttribute("selectedClubId", clubId);

        logger.log(Level.INFO, "Đang chỉnh sửa chiến dịch ID: {0}, Tiêu đề: {1}",
                new Object[]{recruitmentId, campaign.getTitle()});
        logger.log(Level.INFO, "JSON stages để truyền sang client: {0}", stagesJson);

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

        // Log toàn bộ parameters nhận được để debug
        logger.log(Level.INFO, "DEBUG - handleCreateCampaign: Tất cả parameters từ request:");
        java.util.Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            logger.log(Level.INFO, "DEBUG - Parameter: {0} = {1}", new Object[]{paramName, paramValue});
        }

        // Kiểm tra và log các parameter cho các vòng tuyển
        logger.log(Level.INFO, "DEBUG - Kiểm tra thông tin các vòng tuyển từ request:");
        logger.log(Level.INFO, "Vòng nộp đơn: {0} -> {1}",
                new Object[]{request.getParameter("applicationStageStart"), request.getParameter("applicationStageEnd")});
        logger.log(Level.INFO, "Vòng phỏng vấn: {0} -> {1}, địa điểm: {2}",
                new Object[]{request.getParameter("interviewStageStart"), request.getParameter("interviewStageEnd"),
                    request.getParameter("interviewLocationId")});
        logger.log(Level.INFO, "Vòng thử thách: {0} -> {1}, mô tả: {2}",
                new Object[]{request.getParameter("challengeStageStart"), request.getParameter("challengeStageEnd"),
                    request.getParameter("challengeDescription")});

        // Lấy thông tin clubId từ request
        String clubIdParam = request.getParameter("clubId");
        if (clubIdParam == null || clubIdParam.isEmpty()) {
            clubIdParam = request.getParameter("clubID");
            if (clubIdParam == null || clubIdParam.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn câu lạc bộ");
            }
        }
        int clubId = Integer.parseInt(clubIdParam);

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
            throw new IllegalArgumentException("CLB không hợp lệ");
        }

        logger.log(Level.INFO, "DEBUG - Gọi recruitmentService.createCampaign với thông tin: ClubID={0}, Gen={1}, Title={2}, StartDate={3}, EndDate={4}",
                new Object[]{
                    campaign.getClubID(),
                    campaign.getGen(),
                    campaign.getTitle(),
                    dateFormat.format(campaign.getStartDate()),
                    dateFormat.format(campaign.getEndDate())
                });

        int result = recruitmentService.createCampaign(campaign);

        logger.log(Level.INFO, "DEBUG - Kết quả tạo chiến dịch: {0}", result);

        if (result > 0) {
            // Lấy ID của chiến dịch vừa tạo
            int recruitmentId = result;
            logger.log(Level.INFO, "DEBUG - Chiến dịch đã được tạo với ID={0}, tiếp tục tạo các vòng tuyển", recruitmentId);

            // DEBUG: Kiểm tra lại một lần nữa các thông tin vòng tuyển
            logger.log(Level.INFO, "DEBUG - Kiểm tra lại thông tin vòng tuyển từ request trước khi tạo:");
            logger.log(Level.INFO, "DEBUG - Vòng nộp đơn: {0} -> {1}",
                    new Object[]{request.getParameter("applicationStageStart"), request.getParameter("applicationStageEnd")});
            logger.log(Level.INFO, "DEBUG - Vòng phỏng vấn: {0} -> {1}, Địa điểm: {2}",
                    new Object[]{request.getParameter("interviewStageStart"),
                        request.getParameter("interviewStageEnd"),
                        request.getParameter("interviewLocationId")});
            logger.log(Level.INFO, "DEBUG - Vòng thử thách: {0} -> {1}, Mô tả: {2}",
                    new Object[]{request.getParameter("challengeStageStart"),
                        request.getParameter("challengeStageEnd"),
                        request.getParameter("challengeDescription")});

            // Tạo các vòng tuyển cho chiến dịch
            logger.log(Level.INFO, "DEBUG - Bắt đầu gọi createRecruitmentStages()");
            boolean stagesCreated = createRecruitmentStages(request, recruitmentId);

            if (stagesCreated) {
                // Kiểm tra và hiển thị các vòng tuyển đã tạo
                try {
                    List<RecruitmentStage> createdStages = new dal.RecruitmentStageDAO().getStagesByRecruitmentId(recruitmentId);
                    logger.log(Level.INFO, "DEBUG - Đã tạo {0} vòng tuyển trong database", createdStages.size());
                    for (RecruitmentStage stage : createdStages) {
                        logger.log(Level.INFO, "DEBUG - Vòng tuyển đã tạo: ID={0}, Tên={1}, Thời gian={2}->{3}",
                                new Object[]{stage.getStageID(), stage.getStageName(),
                                    dateFormat.format(stage.getStartDate()),
                                    dateFormat.format(stage.getEndDate())});
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "DEBUG - Không thể lấy danh sách vòng tuyển đã tạo: {0}", e.getMessage());
                }

                logger.log(Level.INFO, "DEBUG - Đã tạo thành công các vòng tuyển cho chiến dịch ID {0}", recruitmentId);
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("recruitmentId", recruitmentId);
                jsonResponse.addProperty("clubId", clubId);
                jsonResponse.addProperty("stagesCreated", true);
                jsonResponse.addProperty("message", "Đã tạo hoạt động tuyển quân và các vòng tuyển thành công");

                // Kiểm tra trạng thái chiến dịch và cập nhật IsRecruiting cho CLB
                RecruitmentCampaign createdCampaign = recruitmentService.getCampaignById(recruitmentId);
                if (createdCampaign != null && "ONGOING".equals(createdCampaign.getStatus())) {
                    // Cập nhật trạng thái tuyển quân cho CLB
                    dal.ClubDAO clubDAO = new dal.ClubDAO();
                    boolean isUpdated = clubDAO.updateIsRecruiting(clubId, true);
                    logger.log(Level.INFO, "Cập nhật IsRecruiting=true cho CLB ID {0}: {1}",
                            new Object[]{clubId, isUpdated ? "thành công" : "thất bại"});
                }
            } else {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("recruitmentId", recruitmentId);
                jsonResponse.addProperty("clubId", clubId);
                jsonResponse.addProperty("stagesCreated", false);
                jsonResponse.addProperty("message", "Đã tạo hoạt động tuyển quân thành công, nhưng có lỗi khi tạo các vòng tuyển");

                // Kiểm tra xem đã tạo được vòng tuyển nào không
                try {
                    List<RecruitmentStage> partialStages = new dal.RecruitmentStageDAO().getStagesByRecruitmentId(recruitmentId);
                    if (!partialStages.isEmpty()) {
                        logger.log(Level.INFO, "DEBUG - Đã tạo được {0} vòng tuyển dù báo lỗi", partialStages.size());
                        for (RecruitmentStage stage : partialStages) {
                            logger.log(Level.INFO, "DEBUG - Vòng tuyển đã tạo: {0} (ID: {1})",
                                    new Object[]{stage.getStageName(), stage.getStageID()});
                        }
                    } else {
                        logger.log(Level.SEVERE, "DEBUG - Không tạo được vòng tuyển nào trong database");
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "DEBUG - Lỗi kiểm tra vòng tuyển đã tạo: {0}", e.getMessage());
                }
            }
        } else if (result == -1) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Thời gian bị trùng với hoạt động khác");
        } else if (result == -2) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Ngày bắt đầu không thể nằm trong quá khứ");
        } else {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Lỗi khi tạo hoạt động tuyển quân");
        }
    }

    /**
     * Xử lý cập nhật hoạt động tuyển quân
     */
    private void handleUpdateCampaign(HttpServletRequest request, HttpServletResponse response,
            Users currentUser, JsonObject jsonResponse) throws Exception {

        logger.log(Level.INFO, "DEBUG - Xử lý cập nhật hoạt động tuyển quân - User: {0}",
                currentUser != null ? currentUser.getUserID() : "null");

        // Lấy ID hoạt động tuyển quân - Ưu tiên recruitmentId nhưng vẫn kiểm tra id để đảm bảo tương thích ngược
        String recruitmentIdParam = request.getParameter("recruitmentId");
        if (recruitmentIdParam == null || recruitmentIdParam.isEmpty()) {
            // Thử tìm kiếm tham số id thay thế
            recruitmentIdParam = request.getParameter("id");
            if (recruitmentIdParam == null || recruitmentIdParam.isEmpty()) {
                logger.log(Level.SEVERE, "DEBUG - Thiếu cả recruitmentId và id trong request");
                throw new IllegalArgumentException("Thiếu ID hoạt động tuyển quân");
            }
            logger.log(Level.INFO, "DEBUG - Đã tìm thấy ID trong tham số id thay vì recruitmentId: {0}", recruitmentIdParam);
        } else {
            // Kiểm tra xem id có tồn tại và khác với recruitmentId không
            String idParam = request.getParameter("id");
            if (idParam != null && !idParam.isEmpty() && !idParam.equals(recruitmentIdParam)) {
                logger.log(Level.WARNING, "DEBUG - Cả recruitmentId và id đều tồn tại nhưng giá trị khác nhau: recruitmentId={0}, id={1}. Ưu tiên sử dụng recruitmentId.",
                        new Object[]{recruitmentIdParam, idParam});
            }
        }

        logger.log(Level.INFO, "DEBUG - Cập nhật hoạt động với ID: {0}", recruitmentIdParam);
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
        updatedCampaign.setRecruitmentID(recruitmentId); // Đảm bảo ID được set đúng

        // Log dữ liệu trước khi cập nhật để debug
        logger.log(Level.INFO, "Dữ liệu cập nhật campaign: ID={0}, ClubID={1}, Title={2}, TemplateID={3}",
                new Object[]{updatedCampaign.getRecruitmentID(), updatedCampaign.getClubID(),
                    updatedCampaign.getTitle(), updatedCampaign.getTemplateID()});

        try {
            // Gọi service để cập nhật chiến dịch
            boolean result = recruitmentService.updateCampaign(updatedCampaign);

            if (result) {
                // Cập nhật các vòng tuyển
                boolean stagesUpdated = updateRecruitmentStages(request, recruitmentId);

                if (stagesUpdated) {
                    logger.log(Level.INFO, "Đã cập nhật thành công các vòng tuyển cho chiến dịch ID {0}", recruitmentId);
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Đã cập nhật hoạt động tuyển quân và các vòng tuyển thành công");
                    jsonResponse.addProperty("clubId", updatedCampaign.getClubID());
                } else {
                    logger.log(Level.WARNING, "Chiến dịch ID {0} được cập nhật nhưng các vòng tuyển thất bại", recruitmentId);
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Đã cập nhật hoạt động tuyển quân thành công, nhưng có lỗi khi cập nhật các vòng tuyển");
                    jsonResponse.addProperty("stageUpdateError", true);
                    jsonResponse.addProperty("stageUpdateErrorMsg", "Không thể thay đổi ngày bắt đầu của vòng tuyển đã diễn ra.");
                    jsonResponse.addProperty("clubId", updatedCampaign.getClubID());
                }
            }
        } catch (IllegalStateException e) {
            // Xử lý các lỗi cụ thể từ service
            logger.log(Level.WARNING, "Lỗi khi cập nhật campaign ID {0}: {1}",
                    new Object[]{recruitmentId, e.getMessage()});
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", e.getMessage());
        } catch (Exception e) {
            // Xử lý các lỗi khác
            logger.log(Level.SEVERE, "Lỗi không xác định khi cập nhật campaign ID {0}: {1}",
                    new Object[]{recruitmentId, e.getMessage()});
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Lỗi hệ thống khi cập nhật hoạt động tuyển quân: " + e.getMessage());
        }
    }

    /**
     * Tạo các vòng tuyển cho một hoạt động tuyển quân mới
     *
     * @param request HttpServletRequest chứa dữ liệu form
     * @param recruitmentId ID của chiến dịch tuyển quân vừa được tạo
     * @return true nếu tạo thành công tất cả các vòng tuyển, false nếu có lỗi
     */
    private boolean createRecruitmentStages(HttpServletRequest request, int recruitmentId) {
        logger.log(Level.INFO, "DEBUG - Bắt đầu tạo các vòng tuyển cho chiến dịch ID {0}", recruitmentId);

        try {
            // Tạo các đối tượng để lưu trữ dữ liệu của các vòng tuyển
            List<RecruitmentStage> stages = new ArrayList<>();

            // DEBUG: Kiểm tra một lần nữa các giá trị từ request
            logger.log(Level.INFO, "DEBUG - createRecruitmentStages: Kiểm tra các parameters vòng tuyển:");
            logger.log(Level.INFO, "applicationStageStart = {0}", request.getParameter("applicationStageStart"));
            logger.log(Level.INFO, "applicationStageEnd = {0}", request.getParameter("applicationStageEnd"));
            logger.log(Level.INFO, "interviewStageStart = {0}", request.getParameter("interviewStageStart"));
            logger.log(Level.INFO, "interviewStageEnd = {0}", request.getParameter("interviewStageEnd"));
            logger.log(Level.INFO, "interviewLocationId = {0}", request.getParameter("interviewLocationId"));
            logger.log(Level.INFO, "challengeStageStart = {0}", request.getParameter("challengeStageStart"));
            logger.log(Level.INFO, "challengeStageEnd = {0}", request.getParameter("challengeStageEnd"));
            logger.log(Level.INFO, "challengeDescription = {0}", request.getParameter("challengeDescription"));

            // 1. Vòng Nộp đơn (Application)
            String appStartStr = request.getParameter("applicationStageStart");
            String appEndStr = request.getParameter("applicationStageEnd");

            if (appStartStr != null && !appStartStr.isEmpty() && appEndStr != null && !appEndStr.isEmpty()) {
                logger.log(Level.INFO, "DEBUG - Xử lý vòng Nộp đơn: {0} - {1}", new Object[]{appStartStr, appEndStr});

                try {
                    Date appStartDate = dateFormat.parse(appStartStr);
                    Date appEndDate = dateFormat.parse(appEndStr);

                    LocationDAO locationDAO = new LocationDAO();
                    List<Locations> locations = locationDAO.getAllLocationsOnCampus();
                    int defaultLocationId = locations.isEmpty() ? 1 : locations.get(0).getLocationID();

                    logger.log(Level.INFO, "DEBUG - Using location ID {0} for APPLICATION stage", defaultLocationId);

                    RecruitmentStage appStage = new RecruitmentStage();
                    appStage.setRecruitmentID(recruitmentId);
                    appStage.setStageName("APPLICATION"); // Sử dụng ENUM value thay vì tên tiếng Việt
                    appStage.setStartDate(appStartDate);
                    appStage.setEndDate(appEndDate);
                    // Status sẽ được tính toán dựa trên ngày trong DAO
                    appStage.setLocationID(defaultLocationId); // Sử dụng ID hợp lệ từ bảng Locations
                    appStage.setDescription("Vòng nộp đơn đăng ký");
                    stages.add(appStage);

                    // Debug log
                    logger.log(Level.INFO, "DEBUG - Đã tạo vòng APPLICATION với StageName đúng ENUM value");

                    logger.log(Level.INFO, "DEBUG - Đã tạo object Vòng Nộp đơn: {0} -> {1}",
                            new Object[]{dateFormat.format(appStartDate), dateFormat.format(appEndDate)});
                } catch (ParseException e) {
                    logger.log(Level.SEVERE, "DEBUG - Lỗi parse date cho vòng Nộp đơn: {0}", e.getMessage());
                }
            } else {
                logger.log(Level.WARNING, "DEBUG - Thiếu thông tin vòng Nộp đơn cho chiến dịch ID {0}", recruitmentId);
            }

            // 2. Vòng Phỏng vấn (Interview)
            String interviewStartStr = request.getParameter("interviewStageStart");
            String interviewEndStr = request.getParameter("interviewStageEnd");
            String interviewLocationIdStr = request.getParameter("interviewLocationId");

            if (interviewStartStr != null && !interviewStartStr.isEmpty()
                    && interviewEndStr != null && !interviewEndStr.isEmpty()) {

                logger.log(Level.INFO, "Xử lý vòng Phỏng vấn: {0} - {1}, địa điểm: {2}",
                        new Object[]{interviewStartStr, interviewEndStr, interviewLocationIdStr});

                Date interviewStartDate = dateFormat.parse(interviewStartStr);
                Date interviewEndDate = dateFormat.parse(interviewEndStr);

                RecruitmentStage interviewStage = new RecruitmentStage();
                interviewStage.setRecruitmentID(recruitmentId);
                interviewStage.setStageName("INTERVIEW"); // Sử dụng ENUM value thay vì tên tiếng Việt
                interviewStage.setStartDate(interviewStartDate);
                interviewStage.setEndDate(interviewEndDate);
                // Status sẽ được tính toán dựa trên ngày trong DAO

                // Xử lý locationId nếu có
                if (interviewLocationIdStr != null && !interviewLocationIdStr.isEmpty()) {
                    try {
                        int locationId = Integer.parseInt(interviewLocationIdStr);

                        // Kiểm tra xem locationId có tồn tại trong database không
                        LocationDAO locationDAOInterview = new LocationDAO();
                        List<Locations> locList = locationDAOInterview.getAllLocationsOnCampus();
                        boolean isValid = false;

                        // Verify the location ID exists
                        for (Locations loc : locList) {
                            if (loc.getLocationID() == locationId) {
                                isValid = true;
                                break;
                            }
                        }

                        if (isValid) {
                            logger.log(Level.INFO, "DEBUG - Sử dụng locationId hợp lệ: {0} cho vòng Phỏng vấn", locationId);
                            interviewStage.setLocationID(locationId);
                        } else {
                            // Nếu không hợp lệ, sử dụng ID mặc định
                            int defaultId = locList.isEmpty() ? 1 : locList.get(0).getLocationID();
                            logger.log(Level.WARNING, "DEBUG - LocationId {0} không hợp lệ, sử dụng ID mặc định: {1}",
                                    new Object[]{locationId, defaultId});
                            interviewStage.setLocationID(defaultId);
                        }
                    } catch (NumberFormatException e) {
                        logger.log(Level.WARNING, "Lỗi chuyển đổi locationId cho vòng Phỏng vấn: {0}", e.getMessage());
                        // Sử dụng location mặc định
                        LocationDAO locationDAODefault = new LocationDAO();
                        List<Locations> locList = locationDAODefault.getAllLocationsOnCampus();
                        int defaultId = locList.isEmpty() ? 1 : locList.get(0).getLocationID();
                        interviewStage.setLocationID(defaultId);
                        logger.log(Level.INFO, "DEBUG - Sử dụng locationId mặc định: {0} cho vòng Phỏng vấn", defaultId);
                    }
                } else {
                    // Nếu không có location được chọn, sử dụng location mặc định
                    LocationDAO locationDAODefault = new LocationDAO();
                    List<Locations> locList = locationDAODefault.getAllLocationsOnCampus();
                    int defaultId = locList.isEmpty() ? 1 : locList.get(0).getLocationID();
                    interviewStage.setLocationID(defaultId);
                    logger.log(Level.INFO, "DEBUG - Không có locationId được chọn, sử dụng mặc định: {0}", defaultId);
                }

                interviewStage.setDescription("Vòng phỏng vấn ứng viên");
                stages.add(interviewStage);

                // Debug log
                logger.log(Level.INFO, "DEBUG - Đã tạo vòng INTERVIEW với StageName đúng ENUM value");
            } else {
                logger.log(Level.WARNING, "Thiếu thông tin vòng Phỏng vấn cho chiến dịch ID {0}", recruitmentId);
            }

            // 3. Vòng Thử thách (Challenge) - không bắt buộc
            String challengeStartStr = request.getParameter("challengeStageStart");
            String challengeEndStr = request.getParameter("challengeStageEnd");
            String challengeDesc = request.getParameter("challengeDescription");

            logger.log(Level.INFO, "DEBUG - Kiểm tra dữ liệu vòng Thử thách: Start={0}, End={1}, Description={2}",
                    new Object[]{challengeStartStr, challengeEndStr, challengeDesc});

            if (challengeStartStr != null && !challengeStartStr.isEmpty()
                    && challengeEndStr != null && !challengeEndStr.isEmpty()) {

                try {
                    logger.log(Level.INFO, "DEBUG - Xử lý vòng Thử thách: {0} - {1}",
                            new Object[]{challengeStartStr, challengeEndStr});

                    Date challengeStartDate = dateFormat.parse(challengeStartStr);
                    Date challengeEndDate = dateFormat.parse(challengeEndStr);

                    // Kiểm tra ngày tháng hợp lệ
                    if (challengeStartDate == null || challengeEndDate == null
                            || challengeStartDate.after(challengeEndDate)) {
                        logger.log(Level.WARNING, "DEBUG - Ngày tháng vòng Thử thách không hợp lệ: {0} -> {1}",
                                new Object[]{challengeStartStr, challengeEndStr});
                    } else {
                        // Get a valid location ID from the database for CHALLENGE stage
                        LocationDAO locationDAOChallenge = new LocationDAO();
                        List<Locations> locationsChallenge = locationDAOChallenge.getAllLocationsOnCampus();
                        int defaultLocationIdChallenge = locationsChallenge.isEmpty() ? 1 : locationsChallenge.get(0).getLocationID();

                        logger.log(Level.INFO, "DEBUG - Using location ID {0} for CHALLENGE stage", defaultLocationIdChallenge);

                        RecruitmentStage challengeStage = new RecruitmentStage();
                        challengeStage.setRecruitmentID(recruitmentId);
                        challengeStage.setStageName("CHALLENGE"); // Sử dụng ENUM value thay vì tên tiếng Việt
                        challengeStage.setStartDate(challengeStartDate);
                        challengeStage.setEndDate(challengeEndDate);
                        // Status sẽ được tính toán dựa trên ngày trong DAO
                        challengeStage.setLocationID(defaultLocationIdChallenge); // Sử dụng ID hợp lệ từ bảng Locations

                        // Thêm mô tả thử thách nếu có
                        if (challengeDesc != null && !challengeDesc.isEmpty()) {
                            logger.log(Level.INFO, "DEBUG - Sử dụng mô tả thử thách từ form: {0}",
                                    new Object[]{challengeDesc});
                            challengeStage.setDescription(challengeDesc);
                        } else {
                            logger.log(Level.INFO, "DEBUG - Sử dụng mô tả thử thách mặc định");
                            challengeStage.setDescription("Vòng thử thách đánh giá năng lực");
                        }

                        logger.log(Level.INFO, "DEBUG - Đã khởi tạo đối tượng vòng Thử thách: {0} -> {1}, Mô tả: {2}",
                                new Object[]{dateFormat.format(challengeStartDate), dateFormat.format(challengeEndDate),
                                    challengeStage.getDescription()});

                        stages.add(challengeStage);

                        // Debug log
                        logger.log(Level.INFO, "DEBUG - Đã tạo vòng CHALLENGE với StageName đúng ENUM value");
                    }
                } catch (ParseException e) {
                    logger.log(Level.SEVERE, "DEBUG - Lỗi parse date cho vòng Thử thách: {0}", e.getMessage());
                }
            } else {
                logger.log(Level.INFO, "DEBUG - Không đủ thông tin để tạo vòng Thử thách");
            }

            // Log thông tin các vòng tuyển trước khi lưu
            logger.log(Level.INFO, "DEBUG - Chuẩn bị lưu {0} vòng tuyển cho chiến dịch ID {1}",
                    new Object[]{stages.size(), recruitmentId});

            // Debug: hiển thị chi tiết từng vòng tuyển sẽ lưu
            for (int i = 0; i < stages.size(); i++) {
                RecruitmentStage stage = stages.get(i);
                logger.log(Level.INFO, "DEBUG - Vòng tuyển #{0}: {1}, {2} -> {3}, Location: {4}, Description: {5}",
                        new Object[]{
                            i + 1,
                            stage.getStageName(),
                            dateFormat.format(stage.getStartDate()),
                            dateFormat.format(stage.getEndDate()),
                            stage.getLocationID(),
                            stage.getDescription()
                        });
            }

            // Sử dụng RecruitmentStageDAO để lưu các vòng tuyển vào database
            dal.RecruitmentStageDAO stageDAO = new dal.RecruitmentStageDAO();
            boolean allSuccess = true;
            int successCount = 0;

            logger.log(Level.INFO, "DEBUG - Bắt đầu lưu {0} vòng tuyển vào database", stages.size());

            for (RecruitmentStage stage : stages) {
                try {
                    // Kiểm tra dữ liệu một lần nữa trước khi lưu
                    if (stage.getRecruitmentID() <= 0) {
                        logger.log(Level.SEVERE, "DEBUG - RecruitmentID không hợp lệ: {0}", stage.getRecruitmentID());
                        allSuccess = false;
                        continue;
                    }

                    if (stage.getStageName() == null || stage.getStageName().isEmpty()) {
                        logger.log(Level.SEVERE, "DEBUG - StageName không hợp lệ (null hoặc empty)");
                        allSuccess = false;
                        continue;
                    }

                    if (stage.getStartDate() == null || stage.getEndDate() == null) {
                        logger.log(Level.SEVERE, "DEBUG - Ngày bắt đầu hoặc kết thúc không được để trống");
                        allSuccess = false;
                        continue;
                    }

                    // Đảm bảo mô tả không null
                    if (stage.getDescription() == null) {
                        stage.setDescription("");
                    }

                    // Đảm bảo status không null
                    if (stage.getStatus() == null) {
                        stage.setStatus("UPCOMING");
                    }

                    logger.log(Level.INFO, "DEBUG - Đang lưu vòng {0}: {1} -> {2}",
                            new Object[]{stage.getStageName(),
                                dateFormat.format(stage.getStartDate()),
                                dateFormat.format(stage.getEndDate())});

                    int stageId = stageDAO.createRecruitmentStage(stage);

                    if (stageId <= 0) {
                        logger.log(Level.SEVERE, "DEBUG - Lỗi khi tạo vòng {0} cho chiến dịch ID {1}, trả về ID: {2}",
                                new Object[]{stage.getStageName(), recruitmentId, stageId});
                        allSuccess = false;
                    } else {
                        successCount++;
                        logger.log(Level.INFO, "DEBUG - Đã tạo vòng {0} (ID: {1}) thành công cho chiến dịch ID {2}",
                                new Object[]{stage.getStageName(), stageId, recruitmentId});
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "DEBUG - Exception khi lưu vòng {0}: {1}",
                            new Object[]{stage.getStageName(), e.getMessage()});
                    for (StackTraceElement ste : e.getStackTrace()) {
                        logger.log(Level.SEVERE, "DEBUG - Stack trace: {0}", ste.toString());
                    }
                    allSuccess = false;
                }
            }

            logger.log(Level.INFO, "DEBUG - Kết quả lưu vòng tuyển: {0}/{1} vòng thành công",
                    new Object[]{successCount, stages.size()});

            return allSuccess;

        } catch (ParseException e) {
            logger.log(Level.SEVERE, "Lỗi khi chuyển đổi ngày tháng: {0}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi không xác định khi tạo các vòng tuyển: {0}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật các vòng tuyển cho một hoạt động tuyển quân đã tồn tại
     *
     * @param request HttpServletRequest chứa dữ liệu form
     * @param recruitmentId ID của chiến dịch tuyển quân cần cập nhật
     * @return true nếu cập nhật thành công tất cả các vòng tuyển, false nếu có
     * lỗi
     */
    private boolean updateRecruitmentStages(HttpServletRequest request, int recruitmentId) {
        logger.log(Level.INFO, "Bắt đầu cập nhật các vòng tuyển cho chiến dịch ID {0}", recruitmentId);

        try {
            Date currentDate = new Date(); // Current date for checking if stages have started

            // Lấy danh sách các vòng tuyển hiện tại
            dal.RecruitmentStageDAO stageDAO = new dal.RecruitmentStageDAO();
            List<RecruitmentStage> existingStages = stageDAO.getStagesByRecruitmentId(recruitmentId);

            logger.log(Level.INFO, "Tìm thấy {0} vòng tuyển hiện tại cho chiến dịch ID {1}",
                    new Object[]{existingStages.size(), recruitmentId});

            // Map để lưu trữ các vòng tuyển hiện tại theo tên vòng
            Map<String, RecruitmentStage> stageMap = new java.util.HashMap<>();
            for (RecruitmentStage stage : existingStages) {
                stageMap.put(stage.getStageName(), stage);
            }

            // 1. Vòng Nộp đơn (Application)
            String appStartStr = request.getParameter("applicationStageStart");
            String appEndStr = request.getParameter("applicationStageEnd");

            // Xác thực dữ liệu đầu vào
            if (appStartStr == null || appStartStr.isEmpty() || appEndStr == null || appEndStr.isEmpty()) {
                logger.log(Level.SEVERE, "Thiếu ngày tháng vòng nộp đơn: start={0}, end={1}",
                        new Object[]{appStartStr, appEndStr});
                return false;
            }

            logger.log(Level.INFO, "Xử lý vòng Nộp đơn: {0} - {1}", new Object[]{appStartStr, appEndStr});

            try {
                Date appStartDate = dateFormat.parse(appStartStr);
                Date appEndDate = dateFormat.parse(appEndStr);

                // Xác thực logic ngày tháng
                if (appStartDate.after(appEndDate)) {
                    logger.log(Level.SEVERE, "Ngày bắt đầu vòng nộp đơn phải trước ngày kết thúc");
                    return false;
                }

                // Tìm kiếm vòng nộp đơn theo tên tiếng Việt hoặc ENUM
                RecruitmentStage appStage = stageMap.get("APPLICATION");
                if (appStage == null) {
                    appStage = stageMap.get("Vòng Nộp đơn"); // Tương thích với dữ liệu cũ
                }

                if (appStage != null) {
                    // Kiểm tra nếu vòng đã bắt đầu, không cho thay đổi ngày bắt đầu
                    boolean stageHasStarted = !currentDate.before(appStage.getStartDate());
                    if (stageHasStarted && !dateFormat.format(appStage.getStartDate()).equals(appStartStr)) {
                        logger.log(Level.WARNING, "Không thể thay đổi ngày bắt đầu của vòng Nộp đơn vì đã diễn ra");
                        // Sử dụng ngày bắt đầu hiện tại
                        appStartDate = appStage.getStartDate();
                    }

                    // Cập nhật vòng hiện có
                    appStage.setStartDate(appStartDate);
                    appStage.setEndDate(appEndDate);
                    appStage.setStageName("APPLICATION"); // Đảm bảo dùng ENUM value
                    stageDAO.updateRecruitmentStage(appStage);
                    logger.log(Level.INFO, "Đã cập nhật vòng APPLICATION ID {0}", appStage.getStageID());
                } else {
                    // Tạo mới nếu không tồn tại
                    appStage = new RecruitmentStage();
                    appStage.setRecruitmentID(recruitmentId);
                    appStage.setStageName("APPLICATION"); // Dùng ENUM value
                    appStage.setStartDate(appStartDate);
                    appStage.setEndDate(appEndDate);
                    appStage.setStatus("UPCOMING");
                    appStage.setLocationID(0);
                    appStage.setDescription("Vòng nộp đơn đăng ký");
                    int stageId = stageDAO.createRecruitmentStage(appStage);
                    logger.log(Level.INFO, "Đã tạo mới vòng APPLICATION ID {0}", stageId);
                }
            } catch (ParseException e) {
                logger.log(Level.SEVERE, "Lỗi parse ngày tháng vòng nộp đơn: {0}", e.getMessage());
                return false;
            }

            // 2. Vòng Phỏng vấn (Interview)
            String interviewStartStr = request.getParameter("interviewStageStart");
            String interviewEndStr = request.getParameter("interviewStageEnd");
            String interviewLocationIdStr = request.getParameter("interviewLocationId");

            if (interviewStartStr != null && !interviewStartStr.isEmpty() && interviewEndStr != null && !interviewEndStr.isEmpty()) {
                logger.log(Level.INFO, "Xử lý vòng Phỏng vấn: {0} - {1}, địa điểm ID: {2}",
                        new Object[]{interviewStartStr, interviewEndStr, interviewLocationIdStr});

                Date interviewStartDate = dateFormat.parse(interviewStartStr);
                Date interviewEndDate = dateFormat.parse(interviewEndStr);
                int interviewLocationId = interviewLocationIdStr != null && !interviewLocationIdStr.isEmpty()
                        ? Integer.parseInt(interviewLocationIdStr) : 0;

                // Tìm vòng phỏng vấn hiện có
                RecruitmentStage interviewStage = stageMap.get("INTERVIEW");
                if (interviewStage == null) {
                    interviewStage = stageMap.get("Vòng Phỏng vấn"); // Tương thích với dữ liệu cũ
                }

                if (interviewStage != null) {
                    // Kiểm tra nếu vòng đã bắt đầu, không cho thay đổi ngày bắt đầu
                    boolean stageHasStarted = !currentDate.before(interviewStage.getStartDate());
                    if (stageHasStarted && !dateFormat.format(interviewStage.getStartDate()).equals(interviewStartStr)) {
                        logger.log(Level.WARNING, "Không thể thay đổi ngày bắt đầu của vòng Phỏng vấn vì đã diễn ra");
                        // Sử dụng ngày bắt đầu hiện tại
                        interviewStartDate = interviewStage.getStartDate();
                    }

                    // Cập nhật vòng hiện có
                    interviewStage.setStartDate(interviewStartDate);
                    interviewStage.setEndDate(interviewEndDate);
                    interviewStage.setStageName("INTERVIEW"); // Đảm bảo dùng ENUM value
                    if (interviewLocationId > 0) {
                        interviewStage.setLocationID(interviewLocationId);
                    }
                    stageDAO.updateRecruitmentStage(interviewStage);
                    logger.log(Level.INFO, "Đã cập nhật vòng INTERVIEW ID {0}", interviewStage.getStageID());
                } else {
                    // Tạo mới nếu không tồn tại
                    interviewStage = new RecruitmentStage();
                    interviewStage.setRecruitmentID(recruitmentId);
                    interviewStage.setStageName("INTERVIEW"); // Dùng ENUM value
                    interviewStage.setStartDate(interviewStartDate);
                    interviewStage.setEndDate(interviewEndDate);
                    // Status sẽ được tính toán dựa trên ngày trong DAO
                    interviewStage.setLocationID(interviewLocationId > 0 ? interviewLocationId : 0);
                    interviewStage.setDescription("Vòng phỏng vấn");
                    int stageId = stageDAO.createRecruitmentStage(interviewStage);
                    logger.log(Level.INFO, "Đã tạo mới vòng INTERVIEW ID {0}", stageId);
                }
            }

            // 3. Vòng Thử thách (Challenge)
            String challengeStartStr = request.getParameter("challengeStageStart");
            String challengeEndStr = request.getParameter("challengeStageEnd");

            if (challengeStartStr != null && !challengeStartStr.isEmpty() && challengeEndStr != null && !challengeEndStr.isEmpty()) {
                logger.log(Level.INFO, "Xử lý vòng Thử thách: {0} - {1}", new Object[]{challengeStartStr, challengeEndStr});

                Date challengeStartDate = dateFormat.parse(challengeStartStr);
                Date challengeEndDate = dateFormat.parse(challengeEndStr);

                // Tìm vòng thử thách hiện có
                RecruitmentStage challengeStage = stageMap.get("CHALLENGE");
                if (challengeStage == null) {
                    challengeStage = stageMap.get("Vòng Thử thách"); // Tương thích với dữ liệu cũ
                }

                if (challengeStage != null) {
                    // Kiểm tra nếu vòng đã bắt đầu, không cho thay đổi ngày bắt đầu
                    boolean stageHasStarted = !currentDate.before(challengeStage.getStartDate());
                    if (stageHasStarted && !dateFormat.format(challengeStage.getStartDate()).equals(challengeStartStr)) {
                        logger.log(Level.WARNING, "Không thể thay đổi ngày bắt đầu của vòng Thử thách vì đã diễn ra");
                        // Sử dụng ngày bắt đầu hiện tại
                        challengeStartDate = challengeStage.getStartDate();
                    }

                    // Cập nhật vòng hiện có
                    challengeStage.setStartDate(challengeStartDate);
                    challengeStage.setEndDate(challengeEndDate);
                    challengeStage.setStageName("CHALLENGE"); // Đảm bảo dùng ENUM value
                    stageDAO.updateRecruitmentStage(challengeStage);
                    logger.log(Level.INFO, "Đã cập nhật vòng CHALLENGE ID {0}", challengeStage.getStageID());
                } else {
                    // Tạo mới nếu không tồn tại
                    challengeStage = new RecruitmentStage();
                    challengeStage.setRecruitmentID(recruitmentId);
                    challengeStage.setStageName("CHALLENGE"); // Dùng ENUM value
                    challengeStage.setStartDate(challengeStartDate);
                    challengeStage.setEndDate(challengeEndDate);
                    // Status sẽ được tính toán dựa trên ngày trong DAO

                    // Get a valid location ID from the database
                    LocationDAO locationDAO = new LocationDAO();
                    List<Locations> locations = locationDAO.getAllLocationsOnCampus();
                    int defaultLocationId = locations.isEmpty() ? 1 : locations.get(0).getLocationID();

                    challengeStage.setLocationID(defaultLocationId);
                    challengeStage.setDescription("Vòng thử thách");
                    int stageId = stageDAO.createRecruitmentStage(challengeStage);
                    logger.log(Level.INFO, "Đã tạo mới vòng CHALLENGE ID {0}", stageId);
                }
            }

            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật các vòng tuyển: {0}", e.getMessage());
            e.printStackTrace();
            return false;
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

        // Log tất cả các tham số request để debug
        logger.log(Level.INFO, "DEBUG - Tất cả các tham số từ request:");
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            logger.log(Level.INFO, "DEBUG - Parameter: {0} = {1}", new Object[]{paramName, paramValue});
        }

        // Thử tất cả các nguồn có thể cho clubId
        String[] possibleParams = {"clubId", "clubID", "club_id", "club-id"};

        for (String param : possibleParams) {
            String value = request.getParameter(param);
            if (value != null && !value.isEmpty()) {
                clubIdParam = value;
                logger.log(Level.INFO, "DEBUG - Đã tìm thấy clubId từ param {0} = {1}", new Object[]{param, value});
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
            logger.log(Level.INFO, "DEBUG - Đã thiết lập clubId={0} cho campaign", clubId);
        } else {
            logger.log(Level.SEVERE, "DEBUG - KHÔNG TÌM THẤY CLUB ID HỢP LỆ TRONG REQUEST!");
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
        logger.log(Level.INFO, "DEBUG - Parsing templateId: {0}", templateId);
        if (templateId != null && !templateId.isEmpty()) {
            try {
                int templateIdInt = Integer.parseInt(templateId);
                campaign.setTemplateID(templateIdInt);
                logger.log(Level.INFO, "DEBUG - Đã set templateID: {0}", templateIdInt);
            } catch (NumberFormatException e) {
                logger.log(Level.SEVERE, "DEBUG - Lỗi parse templateId: {0}", e.getMessage());
                throw new IllegalArgumentException("Form đăng ký không hợp lệ");
            }
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
            Date parsedStartDate = dateFormat.parse(startDate);
            logger.log(Level.INFO, "Parsed start date: {0} -> {1}",
                    new Object[]{startDate, parsedStartDate});
            campaign.setStartDate(parsedStartDate);
        } else {
            throw new IllegalArgumentException("Vui lòng chọn ngày bắt đầu");
        }

        String endDate = request.getParameter("endDate");
        if (endDate != null && !endDate.isEmpty()) {
            Date parsedEndDate = dateFormat.parse(endDate);
            logger.log(Level.INFO, "Parsed end date: {0} -> {1}",
                    new Object[]{endDate, parsedEndDate});
            campaign.setEndDate(parsedEndDate);
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
