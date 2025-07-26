package controller;

import dal.DepartmentMemberDAO;
import dal.DepartmentDashboardDAO;
import models.DepartmentMember;
import models.Users;
import models.Tasks;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dal.ClubDepartmentDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.ActivedMembers;

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
                    // Session-based clubID management for maintaining state across requests
                    String clubIDParam = request.getParameter("clubID");
                    HttpSession sessionForClub = request.getSession();
                    int clubID;
                    
                    if (clubIDParam != null && !clubIDParam.trim().isEmpty()) {
                        // Parse clubID parameter and save to session for future requests
                        clubID = Integer.parseInt(clubIDParam);
                        sessionForClub.setAttribute("currentClubID", clubID);
                        System.out.println("Saved clubID to session: " + clubID);
                    } else {
                        // Retrieve clubID from session if not provided in parameter
                        Integer sessionClubID = (Integer) sessionForClub.getAttribute("currentClubID");
                        if (sessionClubID == null) {
                            System.err.println("ERROR: No clubID in parameter or session");
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu thông tin Club ID. Vui lòng truy cập từ dashboard.");
                            return;
                        }
                        clubID = sessionClubID;
                        System.out.println("Retrieved clubID from session: " + clubID);
                    }
                    
                    ClubDepartmentDAO cp = new ClubDepartmentDAO();
                    int departmentID = cp.getClubDepartmentID(currentUser.getUserID(), clubID);

                    handleListMembers(request, response, departmentID);
                    break;
                case "search":
                    // Search action also requires clubID context from session
                    HttpSession searchSession = request.getSession();
                    Integer searchClubID = (Integer) searchSession.getAttribute("currentClubID");
                    if (searchClubID != null) {
                        System.out.println("Search using clubID from session: " + searchClubID);
                        // Pass clubID to JSP for context
                        request.setAttribute("currentClubID", searchClubID);
                    }
                    
                    handleSearchMembers(request, response, clubDepartmentID);
                    break;
                case "searchStudents":
                    handleSearchStudents(request, response, clubDepartmentID);
                    break;
                case "getMemberDetail":
                    handleMemberDetail(request, response, clubDepartmentID);
                    break;
            }

            //Evualte Point ( Chấm điểm thành viên )
            if ("evaluatePoint".equals(action)) {
                ClubDepartmentDAO cd = new ClubDepartmentDAO();
                // PrintWriter out = response.getWriter(); // REMOVED: unused variable

                int clubDepartmentID_ = Integer.parseInt(request.getParameter("clubDepartmentID"));

                List<ActivedMembers> members = cd.getActiveMembersByClubAndDepartment(clubDepartmentID_,currentUser.getUserID());
                
                for (ActivedMembers member : members) {
                    member.setTaskcount(cd.getTaskCountByUserID(member.getUserID()));
                    member.setPositive(cd.getTaskQualityByUserIDAndTaskType(member.getUserID(), "positive"));
                    member.setNeutral(cd.getTaskQualityByUserIDAndTaskType(member.getUserID(), "neutral"));
                    member.setNegative(cd.getTaskQualityByUserIDAndTaskType(member.getUserID(), "negative"));
                }
                
                int clubID = cd.getClubIDByClubDepartmentID(clubDepartmentID_);

                
                request.setAttribute("clubID", clubID);
                request.setAttribute("members", members);
                request.setAttribute("clubDepartmentID", clubDepartmentID_);

                request.getRequestDispatcher("view/student/department-leader/evaluate-point.jsp").forward(request, response);
                return;
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
        }
        try {            // Lấy club department ID của user (user chỉ có thể là trưởng ban 1 ban trong 1 CLB)
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
            }

            if ("submitRating".equals(action)) {
                String member = request.getParameter("userID");
                int points = Integer.parseInt(request.getParameter("points"));
                ClubDepartmentDAO cd = new ClubDepartmentDAO();
                int clubDepartmentID_ = Integer.parseInt(request.getParameter("clubDepartmentID"));

                boolean success = cd.updateProgressPoint(member, points);
                
                if (success) {
                    request.setAttribute("mes", "Update successfully");
                } else {
                    request.setAttribute("err", "Update failure");
                }

                List<ActivedMembers> members = cd.getActiveMembersByClubAndDepartment(clubDepartmentID_,currentUser.getUserID());
                request.setAttribute("members", members);
                request.setAttribute("clubDepartmentID", clubDepartmentID_);
                request.getRequestDispatcher("view/student/department-leader/evaluate-point.jsp").forward(request, response);
                return;
            } else if ("deleteRating".equals(action)) {
                String member = request.getParameter("userID");

                ClubDepartmentDAO cd = new ClubDepartmentDAO();

                int clubDepartmentID_ = Integer.parseInt(request.getParameter("clubDepartmentID"));

                boolean success = cd.clearProgressPoint(member);
                if (success) {
                    request.setAttribute("mesDelete", "Delete successfully");
                } else {
                    request.setAttribute("err", "Delete failure");
                }

                List<ActivedMembers> members = cd.getActiveMembersByClubAndDepartment(clubDepartmentID_,currentUser.getUserID());
                request.setAttribute("members", members);
                request.setAttribute("clubDepartmentID", clubDepartmentID_);
                request.getRequestDispatcher("view/student/department-leader/evaluate-point.jsp").forward(request, response);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    private void handleListMembers(HttpServletRequest request, HttpServletResponse response, int clubDepartmentID)
            throws ServletException, IOException {

        // Pagination parameters
        int page = 1;
        int pageSize = 10;

        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        // Force pageSize to 4 for consistent pagination
        pageSize = 4;        // Retrieve department members with pagination
        List<DepartmentMember> members = memberDAO.getDepartmentMembers(clubDepartmentID, page, pageSize);
        
        // Get member statistics using single optimized query
        models.MemberStatistics stats = memberDAO.getMemberStatistics(clubDepartmentID);
        int totalPages = (int) Math.ceil((double) stats.getTotalMembers() / pageSize);
        // Lấy thông tin user và department để hiển thị sidebar
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        String departmentName = "";
        try {
            var dashboard = dashboardDAO.getCompleteDashboard(currentUser.getUserID());
            if (dashboard != null) {
                departmentName = dashboard.getDepartmentName();
            }
        } catch (Exception e) {
            System.out.println("Error getting department name: " + e.getMessage());
        }

        // Prepare request attributes for JSP
        request.setAttribute("members", members);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalMembers", stats.getTotalMembers());
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("activeMembers", stats.getActiveMembers());
        request.setAttribute("inactiveMembers", stats.getInactiveMembers());
        request.setAttribute("clubDepartmentID", clubDepartmentID);
        request.setAttribute("currentUser", currentUser);
        request.setAttribute("departmentName", departmentName);
        
        // Pass clubID to JSP for pagination context
        HttpSession sessionForJSP = request.getSession();
        Integer currentClubID = (Integer) sessionForJSP.getAttribute("currentClubID");
        if (currentClubID != null) {
            request.setAttribute("currentClubID", currentClubID);
        }

        // Forward đến JSP
        request.getRequestDispatcher("view/student/department-leader/members.jsp").forward(request, response);
    }

    private void handleSearchMembers(HttpServletRequest request, HttpServletResponse response, int clubDepartmentID)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        if (keyword == null) {
            keyword = "";
        }

        int page = 1;
        int pageSize = 4;  // 🔧 ĐỔI từ 10 thành 4

        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                page = 1;
            }
        }        // 🚀 ĐƠN GIẢN HÓA: Tìm kiếm thành viên
        List<DepartmentMember> members;
        int totalMembers;

        if (keyword.trim().isEmpty()) {
            members = memberDAO.getDepartmentMembers(clubDepartmentID, page, pageSize);
            totalMembers = memberDAO.getTotalMembersCount(clubDepartmentID);
        } else {
            members = memberDAO.searchMembers(clubDepartmentID, keyword.trim(), page, pageSize);
            totalMembers = memberDAO.getSearchMembersCount(clubDepartmentID, keyword.trim());
        }
        int totalPages = (int) Math.ceil((double) totalMembers / pageSize);

        // 🚀 ĐƠN GIẢN HÓA: Lấy statistics trong 1 query (không phụ thuộc search)
        models.MemberStatistics stats = memberDAO.getMemberStatistics(clubDepartmentID);

        // Lấy thông tin user và department để hiển thị sidebar
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        String departmentName = "";
        try {
            var dashboard = dashboardDAO.getCompleteDashboard(currentUser.getUserID());
            if (dashboard != null) {
                departmentName = dashboard.getDepartmentName();
            }
        } catch (Exception e) {
            System.out.println("Error getting department name: " + e.getMessage());
        }

        request.setAttribute("members", members);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalMembers", totalMembers);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("activeMembers", stats.getActiveMembers());
        request.setAttribute("inactiveMembers", stats.getInactiveMembers());
        request.setAttribute("clubDepartmentID", clubDepartmentID);
        request.setAttribute("keyword", keyword);
        request.setAttribute("currentUser", currentUser);
        request.setAttribute("departmentName", departmentName);

        request.getRequestDispatcher("view/student/department-leader/members.jsp").forward(request, response);
    }

    private void handleSearchStudents(HttpServletRequest request, HttpServletResponse response, int clubDepartmentID)
            throws IOException {
        String keyword = request.getParameter("keyword");
        if (keyword == null) {
            keyword = "";
        }

        List<Users> students = memberDAO.searchStudentsNotInDepartment(clubDepartmentID, keyword.trim(), 10);

        // Trả về JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        String json = gson.toJson(students);
        response.getWriter().write(json);
    }

    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response, int clubDepartmentID)
            throws IOException {

        String userID = request.getParameter("userID");
        String isActiveParam = request.getParameter("isActive");
        String status = request.getParameter("status");

        if (userID == null || isActiveParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu thông tin cần thiết");
            return;
        }

        try {
            // Kiểm tra xem người dùng có phải là trưởng ban không
            DepartmentMember member = memberDAO.getMemberDetail(clubDepartmentID, userID);
            HttpSession session = request.getSession();
            Users currentUser = (Users) session.getAttribute("user");

            if (member != null && member.getRoleName().equals("Trưởng ban") && !userID.equals(currentUser.getUserID())) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String json = "{\"success\": false, \"message\": \"Không thể thay đổi trạng thái của Trưởng ban\"}";
                response.getWriter().write(json);
                return;
            }

            boolean isActive = Boolean.parseBoolean(isActiveParam);
            boolean success = memberDAO.updateMemberStatus(userID, clubDepartmentID, isActive, status);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String json = "{\"success\":" + success + "}";
            response.getWriter().write(json);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Có lỗi xảy ra");
        }
    }

    private void handleRemoveMember(HttpServletRequest request, HttpServletResponse response, int clubDepartmentID)
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

    private void handleMemberDetail(HttpServletRequest request, HttpServletResponse response, int clubDepartmentID)
            throws IOException {
        String userID = request.getParameter("userID");
        System.out.println("handleMemberDetail: clubDepartmentID=" + clubDepartmentID + ", userID=" + userID);

        // Đảm bảo trả về JSON cho mọi response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (userID == null || userID.trim().isEmpty()) {
            response.getWriter().write("{\"error\":true,\"message\":\"Thiếu ID người dùng\"}");
            return;
        }

        try {
            // Lấy thông tin thành viên
            DepartmentMember member = memberDAO.getMemberDetail(clubDepartmentID, userID);
            System.out.println("getMemberDetail result: " + (member != null ? "FOUND" : "NOT FOUND"));

            if (member == null) {
                response.getWriter().write("{\"error\":true,\"message\":\"Không tìm thấy thành viên với ID = " + userID + " trong ban ID = " + clubDepartmentID + "\"}");
                return;
            }

            // Lấy danh sách công việc (nếu có lỗi, trả về danh sách trống)
            List<Tasks> tasks = new ArrayList<>();
            try {
                tasks = memberDAO.getMemberTasks(userID, clubDepartmentID);
                System.out.println("Tasks found: " + tasks.size());
            } catch (Exception e) {
                System.err.println("Error getting tasks: " + e.getMessage());
                e.printStackTrace();
            }

            // Tạo SimpleDateFormat để xử lý Date trong JSON
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            // Tạo map để chứa kết quả
            Map<String, Object> result = new HashMap<>();
            result.put("member", member);
            result.put("tasks", tasks);            // Chuyển kết quả sang JSON
            try {
                String json = gson.toJson(result);
                response.getWriter().write(json);
            } catch (Exception e) {
                System.err.println("Error serializing to JSON: " + e.getMessage());
                e.printStackTrace();

                // Nếu lỗi khi serialize, trả về thông báo lỗi dạng JSON
                response.getWriter().write("{\"error\":true,\"message\":\"Lỗi khi xử lý dữ liệu thành viên: " + e.getMessage() + "\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\":true,\"message\":\"Lỗi khi lấy thông tin thành viên: " + e.getMessage() + "\"}");
        }
    }
}
