<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Lịch sử hoạt động</title>
        <link rel="stylesheet" href="css/activity-history.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eventsPage.css"/>
        <link rel="stylesheet" href="./css/profileStyle.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    </head>
    <body>
        <jsp:include page="/view/events-page/header.jsp" />
        <jsp:include page="../components/profileSlidebar.jsp" />
        <div style="margin-bottom: 50px"></div>
        <div class="main-content">
            <div class="container">
                <div class="section-box">
                    <h2>Đơn xin tham gia CLB</h2>
                    <c:forEach var="app" items="${clubApplications}">
                        <div class="club-card">
                            <div class="club-logo">${app.clubName.substring(0,1)}</div>
                            <div class="club-info">
                                <div class="club-name">${app.clubName}</div>
                                <div class="club-meta">
                                    Ngày nộp đơn: ${app.submitDate}
                                </div>
                            </div>
                            <div class="club-actions">
                                <span class="badge
                                      ${app.status == 'APPROVED' ? 'badge-active' : 
                                        app.status == 'PENDING' ? 'badge-pending' : 
                                        'badge-rejected'}">
                                          ${app.status}
                                      </span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <div class="section-box">
                        <h2>Sự kiện đã tham gia</h2>

                        <!-- Tabs lọc có thể thêm JS nếu cần -->

                        <table class="table-events">
                            <thead>
                                <tr>
                                    <th>Tên sự kiện</th>
                                    <th>CLB tổ chức</th>
                                    <th>Ngày tổ chức</th>
                                    <th>Địa điểm</th>
                                    <th>Trạng thái tham gia</th>
                                    <th>Trạng thái sự kiện</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="event" items="${events}">
                                    <tr data-status="${event.participationStatus.toLowerCase()}">
                                        <td>${event.eventName}</td>
                                        <td>${event.clubName}</td>
                                        <td><fmt:formatDate value="${event.eventDate}" pattern="dd/MM/yyyy"/></td>
                                        <td>${event.location}</td>

                                        <td>
                                            <span class="badge
                                                  <c:choose>
                                                      <c:when test="${event.participationStatus == 'REGISTERED'}">badge-registered</c:when>
                                                      <c:when test="${event.participationStatus == 'ATTENDED'}">badge-attended</c:when>
                                                      <c:when test="${event.participationStatus == 'ABSENT'}">badge-absent</c:when>
                                                  </c:choose>">
                                                <c:choose>
                                                    <c:when test="${event.participationStatus == 'REGISTERED'}">Đã đăng ký</c:when>
                                                    <c:when test="${event.participationStatus == 'ATTENDED'}">Đã tham dự</c:when>
                                                    <c:when test="${event.participationStatus == 'ABSENT'}">Vắng mặt</c:when>
                                                </c:choose>
                                            </span>
                                        </td>

                                        <td>
                                            <span class="badge ${event.eventStatus == 'Completed' ? 'badge-completed' : 'badge-pending'}">
                                                ${event.eventStatus == 'Completed' ? 'Đã hoàn thành' : 'Sắp diễn ra'}
                                            </span>
                                        </td>

                                        <td>
                                            <div class="table-actions">
                                                <button class="btn btn-outline btn-icon" onclick="window.location.href = '${pageContext.request.contextPath}/event-detail?id=${event.eventID}'"><i class="fas fa-eye"></i></button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div style="margin-bottom: 50px"></div>
                </div>
            </div>
        </body>
    </html>
