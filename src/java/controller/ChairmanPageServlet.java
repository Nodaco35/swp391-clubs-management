/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import dal.ClubDAO;
import dal.EventsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.ClubInfo;
import models.Events;
import models.Users;

/**
 *
 * @author LE VAN THUAN
 */
public class ChairmanPageServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
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
            out.println("<title>Servlet ChairmanPageController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ChairmanPageController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ClubDAO clubDAO = new ClubDAO();
        EventsDAO eventDAO = new EventsDAO();
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");

        String action = request.getParameter("action");
        String subaction = request.getParameter("subaction");

        if (action == null) {
            action = "overview";
        }

        if (user != null) {
            String userID = user.getUserID();
            ClubInfo club = clubDAO.getClubChairman(userID);
            int clubID = clubDAO.getClubIDByUserID(userID);

            request.setAttribute("club", club);
            request.setAttribute("totalMembers", clubDAO.getTotalClubMembers(clubID));
            request.setAttribute("totalEvents", eventDAO.getTotalEvents(clubID));
            request.setAttribute("totalDepartments", clubDAO.getTotalDepartments(clubID));

            if ("myclub-events".equals(action)) {
                if ("add-event".equals(subaction)) {

                } else if ("edit-event".equals(subaction)) {
                    int eventID = Integer.parseInt(request.getParameter("eventID"));
                    Events event = eventDAO.getEventByID(eventID);
                    request.setAttribute("event", event);

//                } else if ("assign-task".equals(subaction)) {
//                    int eventID = Integer.parseInt(request.getParameter("eventID"));
//                    List<Department> departments = clubDAO.getDepartmentsByClubID(clubID);
//                    request.setAttribute("departments", departments);
//                    request.setAttribute("eventID", eventID);
//                    request.getRequestDispatcher("view/student/chairman/assign-task.jsp").forward(request, response);
//                    return;

                } else {
                    // Không có subaction hoặc không hợp lệ -> load danh sách sự kiện
                    List<Events> myClubEvents = eventDAO.getEventsByClubID(clubID);
                    int totalUpcoming = eventDAO.countUpcomingEvents(clubID);
                    int totalOngoing = eventDAO.countOngoingEvents(clubID);
                    int totalPast = eventDAO.countPastEvents(clubID);

                    request.setAttribute("totalUpcoming", totalUpcoming);
                    request.setAttribute("totalOngoing", totalOngoing);
                    request.setAttribute("totalPast", totalPast);
                    request.setAttribute("myClubEvents", myClubEvents);
                }
            }

            // Tương tự, bạn có thể xử lý thêm cho các action khác (vd: "tasks", "members", v.v.)
        }

        // Luôn set lại action để JSP biết tab nào được chọn
        request.setAttribute("action", action);
        request.setAttribute("subaction", subaction);
        request.getRequestDispatcher("view/student/chairman/chairman-page.jsp").forward(request, response);
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
