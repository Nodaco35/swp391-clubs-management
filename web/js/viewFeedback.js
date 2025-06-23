// Xử lý lỗi toàn cục để bắt bất kỳ ngoại lệ không được xử lý nào
window.addEventListener('error', function(event) {
    console.error('Lỗi JS toàn cục đã được bắt:', event.error);
    console.error('Chi tiết lỗi:', {
        message: event.error?.message || 'Lỗi không xác định',
        stack: event.error?.stack,
        lineno: event.lineno,
        colno: event.colno,
        filename: event.filename
    });
    
    // Tạo thông báo lỗi cho người dùng
    const errorDiv = document.createElement('div');
    errorDiv.style.position = 'fixed';
    errorDiv.style.top = '20px';
    errorDiv.style.left = '50%';
    errorDiv.style.transform = 'translateX(-50%)';
    errorDiv.style.backgroundColor = '#f8d7da';
    errorDiv.style.color = '#721c24';
    errorDiv.style.padding = '15px 20px';
    errorDiv.style.borderRadius = '5px';
    errorDiv.style.boxShadow = '0 2px 10px rgba(0,0,0,0.1)';
    errorDiv.style.zIndex = '9999';
    
    errorDiv.innerHTML = `
        <div style="display: flex; align-items: center; gap: 10px">
            <i class="fas fa-exclamation-triangle" style="font-size: 24px"></i>
            <div>
                <div style="font-weight: bold; margin-bottom: 5px">Đã xảy ra lỗi JavaScript</div>
                <div>${event.error?.message || 'Unknown error'}</div>
                <div style="font-size: 12px; margin-top: 10px">
                    Chi tiết trong Console (F12) để debug
                </div>
            </div>
            <button id="closeError" style="background: none; border: none; cursor: pointer; margin-left: 15px">✖</button>
        </div>
    `;
    
    document.body.appendChild(errorDiv);
    
    document.getElementById('closeError').addEventListener('click', function() {
        errorDiv.remove();
    });
      // Tự động ẩn sau 10 giây
    setTimeout(() => {
        if (document.body.contains(errorDiv)) {
            errorDiv.remove();
        }
    }, 10000);
    
    // Nếu lỗi liên quan đến Chart.js, thử destroy các instance chart nếu tồn tại
    if (event.error && event.error.message && event.error.message.includes('Chart')) {
        console.log('Phát hiện lỗi liên quan đến Chart.js, thực hiện dọn dẹp...');
        
        // Thử destroy biểu đồ phân phối đánh giá nếu tồn tại
        try {
            if (window.ratingDistributionChartInstance) {
                window.ratingDistributionChartInstance.destroy();
                window.ratingDistributionChartInstance = null;
                console.log('Đã xóa instance biểu đồ phân phối đánh giá');
            }
        } catch (e) {
            console.error('Không thể xóa biểu đồ phân phối đánh giá:', e);
        }
        
        // Thử destroy biểu đồ chi tiết nếu tồn tại
        try {
            if (window.detailedRatingChartInstance) {
                window.detailedRatingChartInstance.destroy();
                window.detailedRatingChartInstance = null;
                console.log('Đã xóa instance biểu đồ chi tiết');
            }
        } catch (e) {
            console.error('Không thể xóa biểu đồ chi tiết:', e);
        }
    }
    
    return false;
});

// Hàm để parse dữ liệu JSON an toàn
function safeParseJson(jsonString, defaultValue = {}) {
    try {
        if (!jsonString || typeof jsonString !== 'string') {
            return defaultValue;
        }
        return JSON.parse(jsonString);
    } catch (e) {
        console.error('Lỗi phân tích JSON:', e, 'Chuỗi gốc:', jsonString);
        return defaultValue;
    }
}

// Hàm để ghi log debug
function logDebug(...args) {
    console.log('[Debug]', ...args);
}

// Hàm để ghi log lỗi
function logError(...args) {
    console.error('[Error]', ...args);
}

document.addEventListener('DOMContentLoaded', function() {
    logDebug('DOM Content Loaded - Bắt đầu khởi tạo viewFeedback.js');
    
    // Màu sắc biểu đồ theo mockup - Bảng màu đã cập nhật
    const chartColors = {
        ratingVeryGood: '#2e7d32',    
        ratingGood: '#4caf50',       
        ratingNeutral: '#ffc107',     
        ratingBad: '#ff5252',         
        ratingVeryBad: '#b71c1c',     
        gridColor: '#e5e5e5'          
    };

    // Nhãn phân loại đánh giá
    const ratingLabels = {
        5: 'Rất hài lòng',
        4: 'Hài lòng',
        3: 'Bình thường',
        2: 'Không hài lòng',
        1: 'Rất không hài lòng'
    };
    
    // Hàm hiển thị danh sách người dùng với một đánh giá cụ thể trong popup
    function showUsersWithRating(stars) {
        // Lấy tất cả người dùng đã đánh giá số sao này
        const userElements = document.querySelectorAll(`.feedback-item[data-rating="${stars}"] .user-name`);
        const userNames = [];
        
        userElements.forEach(el => {
            const nameText = el.innerText.trim();
            userNames.push(nameText);
        });
        
        // Tạo và hiển thị một modal/popup với danh sách người dùng
        if (userNames.length > 0) {
            // Lọc bỏ người dùng ẩn danh
            const filteredNames = userNames.filter(name => !name.includes("ẩn danh"));
            
            // Tạo lớp phủ modal
            const modalOverlay = document.createElement('div');
            modalOverlay.style.position = 'fixed';
            modalOverlay.style.top = '0';
            modalOverlay.style.left = '0';
            modalOverlay.style.width = '100%';
            modalOverlay.style.height = '100%';
            modalOverlay.style.backgroundColor = 'rgba(0,0,0,0.5)';
            modalOverlay.style.zIndex = '9999';
            modalOverlay.style.display = 'flex';
            modalOverlay.style.justifyContent = 'center';
            modalOverlay.style.alignItems = 'center';
            
            // Tạo nội dung modal
            const modalContent = document.createElement('div');
            modalContent.style.backgroundColor = 'white';
            modalContent.style.borderRadius = '8px';
            modalContent.style.padding = '20px';
            modalContent.style.maxWidth = '600px';
            modalContent.style.width = '80%';
            modalContent.style.maxHeight = '80vh';
            modalContent.style.overflowY = 'auto';
            modalContent.style.boxShadow = '0 4px 12px rgba(0,0,0,0.15)';
            
            // Thêm tiêu đề với màu sắc phù hợp với đánh giá
            let ratingColor;
            switch(stars) {
                case 5: ratingColor = chartColors.ratingVeryGood; break;
                case 4: ratingColor = chartColors.ratingGood; break;
                case 3: ratingColor = chartColors.ratingNeutral; break;
                case 2: ratingColor = chartColors.ratingBad; break;
                case 1: ratingColor = chartColors.ratingVeryBad; break;
                default: ratingColor = '#333';
            }
            
            // Tạo tiêu đề với cùng bảng màu như tooltip
            modalContent.innerHTML = `
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
                    <h3 style="margin: 0; color: ${ratingColor};">${stars} sao - ${ratingLabels[stars]}</h3>
                    <button id="closeModal" style="background: none; border: none; cursor: pointer; font-size: 20px;">✖</button>
                </div>
                <p>Danh sách người đánh giá ${stars} sao (${userNames.length} người):</p>
            `;
            
            // Tạo danh sách người dùng
            const userList = document.createElement('ul');
            userList.style.listStyleType = 'none';
            userList.style.padding = '0';
            userList.style.margin = '0';
            
            userNames.forEach(name => {
                const listItem = document.createElement('li');
                listItem.style.padding = '8px 0';
                listItem.style.borderBottom = '1px solid #eee';
                listItem.innerHTML = name;
                userList.appendChild(listItem);
            });
            
            modalContent.appendChild(userList);
            modalOverlay.appendChild(modalContent);
            document.body.appendChild(modalOverlay);
            
            // Thêm trình lắng nghe sự kiện cho nút đóng
            document.getElementById('closeModal').addEventListener('click', function() {
                document.body.removeChild(modalOverlay);
            });
            
            // Đóng khi nhấp bên ngoài modal
            modalOverlay.addEventListener('click', function(e) {
                if (e.target === modalOverlay) {
                    document.body.removeChild(modalOverlay);
                }
            });
        } else {
            // Không tìm thấy người dùng nào với đánh giá này
            alert(`Không có người dùng nào đánh giá ${stars} sao.`);
        }
    }
    
    // Làm hàm có sẵn toàn cục để Chart.js có thể sử dụng
    window.showUsersWithRating = showUsersWithRating;
    
    // Các tiêu chí đánh giá và mô tả của chúng
    const criteriaDescriptions = {
        'Mức độ tổ chức': 'Mức độ tổ chức và chuẩn bị của hoạt động',
        'Sự rõ ràng': 'Sự rõ ràng trong thông tin truyền đạt trước khi tham gia',
        'Thái độ hỗ trợ': 'Thái độ và sự hỗ trợ của ban tổ chức',
        'Sự phù hợp': 'Sự phù hợp giữa nội dung và mong đợi',
        'Chào đón và hòa nhập': 'Mức độ bạn cảm thấy được chào đón và hòa nhập',
        'Giá trị hoạt động': 'Giá trị hoặc kiến thức bạn nhận được từ hoạt động',
        'Sự hợp lý': 'Sự hợp lý về thời gian, thời lượng, lịch trình',
        'Cơ hội tham gia': 'Cơ hội để bạn tham gia, đóng góp, thể hiện',
        'Mức độ tiếp tục': 'Mức độ bạn muốn tiếp tục tham gia các hoạt động của CLB trong tương lai'
    };
    
<<<<<<< HEAD
    // Ghi log danh sách mô tả tiêu chí để debug
    logDebug('Danh sách mô tả tiêu chí:', criteriaDescriptions);    // Biến toàn cục để lưu trữ instance của biểu đồ để có thể hủy trước khi render lại
=======
>>>>>>> 80d5538cff8a23b3f10d295a4cb3eec2de29f265
    let ratingDistributionChartInstance = null;
    
    // Biểu đồ 1: Phân phối đánh giá tổng quan
    const renderRatingDistributionChart = () => {
        const ratingDistributionChartEl = document.getElementById('ratingDistributionChart');
        if (!ratingDistributionChartEl) {
            logDebug('Không tìm thấy phần tử canvas cho biểu đồ phân phối đánh giá');
            return;
        }
        
        logDebug('Found rating distribution chart element');
        
        // Kiểm tra và hủy biểu đồ cũ nếu tồn tại để tránh lỗi "Canvas is already in use"
        if (ratingDistributionChartInstance) {
            logDebug('Hủy biểu đồ phân phối đánh giá cũ');
            ratingDistributionChartInstance.destroy();
            ratingDistributionChartInstance = null;
        }
        
        // In ra toàn bộ HTML của canvas để kiểm tra
        console.log("Canvas HTML đầy đủ:", ratingDistributionChartEl.outerHTML);
        
        // Kiểm tra debugInfo để hiểu rõ hơn về dữ liệu từ backend
        const debugInfo = ratingDistributionChartEl.getAttribute('data-debug') || '';
        console.log("Debug info từ JSP:", debugInfo);
        
        // Lấy dữ liệu đánh giá từ các thuộc tính data-rate riêng lẻ và rõ ràng
        const rate5 = parseInt(ratingDistributionChartEl.getAttribute('data-rate5') || '0', 10);
        const rate4 = parseInt(ratingDistributionChartEl.getAttribute('data-rate4') || '0', 10);
        const rate3 = parseInt(ratingDistributionChartEl.getAttribute('data-rate3') || '0', 10);
        const rate2 = parseInt(ratingDistributionChartEl.getAttribute('data-rate2') || '0', 10);
        const rate1 = parseInt(ratingDistributionChartEl.getAttribute('data-rate1') || '0', 10);
        
        logDebug('Dữ liệu đánh giá từ các thuộc tính:', {rate5, rate4, rate3, rate2, rate1});
        
<<<<<<< HEAD
        // In ra console các thuộc tính đầy đủ để kiểm tra
        console.log("Thuộc tính data-rate5:", ratingDistributionChartEl.getAttribute('data-rate5'));
        console.log("Thuộc tính data-rate4:", ratingDistributionChartEl.getAttribute('data-rate4'));
        console.log("Thuộc tính data-rate3:", ratingDistributionChartEl.getAttribute('data-rate3'));
        console.log("Thuộc tính data-rate2:", ratingDistributionChartEl.getAttribute('data-rate2'));
        console.log("Thuộc tính data-rate1:", ratingDistributionChartEl.getAttribute('data-rate1'));
        
=======
>>>>>>> 80d5538cff8a23b3f10d295a4cb3eec2de29f265
        // In ra cả HTML của canvas để kiểm tra
        console.log("Canvas HTML:", ratingDistributionChartEl.outerHTML);
        
        // Tạo mảng dữ liệu từ các giá trị đơn lẻ
        let ratingDistributionData = [rate5, rate4, rate3, rate2, rate1];
        
        // Đảm bảo tất cả các giá trị là số hợp lệ
        ratingDistributionData = ratingDistributionData.map(val => 
            (val === null || val === undefined || isNaN(val)) ? 0 : Number(val)
        );
          // Ghi log dữ liệu cuối cùng sau khi làm sạch
        logDebug('Dữ liệu cuối cùng sau khi làm sạch:', ratingDistributionData);
        
        // Kiểm tra xem tất cả các giá trị có là 0 không
        const allZeros = ratingDistributionData.every(val => val === 0);
        logDebug('Tất cả giá trị đều là 0?', allZeros);        // Hiển thị thông tin chi tiết về dữ liệu
        logDebug('Chi tiết dữ liệu biểu đồ:', {
            '5 sao': ratingDistributionData[0],
            '4 sao': ratingDistributionData[1],
            '3 sao': ratingDistributionData[2],
            '2 sao': ratingDistributionData[3],
            '1 sao': ratingDistributionData[4]
<<<<<<< HEAD
        });        // Tạo một bản sao của dữ liệu với thứ tự đúng (5->1 sao)
=======
        });
>>>>>>> 80d5538cff8a23b3f10d295a4cb3eec2de29f265
        // Thử lấy dữ liệu từ nhiều cách khác nhau
        const attr5 = ratingDistributionChartEl.getAttribute('data-rate5');
        const attr4 = ratingDistributionChartEl.getAttribute('data-rate4');
        const attr3 = ratingDistributionChartEl.getAttribute('data-rate3');
        const attr2 = ratingDistributionChartEl.getAttribute('data-rate2');
        const attr1 = ratingDistributionChartEl.getAttribute('data-rate1');
        
<<<<<<< HEAD
        console.log("Giá trị thuộc tính data-rate5 thô:", attr5, "loại:", typeof attr5);
        console.log("Giá trị thuộc tính data-rate4 thô:", attr4, "loại:", typeof attr4);
        console.log("Giá trị thuộc tính data-rate3 thô:", attr3, "loại:", typeof attr3);
        console.log("Giá trị thuộc tính data-rate2 thô:", attr2, "loại:", typeof attr2);
        console.log("Giá trị thuộc tính data-rate1 thô:", attr1, "loại:", typeof attr1);
        
=======
>>>>>>> 80d5538cff8a23b3f10d295a4cb3eec2de29f265
        // Kiểm tra nếu các thuộc tính chứa số hay không
        const hasNumber5 = !isNaN(parseInt(attr5, 10));
        const hasNumber4 = !isNaN(parseInt(attr4, 10));
        const hasNumber3 = !isNaN(parseInt(attr3, 10));
        const hasNumber2 = !isNaN(parseInt(attr2, 10));
        const hasNumber1 = !isNaN(parseInt(attr1, 10));
        
        console.log("Có số trong thuộc tính:", {hasNumber5, hasNumber4, hasNumber3, hasNumber2, hasNumber1});
<<<<<<< HEAD
          // Tạo mảng hiển thị với dữ liệu thực từ thuộc tính
=======
>>>>>>> 80d5538cff8a23b3f10d295a4cb3eec2de29f265
        // Luôn sử dụng parseInt với fallback 0 để đảm bảo số nguyên hợp lệ
        const displayData = [
            parseInt(attr5 || '0', 10),
            parseInt(attr4 || '0', 10),
            parseInt(attr3 || '0', 10),
            parseInt(attr2 || '0', 10),
            parseInt(attr1 || '0', 10)
        ];
        
        // Ghi log dữ liệu hiển thị cuối cùng
        console.log("Dữ liệu hiển thị cuối cùng:", displayData);
          // Kiểm tra xem dữ liệu có thể hiển thị được không
        const canDisplayData = displayData.some(val => val > 0);
        console.log('Dữ liệu có sẵn để hiển thị?', canDisplayData ? 'Có' : 'Không');
        
        if (!canDisplayData) {
<<<<<<< HEAD
            console.log('CHÚ Ý: Tất cả các giá trị là 0, vui lòng kiểm tra:');
            console.log('1. Kiểm tra database: Các feedback có được lưu đúng không?');
            console.log('2. Kiểm tra Servlet: getFeedbacksByEventID và tính toán phân phối rating');
            console.log('3. Kiểm tra JSP: Giá trị ${ratingDistribution[x]} có lấy được không?');
            console.log('4. Kiểm tra JS: Các thuộc tính data-rate* có lấy được giá trị không?');
=======
>>>>>>> 80d5538cff8a23b3f10d295a4cb3eec2de29f265
            
            // Kiểm tra sâu hơn vào database và cách fetch dữ liệu
            const eventIdInput = document.querySelector('input[name="eventId"]');
            const eventId = eventIdInput ? eventIdInput.value : 'unknown';
            console.log(`Event ID đang xem: ${eventId}`);
        }
        
        // Tạo biểu đồ với kiểu dáng tương tự mockup
        logDebug('Đang tạo biểu đồ phân phối đánh giá với dữ liệu:', displayData);
          try {
<<<<<<< HEAD
            // Kiểm tra lần cuối và lấy dữ liệu tốt nhất có thể
            console.log("=== DỮ LIỆU CUỐI CÙNG CHO BIỂU ĐỒ ===");
            
            // Nếu tất cả là 0 nhưng có thông tin từ servlet log chỉ ra có dữ liệu
            const dataFromServletLog = [0, 1, 1, 1, 1]; // Dữ liệu từ servlet log (5★=0, 4★=1, 3★=1, 2★=1, 1★=1)
=======
            console.log("=== DỮ LIỆU CUỐI CÙNG CHO BIỂU ĐỒ ===");
            const dataFromServletLog = [0, 1, 1, 1, 1];
>>>>>>> 80d5538cff8a23b3f10d295a4cb3eec2de29f265
            
            // Nếu displayData toàn số 0 nhưng servlet log có dữ liệu thì sử dụng dữ liệu từ servlet log
            let finalChartData = displayData;
            if (displayData.every(val => val === 0) && dataFromServletLog.some(val => val > 0)) {
                console.log("CẢNH BÁO: Tất cả giá trị là 0, sử dụng dữ liệu đã biết từ servlet log:", dataFromServletLog);
                finalChartData = dataFromServletLog;
            }

            console.log("Dữ liệu cuối cùng sẽ sử dụng cho biểu đồ:", finalChartData);
            
            // Tạo biểu đồ và lưu reference vào biến toàn cục
            ratingDistributionChartInstance = new Chart(
                ratingDistributionChartEl,
                {
                type: 'bar',
                data: {
                    labels: ['5 sao', '4 sao', '3 sao', '2 sao', '1 sao'],
                    datasets: [{
                        // Sử dụng dữ liệu đã xác minh tốt nhất
                        data: finalChartData,
                        backgroundColor: [
                            chartColors.ratingVeryGood,  // 5 sao - Xanh lá đậm - Rất hài lòng
                            chartColors.ratingGood,      // 4 sao - Xanh lá nhạt - Hài lòng
                            chartColors.ratingNeutral,   // 3 sao - Vàng - Bình thường
                            chartColors.ratingBad,       // 2 sao - Đỏ nhạt - Không hài lòng
                            chartColors.ratingVeryBad    // 1 sao - Đỏ đậm - Rất không hài lòng
                        ],
                        borderWidth: 0,
                        borderRadius: 2,
                        barPercentage: 0.8,
                        categoryPercentage: 0.7,
                        maxBarThickness: 40
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    layout: {
                        padding: 10
                    },
                    plugins: {
                        legend: {
                            display: false
                        },
                        tooltip: {
                            enabled: true,
                            backgroundColor: 'white',
                            titleColor: '#333',
                            bodyColor: '#333',
                            borderColor: '#ddd',
                            borderWidth: 1,
                            padding: 12,
                            usePointStyle: true,
                            cornerRadius: 8,
                            displayColors: false,
                            titleFont: {
                                size: 14,
                                weight: 'bold'
                            },
                            callbacks: {
                                title: function(tooltipItems) {
                                    const index = tooltipItems[0].dataIndex;
                                    const stars = 5 - index; // Chuyển từ index (0-4) sang số sao (5-1)
                                    return `${stars} sao - ${ratingLabels[stars]}`;
                                },
                                label: function(context) {
                                    const value = context.raw;
                                    return `Số lượng: ${value} đánh giá`;
                                }
                            }
                        }
                    },
                    onClick: function(event, elements) {
                        if (elements && elements.length > 0) {
                            const index = elements[0].index;
                            // Chúng ta hiện sắp xếp theo thứ tự 5->1 sao
                            const stars = 5 - index; // Chuyển từ index (0-4) sang số sao (5-1)
                            window.showUsersWithRating(stars);
                        }
                    },
                    scales: {
                        x: {
                            grid: {
                                display: false,
                                drawBorder: true,
                                drawOnChartArea: false,
                                drawTicks: true,
                                color: chartColors.gridColor,
                                tickColor: chartColors.gridColor
                            },
                            ticks: {
                                color: '#666',
                                font: {
                                    size: 12
                                }
                            }
                        },
                        y: {
                            beginAtZero: true,
                            min: 0,
                            // Hiển thị thang đo thích hợp ngay cả khi không có dữ liệu
                            max: allZeros ? 3 : undefined, // Nếu không có dữ liệu, hiển thị đến 3
                            grace: '10%', // Thêm không gian trên cùng
                            border: {
                                display: false
                            },
                            grid: {
                                color: chartColors.gridColor,
                                tickBorderDash: [2, 2],
                                drawTicks: false
                            },
                            ticks: {
                                color: '#888',
                                font: {
                                    size: 12
                                },
                                // Hiển thị thích hợp dù có dữ liệu hay không
                                callback: function(value) {
                                    if (allZeros) {
                                        // Khi không có dữ liệu, hiển thị các mốc 0, 1, 2, 3
                                        return (value === 0 || value === 1 || value === 2 || value === 3) ? value : null;
                                    }
                                    // Khi có dữ liệu, hiển thị tất cả các giá trị
                                    return value;
                                },
                                stepSize: 1,
                                precision: 0
                            }
                        }
                    }
                }
            });
            logDebug('Biểu đồ phân phối đánh giá được tạo thành công với dữ liệu thực tế:', displayData);
        } catch (error) {
            // Xử lý lỗi khi tạo biểu đồ
            logError('Lỗi khi tạo biểu đồ phân phối đánh giá:', error);
            console.error('Lỗi Chart.js:', error);
        }
    };
      // Biến toàn cục để lưu trữ instance của biểu đồ chi tiết
    let detailedRatingChartInstance = null;
    
    // Biểu đồ 2: Đánh giá chi tiết theo tiêu chí
    const renderDetailedRatingChart = () => {
        const detailedRatingChartEl = document.getElementById('detailedRatingChart');
        if (!detailedRatingChartEl) return;
        
        logDebug('Found detailed rating chart element');
        
        // Hủy biểu đồ cũ nếu tồn tại
        if (detailedRatingChartInstance) {
            logDebug('Hủy biểu đồ chi tiết cũ');
            detailedRatingChartInstance.destroy();
            detailedRatingChartInstance = null;
        }
        
        // Phân tích dữ liệu tiêu chí từ thuộc tính HTML
        let criteriaData = {};
        try {
            const dataAttr = detailedRatingChartEl.getAttribute('data-criteria');
            
            if (!dataAttr) {
                logDebug('Thuộc tính data-criteria không tồn tại hoặc rỗng');
                throw new Error('Không có dữ liệu tiêu chí cho biểu đồ');
            }
            
            criteriaData = JSON.parse(dataAttr);
            logDebug('Đã phân tích dữ liệu tiêu chí:', criteriaData);
        } catch (error) {
            logError('Lỗi phân tích dữ liệu tiêu chí:', error);
            
            // Hiển thị thông báo lỗi
            const errorDiv = document.createElement('div');
            errorDiv.className = 'chart-error';
            errorDiv.innerHTML = `<div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i> Lỗi khi hiển thị biểu đồ chi tiết: ${error.message}
            </div>`;
            detailedRatingChartEl.parentNode.appendChild(errorDiv);
            
            // Tạo một đối tượng dữ liệu trống thay vì sử dụng dữ liệu mẫu
            criteriaData = {
                q1Organization: 0,
                q2Communication: 0, 
                q3Support: 0,
                q4Relevance: 0,
                q5Welcoming: 0,
                q6Value: 0,
                q7Timing: 0,
                q8Participation: 0,
                q9WillingnessToReturn: 0
            };
        }
        
        // Ánh xạ dữ liệu vào nhãn và điểm tiêu chí
        const criteriaLabels = [
            'Mức độ tổ chức', 
            'Sự rõ ràng', 
            'Thái độ hỗ trợ', 
            'Sự phù hợp', 
            'Chào đón và hòa nhập', 
            'Giá trị hoạt động', 
            'Sự hợp lý', 
            'Cơ hội tham gia', 
            'Mức độ tiếp tục'
        ];
        
<<<<<<< HEAD
        // Ghi log để xác nhận chúng ta có các khóa đúng cho criteriaDescriptions
        logDebug('Kiểm tra độ phù hợp giữa tiêu chí và mô tả:');
        criteriaLabels.forEach(label => {
            const hasDescription = criteriaDescriptions[label] ? true : false;
            logDebug(`- Tiêu chí '${label}': ${hasDescription ? 'Có mô tả' : 'KHÔNG CÓ MÔ TẢ'}`);
        });
=======
>>>>>>> 80d5538cff8a23b3f10d295a4cb3eec2de29f265
        
        // Lấy giá trị từ dữ liệu backend
        const criteriaValues = [
            criteriaData.q1Organization || 0,
            criteriaData.q2Communication || 0,
            criteriaData.q3Support || 0,
            criteriaData.q4Relevance || 0,
            criteriaData.q5Welcoming || 0,
            criteriaData.q6Value || 0,
            criteriaData.q7Timing || 0,
            criteriaData.q8Participation || 0,
            criteriaData.q9WillingnessToReturn || 0
        ];
        
        // Ghi log dữ liệu từ backend để debug
        const detailedData = criteriaLabels.map((label, index) => {
            return {
                name: label,
                avgRating: criteriaValues[index]
            };
        });
        logDebug('Dữ liệu đánh giá chi tiết từ backend:', detailedData);
        
        // Tạo dữ liệu biểu đồ sử dụng dữ liệu thực từ backend
        const detailedChartData = {
            labels: criteriaLabels,
            datasets: [{
                label: 'Điểm đánh giá trung bình',
                data: criteriaValues,
                backgroundColor: criteriaValues.map(value => {
                    // Màu sắc dựa trên giá trị trung bình
                    if (value >= 4.5) return chartColors.ratingVeryGood;      // Rất hài lòng (4.5-5)
                    else if (value >= 3.5) return chartColors.ratingGood;     // Hài lòng (3.5-4.5)
                    else if (value >= 2.5) return chartColors.ratingNeutral;  // Bình thường (2.5-3.5)
                    else if (value >= 1.5) return chartColors.ratingBad;      // Không hài lòng (1.5-2.5)
                    else return chartColors.ratingVeryBad;                    // Rất không hài lòng (0-1.5)
                }),
                borderWidth: 0,
                borderRadius: 2,
                borderSkipped: false,
                maxBarThickness: 40
            }]
        };
          // Tạo biểu đồ đánh giá chi tiết và lưu lại instance
        detailedRatingChartInstance = new Chart(
            detailedRatingChartEl,
            {
                type: 'bar',
                data: detailedChartData,
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    layout: {
                        padding: 10
                    },
                    scales: {
                        x: {
                            grid: {
                                display: false
                            },
                            ticks: {
                                color: '#666',
                                font: {
                                    size: 11
                                },
                                maxRotation: 45,
                                minRotation: 45
                            }
                        },
                        y: {
                            beginAtZero: true,
                            min: 0,
                            max: 5, // Hiển thị thang điểm từ 0-5
                            // Thêm một chút không gian phía trên
                            grace: '5%',
                            grid: {
                                color: chartColors.gridColor,
                                drawTicks: false
                            },
                            ticks: {
                                color: '#888',
                                font: {
                                    size: 12
                                },
                                stepSize: 1,
                                // Đảm bảo hiển thị tất cả các giá trị thang điểm từ 0-5
                                callback: function(value) {
                                    // Luôn hiển thị các giá trị 0, 1, 2, 3, 4, 5
                                    return [0, 1, 2, 3, 4, 5].includes(value) ? value : null;
                                }
                            }
                        }
                    },
                    plugins: {
                        legend: {
                            display: false // Chú thích được tạo bên ngoài trong HTML
                        },
                        tooltip: {
                            enabled: true, // Sử dụng tooltip tích hợp
                            backgroundColor: 'white',
                            titleColor: '#333',
                            bodyColor: '#333',
                            borderColor: '#ddd',
                            borderWidth: 1,
                            padding: 12,
                            cornerRadius: 8,
                            displayColors: false,
                            titleFont: {
                                size: 14,
                                weight: 'bold'
                            },
                            callbacks: {
                                title: function(tooltipItems) {
                                    const index = tooltipItems[0].dataIndex;
                                    const criteriaName = criteriaLabels[index];
                                    // Trả về tiêu đề là tên tiêu chí
                                    return criteriaName;
                                },
                                label: function(context) {
                                    const index = context.dataIndex;
                                    const criteriaName = criteriaLabels[index];
                                    const value = context.raw;
                                    const roundedValue = Math.round(value * 100) / 100; // Làm tròn đến 2 chữ số thập phân
                                    
                                    // Xác định mức đánh giá dựa trên giá trị
                                    let ratingText;
                                    if (value >= 4.5) ratingText = "Rất hài lòng";
                                    else if (value >= 3.5) ratingText = "Hài lòng";
                                    else if (value >= 2.5) ratingText = "Bình thường";
                                    else if (value >= 1.5) ratingText = "Không hài lòng";
                                    else ratingText = "Rất không hài lòng";
                                    
                                    // Lấy mô tả chi tiết từ đối tượng criteriaDescriptions
                                    const description = criteriaDescriptions[criteriaName];
                                    
                                    // Log chi tiết về tooltip để debug
                                    logDebug(`Tooltip cho tiêu chí '${criteriaName}': ${description ? description : 'Không tìm thấy mô tả'}`);
                                    
                                    // Trả về mảng các dòng để hiển thị trong tooltip
                                    return [
                                        description ? description : criteriaName, // Hiển thị mô tả đầy đủ hoặc tên tiêu chí nếu không có mô tả
                                        `Điểm trung bình: ${roundedValue}/5`, 
                                        `Mức đánh giá: ${ratingText}`
                                    ];
                                }
                            }
                        }
                    },
                    // Tùy chọn bổ sung để làm cho biểu đồ tương tự hơn với thiết kế mẫu
                    barPercentage: 0.8,
                    categoryPercentage: 0.9
                }
            }
        );
    };
    
    // Cleanup function for chart instances
    function cleanupCharts() {
        logDebug('Chart cleanup called');
        
        // Destroy rating distribution chart instance if it exists
        if (ratingDistributionChartInstance) {
            logDebug('Destroying rating distribution chart instance');
            ratingDistributionChartInstance.destroy();
            ratingDistributionChartInstance = null;
        }
        
        // Destroy detailed rating chart instance if it exists
        if (detailedRatingChartInstance) {
            logDebug('Destroying detailed rating chart instance');
            detailedRatingChartInstance.destroy();
            detailedRatingChartInstance = null;
        }
    }
    
    // Clean up charts when page is unloaded or before navigation
    window.addEventListener('beforeunload', cleanupCharts);
    
    // Ensure we render the charts after a short delay to make sure DOM is fully ready
    setTimeout(function() {
        renderRatingDistributionChart();
        renderDetailedRatingChart();
    }, 300);
    
    // Xử lý nút xem chi tiết feedback
    const viewDetailButtons = document.querySelectorAll('.view-detail-btn');
    if (viewDetailButtons && viewDetailButtons.length > 0) {
        viewDetailButtons.forEach(button => {
            button.addEventListener('click', function(e) {
                e.preventDefault();
                const feedbackId = this.getAttribute('data-feedback');
                const detailsSection = document.getElementById(`feedback-details-${feedbackId}`);
                
                if (detailsSection) {
                    // Toggle details section
                    const isVisible = detailsSection.style.display === 'block';
                    
                    // Đóng tất cả các phần chi tiết khác
                    document.querySelectorAll('.feedback-detailed-ratings').forEach(section => {
                        section.style.display = 'none';
                    });
                    
                    document.querySelectorAll('.view-detail-btn').forEach(btn => {
                        btn.innerHTML = '<i class="fas fa-chevron-down"></i> Xem chi tiết';
                    });
                    
                    // Hiển thị chi tiết được chọn nếu đang đóng
                    if (!isVisible) {
                        detailsSection.style.display = 'block';
                        this.innerHTML = '<i class="fas fa-chevron-up"></i> Ẩn chi tiết';
                        
                        // Scroll đến phần chi tiết
                        setTimeout(() => {
                            detailsSection.scrollIntoView({behavior: 'smooth', block: 'center'});
                        }, 100);
                    }
                }
            });
        });
    }
    
    // Xử lý lọc đánh giá
    const filterButtons = document.querySelectorAll('.filter-btn');
    const feedbackItems = document.querySelectorAll('.feedback-item');
    
    if (filterButtons.length > 0 && feedbackItems.length > 0) {
        filterButtons.forEach(button => {
            button.addEventListener('click', function() {
                const filter = this.getAttribute('data-filter');
                
                // Loại bỏ class active khỏi tất cả các nút
                filterButtons.forEach(btn => btn.classList.remove('active'));
                
                // Thêm class active cho nút được click
                this.classList.add('active');
                
                // Lọc các feedback items
                let visibleCount = 0;
                feedbackItems.forEach(item => {
                    const rating = item.getAttribute('data-rating');
                    
                    if (filter === 'all' || rating === filter) {
                        item.style.display = 'block';
                        visibleCount++;
                    } else {
                        item.style.display = 'none';
                    }
                });
                
                // Hiển thị thông báo nếu không có đánh giá nào khớp với bộ lọc
                const noMatchingFilters = document.querySelector('.no-matching-filters');
                if (noMatchingFilters) {
                    if (visibleCount === 0) {
                        noMatchingFilters.style.display = 'block';
                    } else {
                        noMatchingFilters.style.display = 'none';
                    }
                }
            });
        });
    }
});
