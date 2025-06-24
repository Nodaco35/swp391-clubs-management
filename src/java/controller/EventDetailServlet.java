/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import dal.EventsDAO;
import dal.UserClubDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.EventOwnerInfo;
import models.EventStats;
import models.Events;
import models.Users;

/**
 *
 * @author LE VAN THUAN
 */
public class EventDetailServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet EventDetailServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EventDetailServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String eventID = request.getParameter("id");
        EventsDAO eventDAO = new EventsDAO();
        UserClubDAO userClubDAO = new UserClubDAO();
        try {
            int id = Integer.parseInt(eventID);
            Events e = eventDAO.getEventByID(id);
            if(e != null) {
                HttpSession session = request.getSession();
                Users currentUser = (Users) session.getAttribute("user");
                boolean isMember = false;
                boolean isLoggedIn = currentUser != null;
                request.setAttribute("isLoggedIn", isLoggedIn);

                if (!e.isPublic() && currentUser != null) {
                    isMember = userClubDAO.isUserMemberOfClub(e.getClubID(), currentUser.getUserID());
                }

                List<Events> relatedEvents = eventDAO.getEventsByClubID(e.getClubID(), id);
                EventStats stats = eventDAO.getSpotsLeftEvent(id);
                EventOwnerInfo ownerInfo = eventDAO.getEventOwnerInfo(id);
                request.setAttribute("ownerInfo", ownerInfo);
                request.setAttribute("relatedEvents", relatedEvents);
                request.setAttribute("event", e);
                request.setAttribute("registeredCount", stats.getRegisteredCount());
                request.setAttribute("spotsLeft", stats.getSpotsLeft());
                request.setAttribute("isMember", isMember);


                request.getRequestDispatcher("view/events-page/event-detail/event-detail.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/event-page");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
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
