
package models;

import java.util.Date;

public class TaskAssignment {
    private int taskAssignmentID;
    private int eventID;
    private int departmentID;
    private String description;
    private String taskName;
    private Date dueDate;
    private String status; // ENUM('PENDING', 'COMPLETED')

    public TaskAssignment() {}

    public TaskAssignment(int taskAssignmentID, int eventID, int departmentID, String description, String taskName, Date dueDate, String status) {
        this.taskAssignmentID = taskAssignmentID;
        this.eventID = eventID;
        this.departmentID = departmentID;
        this.description = description;
        this.taskName = taskName;
        this.dueDate = dueDate;
        this.status = status;
    }

    public int getTaskAssignmentID() { return taskAssignmentID; }
    public void setTaskAssignmentID(int taskAssignmentID) { this.taskAssignmentID = taskAssignmentID; }

    public int getEventID() { return eventID; }
    public void setEventID(int eventID) { this.eventID = eventID; }

    public int getDepartmentID() { return departmentID; }
    public void setDepartmentID(int departmentID) { this.departmentID = departmentID; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
