package controller;

import dal.ClubApplicationDAO;
import dal.ClubDAO;
import dal.EventsDAO;
import dal.PeriodicReportDAO;
import dal.UserDAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.PrintWriter;
import models.Club;
import models.ClubApplication;
import models.Events;
import models.PeriodicReport;
import models.User;

public class HomepageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        
        // Lấy dữ liệu câu lạc bộ nổi bật
        ClubDAO clubDAO = new ClubDAO();
        List<Club> featuredClubs = clubDAO.getFeaturedClubs(6);
        request.setAttribute("featuredClubs", featuredClubs);
        
        // Lấy dữ liệu sự kiện sắp tới
        EventsDAO eventDAO = new EventsDAO();
        List<Events> upcomingEvents = eventDAO.getUpcomingEvents(4);
        request.setAttribute("upcomingEvents", upcomingEvents);
        
        // Lấy dữ liệu thống kê
        int totalClubs = clubDAO.getTotalActiveClubs();
        int totalMembers = clubDAO.getTotalClubMembers();
        int totalEvents = eventDAO.getTotalEvents();
        int totalDepartments = clubDAO.getTotalDepartments();
        
        request.setAttribute("totalClubs", totalClubs);
        request.setAttribute("totalMembers", totalMembers);
        request.setAttribute("totalEvents", totalEvents);
        request.setAttribute("totalDepartments", totalDepartments);
        
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        
        if(user==null || user.getPermissionID()==1){
        // Forward request đến trang index.jsp
        request.getRequestDispatcher("index.jsp").forward(request, response);
        }else if(user.getPermissionID() == 2){
            response.sendRedirect(request.getContextPath() + "/admin");
        }else if(user.getPermissionID() == 3){
            response.sendRedirect(request.getContextPath() + "/ic");
        }
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }
}