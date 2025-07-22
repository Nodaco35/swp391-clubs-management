/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dal.ClubDAO;
import dal.EventsDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import models.ClubInfo;
import models.EventTerms;
import models.Events;
import models.Users;

/**
 *
 * @author LE VAN THUAN
 */
public class ChairmanMyClubEventsServlet extends HttpServlet {
   
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
            out.println("<title>Servlet ChairmanMyClubEventsServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ChairmanMyClubEventsServlet at " + request.getContextPath () + "</h1>");
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
        if (user != null) {
            String userID = user.getUserID();
            ClubInfo club = clubDAO.getClubChairman(userID);
            int clubID = clubDAO.getClubIDByUserID(userID);

            request.setAttribute("club", club);
            List<Events> myClubEvents = eventDAO.getEventsByClubID(clubID);
            int totalUpcoming = eventDAO.countUpcomingEvents(clubID);
            int totalOngoing = eventDAO.countOngoingEvents(clubID);
            int totalPast = eventDAO.countPastEvents(clubID);

            request.setAttribute("totalUpcoming", totalUpcoming);
            request.setAttribute("totalOngoing", totalOngoing);
            request.setAttribute("totalPast", totalPast);
            request.setAttribute("totalEvents", eventDAO.getTotalEvents(clubID));
            request.setAttribute("myClubEvents", myClubEvents);

            String currentPath = request.getServletPath();
            request.setAttribute("currentPath", currentPath);

            request.getRequestDispatcher("/view/student/chairman/myclub-events.jsp").forward(request, response);
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
        String action = request.getParameter("action");
        if ("addTerm".equals(action)) {
            try {
                // Lấy dữ liệu từ form
                int eventID = Integer.parseInt(request.getParameter("eventID"));
                String termName = request.getParameter("termName");
                String termStartStr = request.getParameter("termStart");
                String termEndStr = request.getParameter("termEnd");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date termStart = new Date(sdf.parse(termStartStr).getTime());
                Date termEnd = new Date(sdf.parse(termEndStr).getTime());

                // Tạo đối tượng EventTerms
                EventTerms term = new EventTerms();
                Events e = new Events();
                e.setEventID(eventID);
                term.setEvent(e);
                term.setTermName(termName);
                term.setTermStart(termStart);
                term.setTermEnd(termEnd);

                // Lưu vào database
                EventsDAO termsDAO = new EventsDAO();
                boolean success = termsDAO.addEventTerm(term);

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/chairman-page/tasks");
                } else {
                    // Có thể xảy ra do lỗi SQL hoặc trigger
                    request.setAttribute("termError", "Không thể thêm giai đoạn: Giai đoạn đã tồn tại hoặc đã đủ 3 giai đoạn.");
                    request.setAttribute("showTermModal", true); // để mở lại modal
                    doGet(request, response);
                }

            } catch (SQLException e) {
                String message = e.getMessage();
                if (message.contains("Giai đoạn này đã tồn tại")) {
                    request.setAttribute("termError", "Giai đoạn này đã tồn tại cho sự kiện.");
                } else if (message.contains("Mỗi sự kiện chỉ được có tối đa 3 giai đoạn")) {
                    request.setAttribute("termError", "Không thể thêm giai đoạn: Đã đủ 3 giai đoạn (Trước, Trong, Sau).");
                } else {
                    request.setAttribute("termError", "Lỗi không xác định: " + message);
                }
                request.setAttribute("showTermModal", true); // mở lại modal
                doGet(request, response);
            } catch (Exception e) {
                request.setAttribute("termError", "Lỗi hệ thống: " + e.getMessage());
                request.setAttribute("showTermModal", true); // mở lại modal
                doGet(request, response);
            }
        } else {
            processRequest(request, response);
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
