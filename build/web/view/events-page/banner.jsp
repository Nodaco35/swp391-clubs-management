<%--
  Created by IntelliJ IDEA.
  User: thuan
  Date: 5/25/2025
  Time: 11:47 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
	<title>Title</title>
</head>
<body>
<section class="hero" id="heroSection">
	<div class="container">
		<div class="hero-content">
			<h1 class="hero-title">Khám Phá Events</h1>
			<p class="hero-description">
				Tham gia các events để phát triển kỹ năng và tạo nên những kỷ niệm đáng nhớ trong thời sinh
				viên.
			</p>
			<div class="hero-buttons">
				<a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-lg">
					<i class="fas fa-calendar-alt"></i>
					Khám Phá Events Ngay
				</a>
				<%--                        <a href="/register" class="btn btn-outline btn-lg">--%>
				<%--                            <i class="fas fa-users"></i>--%>
				<%--                            Đăng Ký Events Ngay--%>
				<%--                        </a>--%>
			</div>

			<!-- Feature highlights -->
			<div class="features">
				<div class="feature">
					<div class="feature-icon">
						<i class="fas fa-calendar-alt"></i>
					</div>
					<h3>Sự Kiện Đa Dạng</h3>
					<p>Từ hội thảo học thuật đến hoạt động giải trí</p>
				</div>
				<div class="feature">
					<div class="feature-icon">
						<i class="fas fa-users"></i>
					</div>
					<h3>Kết Nối Bạn Bè</h3>
					<p>Gặp gỡ và kết bạn với những người cùng sở thích</p>
				</div>
				<div class="feature">
					<div class="feature-icon">
						<i class="fas fa-star"></i>
					</div>
					<h3>Phát Triển Kỹ Năng</h3>
					<p>Học hỏi và nâng cao khả năng chuyên môn</p>
				</div>
			</div>
		</div>
	</div>
</section>
</body>
</html>
