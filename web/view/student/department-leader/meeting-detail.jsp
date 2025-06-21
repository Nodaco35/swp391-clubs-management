<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết cuộc họp | Trưởng ban</title>    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css">
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <jsp:include page="sidebar.jsp" />

            <!-- Main content -->
            <main class="col-md-9 ms-sm-auto col-lg-12 px-md-4 pt-4">
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/department-meeting?action=list&departmentID=${departmentID}">Cuộc họp</a></li>
                        <li class="breadcrumb-item active" aria-current="page">Chi tiết cuộc họp</li>
                    </ol>
                </nav>

                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h2>Chi tiết cuộc họp</h2>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <div class="btn-group me-2">
                            <a href="${pageContext.request.contextPath}/department-meeting?action=edit&id=${meeting.meetingID}&departmentID=${departmentID}" class="btn btn-sm btn-outline-secondary">
                                <i class="bi bi-pencil"></i> Chỉnh sửa
                            </a>
                            <button type="button" class="btn btn-sm btn-outline-danger" data-bs-toggle="modal" data-bs-target="#deleteModal">
                                <i class="bi bi-trash"></i> Xóa
                            </button>
                        </div>
                    </div>
                </div>

                <div class="alert alert-success ${successMessage == null ? 'd-none' : ''}" role="alert">
                    ${successMessage}
                </div>

                <div class="alert alert-danger ${errorMessage == null ? 'd-none' : ''}" role="alert">
                    ${errorMessage}
                </div>

                <!-- Meeting Details -->
                <div class="row mb-4">
                    <div class="col-md-8">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="card-title mb-0">Thông tin cuộc họp</h5>
                            </div>
                            <div class="card-body">
                                <div class="row mb-3">
                                    <div class="col-md-4 fw-bold">Mã cuộc họp:</div>
                                    <div class="col-md-8">${meeting.meetingID}</div>
                                </div>
                                <div class="row mb-3">
                                    <div class="col-md-4 fw-bold">Ban:</div>
                                    <div class="col-md-8">${meeting.departmentName}</div>
                                </div>
                                <div class="row mb-3">
                                    <div class="col-md-4 fw-bold">Thời gian:</div>
                                    <div class="col-md-8">
                                        <fmt:formatDate value="${meeting.startedTime}" pattern="dd/MM/yyyy HH:mm" />
                                    </div>
                                </div>
                                <div class="row mb-3">
                                    <div class="col-md-4 fw-bold">Link cuộc họp:</div>
                                    <div class="col-md-8">
                                        <a href="${meeting.urlMeeting}" target="_blank">${meeting.urlMeeting}</a>
                                    </div>
                                </div>
                                <div class="row mb-3">
                                    <div class="col-md-4 fw-bold">Số người tham gia:</div>
                                    <div class="col-md-8">${meeting.participantCount}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-4">
                        <div class="card">
                            <div class="card-header d-flex justify-content-between align-items-center">
                                <h5 class="card-title mb-0">Người tham gia</h5>
                                <button type="button" class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#addParticipantModal">
                                    <i class="bi bi-plus"></i> Thêm
                                </button>
                            </div>
                            <div class="card-body p-0">
                                <ul class="list-group list-group-flush">
                                    <c:forEach items="${participants}" var="participant">
                                        <li class="list-group-item d-flex justify-content-between align-items-center">
                                            <div class="d-flex align-items-center">
                                                <img src="${empty participant.userAvatar ? 'https://via.placeholder.com/40' : pageContext.request.contextPath.concat('/img/').concat(participant.userAvatar)}" 
                                                    class="rounded-circle me-2" width="40" height="40" alt="Avatar">
                                                <div>
                                                    <div class="fw-bold">${participant.userName}</div>
                                                    <small class="text-muted">${participant.userEmail}</small>
                                                </div>
                                            </div>
                                            <div class="d-flex align-items-center">
                                                <select class="form-select form-select-sm me-2 participant-status-select" 
                                                        data-participant-id="${participant.participantID}" 
                                                        data-meeting-id="${meeting.meetingID}">
                                                    <option value="Pending" ${participant.status == 'Pending' ? 'selected' : ''}>Chưa phản hồi</option>
                                                    <option value="Confirmed" ${participant.status == 'Confirmed' ? 'selected' : ''}>Đã xác nhận</option>
                                                    <option value="Declined" ${participant.status == 'Declined' ? 'selected' : ''}>Từ chối</option>
                                                    <option value="Attended" ${participant.status == 'Attended' ? 'selected' : ''}>Đã tham dự</option>
                                                </select>
                                                <button type="button" class="btn btn-sm btn-danger remove-participant-btn"
                                                        data-participant-id="${participant.participantID}"
                                                        data-meeting-id="${meeting.meetingID}"
                                                        data-bs-toggle="tooltip" 
                                                        title="Xóa người tham gia">
                                                    <i class="bi bi-x"></i>
                                                </button>
                                            </div>
                                        </li>
                                    </c:forEach>
                                    <c:if test="${empty participants}">
                                        <li class="list-group-item text-center text-muted">Chưa có người tham gia nào</li>
                                    </c:if>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <!-- Delete Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteModalLabel">Xác nhận xóa</h5>
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

    <!-- Add Participant Modal -->
    <div class="modal fade" id="addParticipantModal" tabindex="-1" aria-labelledby="addParticipantModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="addParticipantModalLabel">Thêm người tham gia</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form action="${pageContext.request.contextPath}/department-meeting" method="post" id="addParticipantForm">
                        <input type="hidden" name="action" value="addParticipant">
                        <input type="hidden" name="meetingID" value="${meeting.meetingID}">
                        
                        <div class="mb-3">
                            <label for="userID" class="form-label">Thành viên:</label>
                            <select class="form-select" id="userID" name="userID" required>
                                <option value="">-- Chọn thành viên --</option>
                                <c:forEach items="${departmentMembers}" var="member">
                                    <c:set var="isAlreadyParticipant" value="false" />
                                    <c:forEach items="${participants}" var="participant">
                                        <c:if test="${participant.userID == member.userID}">
                                            <c:set var="isAlreadyParticipant" value="true" />
                                        </c:if>
                                    </c:forEach>
                                    <c:if test="${!isAlreadyParticipant}">
                                        <option value="${member.userID}">${member.userName}</option>
                                    </c:if>
                                </c:forEach>
                            </select>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" form="addParticipantForm" class="btn btn-primary">Thêm</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Forms for participant actions -->
    <form id="updateParticipantStatusForm" action="${pageContext.request.contextPath}/department-meeting" method="post">
        <input type="hidden" name="action" value="updateParticipantStatus">
        <input type="hidden" name="participantID" id="statusParticipantID">
        <input type="hidden" name="meetingID" id="statusMeetingID">
        <input type="hidden" name="status" id="participantStatus">
    </form>
    
    <form id="removeParticipantForm" action="${pageContext.request.contextPath}/department-meeting" method="post">
        <input type="hidden" name="action" value="removeParticipant">
        <input type="hidden" name="participantID" id="removeParticipantID">
        <input type="hidden" name="meetingID" id="removeMeetingID">
    </form>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.7.0.js"></script>
    <script>
        $(document).ready(function() {
            // Clear session messages after 5 seconds
            setTimeout(function() {
                $('.alert').alert('close');
            }, 5000);
            
            // Initialize tooltips
            var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
            var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
                return new bootstrap.Tooltip(tooltipTriggerEl);
            });
            
            // Handle participant status change
            $('.participant-status-select').change(function() {
                var participantID = $(this).data('participant-id');
                var meetingID = $(this).data('meeting-id');
                var status = $(this).val();
                
                $('#statusParticipantID').val(participantID);
                $('#statusMeetingID').val(meetingID);
                $('#participantStatus').val(status);
                $('#updateParticipantStatusForm').submit();
            });
            
            // Handle participant removal
            $('.remove-participant-btn').click(function() {
                var participantID = $(this).data('participant-id');
                var meetingID = $(this).data('meeting-id');
                
                if (confirm('Bạn có chắc chắn muốn xóa người tham gia này?')) {
                    $('#removeParticipantID').val(participantID);
                    $('#removeMeetingID').val(meetingID);
                    $('#removeParticipantForm').submit();
                }
            });
        });
    </script>
</body>
</html>
