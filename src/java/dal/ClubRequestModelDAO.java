package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.ClubRequestModel;
import models.DepartmentMember;
import models.UserClub;

public class ClubRequestModelDAO {

    public List<ClubRequestModel> getPendingCreateClubRequests() {
        List<ClubRequestModel> requests = new ArrayList<>();
        String query = "SELECT c.*, cc.CategoryName "
                + "FROM Clubs c "
                + "LEFT JOIN ClubCategories cc ON c.CategoryID = cc.CategoryID "
                + "WHERE c.ClubStatus = 0 AND c.ClubRequestStatus = 'Pending' AND c.CurrentRequestType = 'Create'";

        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ClubRequestModel club = new ClubRequestModel();
                club.setClubID(rs.getInt("ClubID"));
                club.setClubName(rs.getString("ClubName"));
                club.setDescription(rs.getString("Description"));
                club.setCategoryID(rs.getInt("CategoryID"));
                club.setEstablishedDate(rs.getString("EstablishedDate"));
                club.setContactPhone(rs.getString("ContactPhone"));
                club.setContactGmail(rs.getString("ContactGmail"));
                club.setContactURL(rs.getString("ContactURL"));
                club.setClubImg(rs.getString("ClubImg"));
                club.setRequestType("Create");
                club.setLastCommentDescription(rs.getString("LastCommentDescription"));
                requests.add(club);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching club creation requests: " + e.getMessage());
        }

        return requests;
    }

    public List<Integer> getAllDepartmentIDByClubID(int clubID) {
        List<Integer> departmentIDs = new ArrayList<>();
        String sql = "SELECT ClubDepartmentID FROM ClubDepartments WHERE ClubID = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clubID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                departmentIDs.add(rs.getInt("ClubDepartmentID"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return departmentIDs;
    }

    public List<DepartmentMember> getMembersForClubApproval(int clubDepartmentID) {
        List<DepartmentMember> members = new ArrayList<>();

        String sql = """
        SELECT u.UserID, u.FullName, u.Email, u.AvatarSrc,
               uc.JoinDate, uc.IsActive,
               r.RoleName,
               d.DepartmentName
        FROM UserClubs uc
        INNER JOIN Users u ON uc.UserID = u.UserID
        INNER JOIN ClubDepartments cd ON uc.ClubDepartmentID = cd.ClubDepartmentID
        INNER JOIN Departments d ON cd.DepartmentID = d.DepartmentID
        LEFT JOIN Roles r ON uc.RoleID = r.RoleID
        WHERE uc.ClubDepartmentID = ?
        ORDER BY 
            CASE 
                WHEN uc.RoleID = 3 THEN 1  -- Trưởng ban
                WHEN uc.RoleID = 4 THEN 2  -- Thành viên
                ELSE 3
            END,
            uc.JoinDate DESC
    """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clubDepartmentID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DepartmentMember member = new DepartmentMember();
                member.setUserID(rs.getString("UserID"));
                member.setFullName(rs.getString("FullName"));
                member.setEmail(rs.getString("Email"));
                member.setAvatar(rs.getString("AvatarSrc"));
                member.setJoinedDate(rs.getTimestamp("JoinDate"));
                member.setActive(rs.getBoolean("IsActive"));
                member.setRoleName(rs.getString("RoleName"));

                members.add(member);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }

}
