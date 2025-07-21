package dal;

import models.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LE VAN THUAN
 */
public class EventsDAO {

    public EventsDAO() {
    }

    public List<Events> findByUCID(String userID) {
        List<Events> events = new ArrayList<>();
        String sql = """
                     SELECT DISTINCT e.*, c.ClubName, c.ClubImg
                     FROM Events e
                     JOIN UserClubs uc ON e.ClubID = uc.ClubID
                     JOIN Clubs c ON e.ClubID = c.ClubID
                     WHERE uc.UserID = ? AND EXISTS (
                         SELECT 1 FROM EventSchedules es WHERE es.EventID = e.EventID AND es.EventDate >= NOW()
                     )
                     ORDER BY e.EventID ASC""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Events event = new Events();
                event.setEventID(rs.getInt("EventID"));
                event.setEventName(rs.getString("EventName"));
                event.setEventImg(rs.getString("EventImg"));
                event.setDescription(rs.getString("Description"));
                event.setClubID(rs.getInt("ClubID"));
                event.setPublic(rs.getBoolean("IsPublic"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));
                event.setClubName(rs.getString("ClubName"));
                event.setClubImg(rs.getString("ClubImg"));
                event.setSchedules(getSchedulesByEventID(rs.getInt("EventID")));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public Locations getLocationByID(int id) {
        String sql = "SELECT * FROM Locations WHERE LocationID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Locations location = new Locations();
                location.setLocationID(rs.getInt("LocationID"));
                location.setLocationName(rs.getString("LocationName"));
                location.setTypeLocation(rs.getString("TypeLocation"));
                return location;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving location by ID", e);
        }
        return null;
    }

    public List<Events> getAllEvents() {
        List<Events> events = new ArrayList<>();
        String sql = "SELECT e.*, c.ClubName, c.ClubImg FROM Events e JOIN Clubs c ON e.ClubID = c.ClubID";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Events event = new Events();
                event.setEventID(rs.getInt("EventID"));
                event.setEventName(rs.getString("EventName"));
                event.setEventImg(rs.getString("EventImg"));
                event.setDescription(rs.getString("Description"));
                event.setClubID(rs.getInt("ClubID"));
                event.setPublic(rs.getBoolean("IsPublic"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));
                event.setClubName(rs.getString("ClubName"));
                event.setClubImg(rs.getString("ClubImg"));
                event.setSchedules(getSchedulesByEventID(rs.getInt("EventID")));
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Events size: " + events.size());
        return events;
    }


    public List<EventSchedule> getSchedulesByEventID(int eventID) {
        List<EventSchedule> schedules = new ArrayList<>();
        String sql = """
                SELECT es.*, l.LocationName, l.TypeLocation
                FROM EventSchedules es
                JOIN Locations l ON es.LocationID = l.LocationID
                WHERE es.EventID = ?""";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EventSchedule schedule = new EventSchedule();
                    schedule.setScheduleID(rs.getInt("ScheduleID"));
                    schedule.setEventID(rs.getInt("EventID"));
                    schedule.setEventDate(rs.getDate("EventDate"));
                    schedule.setLocationID(rs.getInt("LocationID"));
                    schedule.setStartTime(rs.getTime("StartTime"));
                    schedule.setEndTime(rs.getTime("EndTime"));

                    Locations location = new Locations();
                    location.setLocationID(rs.getInt("LocationID"));
                    location.setLocationName(rs.getString("LocationName"));
                    location.setTypeLocation(rs.getString("TypeLocation"));
                    schedule.setLocation(location);

                    schedules.add(schedule);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách lịch trình cho eventID " + eventID + ": " + e.getMessage(), e);
        }
        return schedules;
    }

    public Events getEventByID(int id) {
        String sql = """
                SELECT e.*, c.ClubName, c.ClubImg 
                FROM Events e 
                JOIN Clubs c ON e.ClubID = c.ClubID 
                WHERE e.EventID = ?""";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Events event = new Events();
                    event.setEventID(rs.getInt("EventID"));
                    event.setEventName(rs.getString("EventName"));
                    event.setEventImg(rs.getString("EventImg"));
                    event.setDescription(rs.getString("Description"));
                    event.setClubID(rs.getInt("ClubID"));
                    event.setPublic(rs.getBoolean("IsPublic"));
                    event.setFormID(rs.getInt("FormID") == 0 ? null : rs.getInt("FormID"));
                    event.setCapacity(rs.getInt("Capacity"));
                    event.setStatus(rs.getString("Status"));
                    event.setApprovalStatus(rs.getString("ApprovalStatus"));
                    event.setRejectionReason(rs.getString("RejectionReason"));
                    event.setSemesterID(rs.getString("SemesterID"));
                    event.setClubName(rs.getString("ClubName"));
                    event.setClubImg(rs.getString("ClubImg"));
                    event.setSchedules(getSchedulesByEventID(id));
                    event.setAgendaCount(getAgendaCountByEventID(id));
                    EventStats stats = getSpotsLeftEvent(id);
                    event.setRegistered(stats.getRegisteredCount());
                    event.setSpotsLeft(stats.getSpotsLeft());
                    return event;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy thông tin sự kiện: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Events> getEventsByClubIdForTask(int clubID) {
        List<Events> events = new ArrayList<>();
        String sql = "SELECT e.*, c.ClubName, c.ClubImg FROM Events e JOIN Clubs c ON e.ClubID = c.ClubID " +
                     "WHERE e.ClubID = ? ORDER BY e.EventID DESC";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Events event = new Events();
                event.setEventID(rs.getInt("EventID"));
                event.setEventName(rs.getString("EventName"));
                event.setEventImg(rs.getString("EventImg"));
                event.setDescription(rs.getString("Description"));
                event.setClubID(rs.getInt("ClubID"));
                event.setPublic(rs.getBoolean("IsPublic"));
                event.setFormID(rs.getInt("FormID") == 0 ? null : rs.getInt("FormID"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));
                event.setClubName(rs.getString("ClubName"));
                event.setClubImg(rs.getString("ClubImg"));
                event.setSchedules(getSchedulesByEventID(rs.getInt("EventID")));
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return events;
    }

    public List<Events> getEventsByClubID(int clubID) {
        List<Events> events = new ArrayList<>();
        String sql = """
                    SELECT e.*, c.ClubName, c.ClubImg,
                           COUNT(ep.EventParticipantID) AS RegisteredCount,
                           (e.Capacity - COUNT(ep.EventParticipantID)) AS SpotsLeft
                    FROM Events e
                    JOIN Clubs c ON e.ClubID = c.ClubID
                    LEFT JOIN EventParticipants ep 
                        ON e.EventID = ep.EventID 
                        AND (ep.Status = 'REGISTERED' OR ep.Status = 'ATTENDED' OR ep.Status = 'ABSENT')
                    WHERE e.ClubID = ?
                    GROUP BY e.EventID, e.EventName, e.EventImg, e.Description, 
                             e.ClubID, e.IsPublic, e.FormID, e.Capacity, e.Status, 
                             e.ApprovalStatus, e.RejectionReason, e.SemesterID, 
                             c.ClubName, c.ClubImg
                    ORDER BY e.EventID DESC""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Events event = new Events();
                event.setEventID(rs.getInt("EventID"));
                event.setEventName(rs.getString("EventName"));
                event.setEventImg(rs.getString("EventImg"));
                event.setDescription(rs.getString("Description"));
                event.setClubID(rs.getInt("ClubID"));
                event.setPublic(rs.getBoolean("IsPublic"));
                event.setFormID(rs.getInt("FormID") == 0 ? null : rs.getInt("FormID"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));
                event.setApprovalStatus(rs.getString("ApprovalStatus"));
                event.setRejectionReason(rs.getString("RejectionReason"));
                event.setSemesterID(rs.getString("SemesterID"));
                event.setClubName(rs.getString("ClubName"));
                event.setClubImg(rs.getString("ClubImg"));
                event.setRegistered(rs.getInt("RegisteredCount"));
                event.setSpotsLeft(rs.getInt("SpotsLeft"));
                event.setSchedules(getSchedulesByEventID(rs.getInt("EventID")));
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return events;
    }

    public List<Events> getEventsByClubID(int clubID, int eventID) {
        List<Events> events = new ArrayList<>();
        String sql = """
                    SELECT e.*, c.ClubName, c.ClubImg
                    FROM Events e
                    JOIN Clubs c ON e.ClubID = c.ClubID
                    WHERE e.ClubID = ? AND e.EventID <> ? AND e.ApprovalStatus = 'APPROVED'
                    ORDER BY e.EventID DESC LIMIT 3""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ps.setInt(2, eventID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Events event = new Events();
                event.setEventID(rs.getInt("EventID"));
                event.setEventName(rs.getString("EventName"));
                event.setEventImg(rs.getString("EventImg"));
                event.setDescription(rs.getString("Description"));
                event.setClubID(rs.getInt("ClubID"));
                event.setPublic(rs.getBoolean("IsPublic"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));
                event.setClubName(rs.getString("ClubName"));
                event.setClubImg(rs.getString("ClubImg"));
                event.setSchedules(getSchedulesByEventID(rs.getInt("EventID")));
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return events;
    }

    public List<Events> searchEvents(String keyword, String publicFilter, String sortByDate, int limit, int offset) {
        List<Events> events = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
        SELECT e.*, c.ClubName, c.ClubImg, MIN(es.EventDate) AS FirstEventDate
        FROM Events e
        JOIN Clubs c ON e.ClubID = c.ClubID
        JOIN EventSchedules es ON e.EventID = es.EventID
        WHERE e.ApprovalStatus = 'APPROVED'
    """);

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND e.EventName LIKE ?");
        }

        if (publicFilter != null && !publicFilter.equalsIgnoreCase("all")) {
            sql.append(publicFilter.equalsIgnoreCase("public") ? " AND e.IsPublic = TRUE" : " AND e.IsPublic = FALSE");
        }

        sql.append(" GROUP BY e.EventID");

        if ("oldest".equalsIgnoreCase(sortByDate)) {
            sql.append(" ORDER BY FirstEventDate ASC");
        } else {
            sql.append(" ORDER BY FirstEventDate DESC");
        }

        sql.append(" LIMIT ? OFFSET ?");

        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword.trim() + "%");
            }
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Events event = new Events();
                event.setEventID(rs.getInt("EventID"));
                event.setEventName(rs.getString("EventName"));
                event.setEventImg(rs.getString("EventImg"));
                event.setDescription(rs.getString("Description"));
                event.setClubID(rs.getInt("ClubID"));
                event.setPublic(rs.getBoolean("IsPublic"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));
                event.setClubName(rs.getString("ClubName"));
                event.setClubImg(rs.getString("ClubImg"));
                event.setSchedules(getSchedulesByEventID(rs.getInt("EventID")));
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching events: " + e.getMessage(), e);
        }

        return events;
    }


    public int countEvents(String keyword, String publicFilter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Events WHERE ApprovalStatus = 'APPROVED'");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND EventName LIKE ?");
        }
        if (publicFilter != null && !publicFilter.equalsIgnoreCase("all")) {
            sql.append(publicFilter.equalsIgnoreCase("public") ? " AND IsPublic = TRUE" : " AND IsPublic = FALSE");
        }

        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting events: " + e.getMessage(), e);
        }
        return 0;
    }

    public EventStats getSpotsLeftEvent(int eventID) {
        String sql = """
                    SELECT (e.Capacity - COUNT(ep.EventParticipantID)) AS SpotsLeft, 
                           COUNT(ep.EventParticipantID) AS RegisteredCount
                    FROM Events e
                    LEFT JOIN EventParticipants ep 
                        ON e.EventID = ep.EventID 
                        AND (ep.Status = 'REGISTERED' OR ep.Status = 'ATTENDED' OR ep.Status = 'ABSENT')
                    WHERE e.EventID = ?
                    GROUP BY e.Capacity""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new EventStats(rs.getInt("SpotsLeft"), rs.getInt("RegisteredCount"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting spots: " + e.getMessage(), e);
        }
        return new EventStats(0, 0);
    }

    public boolean registerParticipant(int eventID, String userID) {
        String sql = "INSERT INTO EventParticipants (EventID, UserID, Status) VALUES (?, ?, 'REGISTERED')";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventID);
            ps.setString(2, userID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUserRegistered(int eventID, String userID) {
        String sql = "SELECT * FROM EventParticipants WHERE EventID = ? AND UserID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventID);
            ps.setString(2, userID);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public EventOwnerInfo getEventOwnerInfo(int eventID) {
        String sql = """
                    SELECT u.FullName, u.Email, c.ClubName 
                    FROM Events e
                    JOIN Clubs c ON e.ClubID = c.ClubID
                    JOIN UserClubs uc ON uc.ClubID = c.ClubID
                    JOIN Users u ON uc.UserID = u.UserID
                    WHERE uc.RoleID = 1 AND e.EventID = ?""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new EventOwnerInfo(rs.getString("FullName"), rs.getString("Email"), rs.getString("ClubName"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving event owner info: " + e.getMessage(), e);
        }
        return null;
    }

    public int getTotalEvents(int clubID) {
        String sql = "SELECT COUNT(*) FROM Events WHERE ClubID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countUpcomingEvents(int clubID) {
        String sql = """
                    SELECT COUNT(*) 
                    FROM Events e
                    WHERE e.ClubID = ? AND EXISTS (
                        SELECT 1 FROM EventSchedules es WHERE es.EventID = e.EventID AND es.EventDate > NOW()
                    )""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countOngoingEvents(int clubID) {
        String sql = """
                    SELECT COUNT(*) 
                    FROM Events e
                    WHERE e.ClubID = ? AND EXISTS (
                        SELECT 1 FROM EventSchedules es 
                        WHERE es.EventID = e.EventID AND es.EventDate <= NOW() AND es.EndTime >= NOW()
                    )""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countPastEvents(int clubID) {
        String sql = """
                    SELECT COUNT(*) 
                    FROM Events e
                    WHERE e.ClubID = ? AND EXISTS (
                        SELECT 1 FROM EventSchedules es WHERE es.EventID = e.EventID AND es.EndTime < NOW()
                    )""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String checkTimeConflict(int locationId, Timestamp startTime, Timestamp endTime) {
        String sql = """
                    SELECT e.EventID, e.EventName 
                    FROM Events e
                    JOIN EventSchedules es ON e.EventID = es.EventID
                    WHERE es.LocationID = ? AND (
                        (es.EventDate < ? AND es.EndTime > ?) OR
                        (es.EventDate < ? AND es.EndTime > ?) OR
                        (es.EventDate >= ? AND es.EventDate <= ?)
                    )""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, locationId);
            ps.setTimestamp(2, endTime);
            ps.setTimestamp(3, startTime);
            ps.setTimestamp(4, endTime);
            ps.setTimestamp(5, startTime);
            ps.setTimestamp(6, startTime);
            ps.setTimestamp(7, endTime);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "Địa điểm đã được sử dụng bởi sự kiện '" + rs.getString("EventName") + "' trong khoảng thời gian này.";
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Phiên bản cho AddEventServlet (không cần eventID)
    public boolean isLocationConflictAdd(int locationId, Timestamp start, Timestamp end) {
        String sql = """
                SELECT COUNT(*) 
                FROM EventSchedules es
                WHERE es.LocationID = ? 
                AND (
                    (TIMESTAMP(es.EventDate, es.StartTime) <= ? AND TIMESTAMP(es.EventDate, es.EndTime) >= ?) OR
                    (TIMESTAMP(es.EventDate, es.StartTime) <= ? AND TIMESTAMP(es.EventDate, es.EndTime) >= ?) OR
                    (? <= TIMESTAMP(es.EventDate, es.EndTime) AND ? >= TIMESTAMP(es.EventDate, es.StartTime))
                )""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, locationId);
            ps.setTimestamp(2, end);   // Kiểm tra: start của lịch trình mới <= end của lịch trình cũ
            ps.setTimestamp(3, start); // Kiểm tra: end của lịch trình mới >= start của lịch trình cũ
            ps.setTimestamp(4, end);
            ps.setTimestamp(5, start);
            ps.setTimestamp(6, start);
            ps.setTimestamp(7, end);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra trùng địa điểm: " + e.getMessage(), e);
        }
        return false;
    }

    // Phiên bản cho EditEventServlet (có eventID để loại trừ lịch trình của sự kiện hiện tại)
    public boolean isLocationConflict(int eventID, int locationId, Timestamp start, Timestamp end) {
        String sql = """
                SELECT COUNT(*) 
                FROM EventSchedules es
                WHERE es.LocationID = ? 
                AND es.EventID != ? 
                AND (
                    (TIMESTAMP(es.EventDate, es.StartTime) <= ? AND TIMESTAMP(es.EventDate, es.EndTime) >= ?) OR
                    (TIMESTAMP(es.EventDate, es.StartTime) <= ? AND TIMESTAMP(es.EventDate, es.EndTime) >= ?) OR
                    (? <= TIMESTAMP(es.EventDate, es.EndTime) AND ? >= TIMESTAMP(es.EventDate, es.StartTime))
                )""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, locationId);
            ps.setInt(2, eventID); // Loại trừ lịch trình của sự kiện hiện tại
            ps.setTimestamp(3, end);   // Kiểm tra: start của lịch trình mới <= end của lịch trình cũ
            ps.setTimestamp(4, start); // Kiểm tra: end của lịch trình mới >= start của lịch trình cũ
            ps.setTimestamp(5, end);
            ps.setTimestamp(6, start);
            ps.setTimestamp(7, start);
            ps.setTimestamp(8, end);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra trùng địa điểm: " + e.getMessage(), e);
        }
        return false;
    }

    public int addEvent(String eventName, String description, int clubId, boolean isPublic, int capacity, String eventImgPath, List<EventSchedule> schedules) {
        String sql = """
                INSERT INTO Events (EventName, Description, ClubID, IsPublic, Capacity, Status, SemesterID, EventImg)
                VALUES (?, ?, ?, ?, ?, 'Pending', 'SU25', ?)""";
        String scheduleSql = """
                INSERT INTO EventSchedules (EventID, EventDate, LocationID, StartTime, EndTime)
                VALUES (?, ?, ?, ?, ?)""";
        Connection connection = null;
        try {
            connection = DBContext.getConnection();
            connection.setAutoCommit(false); // Bắt đầu transaction

            int eventId;
            // Thêm sự kiện
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, eventName);
                ps.setString(2, description != null && !description.isEmpty() ? description : null);
                ps.setInt(3, clubId);
                ps.setBoolean(4, isPublic);
                ps.setInt(5, capacity);
                ps.setString(6, eventImgPath != null ? eventImgPath : null);
                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Thêm sự kiện thất bại, không có bản ghi nào được thêm.");
                }
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        eventId = rs.getInt(1);
                    } else {
                        throw new SQLException("Không thể lấy ID của sự kiện vừa tạo.");
                    }
                }
            }

            // Thêm lịch trình
            try (PreparedStatement schedulePs = connection.prepareStatement(scheduleSql)) {
                for (EventSchedule schedule : schedules) {
                    schedulePs.setInt(1, eventId);
                    schedulePs.setDate(2, schedule.getEventDate());
                    schedulePs.setInt(3, schedule.getLocationID());
                    schedulePs.setTime(4, schedule.getStartTime());
                    schedulePs.setTime(5, schedule.getEndTime());
                    schedulePs.addBatch();
                }
                int[] batchResults = schedulePs.executeBatch();
                // Kiểm tra kết quả batch
                for (int result : batchResults) {
                    if (result == Statement.EXECUTE_FAILED) {
                        throw new SQLException("Thêm lịch trình thất bại.");
                    }
                }
            }

            connection.commit();
            return eventId;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm sự kiện: " + e.getMessage(), e);
        }
    }

    public void insertEvent(String eventName, String description, int clubId, boolean isPublic, int capacity, String eventImgPath, List<EventSchedule> schedules) {
        String sql = """
                    INSERT INTO Events (EventName, Description, ClubID, IsPublic, Capacity, Status, SemesterID, EventImg)
                    VALUES (?, ?, ?, ?, ?, 'Pending', 'SU25', ?)""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, eventName);
            ps.setString(2, description != null && !description.isEmpty() ? description : null);
            ps.setInt(3, clubId);
            ps.setBoolean(4, isPublic);
            ps.setInt(5, capacity);
            ps.setString(6, eventImgPath);
            ps.executeUpdate();
            int eventId;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    eventId = rs.getInt(1);
                } else {
                    throw new SQLException("Không thể lấy ID của sự kiện vừa tạo.");
                }
            }
            // Thêm schedules
            String scheduleSql = """
                    INSERT INTO EventSchedules (EventID, EventDate, LocationID, StartTime, EndTime)
                    VALUES (?, ?, ?, ?, ?)""";
            try (PreparedStatement schedulePs = connection.prepareStatement(scheduleSql)) {
                for (EventSchedule schedule : schedules) {
                    schedulePs.setInt(1, eventId);
                    schedulePs.setDate(2, schedule.getEventDate());
                    schedulePs.setInt(3, schedule.getLocationID());
                    schedulePs.setTime(4, schedule.getStartTime());
                    schedulePs.setTime(5, schedule.getEndTime());
                    schedulePs.addBatch();
                }
                schedulePs.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm sự kiện: " + e.getMessage(), e);
        }
    }

    public void updateEvent(int eventID, String name, String description, int capacity, boolean isPublic, List<EventSchedule> schedules) {
        String sql = """
                UPDATE Events SET EventName = ?, Description = ?, Capacity = ?, IsPublic = ?
                WHERE EventID = ?""";
        String deleteAgendaSql = "DELETE FROM Agenda WHERE ScheduleID IN (SELECT ScheduleID FROM EventSchedules WHERE EventID = ?)";
        String deleteScheduleSql = "DELETE FROM EventSchedules WHERE EventID = ?";
        String scheduleSql = """
                INSERT INTO EventSchedules (EventID, EventDate, LocationID, StartTime, EndTime)
                VALUES (?, ?, ?, ?, ?)""";

        Connection connection = null;
        try {
            connection = DBContext.getConnection();
            connection.setAutoCommit(false); // Sử dụng transaction để đảm bảo tính nhất quán

            // Cập nhật thông tin sự kiện
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, description);
                ps.setInt(3, capacity);
                ps.setBoolean(4, isPublic);
                ps.setInt(5, eventID);
                ps.executeUpdate();
            }

            // Xóa các agenda liên quan đến schedules của sự kiện
            try (PreparedStatement deleteAgendaPsSql = connection.prepareStatement(deleteAgendaSql)) {
                deleteAgendaPsSql.setInt(1, eventID);
                deleteAgendaPsSql.executeUpdate();
            }

            // Xóa schedules cũ
            try (PreparedStatement deletePs = connection.prepareStatement(deleteScheduleSql)) {
                deletePs.setInt(1, eventID);
                deletePs.executeUpdate();
            }

            // Thêm schedules mới
            try (PreparedStatement schedulePs = connection.prepareStatement(scheduleSql)) {
                for (EventSchedule schedule : schedules) {
                    schedulePs.setInt(1, eventID);
                    schedulePs.setDate(2, schedule.getEventDate());
                    schedulePs.setInt(3, schedule.getLocationID());
                    schedulePs.setTime(4, schedule.getStartTime());
                    schedulePs.setTime(5, schedule.getEndTime());
                    schedulePs.addBatch();
                }
                schedulePs.executeBatch();
            }

            connection.commit(); // Xác nhận transaction
        } catch (SQLException e) {

            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật sự kiện: " + e.getMessage());
        }
    }

    public void updateEventWithImage(int eventID, String name, String description, int capacity, boolean isPublic, String imageName, List<EventSchedule> schedules) {
        String sql = """
                UPDATE Events SET EventName = ?, Description = ?, Capacity = ?, IsPublic = ?, EventImg = ?
                WHERE EventID = ?""";
        String deleteAgendaSql = "DELETE FROM Agenda WHERE ScheduleID IN (SELECT ScheduleID FROM EventSchedules WHERE EventID = ?)";
        String deleteScheduleSql = "DELETE FROM EventSchedules WHERE EventID = ?";
        String scheduleSql = """
                INSERT INTO EventSchedules (EventID, EventDate, LocationID, StartTime, EndTime)
                VALUES (?, ?, ?, ?, ?)""";

        Connection connection = null;
        try {
            connection = DBContext.getConnection();
            connection.setAutoCommit(false); // Sử dụng transaction để đảm bảo tính nhất quán

            // Cập nhật thông tin sự kiện cùng với hình ảnh
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, description);
                ps.setInt(3, capacity);
                ps.setBoolean(4, isPublic);
                ps.setString(5, imageName);
                ps.setInt(6, eventID);
                ps.executeUpdate();
            }

            // Xóa các agenda liên quan đến schedules của sự kiện
            try (PreparedStatement deleteAgendaPs = connection.prepareStatement(deleteAgendaSql)) {
                deleteAgendaPs.setInt(1, eventID);
                int rowsAffected = deleteAgendaPs.executeUpdate();
                System.out.println("Đã xóa " + rowsAffected + " agenda(s) cho eventID: " + eventID);
            }

            // Xóa schedules cũ
            try (PreparedStatement deletePs = connection.prepareStatement(deleteScheduleSql)) {
                deletePs.setInt(1, eventID);
                int rowsAffected = deletePs.executeUpdate();
                System.out.println("Đã xóa " + rowsAffected + " schedule(s) cho eventID: " + eventID);
            }

            // Thêm schedules mới
            try (PreparedStatement schedulePs = connection.prepareStatement(scheduleSql)) {
                for (EventSchedule schedule : schedules) {
                    schedulePs.setInt(1, eventID);
                    schedulePs.setDate(2, schedule.getEventDate());
                    schedulePs.setInt(3, schedule.getLocationID());
                    schedulePs.setTime(4, schedule.getStartTime());
                    schedulePs.setTime(5, schedule.getEndTime());
                    schedulePs.addBatch();
                }
                int[] batchResults = schedulePs.executeBatch();
                System.out.println("Đã thêm " + batchResults.length + " schedule(s) mới cho eventID: " + eventID);
            }

            connection.commit(); // Xác nhận transaction
        } catch (SQLException e) {

            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật sự kiện với hình ảnh: " + e.getMessage());
        }
    }

    public List<Events> getEventsByPublic() {
        List<Events> events = new ArrayList<>();
        String sql = """
                    SELECT e.*, c.ClubName, c.ClubImg
                    FROM Events e
                    JOIN Clubs c ON e.ClubID = c.ClubID
                    WHERE e.SemesterID = 'SU25' AND e.IsPublic = TRUE""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Events event = new Events();
                event.setEventID(rs.getInt("EventID"));
                event.setEventName(rs.getString("EventName"));
                event.setEventImg(rs.getString("EventImg"));
                event.setDescription(rs.getString("Description"));
                event.setClubID(rs.getInt("ClubID"));
                event.setPublic(rs.getBoolean("IsPublic"));
                event.setFormID(rs.getInt("FormID") == 0 ? null : rs.getInt("FormID"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));
                event.setApprovalStatus(rs.getString("ApprovalStatus"));
                event.setRejectionReason(rs.getString("RejectionReason"));
                event.setClubName(rs.getString("ClubName"));
                event.setClubImg(rs.getString("ClubImg"));
                event.setSchedules(getSchedulesByEventID(rs.getInt("EventID")));
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return events;
    }

    public int countApprovedEvents() {
        String sql = "SELECT COUNT(*) FROM Events WHERE SemesterID = 'SU25' AND IsPublic = TRUE AND ApprovalStatus = 'APPROVED'";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public int countPendingEvents() {
        String sql = "SELECT COUNT(*) FROM Events WHERE SemesterID = 'SU25' AND IsPublic = TRUE AND ApprovalStatus = 'PENDING'";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public int countRejectedEvents() {
        String sql = "SELECT COUNT(*) FROM Events WHERE SemesterID = 'SU25' AND IsPublic = TRUE AND ApprovalStatus = 'REJECTED'";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public void updateApprovalStatus(int eventID, String status, String reason) {
        String sql = "UPDATE Events SET ApprovalStatus = ?, RejectionReason = ? WHERE EventID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, reason);
            ps.setInt(3, eventID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Agenda> getAgendaByEventPublic() {
        List<Agenda> list = new ArrayList<>();
        String sql = """
                    SELECT e.EventID, e.EventName, c.ClubName, a.*, es.EventDate
                    FROM Events e
                    JOIN EventSchedules es ON e.EventID = es.EventID
                    JOIN Agenda a ON es.ScheduleID = a.ScheduleID
                    JOIN Clubs c ON e.ClubID = c.ClubID
                    WHERE e.SemesterID = 'SU25' AND e.IsPublic = TRUE""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Agenda agenda = new Agenda();
                agenda.setAgendaID(rs.getInt("AgendaID"));
                agenda.setScheduleID(rs.getInt("ScheduleID"));
                agenda.setTitle(rs.getString("Title"));
                agenda.setDescription(rs.getString("Description"));
                agenda.setStatus(rs.getString("Status"));
                agenda.setReason(rs.getString("Reason"));
                agenda.setStartTime(rs.getTimestamp("StartTime"));
                agenda.setEndTime(rs.getTimestamp("EndTime"));
                EventSchedule schedule = new EventSchedule();
                schedule.setScheduleID(rs.getInt("ScheduleID"));
                schedule.setEventID(rs.getInt("EventID"));
                schedule.setEventDate(rs.getDate("EventDate"));
                Locations l = getLocationByID(rs.getInt("LocationID"));
                schedule.setLocation(l);
                agenda.setEventSchedule(schedule);
                list.add(agenda);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countEventHasPendingAgenda() {
        String sql = """
                    SELECT COUNT(DISTINCT e.EventID)
                    FROM Agenda a
                    JOIN EventSchedules es ON a.ScheduleID = es.ScheduleID
                    JOIN Events e ON es.EventID = e.EventID
                    WHERE a.Status = 'PENDING' AND e.SemesterID = 'SU25' AND e.IsPublic = TRUE""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countEventHasApprovedAgenda() {
        String sql = """
                    SELECT COUNT(DISTINCT e.EventID)
                    FROM Agenda a
                    JOIN EventSchedules es ON a.ScheduleID = es.ScheduleID
                    JOIN Events e ON es.EventID = e.EventID
                    WHERE a.Status = 'APPROVED' AND e.SemesterID = 'SU25' AND e.IsPublic = TRUE""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countEventHasRejectedAgenda() {
        String sql = """
                    SELECT COUNT(DISTINCT e.EventID)
                    FROM Agenda a
                    JOIN EventSchedules es ON a.ScheduleID = es.ScheduleID
                    JOIN Events e ON es.EventID = e.EventID
                    WHERE a.Status = 'REJECTED' AND e.SemesterID = 'SU25' AND e.IsPublic = TRUE""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updateAgendaStatus(int eventID, String status, String reason) {
        // Bước 1: Lấy danh sách ScheduleID từ EventSchedules dựa trên eventID
        String scheduleSql = "SELECT ScheduleID FROM EventSchedules WHERE EventID = ?";
        String agendaSql = "UPDATE Agenda SET Status = ?, Reason = ? WHERE ScheduleID = ?";
        Connection connection = null;
        PreparedStatement scheduleStmt = null;
        PreparedStatement agendaStmt = null;

        try {
            connection = DBContext.getConnection();
            connection.setAutoCommit(false); // Sử dụng transaction để đảm bảo tính nhất quán

            // Lấy danh sách ScheduleID
            scheduleStmt = connection.prepareStatement(scheduleSql);
            scheduleStmt.setInt(1, eventID);
            ResultSet rs = scheduleStmt.executeQuery();

            // Cập nhật trạng thái cho từng agenda liên quan
            agendaStmt = connection.prepareStatement(agendaSql);
            while (rs.next()) {
                int scheduleID = rs.getInt("ScheduleID");
                agendaStmt.setString(1, status);
                agendaStmt.setString(2, reason);
                agendaStmt.setInt(3, scheduleID);
                agendaStmt.executeUpdate();
            }

            connection.commit(); // Xác nhận transaction
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Agenda> getAgendaByEventID(int eventID) {
        List<Agenda> agendas = new ArrayList<>();
        String sql = """
                    SELECT a.*, e.EventID, e.EventName, c.ClubName, es.EventDate, es.LocationID, 
                           es.StartTime as ScheduleStartTime, es.EndTime as ScheduleEndTime, 
                           COUNT(a2.AgendaID) as AgendaCount
                    FROM Agenda a
                    JOIN EventSchedules es ON a.ScheduleID = es.ScheduleID
                    JOIN Events e ON es.EventID = e.EventID
                    JOIN Clubs c ON e.ClubID = c.ClubID
                    LEFT JOIN Agenda a2 ON es.ScheduleID = a2.ScheduleID
                    WHERE es.EventID = ?
                    GROUP BY a.AgendaID, e.EventID, e.EventName, c.ClubName, es.EventDate, es.LocationID, 
                             es.StartTime, es.EndTime
                    ORDER BY a.StartTime ASC
                """;

        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Agenda agenda = new Agenda();
                agenda.setAgendaID(rs.getInt("AgendaID"));
                agenda.setScheduleID(rs.getInt("ScheduleID"));
                agenda.setTitle(rs.getString("Title"));
                agenda.setDescription(rs.getString("Description"));
                agenda.setStartTime(rs.getTimestamp("StartTime"));
                agenda.setEndTime(rs.getTimestamp("EndTime"));
                agenda.setStatus(rs.getString("Status"));
                agenda.setReason(rs.getString("Reason"));
                EventSchedule schedule = new EventSchedule();
                schedule.setScheduleID(rs.getInt("ScheduleID"));
                schedule.setEventID(rs.getInt("EventID"));
                schedule.setEventDate(rs.getDate("EventDate"));
                schedule.setStartTime(rs.getTime("ScheduleStartTime"));
                schedule.setEndTime(rs.getTime("ScheduleEndTime"));
                Locations l = getLocationByID(rs.getInt("LocationID"));
                schedule.setLocation(l);
                agenda.setEventSchedule(schedule);
                Events event = new Events();
                event.setEventID(rs.getInt("EventID"));
                event.setEventName(rs.getString("EventName"));
                event.setClubName(rs.getString("ClubName"));
                event.setAgendaCount(rs.getInt("AgendaCount"));
                agendas.add(agenda);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return agendas;
    }

    public List<Agenda> getAgendasByEventID(int eventID) {
        List<Agenda> agendas = new ArrayList<>();
        String sql = """
                    SELECT a.*,es.EventID, es.EventDate, es.LocationID, es.StartTime as ScheduleStartTime, es.EndTime as ScheduleEndTime
                    FROM Agenda a
                    JOIN EventSchedules es ON a.ScheduleID = es.ScheduleID
                    WHERE es.EventID = ?
                    ORDER BY a.StartTime ASC""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Agenda agenda = new Agenda();
                agenda.setAgendaID(rs.getInt("AgendaID"));
                agenda.setScheduleID(rs.getInt("ScheduleID"));
                agenda.setTitle(rs.getString("Title"));
                agenda.setDescription(rs.getString("Description"));
                agenda.setStartTime(rs.getTimestamp("StartTime"));
                agenda.setEndTime(rs.getTimestamp("EndTime"));
                agenda.setStatus(rs.getString("Status"));
                agenda.setReason(rs.getString("Reason"));
                EventSchedule schedule = new EventSchedule();
                schedule.setScheduleID(rs.getInt("ScheduleID"));
                schedule.setEventID(rs.getInt("EventID"));
                schedule.setEventDate(rs.getDate("EventDate"));
                schedule.setStartTime(rs.getTime("ScheduleStartTime"));
                schedule.setEndTime(rs.getTime("ScheduleEndTime"));
                Locations l = getLocationByID(rs.getInt("LocationID"));
                schedule.setLocation(l);
                agenda.setEventSchedule(schedule);
                agendas.add(agenda);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return agendas;
    }

    public boolean insertAgendas(int scheduleID, String title, String description, Timestamp startTime, Timestamp endTime) {
        String sql = "INSERT INTO Agenda (ScheduleID, Title, Description, StartTime, EndTime, Status) VALUES (?, ?, ?, ?, ?, 'PENDING')";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, scheduleID);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setTimestamp(4, startTime);
            ps.setTimestamp(5, endTime);
            int rowsAffected = ps.executeUpdate();
            System.out.println("Inserted agenda with PENDING status: " + title);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting agenda: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String getEventApprovalStatus(int eventID) {
        String sql = "SELECT ApprovalStatus FROM Events WHERE EventID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("ApprovalStatus");
            }
        } catch (SQLException e) {
            System.err.println("Error getting event approval status: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateEventApprovalStatus(int eventID, String approvalStatus) {
        String sql = "UPDATE Events SET ApprovalStatus = ? WHERE EventID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, approvalStatus);
            ps.setInt(2, eventID);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating event approval status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAllAgendaStatus(int scheduleID, String status) {
        String sql = "UPDATE Agenda SET Status = ? WHERE ScheduleID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, scheduleID);
            int rowsAffected = ps.executeUpdate();
            System.out.println("Updated " + rowsAffected + " agenda items to status: " + status);
            return rowsAffected >= 0;
        } catch (SQLException e) {
            System.err.println("Error updating agenda status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasRejectedAgenda(int eventID) {
        String sql = """
                    SELECT COUNT(*) 
                    FROM Agenda a
                    JOIN EventSchedules es ON a.ScheduleID = es.ScheduleID
                    WHERE es.EventID = ? AND a.Status = 'REJECTED'""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking rejected agenda: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void insertAgenda(int scheduleID, String title, String description, Timestamp startTime, Timestamp endTime) {
        String sql = "INSERT INTO Agenda (ScheduleID, Title, Description, StartTime, EndTime) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, scheduleID);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setTimestamp(4, startTime);
            ps.setTimestamp(5, endTime);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllByEventID(int eventID) {
        String sql = """
                    DELETE FROM Agenda 
                    WHERE ScheduleID IN (SELECT ScheduleID FROM EventSchedules WHERE EventID = ?)""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEventFormID(int eventId, int formId) {
        String sql = "UPDATE Events SET FormID = ? WHERE EventID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, formId);
            ps.setInt(2, eventId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật FormID cho sự kiện: " + e.getMessage());
        }
    }

    public List<Events> getUpcomingEvents(int limit) {
        List<Events> events = new ArrayList<>();
        String sql = """
                    SELECT e.*, c.ClubName, c.ClubImg
                    FROM Events e
                    JOIN Clubs c ON e.ClubID = c.ClubID
                    WHERE e.IsPublic = TRUE AND EXISTS (
                        SELECT 1 FROM EventSchedules es WHERE es.EventID = e.EventID AND es.EventDate >= NOW()
                    )
                    ORDER BY e.EventID ASC LIMIT ?""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Events event = new Events();
                event.setEventID(rs.getInt("EventID"));
                event.setEventName(rs.getString("EventName"));
                event.setEventImg(rs.getString("EventImg"));
                event.setDescription(rs.getString("Description"));
                event.setClubID(rs.getInt("ClubID"));
                event.setPublic(rs.getBoolean("IsPublic"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));
                event.setClubName(rs.getString("ClubName"));
                event.setClubImg(rs.getString("ClubImg"));
                event.setSchedules(getSchedulesByEventID(rs.getInt("EventID")));
                events.add(event);
            }
        } catch (SQLException e) {
            System.out.println("Error getting upcoming events: " + e.getMessage());
        }
        return events;
    }

    public int getTotalEvents() {
        String sql = "SELECT COUNT(*) FROM Events WHERE Status = 'COMPLETED'";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting total events: " + e.getMessage());
        }
        return 0;
    }

    public int countUpcomingEvents() {
        String sql = """
                    SELECT COUNT(*) 
                    FROM Events e
                    WHERE e.Status = 'PENDING' AND EXISTS (
                        SELECT 1 FROM EventSchedules es WHERE es.EventID = e.EventID AND es.EventDate >= NOW()
                    )""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error counting upcoming events: " + e.getMessage());
        }
        return 0;
    }

    public boolean addFavoriteEvent(String userID, int eventID) {
        String sql = "INSERT INTO FavoriteEvents (UserID, EventID) VALUES (?, ?)";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userID);
            ps.setInt(2, eventID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding favorite event: " + e.getMessage());
            return false;
        }
    }

    public boolean removeFavoriteEvent(String userID, int eventID) {
        String sql = "DELETE FROM FavoriteEvents WHERE UserID = ? AND EventID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userID);
            ps.setInt(2, eventID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error removing favorite event: " + e.getMessage());
            return false;
        }
    }

    public boolean isFavoriteEvent(String userID, int eventID) {
        String sql = "SELECT COUNT(*) FROM FavoriteEvents WHERE UserID = ? AND EventID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userID);
            ps.setInt(2, eventID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking favorite event: " + e.getMessage());
        }
        return false;
    }

    public List<Events> getFavoriteEvents(String userID, int page, int pageSize) {
        List<Events> events = new ArrayList<>();
        String sql = """
                    SELECT e.*, c.ClubName, c.ClubImg
                    FROM FavoriteEvents fe
                    JOIN Events e ON fe.EventID = e.EventID
                    JOIN Clubs c ON e.ClubID = c.ClubID
                    WHERE fe.UserID = ? AND e.Status = 'PENDING' AND e.ApprovalStatus = 'APPROVED'
                    ORDER BY fe.AddedDate DESC
                    LIMIT ? OFFSET ?""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userID);
            ps.setInt(2, pageSize);
            ps.setInt(3, (page - 1) * pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Events event = new Events();
                event.setEventID(rs.getInt("EventID"));
                event.setEventName(rs.getString("EventName"));
                event.setEventImg(rs.getString("EventImg"));
                event.setDescription(rs.getString("Description"));
                event.setClubID(rs.getInt("ClubID"));
                event.setPublic(rs.getBoolean("IsPublic"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));
                event.setClubName(rs.getString("ClubName"));
                event.setClubImg(rs.getString("ClubImg"));
                event.setSchedules(getSchedulesByEventID(rs.getInt("EventID")));
                events.add(event);
            }
        } catch (SQLException e) {
            System.out.println("Error getting favorite events: " + e.getMessage());
        }
        return events;
    }

    public int getTotalFavoriteEvents(String userID) {
        String sql = """
                    SELECT COUNT(*) 
                    FROM FavoriteEvents fe
                    JOIN Events e ON fe.EventID = e.EventID
                    WHERE fe.UserID = ? AND e.Status = 'PENDING' AND e.ApprovalStatus = 'APPROVED'""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error counting favorite events: " + e.getMessage());
        }
        return 0;
    }

    public List<Events> getEventsByClubId(int clubId) {
        List<Events> events = new ArrayList<>();
        String sql = """
                    SELECT e.*, c.ClubName, c.ClubImg
                    FROM Events e
                    JOIN Clubs c ON e.ClubID = c.ClubID
                    WHERE e.ClubID = ?
                    ORDER BY e.EventID DESC""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Events event = new Events();
                event.setEventID(rs.getInt("EventID"));
                event.setEventName(rs.getString("EventName"));
                event.setEventImg(rs.getString("EventImg"));
                event.setDescription(rs.getString("Description"));
                event.setClubID(rs.getInt("ClubID"));
                event.setPublic(rs.getBoolean("IsPublic"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));
                event.setClubName(rs.getString("ClubName"));
                event.setClubImg(rs.getString("ClubImg"));
                event.setSchedules(getSchedulesByEventID(rs.getInt("EventID")));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }


    private int getAgendaCountByEventID(int eventID) {
        String sql = """
                    SELECT COUNT(a.AgendaID) as agendaCount
                    FROM Agenda a
                    JOIN EventSchedules es ON a.ScheduleID = es.ScheduleID
                    WHERE es.EventID = ?""";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("agendaCount");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}