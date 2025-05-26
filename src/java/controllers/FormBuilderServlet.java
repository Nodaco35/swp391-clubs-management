package controllers;

import dao.ApplicationFormTemplateDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import models.ApplicationFormTemplate;
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

    @Override
    public void init() {
        formTemplateDAO = new ApplicationFormTemplateDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        // Uncomment these lines when authentication is ready
        // String userId = (String) session.getAttribute("userId");
        // Integer roleId = (Integer) session.getAttribute("roleId");
        // if (userId == null || !"ADMIN001".equals(userId) || roleId == null || roleId != 1) {
        //     response.sendRedirect("login.jsp");
        //     return;
        // }

        request.getRequestDispatcher("/view/student/chairman/formBuilder.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        // Uncomment when authentication is ready
        // String userId = (String) session.getAttribute("userId");
        // Integer roleId = (Integer) session.getAttribute("roleId");
        // if (userId == null || !"ADMIN001".equals(userId) || roleId == null || roleId != 1) {
        //     response.sendRedirect("login.jsp");
        //     return;
        // }

        String action = request.getParameter("action");
        int clubId = 1; // Get from session when ready

        if ("save".equals(action) || "publish".equals(action)) {
            try {
                saveForm(request, clubId, "publish".equals(action));
                String redirectPath = request.getContextPath() + "/formBuilder?success=true";
                if ("publish".equals(action)) {
                    redirectPath += "&action=publish";
                }
                response.sendRedirect(redirectPath);

            } catch (SQLException ex) {
                ex.printStackTrace();
                Logger.getLogger(FormBuilderServlet.class.getName()).log(Level.SEVERE, null, ex);
                response.sendRedirect(request.getContextPath() + "/formBuilder?error=true");
                //response.sendRedirect("formBuilder?error=" + URLEncoder.encode(ex.getMessage(), "UTF-8"));
            }
        }
    }


    private void saveForm(HttpServletRequest request, int clubId, boolean publish) throws SQLException {
        String formTitle = request.getParameter("formTitle");
        String formType = request.getParameter("formType");
        String questionsJson = request.getParameter("questions");

        JSONArray questions = new JSONArray(questionsJson);
        for (int i = 0; i < questions.length(); i++) {
            JSONObject q = questions.getJSONObject(i);
            ApplicationFormTemplate template = new ApplicationFormTemplate();
            template.setClubId(clubId);
            template.setFormType(formType.equals("member") ? "Club" : "Event");
            template.setTitle(formTitle);
            template.setFieldName(q.getString("label"));
            template.setFieldType(mapFieldType(q.getString("type")));
            template.setRequired(q.getBoolean("required"));

            if (q.has("options")) {
                String options = q.getString("options");
                template.setOptions(options);
            } else {
                template.setOptions(null);
            }

            template.setPublished(publish);
            formTemplateDAO.saveFormTemplate(template);
        }
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
