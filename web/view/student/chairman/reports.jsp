<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
    <head>
        <title>Báo cáo định kỳ CLB</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chairmanPage.css">
        <style>
            .report-card {
                transition: box-shadow 0.3s ease;
            }

            .report-card:hover {
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
            }

            .badge-term {
                font-size: 0.9rem;
                background-color: #0d6efd;
            }
        </style>
    </head>
    <body>
        <jsp:include page="components/sidebar.jsp" />
        <div class="container mt-4">
            <h4 class="mb-4" style="color: #2c5aa0;margin-bottom: 20px; font-size: 24px">Báo cáo định kỳ</h4>

            <c:if test="${empty reports}">
                <div class="alert alert-warning text-center">
                    <i class="fas fa-exclamation-circle me-2"></i>Không có báo cáo nào được tìm thấy.
                </div>
            </c:if>

            <div class="row">
                <c:forEach var="report" items="${reports}">
                    <div class="col-md-6 mb-4">
                        <div class="card report-card">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-center">
                                    <div>
                                        <h5 class="card-title mb-1">Báo cáo kỳ <span class="badge badge-term">${report.term}</span></h5>
                                        <small class="text-muted">
                                            Mã báo cáo: <strong>${report.reportID}</strong><br/>
                                        </small>
                                    </div>
                                    <i class="fas fa-file-alt fa-2x text-secondary"></i>
                                </div>
                                <hr>
                                <p class="mb-1">
                                    Ngày nộp: 
                                    <fmt:formatDate value="${report.submissionDate}" pattern="dd/MM/yyyy HH:mm" />
                                </p>
                                <div class="d-flex justify-content-end">
                                    <a href="${pageContext.request.contextPath}/chairman-page/reports?action=viewDetail&reportId=${report.reportID}" 
                                       class="btn btn-sm btn-outline-primary">
                                        <i class="fas fa-eye me-1"></i>Xem chi tiết
                                    </a>
                                </div>
                                    <c:if test="${report.reportID == reportNowID}">
                                    <div class="d-flex justify-content-end">
                                        <a href="${pageContext.request.contextPath}/chairman-page/reports?action=fixDetailReport" 
                                           class="btn btn-sm btn-outline-secondary">
                                            <i class="fas fa-wrench me-1"></i>Sửa
                                        </a>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <c:if test="${!createReportNow}">
                <div style="margin-bottom: 30px">
                    <a href="${pageContext.request.contextPath}/chairman-page/reports?action=createReports" class="btn btn-success">
                        <i class="fas fa-file-text"></i> Tạo báo cáo kỳ ${termNow}
                    </a>
                </div>
            </c:if>
            <c:if test="${createReportNow}">
                <div style="margin-bottom: 30px">
                    <h6 style="color: green">Đã nộp báo cáo</h6>
                    <a href="${pageContext.request.contextPath}/chairman-page/reports?action=viewDetail&reportId=${reportNowID}" class="btn btn-primary">
                        <i class="fas fa-file-text"></i> Xem báo cáo đã nộp
                    </a>
                </div>
            </c:if>
        </div>
    </body>
</html>
