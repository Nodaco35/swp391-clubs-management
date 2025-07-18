

package controller;

import dal.ApplicationFormDAO;
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
import models.ApplicationForm;
import models.UserClub;

/**
 *
 * @author Vinh
 */
public class FormManagementServlet extends HttpServlet {
    private ApplicationFormTemplateDAO templateDAO;
    private ApplicationResponseDAO responseDAO;
    private UserClubDAO userClubDAO;
    private ApplicationFormDAO applicationFormDAO;
    private static final Logger LOGGER = Logger.getLogger(FormManagementServlet.class.getName());

    @Override
    public void init() {
        templateDAO = new ApplicationFormTemplateDAO();
        responseDAO = new ApplicationResponseDAO();
        userClubDAO = new UserClubDAO();
        applicationFormDAO = new ApplicationFormDAO();
        LOGGER.info("FormManagementServlet initialized");
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
                response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("ID CLB không hợp lệ.", StandardCharsets.UTF_8.name()));
                return;
            }
        }
          try {
            // Kiểm tra nếu clubId là null thì redirect về my-club
            if (clubId == null) {
                response.sendRedirect(request.getContextPath() + "/myclub?error=invalid_club&message=" + 
                    URLEncoder.encode("Vui lòng chọn câu lạc bộ để quản lý form.", StandardCharsets.UTF_8.name()));
                return;
            }

            // Kiểm tra quyền truy cập trong club cụ thể (chỉ cho roleId 1-3)
            UserClub userClub = userClubDAO.getUserClubManagementRole(userId, clubId);
            LOGGER.log(Level.INFO, "Kiểm tra quyền truy cập cho user {0} trong club {1}: {2}", 
                      new Object[]{userId, clubId, userClub != null ? "Có quyền" : "Không có quyền"});
            
            if (userClub == null) {
                response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("Bạn không có quyền quản lý form.", StandardCharsets.UTF_8.name()));
                return;
            }            
            session.setAttribute("userClub", userClub);
            
            LOGGER.log(Level.INFO, "form for clubId: {0}", clubId);
            
            //Danh sách form đã lưu và đã xuất bản
            List<Map<String, Object>> savedForms = templateDAO.getFormsByClubAndStatus(clubId, false);
            LOGGER.log(Level.INFO, "Number of savedForms: {0}", savedForms != null ? savedForms.size() : 0);
            
            List<Map<String, Object>> publishedForms = templateDAO.getFormsByClubAndStatus(clubId, true);
            LOGGER.log(Level.INFO, "Số lượng publishedForms đã lấy: {0}", publishedForms != null ? publishedForms.size() : 0);

        request.setAttribute("savedForms", savedForms);
        request.setAttribute("publishedForms", publishedForms);
        request.setAttribute("savedFormsCount", savedForms.size());
        request.setAttribute("publishedFormsCount", publishedForms.size());

        request.getRequestDispatcher("/view/student/chairman/formManagement.jsp")
                .forward(request, response);

        } catch (SQLException e) {
             LOGGER.log(Level.SEVERE, "SQL Exception in FormManagementServlet", e);
             e.printStackTrace();
            
             // In chi tiết stack trace để debug
             StringBuilder stackTrace = new StringBuilder();
             for (StackTraceElement element : e.getStackTrace()) {
                 stackTrace.append(element.toString()).append("\n");
                 LOGGER.log(Level.SEVERE, "StackTrace: {0}", element.toString());
             }
             LOGGER.log(Level.SEVERE, "SQL Exception complete stack trace: {0}", stackTrace.toString());
            
            // Encode error message for URL
            String errorMessage = "Lỗi SQL: " + e.getMessage();
            try {
                errorMessage = URLEncoder.encode(errorMessage, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                errorMessage = "Có lỗi xảy ra khi tải dữ liệu form";
            }
            response.sendRedirect(request.getContextPath() + "/myclub?error=form_management_error&message=" + errorMessage);
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
            response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("Bạn không có quyền truy cập chức năng này.", StandardCharsets.UTF_8.name()));
            return;
        }
        String action = request.getParameter("action");
        String formIdStr = request.getParameter("formId");
        if (formIdStr == null || formIdStr.trim().isEmpty()) {
            sendJsonResponse(response, false, "Form ID không hợp lệ.");
            return;
        }
        int formId = Integer.parseInt(formIdStr);
        ApplicationFormDAO formDAO = new ApplicationFormDAO();
        ApplicationForm form = formDAO.getFormById(formId);
        if (form == null || form.getClubId() != userClub.getClubID()) {
            sendJsonResponse(response, false, "Bạn không có quyền thao tác với form này.");
            return;
        }
            boolean success = false;
            String message = "";
            switch (action) {
                case "publish":
                    success = applicationFormDAO.publishFormById(formId);
                    message = success ? "Form đã được xuất bản thành công!" : "Không thể xuất bản form.";
                    break;
                case "unpublish":
                    success = applicationFormDAO.unpublishFormById(formId);
                    message = success ? "Form đã được hủy xuất bản thành công!" : "Không thể hủy xuất bản form.";
                    break;
                case "delete":
                    // Kiểm tra xem form đã có người điền chưa
                    boolean hasResponses = responseDAO.hasResponsesByFormId(formId);
                    if (hasResponses) {
                        sendJsonResponse(response, false, "Không thể xóa form đã có người điền!");
                        return;
                    }
                    success = applicationFormDAO.deleteFormById(formId);
                    message = success ? "Form đã được xóa thành công!" : "Không thể xóa form.";
                    break;
                default:
                    sendJsonResponse(response, false, "Hành động không hợp lệ.");
                    return;
            }

            sendJsonResponse(response, success, message);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error processing action in FormManagementServlet", e);
            // Không hiển thị chi tiết lỗi cho người dùng, chỉ log lỗi
            sendJsonResponse(response, false, "Có lỗi xảy ra. Vui lòng thử lại sau.");
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
