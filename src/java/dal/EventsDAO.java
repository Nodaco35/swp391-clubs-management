/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import models.EventOwnerInfo;
import models.EventStats;
import models.Events;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LE VAN THUAN
 */
public class EventsDAO {

    public List<Events> getAllEvents() {
        List<Events> events = new ArrayList<Events>();
        String sql = "select * from Events";
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
                event.setEventDate(rs.getTimestamp("EventDate"));
                event.setLocation(rs.getString("Location"));
                event.setClubID(rs.getInt("ClubID"));
                event.setPublic(rs.getBoolean("IsPublic"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));
                events.add(event);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Events size: " + events.size());
        return events;
    }

    public Events getEventByID(int id) {
        String sql = "SELECT * FROM Events WHERE EventID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Events event = new Events();
                event.setEventID(rs.getInt("EventID"));
                event.setEventName(rs.getString("EventName"));
                event.setEventImg(rs.getString("EventImg"));
                event.setDescription(rs.getString("Description"));
                event.setEventDate(rs.getTimestamp("EventDate"));
                event.setLocation(rs.getString("Location"));
                event.setClubID(rs.getInt("ClubID"));
                event.setPublic(rs.getBoolean("IsPublic"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));
                return event;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Events> getEventsByClubID(int clubID) {
        List<Events> events = new ArrayList<>();
        String sql = """
                    SELECT e.*, 
                           COUNT(ep.EventParticipantID) AS RegisteredCount,
                           (e.Capacity - COUNT(ep.EventParticipantID)) AS SpotsLeft
                    FROM Events e
                    LEFT JOIN EventParticipants ep 
                        ON e.EventID = ep.EventID 
                        AND (ep.Status = 'REGISTERED' OR ep.Status = 'ATTENDED' OR ep.Status = 'ABSENT')
                    WHERE e.ClubID = ?
                    GROUP BY e.EventID, e.EventName, e.EventImg, e.Description, 
                             e.EventDate, e.Location, e.ClubID, e.IsPublic, 
                             e.FormTemplateID, e.Capacity, e.Status
                    ORDER BY e.EventDate DESC
                """;

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
                event.setEventDate(rs.getTimestamp("EventDate"));
                event.setLocation(rs.getString("Location"));
                event.setClubID(rs.getInt("ClubID"));
                event.setPublic(rs.getBoolean("IsPublic"));
                event.setFormTemplateID(rs.getInt("FormTemplateID"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));
                event.setRegistered(rs.getInt("RegisteredCount"));
                event.setSpotsLeft(rs.getInt("SpotsLeft"));
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return events;
    }

    public List<Events> getEventsByClubID(int clubID, int eventID) {
        List<Events> events = new ArrayList<>();
        String sql = "SELECT * FROM Events WHERE ClubID = ? and eventID <> ?";
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
                event.setEventDate(rs.getTimestamp("EventDate"));
                event.setLocation(rs.getString("Location"));
                event.setClubID(rs.getInt("ClubID"));
                event.setPublic(rs.getBoolean("IsPublic"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return events;
    }


    public List<Events> searchEvents(String keyword, String publicFilter, String sortByDate, int limit, int offset) {
        List<Events> events = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT e.*, c.ClubName FROM Events e JOIN Clubs c ON e.ClubID = c.ClubID WHERE 1=1");

        int paramIndex = 1;
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND EventName LIKE ?");
        }

        if (publicFilter != null && !publicFilter.equalsIgnoreCase("all")) {
            if (publicFilter.equalsIgnoreCase("public")) {
                sql.append(" AND IsPublic = TRUE");
            } else if (publicFilter.equalsIgnoreCase("private")) {
                sql.append(" AND IsPublic = FALSE");
            }
        }

        if (sortByDate != null) {
            if (sortByDate.equalsIgnoreCase("newest")) {
                sql.append(" ORDER BY EventDate DESC");
            } else if (sortByDate.equalsIgnoreCase("oldest")) {
                sql.append(" ORDER BY EventDate ASC");
            }
        } else {
            sql.append(" ORDER BY EventDate DESC");
        }

        sql.append(" LIMIT ? OFFSET ?");

        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
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
                event.setEventDate(rs.getTimestamp("EventDate"));
                event.setLocation(rs.getString("Location"));
                event.setClubID(rs.getInt("ClubID"));
                event.setPublic(rs.getBoolean("IsPublic"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));

                event.setClubName(rs.getString("ClubName"));
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching events: " + e.getMessage(), e);
        }
        System.out.println("Generated SQL: " + sql);
        return events;
    }

    public int countEvents(String keyword, String publicFilter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Events WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND EventName LIKE ?");
        }

        if (publicFilter != null && !publicFilter.equalsIgnoreCase("all")) {
            if (publicFilter.equalsIgnoreCase("public")) {
                sql.append(" AND IsPublic = TRUE");
            } else if (publicFilter.equalsIgnoreCase("private")) {
                sql.append(" AND IsPublic = FALSE");
            }
        }

        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            // Set parameter only if keyword is valid
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
        String sql = "SELECT (e.Capacity - COUNT(ep.EventParticipantID)) AS SpotsLeft, COUNT(ep.EventParticipantID) AS RegisteredCount " +
                "FROM Events e " +
                "LEFT JOIN EventParticipants ep ON e.EventID = ep.EventID AND (ep.Status = 'REGISTERED' OR ep.Status = 'ATTENDED' OR ep.Status = 'ABSENT') " +
                "WHERE e.EventID = ? " +
                "GROUP BY e.Capacity";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int spotsLeft = rs.getInt("SpotsLeft");
                int registeredCount = rs.getInt("RegisteredCount");
                return new EventStats(spotsLeft, registeredCount);
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

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public EventOwnerInfo getEventOwnerInfo(int eventId) {
        String sql = "SELECT u.FullName, u.Email, c.ClubName FROM Events e " +
                "JOIN Clubs c ON e.ClubID = c.ClubID " +
                "JOIN UserClubs uc ON uc.ClubID = c.ClubID " +
                "JOIN Users u ON uc.UserID = u.UserID " +
                "WHERE uc.RoleID = 1 AND e.EventID = ?";

        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("FullName");
                String clubName = rs.getString("ClubName");
                String email = rs.getString("Email");
                return new EventOwnerInfo(fullName, email, clubName);
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
        String sql = "SELECT COUNT(*) FROM Events WHERE ClubID = ? AND EventDate > NOW()";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countOngoingEvents(int clubID) {
        String sql = "SELECT COUNT(*) FROM Events WHERE ClubID = ? AND DATE(EventDate) = CURDATE()";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countPastEvents(int clubID) {
        String sql = "SELECT COUNT(*) FROM Events WHERE ClubID = ? AND EventDate < NOW() AND DATE(EventDate) != CURDATE()";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }



    public List<Events> getUpcomingEvents(int limit) {
        List<Events> events = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = """
                    SELECT e.*, c.ClubName, c.ClubImg FROM Events e 
                    JOIN Clubs c ON e.ClubID = c.ClubID 
                    WHERE  e.IsPublic = 1
                    ORDER BY e.EventDate ASC LIMIT ? ;
                    """;

            stmt = conn.prepareStatement(query);
            stmt.setInt(1, limit);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Events event = new Events();
                event.setEventID(rs.getInt("EventID"));
                event.setEventName(rs.getString("EventName"));
                event.setDescription(rs.getString("Description"));
                event.setEventDate(rs.getTimestamp("EventDate"));
                event.setLocation(rs.getString("Location"));
                event.setClubID(rs.getInt("ClubID"));
                event.setPublic(rs.getBoolean("IsPublic"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));

                // Thêm thông tin câu lạc bộ
                event.setClubName(rs.getString("ClubName"));
                event.setClubImg(rs.getString("ClubImg"));

                events.add(event);
            }
        } catch (SQLException e) {
            System.out.println("Error getting upcoming events: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    DBContext.closeConnection(conn);
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return events;
    }

    public int getTotalEvents() {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT COUNT(*) FROM Events WHERE Status = 'COMPLETED'";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting total events: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    DBContext.closeConnection(conn);
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return count;
    }

    public int countUpcomingEvents() {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String sql = "SELECT COUNT(*) FROM Events WHERE Status = 'PENDING' AND EventDate >= NOW()";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error counting upcoming events: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    DBContext.closeConnection(conn);
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return count;
    }

}
