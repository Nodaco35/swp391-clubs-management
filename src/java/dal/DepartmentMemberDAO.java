package dal;

import models.DepartmentMember;
import models.Users;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentMemberDAO {    /**
     * Lấy danh sách thành viên trong ban theo phân trang (sử dụng ClubDepartmentID)
     */
    public List<DepartmentMember> getDepartmentMembers(int clubDepartmentID, int page, int pageSize) {
        List<DepartmentMember> members = new ArrayList<>();
        String sql = """
            SELECT u.UserID, u.FullName, u.Email, u.AvatarSrc,
                   r.RoleName, uc.JoinDate, uc.IsActive,
                   d.DepartmentName, d.DepartmentID, c.ClubName, c.ClubID
            FROM Users u
            INNER JOIN UserClubs uc ON u.UserID = uc.UserID
            INNER JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
            INNER JOIN Departments d ON cd.DepartmentID = d.DepartmentID
            INNER JOIN Clubs c ON cd.ClubID = c.ClubID
            LEFT JOIN Roles r ON uc.RoleID = r.RoleID
            WHERE cd.ClubDepartmentID = ?
            ORDER BY uc.JoinDate DESC
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
                member.setAvatar(rs.getString("AvatarSrc"));
                member.setRoleName(rs.getString("RoleName"));                member.setJoinedDate(rs.getTimestamp("JoinDate"));
                member.setActive(rs.getBoolean("IsActive"));
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
    }    /**
     * Đếm tổng số thành viên trong ban (sử dụng ClubDepartmentID)
     */
    public int getTotalMembersCount(int clubDepartmentID) {
        String sql = """
            SELECT COUNT(*) as total
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
    }    /**
     * Tìm kiếm thành viên theo tên hoặc email (sử dụng ClubDepartmentID)
     */
    public List<DepartmentMember> searchMembers(int clubDepartmentID, String keyword, int page, int pageSize) {
        List<DepartmentMember> members = new ArrayList<>();
        String sql = """
            SELECT u.UserID, u.FullName, u.Email, u.AvatarSrc,
                   r.RoleName, uc.JoinDate, uc.IsActive,
                   d.DepartmentName, d.DepartmentID, c.ClubName, c.ClubID
            FROM Users u
            INNER JOIN UserClubs uc ON u.UserID = uc.UserID
            INNER JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
            INNER JOIN Departments d ON cd.DepartmentID = d.DepartmentID
            INNER JOIN Clubs c ON cd.ClubID = c.ClubID
            LEFT JOIN Roles r ON uc.RoleID = r.RoleID
            WHERE cd.ClubDepartmentID = ? 
            AND (u.FullName LIKE ? OR u.Email LIKE ?)
            ORDER BY uc.JoinDate DESC
            LIMIT ? OFFSET ?
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setInt(1, clubDepartmentID);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setInt(4, pageSize);
            ps.setInt(5, (page - 1) * pageSize);ResultSet rs = ps.executeQuery();
            while (rs.next()) {
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
    }    /**
     * Lấy thành viên theo ID
     */
    public DepartmentMember getMemberById(String userID, int departmentID) {
        String sql = """
            SELECT u.UserID, u.FullName, u.Email, u.AvatarSrc,
                   r.RoleName, uc.JoinDate, uc.IsActive,
                   d.DepartmentName, d.DepartmentID, c.ClubName, c.ClubID
            FROM Users u
            INNER JOIN UserClubs uc ON u.UserID = uc.UserID
            INNER JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
            INNER JOIN Departments d ON cd.DepartmentID = d.DepartmentID
            INNER JOIN Clubs c ON cd.ClubID = c.ClubID
            LEFT JOIN Roles r ON uc.RoleID = r.RoleID
            WHERE u.UserID = ? AND cd.DepartmentID = ?
            """;
          try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userID);
            ps.setInt(2, departmentID);              ResultSet rs = ps.executeQuery();
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
    }    /**
     * Cập nhật trạng thái thành viên
     */
    public boolean updateMemberStatus(String userID, int departmentID, boolean isActive, String status) {
        String sql = """
            UPDATE UserClubs uc
            JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
            SET uc.IsActive = ?
            WHERE uc.UserID = ? AND cd.DepartmentID = ?
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, isActive);
            ps.setString(2, userID);
            ps.setInt(3, departmentID);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }    /**
     * Xóa thành viên khỏi ban
     */
    public boolean removeMemberFromDepartment(String userID, int departmentID) {
        String sql = """
            DELETE uc FROM UserClubs uc
            JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
            WHERE uc.UserID = ? AND cd.DepartmentID = ?
            """;
          try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userID);
            ps.setInt(2, departmentID);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }    /**
     * Tìm kiếm sinh viên để thêm vào ban
     */
    public List<Users> searchStudentsNotInDepartment(int departmentID, String keyword, int limit) {
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
                JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
                WHERE cd.DepartmentID = ?
            )
            ORDER BY u.FullName
            LIMIT ?
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setInt(3, departmentID);
            ps.setInt(4, limit);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Users student = new Users();
                student.setUserID(rs.getString("UserID"));
                student.setFullName(rs.getString("FullName"));
                student.setEmail(rs.getString("Email"));
                student.setAvatar(rs.getString("AvatarSrc"));
                
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }    /**
     * Tìm kiếm sinh viên chưa tham gia ban cụ thể (sử dụng ClubDepartmentID)
     */
    public List<Users> searchStudentsNotInClubDepartment(int clubDepartmentID, String keyword, int limit) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }    /**
     * Thêm thành viên vào ban
     */
    public boolean addMemberToDepartment(String userID, int clubID, int departmentID, int roleID) {
        // First get ClubDepartmentID from clubID and departmentID
        String getClubDeptIdSql = """
            SELECT ClubDepartmentID 
            FROM ClubDepartments 
            WHERE ClubID = ? AND DepartmentID = ?
            """;
        
        String insertSql = """
            INSERT INTO UserClubs (UserID, ClubID, ClubDepartmentID, RoleID, JoinDate, IsActive)
            VALUES (?, ?, ?, ?, NOW(), 1)
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            // Get ClubDepartmentID
            PreparedStatement getPs = conn.prepareStatement(getClubDeptIdSql);
            getPs.setInt(1, clubID);
            getPs.setInt(2, departmentID);
            ResultSet rs = getPs.executeQuery();
            
            if (rs.next()) {
                int clubDepartmentID = rs.getInt("ClubDepartmentID");
                
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
    }    /**
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
        }
        return false;
    }/**
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy Department ID của trưởng ban
     */
    public int getDepartmentIdByLeader(String userID) {
        String sql = """
            SELECT cd.DepartmentID
            FROM UserClubs uc
            JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
            WHERE uc.UserID = ? AND uc.RoleID = 3
            """;
        
        try (Connection conn = DBContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("DepartmentID");
            }
        } catch (SQLException e) {
            e.printStackTrace();        }
        return 0;
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
}
