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
            // Lấy campaign đang diễn ra và các vòng tuyển của nó
            const currentCampaign = activeCampaigns[0];
            
            fetch(`${contextPath}/recruitment/stages?recruitmentId=${currentCampaign.recruitmentId}`, {
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
                    
                    console.log('DEBUG DATE COMPARISON:', {
                        nowDateOnly: nowDateOnly.toLocaleDateString(),
                        startDateOnly: startDateOnly.toLocaleDateString(),
                        endDateOnly: endDateOnly.toLocaleDateString(),
                        isAfterOrEqualStart: nowDateOnly >= startDateOnly,
                        isBeforeOrEqualEnd: nowDateOnly <= endDateOnly,
                        nowTime: now.getTime(),
                        startTime: startDateOnly.getTime(),
                        endTime: endDateOnly.getTime()
                    });
                    
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
                    
                    if (isToday || isAfterStartDay) {
                        if (isBeforeEndDay || isEndDay) {
                            // Vòng đơn đang diễn ra
                            console.log('DEBUG: Vòng đơn đang diễn ra - Ngày nằm trong khoảng');
                            joinButton.href = `${contextPath}/applicationForm?templateId=${currentCampaign.templateId}`;
                            joinButton.innerHTML = '<i class="fas fa-user-plus"></i> Tham gia câu lạc bộ';
                            joinButton.classList.remove('disabled', 'btn-secondary');
                            joinButton.classList.add('btn-primary');
                    } else if (nowDateOnly < startDateOnly) {
                        // Vòng đơn chưa bắt đầu
                        console.log('DEBUG: Vòng đơn chưa bắt đầu');
                        joinButton.href = '#';
                        joinButton.innerHTML = `<i class="fas fa-hourglass-start"></i> Vòng đơn bắt đầu vào ${formatDate(appStartDate)}`;
                        joinButton.classList.add('disabled', 'btn-secondary');
                        joinButton.classList.remove('btn-primary');
                    } else {
                        // Vòng đơn đã kết thúc
                        console.log('DEBUG: Vòng đơn đã kết thúc (đã qua ngày kết thúc)');
                        // Tìm vòng tiếp theo
                        const nextStage = getNextActiveStage(stageData.stages);
                        if (nextStage) {
                            joinButton.href = '#';
                            joinButton.innerHTML = `<i class="fas fa-hourglass-half"></i> ${getStageDisplayName(nextStage.stageName)} bắt đầu vào ${formatDate(new Date(nextStage.startDate))}`;
                            joinButton.classList.add('disabled', 'btn-secondary');
                            joinButton.classList.remove('btn-primary');
                        } else {
                            // Không có vòng nào tiếp theo
                            joinButton.href = '#';
                            joinButton.innerHTML = '<i class="fas fa-hourglass-end"></i> Vòng tuyển quân đã kết thúc';
                            joinButton.classList.add('disabled', 'btn-secondary');
                            joinButton.classList.remove('btn-primary');
                        }
                    }
                    } else {
                        // Vòng đơn đã kết thúc
                        const nextStage = getNextActiveStage(stageData.stages);
                        if (nextStage) {
                            joinButton.href = '#';
                            joinButton.innerHTML = `<i class="fas fa-hourglass-half"></i> ${getStageDisplayName(nextStage.stageName)} bắt đầu vào ${formatDate(new Date(nextStage.startDate))}`;
                            joinButton.classList.add('disabled', 'btn-secondary');
                            joinButton.classList.remove('btn-primary');
                        } else {
                            // Không có vòng nào tiếp theo
                            joinButton.href = '#';
                            joinButton.innerHTML = '<i class="fas fa-hourglass-end"></i> Vòng tuyển quân đã kết thúc';
                            joinButton.classList.add('disabled', 'btn-secondary');
                            joinButton.classList.remove('btn-primary');
                        }
                    }
                } else {
                    // Không có vòng APPLICATION
                    joinButton.href = '#';
                    joinButton.innerHTML = '<i class="fas fa-info-circle"></i> Không có thông tin vòng đơn';
                    joinButton.classList.add('disabled', 'btn-secondary');
                    joinButton.classList.remove('btn-primary');
                }
            })
            .catch(error => {
                console.error('Lỗi khi lấy thông tin vòng tuyển:', error);
            });
        } else {
            // Không có hoạt động tuyển quân nào đang diễn ra
            joinButton.href = '#';
            joinButton.innerHTML = '<i class="fas fa-times-circle"></i> Câu lạc bộ không tuyển thành viên';
            joinButton.classList.add('disabled', 'btn-secondary');
            joinButton.classList.remove('btn-primary');
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
