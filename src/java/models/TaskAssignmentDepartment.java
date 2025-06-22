package models;

import java.util.Date;
import java.util.List;

public class TaskAssignmentDepartment {
    private int taskAssignmentDepartmentID;
    private Events event;
    private String term;
    private Date termStart;
    private Date termEnd;
    private String description;
    private String status;
    private String taskName;
    private Date startedDate;
    private Date dueDate;

    private List<ClubDepartment> departments;

    public TaskAssignmentDepartment() {
    }

    public TaskAssignmentDepartment(Date dueDate, Date startedDate, String taskName, String status, String description, Date termEnd, Date termStart, String term, Events event, int taskAssignmentDepartmentID) {
        this.dueDate = dueDate;
        this.startedDate = startedDate;
        this.taskName = taskName;
        this.status = status;
        this.description = description;
        this.termEnd = termEnd;
        this.termStart = termStart;
        this.term = term;
        this.event = event;
        this.taskAssignmentDepartmentID = taskAssignmentDepartmentID;
    }

    public List<ClubDepartment> getDepartments() {
        return departments;
    }

    public void setDepartments(List<ClubDepartment> departments) {
        this.departments = departments;
    }

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

    public Events getEvent() {
        return event;
    }

    public void setEvent(Events event) {
        this.event = event;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
