
package models;

import java.security.Timestamp;

public class Notification {
    private int notificationID;
    private String title;
    private String content;
    private Timestamp createdDate;
    private Integer receiverID; // Nullable

    public Notification() {
    }

    public Notification(int notificationID, String title, String content, Timestamp createdDate, Integer receiverID) {
        this.notificationID = notificationID;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.receiverID = receiverID;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(Integer receiverID) {
        this.receiverID = receiverID;
    }

    @Override
    public String toString() {
        return "Notification{" + "notificationID=" + notificationID + ", title=" + title + ", content=" + content + ", createdDate=" + createdDate + ", receiverID=" + receiverID + '}';
    }
    
    
}
