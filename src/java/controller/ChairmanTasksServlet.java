/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dal.ClubDAO;
import dal.EventsDAO;
import dal.TaskDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.ClubInfo;
import models.Events;
import models.TaskAssignmentDepartment;
import models.Users;

/**
 *
 * @author LE VAN THUAN
 */
public class ChairmanTasksServlet extends HttpServlet {
   
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
            out.println("<title>Servlet ChairmanTasksServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ChairmanTasksServlet at " + request.getContextPath () + "</h1>");
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

        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");

        if (user != null) {
            String userID = user.getUserID();
            ClubDAO clubDAO = new ClubDAO();
            EventsDAO eventDAO = new EventsDAO();
            TaskDAO taskDAO = new TaskDAO();

            ClubInfo club = clubDAO.getClubChairman(userID);
            List<Events> eventList = eventDAO.getEventsByClubID(club.getClubID());

            // Lấy eventID từ query parameter
            String eventIDParam = request.getParameter("eventID");
            Map<Events, List<TaskAssignmentDepartment>> timelineMap = new LinkedHashMap<>();

            if (eventIDParam != null && !eventIDParam.isEmpty()) {
                try {
                    int eventID = Integer.parseInt(eventIDParam);
                    Events selectedEvent = eventDAO.getEventByID(eventID);
                    List<TaskAssignmentDepartment> tasks = taskDAO.getTaskAssignDepartByEventID(eventID);
                    if (!tasks.isEmpty()) {
                        timelineMap.put(selectedEvent, tasks);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else {
                for (Events event : eventList) {
                    List<TaskAssignmentDepartment> taskList = taskDAO.getTaskAssignDepartByEventID(event.getEventID());
                    if (!taskList.isEmpty()) {
                        timelineMap.put(event, taskList);
                    }
                }
            }

            request.setAttribute("eventList", eventList);
            request.setAttribute("timelineMap", timelineMap);
            request.setAttribute("club", club);
            request.setAttribute("currentPath", request.getServletPath());

            request.getRequestDispatcher("/view/student/chairman/tasks.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
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
