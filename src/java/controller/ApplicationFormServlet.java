package controller;

import dal.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.ApplicationFormTemplate;
import models.ApplicationResponse;
import models.ClubApplication;

/**
 *
 * @author Vinh
 */
public class ApplicationFormServlet extends HttpServlet {
    private ApplicationFormTemplateDAO formDAO;
    private ApplicationResponseDAO responseDAO;
    private ClubApplicationDAO clubDAO;
   
    @Override
    public void init() throws ServletException {
        formDAO = new ApplicationFormTemplateDAO();
        responseDAO = new ApplicationResponseDAO();
        clubDAO = new ClubApplicationDAO();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userID");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        // Lấy templateId từ query parameter
        String templateIdStr = request.getParameter("templateId");
        if (templateIdStr == null || templateIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/formManagement?error=access_denied&message=" + URLEncoder.encode("Link form không hợp lệ", StandardCharsets.UTF_8.name()));
            return;
        }

        try {
            int repTemplateId = Integer.parseInt(templateIdStr);

            // Lấy template đại diện
            ApplicationFormTemplate rep = formDAO.getTemplateById(repTemplateId);
            if (rep == null || !rep.isPublished()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Form not found or not published");
                return;
            }

            String formTitle = rep.getTitle();
            int    clubId    = rep.getClubId();
            String formType  = rep.getFormType();

            // Lấy danh sách câu hỏi cùng group (cùng title & clubId) và đã publish
            List<ApplicationFormTemplate> questions = formDAO
                    .getPublishedTemplatesByGroup(formTitle, clubId);

            if (questions.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Form has no questions");
                return;
            }

            // Sắp xếp câu hỏi theo displayOrder trước khi gửi xuống JSP
            questions.sort((q1, q2) -> {
                // Sắp xếp theo displayOrder trước, nếu bằng nhau thì sắp xếp theo templateId
                int orderComparison = Integer.compare(q1.getDisplayOrder(), q2.getDisplayOrder());
                return orderComparison != 0 ? orderComparison : Integer.compare(q1.getTemplateId(), q2.getTemplateId());
            });
            
            // Forward dữ liệu xuống JSP
            request.setAttribute("formTitle", formTitle);
            request.setAttribute("formType", formType);
            request.setAttribute("questions", questions);
            request.setAttribute("templateId", repTemplateId);
            request.setAttribute("clubId", clubId);
            request.setAttribute("eventId", rep.getEventId());
            request.getRequestDispatcher("/view/student/applicationForm.jsp")
                    .forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid form link: templateId must be a number");
        } catch (SQLException e) {
            throw new ServletException(e);
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
        // Lấy thông tin người dùng từ session
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userID");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy templateId từ form data
        String templateIdStr = request.getParameter("templateId");
        if (templateIdStr == null || templateIdStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã biểu mẫu không hợp lệ");
            return;
        }
        
        int templateId;
        try {
            templateId = Integer.parseInt(templateIdStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã biểu mẫu không hợp lệ");
            return;
        }
        
        try {
            // Lấy template đại diện
            ApplicationFormTemplate template = formDAO.getTemplateById(templateId);
            if (template == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy biểu mẫu");
                return;
            }
            
            int clubId = template.getClubId();
            Integer eventId = template.getEventId();
            String formType = template.getFormType();
            String title = template.getTitle();
            
            // Lấy tất cả câu hỏi từ nhóm biểu mẫu
            List<ApplicationFormTemplate> questions = formDAO.getPublishedTemplatesByGroup(title, clubId);
            
            // Sắp xếp câu hỏi theo displayOrder
            questions.sort((q1, q2) -> {
                int orderComparison = Integer.compare(q1.getDisplayOrder(), q2.getDisplayOrder());
                return orderComparison != 0 ? orderComparison : Integer.compare(q1.getTemplateId(), q2.getTemplateId());
            });
            
            // Lấy dữ liệu JSON từ form
            String responsesJsonString = request.getParameter("responsesJson");
            
            // Nếu responsesJson không được cung cấp, tạo JSON từ các tham số form
            if (responsesJsonString == null || responsesJsonString.isEmpty()) {
                // Thu thập câu trả lời và tạo JSON theo displayOrder
                StringBuilder responsesJson = new StringBuilder("{");
                
                for (ApplicationFormTemplate question : questions) {
                    String fieldName = "ans_" + question.getTemplateId();
                    String fieldType = question.getFieldType();
                    
                    // Log để debug
                    System.out.println("Debug - Đang xử lý câu hỏi ID " + question.getTemplateId() + 
                        ", loại: " + fieldType + ", thứ tự hiển thị: " + question.getDisplayOrder());
                    
                    // Xử lý các loại trường khác nhau
                    if ("Checkbox".equalsIgnoreCase(fieldType)) {
                        // Xử lý checkbox: có thể có nhiều giá trị
                        String[] values = request.getParameterValues(fieldName);
                        if (values != null && values.length > 0) {
                            responsesJson.append("\"").append(fieldName).append("\":[");
                            for (String value : values) {
                                responsesJson.append("\"").append(value.replace("\"", "\\\"")).append("\",");
                            }
                            // Xóa dấu phẩy cuối cùng
                            responsesJson.setLength(responsesJson.length() - 1);
                            responsesJson.append("],");
                            
                            // Log để debug
                            System.out.println("Debug - Checkbox " + fieldName + " có " + values.length + " giá trị được chọn");
                        } else {
                            responsesJson.append("\"").append(fieldName).append("\":[],");
                            System.out.println("Debug - Checkbox " + fieldName + " không có giá trị nào được chọn");
                        }
                    } else if ("Radio".equalsIgnoreCase(fieldType)) {
                        // Xử lý radio và dropdown: một giá trị
                        String value = request.getParameter(fieldName);
                        if (value != null && !value.isEmpty()) {
                            responsesJson.append("\"").append(fieldName).append("\":\"")
                                      .append(value.replace("\"", "\\\"")).append("\",");
                            System.out.println("Debug - " + fieldType + " " + fieldName + " chọn giá trị: " + value);
                        } else {
                            responsesJson.append("\"").append(fieldName).append("\":\"\",");
                            System.out.println("Debug - " + fieldType + " " + fieldName + " không có giá trị được chọn");
                        }
                    } else if ("Info".equalsIgnoreCase(fieldType)) {
                        // Không xử lý trường thông tin vì chỉ hiển thị, không cần lưu
                        System.out.println("Debug - Bỏ qua trường Info " + fieldName);
                        continue; // Chuyển sang câu hỏi tiếp theo
                    } else {
                        // Xử lý các loại trường thông thường khác
                        String value = request.getParameter(fieldName);
                        if (value != null) {
                            responsesJson.append("\"").append(fieldName).append("\":\"")
                                      .append(value.replace("\"", "\\\"")).append("\",");
                            System.out.println("Debug - Trường " + fieldType + " " + fieldName + 
                                " có giá trị: " + (value.length() > 50 ? value.substring(0, 50) + "..." : value));
                        } else {
                            responsesJson.append("\"").append(fieldName).append("\":\"\",");
                            System.out.println("Debug - Trường " + fieldType + " " + fieldName + " không có giá trị");
                        }
                    }
                }
                
                // Thêm metadata với thời gian chính xác
                java.util.Calendar cal = java.util.Calendar.getInstance();
                java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(cal.getTimeInMillis());
                System.out.println("Debug - Timestamp for metadata: " + currentTimestamp);
                
                responsesJson.append("\"_metadata\":{")
                          .append("\"submitTime\":\"").append(currentTimestamp).append("\",")
                          .append("\"formTitle\":\"").append(title.replace("\"", "\\\"")).append("\",")
                          .append("\"formType\":\"").append(formType.replace("\"", "\\\"")).append("\"")
                          .append("}");
                
                responsesJson.append("}");
                
                responsesJsonString = responsesJson.toString();
            }
            
            // Lưu vào ApplicationResponses
            ApplicationResponse appResponse = new ApplicationResponse();
            appResponse.setTemplateID(templateId);
            appResponse.setUserID(userId);
            appResponse.setClubID(clubId);
            appResponse.setEventID(eventId);
            appResponse.setResponses(responsesJsonString);
            appResponse.setStatus("Pending");
            int responseId = responseDAO.saveResponse(appResponse);
            
            // Lưu vào ClubApplications
            ClubApplication clubApp = new ClubApplication();
            clubApp.setUserId(userId);
            clubApp.setClubId(clubId);
            clubApp.setEventId(eventId);
            clubApp.setResponseId(responseId);
            clubApp.setStatus("PENDING");
            
            // Tạo timestamp hiện tại với giờ chính xác
            java.util.Calendar cal = java.util.Calendar.getInstance();
            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(cal.getTimeInMillis());
            clubApp.setSubmitDate(currentTimestamp);
            
            System.out.println("Debug - Đặt thời gian gửi đơn: " + currentTimestamp);
            
            // Tìm email từ các câu trả lời form
            String email = null;
            
            // Lặp qua các câu hỏi để tìm trường email
            for (ApplicationFormTemplate question : questions) {
                if ("Email".equalsIgnoreCase(question.getFieldType())) {
                    String fieldName = "ans_" + question.getTemplateId();
                    String value = request.getParameter(fieldName);
                    if (value != null && !value.isEmpty() && value.contains("@")) {
                        email = value;
                        System.out.println("Debug - Đã tìm thấy email từ form: " + email);
                        break;
                    }
                }
            }
            
            // Nếu không tìm thấy trong form, sử dụng email từ session
            if (email == null || email.isEmpty()) {
                email = (String) session.getAttribute("userEmail");
                System.out.println("Debug - Sử dụng email từ session: " + email);
            }
            
            // Đặt email, đảm bảo không null
            clubApp.setEmail(email != null ? email : "");
            
            clubDAO.saveClubApplication(clubApp);
            
            // Chuyển hướng hoặc hiển thị thành công
            response.sendRedirect(request.getContextPath() + "/applicationForm?success=true&templateId=" + templateId);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Handles application form for club/event membership";
    }// </editor-fold>

}