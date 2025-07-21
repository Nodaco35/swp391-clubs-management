package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

import dal.EventsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.EventSchedule;
import models.Events;

/**
 * @author LE VAN THUAN
 */
public class AgendaServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AgendaServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AgendaServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/student/chairman/edit-event.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String sourcePage = request.getParameter("sourcePage");
        int eventID = Integer.parseInt(request.getParameter("eventID"));
        String[] scheduleIDs = request.getParameterValues("scheduleID[]");
        String[] startTimes = request.getParameterValues("agendaStartTime[]");
        String[] endTimes = request.getParameterValues("agendaEndTime[]");
        String[] activities = request.getParameterValues("agendaActivity[]");
        String[] descriptions = request.getParameterValues("agendaDescription[]");

        EventsDAO eventsDAO = new EventsDAO();

        // Kiểm tra trạng thái hiện tại của event
        String currentApprovalStatus = eventsDAO.getEventApprovalStatus(eventID);
        if ("REJECTED".equals(currentApprovalStatus)) {
            eventsDAO.updateEventApprovalStatus(eventID, "PENDING");
        }

        // Xóa tất cả agenda cũ
        eventsDAO.deleteAllByEventID(eventID);

        if (scheduleIDs != null && startTimes != null && endTimes != null && activities != null && descriptions != null) {
            Events event = eventsDAO.getEventByID(eventID);
            for (int i = 0; i < activities.length; i++) {
                try {
                    int scheduleID = Integer.parseInt(scheduleIDs[i]);
                    // Tìm schedule tương ứng để lấy EventDate
                    EventSchedule schedule = event.getSchedules().stream()
                            .filter(s -> s.getScheduleID() == scheduleID)
                            .findFirst()
                            .orElse(null);
                    if (schedule == null) {
                        session.setAttribute("errorMessage", "Lịch trình không hợp lệ cho agenda thứ " + (i + 1) + ".");
                        response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/edit-event?eventID=" + eventID);
                        return;
                    }

                    LocalTime start = LocalTime.parse(startTimes[i]);
                    LocalTime end = LocalTime.parse(endTimes[i]);
                    String title = activities[i];
                    String description = descriptions[i] != null ? descriptions[i] : "";

                    Timestamp startTS = Timestamp.valueOf(LocalDateTime.of(schedule.getEventDate().toLocalDate(), start));
                    Timestamp endTS = Timestamp.valueOf(LocalDateTime.of(schedule.getEventDate().toLocalDate(), end));

                    eventsDAO.insertAgendas(scheduleID, title, description, startTS, endTS);
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("errorMessage", "Lỗi khi thêm chương trình sự kiện: " + e.getMessage());
                    response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/edit-event?eventID=" + eventID);
                    return;
                }
            }
        }

        session.setAttribute("successMsg", "Cập nhật chương trình sự kiện thành công!");
        if ("add-event".equals(sourcePage)) {
            session.removeAttribute("newEventID");
            response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events");
        } else {
            response.sendRedirect(request.getContextPath() + "/chairman-page/myclub-events/edit-event?eventID=" + eventID);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}