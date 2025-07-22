/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import dal.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.*;

/**
 *
 * @author LE VAN THUAN
 */
public class RegistrationEvent extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RegistrationEvent.class.getName());

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
            out.println("<title>Servlet RegistrationEvent</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RegistrationEvent at " + request.getContextPath () + "</h1>");
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
        Users loggedInUser = (Users) session.getAttribute("user");

        if (loggedInUser != null) {
            UserDAO usersDAO = new UserDAO();
            Users userDetails = usersDAO.getUserById(loggedInUser.getUserID());
            request.setAttribute("userDetails", userDetails);
        }

        String eventID = request.getParameter("id");
        try {
            int id = Integer.parseInt(eventID);
            EventsDAO eventDAO = new EventsDAO();
            Events event = eventDAO.getEventByID(id);

            if (event != null) {
                EventStats stats = eventDAO.getSpotsLeftEvent(id);
                ApplicationFormTemplateDAO templateDAO = new ApplicationFormTemplateDAO();
                List<ApplicationFormTemplate> templates = event.getFormID() != null
                        ? templateDAO.getTemplatesByFormID(event.getFormID())
                        : new ArrayList<>();

                request.setAttribute("event", event);
                request.setAttribute("registeredCount", stats.getRegisteredCount());
                request.setAttribute("spotsLeft", stats.getSpotsLeft());
                request.setAttribute("formTemplates", templates);
                request.getRequestDispatcher("view/events-page/event-detail/registration-event.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/events-page");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/events-page");
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String fullName = request.getParameter("fullName");
        String studentId = request.getParameter("studentId");
        String email = request.getParameter("email");
        String eventIDRaw = request.getParameter("eventID");
        boolean agreed = request.getParameter("agreeTerms") != null;

        if (!agreed || fullName == null || studentId == null || email == null || eventIDRaw == null) {
            request.setAttribute("message", "❗ Vui lòng điền đầy đủ thông tin và đồng ý điều khoản.");
            request.setAttribute("messageType", "error");
            // Reload event and user details for the form
            reloadEventAndUserDetails(request, response, eventIDRaw);
            return;
        }

        try {
            int eventID = Integer.parseInt(eventIDRaw);
            EventsDAO eventDAO = new EventsDAO();
            Events event = eventDAO.getEventByID(eventID);
            if (event == null) {
                response.sendRedirect(request.getContextPath() + "/events-page");
                return;
            }

            // Check if user is already registered
            if (eventDAO.isUserRegistered(eventID, user.getUserID())) {
                request.setAttribute("message", "⚠️ Bạn đã đăng ký sự kiện này.");
                request.setAttribute("messageType", "info");
                reloadEventAndUserDetails(request, response, eventIDRaw);
                return;
            }

            // Get form templates for the event
            ApplicationFormTemplateDAO templateDAO = new ApplicationFormTemplateDAO();
            List<ApplicationFormTemplate> formTemplates = event.getFormID() != null
                    ? templateDAO.getTemplatesByFormID(event.getFormID())
                    : new ArrayList<>();

            // Collect responses for form templates
            StringBuilder responsesJson = new StringBuilder("{");
            for (ApplicationFormTemplate template : formTemplates) {
                String fieldName = "template_" + template.getTemplateId();
                String fieldType = template.getFieldType();

                if ("Checkbox".equalsIgnoreCase(fieldType)) {
                    String[] values = request.getParameterValues(fieldName + "[]");
                    responsesJson.append("\"").append(fieldName).append("\":[");
                    if (values != null && values.length > 0) {
                        for (String value : values) {
                            responsesJson.append("\"").append(value.replace("\"", "\\\"")).append("\",");
                        }
                        responsesJson.setLength(responsesJson.length() - 1); // Remove trailing comma
                    }
                    responsesJson.append("],");
                } else if ("Radio".equalsIgnoreCase(fieldType)) {
                    String value = request.getParameter(fieldName);
                    responsesJson.append("\"").append(fieldName).append("\":\"")
                            .append(value != null ? value.replace("\"", "\\\"") : "").append("\",");
                } else if ("Info".equalsIgnoreCase(fieldType)) {
                    continue; // Skip Info fields
                } else {
                    String value = request.getParameter(fieldName);
                    responsesJson.append("\"").append(fieldName).append("\":\"")
                            .append(value != null ? value.replace("\"", "\\\"") : "").append("\",");
                }
            }

            // Add metadata
            java.util.Calendar cal = java.util.Calendar.getInstance();
            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(cal.getTimeInMillis());
            responsesJson.append("\"_metadata\":{")
                    .append("\"submitTime\":\"").append(currentTimestamp).append("\",")
                    .append("\"formTitle\":\"").append(event.getEventName().replace("\"", "\\\"")).append("\",")
                    .append("\"formType\":\"Event\"")
                    .append("}");

            responsesJson.append("}");
            String responsesJsonString = responsesJson.toString();

            // Save to ApplicationResponses
            ApplicationResponse appResponse = new ApplicationResponse();
            Integer formID = event.getFormID();
            if (formID == null) {
                request.setAttribute("message", "❌ Sự kiện này chưa có đơn đăng ký, vui lòng liên hệ chủ nhiệm để thêm form câu hỏi.");
                request.setAttribute("messageType", "error");
                reloadEventAndUserDetails(request, response, eventIDRaw);
                return;
            }

            appResponse.setFormID(formID);
            appResponse.setUserID(user.getUserID());
            appResponse.setClubID(event.getClubID()); // Assuming Events has a getClubID method
            appResponse.setEventID(eventID);
            appResponse.setResponses(responsesJsonString);
            appResponse.setStatus("Pending");

            ApplicationResponseDAO responseDAO = new ApplicationResponseDAO();
            int responseId = responseDAO.saveResponse(appResponse);

            if (responseId == -1) {
                request.setAttribute("message", "❌ Lỗi khi lưu câu trả lời, vui lòng thử lại.");
                request.setAttribute("messageType", "error");
                reloadEventAndUserDetails(request, response, eventIDRaw);
                return;
            }

            // Save to ClubApplications
            ClubApplication clubApp = new ClubApplication();
            clubApp.setUserId(user.getUserID());
            clubApp.setClubId(event.getClubID());
            clubApp.setEmail(email);
            clubApp.setEventId(eventID);
            clubApp.setResponseId(responseId);
            clubApp.setStatus("PENDING");
            clubApp.setSubmitDate(currentTimestamp);

            ClubApplicationDAO clubApplicationDAO = new ClubApplicationDAO();
            int applicationId = clubApplicationDAO.saveClubApplication(clubApp);

            if (applicationId == -1) {
                request.setAttribute("message", "❌ Lỗi khi lưu đơn đăng ký, vui lòng thử lại.");
                request.setAttribute("messageType", "error");
                reloadEventAndUserDetails(request, response, eventIDRaw);
                return;
            }

            // Register participant in the event
            boolean success = eventDAO.registerParticipant(eventID, user.getUserID());
            if (success) {
                request.setAttribute("message", "🎉 Đăng ký sự kiện thành công!");
                request.setAttribute("messageType", "success");
            } else {
                request.setAttribute("message", "❌ Đăng ký thất bại, vui lòng thử lại.");
                request.setAttribute("messageType", "error");
            }

            // Reload event and user details for the form
            reloadEventAndUserDetails(request, response, eventIDRaw);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/events-page");
        }
    }

    // Helper method to reload event and user details
    private void reloadEventAndUserDetails(HttpServletRequest request, HttpServletResponse response, String eventIDRaw)
            throws ServletException, IOException {
        try {
            int eventID = Integer.parseInt(eventIDRaw);
            EventsDAO eventDAO = new EventsDAO();
            Events event = eventDAO.getEventByID(eventID);
            EventStats stats = eventDAO.getSpotsLeftEvent(eventID);
            ApplicationFormTemplateDAO templateDAO = new ApplicationFormTemplateDAO();
            List<ApplicationFormTemplate> templates = event.getFormID() != null
                    ? templateDAO.getTemplatesByFormID(event.getFormID())
                    : new ArrayList<>();

            UserDAO userDAO = new UserDAO();
            Users userDetails = userDAO.getUserById(((Users) request.getSession().getAttribute("user")).getUserID());

            request.setAttribute("event", event);
            request.setAttribute("registeredCount", stats.getRegisteredCount());
            request.setAttribute("spotsLeft", stats.getSpotsLeft());
            request.setAttribute("formTemplates", templates);
            request.setAttribute("userDetails", userDetails);
            request.getRequestDispatcher("view/events-page/event-detail/registration-event.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/events-page");
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
