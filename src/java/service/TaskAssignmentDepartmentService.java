package service;

import dal.TaskAssignmentDepartmentDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import models.TaskAssignmentDepartment;

/**
 * Service layer for department task management operations
 */
public class TaskAssignmentDepartmentService {
    private final TaskAssignmentDepartmentDAO taskAssignmentDepartmentDAO;
    private final TaskAssignmentMemberService taskMemberService;
    
    public TaskAssignmentDepartmentService() {
        this.taskAssignmentDepartmentDAO = new TaskAssignmentDepartmentDAO();
        this.taskMemberService = new TaskAssignmentMemberService();
    }
    
    /**
     * Lấy tất cả nhiệm vụ của một ban
     * @param clubDepartmentID ID của ban trong CLB
     * @return Danh sách các nhiệm vụ
     */
    public List<TaskAssignmentDepartment> getTasksByDepartment(int clubDepartmentID) {
        return taskAssignmentDepartmentDAO.getTasksByDepartment(clubDepartmentID);
    }
    
    /**
     * Lấy tất cả nhiệm vụ của một sự kiện
     * @param eventID ID của sự kiện
     * @return Danh sách các nhiệm vụ
     */
    public List<TaskAssignmentDepartment> getTasksByEvent(int eventID) {
        return taskAssignmentDepartmentDAO.getTasksByEvent(eventID);
    }
    
    /**
     * Lấy thông tin chi tiết của một nhiệm vụ
     * @param taskID ID của nhiệm vụ
     * @return Thông tin chi tiết của nhiệm vụ
     */
    public TaskAssignmentDepartment getTaskById(int taskID) {
        return taskAssignmentDepartmentDAO.getTaskById(taskID);
    }
    
    /**
     * Tạo một nhiệm vụ mới cho ban
     * @param task Thông tin nhiệm vụ cần tạo
     * @return ID của nhiệm vụ mới tạo, 0 nếu tạo thất bại
     */
    public int createTask(TaskAssignmentDepartment task) {
        return taskAssignmentDepartmentDAO.createTask(task);
    }
    
    /**
     * Cập nhật thông tin của một nhiệm vụ
     * @param task Thông tin nhiệm vụ cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateTask(TaskAssignmentDepartment task) {
        return taskAssignmentDepartmentDAO.updateTask(task);
    }
    
    /**
     * Cập nhật trạng thái của một nhiệm vụ
     * @param taskID ID của nhiệm vụ
     * @param status Trạng thái mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateTaskStatus(int taskID, String status) {
        return taskAssignmentDepartmentDAO.updateTaskStatus(taskID, status);
    }
    
    /**
     * Xóa một nhiệm vụ
     * @param taskID ID của nhiệm vụ cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteTask(int taskID) {
        return taskAssignmentDepartmentDAO.deleteTask(taskID);
    }
    
    /**
     * Đếm số lượng nhiệm vụ của một ban
     * @param departmentID ID của ban
     * @return Số lượng nhiệm vụ
     */
    public int countTasks(int departmentID) {
        List<TaskAssignmentDepartment> tasks = getTasksByDepartment(departmentID);
        return tasks.size();
    }
    
    /**
     * Đếm số lượng nhiệm vụ theo trạng thái của một ban
     * @param departmentID ID của ban
     * @param status Trạng thái cần đếm
     * @return Số lượng nhiệm vụ có trạng thái tương ứng
     */
    public int countTasksByStatus(int departmentID, String status) {
        List<TaskAssignmentDepartment> tasks = getTasksByDepartment(departmentID);
        int count = 0;
        for (TaskAssignmentDepartment task : tasks) {
            if (task.getStatus().equalsIgnoreCase(status)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Lấy danh sách các nhiệm vụ sắp đến hạn của một ban
     * @param departmentID ID của ban
     * @param limit Số lượng nhiệm vụ cần lấy
     * @return Danh sách các nhiệm vụ sắp đến hạn
     */
    public List<TaskAssignmentDepartment> getUpcomingDeadlines(int departmentID, int limit) {
        List<TaskAssignmentDepartment> tasks = getTasksByDepartment(departmentID);
        
        // Sắp xếp nhiệm vụ theo ngày đến hạn (từ gần đến xa)
        tasks.sort((t1, t2) -> {
            if (t1.getDueDate() == null && t2.getDueDate() == null) {
                return 0;
            }
            if (t1.getDueDate() == null) {
                return 1;
            }
            if (t2.getDueDate() == null) {
                return -1;
            }
            return t1.getDueDate().compareTo(t2.getDueDate());
        });
        
        // Lọc các nhiệm vụ chưa hoàn thành
        List<TaskAssignmentDepartment> upcomingTasks = tasks.stream()
                .filter(task -> !"Completed".equalsIgnoreCase(task.getStatus()) 
                              && !"Approved".equalsIgnoreCase(task.getStatus()))
                .limit(limit)
                .toList();
        
        return upcomingTasks;
    }
    
    /**
     * Lấy tất cả nhiệm vụ của một ban với các điều kiện lọc
     * @param clubDepartmentID ID của ban trong CLB
     * @param status Trạng thái cần lọc (null nếu không lọc)
     * @param startDate Ngày bắt đầu cần lọc (null nếu không lọc)
     * @param endDate Ngày kết thúc cần lọc (null nếu không lọc)
     * @param assignee Người được giao nhiệm vụ (null nếu không lọc)
     * @return Danh sách các nhiệm vụ đã lọc
     */    public List<TaskAssignmentDepartment> getTasksByDepartment(int clubDepartmentID, String status, 
                                                              String startDate, String endDate, String assignee) {
        System.out.println("DEBUG TaskAssignmentDepartmentService.getTasksByDepartment: clubDepartmentID=" + clubDepartmentID);
        
        // Lấy tất cả nhiệm vụ của ban
        List<TaskAssignmentDepartment> allTasks = taskAssignmentDepartmentDAO.getTasksByDepartment(clubDepartmentID);
        System.out.println("DEBUG TaskAssignmentDepartmentService.getTasksByDepartment: Found " + (allTasks != null ? allTasks.size() : 0) + " tasks from DAO");
        
        List<TaskAssignmentDepartment> filteredTasks = new ArrayList<>();
        
        // Áp dụng các bộ lọc
        for (TaskAssignmentDepartment task : allTasks) {
            boolean includeTask = true;
            
            // Lọc theo trạng thái
            if (status != null && !status.isEmpty() && !status.equals("all")) {
                if (!task.getStatus().equalsIgnoreCase(status)) {
                    includeTask = false;
                }
            }
            
            // Lọc theo ngày bắt đầu
            if (startDate != null && !startDate.isEmpty()) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date filterStartDate = dateFormat.parse(startDate);
                    if (task.getStartedDate() != null && task.getStartedDate().before(filterStartDate)) {
                        includeTask = false;
                    }
                } catch (ParseException e) {
                    // Bỏ qua nếu định dạng ngày không hợp lệ
                }
            }
            
            // Lọc theo ngày kết thúc
            if (endDate != null && !endDate.isEmpty()) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date filterEndDate = dateFormat.parse(endDate);
                    if (task.getDueDate() != null && task.getDueDate().after(filterEndDate)) {
                        includeTask = false;
                    }
                } catch (ParseException e) {
                    // Bỏ qua nếu định dạng ngày không hợp lệ
                }
            }
            
            // Lọc theo người được giao nhiệm vụ (cần bổ sung logic)
            if (assignee != null && !assignee.isEmpty()) {
                // Giả sử có phương thức kiểm tra người được giao nhiệm vụ
                boolean hasAssignee = taskMemberService.checkTaskHasAssignee(task.getTaskAssignmentDepartmentID(), assignee);
                if (!hasAssignee) {
                    includeTask = false;
                }
            }
            
            if (includeTask) {
                filteredTasks.add(task);
            }
        }
        
        return filteredTasks;
    }
    
    // New methods for dashboard operations with clubDepartmentID
    
    /**
     * Đếm số lượng nhiệm vụ của một ban sử dụng clubDepartmentID
     * @param clubDepartmentID ID của ban trong CLB
     * @return Số lượng nhiệm vụ
     */
    public int countTasksByClubDepartment(int clubDepartmentID) {
        List<TaskAssignmentDepartment> tasks = getTasksByDepartment(clubDepartmentID);
        return tasks.size();
    }
    
    /**
     * Đếm số lượng nhiệm vụ theo trạng thái của một ban sử dụng clubDepartmentID
     * @param clubDepartmentID ID của ban trong CLB
     * @param status Trạng thái cần đếm
     * @return Số lượng nhiệm vụ có trạng thái tương ứng
     */
    public int countTasksByClubDepartmentAndStatus(int clubDepartmentID, String status) {
        List<TaskAssignmentDepartment> tasks = getTasksByDepartment(clubDepartmentID);
        int count = 0;
        for (TaskAssignmentDepartment task : tasks) {
            if (task.getStatus().equalsIgnoreCase(status)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Lấy danh sách các nhiệm vụ sắp đến hạn của một ban sử dụng clubDepartmentID
     * @param clubDepartmentID ID của ban trong CLB
     * @param limit Số lượng nhiệm vụ cần lấy
     * @return Danh sách các nhiệm vụ sắp đến hạn
     */
    public List<TaskAssignmentDepartment> getUpcomingDeadlinesByClubDepartment(int clubDepartmentID, int limit) {
        List<TaskAssignmentDepartment> tasks = getTasksByDepartment(clubDepartmentID);
        
        // Sắp xếp nhiệm vụ theo ngày đến hạn (từ gần đến xa)
        tasks.sort((t1, t2) -> {
            if (t1.getDueDate() == null && t2.getDueDate() == null) {
                return 0;
            }
            if (t1.getDueDate() == null) {
                return 1;
            }
            if (t2.getDueDate() == null) {
                return -1;
            }
            return t1.getDueDate().compareTo(t2.getDueDate());
        });
        
        // Lọc các nhiệm vụ chưa hoàn thành
        List<TaskAssignmentDepartment> upcomingTasks = tasks.stream()
                .filter(task -> !"Completed".equalsIgnoreCase(task.getStatus()) 
                              && !"Approved".equalsIgnoreCase(task.getStatus()))
                .limit(limit)
                .toList();
        
        return upcomingTasks;
    }
    
    /**
     * Method to check database contents for debugging
     */
    public void checkDatabaseContents() {
        taskAssignmentDepartmentDAO.checkAvailableClubDepartmentIDs();
    }
    
    /**
     * Method to create test data for debugging
     */
    public void createTestData() {
        taskAssignmentDepartmentDAO.createTestData();
    }
}
