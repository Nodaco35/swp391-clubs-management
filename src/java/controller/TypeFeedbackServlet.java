package controller;

import dal.EventParticipantDAO;
import dal.EventsDAO;
import dal.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Events;
import models.Feedback;
import models.Users;
import java.io.IOException;


public class TypeFeedbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        // Kiểm tra xem người dùng đã đăng nhập chưa
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
            
            // Kiểm tra xem sự kiện có tồn tại không
            EventsDAO eventsDAO = new EventsDAO();
            Events event = eventsDAO.getEventByID(eventId);
            
            if (event == null) {
                response.sendRedirect(request.getContextPath() + "/events-page");
                return;
            }
            
            // Kiểm tra xem người dùng đã tham gia sự kiện chưa
            EventParticipantDAO participantDAO = new EventParticipantDAO();
            if (!participantDAO.hasUserParticipatedInEvent(currentUser.getUserID(), eventId)) {
                // Người dùng chưa tham gia sự kiện này
                request.setAttribute("message", "Chỉ người đã tham gia sự kiện mới có thể gửi phản hồi.");
                response.sendRedirect(request.getContextPath() + "/events-page");
                return;
            }
            
            // Kiểm tra xem người dùng đã gửi feedback chưa
            FeedbackDAO feedbackDAO = new FeedbackDAO();
            if (feedbackDAO.hasFeedbackForEvent(eventId, currentUser.getUserID())) {
                request.setAttribute("message", "Bạn đã gửi phản hồi cho sự kiện này rồi.");
                response.sendRedirect(request.getContextPath() + "/event-detail?id=" + eventId);
                return;
            }
            
            // Thiết lập thuộc tính và chuyển tiếp đến form feedback
            request.setAttribute("event", event);
            request.getRequestDispatcher("/view/student/typeFeedback.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/events-page");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        

        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        Users currentUser = (Users) session.getAttribute("user");
        
        // Kiểm tra xem người dùng đã đăng nhập chưa
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
            
            // Kiểm tra xem sự kiện có tồn tại không
            EventsDAO eventsDAO = new EventsDAO();
            Events event = eventsDAO.getEventByID(eventId);
            
            if (event == null) {
                response.sendRedirect(request.getContextPath() + "/events-page");
                return;
            }
            
            // Kiểm tra xem người dùng đã tham gia sự kiện chưa
            EventParticipantDAO participantDAO = new EventParticipantDAO();
            if (!participantDAO.hasUserParticipatedInEvent(currentUser.getUserID(), eventId)) {
                // Người dùng chưa tham gia sự kiện này
                request.setAttribute("message", "Chỉ người đã tham gia sự kiện mới có thể gửi phản hồi.");
                response.sendRedirect(request.getContextPath() + "/clubs");
                return;
            }
            
            // Lấy thông số từ form
            boolean isAnonymous = "true".equals(request.getParameter("isAnonymous"));
            
            try {
                // Kiểm tra và xử lý các trường bắt buộc
                String ratingStr = request.getParameter("rating");
                if (ratingStr == null || ratingStr.isEmpty()) {
                    request.setAttribute("error", "Vui lòng chọn đánh giá tổng quan.");
                    request.setAttribute("event", event);
                    request.getRequestDispatcher("/view/student/typeFeedback.jsp").forward(request, response);
                    return;
                }
                
                int rating = Integer.parseInt(ratingStr);
                String content = request.getParameter("content");
                if (content == null) {
                    content = "";
                }
                
                // Kiểm tra các trường đánh giá chi tiết
                String q1Str = request.getParameter("q1_organization");
                String q2Str = request.getParameter("q2_communication");
                String q3Str = request.getParameter("q3_support");
                String q4Str = request.getParameter("q4_relevance");
                String q5Str = request.getParameter("q5_welcoming");
                String q6Str = request.getParameter("q6_value");
                String q7Str = request.getParameter("q7_timing");
                String q8Str = request.getParameter("q8_participation");
                String q9Str = request.getParameter("q9_willingnessToReturn");
                
                // Kiểm tra xem có trường nào bị thiếu không
                if (q1Str == null || q1Str.isEmpty() ||
                    q2Str == null || q2Str.isEmpty() ||
                    q3Str == null || q3Str.isEmpty() ||
                    q4Str == null || q4Str.isEmpty() ||
                    q5Str == null || q5Str.isEmpty() ||
                    q6Str == null || q6Str.isEmpty() ||
                    q7Str == null || q7Str.isEmpty() ||
                    q8Str == null || q8Str.isEmpty() ||
                    q9Str == null || q9Str.isEmpty()) {
                    
                    request.setAttribute("error", "Vui lòng hoàn thành tất cả câu hỏi đánh giá chi tiết.");
                    request.setAttribute("event", event);
                    request.getRequestDispatcher("/view/student/typeFeedback.jsp").forward(request, response);
                    return;
                }
                
                // Chuyển đổi các giá trị sang số
                int q1Organization = Integer.parseInt(q1Str);
                int q2Communication = Integer.parseInt(q2Str);
                int q3Support = Integer.parseInt(q3Str);
                int q4Relevance = Integer.parseInt(q4Str);
                int q5Welcoming = Integer.parseInt(q5Str);
                int q6Value = Integer.parseInt(q6Str);
                int q7Timing = Integer.parseInt(q7Str);
                int q8Participation = Integer.parseInt(q8Str);
                int q9WillingnessToReturn = Integer.parseInt(q9Str);
                
                // Tạo đối tượng feedback
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
                
                // Lưu feedback vào cơ sở dữ liệu
                FeedbackDAO feedbackDAO = new FeedbackDAO();
                boolean success = feedbackDAO.insertFeedback(feedback);
                
                if (success) {
                    // Đặt thông báo thành công và chuyển tiếp đến trang feedback để hiển thị toast
                    request.setAttribute("success", "Cảm ơn bạn đã gửi phản hồi cho sự kiện này!");
                    request.setAttribute("event", event);
                    request.setAttribute("eventId", eventId);
                    request.getRequestDispatcher("/view/student/typeFeedback.jsp").forward(request, response);
                } else {
                    // Chuyển hướng với thông báo lỗi
                    request.setAttribute("error", "Có lỗi xảy ra khi gửi phản hồi. Vui lòng thử lại sau.");
                    request.setAttribute("event", event);
                    request.getRequestDispatcher("/view/student/typeFeedback.jsp").forward(request, response);
                }
            } catch (NumberFormatException e) {
                // Xử lý khi có lỗi chuyển đổi dữ liệu
                request.setAttribute("error", "Vui lòng hoàn thành tất cả các câu hỏi đánh giá chi tiết.");
                request.setAttribute("event", event);
                request.getRequestDispatcher("/view/student/typeFeedback.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/events-page");
        }
    }
}
