<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi Tiết Chiến Dịch Tuyển Quân - UniClub</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;500;600;700&family=Poppins:wght@300;400;500;600;700&family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body style="background-color: #f5f7fa;">
    <jsp:include page="/view/events-page/header.jsp" />
    
    <!-- Toast Container -->
    <div class="toast-container" id="toastContainer"></div>
    
    <div class="container mx-auto px-4 py-8">
        <div class="flex items-center mb-6">
            <a href="${pageContext.request.contextPath}/recruitment/list?clubId=${campaign.clubID}" class="inline-flex items-center text-blue-500 hover:text-blue-700 mr-4">
                <i class="fas fa-arrow-left mr-2"></i> Quay lại
            </a>
            <h1 class="text-3xl font-bold text-gray-800">Chi Tiết Chiến Dịch Tuyển Quân</h1>
        </div>
        
        <div class="bg-white rounded-lg shadow-md p-6 mb-8 border border-gray-200">
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div>
                    <h2 class="text-xl font-semibold mb-4 text-gray-800">Thông Tin Chiến Dịch</h2>
                    
                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-600 mb-1">Tiêu đề</label>
                        <p class="text-gray-800">${campaign.title}</p>
                    </div>
                    
                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-600 mb-1">Mô tả</label>
                        <p class="text-gray-800">${campaign.description}</p>
                    </div>
                    
                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-600 mb-1">Gen</label>
                        <p class="text-gray-800">${campaign.gen}</p>
                    </div>
                </div>
                
                <div>
                    <h2 class="text-xl font-semibold mb-4 text-gray-800">Thời Gian & Trạng Thái</h2>
                    
                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-600 mb-1">Form đăng ký</label>
                        <p class="text-gray-800">${campaign.templateName}</p>
                    </div>
                    
                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-600 mb-1">Ngày bắt đầu</label>
                        <p class="text-gray-800"><fmt:formatDate value="${campaign.startDate}" pattern="dd/MM/yyyy" /></p>
                    </div>
                    
                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-600 mb-1">Ngày kết thúc</label>
                        <p class="text-gray-800"><fmt:formatDate value="${campaign.endDate}" pattern="dd/MM/yyyy" /></p>
                    </div>
                    
                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-600 mb-1">Trạng thái</label>
                        <div>
                            <c:choose>
                                <c:when test="${campaign.status eq 'ONGOING'}">
                                    <span class="px-3 py-1 text-sm rounded-full bg-green-100 text-green-800">Đang diễn ra</span>
                                </c:when>
                                <c:when test="${campaign.status eq 'UPCOMING'}">
                                    <span class="px-3 py-1 text-sm rounded-full bg-yellow-100 text-yellow-800">Sắp tới</span>
                                </c:when>
                                <c:when test="${campaign.status eq 'CLOSED'}">
                                    <span class="px-3 py-1 text-sm rounded-full bg-gray-100 text-gray-800">Đã hoàn thành</span>
                                </c:when>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="flex justify-end mt-4 space-x-4">
                <c:if test="${hasEditPermission}">
                    <a href="${pageContext.request.contextPath}/recruitment/edit?id=${campaign.recruitmentID}" class="bg-blue-500 hover:bg-blue-600 text-white py-2 px-4 rounded transition">
                        <i class="fas fa-edit mr-2"></i> Chỉnh sửa
                    </a>
                    <c:if test="${campaign.status eq 'ONGOING' || campaign.status eq 'UPCOMING'}">
                        <button class="bg-yellow-500 hover:bg-yellow-600 text-white py-2 px-4 rounded transition close-campaign" data-id="${campaign.recruitmentID}">
                            <i class="fas fa-check-circle mr-2"></i> Kết thúc
                        </button>
                    </c:if>
                    <button class="bg-red-500 hover:bg-red-600 text-white py-2 px-4 rounded transition delete-campaign" data-id="${campaign.recruitmentID}">
                        <i class="fas fa-trash-alt mr-2"></i> Xóa
                    </button>
                </c:if>
            </div>
        </div>
        
        <!-- Danh sách các giai đoạn -->
        <div class="bg-white rounded-lg shadow-md p-6 border border-gray-200">
            <h2 class="text-xl font-semibold mb-4 text-gray-800">Các Giai Đoạn</h2>
            
            <c:choose>
                <c:when test="${empty stages}">
                    <div class="text-center py-6">
                        <p class="text-gray-500">Chưa có giai đoạn nào được tạo cho chiến dịch này.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="overflow-x-auto">
                        <table class="min-w-full divide-y divide-gray-200">
                            <thead>
                                <tr>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tên Giai Đoạn</th>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Thời Gian</th>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Địa Điểm</th>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Trạng Thái</th>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hành Động</th>
                                </tr>
                            </thead>
                            <tbody class="bg-white divide-y divide-gray-200">
                                <c:forEach items="${stages}" var="stage">
                                    <tr>
                                        <td class="px-6 py-4 whitespace-nowrap">
                                            <div class="font-medium text-gray-900">${stage.stageName}</div>
                                            <div class="text-sm text-gray-500">${stage.description}</div>
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap">
                                            <div class="text-sm text-gray-900"><fmt:formatDate value="${stage.startDate}" pattern="dd/MM/yyyy" /> - <fmt:formatDate value="${stage.endDate}" pattern="dd/MM/yyyy" /></div>
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap">
                                            <div class="text-sm text-gray-900">${stage.locationName}</div>
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap">
                                            <c:choose>
                                                <c:when test="${stage.status eq 'ONGOING'}">
                                                    <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">Đang diễn ra</span>
                                                </c:when>
                                                <c:when test="${stage.status eq 'UPCOMING'}">
                                                    <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800">Sắp tới</span>
                                                </c:when>
                                                <c:when test="${stage.status eq 'CLOSED'}">
                                                    <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800">Đã hoàn thành</span>
                                                </c:when>
                                            </c:choose>
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                            <a href="${pageContext.request.contextPath}/recruitment/stage/${stage.stageID}" class="text-blue-600 hover:text-blue-900">
                                                <i class="fas fa-eye"></i> Chi tiết
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <!-- Modal xác nhận kết thúc hoạt động -->
    <div id="closeModal" class="hidden fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg p-8 max-w-md mx-auto border border-gray-200">
            <h3 class="text-xl font-bold mb-4">Xác nhận kết thúc hoạt động</h3>
            <p class="text-gray-600 mb-6">Bạn có chắc chắn muốn kết thúc chiến dịch tuyển quân này? Trạng thái sẽ chuyển thành "Đã hoàn thành".</p>
            <div class="flex justify-end">
                <button id="cancelClose" class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded mr-2">
                    Hủy
                </button>
                <button id="confirmClose" class="bg-yellow-500 hover:bg-yellow-700 text-white font-bold py-2 px-4 rounded">
                    Kết thúc
                </button>
            </div>
        </div>
    </div>

    <!-- Modal xác nhận xóa -->
    <div id="deleteModal" class="hidden fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg p-8 max-w-md mx-auto border border-gray-200">
            <h3 class="text-xl font-bold mb-4">Xác nhận xóa</h3>
            <p class="text-gray-600 mb-6">Bạn có chắc chắn muốn xóa chiến dịch tuyển quân này? Hành động này không thể hoàn tác.</p>
            <div class="flex justify-end">
                <button id="cancelDelete" class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded mr-2">
                    Hủy
                </button>
                <button id="confirmDelete" class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">
                    Xóa
                </button>
            </div>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Xử lý kết thúc chiến dịch
            const closeButtons = document.querySelectorAll('.close-campaign');
            const closeModal = document.getElementById('closeModal');
            const cancelClose = document.getElementById('cancelClose');
            const confirmClose = document.getElementById('confirmClose');
            
            let campaignIdToClose = null;
            
            if (closeButtons.length > 0 && closeModal && cancelClose && confirmClose) {
                closeButtons.forEach(button => {
                    button.addEventListener('click', () => {
                        campaignIdToClose = button.getAttribute('data-id');
                        closeModal.classList.remove('hidden');
                    });
                });
                
                cancelClose.addEventListener('click', () => {
                    closeModal.classList.add('hidden');
                });
                
                confirmClose.addEventListener('click', () => {
                    if (campaignIdToClose) {
                        // Gửi yêu cầu kết thúc chiến dịch
                        const xhr = new XMLHttpRequest();
                        xhr.open('POST', `${window.location.origin}/recruitment/close`, true);
                        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                        xhr.onreadystatechange = function() {
                            if (xhr.readyState === 4) {
                                if (xhr.status === 200) {
                                    const response = JSON.parse(xhr.responseText);
                                    if (response.success) {
                                        // Kết thúc thành công, reload trang
                                        window.location.reload();
                                    } else {
                                        alert('Lỗi khi kết thúc chiến dịch: ' + response.message);
                                    }
                                } else {
                                    alert('Đã xảy ra lỗi khi kết thúc chiến dịch.');
                                }
                                closeModal.classList.add('hidden');
                            }
                        };
                        xhr.send(`recruitmentId=${campaignIdToClose}`);
                    }
                });
            }

            // Xử lý xóa chiến dịch
            const deleteButtons = document.querySelectorAll('.delete-campaign');
            const deleteModal = document.getElementById('deleteModal');
            const cancelDelete = document.getElementById('cancelDelete');
            const confirmDelete = document.getElementById('confirmDelete');
            
            let campaignIdToDelete = null;
            
            if (deleteButtons.length > 0 && deleteModal && cancelDelete && confirmDelete) {
                deleteButtons.forEach(button => {
                    button.addEventListener('click', () => {
                        campaignIdToDelete = button.getAttribute('data-id');
                        deleteModal.classList.remove('hidden');
                    });
                });
                
                cancelDelete.addEventListener('click', () => {
                    deleteModal.classList.add('hidden');
                });
                
                confirmDelete.addEventListener('click', () => {
                    if (campaignIdToDelete) {
                        // Gửi yêu cầu xóa chiến dịch
                        const xhr = new XMLHttpRequest();
                        xhr.open('POST', `${window.location.origin}/recruitment/delete`, true);
                        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                        xhr.onreadystatechange = function() {
                            if (xhr.readyState === 4) {
                                if (xhr.status === 200) {
                                    const response = JSON.parse(xhr.responseText);
                                    if (response.success) {
                                        // Xóa thành công, chuyển về trang danh sách
                                        window.location.href = `${window.location.origin}/recruitment?clubId=${campaign.clubID}`;
                                    } else {
                                        alert('Lỗi khi xóa chiến dịch: ' + response.message);
                                    }
                                } else {
                                    alert('Đã xảy ra lỗi khi xử lý yêu cầu.');
                                }
                                deleteModal.classList.add('hidden');
                            }
                        };
                        xhr.send(`recruitmentId=${campaignIdToDelete}`);
                    }
                });
            }
        });
    </script>
    
    <script src="${pageContext.request.contextPath}/js/recruitmentCommon.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="${pageContext.request.contextPath}/js/viewRecruitmentCampaign.js?v=<%= System.currentTimeMillis() %>"></script>
</body>
</html>
