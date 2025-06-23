

package controller;

import dal.ApplicationFormTemplateDAO;
import dal.ApplicationResponseDAO;
import dal.UserClubDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import models.ApplicationFormTemplate;
import models.UserClub;

/**
 *
 * @author Vinh
 */
public class FormManagementServlet extends HttpServlet {
    private ApplicationFormTemplateDAO templateDAO;
    private ApplicationResponseDAO responseDAO;
    private UserClubDAO userClubDAO;
    private static final Logger LOGGER = Logger.getLogger(FormBuilderServlet.class.getName());

    @Override
    public void init() {
        templateDAO = new ApplicationFormTemplateDAO();
        responseDAO = new ApplicationResponseDAO();
        userClubDAO = new UserClubDAO();
    }    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userID");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy clubId từ request parameter
        String clubIdParam = request.getParameter("clubId");
        Integer clubId = null;
        
        if (clubIdParam != null && !clubIdParam.isEmpty()) {
            try {
                clubId = Integer.parseInt(clubIdParam);
            } catch (NumberFormatException e) {
<<<<<<< HEAD
                response.sendRedirect(request.getContextPath() + "/my-club?error=access_denied&message=" + URLEncoder.encode("ID CLB không hợp lệ.", StandardCharsets.UTF_8.name()));
=======
                response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("ID CLB không hợp lệ.", StandardCharsets.UTF_8.name()));
>>>>>>> 80d5538cff8a23b3f10d295a4cb3eec2de29f265
                return;
            }
        }
          try {
            // Kiểm tra nếu clubId là null thì redirect về my-club
            if (clubId == null) {
<<<<<<< HEAD
                response.sendRedirect(request.getContextPath() + "/my-club?error=invalid_club&message=" + 
=======
                response.sendRedirect(request.getContextPath() + "/myclub?error=invalid_club&message=" + 
>>>>>>> 80d5538cff8a23b3f10d295a4cb3eec2de29f265
                    URLEncoder.encode("Vui lòng chọn câu lạc bộ để quản lý form.", StandardCharsets.UTF_8.name()));
                return;
            }

            // Kiểm tra quyền truy cập trong club cụ thể (chỉ cho roleId 1-3)
            UserClub userClub = userClubDAO.getUserClubManagementRole(userId, clubId);
            if (userClub == null) {
<<<<<<< HEAD
                response.sendRedirect(request.getContextPath() + "/my-club?error=access_denied&message=" + URLEncoder.encode("Bạn không có quyền quản lý form.", StandardCharsets.UTF_8.name()));
=======
                response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("Bạn không có quyền quản lý form.", StandardCharsets.UTF_8.name()));
>>>>>>> 80d5538cff8a23b3f10d295a4cb3eec2de29f265
                return;
            }            
            session.setAttribute("userClub", userClub);
            // Use the templateDAO already initialized
            List<Map<String, Object>> savedForms = templateDAO.getFormsByClubAndStatus(clubId, false);
            List<Map<String, Object>> publishedForms = templateDAO.getFormsByClubAndStatus(clubId, true);

        request.setAttribute("savedForms", savedForms);
        request.setAttribute("publishedForms", publishedForms);
        request.setAttribute("savedFormsCount", savedForms.size());
        request.setAttribute("publishedFormsCount", publishedForms.size());

        request.getRequestDispatcher("/view/student/chairman/formManagement.jsp")
                .forward(request, response);

        } catch (SQLException e) {
             e.printStackTrace();            
            // Encode error message for URL
            String errorMessage = "Lỗi SQL: " + e.getMessage();
            try {
                errorMessage = URLEncoder.encode(errorMessage, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                errorMessage = "Có lỗi xảy ra khi tải dữ liệu form";
            }
<<<<<<< HEAD
            response.sendRedirect(request.getContextPath() + "/my-club?error=form_management_error&message=" + errorMessage);
=======
            response.sendRedirect(request.getContextPath() + "/myclub?error=form_management_error&message=" + errorMessage);
>>>>>>> 80d5538cff8a23b3f10d295a4cb3eec2de29f265
            return;
        }
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userID");
        try{
        // Kiểm tra quyền truy cập (chỉ cho roleId 1-3)
        UserClub userClub = userClubDAO.getUserClubByUserId(userId);
        if (userClub == null || userClub.getRoleID() < 1 || userClub.getRoleID() > 3) {
<<<<<<< HEAD
            response.sendRedirect(request.getContextPath() + "/my-club?error=access_denied&message=" + URLEncoder.encode("Bạn không có quyền truy cập chức năng này.", StandardCharsets.UTF_8.name()));
=======
            response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("Bạn không có quyền truy cập chức năng này.", StandardCharsets.UTF_8.name()));
>>>>>>> 80d5538cff8a23b3f10d295a4cb3eec2de29f265
            return;
        }
        String action = request.getParameter("action");
        String templateIdStr = request.getParameter("templateId");
        
        if (templateIdStr == null || templateIdStr.trim().isEmpty()) {
            sendJsonResponse(response, false, "Template ID không hợp lệ.");
            return;
        }

        int templateId;
        try {
                templateId = Integer.parseInt(templateIdStr);
            } catch (NumberFormatException e) {
                sendJsonResponse(response, false, "Template ID không hợp lệ.");
                return;
            }
            // Lấy title của form từ templateId
            ApplicationFormTemplate template = templateDAO.getTemplateById(templateId);
            if (template == null) {
                sendJsonResponse(response, false, "Form không tồn tại.");
                return;
            }
            String title = template.getTitle();

            // Kiểm tra quyền sở hữu
            if (template.getClubId() != userClub.getClubID()) {
                sendJsonResponse(response, false, "Bạn không có quyền thao tác với form này.");
                return;
            }

            boolean success = false;
            String message = "";
            switch (action) {
                case "publish":
                    success = templateDAO.publishFormsByTitle(title);
                    message = success ? "Form đã được xuất bản thành công!" : "Không thể xuất bản form.";
                    break;
                case "unpublish":
                    success = templateDAO.unpublishFormsByTitle(title);
                    message = success ? "Form đã được hủy xuất bản thành công!" : "Không thể hủy xuất bản form.";
                    break;
                case "delete":
                    success = templateDAO.deleteFormsByTitle(title);
                    message = success ? "Form đã được xóa thành công!" : "Không thể xóa form.";
                    break;
                default:
                    sendJsonResponse(response, false, "Hành động không hợp lệ.");
                    return;
            }

            sendJsonResponse(response, success, message);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error processing action in FormManagementServlet", e);
            sendJsonResponse(response, false, "Có lỗi xảy ra: " + e.getMessage());
        }
    }
    private void sendJsonResponse(HttpServletResponse response, boolean success, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        out.print("{\"success\": " + success + ", \"message\": \"" + message + "\"}");
        out.flush();
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
