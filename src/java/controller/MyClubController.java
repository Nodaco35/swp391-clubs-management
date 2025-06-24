
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.ClubApplicationDAO;
import dal.ClubDAO;
import dal.ClubMeetingDAO;
import dal.DepartmentMeetingDAO;
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
import models.ClubMeeting;
import models.Clubs;
import models.DepartmentMeeting;
import models.Events;
import models.Notification;
import models.TaskAssignment;
import models.UserClub;
import models.Users;

public class MyClubController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
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
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            int countTodoLists = TaskAssignmentDAO.countByUserID(user.getUserID());

            int countPendingApplication = ClubApplicationDAO.countpendingApplicationsFindByClub(user.getUserID());

            EventsDAO ev = new EventsDAO();

            int countUpcomingMeeting = ClubMeetingDAO.countByUserID(user.getUserID());

            List<Notification> recentNotifications = NotificationDAO.findRecentByUserID(user.getUserID());

            List<Events> upcomingEvents = ev.findByUCID(user.getUserID());

            List<ClubApplication> pendingApplications = ClubApplicationDAO.pendingApplicationsFindByClub(user.getUserID());

            List<TaskAssignment> todoLists = TaskAssignmentDAO.findByUserID(user.getUserID());

            List<ClubMeeting> clubmeetings = ClubMeetingDAO.findByUserID(user.getUserID());

            //mới
            int countUpcomingDepartmentMeeting = DepartmentMeetingDAO.countByUID(user.getUserID());
            List<DepartmentMeeting> departmentmeetings = DepartmentMeetingDAO.findByUserID(user.getUserID());
            request.setAttribute("countUpcomingDepartmentMeeting", countUpcomingDepartmentMeeting);
            request.setAttribute("departmentmeetings", departmentmeetings);
            List<Clubs> listClubAsChairman = ClubDAO.findByUserIDAndChairman(user.getUserID());
            request.setAttribute("listClubAsChairman", listClubAsChairman);
            //

            request.setAttribute("userclubs", userclubs);
            request.setAttribute("recentNotifications", recentNotifications);
            request.setAttribute("upcomingEvents", upcomingEvents);

            request.setAttribute("countUpcomingMeeting", countUpcomingMeeting);
            request.setAttribute("pendingApplications", pendingApplications);
            request.setAttribute("departmentTasks", todoLists);
            request.setAttribute("countTodoLists", countTodoLists);
            request.setAttribute("countPendingApplication", countPendingApplication);

            request.setAttribute("clubmeetings", clubmeetings);
            request.getRequestDispatcher("view/student/myClub.jsp").forward(request, response);
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action.equals("submitCreateMeeting")) {
            createClubMeeting(request, response);
        }
        if (action.equals("submitUpdateMeeting")) {
            submitUpdateMeeting(request, response);
        }
        if (action.equals("deleteClubMeeting")) {
            deleteClubMeeting(request, response);
        }
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

    private void createClubMeeting(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }
        int clubID = Integer.parseInt(request.getParameter("clubId"));
        String startedTime = request.getParameter("startedTime");
        String URLMeeting = request.getParameter("URLMeeting");

        ClubMeetingDAO.insert(clubID, startedTime, URLMeeting);

        List<UserClub> userInClub = UserClubDAO.findByClubID(clubID);
        for (UserClub userClub : userInClub) {
            NotificationDAO.sentToPerson(user.getUserID(), userClub.getUserID(), "Cuộc họp mới", "Link tham gia: " + URLMeeting);
        }
        doGet(request, response);
    }
    private void submitUpdateMeeting(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }
        int clubID = Integer.parseInt(request.getParameter("clubId"));
        String startedTime = request.getParameter("startedTime");
        String URLMeeting = request.getParameter("URLMeeting");
        int clubMeetingID = Integer.parseInt(request.getParameter("clubMeetingId"));
        ClubMeetingDAO.update(clubID, startedTime, URLMeeting, clubMeetingID);
        String formattedTime = startedTime.replace("T", " ").substring(0, 16);
        String content = "Link tham gia: <a href=\"" + URLMeeting + "\">" + URLMeeting + "</a><br/>Thời gian bắt đầu: <strong>" + formattedTime + "</strong>";
        List<UserClub> userInClub = UserClubDAO.findByClubID(clubID);
        for (UserClub userClub : userInClub) {
            NotificationDAO.sentToPerson(user.getUserID(), userClub.getUserID(), "Thay đổi cuộc họp", content);
        }
        doGet(request, response);
    }

    private void deleteClubMeeting(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        int clubMeetingID = Integer.parseInt(request.getParameter("clubMeetingId"));
        ClubMeetingDAO.delete(clubMeetingID);

        doGet(request, response);
    }

}