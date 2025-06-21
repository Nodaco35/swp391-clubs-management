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
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author Vinh
 */
public class ViewFeedbackServlet extends HttpServlet {
   
    private static final Logger LOGGER = Logger.getLogger(ViewFeedbackServlet.class.getName());
    // Lưu trữ thời điểm cập nhật cuối cùng cho mỗi event
    private static final Map<Integer, Long> lastUpdateTimeMap = new HashMap<>();
    // Lưu trữ số lượng phản hồi cuối cùng cho mỗi sự kiện
    private static final Map<Integer, Integer> lastFeedbackCountMap = new HashMap<>();
      @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // Thêm headers để ngăn trình duyệt cache
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        try {
            // Khởi tạo DAO objects
            FeedbackDAO feedbackDAO = new FeedbackDAO();
            
            // Kiểm tra đăng nhập
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
            
            // Xử lý yêu cầu kiểm tra cập nhật mới qua AJAX
            String checkUpdates = request.getParameter("checkUpdates");
            if ("true".equals(checkUpdates)) {
                handleCheckUpdates(request, response, eventIdStr);
                return;
            }
            
            try {
                int eventId = Integer.parseInt(eventIdStr);
                
                // Lấy thông tin sự kiện
                EventsDAO eventDAO = new EventsDAO();
                Events event = eventDAO.getEventByID(eventId);
                
                if (event == null) {
                    LOGGER.log(Level.SEVERE, "Không tìm thấy sự kiện có ID: {0}. Không thể tiếp tục mà không có dữ liệu sự kiện.", eventId);
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
                    LOGGER.log(Level.INFO, "Đang lấy phản hồi cho sự kiện có ID: {0}", eventId);
                    feedbacks = feedbackDAO.getFeedbacksByEventID(eventId);
                    if (feedbacks == null) {
                        LOGGER.log(Level.WARNING, "getFeedbacksByEventID trả về null, sử dụng danh sách trống thay thế");
                        feedbacks = new ArrayList<>();
                    } else {
                        LOGGER.log(Level.INFO, "Got {0} feedback items for event ID: {1}", 
                            new Object[]{feedbacks.size(), eventId});
                        
                        // Lưu số lượng feedback vào map
                        lastFeedbackCountMap.put(eventId, feedbacks.size());
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error getting feedbacks for event: " + eventId, e);
                    feedbacks = new ArrayList<>();
                }
                
                // Tính toán thống kê
                Feedback statistics = null;
                try {
                    LOGGER.log(Level.INFO, "Calculating statistics for event ID: {0}", eventId);
                    statistics = feedbackDAO.getEventFeedbackStatistics(eventId);
                    if (statistics == null) {
                        LOGGER.log(Level.WARNING, "getEventFeedbackStatistics returned null for event ID: {0}", eventId);
                        request.setAttribute("debugError", "Statistics calculation returned null");
                    } else {
                        LOGGER.log(Level.INFO, "Statistics for event ID {0}: Rating={1}, Q1={2}, Q2={3}, Q3={4}, Q4={5}, Q5={6}", 
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
                    LOGGER.log(Level.SEVERE, "Error getting statistics for event: {0}", new Object[]{eventId});
                    LOGGER.log(Level.SEVERE, "Exception details", e);
                    request.setAttribute("debugError", "Error getting statistics: " + e.getMessage());
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
                                    currentCount = 0; // Đảm bảo không có null
                                }
                                ratingDistribution.put(rating, currentCount + 1);
                            } else {
                                LOGGER.log(Level.WARNING, "Invalid rating value found: {0} for feedback ID: {1}", 
                                    new Object[]{rating, feedback.getFeedbackID()});
                            }
                        } catch (Exception e) {
                            // Bỏ qua các feedback không hợp lệ
                            LOGGER.log(Level.WARNING, "Error processing feedback rating: {0}", e.getMessage());
                            request.setAttribute("ratingError", "Error processing ratings: " + e.getMessage());
                        }
                    }
                }
                
                // Kiểm tra và log phân phối đánh giá (đảm bảo không có null)
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
                        
                        // Log kiểu dữ liệu thực tế
                        LOGGER.log(Level.INFO, "Rating {0} stars: Value={1}, Type={2}", 
                            new Object[]{i, ratingDistribution.get(i), ratingDistribution.get(i).getClass().getName()});
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error validating rating value for {0} stars", i);
                        ratingDistribution.put(i, 0); // Đảm bảo có giá trị mặc định
                    }
                }
                
                // Log phân phối đánh giá để debug
                LOGGER.log(Level.INFO, "Rating distribution: 5★={0}, 4★={1}, 3★={2}, 2★={3}, 1★={4}", 
                    new Object[]{
                        ratingDistribution.get(5),
                        ratingDistribution.get(4),
                        ratingDistribution.get(3),
                        ratingDistribution.get(2),
                        ratingDistribution.get(1)
                    });
                
                // Đặt attribute debug để hiển thị trên trang
                request.setAttribute("ratingDebug", String.format("5★=%d, 4★=%d, 3★=%d, 2★=%d, 1★=%d",
                    ratingDistribution.get(5),
                    ratingDistribution.get(4),
                    ratingDistribution.get(3),
                    ratingDistribution.get(2),
                    ratingDistribution.get(1)));
                
                // Đặt dữ liệu vào request
                request.setAttribute("event", event);
                request.setAttribute("feedbacks", feedbacks);
                request.setAttribute("statistics", statistics);
                request.setAttribute("ratingDistribution", ratingDistribution);
                request.setAttribute("feedbackCount", feedbacks.size());
                
                // Lưu trữ thời gian cập nhật cho event này
                lastUpdateTimeMap.put(eventId, System.currentTimeMillis());
                
                // Forward đến trang JSP để hiển thị
                request.getRequestDispatcher("/view/student/chairman/viewFeedback.jsp").forward(request, response);
                
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid eventId: " + eventIdStr, e);
                response.sendRedirect(request.getContextPath() + "/events-page");
            }
        } catch (Exception e) {
            // Xử lý tất cả các ngoại lệ khác
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

    /**
     * Xử lý yêu cầu kiểm tra cập nhật mới từ AJAX
     */
    private void handleCheckUpdates(HttpServletRequest request, HttpServletResponse response, String eventIdStr) 
    throws ServletException, IOException {
        LOGGER.log(Level.INFO, "Đang xử lý yêu cầu kiểm tra cập nhật cho event ID: {0}", eventIdStr);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        try {
            int eventId = Integer.parseInt(eventIdStr);
            FeedbackDAO feedbackDAO = new FeedbackDAO();
            
            // Lấy số lượng feedback hiện tại
            List<Feedback> currentFeedbacks = feedbackDAO.getFeedbacksByEventID(eventId);
            int currentCount = (currentFeedbacks != null) ? currentFeedbacks.size() : 0;
            
            // Lấy số lượng feedback lần cuối đã biết
            Integer lastKnownCount = lastFeedbackCountMap.get(eventId);
            if (lastKnownCount == null) {
                lastKnownCount = 0;
            }
            
            // Kiểm tra xem có cập nhật mới không
            boolean hasNewFeedback = currentCount > lastKnownCount;
            LOGGER.log(Level.INFO, "Check Updates: EventID={0}, Current Count={1}, Last Known Count={2}, Has New={3}",
                new Object[]{eventId, currentCount, lastKnownCount, hasNewFeedback});
            
            // Cập nhật số lượng feedback mới nhất
            lastFeedbackCountMap.put(eventId, currentCount);
            
            // Tạo response JSON (sử dụng chuỗi thay vì thư viện JSON)
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            jsonBuilder.append("\"hasUpdates\":").append(hasNewFeedback).append(",");
            jsonBuilder.append("\"currentFeedbackCount\":").append(currentCount).append(",");
            jsonBuilder.append("\"previousFeedbackCount\":").append(lastKnownCount).append(",");
            jsonBuilder.append("\"timestamp\":").append(System.currentTimeMillis());
            
            if (hasNewFeedback) {
                // Nếu có cập nhật mới, tính toán lại phân phối đánh giá và thêm vào response
                Map<Integer, Integer> newRatingDistribution = new HashMap<>();
                
                // Khởi tạo tất cả các giá trị là 0
                for (int i = 1; i <= 5; i++) {
                    newRatingDistribution.put(i, 0);
                }
                
                // Tính toán phân phối đánh giá mới
                if (currentFeedbacks != null && !currentFeedbacks.isEmpty()) {
                    for (Feedback feedback : currentFeedbacks) {
                        int rating = feedback.getRating();
                        if (rating >= 1 && rating <= 5) {
                            Integer currentValue = newRatingDistribution.get(rating);
                            newRatingDistribution.put(rating, currentValue + 1);
                        }
                    }
                }
                
                // Thêm phân phối đánh giá mới vào response
                jsonBuilder.append(",\"ratingDistribution\":{");
                for (int i = 1; i <= 5; i++) {
                    jsonBuilder.append("\"").append(i).append("\":").append(newRatingDistribution.get(i));
                    if (i < 5) {
                        jsonBuilder.append(",");
                    }
                }
                jsonBuilder.append("}");
                
                // Thêm thông tin thống kê tổng quan nếu có
                Feedback statistics = feedbackDAO.getEventFeedbackStatistics(eventId);
                if (statistics != null) {
                    jsonBuilder.append(",\"statistics\":{");
                    jsonBuilder.append("\"averageRating\":").append(statistics.getRating());
                    jsonBuilder.append("}");
                }
            }
            
            jsonBuilder.append("}");
            out.print(jsonBuilder.toString());
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid eventId for checkUpdates: " + eventIdStr, e);
            out.print("{\"error\":\"Invalid event ID\"}");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking for updates", e);
            out.print("{\"error\":\"Internal server error\"}");
        }
        
        out.flush();
    }

    @Override
    public String getServletInfo() {
        return "ViewFeedbackServlet - Hiển thị các feedback của một sự kiện";
    }
}
