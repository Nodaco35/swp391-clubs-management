<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"  %>
<%-- Import các lớp tiện ích --%>
<%@ page import="util.StringEscapeUtils" %>
<%@ page import="util.JsonUtils" %>
<%@ page import="models.ApplicationFormTemplate" %> <%-- Import model ApplicationFormTemplate --%>
<%@ page import="org.json.JSONArray" %> <%-- Import JSON library --%>
<%@ page import="org.json.JSONObject" %> <%-- Import JSON library --%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Comparator" %> <%-- Import để sắp xếp câu hỏi theo displayOrder --%>

<%
// Lấy danh sách câu hỏi từ request
List<ApplicationFormTemplate> questions = (List<ApplicationFormTemplate>) request.getAttribute("questions");

// Đảm bảo câu hỏi đã được sắp xếp theo displayOrder
questions.sort(Comparator.comparing(ApplicationFormTemplate::getDisplayOrder)
               .thenComparing(ApplicationFormTemplate::getTemplateId));

// Log thông tin để debug
System.out.println("ApplicationForm.jsp: Đang hiển thị " + questions.size() + " câu hỏi theo displayOrder");
%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <!-- Tiêu đề form lấy từ dữ liệu database -->
        <title>${formTitle} - UniClub</title>
        <!-- Import Google fonts -->
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;500;600;700&family=Poppins:wght@300;400;500;600;700&family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Import Font Awesome icons -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <!-- Import CSS files -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/applicationForm.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body>
    <!-- Include header -->
    <jsp:include page="/view/events-page/header.jsp" />
    
    <div class="container application-form-container">
        <div class="form-header">
            <h1>${formTitle}</h1>
            <c:if test="${param.success == 'true'}">
                <div class="alert alert-success">
                    <i class="fas fa-check-circle" style="font-size: 1.5rem; color: var(--success-color);"></i>
                    <div class="alert-content">
                        <h4 class="alert-title">Đăng ký thành công!</h4>
                        <p class="alert-description">Đơn đăng ký của bạn đã được lưu lại.</p>
                    </div>
                </div>
            </c:if>
            <c:if test="${param.error == 'true'}">
                <div class="alert alert-danger">
                    <i class="fas fa-exclamation-triangle" style="font-size: 1.5rem; color: var(--error-color);"></i>
                    <div class="alert-content">
                        <h4 class="alert-title">Đăng ký thất bại</h4>
                        <p class="alert-description">${param.message}</p>
                    </div>
                </div>
            </c:if>
        </div>

        <div class="form-content">
            <form id="applicationForm" action="${pageContext.request.contextPath}/applicationForm" method="post">
                <input type="hidden" name="templateId" value="${templateId}">
                <input type="hidden" name="clubId" value="${clubId}">
                <input type="hidden" name="formType" value="${formType}">
                <c:if test="${not empty eventId}">
                    <input type="hidden" name="eventId" value="${eventId}">
                </c:if>
                <input type="hidden" name="responsesJson" id="responsesJson" value="">
                
                <div class="form-description">
                    <p>Vui lòng điền đầy đủ thông tin bên dưới để hoàn thành đơn đăng ký.</p>
                </div>
                
                <c:forEach var="question" items="${questions}">
                    <%-- Xác định xem câu hỏi này có phải là câu hỏi chọn ban hay không --%>
                    <% 
                        ApplicationFormTemplate q = (ApplicationFormTemplate)pageContext.getAttribute("question");
                        String fieldName = q.getFieldName();
                        boolean isDepartmentQuestion = fieldName != null && fieldName.contains("Chọn ban");
                        
                        if (isDepartmentQuestion) {
                            pageContext.setAttribute("isDepartmentQuestion", true);
                        } else {
                            pageContext.setAttribute("isDepartmentQuestion", false);
                        }
                        
                        String formType = (String)request.getAttribute("formType");
                        boolean isEventForm = "event".equals(formType);
                        pageContext.setAttribute("isEventForm", isEventForm);
                    %>

                    <div class="form-group question-card" data-template-id="${question.getTemplateId()}" data-display-order="${question.getDisplayOrder()}" 
                        <c:if test="${isDepartmentQuestion && isEventForm}">style="display: none;"</c:if>>
                    
                    <c:choose>
                            <%-- Xử lý trường thông tin (info) --%>
                            <c:when test="${question.getFieldType() eq 'Info'}">
                                <div class="info-field">
                                    <%-- Kiểm tra nếu Options là JSON --%>
                                    <% try { 
                                        ApplicationFormTemplate infoQuestion = (ApplicationFormTemplate)pageContext.getAttribute("question");
                                        String options = infoQuestion.getOptions();
                                        String content = null; // Mặc định là null - sẽ không hiển thị nếu không có nội dung
                                        String imageData = null;
                                        
                                        // Kiểm tra xem options có phải là JSON không
                                        if (options != null && JsonUtils.isValidJson(options)) {
                                            if (options.trim().startsWith("{")) {
                                                JSONObject jsonObj = new JSONObject(options);
                                                if (jsonObj.has("content") && !jsonObj.isNull("content")) {
                                                    String jsonContent = jsonObj.getString("content");
                                                    if (jsonContent != null && !jsonContent.trim().isEmpty()) {
                                                        content = jsonContent;
                                                    }
                                                }
                                                
                                                if (jsonObj.has("image") && !jsonObj.isNull("image")) {
                                                    imageData = jsonObj.getString("image");
                                                    pageContext.setAttribute("imageData", imageData);
                                                }
                                            }
                                        } else if (options != null && !options.trim().isEmpty()) {
                                            // Nếu không phải là JSON, sử dụng nội dung gốc
                                            content = options;
                                        }
                                        
                                        // Chỉ đặt parsedContent khi content không rỗng
                                        if (content != null && !content.trim().isEmpty()) {
                                            pageContext.setAttribute("parsedContent", content);
                                        } else {
                                            pageContext.removeAttribute("parsedContent");
                                        }
                                    } catch (Exception e) {
                                        // Nếu có lỗi xử lý JSON, ghi log và xử lý lỗi
                                        System.out.println("Lỗi xử lý JSON cho trường Info: " + e.getMessage());
                                        pageContext.removeAttribute("parsedContent");
                                        pageContext.removeAttribute("imageData");
                                    } %>
                                    
                                    <div class="info-content">
                                        <c:choose>
                                            <c:when test="${not empty parsedContent && !parsedContent.trim().startsWith('{')}">
                                                ${parsedContent}
                                            </c:when>
                                            <c:otherwise>
                                                <%-- Nếu nội dung rỗng hoặc chỉ chứa JSON mà không có content, không hiển thị gì cả --%>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    
                                    <c:if test="${not empty imageData}">
                                        <img src="${imageData}" alt="Hình ảnh thông tin" class="info-image">
                                    </c:if>
                                    
                                    <input type="hidden" 
                                           id="ans_${question.getTemplateId()}" 
                                           name="ans_${question.getTemplateId()}" 
                                           value="info_field_viewed" 
                                           data-field-type="info">
                                </div>
                            </c:when>
                            
                            <%-- Xử lý các loại trường khác --%>
                            <c:otherwise>
                                <label class="form-label${question.isRequired() ? ' required-label' : ''}" for="ans_${question.getTemplateId()}">
                                    ${question.getFieldName()}
                                    <c:if test="${question.isRequired()}"><span class="required-mark">*</span></c:if>
                                </label>
                                
                                <c:choose>
                                    <%-- Trường văn bản (text) --%>
                                    <c:when test="${question.getFieldType() eq 'Text'}">
                                        <input 
                                            type="text" 
                                            class="form-input" 
                                            id="ans_${question.getTemplateId()}" 
                                            name="ans_${question.getTemplateId()}"
                                            placeholder="${not empty question.getOptions() ? question.getOptions() : 'Nhập câu trả lời của bạn'}"
                                            ${question.isRequired() ? 'required' : ''}
                                        >
                                    </c:when>
                                    
                                    <%-- Trường văn bản nhiều dòng (textarea) --%>
                                    <c:when test="${question.getFieldType() eq 'Textarea'}">
                                        <textarea 
                                            class="form-textarea" 
                                            id="ans_${question.getTemplateId()}" 
                                            name="ans_${question.getTemplateId()}"
                                            placeholder="${not empty question.getOptions() ? question.getOptions() : 'Nhập câu trả lời của bạn'}"
                                            rows="4"
                                            ${question.isRequired() ? 'required' : ''}
                                        ></textarea>
                                    </c:when>
                                    
                                    <%-- Trường ngày tháng (date) --%>
                                    <c:when test="${question.getFieldType() eq 'Date'}">
                                        <%-- Kiểm tra nếu Options chứa giới hạn ngày tháng dạng JSON --%>
                                        <% 
                                            ApplicationFormTemplate dateQuestion = (ApplicationFormTemplate)pageContext.getAttribute("question");
                                            String options = dateQuestion.getOptions();
                                            String minDate = null;
                                            String maxDate = null;
                                            
                                            if (options != null && options.trim().startsWith("{")) {
                                                try {
                                                    JSONObject jsonObj = new JSONObject(options);
                                                    if (jsonObj.has("minDate")) {
                                                        minDate = jsonObj.getString("minDate");
                                                    }
                                                    if (jsonObj.has("maxDate")) {
                                                        maxDate = jsonObj.getString("maxDate");
                                                    }
                                                } catch (Exception e) {
                                                    System.out.println("Lỗi xử lý giới hạn ngày tháng: " + e.getMessage());
                                                }
                                            }
                                            
                                            pageContext.setAttribute("minDate", minDate);
                                            pageContext.setAttribute("maxDate", maxDate);
                                        %>
                                        
                                        <input 
                                            type="date" 
                                            class="form-input form-date" 
                                            id="ans_${question.getTemplateId()}" 
                                            name="ans_${question.getTemplateId()}"
                                            ${not empty minDate ? 'min="'.concat(minDate).concat('"') : ''}
                                            ${not empty maxDate ? 'max="'.concat(maxDate).concat('"') : ''}
                                            ${question.isRequired() ? 'required' : ''}
                                        >
                                    </c:when>
                                    
                                    <%-- Trường chọn nhiều (checkbox) --%>
                                    <c:when test="${question.getFieldType() eq 'Checkbox'}">
                                        <div class="checkbox-group" style="display: flex; flex-direction: column; gap: 10px;">
                                            <%-- Xử lý options với phương thức tập trung --%>
                                            <% 
                                                ApplicationFormTemplate checkboxQuestion = (ApplicationFormTemplate)pageContext.getAttribute("question");
                                                String options = checkboxQuestion.getOptions();
                                                
                                                // Sử dụng phương thức tiện ích mới để xử lý tất cả định dạng tùy chọn
                                                List<String> checkboxOptionsList = JsonUtils.parseFormBuilderOptions(options);
                                                
                                                // Debug log
                                                System.out.println("Debug - Checkbox ID " + q.getTemplateId() + 
                                                    " - Options raw: " + options);
                                                System.out.println("Debug - Checkbox options parsed: " + checkboxOptionsList);
                                                
                                                pageContext.setAttribute("checkboxOptionsList", checkboxOptionsList);
                                            %>
                                            
                                            <c:forEach var="option" items="${checkboxOptionsList}" varStatus="status">
                                                <div class="checkbox-option" style="display: flex; align-items: center; margin: 5px 0;">
                                                    <input 
                                                        type="checkbox" 
                                                        id="ans_${question.getTemplateId()}_${status.index}" 
                                                        name="ans_${question.getTemplateId()}" 
                                                        value="${option}"
                                                        ${question.isRequired() && status.index == 0 ? 'required' : ''}
                                                        style="margin-right: 10px;"
                                                    >
                                                    <label for="ans_${question.getTemplateId()}_${status.index}" style="cursor: pointer;">${option}</label>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    
                                    <%-- Trường chọn một (radio) --%>
                                    <c:when test="${question.getFieldType() eq 'Radio'}">
                                        <div class="radio-group" style="display: flex; flex-direction: column; gap: 10px;">
                                            <%-- Xử lý options với phương thức tập trung --%>
                                            <% 
                                                ApplicationFormTemplate radioQuestion = (ApplicationFormTemplate)pageContext.getAttribute("question");
                                                String options = radioQuestion.getOptions();
                                                
                                                // Sử dụng phương thức tiện ích mới để xử lý tất cả định dạng tùy chọn
                                                List<String> radioOptionsList = JsonUtils.parseFormBuilderOptions(options);
                                                
                                                // Debug log
                                                System.out.println("Debug - Radio ID " + q.getTemplateId() + 
                                                    " - Options raw: " + options);
                                                System.out.println("Debug - Radio options parsed: " + radioOptionsList);
                                                
                                                pageContext.setAttribute("radioOptionsList", radioOptionsList);
                                            %>
                                            
                                            <c:forEach var="option" items="${radioOptionsList}" varStatus="status">
                                                <div class="radio-option" style="display: flex; align-items: center; margin: 5px 0;">
                                                    <input 
                                                        type="radio" 
                                                        id="ans_${question.getTemplateId()}_${status.index}" 
                                                        name="ans_${question.getTemplateId()}" 
                                                        value="${option}"
                                                        ${question.isRequired() ? 'required' : ''}
                                                        style="margin-right: 10px;"
                                                    >
                                                    <label for="ans_${question.getTemplateId()}_${status.index}" style="cursor: pointer;">${option}</label>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    
                                    <%-- Trường số (number) --%>
                                    <c:when test="${question.getFieldType() eq 'Number'}">
                                        <%-- Kiểm tra nếu Options chứa giới hạn số dạng JSON --%>
                                        <% 
                                            ApplicationFormTemplate numberQuestion = (ApplicationFormTemplate)pageContext.getAttribute("question");
                                            String options = numberQuestion.getOptions();
                                            String minValue = null;
                                            String maxValue = null;
                                            String step = "1";
                                            
                                            if (options != null && options.trim().startsWith("{")) {
                                                try {
                                                    JSONObject jsonObj = new JSONObject(options);
                                                    if (jsonObj.has("min")) {
                                                        minValue = jsonObj.getString("min");
                                                    }
                                                    if (jsonObj.has("max")) {
                                                        maxValue = jsonObj.getString("max");
                                                    }
                                                    if (jsonObj.has("step")) {
                                                        step = jsonObj.getString("step");
                                                    }
                                                } catch (Exception e) {
                                                    System.out.println("Lỗi xử lý giới hạn số: " + e.getMessage());
                                                }
                                            }
                                            
                                            pageContext.setAttribute("minValue", minValue);
                                            pageContext.setAttribute("maxValue", maxValue);
                                            pageContext.setAttribute("step", step);
                                        %>
                                        
                                        <input 
                                            type="number" 
                                            class="form-input form-number" 
                                            id="ans_${question.getTemplateId()}" 
                                            name="ans_${question.getTemplateId()}"
                                            ${not empty minValue ? 'min="'.concat(minValue).concat('"') : ''}
                                            ${not empty maxValue ? 'max="'.concat(maxValue).concat('"') : ''}
                                            step="${step}"
                                            ${question.isRequired() ? 'required' : ''}
                                        >
                                    </c:when>
                                    
                                    <%-- Trường email --%>
                                    <c:when test="${question.getFieldType() eq 'Email'}">
                                        <input 
                                            type="email" 
                                            class="form-input" 
                                            id="ans_${question.getTemplateId()}" 
                                            name="ans_${question.getTemplateId()}"
                                            placeholder="${not empty question.getOptions() ? question.getOptions() : 'Nhập địa chỉ email của bạn'}"
                                            ${question.isRequired() ? 'required' : ''}
                                        >
                                    </c:when>
                                    
                                    <%-- Trường số điện thoại --%>
                                    <c:when test="${question.getFieldType() eq 'PhoneNumber'}">
                                        <input 
                                            type="tel" 
                                            class="form-input" 
                                            id="ans_${question.getTemplateId()}" 
                                            name="ans_${question.getTemplateId()}"
                                            placeholder="${not empty question.getOptions() ? question.getOptions() : 'Nhập số điện thoại của bạn'}"
                                            pattern="[0-9]{10,15}"
                                            ${question.isRequired() ? 'required' : ''}
                                        >
                                    </c:when>
                                    
                                    <%-- Mặc định xử lý như trường văn bản --%>
                                    <c:otherwise>
                                        <input 
                                            type="text" 
                                            class="form-input" 
                                            id="ans_${question.getTemplateId()}" 
                                            name="ans_${question.getTemplateId()}"
                                            placeholder="Nhập câu trả lời của bạn"
                                            ${question.isRequired() ? 'required' : ''}
                                        >
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:forEach>
                
                <div class="form-actions">
                    <button type="button" id="submitBtn" class="submit-button" onclick="validateForm()">
                        <i class="fas fa-paper-plane" style="margin-right: 8px;"></i>
                        Gửi đơn đăng ký
                    </button>
                    <div id="loadingIndicator" class="loading-indicator" style="display: none;">
                        <div class="spinner"></div>
                        <span>Đang xử lý...</span>
                    </div>
                </div>
            </form>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/applicationForm.js"></script>
    </body>
</html>
