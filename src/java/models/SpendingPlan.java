// Update SpendingPlan.java: add fields for eventName and totalActual

// New model class: SpendingPlan.java
package models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class SpendingPlan {
    private int planID;
    private int clubID;
    private Integer eventID; // Nullable
    private String planName;
    private BigDecimal totalPlannedBudget;
    private String status;
    private Timestamp createdDate;
    private String eventName; // Added
    private BigDecimal totalActual; // Added

    // Constructors
    public SpendingPlan() {}

    // Getters and Setters
    public int getPlanID() {
        return planID;
    }

    public void setPlanID(int planID) {
        this.planID = planID;
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public BigDecimal getTotalPlannedBudget() {
        return totalPlannedBudget;
    }

    public void setTotalPlannedBudget(BigDecimal totalPlannedBudget) {
        this.totalPlannedBudget = totalPlannedBudget;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public BigDecimal getTotalActual() {
        return totalActual;
    }

    public void setTotalActual(BigDecimal totalActual) {
        this.totalActual = totalActual;
    }

    @Override
    public String toString() {
        return "SpendingPlan{" +
                "planID=" + planID +
                ", clubID=" + clubID +
                ", eventID=" + eventID +
                ", planName='" + planName + '\'' +
                ", totalPlannedBudget=" + totalPlannedBudget +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                ", eventName='" + eventName + '\'' +
                ", totalActual=" + totalActual +
                '}';
    }
}