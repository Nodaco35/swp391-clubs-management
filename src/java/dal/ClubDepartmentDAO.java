package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.ClubDepartment;

/**
 * Data Access Object for the ClubDepartments table
 */
public class ClubDepartmentDAO {
    
    /**
     * Get all active departments for a specific club
     * @param clubId The ID of the club
     * @return List of active ClubDepartment objects
     */
    public List<ClubDepartment> getActiveClubDepartments(int clubId) {
        List<ClubDepartment> departments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT DepartmentID, DepartmentName, DepartmentStatus, Description, ClubID " +
                         "FROM ClubDepartments " +
                         "WHERE ClubID = ? AND DepartmentStatus = 1 " +
                         "ORDER BY DepartmentName";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, clubId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                ClubDepartment department = new ClubDepartment();
                department.setDepartmentId(rs.getInt("DepartmentID"));
                department.setDepartmentName(rs.getString("DepartmentName"));
                department.setDepartmentStatus(rs.getBoolean("DepartmentStatus"));
                department.setDescription(rs.getString("Description"));
                department.setClubId(rs.getInt("ClubID"));
                departments.add(department);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching club departments: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return departments;
    }
    
    /**
     * Get all departments for a specific club (both active and inactive)
     * @param clubId The ID of the club
     * @return List of all ClubDepartment objects
     */
    public List<ClubDepartment> getAllClubDepartments(int clubId) {
        List<ClubDepartment> departments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT DepartmentID, DepartmentName, DepartmentStatus, Description, ClubID " +
                         "FROM ClubDepartments " +
                         "WHERE ClubID = ? " +
                         "ORDER BY DepartmentName";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, clubId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                ClubDepartment department = new ClubDepartment();
                department.setDepartmentId(rs.getInt("DepartmentID"));
                department.setDepartmentName(rs.getString("DepartmentName"));
                department.setDepartmentStatus(rs.getBoolean("DepartmentStatus"));
                department.setDescription(rs.getString("Description"));
                department.setClubId(rs.getInt("ClubID"));
                departments.add(department);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all club departments: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return departments;
    }
    
    /**
     * Get department by its ID
     * @param departmentId The ID of the department
     * @return ClubDepartment object or null if not found
     */
    public ClubDepartment getDepartmentById(int departmentId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ClubDepartment department = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT DepartmentID, DepartmentName, DepartmentStatus, Description, ClubID " +
                         "FROM ClubDepartments " +
                         "WHERE DepartmentID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departmentId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                department = new ClubDepartment();
                department.setDepartmentId(rs.getInt("DepartmentID"));
                department.setDepartmentName(rs.getString("DepartmentName"));
                department.setDepartmentStatus(rs.getBoolean("DepartmentStatus"));
                department.setDescription(rs.getString("Description"));
                department.setClubId(rs.getInt("ClubID"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching department by ID: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return department;
    }
}
