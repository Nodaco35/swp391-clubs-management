/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;
import models.*;

public class ChairmanClubMeetingController extends HttpServlet {
    private static final Pattern GOOGLE_MEET_PATTERN = Pattern.compile("^https://meet\\.google\\.com/[a-z]{3}-[a-z]{4}-[a-z]{3}$");
    private static final Pattern ZOOM_PATTERN = Pattern.compile("^https://([a-z0-9-]+\\.)?zoom\\.us/j/[0-9]{9,11}(\\?pwd=[a-zA-Z0-9]+)?$");
    private static final Pattern GOOGLE_DRIVE_PATTERN = Pattern.compile("^https://drive\\.google\\.com/.*$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        ClubInfo club = (ClubInfo) session.getAttribute("club");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String search = request.getParameter("search") != null ? request.getParameter("search") : "";
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        int pageSize = 6;

        List<ClubMeeting> meetings;
        int totalRecords;
        int totalPages;

        String action = request.getParameter("action");
        if ("edit".equals(action)) {
            int meetingId;
            try {
                meetingId = Integer.parseInt(request.getParameter("meetingId"));
            } catch (NumberFormatException e) {
                request.setAttribute("error", "ID cuộc họp không hợp lệ!");
                request.getRequestDispatcher("view/student/chairman/clubmeeting.jsp").forward(request, response);
                return;
            }

            ClubMeeting meeting = ClubMeetingDAO.findByClubID(club.getClubID(), "", 1, Integer.MAX_VALUE)
                    .stream()
                    .filter(m -> m.getClubMeetingID() == meetingId)
                    .findFirst()
                    .orElse(null);

            if (meeting == null) {
                request.setAttribute("error", "Không tìm thấy cuộc họp!");
            } else {
                List<String> participants = ClubMeetingDAO.getClubDepartmentID(meetingId);
                meeting.setParticipantClubDepartmentIds(participants);
                request.setAttribute("editMeeting", meeting);
                long duration = (meeting.getEndTime().getTime() - meeting.getStartedTime().getTime()) / 60000;
                request.setAttribute("duration", duration);
            }
            meetings = ClubMeetingDAO.findByClubID(club.getClubID(), search, page, pageSize);
            
            totalRecords = ClubMeetingDAO.countByClubID(club.getClubID(), search);
            totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        } else if ("search".equals(action)) {
            if (search == null || search.trim().isEmpty()) {
                meetings = ClubMeetingDAO.findByClubID(club.getClubID(), search, page, pageSize);
                totalRecords = ClubMeetingDAO.countByClubID(club.getClubID(), search);
                totalPages = (int) Math.ceil((double) totalRecords / pageSize);
                request.setAttribute("meetings", meetings);
            } else {
                meetings = ClubMeetingDAO.findByClubID(club.getClubID(), search, page, pageSize);
                totalRecords = ClubMeetingDAO.countByClubID(club.getClubID(), search);
                totalPages = (int) Math.ceil((double) totalRecords / pageSize);

                if (meetings != null && !meetings.isEmpty()) {
                    request.setAttribute("meetings", meetings);
                    request.setAttribute("message", "Tìm kiếm thành công");
                } else {
                    request.setAttribute("message", "Không tìm thấy cuộc họp");
                }
            }
        } else if ("add".equals(action)) {
            request.setAttribute("showAddForm", true);
            meetings = ClubMeetingDAO.findByClubID(club.getClubID(), search, page, pageSize);
            totalRecords = ClubMeetingDAO.countByClubID(club.getClubID(), search);
            totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        } else {
            meetings = ClubMeetingDAO.findByClubID(club.getClubID(), "", page, pageSize);
            totalRecords = ClubMeetingDAO.countByClubID(club.getClubID(), search);
            totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        }
        List<ClubDepartment> departmentMembers = ClubDepartmentDAO.findByClubId(club.getClubID()); 
        request.setAttribute("departmentMembers", departmentMembers);
        request.setAttribute("meetings", meetings);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("search", search);
        request.setAttribute("now", new java.util.Date());
        
        request.getRequestDispatcher("/view/student/chairman/clubmeeting.jsp").forward(request, response);

    }

    public static void main(String[] args) {
        List<String> participants = ClubMeetingDAO.getClubDepartmentID(6);
        for (String participant : participants) {
            System.out.println(participant);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ChairmanClubMeetingController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ChairmanClubMeetingController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
