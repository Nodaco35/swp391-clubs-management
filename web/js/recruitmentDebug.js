/**
 * Module debug dùng để kiểm tra và giám sát dữ liệu cho form tuyển quân
 */

// Tạo panel debug 
function createDebugPanel() {
    // Kiểm tra xem panel đã tồn tại chưa
    if (document.getElementById('recruitmentDebugPanel')) return;
    
    // Tạo panel
    const panel = document.createElement('div');
    panel.id = 'recruitmentDebugPanel';
    panel.style.position = 'fixed';
    panel.style.bottom = '10px';
    panel.style.right = '10px';
    panel.style.width = '300px';
    panel.style.maxHeight = '400px';
    panel.style.overflow = 'auto';
    panel.style.backgroundColor = '#f8d7da';
    panel.style.borderRadius = '5px';
    panel.style.border = '1px solid #f5c6cb';
    panel.style.padding = '10px';
    panel.style.color = '#721c24';
    panel.style.zIndex = '9999';
    
    // Tạo header
    const header = document.createElement('div');
    header.style.display = 'flex';
    header.style.justifyContent = 'space-between';
    header.style.marginBottom = '10px';
    
    const title = document.createElement('h3');
    title.textContent = 'Debug Panel';
    title.style.margin = '0';
    header.appendChild(title);
    
    const closeBtn = document.createElement('button');
    closeBtn.textContent = 'X';
    closeBtn.style.background = 'none';
    closeBtn.style.border = 'none';
    closeBtn.style.cursor = 'pointer';
    closeBtn.style.color = '#721c24';
    closeBtn.style.fontWeight = 'bold';
    closeBtn.onclick = function() {
        panel.style.display = 'none';
    };
    header.appendChild(closeBtn);
    
    panel.appendChild(header);
    
    // Container cho nội dung debug
    const content = document.createElement('div');
    content.id = 'recruitmentDebugContent';
    panel.appendChild(content);
    
    // Thêm vào body
    document.body.appendChild(panel);
    
    return panel;
}

// Hiển thị thông tin debug
function debugRecruitment(message, data) {
    const panel = createDebugPanel();
    const content = document.getElementById('recruitmentDebugContent');
    
    // Tạo entry mới
    const entry = document.createElement('div');
    entry.style.marginBottom = '10px';
    entry.style.paddingBottom = '10px';
    entry.style.borderBottom = '1px dashed #f5c6cb';
    
    // Timestamp
    const timestamp = document.createElement('div');
    timestamp.style.fontSize = '10px';
    timestamp.style.color = '#777';
    timestamp.textContent = new Date().toLocaleTimeString();
    entry.appendChild(timestamp);
    
    // Message
    const messageElem = document.createElement('div');
    messageElem.style.fontWeight = 'bold';
    messageElem.textContent = message;
    entry.appendChild(messageElem);
    
    // Data (if provided)
    if (data) {
        const dataElem = document.createElement('pre');
        dataElem.style.fontSize = '12px';
        dataElem.style.maxHeight = '100px';
        dataElem.style.overflow = 'auto';
        dataElem.style.backgroundColor = '#f5f5f5';
        dataElem.style.padding = '5px';
        dataElem.style.marginTop = '5px';
        dataElem.style.borderRadius = '3px';
        dataElem.textContent = JSON.stringify(data, null, 2);
        entry.appendChild(dataElem);
    }
    
    // Thêm vào đầu content
    content.insertBefore(entry, content.firstChild);
    
    // Hiển thị panel
    panel.style.display = 'block';
    
    // Log ra console đồng thời
    console.log('RECRUITMENT DEBUG:', message, data || '');
}

// Kiểm tra dữ liệu stages từ server
document.addEventListener('DOMContentLoaded', function() {
    // Kiểm tra xem biến stagesFromServer có tồn tại không
    setTimeout(() => {
        if (typeof stagesFromServer !== 'undefined') {
            debugRecruitment(`Dữ liệu stages từ server: ${stagesFromServer.length} vòng`, stagesFromServer);
        } else {
            debugRecruitment('Không có dữ liệu stages từ server');
        }
        
        // Kiểm tra các trường input trong form
        const applicationStageStart = document.getElementById('applicationStageStart');
        const applicationStageEnd = document.getElementById('applicationStageEnd');
        const interviewStageStart = document.getElementById('interviewStageStart');
        const interviewStageEnd = document.getElementById('interviewStageEnd');
        const challengeStageStart = document.getElementById('challengeStageStart');
        const challengeStageEnd = document.getElementById('challengeStageEnd');
        
        debugRecruitment('Giá trị trường vòng nộp đơn', {
            startDate: applicationStageStart ? applicationStageStart.value : 'Element không tồn tại',
            endDate: applicationStageEnd ? applicationStageEnd.value : 'Element không tồn tại'
        });
        
        debugRecruitment('Giá trị trường vòng phỏng vấn', {
            startDate: interviewStageStart ? interviewStageStart.value : 'Element không tồn tại',
            endDate: interviewStageEnd ? interviewStageEnd.value : 'Element không tồn tại'
        });
        
        // Kiểm tra xem form đang ở mode nào
        const form = document.getElementById('recruitmentForm');
        if (form) {
            const mode = form.dataset.mode;
            const recruitmentId = document.querySelector('input[name="recruitmentId"]')?.value;
            debugRecruitment('Thông tin form', {
                mode: mode,
                recruitmentId: recruitmentId || 'Không có ID'
            });
        }
        
    }, 1000); // Chờ 1s để đảm bảo các script khác đã chạy xong
});

// Thêm hàm kiểm tra việc điền form
function monitorFormFilling() {
    // Lấy tất cả input, select và textarea trong form
    const form = document.getElementById('recruitmentForm');
    if (!form) return;
    
    const inputs = form.querySelectorAll('input, select, textarea');
    
    // Thêm sự kiện input cho mỗi trường
    inputs.forEach(input => {
        input.addEventListener('change', function(e) {
            debugRecruitment(`Trường ${e.target.id || e.target.name} đã thay đổi`, {
                value: e.target.value,
                element: e.target.id || e.target.name
            });
        });
    });
}

// Gọi hàm monitor khi trang tải xong
document.addEventListener('DOMContentLoaded', monitorFormFilling);
