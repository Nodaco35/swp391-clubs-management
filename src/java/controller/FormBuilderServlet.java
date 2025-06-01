package controller;

import dal.ApplicationFormTemplateDAO;
import dal.UserClubDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import models.ApplicationFormTemplate;
import models.UserClub;
import models.Users;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "FormBuilderServlet", urlPatterns = {"/formBuilder"})
public class FormBuilderServlet extends HttpServlet {
    private ApplicationFormTemplateDAO formTemplateDAO;
    private UserClubDAO userClubDAO;
    private static final Logger LOGGER = Logger.getLogger(FormBuilderServlet.class.getName());

    @Override
    public void init() {
        formTemplateDAO = new ApplicationFormTemplateDAO();
        userClubDAO = new UserClubDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        String userId = (String) session.getAttribute("userID");
        // Kiểm tra quyền truy cập (chỉ cho roleId 1-7)
        UserClub userClub = userClubDAO.getUserClubByUserId(userId);
        if (userClub == null || userClub.getRoleID() < 1 || userClub.getRoleID() > 7) {
            response.sendRedirect(request.getContextPath() + "/my-club?error=access_denied");
            return;
        }
        LOGGER.info("User " + userId + " accessing form builder with role " + userClub.getRoleID() + " in club " + userClub.getClubID());

        // Lưu thông tin club vào session để sử dụng
        session.setAttribute("userClub", userClub);
        request.getRequestDispatcher("/view/student/chairman/formBuilder.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        String userId = (String) session.getAttribute("userID");

        // Lấy thông tin user club từ session hoặc database
        UserClub userClub = (UserClub) session.getAttribute("userClub");
        if (userClub == null) {
            userClub = userClubDAO.getUserClubByUserId(userId);
        }

        // Kiểm tra quyền truy cập
        if (userClub == null || userClub.getRoleID() < 1 || userClub.getRoleID() > 7) {
            response.sendRedirect(request.getContextPath() + "/my-club?error=access_denied");
            return;
        }

        String action = request.getParameter("action");
        int clubId = userClub.getClubID();

        LOGGER.info("Processing form action: " + action + " for user: " + userId + " in club: " + clubId);

        if ("save".equals(action) || "publish".equals(action)) {
            try {
                saveForm(request, clubId, "publish".equals(action));
                String redirectPath = request.getContextPath() + "/formBuilder?success=true";
                if ("publish".equals(action)) {
                    redirectPath += "&action=publish";
                }
                response.sendRedirect(redirectPath);

            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error saving form for user " + userId + " in club " + clubId, ex);
                response.sendRedirect(request.getContextPath() + "/formBuilder?error=true&message=" + URLEncoder.encode(ex.getMessage(), "UTF-8"));
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Unexpected error in form builder", ex);
                response.sendRedirect(request.getContextPath() + "/formBuilder?error=true&message=" + URLEncoder.encode("Đã xảy ra lỗi không mong muốn", "UTF-8"));
            }
        }
    }

    private void saveForm(HttpServletRequest request, int clubId, boolean publish) throws SQLException {
        String formTitle = request.getParameter("formTitle");
        String formType = request.getParameter("formType");
        String questionsJson = request.getParameter("questions");

        LOGGER.info("Saving form - Title: " + formTitle + ", Type: " + formType + ", ClubID: " + clubId + ", Publish: " + publish);

        // Validate input
        if (formTitle == null || formTitle.trim().isEmpty()) {
            throw new SQLException("Tiêu đề form không được để trống");
        }

        if (formType == null || formType.trim().isEmpty()) {
            throw new SQLException("Loại form không được để trống");
        }

        if (questionsJson == null || questionsJson.trim().isEmpty()) {
            throw new SQLException("Dữ liệu câu hỏi không hợp lệ: questionsJson rỗng hoặc null");
        }

        JSONArray questions;
        try {
            questions = new JSONArray(questionsJson);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error parsing questions JSON: " + questionsJson, e);
            throw new SQLException("Lỗi phân tích JSON: " + e.getMessage());
        }

        if (questions.length() == 0) {
            throw new SQLException("Danh sách câu hỏi rỗng");
        }

        LOGGER.info("Processing " + questions.length() + " questions");

        for (int i = 0; i < questions.length(); i++) {
            try {
                JSONObject q = questions.getJSONObject(i);
                ApplicationFormTemplate template = new ApplicationFormTemplate();
                template.setClubId(clubId);
                template.setFormType(formType.equals("member") ? "Club" : "Event");
                template.setTitle(formTitle);
                template.setFieldName(q.getString("label"));
                template.setFieldType(mapFieldType(q.getString("type")));
                template.setRequired(q.getBoolean("required"));

                // Xử lý options
                String optionsString = null;
                if (q.has("options")) {
                    Object optionsObj = q.get("options");
                    if (optionsObj instanceof JSONArray) {
                        // Nếu là JSONArray (cho radio, checkbox, select)
                        JSONArray optionsArray = (JSONArray) optionsObj;
                        StringBuilder optionsBuilder = new StringBuilder();
                        for (int j = 0; j < optionsArray.length(); j++) {
                            JSONObject option = optionsArray.getJSONObject(j);
                            if (j > 0) optionsBuilder.append(",");
                            optionsBuilder.append(option.getString("value"));
                        }
                        optionsString = optionsBuilder.toString();
                    } else if (optionsObj instanceof String) {
                        // Nếu là String (cho info type với JSON content)
                        optionsString = (String) optionsObj;
                    }
                }
                template.setOptions(optionsString);
                template.setPublished(publish);

                LOGGER.info("Saving question " + (i+1) + ": " + template.getFieldName() + " (" + template.getFieldType() + ")");
                formTemplateDAO.saveFormTemplate(template);

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error processing question " + (i+1), e);
                throw new SQLException("Lỗi xử lý câu hỏi " + (i+1) + ": " + e.getMessage());
            }
        }

        LOGGER.info("Successfully saved form with " + questions.length() + " questions");
    }

    private String mapFieldType(String jsType) {
        switch (jsType) {
            case "text": return "Text";
            case "textarea": return "Textarea";
            case "email": return "Email";
            case "select": return "Dropdown";
            case "tel": return "PhoneNumber";
            case "number": return "Number";
            case "date": return "Date";
            case "radio": return "Radio";
            case "checkbox": return "Checkbox";
            case "info": return "Info";
            default: return "Text";
        }
    }
}