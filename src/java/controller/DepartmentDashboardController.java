package controller;

import dal.ClubDepartmentDAO;
import dal.DepartmentMemberDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import models.ClubDepartment;
import models.DepartmentMember;
import models.TaskAssignmentDepartment;
import models.DepartmentMeeting;
import models.Users;
import service.DepartmentMemberService;
import service.DepartmentMeetingService;
import service.TaskAssignmentDepartmentService;

/**
 * Controller for department dashboard
 */
public class DepartmentDashboardController extends HttpServlet {
    
    private final DepartmentMemberService departmentMemberService;
    private final TaskAssignmentDepartmentService taskAssignmentDepartmentService;
    private final DepartmentMeetingService departmentMeetingService;
    private final ClubDepartmentDAO clubDepartmentDAO;
    
    public DepartmentDashboardController() {
        this.departmentMemberService = new DepartmentMemberService();
        this.taskAssignmentDepartmentService = new TaskAssignmentDepartmentService();
        this.departmentMeetingService = new DepartmentMeetingService();
        this.clubDepartmentDAO = new ClubDepartmentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy clubID từ session hoặc parameter
        int clubID = 0;
        int departmentID = 0;
        
        try {
            if (request.getParameter("clubID") != null) {
                clubID = Integer.parseInt(request.getParameter("clubID"));
                session.setAttribute("clubID", clubID);
            } else if (session.getAttribute("clubID") != null) {
                clubID = (Integer) session.getAttribute("clubID");
            }
            
            if (request.getParameter("departmentID") != null) {
                departmentID = Integer.parseInt(request.getParameter("departmentID"));
                session.setAttribute("departmentID", departmentID);
            } else if (session.getAttribute("departmentID") != null) {
                departmentID = (Integer) session.getAttribute("departmentID");
            } else {                // Nếu không có departmentID, thử lấy từ vai trò của người dùng trong club
                List<ClubDepartment> userDepartments = clubDepartmentDAO.getDepartmentsByUserAndClub(
                        currentUser.getUserID(), clubID);
                if (!userDepartments.isEmpty()) {                    // Lấy department đầu tiên mà user là leader
                    for (ClubDepartment dept : userDepartments) {
                        boolean isLeader = departmentMemberService.isUserDepartmentLeader(
                                currentUser.getUserID(), dept.getDepartmentId());
                        if (isLeader) {
                            departmentID = dept.getDepartmentId();
                            session.setAttribute("departmentID", departmentID);
                            break;
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/user-clubs");
            return;
        }
        
        if (clubID == 0 || departmentID == 0) {
            response.sendRedirect(request.getContextPath() + "/user-clubs");
            return;
        }
        
        // Lấy thông tin tổng quan cho dashboard
        loadDashboardInfo(request, departmentID);
        
        request.setAttribute("departmentID", departmentID);
        request.setAttribute("clubID", clubID);
        request.setAttribute("active", "dashboard");
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/view/student/department-leader/dashboard.jsp");
        dispatcher.forward(request, response);
    }    /**
     * Tải thông tin tổng quan cho dashboard
     */
    private void loadDashboardInfo(HttpServletRequest request, int departmentID) {
        // Lấy ClubDepartmentID từ DepartmentID
        ClubDepartment clubDepartment = clubDepartmentDAO.getDepartmentByDepartmentID(departmentID);
        int clubDepartmentID = 0;
        if (clubDepartment != null) {
            clubDepartmentID = clubDepartment.getClubDepartmentId();
        }
        
        // Thống kê thành viên
        int totalMembers = departmentMemberService.countDepartmentMembers(departmentID);
        Map<String, Integer> membersByRole = departmentMemberService.countMembersByRole(departmentID);
        List<DepartmentMember> recentMembers = departmentMemberService.getRecentDepartmentMembers(departmentID, 5);
        
        // Thống kê nhiệm vụ - sử dụng clubDepartmentID thay vì departmentID
        int totalTasks = taskAssignmentDepartmentService.countTasksByClubDepartment(clubDepartmentID);
        int completedTasks = taskAssignmentDepartmentService.countTasksByClubDepartmentAndStatus(clubDepartmentID, "Completed");
        int pendingTasks = taskAssignmentDepartmentService.countTasksByClubDepartmentAndStatus(clubDepartmentID, "In Progress");
        List<TaskAssignmentDepartment> upcomingDeadlines = taskAssignmentDepartmentService.getUpcomingDeadlinesByClubDepartment(clubDepartmentID, 5);
        
        // Thống kê cuộc họp
        int totalMeetings = departmentMeetingService.countMeetings(departmentID);
        int upcomingMeetings = departmentMeetingService.countUpcomingMeetings(departmentID);
        List<DepartmentMeeting> recentMeetings = departmentMeetingService.getRecentMeetings(departmentID, 5);
        
        // Set attributes
        request.setAttribute("totalMembers", totalMembers);
        request.setAttribute("membersByRole", membersByRole);
        request.setAttribute("recentMembers", recentMembers);
        
        request.setAttribute("totalTasks", totalTasks);
        request.setAttribute("completedTasks", completedTasks);
        request.setAttribute("pendingTasks", pendingTasks);
        request.setAttribute("taskCompletionRate", totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0);
        request.setAttribute("upcomingDeadlines", upcomingDeadlines);
        
        request.setAttribute("totalMeetings", totalMeetings);
        request.setAttribute("upcomingMeetings", upcomingMeetings);
        request.setAttribute("recentMeetings", recentMeetings);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
