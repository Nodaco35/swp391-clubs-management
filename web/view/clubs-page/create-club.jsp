<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <title>${isEdit ? 'Yêu Cầu Cập Nhật Câu Lạc Bộ' : (isEditRejectClub ? 'Sửa đơn Tạo Câu Lạc Bộ' : 'Tạo Câu Lạc Bộ Mới')}</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gray-100 font-sans antialiased">
        <jsp:include page="../components/header.jsp" />

        <div class="container mx-auto max-w-2xl p-6 my-8 bg-white rounded-lg shadow-xl">
            <h2 class="text-2xl font-bold text-gray-800 text-center mb-6">${isEdit ? 'Yêu Cầu Cập Nhật Câu Lạc Bộ' : (isEditRejectClub ? 'Sửa đơn Tạo Câu Lạc Bộ' : 'Tạo Câu Lạc Bộ Mới')}</h2>

            <!-- Error Message -->
            <c:if test="${not empty error}">
                <div class="bg-red-100 text-red-700 p-4 rounded-lg mb-6 text-center">
                    ${error}
                </div>
            </c:if>

            <!-- Success Message -->
            <c:if test="${not empty message}">
                <div class="bg-green-100 text-green-700 p-4 rounded-lg mb-6 text-center">
                    ${message}
                </div>
            </c:if>
            <!-- chia trường hợp url nếu là đơn từ chối sửa lại thì chuyển đến servlet khác để sử lý -->
            <form id="createClubForm" action="${pageContext.request.contextPath}/create-club${(isEdit || isEditRejectClub) ? '?action=updateClub' : ''}" method="post" enctype="multipart/form-data" class="space-y-4">
                <c:if test="${isEdit}">
                    <input type="hidden" name="clubID" value="${club.clubID}">
                </c:if>
                    <c:if test="${isEditRejectClub}">
                    <input type="hidden" name="isEditRejectClub" value="isEditRejectClub">
                    <input type="hidden" name="clubID" value="${club.clubID}">
                </c:if>
                <div>
                    <label for="clubName" class="block text-sm font-medium text-gray-700">Tên Câu Lạc Bộ: <span class="text-red-500">*</span></label>
                    <input type="text" name="clubName" id="clubName" required
                           class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                           value="${(isEdit || isEditRejectClub) ? club.clubName : approvedClubName}">
                </div>

                <div>
                    <label for="description" class="block text-sm font-medium text-gray-700">Mô Tả:</label>
                    <textarea name="description" id="description" rows="5"
                              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">${isEdit || isEditRejectClub ? club.description : param.description}</textarea>
                </div>

                <div>
                    <label for="category" class="block text-sm font-medium text-gray-700">Danh Mục: <span class="text-red-500">*</span></label>
                    <select name="category" id="category" required
                            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                        <option value="" disabled ${isEdit && club.categoryID == null ? 'selected' : ''}>Chọn danh mục</option>
                        <c:forEach items="${categories}" var="category">
                            <option value="${category.categoryID}" 
                                    ${(isEdit || isEditRejectClub ) && club.categoryID == category.categoryID || (!isEdit && approvedCategoryID == category.categoryID) ? 'selected' : ''}>
                                ${category.categoryName}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div>
                    <label for="establishedDate" class="block text-sm font-medium text-gray-700">Ngày Thành Lập:</label>
                    <input type="date" name="establishedDate" id="establishedDate"
                           class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                           value="${(isEdit || isEditRejectClub ) && club.establishedDate != null ? club.establishedDate.toLocalDate() : param.establishedDate}">
                </div>

                <div>
                    <label for="clubImg" class="block text-sm font-medium text-gray-700">Hình Ảnh Câu Lạc Bộ: ${(isEdit || isEditRejectClub ) ? '' : '<span class="text-red-500">*</span>'}</label>
                    <div class="avatar-box mb-4">
                        <img id="clubImgPreview" src="${(isEdit || isEditRejectClub ) && club.clubImg != null && !club.clubImg.isEmpty() ? pageContext.request.contextPath.concat('/').concat(club.clubImg) : pageContext.request.contextPath.concat('/img/default-club-img.png')}" 
                             alt="Club Image" class="avatar-img w-32 h-32 object-cover rounded-lg">
                    </div>
                    <input type="file" name="clubImg" id="clubImg" accept="image/jpeg,image/png,image/gif,image/webp" ${(isEdit || isEditRejectClub ) ? '' : 'required'}
                           class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <c:if test="${(isEdit || isEditRejectClub )}">
                        <p class="text-sm text-gray-500 mt-1">Để giữ ảnh hiện tại, không chọn file mới.</p>
                    </c:if>
                </div>

                <div>
                    <label for="contactPhone" class="block text-sm font-medium text-gray-700">Số Điện Thoại Liên Hệ:</label>
                    <input type="tel" name="contactPhone" id="contactPhone"
                           class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                           value="${(isEdit || isEditRejectClub ) ? club.contactPhone : param.contactPhone}">
                </div>

                <div>
                    <label for="contactGmail" class="block text-sm font-medium text-gray-700">Email Liên Hệ: <span class="text-red-500">*</span></label>
                    <input type="email" name="contactGmail" id="contactGmail" required
                           class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                           value="${(isEdit || isEditRejectClub ) ? club.contactGmail : param.contactGmail}">
                </div>

                <div>
                    <label for="contactURL" class="block text-sm font-medium text-gray-700">URL Liên Hệ (Website/FB):</label>
                    <input type="url" name="contactURL" id="contactURL"
                           class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                           value="${(isEdit || isEditRejectClub ) ? club.contactURL : param.contactURL}">
                </div>

                <div>
                    <c:if test="${(isEdit || isEditRejectClub )}">
                    <label for="updateRequestNote" class="block text-sm font-medium text-gray-700">Ghi chú yêu cầu cập nhật:</label>
                    <textarea name="updateRequestNote" id="updateRequestNote" rows="3"
                              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                              placeholder="Mô tả lý do hoặc các thay đổi chính (bắt buộc khi yêu cầu cập nhật)"></textarea>
                    
                        <p class="text-sm text-gray-500 mt-1">Vui lòng cung cấp thông tin về lý do cập nhật.</p>
                    </c:if>
                </div>

                <div class="flex justify-center gap-4">
                    <button type="submit" class="btn-primary px-6 py-3 rounded-lg text-center bg-blue-500 text-white hover:bg-blue-600 transition">${isEdit ? 'Gửi Yêu Cầu Cập Nhật' : (isEditRejectClub ? 'Nộp lại' : 'Tạo Câu Lạc Bộ')}</button>
                    <a href="${(isEdit || isEditRejectClub ) ? pageContext.request.contextPath.concat('/club-detail?id=').concat(club.clubID) : pageContext.request.contextPath.concat('/clubs')}" class="btn-cancel px-6 py-3 rounded-lg text-center bg-gray-500 text-white hover:bg-gray-600 transition">Hủy</a>
                </div>
            </form>
        </div>

        <script>
            // Preview club image before upload
            document.getElementById('clubImg').addEventListener('change', function (e) {
                const file = e.target.files[0];
                if (file) {
                    const reader = new FileReader();
                    reader.onload = function (event) {
                        document.getElementById('clubImgPreview').src = event.target.result;
                    };
                    reader.readAsDataURL(file);
                }
            });

            document.getElementById('createClubForm').addEventListener('submit', function (e) {
                const clubName = document.getElementById('clubName').value.trim();
                const contactGmail = document.getElementById('contactGmail').value.trim();
                const contactPhone = document.getElementById('contactPhone').value.trim();
                const clubImg = document.getElementById('clubImg').files[0];
                const establishedDate = document.getElementById('establishedDate').value;
                const isEdit = ${isEdit};
                const updateRequestNote = document.getElementById('updateRequestNote').value.trim();

                let errors = [];

                if (!clubName)
                    errors.push('Tên câu lạc bộ không được để trống.');
                if (!contactGmail)
                    errors.push('Email liên hệ không được để trống.');
                if (contactPhone && !/^\d{10}$/.test(contactPhone)) {
                    errors.push('Số điện thoại phải là 10 chữ số và không chứa chữ cái hoặc ký tự đặc biệt.');
                }
                if (!isEdit && !clubImg)
                    errors.push('Vui lòng chọn một hình ảnh.');
                if (clubImg) {
                    if (!['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(clubImg.type)) {
                        errors.push('Hình ảnh phải là định dạng JPEG, PNG, GIF hoặc WebP.');
                    } else if (clubImg.size > 5 * 1024 * 1024) {
                        errors.push('Hình ảnh không được lớn hơn 5MB.');
                    }
                }
                if (establishedDate && !/^\d{4}-\d{2}-\d{2}$/.test(establishedDate)) {
                    errors.push('Ngày thành lập không hợp lệ.');
                }
                if (isEdit && !updateRequestNote) {
                    errors.push('Ghi chú yêu cầu cập nhật không được để trống.');
                }

                if (errors.length > 0) {
                    e.preventDefault();
                    alert('Vui lòng khắc phục các lỗi sau:\n' + errors.join('\n'));
                }
            });
        </script>

        <jsp:include page="../components/footer.jsp" />
    </body>
</html>