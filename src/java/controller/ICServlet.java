package controller;

import dal.ClubDAO;
import dal.PeriodicReportDAO;
import dal.ClubCreationPermissionDAO;
import dal.CreatedClubApplicationsDAO;
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
import models.ClubCreationPermissions;
import models.CreatedClubApplications;

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
        ClubDAO clubDAO = new ClubDAO();
        ClubCreationPermissionDAO ccp = new ClubCreationPermissionDAO();
        CreatedClubApplicationsDAO cca = new CreatedClubApplicationsDAO();

        Users user = (Users) session.getAttribute("user");
        String action = request.getParameter("action");

        if (action == null) {
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

            int numberRequest = cca.countPendingRequests();
            List<ClubCreationPermissions> requests = ccp.getAllRequests();

            request.setAttribute("requests", requests);
            request.setAttribute("numberRequest", numberRequest);

            request.getRequestDispatcher("view/ic/grantPermission.jsp").forward(request, response);
        } else if (action.equals("grantPermisstionForUser")) {

            String userId = (String) request.getParameter("id");
            String adminId = ((Users) session.getAttribute("user")).getUserID();

            Users userFind = ud.getUserByID(userId);

            ccp.insertClubPermission(userId, adminId);

            int numberOfPermissions = ccp.countActiveClubPermission(userId);
            request.setAttribute("activePermissionCount", numberOfPermissions);
            request.setAttribute("userFind", userFind);
            request.setAttribute("userSearchID", userId);

            request.getRequestDispatcher("view/ic/grantPermission.jsp").forward(request, response);
        } else if (action.equals("approvePermissionRequest") || action.equals("rejectPermissionRequest") || action.equals("deletePermissionRequest")) {
            //Sửa duyệt chỉ cần theo id của request -> phân ra action từ chối và đồng ý
            int id = Integer.parseInt(request.getParameter("id"));
            boolean success;
            String message = null, rejectedMessage = null, deletedMessage = null;
            String userId = request.getParameter("userID");
            if (action.equals("approvePermissionRequest")) {
                success = ccp.approveRequest(id, user.getUserID());
                message = success ? "Đã duyệt đơn thành công!" : "Duyệt đơn thất bại. Vui lòng thử lại.";
                 if (success) {
                    notificationDAO.sentToPerson1(user.getUserID(), userId, 
                        "Đơn xin quyền tạo câu lạc bộ được duyệt", 
                        "Đơn xin quyền tạo câu lạc bộ của bạn đã được duyệt. Bạn có thể tạo câu lạc bộ ngay bây giờ!", 
                        "HIGH");
                }

            } else if(action.equals("rejectPermissionRequest")) {
                success = ccp.rejectRequest(id, user.getUserID());
                rejectedMessage = success ? "Đã từ chối đơn thành công!" : "Từ chối đơn thất bại. Vui lòng thử lại.";
                 if (success) {
                    notificationDAO.sentToPerson1(user.getUserID(), userId, 
                        "Đơn xin quyền tạo câu lạc bộ bị từ chối", 
                        "Đơn xin quyền tạo câu lạc bộ của bạn đã bị từ chối. Vui lòng liên hệ IC để biết thêm chi tiết.", 
                        "HIGH");
                }

            }else{
                success = ccp.deleteRequest(id);
                deletedMessage = success ? "Đã xoá đơn thành công!" : "Xoá đơn thất bại. Vui lòng thử lại.";
            }

            int numberRequest = cca.countPendingRequests();
            List<ClubCreationPermissions> requests = ccp.getAllRequests();

            request.setAttribute("requests", requests);
            request.setAttribute("numberRequest", numberRequest);
            request.setAttribute(success ? "successMessage" : "errorMessage", message);
            request.setAttribute(success ? "rejectedMessage" : "errorMessage", rejectedMessage);
            request.setAttribute(success ? "deletedMessage" : "errorMessage", deletedMessage);
            request.setAttribute("pendingRequests", ccp.getPermissionsByStatus("PENDING"));
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
