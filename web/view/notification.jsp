<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>UniCLUB - Thông báo</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eventsPage.css">

        <!-- Font Awesome for icons -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/notification.css"/>


    </head>
    <body>
        <jsp:include page="./events-page/header.jsp" />
        <c:if test="${not empty error}">
            <div class="error-overlay" id="errorOverlay"></div>
            <div class="error-popup" id="errorPopup">
                <h2>Lỗi</h2>
                <p id="errorMessage"><c:out value="${error}" /></p>
                <button class="ok-btn" onclick="hideError()">OK</button>
            </div>
            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    document.getElementById("errorPopup").style.display = "block";
                    document.getElementById("errorOverlay").style.display = "block";
                });

                function hideError() {
                    document.getElementById("errorPopup").style.display = "none";
                    document.getElementById("errorOverlay").style.display = "none";
                }
            </script>
        </c:if>

        <main>

            <div class="container-thongbao">
                <!-- Search Bar -->
                <div class="container-thongbao">
                    <div class="search-box">
                        <form action="notification?action=search" method="POST">
                            <i class="fas fa-search search-icon"></i>
                            <input type="text" id="searchInput" name="key" placeholder="Tìm kiếm thông báo..."
                                    class="search-input" value="${param.key != null ? param.key : ''}">
                            <button type="submit" class="search-btn">
                                <i class="fas fa-search"></i>
                            </button>
                        </form>
                    </div>
                </div>
                <div class="tabs">
                    <a href="notification">Tất cả</a>
                    <a href="notification?action=unreadNotifications">Chưa đọc</a>
                    <a href="notification?action=highNotifications">Quan trọng</a>
                    <a href="notification?action=sendNotifications">Đã gửi</a>

                </div>

                <%-- Notification list --%>
                <c:choose>
                    <c:when test="${empty notifications}">
                        <div class="no-events">
                            Không tìm thấy thông báo nào
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="noti" items="${notifications}">
                            <div class="notification ${noti.status == 'UNREAD' ? 'unread' : 'read'}" id="noti-${noti.notificationID}">
                                <div class="notification-left">
                                    <!-- Hiển thị avatar dựa trên type -->
                                    <img src="${type == 'send' ? noti.receiverAvatar : noti.senderAvatar}" alt="Avatar" />
                                </div>

                                <div class="notification-middle">
                                    <!-- Hiển thị tên và email dựa trên type -->
                                    <div class="sender-name">
                                        ${type == 'send' ? noti.receiverName : noti.senderName}
                                        (${type == 'send' ? noti.receiverEmail : noti.senderEmail})
                                    </div>
                                    <div class="email-title">${noti.title}</div>
                                    <div class="notification-time">${noti.createdDate}</div>
                                </div>

                                <div class="notification-right">
                                    <form action="notification?action=detail&id=${noti.notificationID}" method="POST">
                                        <button class="btn-delete">
                                            <i class="fa-solid fa-eye"></i>
                                        </button>
                                    </form>
                                    <form action="notification?action=delete&id=${noti.notificationID}" method="POST">
                                        <button class="btn-delete">
                                            <i class="fa-solid fa-trash"></i>
                                        </button>
                                    </form>  
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>

                <!-- Icon bút chì -->
                <div class="pencil-icon" onclick="showNotificationForm()">✏️</div>

                <!-- Lớp phủ nền mờ -->
                <div class="overlay" onclick="hideNotificationForm()"></div>

                <!-- Form thông báo -->
                <div class="notification-form" id="notificationForm">
                    <h2>Gửi thông báo</h2>
                    <form id="notificationFormElement" action="notification?action=sentNotification" method="POST">
                        <input type="hidden" name="senderID" value="${user.userID}">
                        <input type="text" name="receiverEmail" placeholder="Email người nhận">
                        <input type="text" id="title" name="title" placeholder="Tiêu đề" required>
                        <textarea id="content" name="content" placeholder="Nội dung thông báo" required></textarea>
                        <div class="buttons">
                            <button type="button" class="cancel-btn" onclick="hideNotificationForm()">Hủy</button>
                            <button type="submit" class="send-btn">Gửi</button>
                        </div>
                    </form>
                </div>
            </div>   
        </main>

        <script>
            function showNotificationForm() {
                document.getElementById('notificationForm').style.display = 'block';
                document.querySelector('.overlay').style.display = 'block';
                document.getElementById('title').focus();
            }

            function hideNotificationForm() {
                document.getElementById('notificationForm').style.display = 'none';
                document.querySelector('.overlay').style.display = 'none';
                document.getElementById('notificationFormElement').reset();
            }
        </script>
        <script src="${pageContext.request.contextPath}/js/script.js"></script>

        <jsp:include page="./components/footer.jsp" />
    </body>
</html>