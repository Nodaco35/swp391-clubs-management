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
import models.*;

public class ChairmanClubMeetingController extends HttpServlet {

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
//                List<String> participants = meetingDAO.getMeetingParticipants(meetingId);
//                meeting.setParticipantUserIds(participants);
                request.setAttribute("editMeeting", meeting);
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

        request.setAttribute("meetings", meetings);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("search", search);
        request.setAttribute("now", new java.util.Date());
        
        request.getRequestDispatcher("/view/student/chairman/clubmeeting.jsp").forward(request, response);

    }

    public static void main(String[] args) {
        ClubMeeting meeting = ClubMeetingDAO.findByClubID(1, "", 1, Integer.MAX_VALUE)
                .stream()
                .filter(m -> m.getClubMeetingID() == 1)
                .findFirst()
                .orElse(null);
        System.out.println(meeting);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        Users user = (Users) request.getSession().getAttribute("user");
        ClubInfo club = (ClubInfo) request.getSession().getAttribute("club");
        switch (action) {
            case "add":

                break;

        }
        doGet(request, response);
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
