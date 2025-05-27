
package models;

import java.sql.Timestamp;

public class Notifications {
    private int notificationID;
    private String title;
    private String content;
    private Timestamp createdDate;
    private String receiverID;
    private String priority; // ENUM('LOW', 'MEDIUM', 'HIGH')
    private String status;   // ENUM('UNREAD', 'READ')

    public Notifications() {}

    public Notifications(int notificationID, String title, String content, Timestamp createdDate, String receiverID, String priority, String status) {
        this.notificationID = notificationID;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.receiverID = receiverID;
        this.priority = priority;
        this.status = status;
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

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
