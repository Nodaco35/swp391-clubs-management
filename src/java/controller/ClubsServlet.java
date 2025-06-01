package controller;

import dal.ClubDAO;
import dal.UserClubDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Clubs;
import models.Users;
import models.UserClub;
import java.io.IOException;
import java.util.List;

public class ClubsServlet extends HttpServlet {

    private ClubDAO clubDAO;
    private UserClubDAO userClubDAO;

    @Override
    public void init() throws ServletException {
        clubDAO = new ClubDAO();
        userClubDAO = new UserClubDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/club-detail".equals(path)) {
            // Handle club detail view
            handleClubDetail(request, response);
        } else {
            // Handle clubs list view (default to /clubs)
            handleClubsList(request, response);
        }
    }

    private void handleClubsList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get category parameter, default to "all"
        String category = request.getParameter("category");
        if (category == null || category.isEmpty()) {
            category = "all";
        }

        // Fetch clubs based on category
        List<Clubs> clubs = clubDAO.getClubsByCategory(category);
        request.setAttribute("clubs", clubs);
        request.setAttribute("selectedCategory", category);

        // Forward to clubs.jsp
        request.getRequestDispatcher("view/clubs-page/club-management.jsp").forward(request, response);
    }

    private void handleClubDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy clubID từ tham số
        int clubID;
        try {
            clubID = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid club ID");
            return;
        }

        // Lấy thông tin câu lạc bộ
        Clubs club = clubDAO.getClubById(clubID);
        if (club == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Club not found");
            return;
        }

        // Lấy thông tin người dùng từ session
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
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
        request.getRequestDispatcher("/view/clubs-page/club-detail.jsp").forward(request, response);
    }
}