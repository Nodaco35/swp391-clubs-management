package controller;

import dal.ApplicationFormDAO;
import dal.ApplicationFormTemplateDAO;
import dal.ApplicationResponseDAO;
import dal.ClubDepartmentDAO;
import dal.UserClubDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import models.ApplicationFormTemplate;
import models.ClubDepartment;
import models.UserClub;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.ApplicationForm;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "FormBuilderServlet", urlPatterns = {"/formBuilder"})
public class FormBuilderServlet extends HttpServlet {

    private ApplicationFormTemplateDAO formTemplateDAO;
    private UserClubDAO userClubDAO;
    private ClubDepartmentDAO departmentDAO;
    private ApplicationResponseDAO responseDAO;
    private static final Logger LOGGER = Logger.getLogger(FormBuilderServlet.class.getName());
    private ApplicationFormDAO formDAO;

    @Override
    public void init() {
        formTemplateDAO = new ApplicationFormTemplateDAO(); // Khởi tạo DAO cho templates
        formDAO = new ApplicationFormDAO(); // Khởi tạo DAO cho forms
        userClubDAO = new UserClubDAO();
        departmentDAO = new ClubDepartmentDAO(); // Khởi tạo DAO cho ClubDepartments
        responseDAO = new ApplicationResponseDAO(); // Khởi tạo Response DAO
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userID");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Lấy clubId từ request parameter
        String clubIdParam = request.getParameter("clubId");
        Integer clubId = null;

        try {
            clubId = Integer.parseInt(clubIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("ID CLB không hợp lệ.", StandardCharsets.UTF_8.name()));
            return;
        }
        // Kiểm tra quyền truy cập trong CLB cụ thể
        UserClub userClub = userClubDAO.getUserClubManagementRole(userId, clubId);

        if (userClub == null || userClub.getRoleID() < 1 || userClub.getRoleID() > 3) {
            response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("Bạn không có quyền quản lý form.", StandardCharsets.UTF_8.name()));
            return;
        }
        session.setAttribute("userClub", userClub);

        // Lấy danh sách các ban hoạt động của câu lạc bộ hiện tại
        List<ClubDepartment> clubDepartments = departmentDAO.getCanRegisterClubDepartments(userClub.getClubID());

        request.setAttribute(
                "clubDepartments", clubDepartments);

        String formIdStr = request.getParameter("formId");
        LOGGER.log(Level.INFO, "DEBUG: formIdStr received in doGet: {0}", formIdStr);
        
        if (formIdStr != null && !formIdStr.isEmpty()) {
            try {
                int formId = Integer.parseInt(formIdStr);
                ApplicationForm form = formDAO.getFormById(formId);
                if (form != null && form.getClubId() == userClub.getClubID()) {
                    boolean formHasResponses = false;
                    formHasResponses = responseDAO.hasResponsesByFormId(formId);
                    if (formHasResponses) {
                        // Nếu form đã có phản hồi, không cho phép chỉnh sửa và chuyển hướng về trang quản lý với thông báo
                        response.sendRedirect(request.getContextPath() + "/formManagement?clubId=" + form.getClubId() + "&error=edit_denied&message="
                                + URLEncoder.encode("Không thể chỉnh sửa form đã có người điền. Vui lòng tạo form mới.", StandardCharsets.UTF_8.name()));
                        return;
                    }
                    
                    List<ApplicationFormTemplate> formQuestions = formTemplateDAO.getTemplatesByFormId(formId);
                    request.setAttribute("formTitleToEdit", form.getTitle());
                    request.setAttribute("formTypeToEdit", form.getFormType());
                    request.setAttribute("formQuestions", formQuestions);
                    request.setAttribute("editingFormId", formId);
                }
                else {
                    response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("Bạn không có quyền chỉnh sửa form này.", StandardCharsets.UTF_8.name()));
                    return;
                }
            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, "Invalid formId format: " + formIdStr, e);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error loading form data", e);
            }
        }
        request.getRequestDispatcher("/view/student/chairman/formBuilder.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userID");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        // Lấy clubId từ request parameter
        String clubIdParam = request.getParameter("clubId");
        Integer clubId = null;
        
        try {
            clubId = Integer.parseInt(clubIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + 
                URLEncoder.encode("ID CLB không hợp lệ.", StandardCharsets.UTF_8.name()));
            return;
        }
        
        UserClub userClub = userClubDAO.getUserClubManagementRole(userId, clubId);
        if (userClub == null || userClub.getRoleID() < 1 || userClub.getRoleID() > 3) {
            response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message="
                    + URLEncoder.encode("Bạn không có quyền quản lý form trong câu lạc bộ này.", StandardCharsets.UTF_8.name()));
            return;
        }
        session.setAttribute("userClub", userClub);
        String action = request.getParameter("action");
        if ("save".equals(action) || "publish".equals(action)) {
            try {
                int savedFormId = saveForm(request, clubId, "publish".equals(action));
                String redirectPath = request.getContextPath() + "/formBuilder?success=true&clubId=" + clubId;
                
                // Nếu có formId được trả về từ saveForm, thêm vào URL để tiếp tục chỉnh sửa
                if (savedFormId > 0) {
                    redirectPath += "&formId=" + savedFormId;
                }
                
                if ("publish".equals(action)) {
                    redirectPath += "&action=publish";
                }
                response.sendRedirect(redirectPath);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error saving form: " + ex.getMessage(), ex);
                
                // Lấy editingFormId để redirect về form hiện tại nếu có lỗi
                String editingFormIdStr = request.getParameter("editingFormId");
                String errorRedirectPath = request.getContextPath() + "/formBuilder?error=true&clubId=" + clubId;
                
                if (editingFormIdStr != null && !editingFormIdStr.isEmpty()) {
                    errorRedirectPath += "&formId=" + editingFormIdStr;
                }
                
                errorRedirectPath += "&message=" + URLEncoder.encode("Lỗi khi lưu form: " + ex.getMessage(), StandardCharsets.UTF_8.name());
                
                response.sendRedirect(errorRedirectPath);
            }
        }
    }

    private int saveForm(HttpServletRequest request, int clubId, boolean publish) throws Exception {
        String formTitle = request.getParameter("formTitle");
        String formTypeClient = request.getParameter("formType");
        String questionsJson = request.getParameter("questions");
        String editingFormIdStr = request.getParameter("editingFormId"); // ID của form đang sửa, hoặc null/empty nếu tạo mới

        if (formTitle == null || formTitle.trim().isEmpty()) {
            throw new SQLException("Tiêu đề form không được để trống.");
        }
        if (formTypeClient == null || formTypeClient.trim().isEmpty()) {
            throw new SQLException("Loại form không được để trống.");
        }
        if (questionsJson == null || questionsJson.trim().isEmpty()) {
            throw new SQLException("Dữ liệu câu hỏi không hợp lệ.");
        }
        String dbFormType = "member".equalsIgnoreCase(formTypeClient) ? "Club" : "Event";
        JSONArray questionsArray = new JSONArray(questionsJson);
        // Xử lý trường hợp đang chỉnh sửa form
        int formId;

        boolean hasDepartmentQuestion = false;
        if ("member".equalsIgnoreCase(formTypeClient)) {
            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject q = questionsArray.getJSONObject(i);
                String label = q.getString("label");
                String type = q.getString("type");
                if (label.contains("Chọn ban") && type.equals("radio")) {
                    hasDepartmentQuestion = true;
                    break;
                }
            }
            if (!hasDepartmentQuestion) {
                throw new SQLException("Form đăng ký thành viên phải bao gồm câu hỏi chọn ban.");
            }
        }

        if (editingFormIdStr != null && !editingFormIdStr.isEmpty()) {
            
            formId = Integer.parseInt(editingFormIdStr);
            ApplicationForm form = formDAO.getFormById(formId);      
            if (form == null) {
                LOGGER.log(Level.SEVERE, "DEBUG SAVE: Form with ID={0} not found in database", formId);
                throw new SQLException("Form không tồn tại.");
            }
            
            boolean hasResponses = responseDAO.hasResponsesByFormId(formId);            
            if (hasResponses) {
                LOGGER.log(Level.WARNING, "DEBUG SAVE: Cannot edit form with responses");
                throw new SQLException("Không thể chỉnh sửa form đã có phản hồi.");
            }
            
            form.setTitle(formTitle);
            form.setFormType(dbFormType);
            form.setPublished(publish);
            formDAO.updateForm(form);
            
            // Lấy danh sách template ID hiện có
            List<Integer> existingDbQuestionIds = new ArrayList<>();
            List<ApplicationFormTemplate> existingTemplates = formTemplateDAO.getTemplatesByFormId(formId);
            
            for (ApplicationFormTemplate template : existingTemplates) {
                existingDbQuestionIds.add(template.getTemplateId());
            }
            // Duyệt qua các câu hỏi từ client và cập nhật/thêm mới
        Set<Integer> processedQuestionIds = new HashSet<>();
        for (int i = 0; i < questionsArray.length(); i++) {
            JSONObject q = questionsArray.getJSONObject(i);
            ApplicationFormTemplate template = new ApplicationFormTemplate();
            template.setFormId(formId);
            template.setFieldName(q.getString("label"));
            template.setFieldType(mapFieldTypeClientToServer(q.getString("type")));
            template.setRequired(q.getBoolean("required"));
            template.setDisplayOrder(i);

            // Xử lý tùy chọn (options) nếu có
            if (q.has("options")) {
                Object optionsObj = q.get("options");
                if (optionsObj instanceof JSONArray) {
                    JSONArray optionsArray = (JSONArray) optionsObj;
                    StringBuilder options = new StringBuilder();
                    for (int j = 0; j < optionsArray.length(); j++) {
                        if (j > 0) options.append(";;");
                        options.append(optionsArray.getJSONObject(j).getString("value"));
                    }
                    template.setOptions(options.toString());
                } else if (optionsObj instanceof String) {
                    template.setOptions((String) optionsObj);
                }
            }

            // Kiểm tra xem câu hỏi đã tồn tại trong DB chưa
            String clientId = q.getString("id");
            
            int currentQuestionDbId = -1;
            try {
                currentQuestionDbId = Integer.parseInt(clientId);
            } catch (NumberFormatException e) {
            }

            if (currentQuestionDbId != -1 && existingDbQuestionIds.contains(currentQuestionDbId)) {
                // Cập nhật câu hỏi hiện có
                template.setTemplateId(currentQuestionDbId);
                formTemplateDAO.updateTemplate(template);
                processedQuestionIds.add(currentQuestionDbId);
            } else {
                // Thêm câu hỏi mới
                formTemplateDAO.saveFormTemplate(template);
            }
        }
        // Xóa các câu hỏi không được gửi lại từ client
        for (int dbId : existingDbQuestionIds) {
            if (!processedQuestionIds.contains(dbId)) {
                formTemplateDAO.deleteTemplate(dbId);
            }
        }
        //Tao form moi
        } else {
            ApplicationForm newForm = new ApplicationForm();
            newForm.setClubId(clubId);
            newForm.setFormType(dbFormType);
            newForm.setTitle(formTitle);
            newForm.setPublished(publish);
            
            formId = formDAO.createForm(newForm);
        // Thêm các câu hỏi mới
        for (int i = 0; i < questionsArray.length(); i++) {
            JSONObject q = questionsArray.getJSONObject(i);
            ApplicationFormTemplate template = new ApplicationFormTemplate();
            template.setFormId(formId);
            template.setFieldName(q.getString("label"));
            template.setFieldType(mapFieldTypeClientToServer(q.getString("type")));
            template.setRequired(q.getBoolean("required"));
            template.setDisplayOrder(i);

            // Xử lý tùy chọn (options) nếu có
            if (q.has("options")) {
                Object optionsObj = q.get("options");
                if (optionsObj instanceof JSONArray) {
                    JSONArray optionsArray = (JSONArray) optionsObj;
                    StringBuilder options = new StringBuilder();
                    for (int j = 0; j < optionsArray.length(); j++) {
                        if (j > 0) options.append(";;");
                        options.append(optionsArray.getJSONObject(j).getString("value"));
                    }
                    template.setOptions(options.toString());
                } else if (optionsObj instanceof String) {
                    template.setOptions((String) optionsObj);
                }
            }
            formTemplateDAO.saveFormTemplate(template);
        }
    }
    
    // Return the formId to be used for redirect
    return formId;
    }

    private String mapFieldTypeClientToServer(String jsType) {
        return switch (jsType.toLowerCase()) {
            case "text" ->
                "Text";
            case "textarea" ->
                "Textarea";
            case "email" ->
                "Email";
            case "number" ->
                "Number";
            case "date" ->
                "Date";
            case "radio" ->
                "Radio";
            case "checkbox" ->
                "Checkbox";
            case "info" ->
                "Info";
            default -> {
                LOGGER.log(Level.WARNING, "Unknown JS field type: {0}. Defaulting to Text.", jsType);
                yield "Text";
            }
        };
    }
}
