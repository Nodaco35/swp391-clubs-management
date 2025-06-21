/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.ClubApplicationDAO;
import dal.EventsDAO;
import dal.NotificationDAO;
import dal.TaskAssignmentDAO;
import dal.UserClubDAO;
import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import models.ClubApplication;
import models.Events;
import models.Notification;
import models.TaskAssignment;
import models.UserClub;
import models.Users;

public class MyClubController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            String error;
            Users user = (Users) request.getSession().getAttribute("user");
            if (user == null) {
                request.getRequestDispatcher("index.jsp").forward(request, response);
                return;
            }
            List<UserClub> userclubs = UserClubDAO.findByUserID(user.getUserID());
            if (userclubs.isEmpty()) {
                error = "Bạn chưa tham gia bất cứ câu lạc bộ nào!";
                request.setAttribute("error", error);
                response.sendRedirect(request.getContextPath() + "/clubs");
                return;
            }
            
            int countTodoLists = TaskAssignmentDAO.countByUserID(user.getUserID());
            
            int countPendingApplication = ClubApplicationDAO.countpendingApplicationsFindByClub(user.getUserID());
            
            List<Notification> recentNotifications = NotificationDAO.findRecentByUserID(user.getUserID());

            List<Events> upcomingEvents = EventsDAO.findByUCID(user.getUserID());

            List<ClubApplication> pendingApplications = ClubApplicationDAO.pendingApplicationsFindByClub(user.getUserID());

            List<TaskAssignment> todoLists = TaskAssignmentDAO.findByUserID(user.getUserID());
            
            

            request.setAttribute("userclubs", userclubs);
            request.setAttribute("recentNotifications", recentNotifications);
            request.setAttribute("upcomingEvents", upcomingEvents);
            request.setAttribute("pendingApplications", pendingApplications);
            request.setAttribute("todoList", todoLists);
            request.setAttribute("countTodoLists", countTodoLists);
            request.setAttribute("countPendingApplication", countPendingApplication);
            request.getRequestDispatcher("view/student/myClub.jsp").forward(request, response);
        }
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet MyClubController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MyClubController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
