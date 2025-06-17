package controller;

import dal.ClubCreationPermissionDAO;
import dal.ClubDAO;
import dal.PeriodicReportDAO;
import dal.UserDAO;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import models.PeriodicReport;
import models.Users;

/**
 *
 * @author NC PC
 */
public class ICServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ICServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ICServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserDAO ud = new UserDAO();
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
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
