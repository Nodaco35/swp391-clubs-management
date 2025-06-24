package controller;

import dal.EventsDAO;
import dal.FeedbackDAO;
import dal.UserClubDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Events;
import models.Feedback;
import models.Users;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;

/**
 *
 * @author Vinh
 */
public class ViewFeedbackServlet extends HttpServlet {
     private static final Logger LOGGER = Logger.getLogger(ViewFeedbackServlet.class.getName());@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // Đảm bảo mã hóa UTF-8 cho cả request và response
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // Thêm headers để ngăn trình duyệt cache
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        
        try {
            FeedbackDAO feedbackDAO = new FeedbackDAO();
            
            HttpSession session = request.getSession();
            Users user = (Users) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            
            // Lấy eventId từ tham số request
            String eventIdStr = request.getParameter("eventId");
            if (eventIdStr == null || eventIdStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/events-page");
                return;
            }
            
            try {
                int eventId = Integer.parseInt(eventIdStr);
                
                // Lấy thông tin sự kiện
                EventsDAO eventDAO = new EventsDAO();
                Events event = eventDAO.getEventByID(eventId);
                
                if (event == null) {
                    LOGGER.log(Level.SEVERE, "Cannot find event with ID: {0}. Cannot continue", eventId);
                    response.sendRedirect(request.getContextPath() + "/events-page?error=event_not_found");
                    return;
                }
                
                // Kiểm tra xem người dùng có phải là chủ nhiệm hoặc trưởng ban của CLB không
                UserClubDAO userClubDAO = new UserClubDAO();
                boolean isClubPresident = userClubDAO.isClubPresident(user.getUserID(), event.getClubID());
                
                if (!isClubPresident) {
                    LOGGER.log(Level.WARNING, "User {0} not authorized to view feedback for event {1}", 
                        new Object[]{user.getUserID(), eventId});
                    response.sendRedirect(request.getContextPath() + "/event-detail?id=" + eventId);
                    return;
                }
                
                // Lấy danh sách feedback của sự kiện
                List<Feedback> feedbacks = new ArrayList<>();                
                try {
                    feedbacks = feedbackDAO.getFeedbacksByEventID(eventId);
                    if (feedbacks == null) {
                        LOGGER.log(Level.WARNING, "getFeedbacksByEventID return null");
                        feedbacks = new ArrayList<>();
                    } else {
                        LOGGER.log(Level.INFO, "Lấy được {0} phản hồi cho sự kiện ID: {1}", 
                            new Object[]{feedbacks.size(), eventId});
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error getting feedbacks for event: " + eventId, e);
                    feedbacks = new ArrayList<>();
                }
                  // Tính toán thống kê
                Feedback statistics = null;                
                try {
                    statistics = feedbackDAO.getEventFeedbackStatistics(eventId);
                    if (statistics == null) {
                        LOGGER.log(Level.WARNING, "getEventFeedbackStatistics returned null for event ID: {0}", eventId);
                        request.setAttribute("debugError", "Tính toán thống kê trả về null");
                    } else {
                        LOGGER.log(Level.INFO, "Statistic for event ID {0}: Rating={1}, Q1={2}, Q2={3}, Q3={4}, Q4={5}, Q5={6}", 
                            new Object[]{
                                eventId, 
                                statistics.getRating(), 
                                statistics.getQ1Organization(), 
                                statistics.getQ2Communication(),
                                statistics.getQ3Support(),
                                statistics.getQ4Relevance(),
                                statistics.getQ5Welcoming()
                            });
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error getting statistics for event {0}", new Object[]{eventId});
                    LOGGER.log(Level.SEVERE, "Chi tiết lỗi", e);
                    request.setAttribute("debugError", "Lỗi khi lấy thống kê: " + e.getMessage());
                }
                
                // Tính phân phối đánh giá
                Map<Integer, Integer> ratingDistribution = new HashMap<>();
                // Khởi tạo tất cả các giá trị là 0 (quan trọng để tránh null)
                for (int i = 1; i <= 5; i++) {
                    ratingDistribution.put(i, 0);
                }
                
                // Chỉ xử lý nếu có feedback
                if (!feedbacks.isEmpty()) {
                    for (Feedback feedback : feedbacks) {
                        try {
                            int rating = feedback.getRating();
                            if (rating >= 1 && rating <= 5) {
                                // Lấy giá trị hiện tại và cộng thêm 1
                                Integer currentCount = ratingDistribution.get(rating);
                                if (currentCount == null) {
                                    currentCount = 0;
                                }
                                ratingDistribution.put(rating, currentCount + 1);
                            } else {
                                LOGGER.log(Level.WARNING, "Invalid rating value found: {0} for feedback ID: {1}", 
                                    new Object[]{rating, feedback.getFeedbackID()});
                            }                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "Error processing feedback rating: {0}", e.getMessage());
                        }
                    }
                }
                
                for (int i = 1; i <= 5; i++) {
                    if (ratingDistribution.get(i) == null) {
                        ratingDistribution.put(i, 0);
                        LOGGER.log(Level.WARNING, "Fixed null value for rating {0}", i);
                    }
                    
                    // Đảm bảo giá trị là số nguyên hợp lệ
                    try {
                        Integer value = ratingDistribution.get(i);
                        // Đảm bảo không phải là null và là số không âm
                        if (value == null || value < 0) {
                            LOGGER.log(Level.WARNING, "Invalid rating distribution value for {0} stars: {1}, setting to 0", 
                                new Object[]{i, value});
                            ratingDistribution.put(i, 0);
                        }
                                      } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error validating rating value for {0} stars", i);
                        ratingDistribution.put(i, 0); // Đảm bảo có giá trị mặc định
                    }
                }
                  // Log tổng quan phân phối đánh giá
                LOGGER.log(Level.INFO, "Rating distribution: 5★={0}, 4★={1}, 3★={2}, 2★={3}, 1★={4}", 
                    new Object[]{
                        ratingDistribution.get(5),
                        ratingDistribution.get(4),
                        ratingDistribution.get(3),
                        ratingDistribution.get(2),
                        ratingDistribution.get(1)
                    });
                
                // Thêm attribute debug để hiển thị trên trang
                request.setAttribute("ratingDebug", String.format("5★=%d, 4★=%d, 3★=%d, 2★=%d, 1★=%d",
                    ratingDistribution.get(5),
                    ratingDistribution.get(4),
                    ratingDistribution.get(3),
                    ratingDistribution.get(2),
                    ratingDistribution.get(1)));
                  // Kiểm tra nếu client yêu cầu dữ liệu JSON
                String format = request.getParameter("format");
                if ("json".equalsIgnoreCase(format)) {
                    // Xử lý request dạng JSON API
                    handleJsonRequest(request, response, ratingDistribution, statistics, feedbacks.size());
                    return; // Không tiếp tục forward sang JSP
                }
                

                // Đặt dữ liệu vào request
                request.setAttribute("event", event);
                request.setAttribute("feedbacks", feedbacks);
                request.setAttribute("statistics", statistics);
                request.setAttribute("ratingDistribution", ratingDistribution);
                request.setAttribute("feedbackCount", feedbacks.size());
                  // Đã xóa đoạn lưu thời gian cập nhật
                
                request.getRequestDispatcher("/view/student/chairman/viewFeedback.jsp").forward(request, response);
                
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid eventId: " + eventIdStr, e);
                response.sendRedirect(request.getContextPath() + "/events-page");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in ViewFeedbackServlet", e);
        }
    } 

    /**
     * Xử lý phương thức POST - Chuyển hướng sang doGet
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGet(request, response);
    }
    
    // Đã xóa phương thức handleCheckUpdates không cần thiết

    @Override
    public String getServletInfo() {
        return "ViewFeedbackServlet - Hiển thị các feedback của một sự kiện";
    }

    /**
     * API endpoint để trả về dữ liệu phân phối đánh giá dưới dạng JSON
     * URI: /view-feedback?eventId=X&format=json
     */
    private void handleJsonRequest(HttpServletRequest request, HttpServletResponse response, 
            Map<Integer, Integer> ratingDistribution, Feedback statistics, int feedbackCount) 
            throws IOException {
        // Thiết lập response type là JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Tạo đối tượng chứa dữ liệu trả về
        Map<String, Object> jsonData = new HashMap<>();
        
        Map<String, Integer> ratingData = new HashMap<>();
        ratingData.put("rate5", ratingDistribution.get(5));
        ratingData.put("rate4", ratingDistribution.get(4));
        ratingData.put("rate3", ratingDistribution.get(3));
        ratingData.put("rate2", ratingDistribution.get(2));
        ratingData.put("rate1", ratingDistribution.get(1));
        
        // Thêm vào đối tượng JSON chính
        jsonData.put("ratingDistribution", ratingData);
        jsonData.put("feedbackCount", feedbackCount);
        
        // Thêm thông tin thống kê nếu có
        if (statistics != null) {
            Map<String, Object> statsData = new HashMap<>();
            statsData.put("averageRating", statistics.getRating());
            statsData.put("q1Organization", statistics.getQ1Organization());
            statsData.put("q2Communication", statistics.getQ2Communication());
            statsData.put("q3Support", statistics.getQ3Support());
            statsData.put("q4Relevance", statistics.getQ4Relevance());
            statsData.put("q5Welcoming", statistics.getQ5Welcoming());
            statsData.put("q6Value", statistics.getQ6Value());
            statsData.put("q7Timing", statistics.getQ7Timing());
            statsData.put("q8Participation", statistics.getQ8Participation());
            statsData.put("q9WillingnessToReturn", statistics.getQ9WillingnessToReturn());
            
            jsonData.put("statistics", statsData);
        }
        
        // Chuyển đối tượng thành chuỗi JSON và gửi về client
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(jsonData);
        
        // Ghi response
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
}
