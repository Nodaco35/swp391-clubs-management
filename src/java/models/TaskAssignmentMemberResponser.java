package models;

import java.sql.Timestamp;

public class TaskAssignmentMemberResponser {
    private int responserID;
    private int taskMemberID; // References TaskAssignmentMember.taskMemberID
    private int responderID;
    private Timestamp respondedAt;
    
    // Additional view fields
    private String responderName;

    public TaskAssignmentMemberResponser() {
    }

    public TaskAssignmentMemberResponser(int responserID, int taskMemberID, int responderID, Timestamp respondedAt) {
        this.responserID = responserID;
        this.taskMemberID = taskMemberID;
        this.responderID = responderID;
        this.respondedAt = respondedAt;
    }

    public int getResponserID() {
        return responserID;
    }

    public void setResponserID(int responserID) {
        this.responserID = responserID;
    }

    public int getTaskMemberID() {
        return taskMemberID;
    }

    public void setTaskMemberID(int taskMemberID) {
        this.taskMemberID = taskMemberID;
    }

    public int getResponderID() {
        return responderID;
    }

    public void setResponderID(int responderID) {
        this.responderID = responderID;
    }

    public Timestamp getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(Timestamp respondedAt) {
        this.respondedAt = respondedAt;
    }
    
    public String getResponderName() {
        return responderName;
    }

    public void setResponderName(String responderName) {
        this.responderName = responderName;
    }

    @Override
    public String toString() {
        return "TaskAssignmentMemberResponser{" +
                "responserID=" + responserID +
                ", taskMemberID=" + taskMemberID +
                ", responderID=" + responderID +
                ", respondedAt=" + respondedAt +
                '}';
    }
}
