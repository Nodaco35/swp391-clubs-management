package service;

import dal.ApplicationStageDAO;
import dal.RecruitmentCampaignDAO;
import dal.RecruitmentStageDAO;
import java.util.Date;
import java.util.List;
import models.ApplicationStage;
import models.RecruitmentCampaign;
import models.RecruitmentStage;

public class RecruitmentService {
    
    private final RecruitmentCampaignDAO campaignDAO = new RecruitmentCampaignDAO();
    private final RecruitmentStageDAO stageDAO = new RecruitmentStageDAO();
    private final ApplicationStageDAO appStageDAO = new ApplicationStageDAO();
    
    // Campaign management methods
    
    public RecruitmentCampaign getCampaignById(int recruitmentID) {
        return campaignDAO.getRecruitmentCampaignById(recruitmentID);
    }
    
    public List<RecruitmentCampaign> getCampaignsByClub(int clubID) {
        return campaignDAO.getRecruitmentCampaignsByClub(clubID);
    }
    
    public List<RecruitmentCampaign> getActiveCampaigns() {
        return campaignDAO.getActiveRecruitmentCampaigns();
    }
    
    public int createCampaign(RecruitmentCampaign campaign) {
        // Validate time overlap
        if (campaignDAO.hasCampaignTimeOverlap(campaign.getClubID(), campaign.getStartDate(), campaign.getEndDate(), null)) {
            return -1; // Indicates time overlap error
        }
        
        int result = campaignDAO.createRecruitmentCampaign(campaign);
        // result = -2 indicates that start date is in the past
        return result;
    }
    
    public boolean updateCampaign(RecruitmentCampaign campaign) {
        RecruitmentCampaign existingCampaign = campaignDAO.getRecruitmentCampaignById(campaign.getRecruitmentID());
        
        // Check if campaign is ongoing and start date is being changed
        if ("ONGOING".equals(existingCampaign.getStatus()) && 
            !existingCampaign.getStartDate().equals(campaign.getStartDate())) {
            return false; // Cannot change start date when campaign is ongoing
        }
        
        // Validate time overlap (excluding this campaign)
        if (campaignDAO.hasCampaignTimeOverlap(campaign.getClubID(), campaign.getStartDate(), 
                                              campaign.getEndDate(), campaign.getRecruitmentID())) {
            return false; // Indicates time overlap error
        }
        
        return campaignDAO.updateRecruitmentCampaign(campaign);
    }
    
    public boolean closeCampaign(int recruitmentID) {
        return campaignDAO.updateRecruitmentStatus(recruitmentID, "CLOSED");
    }
    
    public boolean deleteCampaign(int recruitmentID) {
        return campaignDAO.deleteRecruitmentCampaign(recruitmentID);
    }
    
    public List<RecruitmentCampaign> getAllCampaigns() {
        return campaignDAO.getAllRecruitmentCampaigns();
    }
    
    // Stage management methods
    
    public RecruitmentStage getStageById(int stageID) {
        return stageDAO.getStageById(stageID);
    }
    
    public List<RecruitmentStage> getStagesByCampaign(int recruitmentID) {
        return stageDAO.getStagesByRecruitmentId(recruitmentID);
    }
    
    public int createStage(RecruitmentStage stage) {
        // Validate time overlap within the campaign
        if (stageDAO.hasStageTimeOverlap(stage.getRecruitmentID(), stage.getStartDate(), stage.getEndDate(), null)) {
            return -1; // Indicates time overlap error
        }
        
        // Validate stage dates are within campaign dates
        RecruitmentCampaign campaign = campaignDAO.getRecruitmentCampaignById(stage.getRecruitmentID());
        if (stage.getStartDate().before(campaign.getStartDate()) || 
            stage.getEndDate().after(campaign.getEndDate())) {
            return -2; // Indicates dates outside of campaign range
        }
        
        return stageDAO.createRecruitmentStage(stage);
    }
    
    public boolean updateStage(RecruitmentStage stage) {
        // Validate time overlap (excluding this stage)
        if (stageDAO.hasStageTimeOverlap(stage.getRecruitmentID(), stage.getStartDate(), 
                                        stage.getEndDate(), stage.getStageID())) {
            return false; // Indicates time overlap error
        }
        
        // Validate stage dates are within campaign dates
        RecruitmentCampaign campaign = campaignDAO.getRecruitmentCampaignById(stage.getRecruitmentID());
        if (stage.getStartDate().before(campaign.getStartDate()) || 
            stage.getEndDate().after(campaign.getEndDate())) {
            return false; // Indicates dates outside of campaign range
        }
        
        return stageDAO.updateRecruitmentStage(stage);
    }
    
    public boolean updateStageStatus(int stageID, String status) {
        return stageDAO.updateStageStatus(stageID, status);
    }
    
    // Application stage management methods
    
    public ApplicationStage getApplicationStageById(int applicationStageID) {
        return appStageDAO.getApplicationStageById(applicationStageID);
    }
    
    public List<ApplicationStage> getApplicationStagesByStage(int stageID) {
        return appStageDAO.getApplicationStagesByStageId(stageID);
    }
    
    public List<ApplicationStage> getApplicationStagesByStageAndStatus(int stageID, String status) {
        return appStageDAO.getApplicationStagesByStageAndStatus(stageID, status);
    }
    
    public List<ApplicationStage> getApplicationHistory(int applicationID) {
        return appStageDAO.getApplicationStagesByApplicationId(applicationID);
    }
    
    public int createApplicationStage(ApplicationStage appStage) {
        return appStageDAO.createApplicationStage(appStage);
    }
    
    public boolean updateApplicationStatus(int applicationStageID, String status, String updatedBy) {
        return appStageDAO.updateApplicationStageStatus(applicationStageID, status, updatedBy);
    }
    
    // Helper methods
    
    // Check if a recruitment campaign is active (not closed and current date is within range)
    public boolean isCampaignActive(int recruitmentID) {
        RecruitmentCampaign campaign = campaignDAO.getRecruitmentCampaignById(recruitmentID);
        if (campaign == null) {
            return false;
        }
        
        Date currentDate = new Date();
        return "ONGOING".equals(campaign.getStatus()) && 
               !currentDate.before(campaign.getStartDate()) && 
               !currentDate.after(campaign.getEndDate());
    }
    
    // Check if a stage is active (current date is within range)
    public boolean isStageActive(int stageID) {
        RecruitmentStage stage = stageDAO.getStageById(stageID);
        if (stage == null) {
            return false;
        }
        
        Date currentDate = new Date();
        return !currentDate.before(stage.getStartDate()) && 
               !currentDate.after(stage.getEndDate());
    }
}
