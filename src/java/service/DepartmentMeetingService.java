package service;

import dal.DepartmentMeetingDAO;
import java.util.List;
import models.DepartmentMeeting;
import models.DepartmentMeetingParticipant;

/**
 * Service layer for department meeting management
 */
public class DepartmentMeetingService {

    private final DepartmentMeetingDAO departmentMeetingDAO;
    
    public DepartmentMeetingService() {
        this.departmentMeetingDAO = new DepartmentMeetingDAO();
    }
    
    /**
     * Lấy danh sách cuộc họp theo department
     * @param departmentID ID của ban
     * @return Danh sách cuộc họp
     */
    public List<DepartmentMeeting> getMeetingsByDepartment(int departmentID) {
        return departmentMeetingDAO.getMeetingsByDepartment(departmentID);
    }
    
    /**
     * Lấy thông tin chi tiết cuộc họp
     * @param meetingID ID của cuộc họp
     * @return Thông tin chi tiết cuộc họp hoặc null nếu không tìm thấy
     */
    public DepartmentMeeting getMeetingById(int meetingID) {
        return departmentMeetingDAO.getMeetingById(meetingID);
    }
    
    /**
     * Tạo mới cuộc họp
     * @param meeting Thông tin cuộc họp cần tạo
     * @return ID của cuộc họp mới tạo, hoặc -1 nếu có lỗi
     */
    public int createMeeting(DepartmentMeeting meeting) {
        return departmentMeetingDAO.createMeeting(meeting);
    }
    
    /**
     * Cập nhật thông tin cuộc họp
     * @param meeting Thông tin cuộc họp cần cập nhật
     * @return true nếu cập nhật thành công, false nếu có lỗi
     */
    public boolean updateMeeting(DepartmentMeeting meeting) {
        return departmentMeetingDAO.updateMeeting(meeting);
    }
    
    /**
     * Xóa cuộc họp
     * @param meetingID ID của cuộc họp cần xóa
     * @return true nếu xóa thành công, false nếu có lỗi
     */
    public boolean deleteMeeting(int meetingID) {
        return departmentMeetingDAO.deleteMeeting(meetingID);
    }
    
    /**
     * Lấy danh sách người tham gia cuộc họp
     * @param meetingID ID của cuộc họp
     * @return Danh sách người tham gia
     */
    public List<DepartmentMeetingParticipant> getParticipantsByMeetingId(int meetingID) {
        return departmentMeetingDAO.getParticipantsByMeetingId(meetingID);
    }
    
    /**
     * Thêm người tham gia vào cuộc họp
     * @param participant Thông tin người tham gia
     * @return ID của người tham gia mới, hoặc -1 nếu có lỗi
     */
    public int addParticipant(DepartmentMeetingParticipant participant) {
        return departmentMeetingDAO.addParticipant(participant);
    }
    
    /**
     * Cập nhật trạng thái người tham gia cuộc họp
     * @param participantID ID của người tham gia
     * @param status Trạng thái mới
     * @return true nếu cập nhật thành công, false nếu có lỗi
     */
    public boolean updateParticipantStatus(int participantID, String status) {
        return departmentMeetingDAO.updateParticipantStatus(participantID, status);
    }
    
    /**
     * Xóa người tham gia khỏi cuộc họp
     * @param participantID ID của người tham gia cần xóa
     * @return true nếu xóa thành công, false nếu có lỗi
     */
    public boolean removeParticipant(int participantID) {
        return departmentMeetingDAO.removeParticipant(participantID);
    }
    
    /**
     * Kiểm tra xem người dùng đã tham gia cuộc họp chưa
     * @param meetingID ID của cuộc họp
     * @param userID ID của người dùng
     * @return true nếu đã tham gia, false nếu chưa
     */
    public boolean isUserParticipant(int meetingID, int userID) {
        return departmentMeetingDAO.isUserParticipant(meetingID, userID);
    }
    
    /**
     * Thêm nhiều người tham gia vào cuộc họp
     * @param meetingID ID của cuộc họp
     * @param userIDs Danh sách ID người dùng
     * @param status Trạng thái ban đầu của người tham gia
     * @return Số người tham gia được thêm thành công
     */
    public int addMultipleParticipants(int meetingID, List<Integer> userIDs, String status) {
        int successCount = 0;
        
        for (Integer userID : userIDs) {
            DepartmentMeetingParticipant participant = new DepartmentMeetingParticipant();
            participant.setMeetingID(meetingID);
            participant.setUserID(userID);
            participant.setStatus(status);
            
            int result = addParticipant(participant);
            if (result > 0) {
                successCount++;
            }
        }
        
        return successCount;
    }
    
    /**
     * Đếm tổng số cuộc họp của một ban
     * @param departmentID ID của ban
     * @return Số lượng cuộc họp
     */
    public int countMeetings(int departmentID) {
        return departmentMeetingDAO.countMeetings(departmentID);
    }
    
    /**
     * Đếm số cuộc họp sắp diễn ra của một ban
     * @param departmentID ID của ban
     * @return Số lượng cuộc họp sắp diễn ra
     */
    public int countUpcomingMeetings(int departmentID) {
        return departmentMeetingDAO.countUpcomingMeetings(departmentID);
    }
    
    /**
     * Lấy danh sách các cuộc họp gần đây của một ban
     * @param departmentID ID của ban
     * @param limit Số lượng cuộc họp tối đa cần lấy
     * @return Danh sách các cuộc họp gần đây
     */
    public List<DepartmentMeeting> getRecentMeetings(int departmentID, int limit) {
        return departmentMeetingDAO.getRecentMeetings(departmentID, limit);
    }
}
