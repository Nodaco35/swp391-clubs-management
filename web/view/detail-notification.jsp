<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>UniCLUB - Chi Tiết Thông Báo</title>
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
                padding: 20px;
            }

            footer {
                margin-top: auto;
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

            .container-detail {
                width: 100%;
                max-width: 800px;
                margin: 0 auto;
                padding: 0 1rem;
                background-color: white;
                border-radius: 12px;
                border: 1px solid rgba(40, 59, 72, 0.2);
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
            }

            .notification-header {
                display: flex;
                align-items: center;
                padding: 15px;
                border-bottom: 1px solid rgba(40, 59, 72, 0.2);
            }

            .notification-header img {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                object-fit: cover;
                margin-right: 10px;
            }

            .notification-header-info {
                flex-grow: 1;
            }

            .notification-header-info h2 {
                font-size: 1.2rem;
                font-weight: 600;
                margin: 0;
                color: #222;
            }

            .notification-header-info .sender-time {
                font-size: 0.85rem;
                color: #888;
                margin-top: 3px;
            }

            .notification-title {
                font-size: 1.5rem;
                font-weight: 700;
                text-align: center;
                margin: 20px 0;
                font-family: 'Montserrat', sans-serif;
                color: #000;
            }

            .notification-content {
                padding: 20px;
                font-size: 1rem;
                line-height: 1.8;
                color: #333;
            }

            .notification-content p {
                margin: 10px 0;
            }

            .notification-actions {
                display: flex;
                justify-content: flex-start;
                gap: 10px;
                padding: 15px;
                border-top: 1px solid rgba(40, 59, 72, 0.2);
            }

            .btn-action {
                padding: 8px 16px;
                border: 1px solid #ccc;
                border-radius: 20px;
                background-color: #f5f5f5;
                cursor: pointer;
                font-size: 0.9rem;
                color: #333;
                display: flex;
                align-items: center;
                gap: 5px;
                transition: background-color 0.2s;
            }

            .btn-action:hover {
                background-color: #e0e0e0;
            }
            .btn-delete {
                background: none;
                border: none;
                cursor: pointer;
                font-size: 1rem;
                color: #666;
            }
        </style>
    </head>
    <body>
        <jsp:include page="./components/header.jsp" />

        <main>
            <div class="container-detail">
                <div class="notification-header">
                    <img src="${noti.senderAvatar}" alt="Avatar" />
                    <div class="notification-header-info">
                        <h2>${noti.senderName}(${noti.senderEmail})</h2>
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
                            <i class="fa-solid fa-reply"></i>Quay lại 
                        </button>
                    </form>
                    <form action="notification?action=delete&id=${noti.notificationID}" method="POST">
                        <button class="btn-action">
                            <i class="fa-solid fa-trash"></i>Xóa
                        </button>
                    </form>  
                    <!--                    <button class="btn-action">
                                            <i class="fa-solid fa-forward"></i> 
                                        </button>-->
                </div>
            </div>
        </main>

        <jsp:include page="./components/footer.jsp" />
    </body>
</html>