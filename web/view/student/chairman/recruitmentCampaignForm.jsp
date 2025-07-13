<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>
                    <c:choose>
                        <c:when test="${mode == 'create'}">Tạo Hoạt Động Tuyển Quân Mới</c:when>
                        <c:when test="${mode == 'edit'}">Chỉnh Sửa Hoạt Động Tuyển Quân</c:when>
                        <c:otherwise>
                            <c:choose>
                                <c:when test="${empty campaign.recruitmentID}">Tạo Hoạt Động Tuyển Quân Mới</c:when>
                                <c:otherwise>Chỉnh Sửa Hoạt Động Tuyển Quân</c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                    - UniClub
                </title>
                <link rel="preconnect" href="https://fonts.googleapis.com">
                <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                <link
                    href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;500;600;700&family=Poppins:wght@300;400;500;600;700&family=Roboto:wght@300;400;500;700&display=swap"
                    rel="stylesheet">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/recruitmentForm.css">
            </head>
            <!-- Truyền dữ liệu stages từ server sang JavaScript -->
            <script>
                // Biến global để lưu trữ dữ liệu các vòng tuyển từ server
                var serverStageData = false;
                var stagesFromServer = [];

                <c:if test="${not empty stages}">
                    serverStageData = true;
                    // Sử dụng Gson để chuyển đổi dữ liệu từ Java List sang JSON string
                    var stagesFromServer = ${stagesJson != null ? stagesJson : '[]'};
                </c:if>
            </script>

            <body style="background-color: #d8d7ce;">
                <jsp:include page="/view/events-page/header.jsp" />

                <!-- Toast Container -->
                <div class="toast-container" id="toastContainer"></div>

                <div class="container">
                    <div class="header-container">
                        <a href="${pageContext.request.contextPath}/recruitment?clubId=${empty param.clubId ? campaign.clubID : param.clubId}"
                            class="back-link" id="backLink">
                            <i class="fas fa-arrow-left"></i> Quay lại
                        </a>
                        <h1 class="page-title">
                            <c:choose>
                                <c:when test="${mode == 'create'}">Tạo Hoạt Động Tuyển Quân Mới</c:when>
                                <c:when test="${mode == 'edit'}">Chỉnh Sửa Hoạt Động Tuyển Quân</c:when>
                                <c:otherwise>
                                    <c:choose>
                                        <c:when test="${empty campaign.recruitmentID}">Tạo Hoạt Động Tuyển Quân Mới
                                        </c:when>
                                        <c:otherwise>Chỉnh Sửa Hoạt Động Tuyển Quân</c:otherwise>
                                    </c:choose>
                                </c:otherwise>
                            </c:choose>
                        </h1>
                    </div>

                    <!-- Stepper -->
                    <div class="stepper-container">
                        <div class="stepper">
                            <div class="step">
                                <div class="step-circle active" id="step1-circle">1</div>
                                <span class="step-text active" id="step1-text">Thông tin cơ bản</span>
                            </div>
                            <div class="step-line inactive" id="step1-2-line"></div>
                            <div class="step">
                                <div class="step-circle inactive" id="step2-circle">2</div>
                                <span class="step-text inactive" id="step2-text">Cấu hình các vòng tuyển</span>
                            </div>
                            <div class="step-line inactive" id="step2-3-line"></div>
                            <div class="step">
                                <div class="step-circle inactive" id="step3-circle">3</div>
                                <span class="step-text inactive" id="step3-text">Xác nhận thông tin</span>
                            </div>
                        </div>
                    </div>
                </div>

                <form id="recruitmentForm"
                    data-mode="${empty campaign.recruitmentID || campaign.recruitmentID == 0 ? 'create' : 'edit'}"
                    class="mb-6">
                    <input type="hidden" name="applicationCount"
                        value="${applicationCount != null ? applicationCount : 0}" />
                    <input type="hidden" name="hasApplications"
                        value="${hasApplications != null ? hasApplications : 'false'}" />
                    <c:if test="${not empty campaign.recruitmentID && campaign.recruitmentID > 0}">
                        <input type="hidden" name="recruitmentId" value="${campaign.recruitmentID}" />
                        <input type="hidden" name="status" value="${campaign.status}" />
                    </c:if>
                    <input type="hidden" name="clubId"
                        value="${empty param.clubId ? (empty campaign.clubID ? selectedClubId : campaign.clubID) : param.clubId}" />
                    <input type="hidden" name="clubID"
                        value="${empty param.clubId ? (empty campaign.clubID ? selectedClubId : campaign.clubID) : param.clubId}" />
                    <!-- Thêm data attribute cho form để dễ dàng truy cập clubId -->
                    <c:set var="effectiveClubId"
                        value="${empty param.clubId ? (empty campaign.clubID ? selectedClubId : campaign.clubID) : param.clubId}" />

                    <!-- Step 1: Thông tin cơ bản -->
                    <div id="step1" class="step-panel active">
                        <div class="card">
                            <h2 class="card-title">Thông tin cơ bản</h2>

                            <div class="form-grid">
                                <div>
                                    <div class="form-group">
                                        <label for="gen" class="form-label">Gen <span class="required">*</span></label>
                                        <input type="number" id="gen" name="gen" min="1" value="${campaign.gen}"
                                            class="form-control" required>
                                        <p class="form-text">Số thứ tự của thế hệ CLB</p>
                                    </div>

                                    <div class="form-group">
                                        <label for="templateId" class="form-label">Form đăng ký <span
                                                class="required">*</span></label>
                                        <c:if test="${not empty formWarning}">
                                            <div class="alert alert-warning"
                                                style="margin-bottom: 10px; padding: 5px; color: #856404; background-color: #fff3cd; border: 1px solid #ffeeba; border-radius: 4px;">
                                                ${formWarning}
                                            </div>
                                        </c:if>
                                        <select id="templateId" name="templateId" class="form-control" required>
                                            <option value="">-- Chọn form đăng ký --</option>
                                            <c:forEach items="${publishedTemplates}" var="template">
                                                <option value="${template.templateId}"
                                                    ${template.templateId==campaign.templateID ? 'selected' : '' }>
                                                    ${template.title}
                                                    <c:if
                                                        test="${template.clubId != param.clubId && template.clubId != campaign.clubID}">
                                                        (Form CLB #${template.clubId})
                                                    </c:if>
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <p class="form-text">Form đăng ký thành viên cho hoạt động</p>
                                    </div>
                                </div>

                                <div>
                                    <div class="form-group">
                                        <label for="title" class="form-label">Tiêu đề hoạt động <span
                                                class="required">*</span></label>
                                        <input type="text" id="title" name="title" value="${campaign.title}"
                                            class="form-control" required>
                                        <p class="form-text">Tiêu đề cho hoạt động tuyển quân</p>
                                    </div>

                                    <div class="form-group">
                                        <label for="description" class="form-label">Mô tả <span
                                                class="required">*</span></label>
                                        <textarea id="description" name="description" rows="4" class="form-control"
                                            required>${campaign.description}</textarea>
                                        <p class="form-text">Mô tả ngắn về hoạt động tuyển quân</p>
                                    </div>
                                </div>
                            </div>

                            <div class="form-grid">
                                <div class="form-group">
                                    <label for="startDate" class="form-label">Ngày bắt đầu <span
                                            class="required">*</span></label>
                                    <input type="date" id="startDate" name="startDate"
                                        value="<fmt:formatDate value='${campaign.startDate}' pattern='yyyy-MM-dd' />"
                                        class="form-control" required>
                                </div>

                                <div class="form-group">
                                    <label for="endDate" class="form-label">Ngày kết thúc <span
                                            class="required">*</span></label>
                                    <input type="date" id="endDate" name="endDate"
                                        value="<fmt:formatDate value='${campaign.endDate}' pattern='yyyy-MM-dd' />"
                                        class="form-control" required>
                                </div>
                            </div>
                        </div>

                        <div class="button-group-right">
                            <button type="button" id="step1NextBtn" class="btn btn-primary">
                                Tiếp theo <i class="fas fa-arrow-right"></i>
                            </button>
                        </div>
                    </div>

                    <!-- Step 2: Cấu hình các vòng tuyển -->
                    <div id="step2" class="step-panel">
                        <div class="card">
                            <h2 class="card-title">Cấu hình các vòng tuyển</h2>

                            <!-- Vòng nộp đơn -->
                            <div class="stage-card application">
                                <div class="stage-header">
                                    <div class="stage-icon application">
                                        <i class="fas fa-file-alt"></i>
                                    </div>
                                    <h3 class="stage-title">Vòng nộp đơn</h3>
                                </div>

                                <p class="stage-description">Ứng viên nộp đơn đăng ký và hồ sơ</p>

                                <div class="form-grid">
                                    <div class="form-group">
                                        <label class="form-label">Ngày bắt đầu <span class="required">*</span></label>
                                        <input type="date" name="applicationStageStart" id="applicationStageStart"
                                            class="form-control" required>
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label">Ngày kết thúc <span class="required">*</span></label>
                                        <input type="date" name="applicationStageEnd" id="applicationStageEnd"
                                            class="form-control" required>
                                    </div>
                                </div>
                            </div>

                            <!-- Vòng phỏng vấn -->
                            <div class="stage-card interview">
                                <div class="stage-header">
                                    <div class="stage-icon interview">
                                        <i class="fas fa-comments"></i>
                                    </div>
                                    <h3 class="stage-title">Vòng phỏng vấn</h3>
                                </div>

                                <p class="stage-description">Phỏng vấn trực tiếp với ứng viên</p>

                                <div class="form-grid">
                                    <div class="form-group">
                                        <label class="form-label">Ngày bắt đầu <span class="required">*</span></label>
                                        <input type="date" name="interviewStageStart" id="interviewStageStart"
                                            class="form-control" required>
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label">Ngày kết thúc <span class="required">*</span></label>
                                        <input type="date" name="interviewStageEnd" id="interviewStageEnd"
                                            class="form-control" required>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="form-label">Địa điểm phỏng vấn</label>
                                    <select name="interviewLocationId" id="interviewLocationId" class="form-control">
                                        <option value="">-- Chọn địa điểm --</option>
                                        <c:forEach items="${locations}" var="location">

                                            <option value="${location.locationID}">${location.locationName} -
                                                ${location.typeLocation}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <!-- Vòng thử thách -->
                            <div class="stage-card training">
                                <div class="stage-header">
                                    <div class="stage-icon training">
                                        <i class="fas fa-tasks"></i>
                                    </div>
                                    <h3 class="stage-title">Vòng thử thách</h3>
                                </div>

                                <p class="stage-description">Thử thách năng lực công việc thực tế</p>

                                <div class="form-grid">
                                    <div class="form-group">
                                        <label class="form-label">Ngày bắt đầu <span class="required">*</span></label>
                                        <input type="date" name="challengeStageStart" id="challengeStageStart"
                                            class="form-control" required>
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label">Ngày kết thúc <span class="required">*</span></label>
                                        <input type="date" name="challengeStageEnd" id="challengeStageEnd"
                                            class="form-control" required>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="button-group">
                            <button type="button" id="step2PrevBtn" class="btn btn-secondary">
                                <i class="fas fa-arrow-left"></i> Quay lại
                            </button>
                            <button type="button" id="step2NextBtn" class="btn btn-primary">
                                Tiếp theo <i class="fas fa-arrow-right"></i>
                            </button>
                        </div>
                    </div>

                    <!-- Step 3: Xác nhận thông tin -->
                    <div id="step3" class="step-panel hidden">
                        <div class="card">
                            <h2 class="card-title">Xác nhận thông tin</h2>

                            <!-- Thông tin cơ bản -->
                            <div class="mb-6">
                                <h3 class="stage-title">Thông tin cơ bản</h3>
                                <div class="form-grid">
                                    <div class="summary-item">
                                        <p class="summary-label">Gen:</p>
                                        <p id="confirmGen" class="summary-value"></p>
                                    </div>
                                    <div class="summary-item">
                                        <p class="summary-label">Thời gian:</p>
                                        <p id="confirmTime" class="summary-value"></p>
                                    </div>
                                    <div class="summary-item">
                                        <p class="summary-label">Form đăng ký:</p>
                                        <p id="confirmForm" class="summary-value"></p>
                                    </div>
                                </div>
                            </div>

                            <!-- Các vòng tuyển -->
                            <div>
                                <h3 class="stage-title">Các vòng tuyển</h3>

                                <div>
                                    <!-- Vòng nộp đơn -->
                                    <div class="stage-card application">
                                        <div class="stage-header">
                                            <div class="stage-icon application">
                                                <i class="fas fa-file-alt"></i>
                                            </div>
                                            <h4 class="stage-title">Vòng nộp đơn</h4>
                                        </div>
                                        <p id="confirmApplicationStage" class="stage-description"></p>
                                    </div>

                                    <!-- Vòng phỏng vấn -->
                                    <div class="stage-card interview">
                                        <div class="stage-header">
                                            <div class="stage-icon interview">
                                                <i class="fas fa-comments"></i>
                                            </div>
                                            <h4 class="stage-title">Vòng phỏng vấn</h4>
                                        </div>
                                        <p id="confirmInterviewStage" class="stage-description"></p>
                                    </div>

                                    <!-- Vòng thử thách -->
                                    <div class="stage-card training">
                                        <div class="stage-header">
                                            <div class="stage-icon training">
                                                <i class="fas fa-tasks"></i>
                                            </div>
                                            <h4 class="stage-title">Vòng thử thách</h4>
                                        </div>
                                        <p id="confirmChallengeStage" class="stage-description"></p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="button-group">
                            <button type="button" id="step3PrevBtn" class="btn btn-secondary">
                                <i class="fas fa-arrow-left"></i> Quay lại
                            </button>
                            <button type="submit" id="submitBtn" class="btn btn-primary">
                                <c:choose>
                                    <c:when test="${mode == 'create'}">Tạo hoạt động</c:when>
                                    <c:when test="${mode == 'edit'}">Cập nhật hoạt động</c:when>
                                    <c:otherwise>
                                        <c:choose>
                                            <c:when test="${empty campaign.recruitmentID}">Tạo hoạt động</c:when>
                                            <c:otherwise>Cập nhật hoạt động</c:otherwise>
                                        </c:choose>
                                    </c:otherwise>
                                </c:choose>
                            </button>
                        </div>
                    </div>
                </form>
                </div>

                <!-- Script cho form và wizard -->
                <script src="${pageContext.request.contextPath}/js/recruitmentSaver.js"></script>
                <script src="${pageContext.request.contextPath}/js/recruitmentCommon.js"></script>
                <script src="${pageContext.request.contextPath}/js/createEditRecruitmentCampaign.js"></script>
                <!-- Back to top button -->
                <div class="back-to-top" id="backToTop">
                    <i class="fas fa-arrow-up"></i>
                </div>

                <script>
                    // Khởi tạo FormSaver khi DOM đã sẵn sàng - CHỈ cho chế độ tạo mới
                    document.addEventListener('DOMContentLoaded', function () {
                        const recruitmentForm = document.getElementById('recruitmentForm');
                        const formMode = recruitmentForm.dataset.mode;

                        // CHỈ khởi tạo FormSaver khi ở chế độ tạo mới
                        if (formMode === 'create') {
                            console.log('[FormSaver] Khởi tạo FormSaver cho chế độ tạo mới');

                            // Khởi tạo FormSaver với ID form và prefix key
                            const formSaver = new FormSaver('recruitmentForm', 'recruitment_campaign', 60); // 60 phút

                            // Kiểm tra nếu có dữ liệu đã lưu và hiện prompt khôi phục
                            const hasStoredData = localStorage.getItem(formSaver.storageKey);

                            if (hasStoredData) {
                                formSaver.showRestorePrompt(
                                    'Phát hiện dữ liệu bạn đã điền trước đó nhưng chưa lưu. Bạn có muốn khôi phục không?',
                                    function () {
                                        formSaver.restoreFormData();
                                        console.log('[FormSaver] Đã khôi phục dữ liệu đã lưu');
                                    }, // Khôi phục dữ liệu
                                    function () {
                                        formSaver.clearSavedData();
                                        console.log('[FormSaver] Đã xóa dữ liệu đã lưu theo yêu cầu người dùng');
                                    }   // Xóa dữ liệu đã lưu
                                );
                            }

                            // Xóa dữ liệu đã lưu khi form được submit thành công
                            recruitmentForm.addEventListener('submit', function () {
                                formSaver.clearSavedData();
                                console.log('[FormSaver] Đã xóa dữ liệu đã lưu do form được submit');
                            });
                        } else {
                            console.log('[FormSaver] Chế độ chỉnh sửa - Không khởi tạo FormSaver, hiển thị dữ liệu từ server');

                            // Trong chế độ edit, đảm bảo không có dữ liệu FormSaver cũ can thiệp
                            // Sử dụng static method để xóa dữ liệu cũ
                            FormSaver.clearOldFormData('recruitment_campaign');
                        }
                    });
                </script>

                <script>
                    // Back to top button
                    const backToTopBtn = document.getElementById('backToTop');

                    window.addEventListener('scroll', function () {
                        if (window.pageYOffset > 300) {
                            backToTopBtn.classList.add('visible');
                        } else {
                            backToTopBtn.classList.remove('visible');
                        }
                    });

                    backToTopBtn.addEventListener('click', function () {
                        window.scrollTo({
                            top: 0,
                            behavior: 'smooth'
                        });
                    });

                    // Đảm bảo lấy clubId đúng
                    document.addEventListener('DOMContentLoaded', function () {
                        // Lấy clubId từ form
                        const clubIdInput = document.querySelector('input[name="clubId"]');
                        const clubIDInput = document.querySelector('input[name="clubID"]');
                        const backLink = document.getElementById('backLink');

                        if (backLink && (clubIdInput || clubIDInput)) {
                            // Lấy clubId từ input đầu tiên có giá trị
                            const clubId = clubIdInput?.value || clubIDInput?.value;
                            if (clubId) {
                                // Đảm bảo URL quay lại có clubId
                                const href = backLink.getAttribute('href');
                                if (href && !href.includes('clubId=')) {
                                    backLink.setAttribute('href', href + (href.includes('?') ? '&' : '?') + 'clubId=' + clubId);
                                }
                            }

                            console.log("Đã cài đặt backlink với clubId: " + clubId);
                        }
                    });
                </script>
            </body>

            </html>