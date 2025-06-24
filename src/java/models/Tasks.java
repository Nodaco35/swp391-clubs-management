package models;

import java.util.Date;
import java.util.List;

public class Tasks {
    private int taskID;
    private int parentTask; // nếu NULL là nhiệm vụ giao cho ban
    private String term; // 'Trước sự kiện', 'Trong sự kiện', 'Sau sự kiện'
    private Date termStart;
    private Date termEnd;
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

    public List<Department> getDepartments() {
        return departments;
    }
    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }


    public Tasks() {
    }

    public Tasks(int taskID, Date createdAt, Users createdBy, Date endDate, Date startDate, int progressPercent, String priority, String status, String description, String title, Clubs club, Events event, Date termStart, Date termEnd, String term, int parentTask) {
        this.taskID = taskID;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.endDate = endDate;
        this.startDate = startDate;
        this.progressPercent = progressPercent;
        this.priority = priority;
        this.status = status;
        this.description = description;
        this.title = title;
        this.club = club;
        this.event = event;
        this.termStart = termStart;
        this.termEnd = termEnd;
        this.term = term;
        this.parentTask = parentTask;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Clubs getClub() {
        return club;
    }

    public void setClub(Clubs club) {
        this.club = club;
    }

    public Date getTermEnd() {
        return termEnd;
    }

    public void setTermEnd(Date termEnd) {
        this.termEnd = termEnd;
    }

    public Events getEvent() {
        return event;
    }

    public void setEvent(Events event) {
        this.event = event;
    }

    public Date getTermStart() {
        return termStart;
    }

    public void setTermStart(Date termStart) {
        this.termStart = termStart;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getParentTask() {
        return parentTask;
    }

    public void setParentTask(int parentTask) {
        this.parentTask = parentTask;
    }
}

