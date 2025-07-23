package controller;

import dal.ApprovalHistoryDAO;
import dal.ClubDAO;
import dal.PeriodicReportDAO;
import dal.ClubCreationPermissionDAO;
import dal.ClubPeriodicReportDAO;
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
import java.util.stream.Collectors;
import models.ClubApprovalHistory;
import models.ClubCreationPermissions;
import models.ClubPeriodicReport;
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
            ClubPeriodicReportDAO reportDAO = new ClubPeriodicReportDAO();

            // Lấy tất cả báo cáo của tất cả CLB, sắp xếp theo ngày nộp giảm dần
            List<ClubPeriodicReport> allReports = reportDAO.getAllReportsSortedByDate();
            List<Clubs> requests = clubDAO.getRequestClubs();

            List<Clubs> approvedRequests = clubDAO.getApproveRequestClubs();

            request.setAttribute("requests", requests);
            
            for (ClubPeriodicReport report : allReports) {
                int clubId = report.getClubID();
                String termId = report.getTerm();

                int memberCount = reportDAO.countMembersByClubAndTerm(clubId, termId);
                int eventCount = reportDAO.countEventsByClubAndTerm(clubId, termId);
                
                
                report.setMemberCount(memberCount);
            }
            List<ClubPeriodicReport> latest3 = allReports.stream().limit(3).toList();
            List<Clubs> latest3_clubs = requests.stream().limit(3).toList();
            
            request.setAttribute("requests", latest3_clubs);
            request.setAttribute("reportList",latest3);

            int totalActiveClubs = clubDAO.getTotalActiveClubs();

            request.setAttribute("totalActiveClubs", totalActiveClubs);


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
            } else if (action.equals("rejectPermissionRequest")) {
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
        } else if ("viewClubRequest".equals(action)) {
            ApprovalHistoryDAO approvalHistoryDAO = new ApprovalHistoryDAO();
            int infoClubId = Integer.parseInt(request.getParameter("id"));
            Clubs infoClub = clubDAO.getALLClubById(infoClubId);
            List<ClubApprovalHistory> approvalHistory = approvalHistoryDAO.getHistoryByClubId(infoClubId);

            request.setAttribute("approvalHistory", approvalHistory);
            request.setAttribute("club", infoClub);

//            PrintWriter out = response.getWriter();
//            out.print(approvalHistory.size());
            request.getRequestDispatcher("view/ic/viewClubInfo.jsp").forward(request, response);
        } else if ("periodicReport".equals(action)) {
            ClubPeriodicReportDAO reportDAO = new ClubPeriodicReportDAO();

            // Lấy tất cả báo cáo của tất cả CLB, sắp xếp theo ngày nộp giảm dần
            List<ClubPeriodicReport> allReports = reportDAO.getAllReportsSortedByDate();
            for (ClubPeriodicReport report : allReports) {
                int clubId = report.getClubID();
                String termId = report.getTerm();

                int memberCount = reportDAO.countMembersByClubAndTerm(clubId, termId);
                int eventCount = reportDAO.countEventsByClubAndTerm(clubId, termId);
                
                report.setEventCount(eventCount);
                report.setMemberCount(memberCount);
            }
            // Truyền sang JSP
            request.setAttribute("reportList", allReports);
            request.getRequestDispatcher("view/ic/report-overview.jsp").forward(request, response);

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
