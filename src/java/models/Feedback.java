package models;

import java.util.Date;

public class Feedback {
    private int feedbackID;
    private int eventID;
    private String userID;
    private boolean isAnonymous;
    private int rating;
    private String content;
    
    private int q1Organization; // Q1_Organization
    private int q2Communication; // Q2_Communication
    private int q3Support; // Q3_Support
    private int q4Relevance; // Q4_Relevance
    private int q5Welcoming; // Q5_Welcoming
    private int q6Value; // Q6_Value
    private int q7Timing; // Q7_Timing
    private int q8Participation; // Q8_Participation
    private int q9WillingnessToReturn; // Q9_WillingnessToReturn
    
    private Date createdAt;
    
    // Default constructor
    public Feedback() {
    }

    // Constructor with essential fields
    public Feedback(int eventID, String userID, boolean isAnonymous, int rating) {
        this.eventID = eventID;
        this.userID = userID;
        this.isAnonymous = isAnonymous;
        this.rating = rating;
    }

    // Full constructor
    public Feedback(int feedbackID, int eventID, String userID, boolean isAnonymous, int rating, String content,
                   int q1Organization, int q2Communication, int q3Support, int q4Relevance,
                   int q5Welcoming, int q6Value, int q7Timing, int q8Participation, int q9WillingnessToReturn,
                   Date createdAt) {
        this.feedbackID = feedbackID;
        this.eventID = eventID;
        this.userID = userID;
        this.isAnonymous = isAnonymous;
        this.rating = rating;
        this.content = content;
        this.q1Organization = q1Organization;
        this.q2Communication = q2Communication;
        this.q3Support = q3Support;
        this.q4Relevance = q4Relevance;
        this.q5Welcoming = q5Welcoming;
        this.q6Value = q6Value;
        this.q7Timing = q7Timing;
        this.q8Participation = q8Participation;
        this.q9WillingnessToReturn = q9WillingnessToReturn;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getFeedbackID() {
        return feedbackID;
    }

    public void setFeedbackID(int feedbackID) {
        this.feedbackID = feedbackID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getQ1Organization() {
        return q1Organization;
    }

    public void setQ1Organization(int q1Organization) {
        this.q1Organization = q1Organization;
    }

    public int getQ2Communication() {
        return q2Communication;
    }

    public void setQ2Communication(int q2Communication) {
        this.q2Communication = q2Communication;
    }

    public int getQ3Support() {
        return q3Support;
    }

    public void setQ3Support(int q3Support) {
        this.q3Support = q3Support;
    }

    public int getQ4Relevance() {
        return q4Relevance;
    }

    public void setQ4Relevance(int q4Relevance) {
        this.q4Relevance = q4Relevance;
    }

    public int getQ5Welcoming() {
        return q5Welcoming;
    }

    public void setQ5Welcoming(int q5Welcoming) {
        this.q5Welcoming = q5Welcoming;
    }

    public int getQ6Value() {
        return q6Value;
    }

    public void setQ6Value(int q6Value) {
        this.q6Value = q6Value;
    }

    public int getQ7Timing() {
        return q7Timing;
    }

    public void setQ7Timing(int q7Timing) {
        this.q7Timing = q7Timing;
    }

    public int getQ8Participation() {
        return q8Participation;
    }

    public void setQ8Participation(int q8Participation) {
        this.q8Participation = q8Participation;
    }

    public int getQ9WillingnessToReturn() {
        return q9WillingnessToReturn;
    }

    public void setQ9WillingnessToReturn(int q9WillingnessToReturn) {
        this.q9WillingnessToReturn = q9WillingnessToReturn;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
