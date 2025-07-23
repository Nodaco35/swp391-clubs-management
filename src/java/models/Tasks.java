package models;

import java.util.Date;

public class Tasks {
    private int taskID;
    private Integer parentTaskID;

    private EventTerms term;      // TermID
    private Events event;         // EventID
    private Clubs club;           // ClubID
    private Documents document;   // DocumentID

    private String assigneeType;  // ENUM('User', 'Department')
    private Users userAssignee;   // UserID (nếu assigneeType = 'User')
    private Department departmentAssignee; // DepartmentID (nếu assigneeType = 'Department')

    private String title;
    private String description;
    private String content;

    private String status;        // ENUM('ToDo', 'InProgress', 'Review', 'Rejected', 'Done')
    private String rating;        // ENUM('Positive', 'Neutral', 'Negative')
    private String lastRejectReason;

    private String reviewComment;

    private Date startDate;
    private Date endDate;

    private Users createdBy;      // CreatedBy
    private Date createdAt;


    public Tasks() {}


    public Tasks(int taskID, Integer parentTaskID, EventTerms term, Events event, Clubs club, Documents document, String assigneeType, Users userAssignee, Department departmentAssignee, String title, String description, String content, String status, String rating, String lastRejectReason, String reviewComment, Date startDate, Date endDate, Users createdBy, Date createdAt) {
        this.taskID = taskID;
        this.parentTaskID = parentTaskID;
        this.term = term;
        this.event = event;
        this.club = club;
        this.document = document;
        this.assigneeType = assigneeType;
        this.userAssignee = userAssignee;
        this.departmentAssignee = departmentAssignee;
        this.title = title;
        this.description = description;
        this.content = content;
        this.status = status;
        this.rating = rating;
        this.lastRejectReason = lastRejectReason;
        this.reviewComment = reviewComment;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Documents getDocument() {
        return document;
    }

    public void setDocument(Documents document) {
        this.document = document;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getLastRejectReason() {
        return lastRejectReason;
    }

    public void setLastRejectReason(String lastRejectReason) {
        this.lastRejectReason = lastRejectReason;
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