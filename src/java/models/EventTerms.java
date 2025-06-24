package models;

import java.util.Date;

public class EventTerms {
    private int termID;
    private String termName;
    private Events event;
    private Date termStart;
    private Date termEnd;

    public EventTerms() {
    }

    public EventTerms(Events event, Date termEnd, Date termStart, String termName, int termID) {
        this.event = event;
        this.termEnd = termEnd;
        this.termStart = termStart;
        this.termName = termName;
        this.termID = termID;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public Events getEvent() {
        return event;
    }

    public void setEvent(Events event) {
        this.event = event;
    }

    public Date getTermStart() {
        return termStart;
    }

    public void setTermStart(Date termStart) {
        this.termStart = termStart;
    }

    public Date getTermEnd() {
        return termEnd;
    }

    public void setTermEnd(Date termEnd) {
        this.termEnd = termEnd;
    }

    public int getTermID() {
        return termID;
    }

    public void setTermID(int termID) {
        this.termID = termID;
    }
}
