
// Lưu trữ instance biểu đồ để có thể destroy sau này nếu cần
let ratingDistributionChartInstance = null;

const chartColors = {
    ratingVeryGood: '#2e7d32',
    ratingGood: '#4caf50',
    ratingNeutral: '#ffc107',
    ratingBad: '#ff5252',
    ratingVeryBad: '#b71c1c',
    gridColor: '#e5e5e5'
};

// Nhãn đánh giá
const ratingLabels = {
    5: 'Rất hài lòng',
    4: 'Hài lòng',
    3: 'Bình thường',
    2: 'Không hài lòng',
    1: 'Rất không hài lòng'
};

/**
 * Tải dữ liệu đánh giá từ API
 */
function loadRatingData() {
    showLoadingState();

    // Lấy ID sự kiện từ meta tag
    const eventId = document.querySelector('meta[name="eventId"]').getAttribute('content');
    if (!eventId) {
        console.error('Không tìm thấy eventId trong meta tag');
        showErrorState('Không thể xác định ID của sự kiện');
        return;
    }

    // Gọi API
    fetch(`${window.location.pathname}?eventId=${eventId}&format=json`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Dữ liệu từ API:', data);
            
            hideLoadingState();
            
            document.getElementById('chartContainer').style.display = 'block';
              // Vẽ biểu đồ phân phối đánh giá
            renderRatingDistributionChart(data.ratingDistribution);
            
        })
        .catch(error => {
            console.error('Lỗi khi tải dữ liệu:', error);
            showErrorState('Không thể tải dữ liệu biểu đồ');
        });
}

function showLoadingState() {
    const loadingElement = document.getElementById('chartLoadingState');
    if (loadingElement) {
        loadingElement.style.display = 'flex';
    }
    
    const chartContainer = document.getElementById('chartContainer');
    if (chartContainer) {
        chartContainer.style.display = 'none';
    }
    
    const errorElement = document.getElementById('chartErrorState');
    if (errorElement) {
        errorElement.style.display = 'none';
    }
}

function hideLoadingState() {
    const loadingElement = document.getElementById('chartLoadingState');
    if (loadingElement) {
        loadingElement.style.display = 'none';
    }
}


function showErrorState(message) {
    const errorElement = document.getElementById('chartErrorState');
    if (errorElement) {
        const messageElement = errorElement.querySelector('.error-message');
        if (messageElement) {
            messageElement.textContent = message || 'Không thể tải dữ liệu biểu đồ';
        }
        errorElement.style.display = 'flex';
    }
    
    hideLoadingState();
    
    const chartContainer = document.getElementById('chartContainer');
    if (chartContainer) {
        chartContainer.style.display = 'none';
    }
}

/**
 * Vẽ biểu đồ phân phối đánh giá
 */
function renderRatingDistributionChart(ratingData) {
    const chartElement = document.getElementById('ratingDistributionChart');
    if (!chartElement) {
        console.error('Không tìm thấy phần tử canvas cho biểu đồ phân phối');
        return;
    }
    
    // Kiểm tra Chart.js để đảm bảo không có xung đột
    const existingChart = Chart.getChart(chartElement);
    if (existingChart) {
        console.log('Đã tìm thấy biểu đồ hiện có, hủy nó trước khi tạo mới');
        existingChart.destroy();
    }
    
    // Hủy biểu đồ cũ nếu có
    if (ratingDistributionChartInstance) {
        try {
            ratingDistributionChartInstance.destroy();
        } catch (error) {
            console.warn('Lỗi khi hủy biểu đồ:', error);
        }
        ratingDistributionChartInstance = null;
    }
    
    // Chuẩn bị dữ liệu
    const chartData = [
        ratingData.rate5 || 0,
        ratingData.rate4 || 0,
        ratingData.rate3 || 0,
        ratingData.rate2 || 0,
        ratingData.rate1 || 0
    ];
    
    // Kiểm tra xem có dữ liệu không
    const allZeros = chartData.every(val => val === 0);
    
    // Tạo biểu đồ
    ratingDistributionChartInstance = new Chart(chartElement, {
        type: 'bar',
        data: {
            labels: ['5 sao', '4 sao', '3 sao', '2 sao', '1 sao'],
            datasets: [{
                data: chartData,
                backgroundColor: [
                    chartColors.ratingVeryGood,
                    chartColors.ratingGood,
                    chartColors.ratingNeutral,
                    chartColors.ratingBad,
                    chartColors.ratingVeryBad
                ],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                title: {
                    display: true,
                    text: 'Phân phối đánh giá',
                    font: {
                        size: 16
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const value = context.raw || 0;
                            const index = context.dataIndex;
                            const stars = 5 - index; // Chuyển đổi từ vị trí (0-4) sang số sao (5-1)
                            return `${value} người đánh giá ${stars} sao`;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        color: chartColors.gridColor
                    },
                    ticks: {
                        precision: 0
                    },
                    title: {
                        display: true,
                        text: 'Số lượng đánh giá'
                    }
                },
                x: {
                    grid: {
                        display: false
                    }
                }
            }
        }
    });
    
    // Hiển thị thông báo nếu không có dữ liệu
    if (allZeros) {
        showNoDataMessage(chartElement);
    }
}



/**
 * Hiển thị thông báo không có dữ liệu trên biểu đồ
 */
function showNoDataMessage(chartElement) {
    const container = chartElement.parentNode;
    const message = document.createElement('div');
    message.className = 'no-data-message';
    message.style.position = 'absolute';
    message.style.top = '50%';
    message.style.left = '50%';
    message.style.transform = 'translate(-50%, -50%)';
    message.style.textAlign = 'center';
    message.style.color = '#888';
    message.style.fontSize = '14px';
    message.innerHTML = '<i class="fas fa-info-circle"></i><br>Chưa có dữ liệu đánh giá';
    
    container.style.position = 'relative';
    container.appendChild(message);
}


function retryLoadData() {
    loadRatingData();
}


function checkIfUsingAPI() {
    // Nếu có tham số useJsonApi=true trong URL hoặc trong localStorage, sử dụng JSON API
    const urlParams = new URLSearchParams(window.location.search);
    const useApiFromUrl = urlParams.get('useJsonApi') === 'true';
    const useApiFromStorage = localStorage.getItem('useJsonApi') === 'true';
    
    return useApiFromUrl || useApiFromStorage;
}

// Lưu trạng thái sử dụng API vào localStorage
function setUseJsonApi(value) {
    localStorage.setItem('useJsonApi', value ? 'true' : 'false');
}

// Tải dữ liệu khi trang đã tải xong
document.addEventListener('DOMContentLoaded', function() {
    // Kiểm tra xem có nên sử dụng phương pháp JSON API không
    if (checkIfUsingAPI()) {
        console.log('Sử dụng phương pháp JSON API cho biểu đồ');
        
        // Tải dữ liệu qua API
        loadRatingData();
    } else {
        console.log('Không sử dụng phương pháp JSON API, quay lại cách cũ');
    }
});
