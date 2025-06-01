

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
import java.util.logging.Logger;
import java.net.URLEncoder;
import java.util.logging.Logger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userID");
        try{
        // Kiểm tra quyền truy cập (chỉ cho roleId 1-7)
        UserClub userClub = userClubDAO.getUserClubByUserId(userId);
        if (userClub == null || userClub.getRoleID() < 1 || userClub.getRoleID() > 7) {
            response.sendRedirect(request.getContextPath() + "/my-club?error=access_denied");
            return;
        }
            int clubId = userClub.getClubID();

            // Tách thành 2 danh sách lưu form
            List<Map<String, Object>> savedForms = new ArrayList<>();
            List<Map<String, Object>> publishedForms = new ArrayList<>();

            // form đã lưu (published = 0)
            List<ApplicationFormTemplate> savedTemplates = templateDAO.getTemplatesByClubAndStatus(clubId, false);
            for (ApplicationFormTemplate template : savedTemplates) {
                Map<String, Object> formData = new HashMap<>();
                formData.put("templateId", template.getTemplateId());
                formData.put("title", template.getTitle());
                formData.put("formType", template.getFormType());
                formData.put("clubId", template.getClubId());
                formData.put("eventId", template.getEventId());
                formData.put("published", false);
                savedForms.add(formData);
            }

            // form đã xuất bản (Published = 1)
            List<ApplicationFormTemplate> publishedTemplates = templateDAO.getTemplatesByClubAndStatus(clubId, true);
            for (ApplicationFormTemplate template : publishedTemplates) {
                Map<String, Object> formData = new HashMap<>();
                formData.put("templateId", template.getTemplateId());
                formData.put("title", template.getTitle());
                formData.put("formType", template.getFormType());
                formData.put("clubId", template.getClubId());
                formData.put("eventId", template.getEventId());
                formData.put("published", true);

                // Get response count for published forms
                int responseCount = responseDAO.getResponseCountByTemplateId(template.getTemplateId());
                formData.put("responseCount", responseCount);

                publishedForms.add(formData);
            }

            // Set attributes for JSP
            request.setAttribute("savedForms", savedForms);
            request.setAttribute("publishedForms", publishedForms);
            request.setAttribute("savedFormsCount", savedForms.size());
            request.setAttribute("publishedFormsCount", publishedForms.size());

            request.getRequestDispatcher("/view/student/chairman/formManagement.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
             e.printStackTrace();
            LOGGER.log(Level.SEVERE, "SQL Error in FormManagementServlet doGet for user: " + userId, e);
            
            // Encode error message for URL
            String errorMessage = "Lỗi SQL: " + e.getMessage();
            try {
                errorMessage = URLEncoder.encode(errorMessage, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                errorMessage = "Có lỗi xảy ra khi tải dữ liệu form";
            }
            
            response.sendRedirect(request.getContextPath() + "/my-club?error=form_management_error&message=" + errorMessage);
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
        // Kiểm tra quyền truy cập (chỉ cho roleId 1-7)
        UserClub userClub = userClubDAO.getUserClubByUserId(userId);
        if (userClub == null || userClub.getRoleID() < 1 || userClub.getRoleID() > 7) {
            response.sendRedirect(request.getContextPath() + "/my-club?error=access_denied");
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

        LOGGER.info("Processing action: " + action + " for templateId: " + templateId + " by user: " + userId);

        try {
            // Kiểm tra quyền sở hữu template
            ApplicationFormTemplate template = templateDAO.getTemplateById(templateId);
            if (template == null) {
                sendJsonResponse(response, false, "Form không tồn tại.");
                return;
            }

            if (template.getClubId() != userClub.getClubID()) {
                sendJsonResponse(response, false, "Bạn không có quyền thao tác với form này.");
                return;
            }

            switch (action) {
                case "publish":
                    handlePublish(templateId, response);
                    break;
                case "unpublish":
                    handleUnpublish(templateId, response);
                    break;
                case "delete":
                    handleDelete(templateId, response);
                    break;
                default:
                    sendJsonResponse(response, false, "Hành động không hợp lệ.");
                    break;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error processing action: " + action + " for templateId: " + templateId, e);
            sendJsonResponse(response, false, "Có lỗi xảy ra: " + e.getMessage());
        }
    }
    
    private void handlePublish(int templateId, HttpServletResponse response) throws SQLException, IOException {
        templateDAO.publishTemplate(templateId);
        try {
        // Kiểm tra template tồn tại trước khi publish
        ApplicationFormTemplate template = templateDAO.getTemplateById(templateId);
        if (template == null) {
            LOGGER.warning("Template not found for publish: " + templateId);
            sendJsonResponse(response, false, "Form không tồn tại.");
            return;
        }
        
        LOGGER.info("Template found: " + template.getTitle() + " (ClubID: " + template.getClubId() + ")");
        
        // Thực hiện publish
        boolean result = templateDAO.publishTemplate(templateId);
        
        if (result) {
            LOGGER.info("Successfully published template: " + templateId);
            sendJsonResponse(response, true, "Form đã được xuất bản thành công!");
        } else {
            LOGGER.warning("Failed to publish template: " + templateId);
            sendJsonResponse(response, false, "Không thể xuất bản form. Vui lòng thử lại.");
        }
        
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "SQL Error while publishing template: " + templateId, e);
        sendJsonResponse(response, false, "Lỗi cơ sở dữ liệu: " + e.getMessage());
        throw e;
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Unexpected error while publishing template: " + templateId, e);
        sendJsonResponse(response, false, "Lỗi không mong muốn: " + e.getMessage());
        throw new SQLException("Publish failed", e);
    }
    }

    private void handleUnpublish(int templateId, HttpServletResponse response) throws SQLException, IOException {
        templateDAO.unpublishTemplate(templateId);
        LOGGER.info("Successfully unpublished template: " + templateId);
        sendJsonResponse(response, true, "Form đã được hủy xuất bản thành công!");
    }

    private void handleDelete(int templateId, HttpServletResponse response) throws SQLException, IOException {
        try {
        // Kiểm tra template tồn tại
        ApplicationFormTemplate template = templateDAO.getTemplateById(templateId);
        if (template == null) {
            LOGGER.warning("Template not found for delete: " + templateId);
            sendJsonResponse(response, false, "Form không tồn tại.");
            return;
        }
        
        LOGGER.info("Template found: " + template.getTitle() + " (ClubID: " + template.getClubId() + ")");
        
        // Kiểm tra số lượng responses
        int responseCount = responseDAO.getResponseCountByTemplateId(templateId);
        LOGGER.info("Template has " + responseCount + " responses to delete");
        
        // Xóa tất cả responses trước
        if (responseCount > 0) {
            LOGGER.info("Deleting " + responseCount + " responses for template: " + templateId);
            boolean responsesDeleted = responseDAO.deleteResponsesByTemplateId(templateId);
            
            if (!responsesDeleted) {
                LOGGER.warning("Failed to delete responses for template: " + templateId);
                sendJsonResponse(response, false, "Không thể xóa các phản hồi của form.");
                return;
            }
            
            LOGGER.info("Successfully deleted all responses for template: " + templateId);
        }
        
        // Sau đó xóa template
        LOGGER.info("Deleting template: " + templateId);
        boolean templateDeleted = templateDAO.deleteTemplate(templateId);
        
        if (templateDeleted) {
            LOGGER.info("Successfully deleted template: " + templateId);
            sendJsonResponse(response, true, "Form đã được xóa thành công!");
        } else {
            LOGGER.warning("Failed to delete template: " + templateId);
            sendJsonResponse(response, false, "Không thể xóa form. Vui lòng thử lại.");
        }
        
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "SQL Error while deleting template: " + templateId, e);
        sendJsonResponse(response, false, "Lỗi cơ sở dữ liệu: " + e.getMessage());
        throw e;
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Unexpected error while deleting template: " + templateId, e);
        sendJsonResponse(response, false, "Lỗi không mong muốn: " + e.getMessage());
        throw new SQLException("Delete failed", e);
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
