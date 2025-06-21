package models;

import java.sql.Timestamp;

public class TaskFeedback {
    private int feedbackID;
    private int taskMemberID;  // References TaskAssignmentMember.taskMemberID
    private String content;
    private int feedbackByID;
    private Timestamp feedbackAt;
    
    // Additional view fields
    private String feedbackByName;
    private String taskName;

    public TaskFeedback() {
    }

    public TaskFeedback(int feedbackID, int taskMemberID, String content, int feedbackByID, Timestamp feedbackAt) {
        this.feedbackID = feedbackID;
        this.taskMemberID = taskMemberID;
        this.content = content;
        this.feedbackByID = feedbackByID;
        this.feedbackAt = feedbackAt;
    }

    public int getFeedbackID() {
        return feedbackID;
    }

    public void setFeedbackID(int feedbackID) {
        this.feedbackID = feedbackID;
    }

    public int getTaskMemberID() {
        return taskMemberID;
    }

    public void setTaskMemberID(int taskMemberID) {
        this.taskMemberID = taskMemberID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFeedbackByID() {
        return feedbackByID;
    }

    public void setFeedbackByID(int feedbackByID) {
        this.feedbackByID = feedbackByID;
    }

    public Timestamp getFeedbackAt() {
        return feedbackAt;
    }

    public void setFeedbackAt(Timestamp feedbackAt) {
        this.feedbackAt = feedbackAt;
    }
    
    public String getFeedbackByName() {
        return feedbackByName;
    }

    public void setFeedbackByName(String feedbackByName) {
        this.feedbackByName = feedbackByName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String toString() {
        return "TaskFeedback{" +
                "feedbackID=" + feedbackID +
                ", taskMemberID=" + taskMemberID +
                ", content='" + content + '\'' +
                ", feedbackByID=" + feedbackByID +
                ", feedbackAt=" + feedbackAt +
                '}';
    }
}
