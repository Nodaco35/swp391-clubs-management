package controller;

import dal.ApprovalHistoryDAO;
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
import java.io.PrintWriter;
import java.util.List;
import models.ClubApprovalHistory;
import models.ClubCreationPermissions;
import models.Clubs;
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
            List<Clubs> requests = clubDAO.getRequestClubs();
            
            List<Clubs> approvedRequests = clubDAO.getApproveRequestClubs();

            request.setAttribute("requests", requests);
            request.setAttribute("approvedRequests", approvedRequests);

            request.getRequestDispatcher("view/ic/grantPermission.jsp").forward(request, response);
        } else if (action.equals("approvePermissionRequest") || action.equals("rejectPermissionRequest") || action.equals("deletePermissionRequest")) {
            int requestClubId = Integer.parseInt(request.getParameter("id"));
            boolean success;
            String message = null, rejectedMessage = null, deletedMessage = null;
            String userId = request.getParameter("userID");
            ApprovalHistoryDAO approvalHistoryDAO = new ApprovalHistoryDAO();
            if (action.equals("approvePermissionRequest")) {
                success = approvalHistoryDAO.approveRequest(requestClubId);

                message = success ? "Đã duyệt đơn thành công!" : "Duyệt đơn thất bại. Vui lòng thử lại.";

                if (success) {
                    // Gửi thông báo cho người nộp đơn
                    notificationDAO.sentToPerson1(
                            user.getUserID(), // IC gửi
                            userId, // Người nhận
                            "Đơn xin quyền tạo câu lạc bộ được duyệt",
                            "Đơn xin quyền tạo câu lạc bộ của bạn đã được duyệt. Câu lạc bộ tạo thành công",
                            "HIGH"
                    );
                    approvalHistoryDAO.insertApprovalRecord(requestClubId, "Approved", "Đã duyệt tạo Câu lạc bộ", "Create");
                }
                request.setAttribute(success ? "successMessage" : "errorMessage", message);
            } 
            else if (action.equals("rejectPermissionRequest")) {
                String reason = request.getParameter("reason");        // Lý do từ chối
                success = approvalHistoryDAO.rejectRequest(requestClubId, reason);

                rejectedMessage = success ? "Đã từ chối đơn thành công!" : "Từ chối đơn thất bại. Vui lòng thử lại.";

                if (success) {
                    // Gửi thông báo kèm lý do từ chối
                    notificationDAO.sentToPerson1(
                            user.getUserID(),
                            userId,
                            "Đơn xin quyền tạo câu lạc bộ bị từ chối",
                            "Đơn xin quyền tạo câu lạc bộ của bạn đã bị từ chối. Lý do: " + reason,
                            "HIGH"
                    );
                    // Ghi lịch sử từ chối
                    approvalHistoryDAO.insertApprovalRecord(requestClubId, "Rejected", reason, "Create");
                }

                request.setAttribute(success ? "rejectedMessage" : "errorMessage", rejectedMessage);
            } else {
                success = approvalHistoryDAO.deleteClubRequest(requestClubId);

                deletedMessage = success ? "Đã xoá đơn thành công!" : "Xoá đơn thất bại. Vui lòng thử lại.";

                request.setAttribute(success ? "deletedMessage" : "errorMessage", deletedMessage);
            }

            List<Clubs> requests = clubDAO.getRequestClubs();
            List<Clubs> approvedRequests = clubDAO.getApproveRequestClubs();

            request.setAttribute("requests", requests);
            request.setAttribute("approvedRequests", approvedRequests);

            request.setAttribute(success ? "successMessage" : "errorMessage", message);
            request.setAttribute(success ? "rejectedMessage" : "errorMessage", rejectedMessage);
            request.setAttribute(success ? "deletedMessage" : "errorMessage", deletedMessage);

            request.getRequestDispatcher("view/ic/grantPermission.jsp").forward(request, response);
        } 
        
        else if("viewClubRequest".equals(action)){
            ApprovalHistoryDAO approvalHistoryDAO = new ApprovalHistoryDAO();
            int infoClubId = Integer.parseInt(request.getParameter("id"));
            Clubs infoClub = clubDAO.getALLClubById(infoClubId);
            List<ClubApprovalHistory> approvalHistory = approvalHistoryDAO.getHistoryByClubId(infoClubId);
            
            request.setAttribute("approvalHistory", approvalHistory);
            request.setAttribute("club", infoClub);
            
//            PrintWriter out = response.getWriter();
//            out.print(approvalHistory.size());
            request.getRequestDispatcher("view/ic/viewClubInfo.jsp").forward(request, response);
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
