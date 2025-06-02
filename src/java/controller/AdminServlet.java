package controller;

import dal.ClubApplicationDAO;
import dal.ClubDAO;
import dal.CreatedClubApplicationsDAO;
import dal.EventsDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import models.Clubs;
import models.ClubApplication;
import models.CreatedClubApplications;

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
        String action = request.getParameter("action");
        if (action == null || action.isBlank() || action.isEmpty()) {
            ClubDAO clubDAO = new ClubDAO();
            EventsDAO eventDAO = new EventsDAO();
            CreatedClubApplicationsDAO ccaDAO = new CreatedClubApplicationsDAO();

            int totalClubs = clubDAO.getTotalActiveClubs();

            int totalMembers = clubDAO.getTotalClubMembers();

            int pendingRequests = ccaDAO.countPendingRequests();

            int upcomingEventsCount = eventDAO.countUpcomingEvents();

            List<CreatedClubApplications> pendingClubRequests = ccaDAO.getPendingRequests(10);
            List<CreatedClubApplications> approvedClubRequests = ccaDAO.getRequestsByStatus("APPROVED");
            List<CreatedClubApplications> rejectedClubRequests = ccaDAO.getRequestsByStatus("REJECTED");
            List<Clubs> activeClubs = clubDAO.getActiveClubs();
            List<Clubs> inactiveClubs = clubDAO.getInactiveClubs();

            PrintWriter out = response.getWriter();
//            out.print("recentlyApprovedClubs size = " + (recentlyApprovedClubs != null ? recentlyApprovedClubs.size() : "null"));
//            out.print("pendingClubRequests size = " + (pendingClubRequests != null ? pendingClubRequests.size() : "null"));

            request.setAttribute("totalClubs", totalClubs);
            request.setAttribute("totalMembers", totalMembers);
            request.setAttribute("pendingRequests", pendingRequests);
            request.setAttribute("upcomingEventsCount", upcomingEventsCount);
            request.setAttribute("pendingClubRequests", pendingClubRequests);
            request.setAttribute("approvedClubRequests", approvedClubRequests);
            request.setAttribute("rejectedClubRequests", rejectedClubRequests);
            request.setAttribute("activeClubs", activeClubs);
            request.setAttribute("inactiveClubs", inactiveClubs);

            request.getRequestDispatcher("/view/admin/dashboard.jsp").forward(request, response);
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
        processRequest(request, response);
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

}
