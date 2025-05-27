package controllers;

import dao.ClubDAO;
import dao.UserClubDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Club;
import models.User;
import models.UserClub;

import java.io.IOException;

public class ClubDetailServlet extends HttpServlet {

    private ClubDAO clubDAO;
    private UserClubDAO userClubDAO;

    @Override
    public void init() throws ServletException {
        clubDAO = new ClubDAO();
        userClubDAO = new UserClubDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy clubID từ tham số
        int clubID = Integer.parseInt(request.getParameter("id"));

        // Lấy thông tin câu lạc bộ
        Club club = clubDAO.getClubById(clubID);
        if (club == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Club not found");
            return;
        }

        // Lấy thông tin người dùng từ session
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        boolean isMember = false;
        boolean isPresident = false;
        UserClub userClub = null;

        if (user != null) {
            // Kiểm tra xem người dùng có phải thành viên của câu lạc bộ không
            userClub = userClubDAO.getUserClub(user.getUserID(), clubID);
            if (userClub != null && userClub.isIsActive()) {
                isMember = true;
                // Kiểm tra vai trò chủ nhiệm (RoleID = 1)
                if (userClub.getRoleID() == 1) {
                    isPresident = true;
                }
            }
        }

        // Đặt các thuộc tính để sử dụng trong JSP
        request.setAttribute("club", club);
        request.setAttribute("isMember", isMember);
        request.setAttribute("isPresident", isPresident);
        request.setAttribute("userClub", userClub);

        // Chuyển tiếp đến club-detail.jsp
        request.getRequestDispatcher("./view/admin/club-detail.jsp").forward(request, response);

    }
}
