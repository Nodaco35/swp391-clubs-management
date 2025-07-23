package controller;

import dal.DepartmentDashboardDAO;
import dal.EventsDAO;
import dal.TaskDAO;
import dal.ClubDepartmentDAO;
import models.Users;
import models.Events;
import models.Tasks;
import models.DepartmentDashboard;
import models.ClubDepartment;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class DepartmentPlanEventsServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(DepartmentPlanEventsServlet.class.getName());
    private DepartmentDashboardDAO dashboardDAO;
    private EventsDAO eventsDAO;
    private TaskDAO taskDAO;
    private ClubDepartmentDAO clubDepartmentDAO;
    
    @Override
    public void init() throws ServletException {
        dashboardDAO = new DepartmentDashboardDAO();
        eventsDAO = new EventsDAO();
        taskDAO = new TaskDAO();
        clubDepartmentDAO = new ClubDepartmentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        // Kiểm tra đăng nhập
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Kiểm tra quyền trưởng ban
            if (!dashboardDAO.isDepartmentLeader(currentUser.getUserID())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này");
                return;
            }
            
            Integer clubID = (Integer) session.getAttribute("clubID");
            if (clubID == null) {
                clubID = Integer.parseInt(request.getParameter("clubID"));
                session.setAttribute("clubID", clubID);
            }
            
            // Lấy tab hiện tại (timeline, chairman-plans, upcoming-events)
            String activeTab = request.getParameter("tab");
            if (activeTab == null) {
                activeTab = "timeline";
            }
            
            // Lấy thông tin department của user hiện tại
            DepartmentDashboard dashboard = dashboardDAO.getDepartmentInfo(currentUser.getUserID());
            LOGGER.info("Current user: " + currentUser.getUserID() + " - " + currentUser.getFullName());
            if (dashboard == null) {
                LOGGER.warning("Dashboard is null for user: " + currentUser.getUserID());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Không tìm thấy thông tin ban của bạn");
                return;
            }
            
            int departmentId = dashboard.getDepartmentId();
            LOGGER.info("Department info - ID: " + departmentId + ", Name: " + dashboard.getDepartmentName());
            
            // Lấy danh sách tasks của department
            List<Tasks> departmentTasks = taskDAO.getTasksByClubAndDepartment(clubID, departmentId);
            LOGGER.info("ClubID: " + clubID + ", DepartmentID: " + departmentId + ", Tasks found: " + (departmentTasks != null ? departmentTasks.size() : "null"));
            
            // Lấy danh sách sự kiện của club
            List<Events> clubEvents = eventsDAO.getEventsByClubID(clubID);
            
            // Lọc sự kiện sắp tới (có thể tạo method mới trong EventsDAO sau)
            List<Events> upcomingEvents = clubEvents; // Tạm thời lấy tất cả
            
            // Đưa dữ liệu vào request
            request.setAttribute("departmentTasks", departmentTasks);
            request.setAttribute("clubEvents", clubEvents);
            request.setAttribute("upcomingEvents", upcomingEvents);
            request.setAttribute("activeTab", activeTab);
            request.setAttribute("clubID", clubID);
            request.setAttribute("departmentName", dashboard.getDepartmentName());
            
            // Forward đến JSP
            request.getRequestDispatcher("view/student/department-leader/plan-events.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
