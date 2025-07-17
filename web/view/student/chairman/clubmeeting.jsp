<%-- 
    Document   : chairman-page
    Created on : Jun 14, 2025, 8:42:59 PM
    Author     : LE VAN THUAN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
    <head>
        <title>Cuộc họp của câu lạc bộ</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">


    </head>
    <body>
        <header class="header">
            <div class="container header-container">
                <div class="logo">
                    <i class="fas fa-users"></i>
                    <span>UniClub</span>
                </div>

                <div class="search-container">
                    <div class="search-box">
                        <form action="${pageContext.request.contextPath}/events-page" method="get">
                            <i class="fas fa-search search-icon"></i>
                            <input type="text" id="searchInput" name="key" placeholder="Tìm kiếm sự kiện..."
                                   class="search-input">
                            <button type="submit" class="search-btn">
                                <i class="fas fa-search"></i>
                            </button>
                        </form>
                    </div>
                </div>

                <nav class="main-nav">
                    <ul>
                        <li>
                            <a href="${pageContext.request.contextPath}/"
                               class="${pageContext.request.servletPath == '/' ? 'active' : ''}">
                                Trang Chủ
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/clubs"
                               class="${fn:contains(pageContext.request.servletPath, '/clubs') ? 'active' : ''}">
                                Câu Lạc Bộ
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/events-page"
                               class="${fn:contains(pageContext.request.servletPath, '/events-page') ? 'active' : ''}">
                                Sự Kiện
                            </a>
                        </li>
                    </ul>
                </nav>

                <div class="auth-buttons">
                    <c:choose>
                        <c:when test="${sessionScope.user != null}">
                            <div class="user-menu" id="userMenu">
                                <a href="${pageContext.request.contextPath}/notification" class="btn btn-outline">
                                    <i class="fa-solid fa-bell"></i>
                                </a>
                                <a href="${pageContext.request.contextPath}/profile?action=myProfile" class="btn btn-outline">
                                    <i class="fa-solid fa-user"></i>
                                </a>
                                <form action="${pageContext.request.contextPath}/logout" method="post">
                                    <input class="btn btn-primary" type="submit" value="Logout">
                                </form>
                                <a href="${pageContext.request.contextPath}/myclub" class="btn btn-primary">MyClub</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="guest-menu" id="guestMenu">
                                <a href="${pageContext.request.contextPath}/login" class="btn btn-outline">Đăng Nhập</a>
                                <a href="${pageContext.request.contextPath}/register" class="btn btn-primary">Đăng Ký</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <button class="mobile-menu-btn" onclick="toggleMobileMenu()">
                        <i class="fas fa-bars"></i>
                    </button>
                </div>
            </div>

            <!-- Mobile Menu -->
            <div class="mobile-menu" id="mobileMenu">
                <nav class="mobile-nav">
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/"
                               class="${pageContext.request.servletPath == '/' ? 'active' : ''}">Trang Chủ</a></li>
                        <li><a href="${pageContext.request.contextPath}/clubs"
                               class="${pageContext.request.servletPath == '/clubs' ? 'active' : ''}">Câu Lạc Bộ</a></li>
                        <li><a href="${pageContext.request.contextPath}/events-page"
                               class="${pageContext.request.servletPath == '/events-page' ? 'active' : ''}">Sự Kiện</a></li>
                    </ul>
                </nav>
            </div>


            <nav class="dashboard-nav">
                <ul>
                    <li>
                        <a href="${pageContext.request.contextPath}/chairman-page/overview"
                           class="nav-item ${currentPath == '/chairman-page/overview' ? 'active' : ''}">
                            <i class="fas fa-tachometer-alt"></i> Tổng quan
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/chairman-page/myclub-events"
                           class="nav-item ${currentPath == '/chairman-page/myclub-events' ? 'active' : ''}">
                            <i class="fas fa-calendar-alt"></i> Sự kiện
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/chairman-page/tasks"
                           class="nav-item ${currentPath == '/chairman-page/tasks' ? 'active' : ''}">
                            <i class="fas fa-clock"></i> Timeline & Công việc
                        </a>
                    </li>

                    <li>
                        <a href="${pageContext.request.contextPath}/chairman-page/clubmeeting"
                           class="nav-item active">
                            <i class="fas fa-clock"></i> Cuộc họp
                        </a>
                    </li>
                </ul>
            </nav>

        </header>
        <main class="dashboard-content">


            <c:if test="${not empty message}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="modal fade show" id="errorModal" tabindex="-1" style="display: block;" aria-labelledby="errorModalLabel" aria-modal="true" role="dialog">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header bg-danger text-white">
                                <h5 class="modal-title" id="errorModalLabel">Lỗi</h5>
                                <button type="button" class="btn-close btn-close-white" onclick="closeErrorModal()" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                ${error}
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-danger" onclick="closeErrorModal()">Đóng</button>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>

            <div class="row mb-4">
                <div class="col-12">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <div class="row align-items-center">
                                <div class="col-md-6">
                                    <form action="${pageContext.request.contextPath}/chairman-page/clubmeeting" method="get">
                                        <input type="hidden" name="action" value="search">
                                        <div class="input-group">
                                            <span class="input-group-text">
                                                <i class="fas fa-search"></i>
                                            </span>
                                            <input type="text" name="search" class="form-control" 
                                                   placeholder="Tìm kiếm theo tiêu đề, liên kết hoặc thời gian..." 
                                                   value="${search}">
                                            <button class="btn btn-outline-primary" type="submit">Tìm kiếm</button>
                                        </div>
                                    </form>
                                </div>
                                <div class="col-md-6 text-end">
                                    <button class="btn btn-primary" onclick="toggleAddForm()">
                                        <i class="fas fa-plus me-2"></i>Tạo cuộc họp
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row mb-4 ${editMeeting != null || showAddForm ? 'd-block' : 'd-none'}" id="meetingForm">
                <div class="col-12">
                    <div class="card shadow-sm">
                        <div class="card-header bg-white">
                            <h5 class="mb-0">
                                <i class="fas fa-calendar-plus me-2"></i>
                                ${editMeeting != null ? 'Sửa cuộc họp' : 'Tạo cuộc họp mới'}
                            </h5>
                        </div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/chairman-page/clubmeeting" method="post" id="formData">
                                <input type="hidden" name="action" id="formAction" value="${editMeeting != null ? 'update' : 'add'}">
                                <input type="hidden" name="meetingId" id="meetingId" value="${editMeeting != null ? editMeeting.clubMeetingID : ''}">

                                <div class="mb-3">
                                    <label for="title" class="form-label">Tiêu đề cuộc họp</label>
                                    <input type="text" name="title" id="title" class="form-control" 
                                           placeholder="Nhập tiêu đề cuộc họp" 
                                           value="${editMeeting != null ? editMeeting.meetingTitle : ''}" required>
                                </div>
                                <div class="mb-3">
                                    <label for="urlMeeting" class="form-label">Liên kết cuộc họp</label>
                                    <input type="url" name="urlMeeting" id="urlMeeting" class="form-control" 
                                           placeholder="Nhập URL Google Meet hoặc Zoom (VD: https://meet.google.com/abc-defg-hij)" 
                                           value="${editMeeting != null ? editMeeting.URLMeeting : ''}" required>
                                </div>
                                <div class="mb-3">
                                    <label for="documentLink" class="form-label">Liên kết tài liệu (Google Drive)</label>
                                    <input type="url" name="documentLink" id="documentLink" class="form-control" 
                                           placeholder="Nhập URL Google Drive (nếu có)" 
                                           value="${editMeeting != null ? editMeeting.document : ''}">
                                </div>
                                <div class="mb-3">
                                    <label for="startedTime" class="form-label">Thời gian bắt đầu</label>
                                    <input type="datetime-local" name="startedTime" id="startedTime" class="form-control" 
                                           value="${editMeeting != null ? editMeeting.startedTime : ''}" required>
                                </div>

                                

                                <div class="mb-3">
                                    <label class="form-label">Các ban tham gia</label>
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" name="selectAll" id="selectAll" 
                                               onchange="toggleParticipants(this)" ${editMeeting != null && editMeeting.participantClubDepartmentIds.size() == departmentMembers.size() ? 'checked' : ''}>
                                        <label class="form-check-label" for="selectAll">Tất cả các ban</label>
                                    </div>
                                    <div id="participantsList" class="${editMeeting != null && editMeeting.participantClubDepartmentIds.size() == departmentMembers.size() ? 'd-none' : 'd-block'}">
                                        <c:forEach var="member" items="${departmentMembers}">
                                            <div class="form-check">
                                                <input class="form-check-input"
                                                       type="checkbox"
                                                       name="participants"
                                                       value="${member.clubDepartmentId}"
                                                       id="participant_${member.clubDepartmentId}"
                                                       <c:if test="${editMeeting != null && editMeeting.participantClubDepartmentIds.contains(member.clubDepartmentId.toString())}">checked</c:if>>
                                                <label class="form-check-label" for="participant_${member.clubDepartmentId}">
                                                    ${member.departmentName}
                                                </label>
                                            </div>
                                        </c:forEach>

                                    </div>
                                </div>
                                <div class="d-flex justify-content-end">
                                    <button type="button" class="btn btn-secondary me-2" onclick="hideForm()">Hủy</button>
                                    <button type="submit" id="submitButton" class="btn btn-primary">
                                        <i class="fas fa-save me-2"></i>
                                        ${editMeeting != null ? 'Cập nhật' : 'Tạo'}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <div class="card shadow-sm">
                        <div class="card-header bg-white">
                            <div class="d-flex justify-content-between align-items-center">
                                <h5 class="mb-0">
                                    <i class="fas fa-calendar-alt me-2"></i>Danh sách cuộc họp
                                </h5>
                                <span class="badge bg-primary">${totalRecords} cuộc họp</span>
                            </div>
                        </div>
                        <div class="card-body p-0">
                            <c:if test="${not empty meetings}">
                                <div class="table-responsive">
                                    <table class="table table-hover mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th scope="col" class="ps-4">Cuộc họp</th>
                                                <th scope="col">Thời gian bắt đầu</th>
                                               
                                                <th scope="col">Liên kết</th>
                                                <th scope="col">Tài liệu</th>
                                                <th scope="col">Trạng thái</th>
                                                <th scope="col" class="text-center">Hành động</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="meeting" items="${meetings}">
                                                <tr>
                                                    <td class="ps-4">
                                                        <div class="d-flex align-items-center">
                                                            <div class="avatar-container me-3">
                                                                <img src="${pageContext.request.contextPath}/${meeting.clubImg}" 
                                                                     alt="Club Logo" class="rounded-circle" style="width: 45px; height: 45px; object-fit: cover;">
                                                            </div>
                                                            <div>
                                                                <h6 class="mb-1 fw-semibold">${meeting.meetingTitle}</h6>
                                                                <small class="text-muted">${meeting.clubName}</small>
                                                                <small class="text-muted d-block">ID: ${meeting.clubMeetingID}</small>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <fmt:formatDate value="${meeting.startedTime}" pattern="dd/MM/yyyy HH:mm" />
                                                    </td>

                                                    
                                                    <td>
                                                        <a href="${meeting.URLMeeting}" target="_blank" class="text-decoration-none">
                                                            <i class="fas fa-link me-1"></i>Tham gia
                                                        </a>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty meeting.document}">
                                                                <a href="${meeting.document}" target="_blank" class="text-decoration-none">
                                                                    <i class="fas fa-file-alt me-1"></i>Xem tài liệu
                                                                </a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-muted">Không có</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <span class="badge ${meeting.startedTime > now ? 'bg-info' : 'bg-secondary'}">
                                                            ${meeting.startedTime > now ? 'Sắp tới' : 'Đã qua'}
                                                        </span>
                                                    </td>
                                                    <td class="text-center">
                                                        <div class="btn-group" role="group">
                                                            <a href="${pageContext.request.contextPath}/chairman-page/clubmeeting?action=edit&meetingId=${meeting.clubMeetingID}" 
                                                               class="btn btn-sm btn-outline-info" title="Sửa">
                                                                <i class="fas fa-edit"></i>
                                                            </a>
                                                            <form action="${pageContext.request.contextPath}/chairman-page/clubmeeting" method="post" style="display:inline;">
                                                                <input type="hidden" name="action" value="delete">
                                                                <input type="hidden" name="meetingId" value="${meeting.clubMeetingID}">

                                                                <button type="submit" class="btn btn-sm btn-outline-danger" 
                                                                        title="Xóa" onclick="return confirm('Bạn có chắc muốn xóa cuộc họp này?')">
                                                                    <i class="fas fa-trash"></i>
                                                                </button>
                                                            </form>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:if>
                            <c:if test="${empty meetings}">
                                <div class="text-center py-5">
                                    <div class="mb-3">
                                        <i class="fas fa-calendar-times fa-3x text-muted"></i>
                                    </div>
                                    <h5 class="text-muted">Không có cuộc họp nào</h5>
                                    <p class="text-muted">Chưa có cuộc họp nào được tạo hoặc không tìm thấy kết quả phù hợp.</p>
                                    <button class="btn btn-primary" onclick="toggleAddForm()">
                                        <i class="fas fa-plus me-2"></i>Tạo cuộc họp đầu tiên
                                    </button>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>

            <c:if test="${totalPages > 1}">
                <div class="row mt-4">
                    <div class="col-12">
                        <nav aria-label="Meetings pagination">
                            <ul class="pagination justify-content-center">
                                <c:if test="${currentPage > 1}">
                                    <li class="page-item">
                                        <a class="page-link" href="?page=${currentPage - 1}&search=${search}" aria-label="Previous">
                                            <span aria-hidden="true">«</span>
                                        </a>
                                    </li>
                                </c:if>
                                <c:set var="startPage" value="${currentPage - 2 > 0 ? currentPage - 2 : 1}" />
                                <c:set var="endPage" value="${currentPage + 2 <= totalPages ? currentPage + 2 : totalPages}" />
                                <c:if test="${startPage > 1}">
                                    <li class="page-item">
                                        <a class="page-link" href="?page=1&search=${search}">1</a>
                                    </li>
                                    <c:if test="${startPage > 2}">
                                        <li class="page-item disabled">
                                            <span class="page-link">...</span>
                                        </li>
                                    </c:if>
                                </c:if>
                                <c:forEach begin="${startPage}" end="${endPage}" var="i">
                                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                                        <c:choose>
                                            <c:when test="${i == currentPage}">
                                                <span class="page-link">${i}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <a class="page-link" href="?page=${i}&search=${search}">${i}</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </li>
                                </c:forEach>
                                <c:if test="${endPage < totalPages}">
                                    <c:if test="${endPage < totalPages - 1}">
                                        <li class="page-item disabled">
                                            <span class="page-link">...</span>
                                        </li>
                                    </c:if>
                                    <li class="page-item">
                                        <a class="page-link" href="?page=${totalPages}&search=${search}">${totalPages}</a>
                                    </li>
                                </c:if>
                                <c:if test="${currentPage < totalPages}">
                                    <li class="page-item">
                                        <a class="page-link" href="?page=${currentPage + 1}&search=${search}" aria-label="Next">
                                            <span aria-hidden="true">»</span>
                                        </a>
                                    </li>
                                </c:if>
                            </ul>
                        </nav>
                        <div class="text-center mt-2">
                            <small class="text-muted">
                                Hiển thị trang ${currentPage} / ${totalPages} - Tổng ${totalRecords} cuộc họp
                            </small>
                        </div>
                    </div>
                </div>
            </c:if>
        </main>


        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                        let currentEditMeetingId = null;

                                        document.addEventListener('DOMContentLoaded', function () {
                                            hideForm();
            <c:if test="${editMeeting != null}">
                                            showEditForm(
                ${editMeeting.clubMeetingID},
                                                    '${editMeeting.meetingTitle}',
                                                    '${editMeeting.URLMeeting}',
                                                    '${editMeeting.document}',
                                                    '<fmt:formatDate value="${editMeeting.startedTime}" pattern="yyyy-MM-dd\'T\'HH:mm"/>'
                                                    );
            </c:if>
            <c:if test="${not empty error}">
                                            document.getElementById('errorModal').style.display = 'block';
            </c:if>
                                        });

                                        function toggleAddForm() {
                                            const form = document.getElementById('meetingForm');
                                            if (form.classList.contains('d-block') && document.getElementById('formAction').value === 'add') {
                                                hideForm();
                                            } else {
                                                showAddForm();
                                            }
                                        }

                                        function toggleEditForm(meetingId, title, urlMeeting, documentLink, startedTime) {
                                            const form = document.getElementById('meetingForm');
                                            if (form.classList.contains('d-block') && currentEditMeetingId === meetingId) {
                                                hideForm();
                                                currentEditMeetingId = null;
                                            } else {
                                                showEditForm(meetingId, title, urlMeeting, documentLink, startedTime);
                                                currentEditMeetingId = meetingId;
                                            }
                                        }

                                        function showAddForm() {
                                            const form = document.getElementById('meetingForm');
                                            const formTitle = document.querySelector('#meetingForm .card-header h5');
                                            const formAction = document.getElementById('formAction');
                                            const submitButton = document.getElementById('submitButton');

                                            form.classList.remove('d-none');
                                            form.classList.add('d-block');
                                            formTitle.textContent = 'Tạo cuộc họp mới';
                                            formAction.value = 'add';
                                            submitButton.textContent = 'Tạo';
                                            document.getElementById('formData').reset();
                                            document.getElementById('meetingId').value = '';
                                            document.getElementById('selectAll').checked = false;
                                            toggleParticipants(document.getElementById('selectAll'));
                                            currentEditMeetingId = null;
                                        }

                                        function showEditForm(meetingId, title, urlMeeting, documentLink, startedTime) {
                                            const form = document.getElementById('meetingForm');
                                            const formTitle = document.querySelector('#meetingForm .card-header h5');
                                            const formAction = document.getElementById('formAction');
                                            const submitButton = document.getElementById('submitButton');

                                            form.classList.remove('d-none');
                                            form.classList.add('d-block');
                                            formTitle.textContent = 'Sửa cuộc họp';
                                            formAction.value = 'update';
                                            submitButton.textContent = 'Cập nhật';

                                            document.getElementById('meetingId').value = meetingId;
                                            document.getElementById('title').value = title;
                                            document.getElementById('urlMeeting').value = urlMeeting;
                                            document.getElementById('documentLink').value = documentLink;
                                            document.getElementById('startedTime').value = startedTime;
                                        }

                                        function hideForm() {
                                            const form = document.getElementById('meetingForm');
                                            form.classList.add('d-none');
                                            form.classList.remove('d-block');
                                            document.getElementById('formData').reset();
                                            currentEditMeetingId = null;
                                        }

                                        function closeErrorModal() {
                                            document.getElementById('errorModal').style.display = 'none';
                                        }

                                        function toggleParticipants(checkbox) {
                                            const participantsList = document.getElementById('participantsList');
                                            if (checkbox.checked) {
                                                participantsList.classList.add('d-none');
                                                participantsList.classList.remove('d-block');
                                                document.querySelectorAll('#participantsList input[type=checkbox]').forEach(cb => cb.checked = false);
                                            } else {
                                                participantsList.classList.remove('d-none');
                                                participantsList.classList.add('d-block');
                                            }
                                        }

                                        document.getElementById('formData').addEventListener('submit', function (e) {
                                            const title = document.getElementById('title').value.trim();
                                            const urlMeeting = document.getElementById('urlMeeting').value.trim();
                                            const documentLink = document.getElementById('documentLink').value.trim();
                                            const startedTime = document.getElementById('startedTime').value;
                                            const selectAll = document.getElementById('selectAll').checked;
                                            const participants = document.querySelectorAll('#participantsList input[type=checkbox]:checked');
                                            if (!title) {
                                                alert('Tiêu đề cuộc họp không được để trống!');
                                                e.preventDefault();
                                                return;
                                            }

                                            if (!urlMeeting) {
                                                alert('Liên kết cuộc họp không được để trống!');
                                                e.preventDefault();
                                                return;
                                            }

                                            if (!startedTime) {
                                                alert('Thời gian bắt đầu không được để trống!');
                                                e.preventDefault();
                                                return;
                                            }

                                            if (!selectAll && participants.length === 0) {
                                                alert('Vui lòng chọn ít nhất một người tham gia hoặc chọn tất cả thành viên!');
                                                e.preventDefault();
                                                return;
                                            }

                                            const googleMeetPattern = /^https:\/\/meet\.google\.com\/[a-z]{3}-[a-z]{4}-[a-z]{3}$/;
                                            const zoomPattern = /^https:\/\/([a-z0-9-]+\.)?zoom\.us\/j\/[0-9]{9,11}(\?pwd=[a-zA-Z0-9]+)?$/;
                                            if (!googleMeetPattern.test(urlMeeting) && !zoomPattern.test(urlMeeting)) {
                                                alert('Liên kết cuộc họp không hợp lệ! Vui lòng nhập URL Google Meet (VD: https://meet.google.com/abc-defg-hij) hoặc Zoom (VD: https://zoom.us/j/1234567890).');
                                                e.preventDefault();
                                                return;
                                            }

                                            if (documentLink && !/^https:\/\/drive\.google\.com\/.*$/.test(documentLink)) {
                                                alert('Liên kết tài liệu không hợp lệ! Vui lòng nhập URL Google Drive hợp lệ.');
                                                e.preventDefault();
                                                return;
                                            }
                                        });
        </script>
    </body>
</html>