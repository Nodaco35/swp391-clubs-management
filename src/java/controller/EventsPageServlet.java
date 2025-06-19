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
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import models.Events;
import models.Users;

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
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        String userID = user != null ? user.getUserID() : null;

        String keyword = request.getParameter("key");
        String publicFilter = request.getParameter("publicFilter");
        String sortByDate = request.getParameter("sortByDate");
        String action = request.getParameter("action");

        if (publicFilter == null || publicFilter.isEmpty()) {
            publicFilter = "all";
        }

        if (sortByDate == null || sortByDate.isEmpty()) {
            sortByDate = "newest";
        }

        int page = 1;
        int pageSize = 8;

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

        // Kiểm tra xem người dùng có sự kiện yêu thích nào không
        boolean hasFavoriteEvents = false;
        if (userID != null) {
            hasFavoriteEvents = dao.getTotalFavoriteEvents(userID) > 0;
        }
        request.setAttribute("hasFavoriteEvents", hasFavoriteEvents);

        // Xử lý hành động yêu thích
        if (action != null && user != null) {
            int eventID;
            try {
                eventID = Integer.parseInt(request.getParameter("eventID"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid event ID");
                return;
            }

            boolean success = false;
            if ("addFavorite".equals(action)) {
                success = dao.addFavoriteEvent(userID, eventID);
            } else if ("removeFavorite".equals(action)) {
                success = dao.removeFavoriteEvent(userID, eventID);
                if (success && "favoriteEvents".equalsIgnoreCase(publicFilter)) {
                    // Tính lại tổng số sự kiện yêu thích sau khi xóa
                    int totalFavoriteEvents = dao.getTotalFavoriteEvents(userID);
                    int newTotalPages = (int) Math.ceil((double) totalFavoriteEvents / pageSize);
                    // Nếu trang hiện tại vượt quá số trang mới, điều chỉnh về trang cuối
                    if (page > newTotalPages && newTotalPages > 0) {
                        page = newTotalPages;
                    } else if (newTotalPages == 0) {
                        page = 1;
                    }
                    // Cập nhật URL để chuyển hướng
                    String redirectUrl = String.format("%s/events-page?publicFilter=favoriteEvents&sortByDate=%s&page=%d%s",
                            request.getContextPath(),
                            sortByDate,
                            page,
                            keyword != null && !keyword.isEmpty() ? "&key=" + keyword : "");
                    response.sendRedirect(redirectUrl);
                    return;
                }
            }

            if (success) {
                String referer = request.getHeader("Referer");
                response.sendRedirect(referer != null ? referer : request.getContextPath() + "/events-page");
                return;
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing favorite action");
                return;
            }
        }

        List<Events> events = new ArrayList<>();
        int totalEvents = 0;
        int totalPages = 1;

        if ("favoriteEvents".equalsIgnoreCase(publicFilter)) {
            if (user == null || userID == null || !hasFavoriteEvents) {
                publicFilter = "all";
                events = dao.searchEvents(keyword, publicFilter, sortByDate, pageSize, offset);
                totalEvents = dao.countEvents(keyword, publicFilter);
            } else {
                events = dao.getFavoriteEvents(userID, page, pageSize);
                totalEvents = dao.getTotalFavoriteEvents(userID);
            }
        } else {
            events = dao.searchEvents(keyword, publicFilter, sortByDate, pageSize, offset);
            totalEvents = dao.countEvents(keyword, publicFilter);
        }

        totalPages = (int) Math.ceil((double) totalEvents / pageSize);
        if (totalPages == 0) {
            totalPages = 1; // Đảm bảo ít nhất 1 trang ngay cả khi không có sự kiện
        }

        // Nếu trang hiện tại vượt quá số trang, điều chỉnh về trang cuối
        if (page > totalPages) {
            page = totalPages;
            offset = (page - 1) * pageSize;
            if ("favoriteEvents".equalsIgnoreCase(publicFilter) && userID != null && hasFavoriteEvents) {
                events = dao.getFavoriteEvents(userID, page, pageSize);
            } else {
                events = dao.searchEvents(keyword, publicFilter, sortByDate, pageSize, offset);
            }
        }

        // Kiểm tra trạng thái yêu thích cho từng sự kiện
        List<Integer> favoriteEvents = new ArrayList<>();
        if (userID != null) {
            for (Events event : events) {
                if (dao.isFavoriteEvent(userID, event.getEventID())) {
                    favoriteEvents.add(event.getEventID());
                }
            }
        }

        String currentPath = request.getServletPath();
        request.setAttribute("currentPath", currentPath);

        request.setAttribute("events", events);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalEvents", totalEvents);
        request.setAttribute("pageSize", pageSize);

        request.setAttribute("currentKeyword", keyword != null ? keyword : "");
        request.setAttribute("currentPublicFilter", publicFilter);
        request.setAttribute("currentSortByDate", sortByDate);
        request.setAttribute("favoriteEvents", favoriteEvents);

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
