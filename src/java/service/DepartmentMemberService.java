package service;

import dal.DepartmentMemberDAO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.DepartmentMember;

/**
 * Service layer for department member management operations
 */
public class DepartmentMemberService {
    private final DepartmentMemberDAO departmentMemberDAO;
    
    public DepartmentMemberService() {
        this.departmentMemberDAO = new DepartmentMemberDAO();
    }
    
    /**
     * Lấy danh sách thành viên của một ban theo departmentID
     * @param departmentID ID của ban
     * @return Danh sách thành viên
     */
    public List<DepartmentMember> getDepartmentMembers(int departmentID) {
        return departmentMemberDAO.getDepartmentMembers(departmentID);
    }
    
    /**
     * Get all members of a specific department
     * @param clubDepartmentID The ID of the club department
     * @return List of department members
     */
    public List<DepartmentMember> getAllDepartmentMembers(int clubDepartmentID) {
        return departmentMemberDAO.getAllDepartmentMembers(clubDepartmentID);
    }
    
    /**
     * Get all members for a leader's department
     * @param leaderUserID The user ID of the department leader
     * @param clubID The club ID
     * @return List of department members
     */
    public List<DepartmentMember> getLeaderDepartmentMembers(String leaderUserID, int clubID) {
        return departmentMemberDAO.getLeaderDepartmentMembers(leaderUserID, clubID);
    }
    
    /**
     * Count total members for a leader's department
     * @param leaderUserID The user ID of the department leader
     * @param clubID The club ID
     * @return Total number of members
     */
    public int countLeaderDepartmentMembers(String leaderUserID, int clubID) {
        List<DepartmentMember> members = departmentMemberDAO.getLeaderDepartmentMembers(leaderUserID, clubID);
        return members != null ? members.size() : 0;
    }
    
    /**
     * Count active members for a leader's department
     * @param leaderUserID The user ID of the department leader
     * @param clubID The club ID
     * @return Number of active members
     */
    public int countActiveLeaderDepartmentMembers(String leaderUserID, int clubID) {
        List<DepartmentMember> members = departmentMemberDAO.getLeaderDepartmentMembers(leaderUserID, clubID);
        if (members == null) return 0;
        
        int activeCount = 0;
        for (DepartmentMember member : members) {
            if (member.isActive()) {
                activeCount++;
            }
        }
        return activeCount;
    }
    
    /**
     * Get member details by userClubID
     * @param userClubID The user club ID
     * @return DepartmentMember object or null if not found
     */
    public DepartmentMember getMemberDetails(int userClubID) {
        return departmentMemberDAO.getDepartmentMemberById(userClubID);
    }
    
    /**
     * Update member information (excluding role)
     * @param member The member with updated information
     * @return true if update was successful, false otherwise
     */
    public boolean updateMemberInfo(DepartmentMember member) {
        return departmentMemberDAO.updateMemberInfo(member);
    }
    
    /**
     * Add a new member to the department
     * @param member The member to add
     * @return true if addition was successful, false otherwise
     */
    public boolean addMember(DepartmentMember member) {
        return departmentMemberDAO.addMember(member);
    }
    
    /**
     * Remove a member from the department
     * @param userClubID The user club ID to remove
     * @return true if removal was successful, false otherwise
     */
    public boolean removeMember(int userClubID) {
        return departmentMemberDAO.removeMember(userClubID);
    }
    
    /**
     * Đếm số lượng thành viên trong một ban
     * @param departmentID ID của ban
     * @return Số lượng thành viên
     */
    public int countDepartmentMembers(int departmentID) {
        // Implement by fetching members first
        List<DepartmentMember> members = departmentMemberDAO.getDepartmentMembers(departmentID);
        return members != null ? members.size() : 0;
    }
    
    /**
     * Đếm số lượng thành viên theo vai trò trong một ban
     * @param departmentID ID của ban
     * @return Map chứa số lượng thành viên cho mỗi vai trò
     */
    public Map<String, Integer> countMembersByRole(int departmentID) {
        // Get all members
        List<DepartmentMember> members = departmentMemberDAO.getDepartmentMembers(departmentID);
        
        // Count by role
        Map<String, Integer> roleCount = new HashMap<>();
        for (DepartmentMember member : members) {
            String role = member.getRoleName();
            roleCount.put(role, roleCount.getOrDefault(role, 0) + 1);
        }
        
        return roleCount;
    }
    
    /**
     * Lấy danh sách thành viên mới nhất của một ban
     * @param departmentID ID của ban
     * @param limit Số lượng thành viên cần lấy
     * @return Danh sách thành viên mới nhất
     */
    public List<DepartmentMember> getRecentDepartmentMembers(int departmentID, int limit) {
        // Get all members
        List<DepartmentMember> members = departmentMemberDAO.getDepartmentMembers(departmentID);
        
        // Sort by join date, most recent first
        members.sort((m1, m2) -> {
            if (m1.getJoinDate() == null && m2.getJoinDate() == null) {
                return 0;
            }
            if (m1.getJoinDate() == null) {
                return 1;
            }
            if (m2.getJoinDate() == null) {
                return -1;
            }
            return m2.getJoinDate().compareTo(m1.getJoinDate()); // Reverse order
        });
        
        // Limit the number of results
        if (members.size() > limit) {
            members = members.subList(0, limit);
        }
        
        return members;
    }
      /**
     * Kiểm tra xem một người dùng có phải là trưởng ban không
     * @param userID ID của người dùng (String type)
     * @param departmentID ID của ban
     * @return true nếu là trưởng ban, false nếu không phải
     */
    public boolean isUserDepartmentLeader(String userID, int departmentID) {
        return departmentMemberDAO.isUserDepartmentLeader(userID, departmentID);
    }
}
