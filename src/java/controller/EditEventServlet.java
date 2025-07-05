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
import java.util.ArrayList;
import java.util.List;

import dal.ClubDAO;
import dal.EventsDAO;
import dal.LocationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.*;

/**
 *
 * @author LE VAN THUAN
 */
public class EditEventServlet extends HttpServlet {
   
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
            out.println("<title>Servlet EditEventServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EditEventServlet hiiiii at " + request.getContextPath () + "</h1>");
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
        ClubDAO clubDAO = new ClubDAO();
        EventsDAO eventDAO = new EventsDAO();
        LocationDAO locationDAO = new LocationDAO();

        if (user != null) {
            String userID = user.getUserID();
            ClubInfo club = clubDAO.getClubChairman(userID);
            request.setAttribute("club", club);

            String eventIDParam = request.getParameter("eventID");
            if (eventIDParam != null) {
                try {
                    int eventID = Integer.parseInt(eventIDParam);
                    Events event = eventDAO.getEventByID(eventID);

                    List<Agenda> agendas = eventDAO.getAgendasByEventID(eventID);

                    String locationType = request.getParameter("locationType");
                    if (locationType == null || locationType.isEmpty()) {
                        if (event.getLocation() != null && event.getLocation().getTypeLocation() != null) {
                            locationType = event.getLocation().getTypeLocation();
                        } else {
                            locationType = "OnCampus";
                        }
                    }

                    List<Locations> locations = locationDAO.getLocationsByType(locationType);

                    request.setAttribute("event", event);
                    request.setAttribute("agendas", agendas);
                    request.setAttribute("locations", locations);
                    request.setAttribute("locationType", locationType);

                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "ID sự kiện không hợp lệ.");
                }
            }
            request.getRequestDispatcher("/view/student/chairman/edit-event.jsp").forward(request, response);
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
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Integer myClubID = (Integer) session.getAttribute("myClubID");

        if (myClubID == null || myClubID <= 0) {
            request.setAttribute("errorMessage", "Vui lòng đăng nhập với tư cách chủ nhiệm câu lạc bộ.");
            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
            return;
        }
        try {
            int eventID = Integer.parseInt(request.getParameter("eventID"));
            String eventName = request.getParameter("eventName");
            String eventDateStr = request.getParameter("eventDate");
            String startTimeStr = request.getParameter("eventTime");
            String endTimeStr = request.getParameter("eventEndTime");
            int locationID = Integer.parseInt(request.getParameter("eventLocation"));
            int capacity = Integer.parseInt(request.getParameter("maxParticipants"));
            if (capacity <= 0 || capacity > 10000) {
                session.setAttribute("errorMessage", "Số lượng tối đa phải lớn hơn 0 hoặc nhỏ hơn 10000 người");
                response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/edit-event?eventID=" + eventID);
                return;
            }
            String eventType = request.getParameter("eventType");
            String description = request.getParameter("eventDescription");

            boolean isPublic = "public".equalsIgnoreCase(eventType);

            LocalDate date = LocalDate.parse(eventDateStr);
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);

            Timestamp eventStart = Timestamp.valueOf(LocalDateTime.of(date, startTime));
            Timestamp eventEnd = Timestamp.valueOf(LocalDateTime.of(date, endTime));


            EventsDAO eventDAO = new EventsDAO();
            eventDAO.updateEvent(eventID, eventName, description, eventStart, eventEnd, locationID, capacity, isPublic);

            session.setAttribute("successMsg", "Chỉnh sửa sự kiện thành công!");
            response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/edit-event?eventID=" + eventID);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi cập nhật sự kiện: " + e.getMessage());
            request.getRequestDispatcher("/view/student/chairman/edit-event.jsp").forward(request, response);
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
