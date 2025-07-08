package controller;

import dal.ClubApplicationDAO;
import dal.CreatedClubApplicationsDAO;
import dal.EventParticipantDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Users;
import java.io.IOException;
import java.util.List;
import models.ClubApplication;
import models.EventParticipation;

public class StudentServlet extends HttpServlet {

    private CreatedClubApplicationsDAO applicationDAO;
    private ClubApplicationDAO clubApplicationDAO;
    private EventParticipantDAO eventParticipantDAO;

    @Override
    public void init() throws ServletException {
        applicationDAO = new CreatedClubApplicationsDAO();
        clubApplicationDAO = new ClubApplicationDAO();
        eventParticipantDAO = new EventParticipantDAO();
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

        if ("history".equals(action) && user != null) {
            List<ClubApplication> clubApplications = clubApplicationDAO.getClubApplicationsByUser(user.getUserID());
            request.setAttribute("clubApplications", clubApplications);

            List<EventParticipation> eventParticipations = eventParticipantDAO.getEventParticipationByUser(user.getUserID());
            request.setAttribute("events", eventParticipations);

            request.getRequestDispatcher("view/student/activity-history.jsp").forward(request, response);
            return;
        }

        if (action == null || !action.equals("sendPermissionRequest")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            return;
        }

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String userId = user.getUserID();
        String clubName = request.getParameter("clubName");
        String categoryParam = request.getParameter("category");
        int categoryID;

        try {
            categoryID = Integer.parseInt(categoryParam);
            if (categoryID <= 0) {
                session.setAttribute("error", "Danh mục không hợp lệ. Vui lòng chọn danh mục hợp lệ.");
                response.sendRedirect(request.getContextPath() + "/clubs");
                return;
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Danh mục không hợp lệ. Vui lòng chọn danh mục hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/clubs");
            return;
        }

        if (userId == null || userId.trim().isEmpty() || clubName == null || clubName.trim().isEmpty()) {
            session.setAttribute("error", "Thông tin không hợp lệ. Vui lòng nhập đầy đủ và đúng định dạng.");
            response.sendRedirect(request.getContextPath() + "/clubs");
            return;
        }

        if (applicationDAO.hasActiveApplication(userId)) {
            session.setAttribute("error", "Bạn đã có quyền tạo câu lạc bộ với trạng thái APPROVED. Vui lòng sử dụng quyền hiện tại trước khi gửi đơn mới.");
        } else if (applicationDAO.isClubNameTaken(clubName)) {
            session.setAttribute("error", "Tên câu lạc bộ '" + clubName + "' đã được sử dụng. Vui lòng chọn tên khác.");
        } else {
            boolean success = applicationDAO.insertRequest(userId, clubName, categoryID);
            session.setAttribute(success ? "message" : "error",
                    success ? "Đơn xin quyền tạo câu lạc bộ '" + clubName + "' đã được gửi!" 
                            : "Gửi đơn thất bại. Vui lòng thử lại sau.");
        }

        response.sendRedirect(request.getContextPath() + "/clubs");
    }

    @Override
    public String getServletInfo() {
        return "Student Servlet for handling student club creation application requests";
    }
}