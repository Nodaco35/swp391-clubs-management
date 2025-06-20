<%-- 
    Document   : add-event
    Created on : Jun 16, 2025, 12:19:52 AM
    Author     : LE VAN THUAN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>Chairman Page</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>

    <div class="page-header">
        <div class="page-title">
            <h1><i class="fas fa-plus"></i> Thêm sự kiện mới</h1>
            <p>Tạo sự kiện mới cho CLB Công nghệ</p>
        </div>
        <div class="page-actions">
            <a href="${pageContext.request.contextPath}/chairman-page?action=myclub-events" class="btn-secondary">
                <i class="fas fa-arrow-left"></i> Quay lại
            </a>
        </div>
    </div>

    <div class="form-container">
        <form id="eventForm" class="event-form">
            <div class="form-section">
                <h3>Thông tin cơ bản</h3>
                <div class="form-grid">
                    <div class="form-group">
                        <label for="eventName">Tên sự kiện *</label>
                        <input type="text" id="eventName" name="eventName" required>
                    </div>
                    <div class="form-group">
                        <label for="eventType">Loại sự kiện</label>
                        <select id="eventType" name="eventType">
                            <option value="public">Công khai</option>
                            <option value="private">Riêng tư (CLB)</option>
                        </select>
                    </div>
                </div>
                <div class="form-group full-width">
                    <label for="eventDescription">Mô tả sự kiện</label>
                    <textarea id="eventDescription" name="eventDescription" rows="4"
                              placeholder="Mô tả chi tiết về sự kiện..."></textarea>
                </div>
            </div>

            <div class="form-section">
                <h3>Thời gian và địa điểm</h3>
                <div class="form-grid">
                    <div class="form-group">
                        <label for="eventDate">Ngày tổ chức *</label>
                        <input type="date" id="eventDate" name="eventDate" required>
                    </div>
                    <div class="form-group">
                        <label for="eventTime">Thời gian bắt đầu *</label>
                        <input type="time" id="eventTime" name="eventTime" required>
                    </div>
                    <div class="form-group">
                        <label for="eventEndTime">Thời gian kết thúc *</label>
                        <input type="time" id="eventEndTime" name="eventEndTime" required>
                    </div>
                    <div class="form-group">
                        <label for="maxParticipants">Số người tham gia tối đa *</label>
                        <input type="number" id="maxParticipants" name="maxParticipants" min="1" required>
                    </div>
                </div>
                <div class="form-group">
                    <label for="eventLocation">Địa điểm *</label>
                    <select id="eventLocation" name="eventLocation" required
                            onchange="checkVenueAvailability()">
                        <option value="">Chọn địa điểm...</option>
                        <option value="Hội trường A1">Hội trường A1</option>
                        <option value="Hội trường A2">Hội trường A2</option>
                        <option value="Phòng máy tính B1">Phòng máy tính B1</option>
                        <option value="Phòng máy tính B2">Phòng máy tính B2</option>
                        <option value="Phòng hội thảo C1">Phòng hội thảo C1</option>
                        <option value="Phòng hội thảo C2">Phòng hội thảo C2</option>
                        <option value="Sân vận động">Sân vận động</option>
                        <option value="Thư viện tầng 3">Thư viện tầng 3</option>
                        <option value="Phòng đa năng D1">Phòng đa năng D1</option>
                        <option value="Phòng đa năng D2">Phòng đa năng D2</option>
                    </select>
                </div>

                <!-- Venue Availability Display -->
                <div id="venueAvailability" class="venue-availability" style="display: none;">
                    <h4><i class="fas fa-map-marker-alt"></i> Tình trạng địa điểm</h4>
                    <div id="venueStatus" class="venue-status">
                        <!-- Venue availability will be populated by JavaScript -->
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h3>Chương trình sự kiện</h3>
                <div id="agendaContainer">
                    <div class="agenda-item">
                        <input type="time" name="agendaTime[]" placeholder="Thời gian">
                        <input type="text" name="agendaActivity[]" placeholder="Hoạt động">
                        <button type="button" class="btn-remove-agenda" onclick="removeAgendaItem(this)">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                </div>
                <button type="button" class="btn-add-agenda" onclick="addAgendaItem()">
                    <i class="fas fa-plus"></i> Thêm hoạt động
                </button>
            </div>

            <div class="form-actions">
                <button type="button" class="btn-cancel" onclick="window.location.href='${pageContext.request.contextPath}/chairman-page?action=myclub-events'">
                    <i class="fas fa-times"></i> Hủy
                </button>
                <button type="submit" class="btn-submit">
                    <i class="fas fa-save"></i> Lưu sự kiện
                </button>
            </div>
        </form>
    </div>

</html>
