/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import java.io.IOException;
import java.io.PrintWriter;
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
import models.Users;

/**
 *
 * @author LE VAN THUAN
 */
public class AddLocationServlet extends HttpServlet {
   
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
            out.println("<title>Servlet AddLocationServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AddLocationServlet at " + request.getContextPath () + "</h1>");
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

            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
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
        LocationDAO locationDAO = new LocationDAO();
        String newLocationName = request.getParameter("newLocationName");
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        ClubDAO clubDAO = new ClubDAO();

        if (newLocationName == null || newLocationName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Tên địa điểm không được để trống.");
            request.setAttribute("locations", locationDAO.getLocationsByType("OffCampus"));
            request.setAttribute("locationType", "OffCampus");
            if (user != null) {
                String userID = user.getUserID();
                ClubInfo club = clubDAO.getClubChairman(userID);
                request.setAttribute("club", club);
            }
            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
            return;
        }

        try {
            if (locationDAO.isLocationNameExists(newLocationName.trim(), "OffCampus")) {
                request.setAttribute("errorMessage", "Địa điểm '" + newLocationName + "' đã tồn tại.");
                request.setAttribute("locations", locationDAO.getLocationsByType("OffCampus"));
                request.setAttribute("locationType", "OffCampaign");
                if (user != null) {
                    String userID = user.getUserID();
                    ClubInfo club = clubDAO.getClubChairman(userID);
                    request.setAttribute("club", club);
                }
                request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
                return;
            }

            locationDAO.addOffCampusLocation(newLocationName.trim());
            session.setAttribute("successMsg", "Thêm địa điểm thành công!");
            response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/add-event");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", "Không thể thêm địa điểm: " + e.getMessage());
            request.setAttribute("locations", locationDAO.getLocationsByType("OffCampaign"));
            request.setAttribute("locationType", "OffCampaign");
            if (user != null) {
                String userID = user.getUserID();
                ClubInfo club = clubDAO.getClubChairman(userID);
                request.setAttribute("club", club);
            }
            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
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
