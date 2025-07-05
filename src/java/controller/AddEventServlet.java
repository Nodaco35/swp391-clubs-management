/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import dal.ClubDAO;
import dal.EventsDAO;
import dal.LocationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.ClubInfo;
import models.Locations;
import models.Users;

/**
 * @author LE VAN THUAN
 */
public class AddEventServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AddEventServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AddEventServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        ClubDAO clubDAO = new ClubDAO();
        LocationDAO locationDAO = new LocationDAO();
        Integer myClubID = (Integer) session.getAttribute("myClubID");

        if (myClubID == null || myClubID <= 0) {
            request.setAttribute("errorMessage", "Vui lòng đăng nhập với tư cách chủ nhiệm câu lạc bộ.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (user != null) {
            String userID = user.getUserID();
            ClubInfo club = clubDAO.getClubChairman(userID);
            request.setAttribute("club", club);
            String currentPath = request.getServletPath();
            request.setAttribute("currentPath", currentPath);


            request.setAttribute("locations", locationDAO.getLocationsByType("OnCampus"));

            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
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

        String eventName = request.getParameter("eventName");
        String eventLocationIDStr = request.getParameter("eventLocation");
        String maxParticipantsStr = request.getParameter("maxParticipants");
        String eventDate = request.getParameter("eventDate");
        String eventTime = request.getParameter("eventTime");
        String eventEndTime = request.getParameter("eventEndTime");

        if (eventName == null || eventName.trim().isEmpty() ||
                eventLocationIDStr == null || eventLocationIDStr.trim().isEmpty() ||
                maxParticipantsStr == null || maxParticipantsStr.trim().isEmpty() ||
                eventDate == null || eventDate.trim().isEmpty() ||
                eventTime == null || eventTime.trim().isEmpty() ||
                eventEndTime == null || eventEndTime.trim().isEmpty()) {


            String locationType = request.getParameter("locationType");
            LocationDAO locationDAO = new LocationDAO();
            request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
            request.setAttribute("locationType", locationType);

            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
            return;
        }


        String eventType = request.getParameter("eventType");
        String eventDescription = request.getParameter("eventDescription");

        String locationType = request.getParameter("locationType");
        LocationDAO locationDAO = new LocationDAO();

        if (locationType != null && !locationType.isEmpty()) {
            request.setAttribute("locations", locationDAO.getLocationsByType(locationType));
            request.setAttribute("locationType", locationType);
        } else {
            request.setAttribute("locations", locationDAO.getLocationsByType("OnCampus"));
        }

        try {
            int maxParticipants = Integer.parseInt(maxParticipantsStr);
            int locationId = Integer.parseInt(eventLocationIDStr);

            if (maxParticipants <= 0 || maxParticipants > 10000) {
                request.setAttribute("errorMessage", "Số lượng tối đa phải lớn hơn 0 hoặc nhỏ hơn 10000 người");
                request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
                request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
                return;
            }

            if (locationId <= 0) {
                request.setAttribute("errorMessage", "Vui lòng chọn một địa điểm hợp lệ.");
                request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
                request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
                return;
            }

            Timestamp startDateTime = Timestamp.valueOf(eventDate + " " + eventTime + ":00");
            Timestamp endDateTime = Timestamp.valueOf(eventDate + " " + eventEndTime + ":00");

            EventsDAO dao = new EventsDAO();

            if (dao.isLocationConflict(locationId, startDateTime, endDateTime)) {
                request.setAttribute("errorMessage", "Địa điểm đã được sử dụng trong khoảng thời gian này.");
                request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
                request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
                return;
            }

            boolean isPublic = "public".equalsIgnoreCase(eventType);
            dao.insertEvent(eventName, eventDescription, startDateTime, endDateTime, locationId, myClubID, isPublic, maxParticipants);
            response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events");

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Dữ liệu không hợp lệ, vui lòng kiểm tra số lượng tối đa hoặc địa điểm.");
            request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Định dạng ngày giờ không hợp lệ.");
            request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi thêm sự kiện: " + e.getMessage());
            request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
