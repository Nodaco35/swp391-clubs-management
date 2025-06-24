package controller;

import dal.UserClubDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Users;
import models.UserClub;
import java.io.IOException;
import java.util.List;
import models.Department;
import models.Roles;

public class UserClubServlet extends HttpServlet {

    private UserClubDAO userClubDAO;

    @Override
    public void init() throws ServletException {
        userClubDAO = new UserClubDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int clubID;
        try {
            clubID = Integer.parseInt(request.getParameter("clubID"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Club ID");
            return;
        }

        if (!userClubDAO.isClubPresident(user.getUserID(), clubID)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to manage this club");
            return;
        }

        String search = request.getParameter("search") != null ? request.getParameter("search") : "";
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        int pageSize = 3;

        List<UserClub> userClubs = userClubDAO.getAllUserClubsByClubId(clubID, page, pageSize);
        int totalRecords = userClubDAO.countUserClubs(clubID, search);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        List<Department> departments = userClubDAO.getDepartmentsByClubId(clubID);
        List<Roles> roles = userClubDAO.getRoles();

        String clubName = userClubDAO.getClubNameById(clubID);

        request.setAttribute("userClubs", userClubs);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("search", search);
        request.setAttribute("clubID", clubID);
        request.setAttribute("clubName", clubName);
        request.setAttribute("departments", departments);
        request.setAttribute("roles", roles);

        String action = request.getParameter("action");
        if ("edit".equals(action)) {
            int userClubID = Integer.parseInt(request.getParameter("id"));
            UserClub uc = userClubDAO.getUserClubById(userClubID);
            request.setAttribute("editUserClub", uc);
        }
        if ("search".equals(action)) {
            String keyWords = request.getParameter("search");

            if (keyWords == null || keyWords.trim().isEmpty()) {
                userClubs = userClubDAO.getAllUserClubsByClubId(clubID, page, pageSize);
                request.setAttribute("userClubs", userClubs);
            } else {
                userClubs = userClubDAO.searchUserClubsByKeyWord(clubID, keyWords, page, pageSize);

                if (userClubs != null && !userClubs.isEmpty()) {
                    request.setAttribute("userClubs", userClubs);
                    request.setAttribute("message", "Tìm kiếm thành công");
                } else {
                    request.setAttribute("message", "Không tìm thấy");
                }
            }
        }

        request.getRequestDispatcher("./view/clubs-page/user-club.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int clubID;
        try {
            clubID = Integer.parseInt(request.getParameter("clubID"));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID câu lạc bộ không hợp lệ!");
            doGet(request, response);
            return;
        }

        if (!userClubDAO.isClubPresident(user.getUserID(), clubID)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền quản lý câu lạc bộ này!");
            return;
        }

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            String userID = request.getParameter("userID").trim();
            int roleID;
            int departmentID;
            try {
                roleID = Integer.parseInt(request.getParameter("roleID"));
                departmentID = Integer.parseInt(request.getParameter("departmentID"));
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Vai trò hoặc ban không hợp lệ!");
                doGet(request, response);
                return;
            }

            if (roleID == 1 || roleID == 2) {
                departmentID = 3;
            } else if (departmentID ==3) {
                request.setAttribute("error", "Vai trò này chỉ thuộc các ban còn lại!");
                doGet(request, response);
                return;
            }

            if (userClubDAO.isUserClubExists(userID, clubID)) {
                request.setAttribute("error", "Thành viên đã tồn tại trong câu lạc bộ này!");
            }
            else if (roleID == 1 && userClubDAO.hasClubPresident(clubID)) {
                request.setAttribute("error", "Câu lạc bộ đã có chủ nhiệm!");
            }
            else if (roleID == 3 && userClubDAO.hasDepartmentHead(clubID, departmentID)) {
                request.setAttribute("error", "Ban này đã có trưởng ban!");            } else {
                // Get the correct clubDepartmentID based on clubID and departmentID
                int clubDepartmentID = userClubDAO.getClubDepartmentIdByClubAndDepartment(clubID, departmentID);
                if (clubDepartmentID == -1) {
                    request.setAttribute("error", "Không tìm thấy ban trong câu lạc bộ này!");
                    doGet(request, response);
                    return;
                }
                
                UserClub uc = new UserClub();
                uc.setUserID(userID);
                uc.setClubID(clubID);
                uc.setClubDepartmentID(clubDepartmentID);
                uc.setRoleID(roleID);
                uc.setIsActive(request.getParameter("isActive") != null);
                try {
                    int newUserClubID = userClubDAO.addUserClub(uc);
                    if (newUserClubID != -1) {
                        request.setAttribute("message", "Thêm thành viên thành công! ID: " + newUserClubID);
                    } else {
                        request.setAttribute("error", "Thêm thành viên thất bại! Vui lòng kiểm tra dữ liệu đầu vào.");
                    }
                } catch (Exception e) {
                    request.setAttribute("error", "Lỗi khi thêm thành viên: " + e.getMessage());
                }
            }
        } else if ("update".equals(action)) {
            int userClubID;
            int roleID;
            int departmentID;
            try {
                userClubID = Integer.parseInt(request.getParameter("userClubID"));
                roleID = Integer.parseInt(request.getParameter("roleID"));
                departmentID = Integer.parseInt(request.getParameter("departmentID"));
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Dữ liệu không hợp lệ!");
                doGet(request, response);
                return;
            }

            if (roleID == 1 || roleID == 2) {
                departmentID = 3; 
            } else if (departmentID == 3 ) {
                request.setAttribute("error", "Vai trò này chỉ thuộc các ban còn lại!");
                doGet(request, response);
                return;
            }

            UserClub currentUc = userClubDAO.getUserClubById(userClubID);

            if (roleID == 1 && userClubDAO.hasClubPresident(clubID) && currentUc.getRoleID() != 1) {
                request.setAttribute("error", "Câu lạc bộ đã có chủ nhiệm!");
            }
            else if (roleID == 3 && departmentID != 0 && userClubDAO.hasDepartmentHead(clubID, departmentID) &&
                     currentUc.getRoleID() != 3) {
                request.setAttribute("error", "Ban này đã có trưởng ban!");            } else {
                // Get the correct clubDepartmentID based on clubID and departmentID
                int clubDepartmentID = userClubDAO.getClubDepartmentIdByClubAndDepartment(clubID, departmentID);
                if (clubDepartmentID == -1) {
                    request.setAttribute("error", "Không tìm thấy ban trong câu lạc bộ này!");
                    doGet(request, response);
                    return;
                }
                
                UserClub uc = new UserClub();
                uc.setUserClubID(userClubID);
                uc.setClubDepartmentID(clubDepartmentID);
                uc.setRoleID(roleID);
                uc.setIsActive(request.getParameter("isActive") != null);
                try {
                    if (userClubDAO.updateUserClub(uc)) {
                        request.setAttribute("message", "Cập nhật thành viên thành công!");
                    } else {
                        request.setAttribute("error", "Cập nhật thành viên thất bại! Vui lòng kiểm tra dữ liệu đầu vào.");
                    }
                } catch (Exception e) {
                    request.setAttribute("error", "Lỗi khi cập nhật thành viên: " + e.getMessage());
                }
            }
        } else if ("delete".equals(action)) {
            int userClubID;
            try {
                userClubID = Integer.parseInt(request.getParameter("userClubID"));
            } catch (NumberFormatException e) {
                request.setAttribute("error", "ID thành viên không hợp lệ!");
                doGet(request, response);
                return;
            }

            try {
                if (userClubDAO.deleteUserClub(userClubID)) {
                    request.setAttribute("message", "Xóa thành viên thành công!");
                } else {
                    request.setAttribute("error", "Xóa thành viên thất bại!");
                }
            } catch (Exception e) {
                request.setAttribute("error", "Lỗi khi xóa thành viên: " + e.getMessage());
            }
        }

        doGet(request, response);
    }
}