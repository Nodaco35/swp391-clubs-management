package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import dal.ApplicationFormDAO;
import dal.ClubDAO;
import dal.EventsDAO;
import dal.LocationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import models.*;

/**
 * @author LE VAN THUAN
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class EditEventServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "images/events";
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet EditEventServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EditEventServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        ClubDAO clubDAO = new ClubDAO();
        EventsDAO eventDAO = new EventsDAO();
        LocationDAO locationDAO = new LocationDAO();
        ApplicationFormDAO formDAO = new ApplicationFormDAO();

        if (user != null) {
            String userID = user.getUserID();
            ClubInfo club = clubDAO.getClubChairman(userID);
            int clubID = clubDAO.getClubIDByUserID(userID);
            request.setAttribute("club", club);

            String eventIDParam = request.getParameter("eventID");
            if (eventIDParam != null) {
                try {
                    int eventID = Integer.parseInt(eventIDParam);
                    Events event = eventDAO.getEventByID(eventID);
                    List<Agenda> agendas = eventDAO.getAgendasByEventID(eventID);
                    String locationType = request.getParameter("locationType");
                    if (locationType == null || locationType.isEmpty()) {
                        locationType = event.getSchedules() != null && !event.getSchedules().isEmpty() &&
                                       event.getSchedules().get(0).getLocation() != null ?
                                       event.getSchedules().get(0).getLocation().getTypeLocation() : "OnCampus";
                    }

                    List<Locations> locations = locationDAO.getLocationsByType(locationType);
                    List<ApplicationForm> applicationForms = formDAO.getFormsByClubAndType(clubID, "Event");

                    request.setAttribute("event", event);
                    request.setAttribute("agendas", agendas);
                    request.setAttribute("locations", locations);
                    request.setAttribute("locationType", locationType);
                    request.setAttribute("applicationForms", applicationForms);

                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "ID sự kiện không hợp lệ.");
                }
            }
            request.getRequestDispatcher("/view/student/chairman/edit-event.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Integer myClubID = (Integer) session.getAttribute("myClubID");

        if (myClubID == null || myClubID <= 0) {
            request.setAttribute("errorMessage", "Vui lòng đăng nhập với tư cách chủ nhiệm câu lạc bộ.");
            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
            return;
        }

        try {
            int eventID = Integer.parseInt(request.getParameter("eventID"));
            String eventName = request.getParameter("eventName");
            String maxParticipantsStr = request.getParameter("maxParticipants");
            String eventType = request.getParameter("eventType");
            String description = request.getParameter("eventDescription");
            String[] eventDates = request.getParameterValues("eventDate[]");
            String[] eventLocationIDs = request.getParameterValues("eventLocation[]");
            String[] eventStartTimes = request.getParameterValues("eventTime[]");
            String[] eventEndTimes = request.getParameterValues("eventEndTime[]");

            // Kiểm tra số lượng tối đa người tham gia
            int capacity = Integer.parseInt(maxParticipantsStr);
            if (capacity <= 0 || capacity > 10000) {
                session.setAttribute("errorMessage", "Số lượng tối đa phải lớn hơn 0 và nhỏ hơn 10000 người.");
                response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/edit-event?eventID=" + eventID);
                return;
            }

            boolean isPublic = "public".equalsIgnoreCase(eventType);

            // Kiểm tra lịch trình
            if (eventDates == null || eventDates.length == 0 ||
                    eventLocationIDs == null || eventLocationIDs.length == 0 ||
                    eventStartTimes == null || eventStartTimes.length == 0 ||
                    eventEndTimes == null || eventEndTimes.length == 0) {
                session.setAttribute("errorMessage", "Vui lòng cung cấp ít nhất một lịch trình cho sự kiện.");
                response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/edit-event?eventID=" + eventID);
                return;
            }

            List<EventSchedule> schedules = new ArrayList<>();
            EventsDAO eventDAO = new EventsDAO();
            LocationDAO locationDAO = new LocationDAO();
            for (int i = 0; i < eventDates.length; i++) {
                int locationId = Integer.parseInt(eventLocationIDs[i]);
                if (locationId <= 0) {
                    session.setAttribute("errorMessage", "Vui lòng chọn địa điểm hợp lệ cho lịch trình thứ " + (i + 1) + ".");
                    response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/edit-event?eventID=" + eventID);
                    return;
                }

                Timestamp startDateTime = Timestamp.valueOf(eventDates[i] + " " + eventStartTimes[i] + ":00");
                Timestamp endDateTime = Timestamp.valueOf(eventDates[i] + " " + eventEndTimes[i] + ":00");

                if (eventDAO.isLocationConflict(eventID, locationId, startDateTime, endDateTime)) {
                    session.setAttribute("errorMessage", "Địa điểm đã được sử dụng trong khoảng thời gian của lịch trình thứ " + (i + 1) + ".");
                    response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/edit-event?eventID=" + eventID);
                    return;
                }

                EventSchedule schedule = new EventSchedule();
                schedule.setEventDate(Date.valueOf(eventDates[i]));
                schedule.setLocationID(locationId);
                schedule.setStartTime(Time.valueOf(eventStartTimes[i] + ":00"));
                schedule.setEndTime(Time.valueOf(eventEndTimes[i] + ":00"));
                schedules.add(schedule);
            }

            String imageName = null;
            Part imagePart = request.getPart("eventImage");
            if (imagePart != null && imagePart.getSize() > 0) {
                String originalFileName = imagePart.getSubmittedFileName();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();

                boolean isValidExtension = false;
                for (String ext : ALLOWED_EXTENSIONS) {
                    if (ext.equals(fileExtension)) {
                        isValidExtension = true;
                        break;
                    }
                }

                if (!isValidExtension) {
                    session.setAttribute("errorMessage", "Chỉ chấp nhận file ảnh có định dạng: jpg, jpeg, png, gif");
                    response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/edit-event?eventID=" + eventID);
                    return;
                }

                String fileName = "event_" + eventID + "_" + System.currentTimeMillis() + fileExtension;
                imageName = UPLOAD_DIR + "/" + fileName;

                String buildUploadPath = getServletContext().getRealPath("/") + UPLOAD_DIR;
                Path buildUploadDir = Paths.get(buildUploadPath);
                if (!Files.exists(buildUploadDir)) {
                    Files.createDirectories(buildUploadDir);
                }
                Path buildFilePath = buildUploadDir.resolve(fileName);
                Files.copy(imagePart.getInputStream(), buildFilePath, StandardCopyOption.REPLACE_EXISTING);

                String contextPath = getServletContext().getRealPath("/");
                String projectRoot = Paths.get(contextPath).getParent().getParent().toString();
                String sourceUploadPath = Paths.get(projectRoot, "web", UPLOAD_DIR).toString();
                Path sourceUploadDir = Paths.get(sourceUploadPath);
                if (!Files.exists(sourceUploadDir)) {
                    Files.createDirectories(sourceUploadDir);
                }
                Path sourceFilePath = sourceUploadDir.resolve(fileName);
                Files.copy(buildFilePath, sourceFilePath, StandardCopyOption.REPLACE_EXISTING);

                Events currentEvent = eventDAO.getEventByID(eventID);
                if (currentEvent != null && currentEvent.getEventImg() != null && !currentEvent.getEventImg().isEmpty()) {
                    Path oldBuildImagePath = Paths.get(buildUploadPath, Paths.get(currentEvent.getEventImg()).getFileName().toString());
                    if (Files.exists(oldBuildImagePath)) {
                        Files.delete(oldBuildImagePath);
                    }
                    Path oldSourceImagePath = Paths.get(sourceUploadPath, Paths.get(currentEvent.getEventImg()).getFileName().toString());
                    if (Files.exists(oldSourceImagePath)) {
                        Files.delete(oldSourceImagePath);
                    }
                }
            }

            // Gọi phương thức updateEvent hoặc updateEventWithImage
            try {
                if (imageName != null) {
                    eventDAO.updateEventWithImage(eventID, eventName, description, capacity, isPublic, imageName, schedules);
                } else {
                    eventDAO.updateEvent(eventID, eventName, description, capacity, isPublic, schedules);
                }
                session.setAttribute("successMsg", "Chỉnh sửa sự kiện thành công!");
                response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/edit-event?eventID=" + eventID);
            } catch (RuntimeException e) {
                session.setAttribute("errorMessage", "Lỗi khi cập nhật sự kiện: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/edit-event?eventID=" + eventID);
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Lỗi khi cập nhật sự kiện: " + e.getMessage());
            request.getRequestDispatcher("/view/student/chairman/edit-event.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}