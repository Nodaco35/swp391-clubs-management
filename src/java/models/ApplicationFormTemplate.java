package models;

public class ApplicationFormTemplate {
    private int templateId;
    private int clubId;
    private Integer eventId;
    private String formType; // 'Club', 'Event', 'Other'
    private String title;
    private String fieldName;
    private String fieldType; // 'Text', 'Textarea', 'Dropdown', etc.
    private boolean isRequired;
    private String options; // JSON hoặc text
    private boolean published;

    public ApplicationFormTemplate() {}

    public ApplicationFormTemplate(int templateId, int clubId, Integer eventId, String formType, String title,
                                   String fieldName, String fieldType, boolean isRequired, String options, boolean published) {
        this.templateId = templateId;
        this.clubId = clubId;
        this.eventId = eventId;
        this.formType = formType;
        this.title = title;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.isRequired = isRequired;
        this.options = options;
        this.published = published;
    }

    // Getters và Setters
    public int getTemplateId() { return templateId; }
    public void setTemplateId(int templateId) { this.templateId = templateId; }
    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }
    public Integer getEventId() { return eventId; }
    public void setEventId(Integer eventId) { this.eventId = eventId; }
    public String getFormType() { return formType; }
    public void setFormType(String formType) { this.formType = formType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public boolean isRequired() { return isRequired; }
    public void setRequired(boolean isRequired) { this.isRequired = isRequired; }
    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
}