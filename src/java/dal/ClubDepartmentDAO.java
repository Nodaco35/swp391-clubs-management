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
     */    public List<ClubDepartment> getActiveClubDepartments(int clubId) {
        List<ClubDepartment> departments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT cd.ClubDepartmentID, d.DepartmentID, d.DepartmentName, d.DepartmentStatus, d.Description, cd.ClubID " +
                         "FROM ClubDepartments cd " +
                         "JOIN Departments d ON cd.DepartmentID = d.DepartmentID " +
                         "WHERE cd.ClubID = ? AND d.DepartmentStatus = 1 " +
                         "ORDER BY d.DepartmentName";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, clubId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                ClubDepartment department = new ClubDepartment();
                department.setClubDepartmentId(rs.getInt("ClubDepartmentID"));
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
     */    public List<ClubDepartment> getAllClubDepartments(int clubId) {
        List<ClubDepartment> departments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT cd.ClubDepartmentID, d.DepartmentID, d.DepartmentName, d.DepartmentStatus, d.Description, cd.ClubID " +
                         "FROM ClubDepartments cd " +
                         "JOIN Departments d ON cd.DepartmentID = d.DepartmentID " +
                         "WHERE cd.ClubID = ? " +
                         "ORDER BY d.DepartmentName";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, clubId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                ClubDepartment department = new ClubDepartment();
                department.setClubDepartmentId(rs.getInt("ClubDepartmentID"));
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
     * Get club department by its ID
     * @param clubDepartmentId The ID of the club department
     * @return ClubDepartment object or null if not found
     */
    public ClubDepartment getDepartmentById(int clubDepartmentId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ClubDepartment department = null;
        
        try {
            conn = DBContext.getConnection();
            String sql = "SELECT cd.ClubDepartmentID, d.DepartmentID, d.DepartmentName, d.DepartmentStatus, d.Description, cd.ClubID " +
                         "FROM ClubDepartments cd " +
                         "JOIN Departments d ON cd.DepartmentID = d.DepartmentID " +
                         "WHERE cd.ClubDepartmentID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, clubDepartmentId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                department = new ClubDepartment();
                department.setClubDepartmentId(rs.getInt("ClubDepartmentID"));
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
