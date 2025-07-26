<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Tuyển Quân - UniClub</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;500;600;700&family=Poppins:wght@300;400;500;600;700&family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/recruitmentActivitiesManagement.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://cdn.jsdelivr.net/npm/qrcode@1.5.3/build/qrcode.min.js"></script>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">
</head>
<body style="background-color: #d8d7ce;">
    <jsp:include page="components/sidebar.jsp" />
    
    <!-- Toast Container -->
    <div class="toast-container" id="toastContainer"></div>
    
    <div class="container mx-auto px-4 py-8">
        <a href="${pageContext.request.contextPath}/myclub" class="inline-flex items-center text-blue-500 hover:text-blue-700 mb-6">
            <i class="fas fa-arrow-left mr-2"></i> Quay lại Câu Lạc Bộ Của Tôi
        </a>
        
        <div class="flex justify-between items-center mb-6">
            <h1 class="text-3xl font-bold text-gray-800">Quản Lý Hoạt Động Tuyển Quân</h1>
            <a href="${pageContext.request.contextPath}/recruitment/form?clubId=${param.clubId}" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded inline-flex items-center">
                <i class="fas fa-plus mr-2"></i> Tạo Hoạt Động Mới
            </a>
        </div>
        
        <div class="bg-white rounded-lg shadow-md overflow-hidden border border-gray-200">
            <!-- Tabs -->
            <div class="flex border-b">
                <button class="tab-button active px-6 py-3 text-blue-600 border-b-2 border-blue-600 font-medium" data-tab="all">Tất Cả</button>
                <button class="tab-button px-6 py-3 text-gray-600 font-medium" data-tab="ongoing">Đang Diễn Ra</button>
                <button class="tab-button px-6 py-3 text-gray-600 font-medium" data-tab="upcoming">Sắp Tới</button>
                <button class="tab-button px-6 py-3 text-gray-600 font-medium" data-tab="closed">Đã Hoàn Thành</button>
            </div>
            
            <!-- Danh sách hoạt động -->
            <div class="p-6">
                <div class="mb-4">
                    <input type="text" id="searchCampaign" placeholder="Tìm kiếm hoạt động..." class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                </div>
                
                <c:choose>
                    <c:when test="${empty campaigns}">
                        <div class="text-center py-8">
                            <div class="text-gray-400 text-5xl mb-4"><i class="fas fa-clipboard-list"></i></div>
                            <h3 class="text-xl font-medium text-gray-700 mb-2">Chưa có hoạt động nào</h3>
                            <p class="text-gray-500">Bắt đầu bằng cách tạo một hoạt động tuyển quân mới.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="overflow-x-auto">
                            <table class="min-w-full divide-y divide-gray-200">
                                <thead>
                                    <tr>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tiêu Đề</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Gen</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Thời Gian</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Trạng Thái</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hành Động</th>
                                    </tr>
                                </thead>
                                <tbody class="bg-white divide-y divide-gray-200" id="campaignTable">
                                    <c:forEach items="${campaigns}" var="campaign">
                                        <tr class="campaign-item" data-status="${campaign.status.toLowerCase()}">
                                            <td class="px-6 py-4 whitespace-nowrap">
                                                <div class="font-medium text-gray-900">${campaign.title}</div>
                                                <div class="text-sm text-gray-500">${campaign.description}</div>
                                            </td>
                                            <td class="px-6 py-4 whitespace-nowrap">
                                                <div class="text-sm text-gray-900">Gen ${campaign.gen}</div>
                                            </td>
                                            <td class="px-6 py-4 whitespace-nowrap">
                                                <div class="text-sm text-gray-900"><fmt:formatDate value="${campaign.startDate}" pattern="dd/MM/yyyy" /> - <fmt:formatDate value="${campaign.endDate}" pattern="dd/MM/yyyy" /></div>
                                            </td>
                                            <td class="px-6 py-4 whitespace-nowrap">
                                                <c:choose>
                                                    <c:when test="${campaign.status eq 'ONGOING'}">
                                                        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">Đang diễn ra</span>
                                                    </c:when>
                                                    <c:when test="${campaign.status eq 'UPCOMING'}">
                                                        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800">Sắp tới</span>
                                                    </c:when>
                                                    <c:when test="${campaign.status eq 'CLOSED'}">
                                                        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800">Đã hoàn thành</span>
                                                    </c:when>
                                                </c:choose>
                                            </td>
                                            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                                <div class="relative inline-block text-left dropdown">
                                                    <button class="inline-flex justify-center w-full px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                                                        Thao tác
                                                        <i class="fas fa-chevron-down ml-2 mt-1"></i>
                                                    </button>
                                                    <div class="dropdown-menu hidden origin-top-right absolute right-0 mt-2 w-48 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 py-1 z-10">
                                                        <a href="${pageContext.request.contextPath}/recruitment/view?id=${campaign.recruitmentID}" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                                                            <i class="fas fa-eye text-blue-500 mr-2"></i> Xem chi tiết
                                                        </a>
                                                        <a href="${pageContext.request.contextPath}/recruitment/form/edit?recruitmentId=${campaign.recruitmentID}" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                                                            <i class="fas fa-edit text-blue-500 mr-2"></i> Sửa
                                                        </a>
                                                        <c:if test="${campaign.status eq 'ONGOING' || campaign.status eq 'UPCOMING'}">
                                                            <button class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 close-campaign" data-id="${campaign.recruitmentID}">
                                                                <i class="fas fa-check-circle text-yellow-500 mr-2"></i> Kết thúc
                                                            </button>
                                                        </c:if>
                                                        <button class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 delete-campaign" data-id="${campaign.recruitmentID}">
                                                            <i class="fas fa-trash-alt text-red-500 mr-2"></i> Xóa
                                                        </button>
                                                    </div>
                                                </div>
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
    </div>
    
    <!-- Modal xác nhận xóa -->
    <div id="deleteModal" class="hidden fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg p-8 max-w-md mx-auto border border-gray-200">
            <h3 class="text-xl font-bold mb-4">Xác nhận xóa</h3>
            <p class="text-gray-600 mb-6">Bạn có chắc chắn muốn xóa hoạt động tuyển quân này? Hành động này không thể hoàn tác.</p>
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
    
    <!-- Modal xác nhận kết thúc hoạt động -->
    <div id="closeModal" class="hidden fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg p-8 max-w-md mx-auto border border-gray-200">
            <h3 class="text-xl font-bold mb-4">Xác nhận kết thúc hoạt động</h3>
            <p class="text-gray-600 mb-6">Bạn có chắc chắn muốn kết thúc hoạt động tuyển quân này? Trạng thái sẽ chuyển thành "Đã hoàn thành".</p>
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
    
    <script src="${pageContext.request.contextPath}/js/recruitmentCommon.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="${pageContext.request.contextPath}/js/recruitmentActivitiesManagement.js?v=<%= System.currentTimeMillis() %>"></script>
</body>
</html>
