package controller;

import dal.DepartmentMeetingDAO;
import dal.DepartmentDashboardDAO;
import dal.UserClubDAO;
import dal.NotificationDAO;
import models.DepartmentMeeting;
import models.Users;
import models.UserClub;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class DepartmentMeetingServlet extends HttpServlet {
    
    private DepartmentMeetingDAO meetingDAO;
    private DepartmentDashboardDAO dashboardDAO;
    private UserClubDAO userClubDAO;
    private static final Pattern GOOGLE_MEET_PATTERN = Pattern.compile("^https://meet\\.google\\.com/[a-z]{3}-[a-z]{4}-[a-z]{3}$");
    private static final Pattern ZOOM_PATTERN = Pattern.compile("^https://([a-z0-9-]+\\.)?zoom\\.us/j/[0-9]{9,11}(\\?pwd=[a-zA-Z0-9]+)?$");
    private static final Pattern GOOGLE_DRIVE_PATTERN = Pattern.compile("^https://drive\\.google\\.com/.*$");
    
    @Override
    public void init() throws ServletException {
        meetingDAO = new DepartmentMeetingDAO();
        dashboardDAO = new DepartmentDashboardDAO();
        userClubDAO = new UserClubDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        models.DepartmentDashboard dashboard = dashboardDAO.getDepartmentInfo(user.getUserID());
        if (dashboard == null) {
            request.setAttribute("error", "Không tìm thấy thông tin ban");
            request.getRequestDispatcher("view/student/department-leader/meetings.jsp").forward(request, response);
            return;
        }
        //get sesion
        int clubDepartmentId = (int)session.getAttribute("clubDepartmentId");
        session.setAttribute("clubDepartmentID", clubDepartmentId);
        session.setAttribute("departmentName", dashboard.getDepartmentName());

        String search = request.getParameter("search") != null ? request.getParameter("search") : "";
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        int pageSize = 6;

        List<DepartmentMeeting> meetings;
        int totalRecords;
        int totalPages;

        String action = request.getParameter("action");
        if ("edit".equals(action)) {
            int meetingId;
            try {
                meetingId = Integer.parseInt(request.getParameter("meetingId"));
            } catch (NumberFormatException e) {
                request.setAttribute("error", "ID cuộc họp không hợp lệ!");
                request.getRequestDispatcher("view/student/department-leader/meetings.jsp").forward(request, response);
                return;
            }

            DepartmentMeeting meeting = meetingDAO.getDepartmentMeetings(clubDepartmentId, 1, Integer.MAX_VALUE)
                    .stream()
                    .filter(m -> m.getDepartmentMeetingID() == meetingId)
                    .findFirst()
                    .orElse(null);

            if (meeting == null) {
                request.setAttribute("error", "Không tìm thấy cuộc họp!");
            } else {
                List<String> participants = meetingDAO.getMeetingParticipants(meetingId);
                meeting.setParticipantUserIds(participants);
                request.setAttribute("editMeeting", meeting);
            }
            meetings = meetingDAO.getDepartmentMeetings(clubDepartmentId, page, pageSize);
            totalRecords = meetingDAO.getTotalMeetingsCount(clubDepartmentId);
            totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        } else if ("search".equals(action)) {
            if (search == null || search.trim().isEmpty()) {
                meetings = meetingDAO.getDepartmentMeetings(clubDepartmentId, page, pageSize);
                totalRecords = meetingDAO.getTotalMeetingsCount(clubDepartmentId);
                totalPages = (int) Math.ceil((double) totalRecords / pageSize);
                request.setAttribute("meetings", meetings);
            } else {
                meetings = meetingDAO.searchMeetings(clubDepartmentId, search, "", page, pageSize);
                totalRecords = meetingDAO.getTotalMeetingsCountWithFilter(clubDepartmentId, search, "");
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
            meetings = meetingDAO.getDepartmentMeetings(clubDepartmentId, page, pageSize);
            totalRecords = meetingDAO.getTotalMeetingsCount(clubDepartmentId);
            totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        } else {
            meetings = meetingDAO.getDepartmentMeetings(clubDepartmentId, page, pageSize);
            totalRecords = meetingDAO.getTotalMeetingsCount(clubDepartmentId);
            totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        }

        List<Users> departmentMembers = meetingDAO.getDepartmentMembers(clubDepartmentId);
        request.setAttribute("departmentMembers", departmentMembers);
        request.setAttribute("meetings", meetings);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("search", search);
        request.setAttribute("clubDepartmentId", clubDepartmentId);
        request.setAttribute("now", new java.util.Date());

        request.getRequestDispatcher("view/student/department-leader/meetings.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int clubDepartmentId;
        try {
            clubDepartmentId = Integer.parseInt(request.getParameter("clubDepartmentId"));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID ban không hợp lệ!");
            doGet(request, response);
            return;
        }

        String action = request.getParameter("action");

        if ("add".equals(action) || "update".equals(action)) {
            String title = request.getParameter("title");
            String urlMeeting = request.getParameter("urlMeeting");
            String documentLink = request.getParameter("documentLink");
            String startedTime = request.getParameter("startedTime");
            String[] participantIds = request.getParameterValues("participants");
            boolean selectAll = "true".equals(request.getParameter("selectAll"));

            if (title == null || title.trim().isEmpty()) {
                request.setAttribute("error", "Tiêu đề cuộc họp không được để trống!");
                request.setAttribute("showAddForm", true);
                doGet(request, response);
                return;
            }

            if (urlMeeting == null || urlMeeting.trim().isEmpty() || startedTime == null || startedTime.trim().isEmpty()) {
                request.setAttribute("error", "Liên kết cuộc họp hoặc thời gian không được để trống!");
                request.setAttribute("showAddForm", true);
                doGet(request, response);
                return;
            }

            if (!GOOGLE_MEET_PATTERN.matcher(urlMeeting).matches() && !ZOOM_PATTERN.matcher(urlMeeting).matches()) {
                request.setAttribute("error", "Liên kết cuộc họp không hợp lệ! Vui lòng nhập URL Google Meet (VD: https://meet.google.com/abc-defg-hij) hoặc Zoom (VD: https://zoom.us/j/1234567890).");
                request.setAttribute("showAddForm", true);
                doGet(request, response);
                return;
            }

            if (documentLink != null && !documentLink.trim().isEmpty() && !GOOGLE_DRIVE_PATTERN.matcher(documentLink).matches()) {
                request.setAttribute("error", "Liên kết tài liệu không hợp lệ! Vui lòng nhập URL Google Drive hợp lệ.");
                request.setAttribute("showAddForm", true);
                doGet(request, response);
                return;
            }

            List<String> participants = new ArrayList<>();
            if (selectAll) {
                List<Users> members = meetingDAO.getDepartmentMembers(clubDepartmentId);
                for (Users member : members) {
                    participants.add(member.getUserID());
                }
            } else if (participantIds != null) {
                for (String id : participantIds) {
                    participants.add(id);
                }
            }

            try {
                String formattedTime = startedTime.replace("T", " ") + ":00";
                if ("add".equals(action)) {
                    meetingDAO.createMeeting(clubDepartmentId, title, urlMeeting, documentLink, formattedTime, participants);
                    
                    List<UserClub> members = userClubDAO.findByCDID(clubDepartmentId);
                    for (UserClub member : members) {
                        if (participants.contains(member.getUserID())) {
                            String notifTitle = "Thông báo cuộc họp mới: " + title;
                            String content = String.format("Cuộc họp '%s' tại %s được tổ chức vào %s. Liên kết: %s%s",
                                    title, member.getDepartmentName(), formattedTime, urlMeeting,
                                    documentLink != null && !documentLink.isEmpty() ? "\nTài liệu: " + documentLink : "");
                            NotificationDAO.sentToPerson(user.getUserID(), member.getUserID(), notifTitle, content);
                        }
                    }
                    request.setAttribute("message", "Tạo cuộc họp thành công và đã gửi thông báo đến các thành viên!");
                } else {
                    int meetingId;
                    try {
                        meetingId = Integer.parseInt(request.getParameter("meetingId"));
                    } catch (NumberFormatException e) {
                        request.setAttribute("error", "ID cuộc họp không hợp lệ!");
                        doGet(request, response);
                        return;
                    }

                    DepartmentMeeting meeting = new DepartmentMeeting();
                    meeting.setDepartmentMeetingID(meetingId);
                    meeting.setClubDepartmentID(clubDepartmentId);
                    meeting.setTitle(title);
                    meeting.setURLMeeting(urlMeeting);
                    meeting.setDocumentLink(documentLink);
                    meeting.setStartedTime(java.sql.Timestamp.valueOf(formattedTime));

                    if (meetingDAO.updateMeeting(meeting, participants)) {
                        List<UserClub> members = userClubDAO.findByCDID(clubDepartmentId);
                        for (UserClub member : members) {
                            if (participants.contains(member.getUserID())) {
                                String notifTitle = "Cập nhật cuộc họp: " + title;
                                String content = String.format("Cuộc họp '%s' tại ban %s đã được cập nhật. Thời gian: %s, Liên kết: %s%s",
                                        title, member.getDepartmentName(), formattedTime, urlMeeting,
                                        documentLink != null && !documentLink.isEmpty() ? "\nTài liệu: " + documentLink : "");
                                NotificationDAO.sentToPerson(user.getUserID(), member.getUserID(), notifTitle, content);
                            }
                        }
                        request.setAttribute("message", "Cập nhật cuộc họp thành công và đã gửi thông báo đến các thành viên!");
                    } else {
                        request.setAttribute("error", "Cập nhật cuộc họp thất bại!");
                        request.setAttribute("editMeeting", meeting);
                    }
                }
            } catch (Exception e) {
                request.setAttribute("error", "Lỗi khi " + ("add".equals(action) ? "tạo" : "cập nhật") + " cuộc họp: " + e.getMessage());
                request.setAttribute("showAddForm", true);
            }
        } else if ("delete".equals(action)) {
            int meetingId;
            try {
                meetingId = Integer.parseInt(request.getParameter("meetingId"));
            } catch (NumberFormatException e) {
                request.setAttribute("error", "ID cuộc họp không hợp lệ!");
                doGet(request, response);
                return;
            }

            try {
                if (meetingDAO.deleteMeeting(meetingId, clubDepartmentId)) {
                    request.setAttribute("message", "Xóa cuộc họp thành công!");
                } else {
                    request.setAttribute("error", "Xóa cuộc họp thất bại!");
                }
            } catch (Exception e) {
                request.setAttribute("error", "Lỗi khi xóa cuộc họp: " + e.getMessage());
            }
        }

        doGet(request, response);
    }
}