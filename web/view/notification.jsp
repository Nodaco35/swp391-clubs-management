<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>UniCLUB - Thông báo</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

        <!-- Font Awesome for icons -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            html, body {
                height: 100%;
                margin: 0;
                padding: 0;
                display: flex;
                flex-direction: column;
            }

            main {
                flex: 1;
                background-color: #f5f5f5;
                padding: 10px;
            }

            footer {
                margin-top: auto;
            }
            footer {
                padding: 0;
                background-color: #1d1d1d;
                color: white;
                text-align: center;
            }
            body {
                font-family: 'Roboto', sans-serif;
                background-color: var(--background);
                color: var(--foreground);
                line-height: 1.6;
                margin: 0;
                padding: 0;
            }
            main{
                background-color: #f5f5f5;
                margin: 0;
                padding: 10px;
            }

            .container-thongbao {
                width: 100%;
                max-width: 800px;
                margin: 0 auto; /* Căn giữa container */
                padding: 0 1rem;
            }

            .tabs {
                display: flex;
                justify-content: left; /* Căn giữa các tab */
            }

            .tabs a {
                border: solid black 1px;
                padding: 10px 20px;
                text-decoration: none;
                color: var(--foreground);
                border-radius: 15px;
                margin-right: 10px;
                margin-bottom: 10px;
                font-size: 0.875rem;
                font-weight: 500;
                transition: all 0.2s;
                background-color: rgba(216, 215, 206, 0.95);
            }

            .tabs a.active {
                border-bottom: 2px solid var(--primary);
                color: var(--primary);
            }

            .section {
                margin-bottom: 0;
            }

            .section h2 {
                font-size: 1.5rem;
                margin-bottom: 10px;
                text-align: left; /* Căn giữa tiêu đề */
                font-family: 'Montserrat', sans-serif;
                font-weight: 700;
            }

            .notification {
                display: flex;
                align-items: center;
                justify-content: space-between;
                background-color: white;
                margin-bottom: 2px;
                padding: 0 20px 0px 20px; 
                border-radius: 12px;
                border: 1px solid rgba(40, 59, 72, 0.2);
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
                gap: 10px;
                cursor: pointer;
                transition: transform 0.2s, background-color 0.2s;
            }

            .notification.unread {
                background-color: #f8fafc; /* Slightly lighter background for unread */
                border-left: 4px solid var(--primary, #007bff); /* Blue border on left for emphasis */
                transform: scale(1.02); /* Slight scale for prominence */
            }

            .notification.read .notification-middle * {
                opacity: 0.6; /* Làm mờ văn bản */
                font-weight: 400; /* Loại bỏ strong, đặt font-weight nhẹ */
            }

            .notification-left img {
                width: 48px;
                height: 48px;
                border-radius: 50%;
                object-fit: cover;
            }

            .notification-middle {
                flex-grow: 1;
                display: flex;
                flex-direction: column;
                justify-content: center;
                overflow: hidden;
            }

            .sender-name {
                font-weight: 600;
                font-size: 0.9rem;
                color: #222;
            }

            .email-title {
                font-weight: 500;
                color: #000;
                font-size: 0.95rem;
                margin: 3px 0;
            }

            .notification-time {
                font-size: 0.75rem;
                color: #888;
            }

            .notification-right {
                display: flex;
                flex-direction: column;
                align-items: flex-end;
                gap: 5px;
                min-width: 40px;
            }

            .unread-dot {
                width: 10px;
                height: 10px;
                background-color: var(--primary, blue);
                border-radius: 50%;
            }

            .btn-delete {
                background: none;
                border: none;
                cursor: pointer;
                font-size: 1rem;
                color: #666;
            }
            .btn-delete:hover {
                color: #007bff; /* Đổi màu hover thành xanh để nhất quán */
            }
        </style>
    </head>
    <body>
        <jsp:include page="./components/header.jsp" />

        <main>
            <div class="container-thongbao">
                <div class="tabs">
                    <a href="notification">Tất cả</a>
                    <a href="notification?action=unreadNotifications">Chưa đọc</a>
                </div>

                <c:forEach var="noti" items="${notifications}">
                    <div class="notification ${noti.status == 'UNREAD' ? 'unread' : 'read'}" id="noti-${noti.notificationID}" onclick="showDetail('${noti.notificationID}', '${noti.title}', '${noti.senderName}', '${noti.createdDate}', '${noti.content.replace('\'', '\\\'')}')">
                        <div class="notification-left">
                            <img src="${noti.senderAvatar}" alt="Avatar" />
                        </div>

                        <div class="notification-middle">
                            <div class="sender-name">${noti.senderName}(${noti.senderEmail})</div>
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

                <div id="detailModal" class="modal" style="display:none;">
                    <div class="modal-content">
                        <span class="close" onclick="closeModal()">×</span>
                        <h3 id="detailTitle"></h3>
                        <p><strong>Người gửi:</strong> <span id="detailSender"></span></p>
                        <p><strong>Thời gian:</strong> <span id="detailTime"></span></p>
                        <div id="detailContent" style="margin-top: 10px;"></div>
                    </div>
                </div>
            </div>
        </main>

        <!-- JavaScript -->
        <script src="${pageContext.request.contextPath}/js/script.js">
        </script>
        <jsp:include page="./components/footer.jsp" />
    </body>
</html>