package service;

import dal.TaskAssignmentMemberDAO;
import java.util.List;
import models.TaskAssignmentMember;

/**
 * Service layer for member task management operations
 */
public class TaskAssignmentMemberService {
    private final TaskAssignmentMemberDAO taskAssignmentMemberDAO;
    
    public TaskAssignmentMemberService() {
        this.taskAssignmentMemberDAO = new TaskAssignmentMemberDAO();
    }
    
    /**
     * Lấy tất cả nhiệm vụ của thành viên trong một nhiệm vụ của ban
     * @param departmentTaskID ID của nhiệm vụ ban
     * @return Danh sách các nhiệm vụ của thành viên
     */
    public List<TaskAssignmentMember> getTasksByDepartmentTask(int departmentTaskID) {
        return taskAssignmentMemberDAO.getTasksByDepartmentTask(departmentTaskID);
    }
    
    /**
     * Lấy tất cả nhiệm vụ của một thành viên
     * @param assigneeID ID của thành viên được giao nhiệm vụ
     * @return Danh sách các nhiệm vụ
     */
    public List<TaskAssignmentMember> getTasksByAssignee(String assigneeID) {
        return taskAssignmentMemberDAO.getTasksByAssignee(assigneeID);
    }
    
    /**
     * Lấy thông tin chi tiết của một nhiệm vụ thành viên
     * @param taskID ID của nhiệm vụ
     * @return Thông tin chi tiết của nhiệm vụ
     */
    public TaskAssignmentMember getTaskById(int taskID) {
        return taskAssignmentMemberDAO.getTaskById(taskID);
    }
    
    /**
     * Tạo một nhiệm vụ mới cho thành viên
     * @param task Thông tin nhiệm vụ cần tạo
     * @return ID của nhiệm vụ mới tạo, 0 nếu tạo thất bại
     */
    public int createTask(TaskAssignmentMember task) {
        return taskAssignmentMemberDAO.createTask(task);
    }
    
    /**
     * Cập nhật thông tin của một nhiệm vụ thành viên
     * @param task Thông tin nhiệm vụ cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateTask(TaskAssignmentMember task) {
        return taskAssignmentMemberDAO.updateTask(task);
    }
    
    /**
     * Cập nhật trạng thái của một nhiệm vụ thành viên
     * @param taskID ID của nhiệm vụ
     * @param status Trạng thái mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateTaskStatus(int taskID, String status) {
        return taskAssignmentMemberDAO.updateTaskStatus(taskID, status);
    }
    
    /**
     * Xóa một nhiệm vụ thành viên
     * @param taskID ID của nhiệm vụ cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteTask(int taskID) {
        return taskAssignmentMemberDAO.deleteTask(taskID);
    }
    
    /**
     * Xóa tất cả nhiệm vụ thành viên thuộc về một nhiệm vụ ban
     * @param departmentTaskID ID của nhiệm vụ ban
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteAllTaskMembersByDepartmentTaskID(int departmentTaskID) {
        return taskAssignmentMemberDAO.deleteAllTaskMembersByDepartmentTaskID(departmentTaskID);
    }
    
    /**
     * Kiểm tra xem một nhiệm vụ có được gán cho một người dùng cụ thể không
     * @param taskAssignmentDepartmentID ID của nhiệm vụ cấp ban
     * @param userID ID của người dùng cần kiểm tra
     * @return true nếu người dùng được gán cho nhiệm vụ này, false nếu không
     */
    public boolean checkTaskHasAssignee(int taskAssignmentDepartmentID, String userID) {
        // Lấy các nhiệm vụ thành viên thuộc nhiệm vụ ban này
        List<TaskAssignmentMember> memberTasks = getTasksByDepartmentTask(taskAssignmentDepartmentID);
        
        // Kiểm tra xem có nhiệm vụ nào được gán cho userID không
        for (TaskAssignmentMember memberTask : memberTasks) {
            // Kiểm tra từ danh sách người thực hiện (responders)
            boolean isAssignee = taskAssignmentMemberDAO.isUserTaskResponser(memberTask.getTaskMemberID(), userID);
            if (isAssignee) {
                return true;
            }
            
            // Hoặc kiểm tra trực tiếp nếu assigneeID là userID (tùy cấu trúc dữ liệu)
            if (String.valueOf(memberTask.getAssigneeID()).equals(userID)) {
                return true;
            }
        }
        
        return false;
    }
}
