package controller;

import dal.UserDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.DepartmentMember;
import models.TaskAssignmentDepartment;
import models.TaskAssignmentDepartmentResponser;
import models.TaskAssignmentMember;
import models.Users;
import service.DepartmentMemberService;
import service.TaskAssignmentDepartmentService;
import service.TaskAssignmentMemberService;

/**
 * Controller for department tasks management
 */
public class DepartmentTaskController extends HttpServlet {
    private final TaskAssignmentDepartmentService taskDeptService;
    private final TaskAssignmentMemberService taskMemberService;
    private final DepartmentMemberService departmentMemberService;
      public DepartmentTaskController() {
        this.taskDeptService = new TaskAssignmentDepartmentService();
        this.taskMemberService = new TaskAssignmentMemberService();
        this.departmentMemberService = new DepartmentMemberService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy clubID từ session hoặc parameter
        // Trong thực tế, bạn sẽ cần có cơ chế để lấy thông tin clubID và departmentID của user hiện tại
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
            }
        } catch (NumberFormatException e) {
            // Nếu không có club và department hợp lệ, chuyển người dùng đến trang chọn club
            response.sendRedirect(request.getContextPath() + "/user-clubs");
            return;
        }
        
        if (clubID == 0) {
            response.sendRedirect(request.getContextPath() + "/user-clubs");
            return;
        }
        
        // Lấy clubDepartmentID dựa vào clubID và departmentID
        int clubDepartmentID = getClubDepartmentIDForUser(request, clubID, departmentID);
        session.setAttribute("clubDepartmentID", clubDepartmentID);
        
        switch (action) {
            case "list":
                listTasks(request, response);
                break;
            case "view":
                viewTaskDetail(request, response);
                break;
            case "add":
                showAddForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteTask(request, response);
                break;
            default:
                listTasks(request, response);
                break;
        }
    }
      @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy clubID từ session
        int clubID = 0;
        if (session.getAttribute("clubID") != null) {
            clubID = (Integer) session.getAttribute("clubID");
        } else {
            response.sendRedirect(request.getContextPath() + "/user-clubs");
            return;
        }
        
        switch (action) {
            case "create":
                createTask(request, response);
                break;
            case "update":
                updateTask(request, response);
                break;
            case "updateStatus":
                updateTaskStatus(request, response);
                break;
            case "comment":
                addComment(request, response);
                break;
            case "updateProgress":
                updateProgress(request, response);
                break;
            case "deleteAttachment":
                deleteAttachment(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/department-task?action=list");
                break;
        }
    }
      /**
     * Hiển thị danh sách nhiệm vụ của ban
     */    private void listTasks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer clubID = (Integer) session.getAttribute("clubID");
        Integer clubDepartmentID = (Integer) session.getAttribute("clubDepartmentID");
        
        System.out.println("DEBUG listTasks: clubID = " + clubID + ", clubDepartmentID = " + clubDepartmentID);
        
        if (clubDepartmentID == null) {
            clubDepartmentID = getClubDepartmentIDForUser(request, clubID, 0);
            session.setAttribute("clubDepartmentID", clubDepartmentID);
            System.out.println("DEBUG listTasks: Set new clubDepartmentID = " + clubDepartmentID);
        }
        
        // Lọc nhiệm vụ theo tham số
        String status = request.getParameter("status");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String assignee = request.getParameter("assignee");
        
        System.out.println("DEBUG listTasks: filters - status=" + status + ", startDate=" + startDate + ", endDate=" + endDate + ", assignee=" + assignee);
        
        List<TaskAssignmentDepartment> tasks = taskDeptService.getTasksByDepartment(clubDepartmentID, status, startDate, endDate, assignee);
        
        // Load assigned members for each task
        if (tasks != null) {
            for (TaskAssignmentDepartment task : tasks) {
                List<TaskAssignmentMember> memberTasks = taskMemberService.getTasksByDepartmentTask(task.getTaskAssignmentDepartmentID());
                List<Users> assignedMembers = convertToAssignedMembers(memberTasks);
                task.setAssignedMembers(assignedMembers);
            }
        }
          System.out.println("DEBUG listTasks: Retrieved " + (tasks != null ? tasks.size() : 0) + " tasks");
        if (tasks != null && !tasks.isEmpty()) {
            for (int i = 0; i < Math.min(tasks.size(), 3); i++) {
                TaskAssignmentDepartment task = tasks.get(i);
                System.out.println("DEBUG Task " + i + ": ID=" + task.getTaskAssignmentDepartmentID() + ", Name=" + task.getTaskName() + ", Status=" + task.getStatus());
            }        } else {
            // If no tasks found, check what's available in database
            System.out.println("DEBUG: No tasks found, checking database contents...");
            taskDeptService.checkDatabaseContents();
            
            // Create test data
            System.out.println("DEBUG: Creating test data...");
            taskDeptService.createTestData();
            
            // Try loading tasks again
            tasks = taskDeptService.getTasksByDepartment(clubDepartmentID, status, startDate, endDate, assignee);
            if (tasks != null) {
                for (TaskAssignmentDepartment task : tasks) {
                    List<TaskAssignmentMember> memberTasks = taskMemberService.getTasksByDepartmentTask(task.getTaskAssignmentDepartmentID());
                    List<Users> assignedMembers = convertToAssignedMembers(memberTasks);
                    task.setAssignedMembers(assignedMembers);
                }
            }
        }
        
        // Lấy danh sách thành viên trong ban để hiển thị trong dropdown lọc
        List<DepartmentMember> departmentMembers = departmentMemberService.getAllDepartmentMembers(clubDepartmentID);
        System.out.println("DEBUG listTasks: Retrieved " + (departmentMembers != null ? departmentMembers.size() : 0) + " department members");
        
        request.setAttribute("tasks", tasks);
        request.setAttribute("departmentMembers", departmentMembers);
        request.setAttribute("active", "tasks"); // Đánh dấu menu active
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/view/student/department-leader/tasks.jsp");
        dispatcher.forward(request, response);
    }
    
    /**
     * Hiển thị chi tiết một nhiệm vụ
     */
    private void viewTaskDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer clubID = (Integer) session.getAttribute("clubID");
        
        try {
            int taskID = Integer.parseInt(request.getParameter("id"));
            TaskAssignmentDepartment task = taskDeptService.getTaskById(taskID);
            
            if (task != null) {
                // Lấy danh sách thành viên được giao nhiệm vụ
                List<TaskAssignmentMember> memberTasks = taskMemberService.getTasksByDepartmentTask(taskID);
                List<Users> assignedMembers = convertToAssignedMembers(memberTasks);
                
                // Thiết lập trạng thái của người dùng hiện tại (leader hoặc member)
                Users currentUser = (Users) session.getAttribute("user");
                request.setAttribute("userRole", "leader"); // Mặc định là leader vì đây là trang của trưởng ban
                
                request.setAttribute("task", task);
                request.setAttribute("assignedMembers", assignedMembers); // Pass separately to the view
                request.setAttribute("memberTasks", memberTasks); // Pass the original tasks for reference
                request.setAttribute("active", "tasks");
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("/view/student/department-leader/task-detail.jsp");
                dispatcher.forward(request, response);
            } else {
                request.getSession().setAttribute("errorMessage", "Không tìm thấy nhiệm vụ.");
                response.sendRedirect(request.getContextPath() + "/department-task?action=list");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID nhiệm vụ không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/department-task?action=list");
        }
    }
    
    /**
     * Hiển thị form thêm nhiệm vụ mới
     */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer clubDepartmentID = (Integer) session.getAttribute("clubDepartmentID");
        
        // Lấy danh sách thành viên trong ban để hiển thị trong dropdown
        List<DepartmentMember> departmentMembers = departmentMemberService.getAllDepartmentMembers(clubDepartmentID);
        
        request.setAttribute("departmentMembers", departmentMembers);
        request.setAttribute("active", "tasks");
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/view/student/department-leader/add-task.jsp");
        dispatcher.forward(request, response);
    }
    
    /**
     * Hiển thị form chỉnh sửa nhiệm vụ
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer clubDepartmentID = (Integer) session.getAttribute("clubDepartmentID");
        
        try {
            int taskID = Integer.parseInt(request.getParameter("id"));
            TaskAssignmentDepartment task = taskDeptService.getTaskById(taskID);
            
            if (task != null) {
                // Lấy danh sách thành viên được giao nhiệm vụ
                List<TaskAssignmentMember> memberTasks = taskMemberService.getTasksByDepartmentTask(taskID);
                List<Users> assignedMembers = convertToAssignedMembers(memberTasks);
                
                // Lấy danh sách tất cả thành viên trong ban
                List<DepartmentMember> departmentMembers = departmentMemberService.getAllDepartmentMembers(clubDepartmentID);
                
                request.setAttribute("task", task);
                request.setAttribute("assignedMembers", assignedMembers);
                request.setAttribute("memberTasks", memberTasks);
                request.setAttribute("departmentMembers", departmentMembers);
                request.setAttribute("active", "tasks");
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("/view/student/department-leader/edit-task.jsp");
                dispatcher.forward(request, response);
            } else {
                request.getSession().setAttribute("errorMessage", "Không tìm thấy nhiệm vụ.");
                response.sendRedirect(request.getContextPath() + "/department-task?action=list");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID nhiệm vụ không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/department-task?action=list");
        }
    }
      /**
     * Tạo nhiệm vụ mới
     */
    private void createTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            Integer clubDepartmentID = (Integer) session.getAttribute("clubDepartmentID");
            
            if (clubDepartmentID == null) {
                Integer clubID = (Integer) session.getAttribute("clubID");
                clubDepartmentID = getClubDepartmentIDForUser(request, clubID, 0);
                session.setAttribute("clubDepartmentID", clubDepartmentID);
            }
            
            Users currentUser = (Users) session.getAttribute("user");
            
            // Lấy dữ liệu từ form
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String status = request.getParameter("status");
            
            // Parse dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(request.getParameter("startDate"));
            Date deadline = dateFormat.parse(request.getParameter("deadline"));
            
            // Create task object
            TaskAssignmentDepartment task = new TaskAssignmentDepartment();
            task.setEventID(1); // Cần lấy EventID từ form hoặc session
            task.setTerm("Trước sự kiện"); // Cần lấy từ form
            task.setDescription(description);
            task.setTaskName(title); // Sử dụng title làm taskName
            task.setStatus(status != null ? status : "ToDo");
            task.setStartedDate(startDate);
            task.setDueDate(deadline);
            task.setTermStart(startDate); // Giả định rằng termStart = startedDate
            task.setTermEnd(deadline);    // Giả định rằng termEnd = dueDate
            
            // Thêm department responder vào task
            TaskAssignmentDepartmentResponser responder = new TaskAssignmentDepartmentResponser();
            responder.setResponderID(clubDepartmentID); // ClubDepartmentID
            task.addDepartmentResponser(responder);
            
            // Save to database
            int taskID = taskDeptService.createTask(task);            // Xử lý các thành viên được giao nhiệm vụ
            String[] assignedMembers = request.getParameterValues("memberID");
            System.out.println("DEBUG: assignedMembers = " + java.util.Arrays.toString(assignedMembers));
            
            if (assignedMembers != null && assignedMembers.length > 0) {
                for (String memberID : assignedMembers) {
                    System.out.println("DEBUG: Processing memberID = " + memberID);
                    TaskAssignmentMember memberTask = new TaskAssignmentMember();
                    memberTask.setDepartmentTaskID(taskID);
                    // memberID is actually userClubID from the form
                    memberTask.setAssigneeID(Integer.parseInt(memberID));
                    memberTask.setAssignerID(Integer.parseInt(currentUser.getUserID()));
                    memberTask.setStatus("pending"); // Mặc định là pending
                    memberTask.setDueDate(new java.sql.Timestamp(deadline.getTime()));
                      // Save member task
                    int memberTaskResult = taskMemberService.createTask(memberTask);
                    System.out.println("DEBUG: Member task created with result = " + memberTaskResult + " for memberID = " + memberID);
                }
            } else {
                System.out.println("DEBUG: No members assigned to this task");
            }
            
            if (taskID > 0) {
                session.setAttribute("successMessage", "Nhiệm vụ đã được tạo thành công!");
                response.sendRedirect(request.getContextPath() + "/department-task?action=view&id=" + taskID);
            } else {
                session.setAttribute("errorMessage", "Không thể tạo nhiệm vụ. Vui lòng thử lại!");
                response.sendRedirect(request.getContextPath() + "/department-task?action=add");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Dữ liệu không hợp lệ. Vui lòng thử lại! " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/department-task?action=add");
        }
    }
      /**
     * Cập nhật thông tin nhiệm vụ
     */
    private void updateTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        Integer clubDepartmentID = (Integer) session.getAttribute("clubDepartmentID");
        
        try {
            int taskID = Integer.parseInt(request.getParameter("taskID"));
            
            // Lấy nhiệm vụ hiện tại
            TaskAssignmentDepartment currentTask = taskDeptService.getTaskById(taskID);
            
            if (currentTask != null) {
                // Cập nhật thông tin từ form
                String taskName = request.getParameter("title");
                String description = request.getParameter("description");
                String status = request.getParameter("status");
                
                // Parse dates
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = dateFormat.parse(request.getParameter("startDate"));
                Date deadline = dateFormat.parse(request.getParameter("deadline"));
                
                // Cập nhật task object
                currentTask.setTaskName(taskName);
                currentTask.setDescription(description);
                currentTask.setStatus(status);
                currentTask.setStartedDate(startDate);
                currentTask.setDueDate(deadline);
                currentTask.setTermStart(startDate); // Assume termStart = startedDate
                currentTask.setTermEnd(deadline);    // Assume termEnd = dueDate
                
                // Ensure the ClubDepartmentID is assigned through a responder
                boolean hasClubDepartmentResponder = false;
                for (TaskAssignmentDepartmentResponser responder : currentTask.getDepartmentResponsers()) {
                    if (responder.getResponderID() == clubDepartmentID) {
                        hasClubDepartmentResponder = true;
                        break;
                    }
                }
                
                // Add the club department if it's not already assigned
                if (!hasClubDepartmentResponder && clubDepartmentID != null) {
                    TaskAssignmentDepartmentResponser responder = new TaskAssignmentDepartmentResponser();
                    responder.setResponderID(clubDepartmentID);
                    responder.setTaskAssignmentID(taskID);
                    currentTask.addDepartmentResponser(responder);
                }
                
                // Save to database
                boolean success = taskDeptService.updateTask(currentTask);
                  // Xử lý các thành viên được giao nhiệm vụ
                String[] assignedMembers = request.getParameterValues("memberID");
                
                // Xóa tất cả nhiệm vụ thành viên cũ và tạo lại
                taskMemberService.deleteAllTaskMembersByDepartmentTaskID(taskID);
                
                if (assignedMembers != null && assignedMembers.length > 0) {
                    for (String memberID : assignedMembers) {
                        TaskAssignmentMember memberTask = new TaskAssignmentMember();
                        memberTask.setDepartmentTaskID(taskID);
                        memberTask.setAssigneeID(Integer.parseInt(memberID));
                        memberTask.setAssignerID(Integer.parseInt(currentUser.getUserID()));
                        memberTask.setStatus("pending"); // Mặc định là pending
                        memberTask.setDueDate(new java.sql.Timestamp(deadline.getTime()));
                        
                        // Save member task
                        taskMemberService.createTask(memberTask);
                    }
                }
                
                if (success) {
                    session.setAttribute("successMessage", "Nhiệm vụ đã được cập nhật thành công!");
                } else {
                    session.setAttribute("errorMessage", "Không thể cập nhật nhiệm vụ. Vui lòng thử lại!");
                }
            } else {
                session.setAttribute("errorMessage", "Không tìm thấy nhiệm vụ.");
            }
            
            response.sendRedirect(request.getContextPath() + "/department-task?action=view&id=" + taskID);
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ. Vui lòng thử lại!" + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/department-task?action=list");
        }
    }
    
    /**
     * Cập nhật trạng thái nhiệm vụ
     */
    private void updateTaskStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        try {
            int taskID = Integer.parseInt(request.getParameter("taskID"));
            String status = request.getParameter("status");
            
            boolean success = taskDeptService.updateTaskStatus(taskID, status);
            
            if (success) {
                session.setAttribute("successMessage", "Trạng thái nhiệm vụ đã được cập nhật thành công!");
            } else {
                session.setAttribute("errorMessage", "Không thể cập nhật trạng thái nhiệm vụ. Vui lòng thử lại!");
            }
            
            response.sendRedirect(request.getContextPath() + "/department-task?action=view&id=" + taskID);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "ID nhiệm vụ không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/department-task?action=list");
        }
    }
    
    /**
     * Xóa nhiệm vụ
     */
    private void deleteTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        try {
            int taskID = Integer.parseInt(request.getParameter("id"));
            
            // Xóa tất cả nhiệm vụ thành viên trước
            taskMemberService.deleteAllTaskMembersByDepartmentTaskID(taskID);
            
            // Sau đó xóa nhiệm vụ ban
            boolean success = taskDeptService.deleteTask(taskID);
            
            if (success) {
                session.setAttribute("successMessage", "Nhiệm vụ đã được xóa thành công!");
            } else {
                session.setAttribute("errorMessage", "Không thể xóa nhiệm vụ. Vui lòng thử lại!");
            }
            
            response.sendRedirect(request.getContextPath() + "/department-task?action=list");
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "ID nhiệm vụ không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/department-task?action=list");
        }
    }
    
    /**
     * Thêm bình luận vào nhiệm vụ
     */
    private void addComment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        try {
            int taskID = Integer.parseInt(request.getParameter("taskID"));
            String comment = request.getParameter("comment");
            
            // Lưu bình luận vào database
            // Trong phiên bản thực tế, bạn sẽ cần một bảng comments trong database
            
            session.setAttribute("successMessage", "Đã thêm bình luận thành công!");
            response.sendRedirect(request.getContextPath() + "/department-task?action=view&id=" + taskID);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/department-task?action=list");
        }
    }
    
    /**
     * Cập nhật tiến độ của nhiệm vụ
     */
    private void updateProgress(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        try {
            int taskID = Integer.parseInt(request.getParameter("taskID"));
            int progress = Integer.parseInt(request.getParameter("progress"));
            
            // Lấy nhiệm vụ hiện tại
            TaskAssignmentDepartment task = taskDeptService.getTaskById(taskID);
            
            if (task != null) {
                // Thiết lập trạng thái dựa trên tiến độ
                if (progress == 100) {
                    task.setStatus("Completed");
                } else if (progress > 0) {
                    task.setStatus("In Progress");
                } else {
                    task.setStatus("ToDo");
                }
                
                boolean success = taskDeptService.updateTask(task);
                
                if (success) {
                    session.setAttribute("successMessage", "Tiến độ nhiệm vụ đã được cập nhật thành công!");
                } else {
                    session.setAttribute("errorMessage", "Không thể cập nhật tiến độ nhiệm vụ. Vui lòng thử lại!");
                }
            } else {
                session.setAttribute("errorMessage", "Không tìm thấy nhiệm vụ.");
            }
            
            response.sendRedirect(request.getContextPath() + "/department-task?action=view&id=" + taskID);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ. Vui lòng thử lại!");
            response.sendRedirect(request.getContextPath() + "/department-task?action=list");
        }
    }
    
    /**
     * Xóa tệp đính kèm
     */
    private void deleteAttachment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        try {
            int attachmentID = Integer.parseInt(request.getParameter("id"));
            int taskID = Integer.parseInt(request.getParameter("taskID"));
            
            // Xóa tệp đính kèm từ database và hệ thống tệp
            // Trong phiên bản thực tế, bạn sẽ cần một bảng attachments trong database
            
            session.setAttribute("successMessage", "Đã xóa tệp đính kèm thành công!");
            response.sendRedirect(request.getContextPath() + "/department-task?action=edit&id=" + taskID);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/department-task?action=list");
        }
    }
    
    /**
     * Tạo nhiệm vụ cho thành viên
     */
    private void createMemberTask(HttpServletRequest request, HttpServletResponse response, int clubID) throws ServletException, IOException {
        try {
            int departmentTaskID = Integer.parseInt(request.getParameter("departmentTaskID"));
            String taskName = request.getParameter("taskName");
            String description = request.getParameter("description");
            String assigneeID = request.getParameter("assigneeID");
            int priority = Integer.parseInt(request.getParameter("priority"));
            
            // Lấy thông tin người tạo nhiệm vụ (người đang đăng nhập)
            HttpSession session = request.getSession();
            Users currentUser = (Users) session.getAttribute("user");
            String assignerID = currentUser.getUserID();
            
            // Parse date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            java.sql.Timestamp dueDate = new java.sql.Timestamp(dateFormat.parse(request.getParameter("dueDate")).getTime());
            
            // Create task object
            TaskAssignmentMember task = new TaskAssignmentMember();
            task.setDepartmentTaskID(departmentTaskID);
            task.setTaskName(taskName);
            task.setDescription(description);
            task.setAssignerID(Integer.parseInt(assignerID));
            task.setAssigneeID(Integer.parseInt(assigneeID));
            task.setDueDate(dueDate);
            task.setPriority(priority);
            task.setStatus("ToDo");
            
            // Save to database
            int taskID = taskMemberService.createTask(task);
            
            if (taskID > 0) {
                request.getSession().setAttribute("successMessage", "Nhiệm vụ thành viên đã được tạo thành công!");
            } else {
                request.getSession().setAttribute("errorMessage", "Không thể tạo nhiệm vụ thành viên. Vui lòng thử lại!");
            }
            
            response.sendRedirect(request.getContextPath() + "/department-tasks?action=detail&id=" + departmentTaskID + "&clubID=" + clubID);
        } catch (NumberFormatException | ParseException e) {
            request.getSession().setAttribute("errorMessage", "Dữ liệu không hợp lệ. Vui lòng thử lại!");
            response.sendRedirect(request.getContextPath() + "/department-tasks?clubID=" + clubID);
        }
    }
    
    /**
     * Cập nhật nhiệm vụ thành viên
     */
    private void updateMemberTask(HttpServletRequest request, HttpServletResponse response, int clubID) throws ServletException, IOException {
        try {
            int taskID = Integer.parseInt(request.getParameter("taskID"));
            int departmentTaskID = Integer.parseInt(request.getParameter("departmentTaskID"));
            
            TaskAssignmentMember currentTask = taskMemberService.getTaskById(taskID);
            
            if (currentTask != null) {
                String taskName = request.getParameter("taskName");
                String description = request.getParameter("description");
                String assigneeID = request.getParameter("assigneeID");
                int priority = Integer.parseInt(request.getParameter("priority"));
                String status = request.getParameter("status");
                
                // Parse date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                java.sql.Timestamp dueDate = new java.sql.Timestamp(dateFormat.parse(request.getParameter("dueDate")).getTime());
                
                // Update task object
                currentTask.setTaskName(taskName);
                currentTask.setDescription(description);
                currentTask.setAssigneeID(Integer.parseInt(assigneeID));
                currentTask.setDueDate(dueDate);
                currentTask.setPriority(priority);
                currentTask.setStatus(status);
                
                // Save to database
                boolean success = taskMemberService.updateTask(currentTask);
                
                if (success) {
                    request.getSession().setAttribute("successMessage", "Nhiệm vụ thành viên đã được cập nhật thành công!");
                } else {
                    request.getSession().setAttribute("errorMessage", "Không thể cập nhật nhiệm vụ thành viên. Vui lòng thử lại!");
                }
            } else {
                request.getSession().setAttribute("errorMessage", "Không tìm thấy nhiệm vụ thành viên.");
            }
            
            response.sendRedirect(request.getContextPath() + "/department-tasks?action=detail&id=" + departmentTaskID + "&clubID=" + clubID);
        } catch (NumberFormatException | ParseException e) {
            request.getSession().setAttribute("errorMessage", "Dữ liệu không hợp lệ. Vui lòng thử lại!");
            response.sendRedirect(request.getContextPath() + "/department-tasks?clubID=" + clubID);
        }
    }
      /**
     * Phương thức helper để lấy clubDepartmentID cho người dùng hiện tại
     */    private int getClubDepartmentIDForUser(HttpServletRequest request, int clubID, int departmentID) {
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        System.out.println("DEBUG getClubDepartmentIDForUser: clubID=" + clubID + ", departmentID=" + departmentID + ", currentUser=" + (currentUser != null ? currentUser.getUserID() : "null"));
        
        // Trong thực tế, bạn sẽ cần truy vấn database để lấy clubDepartmentID 
        // dựa trên userID, clubID và departmentID (nếu có)
        
        // Ưu tiên lấy từ tham số request
        String clubDepartmentIDStr = request.getParameter("clubDepartmentID");
        if (clubDepartmentIDStr != null && !clubDepartmentIDStr.isEmpty()) {
            int result = Integer.parseInt(clubDepartmentIDStr);
            System.out.println("DEBUG getClubDepartmentIDForUser: Using clubDepartmentID from request = " + result);
            return result;
        }
        
        // Hoặc từ session
        Object clubDeptIdObj = session.getAttribute("clubDepartmentID");
        if (clubDeptIdObj != null) {
            int result = (Integer) clubDeptIdObj;
            System.out.println("DEBUG getClubDepartmentIDForUser: Using clubDepartmentID from session = " + result);
            return result;
        }
        
        // Trong trường hợp thực tế, bạn sẽ thêm truy vấn database ở đây
        // để lấy clubDepartmentID dựa trên userID và clubID
        
        // Giả sử giá trị mặc định cho demo
        System.out.println("DEBUG getClubDepartmentIDForUser: Using default clubDepartmentID = 1");
        return 1; // Thay thế bằng logic thực tế
    }
    
    /**
     * Phương thức chuyển đổi TaskAssignmentMember sang đối tượng thành viên được giao nhiệm vụ
     */    /**
     * Phương thức chuyển đổi TaskAssignmentMember thành Users với đầy đủ thông tin
     * 
     * @param memberTasks Danh sách nhiệm vụ của thành viên
     * @return Danh sách Users đã được lấy đầy đủ thông tin từ database
     */
    private List<Users> convertToAssignedMembers(List<TaskAssignmentMember> memberTasks) {
        List<Users> assignedMembers = new ArrayList<>();
        
        for (TaskAssignmentMember task : memberTasks) {
            // Lấy thông tin người dùng đầy đủ từ database dựa vào assigneeID
            String assigneeID = String.valueOf(task.getAssigneeID());
            Users user = UserDAO.getUserById(assigneeID);
            
            if (user != null) {
                // Đã lấy được thông tin đầy đủ của người dùng từ database
                // Thêm vào danh sách
                assignedMembers.add(user);
                
                // Lưu ý: vì User.status là boolean, nên không thể lưu trạng thái task kiểu String như "ToDo", "Processing"
                // Khi hiển thị trong JSP, cần sử dụng trạng thái từ danh sách memberTasks gốc
                // (đã được truyền lên JSP như một thuộc tính riêng biệt)
            } else {
                // Fallback nếu không tìm thấy user trong database
                Users fallbackUser = new Users();
                fallbackUser.setUserID(assigneeID);
                fallbackUser.setFullName("Unknown User (" + assigneeID + ")");
                fallbackUser.setAvatar("img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg"); // Avatar mặc định
                fallbackUser.setStatus(false);
                assignedMembers.add(fallbackUser);
            }
        }
        
        return assignedMembers;
    }
}
