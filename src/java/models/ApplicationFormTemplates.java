package models;

public class ApplicationFormTemplates {
    private int templateID;
    private int clubID;
    private String formType;  // ENUM('CLUB','EVENT', 'OTHER')
    private String fieldName;
    private String fieldType; // ENUM('TEXT', 'TEXTAREA', 'DROPDOWN', 'CHECKBOX', 'FILEUPLOAD', 'INFO')
    private boolean isRequired;
    private String options;   // Nullable

    public ApplicationFormTemplates() {}

    public ApplicationFormTemplates(int templateID, int clubID, String formType, String fieldName, String fieldType, boolean isRequired, String options) {
        this.templateID = templateID;
        this.clubID = clubID;
        this.formType = formType;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.isRequired = isRequired;
        this.options = options;
    }

    public int getTemplateID() { return templateID; }
    public void setTemplateID(int templateID) { this.templateID = templateID; }

    public int getClubID() { return clubID; }
    public void setClubID(int clubID) { this.clubID = clubID; }

    public String getFormType() { return formType; }
    public void setFormType(String formType) { this.formType = formType; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }

    public boolean isRequired() { return isRequired; }
    public void setRequired(boolean required) { isRequired = required; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
}
