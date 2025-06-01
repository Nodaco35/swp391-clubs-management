package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Clubs;

public class ClubDAO {

    public List<Clubs> getFeaturedClubs(int limit) {
        List<Clubs> clubs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT c.*, "
                    + "(SELECT COUNT(*) FROM UserClubs uc WHERE uc.ClubID = c.ClubID AND uc.IsActive = 1) as MemberCount "
                    + "FROM Clubs c "
                    + "WHERE c.IsRecruiting = 1 AND c.ClubStatus = 1 "
                    + "ORDER BY MemberCount DESC "
                    + "LIMIT ?";

            stmt = conn.prepareStatement(query);
            stmt.setInt(1, limit);
            rs = stmt.executeQuery();

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

                // Giả định có trường Category trong bảng Clubs
                // Nếu không có, bạn có thể gán giá trị mặc định hoặc tạo logic phân loại
                club.setCategory(getCategoryForClub(rs.getInt("ClubID")));

                club.setMemberCount(rs.getInt("MemberCount"));

                clubs.add(club);
            }
        } catch (SQLException e) {
            System.out.println("Error getting featured clubs: " + e.getMessage());
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

        return clubs;
    }

    public List<Clubs> getClubsByCategory(String category, int page, int pageSize) {
        List<Clubs> clubs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query;
            int offset = (page - 1) * pageSize;

            if ("all".equalsIgnoreCase(category)) {
                query = "SELECT c.*, "
                        + "(SELECT COUNT(*) FROM UserClubs uc WHERE uc.ClubID = c.ClubID AND uc.IsActive = 1) as MemberCount "
                        + "FROM Clubs c "
                        + "WHERE c.ClubStatus = 1 "
                        + "ORDER BY c.ClubName "
                        + "LIMIT ? OFFSET ?";
                stmt = conn.prepareStatement(query);
                stmt.setInt(1, pageSize);
                stmt.setInt(2, offset);
            } else {
                query = "SELECT c.*, "
                        + "(SELECT COUNT(*) FROM UserClubs uc WHERE uc.ClubID = c.ClubID AND uc.IsActive = 1) as MemberCount "
                        + "FROM Clubs c "
                        + "WHERE c.ClubStatus = 1 AND c.Category = ? "
                        + "ORDER BY c.ClubName "
                        + "LIMIT ? OFFSET ?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, category);
                stmt.setInt(2, pageSize);
                stmt.setInt(3, offset);
            }

            rs = stmt.executeQuery();

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
                club.setCategory(rs.getString("Category"));
                club.setMemberCount(rs.getInt("MemberCount"));
                clubs.add(club);
            }
        } catch (SQLException e) {
            System.out.println("Error getting clubs by category: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return clubs;
    }
    
    public List<Clubs> getUserClubs(String userID, int page, int pageSize) {
        List<Clubs> clubs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        if (userID == null || userID.isEmpty()) {
            return clubs; // Return empty list if userID is invalid
        }

        try {
            conn = DBContext.getConnection();
            String query = "SELECT c.*, "
                    + "(SELECT COUNT(*) FROM UserClubs uc WHERE uc.ClubID = c.ClubID AND uc.IsActive = 1) as MemberCount "
                    + "FROM Clubs c "
                    + "JOIN UserClubs uc ON c.ClubID = uc.ClubID "
                    + "WHERE uc.UserID = ? AND uc.IsActive = 1 AND c.ClubStatus = 1 "
                    + "ORDER BY c.ClubName "
                    + "LIMIT ? OFFSET ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, userID);
            stmt.setInt(2, pageSize);
            stmt.setInt(3, (page - 1) * pageSize);
            rs = stmt.executeQuery();

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
                club.setCategory(rs.getString("Category"));
                club.setMemberCount(rs.getInt("MemberCount"));
                clubs.add(club);
            }
        } catch (SQLException e) {
            System.out.println("Error getting user clubs: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return clubs;
    }
    
    public int getTotalClubsByCategory(String category, String userID) {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query;
            if ("all".equalsIgnoreCase(category)) {
                query = "SELECT COUNT(*) FROM Clubs WHERE ClubStatus = 1";
                stmt = conn.prepareStatement(query);
            } else if ("myClubs".equalsIgnoreCase(category)) {
                if (userID == null || userID.isEmpty()) {
                    return 0; // No clubs if userID is invalid
                }
                query = "SELECT COUNT(*) FROM Clubs c "
                        + "JOIN UserClubs uc ON c.ClubID = uc.ClubID "
                        + "WHERE uc.UserID = ? AND uc.IsActive = 1 AND c.ClubStatus = 1";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, userID);
            } else {
                query = "SELECT COUNT(*) FROM Clubs WHERE ClubStatus = 1 AND Category = ?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, category);
            }
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting total clubs by category: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
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
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
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
            String query = "SELECT COUNT(*) FROM ClubDepartments WHERE DepartmentStatus = 1";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting total departments: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return count;
    }

    private String getCategoryForClub(int clubID) {
        String category = "Học Thuật";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT ClubName, Description FROM Clubs WHERE ClubID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, clubID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("ClubName").toLowerCase();
                String desc = rs.getString("Description") != null ? rs.getString("Description").toLowerCase() : "";

                if (name.contains("thể thao") || name.contains("bóng") || name.contains("cầu lông")
                        || desc.contains("thể thao") || desc.contains("bóng") || desc.contains("cầu lông")) {
                    category = "Thể Thao";
                } else if (name.contains("tình nguyện") || name.contains("thanh niên") || name.contains("phong trào")
                        || desc.contains("tình nguyện") || desc.contains("thanh niên") || desc.contains("phong trào")) {
                    category = "Phong Trào";
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting category for club: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return category;
    }

    public List<Clubs> getRecentlyApprovedClubs(int limit) {
        List<Clubs> clubs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String sql = """
            SELECT * FROM Clubs
            WHERE ClubStatus = 1
            ORDER BY EstablishedDate DESC
            LIMIT ?
        """;

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            rs = stmt.executeQuery();

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
                club.setCategory(rs.getString("Category")); // <-- Gọi thẳng luôn

                clubs.add(club);
            }

        } catch (SQLException e) {
            System.out.println("Error getting recently approved clubs: " + e.getMessage());
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

        return clubs;
    }
    
    public Clubs getClubById(int clubID) {
        Clubs club = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String query = "SELECT c.*, "
                    + "(SELECT COUNT(*) FROM UserClubs uc WHERE uc.ClubID = c.ClubID AND uc.IsActive = 1) as MemberCount "
                    + "FROM Clubs c WHERE c.ClubID = ? AND c.ClubStatus = 1";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, clubID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                club = new Clubs();
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
                club.setCategory(getCategoryForClub(clubID));
                club.setMemberCount(rs.getInt("MemberCount"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting club by ID: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) DBContext.closeConnection(conn);
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return club;
    }

}
