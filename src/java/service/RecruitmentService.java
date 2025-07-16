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

    // Các phương thức quản lý chiến dịch tuyển quân
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

    public boolean updateCampaign(RecruitmentCampaign campaign) throws IllegalStateException {
        RecruitmentCampaign existingCampaign = campaignDAO.getRecruitmentCampaignById(campaign.getRecruitmentID());

        if (existingCampaign == null) {
            throw new IllegalStateException("Không tìm thấy hoạt động tuyển quân cần cập nhật");
        }

        // Kiểm tra nếu chiến dịch đang diễn ra và ngày bắt đầu bị thay đổi
        // Sử dụng compareTo để so sánh chính xác thời gian thay vì equals
        if ("ONGOING".equals(existingCampaign.getStatus())
                && existingCampaign.getStartDate().compareTo(campaign.getStartDate()) != 0) {
            throw new IllegalStateException("Không thể thay đổi ngày bắt đầu khi chiến dịch đang diễn ra");
        }

        // Kiểm tra nếu templateId bị thay đổi khi đã có đơn đăng ký
        if (existingCampaign.getTemplateID() != campaign.getTemplateID()) {
            if (stageDAO.hasApplicationResponses(campaign.getRecruitmentID())) {
                throw new IllegalStateException("Không thể thay đổi mẫu đơn khi đã có đơn đăng ký");
            }
        }

        // Xác thực chồng lấp thời gian (loại trừ chiến dịch này)
        if (campaignDAO.hasCampaignTimeOverlap(campaign.getClubID(), campaign.getStartDate(),
                campaign.getEndDate(), campaign.getRecruitmentID())) {
            throw new IllegalStateException("Thời gian hoạt động trùng với hoạt động tuyển quân khác của câu lạc bộ");
        }

        boolean result = campaignDAO.updateRecruitmentCampaign(campaign);
        if (!result) {
            throw new IllegalStateException("Lỗi cơ sở dữ liệu khi cập nhật hoạt động tuyển quân");
        }

        return result;
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

    // Các phương thức quản lý vòng tuyển
    public RecruitmentStage getStageById(int stageID) {
        return stageDAO.getStageById(stageID);
    }

    public List<RecruitmentStage> getStagesByCampaign(int recruitmentID) {
        return stageDAO.getStagesByRecruitmentId(recruitmentID);
    }

    public int createStage(RecruitmentStage stage) {
        // Xác thực chồng lấp thời gian trong chiến dịch
        if (stageDAO.hasStageTimeOverlap(stage.getRecruitmentID(), stage.getStartDate(), stage.getEndDate(), null)) {
            return -1; // Báo lỗi chồng lấp thời gian
        }

        // Xác thực ngày vòng tuyển nằm trong khoảng thời gian chiến dịch
        RecruitmentCampaign campaign = campaignDAO.getRecruitmentCampaignById(stage.getRecruitmentID());
        if (stage.getStartDate().before(campaign.getStartDate())
                || stage.getEndDate().after(campaign.getEndDate())) {
            return -2; // Báo lỗi ngày nằm ngoài phạm vi chiến dịch
        }

        return stageDAO.createRecruitmentStage(stage);
    }

    public boolean updateStage(RecruitmentStage stage) {
        // Lấy vòng tuyển hiện có để kiểm tra trạng thái và thay đổi ngày bắt đầu
        RecruitmentStage existingStage = stageDAO.getStageById(stage.getStageID());
        if (existingStage == null) {
            return false;
        }

        // Kiểm tra nếu vòng tuyển đã bắt đầu và ngày bắt đầu bị thay đổi
        Date currentDate = new Date();
        boolean stageHasStarted = !currentDate.before(existingStage.getStartDate());

        if (stageHasStarted && !existingStage.getStartDate().equals(stage.getStartDate())) {
            return false; // Không thể thay đổi ngày bắt đầu cho vòng tuyển đã diễn ra
        }

        // Xác thực chồng lấp thời gian (loại trừ vòng tuyển này)
        if (stageDAO.hasStageTimeOverlap(stage.getRecruitmentID(), stage.getStartDate(),
                stage.getEndDate(), stage.getStageID())) {
            return false; // Báo lỗi chồng lấp thời gian
        }

        // Xác thực ngày vòng tuyển nằm trong khoảng thời gian chiến dịch
        RecruitmentCampaign campaign = campaignDAO.getRecruitmentCampaignById(stage.getRecruitmentID());
        if (stage.getStartDate().before(campaign.getStartDate())
                || stage.getEndDate().after(campaign.getEndDate())) {
            return false; // Báo lỗi ngày nằm ngoài phạm vi chiến dịch
        }

        return stageDAO.updateRecruitmentStage(stage);
    }

    public boolean updateStageStatus(int stageID, String status) {
        return stageDAO.updateStageStatus(stageID, status);
    }

    // Các phương thức quản lý giai đoạn đơn đăng ký
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

    // Các phương thức hỗ trợ
    // Kiểm tra xem chiến dịch tuyển quân có đang hoạt động không (không đóng và ngày hiện tại nằm trong khoảng thời gian)
    public boolean isCampaignActive(int recruitmentID) {
        RecruitmentCampaign campaign = campaignDAO.getRecruitmentCampaignById(recruitmentID);
        if (campaign == null) {
            return false;
        }

        Date currentDate = new Date();
        return "ONGOING".equals(campaign.getStatus())
                && !currentDate.before(campaign.getStartDate())
                && !currentDate.after(campaign.getEndDate());
    }

    // Kiểm tra xem một vòng tuyển có đang hoạt động không (ngày hiện tại nằm trong khoảng thời gian)
    public boolean isStageActive(int stageID) {
        RecruitmentStage stage = stageDAO.getStageById(stageID);
        if (stage == null) {
            return false;
        }

        Date currentDate = new Date();
        return !currentDate.before(stage.getStartDate())
                && !currentDate.after(stage.getEndDate());
    }

    // Đồng bộ tất cả trạng thái vòng tuyển dựa trên ngày hiện tại
    public int syncAllStageStatus() {
        return stageDAO.syncAllStagesStatus();
    }

    //Đếm số lượng đơn đăng ký ứng viên
    public int countApplicationResponses(int recruitmentID) {
        return stageDAO.countApplicationResponses(recruitmentID);
    }
}
