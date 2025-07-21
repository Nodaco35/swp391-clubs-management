/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
 *
 * @author LE VAN THUAN
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class EditEventServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
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
            out.println("<title>Servlet EditEventServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EditEventServlet hiiiii at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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
                        if (event.getLocation() != null && event.getLocation().getTypeLocation() != null) {
                            locationType = event.getLocation().getTypeLocation();
                        } else {
                            locationType = "OnCampus";
                        }
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


    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private static final String UPLOAD_DIR = "images/events";
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};
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
            String eventDateStr = request.getParameter("eventDate");
            String startTimeStr = request.getParameter("eventTime");
            String endTimeStr = request.getParameter("eventEndTime");
            int locationID = Integer.parseInt(request.getParameter("eventLocation"));
            int capacity = Integer.parseInt(request.getParameter("maxParticipants"));

            if (capacity <= 0 || capacity > 10000) {
                session.setAttribute("errorMessage", "Số lượng tối đa phải lớn hơn 0 hoặc nhỏ hơn 10000 người");
                response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/edit-event?eventID=" + eventID);
                return;
            }

            String eventType = request.getParameter("eventType");
            String description = request.getParameter("eventDescription");
            boolean isPublic = "public".equalsIgnoreCase(eventType);

            LocalDate date = LocalDate.parse(eventDateStr);
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);

            Timestamp eventStart = Timestamp.valueOf(LocalDateTime.of(date, startTime));
            Timestamp eventEnd = Timestamp.valueOf(LocalDateTime.of(date, endTime));

            // Xử lý upload ảnh
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

                // 1. Đường dẫn thư mục build (để hiển thị ngay lập tức)
                String buildUploadPath = getServletContext().getRealPath("/") + UPLOAD_DIR;
                System.err.println("Build Upload Path: " + buildUploadPath);
                Path buildUploadDir = Paths.get(buildUploadPath);
                if (!Files.exists(buildUploadDir)) {
                    Files.createDirectories(buildUploadDir);
                }
                Path buildFilePath = buildUploadDir.resolve(fileName);

                // Lưu file vào build
                Files.copy(imagePart.getInputStream(), buildFilePath, StandardCopyOption.REPLACE_EXISTING);

                // 2. Đường dẫn thư mục source (để không bị mất khi redeploy)
                String contextPath = getServletContext().getRealPath("/");
                String projectRoot = Paths.get(contextPath).getParent().getParent().toString();
                String sourceUploadPath = Paths.get(projectRoot, "web", UPLOAD_DIR).toString();
                System.err.println("Source Upload Path: " + sourceUploadPath);

                Path sourceUploadDir = Paths.get(sourceUploadPath);
                if (!Files.exists(sourceUploadDir)) {
                    System.err.println("Thư mục source không tồn tại, đang tạo: " + sourceUploadDir);
                    Files.createDirectories(sourceUploadDir);
                }
                Path sourceFilePath = sourceUploadDir.resolve(fileName);

                // Sao chép file sang source
                Files.copy(buildFilePath, sourceFilePath, StandardCopyOption.REPLACE_EXISTING);
                System.err.println("File copied to: " + sourceFilePath);

                // Xóa ảnh cũ nếu có
                EventsDAO eventDAO = new EventsDAO();
                Events currentEvent = eventDAO.getEventByID(eventID);
                if (currentEvent != null && currentEvent.getEventImg() != null && !currentEvent.getEventImg().isEmpty()) {
                    // Xóa ảnh cũ trong build
                    Path oldBuildImagePath = Paths.get(buildUploadPath, Paths.get(currentEvent.getEventImg()).getFileName().toString());
                    if (Files.exists(oldBuildImagePath)) {
                        Files.delete(oldBuildImagePath);
                        System.err.println("Deleted old image in build: " + oldBuildImagePath);
                    }
                    // Xóa ảnh cũ trong source
                    Path oldSourceImagePath = Paths.get(sourceUploadPath, Paths.get(currentEvent.getEventImg()).getFileName().toString());
                    if (Files.exists(oldSourceImagePath)) {
                        Files.delete(oldSourceImagePath);
                        System.err.println("Deleted old image in source: " + oldSourceImagePath);
                    }
                }
            }

            EventsDAO eventDAO = new EventsDAO();
            if (imageName != null) {
                eventDAO.updateEventWithImage(eventID, eventName, description, eventStart, eventEnd, locationID, capacity, isPublic, imageName);
            } else {
                eventDAO.updateEvent(eventID, eventName, description, eventStart, eventEnd, locationID, capacity, isPublic);
            }
            session.setAttribute("successMsg", "Chỉnh sửa sự kiện thành công!");
            response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/edit-event?eventID=" + eventID);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi cập nhật sự kiện: " + e.getMessage());
            request.getRequestDispatcher("/view/student/chairman/edit-event.jsp").forward(request, response);
        }
    }


    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
