package dal;

import models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TaskDAO {

    public EventTerms getEventTermsByID(int termID) {
        String sql = "SELECT * FROM EventTerms WHERE termID = ?";
        EventsDAO ed = new EventsDAO();
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, termID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                EventTerms et = new EventTerms();
                et.setTermID(rs.getInt("termID"));
                Events e = ed.getEventByID(rs.getInt("EventID"));
                et.setEvent(e);
                et.setTermName(rs.getString("termName"));
                et.setTermStart(rs.getDate("termStart"));
                et.setTermEnd(rs.getDate("termEnd"));
                return et;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<EventTerms> getTermsByEventID(int eventID) {
        List<EventTerms> terms = new ArrayList<>();
        String sql = "SELECT * FROM EventTerms WHERE EventID = ? ORDER BY EventID DESC, TermStart ASC";
        EventsDAO ed = new EventsDAO();

        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                EventTerms et = new EventTerms();
                et.setTermID(rs.getInt("TermID"));
                Events e = ed.getEventByID(rs.getInt("EventID"));
                et.setEvent(e);
                et.setTermName(rs.getString("termName"));
                et.setTermStart(rs.getDate("termStart"));
                et.setTermEnd(rs.getDate("termEnd"));
                terms.add(et);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return terms;
    }

    public Tasks getTasksByID(int taskID) {
        String sql = "SELECT * FROM Tasks WHERE TaskID = ?";
        EventsDAO ed = new EventsDAO();
        ClubDAO cd = new ClubDAO();
        DepartmentDAO dd = new DepartmentDAO();
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, taskID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Tasks t = new Tasks();

                t.setTaskID(rs.getInt("TaskID"));
                int parentId = rs.getInt("ParentTaskID");
                t.setParentTaskID(rs.wasNull() ? null : parentId); // nullable check

                EventTerms et = getEventTermsByID(rs.getInt("TermID"));
                Events event = ed.getEventByID(rs.getInt("EventID"));
                Clubs club = cd.getCLubByID(rs.getInt("ClubID"));
                Users creator = UserDAO.getUserById(rs.getString("CreatedBy"));

                t.setTerm(et);
                t.setEvent(event);
                t.setClub(club);
                t.setCreatedBy(creator);

                t.setTitle(rs.getString("Title"));
                t.setDescription(rs.getString("Description"));
                t.setStatus(rs.getString("Status"));
                t.setReviewComment(rs.getString("ReviewComment"));
                t.setStartDate(rs.getTimestamp("StartDate"));
                t.setEndDate(rs.getTimestamp("EndDate"));
                t.setCreatedAt(rs.getTimestamp("CreatedAt"));

                String assigneeType = rs.getString("AssigneeType");
                t.setAssigneeType(assigneeType);
                if ("User".equals(assigneeType)) {
                    Users userAssignee = UserDAO.getUserById(rs.getString("UserID"));
                    t.setUserAssignee(userAssignee);
                } else if ("Department".equals(assigneeType)) {
                    Department dept = dd.getDepartmentByID(rs.getInt("DepartmentID"));
                    t.setDepartmentAssignee(dept);
                }

                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Tasks> getTasksByEventID(int eventID) {
        List<Tasks> taskList = new ArrayList<>();
        String sql = "SELECT * FROM Tasks WHERE EventID = ? ORDER BY TermID";
        EventsDAO ed = new EventsDAO();
        ClubDAO cd = new ClubDAO();
        DepartmentDAO dd = new DepartmentDAO();

        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Tasks t = new Tasks();

                t.setTaskID(rs.getInt("TaskID"));
                int parentId = rs.getInt("ParentTaskID");
                t.setParentTaskID(rs.wasNull() ? null : parentId);

                EventTerms et = getEventTermsByID(rs.getInt("TermID"));
                Events event = ed.getEventByID(rs.getInt("EventID"));
                Clubs club = cd.getCLubByID(rs.getInt("ClubID"));
                Users creator = UserDAO.getUserById(rs.getString("CreatedBy"));

                t.setTerm(et);
                t.setEvent(event);
                t.setClub(club);
                t.setCreatedBy(creator);

                t.setTitle(rs.getString("Title"));
                t.setDescription(rs.getString("Description"));
                t.setStatus(rs.getString("Status"));
                t.setReviewComment(rs.getString("ReviewComment"));
                t.setStartDate(rs.getTimestamp("StartDate"));
                t.setEndDate(rs.getTimestamp("EndDate"));
                t.setCreatedAt(rs.getTimestamp("CreatedAt"));

                String assigneeType = rs.getString("AssigneeType");
                t.setAssigneeType(assigneeType);
                if ("User".equals(assigneeType)) {
                    Users userAssignee = UserDAO.getUserById(rs.getString("UserID"));
                    t.setUserAssignee(userAssignee);
                } else if ("Department".equals(assigneeType)) {
                    Department dept = dd.getDepartmentByID(rs.getInt("DepartmentID"));
                    t.setDepartmentAssignee(dept);
                }

                taskList.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskList;
    }

    public List<TaskAssignees> getAssigneesByTaskID(int taskID) {
        List<TaskAssignees> assignees = new ArrayList<>();
        String sql = "SELECT * FROM TaskAssignees WHERE TaskID = ?";
        DepartmentDAO dd = new DepartmentDAO();

        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, taskID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TaskAssignees ta = new TaskAssignees();
                ta.setTaskAssigneeID(rs.getInt("TaskAssigneeID"));
                Tasks t = getTasksByID(rs.getInt("TaskID"));
                ta.setTask(t);
                ta.setAssigneeType(rs.getString("AssigneeType"));

                if ("User".equalsIgnoreCase(rs.getString("AssigneeType"))) {
                    Users u = UserDAO.getUserById(rs.getString("UserID"));
                    ta.setUser(u);
                } else if ("Department".equalsIgnoreCase(rs.getString("AssigneeType"))) {
                    Department d = dd.getDepartmentByID(rs.getInt("DepartmentID"));
                    ta.setDepartment(d);
                }

                assignees.add(ta);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return assignees;
    }

    /**
     * Get tasks assigned to a specific department in a club
     * @param clubId ID of the club
     * @param departmentId ID of the department
     * @return List of tasks assigned to the department
     */
    public List<Tasks> getTasksByClubAndDepartment(int clubId, int departmentId) {
        List<Tasks> taskList = new ArrayList<>();
        String sql = """
            SELECT t.*, et.TermName, et.TermStart, et.TermEnd, 
                   e.EventName, c.ClubName, u.FullName as CreatorName
            FROM Tasks t
            LEFT JOIN EventTerms et ON t.TermID = et.TermID
            LEFT JOIN Events e ON t.EventID = e.EventID
            LEFT JOIN Clubs c ON t.ClubID = c.ClubID
            LEFT JOIN Users u ON t.CreatedBy = u.UserID
            WHERE t.ClubID = ? AND t.DepartmentID = ? AND t.AssigneeType = 'Department'
            ORDER BY t.StartDate DESC, t.CreatedAt DESC
            """;
        
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubId);
            ps.setInt(2, departmentId);
            System.out.println("DEBUG TaskDAO - Executing query with ClubID: " + clubId + ", DepartmentID: " + departmentId);
            System.out.println("DEBUG TaskDAO - SQL: " + sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Tasks task = new Tasks();
                task.setTaskID(rs.getInt("TaskID"));
                
                Integer parentId = rs.getInt("ParentTaskID");
                task.setParentTaskID(rs.wasNull() ? null : parentId);
                
                task.setTitle(rs.getString("Title"));
                task.setDescription(rs.getString("Description"));
                task.setStatus(rs.getString("Status"));
                task.setStartDate(rs.getTimestamp("StartDate"));
                task.setEndDate(rs.getTimestamp("EndDate"));
                task.setCreatedAt(rs.getTimestamp("CreatedAt"));
                task.setAssigneeType("Department");
                
                // Create EventTerms object
                if (rs.getInt("TermID") > 0) {
                    EventTerms et = new EventTerms();
                    et.setTermID(rs.getInt("TermID"));
                    et.setTermName(rs.getString("TermName"));
                    et.setTermStart(rs.getDate("TermStart"));
                    et.setTermEnd(rs.getDate("TermEnd"));
                    task.setTerm(et);
                }
                
                // Create Event object
                if (rs.getInt("EventID") > 0) {
                    Events event = new Events();
                    event.setEventID(rs.getInt("EventID"));
                    event.setEventName(rs.getString("EventName"));
                    task.setEvent(event);
                }
                
                // Create Club object
                Clubs club = new Clubs();
                club.setClubID(rs.getInt("ClubID"));
                club.setClubName(rs.getString("ClubName"));
                task.setClub(club);
                
                // Create Creator object
                Users creator = new Users();
                creator.setUserID(rs.getString("CreatedBy"));
                creator.setFullName(rs.getString("CreatorName"));
                task.setCreatedBy(creator);
                
                taskList.add(task);
            }
            System.out.println("DEBUG TaskDAO - Found " + taskList.size() + " tasks");
        } catch (Exception e) {
            System.out.println("DEBUG TaskDAO - Error: " + e.getMessage());
            e.printStackTrace();
        }
        return taskList;
    }
    
    /**
     * Get task by ID with full details
     */
    public Tasks getTaskById(int taskId) {
        String sql = """
            SELECT t.*, et.TermName, et.TermStart, et.TermEnd, 
                   e.EventName, c.ClubName, u.FullName as CreatorName
            FROM Tasks t
            LEFT JOIN EventTerms et ON t.TermID = et.TermID
            LEFT JOIN Events e ON t.EventID = e.EventID
            LEFT JOIN Clubs c ON t.ClubID = c.ClubID
            LEFT JOIN Users u ON t.CreatedBy = u.UserID
            WHERE t.TaskID = ?
            """;
        
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Tasks task = new Tasks();
                task.setTaskID(rs.getInt("TaskID"));
                
                Integer parentId = rs.getInt("ParentTaskID");
                task.setParentTaskID(rs.wasNull() ? null : parentId);
                
                task.setTitle(rs.getString("Title"));
                task.setDescription(rs.getString("Description"));
                task.setStatus(rs.getString("Status"));
                task.setStartDate(rs.getTimestamp("StartDate"));
                task.setEndDate(rs.getTimestamp("EndDate"));
                task.setCreatedAt(rs.getTimestamp("CreatedAt"));
                task.setAssigneeType("Department");
                
                // Create EventTerms object
                if (rs.getInt("TermID") > 0) {
                    EventTerms et = new EventTerms();
                    et.setTermID(rs.getInt("TermID"));
                    et.setTermName(rs.getString("TermName"));
                    et.setTermStart(rs.getDate("TermStart"));
                    et.setTermEnd(rs.getDate("TermEnd"));
                    task.setTerm(et);
                }
                
                // Create Event object
                if (rs.getInt("EventID") > 0) {
                    Events event = new Events();
                    event.setEventID(rs.getInt("EventID"));
                    event.setEventName(rs.getString("EventName"));
                    task.setEvent(event);
                }
                
                // Create Club object
                Clubs club = new Clubs();
                club.setClubID(rs.getInt("ClubID"));
                club.setClubName(rs.getString("ClubName"));
                task.setClub(club);
                
                // Create Creator object
                Users creator = new Users();
                creator.setUserID(rs.getString("CreatedBy"));
                creator.setFullName(rs.getString("CreatorName"));
                task.setCreatedBy(creator);
                
                return task;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Update task status
     */
    public boolean updateTaskStatus(int taskId, String status) {
        String sql = "UPDATE Tasks SET Status = ? WHERE TaskID = ?";
        
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, taskId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
