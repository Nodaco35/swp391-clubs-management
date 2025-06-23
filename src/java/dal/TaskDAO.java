package dal;

import models.ClubDepartment;
import models.Events;
import models.TaskAssignmentDepartment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public List<ClubDepartment> getDepartmentsByTaskAssignmentDepartmentID(int taskID) {
        List<ClubDepartment> list = new ArrayList<>();
        String sql = "SELECT d.ClubDepartmentID, d.DepartmentID, d.ClubID, dep.DepartmentName " +
                "FROM TaskAssignmentDepartmentResponser r " +
                "JOIN ClubDepartments d ON r.ClubDepartmentID = d.ClubDepartmentID " +
                "JOIN Departments dep ON d.DepartmentID = dep.DepartmentID " +
                "WHERE r.TaskAssignmentDepartmentID = ?";

        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, taskID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ClubDepartment d = new ClubDepartment();
                d.setClubDepartmentId(rs.getInt("ClubDepartmentID"));
                d.setDepartmentId(rs.getInt("DepartmentID"));
                d.setClubId(rs.getInt("ClubID"));
                d.setDepartmentName(rs.getString("DepartmentName"));
                list.add(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }



    public List<TaskAssignmentDepartment> getTaskAssignDepartByEventID(int eventID) {
        List<TaskAssignmentDepartment> list = new ArrayList<>();
        String sql = "SELECT * FROM TaskAssignmentDepartment WHERE EventID = ?";
        EventsDAO ed = new EventsDAO();

        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TaskAssignmentDepartment t = new TaskAssignmentDepartment();
                t.setTaskAssignmentDepartmentID(rs.getInt("TaskAssignmentDepartmentID"));
                Events e = ed.getEventByID(rs.getInt("EventID"));
                t.setEvent(e);
                t.setTerm(rs.getString("Term"));
                t.setTermStart(rs.getDate("TermStart"));
                t.setTermEnd(rs.getDate("TermEnd"));
                t.setDescription(rs.getString("Description"));
                t.setStatus(rs.getString("Status"));
                t.setTaskName(rs.getString("TaskName"));
                t.setStartedDate(rs.getDate("StartedDate"));
                t.setDueDate(rs.getDate("DueDate"));

                // ✅ Lấy phòng ban của task
                List<ClubDepartment> departments = getDepartmentsByTaskAssignmentDepartmentID(t.getTaskAssignmentDepartmentID());
                t.setDepartments(departments);
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


}
