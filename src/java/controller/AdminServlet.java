
package controller;

import dal.ClubApplicationDAO;
import dal.ClubDAO;
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
            out.println("<h1>Servlet AdminServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if(action == null){
            ClubDAO clubDAO = new ClubDAO();
            EventsDAO eventDAO = new EventsDAO();
            int totalClubs = clubDAO.getTotalActiveClubs(); //
            int totalMembers = clubDAO.getTotalClubMembers(); //
            
            ClubApplicationDAO clubApplicationDAO = new ClubApplicationDAO();
            // Thống kê cho Admin
            int pendingRequests = clubApplicationDAO.countPendingRequests(); //
            int upcomingEventsCount = eventDAO.countUpcomingEvents(); //
            
            // Danh sách đơn đề xuất mới
            List<ClubApplication> pendingClubRequests = clubApplicationDAO.getPendingRequests(5);
            
            // Danh sách CLB mới thành lập
            List<Clubs> recentlyApprovedClubs = clubDAO.getRecentlyApprovedClubs(3);
            
            request.setAttribute("totalClubs", totalClubs);
            request.setAttribute("totalMembers", totalMembers);
            request.setAttribute("pendingRequests", pendingRequests);
            request.setAttribute("upcomingEventsCount", upcomingEventsCount);
            request.setAttribute("pendingClubRequests", pendingClubRequests);
            request.setAttribute("recentlyApprovedClubs", recentlyApprovedClubs);
            
            request.getRequestDispatcher("view/admin/dashboard.jsp").forward(request, response);
        }
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
