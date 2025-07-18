package controller;

import dal.ClubDAO;
import dal.EventsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Clubs;
import models.Events;
import models.Users;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class HomepageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        
        // Lấy dữ liệu câu lạc bộ nổi bật
        ClubDAO clubDAO = new ClubDAO();
        List<Clubs> featuredClubs = clubDAO.getFeaturedClubs(6);
        
        // Set favorite status for each featured club
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        String userID = (user != null) ? user.getUserID() : null;
        if (userID != null) {
            for (Clubs club : featuredClubs) {
                boolean isFavorite = clubDAO.isFavoriteClub(userID, club.getClubID());
                club.setFavorite(isFavorite);
            }
        }
        
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

        String currentPath = request.getServletPath();
        request.setAttribute("currentPath", currentPath);
        
        request.setAttribute("totalClubs", totalClubs);
        request.setAttribute("totalMembers", totalMembers);
        request.setAttribute("totalEvents", totalEvents);
        request.setAttribute("totalDepartments", totalDepartments);
        
        if (user == null || user.getPermissionID() == 1) {
            // Forward request đến trang index.jsp
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } else if (user.getPermissionID() == 2) {
            response.sendRedirect(request.getContextPath() + "/admin");
        } else if (user.getPermissionID() == 3) {
            response.sendRedirect(request.getContextPath() + "/ic");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}