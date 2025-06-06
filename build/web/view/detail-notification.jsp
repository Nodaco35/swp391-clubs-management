<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>UniCLUB - Chi Tiết Thông Báo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eventsPage.css"/>
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/detail-notification.css"/>
</head>
<body>
    <jsp:include page="./events-page/header.jsp" />

    <main>
        <div class="container-detail">
            <c:choose>
                <%-- Trường hợp type là null (Tôi là người nhận) --%>
                <c:when test="${type == null}">
                    <div class="notification-header">
                        <img src="${noti.senderAvatar}" alt="Avatar" />
                        <div class="notification-header-info">
                            <h2>Người gửi: ${noti.senderName} (${noti.senderEmail})</h2>
                            <div class="sender-time">${noti.createdDate}</div>
                        </div>
                    </div>
                    <div class="notification-title">
                        ${noti.title}
                    </div>
                    <div class="notification-content">
                        ${noti.content}
                    </div>
                    <div class="notification-actions">
                        <form action="notification">
                            <button class="btn-action">
                                <i class="fa-solid fa-reply"></i> Quay lại
                            </button>
                        </form>
                        <form action="notification?action=delete&id=${noti.notificationID}" method="POST">
                            <button class="btn-action">
                                <i class="fa-solid fa-trash"></i> Xóa
                            </button>
                        </form>
                    </div>
                </c:when>

                <%-- Trường hợp type là 'send' (Tôi là người gửi, hiển thị thông tin người nhận) --%>
                <c:when test="${type == 'send'}">
                    <div class="notification-header">
                        <img src="${noti.receiverAvatar}" alt="Avatar" />
                        <div class="notification-header-info">
                            <h2>Người nhận: ${noti.receiverName} (${noti.receiverEmail})</h2>
                            <div class="sender-time">${noti.createdDate}</div>
                        </div>
                    </div>
                    <div class="notification-title">
                        ${noti.title}
                    </div>
                    <div class="notification-content">
                        ${noti.content}
                    </div>
                    <div class="notification-actions">
                        <form action="notification">
                            <button class="btn-action">
                                <i class="fa-solid fa-reply"></i> Quay lại
                            </button>
                        </form>
                        <form action="notification?action=delete&id=${noti.notificationID}" method="POST">
                            <button class="btn-action">
                                <i class="fa-solid fa-trash"></i> Xóa
                            </button>
                        </form>
                    </div>
                </c:when>
            </c:choose>
        </div>
    </main>

    <jsp:include page="./components/footer.jsp" />
</body>
</html>