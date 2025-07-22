package dal;

import models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TaskDAO {

    public boolean addTask(Tasks task) {
        String sql = "INSERT INTO Tasks (TermID, EventID, ClubID, AssigneeType, DepartmentID, DocumentID, Title, Description, Status, StartDate, EndDate, CreatedBy) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, task.getTerm().getTermID());
            ps.setInt(2, task.getEvent().getEventID());
            ps.setInt(3, task.getClub().getClubID());
            ps.setString(4, task.getAssigneeType());
            ps.setInt(5, task.getDepartmentAssignee().getDepartmentID());
            if (task.getDocument() != null) {
                ps.setInt(6, task.getDocument().getDocumentID());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            ps.setString(7, task.getTitle());
            ps.setString(8, task.getDescription());
            ps.setString(9, task.getStatus());
            ps.setTimestamp(10, new java.sql.Timestamp(task.getStartDate().getTime()));
            ps.setTimestamp(11, new java.sql.Timestamp(task.getEndDate().getTime()));
            ps.setString(12, task.getCreatedBy().getUserID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateTask(Tasks task) {
        String sql = "UPDATE Tasks SET TermID = ?, EventID = ?, ClubID = ?, AssigneeType = ?, DepartmentID = ?, DocumentID = ?, Title = ?, Description = ?, Status = ?, StartDate = ?, EndDate = ?, CreatedBy = ? WHERE TaskID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, task.getTerm().getTermID());
            ps.setInt(2, task.getEvent().getEventID());
            ps.setInt(3, task.getClub().getClubID());
            ps.setString(4, task.getAssigneeType());
            ps.setInt(5, task.getDepartmentAssignee().getDepartmentID());
            if (task.getDocument() != null) {
                ps.setInt(6, task.getDocument().getDocumentID());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            ps.setString(7, task.getTitle());
            ps.setString(8, task.getDescription());
            ps.setString(9, task.getStatus());
            ps.setTimestamp(10, new java.sql.Timestamp(task.getStartDate().getTime()));
            ps.setTimestamp(11, new java.sql.Timestamp(task.getEndDate().getTime()));
            ps.setString(12, task.getCreatedBy().getUserID());
            ps.setInt(13, task.getTaskID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public EventTerms getTermById(int termID) {
        String sql = "SELECT TermID, TermName, TermStart, TermEnd FROM EventTerms WHERE TermID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, termID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                EventTerms term = new EventTerms();
                term.setTermID(rs.getInt("TermID"));
                term.setTermName(rs.getString("TermName"));
                term.setTermStart(rs.getDate("TermStart"));
                term.setTermEnd(rs.getDate("TermEnd"));
                return term;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
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
        UserDAO ud = new UserDAO();
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
                Users creator = ud.getUserByID(rs.getString("CreatedBy"));

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
                    Users userAssignee = ud.getUserByID(rs.getString("UserID"));
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
        UserDAO ud = new UserDAO();
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
                Users creator = ud.getUserByID(rs.getString("CreatedBy"));

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
                    Users userAssignee = ud.getUserByID(rs.getString("UserID"));
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
        UserDAO ud = new UserDAO();
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
                    Users u = ud.getUserByID(rs.getString("UserID"));
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
}
