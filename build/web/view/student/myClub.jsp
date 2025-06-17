<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Câu Lạc Bộ Của Tôi - UniClub</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/myClub.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/view/events-page/header.jsp" />
    <a href="${pageContext.request.contextPath}/" class="back-btn">
        <i class="fas fa-arrow-left"></i>
    </a>

    <div class="container">        <div class="actions-section">
            <h3>Quản lý Form</h3>
            <a href="${pageContext.request.contextPath}/formManagement?clubId=${userClub.clubID}" class="action-btn">
                <i class="fas fa-list-alt"></i> Quản Lý Các Form
            </a>
            <a href="${pageContext.request.contextPath}/formBuilder?clubId=${userClub.clubID}" class="action-btn">
                <i class="fas fa-plus"></i> Tạo Form Mới
            </a>
        </div>
    </div>
</body>
</html>
