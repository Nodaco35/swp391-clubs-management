
package models;

import java.sql.Timestamp;
import java.util.Date;


//taskAssignDepartment day nhe
public class TaskAssignment {
    private int taskAssignmentID;
    private int eventID;
    private int departmentID;
    private String description;
    private String taskName;
    private Timestamp dueDate;
    private String status;
     private String eventName;
     private String departmentName;
     private String clubName;
     private int clubID;
     
     
     //mới
    private int taskAssignmentDepartmentID;
    private String term;
    private Date termStart;
    private Date termEnd;
    private Date startedDate;
    
     
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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Timestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
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

    
    //mới
    public int getTaskAssignmentDepartmentID() {
        return taskAssignmentDepartmentID;
    }

    public void setTaskAssignmentDepartmentID(int taskAssignmentDepartmentID) {
        this.taskAssignmentDepartmentID = taskAssignmentDepartmentID;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Date getTermStart() {
        return termStart;
    }

    public void setTermStart(Date termStart) {
        this.termStart = termStart;
    }

    public Date getTermEnd() {
        return termEnd;
    }

    public void setTermEnd(Date termEnd) {
        this.termEnd = termEnd;
    }

    public Date getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(Date startedDate) {
        this.startedDate = startedDate;
    }
    
    
}
