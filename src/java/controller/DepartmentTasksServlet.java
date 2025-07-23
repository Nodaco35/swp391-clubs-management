package controller;

import dal.TaskDAO;
import dal.UserDAO;
import dal.DepartmentDAO;
import dal.DepartmentDashboardDAO;
import models.Tasks;
import models.Users;
import models.Department;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class DepartmentTasksServlet extends HttpServlet {
    
    private TaskDAO taskDAO;
    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    private DepartmentDashboardDAO departmentDashboardDAO;
    
    @Override
    public void init() throws ServletException {
        taskDAO = new TaskDAO();
        userDAO = new UserDAO();
        departmentDAO = new DepartmentDAO();
        departmentDashboardDAO = new DepartmentDashboardDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Get club ID and department info from session or parameters
            String clubIDParam = request.getParameter("clubID");
            if (clubIDParam == null || clubIDParam.trim().isEmpty()) {
                request.setAttribute("error", "Không tìm thấy thông tin CLB.");
                request.getRequestDispatcher("/view/error.jsp").forward(request, response);
                return;
            }
            
            int clubID = Integer.parseInt(clubIDParam);
            
            // Check if user is department leader of this club
            if (!departmentDashboardDAO.isDepartmentLeader(currentUser.getUserID())) {
                request.setAttribute("error", "Bạn không có quyền truy cập chức năng này.");
                request.getRequestDispatcher("/view/error.jsp").forward(request, response);
                return;
            }
            
            // Get department leader's department ID
            int clubDepartmentId = departmentDashboardDAO.findClubDepartmentId(clubID, currentUser.getUserID());
            if (clubDepartmentId == 0) {
                request.setAttribute("error", "Không tìm thấy thông tin ban của bạn.");
                request.getRequestDispatcher("/view/error.jsp").forward(request, response);
                return;
            }
            
            // Get filter parameters
            String status = request.getParameter("status");
            String searchKeyword = request.getParameter("search");
            String sortByParam = request.getParameter("sortBy");
            String sortOrderParam = request.getParameter("sortOrder");
            
            // Set default sorting if not provided
            final String sortBy = (sortByParam == null || sortByParam.trim().isEmpty()) ? "createdAt" : sortByParam;
            final String sortOrder = (sortOrderParam == null || sortOrderParam.trim().isEmpty()) ? "desc" : sortOrderParam;
            
            // Get tasks assigned to individual members in this department
            List<Tasks> assignedTasks = taskDAO.getTasksForDepartmentMembers(clubDepartmentId);
            
            // Apply search filter
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                String keyword = searchKeyword.toLowerCase().trim();
                assignedTasks = assignedTasks.stream()
                    .filter(task -> {
                        // Search in task title
                        boolean titleMatch = task.getTitle() != null && 
                                           task.getTitle().toLowerCase().contains(keyword);
                        
                        // Search in assignee name
                        boolean assigneeMatch = false;
                        if (task.getUserAssignee() != null && task.getUserAssignee().getFullName() != null) {
                            assigneeMatch = task.getUserAssignee().getFullName().toLowerCase().contains(keyword);
                        }
                        
                        // Search in description
                        boolean descMatch = task.getDescription() != null && 
                                          task.getDescription().toLowerCase().contains(keyword);
                        
                        return titleMatch || assigneeMatch || descMatch;
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Apply status filter
            if (status != null && !status.trim().isEmpty()) {
                assignedTasks = assignedTasks.stream()
                    .filter(task -> status.equals(task.getStatus()))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Apply sorting
            assignedTasks.sort((task1, task2) -> {
                int comparison = 0;
                
                switch (sortBy) {
                    case "title":
                        String title1 = task1.getTitle() != null ? task1.getTitle() : "";
                        String title2 = task2.getTitle() != null ? task2.getTitle() : "";
                        comparison = title1.compareToIgnoreCase(title2);
                        break;
                        
                    case "status":
                        String status1 = task1.getStatus() != null ? task1.getStatus() : "";
                        String status2 = task2.getStatus() != null ? task2.getStatus() : "";
                        comparison = status1.compareToIgnoreCase(status2);
                        break;
                        
                    case "startDate":
                        if (task1.getStartDate() == null && task2.getStartDate() == null) {
                            comparison = 0;
                        } else if (task1.getStartDate() == null) {
                            comparison = 1; // null dates go to end
                        } else if (task2.getStartDate() == null) {
                            comparison = -1;
                        } else {
                            comparison = task1.getStartDate().compareTo(task2.getStartDate());
                        }
                        break;
                        
                    case "endDate":
                        if (task1.getEndDate() == null && task2.getEndDate() == null) {
                            comparison = 0;
                        } else if (task1.getEndDate() == null) {
                            comparison = 1; // null dates go to end
                        } else if (task2.getEndDate() == null) {
                            comparison = -1;
                        } else {
                            comparison = task1.getEndDate().compareTo(task2.getEndDate());
                        }
                        break;
                        
                    case "createdAt":
                    default:
                        if (task1.getCreatedAt() == null && task2.getCreatedAt() == null) {
                            comparison = 0;
                        } else if (task1.getCreatedAt() == null) {
                            comparison = 1;
                        } else if (task2.getCreatedAt() == null) {
                            comparison = -1;
                        } else {
                            comparison = task1.getCreatedAt().compareTo(task2.getCreatedAt());
                        }
                        break;
                }
                
                // Apply sort order
                return "asc".equals(sortOrder) ? comparison : -comparison;
            });
            
            // Get departments for this club
            List<Department> clubDepartments = departmentDAO.getDepartmentsByClubID(clubID);
            
            // Set attributes for JSP
            request.setAttribute("assignedTasks", assignedTasks);
            request.setAttribute("clubDepartments", clubDepartments);
            request.setAttribute("currentUser", currentUser);
            request.setAttribute("clubID", clubID);
            
            // For sidebar navigation
            request.setAttribute("isHauCan", false); // Can set based on user's department
            request.setAttribute("isAccess", false); // Can set based on user's department
            
            request.getRequestDispatcher("/view/student/department-leader/tasks.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }
}
