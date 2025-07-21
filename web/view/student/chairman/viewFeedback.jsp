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
    <meta name="eventId" content="${param.eventId}">
</head>
<body>
    <jsp:include page="/view/events-page/header.jsp" />
    
    <div class="container">
        <div class="feedback-container">
            <div class="feedback-header">
                <h1>Feedback - ${event.eventName}</h1>
                <p>Xem phản hồi đánh giá của người tham gia về sự kiện</p>
            </div>

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
                        <!-- Hiển thị trạng thái loading -->
                        <div id="chartLoadingState" class="chart-loading-state">
                            <div class="loading-spinner">
                                <i class="fas fa-spinner fa-spin"></i>
                            </div>
                            <div class="loading-text">Đang tải dữ liệu biểu đồ...</div>
                        </div>
                        
                        <!-- Hiển thị trạng thái lỗi -->
                        <div id="chartErrorState" class="chart-error-state" style="display: none;">
                            <div class="error-icon">
                                <i class="fas fa-exclamation-triangle"></i>
                            </div>
                            <div class="error-message">Không thể tải dữ liệu biểu đồ</div>
                            <button class="retry-button" onclick="retryLoadData()">Thử lại</button>
                        </div>

                        <!-- Container cho biểu đồ -->
                        <div id="chartContainer" style="display: none;">
                            <canvas id="ratingDistributionChart"></canvas>
                        </div>
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
                        <!-- Sử dụng script type="application/json" cho dữ liệu chi tiết -->
                        <script type="application/json" id="detailedRatingData">
                        {
                            "q1Organization": ${statistics.q1Organization != null ? statistics.q1Organization : 0},
                            "q2Communication": ${statistics.q2Communication != null ? statistics.q2Communication : 0},
                            "q3Support": ${statistics.q3Support != null ? statistics.q3Support : 0},
                            "q4Relevance": ${statistics.q4Relevance != null ? statistics.q4Relevance : 0},
                            "q5Welcoming": ${statistics.q5Welcoming != null ? statistics.q5Welcoming : 0},
                            "q6Value": ${statistics.q6Value != null ? statistics.q6Value : 0},
                            "q7Timing": ${statistics.q7Timing != null ? statistics.q7Timing : 0},
                            "q8Participation": ${statistics.q8Participation != null ? statistics.q8Participation : 0},
                            "q9WillingnessToReturn": ${statistics.q9WillingnessToReturn != null ? statistics.q9WillingnessToReturn : 0}
                        }
                        </script>
                        
                        <canvas id="detailedRatingChart"></canvas>
                        
                        <!-- Script để đọc dữ liệu chi tiết -->
                        <script>
                            try {
                                const detailedJsonData = document.getElementById('detailedRatingData').textContent.trim();
                                window.detailedRatingData = JSON.parse(detailedJsonData);
                            } catch (error) {
                                console.error("Lỗi khi đọc dữ liệu chi tiết từ script JSON:", error);
                                window.detailedRatingData = {}; 
                            }
                        </script>
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
    <script src="${pageContext.request.contextPath}/js/feedbackRatingDistributionChart.js?v=<%= System.currentTimeMillis() %>"></script>

    <!-- Khởi tạo phương pháp JSON API theo tham số URL -->
    <script>
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.has('useJsonApi')) {
            const useJsonApi = urlParams.get('useJsonApi') === 'true';
            localStorage.setItem('useJsonApi', useJsonApi ? 'true' : 'false');
        }
        
        // Nếu chưa có trạng thái lưu trữ, mặc định sử dụng JSON API
        if (localStorage.getItem('useJsonApi') === null) {
            localStorage.setItem('useJsonApi', 'true');
        }
    </script>
</body>
</html>
