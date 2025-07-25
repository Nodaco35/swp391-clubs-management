package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.ActivedMembers;
import models.ClubDepartment;

/**
 * Data Access Object for the ClubDepartments table
 */
public class ClubDepartmentDAO {

    public static List<ClubDepartment> findByClubId(int clubID) {
        List<ClubDepartment> departments = new ArrayList<>();
        String sql = "SELECT * FROM ClubDepartments cd\n"
                + "Join Departments d on cd.DepartmentID = d.DepartmentID\n"
                + "\n"
                + "where ClubID = ?";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubID);
            ResultSet rs = ps.executeQuery();
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
            e.printStackTrace();
        }
        return departments;
    }

    /**
     * Get all active departments for a specific club
     *
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
            String sql = "SELECT cd.ClubDepartmentID, d.DepartmentID, d.DepartmentName, d.DepartmentStatus, d.Description, cd.ClubID "
                    + "FROM ClubDepartments cd "
                    + "JOIN Departments d ON cd.DepartmentID = d.DepartmentID "
                    + "WHERE cd.ClubID = ? AND d.DepartmentStatus = 1 "
                    + "ORDER BY d.DepartmentName";
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
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    DBContext.closeConnection(conn);
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return departments;
    }

    /**
     * Get all departments for a specific club (both active and inactive)
     *
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
            String sql = "SELECT cd.ClubDepartmentID, d.DepartmentID, d.DepartmentName, d.DepartmentStatus, d.Description, cd.ClubID "
                    + "FROM ClubDepartments cd "
                    + "JOIN Departments d ON cd.DepartmentID = d.DepartmentID "
                    + "WHERE cd.ClubID = ? "
                    + "ORDER BY d.DepartmentName";
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
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    DBContext.closeConnection(conn);
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return departments;
    }

    /**
     * Get club department by its ID
     *
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
            String sql = "SELECT cd.ClubDepartmentID, d.DepartmentID, d.DepartmentName, d.DepartmentStatus, d.Description, cd.ClubID "
                    + "FROM ClubDepartments cd "
                    + "JOIN Departments d ON cd.DepartmentID = d.DepartmentID "
                    + "WHERE cd.ClubDepartmentID = ?";
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
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    DBContext.closeConnection(conn);
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return department;
    }

    public int getClubDepartmentID(String userID, int clubID) {
        String query = "SELECT ClubDepartmentID FROM UserClubs WHERE UserID = ? AND ClubID = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, userID);
            ps.setInt(2, clubID);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("ClubDepartmentID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Trả về -1 nếu không tìm thấy hoặc lỗi
    }

    public List<ActivedMembers> getActiveMembersByClubAndDepartment(int departmentID, String departmentLeaderID) {
        List<ActivedMembers> members = new ArrayList<>();
        String sql = """
                       SELECT amc.UserID, u.FullName, u.Email, amc.ClubID, amc.ActiveDate,  amc.ProgressPoint
                                              FROM ActivedMemberClubs AS amc
                                              JOIN Users AS u ON amc.UserID = u.UserID
                                              JOIN UserClubs AS uc ON amc.UserID = uc.UserID AND amc.ClubID = uc.ClubID
                                              JOIN ClubDepartments AS cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
                                              WHERE uc.ClubDepartmentID = ? AND amc.IsActive = TRUE AND amc.UserID != ? ;
                     """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, departmentID);
            ps.setString(2, departmentLeaderID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ActivedMembers member = new ActivedMembers();
                    member.setUserID(rs.getString("UserID"));
                    member.setFullName(rs.getString("FullName"));
                    member.setEmail(rs.getString("Email"));
                    member.setClubID(rs.getInt("ClubID"));
                    member.setActiveDate(rs.getDate("ActiveDate"));
                    member.setProgressPoint(rs.getInt("ProgressPoint"));
                    members.add(member);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (members.size() == 0) {
            return null;
        } else {
            return members;
        }
    }

    public boolean updateProgressPoint(String userID, int progressPoint) {
        String sql = "UPDATE ActivedMemberClubs "
                + "SET ProgressPoint = ? "
                + "WHERE UserID = ? ";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, progressPoint);
            ps.setString(2, userID);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();  // Ghi log nếu cần
            return false;
        }
    }

    public boolean clearProgressPoint(String userID) {
        String sql = "UPDATE ActivedMemberClubs "
                + "SET ProgressPoint = NULL "
                + "WHERE UserID = ? ";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userID);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all active departments for a specific club except for department of
     * director
     *
     * @param clubId The ID of the club
     * @return List of active ClubDepartment objects
     */
    public List<ClubDepartment> getCanRegisterClubDepartments(int clubId) {
        List<ClubDepartment> departments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String sql = "SELECT cd.ClubDepartmentID, d.DepartmentID, d.DepartmentName, d.DepartmentStatus, d.Description, cd.ClubID "
                    + "FROM ClubDepartments cd "
                    + "JOIN Departments d ON cd.DepartmentID = d.DepartmentID "
                    + "WHERE cd.ClubID = ? AND d.DepartmentStatus = 1 AND d.DepartmentID <> 3 "
                    + "ORDER BY d.DepartmentName";

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
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    DBContext.closeConnection(conn);
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return departments;
    }

    public int getClubIDByClubDepartmentID(int clubDepartmentID_) {
        String sql = "select ClubID from clubdepartments where ClubDepartmentID = ? ;";
        
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubDepartmentID_);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("ClubID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; 
    }

}
