package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.ClubInfo;
import models.Clubs;
import models.Department;

public class ClubDAO {

    public static List<Clubs> findByUserIDAndChairman(String userID) {
        List<Clubs> findByUserIDAndChairman = new ArrayList<>();
        String sql = "Select *\n"
                + "from Clubs c \n"
                + "join UserClubs uc on c.ClubID = uc.ClubID\n"
                + "where uc.UserID = ? and RoleID = 1";
        try {
            PreparedStatement ps = DBContext.getConnection().prepareStatement(sql);
            ps.setObject(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Clubs c = new Clubs();
                c.setClubID(rs.getInt("ClubID"));
                c.setClubName(rs.getString("ClubName"));
                findByUserIDAndChairman.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findByUserIDAndChairman;

    }

    public boolean isClubNameTaken(String clubName, int excludeClubID) {
        String query = "SELECT COUNT(*) FROM Clubs WHERE ClubName = ? AND ClubID != ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, clubName.trim());
            stmt.setInt(2, excludeClubID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ClubInfo getClubChairman(String userID) {
        String sql = "SELECT c.ClubID, c.ClubName, c.ClubImg, u.FullName AS ClubChairmanName "
                + "FROM UserClubs uc "
                + "JOIN Clubs c ON uc.ClubID = c.ClubID "
                + "JOIN Users u ON uc.UserID = u.UserID "
                + "WHERE uc.UserID = ? AND uc.RoleID = 1 AND uc.IsActive = 1";

        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ClubInfo club = new ClubInfo();
                club.setClubID(rs.getInt("ClubID"));
                club.setClubName(rs.getString("ClubName"));
                club.setClubImg(rs.getString("ClubImg"));
                club.setClubChairmanName(rs.getString("ClubChairmanName"));
                return club;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Clubs getCLubByID(int clubID) {
        String sql = "SELECT c.*, cc.CategoryName "
                + "FROM Clubs c "
                + "LEFT JOIN ClubCategories cc ON c.CategoryID = cc.CategoryID "
                + "WHERE c.ClubID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Clubs club = new Clubs();
                    club.setClubID(rs.getInt("ClubID"));
                    club.setClubImg(rs.getString("ClubImg"));
                    club.setIsRecruiting(rs.getBoolean("IsRecruiting"));
                    club.setClubName(rs.getString("ClubName"));
                    club.setDescription(rs.getString("Description"));
                    club.setEstablishedDate(rs.getDate("EstablishedDate"));
                    club.setContactPhone(rs.getString("ContactPhone"));
                    club.setContactGmail(rs.getString("ContactGmail"));
                    club.setContactURL(rs.getString("ContactURL"));
                    club.setClubStatus(rs.getBoolean("ClubStatus"));
                    club.setCategoryID(rs.getInt("CategoryID"));
                    club.setCategoryName(rs.getString("CategoryName"));
                    return club;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public int getClubIDByUserID(String userID) {
        String sql = "SELECT ClubID FROM UserClubs WHERE UserID = ? AND RoleID = 1 AND IsActive = 1";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("ClubID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getTotalClubMembers(int clubID) {
        String sql = "SELECT COUNT(*) FROM UserClubs WHERE ClubID = ? AND IsActive = 1";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalDepartments(int clubID) {
        String sql = "SELECT COUNT(*) FROM ClubDepartments WHERE ClubID = ?";
        try {
            Connection connection = DBContext.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Clubs> getFeaturedClubs(int limit) {
        List<Clubs> clubs = new ArrayList<>();
        String query = "SELECT c.*, cc.CategoryName, "
                + "(SELECT COUNT(*) FROM UserClubs uc WHERE uc.ClubID = c.ClubID AND uc.IsActive = 1) AS MemberCount "
                + "FROM Clubs c "
                + "LEFT JOIN ClubCategories cc ON c.CategoryID = cc.CategoryID "
                + "WHERE c.ClubStatus = 1 "
                + "ORDER BY MemberCount DESC "
                + "LIMIT ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Clubs club = new Clubs();
                    club.setClubID(rs.getInt("ClubID"));
                    club.setClubImg(rs.getString("ClubImg"));
                    club.setIsRecruiting(rs.getBoolean("IsRecruiting"));
                    club.setClubName(rs.getString("ClubName"));
                    club.setDescription(rs.getString("Description"));
                    club.setEstablishedDate(rs.getDate("EstablishedDate"));
                    club.setContactPhone(rs.getString("ContactPhone"));
                    club.setContactGmail(rs.getString("ContactGmail"));
                    club.setContactURL(rs.getString("ContactURL"));
                    club.setClubStatus(rs.getBoolean("ClubStatus"));
                    club.setCategoryID(rs.getInt("CategoryID"));
                    club.setCategoryName(rs.getString("CategoryName"));
                    club.setMemberCount(rs.getInt("MemberCount"));
                    clubs.add(club);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting featured clubs: " + e.getMessage());
        }
        return clubs;
    }

    public List<Clubs> getClubsByCategory(int categoryID, int page, int pageSize) {
        List<Clubs> clubs = new ArrayList<>();
        String query;
        int offset = (page - 1) * pageSize;
        try (Connection conn = DBContext.getConnection()) {
            if (categoryID == 0) { // Assuming 0 means "all" categories
                query = "SELECT c.*, cc.CategoryName, "
                        + "(SELECT COUNT(*) FROM UserClubs uc WHERE uc.ClubID = c.ClubID AND uc.IsActive = 1) AS MemberCount "
                        + "FROM Clubs c "
                        + "LEFT JOIN ClubCategories cc ON c.CategoryID = cc.CategoryID "
                        + "WHERE c.ClubStatus = 1 "
                        + "ORDER BY c.ClubName "
                        + "LIMIT ? OFFSET ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, pageSize);
                    stmt.setInt(2, offset);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Clubs club = new Clubs();
                            club.setClubID(rs.getInt("ClubID"));
                            club.setClubImg(rs.getString("ClubImg"));
                            club.setIsRecruiting(rs.getBoolean("IsRecruiting"));
                            club.setClubName(rs.getString("ClubName"));
                            club.setDescription(rs.getString("Description"));
                            club.setEstablishedDate(rs.getDate("EstablishedDate"));
                            club.setContactPhone(rs.getString("ContactPhone"));
                            club.setContactGmail(rs.getString("ContactGmail"));
                            club.setContactURL(rs.getString("ContactURL"));
                            club.setClubStatus(rs.getBoolean("ClubStatus"));
                            club.setCategoryID(rs.getInt("CategoryID"));
                            club.setCategoryName(rs.getString("CategoryName"));
                            club.setMemberCount(rs.getInt("MemberCount"));
                            clubs.add(club);
                        }
                    }
                }
            } else {
                query = "SELECT c.*, cc.CategoryName, "
                        + "(SELECT COUNT(*) FROM UserClubs uc WHERE uc.ClubID = c.ClubID AND uc.IsActive = 1) AS MemberCount "
                        + "FROM Clubs c "
                        + "LEFT JOIN ClubCategories cc ON c.CategoryID = cc.CategoryID "
                        + "WHERE c.ClubStatus = 1 AND c.CategoryID = ? "
                        + "ORDER BY c.ClubName "
                        + "LIMIT ? OFFSET ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, categoryID);
                    stmt.setInt(2, pageSize);
                    stmt.setInt(3, offset);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Clubs club = new Clubs();
                            club.setClubID(rs.getInt("ClubID"));
                            club.setClubImg(rs.getString("ClubImg"));
                            club.setIsRecruiting(rs.getBoolean("IsRecruiting"));
                            club.setClubName(rs.getString("ClubName"));
                            club.setDescription(rs.getString("Description"));
                            club.setEstablishedDate(rs.getDate("EstablishedDate"));
                            club.setContactPhone(rs.getString("ContactPhone"));
                            club.setContactGmail(rs.getString("ContactGmail"));
                            club.setContactURL(rs.getString("ContactURL"));
                            club.setClubStatus(rs.getBoolean("ClubStatus"));
                            club.setCategoryID(rs.getInt("CategoryID"));
                            club.setCategoryName(rs.getString("CategoryName"));
                            club.setMemberCount(rs.getInt("MemberCount"));
                            clubs.add(club);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting clubs by category: " + e.getMessage());
        }
        return clubs;
    }

    public List<Clubs> getUserClubs(String userID, int page, int pageSize) {
        List<Clubs> clubs = new ArrayList<>();
        if (userID == null || userID.isEmpty()) {
            return clubs;
        }
        String query = "SELECT c.*, cc.CategoryName, "
                + "(SELECT COUNT(*) FROM UserClubs uc WHERE uc.ClubID = c.ClubID AND uc.IsActive = 1) AS MemberCount "
                + "FROM Clubs c "
                + "JOIN UserClubs uc ON c.ClubID = uc.ClubID "
                + "LEFT JOIN ClubCategories cc ON c.CategoryID = cc.CategoryID "
                + "WHERE uc.UserID = ? AND uc.IsActive = 1 AND c.ClubStatus = 1 "
                + "ORDER BY c.ClubName "
                + "LIMIT ? OFFSET ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            stmt.setInt(2, pageSize);
            stmt.setInt(3, (page - 1) * pageSize);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Clubs club = new Clubs();
                    club.setClubID(rs.getInt("ClubID"));
                    club.setClubImg(rs.getString("ClubImg"));
                    club.setIsRecruiting(rs.getBoolean("IsRecruiting"));
                    club.setClubName(rs.getString("ClubName"));
                    club.setDescription(rs.getString("Description"));
                    club.setEstablishedDate(rs.getDate("EstablishedDate"));
                    club.setContactPhone(rs.getString("ContactPhone"));
                    club.setContactGmail(rs.getString("ContactGmail"));
                    club.setContactURL(rs.getString("ContactURL"));
                    club.setClubStatus(rs.getBoolean("ClubStatus"));
                    club.setCategoryID(rs.getInt("CategoryID"));
                    club.setCategoryName(rs.getString("CategoryName"));
                    club.setMemberCount(rs.getInt("MemberCount"));
                    clubs.add(club);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting user clubs: " + e.getMessage());
        }
        return clubs;
    }

    public int getTotalClubsByCategory(int categoryID, String userID) {
        int count = 0;
        String query;
        try (Connection conn = DBContext.getConnection()) {
            if (categoryID == 0) { // Assuming 0 means "all" categories
                query = "SELECT COUNT(*) FROM Clubs WHERE ClubStatus = 1";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            count = rs.getInt(1);
                        }
                    }
                }
            } else if (categoryID == -1) { // Assuming -1 means "myClubs"
                if (userID == null || userID.isEmpty()) {
                    return 0;
                }
                query = "SELECT COUNT(*) FROM Clubs c "
                        + "JOIN UserClubs uc ON c.ClubID = uc.ClubID "
                        + "WHERE uc.UserID = ? AND uc.IsActive = 1 AND c.ClubStatus = 1";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, userID);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            count = rs.getInt(1);
                        }
                    }
                }
            } else {
                query = "SELECT COUNT(*) FROM Clubs WHERE ClubStatus = 1 AND CategoryID = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, categoryID);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            count = rs.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting total clubs by category: " + e.getMessage());
        }
        return count;
    }

    public int getTotalActiveClubs() {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT COUNT(*) FROM Clubs WHERE ClubStatus = 1";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting total active clubs: " + e.getMessage());
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

    public int getTotalClubMembers() {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT COUNT(*) FROM UserClubs WHERE IsActive = 1";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting total club members: " + e.getMessage());
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

    public int getTotalDepartments() {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT COUNT(*) FROM ClubDepartments cd "
                    + "JOIN Departments d ON cd.DepartmentID = d.DepartmentID "
                    + "WHERE d.`DepartmentStatus` = 1";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting total departments: " + e.getMessage());
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

//    private String getCategoryForClub(int clubID) {
//        String category = "Học Thuật";
//        Connection conn = null;
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//
//        try {
//            conn = DBContext.getConnection();
//            String query = "SELECT ClubName, Description FROM Clubs WHERE ClubID = ?";
//            stmt = conn.prepareStatement(query);
//            stmt.setInt(1, clubID);
//            rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                String name = rs.getString("ClubName").toLowerCase();
//                String desc = rs.getString("Description") != null ? rs.getString("Description").toLowerCase() : "";
//
//                if (name.contains("thể thao") || name.contains("bóng") || name.contains("cầu lông")
//                        || desc.contains("thể thao") || desc.contains("bóng") || desc.contains("cầu lông")) {
//                    category = "Thể Thao";
//                } else if (name.contains("tình nguyện") || name.contains("thanh niên") || name.contains("phong trào")
//                        || desc.contains("tình nguyện") || desc.contains("thanh niên") || desc.contains("phong trào")) {
//                    category = "Phong Trào";
//                }
//            }
//        } catch (SQLException e) {
//            System.out.println("Error getting category for club: " + e.getMessage());
//        } finally {
//            try {
//                if (rs != null) rs.close();
//                if (stmt != null) stmt.close();
//                if (conn != null) DBContext.closeConnection(conn);
//            } catch (SQLException e) {
//                System.out.println("Error closing resources: " + e.getMessage());
//            }
//        }
//        return category;
//    }
    public List<Clubs> getActiveClubs() {
        return getClubsByStatus(1);
    }

    public List<Clubs> getCreateRequestClubs() {
        int isActive = 0;

        List<Clubs> clubs = new ArrayList<>();
        String sql = """
                     SELECT c.*, cc.CategoryName, u.UserID, u.FullName, uc.RoleID
                                                               FROM Clubs c 
                                                               LEFT JOIN ClubCategories cc ON c.CategoryID = cc.CategoryID 
                                                               join userclubs uc on c.ClubID = uc.ClubID
                                                               join users u on u.UserID = uc.UserID 
                                                               WHERE c.ClubStatus = ? AND uc.RoleID = 1 and CurrentRequestType = 'Create'
                                                               ORDER BY c.EstablishedDate DESC;
                     """;
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, isActive);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Clubs club = new Clubs();
                    club.setClubID(rs.getInt("ClubID"));
                    club.setClubImg(rs.getString("ClubImg"));
                    club.setIsRecruiting(rs.getBoolean("IsRecruiting"));
                    club.setClubName(rs.getString("ClubName"));
                    club.setDescription(rs.getString("Description"));
                    club.setEstablishedDate(rs.getDate("EstablishedDate"));
                    club.setContactPhone(rs.getString("ContactPhone"));
                    club.setContactGmail(rs.getString("ContactGmail"));
                    club.setContactURL(rs.getString("ContactURL"));
                    club.setClubStatus(rs.getBoolean("ClubStatus"));
                    club.setCategoryID(rs.getInt("CategoryID"));
                    club.setCategoryName(rs.getString("CategoryName"));

                    if (isActive == 0) {
                        club.setClubRequestStatus(rs.getString("ClubRequestStatus"));
                        club.setCurrentRequestType(rs.getString("CurrentRequestType"));
                        club.setUpdateRequestNote(rs.getString("UpdateRequestNote"));

                        club.setChairmanID(rs.getString("UserID"));
                        club.setChairmanFullName(rs.getString("FullName"));
                    }

                    clubs.add(club);

                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting clubs: " + e.getMessage());
        }
        return clubs;
    }

    public List<Clubs> getUpdateRequestClubs() {

        List<Clubs> clubs = new ArrayList<>();
        String sql = """
                     SELECT c.*, cc.CategoryName, u.UserID, u.FullName, uc.RoleID
                                                               FROM Clubs c 
                                                               LEFT JOIN ClubCategories cc ON c.CategoryID = cc.CategoryID 
                                                               join userclubs uc on c.ClubID = uc.ClubID
                                                               join users u on u.UserID = uc.UserID 
                                                               WHERE c.ClubStatus = 1 AND uc.RoleID = 1 and CurrentRequestType = 'Update'
                                                               ORDER BY c.EstablishedDate DESC;
                     """;
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Clubs club = new Clubs();
                    club.setClubID(rs.getInt("ClubID"));
                    club.setClubImg(rs.getString("ClubImg"));
                    club.setIsRecruiting(rs.getBoolean("IsRecruiting"));
                    club.setClubName(rs.getString("ClubName"));
                    club.setDescription(rs.getString("Description"));
                    club.setEstablishedDate(rs.getDate("EstablishedDate"));
                    club.setContactPhone(rs.getString("ContactPhone"));
                    club.setContactGmail(rs.getString("ContactGmail"));
                    club.setContactURL(rs.getString("ContactURL"));
                    club.setClubStatus(rs.getBoolean("ClubStatus"));
                    club.setCategoryID(rs.getInt("CategoryID"));
                    club.setCategoryName(rs.getString("CategoryName"));

                    club.setClubRequestStatus(rs.getString("ClubRequestStatus"));
                    club.setCurrentRequestType(rs.getString("CurrentRequestType"));
                    club.setUpdateRequestNote(rs.getString("UpdateRequestNote"));

                    club.setChairmanID(rs.getString("UserID"));
                    club.setChairmanFullName(rs.getString("FullName"));

                    clubs.add(club);

                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting clubs: " + e.getMessage());
        }
        return clubs;
    }

    private List<Clubs> getClubsByStatus(int isActive) {
        List<Clubs> clubs = new ArrayList<>();
        String sql = """
                     SELECT c.*, cc.CategoryName, u.UserID, u.FullName, uc.RoleID
                                                               FROM Clubs c 
                                                               LEFT JOIN ClubCategories cc ON c.CategoryID = cc.CategoryID 
                                                               join userclubs uc on c.ClubID = uc.ClubID
                                                               join users u on u.UserID = uc.UserID 
                                                               WHERE c.ClubStatus = ? AND uc.RoleID = 1
                                                               ORDER BY c.EstablishedDate DESC;
                     """;
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, isActive);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Clubs club = new Clubs();
                    club.setClubID(rs.getInt("ClubID"));
                    club.setClubImg(rs.getString("ClubImg"));
                    club.setIsRecruiting(rs.getBoolean("IsRecruiting"));
                    club.setClubName(rs.getString("ClubName"));
                    club.setDescription(rs.getString("Description"));
                    club.setEstablishedDate(rs.getDate("EstablishedDate"));
                    club.setContactPhone(rs.getString("ContactPhone"));
                    club.setContactGmail(rs.getString("ContactGmail"));
                    club.setContactURL(rs.getString("ContactURL"));
                    club.setClubStatus(rs.getBoolean("ClubStatus"));
                    club.setCategoryID(rs.getInt("CategoryID"));
                    club.setCategoryName(rs.getString("CategoryName"));

                    if (isActive == 0) {
                        club.setClubRequestStatus(rs.getString("ClubRequestStatus"));
                        club.setCurrentRequestType(rs.getString("CurrentRequestType"));
                        club.setUpdateRequestNote(rs.getString("UpdateRequestNote"));

                        club.setChairmanID(rs.getString("UserID"));
                        club.setChairmanFullName(rs.getString("FullName"));
                    }

                    clubs.add(club);

                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting clubs: " + e.getMessage());
        }
        return clubs;
    }

    public List<Clubs> getApproveRequestClubs() {
        List<Clubs> clubs = new ArrayList<>();
        String sql = """
                     SELECT c.*, cc.CategoryName, u.UserID, u.FullName, uc.RoleID
                            FROM Clubs c 
                            LEFT JOIN ClubCategories cc ON c.CategoryID = cc.CategoryID 
                            join userclubs uc on c.ClubID = uc.ClubID
                            join users u on u.UserID = uc.UserID 
                            WHERE c.ClubStatus = 1 AND uc.RoleID = 1 and ClubRequestStatus = 'Approved' and CurrentRequestType = 'Create'
                            ORDER BY c.EstablishedDate DESC;
                     """;
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Clubs club = new Clubs();
                    club.setClubID(rs.getInt("ClubID"));
                    club.setClubImg(rs.getString("ClubImg"));
                    club.setIsRecruiting(rs.getBoolean("IsRecruiting"));
                    club.setClubName(rs.getString("ClubName"));
                    club.setDescription(rs.getString("Description"));
                    club.setEstablishedDate(rs.getDate("EstablishedDate"));
                    club.setContactPhone(rs.getString("ContactPhone"));
                    club.setContactGmail(rs.getString("ContactGmail"));
                    club.setContactURL(rs.getString("ContactURL"));
                    club.setClubStatus(rs.getBoolean("ClubStatus"));
                    club.setCategoryID(rs.getInt("CategoryID"));
                    club.setCategoryName(rs.getString("CategoryName"));

                    club.setClubRequestStatus(rs.getString("ClubRequestStatus"));
                    club.setCurrentRequestType(rs.getString("CurrentRequestType"));
                    club.setUpdateRequestNote(rs.getString("UpdateRequestNote"));

                    club.setChairmanID(rs.getString("UserID"));
                    club.setChairmanFullName(rs.getString("FullName"));

                    clubs.add(club);

                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting clubs: " + e.getMessage());
        }
        return clubs;

    }

    public Clubs getClubById(int clubID) {
        String query = "SELECT c.*, cc.CategoryName, "
                + "(SELECT COUNT(*) FROM UserClubs uc WHERE uc.ClubID = c.ClubID AND uc.IsActive = 1) AS MemberCount "
                + "FROM Clubs c "
                + "LEFT JOIN ClubCategories cc ON c.CategoryID = cc.CategoryID "
                + "WHERE c.ClubID = ? ";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clubID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Clubs club = new Clubs();
                    club.setClubID(rs.getInt("ClubID"));
                    club.setClubImg(rs.getString("ClubImg"));
                    club.setIsRecruiting(rs.getBoolean("IsRecruiting"));
                    club.setClubName(rs.getString("ClubName"));
                    club.setDescription(rs.getString("Description"));
                    club.setEstablishedDate(rs.getDate("EstablishedDate"));
                    club.setContactPhone(rs.getString("ContactPhone"));
                    club.setContactGmail(rs.getString("ContactGmail"));
                    club.setContactURL(rs.getString("ContactURL"));
                    club.setClubStatus(rs.getBoolean("ClubStatus"));
                    club.setCategoryID(rs.getInt("CategoryID"));
                    club.setCategoryName(rs.getString("CategoryName"));
                    club.setMemberCount(rs.getInt("MemberCount"));
                    club.setClubRequestStatus(rs.getString("ClubRequestStatus"));
                    club.setCurrentRequestType(rs.getString("CurrentRequestType"));
                    club.setLastRejectReason(rs.getString("LastRejectReason"));
                    return club;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting club by ID: " + e.getMessage());
        }
        return null;
    }

    public Clubs getALLClubById(int clubID) {
        String query = """
                       SELECT c.*, cc.CategoryName, u.UserID, u.FullName, uc.RoleID
                       FROM Clubs c 
                       LEFT JOIN ClubCategories cc ON c.CategoryID = cc.CategoryID 
                                       join userclubs uc on c.ClubID = uc.ClubID
                                       join users u on u.UserID = uc.UserID
                                       WHERE c.ClubID = ? and uc.RoleID = 1
                       """;
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clubID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Clubs club = new Clubs();
                    club.setClubID(rs.getInt("ClubID"));
                    club.setClubImg(rs.getString("ClubImg"));
                    club.setIsRecruiting(rs.getBoolean("IsRecruiting"));
                    club.setClubName(rs.getString("ClubName"));
                    club.setDescription(rs.getString("Description"));
                    club.setEstablishedDate(rs.getDate("EstablishedDate"));
                    club.setContactPhone(rs.getString("ContactPhone"));
                    club.setContactGmail(rs.getString("ContactGmail"));
                    club.setContactURL(rs.getString("ContactURL"));
                    club.setClubStatus(rs.getBoolean("ClubStatus"));
                    club.setCategoryID(rs.getInt("CategoryID"));
                    club.setCategoryName(rs.getString("CategoryName"));

                    club.setClubRequestStatus(rs.getString("ClubRequestStatus"));
                    club.setCurrentRequestType(rs.getString("CurrentRequestType"));
                    club.setUpdateRequestNote(rs.getString("UpdateRequestNote"));

                    club.setChairmanID(rs.getString("UserID"));
                    club.setChairmanFullName(rs.getString("FullName"));

                    return club;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting club by ID: " + e.getMessage());
        }
        return null;
    }

    public Clubs getUpdateClubById(int clubID) {
        String query = """
                       select *, cc.CategoryName from clubs c LEFT JOIN ClubCategories cc ON c.CategoryID = cc.CategoryID  where ParentClubID = ?
                       """;
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clubID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Clubs club = new Clubs();
                    club.setClubID(rs.getInt("ParentClubID"));
                    club.setClubImg(rs.getString("ClubImg"));
                    club.setIsRecruiting(rs.getBoolean("IsRecruiting"));
                    club.setClubName(rs.getString("ClubName"));
                    club.setDescription(rs.getString("Description"));
                    club.setEstablishedDate(rs.getDate("EstablishedDate"));
                    club.setContactPhone(rs.getString("ContactPhone"));
                    club.setContactGmail(rs.getString("ContactGmail"));
                    club.setContactURL(rs.getString("ContactURL"));
                    club.setClubStatus(rs.getBoolean("ClubStatus"));
                    club.setCategoryID(rs.getInt("CategoryID"));
                    club.setCategoryName(rs.getString("CategoryName"));

                    club.setClubRequestStatus(rs.getString("ClubRequestStatus"));
                    club.setCurrentRequestType(rs.getString("CurrentRequestType"));
                    club.setUpdateRequestNote(rs.getString("UpdateRequestNote"));


                    return club;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting club by ID: " + e.getMessage());
        }
        return null;
    }

    public int getMemberCount(int clubID) {
        String query = "SELECT COUNT(*) FROM UserClubs WHERE ClubID = ? AND IsActive = 1";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clubID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting member count: " + e.getMessage());
        }
        return 0;
    }

    public boolean addFavoriteClub(String userID, int clubID) {
        String query = "INSERT INTO FavoriteClubs (UserID, ClubID) VALUES (?, ?)";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            stmt.setInt(2, clubID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding favorite club: " + e.getMessage());
            return false;
        }
    }

    public boolean removeFavoriteClub(String userID, int clubID) {
        String query = "DELETE FROM FavoriteClubs WHERE UserID = ? AND ClubID = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            stmt.setInt(2, clubID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error removing favorite club: " + e.getMessage());
            return false;
        }
    }

    public boolean isFavoriteClub(String userID, int clubID) {
        String query = "SELECT COUNT(*) FROM FavoriteClubs WHERE UserID = ? AND ClubID = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            stmt.setInt(2, clubID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking favorite club: " + e.getMessage());
        }
        return false;
    }

    public List<Clubs> getFavoriteClubs(String userID, int page, int pageSize) {
        List<Clubs> clubs = new ArrayList<>();
        String query = "SELECT c.*, cc.CategoryName "
                + "FROM FavoriteClubs fc "
                + "JOIN Clubs c ON fc.ClubID = c.ClubID "
                + "LEFT JOIN ClubCategories cc ON c.CategoryID = cc.CategoryID "
                + "WHERE fc.UserID = ? AND c.ClubStatus = 1 "
                + "ORDER BY fc.AddedDate DESC "
                + "LIMIT ? OFFSET ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            stmt.setInt(2, pageSize);
            stmt.setInt(3, (page - 1) * pageSize);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Clubs club = new Clubs();
                    club.setClubID(rs.getInt("ClubID"));
                    club.setClubImg(rs.getString("ClubImg"));
                    club.setIsRecruiting(rs.getBoolean("IsRecruiting"));
                    club.setClubName(rs.getString("ClubName"));
                    club.setDescription(rs.getString("Description"));
                    club.setEstablishedDate(rs.getDate("EstablishedDate"));
                    club.setContactPhone(rs.getString("ContactPhone"));
                    club.setContactGmail(rs.getString("ContactGmail"));
                    club.setContactURL(rs.getString("ContactURL"));
                    club.setClubStatus(rs.getBoolean("ClubStatus"));
                    club.setCategoryID(rs.getInt("CategoryID"));
                    club.setCategoryName(rs.getString("CategoryName"));
                    club.setMemberCount(getMemberCount(club.getClubID()));
                    club.setFavorite(true);
                    clubs.add(club);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting favorite clubs: " + e.getMessage());
        }
        return clubs;
    }

    public int getTotalFavoriteClubs(String userID) {
        String query = "SELECT COUNT(*) FROM FavoriteClubs WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error counting favorite clubs: " + e.getMessage());
        }
        return 0;
    }

    public List<Clubs> getRequestClub(String userID, int page, int pageSize) {
        List<Clubs> clubs = new ArrayList<>();
        String query = """
                       SELECT c.*
                       FROM Clubs c
                       INNER JOIN UserClubs uc ON c.ClubID = uc.ClubID
                       WHERE c.ClubRequestStatus IN ( 'Pending', 'Rejected')
                         AND c.CurrentRequestType = 'Create'
                         AND uc.RoleID = 1
                         AND uc.UserID = ?
                       LIMIT ? OFFSET ?
                       """;
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            stmt.setInt(2, pageSize);
            stmt.setInt(3, (page - 1) * pageSize);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Clubs club = new Clubs();
                    club.setClubID(rs.getInt("ClubID"));
                    club.setClubImg(rs.getString("ClubImg"));
                    club.setIsRecruiting(rs.getBoolean("IsRecruiting"));
                    club.setClubName(rs.getString("ClubName"));
                    club.setDescription(rs.getString("Description"));
                    club.setCategoryID(rs.getInt("CategoryID"));
                    club.setEstablishedDate(rs.getDate("EstablishedDate"));
                    club.setContactPhone(rs.getString("ContactPhone"));
                    club.setContactGmail(rs.getString("ContactGmail"));
                    club.setContactURL(rs.getString("ContactURL"));
                    club.setClubStatus(rs.getBoolean("ClubStatus"));
                    club.setLastRejectReason(rs.getString("LastRejectReason"));
                    club.setParentClubID(rs.getInt("ParentClubID"));
                    club.setClubRequestStatus(rs.getString("ClubRequestStatus"));
                    club.setCurrentRequestType(rs.getString("CurrentRequestType"));
                    club.setUpdateRequestNote(rs.getString("UpdateRequestNote"));
                    clubs.add(club);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting favorite clubs: " + e.getMessage());
        }
        return clubs;
    }

    public int getTotalRequestClub(String userID) {
        String query = "SELECT COUNT(*)\n"
                + "FROM Clubs c\n"
                + "INNER JOIN UserClubs uc ON c.ClubID = uc.ClubID\n"
                + "WHERE c.ClubRequestStatus IN ( 'Pending', 'Rejected')\n"
                + " AND c.CurrentRequestType = 'Create'\n"
                + "  AND uc.RoleID = 1\n"
                + "  AND uc.UserID = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error counting request clubs: " + e.getMessage());
        }
        return 0;
    }

    public List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT DepartmentID, DepartmentName FROM Departments WHERE `DepartmentStatus` = 1";
            stmt = conn.prepareStatement(query);
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

    public List<Integer> getAllDepartmentIDs() throws SQLException {
        List<Integer> departmentIDs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBContext.getConnection();
            String query = "SELECT DepartmentID FROM Departments";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                departmentIDs.add(rs.getInt("DepartmentID"));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                DBContext.closeConnection(conn);
            }
        }
        return departmentIDs;
    }

    public int createClub(Clubs club) throws SQLException {
        Connection conn = null;
        PreparedStatement clubStmt = null;
        PreparedStatement deptStmt = null;
        ResultSet generatedKeys = null;
        int newClubID = -1;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            String clubQuery = "INSERT INTO Clubs (ClubName, Description, CategoryID, ClubImg, ContactPhone, ContactGmail, ContactURL, EstablishedDate, ClubStatus, IsRecruiting, ClubRequestStatus, CurrentRequestType) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Pending', 'Create')";
            clubStmt = conn.prepareStatement(clubQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            clubStmt.setString(1, club.getClubName());
            clubStmt.setString(2, club.getDescription());
            clubStmt.setInt(3, club.getCategoryID());
            clubStmt.setString(4, club.getClubImg());
            clubStmt.setString(5, club.getContactPhone());
            clubStmt.setString(6, club.getContactGmail());
            clubStmt.setString(7, club.getContactURL());
            if (club.getEstablishedDate() != null) {
                clubStmt.setDate(8, new java.sql.Date(club.getEstablishedDate().getTime()));
            } else {
                clubStmt.setNull(8, java.sql.Types.DATE);
            }
            clubStmt.setBoolean(9, club.isClubStatus());
            clubStmt.setBoolean(10, club.isIsRecruiting());

            int rows = clubStmt.executeUpdate();
            if (rows > 0) {
                generatedKeys = clubStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    newClubID = generatedKeys.getInt(1);
                }
            }

            if (newClubID == -1) {
                conn.rollback();
                return -1;
            }

            List<Integer> allDepartmentIDs = getAllDepartmentIDs();
            String deptQuery = "INSERT INTO ClubDepartments (DepartmentID, ClubID) VALUES (?, ?)";
            deptStmt = conn.prepareStatement(deptQuery);
            for (Integer deptID : allDepartmentIDs) {
                deptStmt.setInt(1, deptID);
                deptStmt.setInt(2, newClubID);
                deptStmt.addBatch();
            }
            deptStmt.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.out.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            throw e;
        } finally {
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
                if (clubStmt != null) {
                    clubStmt.close();
                }
                if (deptStmt != null) {
                    deptStmt.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    DBContext.closeConnection(conn);
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return newClubID;
    }

    public int requestUpdateClub(Clubs newClub, int originalClubID) throws SQLException {
        Connection conn = null;
        PreparedStatement newClubStmt = null;
        PreparedStatement updateOriginalStmt = null;
        PreparedStatement deptStmt = null;
        ResultSet generatedKeys = null;
        int newClubID = -1;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // Insert new club record with updated info
            String newClubQuery = "INSERT INTO Clubs (ClubName, Description, CategoryID, ClubImg, ContactPhone, ContactGmail, ContactURL, EstablishedDate, ClubStatus, IsRecruiting, ClubRequestStatus, CurrentRequestType, ParentClubID, UpdateRequestNote) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Pending', 'Update', ?, ?)";
            newClubStmt = conn.prepareStatement(newClubQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            newClubStmt.setString(1, newClub.getClubName());
            newClubStmt.setString(2, newClub.getDescription());
            newClubStmt.setInt(3, newClub.getCategoryID());
            newClubStmt.setString(4, newClub.getClubImg());
            newClubStmt.setString(5, newClub.getContactPhone());
            newClubStmt.setString(6, newClub.getContactGmail());
            newClubStmt.setString(7, newClub.getContactURL());
            if (newClub.getEstablishedDate() != null) {
                newClubStmt.setDate(8, new java.sql.Date(newClub.getEstablishedDate().getTime()));
            } else {
                newClubStmt.setNull(8, java.sql.Types.DATE);
            }
            newClubStmt.setBoolean(9, false); // ClubStatus = 0
            newClubStmt.setBoolean(10, newClub.isIsRecruiting());
            newClubStmt.setInt(11, originalClubID); // ParentClubID
            newClubStmt.setString(12, newClub.getUpdateRequestNote());

            int rows = newClubStmt.executeUpdate();
            if (rows > 0) {
                generatedKeys = newClubStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    newClubID = generatedKeys.getInt(1);
                }
            }

            if (newClubID == -1) {
                conn.rollback();
                return -1;
            }

            // Update original club to pending update status
            String updateOriginalQuery = "UPDATE Clubs SET CurrentRequestType = 'Update', ClubRequestStatus = 'Pending' WHERE ClubID = ?";
            updateOriginalStmt = conn.prepareStatement(updateOriginalQuery);
            updateOriginalStmt.setInt(1, originalClubID);
            int updateRows = updateOriginalStmt.executeUpdate();
            if (updateRows == 0) {
                conn.rollback();
                return -1;
            }

            // Copy departments from original club to new club
            List<Integer> allDepartmentIDs = getAllDepartmentIDs();
            String deptQuery = "INSERT INTO ClubDepartments (DepartmentID, ClubID) VALUES (?, ?)";
            deptStmt = conn.prepareStatement(deptQuery);
            for (Integer deptID : allDepartmentIDs) {
                deptStmt.setInt(1, deptID);
                deptStmt.setInt(2, newClubID);
                deptStmt.addBatch();
            }
            deptStmt.executeBatch();

            conn.commit();
            return newClubID;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (generatedKeys != null) {
                generatedKeys.close();
            }
            if (newClubStmt != null) {
                newClubStmt.close();
            }
            if (updateOriginalStmt != null) {
                updateOriginalStmt.close();
            }
            if (deptStmt != null) {
                deptStmt.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                DBContext.closeConnection(conn);
            }
        }
    }

    public boolean updateClub(Clubs club) throws SQLException {
        Connection conn = null;
        PreparedStatement clubStmt = null;
        PreparedStatement checkDeptStmt = null;
        PreparedStatement insertStmt = null;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            String clubQuery = "UPDATE Clubs SET ClubName = ?, Description = ?, CategoryID = ?, ClubImg = ?, ContactPhone = ?, ContactGmail = ?, ContactURL = ?, EstablishedDate = ?, ClubRequestStatus = 'Pending', CurrentRequestType = 'Create', UpdateRequestNote = ? WHERE ClubID = ?";
            clubStmt = conn.prepareStatement(clubQuery);
            clubStmt.setString(1, club.getClubName());
            clubStmt.setString(2, club.getDescription());
            clubStmt.setInt(3, club.getCategoryID());
            clubStmt.setString(4, club.getClubImg());
            clubStmt.setString(5, club.getContactPhone());
            clubStmt.setString(6, club.getContactGmail());
            clubStmt.setString(7, club.getContactURL());
            clubStmt.setDate(8, club.getEstablishedDate() != null ? new java.sql.Date(club.getEstablishedDate().getTime()) : null);
            clubStmt.setString(9, club.getUpdateRequestNote());
            clubStmt.setInt(10, club.getClubID());

            if (clubStmt.executeUpdate() == 0) {
                conn.rollback();
                return false;
            }

            String checkDept = "SELECT COUNT(*) FROM ClubDepartments WHERE ClubID = ? AND DepartmentID = ?";
            String insertDept = "INSERT INTO ClubDepartments (DepartmentID, ClubID) VALUES (?, ?)";
            checkDeptStmt = conn.prepareStatement(checkDept);
            insertStmt = conn.prepareStatement(insertDept);

            for (Integer deptID : getAllDepartmentIDs()) {
                if (deptID != null && !getClubDepartmentIDs(club.getClubID()).contains(deptID)) {
                    checkDeptStmt.setInt(1, club.getClubID());
                    checkDeptStmt.setInt(2, deptID);
                    try (ResultSet rs = checkDeptStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) == 0) {
                            insertStmt.setInt(1, deptID);
                            insertStmt.setInt(2, club.getClubID());
                            insertStmt.addBatch();
                        }
                    }
                }
            }
            insertStmt.executeBatch();

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (clubStmt != null) {
                clubStmt.close();
            }
            if (checkDeptStmt != null) {
                checkDeptStmt.close();
            }
            if (insertStmt != null) {
                insertStmt.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                DBContext.closeConnection(conn);
            }
        }
    }

    public List<Integer> getClubDepartmentIDs(int clubID) {
        List<Integer> departmentIDs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT DepartmentID FROM ClubDepartments WHERE ClubID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, clubID);
            rs = stmt.executeQuery();

            while (rs.next()) {
                departmentIDs.add(rs.getInt("DepartmentID"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting club department IDs: " + e.getMessage());
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
        return departmentIDs;
    }

    /**
     * Cập nhật trạng thái tuyển quân của câu lạc bộ
     *
     * @param clubID ID của câu lạc bộ cần cập nhật
     * @param isRecruiting trạng thái tuyển quân mới (true = đang tuyển quân,
     * false = không tuyển quân)
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateIsRecruiting(int clubID, boolean isRecruiting) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBContext.getConnection();
            String query = "UPDATE Clubs SET IsRecruiting = ? WHERE ClubID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setBoolean(1, isRecruiting);
            stmt.setInt(2, clubID);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật trạng thái tuyển quân: " + e.getMessage());
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
                System.out.println("Lỗi khi đóng kết nối: " + e.getMessage());
            }
        }
    }

    public List<Clubs> findPendingCreateClubsByChairman(String userID) {
        List<Clubs> pendingClubs = new ArrayList<>();
        String sql = "SELECT c.* FROM Clubs c "
                + "INNER JOIN UserClubs uc ON c.ClubID = uc.ClubID "
                + "WHERE c.ClubRequestStatus = 'Pending' "
                + "AND c.CurrentRequestType = 'Create' "
                + "AND uc.RoleID = 1 "
                + "AND uc.UserID = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Clubs club = new Clubs();
                club.setClubID(rs.getInt("ClubID"));
                club.setClubImg(rs.getString("ClubImg"));
                club.setIsRecruiting(rs.getBoolean("IsRecruiting"));
                club.setClubName(rs.getString("ClubName"));
                club.setDescription(rs.getString("Description"));
                club.setCategoryID(rs.getInt("CategoryID"));
                club.setEstablishedDate(rs.getDate("EstablishedDate"));
                club.setContactPhone(rs.getString("ContactPhone"));
                club.setContactGmail(rs.getString("ContactGmail"));
                club.setContactURL(rs.getString("ContactURL"));
                club.setClubStatus(rs.getBoolean("ClubStatus"));
                club.setLastRejectReason(rs.getString("LastRejectReason"));
                club.setParentClubID(rs.getInt("ParentClubID"));
                club.setClubRequestStatus(rs.getString("ClubRequestStatus"));
                club.setCurrentRequestType(rs.getString("CurrentRequestType"));
                club.setUpdateRequestNote(rs.getString("UpdateRequestNote"));
                pendingClubs.add(club);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pendingClubs;
    }
}
