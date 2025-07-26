
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="models.Clubs" %>
<%@ page import="models.ClubApprovalHistory" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Thông tin CLB</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ic-dashboard.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&family=Montserrat:wght@600&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-LN+7fdVzj6u52u30Kp6M/trliBMCMKTyK833zpbD+pXdCLuTusPj697FH4R/5mcr" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <!-- Bootstrap 5 CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

        <!-- Bootstrap 5 JS (ở cuối body) -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </head>
    <body>
        <div class="dashboard d-flex">
            <jsp:include page="components/ic-sidebar.jsp" />

            <div class="flex-grow-1 p-4">
                <div class="container mt-4">
                    <h2 class="mb-4">Thông tin Câu Lạc Bộ</h2>

                    <c:if test="${not empty club}">
                        <div class="card mb-4">
                            <div class="row g-0">
                                <div class="col-md-4">
                                    <img src="${pageContext.request.contextPath}/${club.clubImg != null && not empty club.clubImg ? club.clubImg : 'images/default-club.jpg'}?t=<%= System.currentTimeMillis() %>" 
                                         alt="${club.clubName}" style="width: 100%; height: 100%">
                                </div>
                                <div class="col-md-8">
                                    <div class="card-body">
                                        <h5 class="card-title">${club.clubName}</h5>
                                        <p class="card-text"><strong>Mô tả:</strong> ${club.description}</p>
                                        <p class="card-text"><strong>Ngày thành lập:</strong> 
                                            <fmt:formatDate value="${club.establishedDate}" pattern="dd/MM/yyyy" />
                                        </p>
                                        <p class="card-text"><strong>Chủ nhiệm:</strong> ${club.chairmanFullName} - <strong>Mã SV:</strong> ${club.chairmanID}</p>
                                        <p class="card-text"><strong>Trạng thái hoạt động:</strong> 
                                            <c:choose>
                                                <c:when test="${club.clubStatus}"><strong style="color: green">Đang hoạt động</strong></c:when>
                                                <c:otherwise><strong style="color: red">Không hoạt động</strong></c:otherwise>
                                            </c:choose>
                                        </p>
                                        <p class="card-text"><strong>Tình trạng đăng ký:</strong> 
                                            <c:choose>
                                                <c:when test="${club.clubRequestStatus == 'Pending'}"><strong style="color: blue">Đợi xét duyệt</strong></c:when>
                                                <c:when test="${club.clubRequestStatus == 'Approved'}"><strong style="color: green">Đã đồng ý</strong></c:when>
                                                <c:otherwise><strong style="color: red">Đã từ chối</strong></c:otherwise>
                                            </c:choose>
                                        </p>
                                        <p class="card-text"><strong>Có đang tuyển thành viên không:</strong> 
                                            <c:choose>
                                                <c:when test="${club.isRecruiting}">Có</c:when>
                                                <c:otherwise>Không</c:otherwise>
                                            </c:choose>
                                        </p>
                                        <p class="card-text"><strong>Danh mục:</strong> ${club.categoryName}</p>
                                        <p class="card-text"><strong>Số điện thoại liên hệ:</strong> ${club.contactPhone}</p>
                                        <p class="card-text"><strong>Email:</strong> ${club.contactGmail}</p>
                                        <p class="card-text"><strong>Liên kết mạng xã hội:</strong> 
                                            <a href="${club.contactURL}" target="_blank">${club.contactURL}</a>
                                        </p>
                                        <c:if test="${club.clubRequestStatus == 'Pending'}">
                                            <div class="mt-3">
                                                <a href="${pageContext.request.contextPath}/ic?action=approvePermissionRequest&id=${club.clubID}&userID=${club.chairmanID}" 
                                                   class="btn btn-success me-2">
                                                    <i class="fas fa-check"></i> Duyệt đơn
                                                </a>

                                                <button class="btn btn-danger" onclick="showRejectModal('${club.clubID}', '${club.chairmanID}')">
                                                    <i class="fas fa-times"></i> Từ chối đơn
                                                </button>
                                            </div>
                                        </c:if>
                                        <c:if test="${club.clubRequestStatus == 'Approved'}">
                                            <div class="mt-3">
                                                <button class="btn btn-danger" onclick="showRejectModal('${club.clubID}', '${club.chairmanID}')">
                                                    <i class="fas fa-times"></i> Từ chối đơn
                                                </button>
                                            </div>
                                        </c:if>
                                        <c:if test="${club.clubRequestStatus == 'Rejected'}">
                                            <div class="mt-3">
                                                <a href="${pageContext.request.contextPath}/ic?action=approvePermissionRequest&id=${club.clubID}&userID=${club.chairmanID}" 
                                                   class="btn btn-success me-2">
                                                    <i class="fas fa-check"></i> Duyệt đơn
                                                </a>

                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="card mb-4">
                            <div class="row g-0">
                                <div class="col-md-4">
                                    <img src="${pageContext.request.contextPath}/${club.clubImg != null && not empty updateClub.clubImg ? updateClub.clubImg : 'images/default-club.jpg'}?t=<%= System.currentTimeMillis() %>" 
                                         alt="${updateClub.clubName}" style="width: 100%; height: 100%">
                                </div>
                                <div class="col-md-8">
                                    <div class="card-body">
                                        <h5 class="card-title">${updateClub.clubName}</h5>
                                        <p class="card-text"><strong>Mô tả:</strong> ${updateClub.description}</p>
                                        <p class="card-text"><strong>Ngày thành lập:</strong> 
                                            <fmt:formatDate value="${updateClub.establishedDate}" pattern="dd/MM/yyyy" />
                                        </p>
                                        <p class="card-text"><strong>Tình trạng đăng ký:</strong> 
                                            <c:choose>
                                                <c:when test="${updateClub.clubRequestStatus == 'Pending'}"><strong style="color: blue">Đợi xét duyệt</strong></c:when>
                                                <c:when test="${updateClub.clubRequestStatus == 'Approved'}"><strong style="color: green">Đã đồng ý</strong></c:when>
                                                <c:otherwise><strong style="color: red">Đã từ chối</strong></c:otherwise>
                                            </c:choose>
                                        </p>
                                        <p class="card-text"><strong>Danh mục:</strong> ${updateClub.categoryName}</p>
                                        <p class="card-text"><strong>Số điện thoại liên hệ:</strong> ${updateClub.contactPhone}</p>
                                        <p class="card-text"><strong>Email:</strong> ${updateClub.contactGmail}</p>
                                        <p class="card-text"><strong>Liên kết mạng xã hội:</strong> 
                                            <a href="${updateClub.contactURL}" target="_blank">${updateClub.contactURL}</a>
                                        </p>
                                        <c:if test="${club.clubRequestStatus == 'Pending'}">
                                            <div class="mt-3">
                                                <a href="${pageContext.request.contextPath}/ic?action=approvePermissionRequest&id=${updateClub.clubID}&userID=${club.chairmanID}" 
                                                   class="btn btn-success me-2">
                                                    <i class="fas fa-check"></i> Duyệt đơn
                                                </a>

                                                <button class="btn btn-danger" onclick="showRejectModal('${updateClub.clubID}', '${club.chairmanID}')">
                                                    <i class="fas fa-times"></i> Từ chối đơn
                                                </button>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <c:if test="${empty club}">
                        <div class="alert alert-warning">Không tìm thấy thông tin câu lạc bộ.</div>
                    </c:if>

                    <a href="${pageContext.request.contextPath}/ic?action=grantPermission" class="btn btn-secondary">Quay lại</a>

                </div>



            </div>
            <!-- Modal Bootstrap: Nhập lý do từ chối -->
            <div class="modal fade" id="rejectReasonModal" tabindex="-1" aria-labelledby="rejectReasonModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <form action="${pageContext.request.contextPath}/ic" method="get" class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="rejectReasonModalLabel">Lý do từ chối</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="action" value="rejectPermissionRequest">
                            <input type="hidden" name="id" id="rejectClubID">
                            <input type="hidden" name="userID" id="rejectUserID">

                            <div class="mb-3">
                                <label for="reason" class="form-label">Nhập lý do từ chối:</label>
                                <textarea class="form-control" name="reason" id="reason" rows="4" required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-danger">Từ chối</button>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        </div>
                    </form>
                </div>
            </div>


        </div>
        <script>
            function showRejectModal(clubID, userID) {
                // Gán dữ liệu vào các hidden input
                document.getElementById("rejectClubID").value = clubID;
                document.getElementById("rejectUserID").value = userID;

                // Hiện modal Bootstrap
                const modal = new bootstrap.Modal(document.getElementById('rejectReasonModal'));
                modal.show();
            }
        </script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>

