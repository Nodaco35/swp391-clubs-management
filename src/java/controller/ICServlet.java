package controllers;

import dal.ClubDAO;
import dal.PeriodicReportDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import models.PeriodicReport;

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
        String action = request.getParameter("action");
        if (action == null) {
            ClubDAO clubDAO = new ClubDAO();
//            PeriodicReportDAO reportDAO = new PeriodicReportDAO();
//
//            // Thống kê cho IC Officer
//            int pendingReports = reportDAO.countPendingReports();
//            int approvedReports = reportDAO.countApprovedReports();
//            int overdueClubs = reportDAO.countOverdueClubs();
//            int totalActiveClubs = clubDAO.getTotalActiveClubs();
//
//            // Danh sách báo cáo
//            List<PeriodicReport> pendingReportsList = reportDAO.getPendingReports();
//            List<PeriodicReport> approvedReportsList = reportDAO.getApprovedReports(3);
//            List<PeriodicReport> overdueReportsList = reportDAO.getOverdueReports();
//            List<PeriodicReport> rejectedReportsList = reportDAO.getRejectedReports(2);
//
//            request.setAttribute("pendingReports", pendingReports);
//            request.setAttribute("approvedReports", approvedReports);
//            request.setAttribute("overdueClubs", overdueClubs);
//            request.setAttribute("totalActiveClubs", totalActiveClubs);
//
//            request.setAttribute("pendingReportsList", pendingReportsList);
//            request.setAttribute("approvedReportsList", approvedReportsList);
//            request.setAttribute("overdueReportsList", overdueReportsList);
//            request.setAttribute("rejectedReportsList", rejectedReportsList);
            
            request.getRequestDispatcher("view/ic/tmp.html").forward(request, response);
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
