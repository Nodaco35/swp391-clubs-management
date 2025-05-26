package controllers;

import dao.ApplicationFormTemplateDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.ApplicationFormTemplate;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "FormListServlet", urlPatterns = {"/formList"})
public class FormListServlet extends HttpServlet {
    private ApplicationFormTemplateDAO formTemplateDAO;

    // Khởi tạo DAO khi Servlet được tải
    @Override
    public void init() throws ServletException {
        formTemplateDAO = new ApplicationFormTemplateDAO();
    }

    // Xử lý yêu cầu GET để hiển thị danh sách form
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Giả sử ClubID của câu lạc bộ hiện tại là 1 (có thể thay bằng giá trị từ session sau)
            int clubId = 1;

            // Lấy danh sách form từ DAO
            List<ApplicationFormTemplate> forms = formTemplateDAO.getTemplatesByClub(clubId);

            // Kiểm tra nếu danh sách rỗng
            if (forms == null || forms.isEmpty()) {
                request.setAttribute("message", "Hiện tại không có form nào cho câu lạc bộ này.");
            } else {
                request.setAttribute("forms", forms);
            }

            // Chuyển tiếp đến trang JSP để hiển thị
            request.getRequestDispatcher("/view/student/chairman/formList.jsp")
                   .forward(request, response);
        } catch (Exception e) {
            // Xử lý lỗi và gửi thông báo đến JSP
            request.setAttribute("error", "Đã xảy ra lỗi khi lấy danh sách form: " + e.getMessage());
            request.getRequestDispatcher("/view/student/chairman/formList.jsp")
                   .forward(request, response);
        }
    }
}