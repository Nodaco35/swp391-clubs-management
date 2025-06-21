<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Department Leader - Dashboard</title>      <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
</head>
<body>    
    <!-- Header Include -->
    <jsp:include page="../../components/header.jsp"></jsp:include>
    
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar Include -->
            <jsp:include page="sidebar.jsp">
                <jsp:param name="active" value="dashboard" />
            </jsp:include>
            
            <!-- Main Content -->
            <main class="col-md-9 ms-sm-auto col-lg-12 px-md-4 py-4">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-4 border-bottom">
                    <h1 class="h2"><i class="fas fa-tachometer-alt me-2"></i>Dashboard - Trưởng Ban</h1>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <a href="${pageContext.request.contextPath}/department-task?action=list&clubID=${clubID}" class="btn btn-primary me-2">
                            <i class="fas fa-list me-1"></i> Xem tất cả nhiệm vụ
                        </a>
                        <a href="${pageContext.request.contextPath}/department-meeting?action=list&clubID=${clubID}" class="btn btn-success">
                            <i class="fas fa-calendar me-1"></i> Xem cuộc họp
                        </a>
                    </div>
                </div>
                
                <!-- Statistics Cards -->
                <div class="row mb-5">
                    <div class="col-md-3">
                        <div class="card text-center bg-primary text-white h-100">
                            <div class="card-body">
                                <div class="display-6">
                                    <i class="fas fa-clipboard-list"></i>
                                </div>
                                <h3 class="card-title">${totalTasks}</h3>
                                <p class="card-text">Tổng số nhiệm vụ</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card text-center bg-warning text-white h-100">
                            <div class="card-body">
                                <div class="display-6">
                                    <i class="fas fa-hourglass-half"></i>
                                </div>
                                <h3 class="card-title">${pendingTasks}</h3>
                                <p class="card-text">Đang thực hiện</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card text-center bg-info text-white h-100">
                            <div class="card-body">
                                <div class="display-6">
                                    <i class="fas fa-users"></i>
                                </div>
                                <h3 class="card-title">${totalMembers}</h3>
                                <p class="card-text">Thành viên ban</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card text-center bg-success text-white h-100">
                            <div class="card-body">
                                <div class="display-6">
                                    <i class="fas fa-calendar-check"></i>
                                </div>
                                <h3 class="card-title">${totalMeetings}</h3>
                                <p class="card-text">Cuộc họp</p>
                            </div>
                        </div>
                    </div>
                </div>
                  <!-- Recent Tasks Section -->
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h4 class="mb-0"><i class="fas fa-tasks me-2"></i>Nhiệm vụ sắp tới hạn</h4>
                    <a href="${pageContext.request.contextPath}/department-task?action=list&clubID=${clubID}" class="btn btn-sm btn-outline-primary">
                        <i class="fas fa-external-link-alt me-1"></i> Xem tất cả
                    </a>
                </div>
                
                <div class="row">
                    <c:choose>
                        <c:when test="${not empty upcomingDeadlines}">
                            <c:forEach var="task" items="${upcomingDeadlines}" varStatus="loop">
                                <c:if test="${loop.index < 6}">
                                    <div class="col-md-6 mb-4">
                                        <div class="card h-100">
                                            <div class="card-body">
                                                <div class="d-flex justify-content-between align-items-center mb-2">
                                                    <h5 class="card-title text-truncate">${task.taskName}</h5>
                                                    <span class="badge 
                                                        <c:choose>
                                                            <c:when test="${task.status == 'ToDo'}">bg-secondary</c:when>
                                                            <c:when test="${task.status == 'Processing'}">bg-warning</c:when>
                                                            <c:when test="${task.status == 'Review'}">bg-info</c:when>
                                                            <c:when test="${task.status == 'Approved'}">bg-success</c:when>
                                                            <c:otherwise>bg-light text-dark</c:otherwise>
                                                        </c:choose>">
                                                        <c:choose>
                                                            <c:when test="${task.status == 'ToDo'}">Cần làm</c:when>
                                                            <c:when test="${task.status == 'Processing'}">Đang xử lý</c:when>
                                                            <c:when test="${task.status == 'Review'}">Cần xem xét</c:when>
                                                            <c:when test="${task.status == 'Approved'}">Đã hoàn thành</c:when>
                                                            <c:otherwise>${task.status}</c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                </div>
                                                
                                                <p class="card-text text-muted">${task.description}</p>
                                                
                                                <div class="d-flex justify-content-between align-items-center">
                                                    <small class="text-muted">
                                                        <i class="far fa-calendar-alt me-1"></i> 
                                                        Hạn: <fmt:formatDate value="${task.dueDate}" pattern="dd/MM/yyyy" />
                                                    </small>
                                                    <a href="${pageContext.request.contextPath}/department-task?action=view&id=${task.taskAssignmentDepartmentID}" 
                                                       class="btn btn-sm btn-outline-primary">
                                                        Chi tiết <i class="fas fa-arrow-right ms-1"></i>
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="col-12">
                                <div class="alert alert-info d-flex align-items-center" role="alert">
                                    <i class="fas fa-info-circle me-3 fa-2x"></i>
                                    <div>
                                        <h5 class="mb-1">Không có nhiệm vụ sắp tới hạn</h5>
                                        <p class="mb-0">Hiện tại không có nhiệm vụ nào sắp tới hạn cho ban của bạn.</p>
                                    </div>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <!-- Recent Meetings Section -->
                <div class="d-flex justify-content-between align-items-center mb-4 mt-5">
                    <h4 class="mb-0"><i class="fas fa-calendar-alt me-2"></i>Cuộc họp sắp tới</h4>
                    <a href="${pageContext.request.contextPath}/department-meeting?action=list&clubID=${clubID}" class="btn btn-sm btn-outline-success">
                        <i class="fas fa-external-link-alt me-1"></i> Xem tất cả
                    </a>
                </div>
                
                <div class="row">
                    <c:choose>
                        <c:when test="${not empty recentMeetings}">
                            <c:forEach var="meeting" items="${recentMeetings}" varStatus="loop">
                                <c:if test="${loop.index < 4}">
                                    <div class="col-md-6 mb-3">
                                        <div class="card">
                                            <div class="card-body">
                                                <h6 class="card-title">${meeting.title}</h6>
                                                <p class="card-text text-muted small">${meeting.description}</p>
                                                <div class="d-flex justify-content-between align-items-center">
                                                    <small class="text-muted">
                                                        <i class="fas fa-clock me-1"></i>
                                                        <fmt:formatDate value="${meeting.meetingDate}" pattern="dd/MM/yyyy HH:mm" />
                                                    </small>
                                                    <a href="${pageContext.request.contextPath}/department-meeting?action=view&id=${meeting.meetingID}" 
                                                       class="btn btn-sm btn-outline-success">
                                                        Chi tiết
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="col-12">
                                <div class="alert alert-secondary d-flex align-items-center" role="alert">
                                    <i class="fas fa-calendar-times me-3 fa-2x"></i>
                                    <div>
                                        <h5 class="mb-1">Không có cuộc họp</h5>
                                        <p class="mb-0">Hiện tại không có cuộc họp nào sắp diễn ra.</p>
                                    </div>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </main>
        </div>
    </div>
    
    <!-- Footer Include -->
    <jsp:include page="../../components/footer.jsp"></jsp:include>
    
    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
