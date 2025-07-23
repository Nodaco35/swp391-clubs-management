<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Dashboard Trưởng Ban - ${dashboard.departmentName}</title>

        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <!-- Custom CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
    </head>
    <body>
        <!-- Mobile Menu Toggle -->
        <%@ include file="components/mobile-menu.jsp" %>
        
        <!-- Sidebar -->
        <div class="department-leader-container">
            <c:set var="activePage" value="dashboard" />
            <%@ include file="components/sidebar.jsp" %>

            <!-- Main Content -->
            <main class="main-content">

                <!-- Header -->
                <header class="header">
                    <div class="header-left">
                        <h1>Dashboard Trưởng Ban</h1>
                        <p class="breadcrumb">
                            <span>${dashboard.clubName}</span> 
                            <i class="fas fa-chevron-right"></i> 
                            <span>${dashboard.departmentName}</span>
                        </p>
                    </div>

                    <div class="header-right">
                        <div class="current-time" id="currentTime"></div>
                    </div>
                </header>

                <!-- Dashboard Cards -->
                <div class="dashboard-cards">
                    <!-- Department Info Card -->
                    <div class="card info-card">
                        <div class="card-header">
                            <h3>Thông tin ban</h3>
                            <i class="fas fa-info-circle"></i>
                        </div>
                        <div class="card-body">
                            <div class="info-item">
                                <span class="label">Tên ban:</span>
                                <span class="value">${dashboard.departmentName}</span>
                            </div>                        <div class="info-item">
                                <span class="label">Thuộc CLB:</span>
                                <span class="value">${dashboard.clubName}</span>
                            </div>
                            <div class="info-item">
                                <span class="label">ID Ban:</span>
                                <span class="value">#${dashboard.departmentId}</span>
                            </div>
                        </div>
                    </div>

                    <!-- Members Stats Card -->
                    <div class="card stats-card">
                        <div class="card-header">
                            <h3>Thành viên</h3>
                            <i class="fas fa-users"></i>
                        </div>
                        <div class="card-body">
                            <div class="stat-item primary">
                                <div class="stat-number">${dashboard.totalMembers}</div>
                                <div class="stat-label">Tổng thành viên</div>
                            </div>
                            <div class="stat-item success">
                                <div class="stat-number">${dashboard.activeMembers}</div>
                                <div class="stat-label">Thành viên hoạt động</div>
                            </div>
                            <div class="stat-item warning">
                                <div class="stat-number">${dashboard.totalMembers - dashboard.activeMembers}</div>
                                <div class="stat-label">Không hoạt động</div>
                            </div>
                        </div>
                    </div>

                    <!-- Tasks Stats Card -->
                    <div class="card stats-card">
                        <div class="card-header">
                            <h3>Công việc</h3>
                            <i class="fas fa-tasks"></i>
                        </div>
                        <div class="card-body">
                            <div class="stat-item primary">
                                <div class="stat-number">${dashboard.totalTasks}</div>
                                <div class="stat-label">Tổng công việc</div>
                            </div>                        <div class="stat-item success">
                                <div class="stat-number">${dashboard.doneTasks}</div>
                                <div class="stat-label">Đã hoàn thành</div>
                            </div>
                            <div class="stat-item danger">
                                <div class="stat-number">${dashboard.inProgressTasks}</div>
                                <div class="stat-label">Đang thực hiện</div>
                            </div>
                        </div>
                    </div>

                    <!-- Events Stats Card -->
                    <div class="card stats-card">
                        <div class="card-header">
                            <h3>Sự kiện</h3>
                            <i class="fas fa-calendar-check"></i>
                        </div>
                        <div class="card-body">
                            <div class="stat-item primary">
                                <div class="stat-number">${dashboard.totalEvents}</div>
                                <div class="stat-label">Tổng sự kiện</div>
                            </div>
                            <div class="stat-item info">
                                <div class="stat-number">${dashboard.upcomingEvents}</div>
                                <div class="stat-label">Sắp tới</div>
                            </div>
                            <div class="stat-item success">
                                <div class="stat-number">${dashboard.completedEvents}</div>
                                <div class="stat-label">Đã hoàn thành</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Charts Section -->
                <div class="charts-section">
                    <!-- Task Progress Chart -->
                    <div class="card chart-card">
                        <div class="card-header">
                            <h3>Tiến độ công việc</h3>
                            <i class="fas fa-chart-pie"></i>
                        </div>
                        <div class="card-body">
                            <div class="chart-container">
                                <canvas id="taskProgressChart"></canvas>
                            </div>                        <div class="chart-legend">
                                <div class="legend-item">
                                    <span class="legend-color completed"></span>
                                    <span>Hoàn thành (${dashboard.doneTasks})</span>
                                </div>
                                <div class="legend-item">
                                    <span class="legend-color in-progress"></span>
                                    <span>Đang thực hiện (${dashboard.inProgressTasks})</span>
                                </div>
                                <div class="legend-item">
                                    <span class="legend-color overdue"></span>
                                    <span>To Do (${dashboard.todoTasks})</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Member Activity Chart -->
                    <div class="card chart-card">
                        <div class="card-header">
                            <h3>Tình trạng thành viên</h3>
                            <i class="fas fa-chart-donut"></i>
                        </div>
                        <div class="card-body">
                            <div class="chart-container">
                                <canvas id="memberActivityChart"></canvas>
                            </div>
                            <div class="chart-legend">
                                <div class="legend-item">
                                    <span class="legend-color active"></span>
                                    <span>Hoạt động (${dashboard.activeMembers})</span>
                                </div>
                                <div class="legend-item">
                                    <span class="legend-color inactive"></span>
                                    <span>Không hoạt động (${dashboard.totalMembers - dashboard.activeMembers})</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Recent Activities -->
                <div class="recent-activities">
                    <div class="card">
                        <div class="card-header">
                            <h3>Hoạt động gần đây</h3>
                            <i class="fas fa-clock"></i>
                        </div>
                        <div class="card-body">
                            <div class="activity-list">
                                <div class="activity-item">
                                    <div class="activity-icon success">
                                        <i class="fas fa-check"></i>
                                    </div>                                <div class="activity-content">
                                        <div class="activity-title">Có ${dashboard.doneTasks} công việc đã hoàn thành</div>
                                        <div class="activity-time">Cập nhật lúc: <span id="lastUpdate"></span></div>
                                    </div>
                                </div>

                                <c:if test="${dashboard.todoTasks > 0}">
                                    <div class="activity-item">
                                        <div class="activity-icon danger">
                                            <i class="fas fa-exclamation-triangle"></i>
                                        </div>
                                        <div class="activity-content">
                                            <div class="activity-title">Có ${dashboard.todoTasks} công việc cần thực hiện</div>
                                            <div class="activity-time">Cần xử lý</div>
                                        </div>
                                    </div>
                                </c:if>

                                <div class="activity-item">
                                    <div class="activity-icon info">
                                        <i class="fas fa-users"></i>
                                    </div>
                                    <div class="activity-content">
                                        <div class="activity-title">Ban hiện có ${dashboard.totalMembers} thành viên</div>
                                        <div class="activity-time">${dashboard.activeMembers} thành viên đang hoạt động</div>
                                    </div>
                                </div>

                                <c:if test="${dashboard.upcomingEvents > 0}">
                                    <div class="activity-item">
                                        <div class="activity-icon warning">
                                            <i class="fas fa-calendar"></i>
                                        </div>
                                        <div class="activity-content">
                                            <div class="activity-title">Có ${dashboard.upcomingEvents} sự kiện sắp tới</div>
                                            <div class="activity-time">Cần chuẩn bị</div>
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>        
            </main>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

        <!-- Chart.js -->
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

        <!-- Dashboard data for JavaScript -->
        <script>
            // Pass dashboard data to JavaScript
            window.dashboardData = {
                doneTasks: ${dashboard.doneTasks},
                inProgressTasks: ${dashboard.inProgressTasks},
                todoTasks: ${dashboard.todoTasks},
                activeMembers: ${dashboard.activeMembers},
                totalMembers: ${dashboard.totalMembers}
            };
        </script>    <!-- Custom Dashboard JS -->
        <script src="${pageContext.request.contextPath}/js/department-dashboard.js"></script>
    </body>
</html>
