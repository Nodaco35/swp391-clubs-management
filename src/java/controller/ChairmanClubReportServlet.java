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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import models.ActivedMemberClubs;
import models.ClubEvent;
import models.ClubInfo;
import models.ClubPeriodicReport;
import models.EventScheduleDetail;
import models.PeriodicReportEvents;
import models.PeriodicReport_MemberAchievements;
import models.Semesters;
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

        PeriodicReportDAO pd = new PeriodicReportDAO();
        if (user == null) {
            response.sendRedirect("http://localhost:8080/clubs/login");
        } else {

            String userID = user.getUserID();
            if (action == null) {
                PeriodicReportDAO pr = new PeriodicReportDAO();
                ClubInfo club = clubDAO.getClubChairman(userID);
                List<ClubPeriodicReport> lists = ClubPeriodicReportDAO.getReportsByClubID(club.getClubID());
                String termNow = pd.getActiveTermID();

                boolean createReportNow = false;
                for (ClubPeriodicReport c : lists) {
                    if (c.getTerm().equals(termNow)) {
                        createReportNow = true;
                    }
                }
                int reportNowID = pr.getReportIdByClubAndTerm(club.getClubID(), termNow);

                request.setAttribute("createReportNow", createReportNow);
                request.setAttribute("reports", lists);
                request.setAttribute("reportNowID", reportNowID);
                request.setAttribute("termNow", termNow);

                request.getRequestDispatcher("/view/student/chairman/reports.jsp").forward(request, response);
            } else if ("viewDetail".equals(action)) {
                PeriodicReportDAO reportDAO = new PeriodicReportDAO();
                int reportID = Integer.parseInt(request.getParameter("reportId"));

                String termID = reportDAO.getTermIDByReportID(reportID);
                PeriodicReportDAO pr = new PeriodicReportDAO();
                int clubID = pr.getClubIdByChairmanId(userID);

                // Lấy kỳ đang ACTIVE
                Semesters currentTerm = pr.getCurrentTerm();
                if (currentTerm == null) {
                    PrintWriter out = response.getWriter();
                    out.print("Semester Error");
                    return;
                }

                // Lấy danh sách thành viên hoạt động trong kỳ và CLB này
                List<ActivedMemberClubs> members = pr.getActiveMembersForReport(termID, clubID);

                List<ClubEvent> events = pr.getPublicEventsWithSchedules(termID, clubID);

                request.setAttribute("publicEvents", events);

                // Gửi dữ liệu sang JSP
                request.setAttribute("semesterID", termID);
                request.setAttribute("members", members);
                PrintWriter out = response.getWriter();

                for (ClubEvent event : events) {
                    out.print(event.getEventName() + " - ");
                    for (EventScheduleDetail s : event.getScheduleList()) {
                        out.print(s.getLocationName() + " + " + s.getEventDate());
                    }
                }

                request.getRequestDispatcher("/view/student/chairman/reportDetail.jsp").forward(request, response);

            } else if ("createReports".equals(action)) {
                PeriodicReportDAO pr = new PeriodicReportDAO();

                int clubID = pr.getClubIdByChairmanId(userID);

                // Lấy kỳ đang ACTIVE
                Semesters currentTerm = pr.getCurrentTerm();
                if (currentTerm == null) {
                    PrintWriter out = response.getWriter();
                    out.print("Semester Error");
                    return;
                }

                // Lấy danh sách thành viên hoạt động trong kỳ và CLB này
                List<ActivedMemberClubs> members = pr.getActiveMembersForReport(currentTerm.getTermID(), clubID);

                List<ClubEvent> events = pr.getPublicEventsWithSchedules(currentTerm.getTermID(), clubID);

                request.setAttribute("publicEvents", events);

                // Gửi dữ liệu sang JSP
                request.setAttribute("semesterID", currentTerm.getTermID());
                request.setAttribute("members", members);

                request.getRequestDispatcher("/view/student/chairman/createReport.jsp").forward(request, response);

            } else if ("submitReport".equals(action)) {
                PeriodicReportDAO pr = new PeriodicReportDAO();

                int clubID = pr.getClubIdByChairmanId(userID);
                Semesters currentTerm = pr.getCurrentTerm();

//                PrintWriter out = response.getWriter();
//                out.print(userID+", "+currentTerm.getTermID());
                String url = "reports?statusCreate=success";
                pr.insertClubReport(clubID, currentTerm.getTermID());

                // Redirect lại trang báo cáo hoặc gửi thông báo
                response.sendRedirect(url);
            } else if ("resubmitReport".equals(action)) {
                PeriodicReportDAO pr = new PeriodicReportDAO();

                int clubID = pr.getClubIdByChairmanId(userID);
                Semesters currentTerm = pr.getCurrentTerm();

//                PrintWriter out = response.getWriter();
//                out.print(userID+", "+currentTerm.getTermID());
                String url = "reports?statusCreate=success";
                pr.updateReportLastUpdatedByClubAndTerm(clubID, currentTerm.getTermID());

                // Redirect lại trang báo cáo hoặc gửi thông báo
                response.sendRedirect(url);
            } 
            else if ("fixDetailReport".equals(action)) {
                PeriodicReportDAO pr = new PeriodicReportDAO();

                int clubID = pr.getClubIdByChairmanId(userID);

                // Lấy kỳ đang ACTIVE
                Semesters currentTerm = pr.getCurrentTerm();
                if (currentTerm == null) {
                    PrintWriter out = response.getWriter();
                    out.print("Semester Error");
                    return;
                }

                // Lấy danh sách thành viên hoạt động trong kỳ và CLB này
                List<ActivedMemberClubs> members = pr.getActiveMembersForReport(currentTerm.getTermID(), clubID);

                List<ClubEvent> events = pr.getPublicEventsWithSchedules(currentTerm.getTermID(), clubID);

                request.setAttribute("publicEvents", events);

                // Gửi dữ liệu sang JSP
                request.setAttribute("semesterID", currentTerm.getTermID());
                request.setAttribute("members", members);

                request.getRequestDispatcher("/view/student/chairman/updateReport.jsp").forward(request, response);
            } else if ("editPointFromReport".equals(action)) {
                String userID_Update = request.getParameter("userID");
                String termID = request.getParameter("termID");
                int points = Integer.parseInt(request.getParameter("points"));

                PeriodicReportDAO dao = new PeriodicReportDAO(); // hoặc DAO nào chứa hàm updateProgressPointFromReport
                boolean success = dao.updateProgressPointFromReport(userID_Update, termID, points);

                if (success) {
                    request.setAttribute("mes", "Cập nhật điểm thành công");
                } else {
                    request.setAttribute("err", "Cập nhật thất bại");
                }

                // Sau khi cập nhật, redirect lại trang report cùng kỳ đó
                response.sendRedirect("reports?action=createReports&" + (success ? "success" : "fail"));
                return;
            }else if ("updatePointFromReport".equals(action)) {
                String userID_Update = request.getParameter("userID");
                String termID = request.getParameter("termID");
                int points = Integer.parseInt(request.getParameter("points"));

                PeriodicReportDAO dao = new PeriodicReportDAO(); // hoặc DAO nào chứa hàm updateProgressPointFromReport
                boolean success = dao.updateProgressPointFromReport(userID_Update, termID, points);

                if (success) {
                    request.setAttribute("mes", "Cập nhật điểm thành công");
                } else {
                    request.setAttribute("err", "Cập nhật thất bại");
                }

                // Sau khi cập nhật, redirect lại trang report cùng kỳ đó
                response.sendRedirect("reports?action=fixDetailReport&" + (success ? "success" : "fail"));
                return;
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
