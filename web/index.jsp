
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>UniClub - Quản Lý Câu Lạc Bộ</title>
    <meta name="description" content="Nền tảng quản lý câu lạc bộ sinh viên">
    
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;500;600;700&family=Poppins:wght@300;400;500;600;700&family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    
    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <jsp:include page="view/components/header.jsp" />
    
    <main>
        <c:if test="${sessionScope.user==null}">
        <jsp:include page="view/homepage/hero-section.jsp" />
        </c:if>
        <jsp:include page="view/homepage/featured-clubs.jsp" />
        <jsp:include page="view/homepage/upcoming-events.jsp" />
        <jsp:include page="view/homepage/statistics.jsp" />
    </main>
    
    <jsp:include page="view/components/footer.jsp" />
    
    <!-- JavaScript -->
    <script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
