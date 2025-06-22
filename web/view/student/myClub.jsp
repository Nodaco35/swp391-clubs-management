<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Câu Lạc Bộ Của Tôi - UniClub</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/myClub.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eventsPage.css">
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body>
        <jsp:include page="/view/events-page/header.jsp" />
        <a href="${pageContext.request.contextPath}/" class="back-btn">
            <i class="fas fa-arrow-left"></i>
        </a>
        <div class="flex">
            <!-- Sidebar -->
            <aside class="sidebar w-64 bg-gray-100 p-4 fixed h-screen shadow-lg">
                <h2 class="text-xl font-semibold text-gray-800 mb-4">Menu</h2>
                <aside class="sidebar w-64 bg-gray-100 p-4 fixed h-screen shadow-lg">
                    <h2 class="text-xl font-semibold text-gray-800 mb-4">Menu</h2>
                    <ul class="space-y-2">
                        <li><a href="#club-summaries" class="text-blue-500 hover:text-blue-700 block py-2 px-4 rounded">Câu Lạc Bộ Của Tôi</a></li>
                        <li><a href="#notifications" class="text-blue-500 hover:text-blue-700 block py-2 px-4 rounded">Thông Báo Gần Đây</a></li>
                        <li><a href="#calendar" class="text-blue-500 hover:text-blue-700 block py-2 px-4 rounded">Nhiệm vụ của ban</a></li>
                        <li><a href="#upcoming-clubmeeting" class="text-blue-500 hover:text-blue-700 block py-2 px-4 rounded">Cuộc học sắp tới của clb</a></li>
                        <li><a href="#upcoming-events" class="text-blue-500 hover:text-blue-700 block py-2 px-4 rounded">Sự Kiện Sắp Tới</a></li>
                        <li><a href="#actions" class="text-blue-500 hover:text-blue-700 block py-2 px-4 rounded">Quản Lý Form</a></li>
                            <c:set var="isLeader" value="false" />
                            <c:forEach items="${userclubs}" var="uc">
                                <c:if test="${not empty uc and uc.roleID == 1}">
                                    <c:set var="isLeader" value="true" />
                                </c:if>
                            </c:forEach>
                            <c:if test="${isLeader}">
                            <li><a href="#pending-applications" class="text-blue-500 hover:text-blue-700 block py-2 px-4 rounded">Các Đơn Chờ Duyệt</a></li>
                            <li><a href="#financial-overview" class="text-blue-500 hover:text-blue-700 block py-2 px-4 rounded">Tổng Quan Tài Chính</a></li>
                            </c:if>
                    </ul>
                </aside>
            </aside>
            <main class="ml-64 flex-1 p-6">

                <div class="container mx-auto px-4 py-8">
                    <section class="mb-10">
                        <!-- Thẻ thông tin trên đầu -->
                        <div class="grid grid-cols-4 gap-4 mb-6">
                            <div class="p-4 bg-white rounded-lg shadow-md border border-gray-200 flex items-center">
                                <i class="fas fa-calendar text-blue-500 mr-2"></i>
                                <div>
                                    <p class="text-sm text-gray-600">Số nhiệm vụ của ban</p>
                                    <p class="text-lg font-semibold text-gray-800">${countTodoLists} <a href="#calendar" ><i class="fa-solid fa-eye"></i></a></p>

                                </div>
                            </div>

                            <!-- moiws -->
                            <div class="p-4 bg-white rounded-lg shadow-md border border-gray-200 flex items-center">
                                <i class="fas fa-users text-blue-500 mr-2"></i>
                                <div>
                                    <p class="text-sm text-gray-600">Số cuộc họp của clb</p>
                                    <p class="text-lg font-semibold text-gray-800">${countUpcomingMeeting} <a href="#upcoming-clubmeeting" ><i class="fa-solid fa-eye"></i></a></p>
                                </div>
                            </div>

                            <c:set var="isLeader" value="false" />
                            <c:forEach items="${userclubs}" var="uc">
                                <c:if test="${not empty uc and uc.roleID == 1}">
                                    <c:set var="isLeader" value="true" />
                                </c:if>
                            </c:forEach>
                            <c:if test="${isLeader}">
                                <div class="p-4 bg-white rounded-lg shadow-md border border-gray-200 flex items-center">
                                    <i class="fas fa-file-alt text-blue-500 mr-2"></i>
                                    <div>
                                        <p class="text-sm text-gray-600">Số đơn cần duyệt</p>
                                        <p class="text-lg font-semibold text-gray-800">${countPendingApplication} <a href="#pending-applications"><i class="fa-solid fa-eye"></i></a></p>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </section>


                    <!-- Club Summaries -->
                    <section id="club-summaries" class="mb-10">
                        <h2 class="text-2xl font-bold text-gray-800 mb-4">Câu Lạc Bộ Của Tôi</h2>
                        <c:choose>
                            <c:when test="${empty userclubs}">
                                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                    <p class="text-gray-600 text-center py-8">Hiện chưa tham gia câu lạc bộ nào.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                    <c:forEach items="${userclubs}" var="uc">
                                        <div class="bg-white rounded-lg shadow-md p-6 border border-gray-200 hover:shadow-lg transition">
                                            <div class="flex items-center gap-6">
                                                <img src="${pageContext.request.contextPath}${uc.clubImg != null ? uc.clubImg : '/images/default-club.jpg'}" alt="${uc.clubName}" class="w-20 h-20 rounded-full object-cover border-2 border-blue-100">
                                                <div>
                                                    <h3 class="text-xl font-medium text-gray-800">${uc.clubName}</h3>
                                                    <p class="text-sm text-gray-600">Vai trò: ${uc.roleName}</p>
                                                    <p class="text-sm text-gray-600">Ban: ${uc.departmentName}</p>
                                                    <p class="text-sm text-gray-600">Ngày tham gia: ${uc.joinDate}</p>
                                                </div>
                                            </div>
                                            <a href="${pageContext.request.contextPath}/club-detail?id=${uc.clubID}" class="mt-4 inline-block text-white bg-blue-500 hover:bg-blue-600 px-4 py-2 rounded-full transition">Xem chi tiết</a>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </section>

                    <!-- Recent Notifications -->
                    <section id="notifications" class="mb-10">
                        <h2 class="text-2xl font-bold text-gray-800 mb-4">Thông Báo Gần Đây</h2>
                        <div class="bg-white rounded-lg shadow-md p-6 border border-gray-200">
                            <c:choose>
                                <c:when test="${empty recentNotifications}">
                                    <p class="text-gray-600 text-center py-8">Không có thông báo mới trong tuần qua.</p>
                                </c:when>
                                <c:otherwise>
                                    <ul class="space-y-5">
                                        <c:forEach items="${recentNotifications}" var="notification">
                                            <li class="flex items-start gap-5 p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition">
                                                <i class="fas fa-bell text-blue-500 mt-1"></i>
                                                <div>
                                                    <h4 class="text-base font-medium text-gray-800">${notification.title}</h4>
                                                    <p class="text-sm text-gray-600">${notification.content}</p>
                                                    <p class="text-xs text-gray-500">${notification.createdDate}</p>
                                                </div>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                    <a href="${pageContext.request.contextPath}/notification" class="mt-5 inline-block text-white bg-blue-500 hover:bg-blue-600 px-4 py-2 rounded-full transition">Xem tất cả</a>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </section>


                    <!-- Nhiệm vụ của người dùng -->
                    <section id="calendar" class="mb-10">


                        <!--                        
                                                mới-->
                        <section class="mb-10">
                            <h2 class="text-3xl font-semibold text-black mb-6">Nhiệm Vụ Của Ban</h2>
                            <div class="bg-white rounded-lg shadow-md p-6 border border-gray-200">
                                <c:choose>
                                    <c:when test="${empty departmentTasks}">
                                        <p class="text-gray-600 text-center py-4">Không có nhiệm vụ ban nào.</p>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="grid grid-cols-2 gap-4">
                                            <c:forEach items="${departmentTasks}" var="task"  varStatus="loop">
                                                <div class="p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition">
                                                    <h4 class="text-base font-medium text-gray-800">${task.taskName}</h4>
                                                    <p class="text-sm text-gray-600"><strong>Hạn chót: ${task.dueDate}</strong> </p>
                                                    <p class="text-sm text-gray-600">Trạng thái: ${task.status}</p>
                                                    <p class="text-sm text-gray-600">Ban: ${task.departmentName}</p>
                                                    <p class="text-sm text-gray-600">CLB: ${task.clubName}</p>
                                                    <p class="text-sm text-gray-600">Sự kiện: ${task.eventName}</p>

                                            </div>

                                            <c:if test="${loop.count % 2 == 1 and loop.last}">
                                                <div></div>
                                            </c:if>
                                        </c:forEach>


                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${isLeader}">
                                <a href="#" class="mt-5 inline-block text-white bg-blue-500 hover:bg-blue-600 px-4 py-2 rounded-full transition" >Tạo nhiệm vụ</a>
                            </c:if>
                        </div>
                    </section>

                        <!-- Mới đức -->
                        <section id="upcoming-clubmeeting" class="mb-10">
                            <h2 class="text-3xl font-semibold text-black mb-6">Cuộc họp sắp tới của clb</h2>
                            <div class="bg-white rounded-lg shadow-md p-6 border border-gray-200">
                                <c:choose>
                                    <c:when test="${empty clubmeetings}">
                                        <p class="text-gray-600 text-center py-4">Không có cuộc họp nào.</p>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="grid grid-cols-2 gap-4">
                                            <c:forEach items="${clubmeetings}" var="cm" varStatus="loop">
                                                <div class="bg-white rounded-lg shadow-md p-6 border border-gray-150 hover:shadow-lg transition">
                                                    <div class="flex items-center gap-6">
                                                        <img src="${pageContext.request.contextPath}/${cm.clubImg}" alt="${cm.clubName}" class="w-24 h-24 rounded-lg object-cover border-2 border-blue-100">
                                                        <div>
                                                            <h3 class="text-xl font-medium text-gray-800">${cm.clubName}</h3>
                                                            <p class="text-sm text-gray-600"><i class="fas fa-calendar-alt"></i> ${cm.startedTime}</p>
                                                            <p class="text-sm text-gray-600">
                                                                Link: <strong><a href="${cm.URLMeeting}" target="_blank" class="text-blue-600 underline">${cm.URLMeeting}</a></strong>
                                                            </p>                                                    </div>
                                                    </div>
                                                </div>

                                                <c:if test="${loop.count % 2 == 1 and loop.last}">
                                                    <div></div>
                                                </c:if>

                                            </c:forEach>


                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                <c:if test="${isLeader}">
                                    <a href="#" class="mt-5 inline-block text-white bg-blue-500 hover:bg-blue-600 px-4 py-2 rounded-full transition" >Tạo cuộc họp mới</a>
                                </c:if>
                            </div>
                        </section>
                        
                        <!-- Upcoming Events -->
                        <section id="upcoming-events" class="mb-10">
                            <h2 class="text-2xl font-bold text-gray-800 mb-4">Sự Kiện Sắp Tới</h2>
                            <c:choose>
                                <c:when test="${empty userclubs and empty upcomingEvents}">
                                    <p class="text-gray-600 text-center py-8">Bạn chưa tham gia câu lạc bộ nào, do đó không có sự kiện sắp tới.</p>
                                </c:when>
                                <c:when test="${not empty userclubs and empty upcomingEvents}">
                                    <p class="text-gray-600 text-center py-8">Không có sự kiện sắp tới cho các câu lạc bộ bạn đã tham gia.</p>
                                </c:when>
                                <c:otherwise>
                                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                                        <c:forEach items="${upcomingEvents}" var="event">
                                            <div class="bg-white rounded-lg shadow-md p-6 border border-gray-200 hover:shadow-lg transition">
                                                <div class="flex items-center gap-6">
                                                    <img src="${pageContext.request.contextPath}/${event.eventImg}" alt="${event.eventName}" class="w-24 h-24 rounded-lg object-cover border-2 border-blue-100">
                                                    <div>
                                                        <h3 class="text-xl font-medium text-gray-800">${event.eventName}</h3>
                                                        <p class="text-sm text-gray-600"><i class="fas fa-calendar-alt"></i> ${event.eventDate}</p>
                                                        <p class="text-sm text-gray-600"><i class="fas fa-map-marker-alt"></i> ${event.location.locationName}</p>
                                                    </div>
                                                </div>
                                                <a href="${pageContext.request.contextPath}/event-detail?id=${event.eventID}" class="mt-4 inline-block text-white bg-blue-500 hover:bg-blue-600 px-4 py-2 rounded-full transition">Đăng kí ngay</a>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </section>

                        <!-- Pending Member Applications -->
                        <c:set var="isLeader" value="false" />
                        <c:forEach items="${userclubs}" var="uc">
                            <c:if test="${uc.roleID == 1}">
                                <c:set var="isLeader" value="true" />
                            </c:if>
                        </c:forEach>
                        <c:if test="${isLeader}">
                            <section id="pending-applications" class="mb-10">
                                <h2 class="text-2xl font-bold text-gray-800 mb-4">Các Đơn Chờ Duyệt</h2>
                                <c:choose>
                                    <c:when test="${empty pendingApplications}">
                                        <p class="text-gray-600 text-center py-8">Không có đơn đăng ký nào đang chờ.</p>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="bg-white rounded-lg shadow-md p-6 border border-gray-200">
                                            <table class="w-full text-sm text-gray-600">
                                                <thead>
                                                    <tr class="border-b">
                                                        <th class="py-3 text-left">Họ Tên</th>
                                                        <th class="py-3 text-left">Email</th>
                                                        <th class="py-3 text-left">Ngày Gửi</th>
                                                        <th class="py-3 text-left">Tên CLB</th>
                                                        <th class="py-3 text-left">Hành Động</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach items="${pendingApplications}" var="application">
                                                        <tr class="border-b">
                                                            <td class="py-2">${application.userName}</td>
                                                            <td class="py-2">${application.email}</td>
                                                            <td class="py-2">${application.submitDate}</td>
                                                            <td class="py-2">${application.clubName}</td>
                                                            <td class="py-2">
                                                                <a href="#" class="text-white bg-blue-500 hover:bg-blue-600 px-3 py-1 rounded-full transition">Xem</a>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </section>
                        </c:if>



                        <!-- Financial Overview -->
                        <c:if test="${isLeader}">
                            <div class="mb-8">
                                <h2 class="text-2xl font-bold text-gray-800 mb-4">Tổng Quan Tài Chính</h2>
                                <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                                    <div class="bg-white rounded-lg shadow-md p-4 border border-gray-200">
                                        <h3 class="text-lg font-semibold text-gray-800">Câu Lạc Bộ Bóng Đá</h3>
                                        <p class="text-sm text-gray-600">Tổng thu: 5,000,000 VND</p>
                                        <p class="text-sm text-gray-600">Tổng chi: 3,000,000 VND</p>
                                        <p class="text-sm text-gray-600">Số dư: 2,000,000 VND</p>
                                    </div>
                                    <div class="bg-white rounded-lg shadow-md p-4 border border-gray-200">
                                        <h3 class="text-lg font-semibold text-gray-800">Câu Lạc Bộ Tiếng Anh</h3>
                                        <p class="text-sm text-gray-600">Tổng thu: 2,000,000 VND</p>
                                        <p class="text-sm text-gray-600">Tổng chi: 1,500,000 VND</p>
                                        <p class="text-sm text-gray-600">Số dư: 500,000 VND</p>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        
                            <!-- Existing Actions Section -->
                            <section id="actions" class="mb-10">
                                <h2 class="text-2xl font-bold text-gray-800 mb-4">Quản Lý Form</h2>
                                
                                <!-- Club selection dropdown -->
                                <div class="bg-white rounded-lg shadow-md p-6 border border-gray-200 mb-4">
                                    <h3 class="text-lg font-medium text-gray-800 mb-3">Chọn Câu Lạc Bộ</h3>
                                    <p class="text-sm text-gray-600 mb-4">Vui lòng chọn câu lạc bộ mà bạn muốn quản lý form:</p>
                                    
                                    <div class="relative inline-block w-full md:w-64 mb-4">
                                        <select id="clubSelector" class="block appearance-none w-full bg-white border border-gray-300 hover:border-gray-400 px-4 py-2 pr-8 rounded-lg shadow leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                                            <option value="">-- Chọn câu lạc bộ --</option>
                                            <c:forEach items="${userclubs}" var="club">
                                                <option value="${club.clubID}">${club.clubName}</option>
                                            </c:forEach>
                                        </select>
                                        <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
                                            <i class="fas fa-chevron-down"></i>
                                        </div>
                                    </div>
                                    
                                    <div id="formManagementButtons" style="display: none;" class="flex gap-4">
                                        <a href="#" id="formManagementLink" class="text-white bg-blue-500 hover:bg-blue-600 px-6 py-3 rounded-full transition flex items-center gap-2">
                                            <i class="fas fa-list-alt"></i> Quản Lý Các Form
                                        </a>
                                        <a href="#" id="formBuilderLink" class="text-white bg-blue-500 hover:bg-blue-600 px-6 py-3 rounded-full transition flex items-center gap-2">
                                            <i class="fas fa-plus"></i> Tạo Form Mới
                                        </a>
                                    </div>
                                                      <p id="noPermissionMessage" class="hidden text-sm text-red-500 mt-2">
                                        <i class="fas fa-exclamation-circle mr-1"></i> Bạn không có đủ quyền quản lý form trong câu lạc bộ này. Chỉ thành viên có vai trò quản lý mới có thể truy cập.
                                    </p>
                                </div>
                            </section>
                </div>
            </main>

        </div>


    </body>
    <script>
        document.querySelectorAll('.sidebar a').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                document.querySelector(this.getAttribute('href')).scrollIntoView({behavior: 'smooth'});
            });
        });
        
        // Club selection for form management
        const clubSelector = document.getElementById('clubSelector');
        const formManagementButtons = document.getElementById('formManagementButtons');
        const formManagementLink = document.getElementById('formManagementLink');
        const formBuilderLink = document.getElementById('formBuilderLink');
        const noPermissionMessage = document.getElementById('noPermissionMessage');
          if (clubSelector) {
            clubSelector.addEventListener('change', function() {
                const selectedClubId = this.value;
                  if (selectedClubId) {
                    // Create array of clubs where user has permission (roleID 1-3)
                    const permittedClubIds = [
                        <c:forEach items="${userclubs}" var="club" varStatus="status">
                            <c:if test="${club.roleID >= 1 && club.roleID <= 3}">${club.clubID}<c:if test="${!status.last}">,</c:if></c:if>
                        </c:forEach>
                    ];
                    
                    // Check if user has permission for this club
                    const hasPermission = permittedClubIds.includes(parseInt(selectedClubId));
                    
                    if (hasPermission) {
                        // Show form management buttons if user has permission
                        formManagementButtons.style.display = 'flex';
                        noPermissionMessage.classList.add('hidden');
                        
                        // Update links with the selected club ID
                        formManagementLink.href = '${pageContext.request.contextPath}/formManagement?clubId=' + selectedClubId;
                        formBuilderLink.href = '${pageContext.request.contextPath}/formBuilder?clubId=' + selectedClubId;
                    } else {
                        // Show no permission message
                        formManagementButtons.style.display = 'none';
                        noPermissionMessage.classList.remove('hidden');
                    }
                } else {
                    // Hide form management buttons when no club is selected
                    formManagementButtons.style.display = 'none';
                    noPermissionMessage.classList.add('hidden');
                }
            });
        }
    </script>
</html>
