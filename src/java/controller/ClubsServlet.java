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
            handleClubDetail(request, response);
        } else {
            handleClubsList(request, response);
        }
    }

    private void handleClubsList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String category = request.getParameter("category");
        if (category == null || category.isEmpty()) {
            category = "all";
        }

        // Get page parameter, default to 1
        int page;
        try {
            page = Integer.parseInt(request.getParameter("page"));
            if (page < 1) page = 1;
        } catch (NumberFormatException e) {
            page = 1;
        }

        // Set page size (4 clubs per page)
        int pageSize = 4;

        // Fetch clubs based on category
        List<Clubs> clubs;
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        String userID = (user != null) ? user.getUserID() : null;

        if ("myClubs".equalsIgnoreCase(category)) {
            if (user == null || userID == null) {
                // Redirect to login page or show all clubs if user is not logged in
                clubs = clubDAO.getClubsByCategory("all", page, pageSize);
                category = "all";
            } else {
                clubs = clubDAO.getUserClubs(userID, page, pageSize);
            }
        } else {
            clubs = clubDAO.getClubsByCategory(category, page, pageSize);
        }

        // Calculate total pages
        int totalClubs = clubDAO.getTotalClubsByCategory(category, userID);
        int totalPages = (int) Math.ceil((double) totalClubs / pageSize);

        // Set attributes for JSP
        request.setAttribute("clubs", clubs);
        request.setAttribute("selectedCategory", category);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);

        // Forward to clubs.jsp
        request.getRequestDispatcher("view/clubs-page/club-management.jsp").forward(request, response);
    }

    private void handleClubDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int clubID;
        try {
            clubID = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid club ID");
            return;
        }

        Clubs club = clubDAO.getClubById(clubID);
        if (club == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Club not found");
            return;
        }

        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        boolean isMember = false;
        boolean isPresident = false;
        UserClub userClub = null;

        if (user != null) {
            userClub = userClubDAO.getUserClub(user.getUserID(), clubID);
            if (userClub != null && userClub.isIsActive()) {
                isMember = true;
                if (userClub.getRoleID() == 1) {
                    isPresident = true;
                }
            }
        }

        request.setAttribute("club", club);
        request.setAttribute("isMember", isMember);
        request.setAttribute("isPresident", isPresident);
        request.setAttribute("userClub", userClub);

        request.getRequestDispatcher("/view/clubs-page/club-detail.jsp").forward(request, response);
    }
}