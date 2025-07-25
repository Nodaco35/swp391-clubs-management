<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Form Management - UniClub</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;500;600;700&family=Poppins:wght@300;400;500;600;700&family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/formManagement.css">
    <script src="https://cdn.jsdelivr.net/npm/qrcode@1.5.3/build/qrcode.min.js"></script>
</head>
<body>
    <jsp:include page="/view/events-page/header.jsp" />
    
    <div class="form-management-container">
        <!-- Page Header -->
        <div class="page-header">
            <h1><i class="fas fa-clipboard-list"></i>Quản lý Form</h1>
            <div class="header-actions">            
                <a href="${pageContext.request.contextPath}/formBuilder?clubId=${param.clubId}" class="btn btn-primary">
                    <i class="fas fa-plus"></i> Tạo Form Mới
                </a>
            </div>
        </div>        <!-- Search and Filter -->
        <div class="search-section">
            <div class="filter-section">
                <label for="formTypeFilter">
                    <i class="fas fa-filter"></i> Lọc theo loại form:
                </label>
                <select id="formTypeFilter">
                    <option value="">Tất cả loại form</option>
                    <option value="Club">Đăng ký thành viên</option>
                    <option value="Event">Đăng ký sự kiện</option>
                </select>
            </div>
        </div>

        <!-- Tabs -->
        <div class="tab-navigation">
            <button class="tab-btn active" data-tab="saved">
                Form đã lưu
                <span class="count">${savedFormsCount}</span>
            </button>
            <button class="tab-btn" data-tab="published">
                Form đã xuất bản
                <span class="count">${publishedFormsCount}</span>
            </button>
        </div>

        <!-- Saved Forms Tab -->
        <div id="saved-tab" class="tab-content active">
            <div class="forms-grid">
                <c:choose>
                    <c:when test="${empty savedForms}">
                        <div class="empty-state">
                            <i class="fas fa-inbox"></i>
                            <h3>Chưa có form nào được lưu</h3>
                            <p>Tạo form mới để bắt đầu thu thập đăng ký từ thành viên.</p>                            
                            <a href="${pageContext.request.contextPath}/formBuilder?clubId=${param.clubId}" class="btn btn-primary">
                                <i class="fas fa-plus"></i> Tạo Form Đầu Tiên
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="form" items="${savedForms}">
                            <div class="form-card" data-form-title="${form.title}" data-form-type="${form.formType}">
                                <div class="form-header">
                                    <span class="form-type-badge ${form.formType.toLowerCase()}">${form.formType}</span>
                                    <span class="form-status draft">Bản nháp</span>
                                </div>
                                <div class="form-content">
                                    <h3 class="form-title">${form.title}</h3>
                                    <div class="form-meta">
                                        <div class="meta-item">
                                            <i class="fas fa-eye-slash"></i>
                                            <span>Chưa công khai</span>
                                        </div>
                                        <div class="meta-item">
                                            <span>ID: ${form.formId}</span>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-actions">
                                    <button class="btn btn-outline btn-sm edit-form" data-form-id="${form.formId}">
                                        <i class="fas fa-edit"></i> Chỉnh sửa
                                    </button>
                                    <button class="btn btn-success btn-sm publish-form" data-form-id="${form.formId}">
                                        <i class="fas fa-globe"></i> Xuất bản
                                    </button>
                                    <button class="btn btn-danger btn-sm delete-form" 
                                            data-form-id="${form.formId}" 
                                            data-form-title="${form.title}">
                                        <i class="fas fa-trash"></i> Xóa
                                    </button>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Published Forms Tab -->
        <div id="published-tab" class="tab-content">
            <div class="forms-grid">
                <c:choose>
                    <c:when test="${empty publishedForms}">
                        <div class="empty-state">
                            <i class="fas fa-globe"></i>
                            <h3>Chưa có form nào được xuất bản</h3>
                            <p>Xuất bản form để cho phép mọi người đăng ký.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="form" items="${publishedForms}">
                            <div class="form-card published" data-form-title="${form.title}" data-form-type="${form.formType}">
                                <div class="form-header">
                                    <span class="form-type-badge ${form.formType.toLowerCase()}">${form.formType}</span>
                                    <span class="form-status published">Đã xuất bản</span>
                                </div>
                                <div class="form-content">
                                    <h3 class="form-title">${form.title}</h3>
                                    <div class="form-link">
                                        <input type="text" class="public-link" readonly 
                                               value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/applicationForm?formId=${form.formId}">
                                    </div>
                                </div>                                <div class="form-actions">
                                    <button class="btn btn-info btn-sm view-responses" data-form-id="${form.formId}" data-club-id="${param.clubId}" data-form-type="${form.formType eq 'Event' ? 'event' : 'club'}">
                                        <i class="fas fa-chart-bar"></i> Xem phản hồi
                                    </button>
                                    <button class="btn btn-outline btn-sm copy-link" data-form-id="${form.formId}">
                                        <i class="fas fa-copy"></i> Sao chép link
                                    </button>
                                    <button class="btn btn-secondary btn-sm unpublish-form" 
                                            data-form-id="${form.formId}" 
                                            data-form-title="${form.title}">
                                        <i class="fas fa-eye-slash"></i> Hủy xuất bản
                                    </button>
                                        <a class="btn btn-sm btn-primary"
                                           href="${pageContext.request.contextPath}/applicationForm?formId=${form.formId}">
                                            Xem form
                                        </a>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Delete Modal -->
    <div id="deleteModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Xác nhận xóa form</h3>
                <span class="close">&times;</span>
            </div>
            <div class="modal-body">
                <p>Bạn có chắc chắn muốn xóa form "<strong id="deleteFormTitle"></strong>"?</p>
                <div class="warning-text">
                    <i class="fas fa-exclamation-triangle"></i>
                    Hành động này không thể hoàn tác. Tất cả dữ liệu phản hồi sẽ bị xóa vĩnh viễn.
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-outline cancel-delete">Hủy</button>
                <button class="btn btn-danger confirm-delete">Xóa form</button>
            </div>
        </div>
    </div>

    <!-- Unpublish Modal -->
    <div id="unpublishModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Xác nhận hủy xuất bản</h3>
                <span class="close">&times;</span>
            </div>
            <div class="modal-body">
                <p>Bạn có chắc chắn muốn hủy xuất bản form "<strong id="unpublishFormTitle"></strong>"?</p>
                <div class="info-text">
                    <i class="fas fa-info-circle"></i>
                    Form sẽ không còn công khai và người dùng không thể truy cập được nữa.
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-outline cancel-unpublish">Hủy</button>
                <button class="btn btn-secondary confirm-unpublish">Hủy xuất bản</button>
            </div>
        </div>
    </div>

    <!-- Toast Notifications -->
    <div id="successToast" class="toast success">
        <i class="fas fa-check-circle"></i>
        <span class="toast-message"></span>
    </div>    <div id="errorToast" class="toast error">
        <i class="fas fa-exclamation-circle"></i>
        <span class="toast-message"></span>
    </div>    <script src="${pageContext.request.contextPath}/js/formManagement.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="${pageContext.request.contextPath}/js/formResponseCheck.js?v=<%= System.currentTimeMillis() %>"></script>
</body>
</html>
