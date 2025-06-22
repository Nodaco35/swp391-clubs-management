package controller;

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

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


@WebServlet(name = "FormBuilderServlet", urlPatterns = {"/formBuilder"})
public class FormBuilderServlet extends HttpServlet {    private ApplicationFormTemplateDAO formTemplateDAO;
    private UserClubDAO userClubDAO;
    private ClubDepartmentDAO departmentDAO;
    private ApplicationResponseDAO responseDAO;
    private static final Logger LOGGER = Logger.getLogger(FormBuilderServlet.class.getName());    @Override
    public void init() {
        formTemplateDAO = new ApplicationFormTemplateDAO(); // Khởi tạo DAO
        userClubDAO = new UserClubDAO();
        departmentDAO = new ClubDepartmentDAO(); // Khởi tạo DAO cho ClubDepartments
        responseDAO = new ApplicationResponseDAO(); // Khởi tạo Response DAO
    }    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userID");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }        
        
        // Lấy clubId từ request parameter nếu có
        String clubIdParam = request.getParameter("clubId");
        Integer clubId = null;
        
        if (clubIdParam != null && !clubIdParam.isEmpty()) {
            try {
                clubId = Integer.parseInt(clubIdParam);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("ID CLB không hợp lệ.", StandardCharsets.UTF_8.name()));
                return;
            }
        } else {
            // Nếu không có clubId, chuyển hướng về trang myClub để chọn CLB
            response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("Bạn không có quyền truy cập chức năng này.", StandardCharsets.UTF_8.name()));
            return;
        }
          // Kiểm tra quyền truy cập (chỉ cho roleId 1-3) trong CLB cụ thể
        UserClub userClub = userClubDAO.getUserClubManagementRole(userId, clubId);
        if (userClub == null || userClub.getRoleID() < 1 || userClub.getRoleID() > 3) {
            response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("Bạn không có quyền quản lý form.", StandardCharsets.UTF_8.name()));
            return;
        }
        session.setAttribute("userClub", userClub);
        
        // Lấy danh sách các ban hoạt động của câu lạc bộ hiện tại
        List<ClubDepartment> clubDepartments = departmentDAO.getActiveClubDepartments(userClub.getClubID());
        request.setAttribute("clubDepartments", clubDepartments);

        String templateIdStr = request.getParameter("templateId");

        if (templateIdStr != null && !templateIdStr.isEmpty()) {
            try {
                //ID của một câu hỏi bất kỳ trong form, dùng để lấy title
                int representativeTemplateId = Integer.parseInt(templateIdStr);
                ApplicationFormTemplate representativeQuestion = formTemplateDAO.getTemplateById(representativeTemplateId);                if (representativeQuestion != null) {
                    String formTitle = representativeQuestion.getTitle();
                    String formType = representativeQuestion.getFormType(); // 'Club' hoặc 'Event'
                    int clubIdForForm = representativeQuestion.getClubId();

                    // Chỉ cho phép chỉnh sửa form của club hiện tại
                    if (clubIdForForm != userClub.getClubID()) {
                        response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("Bạn không có quyền chỉnh sửa form này.", StandardCharsets.UTF_8.name()));
                        return;
                    }                    // Kiểm tra xem form đã có phản hồi nào chưa
                    boolean formHasResponses = false;
                    try {
                        formHasResponses = responseDAO.hasResponsesByFormTitle(formTitle, clubIdForForm);
                        if (formHasResponses) {
                            // Nếu form đã có phản hồi, không cho phép chỉnh sửa và chuyển hướng về trang quản lý với thông báo
                            response.sendRedirect(request.getContextPath() + "/formManagement?clubId=" + clubIdForForm + "&error=edit_denied&message=" + 
                                URLEncoder.encode("Không thể chỉnh sửa form đã có người điền. Vui lòng tạo form mới.", StandardCharsets.UTF_8.name()));
                            return;
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error checking form responses: " + e.getMessage(), e);
                    }

                    // Lấy tất cả các câu hỏi (ApplicationFormTemplate) có cùng title và clubId
                    List<ApplicationFormTemplate> formQuestions = formTemplateDAO.getTemplatesByGroup(formTitle, clubIdForForm, representativeTemplateId);

                    if (formQuestions != null && !formQuestions.isEmpty()) {
                        LOGGER.info("Found " + formQuestions.size() + " questions for title: " + formTitle);
                    } else {
                        LOGGER.warning("No questions found for title: " + formTitle + " and clubId: " + clubIdForForm);
                    }

                    request.setAttribute("formTitleToEdit", formTitle);
                    request.setAttribute("formTypeToEdit", formType);
                    request.setAttribute("formQuestions", formQuestions);
                    // editingTemplateId là ID đại diện cho form đang sửa,
                    request.setAttribute("editingTemplateId", representativeTemplateId); // ID của một câu hỏi trong form

                } else {
                    request.setAttribute("errorMessage", "Không tìm thấy mẫu form với ID cung cấp.");
                }
            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, "Invalid templateId format: " + templateIdStr, e);
                request.setAttribute("errorMessage", "ID mẫu form không hợp lệ.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error loading form data for editing", e);
                request.setAttribute("errorMessage", "Không thể tải dữ liệu form: " + e.getMessage());
            }
        } else {
            LOGGER.info("Creating a new form.");
        }
        request.getRequestDispatcher("/view/student/chairman/formBuilder.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userID");

        UserClub userClub = (UserClub) session.getAttribute("userClub");
        if (userClub == null) {
            userClub = userClubDAO.getUserClubByUserId(userId);
            if (userClub != null) {
                session.setAttribute("userClub", userClub);
            }
        }

        if (userClub == null || userClub.getRoleID() < 1 || userClub.getRoleID() > 3) {
            response.sendRedirect(request.getContextPath() + "/myclub?error=access_denied&message=" + URLEncoder.encode("Bạn không có quyền truy cập chức năng này.", StandardCharsets.UTF_8.name()));
            return;
        }

        String action = request.getParameter("action");
        int clubId = userClub.getClubID();        if ("save".equals(action) || "publish".equals(action)) {
            try {
                // Kiểm tra formTitle trước
                String formTitle = request.getParameter("formTitle");
                String editingTemplateIdStr = request.getParameter("editingTemplateId");
                boolean isEditing = editingTemplateIdStr != null && !editingTemplateIdStr.isEmpty();
                
                // Nếu đang chỉnh sửa form hiện có, cần kiểm tra xem form có phản hồi chưa
                if (isEditing) {
                    try {
                        int representativeTemplateId = Integer.parseInt(editingTemplateIdStr);
                        ApplicationFormTemplate template = formTemplateDAO.getTemplateById(representativeTemplateId);
                        
                        if (template != null) {                            boolean formHasResponses = responseDAO.hasResponsesByFormTitle(template.getTitle(), template.getClubId());
                            if (formHasResponses) {
                                // Nếu form đã có phản hồi, không cho phép chỉnh sửa và chuyển hướng về trang quản lý với thông báo
                                response.sendRedirect(request.getContextPath() + "/formManagement?clubId=" + clubId + "&error=edit_denied&message=" + 
                                    URLEncoder.encode("Không thể chỉnh sửa form đã có người điền. Vui lòng tạo form mới.", StandardCharsets.UTF_8.name()));
                                return;
                            }
                        }
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Error checking form responses: " + e.getMessage(), e);
                    }
                }
                
                saveForm(request, clubId, "publish".equals(action));
                String redirectPath = request.getContextPath() + "/formBuilder?success=true";
                if ("publish".equals(action)) {
                    redirectPath += "&action=publish";
                }
                response.sendRedirect(redirectPath);            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error in form builder (doPost): " + ex.getMessage(), ex);
                response.sendRedirect(request.getContextPath() + "/formBuilder?error=true&message=" + 
                    URLEncoder.encode("Đã xảy ra lỗi không mong muốn: " + ex.getMessage(), StandardCharsets.UTF_8.name()));
            }
        }
    }

    private void saveForm(HttpServletRequest request, int clubId, boolean publish) throws Exception {
        String formTitleFromInput = request.getParameter("formTitle");
        String formTypeClient = request.getParameter("formType"); // "member" hoặc "event"
        String questionsJson = request.getParameter("questions");
        String editingTemplateIdStr = request.getParameter("editingTemplateId"); // ID của một câu hỏi trong form đang sửa, hoặc null/empty nếu tạo mới


        if (formTitleFromInput == null || formTitleFromInput.trim().isEmpty()) throw new SQLException("Tiêu đề form không được để trống.");
        if (formTypeClient == null || formTypeClient.trim().isEmpty()) throw new SQLException("Loại form không được để trống.");
        if (questionsJson == null || questionsJson.trim().isEmpty()) throw new SQLException("Dữ liệu câu hỏi không hợp lệ.");

        JSONArray questionsArray = new JSONArray(questionsJson);
        String dbFormType = "member".equalsIgnoreCase(formTypeClient) ? "Club" : "Event";

        // Xác định title thực sự của form (quan trọng khi đổi tên form)
        String originalFormTitleForEdit = formTitleFromInput; // Mặc định là title mới từ input
        if (editingTemplateIdStr != null && !editingTemplateIdStr.isEmpty()) {
            try {
                int repId = Integer.parseInt(editingTemplateIdStr);
                ApplicationFormTemplate repQuestion = formTemplateDAO.getTemplateById(repId);
                if (repQuestion != null && repQuestion.getClubId() == clubId) {
                    originalFormTitleForEdit = repQuestion.getTitle(); // Lấy title gốc từ DB
                }
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid editingTemplateIdStr: " + editingTemplateIdStr + ". Treating as new title.");
            }
        }


        List<Integer> existingDbQuestionIds = new ArrayList<>();
        int editingTemplateId = -1;
        if (editingTemplateIdStr != null && !editingTemplateIdStr.isEmpty()) {
            try {
                editingTemplateId = Integer.parseInt(editingTemplateIdStr);

                // === SỬA: Lấy TemplateID lớn nhất (true max) của form cũ, thay vì dùng editingTemplateId ===
                int trueMaxId = formTemplateDAO.getMaxTemplateIdForForm(originalFormTitleForEdit, clubId);
                existingDbQuestionIds = formTemplateDAO
                        .getTemplateIdsByGroup(originalFormTitleForEdit, clubId, trueMaxId);

            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid editingTemplateIdStr in saveForm: " + editingTemplateIdStr);
            }
        }


        Set<Integer> processedQuestionIds = new HashSet<>();   
        Set<String> foundRequiredTypes = new HashSet<>();
        
        // Duyệt qua các câu hỏi từ form
        for (int i = 0; i < questionsArray.length(); i++) {
            JSONObject q = questionsArray.getJSONObject(i);
            String label = q.getString("label");
            String type = q.getString("type");
            
            // Kiểm tra nếu là câu hỏi bắt buộc dựa vào nhãn và loại
            boolean isFullnameQuestion = label.equals("Họ và tên") && type.equals("text");
            boolean isEmailQuestion = label.equals("Email") && type.equals("email");
            boolean isDepartmentQuestion = label.contains("Chọn ban") && type.equals("radio");
            
            if (isFullnameQuestion) {
                foundRequiredTypes.add("fullname");
                q.put("required", true);
            } else if (isEmailQuestion) {
                foundRequiredTypes.add("email");
                q.put("required", true);
            } else if (isDepartmentQuestion) {
                foundRequiredTypes.add("department");
                q.put("required", true);
            }
            
            ApplicationFormTemplate template = new ApplicationFormTemplate();
            template.setClubId(clubId);
            template.setFormType(dbFormType);
            template.setTitle(formTitleFromInput); // Luôn dùng title mới từ input
            template.setFieldName(label);
            template.setFieldType(mapFieldTypeClientToServer(q.getString("type")));
            template.setIsRequired(q.getBoolean("required"));
            
            // Lấy display order từ JSON (đã được thêm trong getFormData của formBuilder.js)
            if (q.has("displayOrder")) {
                template.setDisplayOrder(q.getInt("displayOrder"));
            } else {
                // Mặc định là thứ tự trong mảng JSON nếu không có displayOrder
                template.setDisplayOrder(i);
            }

            String optionsString = null;
            if (q.has("options")) {
                Object optionsObj = q.get("options");
                if (optionsObj instanceof JSONArray) {
                    JSONArray clientOptionsArray = (JSONArray) optionsObj;
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < clientOptionsArray.length(); j++) {
                        JSONObject opt = clientOptionsArray.getJSONObject(j);
                        if (j > 0) sb.append(";;"); // SỬA: Dùng ;; làm phân tách
                        sb.append(opt.getString("value"));
                    }
                    optionsString = sb.toString();
                } else if (optionsObj instanceof String) {
                    optionsString = (String) optionsObj; // Cho 'info' type
                }
            }
            template.setOptions(optionsString);
            template.setPublished(publish);

            String clientId = q.getString("id"); // ID từ client: có thể là DB ID (số) hoặc temp ID (chuỗi)
            int currentQuestionDbId = -1;
            try {
                currentQuestionDbId = Integer.parseInt(clientId);
            } catch (NumberFormatException e) {
                // clientId không phải là số, tức là câu hỏi mới
            }

            if (currentQuestionDbId != -1 && existingDbQuestionIds.contains(currentQuestionDbId)) {
                // Câu hỏi đã tồn tại -> Cập nhật
                template.setTemplateId(currentQuestionDbId);
                formTemplateDAO.updateTemplate(template);
                processedQuestionIds.add(currentQuestionDbId);
                LOGGER.fine("Updated question ID: " + currentQuestionDbId + " with new title: " + formTitleFromInput);
            } else {
                int newId = formTemplateDAO.saveFormTemplateAndGetId(template); // Giả sử DAO trả về ID
                LOGGER.fine("Saved new question with ID: " + newId + " for title: " + formTitleFromInput);
            }        }
          // Kiểm tra câu hỏi "Chọn ban" chỉ bắt buộc cho form member
        if ("member".equals(formTypeClient) && !foundRequiredTypes.contains("department")) {
            throw new SQLException("Form đăng ký thành viên phải bao gồm câu hỏi chọn ban");
        }

        if (editingTemplateIdStr != null && !editingTemplateIdStr.isEmpty()) {
            for (int dbId : existingDbQuestionIds) {
                if (!processedQuestionIds.contains(dbId)) {
                    formTemplateDAO.deleteTemplate(dbId);
                }
            }

        }
        
    }


    private String mapFieldTypeClientToServer(String jsType) {
        switch (jsType.toLowerCase()) {
            case "text": return "Text";
            case "textarea": return "Textarea";
            case "email": return "Email";
            case "number": return "Number";
            case "date": return "Date";
            case "radio": return "Radio";
            case "checkbox": return "Checkbox";
            case "info": return "Info";
            default:
                LOGGER.warning("Unknown JS field type: " + jsType + ". Defaulting to Text.");
                return "Text";
        }
    }
}
