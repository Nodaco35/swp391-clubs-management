package models;

import java.sql.Timestamp;

public class TaskAssignmentMember {
    private int taskMemberID;
    private int departmentTaskID; // References TaskAssignmentDepartment.taskAssignmentID
    private String taskName;
    private String description;
    private int assignerID; // User who assigns the task
    private int assigneeID; // User to whom the task is assigned
    private Timestamp dueDate;
    private int priority;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // View properties (not stored in database but retrieved via joins)
    private String assignerName;
    private String assigneeName;
    private String departmentName;
    private String eventName;

    public TaskAssignmentMember() {
    }

    public TaskAssignmentMember(int taskMemberID, int departmentTaskID, String taskName, String description, 
                              int assignerID, int assigneeID, Timestamp dueDate, int priority, 
                              String status, Timestamp createdAt, Timestamp updatedAt) {
        this.taskMemberID = taskMemberID;
        this.departmentTaskID = departmentTaskID;
        this.taskName = taskName;
        this.description = description;
        this.assignerID = assignerID;
        this.assigneeID = assigneeID;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }    public int getTaskMemberID() {
        return taskMemberID;
    }

    public void setTaskMemberID(int taskMemberID) {
        this.taskMemberID = taskMemberID;
    }

    public int getDepartmentTaskID() {
        return departmentTaskID;
    }

    public void setDepartmentTaskID(int departmentTaskID) {
        this.departmentTaskID = departmentTaskID;
    }    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAssignerID() {
        return assignerID;
    }

    public void setAssignerID(int assignerID) {
        this.assignerID = assignerID;
    }

    public int getAssigneeID() {
        return assigneeID;
    }

    public void setAssigneeID(int assigneeID) {
        this.assigneeID = assigneeID;
    }

    public Timestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getAssignerName() {
        return assignerName;
    }

    public void setAssignerName(String assignerName) {
        this.assignerName = assignerName;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public String toString() {
        return "TaskAssignmentMember{" +
                "taskMemberID=" + taskMemberID +
                ", departmentTaskID=" + departmentTaskID +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", assignerID=" + assignerID +
                ", assigneeID=" + assigneeID +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
