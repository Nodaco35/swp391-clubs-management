
package controller;

import dal.ClubDAO;
import dal.PeriodicReportDAO;
import dal.ClubCreationPermissionDAO;
import dal.NotificationDAO;
import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.PeriodicReport;
import models.Users;
import java.io.IOException;
import java.util.List;

public class ICServlet extends HttpServlet {

    private NotificationDAO notificationDAO;
    private ClubCreationPermissionDAO permissionDAO;

    @Override
    public void init() throws ServletException {
        notificationDAO = new NotificationDAO();
        permissionDAO = new ClubCreationPermissionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserDAO ud = new UserDAO();
        Users user = (Users) session.getAttribute("user");
        String action = request.getParameter("action");

        if (action == null) {
            ClubDAO clubDAO = new ClubDAO();
            PeriodicReportDAO reportDAO = new PeriodicReportDAO();

            // Thống kê
            int pendingReports = reportDAO.countByStatus("PENDING");
            int approvedReports = reportDAO.countByStatus("APPROVED");
            int overdueClubs = reportDAO.countOverdueClubs();
            int totalActiveClubs = clubDAO.getTotalActiveClubs();

            // Danh sách báo cáo theo từng trạng thái
            List<PeriodicReport> pendingReportsList = reportDAO.getReportsByStatus("PENDING");
            List<PeriodicReport> approvedReportsList = reportDAO.getReportsByStatus("APPROVED");

            // Truyền dữ liệu sang view
            request.setAttribute("pendingReports", pendingReports);
            request.setAttribute("approvedReports", approvedReports);
            request.setAttribute("overdueClubs", overdueClubs);
            request.setAttribute("totalActiveClubs", totalActiveClubs);

            request.setAttribute("pendingReportsList", pendingReportsList);
            request.setAttribute("approvedReportsList", approvedReportsList);

            request.getRequestDispatcher("/view/ic/dashboard.jsp").forward(request, response);
        } else if (action.equals("grantPermission")) {
            request.getRequestDispatcher("view/ic/grantPermission.jsp").forward(request, response);
        } else if (action.equals("grantPermisstionForUser")) {
            // Xử lý cấp quyền
            ClubCreationPermissionDAO ccp = new ClubCreationPermissionDAO();
            String userId = (String) request.getParameter("id"); // hoặc lấy từ param nếu bạn truyền userID
            String adminId = ((Users) session.getAttribute("user")).getUserID();

            Users userFind = ud.getUserByID(userId);

            ccp.insertClubPermission(userId, adminId);

            int numberOfPermissions = ccp.countActiveClubPermission(userId);
            request.setAttribute("activePermissionCount", numberOfPermissions);
            request.setAttribute("userFind", userFind);
            request.setAttribute("userSearchID", userId);

            // Forward về trang grantPermission.jsp (giả sử bạn forward lại)
            request.getRequestDispatcher("view/ic/grantPermission.jsp").forward(request, response);
        } else if (action.equals("findUserById")) {

            String userSearchID = request.getParameter("userSearchID");
            Users userFind = ud.getUserByID(userSearchID);
            ClubCreationPermissionDAO ccp = new ClubCreationPermissionDAO();
            int numberOfPermissions = ccp.countActiveClubPermission(userSearchID);
            request.setAttribute("activePermissionCount", numberOfPermissions);
            request.setAttribute("userFind", userFind);
            request.setAttribute("userSearchID", userSearchID);

            request.getRequestDispatcher("view/ic/grantPermission.jsp").forward(request, response);
        } else if (action.equals("DeleteByUserId")) {
            ClubCreationPermissionDAO ccp = new ClubCreationPermissionDAO();
            String userId = (String) request.getParameter("id");
            ccp.revokeClubPermission(userId);

            int numberOfPermissions = ccp.countActiveClubPermission(userId);

            Users userFind = ud.getUserByID(userId);
            request.setAttribute("activePermissionCount", numberOfPermissions);
            request.setAttribute("userFind", userFind);
            request.setAttribute("userSearchID", userId);

            request.getRequestDispatcher("view/ic/grantPermission.jsp").forward(request, response);
            
            
            
            
            
            
            
            
        } else if (action.equals("approvePermissionRequest") || action.equals("rejectPermissionRequest")) {
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            int id;
            try {
                id = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID");
                return;
            }
            String userId = request.getParameter("userId");
            if (userId == null || user.getUserID() == null) {
                request.setAttribute("errorMessage", "Thông tin người dùng không hợp lệ.");
                request.setAttribute("pendingRequests", permissionDAO.getPermissionsByStatus("PENDING"));
                request.getRequestDispatcher("view/ic/grantPermission.jsp").forward(request, response);
                return;
            }
            boolean success;
            String message;
            if (action.equals("approvePermissionRequest")) {
                success = permissionDAO.approveRequest(id, user.getUserID());
                message = success ? "Đã duyệt đơn thành công!" : "Duyệt đơn thất bại. Vui lòng thử lại.";
                if (success) {
                    notificationDAO.sentToPerson1(user.getUserID(), userId, 
                        "Đơn xin quyền tạo câu lạc bộ được duyệt", 
                        "Đơn xin quyền tạo câu lạc bộ của bạn đã được duyệt. Bạn có thể tạo câu lạc bộ ngay bây giờ!", 
                        "HIGH");
                }
            } else {
                success = permissionDAO.rejectRequest(id, user.getUserID());
                message = success ? "Đã từ chối đơn thành công!" : "Từ chối đơn thất bại. Vui lòng thử lại.";
                if (success) {
                    notificationDAO.sentToPerson1(user.getUserID(), userId, 
                        "Đơn xin quyền tạo câu lạc bộ bị từ chối", 
                        "Đơn xin quyền tạo câu lạc bộ của bạn đã bị từ chối. Vui lòng liên hệ IC để biết thêm chi tiết.", 
                        "HIGH");
                }
            }
            request.setAttribute(success ? "successMessage" : "errorMessage", message);
            request.setAttribute("pendingRequests", permissionDAO.getPermissionsByStatus("PENDING"));
            request.getRequestDispatcher("view/ic/grantPermission.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "IC Servlet for managing club permission requests";
    }
}
