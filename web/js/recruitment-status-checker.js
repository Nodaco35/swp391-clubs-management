/**
 * Recruitment Status Checker
 * Script này sẽ kiểm tra trạng thái của các hoạt động tuyển quân đang diễn ra 
 * và tự động đóng hoạt động khi qua ngày kết thúc.
 * 
 * Cũng sẽ cập nhật trạng thái nút "Tham gia câu lạc bộ" dựa trên trạng thái của các vòng tuyển quân.
 */

document.addEventListener('DOMContentLoaded', function() {
    // Chỉ chạy trên các trang cần kiểm tra trạng thái tuyển quân
    const pathsToCheck = ['/club-detail', '/clubs', '/index.jsp', '/'];
    const currentPath = window.location.pathname.split('/').pop();
    
    if (pathsToCheck.includes(currentPath) || currentPath === '') {
        initializeRecruitmentStatusChecker();
    }
    
    // Cập nhật nút tham gia câu lạc bộ nếu đang ở trang chi tiết câu lạc bộ
    if (currentPath.includes('club-detail') || document.querySelector('.club-detail-page')) {
        initializeJoinClubButton();
    }
});

/**
 * Khởi tạo bộ kiểm tra trạng thái tuyển quân
 */
function initializeRecruitmentStatusChecker() {
    // Kiểm tra và đóng các hoạt động tuyển quân đã hết hạn
    checkAndCloseExpiredCampaigns();
    
    // Thiết lập kiểm tra định kỳ mỗi giờ
    setInterval(checkAndCloseExpiredCampaigns, 3600000); // 1 giờ = 3600000 ms
}

/**
 * Kiểm tra và đóng các hoạt động tuyển quân đã hết hạn
 */
function checkAndCloseExpiredCampaigns() {
    const contextPath = document.querySelector('meta[name="contextPath"]')?.content || '';
    
    // Gọi API endpoint mới để kiểm tra và đóng hoạt động tuyển quân đã hết hạn
    fetch(`${contextPath}/recruitment/checkExpired`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            console.log(`Kết quả kiểm tra hoạt động tuyển quân: ${data.message}`);
            
            // Nếu có hoạt động nào đã được đóng, làm mới trạng thái trang
            if (data.closedCount > 0) {
                console.log(`Đã đóng ${data.closedCount} hoạt động tuyển quân hết hạn`);
                
                // Làm mới trạng thái nút tham gia nếu đang ở trang chi tiết câu lạc bộ
                if (window.location.pathname.includes('club-detail')) {
                    refreshClubStatus();
                }
            }
        } else {
            console.error('Không thể kiểm tra hoạt động tuyển quân:', data.message);
        }
    })
    .catch(error => {
        console.error('Lỗi khi kiểm tra hoạt động tuyển quân:', error);
    });
}

/**
 * Làm mới thông tin trạng thái của câu lạc bộ
 */
function refreshClubStatus() {
    // Nếu đang ở trang chi tiết câu lạc bộ, làm mới để cập nhật trạng thái
    const recruitingStatus = document.querySelector('.recruiting-status');
    if (recruitingStatus) {
        recruitingStatus.innerHTML = '<span class="status-indicator closed"></span> Không tuyển thành viên';
    }
    
    // Cập nhật trạng thái nút tham gia
    initializeJoinClubButton();
}

/**
 * Biến global để lưu thông tin Hoạt động hiện tại
 */
let currentCampaignData = null;
let currentStagesData = null;
let applicationFormUrl = null;
let isApplicationStageActive = false;

/**
 * Khởi tạo nút Tham gia câu lạc bộ
 */
function initializeJoinClubButton() {
    // Kiểm tra xem đang ở trang club-detail không
    const isClubDetail = document.body.classList.contains('club-detail-page') || 
                       window.location.pathname.includes('club-detail');
    if (!isClubDetail) return;
    
    // Tìm nút Tham gia CLB
    let joinButton = document.getElementById('joinClubButton');
    if (!joinButton) {
        console.warn('Không tìm thấy nút tham gia câu lạc bộ');
        return;
    }
    
    // Lấy clubId từ nhiều nguồn
    let clubId = null;
    
    // Ưu tiên lấy từ data attribute
    if (joinButton.hasAttribute('data-club-id')) {
        clubId = joinButton.getAttribute('data-club-id');
    }
    
    // Nếu không có, thử lấy từ URL
    if (!clubId) {
        clubId = new URLSearchParams(window.location.search).get('id');
    }
    
    // Nếu vẫn không có, dừng lại
    if (!clubId) {
        console.warn('Không thể xác định ID câu lạc bộ');
        return;
    }
    
    const contextPath = document.querySelector('meta[name="contextPath"]')?.content || '';
    
    console.log(`Đang kiểm tra hoạt động tuyển quân cho câu lạc bộ ID: ${clubId}`);
    
    // Thêm class để xác định trạng thái đang tải
    joinButton.classList.add('loading');
    joinButton.setAttribute('title', 'Đang tải thông tin tuyển quân...');
    
    // Thêm sự kiện click để mở modal thay vì chuyển hướng
    joinButton.addEventListener('click', function(e) {
        e.preventDefault(); // Ngăn chặn hành vi mặc định (điều hướng)
        
        // Chỉ mở modal nếu nút không bị disabled và có thông tin Hoạt động
        if (!joinButton.classList.contains('disabled') && currentCampaignData) {
            showRecruitmentModal();
        }
    });
    
    // Kiểm tra xem câu lạc bộ có hoạt động tuyển quân đang diễn ra không
    fetch(`${contextPath}/recruitment/club-campaigns?clubId=${clubId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        joinButton.classList.remove('loading');
        
        if (!data.success) {
            console.warn('Không thể lấy thông tin tuyển quân:', data.message);
            return;
        }
        
        const activeCampaigns = data.campaigns?.filter(c => c.status === 'ONGOING') || [];
        
        if (activeCampaigns.length > 0) {
            // Lưu thông tin Hoạt động hiện tại vào biến global
            currentCampaignData = activeCampaigns[0];
            
            fetch(`${contextPath}/recruitment/stages?recruitmentId=${currentCampaignData.recruitmentId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => response.json())
            .then(stageData => {
                if (!stageData.success || !stageData.stages) {
                    console.warn('Không thể lấy thông tin các vòng tuyển:', stageData.message);
                    return;
                }
                
                // Lưu thông tin các vòng tuyển
                currentStagesData = stageData.stages;
                
                const now = new Date();
                const applicationStage = stageData.stages.find(stage => stage.stageName === 'APPLICATION');
                
                if (applicationStage) {
                    const appStartDate = new Date(applicationStage.startDate);
                    const appEndDate = new Date(applicationStage.endDate);
                    
                    // Debug log thời gian thực
                    console.log('DEBUG TIME CHECK:', {
                        now: now.toLocaleString(),
                        nowDate: formatDate(now),
                        appStartDate: appStartDate.toLocaleString(),
                        appStartDateFormatted: formatDate(appStartDate),
                        appEndDate: appEndDate.toLocaleString(),
                        appEndDateFormatted: formatDate(appEndDate),
                        sameDay: sameDay(now, appStartDate)
                    });
                    
                    // Kiểm tra ngày, bỏ qua phần thời gian
                    const startDateOnly = new Date(appStartDate.getFullYear(), appStartDate.getMonth(), appStartDate.getDate());
                    const endDateOnly = new Date(appEndDate.getFullYear(), appEndDate.getMonth(), appEndDate.getDate());
                    const nowDateOnly = new Date(now.getFullYear(), now.getMonth(), now.getDate());
                    
                    // Kiểm tra đúng ngày hiện tại so với ngày bắt đầu và ngày kết thúc bằng getTime()
                    const isToday = nowDateOnly.getTime() === startDateOnly.getTime();
                    const isAfterStartDay = nowDateOnly.getTime() > startDateOnly.getTime();
                    const isBeforeEndDay = nowDateOnly.getTime() < endDateOnly.getTime();
                    const isEndDay = nowDateOnly.getTime() === endDateOnly.getTime();
                    
                    console.log('DEBUG EXACT COMPARISON:', {
                        isToday,
                        isAfterStartDay,
                        isBeforeEndDay,
                        isEndDay,
                        compareStartDay: nowDateOnly.getTime() - startDateOnly.getTime(),
                        compareEndDay: nowDateOnly.getTime() - endDateOnly.getTime()
                    });
                    
                    // Kiểm tra nếu đang trong thời gian vòng đơn (để cập nhật biến global)
                    if ((isToday || isAfterStartDay) && (isBeforeEndDay || isEndDay)) {
                        // Vòng đơn đang diễn ra
                        console.log('DEBUG: Vòng đơn đang diễn ra - Ngày nằm trong khoảng');
                        applicationFormUrl = `${contextPath}/applicationForm?formId=${currentCampaignData.formId}`;
                        isApplicationStageActive = true;
                    } else {
                        // Vòng đơn chưa bắt đầu hoặc đã kết thúc
                        isApplicationStageActive = false;
                    }
                    
                    // Nút "Xem thông tin tuyển quân" luôn hiển thị và có thể nhấp vào
                    joinButton.innerHTML = `<i class="fas fa-info-circle"></i> Xem thông tin tuyển quân`;
                    joinButton.classList.remove('disabled');
                    joinButton.classList.remove('btn-secondary');
                    joinButton.classList.add('btn-info');
                    
                } else {
                    // Không có vòng APPLICATION
                    isApplicationStageActive = false;
                    joinButton.innerHTML = `<i class="fas fa-info-circle"></i> Xem thông tin tuyển quân`;
                    joinButton.classList.remove('disabled');
                    joinButton.classList.remove('btn-secondary');
                    joinButton.classList.add('btn-info');
                }
                
                // Thiết lập modal cho Hoạt động
                setupRecruitmentModal();
            })
            .catch(error => {
                console.error('Lỗi khi lấy thông tin vòng tuyển:', error);
            });
        } else {
            // Không có hoạt động tuyển quân nào đang diễn ra
            currentCampaignData = null;
            currentStagesData = null;
            isApplicationStageActive = false;
            joinButton.href = '#';
            joinButton.innerHTML = '<i class="fas fa-times-circle"></i> Câu lạc bộ không tuyển thành viên';
            joinButton.classList.add('disabled', 'btn-secondary');
            joinButton.classList.remove('btn-primary', 'btn-info');
        }
        
        // Thêm hiệu ứng cập nhật
        joinButton.classList.add('button-updated');
        setTimeout(() => {
            joinButton.classList.remove('button-updated');
        }, 600);
    })
    .catch(error => {
        console.error('Lỗi khi lấy thông tin tuyển quân:', error);
    });
}

/**
 * Thiết lập modal thông tin tuyển quân
 */
function setupRecruitmentModal() {
    if (!currentCampaignData || !currentStagesData) {
        console.warn('Không có dữ liệu Hoạt động để thiết lập modal');
        return;
    }
    
    // Thiết lập sự kiện cho nút đóng modal
    const closeButton = document.querySelector('.modal-close');
    const modalCloseButton = document.getElementById('modalCloseButton');
    const modalApplyButton = document.getElementById('modalApplyButton');
    const modal = document.getElementById('recruitmentInfoModal');
    
    if (!closeButton || !modalCloseButton || !modal) {
        console.warn('Không tìm thấy các phần tử modal cần thiết');
        return;
    }
    
    // Thiết lập sự kiện đóng modal
    const closeModal = () => {
        modal.classList.remove('show');
        document.body.style.overflow = '';
    };
    
    closeButton.addEventListener('click', closeModal);
    modalCloseButton.addEventListener('click', closeModal);
    
    // Thiết lập sự kiện cho nút nộp đơn
    modalApplyButton.addEventListener('click', function() {
        if (applicationFormUrl) {
            window.location.href = applicationFormUrl;
        }
    });
    
    // Ẩn/hiện và cập nhật nội dung nút "Nộp đơn tham gia" dựa vào trạng thái vòng đơn
    const applicationStage = currentStagesData.find(stage => stage.stageName === 'APPLICATION');
    if (applicationStage) {
        const now = new Date();
        const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        const appStartDay = new Date(
            new Date(applicationStage.startDate).getFullYear(), 
            new Date(applicationStage.startDate).getMonth(), 
            new Date(applicationStage.startDate).getDate()
        );
        const appEndDay = new Date(
            new Date(applicationStage.endDate).getFullYear(), 
            new Date(applicationStage.endDate).getMonth(), 
            new Date(applicationStage.endDate).getDate()
        );
        
        // Kiểm tra ngày hiện tại so với ngày bắt đầu và ngày kết thúc
        if (today >= appStartDay && today <= appEndDay && applicationFormUrl) {
            // Đang trong thời gian nộp đơn
            modalApplyButton.innerHTML = '<i class="fas fa-user-plus"></i> Nộp đơn tham gia';
            modalApplyButton.style.display = 'block';
        } else if (today < appStartDay) {
            // Chưa đến thời gian nộp đơn
            modalApplyButton.style.display = 'none';
        } else {
            // Đã hết thời gian nộp đơn
            modalApplyButton.style.display = 'none';
        }
    } else {
        modalApplyButton.style.display = 'none';
    }
    
    // Thiết lập đóng modal khi click bên ngoài
    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            closeModal();
        }
    });
}

/**
 * Hiển thị modal thông tin tuyển quân
 */
function showRecruitmentModal() {
    if (!currentCampaignData || !currentStagesData) {
        console.warn('Không có dữ liệu để hiển thị modal');
        return;
    }
    
    // Tìm các phần tử trong modal
    const modal = document.getElementById('recruitmentInfoModal');
    const campaignTitle = document.getElementById('modalCampaignTitle');
    const campaignGen = document.getElementById('modalCampaignGen');
    const campaignTime = document.getElementById('modalCampaignTime');
    const campaignDesc = document.getElementById('modalCampaignDescription');
    const stagesTimeline = document.getElementById('modalStagesTimeline');
    const currentStatus = document.getElementById('modalCurrentStatus');
    const modalApplyButton = document.getElementById('modalApplyButton');
    
    if (!modal || !campaignTitle || !campaignGen || !campaignTime || !campaignDesc || !stagesTimeline || !currentStatus) {
        console.warn('Không tìm thấy các phần tử modal cần thiết');
        return;
    }
    
    // Cập nhật thông tin Hoạt động
    campaignTitle.textContent = currentCampaignData.title || 'Không có tiêu đề';
    campaignGen.textContent = 'Gen: ' + (currentCampaignData.gen || 'N/A');
    
    // Format thời gian
    const startDate = new Date(currentCampaignData.startDate);
    const endDate = new Date(currentCampaignData.endDate);
    campaignTime.textContent = `${formatDate(startDate)} - ${formatDate(endDate)}`;
    
    // Cập nhật mô tả (nếu có)
    campaignDesc.textContent = currentCampaignData.description || 'Không có mô tả';
    
    // Sắp xếp các vòng theo ngày bắt đầu
    const sortedStages = [...currentStagesData].sort((a, b) => 
        new Date(a.startDate) - new Date(b.startDate)
    );
    
    // Cập nhật timeline các vòng tuyển
    stagesTimeline.innerHTML = '';
    const now = new Date();
    
    sortedStages.forEach(stage => {
        const stageStart = new Date(stage.startDate);
        const stageEnd = new Date(stage.endDate);
        
        // Xác định trạng thái của vòng
        let stageStatus;
        if (now < stageStart) {
            stageStatus = 'future';
        } else if (now > stageEnd) {
            stageStatus = 'past';
        } else {
            stageStatus = 'active';
        }
        
        // Tạo HTML cho vòng
        const stageElement = document.createElement('div');
        stageElement.className = `stage-item ${stageStatus}`;
        stageElement.innerHTML = `
            <div class="stage-dot"><i class="fas fa-${getStageIcon(stage.stageName)}"></i></div>
            <div class="stage-content">
                <div class="stage-name">${getStageDisplayName(stage.stageName)}</div>
                <div class="stage-time">${formatDate(stageStart)} - ${formatDate(stageEnd)}</div>
            </div>
        `;
        
        stagesTimeline.appendChild(stageElement);
    });
    
    // Cập nhật trạng thái hiện tại
    updateCurrentStatus(currentStatus);
    
    // Ẩn/hiện nút "Nộp đơn tham gia" dựa vào trạng thái vòng đơn hiện tại
    const applicationStage = currentStagesData.find(stage => stage.stageName === 'APPLICATION');
    if (applicationStage) {
        const now = new Date();
        const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        const appStartDay = new Date(
            new Date(applicationStage.startDate).getFullYear(), 
            new Date(applicationStage.startDate).getMonth(), 
            new Date(applicationStage.startDate).getDate()
        );
        const appEndDay = new Date(
            new Date(applicationStage.endDate).getFullYear(), 
            new Date(applicationStage.endDate).getMonth(), 
            new Date(applicationStage.endDate).getDate()
        );
        
        // Hiển thị nút nếu đang trong thời gian vòng đơn (kể cả ngày bắt đầu và kết thúc)
        if ((today >= appStartDay && today <= appEndDay) && applicationFormUrl) {
            modalApplyButton.style.display = 'inline-block';
            modalApplyButton.disabled = false;
        } else {
            modalApplyButton.style.display = 'none';
        }
    } else {
        modalApplyButton.style.display = 'none';
    }
    
    // Hiển thị modal
    modal.classList.add('show');
    document.body.style.overflow = 'hidden'; // Ngăn scroll trang khi modal hiển thị
}

/**
 * Cập nhật thông tin trạng thái hiện tại cho modal
 */
function updateCurrentStatus(statusElement) {
    if (!currentStagesData || !statusElement) return;
    
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate()); // Chỉ lấy ngày không lấy giờ
    const applicationStage = currentStagesData.find(stage => stage.stageName === 'APPLICATION');
    
    if (applicationStage) {
        const appStartDate = new Date(applicationStage.startDate);
        const appStartDay = new Date(appStartDate.getFullYear(), appStartDate.getMonth(), appStartDate.getDate());
        const appEndDate = new Date(applicationStage.endDate);
        const appEndDay = new Date(appEndDate.getFullYear(), appEndDate.getMonth(), appEndDate.getDate());
        
        if (today < appStartDay) {
            statusElement.innerHTML = `
                <i class="fas fa-hourglass-start"></i> Vòng nộp đơn sẽ bắt đầu vào ${formatDate(appStartDate)}.
                <br>Vui lòng quay lại sau để đăng ký!
            `;
            statusElement.style.borderLeftColor = '#f39c12';
        } else if (today <= appEndDay) {
            statusElement.innerHTML = `
                <i class="fas fa-check-circle"></i> Vòng nộp đơn đang diễn ra! 
                <br>Bạn có thể nộp đơn đăng ký từ nay đến ${formatDate(appEndDate)}.
            `;
            statusElement.style.borderLeftColor = '#4caf50';
        } else {
            // Vòng đơn đã kết thúc, kiểm tra vòng tiếp theo
            const nextStage = getNextActiveStage(currentStagesData);
            if (nextStage) {
                const nextStartDate = new Date(nextStage.startDate);
                statusElement.innerHTML = `
                    <i class="fas fa-hourglass-half"></i> Vòng nộp đơn đã kết thúc. 
                    <br>${getStageDisplayName(nextStage.stageName)} sẽ bắt đầu vào ${formatDate(nextStartDate)}.
                `;
                statusElement.style.borderLeftColor = '#3498db';
            } else {
                statusElement.innerHTML = `
                    <i class="fas fa-hourglass-end"></i> Hoạt động tuyển quân đã kết thúc.
                    <br>Vui lòng theo dõi thông báo từ câu lạc bộ.
                `;
                statusElement.style.borderLeftColor = '#e74c3c';
            }
        }
    } else {
        statusElement.innerHTML = `
            <i class="fas fa-exclamation-triangle"></i> Không tìm thấy thông tin vòng nộp đơn.
            <br>Vui lòng liên hệ câu lạc bộ để biết thêm chi tiết.
        `;
        statusElement.style.borderLeftColor = '#e74c3c';
    }
}

/**
 * Lấy icon tương ứng cho từng loại vòng
 */
function getStageIcon(stageName) {
    switch (stageName) {
        case 'APPLICATION':
            return 'file-alt';
        case 'INTERVIEW':
            return 'comments';
        case 'CHALLENGE':
            return 'tasks';
        default:
            return 'circle';
    }
}

/**
 * Lấy vòng tiếp theo đang hoặc sắp diễn ra
 * @param {Array} stages Danh sách các vòng
 * @returns {Object|null} Vòng tiếp theo hoặc null nếu không có
 */
function getNextActiveStage(stages) {
    if (!stages || !Array.isArray(stages) || stages.length === 0) return null;
    
    const now = new Date();
    const activeStages = stages.filter(stage => {
        const startDate = new Date(stage.startDate);
        return startDate >= now;
    });
    
    if (activeStages.length === 0) return null;
    
    // Sắp xếp theo ngày bắt đầu
    activeStages.sort((a, b) => new Date(a.startDate) - new Date(b.startDate));
    return activeStages[0];
}

/**
 * Định dạng ngày tháng thành chuỗi dễ đọc
 * @param {Date} date Đối tượng ngày tháng
 * @returns {String} Chuỗi ngày tháng định dạng
 */
function formatDate(date) {
    if (!date || !(date instanceof Date)) return '';
    
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    
    return `${day}/${month}/${year}`;
}

/**
 * Chuyển đổi tên vòng thành tên hiển thị
 * @param {String} stageName Tên vòng
 * @returns {String} Tên hiển thị
 */
function getStageDisplayName(stageName) {
    const stageNames = {
        'APPLICATION': 'Vòng đơn',
        'INTERVIEW': 'Vòng phỏng vấn',
        'CHALLENGE': 'Vòng thử thách',
        'RESULT': 'Công bố kết quả'
    };
    
    return stageNames[stageName] || 'Vòng tiếp theo';
}

/**
 * Kiểm tra xem hai ngày có cùng ngày không (không quan tâm giờ, phút, giây)
 * @param {Date} date1 Ngày thứ nhất
 * @param {Date} date2 Ngày thứ hai
 * @returns {boolean} true nếu cùng ngày, false nếu khác ngày
 */
function sameDay(date1, date2) {
    if (!date1 || !date2) return false;
    
    return date1.getFullYear() === date2.getFullYear() &&
           date1.getMonth() === date2.getMonth() &&
           date1.getDate() === date2.getDate();
}
