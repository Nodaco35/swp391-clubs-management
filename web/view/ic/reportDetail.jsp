<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <title>View Detail Periodic Report (IC Officer)</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&family=Montserrat:wght@600&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-LN+7fdVzj6u52u30Kp6M/trliBMCMKTyK833zpbD+pXdCLuTusPj697FH4R/5mcr" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ic-dashboard.css">
        <!-- Bootstrap 5 CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

        <!-- Bootstrap 5 JS (ở cuối body) -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
 <jsp:include page="components/ic-sidebar.jsp" />
<div class="container mt-4">
    <h3 class="mb-4">View Periodic Report Detail</h3>

    <!-- Report Info -->
    <div class="border p-3 mb-4">
        <p><strong>Club:</strong> ${club.clubName}</p>
        <p><strong>Chairman:</strong> ${club.chairmanFullName}</p>
        <p><strong>Report Term:</strong> ${report.term}</p>
        <p><strong>Submission Date:</strong> <fmt:formatDate value="${report.submissionDate}" pattern="dd/MM/yyyy"/></p>
        <p><strong>Submitted By:</strong> ${club.chairmanFullName}</p>
    </div>

    <!-- Member Report -->
    <h5>Member Report</h5>
    <div class="table-responsive mb-4">
        <table class="table table-bordered">
            <thead>
                <tr>
                    <th>Full Name</th>
                    <th>Student Code</th>
                    <th>Role</th>
                    <th>Department</th>
                    <th>Progress Point</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="m" items="${members}">
                    <tr>
                        <td>${m.fullName}</td>
                        <td>${m.studentCode}</td>
                        <td>${m.role}</td>
                        <td>${m.department}</td>
                        <td>${m.progressPoint}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <!-- Event Report -->
    <h5>Event Report</h5>
    <div class="table-responsive">
        <table class="table table-bordered">
            <thead>
                <tr>
                    <th>Event Name</th>
                    <th>Event Date</th>
                    <th>Participant Count</th>
                    <th>Date</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Location</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="e" items="${publicEvents}">
                    <c:forEach var="schedule" items="${e.scheduleList}" varStatus="status">
                        <tr>
                            <c:if test="${status.first}">
                                <td rowspan="${fn:length(e.scheduleList)}">${e.eventName}</td>
                            </c:if>
                            <td><fmt:formatDate value="${schedule.eventDate}" pattern="dd/MM/yyyy"/></td>
                            <c:if test="${status.first}">
                                <td rowspan="${fn:length(e.scheduleList)}">${e.participantCount}</td>
                            </c:if>
                            <td>${schedule.eventDate}</td>
                            <td>${schedule.startTime}</td>
                            <td>${schedule.endTime}</td>
                            <td>${schedule.locationName}</td>
                        </tr>
                    </c:forEach>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <div class="mt-4">
        <a href="${pageContext.request.contextPath}/ic?action=periodicReport" class="btn btn-secondary">
            <i class="fas fa-arrow-left me-1"></i> Back to Reports
        </a>
    </div>
</div>
</body>
</html>
