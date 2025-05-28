
package models;


import java.sql.Timestamp;

public class Notification {
    private int notificationID;
    private String title;
    private String content;
    private Timestamp createdDate;
    private String receiverID; // Nullable
    private String prioity;
    private String status;
    private String senderID;
    private String senderName;
    private String senderAvatar;
    private String senderEmail;
    public Notification() {
    }

    public Notification(int notificationID, String title, String content, Timestamp createdDate, String receiverID) {
        this.notificationID = notificationID;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.receiverID = receiverID;
    }

    public String getPrioity() {
        return prioity;
    }

    public void setPrioity(String prioity) {
        this.prioity = prioity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    @Override
    public String toString() {
        return "Notification{" + "notificationID=" + notificationID + ", title=" + title + ", content=" + content + ", createdDate=" + createdDate + ", receiverID=" + receiverID + '}';
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvataSrc) {
        this.senderAvatar = senderAvataSrc;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }
    
    
    
    
}
