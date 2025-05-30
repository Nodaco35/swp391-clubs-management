/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

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

    public List<Events> searchEvents(String keyword, String publicFilter, String sortByDate, int limit, int offset) {
        List<Events> events = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Events WHERE 1=1");

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
        System.out.println("Generated SQL: " + sql);

        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {
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

        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            // Set parameter only if keyword is valid
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error counting events: " + e.getMessage(), e);
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
