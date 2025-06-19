
package models;

public class EventParticipant {
    private int eventParticipantID;
    private int eventID;    private String userID;
    private String status; // ENUM('REGISTERED', 'ATTENDED', 'ABSENT') - Đăng ký, Có mặt, Vắng mặt

    public EventParticipant() {
    }    public EventParticipant(int eventParticipantID, int eventID, String userID, String status) {
        this.eventParticipantID = eventParticipantID;
        this.eventID = eventID;
        this.userID = userID;
        this.status = status;
    }

    public EventParticipant(int eventID, String userID) {
        this.eventID = eventID;
        this.userID = userID;
        this.status = "REGISTERED"; // Mặc định là REGISTERED (đã đăng ký) cho người tham gia mới
    }

    public int getEventParticipantID() {
        return eventParticipantID;
    }

    public void setEventParticipantID(int eventParticipantID) {
        this.eventParticipantID = eventParticipantID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getStatus() {
        return status;
    }    public void setStatus(String status) {        // Kiểm tra và chuyển đổi về chữ in hoa để đảm bảo tính nhất quán
        if (status != null) {
            status = status.toUpperCase();
            // Xác thực enum status
            if (status.equals("REGISTERED") || status.equals("ATTENDED") || status.equals("ABSENT")) {
                this.status = status;
            } else {
                throw new IllegalArgumentException("Trạng thái không hợp lệ. Phải là một trong: REGISTERED, ATTENDED, ABSENT");
            }
        } else {
            throw new IllegalArgumentException("Trạng thái không được để trống");
        }
    }

    @Override
    public String toString() {
        return "EventParticipant{" + "eventParticipantID=" + eventParticipantID + ", eventID=" + eventID + ", userID=" + userID + ", status=" + status + '}';
    }


}
