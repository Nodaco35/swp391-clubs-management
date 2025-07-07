/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import dal.EventsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Events;

/**
 *
 * @author LE VAN THUAN
 */
public class AgendaServlet extends HttpServlet {
   
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
            out.println("<title>Servlet AgendaServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AgendaServlet at " + request.getContextPath () + "</h1>");
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
        request.getRequestDispatcher("/view/student/chairman/edit-event.jsp").forward(request, response);

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
        HttpSession session = request.getSession();
        String sourcePage = request.getParameter("sourcePage");
        int eventID = Integer.parseInt(request.getParameter("eventID"));
        String[] startTimes = request.getParameterValues("agendaStartTime[]");
        String[] endTimes = request.getParameterValues("agendaEndTime[]");
        String[] activities = request.getParameterValues("agendaActivity[]");

        EventsDAO eventsDAO = new EventsDAO();

        Events event = eventsDAO.getEventByID(eventID);
        Date date = event.getEventDate();
        LocalDate eventDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        eventsDAO.deleteAllByEventID(eventID);

        if (startTimes != null && endTimes != null && activities != null) {
            for (int i = 0; i < activities.length; i++) {
                try {
                    LocalTime start = LocalTime.parse(startTimes[i]);
                    LocalTime end = LocalTime.parse(endTimes[i]);
                    String title = activities[i];

                    Timestamp startTS = Timestamp.valueOf(LocalDateTime.of(eventDate, start));
                    Timestamp endTS = Timestamp.valueOf(LocalDateTime.of(eventDate, end));

                    eventsDAO.insertAgenda(eventID, title, "", startTS, endTS);
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("errorMessage", "Lỗi khi thêm chương trình sự kiện: " + e.getMessage());
                }
            }
        }

        session.setAttribute("successMsg", "Thêm chương trình sự kiện thành công!");
        if ("add-event".equals(sourcePage)) {
            session.removeAttribute("newEventID"); // Clear newEventID after agenda is saved
            response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events");
        } else {
            response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/edit-event?eventID=" + eventID);
        }
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
