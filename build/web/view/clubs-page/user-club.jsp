<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý thành viên câu lạc bộ</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <script src="https://kit.fontawesome.com/a076d05399.js" crossorigin="anonymous"></script>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    
    <body class="bg-gray-100 font-sans antialiased">
        <jsp:include page="../components/header.jsp" />

        <div style="display: flex; flex-direction: column">
            <div class="container mx-auto max-w-6xl rounded-2xl bg-white shadow-xl p-6 my-8">
                <h2 class="text-2xl md:text-3xl font-bold text-gray-800 text-center mb-6">
                    Quản lý thành viên của ${clubName}
                </h2>

                <!-- Hiển thị thông báo -->
                <c:if test="${not empty message}">
                    <div class="bg-green-100 text-green-700 p-4 rounded-lg mb-6 text-center">
                        ${message}
                    </div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" id="errorModal">
                        <div class="bg-white rounded-lg w-full max-w-md">
                            <div class="bg-red-500 text-white p-4 rounded-t-lg flex justify-between items-center">
                                <h5 class="text-lg font-semibold">Lỗi</h5>
                                <button type="button" class="text-xl" onclick="closeErrorModal()">×</button>
                            </div>
                            <div class="p-6">
                                <p>${error}</p>
                            </div>
                            <div class="p-4 border-t border-gray-200 text-right">
                                <button type="button" class="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition" onclick="closeErrorModal()">Đóng</button>
                            </div>
                        </div>
                    </div>
                </c:if>

                <!-- Header controls: Form tìm kiếm và nút thêm -->
                <div class="flex flex-col md:flex-row justify-between items-center mb-6 gap-4">
                    <form class="flex w-full md:w-auto gap-2" action="${pageContext.request.contextPath}/club-members" method="post">
                        <input type="hidden" name="clubID" value="${clubID}">
                        <input type="hidden" name="action" value="search">
                        <input type="text" name="search" value="${search}" placeholder="Tìm kiếm theo tên thành viên..." class="w-full md:w-64 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                        <button type="submit" class="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition">Tìm kiếm</button>
                    </form>
                    <button type="button" class="bg-green-500 text-white px-4 py-2 rounded-lg hover:bg-green-600 transition" onclick="toggleAddForm()">Thêm thành viên</button>
                </div>

                <!-- Bảng danh sách thành viên -->
                <div class="overflow-x-auto">
                    <table class="w-full border-collapse bg-white rounded-lg shadow">
                        <thead class="bg-gray-800 text-white">
                            <tr>
                                <th class="p-3 text-left">Mã SV</th>
                                <th class="p-3 text-left">Họ tên</th>
                                <th class="p-3 text-left">Ban</th>
                                <th class="p-3 text-left">Vai trò</th>
                                <th class="p-3 text-left">Ngày tham gia</th>
                                <th class="p-3 text-left">Trạng thái</th>
                                <th class="p-3 text-left">Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="uc" items="${userClubs}">
                                <tr class="hover:bg-gray-50 transition">
                                    <td class="p-3 border-b">${uc.userID}</td>
                                    <td class="p-3 border-b">${uc.fullName}</td>
                                    <td class="p-3 border-b">${uc.departmentName}</td>
                                    <td class="p-3 border-b">${uc.roleName}</td>
                                    <td class="p-3 border-b">${uc.joinDate}</td>
                                    <td class="p-3 border-b">${uc.isActive ? 'Hoạt động' : 'Không hoạt động'}</td>
                                    <td class="p-3 border-b flex gap-2">
                                        <button class="bg-yellow-500 text-white px-3 py-1 rounded-lg hover:bg-yellow-600 transition" onclick="toggleEditForm(${uc.userClubID}, '${uc.userID}', ${uc.departmentID}, ${uc.roleID}, ${uc.isActive})">Sửa</button>
                                        <form action="${pageContext.request.contextPath}/club-members" method="post" class="inline">
                                            <input type="hidden" name="clubID" value="${clubID}">
                                            <input type="hidden" name="userClubID" value="${uc.userClubID}">
                                            <input type="hidden" name="action" value="delete">
                                            <button type="submit" class="bg-red-500 text-white px-3 py-1 rounded-lg hover:bg-red-600 transition" onclick="return confirm('Bạn có chắc muốn xóa?')">Xóa</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <!-- Form thêm/sửa thành viên -->
                <div class="hidden bg-gray-50 p-6 rounded-lg shadow-inner mt-6" id="memberForm">
                    <h3 id="formTitle" class="text-xl font-semibold text-gray-800 mb-4">Thêm thành viên</h3>
                    <form action="${pageContext.request.contextPath}/club-members" method="post" id="formData" class="space-y-4">
                        <input type="hidden" name="clubID" value="${clubID}">
                        <input type="hidden" name="action" id="formAction" value="add">
                        <input type="hidden" name="userClubID" id="userClubID">

                        <div>
                            <label for="userID" class="block text-sm font-medium text-gray-700">Mã thành viên:</label>
                            <input type="text" name="userID" id="userID" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                        </div>

                        <div>
                        <label for="departmentID" class="block text-sm font-medium text-gray-700">Ban:</label>
                        <select name="departmentID" id="departmentID" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                            <c:forEach var="dept" items="${departments}">
                                <option value="${dept.departmentID}">${dept.departmentName}</option>
                            </c:forEach>
                        </select>
                    </div>

                        <div>
                            <label for="roleID" class="block text-sm font-medium text-gray-700">Vai trò:</label>
                            <select name="roleID" id="roleID" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" onchange="updateDepartmentOptions()">
                                <c:forEach var="role" items="${roles}">
                                    <option value="${role.roleID}">${role.roleName}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="flex items-center">
                            <input type="checkbox" name="isActive" id="isActive" value="true" class="h-4 w-4 text-blue-500 focus:ring-blue-500 border-gray-300 rounded" checked>
                            <label for="isActive" class="ml-2 text-sm text-gray-700">Hoạt động</label>
                        </div>

                        <div class="flex justify-center gap-4">
                            <button type="submit" id="submitButton" class="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition">Thêm</button>
                            <button type="button" class="bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600 transition" onclick="hideForm()">Hủy</button>
                        </div>
                    </form>
                </div>

                <!-- Footer controls: Pagination và Nút quay lại -->
                <div class="flex flex-col md:flex-row justify-between items-center mt-6 gap-4">
                    <a href="${pageContext.request.contextPath}/club-detail?id=${clubID}" class="text-blue-500 hover:text-blue-600 transition flex items-center gap-2">
                        <i class="fas fa-arrow-left"></i> Back
                    </a>
                    <nav>
                        <ul class="flex gap-2">
                            <c:if test="${currentPage > 1}">
                                <li>
                                    <a href="${pageContext.request.contextPath}/club-members?clubID=${clubID}&page=${currentPage - 1}&search=${search}" class="px-3 py-1 border border-gray-300 rounded-lg hover:bg-blue-500 hover:text-white transition">« Trước</a>
                                </li>
                            </c:if>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li>
                                    <a href="${pageContext.request.contextPath}/club-members?clubID=${clubID}&page=${i}&search=${search}" class="px-3 py-1 border border-gray-300 rounded-lg ${i == currentPage ? 'bg-blue-500 text-white' : 'hover:bg-blue-500 hover:text-white'} transition">${i}</a>
                                </li>
                            </c:forEach>
                            <c:if test="${currentPage < totalPages}">
                                <li>
                                    <a href="${pageContext.request.contextPath}/club-members?clubID=${clubID}&page=${currentPage + 1}&search=${search}" class="px-3 py-1 border border-gray-300 rounded-lg hover:bg-blue-500 hover:text-white transition">Tiếp »</a>
                                </li>
                            </c:if>
                        </ul>
                    </nav>
                </div>
            </div>
        </div>

        <script>
            let currentEditUserClubID = null;
            const departments = [
                <c:forEach var="dept" items="${departments}" varStatus="loop">
                    { id: ${dept.departmentID}, name: "${dept.departmentName}" }${loop.last ? '' : ','}
                </c:forEach>
            ];

            // Ẩn form khi tải trang
            document.addEventListener('DOMContentLoaded', function () {
                hideForm();
                <c:if test="${editUserClub != null}">
                    showEditForm(
                        ${editUserClub.userClubID},
                        '${editUserClub.userID}',
                        ${editUserClub.departmentID},
                        ${editUserClub.roleID},
                        ${editUserClub.isActive}
                    );
                    updateDepartmentOptions();
                </c:if>
                <c:if test="${not empty error}">
                    document.getElementById('errorModal').classList.remove('hidden');
                </c:if>
            });

            function updateDepartmentOptions() {
                const roleID = document.getElementById('roleID').value;
                const departmentSelect = document.getElementById('departmentID');
                const currentDepartmentID = departmentSelect.value;

                // Xóa tất cả tùy chọn hiện tại
                departmentSelect.innerHTML = '';

                if (roleID === '1' || roleID === '2') {
                    const option = document.createElement('option');
                    option.value = '3';
                    option.text = 'Ban Chủ nhiệm';
                    departmentSelect.appendChild(option);
                } else {
                    departments.forEach(dept => {
                        if (dept.id === 1 || dept.id === 2) {
                            const option = document.createElement('option');
                            option.value = dept.id;
                            option.text = dept.name;
                            departmentSelect.appendChild(option);
                        }
                    });
                }

                // Đặt giá trị mặc định
                departmentSelect.value = (roleID === '1' || roleID ==='2') ? '3' :
                    (currentDepartmentID && (currentDepartmentID === '1' || currentDepartmentID === '2') ? currentDepartmentID :
                    (departments.length > 0 ? '1' : ''));
            }

            function toggleAddForm() {
                const form = document.getElementById('memberForm');
                if (form.classList.contains('block') && document.getElementById('formAction').value === 'add') {
                    hideForm();
                } else {
                    showAddForm();
                }
            }

            // Toggle form sửa
            function toggleEditForm(userClubID, userID, departmentID, roleID, isActive) {
                const form = document.getElementById('memberForm');
                if (form.classList.contains('block') && currentEditUserClubID === userClubID) {
                    hideForm();
                    currentEditUserClubID = null;
                } else {
                    showEditForm(userClubID, userID, departmentID, roleID, isActive);
                    currentEditUserClubID = userClubID;
                }
            }

            // Hiển thị form thêm
            function showAddForm() {
                const form = document.getElementById('memberForm');
                const formTitle = document.getElementById('formTitle');
                const formAction = document.getElementById('formAction');
                const submitButton = document.getElementById('submitButton');
                const userIDInput = document.getElementById('userID');

                form.classList.remove('hidden');
                form.classList.add('block');
                formTitle.textContent = 'Thêm thành viên';
                formAction.value = 'add';
                submitButton.textContent = 'Thêm';
                userIDInput.removeAttribute('readonly');
                document.getElementById('formData').reset();
                document.getElementById('userClubID').value = '';
                document.getElementById('isActive').checked = true;
                document.getElementById('roleID').value = departments.length > 0 ? '4' : '1'; // Mặc định trưởng ban nếu có ban
                updateDepartmentOptions();
                currentEditUserClubID = null;
            }

            // Hiển thị form sửa
            function showEditForm(userClubID, userID, departmentID, roleID, isActive) {
                const form = document.getElementById('memberForm');
                const formTitle = document.getElementById('formTitle');
                const formAction = document.getElementById('formAction');
                const submitButton = document.getElementById('submitButton');
                const userIDInput = document.getElementById('userID');

                form.classList.remove('hidden');
                form.classList.add('block');
                formTitle.textContent = 'Sửa thành viên';
                formAction.value = 'update';
                submitButton.textContent = 'Cập nhật';
                userIDInput.setAttribute('readonly', 'readonly');

                document.getElementById('userClubID').value = userClubID;
                document.getElementById('userID').value = userID;
                document.getElementById('roleID').value = roleID;
                document.getElementById('isActive').checked = isActive;
                updateDepartmentOptions();
                document.getElementById('departmentID').value = (roleID === 1 || roleID === 2) ? '3' : departmentID;
            }

            // Ẩn form
            function hideForm() {
                document.getElementById('memberForm').classList.add('hidden');
                document.getElementById('memberForm').classList.remove('block');
                document.getElementById('formData').reset();
                currentEditUserClubID = null;
            }

            // Đóng modal lỗi
            function closeErrorModal() {
                document.getElementById('errorModal').classList.add('hidden');
            }

            // Validation client-side trước khi submit
            document.getElementById('formData').addEventListener('submit', function(e) {
                const userID = document.getElementById('userID').value.trim();
                const roleID = document.getElementById('roleID').value;
                const departmentID = document.getElementById('departmentID').value;

                if (!userID) {
                    alert('Mã thành viên không được để trống!');
                    e.preventDefault();
                    return;
                }

                if ((roleID === '1' || roleID === '2') && departmentID !== '3') {
                    alert('Chủ nhiệm và phó chủ nhiệm phải thuộc Ban Chủ nhiệm!');
                    e.preventDefault();
                    return;
                }

                if (roleID !== '1' && roleID !== '2' && (departmentID !== '1' && departmentID !== '2')) {
                    alert('Vai trò này chỉ được thuộc Ban 1 hoặc Ban 2!');
                    e.preventDefault();
                    return;
                }
            });
        </script>
        
        <div class="fixed bottom-0 left-0 right-0">
            <jsp:include page="../components/footer.jsp" />
        </div>
    </body>
</html>