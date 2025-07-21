/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.math.BigDecimal;
import java.sql.Timestamp;
/**
 *
 * @author he181
 */
public class Income {
    private int incomeID;
    private int clubID;
    private String termID;
    private String source;
    private BigDecimal amount;
    private Timestamp incomeDate;
    private String description;
    private String attachment;
    private String status;

    public Income() {
    }

    public int getIncomeID() {
        return incomeID;
    }

    public void setIncomeID(int incomeID) {
        this.incomeID = incomeID;
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public String getTermID() {
        return termID;
    }

    public void setTermID(String termID) {
        this.termID = termID;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Timestamp getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(Timestamp incomeDate) {
        this.incomeDate = incomeDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Income{" + "incomeID=" + incomeID + ", clubID=" + clubID + ", termID=" + termID + ", source=" + source + ", amount=" + amount + ", incomeDate=" + incomeDate + ", description=" + description + ", attachment=" + attachment + ", status=" + status + '}';
    }
    
    
}
