<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isErrorPage="true"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Không tìm thấy trang - UniClub</title>
        <!-- Import Google fonts -->
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;500;600;700&family=Poppins:wght@300;400;500;600;700&family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Import Font Awesome icons -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <!-- Import CSS files -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <style>
            .error-container {
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                min-height: 80vh;
                text-align: center;
                padding: 2rem;
            }

            .error-code {
                font-size: 8rem;
                font-weight: bold;
                color: #00a6c0;
                margin-bottom: 0;
                line-height: 1;
            }

            .error-heading {
                font-size: 2rem;
                color: #283b48;
                margin: 1rem 0;
            }

            .error-message {
                font-size: 1.1rem;
                color: #666;
                max-width: 600px;
                margin: 1rem auto;
            }

            .error-image {
                max-width: 300px;
                margin: 2rem 0;
            }

            .error-actions {
                margin-top: 2rem;
                display: flex;
                gap: 1rem;
                flex-wrap: wrap;
                justify-content: center;
            }

            .error-button {
                display: inline-flex;
                align-items: center;
                justify-content: center;
                padding: 0.8rem 1.5rem;
                font-size: 1rem;
                font-weight: 500;
                color: white;
                background-color: #00a6c0;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                transition: all 0.3s ease;
                text-decoration: none;
            }

            .error-button:hover {
                background-color: #0088a0;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            }

            .error-button i {
                margin-right: 8px;
            }

            .error-button.secondary {
                background-color: #f0f0f0;
                color: #333;
            }

            .error-button.secondary:hover {
                background-color: #e0e0e0;
            }

            @media (max-width: 768px) {
                .error-code {
                    font-size: 6rem;
                }

                .error-heading {
                    font-size: 1.5rem;
                }

                .error-image {
                    max-width: 200px;
                }
            }
        </style>
    </head>
    <body>
        <!-- Include header -->
        <jsp:include page="/view/events-page/header.jsp" />
        
        <div class="container error-container">
            <h1 class="error-code">404</h1>
            <h2 class="error-heading">Không tìm thấy trang</h2>
            <p class="error-message">
                Rất tiếc, trang bạn đang tìm kiếm không tồn tại hoặc đã bị di chuyển. 
                Vui lòng kiểm tra lại URL hoặc chuyển hướng đến một trong những trang dưới đây.
            </p>
            
            <div class="error-actions">
                <a href="${pageContext.request.contextPath}/" class="error-button">
                    <i class="fas fa-home"></i> Trang chủ
                </a>
                <a href="javascript:history.back()" class="error-button secondary">
                    <i class="fas fa-arrow-left"></i> Quay lại
                </a>
                <a href="${pageContext.request.contextPath}/clubs" class="error-button secondary">
                    <i class="fas fa-users"></i> Danh sách CLB
                </a>
            </div>
        </div>
        
        <!-- Footer nếu có -->
        <jsp:include page="/view/events-page/footer.jsp" />
    </body>
</html>
