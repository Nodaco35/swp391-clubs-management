<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="dal.UserDAO" %>
<%@ page import="java.util.*" %>
<%@ page import="models.Users" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Feedback - ${event.eventName}</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;500;600;700&family=Poppins:wght@300;400;500;600;700&family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewFeedback.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<<<<<<< HEAD
=======

    <!-- Thêm event ID làm meta tag để giúp debug JS -->
    <meta name="eventId" content="${param.eventId}">

    <!-- Script debug cho ratingDistribution -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // In chi tiết về object ratingDistribution
            const ratingDebug = {
                isEmpty: "${empty ratingDistribution}",
                isNull: "${ratingDistribution == null}",
                size: "${not empty ratingDistribution ? ratingDistribution.size() : 'N/A'}",
                keys: "${not empty ratingDistribution ? ratingDistribution.keySet() : 'N/A'}",
                values: "${not empty ratingDistribution ? ratingDistribution.values() : 'N/A'}"
            };
            console.log("Chi tiết ratingDistribution:", ratingDebug);
        });
    </script>


>>>>>>> 80d5538cff8a23b3f10d295a4cb3eec2de29f265
</head>
<body>
    <jsp:include page="/view/events-page/header.jsp" />
    
    <%-- Tạo Map để lưu thông tin người dùng từ UserID --%>
    <%
        // Tạo map lưu trữ thông tin user từ userID để không cần truy vấn database nhiều lần
        Map<String, String> userNames = new HashMap<>();
        UserDAO userDAO = new UserDAO();
        
        // Kiểm tra danh sách feedback từ request attribute
        List<models.Feedback> feedbackList = (List<models.Feedback>)request.getAttribute("feedbacks");
        if (feedbackList != null) {
            for (models.Feedback fb : feedbackList) {
                String userId = fb.getUserID();
                if (!fb.isAnonymous() && userId != null && !userId.isEmpty() && !userNames.containsKey(userId)) {
                    try {
                        Users userInfo = userDAO.getUserByID(userId);
                        if (userInfo != null && userInfo.getFullName() != null) {
                            userNames.put(userId, userInfo.getFullName());
                        } else {
                            userNames.put(userId, userId); // Fallback to ID if user not found
                        }
                    } catch (Exception e) {
                        userNames.put(userId, userId); // Fallback to ID if exception
                    }
                }
            }
        }
        request.setAttribute("userNames", userNames);
    %>
    
    <div class="container">
        <div class="feedback-container">
            <div class="feedback-header">
                <h1>Feedback - ${event.eventName}</h1>
                <p>Xem phản hồi đánh giá của người tham gia về sự kiện</p>
            </div>

            <!-- Thông tin sự kiện -->
            <div class="event-info">
                <img src="${pageContext.request.contextPath}/images/events/${event.eventImg}" alt="${event.eventName}" class="event-image">
                <div class="event-details">
                    <h3>${event.eventName}</h3>
                    <div class="event-date">
                        <i class="fas fa-calendar-alt"></i> 
                        <fmt:formatDate value="${event.eventDate}" pattern="dd/MM/yyyy HH:mm"/>
                    </div>
                    <div class="event-location">
                        <i class="fas fa-map-marker-alt"></i> ${event.location}
                    </div>
                </div>
            </div>

            <!-- Tổng quan đánh giá -->
            <c:choose>
                <c:when test="${statistics != null && feedbackCount > 0}">
                <div class="rating-overview">
                    <div class="average-rating">
                        <div class="rating-stars">
                            <c:forEach begin="1" end="5" var="i">
                                <c:choose>
                                    <c:when test="${i <= statistics.rating}">
                                        <i class="fas fa-star"></i>
                                    </c:when>
                                    <c:otherwise>
                                        <i class="far fa-star"></i>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </div>
                        <div class="rating-value">${statistics.rating}.0/5</div>
                        <div class="rating-count">${feedbackCount} đánh giá</div>
                    </div>
                </div>
                </c:when>
                <c:otherwise>
                <div class="rating-overview">
                    <div class="average-rating">
                        <div class="no-ratings">
                            <i class="far fa-star"></i>
                            <i class="far fa-star"></i>
                            <i class="far fa-star"></i>
                            <i class="far fa-star"></i>
                            <i class="far fa-star"></i>
                        </div>
                        <div class="rating-value">0.0/5</div>
                        <div class="rating-count">Chưa có đánh giá</div>
                    </div>
                </div>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${feedbackCount > 0}">
                <!-- Biểu đồ phân bố đánh giá -->
                <div class="rating-stats">
                    <h2>Phân bố đánh giá tổng quan</h2>
                    <div class="chart-container">
                        <div class="chart-legend" id="ratingDistributionLegend">
                            <div class="legend-item">
                                <span class="legend-color" style="background-color: #b71c1c;"></span>
                                <span class="legend-text">1 sao - Rất không hài lòng</span>
                            </div>
                            <div class="legend-item">
                                <span class="legend-color" style="background-color: #ff5252;"></span>
                                <span class="legend-text">2 sao - Không hài lòng</span>
                            </div>
                            <div class="legend-item">
                                <span class="legend-color" style="background-color: #ffc107;"></span>
                                <span class="legend-text">3 sao - Bình thường</span>
                            </div>
                            <div class="legend-item">
                                <span class="legend-color" style="background-color: #4caf50;"></span>
                                <span class="legend-text">4 sao - Hài lòng</span>
                            </div>
                            <div class="legend-item">
                                <span class="legend-color" style="background-color: #2e7d32;"></span>
                                <span class="legend-text">5 sao - Rất hài lòng</span>
                            </div>
                        </div>
                        <canvas id="ratingDistributionChart" 
<<<<<<< HEAD
                            data-rate5="<c:out value="${ratingDistribution[5]}" default="0"/>"
                            data-rate4="<c:out value="${ratingDistribution[4]}" default="0"/>"
                            data-rate3="<c:out value="${ratingDistribution[3]}" default="0"/>"
                            data-rate2="<c:out value="${ratingDistribution[2]}" default="0"/>"
                            data-rate1="<c:out value="${ratingDistribution[1]}" default="0"/>"
                            data-debug="5★=${ratingDistribution[5]}, 4★=${ratingDistribution[4]}, 3★=${ratingDistribution[3]}, 2★=${ratingDistribution[2]}, 1★=${ratingDistribution[1]}"></canvas>

                        <!-- Dữ liệu đánh giá trực tiếp từ Java - Sử dụng JSTL -->
                        <script>
                            // Dữ liệu đánh giá truyền trực tiếp từ JSP sang JS
                            window.ratingDistributionDirectData = {
                                rate5: "${ratingDistribution[5]}",
                                rate4: "${ratingDistribution[4]}",
                                rate3: "${ratingDistribution[3]}",
                                rate2: "${ratingDistribution[2]}",
                                rate1: "${ratingDistribution[1]}"
                            };
                            console.log("Dữ liệu trực tiếp từ JSP JSTL:", window.ratingDistributionDirectData);
                            
                            // Chuyển đổi thành số
                            window.ratingDistributionDirectDataNumbers = {
                                rate5: parseInt(window.ratingDistributionDirectData.rate5 || "0", 10),
                                rate4: parseInt(window.ratingDistributionDirectData.rate4 || "0", 10),
                                rate3: parseInt(window.ratingDistributionDirectData.rate3 || "0", 10),
                                rate2: parseInt(window.ratingDistributionDirectData.rate2 || "0", 10),
                                rate1: parseInt(window.ratingDistributionDirectData.rate1 || "0", 10)
                            };
                            console.log("Dữ liệu số từ JSP JSTL:", window.ratingDistributionDirectDataNumbers);
=======
                            data-rate5="${ratingDistribution.get(5)}" 
                            data-rate4="${ratingDistribution.get(4)}" 
                            data-rate3="${ratingDistribution.get(3)}" 
                            data-rate2="${ratingDistribution.get(2)}" 
                            data-rate1="${ratingDistribution.get(1)}">
                        </canvas>
                        
                        <!-- Truyền dữ liệu qua JavaScript trực tiếp thay vì data-attribute -->
                        <script>
                            // Cách thiết lập dữ liệu thủ công để đảm bảo giá trị chính xác
                            window.ratingDistributionData = {
                                rate5: parseInt("${ratingDistribution.get(5)}", 10) || 0,
                                rate4: parseInt("${ratingDistribution.get(4)}", 10) || 0,
                                rate3: parseInt("${ratingDistribution.get(3)}", 10) || 0,
                                rate2: parseInt("${ratingDistribution.get(2)}", 10) || 0,
                                rate1: parseInt("${ratingDistribution.get(1)}", 10) || 0
                            };
                            
                            // Log các giá trị để kiểm tra
                            console.log("Truyền dữ liệu trực tiếp vào JavaScript: ", window.ratingDistributionData);
                        </script>

                        
                        <script>
                            // Mặc định là true, sẽ được cập nhật trong JS dựa trên dữ liệu thực tế
                            window.shouldDisplayCharts = true;
                            
                            console.log("Dữ liệu phân phối đánh giá raw từ backend (get + containsKey):", {
                                rate5: "${ratingDistribution.containsKey(5) ? ratingDistribution.get(5) : 'Key không tồn tại'}",
                                rate4: "${ratingDistribution.containsKey(4) ? ratingDistribution.get(4) : 'Key không tồn tại'}",
                                rate3: "${ratingDistribution.containsKey(3) ? ratingDistribution.get(3) : 'Key không tồn tại'}",
                                rate2: "${ratingDistribution.containsKey(2) ? ratingDistribution.get(2) : 'Key không tồn tại'}",
                                rate1: "${ratingDistribution.containsKey(1) ? ratingDistribution.get(1) : 'Key không tồn tại'}"
                            });
                            
                            // Thêm debug type của ratingDistribution
                            console.log("Kiểu dữ liệu ratingDistribution: ${ratingDistribution.getClass().getName()}");
                            
                            // Kiểm tra dữ liệu JSON được tạo
                            document.addEventListener('DOMContentLoaded', function() {
                                console.log("JSON data-distribution:", document.getElementById("ratingDistributionChart").getAttribute("data-distribution"));
                                console.log("Debug attribute:", document.getElementById("ratingDistributionChart").getAttribute("data-debug"));
                                
                                // Debug thông tin tổng hợp
                                console.log("Thông tin tổng hợp:", {
                                    feedbackCount: "${feedbackCount}",
                                    hasData: "${feedbackCount > 0}",
                                    hasRatingDistribution: "${not empty ratingDistribution}",
                                    statisticsNotNull: "${statistics != null}"
                                });
                                    // Debug biến toàn cục mới tạo
                                    console.log("Dữ liệu từ biến window.ratingDistributionData:", window.ratingDistributionData);
                                } catch(e) {
                                    console.error("Lỗi debug thuộc tính:", e);
                                }
                            });
>>>>>>> 80d5538cff8a23b3f10d295a4cb3eec2de29f265
                        </script>
                    </div>
                </div>

                <!-- Biểu đồ đánh giá chi tiết theo tiêu chí -->
                <div class="detailed-stats">
                    <h2>Thống kê đánh giá chi tiết theo tiêu chí</h2>
                    <div class="chart-container">
                        <div class="chart-legend" id="detailedRatingLegend">
                            <div class="legend-item">
                                <span class="legend-color" style="background-color: #b71c1c;"></span>
                                <span class="legend-text">Rất không hài lòng</span>
                            </div>
                            <div class="legend-item">
                                <span class="legend-color" style="background-color: #ff5252;"></span>
                                <span class="legend-text">Không hài lòng</span>
                            </div>
                            <div class="legend-item">
                                <span class="legend-color" style="background-color: #ffc107;"></span>
                                <span class="legend-text">Bình thường</span>
                            </div>
                            <div class="legend-item">
                                <span class="legend-color" style="background-color: #4caf50;"></span>
                                <span class="legend-text">Hài lòng</span>
                            </div>
                            <div class="legend-item">
                                <span class="legend-color" style="background-color: #2e7d32;"></span>
                                <span class="legend-text">Rất hài lòng</span>
                            </div>
                        </div>
                        <canvas id="detailedRatingChart" 
                            data-criteria='{
                                "q1Organization": ${statistics.q1Organization != null ? statistics.q1Organization : 0},
                                "q2Communication": ${statistics.q2Communication != null ? statistics.q2Communication : 0},
                                "q3Support": ${statistics.q3Support != null ? statistics.q3Support : 0},
                                "q4Relevance": ${statistics.q4Relevance != null ? statistics.q4Relevance : 0},
                                "q5Welcoming": ${statistics.q5Welcoming != null ? statistics.q5Welcoming : 0},
                                "q6Value": ${statistics.q6Value != null ? statistics.q6Value : 0},
                                "q7Timing": ${statistics.q7Timing != null ? statistics.q7Timing : 0},
                                "q8Participation": ${statistics.q8Participation != null ? statistics.q8Participation : 0},
                                "q9WillingnessToReturn": ${statistics.q9WillingnessToReturn != null ? statistics.q9WillingnessToReturn : 0}
                            }'>
                        </canvas>
                    </div>
                </div>
                </c:when>
                <c:otherwise>
                <!-- Thông báo khi không có đánh giá -->
                <div class="no-feedback-section">
                    <div class="no-feedback-message">
                        <i class="fas fa-chart-bar"></i>
                        <p>Chưa có đánh giá nào cho sự kiện này.</p>
                        <p class="small-text">Các biểu đồ thống kê sẽ được hiển thị khi có người gửi đánh giá.</p>
                    </div>
                </div>
                </c:otherwise>
            </c:choose>

            <!-- Danh sách feedback -->
            <div class="feedback-list">
                <h2>Chi tiết feedback - ${event.eventName}</h2>
                
                <!-- Bộ lọc -->
                <div class="feedback-filter">
                    <div class="filter-label">Lọc theo đánh giá:</div>
                    <div class="filter-buttons">
                        <button type="button" class="filter-btn active" data-filter="all">Tất cả đánh giá</button>
                        <button type="button" class="filter-btn" data-filter="5">5 <i class="fas fa-star"></i></button>
                        <button type="button" class="filter-btn" data-filter="4">4 <i class="fas fa-star"></i></button>
                        <button type="button" class="filter-btn" data-filter="3">3 <i class="fas fa-star"></i></button>
                        <button type="button" class="filter-btn" data-filter="2">2 <i class="fas fa-star"></i></button>
                        <button type="button" class="filter-btn" data-filter="1">1 <i class="fas fa-star"></i></button>
                    </div>
                </div>
                
                <!-- Danh sách feedback chi tiết -->
                <div class="feedback-items">
                    <c:forEach items="${feedbacks}" var="feedback">
                        <div class="feedback-item" data-rating="${feedback.rating}">
                            <div class="feedback-item-header">
                                <div class="feedback-user">
                                    <c:choose>
                                        <c:when test="${feedback.anonymous}">
                                            <span class="user-name"><i class="fas fa-user-secret"></i> Người dùng ẩn danh</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="user-name" data-userid="${feedback.userID}"><i class="fas fa-user"></i> ${userNames[feedback.userID] != null ? userNames[feedback.userID] : feedback.userID}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="feedback-date">
                                    <fmt:formatDate value="${feedback.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </div>
                            </div>
                            
                            <div class="feedback-rating">
                                <div class="rating-label">
                                    <c:choose>
                                        <c:when test="${feedback.rating == 5}">
                                            <span class="rating-good">Rất hài lòng</span>
                                        </c:when>
                                        <c:when test="${feedback.rating == 4}">
                                            <span class="rating-good">Hài lòng</span>
                                        </c:when>
                                        <c:when test="${feedback.rating == 3}">
                                            <span class="rating-neutral">Bình thường</span>
                                        </c:when>
                                        <c:when test="${feedback.rating == 2}">
                                            <span class="rating-bad">Không hài lòng</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="rating-bad">Rất không hài lòng</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="rating-stars">
                                    <c:forEach begin="1" end="5" var="i">
                                        <c:choose>
                                            <c:when test="${i <= feedback.rating}">
                                                <i class="fas fa-star"></i>
                                            </c:when>
                                            <c:otherwise>
                                                <i class="far fa-star"></i>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </div>
                            </div>
                            
                            <div class="feedback-content">
                                <p>${feedback.content}</p>
                            </div>
                            
                            <div class="feedback-actions">
                                <button class="view-detail-btn" data-feedback="${feedback.feedbackID}">
                                    <i class="fas fa-chevron-down"></i> Xem chi tiết
                                </button>
                            </div>
                            
                            <div id="feedback-details-${feedback.feedbackID}" class="feedback-detailed-ratings" style="display: none;">
                                <div class="detailed-rating-title">Đánh giá chi tiết:</div>
                                <div class="detailed-rating-item">
                                    <span class="item-label">Mức độ tổ chức và chuẩn bị của hoạt động:</span>
                                    <div class="item-rating rating-${feedback.q1Organization}">${feedback.q1Organization}/5</div>
                                </div>
                                <div class="detailed-rating-item">
                                    <span class="item-label">Sự rõ ràng trong thông tin truyền đạt trước khi tham gia</span>
                                    <div class="item-rating rating-${feedback.q2Communication}">${feedback.q2Communication}/5</div>
                                </div>
                                <div class="detailed-rating-item">
                                    <span class="item-label">Thái độ và sự hỗ trợ của ban tổ chức:</span>
                                    <div class="item-rating rating-${feedback.q3Support}">${feedback.q3Support}/5</div>
                                </div>
                                <div class="detailed-rating-item">
                                    <span class="item-label">Sự phù hợp giữa nội dung và mong đợi:</span>
                                    <div class="item-rating rating-${feedback.q4Relevance}">${feedback.q4Relevance}/5</div>
                                </div>
                                <div class="detailed-rating-item">
                                    <span class="item-label">Mức độ bạn cảm thấy được chào đón và hòa nhập:</span>
                                    <div class="item-rating rating-${feedback.q5Welcoming}">${feedback.q5Welcoming}/5</div>
                                </div>
                                <div class="detailed-rating-item">
                                    <span class="item-label">Giá trị hoặc kiến thức bạn nhận được từ hoạt động:</span>
                                    <div class="item-rating rating-${feedback.q6Value}">${feedback.q6Value}/5</div>
                                </div>
                                <div class="detailed-rating-item">
                                    <span class="item-label">Sự hợp lý về thời gian, thời lượng, lịch trình:</span>
                                    <div class="item-rating rating-${feedback.q7Timing}">${feedback.q7Timing}/5</div>
                                </div>
                                <div class="detailed-rating-item">
                                    <span class="item-label">Cơ hội để bạn tham gia, đóng góp, thể hiện:</span>
                                    <div class="item-rating rating-${feedback.q8Participation}">${feedback.q8Participation}/5</div>
                                </div>
                                <div class="detailed-rating-item">
                                    <span class="item-label">Mức độ bạn muốn tiếp tục tham gia các hoạt động của CLB trong tương lai:</span>
                                    <div class="item-rating rating-${feedback.q9WillingnessToReturn}">${feedback.q9WillingnessToReturn}/5</div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                    
                    <c:if test="${empty feedbacks}">
                        <div class="no-feedback-message">
                            <i class="fas fa-info-circle"></i>
                            <p>Chưa có đánh giá nào cho sự kiện này.</p>
                        </div>
                    </c:if>
                    
                    <div class="no-matching-filters" style="display: none;">
                        <i class="fas fa-filter"></i>
                        <p>Không có đánh giá nào khớp với bộ lọc đã chọn.</p>
                    </div>
                </div>
            </div>
            
            <div class="feedback-footer">
                <a href="${pageContext.request.contextPath}/event-detail?id=${event.eventID}" class="back-btn">
                    <i class="fas fa-arrow-left"></i> Quay lại chi tiết sự kiện
                </a>
            </div>
        </div>
    </div>
    
    <!-- Footer -->
    <jsp:include page="/view/events-page/footer.jsp" />

    <script src="${pageContext.request.contextPath}/js/viewFeedback.js?v=<%= System.currentTimeMillis() %>"></script>
</body>
</html>
