package models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Expenses {
    private int expenseID;
    private int clubID;
    private String termID;
    private String purpose;
    private BigDecimal amount;
    private Timestamp expenseDate;
    private String description;
    private String attachment;
    private String createdBy;
    private String status;
    private Timestamp createdAt;
    private String approvedBy;
    private Timestamp approvedAt;
    private String createdByName; // For display purposes

    // Getters and Setters
    public int getExpenseID() { return expenseID; }
    public void setExpenseID(int expenseID) { this.expenseID = expenseID; }
    public int getClubID() { return clubID; }
    public void setClubID(int clubID) { this.clubID = clubID; }
    public String getTermID() { return termID; }
    public void setTermID(String termID) { this.termID = termID; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Timestamp getExpenseDate() { return expenseDate; }
    public void setExpenseDate(Timestamp expenseDate) { this.expenseDate = expenseDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAttachment() { return attachment; }
    public void setAttachment(String attachment) { this.attachment = attachment; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public Timestamp getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Timestamp approvedAt) { this.approvedAt = approvedAt; }
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
}