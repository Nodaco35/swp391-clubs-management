<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Form Builder - UniClub</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;500;600;700&family=Poppins:wght@300;400;500;600;700&family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/formBuilder.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/sortablejs@1.15.0/Sortable.min.js"></script>
</head>
<body>
<jsp:include page="/view/events-page/header.jsp" />
<div class="container">
    <section class="form-header">
        <div class="header-content">
            <div class="header-left">
                <h1 class="title">Form Builder</h1>
                <input type="text" id="formTitle" class="input-title" value="Đơn đăng ký" placeholder="Tiêu đề form" required>
            </div>
            <div class="header-actions">
                <button id="saveBtn" class="btn btn-outline">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                        <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path>
                        <polyline points="17 21 17 13 7 13 7 21"></polyline>
                        <polyline points="7 3 7 8 15 8"></polyline>
                    </svg>
                    Lưu
                </button>
                <button id="publishBtn" class="btn btn-primary">Xuất bản</button>
            </div>
        </div>
    </section>

    <main class="main-content">
        <!-- Thông báo lưu thất bại-->
        <c:if test="${param.error == 'true'}">
            <div id="saveErrorAlert" class="alert alert-danger">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="15" y1="9" x2="9" y2="15"></line>
                    <line x1="9" y1="9" x2="15" y2="15"></line>
                </svg>
                <div class="alert-content">
                    <h4 class="alert-title">Lưu thất bại</h4>
                    <p class="alert-description">
                        <c:choose>
                            <c:when test="${not empty param.message}">
                                ${param.message}
                            </c:when>
                            <c:otherwise>
                                Đã xảy ra lỗi khi lưu form. Vui lòng thử lại sau!
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </div>
        </c:if>
        <!-- Thông báo lưu thành công -->
        <c:if test="${param.success == 'true'}">
            <div id="saveSuccessAlert" class="alert alert-success">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                    <polyline points="22 4 12 14.01 9 11.01"></polyline>
                </svg>
                <div class="alert-content">
                    <h4 class="alert-title">
                        <c:choose>
                            <c:when test="${param.action == 'publish'}">Đã xuất bản thành công</c:when>
                            <c:otherwise>Đã lưu thành công</c:otherwise>
                        </c:choose>
                    </h4>
                    <p class="alert-description">
                        <c:choose>
                            <c:when test="${param.action == 'publish'}">Form của bạn đã được xuất bản và có thể sử dụng.</c:when>
                            <c:otherwise>Form của bạn đã được lưu thành công.</c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </div>
        </c:if>

        <!-- Tabs -->
        <div class="tabs">
            <div class="tabs-list">
                <button class="tab-item active" data-tab="edit">Chỉnh sửa</button>
                <button class="tab-item" data-tab="preview">Xem trước</button>
            </div>
            <!-- Edit tab Content -->
            <div id="editTab" class="tab-content active">
                <!-- Basic Info Card -->
                <div class="card">
                    <c:if test="${not empty formQuestions}">
                        <script>
                            window.existingQuestions = [
                                <c:forEach var="question" items="${formQuestions}" varStatus="status">
                                {
                                    id: "${question.templateId}", // TemplateID để xác định câu hỏi
                                    type: "${question.fieldType.toLowerCase()}", // Chuyển thành chữ thường để khớp với form
                                    label: "${question.fieldName}", // Tên câu hỏi
                                    required: ${question.isRequired()}, // Có bắt buộc hay không
                                    options: ${question.options != null ? '"' + question.options + '"' : 'null'} // Tùy chọn nếu có
                                }<c:if test="${not status.last}">,</c:if>
                                </c:forEach>
                            ];

                            // Hàm để hiển thị các câu hỏi đã lưu
                            document.addEventListener("DOMContentLoaded", function() {
                                if (window.existingQuestions && window.existingQuestions.length > 0) {
                                    window.existingQuestions.forEach(function(q) {
                                        addQuestion(q.type, q.label, q.required, q.options, q.id);
                                    });
                                }
                            });
                        </script>
                    </c:if>
                    <div class="card-header">
                        <h2 class="card-title">Đơn đăng ký</h2>
                    </div>
                    <div class="card-content">
                            <div class="form-group">
                                <label class="form-label">Chọn loại form:</label>
                                <select id="formType" name="eventType" class="form-select mb-2" onchange="toggleEventField(this)" required>
                                    <option value="">Chọn loại form</option>
                                    <option value="event">Đăng ký tham gia sự kiện</option>
                                    <option value="member">Đăng ký làm thành viên</option>
                                </select>
                                <div class="error-feedback" id="formTypeError" style="color: #dc3545; font-size: 0.875rem; margin-top: 5px; display: none;">
                                    Vui lòng chọn loại form
                                </div>
                                <div id="eventNameField" class="mb-2" style="display: none;">
                                    <label class="form-label">Tên sự kiện:</label>
                                    <input type="text" name="eventName" class="form-input" placeholder="Nhập tên sự kiện">
                                </div>
                            </div>
                        </div>
                    </div>
                <!-- Questions Section -->
                <div class="section-header">
                    <h2 class="section-title">Câu hỏi (<span id="questionCount">3</span>/20)</h2>
                    <div class="section-actions">
                        <button id="addInfoBtn" class="btn btn-outline">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                                <polyline points="14 2 14 8 20 8"></polyline>
                                <line x1="16" y1="13" x2="8" y2="13"></line>
                                <line x1="16" y1="17" x2="8" y2="17"></line>
                                <polyline points="10 9 9 9 8 9"></polyline>
                            </svg>
                            Thêm thông tin
                        </button>
                        <button id="addQuestionBtn" class="btn btn-primary">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                <line x1="12" y1="5" x2="12" y2="19"></line>
                                <line x1="5" y1="12" x2="19" y2="12"></line>
                            </svg>
                            Thêm câu hỏi
                        </button>
                    </div>
                </div>

                <!-- Max Questions Alert -->
                <div id="maxQuestionsAlert" class="alert alert-warning" style="display: none;">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="12" y1="8" x2="12" y2="12"></line>
                        <line x1="12" y1="16" x2="12.01" y2="16"></line>
                    </svg>
                    <div class="alert-content">
                        <h4 class="alert-title">Đã đạt giới hạn</h4>
                        <p class="alert-description">Bạn đã đạt đến giới hạn 20 câu hỏi cho mỗi form.</p>
                    </div>
                </div>

                <!-- Questions List -->
                <div id="questionsList" class="questions-list">
                    <!-- Question 1: Họ và tên (Required) -->
                    <div class="card question-card required-question" data-id="q1" draggable="true">
                        <div class="card-header">
                            <div class="question-header">
                                <div class="drag-handle">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <circle cx="9" cy="12" r="1"></circle>
                                        <circle cx="9" cy="5" r="1"></circle>
                                        <circle cx="9" cy="19" r="1"></circle>
                                        <circle cx="15" cy="12" r="1"></circle>
                                        <circle cx="15" cy="5" r="1"></circle>
                                        <circle cx="15" cy="19" r="1"></circle>
                                    </svg>
                                </div>
                                <span class="question-number">1</span>
                                <span class="required-badge">Cố định</span>
                            </div>
                            <div class="question-actions">
                                <button class="btn-icon move-up " title="Di chuyển lên" >
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <line x1="12" y1="19" x2="12" y2="5"></line>
                                        <polyline points="5 12 12 5 19 12"></polyline>
                                    </svg>
                                </button>
                                <button class="btn-icon move-down " title="Di chuyển xuống" >
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <line x1="12" y1="5" x2="12" y2="19"></line>
                                        <polyline points="19 12 12 19 5 12"></polyline>
                                    </svg>
                                </button>
                                <button class="btn-icon duplicate" title="Nhân bản">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                                        <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                                    </svg>
                                </button>
                                <button class="btn-icon delete " title="Xóa" >
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <polyline points="3 6 5 6 21 6"></polyline>
                                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                    </svg>
                                </button>
                            </div>
                        </div>
                        <div class="card-content">
                            <div class="form-row">
                                <div class="form-group">
                                    <label class="form-label">Nhãn câu hỏi</label>
                                    <input type="text" class="form-input question-label" value="Họ và tên">
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Loại câu hỏi</label>
                                    <select class="form-select question-type">
                                        <option value="text" selected>Văn bản ngắn</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Placeholder</label>
                                <input type="text" class="form-input question-placeholder" value="Nhập họ và tên của bạn">
                            </div>
                            <div class="form-group">
                                <div class="checkbox-wrapper">
                                    <input type="checkbox" id="required-q1" class="question-required" checked>
                                    <label for="required-q1">Bắt buộc</label>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Question 2: Email (Required) -->
                    <div class="card question-card required-question" data-id="q2" draggable="true">
                        <div class="card-header">
                            <div class="question-header">
                                <div class="drag-handle ">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <circle cx="9" cy="12" r="1"></circle>
                                        <circle cx="9" cy="5" r="1"></circle>
                                        <circle cx="9" cy="19" r="1"></circle>
                                        <circle cx="15" cy="12" r="1"></circle>
                                        <circle cx="15" cy="5" r="1"></circle>
                                        <circle cx="15" cy="19" r="1"></circle>
                                    </svg>
                                </div>
                                <span class="question-number">2</span>
                                <span class="required-badge">Cố định</span>
                            </div>
                            <div class="question-actions">
                                <button class="btn-icon move-up " title="Di chuyển lên" >
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <line x1="12" y1="19" x2="12" y2="5"></line>
                                        <polyline points="5 12 12 5 19 12"></polyline>
                                    </svg>
                                </button>
                                <button class="btn-icon move-down " title="Di chuyển xuống" >
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <line x1="12" y1="5" x2="12" y2="19"></line>
                                        <polyline points="19 12 12 19 5 12"></polyline>
                                    </svg>
                                </button>
                                <button class="btn-icon duplicate" title="Nhân bản">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                                        <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                                    </svg>
                                </button>
                                <button class="btn-icon delete " title="Xóa" >
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <polyline points="3 6 5 6 21 6"></polyline>
                                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                    </svg>
                                </button>
                            </div>
                        </div>
                        <div class="card-content">
                            <div class="form-row">
                                <div class="form-group">
                                    <label class="form-label">Nhãn câu hỏi</label>
                                    <input type="text" class="form-input question-label" value="Email">
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Loại câu hỏi</label>
                                    <select class="form-select question-type">
                                        <option value="email" selected>Email</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Placeholder</label>
                                <input type="text" class="form-input question-placeholder" value="Nhập địa chỉ email của bạn">
                            </div>
                            <div class="form-group">
                                <div class="checkbox-wrapper">
                                    <input type="checkbox" id="required-q2" class="question-required" checked>
                                    <label for="required-q2">Bắt buộc</label>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Question 3: Mã số sinh viên (Required) -->
                    <div class="card question-card required-question" data-id="q3" draggable="true">
                        <div class="card-header">
                            <div class="question-header">
                                <div class="drag-handle ">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <circle cx="9" cy="12" r="1"></circle>
                                        <circle cx="9" cy="5" r="1"></circle>
                                        <circle cx="9" cy="19" r="1"></circle>
                                        <circle cx="15" cy="12" r="1"></circle>
                                        <circle cx="15" cy="5" r="1"></circle>
                                        <circle cx="15" cy="19" r="1"></circle>
                                    </svg>
                                </div>
                                <span class="question-number">3</span>
                                <span class="required-badge">Cố định</span>
                            </div>
                            <div class="question-actions">
                                <button class="btn-icon move-up " title="Di chuyển lên" >
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <line x1="12" y1="19" x2="12" y2="5"></line>
                                        <polyline points="5 12 12 5 19 12"></polyline>
                                    </svg>
                                </button>
                                <button class="btn-icon move-down " title="Di chuyển xuống" >
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <line x1="12" y1="5" x2="12" y2="19"></line>
                                        <polyline points="19 12 12 19 5 12"></polyline>
                                    </svg>
                                </button>
                                <button class="btn-icon duplicate" title="Nhân bản">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                                        <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                                    </svg>
                                </button>
                                <button class="btn-icon delete " title="Xóa" >
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <polyline points="3 6 5 6 21 6"></polyline>
                                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                    </svg>
                                </button>
                            </div>
                        </div>
                        <div class="card-content">
                            <div class="form-row">
                                <div class="form-group">
                                    <label class="form-label">Nhãn câu hỏi</label>
                                    <input type="text" class="form-input question-label" value="Mã số sinh viên">
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Loại câu hỏi</label>
                                    <select class="form-select question-type">
                                        <option value="text" selected>Văn bản ngắn</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Placeholder</label>
                                <input type="text" class="form-input question-placeholder" value="Nhập mã số sinh viên của bạn">
                            </div>
                            <div class="form-group">
                                <div class="checkbox-wrapper">
                                    <input type="checkbox" id="required-q3" class="question-required" checked>
                                    <label for="required-q3">Bắt buộc</label>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Preview Tab Content -->
            <div id="previewTab" class="tab-content">
                <div class="preview-container">
                    <div class="card preview-card">
                        <div class="card-header">
                            <h2 id="previewTitle" class="card-title">Đơn đăng ký tham gia câu lạc bộ</h2>
                        </div>
                        <div class="card-content">
                            <form id="previewForm" class="preview-form">
                                <!-- Preview questions in JavaScript -->
                            </form>
                        </div>
                        <div class="card-footer">
                            <button type="button" class="btn btn-primary btn-block">Gửi đơn đăng ký</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<!-- Publish Dialog -->
<div id="publishDialog" class="dialog">
    <div class="dialog-overlay"></div>
    <div class="dialog-content">
        <div class="dialog-header">
            <h3 class="dialog-title">Xuất bản form</h3>
            <button class="dialog-close" aria-label="Close">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round">
                    <line x1="18" y1="6" x2="6" y2="18"></line>
                    <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
            </button>
        </div>
        <div class="dialog-body">
            <p>Khi xuất bản, form này sẽ được công khai và có thể được sử dụng để đăng ký.</p>
            <p class="mt-4">Bạn có chắc chắn muốn xuất bản form này?</p>
        </div>
        <div class="dialog-footer">
            <button class="btn btn-outline dialog-cancel">Hủy</button>
            <button id="confirmPublishBtn" class="btn btn-primary">Xuất bản</button>
        </div>
    </div>
</div>

<!-- Question Template (Hidden) -->
<template id="questionTemplate">
    <div class="card question-card" data-id="{{id}}">
        <div class="card-header">
            <div class="question-header">
                <div class="drag-handle">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="9" cy="12" r="1"></circle>
                        <circle cx="9" cy="5" r="1"></circle>
                        <circle cx="9" cy="19" r="1"></circle>
                        <circle cx="15" cy="12" r="1"></circle>
                        <circle cx="15" cy="5" r="1"></circle>
                        <circle cx="15" cy="19" r="1"></circle>
                    </svg>
                </div>
                <span class="question-number">{{number}}</span>
            </div>
            <div class="question-actions">
                <button class="btn-icon move-up" title="Di chuyển lên">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="12" y1="19" x2="12" y2="5"></line>
                        <polyline points="5 12 12 5 19 12"></polyline>
                    </svg>
                </button>
                <button class="btn-icon move-down" title="Di chuyển xuống">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="12" y1="5" x2="12" y2="19"></line>
                        <polyline points="19 12 12 19 5 12"></polyline>
                    </svg>
                </button>
                <button class="btn-icon duplicate" title="Nhân bản">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                        <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                    </svg>
                </button>
                <button class="btn-icon delete" title="Xóa">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="3 6 5 6 21 6"></polyline>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                    </svg>
                </button>
            </div>
        </div>
        <div class="card-content">
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Nhãn câu hỏi</label>
                    <input type="text" class="form-input question-label" value="Câu hỏi mới">
                </div>
                <div class="form-group">
                    <label class="form-label">Loại câu hỏi</label>
                    <select class="form-select question-type">
                        <option value="text" selected>Văn bản ngắn</option>
                        <option value="textarea">Văn bản dài</option>
                        <option value="email">Email</option>
                        <option value="tel">Số điện thoại</option>
                        <option value="number">Số</option>
                        <option value="date">Ngày</option>
                        <option value="radio">Trắc nghiệm (một lựa chọn)</option>
                        <option value="checkbox">Hộp kiểm (nhiều lựa chọn)</option>
                        <option value="select">Danh sách thả xuống</option>
                        <option value="info">Thông tin (chỉ đọc)</option>
                    </select>
                </div>
            </div>
            <div class="form-group placeholder-group">
                <label class="form-label">Placeholder</label>
                <input type="text" class="form-input question-placeholder" value="Nhập câu trả lời của bạn">
            </div>
            <!-- Add more questions here -->
            <div class="form-group info-group" style="display: none;">
                <label class="form-label">Nội dung thông tin</label>
                <textarea class="form-textarea question-content" rows="4" placeholder="Nhập nội dung thông tin..."></textarea>

                <label class="form-label">Hình ảnh</label>
                <div class="image-upload-container">
                    <input type="file" class="form-input question-image-file" accept="image/*" style="display: none;">
                    <div class="image-upload-area" onclick="this.previousElementSibling.click()">
                        <div class="upload-placeholder">
                            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                                <circle cx="8.5" cy="8.5" r="1.5"></circle>
                                <polyline points="21 15 16 10 5 21"></polyline>
                            </svg>
                            <p>Click để chọn ảnh hoặc kéo thả ảnh vào đây</p>
                            <small>Hỗ trợ: JPG, PNG, GIF (tối đa 5MB)</small>
                        </div>
                        <div class="image-preview-container" style="display: none;">
                            <img src="/placeholder.svg" alt="Preview" class="uploaded-image-preview">
                            <div class="image-actions">
                                <button type="button" class="btn-icon change-image" title="Thay đổi ảnh">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                    </svg>
                                </button>
                                <button type="button" class="btn-icon remove-image" title="Xóa ảnh">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <polyline points="3 6 5 6 21 6"></polyline>
                                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                    </svg>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="options-container" style="display: none;">
                <div class="options-header">
                    <label class="form-label">Tùy chọn</label>
                    <button class="btn btn-sm btn-outline add-option">
                        <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <line x1="12" y1="5" x2="12" y2="19"></line>
                            <line x1="5" y1="12" x2="19" y2="12"></line>
                        </svg>
                        Thêm tùy chọn
                    </button>
                </div>
                <div class="options-list">
                    <!-- Options will be added here -->
                </div>
            </div>
            <div class="form-group">
                <div class="checkbox-wrapper">
                    <input type="checkbox" id="required-{{id}}" class="question-required">
                    <label for="required-{{id}}">Bắt buộc</label>
                </div>
            </div>
        </div>
    </div>
</template>

<!-- Option Template (Hidden) -->
<template id="optionTemplate">
    <div class="option-item" data-id="{{id}}">
        <div class="option-number">{{number}}.</div>
        <input type="text" class="form-input option-value" value="{{value}}">
        <button class="btn-icon delete-option" title="Xóa tùy chọn">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="3 6 5 6 21 6"></polyline>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
            </svg>
        </button>
    </div>
</template>
<script src="${pageContext.request.contextPath}/js/formBuilder.js"></script>
<form id="formBuilderForm" action="${pageContext.request.contextPath}/formBuilder" method="post" style="display: none;">
    <input type="hidden" id="action" name="action">
    <input type="hidden" id="formTitleHidden" name="formTitle">
    <input type="hidden" id="formTypeHidden" name="formType">
    <input type="hidden" id="questionsHidden" name="questions">
</form>
</body>
</html>
