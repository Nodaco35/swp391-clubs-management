package dal;

import models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class TaskDAO {

    public static List<Tasks> getAllByUser(String userID) {
        List<Tasks> tasks = new ArrayList<>();
        String sql = """
            SELECT 
                t.TaskID,
                t.ParentTaskID,
                t.TermID,
                t.EventID,
                t.ClubID,
                t.DocumentID,
                t.AssigneeType,
                t.UserID AS UserAssigneeID,
                t.DepartmentID AS DepartmentAssigneeID,
                t.Title,
                t.Description,
                t.Content,
                t.Status,
                t.Rating,
                t.LastRejectReason,
                t.ReviewComment,
                t.StartDate,
                t.EndDate,
                t.CreatedBy AS CreatedByID,
                c.ClubName,
                d.DepartmentName,
                e.EventName,
                doc.DocumentName,
                doc.DocumentURL,t.CreatedAt,
                u1.FullName AS UserAssigneeFullName,
                u2.FullName AS CreatedByFullName
            FROM 
                Tasks t
            LEFT JOIN 
                Clubs c ON t.ClubID = c.ClubID
            LEFT JOIN 
                Departments d ON t.DepartmentID = d.DepartmentID
            LEFT JOIN 
                Events e ON t.EventID = e.EventID
            LEFT JOIN 
                Documents doc ON t.DocumentID = doc.DocumentID
            LEFT JOIN 
                Users u1 ON t.UserID = u1.UserID
            LEFT JOIN 
                Users u2 ON t.CreatedBy = u2.UserID
            WHERE 
                (t.AssigneeType = 'User' AND t.UserID = ?)
                OR 
                (
                    t.AssigneeType = 'Department' 
                    AND t.DepartmentID IN (
                        SELECT uc.ClubDepartmentID 
                        FROM UserClubs uc
                        JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
                        WHERE uc.UserID = ? AND uc.IsActive = TRUE
                    )
                )
                AND t.EndDate >= CURRENT_TIMESTAMP
                AND t.Status IN ('ToDo', 'InProgress', 'Review')
            ORDER BY 
                t.EndDate ASC
        """;

        try (PreparedStatement stmt = DBContext.getConnection().prepareStatement(sql)) {
            stmt.setString(1, userID);
            stmt.setString(2, userID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Tasks task = new Tasks();
                task.setTaskID(rs.getInt("TaskID"));
                task.setParentTaskID(rs.getObject("ParentTaskID", Integer.class));

                // Set Term (assuming TermID is sufficient for EventTerms)
                if (rs.getObject("TermID") != null) {
                    EventTerms term = new EventTerms();
                    term.setTermID(rs.getInt("TermID"));
                    task.setTerm(term);
                }

                // Set Event
                if (rs.getObject("EventID") != null) {
                    Events event = new Events();
                    event.setEventID(rs.getInt("EventID"));
                    event.setEventName(rs.getString("EventName"));
                    task.setEvent(event);
                }

                // Set Club
                if (rs.getObject("ClubID") != null) {
                    Clubs club = new Clubs();
                    club.setClubID(rs.getInt("ClubID"));
                    club.setClubName(rs.getString("ClubName"));
                    task.setClub(club);
                }

                // Set Document
                if (rs.getObject("DocumentID") != null) {
                    Documents document = new Documents();
                    document.setDocumentID(rs.getInt("DocumentID"));
                    document.setDocumentName(rs.getString("DocumentName"));
                    document.setDocumentURL(rs.getString("DocumentURL"));
                    task.setDocument(document);
                }

                task.setAssigneeType(rs.getString("AssigneeType"));

                // Set UserAssignee
                if (rs.getObject("UserAssigneeID") != null) {
                    Users userAssignee = new Users();
                    userAssignee.setUserID(rs.getString("UserAssigneeID"));
                    userAssignee.setFullName(rs.getString("UserAssigneeFullName"));
                    task.setUserAssignee(userAssignee);
                }

                // Set DepartmentAssignee
                if (rs.getObject("DepartmentAssigneeID") != null) {
                    Department department = new Department();
                    department.setDepartmentID(rs.getInt("DepartmentAssigneeID"));
                    department.setDepartmentName(rs.getString("DepartmentName"));
                    task.setDepartmentAssignee(department);
                }

                task.setTitle(rs.getString("Title"));
                task.setDescription(rs.getString("Description"));
                task.setContent(rs.getString("Content"));
                task.setStatus(rs.getString("Status"));
                task.setRating(rs.getString("Rating"));
                task.setLastRejectReason(rs.getString("LastRejectReason"));
                task.setReviewComment(rs.getString("ReviewComment"));
                task.setStartedDate(rs.getTimestamp("StartDate"));
                task.setEndedDate(rs.getTimestamp("EndDate"));

                // Set CreatedBy
                if (rs.getObject("CreatedByID") != null) {
                    Users createdBy = new Users();
                    createdBy.setUserID(rs.getString("CreatedByID"));
                    createdBy.setFullName(rs.getString("CreatedByFullName"));
                    task.setCreatedBy(createdBy);
                }

                task.setCreatedTime(rs.getTimestamp("CreatedAt"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    public static int countByUser(String userID) {
        String sql = """
            SELECT COUNT(*) AS taskCount
            FROM Tasks t
            WHERE 
                (t.AssigneeType = 'User' AND t.UserID = ?)
                OR 
                (
                    t.AssigneeType = 'Department' 
                    AND t.DepartmentID IN (
                        SELECT uc.ClubDepartmentID 
                        FROM UserClubs uc
                        JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
                        WHERE uc.UserID = ? AND uc.IsActive = TRUE
                    )
                )
                AND t.EndDate >= CURRENT_TIMESTAMP
                AND t.Status IN ('ToDo', 'InProgress', 'Review')
        """;

        try (PreparedStatement stmt = DBContext.getConnection().prepareStatement(sql)) {
            stmt.setString(1, userID);
            stmt.setString(2, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("taskCount");
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean addTask(Tasks task) {
        String sql;
        if ("User".equals(task.getAssigneeType())) {
            sql = "INSERT INTO Tasks (ParentTaskID, TermID, EventID, ClubID, AssigneeType, UserID, DocumentID, Title, Description, Status, StartDate, EndDate, CreatedBy) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO Tasks (ParentTaskID, TermID, EventID, ClubID, AssigneeType, DepartmentID, DocumentID, Title, Description, Status, StartDate, EndDate, CreatedBy) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }

        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            
            // Handle ParentTaskID - can be null for department tasks
            if (task.getParentTaskID() != null) {
                ps.setInt(1, task.getParentTaskID());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            
            // Handle Term - can be null
            if (task.getTerm() != null) {
                ps.setInt(2, task.getTerm().getTermID());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            
            // Handle Event - can be null
            if (task.getEvent() != null) {
                ps.setInt(3, task.getEvent().getEventID());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            
            ps.setInt(4, task.getClub().getClubID());
            ps.setString(5, task.getAssigneeType());

            // Set assignee based on type
            if ("User".equals(task.getAssigneeType())) {
                ps.setString(6, task.getUserAssignee().getUserID());
            } else {
                ps.setInt(6, task.getDepartmentAssignee().getDepartmentID());
            }

            if (task.getDocument() != null) {
                ps.setInt(7, task.getDocument().getDocumentID());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }
            ps.setString(8, task.getTitle());
            ps.setString(9, task.getDescription());
            ps.setString(10, task.getStatus());
            ps.setTimestamp(11, new java.sql.Timestamp(task.getStartDate().getTime()));
            ps.setTimestamp(12, new java.sql.Timestamp(task.getEndDate().getTime()));
            ps.setString(13, task.getCreatedBy().getUserID());
            
            System.out.println("DEBUG addTask: Executing with ParentTaskID=" + task.getParentTaskID() + 
                ", Event=" + (task.getEvent() != null ? task.getEvent().getEventID() : "null") + 
                ", Term=" + (task.getTerm() != null ? task.getTerm().getTermID() : "null"));
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ERROR addTask: " + e.getMessage());
            e.printStackTrace();
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
                Users creator = UserDAO.getUserById(rs.getString("CreatedBy"));

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
     *
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
                   e.EventName, c.ClubName, 
                   creator.FullName as CreatorName,
                   assignee.UserID as AssigneeUserID,
                   assignee.FullName as AssigneeFullName,
                   assignee.Email as AssigneeEmail,
                   assignee.AvatarSrc as AssigneeAvatar
            FROM Tasks t
            LEFT JOIN EventTerms et ON t.TermID = et.TermID
            LEFT JOIN Events e ON t.EventID = e.EventID
            LEFT JOIN Clubs c ON t.ClubID = c.ClubID
            LEFT JOIN Users creator ON t.CreatedBy = creator.UserID
            LEFT JOIN Users assignee ON t.UserID = assignee.UserID
            WHERE t.TaskID = ?
            """;

        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, taskId);

            System.out.println("DEBUG TaskDAO.getTaskById: Executing query for taskId=" + taskId);
            System.out.println("DEBUG TaskDAO.getTaskById: SQL=" + sql);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("DEBUG TaskDAO.getTaskById: Found task with ID=" + rs.getInt("TaskID"));

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
                task.setAssigneeType(rs.getString("AssigneeType"));

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
                if (rs.getString("CreatedBy") != null) {
                    Users creator = new Users();
                    creator.setUserID(rs.getString("CreatedBy"));
                    creator.setFullName(rs.getString("CreatorName"));
                    task.setCreatedBy(creator);
                }

                // Create Assignee object if available
                if (rs.getString("AssigneeUserID") != null) {
                    Users assignee = new Users();
                    assignee.setUserID(rs.getString("AssigneeUserID"));
                    assignee.setFullName(rs.getString("AssigneeFullName"));
                    assignee.setEmail(rs.getString("AssigneeEmail"));
                    assignee.setAvatar(rs.getString("AssigneeAvatar"));
                    task.setUserAssignee(assignee);
                }

                return task;
            } else {
                System.out.println("DEBUG TaskDAO.getTaskById: No task found with ID=" + taskId);
            }
        } catch (Exception e) {
            System.err.println("Error in getTaskById: " + e.getMessage());
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
     * Get tasks assigned to individual members in a specific department This
     * returns tasks where AssigneeType = 'User' and the user belongs to the
     * given department
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
                if (ps != null) {
                    ps.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get department tasks by club and event ID for dropdown selection
     * These are tasks that can serve as parent tasks for individual assignments
     */
    public List<Tasks> getDepartmentTasksByClubAndEvent(int clubID, int eventID) {
        List<Tasks> taskList = new ArrayList<>();
        String sql = """
            SELECT t.*, et.TermName, et.TermStart, et.TermEnd, 
                   e.EventName, c.ClubName, u.FullName as CreatorName,
                   d.DepartmentName
            FROM Tasks t
            LEFT JOIN EventTerms et ON t.TermID = et.TermID
            LEFT JOIN Events e ON t.EventID = e.EventID
            LEFT JOIN Clubs c ON t.ClubID = c.ClubID
            LEFT JOIN Users u ON t.CreatedBy = u.UserID
            LEFT JOIN Departments d ON t.DepartmentID = d.DepartmentID
            WHERE t.ClubID = ? 
              AND t.EventID = ? 
              AND t.ParentTaskID IS NULL
              AND t.Status IN ('ToDo', 'InProgress', 'Review')
            ORDER BY t.CreatedAt DESC
            """;

        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ps.setInt(2, eventID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Tasks task = new Tasks();
                task.setTaskID(rs.getInt("TaskID"));
                task.setTitle(rs.getString("Title"));
                task.setDescription(rs.getString("Description"));
                task.setStatus(rs.getString("Status"));
                task.setStartDate(rs.getTimestamp("StartDate"));
                task.setEndDate(rs.getTimestamp("EndDate"));
                task.setCreatedAt(rs.getTimestamp("CreatedAt"));
                task.setAssigneeType(rs.getString("AssigneeType"));

                // Set Term info
                if (rs.getInt("TermID") > 0) {
                    EventTerms et = new EventTerms();
                    et.setTermID(rs.getInt("TermID"));
                    et.setTermName(rs.getString("TermName"));
                    et.setTermStart(rs.getDate("TermStart"));
                    et.setTermEnd(rs.getDate("TermEnd"));
                    task.setTerm(et);
                }

                // Set Event info
                if (rs.getInt("EventID") > 0) {
                    Events event = new Events();
                    event.setEventID(rs.getInt("EventID"));
                    event.setEventName(rs.getString("EventName"));
                    task.setEvent(event);
                }

                // Set Department info
                if (rs.getInt("DepartmentID") > 0) {
                    Department dept = new Department();
                    dept.setDepartmentID(rs.getInt("DepartmentID"));
                    dept.setDepartmentName(rs.getString("DepartmentName"));
                    task.setDepartmentAssignee(dept);
                }

                taskList.add(task);
            }

            connection.close();
        } catch (SQLException e) {
            System.err.println("ERROR TaskDAO - Failed to get department tasks: " + e.getMessage());
            e.printStackTrace();
        }

        return taskList;
    }
}
