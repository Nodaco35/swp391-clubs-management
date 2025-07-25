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

/**
 *
 * @author LE VAN THUAN
 */
public class ApprovalAgendaServlet extends HttpServlet {
   
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
            out.println("<title>Servlet ApprovalAgendaServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ApprovalAgendaServlet at " + request.getContextPath () + "</h1>");
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

        List<Agenda> agendaList = dao.getAgendaByEventPublic();
        int pendingCount = dao.countEventHasPendingAgenda();
        int approvedCount = dao.countEventHasApprovedAgenda();
        int rejectedCount = dao.countEventHasRejectedAgenda();
        request.setAttribute("agendaList", agendaList);
        request.setAttribute("pendingCount", pendingCount);
        request.setAttribute("approvedCount", approvedCount);
        request.setAttribute("rejectedCount", rejectedCount);

        String eventID = request.getParameter("eventID");
        if (eventID != null && !eventID.isEmpty()) {
            try {
                List<Agenda> agendaDetails = dao.getAgendaByEventID(Integer.parseInt(eventID));
                request.setAttribute("agendaDetails", agendaDetails);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID sự kiện không hợp lệ!");
            }
        }

        String currentPath = request.getServletPath();
        request.setAttribute("currentPath", currentPath);

        request.getRequestDispatcher("/view/ic/approval-agenda.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String eventID = request.getParameter("eventID");
        String status = request.getParameter("status");
        String reason = request.getParameter("reason");

        if (eventID != null && status != null) {
            EventsDAO dao = new EventsDAO();
            dao.updateAgendaStatus(Integer.parseInt(eventID), status, reason);
            request.setAttribute("message", status.equals("approved") ?
                    "Agenda đã được duyệt thành công!" : "Agenda đã bị từ chối!");
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
