package dal;

import models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
        DepartmentDAO dd = new DepartmentDAO();
        DocumentsDAO docDAO = new DocumentsDAO();
        UserDAO ud = new UserDAO();
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, taskID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Tasks t = new Tasks();
                t.setTaskID(rs.getInt("TaskID"));
                int parentId = rs.getInt("ParentTaskID");
                t.setParentTaskID(rs.wasNull() ? null : parentId);
                EventTerms et = getEventTermsByID(rs.getInt("TermID"));
                Events event = ed.getEventByID(rs.getInt("EventID"));
                Clubs club = cd.getCLubByID(rs.getInt("ClubID"));
                Users creator = ud.getUserById(rs.getString("CreatedBy"));

                t.setTerm(et);
                t.setEvent(event);
                t.setClub(club);
                t.setCreatedBy(creator);
                t.setTitle(rs.getString("Title"));
                t.setDescription(rs.getString("Description"));
                t.setStatus(rs.getString("Status"));
                t.setRating(rs.getString("Rating"));
                t.setLastRejectReason(rs.getString("LastRejectReason"));
                t.setReviewComment(rs.getString("ReviewComment"));
                t.setStartDate(rs.getTimestamp("StartDate"));
                t.setEndDate(rs.getTimestamp("EndDate"));
                t.setCreatedAt(rs.getTimestamp("CreatedAt"));
                int documentID = rs.getInt("DocumentID");
                if (!rs.wasNull()) {
                    Documents doc = docDAO.getDocumentByID(documentID);
                    t.setDocument(doc);
                }
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
    
    /**
     * Get all tasks by club ID
     */
    public List<Tasks> getTasksByClubID(int clubID) {
        List<Tasks> taskList = new ArrayList<>();
        String sql = """
            SELECT t.*, et.TermName, et.TermStart, et.TermEnd, 
                   e.EventName, c.ClubName, u.FullName as CreatorName,
                   d.DepartmentName, du.FullName as UserAssigneeName
            FROM Tasks t
            LEFT JOIN EventTerms et ON t.TermID = et.TermID
            LEFT JOIN Events e ON t.EventID = e.EventID
            LEFT JOIN Clubs c ON t.ClubID = c.ClubID
            LEFT JOIN Users u ON t.CreatedBy = u.UserID
            LEFT JOIN Departments d ON t.DepartmentID = d.DepartmentID
            LEFT JOIN Users du ON t.UserID = du.UserID
            WHERE t.ClubID = ?
            ORDER BY t.CreatedAt DESC, t.StartDate DESC
            """;
        
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Tasks task = new Tasks();
                task.setTaskID(rs.getInt("TaskID"));
                task.setTitle(rs.getString("Title"));
                task.setDescription(rs.getString("Description"));
                task.setContent(rs.getString("Content"));
                task.setStatus(rs.getString("Status"));
                task.setAssigneeType(rs.getString("AssigneeType"));
                task.setStartDate(rs.getTimestamp("StartDate"));
                task.setEndDate(rs.getTimestamp("EndDate"));
                task.setCreatedAt(rs.getTimestamp("CreatedAt"));
                task.setRating(rs.getString("Rating"));
                task.setLastRejectReason(rs.getString("LastRejectReason"));
                task.setReviewComment(rs.getString("ReviewComment"));

                // Set creator
                if (rs.getString("CreatedBy") != null) {
                    Users creator = new Users();
                    creator.setUserID(rs.getString("CreatedBy"));
                    creator.setFullName(rs.getString("CreatorName"));
                    task.setCreatedBy(creator);
                }

                // Set club
                if (rs.getInt("ClubID") > 0) {
                    Clubs club = new Clubs();
                    club.setClubID(rs.getInt("ClubID"));
                    club.setClubName(rs.getString("ClubName"));
                    task.setClub(club);
                }

                // Set event
                if (rs.getInt("EventID") > 0) {
                    Events event = new Events();
                    event.setEventID(rs.getInt("EventID"));
                    event.setEventName(rs.getString("EventName"));
                    task.setEvent(event);
                }

                // Set term
                if (rs.getInt("TermID") > 0) {
                    EventTerms term = new EventTerms();
                    term.setTermID(rs.getInt("TermID"));
                    term.setTermName(rs.getString("TermName"));
                    term.setTermStart(rs.getTimestamp("TermStart"));
                    term.setTermEnd(rs.getTimestamp("TermEnd"));
                    task.setTerm(term);
                }

                // Set assignee based on type
                String assigneeType = rs.getString("AssigneeType");
                if ("Department".equals(assigneeType) && rs.getInt("DepartmentID") > 0) {
                    Department dept = new Department();
                    dept.setDepartmentID(rs.getInt("DepartmentID"));
                    dept.setDepartmentName(rs.getString("DepartmentName"));
                    task.setDepartmentAssignee(dept);
                } else if ("User".equals(assigneeType) && rs.getString("UserID") != null) {
                    Users userAssignee = new Users();
                    userAssignee.setUserID(rs.getString("UserID"));
                    userAssignee.setFullName(rs.getString("UserAssigneeName"));
                    task.setUserAssignee(userAssignee);
                }

                taskList.add(task);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return taskList;
    }
    
    /**
     * Get tasks assigned to individual members in a specific department
     * This returns tasks where AssigneeType = 'User' and the user belongs to the given department
     */
    public List<Tasks> getTasksForDepartmentMembers(int clubDepartmentId) {
        List<Tasks> taskList = new ArrayList<>();
        String sql = """
            SELECT t.*, et.TermName, et.TermStart, et.TermEnd, 
                   e.EventName, c.ClubName, u.FullName as CreatorName,
                   assignee.FullName as AssigneeName
            FROM Tasks t
            LEFT JOIN EventTerms et ON t.TermID = et.TermID
            LEFT JOIN Events e ON t.EventID = e.EventID
            LEFT JOIN Clubs c ON t.ClubID = c.ClubID
            LEFT JOIN Users u ON t.CreatedBy = u.UserID
            LEFT JOIN Users assignee ON t.UserID = assignee.UserID
            INNER JOIN UserClubs uc ON t.UserID = uc.UserID 
            WHERE t.AssigneeType = 'User' 
              AND uc.ClubDepartmentID = ? 
              AND uc.IsActive = 1
            ORDER BY t.StartDate DESC, t.CreatedAt DESC
            """;
        
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubDepartmentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Tasks task = new Tasks();
                task.setTaskID(rs.getInt("TaskID"));
                task.setParentTaskID(rs.getInt("ParentTaskID"));
                task.setTitle(rs.getString("Title"));
                task.setDescription(rs.getString("Description"));
                task.setContent(rs.getString("Content"));
                task.setStatus(rs.getString("Status"));
                task.setRating(rs.getString("Rating"));
                task.setLastRejectReason(rs.getString("LastRejectReason"));
                task.setReviewComment(rs.getString("ReviewComment"));
                task.setStartDate(rs.getTimestamp("StartDate"));
                task.setEndDate(rs.getTimestamp("EndDate"));
                task.setCreatedAt(rs.getTimestamp("CreatedAt"));
                task.setAssigneeType(rs.getString("AssigneeType"));

                // Set user assignee info
                if (rs.getString("UserID") != null) {
                    Users userAssignee = new Users();
                    userAssignee.setUserID(rs.getString("UserID"));
                    userAssignee.setFullName(rs.getString("AssigneeName"));
                    task.setUserAssignee(userAssignee);
                }

                // Set creator info
                if (rs.getString("CreatedBy") != null) {
                    Users creator = new Users();
                    creator.setUserID(rs.getString("CreatedBy"));
                    creator.setFullName(rs.getString("CreatorName"));
                    task.setCreatedBy(creator);
                }

                taskList.add(task);
            }
            
            connection.close();
        } catch (SQLException e) {
            System.err.println("ERROR TaskDAO - Failed to get tasks for department members: " + e.getMessage());
            e.printStackTrace();
        }
        
        return taskList;
    }
    
    /**
     * Tạo task mới cho cá nhân
     */
    public boolean createTask(Tasks task) {
        String sql = """
            INSERT INTO Tasks (Title, Description, StartDate, EndDate, AssignedTo, AssigneeType, 
                              CreatedBy, CreatedAt, UpdatedAt, Status, EventID) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        Connection connection = null;
        PreparedStatement ps = null;
        
        try {
            connection = DBContext.getConnection();
            ps = connection.prepareStatement(sql);
            
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setDate(3, new java.sql.Date(task.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(task.getEndDate().getTime()));
            ps.setString(5, task.getUserAssignee().getUserID());
            ps.setString(6, task.getAssigneeType());
            ps.setString(7, task.getCreatedBy().getUserID());
            ps.setTimestamp(8, new Timestamp(task.getCreatedAt().getTime()));
            ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            ps.setString(10, task.getStatus());
            ps.setInt(11, task.getEvent().getEventID());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("ERROR TaskDAO - Failed to create task: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
