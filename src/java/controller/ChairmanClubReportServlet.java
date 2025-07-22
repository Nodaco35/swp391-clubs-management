/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.ClubDAO;
import dal.ClubPeriodicReportDAO;
import dal.PeriodicReportDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import models.ClubInfo;
import models.ClubPeriodicReport;
import models.PeriodicReportEvents;
import models.PeriodicReport_MemberAchievements;
import models.Users;

/**
 *
 * @author NC PC
 */
public class ChairmanClubReportServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ChairmanClubReportServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ChairmanClubReportServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        ClubDAO clubDAO = new ClubDAO();
        Users user = (Users) session.getAttribute("user");
        String action = request.getParameter("action");

        if (user == null) {
            response.sendRedirect("http://localhost:8080/clubs/login");
        } else {

            String userID = user.getUserID();
            if (action == null) {
                ClubInfo club = clubDAO.getClubChairman(userID);
                List<ClubPeriodicReport> lists = ClubPeriodicReportDAO.getReportsByClubID(club.getClubID());

                request.setAttribute("reports", lists);
                request.getRequestDispatcher("/view/student/chairman/reports.jsp").forward(request, response);
            } else if ("viewDetail".equals(action)) {
                PeriodicReportDAO reportDAO = new PeriodicReportDAO();
                int reportID = Integer.parseInt(request.getParameter("reportId"));

                List<PeriodicReport_MemberAchievements> members = reportDAO.getMemberAchievementsByReportID(reportID);
                List<PeriodicReportEvents> events = reportDAO.getEventReportsByReportID(reportID);
                String termName = reportDAO.getTermNameByReportID(reportID);

                request.setAttribute("term", termName);
                request.setAttribute("members", members);
                request.setAttribute("events", events);

                request.getRequestDispatcher("/view/student/chairman/reportDetail.jsp").forward(request, response);

//                response.setContentType("text/html;charset=UTF-8");
//                PrintWriter out = response.getWriter();
//                out.println("<h2>📌 Báo cáo thành viên</h2>");
//                for (PeriodicReport_MemberAchievements m : members) {
//                    out.printf("<div>%s - %s (%s) - Vai trò: %s - Điểm: %d</div>",
//                            m.getStudentCode(), m.getFullName(), m.getMemberID(),
//                            m.getRole(), m.getProgressPoint());
//                }
//
//                out.println("<hr/><h2>📌 Báo cáo sự kiện</h2>");
//                for (PeriodicReportEvents e : events) {
//                    out.printf("<div>%s | Ngày: %s | Kiểu: %s | SL: %d | Chứng minh: %s</div>",
//                            e.getEventName(), e.getEventDate(),
//                            e.getEventType(), e.getParticipantCount(), e.getProofLink());
//                }
            } else if ("createReports".equals(action)) {
                response.setContentType("text/html;charset=UTF-8");
                PrintWriter out = response.getWriter();
                
                out.print("View create report template");
            }

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
