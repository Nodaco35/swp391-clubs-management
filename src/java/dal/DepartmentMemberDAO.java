package dal;

import models.DepartmentMember;
import models.Tasks; // Chỉ sử dụng class Tasks
import models.Users;
import models.Clubs;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentMemberDAO {
    
    /**
     * Lấy danh sách thành viên trong ban theo phân trang (sử dụng ClubDepartmentID)
     */    public List<DepartmentMember> getDepartmentMembers(int clubDepartmentID, int page, int pageSize) {
        List<DepartmentMember> members = new ArrayList<>();
          String sql = """
            SELECT DISTINCT u.UserID, u.FullName, u.Email, u.AvatarSrc,
                   r.RoleName, uc.JoinDate, uc.IsActive, uc.RoleID,
                   cd.ClubDepartmentID, d.DepartmentName, d.DepartmentID, c.ClubName, c.ClubID
            FROM Users u
            INNER JOIN UserClubs uc ON u.UserID = uc.UserID
            INNER JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
            INNER JOIN Departments d ON cd.DepartmentID = d.DepartmentID
            INNER JOIN Clubs c ON cd.ClubID = c.ClubID            
            LEFT JOIN Roles r ON uc.RoleID = r.RoleID
            WHERE cd.ClubDepartmentID = ?
            ORDER BY 
                CASE 
                    WHEN uc.RoleID = 3 THEN 1  -- Trưởng ban
                    ELSE 3                     -- Thành viên thường
                END,
                uc.JoinDate DESC
            LIMIT ? OFFSET ?
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, clubDepartmentID);
            ps.setInt(2, pageSize);
            ps.setInt(3, (page - 1) * pageSize);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DepartmentMember member = new DepartmentMember();
                member.setUserID(rs.getString("UserID"));
                member.setFullName(rs.getString("FullName"));
                member.setEmail(rs.getString("Email"));
                member.setAvatar(rs.getString("AvatarSrc"));                member.setRoleName(rs.getString("RoleName"));                member.setJoinedDate(rs.getTimestamp("JoinDate"));
                member.setActive(rs.getBoolean("IsActive"));
                member.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                member.setDepartmentID(rs.getInt("DepartmentID"));
                member.setDepartmentName(rs.getString("DepartmentName"));
                member.setClubID(rs.getInt("ClubID"));
                member.setClubName(rs.getString("ClubName"));
                
                // Set default values for task-related fields
                member.setCompletedTasks(0);
                member.setAssignedTasks(0);
                member.setLastActivity(rs.getTimestamp("JoinDate"));
                
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }    
    /**
     * Đếm tổng số thành viên trong ban (sử dụng ClubDepartmentID)
     */public int getTotalMembersCount(int clubDepartmentID) {
        String sql = """
            SELECT COUNT(DISTINCT uc.UserID) as total
            FROM UserClubs uc
            INNER JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
            WHERE cd.ClubDepartmentID = ?
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, clubDepartmentID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }    
    /**
     * Tìm kiếm thành viên theo tên hoặc email (sử dụng ClubDepartmentID)
     */
    public List<DepartmentMember> searchMembers(int clubDepartmentID, String keyword, int page, int pageSize) {
        List<DepartmentMember> members = new ArrayList<>();
          String sql = """
            SELECT DISTINCT u.UserID, u.FullName, u.Email, u.AvatarSrc,
                   r.RoleName, uc.JoinDate, uc.IsActive, uc.RoleID,
                   cd.ClubDepartmentID, d.DepartmentName, d.DepartmentID, c.ClubName, c.ClubID
            FROM Users u
            INNER JOIN UserClubs uc ON u.UserID = uc.UserID
            INNER JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
            INNER JOIN Departments d ON cd.DepartmentID = d.DepartmentID
            INNER JOIN Clubs c ON cd.ClubID = c.ClubID
            LEFT JOIN Roles r ON uc.RoleID = r.RoleID            WHERE cd.ClubDepartmentID = ? 
            AND (u.FullName LIKE ? OR u.Email LIKE ?)
            ORDER BY 
                CASE 
                    WHEN uc.RoleID = 3 THEN 1  -- Trưởng ban
                    WHEN uc.RoleID = 4 THEN 2  -- Phó ban  
                    ELSE 3                     -- Thành viên thường
                END,
                uc.JoinDate DESC
            LIMIT ? OFFSET ?
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setInt(1, clubDepartmentID);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setInt(4, pageSize);
            ps.setInt(5, (page - 1) * pageSize);            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DepartmentMember member = new DepartmentMember();
                member.setUserID(rs.getString("UserID"));
                member.setFullName(rs.getString("FullName"));
                member.setEmail(rs.getString("Email"));
                member.setAvatar(rs.getString("AvatarSrc"));
                member.setRoleName(rs.getString("RoleName"));
                member.setJoinedDate(rs.getTimestamp("JoinDate"));
                member.setActive(rs.getBoolean("IsActive"));
                member.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                member.setDepartmentID(rs.getInt("DepartmentID"));
                member.setDepartmentName(rs.getString("DepartmentName"));
                member.setClubID(rs.getInt("ClubID"));
                member.setClubName(rs.getString("ClubName"));
                
                // Set default values for task-related fields
                member.setCompletedTasks(0);
                member.setAssignedTasks(0);
                member.setLastActivity(rs.getTimestamp("JoinDate"));
                
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }    
    /**
     * Lấy thành viên theo ID (sửa lại để sử dụng ClubDepartmentID)
     */
    public DepartmentMember getMemberById(String userID, int clubDepartmentID) {
        
        String sql = """
            SELECT u.UserID, u.FullName, u.Email, u.AvatarSrc,
                   r.RoleName, uc.JoinDate, uc.IsActive,
                   cd.ClubDepartmentID, d.DepartmentName, d.DepartmentID, c.ClubName, c.ClubID
            FROM Users u
            INNER JOIN UserClubs uc ON u.UserID = uc.UserID
            INNER JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
            INNER JOIN Departments d ON cd.DepartmentID = d.DepartmentID
            INNER JOIN Clubs c ON cd.ClubID = c.ClubID
            LEFT JOIN Roles r ON uc.RoleID = r.RoleID            WHERE u.UserID = ? AND cd.ClubDepartmentID = ?
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userID);
            ps.setInt(2, clubDepartmentID);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DepartmentMember member = new DepartmentMember();
                member.setUserID(rs.getString("UserID"));
                member.setFullName(rs.getString("FullName"));
                member.setEmail(rs.getString("Email"));
                member.setAvatar(rs.getString("AvatarSrc"));
                member.setRoleName(rs.getString("RoleName"));
                member.setJoinedDate(rs.getTimestamp("JoinDate"));
                member.setActive(rs.getBoolean("IsActive"));
                member.setDepartmentID(rs.getInt("DepartmentID"));
                member.setDepartmentName(rs.getString("DepartmentName"));
                member.setClubID(rs.getInt("ClubID"));
                member.setClubName(rs.getString("ClubName"));
                
                return member;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }    
    /**
     * Cập nhật trạng thái thành viên (sử dụng ClubDepartmentID)
     */public boolean updateMemberStatus(String userID, int clubDepartmentID, boolean isActive, String status) {
        String sql = """
            UPDATE UserClubs 
            SET IsActive = ?
            WHERE UserID = ? AND ClubDepartmentID = ?
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, isActive);
            ps.setString(2, userID);
            ps.setInt(3, clubDepartmentID);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;    }

    /**
     * Xóa thành viên khỏi ban (sử dụng ClubDepartmentID)
     */public boolean removeMemberFromDepartment(String userID, int clubDepartmentID) {        String sql = """
            DELETE FROM UserClubs 
            WHERE UserID = ? AND ClubDepartmentID = ?
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userID);
            ps.setInt(2, clubDepartmentID);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;    }

    /**
     * Tìm kiếm sinh viên để thêm vào ban (sử dụng ClubDepartmentID)
     */
    public List<Users> searchStudentsNotInDepartment(int clubDepartmentID, String keyword, int limit) {
        List<Users> students = new ArrayList<>();
        String sql = """
            SELECT DISTINCT u.UserID, u.FullName, u.Email, u.AvatarSrc
            FROM Users u
            WHERE u.PermissionID = 1 
            AND u.Status = 1
            AND (u.FullName LIKE ? OR u.Email LIKE ?)
            AND u.UserID NOT IN (
                SELECT uc.UserID 
                FROM UserClubs uc 
                WHERE uc.ClubDepartmentID = ?
            )
            ORDER BY u.FullName
            LIMIT ?
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setInt(3, clubDepartmentID);
            ps.setInt(4, limit);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Users student = new Users();
                student.setUserID(rs.getString("UserID"));
                student.setFullName(rs.getString("FullName"));
                student.setEmail(rs.getString("Email"));
                student.setAvatar(rs.getString("AvatarSrc"));
                
                students.add(student);
            }        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }    
    /**
     * Thêm thành viên vào ban (sử dụng ClubDepartmentID trực tiếp)
     */
    public boolean addMemberToClubDepartment(String userID, int clubDepartmentID, int roleID) {
        // Get clubID from clubDepartmentID
        String getClubIdSql = """
            SELECT ClubID 
            FROM ClubDepartments 
            WHERE ClubDepartmentID = ?
            """;
        
        String insertSql = """
            INSERT INTO UserClubs (UserID, ClubID, ClubDepartmentID, RoleID, JoinDate, IsActive)
            VALUES (?, ?, ?, ?, NOW(), 1)
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            // Get ClubID
            PreparedStatement getPs = conn.prepareStatement(getClubIdSql);
            getPs.setInt(1, clubDepartmentID);
            ResultSet rs = getPs.executeQuery();
            
            if (rs.next()) {
                int clubID = rs.getInt("ClubID");
                
                // Insert new member
                PreparedStatement insertPs = conn.prepareStatement(insertSql);
                insertPs.setString(1, userID);
                insertPs.setInt(2, clubID);
                insertPs.setInt(3, clubDepartmentID);
                insertPs.setInt(4, roleID);
                
                return insertPs.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }    
    /**
     * Kiểm tra quyền trưởng ban cho ban cụ thể
     * @param userID ID của user
     * @param clubDepartmentID ID của ban cụ thể
     */
    public boolean isDepartmentLeader(String userID, int clubDepartmentID) {
        String sql = """
            SELECT COUNT(*) as count
            FROM UserClubs uc
            WHERE uc.UserID = ? AND uc.ClubDepartmentID = ? AND uc.RoleID = 3 AND uc.IsActive = 1
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userID);
            ps.setInt(2, clubDepartmentID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }        return false;
    }

    /**
     * Lấy ClubDepartment ID của trưởng ban
     */
    public int getClubDepartmentIdByLeader(String userID) {
        String sql = """
            SELECT uc.ClubDepartmentID
            FROM UserClubs uc
            WHERE uc.UserID = ? AND uc.RoleID = 3 AND uc.IsActive = 1
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("ClubDepartmentID");
            }        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Đếm số thành viên phù hợp với tìm kiếm (sử dụng ClubDepartmentID)
     */
    public int getSearchMembersCount(int clubDepartmentID, String keyword) {
        String sql = """
            SELECT COUNT(DISTINCT u.UserID) as total
            FROM Users u
            INNER JOIN UserClubs uc ON u.UserID = uc.UserID
            INNER JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
            WHERE cd.ClubDepartmentID = ?
            AND (u.FullName LIKE ? OR u.Email LIKE ?)
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setInt(1, clubDepartmentID);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Đếm số thành viên active trong ban (sử dụng ClubDepartmentID)
     */
    public int getActiveMembersCount(int clubDepartmentID) {
        String sql = """
            SELECT COUNT(DISTINCT uc.UserID) as total
            FROM UserClubs uc
            INNER JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
            WHERE cd.ClubDepartmentID = ? AND uc.IsActive = 1
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, clubDepartmentID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Đếm số thành viên inactive trong ban (sử dụng ClubDepartmentID)
     */
    public int getInactiveMembersCount(int clubDepartmentID) {
        String sql = """
            SELECT COUNT(DISTINCT uc.UserID) as total
            FROM UserClubs uc
            INNER JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
            WHERE cd.ClubDepartmentID = ? AND uc.IsActive = 0
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, clubDepartmentID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Lấy thông tin chi tiết về một thành viên trong ban
     * Sửa lại để đảm bảo trả về dữ liệu nhất quán
     */
    public DepartmentMember getMemberDetail(int clubDepartmentID, String userID) {
        DepartmentMember member = null;
        
        String sql = """
            SELECT DISTINCT u.UserID, u.FullName, u.Email, u.Phone, u.StudentCode, u.Major, u.AvatarSrc,
                    r.RoleName, uc.JoinDate, uc.IsActive, uc.RoleID, uc.Status,
                    cd.ClubDepartmentID, d.DepartmentName, d.DepartmentID, c.ClubName, c.ClubID
            FROM UserClubs uc
            INNER JOIN Users u ON u.UserID = uc.UserID
            INNER JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
            INNER JOIN Departments d ON cd.DepartmentID = d.DepartmentID
            INNER JOIN Clubs c ON cd.ClubID = c.ClubID
            LEFT JOIN Roles r ON uc.RoleID = r.RoleID
            WHERE uc.ClubDepartmentID = ? AND uc.UserID = ?
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, clubDepartmentID);
            ps.setString(2, userID);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                member = new DepartmentMember();
                member.setUserID(rs.getString("UserID"));
                member.setFullName(rs.getString("FullName"));
                member.setEmail(rs.getString("Email"));
                member.setPhone(rs.getString("Phone") != null ? rs.getString("Phone") : "");
                member.setStudentCode(rs.getString("StudentCode") != null ? rs.getString("StudentCode") : "");
                member.setMajor(rs.getString("Major") != null ? rs.getString("Major") : "");
                member.setAvatar(rs.getString("AvatarSrc") != null ? rs.getString("AvatarSrc") : "");
                member.setRoleName(rs.getString("RoleName") != null ? rs.getString("RoleName") : "Thành viên");
                member.setJoinedDate(rs.getTimestamp("JoinDate"));
                member.setActive(rs.getBoolean("IsActive"));
                member.setStatus(rs.getString("Status") != null ? rs.getString("Status") : "");
                member.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                member.setDepartmentID(rs.getInt("DepartmentID"));
                member.setDepartmentName(rs.getString("DepartmentName") != null ? rs.getString("DepartmentName") : "");
                member.setClubID(rs.getInt("ClubID"));
                member.setClubName(rs.getString("ClubName") != null ? rs.getString("ClubName") : "");
                
                // Lấy thống kê công việc trực tiếp ở đây để tránh nhiều truy vấn
                try {
                    member.setCompletedTasks(getCompletedTasksCount(userID, clubDepartmentID));
                    member.setAssignedTasks(getAssignedTasksCount(userID, clubDepartmentID));
                    member.setLastActivity(getLastActivity(userID, clubDepartmentID));
                } catch (Exception e) {
                    // Nếu có lỗi khi lấy thông tin nhiệm vụ, vẫn trả về thông tin thành viên
                    System.err.println("Error getting task statistics: " + e.getMessage());
                    member.setCompletedTasks(0);
                    member.setAssignedTasks(0);
                    member.setLastActivity(null);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return member;
    }
    
    /**
     * Lấy số lượng công việc đã hoàn thành của thành viên
     */
    public int getCompletedTasksCount(String userID, int clubDepartmentID) {
        String sql = """
            SELECT COUNT(*) as total
            FROM Tasks t 
            WHERE t.AssignedTo = ? 
            AND t.ClubDepartmentID = ?
            AND t.Status = 'Completed'
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userID);
            ps.setInt(2, clubDepartmentID);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Lấy tổng số công việc được giao cho thành viên
     */
    public int getAssignedTasksCount(String userID, int clubDepartmentID) {
        String sql = """
            SELECT COUNT(*) as total
            FROM Tasks t 
            WHERE t.AssignedTo = ? 
            AND t.ClubDepartmentID = ?
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userID);
            ps.setInt(2, clubDepartmentID);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Lấy thời gian hoạt động gần nhất của thành viên
     */
    public Timestamp getLastActivity(String userID, int clubDepartmentID) {
        String sql = """
            SELECT MAX(UpdatedAt) as lastActivity
            FROM Tasks t 
            WHERE t.AssignedTo = ? 
            AND t.ClubDepartmentID = ?
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userID);
            ps.setInt(2, clubDepartmentID);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getTimestamp("lastActivity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
      /**
     * Lấy danh sách công việc của thành viên - Sửa đổi để xử lý lỗi
     */
    public List<Tasks> getMemberTasks(String userID, int clubDepartmentID) {
        List<Tasks> tasks = new ArrayList<>();
        
        try {
            String sql = """
                SELECT t.TaskID, t.Title, t.Description, t.Status, t.Priority, 
                       t.ProgressPercent, t.StartDate, t.EndDate, t.CreatedAt, 
                       t.ClubID, c.ClubName, t.CreatedBy, u.FullName as CreatorName
                FROM Tasks t
                LEFT JOIN Users u ON t.CreatedBy = u.UserID
                LEFT JOIN Clubs c ON t.ClubID = c.ClubID
                WHERE t.AssignedTo = ? 
                AND t.ClubDepartmentID = ?
                ORDER BY t.EndDate DESC
                """;
            
            try (Connection conn = DBContext.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, userID);
                ps.setInt(2, clubDepartmentID);
                
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    try {
                        Tasks task = new Tasks();
                        task.setTaskID(rs.getInt("TaskID"));
                        task.setTitle(rs.getString("Title") != null ? rs.getString("Title") : "");
                        task.setDescription(rs.getString("Description") != null ? rs.getString("Description") : "");
                        task.setStatus(rs.getString("Status") != null ? rs.getString("Status") : "");
                        //task.setPriority(rs.getString("Priority") != null ? rs.getString("Priority") : "");
                        //task.setProgressPercent(rs.getInt("ProgressPercent"));
                        task.setStartDate(rs.getDate("StartDate"));
                        task.setEndDate(rs.getDate("EndDate"));
                        task.setCreatedAt(rs.getDate("CreatedAt"));
                        
                        // Tạo object Clubs với thuộc tính cần thiết
                        Clubs club = new Clubs();
                        club.setClubID(rs.getInt("ClubID"));
                        club.setClubName(rs.getString("ClubName") != null ? rs.getString("ClubName") : "");
                        task.setClub(club);
                        
                        // Tạo object Users cho creator
                        Users creator = new Users();
                        creator.setUserID(rs.getString("CreatedBy") != null ? rs.getString("CreatedBy") : "");
                        creator.setFullName(rs.getString("CreatorName") != null ? rs.getString("CreatorName") : "");
                        task.setCreatedBy(creator);
                        
                        tasks.add(task);
                    } catch (Exception e) {
                        // Log lỗi và tiếp tục với task tiếp theo
                        System.err.println("Error processing task row: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in getMemberTasks: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in getMemberTasks: " + e.getMessage());
            e.printStackTrace();
        }
        return tasks;
    }
}