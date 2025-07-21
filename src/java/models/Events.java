package models;
import java.util.List;

public class Events {
    private int eventID;
    private String eventName;
    private String eventImg;
    private String description;
    private int clubID;
    private boolean isPublic;
    private Integer formID;
    private int capacity;
    private String status;
    private String approvalStatus;
    private String rejectionReason;
    private String semesterID;

    // Thêm danh sách lịch trình
    private List<EventSchedule> schedules; // Danh sách các lịch trình của sự kiện

    // Các trường bổ sung cho hiển thị
    private String clubName;
    private String clubImg;
    private int registered;
    private int spotsLeft;
    private int agendaCount;

    // Constructor mặc định
    public Events() {
    }

    // Constructor đầy đủ
    public Events(int eventID, String eventName, String eventImg, String description, int clubID,
                  boolean isPublic, Integer formID, int capacity, String status,
                  String approvalStatus, String rejectionReason, String semesterID,
                  List<EventSchedule> schedules, String clubName, String clubImg,
                  int registered, int spotsLeft, int agendaCount) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventImg = eventImg;
        this.description = description;
        this.clubID = clubID;
        this.isPublic = isPublic;
        this.formID = formID;
        this.capacity = capacity;
        this.status = status;
        this.approvalStatus = approvalStatus;
        this.rejectionReason = rejectionReason;
        this.semesterID = semesterID;
        this.schedules = schedules;
        this.clubName = clubName;
        this.clubImg = clubImg;
        this.registered = registered;
        this.spotsLeft = spotsLeft;
        this.agendaCount = agendaCount;
    }

    // Getters và Setters
    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventImg() {
        return eventImg;
    }

    public void setEventImg(String eventImg) {
        this.eventImg = eventImg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Integer getFormID() {
        return formID;
    }

    public void setFormID(Integer formID) {
        this.formID = formID;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getSemesterID() {
        return semesterID;
    }

    public void setSemesterID(String semesterID) {
        this.semesterID = semesterID;
    }

    public List<EventSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<EventSchedule> schedules) {
        this.schedules = schedules;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubImg() {
        return clubImg;
    }

    public void setClubImg(String clubImg) {
        this.clubImg = clubImg;
    }

    public int getRegistered() {
        return registered;
    }

    public void setRegistered(int registered) {
        this.registered = registered;
    }

    public int getSpotsLeft() {
        return spotsLeft;
    }

    public void setSpotsLeft(int spotsLeft) {
        this.spotsLeft = spotsLeft;
    }

    public int getAgendaCount() {
        return agendaCount;
    }

    public void setAgendaCount(int agendaCount) {
        this.agendaCount = agendaCount;
    }
}