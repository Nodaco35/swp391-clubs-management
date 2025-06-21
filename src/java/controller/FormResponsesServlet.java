/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dal.UserClubDAO;
import dal.FormResponseDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.UserClub;
import models.ClubApplicationExtended;
import com.google.gson.Gson;

/**
 *
 * @author Vinh
 */
public class FormResponsesServlet extends HttpServlet {
    private FormResponseDAO formResponseDAO;
    private UserClubDAO userClubDAO;
    private static final Logger LOGGER = Logger.getLogger(FormResponsesServlet.class.getName());

    @Override
    public void init() {
        formResponseDAO = new FormResponseDAO();
        userClubDAO = new UserClubDAO();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            System.out.println("FormResponsesServlet.doGet - Starting");
            HttpSession session = request.getSession();
            String userId = (String) session.getAttribute("userID");
            if (userId == null) {
                System.out.println("FormResponsesServlet.doGet - No userID, redirecting to login");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        
        // Lấy templateId từ request parameter
        String templateIdParam = request.getParameter("templateId");
        Integer templateId = null;
        
        if (templateIdParam != null && !templateIdParam.isEmpty()) {
            try {
                templateId = Integer.parseInt(templateIdParam);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/my-club?error=access_denied&message=" + URLEncoder.encode("ID Form không hợp lệ.", StandardCharsets.UTF_8.name()));
                return;
            }
        }
        
        // Kiểm tra nếu templateId là null
        if (templateId == null) {
            response.sendRedirect(request.getContextPath() + "/my-club?error=invalid_form&message=" + 
                    URLEncoder.encode("Vui lòng chọn form để xem phản hồi.", StandardCharsets.UTF_8.name()));
            return;
        }
        
        // Lấy clubId từ request parameter
        String clubIdParam = request.getParameter("clubId");
        Integer clubId = null;
        
        if (clubIdParam != null && !clubIdParam.isEmpty()) {
            try {
                clubId = Integer.parseInt(clubIdParam);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/my-club?error=access_denied&message=" + URLEncoder.encode("ID CLB không hợp lệ.", StandardCharsets.UTF_8.name()));
                return;
            }
        }
        
        // Kiểm tra nếu clubId là null thì redirect về my-club
        if (clubId == null) {
            response.sendRedirect(request.getContextPath() + "/my-club?error=invalid_club&message=" + 
                    URLEncoder.encode("Vui lòng chọn câu lạc bộ để quản lý form.", StandardCharsets.UTF_8.name()));
            return;
        }
        
        // Kiểm tra quyền truy cập trong club cụ thể (chỉ cho roleId 1-3)
        UserClub userClub = userClubDAO.getUserClubManagementRole(userId, clubId);
        if (userClub == null) {
            response.sendRedirect(request.getContextPath() + "/my-club?error=access_denied&message=" + URLEncoder.encode("Bạn không có quyền quản lý form trong CLB này.", StandardCharsets.UTF_8.name()));
            return;
        }
        
        // Xác định loại form (member hoặc event)
        String formTypeParam = request.getParameter("formType");
        String formType = "member"; // Mặc định là member
        
        
        if (formTypeParam != null && formTypeParam.toLowerCase().equals("event")) {
            formType = "event";
        }
        
        System.out.println("Final formType being used: " + formType);
        
        // Lấy danh sách đơn đăng ký cho form template và club này
        try {
            List<ClubApplicationExtended> applications = formResponseDAO.getApplicationsByTemplateAndClub(templateId, clubId);
            System.out.println("Found " + applications.size() + " applications");
            
            // Chuyển đổi dữ liệu thành JSON để JavaScript có thể sử dụng
            Gson gson = new Gson();
            String applicationsJson = gson.toJson(applications);
            
            request.setAttribute("applicationsJson", applicationsJson);
            
            // Thêm danh sách applications để JSP có thể sử dụng
            request.setAttribute("applications", applications);
        } catch (Exception e) {
            System.err.println("Error getting applications: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("applicationsJson", "[]"); // Empty array as fallback
        }
        
        // Thêm thông tin templateId, clubId và formType để sử dụng trong trang formResponse.jsp
        request.setAttribute("templateId", templateId);
        request.setAttribute("clubId", clubId);
        request.setAttribute("formType", formType);
        
        System.out.println("FormResponsesServlet.doGet - Forwarding to formResponse.jsp with attributes:");
        System.out.println("- templateId: " + templateId);
        System.out.println("- clubId: " + clubId);
        System.out.println("- formType: " + formType);
        
        request.getRequestDispatcher("/view/student/chairman/formResponse.jsp")
                .forward(request, response);
    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("errorMessage", "Đã xảy ra lỗi khi lấy dữ liệu. Vui lòng thử lại sau.");
        request.setAttribute("applicationsJson", "[]"); // Empty array as fallback
        request.getRequestDispatcher("/view/student/chairman/formResponse.jsp")
                .forward(request, response);
    }
        
    } 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        
        try {
            // Kiểm tra đăng nhập
            HttpSession session = request.getSession();
            String userId = (String) session.getAttribute("userID");
            if (userId == null) {
                out.print(gson.toJson(new ApiResponse(false, "Bạn cần đăng nhập để thực hiện thao tác này.")));
                return;
            }
            
            // Lấy thông tin từ request
            String action = request.getParameter("action");
            String responseIdStr = request.getParameter("responseId");
            String clubIdStr = request.getParameter("clubId");
            
            System.out.println("FormResponsesServlet.doPost - Action: " + action + ", ResponseID: " + responseIdStr);
            
            if (action == null || responseIdStr == null || clubIdStr == null) {
                out.print(gson.toJson(new ApiResponse(false, "Thiếu tham số cần thiết.")));
                return;
            }
            
            // Parse IDs
            int responseId;
            int clubId;
            
            try {
                responseId = Integer.parseInt(responseIdStr);
                clubId = Integer.parseInt(clubIdStr);
            } catch (NumberFormatException e) {
                out.print(gson.toJson(new ApiResponse(false, "ID không hợp lệ.")));
                return;
            }
            
            // Kiểm tra quyền của user trong club
            UserClub userClub = userClubDAO.getUserClubManagementRole(userId, clubId);
            if (userClub == null) {
                out.print(gson.toJson(new ApiResponse(false, "Bạn không có quyền quản lý trong câu lạc bộ này.")));
                return;
            }
            
            boolean success = false;
            String message = "";
            String newStatus = "";
            
            // Thực hiện hành động dựa trên tham số action
            if ("approve".equals(action)) {
                // Lấy thông tin đơn hiện tại
                ClubApplicationExtended application = formResponseDAO.getApplicationById(responseId);
                if (application == null) {
                    out.print(gson.toJson(new ApiResponse(false, "Không tìm thấy đơn đăng ký.")));
                    return;
                }
                
                // Xử lý duyệt đơn
                success = formResponseDAO.approveApplication(responseId);
                
                // Lấy trạng thái mới sau khi duyệt
                if (success) {
                    application = formResponseDAO.getApplicationById(responseId);
                    if (application != null) {
                        newStatus = application.getResponseStatus().toLowerCase();
                        
                        // Hiển thị thông báo phù hợp với trạng thái mới
                        switch (newStatus) {
                            case "approved":
                                message = "event".equalsIgnoreCase(application.getFormType()) ? 
                                        "Đã duyệt đăng ký sự kiện thành công" : 
                                        "Đã nâng cấp thành thành viên chính thức";
                                break;
                            case "candidate":
                                message = "Đã duyệt thành ứng viên";
                                break;
                            case "collaborator":
                                message = "Đã nâng cấp thành cộng tác viên";
                                break;
                            default:
                                message = "Đã cập nhật trạng thái thành công";
                        }
                    } else {
                        message = "Đã duyệt đơn thành công";
                    }
                } else {
                    message = "Không thể duyệt đơn. Vui lòng thử lại sau.";
                }
            } else if ("reject".equals(action)) {
                // Xử lý từ chối đơn
                success = formResponseDAO.rejectApplication(responseId);
                message = success ? "Đã từ chối đơn đăng ký" : "Không thể từ chối đơn. Vui lòng thử lại sau.";
                newStatus = "rejected";
            } else {
                out.print(gson.toJson(new ApiResponse(false, "Hành động không hợp lệ.")));
                return;
            }
            
            // Trả về kết quả
            ApiResponse response1 = new ApiResponse(success, message);
            response1.setStatus(newStatus);
            out.print(gson.toJson(response1));
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("application/json");
            response.setStatus(200); // Use 200 instead of 500 to ensure the response is processed by fetch
            out.print(gson.toJson(new ApiResponse(false, "Đã xảy ra lỗi: " + e.getMessage())));
        }
    }
    
    // Lớp hỗ trợ để trả về API response dạng JSON
    private static class ApiResponse {
        private boolean success;
        private String message;
        private String status;
        
        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
