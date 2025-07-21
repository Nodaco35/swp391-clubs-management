/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import java.io.IOException;
import java.io.PrintWriter;

import dal.ApplicationFormDAO;
import dal.EventsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.ApplicationForm;
import models.Events;
import models.Users;

/**
 *
 * @author LE VAN THUAN
 */
public class AssignFormServlet extends HttpServlet {
   
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
            out.println("<title>Servlet AssignFormServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AssignFormServlet at " + request.getContextPath () + "</h1>");
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
        processRequest(request, response);
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
        Users user = (Users) session.getAttribute("user");
        ApplicationFormDAO formDAO = new ApplicationFormDAO();

        if (user != null) {
            String eventIDParam = request.getParameter("eventID");
            String formIDParam = request.getParameter("formId");

            try {
                int eventID = Integer.parseInt(eventIDParam);
                int formID = Integer.parseInt(formIDParam);

                // Kiểm tra xem formId và eventId có hợp lệ không
                EventsDAO eventDAO = new EventsDAO();
                Events event = eventDAO.getEventByID(eventID);
                ApplicationForm form = formDAO.getFormByID(formID);

                if (event != null && form != null && form.getClubId() == event.getClubID()) {
                    // Gán form cho sự kiện
                    formDAO.assignFormToEvent(formID, eventID);
                    // Cập nhật FormID trong bảng Events
                    eventDAO.updateEventFormID(eventID, formID);
                    session.setAttribute("successMsg", "Gán form đăng ký thành công!");
                } else {
                    session.setAttribute("errorMessage", "Form không hợp lệ hoặc không thuộc câu lạc bộ này.");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("errorMessage", "ID sự kiện hoặc form không hợp lệ.");
            } catch (Exception e) {
                session.setAttribute("errorMessage", "Lỗi khi gán form: " + e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/edit-event?eventID=" + eventIDParam);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
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
