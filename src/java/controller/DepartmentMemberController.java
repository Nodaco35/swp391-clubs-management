package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import models.DepartmentMember;
import models.Users;
import service.DepartmentMemberService;

/**
 * Controller for managing department members
 */
public class DepartmentMemberController extends HttpServlet {
    private final DepartmentMemberService departmentMemberService;
    
    public DepartmentMemberController() {
        this.departmentMemberService = new DepartmentMemberService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String userID = currentUser.getUserID();
        int clubID = 0;
        
        try {
            clubID = Integer.parseInt(request.getParameter("clubID"));
        } catch (NumberFormatException e) {
            // Handle the exception or get club ID from session
            Object clubIdObj = session.getAttribute("clubID");
            if (clubIdObj != null) {
                clubID = (Integer) clubIdObj;
            } else {
                response.sendRedirect(request.getContextPath() + "/user-clubs");
                return;
            }
        }
        
        switch (action) {
            case "list":
                listMembers(request, response, userID, clubID);
                break;
            case "add":
                showAddForm(request, response, clubID);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteMember(request, response, clubID);
                break;
            case "view":
                viewMemberDetails(request, response);
                break;
            default:
                listMembers(request, response, userID, clubID);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String userID = currentUser.getUserID();
        int clubID = 0;
        
        try {
            clubID = Integer.parseInt(request.getParameter("clubID"));
        } catch (NumberFormatException e) {
            // Handle the exception or get club ID from session
            Object clubIdObj = session.getAttribute("clubID");
            if (clubIdObj != null) {
                clubID = (Integer) clubIdObj;
            } else {
                response.sendRedirect(request.getContextPath() + "/user-clubs");
                return;
            }
        }
        
        switch (action) {
            case "add":
                addMember(request, response, clubID);
                break;
            case "update":
                updateMember(request, response, clubID);
                break;
            default:
                listMembers(request, response, userID, clubID);
                break;
        }
    }
      /**
     * List all members of the department
     * @param request The HttpServletRequest object
     * @param response The HttpServletResponse object
     * @param userID The user ID of the department leader
     * @param clubID The club ID
     */
    private void listMembers(HttpServletRequest request, HttpServletResponse response, String userID, int clubID) throws ServletException, IOException {
        List<DepartmentMember> members = departmentMemberService.getLeaderDepartmentMembers(userID, clubID);
        int totalMembers = departmentMemberService.countLeaderDepartmentMembers(userID, clubID);
        int activeMembers = departmentMemberService.countActiveLeaderDepartmentMembers(userID, clubID);
        
        request.setAttribute("members", members);
        request.setAttribute("totalMembers", totalMembers);
        request.setAttribute("activeMembers", activeMembers);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/view/student/department-leader/members.jsp");
        dispatcher.forward(request, response);
    }
    
    /**
     * Show the add member form
     * @param request The HttpServletRequest object
     * @param response The HttpServletResponse object
     * @param clubID The club ID
     */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response, int clubID) throws ServletException, IOException {
        // Additional attributes can be set here if needed
        request.setAttribute("clubID", clubID);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/view/student/department-leader/add-member.jsp");
        dispatcher.forward(request, response);
    }
    
    /**
     * Add a new member to the department
     * @param request The HttpServletRequest object
     * @param response The HttpServletResponse object
     * @param clubID The club ID
     */
    private void addMember(HttpServletRequest request, HttpServletResponse response, int clubID) throws ServletException, IOException {
        try {
            String userId = request.getParameter("userId");
            int clubDepartmentId = Integer.parseInt(request.getParameter("clubDepartmentId"));
            int roleId = Integer.parseInt(request.getParameter("roleId")); // Default role = 4 (Member)
            boolean isActive = true; // Default to active
            
            DepartmentMember member = new DepartmentMember();
            member.setUserID(userId);
            member.setClubID(clubID);
            member.setClubDepartmentID(clubDepartmentId);
            member.setRoleID(roleId);
            member.setActive(isActive);
            
            boolean success = departmentMemberService.addMember(member);
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Thêm thành viên thành công!");
            } else {
                request.getSession().setAttribute("errorMessage", "Không thể thêm thành viên. Vui lòng thử lại!");
            }
            
            response.sendRedirect(request.getContextPath() + "/department-members?clubID=" + clubID);
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Dữ liệu không hợp lệ. Vui lòng thử lại!");
            response.sendRedirect(request.getContextPath() + "/department-members?clubID=" + clubID);
        }
    }
    
    /**
     * Show the edit member form
     * @param request The HttpServletRequest object
     * @param response The HttpServletResponse object
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int userClubId = Integer.parseInt(request.getParameter("id"));
            DepartmentMember member = departmentMemberService.getMemberDetails(userClubId);
            
            if (member != null) {
                request.setAttribute("member", member);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/view/student/department-leader/member-detail.jsp");
                dispatcher.forward(request, response);
            } else {
                request.getSession().setAttribute("errorMessage", "Không tìm thấy thành viên.");
                response.sendRedirect(request.getContextPath() + "/department-members");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID thành viên không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/department-members");
        }
    }
    
    /**
     * Update a member's information
     * @param request The HttpServletRequest object
     * @param response The HttpServletResponse object
     * @param clubID The club ID
     */
    private void updateMember(HttpServletRequest request, HttpServletResponse response, int clubID) throws ServletException, IOException {
        try {
            int userClubId = Integer.parseInt(request.getParameter("userClubId"));
            boolean isActive = request.getParameter("isActive") != null;
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            
            DepartmentMember member = departmentMemberService.getMemberDetails(userClubId);
            
            if (member != null) {
                member.setActive(isActive);
                member.setFullName(fullName);
                member.setEmail(email);
                
                boolean success = departmentMemberService.updateMemberInfo(member);
                
                if (success) {
                    request.getSession().setAttribute("successMessage", "Cập nhật thông tin thành viên thành công!");
                } else {
                    request.getSession().setAttribute("errorMessage", "Không thể cập nhật thông tin thành viên. Vui lòng thử lại!");
                }
            } else {
                request.getSession().setAttribute("errorMessage", "Không tìm thấy thành viên.");
            }
            
            response.sendRedirect(request.getContextPath() + "/department-members?clubID=" + clubID);
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID thành viên không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/department-members?clubID=" + clubID);
        }
    }
    
    /**
     * Delete a member from the department
     * @param request The HttpServletRequest object
     * @param response The HttpServletResponse object
     * @param clubID The club ID
     */
    private void deleteMember(HttpServletRequest request, HttpServletResponse response, int clubID) throws ServletException, IOException {
        try {
            int userClubId = Integer.parseInt(request.getParameter("id"));
            boolean success = departmentMemberService.removeMember(userClubId);
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Xóa thành viên thành công!");
            } else {
                request.getSession().setAttribute("errorMessage", "Không thể xóa thành viên. Vui lòng thử lại!");
            }
            
            response.sendRedirect(request.getContextPath() + "/department-members?clubID=" + clubID);
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID thành viên không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/department-members?clubID=" + clubID);
        }
    }
    
    /**
     * View member details
     * @param request The HttpServletRequest object
     * @param response The HttpServletResponse object
     */
    private void viewMemberDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int userClubId = Integer.parseInt(request.getParameter("id"));
            DepartmentMember member = departmentMemberService.getMemberDetails(userClubId);
            
            if (member != null) {
                request.setAttribute("member", member);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/view/student/department-leader/member-detail.jsp");
                dispatcher.forward(request, response);
            } else {
                request.getSession().setAttribute("errorMessage", "Không tìm thấy thành viên.");
                response.sendRedirect(request.getContextPath() + "/department-members");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID thành viên không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/department-members");
        }
    }
}
