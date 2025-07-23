package controller;

import dal.EventsDAO;
import dal.TaskDAO;
import dal.UserClubDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import models.Events;
import models.Tasks;
import models.Users;
import models.UserClub;

@WebServlet(name = "CreateTaskServlet", urlPatterns = {"/create-task"})
public class CreateTaskServlet extends HttpServlet {

    private TaskDAO taskDAO = new TaskDAO();
    private UserClubDAO userClubDAO = new UserClubDAO();
    private EventsDAO eventsDAO = new EventsDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect("login");
            return;
        }
        
        // Kiểm tra quyền trưởng ban
        UserClub userClub = userClubDAO.getUserClubByUserIdAndRole(currentUser.getUserID(), "Department Leader");
        if (userClub == null) {
            response.sendRedirect("home?error=unauthorized");
            return;
        }
        
        int clubDepartmentID = userClub.getClubDepartmentID();
        
        // Lấy danh sách thành viên trong ban
        List<Users> departmentMembers = userClubDAO.getUsersByDepartmentAndRole(clubDepartmentID, "Member");
        request.setAttribute("departmentMembers", departmentMembers);
        
        // Lấy danh sách sự kiện của CLB
        int clubID = userClub.getClubID();
        List<Events> clubEvents = eventsDAO.getEventsByClubId(clubID);
        request.setAttribute("clubEvents", clubEvents);
        
        // Chuyển đến trang tạo nhiệm vụ
        request.getRequestDispatcher("view/createTask.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect("login");
            return;
        }
        
        try {
            // Lấy thông tin từ form
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String assignedToStr = request.getParameter("assignedTo");
            String eventIdStr = request.getParameter("eventId");
            
            // Validation
            if (title == null || title.trim().isEmpty() ||
                description == null || description.trim().isEmpty() ||
                startDateStr == null || endDateStr == null ||
                assignedToStr == null || eventIdStr == null) {
                
                request.setAttribute("error", "Vui lòng điền đầy đủ thông tin!");
                doGet(request, response);
                return;
            }
            
            // Parse dates
            Date startDate = Date.valueOf(startDateStr);
            Date endDate = Date.valueOf(endDateStr);
            
            // Kiểm tra ngày hợp lệ
            if (endDate.before(startDate)) {
                request.setAttribute("error", "Ngày kết thúc phải sau ngày bắt đầu!");
                doGet(request, response);
                return;
            }
            
            int eventId = Integer.parseInt(eventIdStr);
            
            // Tạo task mới
            Tasks newTask = new Tasks();
            newTask.setTitle(title);
            newTask.setDescription(description);
            newTask.setStartDate(startDate);
            newTask.setEndDate(endDate);
            newTask.setAssigneeType("User");
            newTask.setStatus("ToDo");
            newTask.setCreatedAt(new java.util.Date());
            
            // Set User Assignee
            Users assignee = new Users();
            assignee.setUserID(assignedToStr);
            newTask.setUserAssignee(assignee);
            
            // Set Creator
            Users creator = new Users();
            creator.setUserID(currentUser.getUserID());
            newTask.setCreatedBy(creator);
            
            // Set Event
            Events event = new Events();
            event.setEventID(eventId);
            newTask.setEvent(event);
            
            // Lưu task
            boolean success = taskDAO.createTask(newTask);
            
            if (success) {
                response.sendRedirect("department-tasks?success=Tạo nhiệm vụ thành công!");
            } else {
                request.setAttribute("error", "Có lỗi xảy ra khi tạo nhiệm vụ!");
                doGet(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Dữ liệu không hợp lệ!");
            doGet(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Định dạng ngày không hợp lệ!");
            doGet(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            doGet(request, response);
        }
    }
}
