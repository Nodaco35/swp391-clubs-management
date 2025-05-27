package models;

public class ApplicationResponses {
    private int responseID;
    private int applicationID;
    private int templateID;
    private String fieldValue;

    public ApplicationResponses() {}

    public ApplicationResponses(int responseID, int applicationID, int templateID, String fieldValue) {
        this.responseID = responseID;
        this.applicationID = applicationID;
        this.templateID = templateID;
        this.fieldValue = fieldValue;
    }

    public int getResponseID() { return responseID; }
    public void setResponseID(int responseID) { this.responseID = responseID; }

    public int getApplicationID() { return applicationID; }
    public void setApplicationID(int applicationID) { this.applicationID = applicationID; }

    public int getTemplateID() { return templateID; }
    public void setTemplateID(int templateID) { this.templateID = templateID; }

    public String getFieldValue() { return fieldValue; }
    public void setFieldValue(String fieldValue) { this.fieldValue = fieldValue; }
}
