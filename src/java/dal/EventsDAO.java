/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import models.Events;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
                event.setUrlGGForm(rs.getString("UrlGGForm"));
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

    public List<Events> getEventsByKeyword(String keyword) {
        String sql = "select * from Events where EventName like ?";
        List<Events> events = new ArrayList<Events>();
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
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
                event.setUrlGGForm(rs.getString("UrlGGForm"));
                event.setCapacity(rs.getInt("Capacity"));
                event.setStatus(rs.getString("Status"));
                events.add(event);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return events;
    }

    public List<Events> searchEventsByFilters(
            String publicFilter, // "all", "public", "private"
            String statusFilter, // "all", "PENDING", "COMPLETED"
            String sortByDate // "newest", "oldest", or null
    ) {
        List<Events> listEvents = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Events WHERE 1=1");

        // Filter IsPublic
        if (publicFilter != null && !publicFilter.equalsIgnoreCase("all")) {
            if (publicFilter.equalsIgnoreCase("public")) {
                sql.append(" AND IsPublic = TRUE");
            } else if (publicFilter.equalsIgnoreCase("private")) {
                sql.append(" AND IsPublic = FALSE");
            }
        }

        // Filter Status
        if (statusFilter != null && !statusFilter.equalsIgnoreCase("all")) {
            sql.append(" AND Status = ?");
        }

        // Sort by date
        if (sortByDate != null) {
            if (sortByDate.equalsIgnoreCase("newest")) {
                sql.append(" ORDER BY EventDate DESC");
            } else if (sortByDate.equalsIgnoreCase("oldest")) {
                sql.append(" ORDER BY EventDate ASC");
            }
        }

        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql.toString());

            int paramIndex = 1;
            if (statusFilter != null && !statusFilter.equalsIgnoreCase("all")) {
                ps.setString(paramIndex++, statusFilter);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Events e = new Events();
                e.setEventID(rs.getInt("EventID"));
                e.setEventName(rs.getString("EventName"));
                e.setDescription(rs.getString("Description"));
                e.setEventDate(rs.getTimestamp("EventDate"));
                e.setLocation(rs.getString("Location"));
                e.setClubID(rs.getInt("ClubID"));
                e.setPublic(rs.getBoolean("IsPublic"));
                e.setUrlGGForm(rs.getString("URLGGForm"));
                e.setCapacity(rs.getInt("Capacity"));
                e.setStatus(rs.getString("Status"));

                listEvents.add(e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

        return listEvents;
    }

    public List<Events> searchEventsByFiltersAndKeyword(String keyword, String publicFilter, String statusFilter, String sortByDate) {
        List<Events> listEvents = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Events WHERE 1=1");

        // keyword search
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND EventName LIKE ?");
        }

        // public filter
        if (publicFilter != null && !publicFilter.equalsIgnoreCase("all")) {
            if (publicFilter.equalsIgnoreCase("public")) {
                sql.append(" AND IsPublic = TRUE");
            } else if (publicFilter.equalsIgnoreCase("private")) {
                sql.append(" AND IsPublic = FALSE");
            }
        }

        // status filter
        if (statusFilter != null && !statusFilter.equalsIgnoreCase("all")) {
            sql.append(" AND Status = ?");
        }

        // sort
        if (sortByDate != null) {
            if (sortByDate.equalsIgnoreCase("newest")) {
                sql.append(" ORDER BY EventDate DESC");
            } else if (sortByDate.equalsIgnoreCase("oldest")) {
                sql.append(" ORDER BY EventDate ASC");
            }
        }

        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql.toString());

            int index = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(index++, "%" + keyword.trim() + "%");
            }
            if (statusFilter != null && !statusFilter.equalsIgnoreCase("all")) {
                ps.setString(index++, statusFilter);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Events e = new Events();
                e.setEventID(rs.getInt("EventID"));
                e.setEventName(rs.getString("EventName"));
                e.setDescription(rs.getString("Description"));
                e.setEventDate(rs.getTimestamp("EventDate"));
                e.setLocation(rs.getString("Location"));
                e.setClubID(rs.getInt("ClubID"));
                e.setPublic(rs.getBoolean("IsPublic"));
                e.setUrlGGForm(rs.getString("UrlGGForm"));
                e.setCapacity(rs.getInt("Capacity"));
                e.setStatus(rs.getString("Status"));

                listEvents.add(e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return listEvents;
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
                event.setUrlGGForm(rs.getString("UrlGGForm"));
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

}
