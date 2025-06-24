package controller;

import dal.DepartmentMemberDAO;
import dal.DepartmentDashboardDAO;
import models.DepartmentMember;
import models.Users;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class DepartmentMemberServlet extends HttpServlet {
    
    private DepartmentMemberDAO memberDAO;
    private DepartmentDashboardDAO dashboardDAO;
    
    @Override
    public void init() throws ServletException {
        memberDAO = new DepartmentMemberDAO();
        dashboardDAO = new DepartmentDashboardDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        // Kiểm tra đăng nhập
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
          try {            // Debug logging
            System.out.println("DepartmentMemberServlet: User ID = " + currentUser.getUserID());
            
            // Lấy club department ID của user (user chỉ có thể là trưởng ban 1 ban trong 1 CLB)
            int clubDepartmentID = memberDAO.getClubDepartmentIdByLeader(currentUser.getUserID());
            System.out.println("DepartmentMemberServlet: ClubDepartment ID = " + clubDepartmentID);
            
            if (clubDepartmentID == 0) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này hoặc không phải là trưởng ban");
                return;
            }
            
            String action = request.getParameter("action");
            if (action == null) {
                action = "list";
            }
            
            switch (action) {
                case "list":
                    handleListMembers(request, response, clubDepartmentID);
                    break;
                case "search":
                    handleSearchMembers(request, response, clubDepartmentID);
                    break;
                case "searchStudents":
                    handleSearchStudents(request, response, clubDepartmentID);
                    break;
                default:
                    handleListMembers(request, response, clubDepartmentID);
                    break;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect("auth");
            return;
        }          try {            // Lấy club department ID của user (user chỉ có thể là trưởng ban 1 ban trong 1 CLB)
            int clubDepartmentID = memberDAO.getClubDepartmentIdByLeader(currentUser.getUserID());
            System.out.println("DepartmentMemberServlet POST: ClubDepartment ID = " + clubDepartmentID);
            
            if (clubDepartmentID == 0) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này hoặc không phải là trưởng ban");
                return;
            }
            
            String action = request.getParameter("action");
            
            switch (action) {
                case "updateStatus":
                    handleUpdateStatus(request, response, clubDepartmentID);
                    break;
                case "removeMember":
                    handleRemoveMember(request, response, clubDepartmentID);
                    break;
                case "addMember":
                    handleAddMember(request, response, clubDepartmentID);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Hành động không hợp lệ");
                    break;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage());
        }
    }
      private void handleListMembers(HttpServletRequest request, HttpServletResponse response, int clubDepartmentID)
            throws ServletException, IOException {
        
        // Lấy tham số phân trang
        int page = 1;
        int pageSize = 10;
        
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        String pageSizeParam = request.getParameter("pageSize");
        if (pageSizeParam != null && !pageSizeParam.isEmpty()) {
            try {
                pageSize = Integer.parseInt(pageSizeParam);
                if (pageSize < 5) pageSize = 5;
                if (pageSize > 50) pageSize = 50;
            } catch (NumberFormatException e) {
                pageSize = 10;
            }
        }
          // Lấy danh sách thành viên
        List<DepartmentMember> members = memberDAO.getDepartmentMembers(clubDepartmentID, page, pageSize);
        int totalMembers = memberDAO.getTotalMembersCount(clubDepartmentID);
        int totalPages = (int) Math.ceil((double) totalMembers / pageSize);
        
        // Lấy thông tin department để hiển thị
        // (Có thể tái sử dụng thông tin từ DepartmentDashboardDAO nếu cần)
        
        // Đưa dữ liệu vào request
        request.setAttribute("members", members);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalMembers", totalMembers);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("clubDepartmentID", clubDepartmentID);
        
        // Forward đến JSP
        request.getRequestDispatcher("view/student/department-leader/members.jsp").forward(request, response);
    }
      private void handleSearchMembers(HttpServletRequest request, HttpServletResponse response, int clubDepartmentID)
            throws ServletException, IOException {
        
        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = "";
        
        int page = 1;
        int pageSize = 10;
        
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
          // Tìm kiếm thành viên
        List<DepartmentMember> members;
        if (keyword.trim().isEmpty()) {
            members = memberDAO.getDepartmentMembers(clubDepartmentID, page, pageSize);
        } else {
            members = memberDAO.searchMembers(clubDepartmentID, keyword.trim(), page, pageSize);
        }
        
        int totalMembers = memberDAO.getTotalMembersCount(clubDepartmentID);
        int totalPages = (int) Math.ceil((double) totalMembers / pageSize);
        
        request.setAttribute("members", members);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalMembers", totalMembers);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("clubDepartmentID", clubDepartmentID);
        request.setAttribute("keyword", keyword);
        
        request.getRequestDispatcher("view/student/department-leader/members.jsp").forward(request, response);
    }
      private void handleSearchStudents(HttpServletRequest request, HttpServletResponse response, int clubDepartmentID)
            throws IOException {
        
        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = "";
        
        List<Users> students = memberDAO.searchStudentsNotInClubDepartment(clubDepartmentID, keyword.trim(), 10);
        
        // Trả về JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Gson gson = new Gson();
        String json = gson.toJson(students);
        response.getWriter().write(json);
    }      private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response, int clubDepartmentID)
            throws IOException {
        
        String userID = request.getParameter("userID");
        String isActiveParam = request.getParameter("isActive");
        String status = request.getParameter("status");
        
        if (userID == null || isActiveParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu thông tin cần thiết");
            return;
        }
        
        try {
            boolean isActive = Boolean.parseBoolean(isActiveParam);
              boolean success = memberDAO.updateMemberStatus(userID, clubDepartmentID, isActive, status);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            String json = "{\"success\":" + success + "}";
            response.getWriter().write(json);
            
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Có lỗi xảy ra");
        }
    }      private void handleRemoveMember(HttpServletRequest request, HttpServletResponse response, int clubDepartmentID)
            throws IOException {
        
        String userID = request.getParameter("userID");
        
        if (userID == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu ID người dùng");
            return;
        }
          boolean success = memberDAO.removeMemberFromDepartment(userID, clubDepartmentID);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String json = "{\"success\":" + success + "}";
        response.getWriter().write(json);
    }
    
    private void handleAddMember(HttpServletRequest request, HttpServletResponse response, int clubDepartmentID)
            throws IOException {
          String userID = request.getParameter("userID");
        String roleIDParam = request.getParameter("roleID");
        
        if (userID == null || roleIDParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu thông tin cần thiết");
            return;
        }
        
        try {
            int roleID = Integer.parseInt(roleIDParam);
            
            boolean success = memberDAO.addMemberToClubDepartment(userID, clubDepartmentID, roleID);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            String json = "{\"success\":" + success + "}";
            response.getWriter().write(json);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thông tin không hợp lệ");
        }
    }
}
