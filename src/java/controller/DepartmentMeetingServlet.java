package controller;

import dal.DepartmentMeetingDAO;
import dal.DepartmentDashboardDAO;
import dal.UserClubDAO;
import dal.NotificationDAO;
import models.DepartmentMeeting;
import models.Users;
import models.UserClub;
import java.io.IOException;
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

        // Lấy thông tin department
        models.DepartmentDashboard dashboard = dashboardDAO.getDepartmentInfo(user.getUserID());
        if (dashboard == null) {
            request.setAttribute("error", "Không tìm thấy thông tin ban");
            request.getRequestDispatcher("view/student/department-leader/meetings.jsp").forward(request, response);
            return;
        }

        int clubDepartmentId = dashboard.getClubDepartmentId();
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

        if ("add".equals(action)) {
            String urlMeeting = request.getParameter("urlMeeting");
            String startedTime = request.getParameter("startedTime");

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

            try {
                String formattedTime = startedTime.replace("T", " ") + ":00"; // Convert yyyy-MM-dd'T'HH:mm to yyyy-MM-dd HH:mm:ss
                meetingDAO.createMeeting(clubDepartmentId, formattedTime, urlMeeting);
                
                // Send notifications to department members
                List<UserClub> members = userClubDAO.findByCDID(clubDepartmentId);
                for (UserClub member : members) {
                    String title = "Thông báo cuộc họp mới";
                    String content = String.format("Cuộc họp mới tại %s được tổ chức vào %s. Liên kết: %s",
                            member.getDepartmentName(), formattedTime, urlMeeting);
                    NotificationDAO.sentToPerson(user.getUserID(), member.getUserID(), title, content);
                }
                
                request.setAttribute("message", "Tạo cuộc họp thành công và đã gửi thông báo đến các thành viên!");
            } catch (Exception e) {
                request.setAttribute("error", "Lỗi khi tạo cuộc họp: " + e.getMessage());
                request.setAttribute("showAddForm", true);
            }
        } else if ("update".equals(action)) {
            int meetingId;
            String urlMeeting = request.getParameter("urlMeeting");
            String startedTime = request.getParameter("startedTime");

            try {
                meetingId = Integer.parseInt(request.getParameter("meetingId"));
            } catch (NumberFormatException e) {
                request.setAttribute("error", "ID cuộc họp không hợp lệ!");
                doGet(request, response);
                return;
            }

            if (urlMeeting == null || urlMeeting.trim().isEmpty() || startedTime == null || startedTime.trim().isEmpty()) {
                request.setAttribute("error", "Liên kết cuộc họp hoặc thời gian không được để trống!");
                DepartmentMeeting meeting = new DepartmentMeeting();
                meeting.setDepartmentMeetingID(meetingId);
                meeting.setURLMeeting(urlMeeting);
                request.setAttribute("editMeeting", meeting);
                doGet(request, response);
                return;
            }

            if (!GOOGLE_MEET_PATTERN.matcher(urlMeeting).matches() && !ZOOM_PATTERN.matcher(urlMeeting).matches()) {
                request.setAttribute("error", "Liên kết cuộc họp không hợp lệ! Vui lòng nhập URL Google Meet (VD: https://meet.google.com/abc-defg-hij) hoặc Zoom (VD: https://zoom.us/j/1234567890).");
                DepartmentMeeting meeting = new DepartmentMeeting();
                meeting.setDepartmentMeetingID(meetingId);
                meeting.setURLMeeting(urlMeeting);
                request.setAttribute("editMeeting", meeting);
                doGet(request, response);
                return;
            }

            try {
                String formattedTime = startedTime.replace("T", " ") + ":00"; // Convert yyyy-MM-dd'T'HH:mm to yyyy-MM-dd HH:mm:ss
                DepartmentMeeting meeting = new DepartmentMeeting();
                meeting.setDepartmentMeetingID(meetingId);
                meeting.setClubDepartmentID(clubDepartmentId);
                meeting.setURLMeeting(urlMeeting);
                meeting.setStartedTime(java.sql.Timestamp.valueOf(formattedTime));

                if (meetingDAO.updateMeeting(meeting)) {
                    // Send notifications to department members
                    List<UserClub> members = userClubDAO.findByCDID(clubDepartmentId);
                    for (UserClub member : members) {
                        String title = "Cập nhật thông tin cuộc họp";
                        String content = String.format("Cuộc họp tại ban %s đã được cập nhật. Thời gian: %s, Liên kết: %s",
                                member.getDepartmentName(), formattedTime, urlMeeting);
                        NotificationDAO.sentToPerson(user.getUserID(), member.getUserID(), title, content);
                    }
                    
                    request.setAttribute("message", "Cập nhật cuộc họp thành công và đã gửi thông báo đến các thành viên!");
                } else {
                    request.setAttribute("error", "Cập nhật cuộc họp thất bại!");
                    request.setAttribute("editMeeting", meeting);
                }
            } catch (Exception e) {
                request.setAttribute("error", "Lỗi khi cập nhật cuộc họp: " + e.getMessage());
                DepartmentMeeting meeting = new DepartmentMeeting();
                meeting.setDepartmentMeetingID(meetingId);
                meeting.setURLMeeting(urlMeeting);
                request.setAttribute("editMeeting", meeting);
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