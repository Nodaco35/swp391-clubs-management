<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<section class="hero">
    <div class="container">
        <div class="hero-content">
            <h1 class="hero-title">Khám Phá Câu Lạc Bộ Sinh Viên</h1>
            <p class="hero-description">Tham gia các câu lạc bộ để phát triển kỹ năng, mở rộng mạng lưới và tạo nên những kỷ niệm đáng nhớ trong thời sinh viên.</p>
            <div class="hero-buttons">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-lg">Khám Phá Câu Lạc Bộ Ngay</a>
                <c:if test="${sessionScope.user == null}">
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-outline btn-lg">Đăng Ký Ngay</a>
                </c:if>
            </div>
        </div>
    </div>
</section>