package controller;

import dal.EventsDAO;
import dal.FeedbackDAO;
import dal.UserClubDAO;
import dal.UserDAO;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    private static final Logger LOGGER = Logger.getLogger(ViewFeedbackServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            FeedbackDAO feedbackDAO = new FeedbackDAO();

            HttpSession session = request.getSession();
            Users user = (Users) session.getAttribute("user");

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String eventIdStr = request.getParameter("eventId");
            if (eventIdStr == null || eventIdStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/events-page?error=missing_event_id&message=" + 
                    URLEncoder.encode("Thiếu thông tin sự kiện", StandardCharsets.UTF_8.name()));
                return;
            }

            try {
                int eventId = Integer.parseInt(eventIdStr);

                EventsDAO eventDAO = new EventsDAO();
                Events event = eventDAO.getEventByID(eventId);

                if (event == null) {
                    LOGGER.log(Level.SEVERE, "Cannot find event with ID: {0}. Cannot continue", eventId);
                    response.sendRedirect(request.getContextPath() + "/events-page?error=event_not_found");
                    return;
                }

                UserClubDAO userClubDAO = new UserClubDAO();
                boolean isClubPresident = userClubDAO.isClubPresident(user.getUserID(), event.getClubID());

                if (!isClubPresident) {
                    LOGGER.log(Level.WARNING, "User {0} not authorized to view feedback for event {1}",
                            new Object[]{user.getUserID(), eventId});
                    response.sendRedirect(request.getContextPath() + "/event-detail?id=" + eventId + "&error=not_authorized&message=" + 
                        URLEncoder.encode("Bạn không có quyền xem feedback của sự kiện này", StandardCharsets.UTF_8.name()));
                    return;
                }

                // Lấy danh sách feedback của sự kiện
                List<Feedback> feedbacks = new ArrayList<>();
                feedbacks = feedbackDAO.getFeedbacksByEventID(eventId);
                if (feedbacks == null) {
                    LOGGER.log(Level.WARNING, "getFeedbacksByEventID return null");
                    feedbacks = new ArrayList<>();
                } else {
                    LOGGER.log(Level.INFO, "{0} feedbacks, eventID: {1}",
                            new Object[]{feedbacks.size(), eventId});
                }

                // Tạo map lưu trữ thông tin user từ userID
                Map<String, String> userNames = new HashMap<>();
                UserDAO userDAO = new UserDAO();

                // Duyệt qua danh sách feedback để lấy tên người dùng
                if (!feedbacks.isEmpty()) {
                    for (Feedback fb : feedbacks) {
                        String userId = fb.getUserID();
                        if (!fb.isAnonymous() && userId != null && !userId.isEmpty() && !userNames.containsKey(userId)) {
                            Users userInfo = userDAO.getUserByID(userId);
                            if (userInfo != null && userInfo.getFullName() != null) {
                                userNames.put(userId, userInfo.getFullName());
                            } else {
                                userNames.put(userId, userId); // if user not found
                            }
                        }
                    }
                }

                Feedback statistics = null;
                statistics = feedbackDAO.getEventFeedbackStatistics(eventId);
                if (statistics == null) {
                    LOGGER.log(Level.WARNING, "getEventFeedbackStatistics returned null for event ID: {0}", eventId);
                }

                // Tính phân phối đánh giá
                Map<Integer, Integer> ratingDistribution = new HashMap<>();
                // Khởi tạo tất cả các giá trị là 0
                for (int i = 1; i <= 5; i++) {
                    ratingDistribution.put(i, 0);
                }

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
                            }
                        } catch (Exception e) {
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
                        ratingDistribution.put(i, 0);
                    }
                }

                request.setAttribute("userNames", userNames);

                // Kiểm tra nếu client yêu cầu dữ liệu JSON
                String format = request.getParameter("format");
                if ("json".equalsIgnoreCase(format)) {
                    // Xử lý request dạng JSON API
                    handleJsonRequest(request, response, ratingDistribution, statistics, feedbacks.size());
                    return;
                }

                request.setAttribute("event", event);
                request.setAttribute("feedbacks", feedbacks);
                request.setAttribute("statistics", statistics);
                request.setAttribute("ratingDistribution", ratingDistribution);
                request.setAttribute("feedbackCount", feedbacks.size());
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

    @Override
    public String getServletInfo() {
        return "ViewFeedbackServlet - Hiển thị các feedback của một sự kiện";
    }

    //API endpoint để trả về dữ liệu phân phối đánh giá dưới dạng JSON
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
