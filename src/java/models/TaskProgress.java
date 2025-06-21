package models;

import java.sql.Timestamp;

public class TaskProgress {
    private int progressID;
    private int taskMemberID;  // References TaskAssignmentMember.taskMemberID
    private String description;
    private int progressPercentage;
    private int submittedByID;
    private Timestamp submittedAt;
    
    // Additional view fields
    private String submitterName;
    private String taskName;

    public TaskProgress() {
    }

    public TaskProgress(int progressID, int taskMemberID, String description, int progressPercentage, int submittedByID, Timestamp submittedAt) {
        this.progressID = progressID;
        this.taskMemberID = taskMemberID;
        this.description = description;
        this.progressPercentage = progressPercentage;
        this.submittedByID = submittedByID;
        this.submittedAt = submittedAt;
    }

    public int getProgressID() {
        return progressID;
    }

    public void setProgressID(int progressID) {
        this.progressID = progressID;
    }

    public int getTaskMemberID() {
        return taskMemberID;
    }

    public void setTaskMemberID(int taskMemberID) {
        this.taskMemberID = taskMemberID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public int getSubmittedByID() {
        return submittedByID;
    }

    public void setSubmittedByID(int submittedByID) {
        this.submittedByID = submittedByID;
    }

    public Timestamp getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Timestamp submittedAt) {
        this.submittedAt = submittedAt;
    }
    
    public String getSubmitterName() {
        return submitterName;
    }

    public void setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String toString() {
        return "TaskProgress{" +
                "progressID=" + progressID +
                ", taskMemberID=" + taskMemberID +
                ", description='" + description + '\'' +
                ", progressPercentage=" + progressPercentage +
                ", submittedByID=" + submittedByID +
                ", submittedAt=" + submittedAt +
                '}';
    }
}
