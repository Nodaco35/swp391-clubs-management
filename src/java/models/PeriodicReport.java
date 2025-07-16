package models;

import java.util.Date;

public class PeriodicReport {
    private int reportId;
    private String term;
    private String clubName;
    private String submittedBy;
    private String status;
    private Date submissionDate;

    // Extra info for dashboard display
    private String overview;
    private int memberCount;
    private int eventCount;
    private double budget;
    private double participationRate;

    // Constructors
    public PeriodicReport() {
    }

    public PeriodicReport(int reportId, String term, String clubName, String submittedBy, String status, Date submissionDate,
                          String overview, int memberCount, int eventCount, double budget, double participationRate) {
        this.reportId = reportId;
        this.term = term;
        this.clubName = clubName;
        this.submittedBy = submittedBy;
        this.status = status;
        this.submissionDate = submissionDate;
        this.overview = overview;
        this.memberCount = memberCount;
        this.eventCount = eventCount;
        this.budget = budget;
        this.participationRate = participationRate;
    }

    // Getters and Setters
    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getParticipationRate() {
        return participationRate;
    }

    public void setParticipationRate(double participationRate) {
        this.participationRate = participationRate;
    }
}
