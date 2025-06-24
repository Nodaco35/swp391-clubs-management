package dal;

import models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public List<Tasks> getTasksByEventID(int eventID) {
        List<Tasks> taskList = new ArrayList<>();
        String sql = "SELECT t.*, e.EventName, c.ClubName, u.FullName AS CreatorName " +
                "FROM Tasks t " +
                "JOIN Events e ON t.EventID = e.EventID " +
                "JOIN Clubs c ON t.ClubID = c.ClubID " +
                "JOIN Users u ON t.CreatedBy = u.UserID " +
                "WHERE t.EventID = ?";
        EventsDAO ed = new EventsDAO();
        ClubDAO cd = new ClubDAO();
        UserDAO ud = new UserDAO();
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Tasks t = new Tasks();

                t.setTaskID(rs.getInt("TaskID"));
                t.setParentTask(rs.getInt("ParentTaskID"));
                t.setTerm(rs.getString("Term"));
                t.setTermStart(rs.getDate("TermStart"));
                t.setTermEnd(rs.getDate("TermEnd"));

                Events event = ed.getEventByID(rs.getInt("EventID"));
                Clubs club = cd.getCLubByID(rs.getInt("ClubID"));
                Users creator = ud.getUserByID(rs.getString("CreatedBy"));

                t.setEvent(event);
                t.setClub(club);
                t.setCreatedBy(creator);

                t.setTitle(rs.getString("Title"));
                t.setDescription(rs.getString("Description"));
                t.setStatus(rs.getString("Status"));
                t.setPriority(rs.getString("Priority"));
                t.setProgressPercent(rs.getInt("ProgressPercent"));
                t.setStartDate(rs.getTimestamp("StartDate"));
                t.setEndDate(rs.getTimestamp("EndDate"));
                t.setCreatedAt(rs.getTimestamp("CreatedAt"));

                taskList.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskList;
    }

    public Tasks getTasksByID(int taskID) {
        String sql = "SELECT * from Tasks where TaskID = ?";
        EventsDAO ed = new EventsDAO();
        ClubDAO cd = new ClubDAO();
        UserDAO ud = new UserDAO();
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, taskID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Tasks t = new Tasks();

                t.setTaskID(rs.getInt("TaskID"));
                t.setParentTask(rs.getInt("ParentTaskID"));
                t.setTerm(rs.getString("Term"));
                t.setTermStart(rs.getDate("TermStart"));
                t.setTermEnd(rs.getDate("TermEnd"));

                Events event = ed.getEventByID(rs.getInt("EventID"));
                Clubs club = cd.getCLubByID(rs.getInt("ClubID"));
                Users creator = ud.getUserByID(rs.getString("CreatedBy"));

                t.setEvent(event);
                t.setClub(club);
                t.setCreatedBy(creator);

                t.setTitle(rs.getString("Title"));
                t.setDescription(rs.getString("Description"));
                t.setStatus(rs.getString("Status"));
                t.setPriority(rs.getString("Priority"));
                t.setProgressPercent(rs.getInt("ProgressPercent"));
                t.setStartDate(rs.getTimestamp("StartDate"));
                t.setEndDate(rs.getTimestamp("EndDate"));
                t.setCreatedAt(rs.getTimestamp("CreatedAt"));

                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TaskAssignees> getAssigneesByTaskID(int taskID) {
        List<TaskAssignees> assignees = new ArrayList<>();
        String sql = "SELECT ta.*, u.FullName, d.DepartmentName FROM TaskAssignees ta " +
                "LEFT JOIN Users u ON ta.UserID = u.UserID " +
                "LEFT JOIN Departments d ON ta.DepartmentID = d.DepartmentID " +
                "WHERE ta.TaskID = ?";
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
                    ta.setDepartment(null);
                } else if ("Department".equalsIgnoreCase(rs.getString("AssigneeType"))) {
                    Department d = dd.getDepartmentByID(rs.getInt("DepartmentID"));
                    ta.setDepartment(d);
                    ta.setUser(null);
                }

                assignees.add(ta);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return assignees;
    }
}


