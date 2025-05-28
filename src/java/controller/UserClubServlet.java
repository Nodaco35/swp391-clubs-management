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

        // Kiểm tra quyền chủ nhiệm
        if (!userClubDAO.isClubPresident(user.getUserID(), clubID)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to manage this club");
            return;
        }

        // Xử lý phân trang và tìm kiếm
        String search = request.getParameter("search") != null ? request.getParameter("search") : "";
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        int pageSize = 3;

        List<UserClub> userClubs = userClubDAO.getAllUserClubsByClubId(clubID, page, pageSize);
        int totalRecords = userClubDAO.countUserClubs(clubID, search);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        // Lấy danh sách departments và roles để thêm/sửa
        List<Department> departments = userClubDAO.getDepartmentsByClubId(clubID);
        List<Roles> roles = userClubDAO.getRoles();

        String clubName = userClubDAO.getClubNameById(clubID);

        // Đặt thuộc tính để sử dụng trong JSP
        request.setAttribute("userClubs", userClubs);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("search", search);
        request.setAttribute("clubID", clubID);
        request.setAttribute("clubName", clubName);
        request.setAttribute("departments", departments);
        request.setAttribute("roles", roles);

        // Xử lý chỉnh sửa
        String action = request.getParameter("action");
        if ("edit".equals(action)) {
            int userClubID = Integer.parseInt(request.getParameter("id"));
            UserClub uc = userClubDAO.getUserClubById(userClubID);
            request.setAttribute("editUserClub", uc);
        }
        if ("search".equals(action)) {
            String keyWords = request.getParameter("search");
            userClubs = userClubDAO.searchUserClubsByKeyWord(clubID, keyWords, page, pageSize);
            if (userClubs != null) {
                request.setAttribute("userClubs", userClubs);
                request.setAttribute("message", "Tìm kiếm thành công");
            } else {
                request.setAttribute("error", "không tìm thấy");
            }
        }

        request.getRequestDispatcher("./view/admin/user-club.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int clubID = Integer.parseInt(request.getParameter("clubID"));
        if (!userClubDAO.isClubPresident(user.getUserID(), clubID)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to manage this club");
            return;
        }

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            String userID = request.getParameter("userID");
            // Kiểm tra xem thành viên đã tồn tại trong câu lạc bộ chưa
            if (userClubDAO.isUserClubExists(userID, clubID)) {
                request.setAttribute("error", "Thành viên đã tồn tại trong câu lạc bộ này!");
            } else {
                UserClub uc = new UserClub();
                uc.setUserID(userID);
                uc.setClubID(clubID);
                uc.setDepartmentID(Integer.parseInt(request.getParameter("departmentID")));
                uc.setRoleID(Integer.parseInt(request.getParameter("roleID")));
                uc.setIsActive(Boolean.parseBoolean(request.getParameter("isActive")));
                int newUserClubID = userClubDAO.addUserClub(uc);
                if (newUserClubID != -1) {
                    request.setAttribute("message", "Thêm thành viên thành công! ID: " + newUserClubID);
                } else {
                    request.setAttribute("error", "Thêm thành viên thất bại!");
                }
            }
        } else if ("update".equals(action)) {
            UserClub uc = new UserClub();
            uc.setUserClubID(Integer.parseInt(request.getParameter("userClubID")));
            uc.setDepartmentID(Integer.parseInt(request.getParameter("departmentID")));
            uc.setRoleID(Integer.parseInt(request.getParameter("roleID")));
            uc.setIsActive(Boolean.parseBoolean(request.getParameter("isActive")));
            if (userClubDAO.updateUserClub(uc)) {
                request.setAttribute("message", "Cập nhật thành viên thành công!");
            } else {
                request.setAttribute("error", "Cập nhật thành viên thất bại!");
            }
        } else if ("delete".equals(action)) {
            int userClubID = Integer.parseInt(request.getParameter("userClubID"));
            if (userClubDAO.deleteUserClub(userClubID)) {
                request.setAttribute("message", "Xóa thành viên thành công!");
            } else {
                request.setAttribute("error", "Xóa thành viên thất bại!");
            }
        }

        doGet(request, response);
    }
}
