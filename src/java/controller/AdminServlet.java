package controller;

import dal.ClubApplicationDAO;
import dal.ClubCreationPermissionDAO;
import dal.ClubDAO;
import dal.CreatedClubApplicationsDAO;
import dal.EventsDAO;
import dal.PermissionDAO;

import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import models.Clubs;
import models.ClubApplication;
import models.CreatedClubApplications;
import models.Permission;

import models.Users;

public class AdminServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AdminServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AdminServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserDAO ud = new UserDAO();
        String action = request.getParameter("action");
        if (action == null || action.isBlank() || action.isEmpty()) {
            ClubDAO clubDAO = new ClubDAO();
            EventsDAO eventDAO = new EventsDAO();
            CreatedClubApplicationsDAO ccaDAO = new CreatedClubApplicationsDAO();

            int totalClubs = clubDAO.getTotalActiveClubs();

            int totalMembers = clubDAO.getTotalClubMembers();

            int pendingRequests = ccaDAO.countPendingRequests();

            int upcomingEventsCount = eventDAO.countUpcomingEvents();

            List<Clubs> activeClubs = clubDAO.getActiveClubs();
            List<Clubs> inactiveClubs = clubDAO.getInactiveClubs();

            PrintWriter out = response.getWriter();
//            out.print("recentlyApprovedClubs size = " + (recentlyApprovedClubs != null ? recentlyApprovedClubs.size() : "null"));
//            out.print("pendingClubRequests size = " + (pendingClubRequests != null ? pendingClubRequests.size() : "null"));

            request.setAttribute("totalClubs", totalClubs);
            request.setAttribute("totalMembers", totalMembers);
            request.setAttribute("pendingRequests", pendingRequests);
            request.setAttribute("upcomingEventsCount", upcomingEventsCount);
            request.setAttribute("activeClubs", activeClubs);
            request.setAttribute("inactiveClubs", inactiveClubs);

            request.getRequestDispatcher("/view/admin/dashboard.jsp").forward(request, response);
        } else if (action.equals("manageAccounts")) {

            showAllUser(request, response);
        } else if (action.equals("filter")) {
            filterByPermission(request, response);
        } else if (action.equals("search")) {
            search(request, response);
        }

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "insert":
                insertUser(request, response);
                break;
            case "update":
                updateUser(request, response);
                break;
            case "deleteAccounts":
                changeActiveAccountByAdmin(request, response);
                break;
            default:
                throw new AssertionError();
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void showAllUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = 1;
        int recordsPerPage = 9;

        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }
        
        List<Users> users = UserDAO.findUsersByPage((page - 1) * recordsPerPage, recordsPerPage);
        int totalRecords = UserDAO.countAllUsers();
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

        request.setAttribute("list", users);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("permissionID", 0); // Không lọc
        request.setAttribute("query", ""); // Không tìm kiếm
        request.getRequestDispatcher("view/admin/users.jsp").forward(request, response);
    }

    private void insertUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String dateOfBirth = request.getParameter("dateOfBirth");
        int permissionID = Integer.parseInt(request.getParameter("permissionID"));
        String status = request.getParameter("status");

        if (UserDAO.getUserByEmail(email) != null) {
            request.setAttribute("error", "Email đã tồn tại. Vui lòng sử dụng email khác.");
            request.setAttribute("formFullName", fullName);
            request.setAttribute("formEmail", email);
            request.setAttribute("formPassword", password);
            request.setAttribute("formDateOfBirth", dateOfBirth);
            request.setAttribute("formPermissionID", permissionID);
            request.setAttribute("formStatus", status);
            request.setAttribute("type", "insert");
            // Chuyển hướng lại trang manageAccounts
            showAllUser(request, response);
            return;
        }
        UserDAO.insertByAdmin(fullName, email, password, dateOfBirth, permissionID, status);
        showAllUser(request, response);
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userID = request.getParameter("userID");
        String new_name = request.getParameter("fullName");
        String new_email = request.getParameter("email");
        String password_raw = request.getParameter("password");
        String new_dob = request.getParameter("dateOfBirth");
        int new_permissionID = Integer.parseInt(request.getParameter("permissionID"));
        String status_raw = request.getParameter("status");
        int status;
        if (status_raw.equals("true")) {
            status = 1;
        } else {
            status = 0;
        }
        String new_password;
        if (password_raw == null || password_raw.equals("null") || password_raw.isEmpty()) {
            new_password = UserDAO.getUserById(userID).getPassword();
        } else {
            new_password = password_raw;
        }
        if (new_dob == null || new_dob.equals("null") || new_dob.isEmpty()) {
            new_dob = null;
        }

        if (!new_email.equals(UserDAO.getUserById(userID).getEmail()) && UserDAO.getUserByEmail(new_email) != null) {
            request.setAttribute("error", "Email đã tồn tại. Vui lòng sử dụng email khác.");
            request.setAttribute("formUserID", userID);
            request.setAttribute("formFullName", new_name);
            request.setAttribute("formEmail", new_email);
            request.setAttribute("formPassword", new_password);
            request.setAttribute("formDateOfBirth", new_dob);
            request.setAttribute("formPermissionID", new_permissionID);
            request.setAttribute("formStatus", status_raw);
            request.setAttribute("type", "update");
            // Chuyển hướng lại trang manageAccounts
            showAllUser(request, response);
            return;
        }
        UserDAO.updateAccountByAdmin(userID, new_name, new_email, new_password, new_dob, new_permissionID, status);
        showAllUser(request, response);
    }

    private void changeActiveAccountByAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userID = request.getParameter("id");
        UserDAO.changeActiveAccountByAdmin(userID);
        showAllUser(request, response);
    }

    private void filterByPermission(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int perID = Integer.parseInt(request.getParameter("permissionID"));
        if (perID == 0) {
            showAllUser(request, response);
            return;
        }
        int page = 1;
        int recordsPerPage = 9;

        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        List users = UserDAO.findUsersByPageAndPerID(perID, (page - 1) * recordsPerPage, recordsPerPage);
        int totalRecords = UserDAO.countUsersByPermissionID(perID);
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

        request.setAttribute("list", users);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("permissionID", perID);
        request.getRequestDispatcher("view/admin/users.jsp").forward(request, response);

    }

    private void search(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        if (query == null || query.trim().isEmpty()) {
            showAllUser(request, response);
            return;
        }
        int page = 1;
        int recordsPerPage = 9;

        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        List users = UserDAO.searchUsersByQuery(query, (page - 1) * recordsPerPage, recordsPerPage);
        int totalRecords = UserDAO.countUsersByQuery(query);
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

        request.setAttribute("list", users);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("permissionID", 0); // Không lọc khi tìm kiếm
        request.setAttribute("query", query); // Giữ query cho form tìm kiếm và phân trang
        request.getRequestDispatcher("view/admin/users.jsp").forward(request, response);
    }

}
