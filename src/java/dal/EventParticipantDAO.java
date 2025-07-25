package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.EventParticipant;
import models.EventParticipation;
import models.EventScheduleDetail;

public class EventParticipantDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(EventParticipantDAO.class.getName());

    //Thêm người tham gia vào sự kiện với trạng thái REGISTERED
    public boolean registerUserForEvent(int eventId, String userId) {
        // Kiểm tra xem người dùng đã đăng ký chưa
        if (isUserRegisteredForEvent(userId, eventId)) {
            LOGGER.info("User " + userId + " is already registered for event " + eventId);
            return true; // Người dùng đã đăng ký rồi, coi như thành công
        }

        String sql = "INSERT INTO EventParticipants (EventID, UserID, Status) VALUES (?, ?, 'REGISTERED')";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setString(2, userId);

            int result = ps.executeUpdate();
            if (result > 0) {
                LOGGER.info("User " + userId + " successfully registered for event " + eventId);
                return true;
            } else {
                LOGGER.warning("Failed to register user " + userId + " for event " + eventId);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error registering user for event", e);
            return false;
        }
    }
    //Kiểm tra xem người dùng đã đăng ký tham gia sự kiện chưa

    public boolean isUserRegisteredForEvent(String userId, int eventId) {
        String sql = "SELECT COUNT(*) FROM EventParticipants WHERE EventID = ? AND UserID = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setString(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if user is registered for event", e);
        }
        return false;
    }
    //Lấy tất cả người tham gia cho một sự kiện

    public List<EventParticipant> getEventParticipants(int eventId) {
        List<EventParticipant> participants = new ArrayList<>();
        String sql = "SELECT * FROM EventParticipants WHERE EventID = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EventParticipant participant = new EventParticipant(
                            rs.getInt("EventParticipantID"),
                            rs.getInt("EventID"),
                            rs.getString("UserID"),
                            rs.getString("Status")
                    );
                    participants.add(participant);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting event participants", e);
        }
        return participants;
    }
    //Cập nhật trạng thái người tham gia (ví dụ: từ REGISTERED sang ATTENDED)

    public boolean updateParticipantStatus(int eventId, String userId, String newStatus) {
        // Xác thực status trước khi update
        newStatus = newStatus.toUpperCase();
        if (!newStatus.equals("REGISTERED") && !newStatus.equals("ATTENDED") && !newStatus.equals("ABSENT")) {
            LOGGER.warning("Invalid status: " + newStatus);
            return false;
        }

        String sql = "UPDATE EventParticipants SET Status = ? WHERE EventID = ? AND UserID = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, eventId);
            ps.setString(3, userId);

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating participant status", e);
            return false;
        }
    }

    /**
     * Xóa người tham gia khỏi sự kiện
     *
     * @param eventId ID của sự kiện
     * @param userId ID của người dùng
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean removeParticipant(int eventId, String userId) {
        String sql = "DELETE FROM EventParticipants WHERE EventID = ? AND UserID = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setString(2, userId);

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing event participant", e);
            return false;
        }
    }

    /**
     * Đếm số người tham gia sự kiện theo trạng thái cụ thể
     *
     * @param eventId ID của sự kiện
     * @param status Trạng thái cần đếm (REGISTERED, ATTENDED, ABSENT)
     * @return Số lượng người tham gia có trạng thái đã chọn
     */
    public int countParticipantsByStatus(int eventId, String status) {
        // Xác thực status
        status = status.toUpperCase();
        if (!status.equals("REGISTERED") && !status.equals("ATTENDED") && !status.equals("ABSENT")) {
            LOGGER.warning("Invalid status: " + status);
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM EventParticipants WHERE EventID = ? AND Status = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setString(2, status);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting participants by status", e);
        }
        return 0;
    }

    public List<EventParticipation> getEventParticipationByUser(String userID) {
        List<EventParticipation> list = new ArrayList<>();

        String sqlEvent = """
        SELECT 
            e.EventID, 
            e.EventName, 
            c.ClubName, 
            ep.Status AS ParticipationStatus, 
            e.Status AS EventStatus
        FROM EventParticipants ep
        JOIN Events e ON ep.EventID = e.EventID
        JOIN Clubs c ON e.ClubID = c.ClubID
        WHERE ep.UserID = ?
    """;

        String sqlSchedule = """
        SELECT es.EventDate, es.StartTime, es.EndTime, l.LocationName
        FROM EventSchedules es
        JOIN Locations l ON es.LocationID = l.LocationID
        WHERE es.EventID = ?
        ORDER BY es.EventDate, es.StartTime
    """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement psEvent = conn.prepareStatement(sqlEvent)) {
            psEvent.setString(1, userID);
            ResultSet rsEvent = psEvent.executeQuery();

            while (rsEvent.next()) {
                EventParticipation ep = new EventParticipation();
                int eventID = rsEvent.getInt("EventID");

                ep.setEventID(String.valueOf(eventID));
                ep.setEventName(rsEvent.getString("EventName"));
                ep.setClubName(rsEvent.getString("ClubName"));
                ep.setParticipationStatus(rsEvent.getString("ParticipationStatus"));
                ep.setEventStatus(rsEvent.getString("EventStatus"));

                // Truy vấn danh sách lịch trình cho event
                try (PreparedStatement psSchedule = conn.prepareStatement(sqlSchedule)) {
                    psSchedule.setInt(1, eventID);
                    ResultSet rsSchedule = psSchedule.executeQuery();

                    List<EventScheduleDetail> scheduleList = new ArrayList<>();
                    while (rsSchedule.next()) {
                        EventScheduleDetail detail = new EventScheduleDetail();
                        detail.setEventDate(rsSchedule.getDate("EventDate"));
                        detail.setStartTime(rsSchedule.getString("StartTime"));
                        detail.setEndTime(rsSchedule.getString("EndTime"));
                        detail.setLocationName(rsSchedule.getString("LocationName"));
                        scheduleList.add(detail);
                    }
                    ep.setScheduleList(scheduleList);
                }

                list.add(ep);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean hasUserParticipatedInEvent(String userId, int eventId) {
        String sql = "SELECT COUNT(*) FROM EventParticipants WHERE EventID = ? AND UserID = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setString(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if user participated in event", e);
        }
        return false;
    }
}
