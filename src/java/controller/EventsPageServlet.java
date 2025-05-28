/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dal.EventsDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import models.Events;

/**
 *
 * @author LE VAN THUAN
 */
public class EventsPageServlet extends HttpServlet {
   
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
            out.println("<title>Servlet EventsPageServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EventsPageServlet at " + request.getContextPath () + "</h1>");
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

        String keyword = request.getParameter("key");
        String publicFilter = request.getParameter("publicFilter");
        String sortByDate = request.getParameter("sortByDate");

        if (publicFilter == null || publicFilter.isEmpty()) {
            publicFilter = "all";
        }

        if (sortByDate == null || sortByDate.isEmpty()) {
            sortByDate = "newest";
        }

        int page = 1;
        int pageSize = 6;

        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                page = Integer.parseInt(pageParam);
                if (page < 1) {
                    page = 1;
                }
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        int offset = (page - 1) * pageSize;

        List<Events> events = dao.searchEvents(keyword, publicFilter, sortByDate, pageSize, offset);

        int totalEvents = dao.countEvents(keyword, publicFilter);
        int totalPages = (int) Math.ceil((double) totalEvents / pageSize);

        if (page > totalPages && totalPages > 0) {
            page = totalPages;
            offset = (page - 1) * pageSize;
            events = dao.searchEvents(keyword, publicFilter, sortByDate, pageSize, offset);
        }

        request.setAttribute("events", events);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalEvents", totalEvents);
        request.setAttribute("pageSize", pageSize);

        request.setAttribute("currentKeyword", keyword != null ? keyword : "");
        request.setAttribute("currentPublicFilter", publicFilter);
        request.setAttribute("currentSortByDate", sortByDate);

        System.out.println("=== Search Parameters ===");
        System.out.println("Keyword: " + keyword);
        System.out.println("Public Filter: " + publicFilter);
        System.out.println("Sort By: " + sortByDate);
        System.out.println("Page: " + page + "/" + totalPages);
        System.out.println("Total Events: " + totalEvents);
        System.out.println("Events Found: " + events.size());

        request.getRequestDispatcher("view/events-page/events-page.jsp").forward(request, response);
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
