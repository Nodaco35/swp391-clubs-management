/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.math.BigDecimal;
import java.sql.*;
/**
 *
 * @author he181
 */
public class MemberIncomeContributions {
    private int contributionID;
    private int incomeID;
    private String userID;
    private int clubID;
    private String termID;
    private BigDecimal amount;
    private String contributionStatus;
    private Timestamp paidDate;
    private Timestamp createdAt;
    private Timestamp dueDate;
    private String userName;
    private String email;
    private String avtSrc;
    public MemberIncomeContributions() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public String getAvtSrc() {
        return avtSrc;
    }

    public void setAvtSrc(String avtSrc) {
        this.avtSrc = avtSrc;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    
    public int getContributionID() {
        return contributionID;
    }

    public void setContributionID(int contributionID) {
        this.contributionID = contributionID;
    }

    public int getIncomeID() {
        return incomeID;
    }

    public void setIncomeID(int incomeID) {
        this.incomeID = incomeID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getContributionStatus() {
        return contributionStatus;
    }

    public void setContributionStatus(String contributionStatus) {
        this.contributionStatus = contributionStatus;
    }

    public Timestamp getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Timestamp paidDate) {
        this.paidDate = paidDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "MemberIncomeContributions{" + "contributionID=" + contributionID + ", incomeID=" + incomeID + ", userID=" + userID + ", clubID=" + clubID + ", termID=" + termID + ", amount=" + amount + ", contributionStatus=" + contributionStatus + ", paidDate=" + paidDate + ", createdAt=" + createdAt + ", userName=" + userName + ", email=" + email + ", avtSrc=" + avtSrc + '}';
    }

    public Timestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    
    
    
    
}
