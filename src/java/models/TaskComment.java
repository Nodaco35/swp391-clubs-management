package models;

import java.sql.Timestamp;

public class TaskComment {
    private int commentID;
    private int taskMemberID;  // References TaskAssignmentMember.taskMemberID
    private int userID;
    private String content;
    private Timestamp commentedAt;
    
    // Additional view fields
    private String userName;
    private String userAvatar;
    private String taskName;

    public TaskComment() {
    }

    public TaskComment(int commentID, int taskMemberID, int userID, String content, Timestamp commentedAt) {
        this.commentID = commentID;
        this.taskMemberID = taskMemberID;
        this.userID = userID;
        this.content = content;
        this.commentedAt = commentedAt;
    }

    public int getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public int getTaskMemberID() {
        return taskMemberID;
    }

    public void setTaskMemberID(int taskMemberID) {
        this.taskMemberID = taskMemberID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCommentedAt() {
        return commentedAt;
    }

    public void setCommentedAt(Timestamp commentedAt) {
        this.commentedAt = commentedAt;
    }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String toString() {
        return "TaskComment{" +
                "commentID=" + commentID +
                ", taskMemberID=" + taskMemberID +
                ", userID=" + userID +
                ", content='" + content + '\'' +
                ", commentedAt=" + commentedAt +
                '}';
    }
}
