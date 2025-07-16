package models;

import java.util.Date;
import java.util.List;

public class Tasks {
    private int taskID;
    private Integer parentTaskID; // NULL nếu là task dành cho ban

    private EventTerms term;      // foreign key: TermID
    private Events event;         // foreign key: EventID
    private Clubs club;           // foreign key: ClubID

    private String assigneeType;  // ENUM('User', 'Department')
    private Users userAssignee;         // nếu AssigneeType = 'User'
    private Department departmentAssignee; // nếu AssigneeType = 'Department'

    private String title;
    private String description;
    private String status;        // ENUM('ToDo', 'InProgress', 'Review', 'Rejected', 'Done')
    private String reviewComment;

    private Date startDate;
    private Date endDate;

    private Users createdBy;      // foreign key: CreatedBy
    private Date createdAt;

    public Tasks() {}

    public Tasks(int taskID, Date createdAt, Users createdBy, Date endDate, Date startDate, String reviewComment, String status, String description, String title, Department departmentAssignee, Users userAssignee, String assigneeType, Clubs club, Events event, EventTerms term, Integer parentTaskID) {
        this.taskID = taskID;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.endDate = endDate;
        this.startDate = startDate;
        this.reviewComment = reviewComment;
        this.status = status;
        this.description = description;
        this.title = title;
        this.departmentAssignee = departmentAssignee;
        this.userAssignee = userAssignee;
        this.assigneeType = assigneeType;
        this.club = club;
        this.event = event;
        this.term = term;
        this.parentTaskID = parentTaskID;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public Integer getParentTaskID() {
        return parentTaskID;
    }

    public void setParentTaskID(Integer parentTaskID) {
        this.parentTaskID = parentTaskID;
    }

    public EventTerms getTerm() {
        return term;
    }

    public void setTerm(EventTerms term) {
        this.term = term;
    }

    public Events getEvent() {
        return event;
    }

    public void setEvent(Events event) {
        this.event = event;
    }

    public Clubs getClub() {
        return club;
    }

    public void setClub(Clubs club) {
        this.club = club;
    }

    public String getAssigneeType() {
        return assigneeType;
    }

    public void setAssigneeType(String assigneeType) {
        this.assigneeType = assigneeType;
    }

    public Users getUserAssignee() {
        return userAssignee;
    }

    public void setUserAssignee(Users userAssignee) {
        this.userAssignee = userAssignee;
    }

    public Department getDepartmentAssignee() {
        return departmentAssignee;
    }

    public void setDepartmentAssignee(Department departmentAssignee) {
        this.departmentAssignee = departmentAssignee;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Users getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Users createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}