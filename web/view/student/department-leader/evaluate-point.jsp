<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chấm điểm thành viên - Ban ${sessionScope.departmentName}</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/department-leader.css">
    </head>
    <body>
        <div class="department-leader-container">
            <!-- Sidebar -->
            <%@ include file="components/sidebar.jsp" %>
            <!-- Main Content -->
            <main class="main-content">
                <header class="header mb-4">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h1 class="h3 mb-1">Chấm điểm thành viên</h1>
                            <nav aria-label="breadcrumb">
                                <ol class="breadcrumb mb-0">
                                    <li class="breadcrumb-item">
                                        <a href="${pageContext.request.contextPath}/department-dashboard?clubID=${clubID}" class="text-decoration-none">
                                            Dashboard
                                        </a>
                                    </li>
                                    <li class="breadcrumb-item">
                                        <a href="${pageContext.request.contextPath}/department-members?clubID=${clubID}">
                                            Thành viên
                                        </a>
                                    </li>
                                    <li class="breadcrumb-item active" aria-current="page">Chấm điểm</li>
                                </ol>
                            </nav>
                        </div>
                    </div>

                    <c:if test="${not empty mes}">
                        <div id="successMessage" class="alert alert-success">
                            <i class="fas fa-check-circle"></i> ${mes}
                        </div>
                        <script>
                            setTimeout(() => {
                                const message = document.getElementById('successMessage');
                                if (message) {
                                    message.style.display = 'none';
                                }
                            }, 4000);
                        </script>
                    </c:if>

                    <c:if test="${not empty mesDelete}">
                        <div id="mesDelete" class="alert alert-danger">
                            <i class="fa fa-trash" aria-hidden="true"></i> ${mesDelete}
                        </div>
                        <script>
                            setTimeout(() => {
                                const message = document.getElementById('mesDelete');
                                if (message) {
                                    message.style.display = 'none';
                                }
                            }, 4000);
                        </script>
                    </c:if>

                    <c:if test="${not empty err}">
                        <div id="errorMessage" class="alert alert-danger">
                            <i class="fas fa-exclamation-circle"></i> ${err}
                        </div>
                        <script>
                            setTimeout(() => {
                                const message = document.getElementById('errorMessage');
                                if (message) {
                                    message.style.display = 'none';
                                }
                            }, 4000);
                        </script>
                    </c:if>




                </header>

                <!-- Members Grading Table -->
                <div class="card shadow-sm">
                    <div class="card-header bg-white">
                        <h5 class="mb-0"><i class="fa-solid fa-user-graduate me-2"></i>Danh sách thành viên ban</h5>
                    </div>
                    <div class="card-body p-0">
                        <c:if test="${not empty members}">

                            <div class="table-responsive">
                                <table class="table table-hover mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th scope="col">Thành viên</th>
                                            <th scope="col">Email</th>
                                            <th scope="col">Ngày tham gia</th>
                                            <th scope="col">Điểm rèn luyện</th>
                                            <th scope="col">Hành động</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="member" items="${members}">
                                            <tr>
                                        <form action="department-members" method="post">
                                            <input type="hidden" name="userID" value="${member.userID}">
                                            <input type="hidden" name="clubDepartmentID" value="${clubDepartmentID}">
                                            <td>
                                                <div class="d-flex align-items-center">
                                                    <div>
                                                        <div class="fw-semibold">${member.fullName}</div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>${member.email}</td>
                                            <td><fmt:formatDate value="${member.activeDate}" pattern="dd/MM/yyyy" /></td>
                                            <td>
                                                <input type="text" name="points" class="form-control" 
                                                       pattern="^(100|[1-9]?[0-9])$" title="Vui lòng nhập từ 0-100"
                                                       value="${member.progressPoint != 0 ? member.progressPoint : ''}" required>
                                            </td>
                                            <td>
                                                <button type="submit" name="action" value="submitRating" class="btn btn-sm btn-success">
                                                    <i class="fas fa-check"></i> Lưu
                                                </button>
                                                <button type="submit" name="action" value="deleteRating" class="btn btn-sm btn-danger">
                                                    <i class="fas fa-trash"></i> Xoá
                                                </button>
                                            </td>
                                        </form>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>

                        </c:if>
                        <c:if test="${empty members}">
                            <div class="text-center p-4">
                                <p class="text-muted">Không có thành viên nào hoạt động trong ban này.</p>
                            </div>
                        </c:if>
                    </div>
                </div>
            </main>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
