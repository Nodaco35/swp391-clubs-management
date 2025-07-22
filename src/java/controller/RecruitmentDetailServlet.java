package controller;

import dal.ApplicationStageDAO;
import dal.UserClubDAO;
import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.*;
import service.RecruitmentService;

/**
 * Servlet để xử lý việc xem chi tiết hoạt động tuyển quân
 * Các chức năng API đã được chuyển sang RecruitmentApiServlet
 */
public class RecruitmentDetailServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(RecruitmentDetailServlet.class.getName());
    private final RecruitmentService recruitmentService = new RecruitmentService();
    private final UserClubDAO userClubDAO = new UserClubDAO();
    private final ApplicationStageDAO applicationStageDAO = new ApplicationStageDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Đảm bảo charset UTF-8 cho request và response
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // Xử lý trang chi tiết hoạt động tuyển quân
        handleDetailPageRequest(request, response);
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
                
                // DEBUG: In ra chi tiết từng giai đoạn và thống kê của nó
                logger.log(Level.INFO, "============ DEBUG STAGE STATS ============");
                for (Integer stageId : stageStats.keySet()) {
                    java.util.Map<String, Integer> stats = stageStats.get(stageId);
                    logger.log(Level.INFO, "Stage ID: {0}, Stats: TOTAL={1}, PENDING={2}, APPROVED={3}, REJECTED={4}", 
                        new Object[]{stageId, 
                                    stats.getOrDefault("TOTAL", 0),
                                    stats.getOrDefault("PENDING", 0), 
                                    stats.getOrDefault("APPROVED", 0), 
                                    stats.getOrDefault("REJECTED", 0)});
                }
                logger.log(Level.INFO, "=========================================");
                
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

    // Helper methods cho trang chi tiết hoạt động tuyển quân
    private String getUserName(String userId) {
        try {
            return userDAO.getUserName(userId);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error getting user name for userId: " + userId, e);
            return null;
        }
    }
}
