package controller;

import dal.ClubCreationPermissionDAO;
import dal.ClubDAO;
import dal.ClubCategoryDAO;
import dal.DepartmentMemberDAO;
import dal.UserClubDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Clubs;
import models.Users;
import models.UserClub;
import models.ClubCategory;
import java.io.IOException;
import java.util.List;

public class ClubsServlet extends HttpServlet {

    private ClubDAO clubDAO;
    private UserClubDAO userClubDAO;
    private ClubCreationPermissionDAO permissionDAO;
    private ClubCategoryDAO clubCategoryDAO;

    @Override
    public void init() throws ServletException {
        clubDAO = new ClubDAO();
        userClubDAO = new UserClubDAO();
        permissionDAO = new ClubCreationPermissionDAO();
        clubCategoryDAO = new ClubCategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        String action = request.getParameter("action");

        if ("/club-detail".equals(path)) {
            handleClubDetail(request, response);
        } else if (action != null && (action.equals("addFavorite") || action.equals("removeFavorite"))) {
            handleFavoriteAction(request, response);
        } else {
            handleClubsList(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private void handleClubsList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String categoryParam = request.getParameter("category");
        int categoryID;
        String selectedCategoryName = "all"; // Default for display purposes

        // Fetch all categories
        List<ClubCategory> categories = clubCategoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        // Parse category parameter
        if (categoryParam == null || categoryParam.isEmpty() || categoryParam.equals("all")) {
            categoryID = 0; // 0 represents "all" categories
        } else if (categoryParam.equals("myClubs")) {
            categoryID = -1; // -1 represents "myClubs"
            selectedCategoryName = "myClubs";
        } else if (categoryParam.equals("favoriteClubs")) {
            categoryID = -2; // -2 represents "favoriteClubs"
            selectedCategoryName = "favoriteClubs";
        } else {
            try {
                categoryID = Integer.parseInt(categoryParam);
                // Find CategoryName for display
                for (ClubCategory category : categories) {
                    if (category.getCategoryID() == categoryID) {
                        selectedCategoryName = category.getCategoryName();
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                categoryID = 0; // Fallback to "all" if invalid
            }
        }

        int page;
        try {
            page = Integer.parseInt(request.getParameter("page"));
            if (page < 1) page = 1;
        } catch (NumberFormatException e) {
            page = 1;
        }

        int pageSize = 3;

        List<Clubs> clubs;
        int totalClubs;
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        String userID = (user != null) ? user.getUserID() : null;

        // Check if user has any clubs and favorite clubs
        boolean hasClubs = false;
        boolean hasFavoriteClubs = false;
        boolean hasPendingRequest = false;
        if (userID != null) {
            int userClubCount = clubDAO.getTotalClubsByCategory(-1, userID); // -1 for myClubs
            hasClubs = userClubCount > 0;
            int favoriteClubCount = clubDAO.getTotalFavoriteClubs(userID);
            hasFavoriteClubs = favoriteClubCount > 0;
            hasPendingRequest = permissionDAO.hasPendingRequest(userID);
        }

        // Handle favoriteClubs category
        if (categoryID == -2) { // favoriteClubs
            if (user == null || userID == null) {
                clubs = clubDAO.getClubsByCategory(0, page, pageSize); // Fallback to all
                totalClubs = clubDAO.getTotalClubsByCategory(0, null);
                categoryID = 0;
                selectedCategoryName = "all";
            } else {
                clubs = clubDAO.getFavoriteClubs(userID, page, pageSize);
                totalClubs = clubDAO.getTotalFavoriteClubs(userID);
            }
        } else if (categoryID == -1) { // myClubs
            if (user == null || userID == null) {
                clubs = clubDAO.getClubsByCategory(0, page, pageSize); // Fallback to all
                totalClubs = clubDAO.getTotalClubsByCategory(0, null);
                categoryID = 0;
                selectedCategoryName = "all";
            } else {
                clubs = clubDAO.getUserClubs(userID, page, pageSize);
                totalClubs = clubDAO.getTotalClubsByCategory(-1, userID);
            }
        } else {
            clubs = clubDAO.getClubsByCategory(categoryID, page, pageSize);
            totalClubs = clubDAO.getTotalClubsByCategory(categoryID, userID);
        }

        // Set favorite status for each club
        if (userID != null) {
            for (Clubs club : clubs) {
                boolean isFavorite = clubDAO.isFavoriteClub(userID, club.getClubID());
                club.setFavorite(isFavorite);
            }
        }

        int totalPages = (int) Math.ceil((double) totalClubs / pageSize);

        // Check for club creation permission
        boolean hasPermission = user != null && permissionDAO.hasActivePermission(userID);

        request.setAttribute("clubs", clubs);
        request.setAttribute("selectedCategory", selectedCategoryName); // For display
        request.setAttribute("selectedCategoryID", categoryID); // For pagination
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("hasClubs", hasClubs);
        request.setAttribute("hasFavoriteClubs", hasFavoriteClubs);
        request.setAttribute("hasPermission", hasPermission);
        request.setAttribute("hasPendingRequest", hasPendingRequest);

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
        boolean isDepartmentLeader = false;
        boolean isFavorite = false;
        UserClub userClub = null;

        // Set favorite status
        if (user != null) {
            String userID = user.getUserID();
            userClub = userClubDAO.getUserClub(userID, clubID);
            if (userClub != null && userClub.isIsActive()) {
                isMember = true;
                if (userClub.getRoleID() == 1 || userClub.getRoleID() == 2) {
                    isPresident = true;
                }
                // Check if user is department leader
                if (userClub.getRoleID() == 3) {
                    isDepartmentLeader = true;
                    int departmentID = userClub.getClubDepartmentID();
                    request.setAttribute("departmentID", departmentID);
                } else {
                    // Additional check using DAO to verify department leadership
                    DepartmentMemberDAO deptDAO = new DepartmentMemberDAO();
                    if (deptDAO.isDepartmentLeader(userID, userClub.getClubDepartmentID())) {
                        isDepartmentLeader = true;
                        int departmentID = userClub.getClubDepartmentID();
                        request.setAttribute("departmentID", departmentID);
                    }
                }
            }
            isFavorite = clubDAO.isFavoriteClub(userID, clubID);
            club.setFavorite(isFavorite);
        }

        // Check for club creation permission
        boolean hasPermission = user != null && permissionDAO.hasActivePermission(user.getUserID());
        request.setAttribute("club", club);
        request.setAttribute("isMember", isMember);
        request.setAttribute("isPresident", isPresident);
        request.setAttribute("isDepartmentLeader", isDepartmentLeader);
        request.setAttribute("userClub", userClub);
        request.setAttribute("hasPermission", hasPermission);

        request.getRequestDispatcher("/view/clubs-page/club-detail.jsp").forward(request, response);
    }

    private void handleFavoriteAction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        int clubID;
        try {
            clubID = Integer.parseInt(request.getParameter("clubID"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid club ID");
            return;
        }

        String userID = user.getUserID();
        boolean success = false;

        if ("addFavorite".equals(action)) {
            success = clubDAO.addFavoriteClub(userID, clubID);
        } else if ("removeFavorite".equals(action)) {
            success = clubDAO.removeFavoriteClub(userID, clubID);
        }

        if (success) {
            String referer = request.getHeader("Referer");
            String category = request.getParameter("category");
            if ("removeFavorite".equals(action) && "favoriteClubs".equalsIgnoreCase(category)) {
                int favoriteClubCount = clubDAO.getTotalFavoriteClubs(userID);
                if (favoriteClubCount == 0) {
                    response.sendRedirect(request.getContextPath() + "/clubs?category=0");
                    return;
                }
            }
            response.sendRedirect(referer != null ? referer : request.getContextPath() + "/clubs?category=" + (category != null ? category : "0"));
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing favorite action");
        }
    }
}