package controller;

import dal.TaskDAO;
import dal.UserDAO;
import dal.DepartmentDAO;
import dal.DepartmentDashboardDAO;
import dal.UserClubDAO;
import dal.EventsDAO;
import dal.ClubDAO;
import models.Tasks;
import models.Users;
import models.Department;
import models.Events;
import models.Clubs;
import models.EventTerms;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import dal.ApprovalHistoryDAO;

public class DepartmentTasksServlet extends HttpServlet {
    
    private TaskDAO taskDAO;
    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    private DepartmentDashboardDAO departmentDashboardDAO;
    private UserClubDAO userClubDAO;
    private EventsDAO eventsDAO;
    
    @Override
    public void init() throws ServletException {
        taskDAO = new TaskDAO();
        userDAO = new UserDAO();
        departmentDAO = new DepartmentDAO();
        departmentDashboardDAO = new DepartmentDashboardDAO();
        userClubDAO = new UserClubDAO();
        eventsDAO = new EventsDAO();
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
        
        // Check if this is an AJAX request for member search or task detail
        String action = request.getParameter("action");
        System.out.println("DEBUG GET: action=" + action + ", requestURI=" + request.getRequestURI());
        
        if ("searchMembers".equals(action)) {
            System.out.println("DEBUG GET: Calling handleMemberSearch");
            handleMemberSearch(request, response, currentUser);
            return;
        }
        
        if ("getTaskDetail".equals(action)) {
            System.out.println("DEBUG GET: Calling handleGetTaskDetail");
            handleGetTaskDetail(request, response, currentUser);
            return;
        }
        if("rejectTask".equals(action)){
            ApprovalHistoryDAO ah = new ApprovalHistoryDAO();
            String reason = request.getParameter("reason");  
            String taskID_raw = request.getParameter("taskID"); 
            String clubID_raw = request.getParameter("clubID"); 
            int taskID = -1;
            if(taskID_raw != null){
                taskID = Integer.parseInt(taskID_raw);
            }
            if(taskID!= - 1){
                ah.rejectTask(taskID, reason);
            }
            String url = "/department-tasks?clubID="+clubID_raw;
            response.sendRedirect(url);
            return;
        }
        if("approveTask".equals(action)){
            ApprovalHistoryDAO ah = new ApprovalHistoryDAO();
            String rating = request.getParameter("rating");  
            String taskID_raw = request.getParameter("taskID"); 
            String clubID_raw = request.getParameter("clubID"); 
            int taskID = -1;
            if(taskID_raw != null){
                taskID = Integer.parseInt(taskID_raw);
            }
            if(taskID!= - 1){
                ah.approveTask(taskID, rating);
            }
            String url = "/department-tasks?clubID="+clubID_raw;
            response.sendRedirect(url);
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
            
            // Get department members for modal (get current user's department members)
            List<Users> departmentMembers = userClubDAO.getUsersByDepartmentAndRole(clubDepartmentId, "Thành viên");
            System.out.println("DEBUG: Found " + departmentMembers.size() + " department members for clubDepartmentId: " + clubDepartmentId);
            
            // Get club events for modal
            List<Events> clubEvents = eventsDAO.getEventsByClubId(clubID);
            System.out.println("DEBUG: Found " + clubEvents.size() + " club events for clubID: " + clubID);
            
            // Set attributes for JSP
            request.setAttribute("assignedTasks", assignedTasks);
            request.setAttribute("clubDepartments", clubDepartments);
            request.setAttribute("departmentMembers", departmentMembers);
            request.setAttribute("clubEvents", clubEvents);
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
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        System.out.println("DEBUG POST: Request received - Method: " + request.getMethod());
        System.out.println("DEBUG POST: Request URI: " + request.getRequestURI());
        System.out.println("DEBUG POST: Query String: " + request.getQueryString());
        
        // Debug all parameters
        System.out.println("DEBUG POST: All parameters:");
        request.getParameterMap().forEach((key, values) -> {
            System.out.println("  " + key + " = " + String.join(", ", values));
        });

        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            System.err.println("ERROR POST: No user in session");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        System.out.println("DEBUG POST: action parameter = " + action);
        
        if ("createTask".equals(action)) {
            handleCreateTask(request, response, currentUser);
        } else {
            System.err.println("ERROR POST: Invalid or missing action parameter: " + action);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }    /**
     * Handle task creation
     */
    private void handleCreateTask(HttpServletRequest request, HttpServletResponse response, Users currentUser)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        try {
            // Get parameters
            String clubIDParam = request.getParameter("clubID");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String assigneeType = request.getParameter("assigneeType");
            String assigneeId = request.getParameter("assigneeId");
            String eventIdParam = request.getParameter("eventId");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            
            // Validate required fields
            if (clubIDParam == null || clubIDParam.trim().isEmpty()) {
                System.err.println("ERROR CREATE: Missing clubID parameter");
                session.setAttribute("error", "Thiếu thông tin câu lạc bộ.");
                response.sendRedirect(request.getContextPath() + "/department-tasks");
                return;
            }
            
            if (title == null || title.trim().isEmpty()) {
                System.err.println("ERROR CREATE: Missing title parameter");
                session.setAttribute("error", "Vui lòng nhập tiêu đề nhiệm vụ.");
                response.sendRedirect(request.getContextPath() + "/department-tasks?clubID=" + clubIDParam);
                return;
            }
            
            if (assigneeType == null || assigneeId == null || assigneeId.trim().isEmpty()) {
                System.err.println("ERROR CREATE: Missing assignee parameters - type: " + assigneeType + ", id: " + assigneeId);
                session.setAttribute("error", "Vui lòng chọn người phụ trách.");
                response.sendRedirect(request.getContextPath() + "/department-tasks?clubID=" + clubIDParam);
                return;
            }
            
            if (eventIdParam == null || eventIdParam.trim().isEmpty()) {
                System.err.println("ERROR CREATE: Missing eventId parameter");
                session.setAttribute("error", "Vui lòng chọn sự kiện liên quan.");
                response.sendRedirect(request.getContextPath() + "/department-tasks?clubID=" + clubIDParam);
                return;
            }
            
            int clubID = Integer.parseInt(clubIDParam);
            
            // Check if user is department leader
            if (!departmentDashboardDAO.isDepartmentLeader(currentUser.getUserID())) {
                session.setAttribute("error", "Bạn không có quyền tạo nhiệm vụ.");
                response.sendRedirect(request.getContextPath() + "/department-tasks?clubID=" + clubID);
                return;
            }
            
            // Get department leader's department ID
            int clubDepartmentId = departmentDashboardDAO.findClubDepartmentId(clubID, currentUser.getUserID());
            if (clubDepartmentId == 0) {
                session.setAttribute("error", "Không tìm thấy thông tin ban của bạn.");
                response.sendRedirect(request.getContextPath() + "/department-tasks?clubID=" + clubID);
                return;
            }
            
            // Create Tasks object
            Tasks task = new Tasks();
            task.setTitle(title.trim());
            task.setDescription(description != null ? description.trim() : "");
            task.setStatus("ToDo");
            task.setCreatedBy(currentUser);
            
            // Set Club - REQUIRED field
            ClubDAO clubDAO = new ClubDAO();
            Clubs club = clubDAO.getClubById(clubID);
            if (club == null) {
                session.setAttribute("error", "Không tìm thấy câu lạc bộ.");
                response.sendRedirect(request.getContextPath() + "/department-tasks?clubID=" + clubID);
                return;
            }
            task.setClub(club);
            
            // Set Event - REQUIRED field from form
            int eventId = Integer.parseInt(eventIdParam);
            Events event = eventsDAO.getEventByID(eventId);
            if (event == null) {
                session.setAttribute("error", "Không tìm thấy sự kiện được chọn.");
                response.sendRedirect(request.getContextPath() + "/department-tasks?clubID=" + clubID);
                return;
            }
            task.setEvent(event);
            
            // Set Term - get first available term for this event
            List<EventTerms> eventTerms = taskDAO.getTermsByEventID(eventId);
            if (eventTerms.isEmpty()) {
                session.setAttribute("error", "Sự kiện này chưa có giai đoạn nào được định nghĩa.");
                response.sendRedirect(request.getContextPath() + "/department-tasks?clubID=" + clubID);
                return;
            }
            // Use first term (usually "Trước sự kiện")
            task.setTerm(eventTerms.get(0));
            
            // Set assignee based on type
            if ("User".equals(assigneeType)) {
                task.setAssigneeType("User");
                // Get user object for assignee
                Users assigneeUser = UserDAO.getUserById(assigneeId);
                if (assigneeUser != null) {
                    task.setUserAssignee(assigneeUser);
                    System.out.println("DEBUG: Assigned task to user: " + assigneeUser.getUserID());
                } else {
                    session.setAttribute("error", "Không tìm thấy thành viên được chọn.");
                    response.sendRedirect(request.getContextPath() + "/department-tasks?clubID=" + clubID);
                    return;
                }
            } else if ("Department".equals(assigneeType)) {
                task.setAssigneeType("Department");
                // Get department object
                Department dept = departmentDAO.getDepartmentByID(Integer.parseInt(assigneeId));
                if (dept != null) {
                    task.setDepartmentAssignee(dept);
                    System.out.println("DEBUG: Assigned task to department: " + dept.getDepartmentID());
                } else {
                    session.setAttribute("error", "Không tìm thấy ban được chọn.");
                    response.sendRedirect(request.getContextPath() + "/department-tasks?clubID=" + clubID);
                    return;
                }
            }
            
            // Set dates
            if (startDateStr != null && !startDateStr.trim().isEmpty()) {
                try {
                    java.sql.Date startDate = java.sql.Date.valueOf(startDateStr);
                    task.setStartDate(startDate);
                } catch (IllegalArgumentException e) {
                    session.setAttribute("error", "Ngày bắt đầu không hợp lệ.");
                    response.sendRedirect(request.getContextPath() + "/department-tasks?clubID=" + clubID);
                    return;
                }
            }
            
            if (endDateStr != null && !endDateStr.trim().isEmpty()) {
                try {
                    java.sql.Date endDate = java.sql.Date.valueOf(endDateStr);
                    task.setEndDate(endDate);
                } catch (IllegalArgumentException e) {
                    session.setAttribute("error", "Ngày kết thúc không hợp lệ.");
                    response.sendRedirect(request.getContextPath() + "/department-tasks?clubID=" + clubID);
                    return;
                }
            }
            
            // Validate dates
            if (task.getStartDate() != null && task.getEndDate() != null && 
                task.getStartDate().after(task.getEndDate())) {
                session.setAttribute("error", "Ngày bắt đầu không thể sau ngày kết thúc.");
                response.sendRedirect(request.getContextPath() + "/department-tasks?clubID=" + clubID);
                return;
            }
            
            // Set timestamps
            java.util.Date now = new java.util.Date();
            task.setCreatedAt(now);
            
            // Create task
            boolean success = taskDAO.addTask(task);
            
            if (success) {
                session.setAttribute("success", "Tạo nhiệm vụ thành công!");
                System.out.println("DEBUG: Task created successfully by user: " + currentUser.getUserID());
            } else {
                session.setAttribute("error", "Không thể tạo nhiệm vụ. Vui lòng thử lại.");
                System.err.println("ERROR: Failed to create task for user: " + currentUser.getUserID());
            }
            
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Dữ liệu không hợp lệ.");
            System.err.println("NumberFormatException in createTask: " + e.getMessage());
        } catch (Exception e) {
            session.setAttribute("error", "Có lỗi xảy ra khi tạo nhiệm vụ: " + e.getMessage());
            System.err.println("Exception in createTask: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Redirect back to tasks page
        String clubIDParam = request.getParameter("clubID");
        response.sendRedirect(request.getContextPath() + "/department-tasks?clubID=" + clubIDParam);
    }
    
    /**
     * Handle AJAX request for member search
     */
    private void handleMemberSearch(HttpServletRequest request, HttpServletResponse response, Users currentUser) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            System.out.println("DEBUG Ajax: handleMemberSearch called");
            String clubIDParam = request.getParameter("clubID");
            String searchTerm = request.getParameter("q");
            
            System.out.println("DEBUG Ajax: clubID=" + clubIDParam + ", searchTerm=" + searchTerm);
            
            if (clubIDParam == null || clubIDParam.trim().isEmpty()) {
                System.err.println("ERROR Ajax: Missing clubID parameter");
                out.write("{\"error\": \"Missing clubID parameter\"}");
                return;
            }
            
            int clubID = Integer.parseInt(clubIDParam);
            System.out.println("DEBUG Ajax: Parsed clubID=" + clubID);
            
            // Get department leader's department ID
            int clubDepartmentId = departmentDashboardDAO.findClubDepartmentId(clubID, currentUser.getUserID());
            System.out.println("DEBUG Ajax: clubDepartmentId=" + clubDepartmentId + " for user=" + currentUser.getUserID());
            
            if (clubDepartmentId == 0) {
                System.err.println("ERROR Ajax: Department not found for user=" + currentUser.getUserID() + ", clubID=" + clubID);
                out.write("{\"error\": \"Department not found\"}");
                return;
            }
            
            // Get department members - try actual role names from database
            List<Users> departmentMembers = userClubDAO.getUsersByDepartmentAndRole(clubDepartmentId, "Thành viên");
            System.out.println("DEBUG Ajax: Found " + departmentMembers.size() + " members for clubDepartmentId: " + clubDepartmentId + " with role 'Thành viên'");
            
            // If no members found, try getting all users except department leader
            if (departmentMembers.isEmpty()) {
                departmentMembers = userClubDAO.getUsersByDepartmentAndRole(clubDepartmentId, "");
                System.out.println("DEBUG Ajax: Tried empty role (all users) - Found " + departmentMembers.size() + " members");
                
                // Filter out department leaders (Trưởng ban)
                departmentMembers = departmentMembers.stream()
                    .filter(member -> !member.getUserID().equals(currentUser.getUserID()))
                    .collect(java.util.stream.Collectors.toList());
                System.out.println("DEBUG Ajax: After filtering out current user - Found " + departmentMembers.size() + " members");
            }
            
            // Filter by search term if provided
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String keyword = searchTerm.toLowerCase().trim();
                departmentMembers = departmentMembers.stream()
                    .filter(member -> {
                        String fullName = member.getFullName() != null ? member.getFullName().toLowerCase() : "";
                        String email = member.getEmail() != null ? member.getEmail().toLowerCase() : "";
                        return fullName.contains(keyword) || email.contains(keyword);
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Convert to JSON
            Gson gson = new Gson();
            JsonArray jsonArray = new JsonArray();
            
            for (Users member : departmentMembers) {
                JsonObject memberObj = new JsonObject();
                memberObj.addProperty("id", member.getUserID());
                memberObj.addProperty("text", member.getFullName() + " (" + member.getEmail() + ")");
                memberObj.addProperty("fullName", member.getFullName());
                memberObj.addProperty("email", member.getEmail());
                memberObj.addProperty("avatar", member.getAvatar());
                jsonArray.add(memberObj);
            }
            
            JsonObject result = new JsonObject();
            result.add("results", jsonArray);
            
            out.write(gson.toJson(result));
            
        } catch (Exception e) {
            e.printStackTrace();
            out.write("{\"error\": \"" + e.getMessage() + "\"}");
        } finally {
            out.flush();
            out.close();
        }
    }
    
    /**
     * Handle AJAX request to get task detail
     */
    private void handleGetTaskDetail(HttpServletRequest request, HttpServletResponse response, Users currentUser) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            String taskIdParam = request.getParameter("taskId");
            
            if (taskIdParam == null || taskIdParam.trim().isEmpty()) {
                out.write("{\"error\": \"Task ID is required\"}");
                return;
            }
            
            int taskId = Integer.parseInt(taskIdParam);
            
            System.out.println("DEBUG handleGetTaskDetail: Requesting taskId=" + taskId);
            
            // Quick test to see what tasks exist
            try {
                java.sql.Connection conn = dal.DBContext.getConnection();
                java.sql.PreparedStatement testStmt = conn.prepareStatement("SELECT TaskID, Title FROM Tasks LIMIT 5");
                java.sql.ResultSet testRs = testStmt.executeQuery();
                System.out.println("DEBUG: Available tasks in database:");
                while (testRs.next()) {
                    System.out.println("  - TaskID: " + testRs.getInt("TaskID") + ", Title: " + testRs.getString("Title"));
                }
                testRs.close();
                testStmt.close();
                conn.close();
            } catch (Exception e) {
                System.err.println("DEBUG: Error checking available tasks: " + e.getMessage());
            }
            
            // Get task details from database
            Tasks task = taskDAO.getTaskById(taskId);
            
            if (task == null) {
                out.write("{\"error\": \"Task not found\"}");
                return;
            }
            
            // Debug log to check task data
            System.out.println("DEBUG: Task ID " + taskId + " details:");
            System.out.println("  - Title: " + task.getTitle());
            System.out.println("  - AssigneeType: " + task.getAssigneeType());
            System.out.println("  - UserAssignee: " + (task.getUserAssignee() != null ? task.getUserAssignee().getFullName() : "null"));
            System.out.println("  - CreatedBy: " + (task.getCreatedBy() != null ? task.getCreatedBy().getFullName() : "null"));
            
            // Check if current user has permission to view this task
            // (Department leader should be able to view tasks from their department)
            if (!departmentDashboardDAO.isDepartmentLeader(currentUser.getUserID())) {
                out.write("{\"error\": \"Access denied\"}");
                return;
            }
            
            // Convert task to JSON
            Gson gson = new Gson();
            JsonObject taskJson = new JsonObject();
            
            taskJson.addProperty("taskID", task.getTaskID());
            taskJson.addProperty("title", task.getTitle());
            taskJson.addProperty("description", task.getDescription());
            taskJson.addProperty("status", task.getStatus());
            taskJson.addProperty("statusText", getStatusText(task.getStatus()));
            
            // Format dates
            if (task.getStartDate() != null) {
                taskJson.addProperty("startDate", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(task.getStartDate()));
            }
            if (task.getEndDate() != null) {
                taskJson.addProperty("endDate", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(task.getEndDate()));
            }
            if (task.getCreatedAt() != null) {
                taskJson.addProperty("createdAt", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(task.getCreatedAt()));
            }
            
            // Add assignee info
            if (task.getUserAssignee() != null) {
                JsonObject assigneeJson = new JsonObject();
                assigneeJson.addProperty("fullName", task.getUserAssignee().getFullName());
                assigneeJson.addProperty("email", task.getUserAssignee().getEmail());
                assigneeJson.addProperty("avatar", task.getUserAssignee().getAvatar());
                taskJson.add("assignee", assigneeJson);
            }
            
            // Add creator info
            if (task.getCreatedBy() != null) {
                JsonObject creatorJson = new JsonObject();
                creatorJson.addProperty("fullName", task.getCreatedBy().getFullName());
                creatorJson.addProperty("email", task.getCreatedBy().getEmail());
                taskJson.add("creator", creatorJson);
            }
            
            // Add event info if available
            if (task.getEvent() != null) {
                JsonObject eventJson = new JsonObject();
                eventJson.addProperty("eventName", task.getEvent().getEventName());
                eventJson.addProperty("eventID", task.getEvent().getEventID());
                taskJson.add("event", eventJson);
            }
            
            out.write(gson.toJson(taskJson));
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.write("{\"error\": \"Invalid task ID format\"}");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.write("{\"error\": \"" + e.getMessage() + "\"}");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Convert status code to Vietnamese text
     */
    private String getStatusText(String status) {
        if (status == null) return "Không xác định";
        
        switch (status) {
            case "ToDo": return "Chưa bắt đầu";
            case "InProgress": return "Đang thực hiện";
            case "Review": return "Chờ duyệt";
            case "Done": return "Hoàn thành";
            case "Rejected": return "Từ chối";
            default: return status;
        }
    }
    
    /**
     * Debug method to check available roles in department
     */
    private void debugRolesInDepartment(int clubDepartmentId) {
        try {
            // Simple query to see what roles exist in this department
            String debugQuery = """
                SELECT DISTINCT r.RoleName, COUNT(*) as count
                FROM UserClubs uc
                JOIN Roles r ON uc.RoleID = r.RoleID
                WHERE uc.ClubDepartmentID = ? AND uc.IsActive = 1
                GROUP BY r.RoleName
            """;
            
            java.sql.Connection conn = dal.DBContext.getConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement(debugQuery);
            stmt.setInt(1, clubDepartmentId);
            java.sql.ResultSet rs = stmt.executeQuery();
            
            System.out.println("DEBUG: Available roles in clubDepartmentId " + clubDepartmentId + ":");
            while (rs.next()) {
                System.out.println("  - Role: '" + rs.getString("RoleName") + "', Count: " + rs.getInt("count"));
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            System.err.println("Error in debugRolesInDepartment: " + e.getMessage());
        }
    }
}
