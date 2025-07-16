/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dal.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import models.*;


public class ChairmanClubMeetingController extends HttpServlet {
   

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        Users user =  (Users) request.getSession().getAttribute("user");
        ClubInfo club =  (ClubInfo) request.getSession().getAttribute("club");
        if (user!= null) {
           int clubID = club.getClubID();
        String action = request.getParameter("action");
        String search = request.getParameter("search") != null ? request.getParameter("search") : "";
        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException | NullPointerException e) {
            page = 1;
        }
        int pageSize = 6;

        List<ClubMeeting> meetings = ClubMeetingDAO.findByClubID(clubID, search, page, pageSize);
        int totalRecords = ClubMeetingDAO.countByClubID(clubID, search);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        
        request.setAttribute("meetings", meetings);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("search", search);
        
        

        
            
            request.getRequestDispatcher("/view/student/chairman/clubmeeting.jsp").forward(request, response);
        }else{
            response.sendRedirect(request.getContextPath() + "/login");
        }
        
    } 
    

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "add":
                
                break;
            default:
                throw new AssertionError();
        }
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ChairmanClubMeetingController</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ChairmanClubMeetingController at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    


    
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
