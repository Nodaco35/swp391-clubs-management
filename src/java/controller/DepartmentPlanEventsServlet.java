package controller;

import dal.DepartmentDashboardDAO;
import dal.EventsDAO;
import dal.TaskDAO;
import models.Users;
import models.Events;
import models.Tasks;
import models.DepartmentDashboard;
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
    
    @Override
    public void init() throws ServletException {
        dashboardDAO = new DepartmentDashboardDAO();
        eventsDAO = new EventsDAO();
        taskDAO = new TaskDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // Handle API requests
        if ("task-detail".equals(action)) {
            getTaskDetail(request, response);
            return;
        }
        
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
            
            // FIX: Cần lấy ClubDepartmentID thay vì DepartmentID
            int departmentId = dashboard.getDepartmentId(); // Này là DepartmentID từ bảng Departments
            LOGGER.info("Department info - ID: " + departmentId + ", Name: " + dashboard.getDepartmentName());
            
            // DEBUG: In ra để kiểm tra data
            System.out.println("DEBUG - ClubID: " + clubID + ", DepartmentID: " + departmentId);
            
            // Lấy danh sách tasks của department - SỬA ĐỂ MATCH VỚI DATABASE
            List<Tasks> departmentTasks = taskDAO.getTasksByClubAndDepartment(clubID, departmentId);
            LOGGER.info("ClubID: " + clubID + ", DepartmentID: " + departmentId + ", Tasks found: " + (departmentTasks != null ? departmentTasks.size() : "null"));
            
            // DEBUG: In ra từng task để kiểm tra
            if (departmentTasks != null) {
                for (Tasks task : departmentTasks) {
                    System.out.println("DEBUG Task: ID=" + task.getTaskID() + ", Title=" + task.getTitle() + ", Status=" + task.getStatus());
                }
            }
            
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
        
        String action = request.getParameter("action");
        
        // Handle update task status
        if ("update-task-status".equals(action)) {
            updateTaskStatus(request, response);
            return;
        }
        
        doGet(request, response);
    }
    
    private void getTaskDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String taskIdParam = request.getParameter("taskId");
            if (taskIdParam == null || taskIdParam.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Task ID is required\"}");
                return;
            }
            
            int taskId = Integer.parseInt(taskIdParam);
            Tasks task = taskDAO.getTaskById(taskId);
            
            if (task == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Task not found\"}");
                return;
            }
            
            // Convert task to JSON manually since we're avoiding Gson imports
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"taskId\": ").append(task.getTaskID()).append(",");
            json.append("\"title\": \"").append(escapeJson(task.getTitle())).append("\",");
            json.append("\"description\": \"").append(escapeJson(task.getDescription())).append("\",");
            json.append("\"status\": \"").append(task.getStatus()).append("\",");
            json.append("\"startDate\": \"").append(task.getStartDate() != null ? task.getStartDate().toString() : "").append("\",");
            json.append("\"endDate\": \"").append(task.getEndDate() != null ? task.getEndDate().toString() : "").append("\",");
            json.append("\"createdAt\": \"").append(task.getCreatedAt() != null ? task.getCreatedAt().toString() : "").append("\"");
            if (task.getCreatedBy() != null) {
                json.append(",\"createdBy\": \"").append(escapeJson(task.getCreatedBy().getFullName())).append("\"");
            }
            if (task.getEvent() != null) {
                json.append(",\"eventName\": \"").append(escapeJson(task.getEvent().getEventName())).append("\"");
            }
            json.append("}");
            
            response.getWriter().write(json.toString());
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid task ID format\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error\"}");
        }
    }
    
    private void updateTaskStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String taskIdParam = request.getParameter("taskId");
            String newStatus = request.getParameter("status");
            
            if (taskIdParam == null || taskIdParam.isEmpty() || newStatus == null || newStatus.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Task ID and status are required\"}");
                return;
            }
            
            int taskId = Integer.parseInt(taskIdParam);
            
            // Validate status
            if (!isValidStatus(newStatus)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Invalid status value\"}");
                return;
            }
            
            boolean success = taskDAO.updateTaskStatus(taskId, newStatus);
            
            if (success) {
                response.getWriter().write("{\"success\": true, \"message\": \"Task status updated successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Failed to update task status\"}");
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid task ID format\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error\"}");
        }
    }
    
    private boolean isValidStatus(String status) {
        return "ToDo".equals(status) || "InProgress".equals(status) || 
               "Review".equals(status) || "Done".equals(status) || "Rejected".equals(status);
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
