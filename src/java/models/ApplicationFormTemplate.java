package models;

public class ApplicationFormTemplate {
    private int templateId;
    private int formId;
    private String fieldName;
    private String fieldType; // 'Text', 'Textarea', 'Dropdown', etc.
    private boolean Required;
    private String options; // JSON hoặc text
    private int displayOrder; // Thứ tự hiển thị của câu hỏi trong form

    public ApplicationFormTemplate() {
    }

    public ApplicationFormTemplate(int templateId, int formId, String fieldName, String fieldType,
            boolean isRequired, String options, int displayOrder) {
        this.templateId = templateId;
        this.formId = formId;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.Required = isRequired;
        this.options = options;
        this.displayOrder = displayOrder;
    }
    
    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public boolean isRequired() {
        return Required;
    }

    public void setRequired(boolean Required) {
        this.Required = Required;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }
    public void setDisplayOrder(int displayOrder) {    
        this.displayOrder = displayOrder;
    }

    public String getClassName() {
        return this.getClass().getName();
    }
}