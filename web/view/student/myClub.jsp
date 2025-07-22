<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Câu Lạc Bộ Của Tôi - UniClub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/myClub.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eventsPage.css">
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body>
<jsp:include page="/view/events-page/header.jsp" />
<a href="${pageContext.request.contextPath}/" class="back-btn">
    <i class="fas fa-arrow-left"></i>
</a>
<div class="flex min-h-screen">
    <!-- Sidebar -->
    <aside class="sidebar w-64 bg-white p-6 fixed h-screen shadow-lg transition-transform duration-300">
        <h2 class="text-2xl font-bold text-gray-900 mb-6">Menu</h2>
        <ul class="space-y-3">
            <li><a href="#club-summaries" class="text-blue-600 hover:text-white block py-3 px-4 rounded-lg"><i class="fas fa-users"></i> Câu Lạc Bộ Của Tôi</a></li>
            <c:set var="isLeader" value="false" />
            <c:set var="isMember" value="false" />
            <c:forEach items="${userclubs}" var="uc">
                <c:if test="${not empty uc and uc.roleID == 1}">
                    <c:set var="isLeader" value="true" />
                </c:if>
                <c:if test="${not empty uc and (uc.roleID == 1 || uc.roleID == 3 || uc.roleID == 4)}">
                    <c:set var="isMember" value="true" />
                </c:if>
            </c:forEach>
            <c:if test="${isMember}">
                <li><a href="#documents" class="text-blue-600 hover:text-white block py-3 px-4 rounded-lg"><i class="fas fa-file-alt"></i> Quản Lý Tài Liệu Chung</a></li>
            </c:if>
            <li><a href="#notifications" class="text-blue-600 hover:text-white block py-3 px-4 rounded-lg"><i class="fas fa-bell"></i> Thông Báo Gần Đây</a></li>
            <li>
                <a href="${pageContext.request.contextPath}/financial/cart-member-contribution"
                   class="text-blue-600 hover:text-white block py-3 px-4 rounded-lg relative">
                    <i class="fas fa-chart-pie"></i>
                    Thanh toán phí thành viên
                    <c:if test="${hasPendingInvoices > 0}">
                                <span class="absolute top-1 right-1 bg-red-600 text-white text-xs font-bold px-2 py-0.5 rounded-full animate-pulse">
                                        ${hasPendingInvoices}
                                </span>
                    </c:if>
                </a>
            </li>
            <li><a href="#calendar" class="text-blue-600 hover:text-white block py-3 px-4 rounded-lg"><i class="fas fa-tasks"></i> Nhiệm Vụ Của Ban</a></li>
            <li><a href="#upcoming-departmentmeeting" class="text-blue-600 hover:text-white block py-3 px-4 rounded-lg"><i class="fas fa-calendar-check"></i> Cuộc Họp Sắp Tới Của Ban</a></li>
            <li><a href="#upcoming-events" class="text-blue-600 hover:text-white block py-3 px-4 rounded-lg"><i class="fas fa-calendar-alt"></i> Sự Kiện Sắp Tới</a></li>
            <c:if test="${isLeader || isDepartment}">
                <li><a href="#upcoming-clubmeeting" class="text-blue-600 hover:text-white block py-3 px-4 rounded-lg"><i class="fas fa-users-cog"></i> Cuộc Họp Sắp Tới Của CLB</a></li>
            </c:if>
            <li><a href="#actions" class="text-blue-600 hover:text-white block py-3 px-4 rounded-lg"><i class="fas fa-file-alt"></i> Quản Lý Form Và Hoạt Động Tuyển Quân</a></li>
        </ul>
    </aside>
    <main class="ml-64 flex-1 p-8 bg-gray-50">
        <c:if test="${not empty message}">
            <div class="alert alert-success alert-dismissible fade show rounded-lg shadow-md border border-green-200" role="alert">
                <i class="fas fa-check-circle mr-2"></i> ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="modal fade show" id="errorModal" tabindex="-1" style="display: block;" aria-labelledby="errorModalLabel" aria-modal="true" role="dialog">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header bg-red-600 text-white">
                            <h5 class="modal-title" id="errorModalLabel">Lỗi</h5>
                            <button type="button" class="btn-close btn-close-white" onclick="closeErrorModal()" aria-label="Close"></button>
                        </div>
                        <div class="modal-body text-gray-800">
                                ${error}
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn bg-red-600 text-white hover:bg-red-700 rounded-lg px-4 py-2" onclick="closeErrorModal()">Đóng</button>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <div class="container mx-auto px-4 py-8">
            <section class="mb-12">
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                    <div class="card flex items-center p-6">
                        <i class="fas fa-calendar text-blue-600 text-2xl mr-3"></i>
                        <div>
                            <p class="text-sm text-gray-500">Số nhiệm vụ của ban</p>
                            <p class="text-xl font-bold text-gray-900">${countTodoLists} <a href="#calendar" class="text-blue-600 hover:text-blue-800"><i class="fa-solid fa-eye"></i></a></p>
                        </div>
                    </div>
                    <c:if test="${isLeader || isDepartment}">
                        <div class="card flex items-center p-6">
                            <i class="fas fa-users text-blue-600 text-2xl mr-3"></i>
                            <div>
                                <p class="text-sm text-gray-500">Số cuộc họp của CLB</p>
                                <p class="text-xl font-bold text-gray-900">${countUpcomingMeeting} <a href="#upcoming-clubmeeting" class="text-blue-600 hover:text-blue-800"><i class="fa-solid fa-eye"></i></a></p>
                            </div>
                        </div>
                    </c:if>
                    <div class="card flex items-center p-6">
                        <i class="fas fa-users text-blue-600 text-2xl mr-3"></i>
                        <div>
                            <p class="text-sm text-gray-500">Số cuộc họp trong ban</p>
                            <p class="text-xl font-bold text-gray-900">${countUpcomingDepartmentMeeting} <a href="#upcoming-departmentmeeting" class="text-blue-600 hover:text-blue-800"><i class="fa-solid fa-eye"></i></a></p>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Club Summaries -->
            <section id="club-summaries" class="mb-12">
                <h2 class="text-3xl font-bold text-gray-900 mb-6">Câu Lạc Bộ Của Tôi</h2>
                <c:choose>
                    <c:when test="${empty userclubs}">
                        <div class="card text-center py-10">
                            <p class="text-gray-500 text-lg">Hiện chưa tham gia câu lạc bộ nào.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            <c:forEach items="${userclubs}" var="uc">
                                <div class="card p-6">
                                    <div class="flex items-center gap-6">
                                        <img src="${pageContext.request.contextPath}${uc.clubImg != null ? uc.clubImg : '/images/default-club.jpg'}" alt="${uc.clubName}" class="w-20 h-20 rounded-full object-cover border-2 border-blue-200 shadow-sm">
                                        <div>
                                            <h3 class="text-xl font-semibold text-gray-900">${uc.clubName}</h3>
                                            <p class="text-sm text-gray-500">Vai trò: ${uc.roleName}</p>
                                            <p class="text-sm text-gray-500">Ban: ${uc.departmentName}</p>
                                            <p class="text-sm text-gray-500">Ngày tham gia: ${uc.joinDate}</p>
                                        </div>
                                    </div>
                                    <c:choose>
                                        <c:when test="${uc.roleID == 1}">
                                            <a href="${pageContext.request.contextPath}/chairman-page/overview" class="mt-4 inline-block text-white bg-blue-600 hover:bg-blue-700 px-5 py-2.5 rounded-lg transition">MyClub Dashboard</a>
                                        </c:when>
                                        <c:when test="${uc.roleID == 3}">
                                            <a href="${pageContext.request.contextPath}/department-dashboard?clubID=${uc.clubID}" class="mt-4 inline-block text-white bg-blue-600 hover:bg-blue-700 px-5 py-2.5 rounded-lg transition">MyClub Dashboard</a>
                                        </c:when>
                                        <c:when test="${uc.roleID == 4}">
                                            <a href="${pageContext.request.contextPath}/myclub" class="mt-4 inline-block text-white bg-blue-600 hover:bg-blue-700 px-5 py-2.5 rounded-lg transition">MyClub Dashboard</a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="#" class="mt-4 inline-block text-white bg-blue-600 hover:bg-blue-700 px-5 py-2.5 rounded-lg transition">MyClub Dashboard</a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>

            <!-- Document Management -->
            <c:if test="${isMember}">
                <section id="documents" class="mb-12">
                    <h2 class="text-3xl font-bold text-gray-900 mb-6">Quản Lý Tài Liệu Chung</h2>
                    <div class="card p-6">
                        <h3 class="text-lg font-semibold text-gray-900 mb-3">Chọn Câu Lạc Bộ</h3>
                        <p class="text-sm text-gray-500 mb-4">Vui lòng chọn câu lạc bộ để xem hoặc quản lý tài liệu:</p>
                        <div class="relative inline-block w-full md:w-80 mb-6">
                            <select id="documentClubSelector" class="block appearance-none w-full bg-white border border-gray-300 hover:border-gray-400 px-4 py-3 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                                <option value="">-- Chọn câu lạc bộ --</option>
                                <c:forEach items="${userclubs}" var="club">
                                    <option value="${club.clubID}" ${club.clubID == selectedClubID ? 'selected' : ''}>${club.clubName}</option>
                                </c:forEach>
                            </select>
                            <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-3 text-gray-600">
                                <i class="fas fa-chevron-down"></i>
                            </div>
                        </div>
                        <div id="documentManagementSection" style="display: ${not empty selectedClubID ? 'block' : 'none'};">
                            <c:if test="${isLeader || isDepartment}">
                                <a href="#" id="addDocumentBtn" class="inline-block text-white bg-blue-600 hover:bg-blue-700 px-6 py-3 rounded-lg transition flex items-center gap-2 mb-4">
                                    <i class="fas fa-plus"></i> Thêm Tài Liệu Mới
                                </a>
                            </c:if>
                            <div id="documentForm" class="mb-6" style="display: ${not empty showDocumentForm && showDocumentForm ? 'block' : 'none'};">
                                <form action="${pageContext.request.contextPath}/myclub" method="post">
                                    <input type="hidden" name="action" id="documentAction" value="createDocument">
                                    <input type="hidden" name="documentID" id="documentID" value="${selectedDocumentID}">
                                    <input type="hidden" name="showDocumentForm" value="true">
                                    <div class="mb-4">
                                        <label for="documentName" class="block text-sm font-medium text-gray-700">Tên tài liệu</label>
                                        <input type="text" name="documentName" id="documentName" class="mt-1 block w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-blue-500 focus:border-blue-500" value="${fn:escapeXml(selectedDocumentName)}" required>
                                    </div>
                                    <div class="mb-4">
                                        <label for="description" class="block text-sm font-medium text-gray-700">Mô tả</label>
                                        <textarea name="description" id="description" class="mt-1 block w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-blue-500 focus:border-blue-500" rows="4">${fn:escapeXml(selectedDescription)}</textarea>
                                    </div>
                                    <div class="mb-4">
                                        <label for="documentURL" class="block text-sm font-medium text-gray-700">Link tài liệu (Google Drive)</label>
                                        <input type="text" name="documentURL" id="documentURL" class="mt-1 block w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-blue-500 focus:border-blue-500" value="${selectedDocumentURL}" required>
                                    </div>
                                    <div class="mb-4">
                                        <label for="documentType" class="block text-sm font-medium text-gray-700">Loại tài liệu</label>
                                        <select name="documentType" id="documentType" class="block w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-blue-500 focus:border-blue-500" required>
                                            <option value="Meeting" ${selectedDocumentType == 'Meeting' ? 'selected' : ''}>Meeting</option>
                                            <option value="Tasks" ${selectedDocumentType == 'Tasks' ? 'selected' : ''}>Tasks</option>
                                        </select>
                                    </div>
                                    <div class="mb-4">
                                        <label for="departmentID" class="block text-sm font-medium text-gray-700">Ban</label>
                                        <select name="departmentID" id="departmentID" class="block w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-blue-500 focus:border-blue-500" required>
                                            <option value="">-- Chọn ban --</option>
                                            <c:forEach items="${departments}" var="dept">
                                                <option value="${dept.departmentID}" ${dept.departmentID == selectedDepartmentID ? 'selected' : ''}>${dept.departmentName}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <input type="hidden" name="clubID" id="documentClubID" value="${selectedClubID}">
                                    <div class="flex gap-4">
                                        <button type="submit" class="text-white bg-blue-600 hover:bg-blue-700 px-6 py-2 rounded-lg transition">Lưu</button>
                                        <button type="button" id="cancelDocumentForm" class="text-gray-700 bg-gray-200 hover:bg-gray-300 px-6 py-2 rounded-lg transition">Hủy</button>
                                    </div>
                                </form>
                            </div>
                            <div id="documentList">
                                <c:choose>
                                    <c:when test="${empty documents}">
                                        <p class="text-gray-500 text-center py-8 text-lg">Không có tài liệu nào.</p>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="grid">
                                            <c:forEach items="${documents}" var="doc">
                                                <div class="document-card">
                                                    <span class="document-type-badge ${fn:toLowerCase(doc.documentType)}">${doc.documentType}</span>
                                                    <h4>${doc.documentName}</h4>
                                                    <p>${doc.description}</p>
                                                    <p><strong>Ban:</strong> ${doc.department.departmentName}</p>
                                                    <p><strong>CLB:</strong> ${doc.club.clubName}</p>
                                                    <p><strong>Link:</strong> <a href="${doc.documentURL}" target="_blank" class="document-link">${doc.documentURL}</a></p>
                                                    <c:if test="${isLeader || isDepartment}">
                                                        <div class="action-buttons">
                                                            <a href="#" class="edit-document-btn" data-id="${doc.documentID}" data-name="${fn:escapeXml(doc.documentName)}" data-description="${fn:escapeXml(doc.description)}" data-url="${doc.documentURL}" data-type="${doc.documentType}" data-department="${doc.department.departmentID}" data-club="${doc.club.clubID}"><i class="fas fa-edit"></i> Sửa</a>
                                                            <a href="${pageContext.request.contextPath}/myclub?action=deleteDocument&documentID=${doc.documentID}&clubID=${doc.club.clubID}" onclick="return confirm('Bạn có chắc muốn xóa tài liệu này?');"><i class="fas fa-trash"></i> Xóa</a>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </section>
            </c:if>


            <!-- Recent Notifications -->
            <section id="notifications" class="mb-12">
                <h2 class="text-3xl font-bold text-gray-900 mb-6">Thông Báo Gần Đây</h2>
                <div class="card p-6">
                    <c:choose>
                        <c:when test="${empty recentNotifications}">
                            <p class="text-gray-500 text-center py-8 text-lg">Không có thông báo mới trong tuần qua.</p>
                        </c:when>
                        <c:otherwise>
                            <ul class="space-y-4">
                                <c:forEach items="${recentNotifications}" var="notification">
                                    <li class="flex items-start gap-4 p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition">
                                        <i class="fas fa-bell text-blue-600 text-xl mt-1"></i>
                                        <div>
                                            <h4 class="text-base font-semibold text-gray-900">${notification.title}</h4>
                                            <p class="text-sm text-gray-600">${notification.content}</p>
                                            <p class="text-xs text-gray-400">${notification.createdDate}</p>
                                        </div>
                                    </li>
                                </c:forEach>
                            </ul>
                            <a href="${pageContext.request.contextPath}/notification" class="mt-6 inline-block text-white bg-blue-600 hover:bg-blue-700 px-5 py-2.5 rounded-lg transition">Xem tất cả</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>

            <!-- Department Tasks -->
            <section id="calendar" class="mb-12">
                <h2 class="text-3xl font-bold text-gray-900 mb-6">Nhiệm Vụ Của Ban</h2>
                <div class="card p-6">
                    <c:choose>
                        <c:when test="${empty departmentTasks}">
                            <p class="text-gray-500 text-center py-8 text-lg">Không có nhiệm vụ ban nào.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <c:forEach items="${departmentTasks}" var="task" varStatus="loop">
                                    <div class="p-5 bg-gray-50 rounded-lg hover:bg-gray-100 transition">
                                        <h4 class="text-base font-semibold text-gray-900">${task.taskName}</h4>
                                        <p class="text-sm text-gray-600"><strong>Hạn chót:</strong> ${task.dueDate}</p>
                                        <p class="text-sm text-gray-600"><strong>Trạng thái:</strong> ${task.status}</p>
                                        <p class="text-sm text-gray-600"><strong>Ban:</strong> ${task.departmentName}</p>
                                        <p class="text-sm text-gray-600"><strong>CLB:</strong> ${task.clubName}</p>
                                        <p class="text-sm text-gray-600"><strong>Sự kiện:</strong> ${task.eventName}</p>
                                    </div>
                                    <c:if test="${loop.count % 2 == 1 and loop.last}">
                                        <div></div>
                                    </c:if>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>

            <c:if test="${isLeader || isDepartment}">
                <section id="upcoming-clubmeeting" class="mb-12">
                    <h2 class="text-3xl font-bold text-gray-900 mb-6">Cuộc Họp Sắp Tới Của CLB</h2>
                    <div class="card p-6">
                        <c:choose>
                            <c:when test="${empty clubmeetings}">
                                <p class="text-gray-500 text-center py-8 text-lg">Không có cuộc họp nào.</p>
                            </c:when>
                            <c:otherwise>
                                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                                    <c:forEach items="${clubmeetings}" var="cm" varStatus="loop">
                                        <div class="card p-6">
                                            <div class="flex items-center gap-6">
                                                <img src="${pageContext.request.contextPath}/${cm.clubImg}" alt="${cm.clubName}" class="w-24 h-24 rounded-lg object-cover border-2 border-blue-200 shadow-sm">
                                                <div>
                                                    <h3 class="text-xl font-semibold text-gray-900">${cm.clubName}</h3>
                                                    <h4 class="text-base font-medium text-gray-800">${cm.meetingTitle}</h4>
                                                    <p class="text-sm text-gray-600"><i class="fas fa-calendar-alt mr-2"></i> <strong>${cm.startedTime}</strong></p>
                                                    <p class="text-sm text-gray-600">
                                                        <strong>Link:</strong> <a href="${cm.URLMeeting}" target="_blank" class="text-blue-600 hover:underline">${cm.URLMeeting}</a>
                                                    </p>
                                                    <c:if test="${not empty cm.document}">
                                                        <p class="text-sm text-gray-600">
                                                            <strong>Tài liệu:</strong> <a href="${cm.document}" target="_blank" class="text-blue-600 hover:underline">${cm.document}</a>
                                                        </p>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>
                                        <c:if test="${loop.count % 2 == 1 and loop.last}">
                                            <div></div>
                                        </c:if>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </section>
            </c:if>

            <section id="upcoming-departmentmeeting" class="mb-12">
                <h2 class="text-3xl font-bold text-gray-900 mb-6">Cuộc Họp Sắp Tới Của Ban</h2>
                <div class="card p-6">
                    <c:choose>
                        <c:when test="${empty departmentmeetings}">
                            <p class="text-gray-500 text-center py-8 text-lg">Không có cuộc họp nào.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <c:forEach items="${departmentmeetings}" var="cm" varStatus="loop">
                                    <div class="card p-6">
                                        <div class="flex items-center gap-6">
                                            <img src="${pageContext.request.contextPath}/${cm.clubImg}" alt="${cm.clubName}" class="w-24 h-24 rounded-lg object-cover border-2 border-blue-200 shadow-sm">
                                            <div>
                                                <h3 class="text-xl font-semibold text-gray-900">${cm.clubName}</h3>
                                                <p class="text-sm text-gray-600"><i class="fas fa-users mr-2"></i> ${cm.departmentName}</p>
                                                <p class="text-sm text-gray-600"><i class="fas fa-calendar-alt mr-2"></i> <strong>${cm.startedTime}</strong></p>
                                                <p class="text-sm text-gray-600">
                                                    <strong>Link:</strong> <a href="${cm.URLMeeting}" target="_blank" class="text-blue-600 hover:underline">${cm.URLMeeting}</a>
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                    <c:if test="${loop.count % 2 == 1 and loop.last}">
                                        <div></div>
                                    </c:if>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>

            <section id="upcoming-events" class="mb-12">
                <h2 class="text-3xl font-bold text-gray-900 mb-6">Sự Kiện Sắp Tới</h2>
                <div class="card p-6">
                    <c:choose>
                        <c:when test="${empty userclubs and empty upcomingEvents}">
                            <p class="text-gray-500 text-center py-8 text-lg">Bạn chưa tham gia câu lạc bộ nào, do đó không có sự kiện sắp tới.</p>
                        </c:when>
                        <c:when test="${not empty userclubs and empty upcomingEvents}">
                            <p class="text-gray-500 text-center py-8 text-lg">Không có sự kiện sắp tới cho các câu lạc bộ bạn đã tham gia.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <c:forEach items="${upcomingEvents}" var="event">
                                    <div class="card p-6">
                                        <div class="flex items-center gap-6">
                                            <img src="${pageContext.request.contextPath}/${event.eventImg}" alt="${event.eventName}" class="w-36 h-24 rounded-lg object-cover border-2 border-blue-200 shadow-sm">
                                            <div>
                                                <h3 class="text-xl font-medium text-gray-800">${event.eventName}</h3>
                                                <p class="text-sm text-gray-600">
                                                    <i class="fas fa-calendar-alt"></i>
                                                    <c:choose>
                                                        <c:when test="${not empty event.schedules}">
                                                            <fmt:formatDate value="${event.schedules[0].eventDate}" pattern="dd/MM/yyyy"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            Chưa có lịch trình
                                                        </c:otherwise>
                                                    </c:choose>
                                                </p>
                                                <p class="text-sm text-gray-600">
                                                    <i class="fas fa-clock"></i>
                                                    <c:choose>
                                                        <c:when test="${not empty event.schedules}">
                                                            <fmt:formatDate value="${event.schedules[0].startTime}" pattern="HH:mm"/> -
                                                            <fmt:formatDate value="${event.schedules[0].endTime}" pattern="HH:mm"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            Không xác định
                                                        </c:otherwise>
                                                    </c:choose>
                                                </p>
                                                <p class="text-sm text-gray-600">
                                                    <i class="fas fa-map-marker-alt"></i>
                                                    <c:choose>
                                                        <c:when test="${not empty event.schedules}">
                                                            ${event.schedules[0].location.locationName}
                                                        </c:when>
                                                        <c:otherwise>
                                                            Không xác định
                                                        </c:otherwise>
                                                    </c:choose>
                                                </p>
                                            </div>
                                        </div>
                                        <a href="${pageContext.request.contextPath}/event-detail?id=${event.eventID}" class="mt-4 inline-block text-white bg-blue-600 hover:bg-blue-700 px-5 py-2.5 rounded-lg transition">Đăng ký ngay</a>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>

            <section id="actions" class="mb-12">
                <h2 class="text-3xl font-bold text-gray-900 mb-6">Quản Lý Form và Hoạt Động Tuyển Quân</h2>
                <div class="card p-6">
                    <h3 class="text-lg font-semibold text-gray-900 mb-3">Chọn Câu Lạc Bộ</h3>
                    <p class="text-sm text-gray-500 mb-4">Vui lòng chọn câu lạc bộ mà bạn muốn quản lý form hoặc hoạt động tuyển quân:</p>
                    <div class="relative inline-block w-full md:w-80 mb-6">
                        <select id="clubSelector" class="block appearance-none w-full bg-white border border-gray-300 hover:border-gray-400 px-4 py-3 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                            <option value="">-- Chọn câu lạc bộ --</option>
                            <c:forEach items="${userclubs}" var="club">
                                <option value="${club.clubID}">${club.clubName}</option>
                            </c:forEach>
                        </select>
                        <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-3 text-gray-600">
                            <i class="fas fa-chevron-down"></i>
                        </div>
                    </div>
                    <div id="recruitmentManagementSection" class="mb-6" style="display: none;">
                        <h3 class="text-lg font-semibold text-gray-900 mb-3">Quản Lý Tuyển Quân</h3>
                        <a href="#" id="recruitmentLink" class="inline-block text-white bg-green-600 hover:bg-green-700 px-6 py-3 rounded-lg transition flex items-center gap-2">
                            <i class="fas fa-users"></i> Quản Lý Hoạt Động Tuyển Quân
                        </a>
                    </div>
                    <div id="formManagementButtons" style="display: none;" class="flex flex-wrap gap-4">
                        <a href="#" id="formManagementLink" class="inline-block text-white bg-blue-600 hover:bg-blue-700 px-6 py-3 rounded-lg transition flex items-center gap-2">
                            <i class="fas fa-list-alt"></i> Quản Lý Các Form
                        </a>
                        <a href="#" id="formBuilderLink" class="inline-block text-white bg-blue-600 hover:bg-blue-700 px-6 py-3 rounded-lg transition flex items-center gap-2">
                            <i class="fas fa-plus"></i> Tạo Form Mới
                        </a>
                    </div>
                    <p id="noPermissionMessage" class="hidden text-sm text-red-600 mt-3">
                        <i class="fas fa-exclamation-circle mr-1"></i> Bạn không có đủ quyền quản lý trong câu lạc bộ này.
                    </p>
                </div>
            </section>
        </div>
    </main>
</div>

<script>
    document.querySelectorAll('.sidebar a').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            const href = this.getAttribute('href');
            if (href.startsWith('#')) {
                e.preventDefault();
                document.querySelector(href).scrollIntoView({behavior: 'smooth'});
            }
        });
    });

    // Club selection for form management
    const clubSelector = document.getElementById('clubSelector');
    const formManagementButtons = document.getElementById('formManagementButtons');
    const formManagementLink = document.getElementById('formManagementLink');
    const formBuilderLink = document.getElementById('formBuilderLink');
    const noPermissionMessage = document.getElementById('noPermissionMessage');
    const recruitmentLink = document.getElementById('recruitmentLink');
    const recruitmentManagementSection = document.getElementById('recruitmentManagementSection');

    // Document management
    const documentClubSelector = document.getElementById('documentClubSelector');
    const documentManagementSection = document.getElementById('documentManagementSection');
    const addDocumentBtn = document.getElementById('addDocumentBtn');
    const documentForm = document.getElementById('documentForm');
    const cancelDocumentForm = document.getElementById('cancelDocumentForm');
    const documentAction = document.getElementById('documentAction');
    const documentIDInput = document.getElementById('documentID');
    const documentNameInput = document.getElementById('documentName');
    const descriptionInput = document.getElementById('description');
    const documentURLInput = document.getElementById('documentURL');
    const documentTypeSelect = document.getElementById('documentType');
    const departmentSelect = document.getElementById('departmentID');
    const documentClubIDInput = document.getElementById('documentClubID');

    const chairmanClubIds = [
        <c:forEach items="${userclubs}" var="club" varStatus="status">
        <c:if test="${club.roleID == 1}">${club.clubID}<c:if test="${!status.last}">,</c:if></c:if>
        </c:forEach>
    ];

    const permittedClubIds = [
        <c:forEach items="${userclubs}" var="club" varStatus="status">
        <c:if test="${club.roleID >= 1 && club.roleID <= 3}">${club.clubID}<c:if test="${!status.last}">,</c:if></c:if>
        </c:forEach>
    ];

    if (recruitmentLink) {
        recruitmentLink.addEventListener('click', function (e) {
            e.preventDefault();
            const selectedClubId = clubSelector.value;
            if (selectedClubId) {
                window.location.href = '${pageContext.request.contextPath}/recruitment?clubId=' + selectedClubId;
            } else {
                alert('Vui lòng chọn câu lạc bộ trước khi truy cập trang quản lý tuyển quân.');
            }
        });
    }

    if (clubSelector) {
        clubSelector.addEventListener('change', function () {
            const selectedClubId = this.value;
            if (selectedClubId) {
                const hasFormPermission = permittedClubIds.includes(parseInt(selectedClubId));
                const hasRecruitmentPermission = chairmanClubIds.includes(parseInt(selectedClubId));
                if (hasFormPermission) {
                    formManagementButtons.style.display = 'flex';
                    noPermissionMessage.classList.add('hidden');
                    formManagementLink.href = '${pageContext.request.contextPath}/formManagement?clubId=' + selectedClubId;
                    formBuilderLink.href = '${pageContext.request.contextPath}/formBuilder?clubId=' + selectedClubId;
                } else {
                    formManagementButtons.style.display = 'none';
                    noPermissionMessage.classList.remove('hidden');
                }
                if (hasRecruitmentPermission) {
                    recruitmentManagementSection.style.display = 'block';
                } else {
                    recruitmentManagementSection.style.display = 'none';
                }
            } else {
                formManagementButtons.style.display = 'none';
                noPermissionMessage.classList.add('hidden');
                recruitmentManagementSection.style.display = 'none';
            }
        });
    }

    if (documentClubSelector) {
        documentClubSelector.addEventListener('change', function () {
            const selectedClubId = this.value;
            documentClubIDInput.value = selectedClubId;
            if (selectedClubId) {
                const form = document.createElement('form');
                form.method = 'post';
                form.action = '${pageContext.request.contextPath}/myclub';
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'loadDocuments';
                const clubIdInput = document.createElement('input');
                clubIdInput.type = 'hidden';
                clubIdInput.name = 'clubID';
                clubIdInput.value = selectedClubId;
                form.appendChild(actionInput);
                form.appendChild(clubIdInput);
                document.body.appendChild(form);
                form.submit();
            } else {
                documentManagementSection.style.display = 'none';
                documentForm.style.display = 'none';
                departmentSelect.innerHTML = '<option value="">-- Chọn ban --</option>';
            }
        });
    }

    if (addDocumentBtn && documentForm) {
        addDocumentBtn.addEventListener('click', function (e) {
            e.preventDefault();
            const selectedClubId = documentClubSelector.value;
            if (!selectedClubId) {
                alert('Vui lòng chọn câu lạc bộ trước.');
                return;
            }
            documentAction.value = 'createDocument';
            documentIDInput.value = '';
            documentNameInput.value = '';
            descriptionInput.value = '';
            documentURLInput.value = '';
            documentTypeSelect.value = 'Meeting';
            documentClubIDInput.value = selectedClubId;
            const form = document.createElement('form');
            form.method = 'post';
            form.action = '${pageContext.request.contextPath}/myclub';
            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'loadDepartments';
            const clubIdInput = document.createElement('input');
            clubIdInput.type = 'hidden';
            clubIdInput.name = 'clubID';
            clubIdInput.value = selectedClubId;
            const showDocumentFormInput = document.createElement('input');
            showDocumentFormInput.type = 'hidden';
            showDocumentFormInput.name = 'showDocumentForm';
            showDocumentFormInput.value = 'true';
            form.appendChild(actionInput);
            form.appendChild(clubIdInput);
            form.appendChild(showDocumentFormInput);
            document.body.appendChild(form);
            form.submit();
        });
    }

    if (cancelDocumentForm && documentForm) {
        cancelDocumentForm.addEventListener('click', function () {
            documentForm.style.display = 'none';
            departmentSelect.innerHTML = '<option value="">-- Chọn ban --</option>';
        });
    }

    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('edit-document-btn')) {
            e.preventDefault();
            const btn = e.target;
            const documentId = btn.getAttribute('data-id');
            const documentName = btn.getAttribute('data-name');
            const description = btn.getAttribute('data-description');
            const documentURL = btn.getAttribute('data-url');
            const documentType = btn.getAttribute('data-type');
            const departmentId = btn.getAttribute('data-department');
            const clubId = btn.getAttribute('data-club');
            documentAction.value = 'updateDocument';
            documentIDInput.value = documentId;
            documentNameInput.value = documentName;
            descriptionInput.value = description;
            documentURLInput.value = documentURL;
            documentTypeSelect.value = documentType;
            documentClubIDInput.value = clubId;
            documentClubSelector.value = clubId;
            const form = document.createElement('form');
            form.method = 'post';
            form.action = '${pageContext.request.contextPath}/myclub';
            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'loadDepartments';
            const clubIdInput = document.createElement('input');
            clubIdInput.type = 'hidden';
            clubIdInput.name = 'clubID';
            clubIdInput.value = clubId;
            const documentIdInput = document.createElement('input');
            documentIdInput.type = 'hidden';
            documentIdInput.name = 'documentID';
            documentIdInput.value = documentId;
            const showDocumentFormInput = document.createElement('input');
            showDocumentFormInput.type = 'hidden';
            showDocumentFormInput.name = 'showDocumentForm';
            showDocumentFormInput.value = 'true';
            form.appendChild(actionInput);
            form.appendChild(clubIdInput);
            form.appendChild(documentIdInput);
            form.appendChild(showDocumentFormInput);
            document.body.appendChild(form);
            form.submit();
        }
    });

    document.addEventListener('DOMContentLoaded', () => {
        const createBtn = document.querySelector('a[href="myclub?action=createClubMeeting"]');
        const createForm = document.getElementById('createMeetingForm');
        const cancelBtn = document.getElementById('cancelMeetingForm');
        const formTitle = document.getElementById('formTitle');
        const meetingIdInput = document.getElementById('meetingIdInput');
        const clubSelect = document.getElementById('clubId');
        const meetingTimeInput = document.getElementById('meetingTime');
        const meetingLinkInput = document.getElementById('meetingLink');
        const formElement = createForm?.querySelector('form');

        if (recruitmentManagementSection) {
            recruitmentManagementSection.style.display = 'none';
        }

        if (createBtn && createForm) {
            createBtn.addEventListener('click', (e) => {
                e.preventDefault();
                formTitle.textContent = 'Tạo Cuộc Họp Mới';
                formElement.action = '${pageContext.request.contextPath}/myclub?action=submitCreateMeeting';
                meetingIdInput.value = '';
                meetingTimeInput.value = '';
                meetingLinkInput.value = '';
                clubSelect.selectedIndex = 0;
                createForm.classList.remove('hidden');
                createForm.scrollIntoView({behavior: 'smooth', block: 'start'});
            });
        }

        if (cancelBtn && createForm) {
            cancelBtn.addEventListener('click', () => {
                createForm.classList.add('hidden');
            });
        }

        document.querySelectorAll('.edit-meeting-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                const meetingId = btn.getAttribute('data-id');
                const clubId = btn.getAttribute('data-clubid');
                const startedTime = btn.getAttribute('data-time');
                const meetingURL = btn.getAttribute('data-url');
                formTitle.textContent = 'Chỉnh sửa Cuộc Họp';
                meetingIdInput.value = meetingId;
                meetingTimeInput.value = startedTime;
                meetingLinkInput.value = meetingURL;
                [...clubSelect.options].forEach(option => {
                    option.selected = option.value === clubId;
                });
                formElement.action = '${pageContext.request.contextPath}/myclub?action=submitUpdateMeeting';
                createForm.classList.remove('hidden');
                createForm.scrollIntoView({behavior: 'smooth', block: 'start'});
            });
        });
    });

    function closeErrorModal() {
        document.getElementById('errorModal').style.display = 'none';
    }
</script>
</body>
</html>