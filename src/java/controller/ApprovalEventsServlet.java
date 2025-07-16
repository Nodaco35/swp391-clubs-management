/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import dal.EventsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Agenda;
import models.Events;

/**
 *
 * @author LE VAN THUAN
 */
public class ApprovalEventsServlet extends HttpServlet {
   
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
            out.println("<title>Servlet ApprovalEventsServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ApprovalEventsServlet at " + request.getContextPath () + "</h1>");
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
        EventsDAO dao = new EventsDAO();

        List<Events> events = dao.getEventsByPublic();
        int approvedCount = dao.countApprovedEvents();
        int pendingCount = dao.countPendingEvents();
        int rejectedCount = dao.countRejectedEvents();
        request.setAttribute("events", events);
        request.setAttribute("approvedCount", approvedCount);
        request.setAttribute("pendingCount", pendingCount);
        request.setAttribute("rejectedCount", rejectedCount);

        // Lấy eventID từ request (từ onclick trong JSP)
        String eventID = request.getParameter("eventID");
        if (eventID != null && !eventID.isEmpty()) {
            try {
                Events eventDetails = dao.getEventByID(Integer.parseInt(eventID));
                request.setAttribute("eventDetails", eventDetails);

                // Thêm phần lấy agenda details
                List<Agenda> agendaDetails = dao.getAgendaByEventID(Integer.parseInt(eventID));
                request.setAttribute("agendaDetails", agendaDetails);

                // Kiểm tra trạng thái agenda của event này
                if (!agendaDetails.isEmpty()) {
                    String agendaStatus = agendaDetails.get(0).getStatus(); // Giả sử tất cả agenda cùng status
                    request.setAttribute("agendaStatus", agendaStatus);
                }

            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID sự kiện không hợp lệ!");
            }
        }

        String currentPath = request.getServletPath();
        request.setAttribute("currentPath", currentPath);

        request.getRequestDispatcher("/view/ic/approval-events.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String eventID = request.getParameter("eventID");
        String status = request.getParameter("status");
        String reason = request.getParameter("reason");
        String approveAgenda = request.getParameter("approveAgenda"); // Checkbox để duyệt cả agenda

        if (eventID != null && status != null) {
            EventsDAO dao = new EventsDAO();

            // Cập nhật trạng thái event
            dao.updateApprovalStatus(Integer.parseInt(eventID), status, reason);

            // Nếu checkbox được chọn và event được duyệt, thì duyệt cả agenda
            if ("on".equals(approveAgenda) && "approved".equals(status)) {
                dao.updateAgendaStatus(Integer.parseInt(eventID), "approved", null);
                request.setAttribute("message", "Event và Agenda đã được duyệt thành công!");
            }
            // Nếu event bị từ chối, có thể tự động từ chối agenda
            else if ("rejected".equals(status)) {
                dao.updateAgendaStatus(Integer.parseInt(eventID), "rejected", reason);
                request.setAttribute("message", "Event và Agenda đã bị từ chối!");
            }
            else {
                request.setAttribute("message", status.equals("approved") ?
                        "Event đã được duyệt thành công!" : "Event đã bị từ chối!");
            }
        }

        doGet(request, response);
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
