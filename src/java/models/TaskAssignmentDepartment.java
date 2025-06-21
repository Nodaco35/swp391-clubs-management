package models;

import java.util.ArrayList;
import java.util.List;

public class TaskAssignmentDepartment {
    private int taskAssignmentDepartmentID;
    private int eventID;
    // Removed clubDepartmentID as it's not in the database schema
    private String term;  // ENUM('Trước sự kiện', 'Trong sự kiện', 'Sau sự kiện')
    private java.util.Date termStart;
    private java.util.Date termEnd;
    private String description;
    private String taskName;
    private String status;  // ENUM('ToDo','Processing','Review','Approved')
    private java.util.Date startedDate;
    private java.util.Date dueDate;
    
    // View properties (not stored in database but retrieved via joins)
    private String eventName;
    private String departmentName;
    private String clubName;
    private int clubID;
    
    // List of department responders for this task (for many-to-many relationship)
    private List<TaskAssignmentDepartmentResponser> departmentResponsers = new ArrayList<>();
      // List of assigned members for this task (for displaying in JSP)
    private List<Users> assignedMembers = new ArrayList<>();
    
    public TaskAssignmentDepartment() {
    }

    public TaskAssignmentDepartment(int taskAssignmentDepartmentID, int eventID, String term, java.util.Date termStart, 
                                   java.util.Date termEnd, String description, String taskName, String status,
                                   java.util.Date startedDate, java.util.Date dueDate) {
        this.taskAssignmentDepartmentID = taskAssignmentDepartmentID;
        this.eventID = eventID;
        this.term = term;
        this.termStart = termStart;
        this.termEnd = termEnd;
        this.description = description;
        this.taskName = taskName;
        this.status = status;
        this.startedDate = startedDate;
        this.dueDate = dueDate;
    }
    
    // Constructor compatibility method for existing code - will set clubDepartmentID through departmentResponsers
    public TaskAssignmentDepartment(int taskAssignmentDepartmentID, int eventID, int clubDepartmentID, String term, java.util.Date termStart, 
                                   java.util.Date termEnd, String description, String taskName, String status,
                                   java.util.Date startedDate, java.util.Date dueDate) {
        this(taskAssignmentDepartmentID, eventID, term, termStart, termEnd, description, taskName, status, startedDate, dueDate);
        // Create a responder for backward compatibility
        if (clubDepartmentID > 0) {
            TaskAssignmentDepartmentResponser responder = new TaskAssignmentDepartmentResponser();
            responder.setTaskAssignmentID(taskAssignmentDepartmentID);
            responder.setResponderID(clubDepartmentID); // Assuming responderID corresponds to clubDepartmentID
            this.departmentResponsers.add(responder);
        }
    }
    
    public int getTaskAssignmentDepartmentID() {
        return taskAssignmentDepartmentID;
    }

    public void setTaskAssignmentDepartmentID(int taskAssignmentDepartmentID) {
        this.taskAssignmentDepartmentID = taskAssignmentDepartmentID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }
      // For backward compatibility - returns the first department ID if any exists
    public int getClubDepartmentID() {
        if (!departmentResponsers.isEmpty()) {
            return departmentResponsers.get(0).getResponderID(); // Assuming ResponderID is the ClubDepartmentID
        }
        return 0; // No department assigned
    }

    // For backward compatibility - adds a department to the list
    public void setClubDepartmentID(int clubDepartmentID) {
        // Clear existing entries for single assignment mode
        departmentResponsers.clear();
        
        if (clubDepartmentID > 0) {
            TaskAssignmentDepartmentResponser responder = new TaskAssignmentDepartmentResponser();
            responder.setTaskAssignmentID(this.taskAssignmentDepartmentID);
            responder.setResponderID(clubDepartmentID);
            departmentResponsers.add(responder);
        }
    }
    
    // New methods to work with multiple departments
    public List<TaskAssignmentDepartmentResponser> getDepartmentResponsers() {
        return departmentResponsers;
    }
    
    public void setDepartmentResponsers(List<TaskAssignmentDepartmentResponser> departmentResponsers) {
        this.departmentResponsers = departmentResponsers;
    }
    
    public void addDepartmentResponser(TaskAssignmentDepartmentResponser responder) {
        this.departmentResponsers.add(responder);
    }
    
    // Get all department IDs assigned to this task
    public List<Integer> getAllClubDepartmentIDs() {
        List<Integer> departmentIDs = new ArrayList<>();
        for (TaskAssignmentDepartmentResponser responder : departmentResponsers) {
            departmentIDs.add(responder.getResponderID());
        }
        return departmentIDs;
    }
    
    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public java.util.Date getTermStart() {
        return termStart;
    }

    public void setTermStart(java.util.Date termStart) {
        this.termStart = termStart;
    }

    public java.util.Date getTermEnd() {
        return termEnd;
    }

    public void setTermEnd(java.util.Date termEnd) {
        this.termEnd = termEnd;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    
    // Alias method for compatibility with JSP that uses 'title'
    public String getTitle() {
        return taskName;
    }
    
    public void setTitle(String title) {
        this.taskName = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public java.util.Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(java.util.Date dueDate) {
        this.dueDate = dueDate;
    }
    
    public java.util.Date getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(java.util.Date startedDate) {
        this.startedDate = startedDate;
    }    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }
      public List<Users> getAssignedMembers() {
        return assignedMembers;
    }
    
    public void setAssignedMembers(List<Users> assignedMembers) {
        this.assignedMembers = assignedMembers;
    }

    @Override
    public String toString() {
        return "TaskAssignmentDepartment{" +
                "taskAssignmentDepartmentID=" + taskAssignmentDepartmentID +
                ", eventID=" + eventID +
                ", term='" + term + '\'' +
                ", termStart=" + termStart +
                ", termEnd=" + termEnd +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", startedDate=" + startedDate +
                ", dueDate=" + dueDate +
                ", departmentResponsers=" + departmentResponsers +
                '}';
    }
}
