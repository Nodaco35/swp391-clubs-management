<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý cuộc họp | Trưởng ban</title>    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/fullcalendar/5.10.2/main.min.css">
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <jsp:include page="sidebar.jsp" />

            <!-- Main content -->
            <main class="col-md-9 ms-sm-auto col-lg-12 px-md-4 pt-4">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h2>Quản lý cuộc họp</h2>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <a href="${pageContext.request.contextPath}/department-meeting?action=add&departmentID=${departmentID}" class="btn btn-sm btn-primary">
                            <i class="bi bi-plus"></i> Tạo cuộc họp mới
                        </a>
                    </div>
                </div>

                <div class="alert alert-success ${successMessage == null ? 'd-none' : ''}" role="alert">
                    ${successMessage}
                </div>

                <div class="alert alert-danger ${errorMessage == null ? 'd-none' : ''}" role="alert">
                    ${errorMessage}
                </div>

                <!-- Calendar View -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="mb-0">Lịch cuộc họp</h5>
                    </div>
                    <div class="card-body">
                        <div id="calendar"></div>
                    </div>
                </div>

                <!-- Meeting List -->
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Danh sách cuộc họp</h5>
                    </div>
                    <div class="card-body">
                        <table id="meetingTable" class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Thời gian</th>
                                    <th>URL Cuộc họp</th>
                                    <th>Số người tham gia</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${meetings}" var="meeting">
                                    <tr>
                                        <td>Meeting-${meeting.meetingID}</td>
                                        <td>
                                            <fmt:formatDate value="${meeting.startedTime}" pattern="dd/MM/yyyy HH:mm" />
                                        </td>
                                        <td>
                                            <a href="${meeting.urlMeeting}" target="_blank">${meeting.urlMeeting}</a>
                                        </td>
                                        <td>${meeting.participantCount}</td>
                                        <td>
                                            <div class="btn-group">
                                                <a href="${pageContext.request.contextPath}/department-meeting?action=view&id=${meeting.meetingID}&departmentID=${departmentID}" class="btn btn-sm btn-info">
                                                    <i class="bi bi-eye"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/department-meeting?action=edit&id=${meeting.meetingID}&departmentID=${departmentID}" class="btn btn-sm btn-warning">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <button type="button" class="btn btn-sm btn-danger" data-bs-toggle="modal" data-bs-target="#deleteModal${meeting.meetingID}">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            </div>
                                            
                                            <!-- Delete Modal for each meeting -->
                                            <div class="modal fade" id="deleteModal${meeting.meetingID}" tabindex="-1" aria-labelledby="deleteModalLabel${meeting.meetingID}" aria-hidden="true">
                                                <div class="modal-dialog">
                                                    <div class="modal-content">
                                                        <div class="modal-header">
                                                            <h5 class="modal-title" id="deleteModalLabel${meeting.meetingID}">Xác nhận xóa</h5>
                                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                        </div>
                                                        <div class="modal-body">
                                                            Bạn có chắc chắn muốn xóa cuộc họp này không? Hành động này không thể hoàn tác.
                                                        </div>
                                                        <div class="modal-footer">
                                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                                            <a href="${pageContext.request.contextPath}/department-meeting?action=delete&id=${meeting.meetingID}&departmentID=${departmentID}" class="btn btn-danger">Xóa</a>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.7.0.js"></script>
    <script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.6/js/dataTables.bootstrap5.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/fullcalendar/5.10.2/main.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/fullcalendar/5.10.2/locales/vi.min.js"></script>
    <script>
        $(document).ready(function() {
            // Initialize DataTable
            $('#meetingTable').DataTable({
                language: {
                    url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/vi.json',
                },
                order: [[1, 'desc']] // Sort by date
            });
            
            // Initialize FullCalendar
            var calendarEl = document.getElementById('calendar');
            var calendar = new FullCalendar.Calendar(calendarEl, {
                initialView: 'dayGridMonth',
                headerToolbar: {
                    left: 'prev,next today',
                    center: 'title',
                    right: 'dayGridMonth,timeGridWeek,timeGridDay'
                },
                locale: 'vi',
                events: [
                    <c:forEach items="${meetings}" var="meeting" varStatus="loop">
                    {
                        id: '${meeting.meetingID}',
                        title: 'Cuộc họp #${meeting.meetingID}',
                        start: '<fmt:formatDate value="${meeting.startedTime}" pattern="yyyy-MM-dd\'T\'HH:mm:ss" />',
                        url: '${pageContext.request.contextPath}/department-meeting?action=view&id=${meeting.meetingID}&departmentID=${departmentID}',
                        backgroundColor: '#0d6efd'
                    }<c:if test="${!loop.last}">,</c:if>
                    </c:forEach>
                ],
                eventClick: function(info) {
                    if (info.event.url) {
                        window.location.href = info.event.url;
                        info.jsEvent.preventDefault(); // prevent default browser action
                    }
                }
            });
            calendar.render();
            
            // Clear session messages after 5 seconds
            setTimeout(function() {
                $('.alert').alert('close');
            }, 5000);
        });
    </script>
</body>
</html>
