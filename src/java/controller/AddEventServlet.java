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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
public class AddEventServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "images/events";
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AddEventServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AddEventServlet at " + request.getContextPath() + "</h1>");
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
        LocationDAO locationDAO = new LocationDAO();
        Integer myClubID = (Integer) session.getAttribute("myClubID");

        if (myClubID == null || myClubID <= 0) {
            request.setAttribute("errorMessage", "Vui lòng đăng nhập với tư cách chủ nhiệm câu lạc bộ.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (user != null) {
            String userID = user.getUserID();
            ClubInfo club = clubDAO.getClubChairman(userID);
            request.setAttribute("club", club);
            String currentPath = request.getServletPath();
            request.setAttribute("currentPath", currentPath);
            request.setAttribute("locations", locationDAO.getLocationsByType("OnCampus"));
            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
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
        Users user = (Users) session.getAttribute("user");
        ClubDAO clubDAO = new ClubDAO();

        if (myClubID == null || myClubID <= 0) {
            request.setAttribute("errorMessage", "Vui lòng đăng nhập với tư cách chủ nhiệm câu lạc bộ.");
            if (user != null) {
                String userID = user.getUserID();
                ClubInfo club = clubDAO.getClubChairman(userID);
                request.setAttribute("club", club);
            }
            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
            return;
        }

        String eventName = request.getParameter("eventName");
        String maxParticipantsStr = request.getParameter("maxParticipants");
        String eventType = request.getParameter("eventType");
        String eventDescription = request.getParameter("eventDescription");
        String locationType = request.getParameter("locationType");
        String[] eventDates = request.getParameterValues("eventDate[]");
        String[] eventLocationIDs = request.getParameterValues("eventLocation[]");
        String[] eventStartTimes = request.getParameterValues("eventTime[]");
        String[] eventEndTimes = request.getParameterValues("eventEndTime[]");

        String eventImgPath = null;
        Part imagePart = request.getPart("eventImg");

        if (imagePart != null && imagePart.getSize() > 0) {
            String fileName = imagePart.getSubmittedFileName();
            if (fileName != null && !fileName.isEmpty()) {
                String fileExtension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
                boolean isValidExtension = false;
                for (String ext : ALLOWED_EXTENSIONS) {
                    if (ext.equals(fileExtension)) {
                        isValidExtension = true;
                        break;
                    }
                }

                if (!isValidExtension) {
                    request.setAttribute("errorMessage", "Chỉ chấp nhận file ảnh có định dạng: jpg, jpeg, png, gif");
                    LocationDAO locationDAO = new LocationDAO();
                    request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
                    request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
                    return;
                }

                try {
                    String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
                    eventImgPath = UPLOAD_DIR + "/" + uniqueFileName;

                    String buildUploadPath = getServletContext().getRealPath("/") + UPLOAD_DIR;
                    Path buildUploadDir = Paths.get(buildUploadPath);
                    if (!Files.exists(buildUploadDir)) {
                        Files.createDirectories(buildUploadDir);
                    }
                    Path buildFilePath = buildUploadDir.resolve(uniqueFileName);
                    Files.copy(imagePart.getInputStream(), buildFilePath, StandardCopyOption.REPLACE_EXISTING);

                    String contextPath = getServletContext().getRealPath("/");
                    String projectRoot = Paths.get(contextPath).getParent().getParent().toString();
                    String sourceUploadPath = Paths.get(projectRoot, "web", UPLOAD_DIR).toString();
                    Path sourceUploadDir = Paths.get(sourceUploadPath);
                    if (!Files.exists(sourceUploadDir)) {
                        Files.createDirectories(sourceUploadDir);
                    }
                    Path sourceFilePath = sourceUploadDir.resolve(uniqueFileName);
                    Files.copy(buildFilePath, sourceFilePath, StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException e) {
                    request.setAttribute("errorMessage", "Lỗi khi upload ảnh: " + e.getMessage());
                    LocationDAO locationDAO = new LocationDAO();
                    request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
                    request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
                    return;
                }
            }
        }

        if (eventName == null || eventName.trim().isEmpty() ||
                maxParticipantsStr == null || maxParticipantsStr.trim().isEmpty() ||
                eventDates == null || eventDates.length == 0 ||
                eventLocationIDs == null || eventLocationIDs.length == 0 ||
                eventStartTimes == null || eventStartTimes.length == 0 ||
                eventEndTimes == null || eventEndTimes.length == 0) {
            LocationDAO locationDAO = new LocationDAO();
            request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
            request.setAttribute("locationType", locationType);
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin sự kiện và ít nhất một lịch trình.");
            if (user != null) {
                String userID = user.getUserID();
                ClubInfo club = clubDAO.getClubChairman(userID);
                request.setAttribute("club", club);
            }
            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
            return;
        }

        LocationDAO locationDAO = new LocationDAO();
        if (locationType != null && !locationType.isEmpty()) {
            request.setAttribute("locations", locationDAO.getLocationsByType(locationType));
            request.setAttribute("locationType", locationType);
        } else {
            request.setAttribute("locations", locationDAO.getLocationsByType("OnCampus"));
        }

        try {
            int maxParticipants = Integer.parseInt(maxParticipantsStr);
            if (maxParticipants <= 0 || maxParticipants > 10000) {
                request.setAttribute("errorMessage", "Số lượng tối đa phải lớn hơn 0 và nhỏ hơn 10000 người.");
                request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
                request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
                return;
            }

            List<EventSchedule> schedules = new ArrayList<>();
            EventsDAO dao = new EventsDAO();
            for (int i = 0; i < eventDates.length; i++) {
                int locationId = Integer.parseInt(eventLocationIDs[i]);
                if (locationId <= 0) {
                    request.setAttribute("errorMessage", "Vui lòng chọn địa điểm hợp lệ cho lịch trình thứ " + (i + 1) + ".");
                    request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
                    request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
                    return;
                }

                Timestamp startDateTime = Timestamp.valueOf(eventDates[i] + " " + eventStartTimes[i] + ":00");
                Timestamp endDateTime = Timestamp.valueOf(eventDates[i] + " " + eventEndTimes[i] + ":00");

                if (dao.isLocationConflictAdd(locationId, startDateTime, endDateTime)) {
                    request.setAttribute("errorMessage", "Địa điểm đã được sử dụng trong khoảng thời gian của lịch trình thứ " + (i + 1) + ".");
                    request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
                    request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
                    return;
                }

                EventSchedule schedule = new EventSchedule();
                schedule.setEventDate(Date.valueOf(eventDates[i]));
                schedule.setLocationID(locationId);
                schedule.setStartTime(Time.valueOf(eventStartTimes[i] + ":00"));
                schedule.setEndTime(Time.valueOf(eventEndTimes[i] + ":00"));
                schedules.add(schedule);
            }

            boolean isPublic = "public".equalsIgnoreCase(eventType);
            int newEventID = dao.addEvent(eventName, eventDescription, myClubID, isPublic, maxParticipants, eventImgPath, schedules);

            // Lấy thông tin sự kiện mới để hiển thị lịch trình và agenda
            Events newEvent = dao.getEventByID(newEventID);
            request.setAttribute("event", newEvent);

            // Lưu thông tin lịch trình đã nhập để hiển thị lại trong form
            request.setAttribute("eventDates", eventDates);
            request.setAttribute("eventLocationIDs", eventLocationIDs);
            request.setAttribute("eventStartTimes", eventStartTimes);
            request.setAttribute("eventEndTimes", eventEndTimes);

            session.setAttribute("successMsg", "Thêm sự kiện thành công! Vui lòng thêm Agenda cho sự kiện ở bên dưới.");
            session.setAttribute("newEventID", newEventID);
            request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Dữ liệu không hợp lệ, vui lòng kiểm tra số lượng tối đa hoặc địa điểm.");
            request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Định dạng ngày giờ không hợp lệ.");
            request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi thêm sự kiện: " + e.getMessage());
            request.setAttribute("locations", locationDAO.getLocationsByType(locationType != null ? locationType : "OnCampus"));
            request.getRequestDispatcher("/view/student/chairman/add-event.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}