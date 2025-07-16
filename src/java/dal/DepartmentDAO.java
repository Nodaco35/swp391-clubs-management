package dal;

import models.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
