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
        // Kiểm tra quyền truy cập (chỉ cho roleId 1-3) trong CLB cụ thể
        UserClub userClub = userClubDAO.getUserClubManagementRole(userId, clubId);

        if (userClub == null || userClub.getRoleID() < 1 || userClub.getRoleID() > 3) {
            response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("Bạn không có quyền quản lý form.", StandardCharsets.UTF_8.name()));
            return;
        }
        session.setAttribute(
                "userClub", userClub);

        // Lấy danh sách các ban hoạt động của câu lạc bộ hiện tại
        List<ClubDepartment> clubDepartments = departmentDAO.getCanRegisterClubDepartments(userClub.getClubID());

        request.setAttribute(
                "clubDepartments", clubDepartments);

        String formIdStr = request.getParameter("formId");
        LOGGER.log(Level.INFO, "DEBUG: formIdStr received in doGet: {0}", formIdStr);
        
        if (formIdStr != null && !formIdStr.isEmpty()) {
            try {
                int formId = Integer.parseInt(formIdStr);
                LOGGER.log(Level.INFO, "DEBUG: Attempting to load form with ID: {0}", formId);
                
                ApplicationForm form = formDAO.getFormById(formId);
                LOGGER.log(Level.INFO, "DEBUG: Form loaded from database: {0}", 
                          (form != null ? "ID=" + form.getFormId() + ", Title=" + form.getTitle() : "null"));
                
                if (form != null && form.getClubId() == userClub.getClubID()) {
                    LOGGER.log(Level.INFO, "DEBUG: Form belongs to correct club. ClubID={0}, UserClubID={1}",
                              new Object[]{form.getClubId(), userClub.getClubID()});
                    
                    // Kiểm tra xem form đã có phản hồi nào chưa
                    boolean formHasResponses = false;
                    formHasResponses = responseDAO.hasResponsesByFormId(formId);
                    LOGGER.log(Level.INFO, "DEBUG: Form has responses? {0}", formHasResponses);
                    
                    if (formHasResponses) {
                        // Nếu form đã có phản hồi, không cho phép chỉnh sửa và chuyển hướng về trang quản lý với thông báo
                        LOGGER.log(Level.INFO, "DEBUG: Form has responses, redirecting to formManagement");
                        response.sendRedirect(request.getContextPath() + "/formManagement?clubId=" + form.getClubId() + "&error=edit_denied&message="
                                + URLEncoder.encode("Không thể chỉnh sửa form đã có người điền. Vui lòng tạo form mới.", StandardCharsets.UTF_8.name()));
                        return;
                    }
                    
                    List<ApplicationFormTemplate> formQuestions = formTemplateDAO.getTemplatesByFormId(formId);
                    LOGGER.log(Level.INFO, "DEBUG: Retrieved {0} question templates for form", 
                              formQuestions != null ? formQuestions.size() : 0);
                    
                    if (formQuestions != null && !formQuestions.isEmpty()) {
                        LOGGER.log(Level.INFO, "DEBUG: First question: FieldName={0}, FieldType={1}", 
                                  new Object[]{formQuestions.get(0).getFieldName(), formQuestions.get(0).getFieldType()});
                    }
                    
                    request.setAttribute("formTitleToEdit", form.getTitle());
                    request.setAttribute("formTypeToEdit", form.getFormType());
                    request.setAttribute("formQuestions", formQuestions);
                    request.setAttribute("editingFormId", formId);
                } // Chỉ cho phép chỉnh sửa form của club hiện tại
                else {
                    response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("Bạn không có quyền chỉnh sửa form này.", StandardCharsets.UTF_8.name()));
                    return;
                }
            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, "Invalid formId format: " + formIdStr, e);
                request.setAttribute("errorMessage", "ID form không hợp lệ.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error loading form data", e);
                request.setAttribute("errorMessage", "Không thể tải dữ liệu form: " + e.getMessage());
            }
        }
        // Forward đến trang JSP dù có formId hay không (tạo mới hoặc chỉnh sửa)
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
        
        // Kiểm tra quyền truy cập trong club cụ thể (chỉ cho roleId 1-3)
        UserClub userClub = userClubDAO.getUserClubManagementRole(userId, clubId);
        if (userClub == null || userClub.getRoleID() < 1 || userClub.getRoleID() > 3) {
            response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message="
                    + URLEncoder.encode("Bạn không có quyền quản lý form trong câu lạc bộ này.", StandardCharsets.UTF_8.name()));
            return;
        }
        // Lưu userClub vào session để sử dụng sau này
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
        String formTypeClient = request.getParameter("formType"); // "member" hoặc "event"
        String questionsJson = request.getParameter("questions");
        String editingFormIdStr = request.getParameter("editingFormId"); // ID của form đang sửa, hoặc null/empty nếu tạo mới

        LOGGER.log(Level.INFO, "DEBUG SAVE: Starting saveForm. clubId={0}, publish={1}", new Object[]{clubId, publish});
        LOGGER.log(Level.INFO, "DEBUG SAVE: FormTitle={0}, FormType={1}", new Object[]{formTitle, formTypeClient});
        LOGGER.log(Level.INFO, "DEBUG SAVE: EditingFormId raw value={0}", editingFormIdStr);
        LOGGER.log(Level.INFO, "DEBUG SAVE: EditingFormId is null? {0}", editingFormIdStr == null);
        LOGGER.log(Level.INFO, "DEBUG SAVE: EditingFormId is empty? {0}", editingFormIdStr != null && editingFormIdStr.isEmpty());
        LOGGER.log(Level.INFO, "DEBUG SAVE: QuestionsJson length={0}", questionsJson != null ? questionsJson.length() : 0);
        // Debug tất cả parameters
        LOGGER.log(Level.INFO, "DEBUG SAVE: All request parameters:");
        java.util.Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            LOGGER.log(Level.INFO, "DEBUG SAVE: Parameter {0} = {1}", new Object[]{paramName, paramValue});
        }

        if (formTitle == null || formTitle.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "DEBUG SAVE: Empty form title");
            throw new SQLException("Tiêu đề form không được để trống.");
        }
        if (formTypeClient == null || formTypeClient.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "DEBUG SAVE: Empty form type");
            throw new SQLException("Loại form không được để trống.");
        }
        if (questionsJson == null || questionsJson.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "DEBUG SAVE: Empty questions JSON");
            throw new SQLException("Dữ liệu câu hỏi không hợp lệ.");
        }
        String dbFormType = "member".equalsIgnoreCase(formTypeClient) ? "Club" : "Event";
        JSONArray questionsArray = new JSONArray(questionsJson);
        // Xử lý trường hợp đang chỉnh sửa form
        int formId;
        // Kiểm tra câu hỏi "Chọn ban" cho form loại "member"
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
            LOGGER.log(Level.INFO, "DEBUG SAVE: Editing existing form with ID={0}", editingFormIdStr);
            
            formId = Integer.parseInt(editingFormIdStr);
            ApplicationForm form = formDAO.getFormById(formId);
            
            LOGGER.log(Level.INFO, "DEBUG SAVE: Form data loaded: {0}", 
                       form != null ? "ID=" + form.getFormId() + ", Title=" + form.getTitle() + 
                                      ", Type=" + form.getFormType() + ", ClubId=" + form.getClubId() : "null");
            
            if (form == null) {
                LOGGER.log(Level.SEVERE, "DEBUG SAVE: Form with ID={0} not found in database", formId);
                throw new SQLException("Form không tồn tại.");
            }
            
            boolean hasResponses = responseDAO.hasResponsesByFormId(formId);
            LOGGER.log(Level.INFO, "DEBUG SAVE: Form has responses? {0}", hasResponses);
            
            if (hasResponses) {
                LOGGER.log(Level.WARNING, "DEBUG SAVE: Cannot edit form with responses");
                throw new SQLException("Không thể chỉnh sửa form đã có phản hồi.");
            }
            
            LOGGER.log(Level.INFO, "DEBUG SAVE: Updating form data. Original title={0}, new title={1}",
                      new Object[]{form.getTitle(), formTitle});
            
            form.setTitle(formTitle);
            form.setFormType(dbFormType);
            form.setPublished(publish);
            formDAO.updateForm(form);
            
            // Lấy danh sách template ID hiện có
            List<Integer> existingDbQuestionIds = new ArrayList<>();
            List<ApplicationFormTemplate> existingTemplates = formTemplateDAO.getTemplatesByFormId(formId);
            
            LOGGER.log(Level.INFO, "DEBUG SAVE: Retrieved {0} existing templates for form", 
                       existingTemplates != null ? existingTemplates.size() : 0);
            
            for (ApplicationFormTemplate template : existingTemplates) {
                existingDbQuestionIds.add(template.getTemplateId());
                LOGGER.log(Level.INFO, "DEBUG SAVE: Existing template: ID={0}, FieldName={1}, FieldType={2}", 
                          new Object[]{template.getTemplateId(), template.getFieldName(), template.getFieldType()});
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
            LOGGER.log(Level.INFO, "DEBUG SAVE: Processing question with client ID={0}, label={1}, type={2}",
                      new Object[]{clientId, q.getString("label"), q.getString("type")});
            
            int currentQuestionDbId = -1;
            try {
                currentQuestionDbId = Integer.parseInt(clientId);
                LOGGER.log(Level.INFO, "DEBUG SAVE: Client ID is numeric: {0}", currentQuestionDbId);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.INFO, "DEBUG SAVE: Client ID is not numeric, treating as new question");
                // clientId không phải là số, tức là câu hỏi mới
            }

            if (currentQuestionDbId != -1 && existingDbQuestionIds.contains(currentQuestionDbId)) {
                // Cập nhật câu hỏi hiện có
                LOGGER.log(Level.INFO, "DEBUG SAVE: Updating existing question with ID={0}", currentQuestionDbId);
                template.setTemplateId(currentQuestionDbId);
                formTemplateDAO.updateTemplate(template);
                processedQuestionIds.add(currentQuestionDbId);
                LOGGER.log(Level.INFO, "DEBUG SAVE: Question updated successfully");
            } else {
                // Thêm câu hỏi mới
                LOGGER.log(Level.INFO, "DEBUG SAVE: Saving new question for form ID={0}", formId);
                formTemplateDAO.saveFormTemplate(template);
                LOGGER.log(Level.INFO, "DEBUG SAVE: New question saved successfully");
            }
        }
        // Xóa các câu hỏi không được gửi lại từ client
        LOGGER.log(Level.INFO, "DEBUG SAVE: Checking for templates to delete. Existing IDs count={0}, Processed IDs count={1}",
                  new Object[]{existingDbQuestionIds.size(), processedQuestionIds.size()});
                  
        for (int dbId : existingDbQuestionIds) {
            if (!processedQuestionIds.contains(dbId)) {
                LOGGER.log(Level.INFO, "DEBUG SAVE: Deleting template with ID={0} as it was not returned by client", dbId);
                formTemplateDAO.deleteTemplate(dbId);
            }
        }
        //Tao form moi
        } else {
            LOGGER.log(Level.INFO, "DEBUG SAVE: Creating new form with title={0}, type={1}, clubId={2}, publish={3}", 
                       new Object[]{formTitle, dbFormType, clubId, publish});
                       
            ApplicationForm newForm = new ApplicationForm();
            newForm.setClubId(clubId);
            newForm.setFormType(dbFormType);
            newForm.setTitle(formTitle);
            newForm.setPublished(publish);
            
            formId = formDAO.createForm(newForm);
            LOGGER.log(Level.INFO, "DEBUG SAVE: New form created with ID={0}", formId);
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
