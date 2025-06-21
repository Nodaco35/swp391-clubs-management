package models;

import java.sql.Timestamp;

public class TaskAssignmentDepartmentResponser {
    private int responserID;
    private int taskAssignmentID; // References TaskAssignmentDepartment.taskAssignmentID
    private int responderID;
    private Timestamp respondedAt;
    
    // Additional view fields
    private String responderName;

    public TaskAssignmentDepartmentResponser() {
    }

    public TaskAssignmentDepartmentResponser(int responserID, int taskAssignmentID, int responderID, Timestamp respondedAt) {
        this.responserID = responserID;
        this.taskAssignmentID = taskAssignmentID;
        this.responderID = responderID;
        this.respondedAt = respondedAt;
    }    public int getResponserID() {
        return responserID;
    }

    public void setResponserID(int responserID) {
        this.responserID = responserID;
    }

    public int getTaskAssignmentID() {
        return taskAssignmentID;
    }

    public void setTaskAssignmentID(int taskAssignmentID) {
        this.taskAssignmentID = taskAssignmentID;
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
        return "TaskAssignmentDepartmentResponser{" +
                "responserID=" + responserID +
                ", taskAssignmentID=" + taskAssignmentID +
                ", responderID=" + responderID +
                ", respondedAt=" + respondedAt +
                '}';
    }
}
