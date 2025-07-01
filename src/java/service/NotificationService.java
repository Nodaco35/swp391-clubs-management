package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dal.NotificationTemplateDAO;
import dal.StageNotificationDAO;
import models.NotificationTemplate;
import models.StageNotification;

public class NotificationService {
    
    private final NotificationTemplateDAO templateDAO = new NotificationTemplateDAO();
    private final StageNotificationDAO notificationDAO = new StageNotificationDAO();
    
    
    public NotificationTemplate getTemplateById(int templateID) {
        return templateDAO.getTemplateById(templateID);
    }
    
    public List<NotificationTemplate> getTemplatesByClub(int clubID) {
        return templateDAO.getTemplatesByClubId(clubID);
    }
    
    public List<NotificationTemplate> getReusableTemplatesByClub(int clubID) {
        return templateDAO.getReusableTemplatesByClubId(clubID);
    }
    
    public int createTemplate(NotificationTemplate template) {
        return templateDAO.createNotificationTemplate(template);
    }
    
    public boolean updateTemplate(NotificationTemplate template) {
        return templateDAO.updateNotificationTemplate(template);
    }
    
    public boolean deleteTemplate(int templateID) {
        return templateDAO.deleteNotificationTemplate(templateID);
    }
    
    
    public StageNotification getNotificationById(int notificationID) {
        return notificationDAO.getNotificationById(notificationID);
    }
    
    public List<StageNotification> getNotificationsByStage(int stageID) {
        return notificationDAO.getNotificationsByStageId(stageID);
    }
    
    public int createStageNotification(StageNotification notification) {
        return notificationDAO.createStageNotification(notification);
    }
    
    public boolean updateStageNotification(StageNotification notification) {
        return notificationDAO.updateStageNotification(notification);
    }
    

    public String processTemplate(String template, Map<String, String> data) {
        String result = template;
        
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            result = result.replace(placeholder, entry.getValue());
        }
        
        return result;
    }
    
    // Create a notification from a template with dynamic data
    public StageNotification createNotificationFromTemplate(int stageID, int templateID, Map<String, String> data, String createdBy) {
        NotificationTemplate template = templateDAO.getTemplateById(templateID);
        if (template == null) {
            return null;
        }
        
        String processedTitle = processTemplate(template.getTitle(), data);
        String processedContent = processTemplate(template.getContent(), data);
        
        StageNotification notification = new StageNotification();
        notification.setStageID(stageID);
        notification.setTemplateID(templateID);
        notification.setTitle(processedTitle);
        notification.setContent(processedContent);
        notification.setCreatedBy(createdBy);
        
        int newId = notificationDAO.createStageNotification(notification);
        if (newId > 0) {
            notification.setNotificationID(newId);
            return notification;
        }
        
        return null;
    }
    

    public Map<String, String> createCommonPlaceholders(String candidateName, String stageName, String clubName, String interviewDate) {
        Map<String, String> data = new HashMap<>();
        data.put("candidate_name", candidateName != null ? candidateName : "[Tên ứng viên]");
        data.put("stage_name", stageName != null ? stageName : "[Tên vòng]");
        data.put("club_name", clubName != null ? clubName : "[Tên CLB]");
        data.put("interview_date", interviewDate != null ? interviewDate : "[Ngày phỏng vấn]");
        return data;
    }
}
