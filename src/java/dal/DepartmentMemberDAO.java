package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.DepartmentMember;

/**
 * Data Access Object for managing department members
 */
public class DepartmentMemberDAO {
    
    /**
     * Get all members of a specific department
     * @param clubDepartmentID The ID of the club department
     * @return List of department members
     */
    public List<DepartmentMember> getAllDepartmentMembers(int clubDepartmentID) {
        List<DepartmentMember> members = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT uc.UserClubID, uc.UserID, uc.ClubID, uc.ClubDepartmentID, cd.DepartmentID, " +
                         "uc.RoleID, r.RoleName, u.FullName, u.Email, u.AvatarSrc, uc.JoinDate, uc.IsActive, " +
                         "d.DepartmentName " +
                         "FROM UserClubs uc " +
                         "JOIN Users u ON uc.UserID = u.UserID " +
                         "JOIN Roles r ON uc.RoleID = r.RoleID " +
                         "JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID " +
                         "JOIN Departments d ON cd.DepartmentID = d.DepartmentID " +
                         "WHERE uc.ClubDepartmentID = ? " +
                         "ORDER BY uc.RoleID, u.FullName";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, clubDepartmentID);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                DepartmentMember member = extractMemberFromResultSet(rs);
                members.add(member);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching department members: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return members;
    }
    
    /**
     * Get all members for a leader's department
     * @param leaderUserID The user ID of the department leader
     * @param clubID The club ID
     * @return List of department members
     */
    public List<DepartmentMember> getLeaderDepartmentMembers(String leaderUserID, int clubID) {
        List<DepartmentMember> members = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            // First find the department where the user is a leader
            String sql = "SELECT ClubDepartmentID FROM UserClubs " +
                         "WHERE UserID = ? AND ClubID = ? AND RoleID = 3"; // RoleID 3 is for Department Leader
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, leaderUserID);
            stmt.setInt(2, clubID);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                int departmentID = rs.getInt("ClubDepartmentID");
                closeResources(rs, stmt, null);
                
                // Now get all members of this department
                sql = "SELECT uc.UserClubID, uc.UserID, uc.ClubID, uc.ClubDepartmentID, cd.DepartmentID, " +
                      "uc.RoleID, r.RoleName, u.FullName, u.Email, u.AvatarSrc, uc.JoinDate, uc.IsActive, " +
                      "d.DepartmentName " +
                      "FROM UserClubs uc " +
                      "JOIN Users u ON uc.UserID = u.UserID " +
                      "JOIN Roles r ON uc.RoleID = r.RoleID " +
                      "JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID " +
                      "JOIN Departments d ON cd.DepartmentID = d.DepartmentID " +
                      "WHERE uc.ClubDepartmentID = ? " +
                      "ORDER BY uc.RoleID, u.FullName";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, departmentID);
                rs = stmt.executeQuery();
                
                while (rs.next()) {
                    DepartmentMember member = extractMemberFromResultSet(rs);
                    members.add(member);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching leader's department members: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return members;
    }
    
    /**
     * Get member details by userClubID
     * @param userClubID The user club ID
     * @return DepartmentMember object or null if not found
     */
    public DepartmentMember getDepartmentMemberById(int userClubID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        DepartmentMember member = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT uc.UserClubID, uc.UserID, uc.ClubID, uc.ClubDepartmentID, cd.DepartmentID, " +
                         "uc.RoleID, r.RoleName, u.FullName, u.Email, u.AvatarSrc, uc.JoinDate, uc.IsActive, " +
                         "d.DepartmentName " +
                         "FROM UserClubs uc " +
                         "JOIN Users u ON uc.UserID = u.UserID " +
                         "JOIN Roles r ON uc.RoleID = r.RoleID " +
                         "JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID " +
                         "JOIN Departments d ON cd.DepartmentID = d.DepartmentID " +
                         "WHERE uc.UserClubID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userClubID);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                member = extractMemberFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching department member: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return member;
    }
    
    /**
     * Update member information (excluding role)
     * @param member The member with updated information
     * @return true if update was successful, false otherwise
     */
    public boolean updateMemberInfo(DepartmentMember member) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBContext.getConnection();
            // We only update the active status in UserClubs table
            String sql = "UPDATE UserClubs SET IsActive = ? WHERE UserClubID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, member.isActive());
            stmt.setInt(2, member.getUserClubID());
            
            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
            
            // Update basic info in Users table if needed
            if (success) {
                closeResources(null, stmt, null);
                sql = "UPDATE Users SET FullName = ?, Email = ? WHERE UserID = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, member.getFullName());
                stmt.setString(2, member.getEmail());
                stmt.setString(3, member.getUserID());
                
                rowsAffected = stmt.executeUpdate();
                success = rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error updating member info: " + e.getMessage());
            success = false;
        } finally {
            closeResources(null, stmt, conn);
        }
        
        return success;
    }
    
    /**
     * Add a new member to the department
     * @param member The member to add
     * @return true if addition was successful, false otherwise
     */
    public boolean addMember(DepartmentMember member) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBContext.getConnection();
            String sql = "INSERT INTO UserClubs (UserID, ClubID, ClubDepartmentID, RoleID, JoinDate, IsActive) " +
                         "VALUES (?, ?, ?, ?, NOW(), ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, member.getUserID());
            stmt.setInt(2, member.getClubID());
            stmt.setInt(3, member.getClubDepartmentID());
            stmt.setInt(4, member.getRoleID());
            stmt.setBoolean(5, member.isActive());
            
            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error adding member: " + e.getMessage());
            success = false;
        } finally {
            closeResources(null, stmt, conn);
        }
        
        return success;
    }
    
    /**
     * Remove a member from the department
     * @param userClubID The user club ID to remove
     * @return true if removal was successful, false otherwise
     */
    public boolean removeMember(int userClubID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBContext.getConnection();
            String sql = "DELETE FROM UserClubs WHERE UserClubID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userClubID);
            
            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error removing member: " + e.getMessage());
            success = false;
        } finally {
            closeResources(null, stmt, conn);
        }
        
        return success;
    }
    
    /**
     * Get all members of a specific department by departmentID
     * @param departmentID The ID of the department
     * @return List of department members
     */
    public List<DepartmentMember> getDepartmentMembers(int departmentID) {
        List<DepartmentMember> members = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT uc.UserClubID, uc.UserID, uc.ClubID, uc.ClubDepartmentID, cd.DepartmentID, " +
                         "uc.RoleID, r.RoleName, u.FullName, u.Email, u.AvatarSrc, uc.JoinDate, uc.IsActive, " +
                         "d.DepartmentName " +
                         "FROM UserClubs uc " +
                         "JOIN Users u ON uc.UserID = u.UserID " +
                         "JOIN Roles r ON uc.RoleID = r.RoleID " +
                         "JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID " +
                         "JOIN Departments d ON cd.DepartmentID = d.DepartmentID " +
                         "WHERE cd.DepartmentID = ? " +
                         "ORDER BY uc.RoleID, u.FullName";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departmentID);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                DepartmentMember member = extractMemberFromResultSet(rs);
                members.add(member);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching department members by departmentID: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return members;
    }
    
    /**
     * Check if a user is a department leader
     * @param userID The user ID
     * @param departmentID The department ID
     * @return true if the user is a leader of the department, false otherwise
     */    public boolean isUserDepartmentLeader(String userID, int departmentID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean isLeader = false;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT 1 FROM UserClubs uc " +
                         "JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID " +
                         "WHERE uc.UserID = ? AND cd.DepartmentID = ? AND uc.RoleID = 3"; // RoleID 3 is for Department Leader
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userID);
            stmt.setInt(2, departmentID);
            rs = stmt.executeQuery();
            
            isLeader = rs.next(); // If there is a row, user is leader
        } catch (SQLException e) {
            System.out.println("Error checking if user is department leader: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return isLeader;
    }
    
    /**
     * Extract member data from ResultSet
     * @param rs The ResultSet containing member data
     * @return DepartmentMember object
     * @throws SQLException if error occurs while accessing ResultSet
     */
    private DepartmentMember extractMemberFromResultSet(ResultSet rs) throws SQLException {
        DepartmentMember member = new DepartmentMember();
        member.setUserClubID(rs.getInt("UserClubID"));
        member.setUserID(rs.getString("UserID"));
        member.setClubID(rs.getInt("ClubID"));
        member.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
        member.setDepartmentID(rs.getInt("DepartmentID"));
        member.setRoleID(rs.getInt("RoleID"));
        member.setRoleName(rs.getString("RoleName"));
        member.setFullName(rs.getString("FullName"));
        member.setEmail(rs.getString("Email"));
        member.setJoinDate(rs.getDate("JoinDate"));
        member.setActive(rs.getBoolean("IsActive"));
        member.setDepartmentName(rs.getString("DepartmentName"));
        member.setAvatarUrl(rs.getString("AvatarSrc"));
        return member;
    }
    
    /**
     * Close database resources
     * @param rs ResultSet to close
     * @param stmt PreparedStatement to close
     * @param conn Connection to close
     */
    private void closeResources(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DBContext.closeConnection(conn);
        } catch (SQLException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }
}
