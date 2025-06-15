<%-- 
    Document   : overview
    Created on : Jun 15, 2025, 11:26:39 AM
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
    <body>

        <!-- Overview Tab -->
        <div id="overview-tab" class="tab-content active">
            <!-- Club Statistics -->
            <section class="stats-section">
                <h2>Thống kê tổng quan</h2>
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-icon departments">
                            <i class="fas fa-sitemap"></i>
                        </div>
                        <div class="stat-content">
                            <h3 id="totalDepartments">${totalDepartments}</h3>
                            <p>Các ban</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon members">
                            <i class="fas fa-users"></i>
                        </div>
                        <div class="stat-content">
                            <h3 id="totalMembers">${totalMembers}</h3>
                            <p>Thành viên</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon events">
                            <i class="fas fa-calendar"></i>
                        </div>
                        <div class="stat-content">
                            <h3 id="totalEvents">${totalEvents}</h3>
                            <p>Sự kiện</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon active-tasks">
                            <i class="fas fa-tasks"></i>
                        </div>
                        <div class="stat-content">
                            <h3 id="activeTasks">8</h3>
                            <p>Nhiệm vụ đang thực hiện</p>
                        </div>
                    </div>
                </div>
            </section>



        </div>


    </body>
</html>
