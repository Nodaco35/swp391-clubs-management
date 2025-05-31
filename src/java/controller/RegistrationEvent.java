/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import java.io.IOException;
import java.io.PrintWriter;

import dal.EventsDAO;
import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.EventStats;
import models.Events;
import models.Users;

/**
 *
 * @author LE VAN THUAN
 */
public class RegistrationEvent extends HttpServlet {
   
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
            out.println("<title>Servlet RegistrationEvent</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RegistrationEvent at " + request.getContextPath () + "</h1>");
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
        Users loggedInUser = (Users) session.getAttribute("user");

        if (loggedInUser != null) {
            UserDAO usersDAO = new UserDAO();
            Users userDetails = usersDAO.getUserByID(loggedInUser.getUserID());
            request.setAttribute("userDetails", userDetails);
        }

        String eventID = request.getParameter("id");
        try {
            int id = Integer.parseInt(eventID);
            EventsDAO eventDAO = new EventsDAO();
            Events event = eventDAO.getEventByID(id);

            if (event != null) {
                EventStats stats = eventDAO.getSpotsLeftEvent(id);
                request.setAttribute("event", event);
                request.setAttribute("registeredCount", stats.getRegisteredCount());
                request.setAttribute("spotsLeft", stats.getSpotsLeft());
                request.getRequestDispatcher("view/events-page/event-detail/registration-event.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/events-page");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/events-page");
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

        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String fullName = request.getParameter("fullName");
        String studentId = request.getParameter("studentId");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String eventIDRaw = request.getParameter("eventID");

        boolean agreed = request.getParameter("agreeTerms") != null;

        if (!agreed || fullName == null || studentId == null || email == null || phone == null || eventIDRaw == null) {
            request.setAttribute("message", "❗ Vui lòng điền đầy đủ thông tin và đồng ý điều khoản.");
            request.setAttribute("messageType", "error");
            request.getRequestDispatcher("view/events-page/event-detail/registration-event.jsp").forward(request, response);
            return;
        }

        try {
            int eventID = Integer.parseInt(eventIDRaw);
            EventsDAO eventDAO = new EventsDAO();

            if (eventDAO.isUserRegistered(eventID, user.getUserID())) {
                request.setAttribute("message", "⚠️ Bạn đã đăng ký sự kiện này.");
                request.setAttribute("messageType", "info");
            } else {
                boolean success = eventDAO.registerParticipant(eventID, user.getUserID());
                if (success) {
                    request.setAttribute("message", "🎉 Đăng ký sự kiện thành công!");
                    request.setAttribute("messageType", "success");
                } else {
                    request.setAttribute("message", "❌ Đăng ký thất bại, vui lòng thử lại.");
                    request.setAttribute("messageType", "error");
                }
            }

            // Lấy lại thông tin sự kiện để hiển thị sau khi đăng ký
            Events event = eventDAO.getEventByID(eventID);
            EventStats stats = eventDAO.getSpotsLeftEvent(eventID);

            UserDAO userDAO = new UserDAO();
            Users userDetails = userDAO.getUserByID(user.getUserID());

            request.setAttribute("event", event);
            request.setAttribute("registeredCount", stats.getRegisteredCount());
            request.setAttribute("spotsLeft", stats.getSpotsLeft());
            request.setAttribute("userDetails", userDetails);
            request.getRequestDispatcher("view/events-page/event-detail/registration-event.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("events-page");
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
