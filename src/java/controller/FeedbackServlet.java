package controller;

import dal.EventParticipantDAO;
import dal.EventsDAO;
import dal.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Events;
import models.Feedback;
import models.Users;
import java.io.IOException;

@WebServlet(name = "FeedbackServlet", urlPatterns = {"/feedback"})
public class FeedbackServlet extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     * Shows the feedback form for a specific event
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
        Users currentUser = (Users) session.getAttribute("user");
        
        // Check if user is logged in
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String eventIdParam = request.getParameter("eventId");
        if (eventIdParam == null || eventIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/events-page");
            return;
        }
        
        try {
            int eventId = Integer.parseInt(eventIdParam);
            
            // Check if the event exists
            EventsDAO eventsDAO = new EventsDAO();
            Events event = eventsDAO.getEventByID(eventId);
            
            if (event == null) {
                response.sendRedirect(request.getContextPath() + "/events-page");
                return;
            }
            
            // Check if user participated in the event
            EventParticipantDAO participantDAO = new EventParticipantDAO();
            if (!participantDAO.hasUserParticipatedInEvent(currentUser.getUserID(), eventId)) {
                // User did not participate in this event
                request.setAttribute("message", "Chỉ người đã tham gia sự kiện mới có thể gửi phản hồi.");
                response.sendRedirect(request.getContextPath() + "/events-page");
                return;
            }
            
            // Check if user already submitted feedback
            FeedbackDAO feedbackDAO = new FeedbackDAO();
            if (feedbackDAO.hasFeedbackForEvent(eventId, currentUser.getUserID())) {
                request.setAttribute("message", "Bạn đã gửi phản hồi cho sự kiện này rồi.");
                response.sendRedirect(request.getContextPath() + "/event?id=" + eventId);
                return;
            }
            
            // Set attributes and forward to feedback form
            request.setAttribute("event", event);
            request.getRequestDispatcher("/view/events-page/feedback/feedback-form.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/events-page");
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Process feedback submission
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Set character encoding for Vietnamese content
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        // Check if user is logged in
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String eventIdParam = request.getParameter("eventId");
        if (eventIdParam == null || eventIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/events-page");
            return;
        }
        
        try {
            int eventId = Integer.parseInt(eventIdParam);
            
            // Check if the event exists
            EventsDAO eventsDAO = new EventsDAO();
            Events event = eventsDAO.getEventByID(eventId);
            
            if (event == null) {
                response.sendRedirect(request.getContextPath() + "/events-page");
                return;
            }
            
            // Check if user participated in the event
            EventParticipantDAO participantDAO = new EventParticipantDAO();
            if (!participantDAO.hasUserParticipatedInEvent(currentUser.getUserID(), eventId)) {
                // User did not participate in this event
                request.setAttribute("message", "Chỉ người đã tham gia sự kiện mới có thể gửi phản hồi.");
                response.sendRedirect(request.getContextPath() + "/clubs");
                return;
            }
            
            // Get form parameters
            boolean isAnonymous = "true".equals(request.getParameter("isAnonymous"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String content = request.getParameter("content");
            
            // Get detailed ratings
            int q1Organization = Integer.parseInt(request.getParameter("q1_organization"));
            int q2Communication = Integer.parseInt(request.getParameter("q2_communication"));
            int q3Support = Integer.parseInt(request.getParameter("q3_support"));
            int q4Relevance = Integer.parseInt(request.getParameter("q4_relevance"));
            int q5Welcoming = Integer.parseInt(request.getParameter("q5_welcoming"));
            int q6Value = Integer.parseInt(request.getParameter("q6_value"));
            int q7Timing = Integer.parseInt(request.getParameter("q7_timing"));
            int q8Participation = Integer.parseInt(request.getParameter("q8_participation"));
            int q9WillingnessToReturn = Integer.parseInt(request.getParameter("q9_willingnessToReturn"));
            
            // Create feedback object
            Feedback feedback = new Feedback();
            feedback.setEventID(eventId);
            feedback.setUserID(currentUser.getUserID());
            feedback.setAnonymous(isAnonymous);
            feedback.setRating(rating);
            feedback.setContent(content);
            feedback.setQ1Organization(q1Organization);
            feedback.setQ2Communication(q2Communication);
            feedback.setQ3Support(q3Support);
            feedback.setQ4Relevance(q4Relevance);
            feedback.setQ5Welcoming(q5Welcoming);
            feedback.setQ6Value(q6Value);
            feedback.setQ7Timing(q7Timing);
            feedback.setQ8Participation(q8Participation);
            feedback.setQ9WillingnessToReturn(q9WillingnessToReturn);
            
            // Save feedback to database
            FeedbackDAO feedbackDAO = new FeedbackDAO();
            boolean success = feedbackDAO.insertFeedback(feedback);
            
            if (success) {
                // Redirect with success message
                response.sendRedirect(request.getContextPath() + "/event?id=" + eventId + "&message=feedback_success");
            } else {
                // Redirect with error message
                request.setAttribute("error", "Có lỗi xảy ra khi gửi phản hồi. Vui lòng thử lại sau.");
                request.setAttribute("event", event);
                request.getRequestDispatcher("/view/events-page/feedback/feedback-form.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/events-page");
        }
    }
}
