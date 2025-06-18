package controller;

import dal.ClubCreationPermissionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Users;
import java.io.IOException;

public class StudentServlet extends HttpServlet {

    private ClubCreationPermissionDAO permissionDAO;

    @Override
    public void init() throws ServletException {
        permissionDAO = new ClubCreationPermissionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        String action = request.getParameter("action");

        if (action == null || !action.equals("sendPermissionRequest")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            return;
        }

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String userId = user.getUserID();
        if (userId == null || userId.trim().isEmpty()) {
            session.setAttribute("error", "Thông tin người dùng không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/clubs");
            return;
        }

        if (permissionDAO.hasPendingRequest(userId)) {
            session.setAttribute("error", "Bạn đã có đơn đang chờ xử lý. Vui lòng đợi xử lý trước khi gửi đơn mới.");
        } else if (permissionDAO.hasActivePermission(userId)) {
            session.setAttribute("error", "Bạn đã có quyền tạo câu lạc bộ.");
        } else {
            boolean success = permissionDAO.insertRequest(userId);
            session.setAttribute(success ? "message" : "error", 
                success ? "Đơn xin quyền tạo câu lạc bộ đã được gửi!" : "Gửi đơn thất bại. Vui lòng thử lại sau.");
        }

        response.sendRedirect(request.getContextPath() + "/clubs");
    }

    @Override
    public String getServletInfo() {
        return "Student Servlet for handling student permission requests";
    }
}