package models;

import java.util.Date;
import java.util.List;

public class Tasks {
    private int taskID;
    private int parentTaskID; // nếu NULL là nhiệm vụ giao cho ban
    private EventTerms term; // 'Trước sự kiện', 'Trong sự kiện', 'Sau sự kiện'
    private Events event; // foreign key -> Events
    private Clubs club;   // foreign key -> Clubs
    private String title;
    private String description;
    private String status; // 'ToDo', 'InProgress', 'Review', 'Done'
    private String priority; // 'LOW', 'MEDIUM', 'HIGH'
    private int progressPercent;
    private Date startDate;
    private Date endDate;
    private Users createdBy; // foreign key -> Users
    private Date createdAt;

    private List<Department> departments;




    public Tasks() {
    }

    public Tasks(int parentTaskID, int taskID, EventTerms term, Events event, Clubs club, String title, String status, String description, String priority, int progressPercent, Date startDate, Date endDate, Users createdBy, Date createdAt) {
        this.parentTaskID = parentTaskID;
        this.taskID = taskID;
        this.term = term;
        this.event = event;
        this.club = club;
        this.title = title;
        this.status = status;
        this.description = description;
        this.priority = priority;
        this.progressPercent = progressPercent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public List<Department> getDepartments() {
        return departments;
    }
    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Users getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Users createdBy) {
        this.createdBy = createdBy;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
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

    public int getParentTaskID() {
        return parentTaskID;
    }

    public void setParentTaskID(int parentTaskID) {
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
}