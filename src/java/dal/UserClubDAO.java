package dal;

import models.UserClub;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Department;
import models.Roles;

public class UserClubDAO {

    public boolean isUserMemberOfClub(int clubID, String userID) {
        String sql = "SELECT 1 FROM UserClubs WHERE ClubID = ? AND UserID = ? AND IsActive = 1";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ps.setString(2, userID);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // true nếu là thành viên
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isClubPresident(String userID, int clubID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT RoleID FROM UserClubs WHERE UserID = ? AND ClubID = ? AND IsActive = 1";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, userID);
            stmt.setInt(2, clubID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("RoleID") == 1;
            }
        } catch (SQLException e) {
            System.out.println("Error checking club president: " + e.getMessage());
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
        return false;
    }
    
    public boolean isUserClubExists(String userID, int clubID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT COUNT(*) FROM UserClubs WHERE UserID = ? AND ClubID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, userID);
            stmt.setInt(2, clubID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking user club existence: " + e.getMessage());
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
        return false;
    }

    public List<UserClub> getAllUserClubsByClubId(int clubID, int page, int pageSize) {
        List<UserClub> userClubs = new ArrayList<>();
        String query = """
        SELECT uc.*, u.FullName, r.RoleName, d.DepartmentName 
        FROM UserClubs uc
        JOIN Users u ON uc.UserID = u.UserID
        JOIN Roles r ON uc.RoleID = r.RoleID
        JOIN ClubDepartments d ON uc.DepartmentID = d.DepartmentID
        WHERE uc.ClubID = ? AND uc.IsActive = 1
        ORDER BY uc.JoinDate DESC
        LIMIT ? OFFSET ?
    """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, clubID);
            stmt.setInt(2, pageSize);
            stmt.setInt(3, (page - 1) * pageSize);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UserClub uc = new UserClub();
                uc.setUserClubID(rs.getInt("UserClubID"));
                uc.setUserID(rs.getString("UserID"));
                uc.setClubID(rs.getInt("ClubID"));
                uc.setDepartmentID(rs.getInt("DepartmentID"));
                uc.setRoleID(rs.getInt("RoleID"));
                uc.setJoinDate(rs.getTimestamp("JoinDate"));
                uc.setIsActive(rs.getBoolean("IsActive"));
                uc.setFullName(rs.getString("FullName"));
                //uc.setEmail(rs.getString("Email"));
                uc.setRoleName(rs.getString("RoleName"));
                uc.setDepartmentName(rs.getString("DepartmentName"));
                userClubs.add(uc);
            }

        } catch (Exception e) {
            System.out.println("Error in getAllUserClubsByClubId: " + e.getMessage());
        }

        return userClubs;
    }
    
    public String getClubNameById(int clubID) {
    String clubName = null;
    String query = "SELECT ClubName FROM Clubs WHERE ClubID = ?";

    try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, clubID);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            clubName = rs.getString("ClubName");
        }
    } catch (Exception e) {
        System.out.println("Error in getClubNameById: " + e.getMessage());
    }
    return clubName;
}

    public List<UserClub> searchUserClubsByClubId(int clubID, String search, int page, int pageSize) {
        List<UserClub> userClubs = new ArrayList<>();
        String query = """
        SELECT uc.*, u.FullName, r.RoleName, d.DepartmentName 
        FROM UserClubs uc
        JOIN Users u ON uc.UserID = u.UserID
        JOIN Roles r ON uc.RoleID = r.RoleID
        JOIN ClubDepartments d ON uc.DepartmentID = d.DepartmentID
        WHERE uc.ClubID = ? AND uc.IsActive = 1
        AND (u.FullName LIKE ? OR uc.UserID LIKE ?)
        ORDER BY uc.JoinDate DESC
        LIMIT ? OFFSET ?
    """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, clubID);
            stmt.setString(2, "%" + search + "%");
            stmt.setString(3, "%" + search + "%");
            stmt.setInt(4, pageSize);
            stmt.setInt(5, (page - 1) * pageSize);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UserClub uc = new UserClub();
                uc.setUserClubID(rs.getInt("UserClubID"));
                uc.setUserID(rs.getString("UserID"));
                uc.setClubID(rs.getInt("ClubID"));
                uc.setDepartmentID(rs.getInt("DepartmentID"));
                uc.setRoleID(rs.getInt("RoleID"));
                uc.setJoinDate(rs.getTimestamp("JoinDate"));
                uc.setIsActive(rs.getBoolean("IsActive"));
                uc.setFullName(rs.getString("FullName"));
                uc.setRoleName(rs.getString("RoleName"));
                uc.setDepartmentName(rs.getString("DepartmentName"));
                userClubs.add(uc);
            }

        } catch (Exception e) {
            System.out.println("Error in searchUserClubsByClubId: " + e.getMessage());
        }

        return userClubs;
    }

    public int countUserClubs(int clubID, String search) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = DBContext.getConnection();
            String query = """
                SELECT COUNT(*) 
                FROM UserClubs uc
                JOIN Users u ON uc.UserID = u.UserID
                WHERE uc.ClubID = ? AND uc.IsActive = 1
                AND (u.FullName LIKE ? OR uc.UserID LIKE ?)
            """;
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, clubID);
            stmt.setString(2, "%" + search + "%");
            stmt.setString(3, "%" + search + "%");
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error counting user clubs: " + e.getMessage());
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
        return count;
    }

    public UserClub getUserClubById(int userClubID) {
        UserClub uc = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = """
                SELECT uc.*, u.FullName, r.RoleName, d.DepartmentName 
                FROM UserClubs uc
                JOIN Users u ON uc.UserID = u.UserID
                JOIN Roles r ON uc.RoleID = r.RoleID
                JOIN ClubDepartments d ON uc.DepartmentID = d.DepartmentID
                WHERE uc.UserClubID = ?
            """;
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, userClubID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                uc = new UserClub();
                uc.setUserClubID(rs.getInt("UserClubID"));
                uc.setUserID(rs.getString("UserID"));
                uc.setClubID(rs.getInt("ClubID"));
                uc.setDepartmentID(rs.getInt("DepartmentID"));
                uc.setRoleID(rs.getInt("RoleID"));
                uc.setJoinDate(rs.getTimestamp("JoinDate"));
                uc.setIsActive(rs.getBoolean("IsActive"));
                uc.setFullName(rs.getString("FullName"));
                uc.setRoleName(rs.getString("RoleName"));
                uc.setDepartmentName(rs.getString("DepartmentName"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting user club by ID: " + e.getMessage());
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
        return uc;
    }

    public int addUserClub(UserClub uc) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        int generatedId = -1;

        try {
            conn = DBContext.getConnection();
            String query = """
                INSERT INTO UserClubs (UserID, ClubID, DepartmentID, RoleID, JoinDate, IsActive)
                VALUES (?, ?, ?, ?, NOW(), ?)
            """;
            stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, uc.getUserID());
            stmt.setInt(2, uc.getClubID());
            stmt.setInt(3, uc.getDepartmentID());
            stmt.setInt(4, uc.getRoleID());
            stmt.setBoolean(5, uc.isIsActive());
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding user club: " + e.getMessage());
        } finally {
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
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
        return generatedId;
    }

    public boolean updateUserClub(UserClub uc) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBContext.getConnection();
            String query = """
                UPDATE UserClubs 
                SET DepartmentID = ?, RoleID = ?, IsActive = ?
                WHERE UserClubID = ?
            """;
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, uc.getDepartmentID());
            stmt.setInt(2, uc.getRoleID());
            stmt.setBoolean(3, uc.isIsActive());
            stmt.setInt(4, uc.getUserClubID());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error updating user club: " + e.getMessage());
            return false;
        } finally {
            try {
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
    }

    public boolean deleteUserClub(int userClubID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBContext.getConnection();
            String query = "DELETE FROM UserClubs WHERE UserClubID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, userClubID);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting user club: " + e.getMessage());
            return false;
        } finally {
            try {
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
    }

    public List<Department> getDepartmentsByClubId(int clubID) {
        List<Department> departments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT DepartmentID, DepartmentName FROM ClubDepartments WHERE ClubID = ? AND DepartmentStatus = 1;";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, clubID);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Department dept = new Department();
                dept.setDepartmentID(rs.getInt("DepartmentID"));
                dept.setDepartmentName(rs.getString("DepartmentName"));
                departments.add(dept);
            }
        } catch (SQLException e) {
            System.out.println("Error getting departments: " + e.getMessage());
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

    public List<Roles> getRoles() {
        List<Roles> roles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT RoleID, RoleName FROM Roles";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Roles role = new Roles();
                role.setRoleID(rs.getInt("RoleID"));
                role.setRoleName(rs.getString("RoleName"));
                roles.add(role);
            }
        } catch (SQLException e) {
            System.out.println("Error getting roles: " + e.getMessage());
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
        return roles;
    }

    public UserClub getUserClub(String userID, int clubID) {
        UserClub userClub = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT * FROM UserClubs WHERE UserID = ? AND ClubID = ? AND IsActive = 1";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, userID);
            stmt.setInt(2, clubID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                userClub = new UserClub();
                userClub.setUserClubID(rs.getInt("UserClubID"));
                userClub.setUserID(rs.getString("UserID"));
                userClub.setClubID(rs.getInt("ClubID"));
                userClub.setDepartmentID(rs.getInt("DepartmentID"));
                userClub.setRoleID(rs.getInt("RoleID"));
                userClub.setJoinDate(rs.getTimestamp("JoinDate"));
                userClub.setIsActive(rs.getBoolean("IsActive"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting user club: " + e.getMessage());
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
        return userClub;
    }

    public List<UserClub> searchUserClubsByKeyWord(int clubID, String keyWords, int page, int pageSize) {
        List<UserClub> members = new ArrayList<>();
        String sql = """
        SELECT uc.UserClubID,
               uc.UserID,
               uc.ClubID,
               uc.DepartmentID,
               uc.RoleID,
               uc.JoinDate,
               uc.IsActive,
               u.FullName,
               r.RoleName,
               d.DepartmentName
        FROM UserClubs uc
        JOIN Users u ON uc.UserID = u.UserID
        JOIN Roles r ON uc.RoleID = r.RoleID
        JOIN ClubDepartments d ON uc.DepartmentID = d.DepartmentID
        WHERE uc.ClubID = ?
          AND u.FullName LIKE ?
        ORDER BY uc.JoinDate DESC
        LIMIT ? OFFSET ?
    """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String keySearch = "%" + keyWords + "%";
            ps.setInt(1, clubID);
            ps.setString(2, keySearch);
            ps.setInt(3, pageSize);
            ps.setInt(4, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserClub member = new UserClub();
                    member.setUserClubID(rs.getInt("UserClubID"));
                    member.setUserID(rs.getString("UserID"));
                    member.setClubID(rs.getInt("ClubID"));
                    member.setDepartmentID(rs.getInt("DepartmentID"));
                    member.setRoleID(rs.getInt("RoleID"));
                    member.setJoinDate(rs.getTimestamp("JoinDate"));
                    member.setIsActive(rs.getBoolean("IsActive"));
                    member.setFullName(rs.getString("FullName"));
                    member.setRoleName(rs.getString("RoleName"));
                    member.setDepartmentName(rs.getString("DepartmentName"));
                    members.add(member);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in searchUserClubsByKeyWord: " + e.getMessage());
        }
        return members;
    }

    public UserClub getUserClubByUserId(String userID) {
        UserClub userClub = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = """
                SELECT uc.UserClubID, uc.UserID, uc.ClubID, uc.DepartmentID, uc.RoleID, 
                       uc.JoinDate, uc.IsActive, u.FullName, r.RoleName, cd.DepartmentName
                FROM UserClubs uc
                JOIN Users u ON uc.UserID = u.UserID
                JOIN Roles r ON uc.RoleID = r.RoleID
                JOIN ClubDepartments cd ON uc.DepartmentID = cd.DepartmentID
                WHERE uc.UserID = ? AND uc.IsActive = 1
                ORDER BY uc.RoleID ASC
                LIMIT 1
            """;
            stmt = conn.prepareStatement(query);
            stmt.setString(1, userID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                userClub = new UserClub();
                userClub.setUserClubID(rs.getInt("UserClubID"));
                userClub.setUserID(rs.getString("UserID"));
                userClub.setClubID(rs.getInt("ClubID"));
                userClub.setDepartmentID(rs.getInt("DepartmentID"));
                userClub.setRoleID(rs.getInt("RoleID"));
                userClub.setJoinDate(rs.getTimestamp("JoinDate"));
                userClub.setIsActive(rs.getBoolean("IsActive"));
                userClub.setFullName(rs.getString("FullName"));
                userClub.setRoleName(rs.getString("RoleName"));
                userClub.setDepartmentName(rs.getString("DepartmentName"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting user club by user ID: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return userClub;
    }

    public boolean hasManagementRole(String userID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = """
                SELECT COUNT(*) as count
                FROM UserClubs uc
                WHERE uc.UserID = ? AND uc.RoleID BETWEEN 1 AND 5 AND uc.IsActive = 1
            """;
            stmt = conn.prepareStatement(query);
            stmt.setString(1, userID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking management role: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return false;
    }
}
