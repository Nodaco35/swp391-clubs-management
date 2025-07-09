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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        String establishedDate = userClubDAO.getClubEstablishedDate(clubID);

        request.setAttribute("userClubs", userClubs);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("search", search);
        request.setAttribute("clubID", clubID);
        request.setAttribute("clubName", clubName);
        request.setAttribute("departments", departments);
        request.setAttribute("roles", roles);
        request.setAttribute("establishedDate", establishedDate);

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
                userClubs = userClubDAO.searchUserClubsByClubId(clubID, keyWords, page, pageSize);
                if (userClubs != null && !userClubs.isEmpty()) {
                    request.setAttribute("userClubs", userClubs);
                    request.setAttribute("message", "Tìm kiếm thành công");
                } else {
                    request.setAttribute("error", "Không tìm thấy");
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
        String establishedDate = userClubDAO.getClubEstablishedDate(clubID);
        if (establishedDate == null) {
            request.setAttribute("error", "Không thể thêm thành viên: Ngày thành lập câu lạc bộ không được xác định!");
            doGet(request, response);
            return;
        }

        if ("add".equals(action)) {
            String userID = request.getParameter("userID").trim();
            int roleID;
            int departmentID;
            String joinDateStr = request.getParameter("joinDate");
            java.util.Date joinDate;
            try {
                roleID = Integer.parseInt(request.getParameter("roleID"));
                departmentID = Integer.parseInt(request.getParameter("departmentID"));
                if (joinDateStr == null || joinDateStr.trim().isEmpty()) {
                    request.setAttribute("error", "Ngày tham gia không được để trống!");
                    doGet(request, response);
                    return;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dateFormat.setLenient(false);
                joinDate = dateFormat.parse(joinDateStr);
                java.util.Date estDate = dateFormat.parse(establishedDate.split(" ")[0]);
                if (joinDate.before(estDate)) {
                    request.setAttribute("error", "Ngày tham gia phải sau ngày thành lập câu lạc bộ!");
                    doGet(request, response);
                    return;
                }
                if (joinDate.after(new java.util.Date())) {
                    request.setAttribute("error", "Ngày tham gia không được là ngày trong tương lai!");
                    doGet(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Vai trò hoặc ban không hợp lệ!");
                doGet(request, response);
                return;
            } catch (ParseException e) {
                request.setAttribute("error", "Định dạng ngày tham gia không hợp lệ!");
                doGet(request, response);
                return;
            }

            if (!userClubDAO.isUserExists(userID)) {
                request.setAttribute("error", "Mã thành viên không tồn tại!");
                doGet(request, response);
                return;
            }

            if (roleID == 1 || roleID == 2) {
                departmentID = 3;
            } else if (departmentID == 3) {
                request.setAttribute("error", "Vai trò này chỉ thuộc các ban còn lại!");
                doGet(request, response);
                return;
            }

            if (userClubDAO.isUserClubExists(userID, clubID)) {
                request.setAttribute("error", "Thành viên đã tồn tại trong câu lạc bộ này!");
            } else if (roleID == 1 && userClubDAO.hasClubPresident(clubID)) {
                request.setAttribute("error", "Câu lạc bộ đã có chủ nhiệm!");
            } else if (roleID == 3 && userClubDAO.hasDepartmentHead(clubID, departmentID)) {
                request.setAttribute("error", "Ban này đã có trưởng ban!");
            } else {
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
                uc.setJoinDate(joinDate);
                System.out.println("Adding UserClub: userID=" + userID + ", clubID=" + clubID + ", clubDepartmentID=" + clubDepartmentID + ", roleID=" + roleID + ", joinDate=" + joinDate + ", isActive=" + uc.isIsActive());
                try {
                    int newUserClubID = userClubDAO.addUserClub(uc);
                    if (newUserClubID != -1) {
                        request.setAttribute("message", "Thêm thành viên thành công! ID: " + newUserClubID);
                    } else {
                        request.setAttribute("error", "Thêm thành viên thất bại! Vui lòng kiểm tra dữ liệu đầu vào.");
                    }
                } catch (RuntimeException e) {
                    request.setAttribute("error", "Lỗi khi thêm thành viên: " + e.getMessage());
                }
            }
        } else if ("update".equals(action)) {
            int userClubID;
            int roleID;
            int departmentID;
            String joinDateStr = request.getParameter("joinDate");
            java.util.Date joinDate;
            try {
                userClubID = Integer.parseInt(request.getParameter("userClubID"));
                roleID = Integer.parseInt(request.getParameter("roleID"));
                departmentID = Integer.parseInt(request.getParameter("departmentID"));
                if (joinDateStr == null || joinDateStr.trim().isEmpty()) {
                    request.setAttribute("error", "Ngày tham gia không được để trống!");
                    doGet(request, response);
                    return;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dateFormat.setLenient(false);
                joinDate = dateFormat.parse(joinDateStr);
                java.util.Date estDate = dateFormat.parse(establishedDate.split(" ")[0]);
                if (joinDate.before(estDate)) {
                    request.setAttribute("error", "Ngày tham gia phải sau ngày thành lập câu lạc bộ!");
                    doGet(request, response);
                    return;
                }
                if (joinDate.after(new java.util.Date())) {
                    request.setAttribute("error", "Ngày tham gia không được là ngày trong tương lai!");
                    doGet(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Dữ liệu không hợp lệ!");
                doGet(request, response);
                return;
            } catch (ParseException e) {
                request.setAttribute("error", "Định dạng ngày tham gia không hợp lệ!");
                doGet(request, response);
                return;
            }

            if (roleID == 1 || roleID == 2) {
                departmentID = 3;
            } else if (departmentID == 3) {
                request.setAttribute("error", "Vai trò này chỉ thuộc các ban còn lại!");
                doGet(request, response);
                return;
            }

            UserClub currentUc = userClubDAO.getUserClubById(userClubID);

            if (roleID == 1 && userClubDAO.hasClubPresident(clubID) && currentUc.getRoleID() != 1) {
                request.setAttribute("error", "Câu lạc bộ đã có chủ nhiệm!");
            } else if (roleID == 3 && departmentID != 0 && userClubDAO.hasDepartmentHead(clubID, departmentID) &&
                     currentUc.getRoleID() != 3) {
                request.setAttribute("error", "Ban này đã có trưởng ban!");
            } else {
                int clubDepartmentID = userClubDAO.getClubDepartmentIdByClubAndDepartment(clubID, departmentID);
                if (clubDepartmentID == -1) {
                    request.setAttribute("error", "Không tìm thấy ban trong câu lạc bộ này!");
                    doGet(request, response);
                    return;
                }

                UserClub uc = new UserClub();
                uc.setUserClubID(userClubID);
                uc.setClubID(clubID);
                uc.setClubDepartmentID(clubDepartmentID);
                uc.setRoleID(roleID);
                uc.setIsActive(request.getParameter("isActive") != null);
                uc.setJoinDate(joinDate);
                System.out.println("Updating UserClub: userClubID=" + userClubID + ", clubID=" + clubID + ", clubDepartmentID=" + clubDepartmentID + ", roleID=" + roleID + ", joinDate=" + joinDate + ", isActive=" + uc.isIsActive());
                try {
                    if (userClubDAO.updateUserClub(uc)) {
                        request.setAttribute("message", "Cập nhật thành viên thành công!");
                    } else {
                        request.setAttribute("error", "Cập nhật thành viên thất bại! Vui lòng kiểm tra dữ liệu đầu vào.");
                    }
                } catch (RuntimeException e) {
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