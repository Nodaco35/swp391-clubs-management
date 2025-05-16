
package models;

public class TaskAssignment {
    private int taskAssignmentID;
    private int eventID;
    private int departmentID;
    private String description;

    public TaskAssignment() {
    }

    public TaskAssignment(int taskAssignmentID, int eventID, int departmentID, String description) {
        this.taskAssignmentID = taskAssignmentID;
        this.eventID = eventID;
        this.departmentID = departmentID;
        this.description = description;
    }

    public int getTaskAssignmentID() {
        return taskAssignmentID;
    }

    public void setTaskAssignmentID(int taskAssignmentID) {
        this.taskAssignmentID = taskAssignmentID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TaskAssignment{" + "taskAssignmentID=" + taskAssignmentID + ", eventID=" + eventID + ", departmentID=" + departmentID + ", description=" + description + '}';
    }
    
}
