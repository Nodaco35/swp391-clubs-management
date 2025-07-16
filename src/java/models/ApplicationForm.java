
package models;

/**
 *
 * @author Vinh
 */
public class ApplicationForm {
    private int formId;
    private int clubId;
    private Integer eventId;
    private String formType;
    private String title;
    private boolean published;

    public ApplicationForm() {}

    public ApplicationForm(int formId, int clubId, Integer eventId, String formType, String title, boolean published) {
        this.formId = formId;
        this.clubId = clubId;
        this.eventId = eventId;
        this.formType = formType;
        this.title = title;
        this.published = published;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
    
}
