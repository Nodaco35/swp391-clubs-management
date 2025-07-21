package models;

import java.sql.Timestamp;

public class ClubApprovalHistory {
    private int id;
    private int clubID;
    private String actionType;     // "Approved", "Rejected", ...
    private String reason;         // Lý do từ chối (nếu có)
    private String requestType;    // "Create", "Update", ...
    private Timestamp actionAt;    // Thời điểm hành động

    public ClubApprovalHistory() {
    }

    public ClubApprovalHistory(int id, int clubID, String actionType, String reason, String requestType, Timestamp actionAt) {
        this.id = id;
        this.clubID = clubID;
        this.actionType = actionType;
        this.reason = reason;
        this.requestType = requestType;
        this.actionAt = actionAt;
    }

    
    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Timestamp getActionAt() {
        return actionAt;
    }

    public void setActionAt(Timestamp actionAt) {
        this.actionAt = actionAt;
    }
}
