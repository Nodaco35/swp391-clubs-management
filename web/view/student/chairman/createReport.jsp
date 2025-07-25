<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

<jsp:include page="components/sidebar.jsp" />


<div class="container mt-4">
    <!-- Kỳ báo cáo -->
    <div class="mb-4">
        <h4 class="fw-bold">Tạo báo cáo kỳ: <span class="text-primary">${semesterID}</span></h4>
    </div>

    <form method="get" action="${pageContext.request.contextPath}/chairman-page/reports?action=submitReport">
        <!-- Báo cáo thành viên -->
        <div class="mb-5">
            <h5 class="mb-3">Báo cáo thành viên</h5>
            <div class="table-responsive">
                <table class="table table-borderless align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Họ và tên</th>
                            <th>Mã sinh viên</th>
                            <th>Vai trò</th>
                            <th>Ban</th>
                            <th>Điểm rèn luyện</th>
                            <th>Hành động</th> <!-- Thêm cột nút Lưu -->
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="m" items="${members}" varStatus="i">
                            <tr>
                        <form action="${pageContext.request.contextPath}/chairman-page/reports" method="get">
                            <input type="hidden" name="action" value="editPointFromReport" />
                            <input type="hidden" name="userID" value="${m.userID}" />
                            <input type="hidden" name="termID" value="${semesterID}" />
                            <td>${m.fullName}</td>
                            <td>${m.studentCode}</td>
                            <td>${m.role}</td>
                            <td>${m.department}</td>
                            <td>
                                <input type="number"
                                       name="points"
                                       min="0"
                                       max="100"
                                       pattern="[0-9]*"
                                       class="form-control"
                                       value="${m.progressPoint}" required>
                            </td>
                            <td>
                                <button type="submit" class="btn btn-success btn-sm">
                                    <i class="fas fa-check"></i> Lưu
                                </button>
                            </td>
                        </form>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>


        <!-- Báo cáo sự kiện -->
        <div>
            <h5 class="mb-3">Báo cáo sự kiện</h5>
            <div class="table-responsive">
                <table class="table align-middle" style="border: 1px">
                    <thead class="table-light">
                        <tr>
                            <th>Tên sự kiện</th>
                            <th>Ngày tổ chức</th>
                            <th>Số người tham gia</th>
                            <th>Ngày</th>
                            <th>Thời gian bắt đầu</th>
                            <th>Thời gian kết thúc</th>
                            <th>Địa điểm</th>
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


        </div>

        <!-- Nút hành động -->
        <div class="mt-4 mb-5">
            <a href="${pageContext.request.contextPath}/chairman-page/reports" class="btn btn-secondary me-2">
                <i class="fas fa-arrow-left me-1"></i> Quay lại
            </a>
            <a href="${pageContext.request.contextPath}/chairman-page/reports?action=submitReport" 
               class="btn btn-success"><i class="fas fa-paper-plane me-1"></i> Nộp báo cáo
            </a>

        </div>
    </form>
</div>
