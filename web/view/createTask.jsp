<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo Nhiệm Vụ Mới</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <style>
        .form-container {
            max-width: 800px;
            margin: 2rem auto;
            padding: 2rem;
            background: white;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .form-header {
            border-bottom: 2px solid #007bff;
            padding-bottom: 1rem;
            margin-bottom: 2rem;
        }
        .form-header h2 {
            color: #007bff;
            margin: 0;
        }
        .form-group {
            margin-bottom: 1.5rem;
        }
        .required {
            color: #dc3545;
        }
        .select2-container .select2-selection--single {
            height: 38px;
            border: 1px solid #ced4da;
            border-radius: 0.375rem;
        }
        .select2-container--default .select2-selection--single .select2-selection__rendered {
            line-height: 36px;
            padding-left: 12px;
        }
        .select2-container--default .select2-selection--single .select2-selection__arrow {
            height: 36px;
        }
        .btn-create {
            background: linear-gradient(45deg, #007bff, #0056b3);
            border: none;
            padding: 12px 30px;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .btn-create:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
        }
        .alert {
            border-radius: 8px;
            border: none;
        }
        .member-search {
            position: relative;
        }
        .member-item {
            display: flex;
            align-items: center;
            padding: 8px 12px;
            cursor: pointer;
            border-radius: 4px;
            transition: background 0.2s;
        }
        .member-item:hover {
            background: #f8f9fa;
        }
        .member-avatar {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            margin-right: 10px;
            object-fit: cover;
        }
    </style>
</head>
<body class="bg-light">
    <div class="container">
        <div class="form-container">
            <!-- Header -->
            <div class="form-header">
                <div class="d-flex justify-content-between align-items-center">
                    <h2><i class="fas fa-tasks me-2"></i>Tạo Nhiệm Vụ Mới</h2>
                    <a href="department-tasks" class="btn btn-outline-secondary">
                        <i class="fas fa-arrow-left me-1"></i>Quay lại
                    </a>
                </div>
            </div>

            <!-- Thông báo lỗi -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-triangle me-2"></i>${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Form tạo nhiệm vụ -->
            <form action="create-task" method="post" id="createTaskForm">
                <div class="row">
                    <!-- Tiêu đề nhiệm vụ -->
                    <div class="col-md-12">
                        <div class="form-group">
                            <label for="title" class="form-label">
                                <i class="fas fa-heading me-1"></i>Tiêu đề nhiệm vụ <span class="required">*</span>
                            </label>
                            <input type="text" class="form-control" id="title" name="title" 
                                   value="${param.title}" required maxlength="200"
                                   placeholder="Nhập tiêu đề nhiệm vụ...">
                        </div>
                    </div>

                    <!-- Mô tả nhiệm vụ -->
                    <div class="col-md-12">
                        <div class="form-group">
                            <label for="description" class="form-label">
                                <i class="fas fa-align-left me-1"></i>Mô tả nhiệm vụ <span class="required">*</span>
                            </label>
                            <textarea class="form-control" id="description" name="description" rows="4" 
                                      required maxlength="1000" placeholder="Mô tả chi tiết nhiệm vụ cần thực hiện...">${param.description}</textarea>
                            <div class="form-text">Tối đa 1000 ký tự</div>
                        </div>
                    </div>

                    <!-- Ngày bắt đầu và kết thúc -->
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="startDate" class="form-label">
                                <i class="fas fa-calendar-alt me-1"></i>Ngày bắt đầu <span class="required">*</span>
                            </label>
                            <input type="date" class="form-control" id="startDate" name="startDate" 
                                   value="${param.startDate}" required>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="endDate" class="form-label">
                                <i class="fas fa-calendar-check me-1"></i>Ngày kết thúc <span class="required">*</span>
                            </label>
                            <input type="date" class="form-control" id="endDate" name="endDate" 
                                   value="${param.endDate}" required>
                        </div>
                    </div>

                    <!-- Chọn sự kiện -->
                    <div class="col-md-12">
                        <div class="form-group">
                            <label for="eventId" class="form-label">
                                <i class="fas fa-calendar-star me-1"></i>Sự kiện liên quan <span class="required">*</span>
                            </label>
                            <select class="form-select" id="eventId" name="eventId" required>
                                <option value="">-- Chọn sự kiện --</option>
                                <c:forEach var="event" items="${clubEvents}">
                                    <option value="${event.eventID}" 
                                            ${param.eventId == event.eventID ? 'selected' : ''}>
                                        ${event.eventName}
                                    </option>
                                </c:forEach>
                            </select>
                            <div class="form-text">Chọn sự kiện mà nhiệm vụ này thuộc về</div>
                        </div>
                    </div>

                    <!-- Chọn người phụ trách -->
                    <div class="col-md-12">
                        <div class="form-group">
                            <label for="assignedTo" class="form-label">
                                <i class="fas fa-user-check me-1"></i>Người phụ trách <span class="required">*</span>
                            </label>
                            <select class="form-select member-select" id="assignedTo" name="assignedTo" required>
                                <option value="">-- Tìm kiếm và chọn thành viên --</option>
                                <c:forEach var="member" items="${departmentMembers}">
                                    <option value="${member.userID}" 
                                            data-avatar="${member.avatar}"
                                            data-email="${member.email}"
                                            ${param.assignedTo == member.userID ? 'selected' : ''}>
                                        ${member.fullName} (${member.email})
                                    </option>
                                </c:forEach>
                            </select>
                            <div class="form-text">Chọn thành viên trong ban để giao nhiệm vụ</div>
                        </div>
                    </div>
                </div>

                <!-- Buttons -->
                <div class="d-flex justify-content-end gap-2 mt-4">
                    <a href="department-tasks" class="btn btn-secondary">
                        <i class="fas fa-times me-1"></i>Hủy bỏ
                    </a>
                    <button type="submit" class="btn btn-primary btn-create">
                        <i class="fas fa-plus me-1"></i>Tạo Nhiệm Vụ
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>

    <script>
        $(document).ready(function() {
            // Khởi tạo Select2 cho dropdown thành viên
            $('.member-select').select2({
                placeholder: "Tìm kiếm thành viên...",
                allowClear: true,
                templateResult: function (member) {
                    if (!member.id) {
                        return member.text;
                    }
                    
                    var avatar = $(member.element).data('avatar');
                    var email = $(member.element).data('email');
                    
                    var $member = $(
                        '<div class="member-item">' +
                            '<img class="member-avatar" src="' + (avatar || 'img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg') + '" alt="Avatar">' +
                            '<div>' +
                                '<div style="font-weight: 500;">' + member.text.split(' (')[0] + '</div>' +
                                '<div style="font-size: 0.85em; color: #6c757d;">' + email + '</div>' +
                            '</div>' +
                        '</div>'
                    );
                    return $member;
                },
                templateSelection: function (member) {
                    if (!member.id) {
                        return member.text;
                    }
                    return member.text.split(' (')[0];
                }
            });

            // Validation ngày
            $('#startDate, #endDate').on('change', function() {
                var startDate = new Date($('#startDate').val());
                var endDate = new Date($('#endDate').val());
                
                if (startDate && endDate && endDate < startDate) {
                    $('#endDate')[0].setCustomValidity('Ngày kết thúc phải sau ngày bắt đầu');
                } else {
                    $('#endDate')[0].setCustomValidity('');
                }
            });

            // Set ngày tối thiểu là hôm nay
            var today = new Date().toISOString().split('T')[0];
            $('#startDate').attr('min', today);
            
            $('#startDate').on('change', function() {
                $('#endDate').attr('min', this.value);
            });

            // Validation form trước khi submit
            $('#createTaskForm').on('submit', function(e) {
                var title = $('#title').val().trim();
                var description = $('#description').val().trim();
                var startDate = $('#startDate').val();
                var endDate = $('#endDate').val();
                var eventId = $('#eventId').val();
                var assignedTo = $('#assignedTo').val();

                if (!title || !description || !startDate || !endDate || !eventId || !assignedTo) {
                    e.preventDefault();
                    alert('Vui lòng điền đầy đủ thông tin bắt buộc!');
                    return false;
                }

                if (new Date(endDate) < new Date(startDate)) {
                    e.preventDefault();
                    alert('Ngày kết thúc phải sau ngày bắt đầu!');
                    return false;
                }

                // Hiển thị loading
                $(this).find('button[type="submit"]').prop('disabled', true).html(
                    '<i class="fas fa-spinner fa-spin me-1"></i>Đang tạo...'
                );
            });
        });
    </script>
</body>
</html>
