package models;

public class TaskAssignees {
    private int taskAssigneeID;
    private Tasks task; // foreign key -> Tasks
    private String assigneeType; // 'User' hoặc 'Department'
    private Users user;          // nếu AssigneeType = 'User'
    private Department department; // nếu AssigneeType = 'Department'

    public TaskAssignees() {
    }

    public TaskAssignees(int taskAssigneeID, Users user, Department department, String assigneeType, Tasks task) {
        this.taskAssigneeID = taskAssigneeID;
        this.user = user;
        this.department = department;
        this.assigneeType = assigneeType;
        this.task = task;
    }

    public int getTaskAssigneeID() {
        return taskAssigneeID;
    }

    public void setTaskAssigneeID(int taskAssigneeID) {
        this.taskAssigneeID = taskAssigneeID;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getAssigneeType() {
        return assigneeType;
    }

    public void setAssigneeType(String assigneeType) {
        this.assigneeType = assigneeType;
    }

    public Tasks getTask() {
        return task;
    }

    public void setTask(Tasks task) {
        this.task = task;
    }
}
