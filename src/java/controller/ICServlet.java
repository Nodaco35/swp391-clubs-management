package controller;

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
