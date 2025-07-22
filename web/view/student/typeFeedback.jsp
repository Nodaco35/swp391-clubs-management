<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gửi Feedback - ${event.eventName}</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;500;600;700&family=Poppins:wght@300;400;500;600;700&family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/typeFeedback.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/view/events-page/header.jsp" />

    <!-- Toast Container -->
    <div class="toast-container" id="toastContainer"></div>

    <div class="container" style="margin-top: 30px;">
        <div class="feedback-container">
            <div class="feedback-header">
                <h1>Gửi Feedback cho: ${event.eventName}</h1>
            </div>
            
            <!-- Hiển thị bất kỳ thông báo lỗi nào -->
            <c:if test="${not empty error}">
                <div class="feedback-error">
                    ${error}
                </div>
            </c:if>
            
            <!-- Thông tin sự kiện -->
            <div class="event-info">
                <div class="event-details">
                    <h3>${event.eventName}</h3>
                    <div class="event-date">
                        <i class="fas fa-calendar-alt"></i> 
                        <fmt:formatDate value="${event.eventDate}" pattern="dd/MM/yyyy HH:mm"/>
                    </div>
                    <div class="event-location">
                        <i class="fas fa-map-marker-alt"></i> ${event.location.locationName}
                    </div>
                </div>
            </div>
            
            <form action="${pageContext.request.contextPath}/typefeedback" method="POST" novalidate>
                <input type="hidden" name="eventId" value="${event.eventID}">
                
                <!-- Đánh giá tổng quan -->
                <div class="star-rating">
                    <div class="star-rating-title">Đánh giá tổng quan</div>
                    <div class="rating-description">Mức độ hài lòng của bạn về sự kiện này</div>
                    <div class="rating-group">
                        <input type="radio" name="rating" value="5" id="star-5">
                        <label for="star-5" aria-label="5 stars" title="5 sao - Rất hài lòng">★</label>
                        
                        <input type="radio" name="rating" value="4" id="star-4">
                        <label for="star-4" aria-label="4 stars" title="4 sao - Hài lòng">★</label>
                        
                        <input type="radio" name="rating" value="3" id="star-3">
                        <label for="star-3" aria-label="3 stars" title="3 sao - Bình thường">★</label>
                        
                        <input type="radio" name="rating" value="2" id="star-2">
                        <label for="star-2" aria-label="2 stars" title="2 sao - Không hài lòng">★</label>
                        
                        <input type="radio" name="rating" value="1" id="star-1">
                        <label for="star-1" aria-label="1 star" title="1 sao - Rất không hài lòng">★</label>
                    </div>
                </div>
                
                <!-- Đánh giá chi tiết -->
                <div class="detailed-ratings">
                    <div class="star-rating-title">Đánh giá chi tiết</div>
                    <div class="rating-description">Vui lòng đánh giá từng khía cạnh của sự kiện (1 = Rất không hài lòng, 5 = Rất hài lòng)</div>
                    
                    <!-- Câu hỏi 1: Tổ chức -->
                    <div class="rating-item">
                        <div class="rating-question">Mức độ tổ chức và chuẩn bị của hoạt động</div>
                        <div class="rating-legend">
                            <span class="legend-item">Rất không hài lòng</span>
                            <span class="legend-item">Rất hài lòng</span>
                        </div>
                        <div class="rating-options">
                            <input type="radio" name="q1_organization" id="q1-1" value="1">
                            <label for="q1-1">1</label>
                            
                            <input type="radio" name="q1_organization" id="q1-2" value="2">
                            <label for="q1-2">2</label>
                            
                            <input type="radio" name="q1_organization" id="q1-3" value="3">
                            <label for="q1-3">3</label>
                            
                            <input type="radio" name="q1_organization" id="q1-4" value="4">
                            <label for="q1-4">4</label>
                            
                            <input type="radio" name="q1_organization" id="q1-5" value="5">
                            <label for="q1-5">5</label>
                        </div>
                    </div>
                    
                    <!-- Q2: Communication -->
                    <div class="rating-item">
                        <div class="rating-question">Sự rõ ràng trong thông tin truyền đạt trước khi tham gia</div>
                        <div class="rating-options">
                            <input type="radio" name="q2_communication" id="q2-1" value="1" required>
                            <label for="q2-1">1</label>
                            
                            <input type="radio" name="q2_communication" id="q2-2" value="2">
                            <label for="q2-2">2</label>
                            
                            <input type="radio" name="q2_communication" id="q2-3" value="3">
                            <label for="q2-3">3</label>
                            
                            <input type="radio" name="q2_communication" id="q2-4" value="4">
                            <label for="q2-4">4</label>
                            
                            <input type="radio" name="q2_communication" id="q2-5" value="5">
                            <label for="q2-5">5</label>
                        </div>
                    </div>
                    
                    <!-- Q3: Support -->
                    <div class="rating-item">
                        <div class="rating-question">Thái độ và sự hỗ trợ của ban tổ chức</div>
                        <div class="rating-options">
                            <input type="radio" name="q3_support" id="q3-1" value="1" required>
                            <label for="q3-1">1</label>
                            
                            <input type="radio" name="q3_support" id="q3-2" value="2">
                            <label for="q3-2">2</label>
                            
                            <input type="radio" name="q3_support" id="q3-3" value="3">
                            <label for="q3-3">3</label>
                            
                            <input type="radio" name="q3_support" id="q3-4" value="4">
                            <label for="q3-4">4</label>
                            
                            <input type="radio" name="q3_support" id="q3-5" value="5">
                            <label for="q3-5">5</label>
                        </div>
                    </div>
                    
                    <!-- Q4: Relevance -->
                    <div class="rating-item">
                        <div class="rating-question">Sự phù hợp giữa nội dung và mong đợi</div>
                        <div class="rating-options">
                            <input type="radio" name="q4_relevance" id="q4-1" value="1">
                            <label for="q4-1">1</label>
                            
                            <input type="radio" name="q4_relevance" id="q4-2" value="2">
                            <label for="q4-2">2</label>
                            
                            <input type="radio" name="q4_relevance" id="q4-3" value="3">
                            <label for="q4-3">3</label>
                            
                            <input type="radio" name="q4_relevance" id="q4-4" value="4">
                            <label for="q4-4">4</label>
                            
                            <input type="radio" name="q4_relevance" id="q4-5" value="5">
                            <label for="q4-5">5</label>
                        </div>
                    </div>
                    
                    <!-- Q5: Welcoming -->
                    <div class="rating-item">
                        <div class="rating-question">Mức độ bạn cảm thấy được chào đón và hòa nhập</div>
                        <div class="rating-options">
                            <input type="radio" name="q5_welcoming" id="q5-1" value="1">
                            <label for="q5-1">1</label>
                            
                            <input type="radio" name="q5_welcoming" id="q5-2" value="2">
                            <label for="q5-2">2</label>
                            
                            <input type="radio" name="q5_welcoming" id="q5-3" value="3">
                            <label for="q5-3">3</label>
                            
                            <input type="radio" name="q5_welcoming" id="q5-4" value="4">
                            <label for="q5-4">4</label>
                            
                            <input type="radio" name="q5_welcoming" id="q5-5" value="5">
                            <label for="q5-5">5</label>
                        </div>
                    </div>
                    
                    <!-- Q6: Value -->
                    <div class="rating-item">
                        <div class="rating-question">Giá trị hoặc kiến thức bạn nhận được từ hoạt động</div>
                        <div class="rating-options">
                            <input type="radio" name="q6_value" id="q6-1" value="1">
                            <label for="q6-1">1</label>
                            
                            <input type="radio" name="q6_value" id="q6-2" value="2">
                            <label for="q6-2">2</label>
                            
                            <input type="radio" name="q6_value" id="q6-3" value="3">
                            <label for="q6-3">3</label>
                            
                            <input type="radio" name="q6_value" id="q6-4" value="4">
                            <label for="q6-4">4</label>
                            
                            <input type="radio" name="q6_value" id="q6-5" value="5">
                            <label for="q6-5">5</label>
                        </div>
                    </div>
                    
                    <!-- Q7: Timing -->
                    <div class="rating-item">
                        <div class="rating-question">Sự hợp lý về thời gian, thời lượng, lịch trình</div>
                        <div class="rating-options">
                            <input type="radio" name="q7_timing" id="q7-1" value="1">
                            <label for="q7-1">1</label>
                            
                            <input type="radio" name="q7_timing" id="q7-2" value="2">
                            <label for="q7-2">2</label>
                            
                            <input type="radio" name="q7_timing" id="q7-3" value="3">
                            <label for="q7-3">3</label>
                            
                            <input type="radio" name="q7_timing" id="q7-4" value="4">
                            <label for="q7-4">4</label>
                            
                            <input type="radio" name="q7_timing" id="q7-5" value="5">
                            <label for="q7-5">5</label>
                        </div>
                    </div>
                    
                    <!-- Q8: Participation -->
                    <div class="rating-item">
                        <div class="rating-question">Cơ hội để bạn tham gia, đóng góp, thể hiện</div>
                        <div class="rating-options">
                            <input type="radio" name="q8_participation" id="q8-1" value="1" required>
                            <label for="q8-1">1</label>
                            
                            <input type="radio" name="q8_participation" id="q8-2" value="2">
                            <label for="q8-2">2</label>
                            
                            <input type="radio" name="q8_participation" id="q8-3" value="3">
                            <label for="q8-3">3</label>
                            
                            <input type="radio" name="q8_participation" id="q8-4" value="4">
                            <label for="q8-4">4</label>
                            
                            <input type="radio" name="q8_participation" id="q8-5" value="5">
                            <label for="q8-5">5</label>
                        </div>
                    </div>
                    
                    <!-- Q9: Willingness to Return -->
                    <div class="rating-item">
                        <div class="rating-question">Mức độ bạn muốn tiếp tục tham gia các hoạt động của CLB trong tương lai</div>
                        <div class="rating-options">
                            <input type="radio" name="q9_willingnessToReturn" id="q9-1" value="1" required>
                            <label for="q9-1">1</label>
                            
                            <input type="radio" name="q9_willingnessToReturn" id="q9-2" value="2">
                            <label for="q9-2">2</label>
                            
                            <input type="radio" name="q9_willingnessToReturn" id="q9-3" value="3">
                            <label for="q9-3">3</label>
                            
                            <input type="radio" name="q9_willingnessToReturn" id="q9-4" value="4">
                            <label for="q9-4">4</label>
                            
                            <input type="radio" name="q9_willingnessToReturn" id="q9-5" value="5">
                            <label for="q9-5">5</label>
                        </div>
                    </div>
                </div>
                

                <div class="feedback-comments">
                    <div class="star-rating-title">Nội dung feedback</div>
                    <textarea name="content" placeholder="Vui lòng chia sẻ đánh giá hoặc góp ý của bạn về sự kiện..."></textarea>
                </div>

                <div class="anonymous-checkbox">
                    <input type="checkbox" id="anonymous" name="isAnonymous" value="true">
                    <label for="anonymous">Gửi feedback ẩn danh</label>
                </div>
                
                <!-- Nút gửi -->
                <button type="submit" class="btn-submit">Gửi Feedback</button>
            </form>
        </div>
    </div>

    <!-- Footer -->
    <jsp:include page="/view/events-page/footer.jsp" />

    <!-- Thiết lập các biến JavaScript cần thiết -->
    <script type="text/javascript">
        // Thiết lập các biến từ JSP để sử dụng trong JavaScript
        var contextPath = '${pageContext.request.contextPath}';
        var eventId = '${eventId}';
        <c:choose>
            <c:when test="${not empty success}">
                var success = "${success}";
            </c:when>
            <c:otherwise>
                var success = null;
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${not empty error}">
                var error = "${error}";
            </c:when>
            <c:otherwise>
                var error = null;
            </c:otherwise>
        </c:choose>
    </script>
    <script src="${pageContext.request.contextPath}/js/typeFeedback.js?v=<%= System.currentTimeMillis() %>"></script>
</body>
</html>
