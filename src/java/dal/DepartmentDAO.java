package dal;

import models.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public Department getDepartmentByID(int departmentID) {
        String sql = "select * from Departments where departmentID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, departmentID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Department d = new Department();
                d.setDepartmentID(rs.getInt("departmentID"));
                d.setDepartmentName(rs.getString("departmentName"));
                d.setDepartmentStatus(rs.getBoolean("departmentStatus"));
                d.setDescription(rs.getString("description"));
                return d;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Department> getDepartmentsByClubID(int clubID) {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT d.* FROM Departments d "
                + "JOIN ClubDepartments cd ON d.DepartmentID = cd.DepartmentID "
                + "WHERE cd.ClubID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Department d = new Department();
                    d.setDepartmentID(rs.getInt("DepartmentID"));
                    d.setDepartmentName(rs.getString("DepartmentName"));
                    d.setDepartmentStatus(rs.getBoolean("DepartmentStatus"));
                    d.setDescription(rs.getString("Description"));
                    departments.add(d);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return departments;
    }

    public List<Department> findByClubID(int clubID) {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT d.* FROM Departments d "
                + "JOIN ClubDepartments cd ON d.DepartmentID = cd.DepartmentID "
                + "WHERE cd.ClubID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Department d = new Department();
                    d.setDepartmentID(rs.getInt("DepartmentID"));
                    d.setDepartmentName(rs.getString("DepartmentName"));
                    d.setDepartmentStatus(rs.getBoolean("DepartmentStatus"));
                    d.setDescription(rs.getString("Description"));
                    departments.add(d);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return departments;
    }

}
