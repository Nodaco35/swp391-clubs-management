package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Department;
import models.Roles;
import models.UserClub;
import models.Users;

public class UserClubDAO {

    // Phương thức để kiểm tra xem một cột có tồn tại trong ResultSet hay không
    private static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            if (columnName.equalsIgnoreCase(rsmd.getColumnName(i))) {
                return true;
            }
        }
        return false;
    }

    public static List<UserClub> findByUserID(String userID) {
        String sql = """
                    SELECT uc.*, r.RoleName, d.DepartmentName , c.ClubImg, c.ClubName
                                        FROM UserClubs uc
                                        JOIN Clubs c on uc.ClubID = c.ClubID
                                        JOIN Roles r ON uc.RoleID = r.RoleID
                                        JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
                                        JOIN Departments d on cd.DepartmentID = d.DepartmentID
                                        WHERE uc.UserID = ? AND isActive = 1 AND ClubStatus = 1 """;
        List<UserClub> findByUserID = new ArrayList<>();
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserClub uc = new UserClub();
                uc.setUserClubID(rs.getInt("UserClubID"));
                uc.setUserID(rs.getString("UserID"));
                uc.setClubID(rs.getInt("ClubID"));
                uc.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                uc.setRoleID(rs.getInt("RoleID"));
                uc.setJoinDate(rs.getDate("JoinDate"));
                uc.setIsActive(rs.getBoolean("IsActive"));
                uc.setRoleName(rs.getString("RoleName"));
                uc.setDepartmentName(rs.getString("DepartmentName"));
                uc.setClubImg(rs.getString("ClubImg"));
                uc.setClubName(rs.getString("ClubName"));
                if (hasColumn(rs, "Gen")) {
                    uc.setGen(rs.getInt("Gen"));
                }
                findByUserID.add(uc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findByUserID;
    }
    
    public static List<UserClub> findMemberClubsByUserID(String userID) {
        String sql = """
                    SELECT uc.*, r.RoleName, d.DepartmentName , c.ClubImg, c.ClubName
                                        FROM UserClubs uc
                                        JOIN Clubs c on uc.ClubID = c.ClubID
                                        JOIN Roles r ON uc.RoleID = r.RoleID
                                        JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
                                        JOIN Departments d on cd.DepartmentID = d.DepartmentID
                                        WHERE uc.UserID = ? AND isActive = 1 and uc.RoleID = 4 """;
        List<UserClub> findByUserID = new ArrayList<>();
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserClub uc = new UserClub();
                uc.setUserClubID(rs.getInt("UserClubID"));
                uc.setUserID(rs.getString("UserID"));
                uc.setClubID(rs.getInt("ClubID"));
                uc.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                uc.setRoleID(rs.getInt("RoleID"));
                uc.setJoinDate(rs.getDate("JoinDate"));
                uc.setIsActive(rs.getBoolean("IsActive"));
                uc.setRoleName(rs.getString("RoleName"));
                uc.setDepartmentName(rs.getString("DepartmentName"));
                uc.setClubImg(rs.getString("ClubImg"));
                uc.setClubName(rs.getString("ClubName"));
                if (hasColumn(rs, "Gen")) {
                    uc.setGen(rs.getInt("Gen"));
                }
                findByUserID.add(uc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findByUserID;
    }
     public static List<UserClub> findByClubIDAndIsActive(int clubID) {
        List<UserClub> findByClubID = new ArrayList<>();
        String sql = """
                     Select *
                     from Userclubs uc
                     join Clubs c on uc.ClubID = c.ClubID
                     where c.ClubID = ? and uc.Isactive = 1""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserClub uc = new UserClub();
                uc.setUserID(rs.getString("UserID"));
                findByClubID.add(uc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findByClubID;
    }
    public static List<UserClub> findByClubID(int clubID) {
        List<UserClub> findByClubID = new ArrayList<>();
        String sql = """
                     Select *
                     from Userclubs uc
                     join Clubs c on uc.ClubID = c.ClubID
                     where c.ClubID = ?""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserClub uc = new UserClub();
                uc.setUserID(rs.getString("UserID"));
                findByClubID.add(uc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findByClubID;
    }

    public static List<UserClub> findByClubIDAndDepartmentId(int clubID) {
        List<UserClub> findByClubID = new ArrayList<>();
        String sql = """
                     Select *
                     from Userclubs uc
                     join Clubs c on uc.ClubID = c.ClubID
                     where c.ClubID = ? and uc.RoleID = 3""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserClub uc = new UserClub();
                uc.setUserID(rs.getString("UserID"));
                uc.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                findByClubID.add(uc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findByClubID;
    }
    
    

    public boolean isUserMemberOfClub(int clubID, String userID) {
        String sql = "SELECT 1 FROM UserClubs WHERE ClubID = ? AND UserID = ? AND IsActive = 1";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ps.setString(2, userID);
            ResultSet rs = ps.executeQuery();
            return rs.next();
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
                int roleID = rs.getInt("RoleID");
                return roleID == 1 || roleID == 3;
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

    public boolean hasClubPresident(int clubID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT COUNT(*) FROM UserClubs WHERE ClubID = ? AND RoleID = 1 AND IsActive = 1";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, clubID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking club president existence: " + e.getMessage());
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

    public boolean hasDepartmentHead(int clubID, int departmentID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT COUNT(*) \n"
                    + "FROM UserClubs uc\n"
                    + "JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID\n"
                    + "WHERE uc.ClubID = ? \n"
                    + "AND cd.DepartmentID = ? \n"
                    + "AND uc.RoleID = 3 \n"
                    + "AND uc.IsActive = 1;";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, clubID);
            stmt.setInt(2, departmentID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking department head existence: " + e.getMessage());
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
        JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
        JOIN Departments d ON cd.DepartmentID = d.DepartmentID
        WHERE uc.ClubID = ? AND uc.IsActive = 1
        ORDER BY uc.JoinDate DESC
        LIMIT ? OFFSET ?
    """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, clubID);
            stmt.setInt(2, pageSize);
            stmt.setInt(3, (page - 1) * pageSize);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UserClub uc = new UserClub();
                    uc.setUserClubID(rs.getInt("UserClubID"));
                    uc.setUserID(rs.getString("UserID"));
                    uc.setClubID(rs.getInt("ClubID"));
                    uc.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                    uc.setRoleID(rs.getInt("RoleID"));
                    uc.setJoinDate(rs.getDate("JoinDate"));
                    uc.setIsActive(rs.getBoolean("IsActive"));
                    uc.setFullName(rs.getString("FullName"));
                    uc.setRoleName(rs.getString("RoleName"));
                    uc.setDepartmentName(rs.getString("DepartmentName"));
                    if (hasColumn(rs, "Gen")) {
                        uc.setGen(rs.getInt("Gen"));
                    }
                    userClubs.add(uc);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in getAllUserClubsByClubId: " + e.getMessage());
            throw new RuntimeException("Database error occurred while retrieving user clubs", e);
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
        JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
        JOIN Departments d ON cd.DepartmentID = d.DepartmentID
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
                uc.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                uc.setRoleID(rs.getInt("RoleID"));
                uc.setJoinDate(rs.getDate("JoinDate"));
                uc.setIsActive(rs.getBoolean("IsActive"));
                uc.setFullName(rs.getString("FullName"));
                uc.setRoleName(rs.getString("RoleName"));
                uc.setDepartmentName(rs.getString("DepartmentName"));
                if (hasColumn(rs, "Gen")) {
                    uc.setGen(rs.getInt("Gen"));
                }
                userClubs.add(uc);
            }

        } catch (Exception e) {
            System.out.println("Error in searchUserClubsByClubId: " + e.getMessage());
        }

        return userClubs;
    }

    public boolean isUserExists(String userID) {
        String query = "SELECT COUNT(*) FROM Users WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
        }
        return false;
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
                JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
                JOIN Departments d ON cd.DepartmentID = d.DepartmentID
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
                uc.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                uc.setRoleID(rs.getInt("RoleID"));
                uc.setJoinDate(rs.getDate("JoinDate"));
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

    // Get club's founding year
    public int getClubFoundingYear(int clubID) {
        String sql = "SELECT EstablishedDate FROM Clubs WHERE ClubID = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clubID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getDate("EstablishedDate") != null) {
                    return rs.getDate("EstablishedDate").toLocalDate().getYear();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting club established year: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
        }
        return -1;
    }

    public String getClubEstablishedDate(int clubID) {
        String sql = "SELECT EstablishedDate FROM Clubs WHERE ClubID = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clubID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getDate("EstablishedDate") != null) {
                    return rs.getDate("EstablishedDate").toString();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting club established date: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
        }
        return null;
    }

    private int calculateGen(int clubID, java.util.Date joinDate) {
        if (joinDate == null) {
            throw new RuntimeException("JoinDate cannot be null for ClubID: " + clubID);
        }
        int foundingYear = getClubFoundingYear(clubID);
        if (foundingYear == -1) {
            throw new RuntimeException("Cannot determine club established year for ClubID: " + clubID + ". Ensure EstablishedDate is not null.");
        }
        int joinYear = Integer.parseInt(new java.text.SimpleDateFormat("yyyy").format(joinDate));
        int gen = joinYear - foundingYear + 1;
        if (gen < 1) {
            throw new RuntimeException("Invalid Gen calculated: " + gen + ". JoinDate (" + joinDate + ") must not be before EstablishedDate for ClubID: " + clubID);
        }
        return gen;
    }

    public int addUserClub(UserClub uc) {
        String query = """
            INSERT INTO UserClubs (UserID, ClubID, ClubDepartmentID, RoleID, JoinDate, IsActive, Gen)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            int gen = calculateGen(uc.getClubID(), uc.getJoinDate());
            stmt.setString(1, uc.getUserID());
            stmt.setInt(2, uc.getClubID());
            stmt.setInt(3, uc.getClubDepartmentID());
            stmt.setInt(4, uc.getRoleID());
            stmt.setDate(5, new java.sql.Date(uc.getJoinDate().getTime()));
            stmt.setBoolean(6, uc.isIsActive());
            stmt.setInt(7, gen);
            System.out.println("Executing INSERT: userID=" + uc.getUserID() + ", clubID=" + uc.getClubID() + ", clubDepartmentID=" + uc.getClubDepartmentID() + ", roleID=" + uc.getRoleID() + ", joinDate=" + uc.getJoinDate() + ", isActive=" + uc.isIsActive() + ", gen=" + gen);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            } else {
                System.err.println("No rows inserted for UserClub: userID=" + uc.getUserID() + ", clubID=" + uc.getClubID());
            }
        } catch (SQLException e) {
            System.err.println("Error adding user club: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            throw new RuntimeException("Failed to add user club: " + e.getMessage(), e);
        }
        return -1;
    }

    public boolean updateUserClub(UserClub uc) {
        String query = """
            UPDATE UserClubs 
            SET ClubDepartmentID = ?, RoleID = ?, IsActive = ?, JoinDate = ?, Gen = ?
            WHERE UserClubID = ?
        """;
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            int gen = calculateGen(uc.getClubID(), uc.getJoinDate());
            stmt.setInt(1, uc.getClubDepartmentID());
            stmt.setInt(2, uc.getRoleID());
            stmt.setBoolean(3, uc.isIsActive());
            stmt.setDate(4, new java.sql.Date(uc.getJoinDate().getTime()));
            stmt.setInt(5, gen);
            stmt.setInt(6, uc.getUserClubID());
            System.out.println("Executing UPDATE: userClubID=" + uc.getUserClubID() + ", clubDepartmentID=" + uc.getClubDepartmentID() + ", roleID=" + uc.getRoleID() + ", joinDate=" + uc.getJoinDate() + ", isActive=" + uc.isIsActive() + ", gen=" + gen);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user club: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            throw new RuntimeException("Failed to update user club: " + e.getMessage(), e);
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
            String query
                    = """
                    SELECT cd.DepartmentID, d.DepartmentName 
                    FROM ClubDepartments cd
                    JOIN Departments d ON cd.DepartmentID = d.DepartmentID
                    WHERE cd.ClubID = ? AND d.`DepartmentStatus` = 1
                    """;
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
            String query = """
                SELECT uc.*, u.FullName, r.RoleName, d.DepartmentName , d.DepartmentID
                FROM UserClubs uc
                JOIN Users u ON uc.UserID = u.UserID
                JOIN Roles r ON uc.RoleID = r.RoleID
                JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
                JOIN Departments d ON cd.DepartmentID = d.DepartmentID
                WHERE uc.UserID = ? AND uc.ClubID = ? AND uc.IsActive = 1
                ORDER BY uc.RoleID ASC
            """;
            stmt = conn.prepareStatement(query);
            stmt.setString(1, userID);
            stmt.setInt(2, clubID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                userClub = new UserClub();
                userClub.setUserClubID(rs.getInt("UserClubID"));
                userClub.setUserID(rs.getString("UserID"));
                userClub.setClubID(rs.getInt("ClubID"));
                userClub.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                userClub.setRoleID(rs.getInt("RoleID"));
                userClub.setJoinDate(rs.getDate("JoinDate"));
                userClub.setIsActive(rs.getBoolean("IsActive"));
                userClub.setFullName(rs.getString("FullName"));
                userClub.setRoleName(rs.getString("RoleName"));
                userClub.setDepartmentName(rs.getString("DepartmentName"));
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
               uc.ClubDepartmentID,
               uc.RoleID,
               uc.JoinDate,
               uc.IsActive,
               u.FullName,
               r.RoleName,
               d.DepartmentName
        FROM UserClubs uc
        JOIN Users u ON uc.UserID = u.UserID
        JOIN Roles r ON uc.RoleID = r.RoleID
        JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
        JOIN Departments d ON cd.DepartmentID = d.DepartmentID
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
                    member.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                    member.setRoleID(rs.getInt("RoleID"));
                    member.setJoinDate(rs.getDate("JoinDate"));
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
                SELECT uc.UserClubID, uc.UserID, uc.ClubID, uc.ClubDepartmentID, uc.RoleID, 
                       uc.JoinDate, uc.IsActive, u.FullName, r.RoleName, d.DepartmentName
                FROM UserClubs uc
                JOIN Users u ON uc.UserID = u.UserID
                JOIN Roles r ON uc.RoleID = r.RoleID
                JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
                JOIN Departments d ON cd.DepartmentID = d.DepartmentID
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
                userClub.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                userClub.setRoleID(rs.getInt("RoleID"));
                userClub.setJoinDate(rs.getDate("JoinDate"));
                userClub.setIsActive(rs.getBoolean("IsActive"));
                userClub.setFullName(rs.getString("FullName"));
                userClub.setRoleName(rs.getString("RoleName"));
                userClub.setDepartmentName(rs.getString("DepartmentName"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting user club by user ID: " + e.getMessage());
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

    /**
     * Kiểm tra quyền sử dụng chức năng(roleID 1-3) nếu clubId null thì lấy
     * quyền quản lý đầu tiên của user
     */
    public UserClub getUserClubManagementRole(String userID, Integer clubId) {
        UserClub userClub = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query;

            if (clubId != null) {
                query = """
                    SELECT uc.UserClubID, uc.UserID, uc.ClubID, uc.ClubDepartmentID, uc.RoleID, 
                           uc.JoinDate, uc.IsActive, u.FullName, r.RoleName, d.DepartmentName
                    FROM UserClubs uc
                    JOIN Users u ON uc.UserID = u.UserID
                    JOIN Roles r ON uc.RoleID = r.RoleID
                    JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
                    JOIN Departments d ON cd.DepartmentID = d.DepartmentID
                    WHERE uc.UserID = ? AND uc.ClubID = ? AND uc.RoleID BETWEEN 1 AND 3 AND uc.IsActive = 1
                    ORDER BY uc.RoleID ASC
                    LIMIT 1
                """;
                stmt = conn.prepareStatement(query);
                stmt.setString(1, userID);
                stmt.setInt(2, clubId);
            }
            rs = stmt.executeQuery();

            if (rs.next()) {
                userClub = new UserClub();
                userClub.setUserClubID(rs.getInt("UserClubID"));
                userClub.setUserID(rs.getString("UserID"));
                userClub.setClubID(rs.getInt("ClubID"));
                userClub.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
                userClub.setRoleID(rs.getInt("RoleID"));
                userClub.setJoinDate(rs.getDate("JoinDate"));
                userClub.setIsActive(rs.getBoolean("IsActive"));
                userClub.setFullName(rs.getString("FullName"));
                userClub.setRoleName(rs.getString("RoleName"));
                userClub.setDepartmentName(rs.getString("DepartmentName"));
            }
        } catch (SQLException e) {
            System.out.println("Error checking management role in club: " + e.getMessage());
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

    /**
     * Get clubDepartmentID by clubID and departmentID
     */
    public int getClubDepartmentIdByClubAndDepartment(int clubID, int departmentID) {
        String sql = "SELECT ClubDepartmentID FROM ClubDepartments WHERE ClubID = ? AND DepartmentID = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int clubDepartmentID = -1;

        try {
            conn = DBContext.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, clubID);
            stmt.setInt(2, departmentID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                clubDepartmentID = rs.getInt("ClubDepartmentID");
            }
        } catch (SQLException e) {
            System.out.println("Error getting clubDepartmentID: " + e.getMessage());
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

        return clubDepartmentID;
    }

    public static List<UserClub> findByCDID(int clubDepartmentID) {
        List<UserClub> findByCDID = new ArrayList<>();
        String sql = """
                     select * from userclubs uc
                     JOIN clubdepartments cd on uc.ClubDepartmentID = cd.ClubDepartmentID
                     Join departments d on cd.DepartmentID = d.DepartmentID
                     where uc.ClubDepartmentID = ?""";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, clubDepartmentID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserClub uc = new UserClub();

                uc.setUserID(rs.getString("UserID"));
                uc.setDepartmentName(rs.getString("DepartmentName"));
                findByCDID.add(uc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findByCDID;
    }
    public Users getClubLeader(int clubId) {
    String sql = """
        SELECT u.* FROM UserClubs uc 
        JOIN Users u ON uc.UserID = u.UserID 
        WHERE uc.ClubID = ? AND uc.RoleID = 1 AND uc.IsActive = 1
        """;

    try (Connection conn = DBContext.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, clubId);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                Users u = new Users();
                u.setUserID(rs.getString("UserID"));
                u.setFullName(rs.getString("FullName"));
                u.setEmail(rs.getString("Email"));
                return u;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

/**
 * Lấy UserClub của user theo Role cụ thể
 */
public UserClub getUserClubByUserIdAndRole(String userID, String roleName) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    
    try {
        conn = DBContext.getConnection();
        String query = """
            SELECT uc.UserClubID, uc.UserID, uc.ClubID, uc.ClubDepartmentID, uc.RoleID, 
                   uc.JoinDate, uc.IsActive, r.RoleName, d.DepartmentName
            FROM UserClubs uc
            JOIN Roles r ON uc.RoleID = r.RoleID
            JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
            JOIN Departments d ON cd.DepartmentID = d.DepartmentID
            WHERE uc.UserID = ? AND r.RoleName = ? AND uc.IsActive = 1
            LIMIT 1
        """;
        
        stmt = conn.prepareStatement(query);
        stmt.setString(1, userID);
        stmt.setString(2, roleName);
        rs = stmt.executeQuery();
        
        if (rs.next()) {
            UserClub userClub = new UserClub();
            userClub.setUserClubID(rs.getInt("UserClubID"));
            userClub.setUserID(rs.getString("UserID"));
            userClub.setClubID(rs.getInt("ClubID"));
            userClub.setClubDepartmentID(rs.getInt("ClubDepartmentID"));
            userClub.setRoleID(rs.getInt("RoleID"));
            userClub.setJoinDate(rs.getDate("JoinDate"));
            userClub.setIsActive(rs.getBoolean("IsActive"));
            userClub.setRoleName(rs.getString("RoleName"));
            userClub.setDepartmentName(rs.getString("DepartmentName"));
            return userClub;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    return null;
}

/**
 * Lấy danh sách Users trong department theo role
 */
public List<Users> getUsersByDepartmentAndRole(int clubDepartmentID, String roleName) {
    List<Users> users = new ArrayList<>();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    
    try {
        conn = DBContext.getConnection();
        String query;
        
        if (roleName == null || roleName.trim().isEmpty()) {
            // Get all users in department regardless of role
            query = """
                SELECT DISTINCT u.UserID, u.FullName, u.Email
                FROM Users u
                JOIN UserClubs uc ON u.UserID = uc.UserID
                WHERE uc.ClubDepartmentID = ? AND uc.IsActive = 1
                ORDER BY u.FullName
            """;
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, clubDepartmentID);
        } else {
            // Get users by specific role
            query = """
                SELECT DISTINCT u.UserID, u.FullName, u.Email
                FROM Users u
                JOIN UserClubs uc ON u.UserID = uc.UserID
                JOIN Roles r ON uc.RoleID = r.RoleID
                WHERE uc.ClubDepartmentID = ? AND r.RoleName = ? AND uc.IsActive = 1
                ORDER BY u.FullName
            """;
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, clubDepartmentID);
            stmt.setString(2, roleName);
        }
        
        rs = stmt.executeQuery();
        
        while (rs.next()) {
            Users user = new Users();
            user.setUserID(rs.getString("UserID"));
            user.setFullName(rs.getString("FullName"));
            user.setEmail(rs.getString("Email"));
            // Set default avatar if column doesn't exist
            user.setAvatar("Hinh-anh-dai-dien-mac-dinh-Facebook.jpg");
            users.add(user);
        }
        
        System.out.println("DEBUG DAO: Found " + users.size() + " users for clubDepartmentID=" + clubDepartmentID + ", roleName='" + roleName + "'");
        
    } catch (SQLException e) {
        System.err.println("Error in getUsersByDepartmentAndRole: " + e.getMessage());
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    return users;
}
}
