// Hàm debug logger
function debug(component, message, data = null) {
  const timestamp = new Date().toISOString().split('T')[1].split('.')[0]; // Lấy thời gian HH:MM:SS
  const prefix = `[${timestamp}][${component}]`;
  
  if (data) {
    console.log(prefix, message, data);
  } else {
    console.log(prefix, message);
  }
}

// Biến toàn cục
let contextPath;
let campaignData;

document.addEventListener("DOMContentLoaded", () => {
  // Lấy đường dẫn ngữ cảnh để xây dựng URL chính xác
  contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2)) || "";
  debug("INIT", "Đường dẫn ngữ cảnh:", contextPath);
  
  // Đảm bảo biến contextPath có sẵn toàn cục
  window.contextPath = contextPath;
  
  // Sử dụng dữ liệu chiến dịch từ biến toàn cục
  campaignData = window.campaignData;

  // Kiểm tra trạng thái modal lúc khởi tạo
  const modals = document.querySelectorAll('.modal');
  modals.forEach(modal => {
    debug("INIT", `Modal ${modal.id} trạng thái ban đầu:`, modal.style.display);
    if (modal.style.display !== 'none' && modal.style.display !== '') {
      debug("WARNING", `Modal ${modal.id} đang hiển thị khi trang tải!`);
      modal.style.display = 'none';  // Đảm bảo modal ẩn đi lúc khởi tạo
    }
  });

  // Khởi tạo trang
  debug("INIT", "Khởi tạo ứng dụng");
  initializeTabs();
  initializeChart();
  initializeEventHandlers();
  initializeModals();
  initializeNotificationPanel();

  // Tải dữ liệu ban đầu
  debug("INIT", "Tải dữ liệu ban đầu");
  loadCandidatesForActiveTab();
  loadNotificationTemplates();
});

// Chức năng Tab
function initializeTabs() {
  const tabButtons = document.querySelectorAll(".tab-btn");
  const tabContents = document.querySelectorAll(".tab-content");

  tabButtons.forEach((button) => {
    button.addEventListener("click", () => {
      const tabId = button.getAttribute("data-tab");

      // Xóa lớp active khỏi tất cả các tab và nội dung
      tabButtons.forEach((btn) => btn.classList.remove("active"));
      tabContents.forEach((content) => content.classList.remove("active"));

      // Thêm lớp active vào tab được nhấp và nội dung tương ứng
      button.classList.add("active");
      document.getElementById(tabId).classList.add("active");

      // Tải danh sách ứng viên cho tab đang hoạt động
      loadCandidatesForActiveTab();
    });
  });
}

// Biểu đồ Chart.js
function initializeChart() {
  const ctx = document.getElementById("stageProgressChart");
  if (!ctx || !window.campaignData) return;

  const stages = window.campaignData.stages;
  const labels = stages.map((stage) => stage.stageName);
  const totalData = stages.map((stage) => stage.stats.total);
  const pendingData = stages.map((stage) => stage.stats.pending);
  const approvedData = stages.map((stage) => stage.stats.approved);
  const rejectedData = stages.map((stage) => stage.stats.rejected);

  new Chart(ctx, {
    type: "bar",
    data: {
      labels: labels,
      datasets: [
        {
          label: "Tổng ứng viên",
          data: totalData,
          backgroundColor: "#3b82f6",
          borderColor: "#2563eb",
          borderWidth: 1,
        },
        {
          label: "Chờ xử lý",
          data: pendingData,
          backgroundColor: "#f59e0b",
          borderColor: "#d97706",
          borderWidth: 1,
        },
        {
          label: "Đã duyệt",
          data: approvedData,
          backgroundColor: "#10b981",
          borderColor: "#059669",
          borderWidth: 1,
        },
        {
          label: "Từ chối",
          data: rejectedData,
          backgroundColor: "#ef4444",
          borderColor: "#dc2626",
          borderWidth: 1,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: {
          grid: {
            display: false,
          },
        },
        y: {
          beginAtZero: true,
          precision: 0,
        },
      },
      plugins: {
        legend: {
          position: "top",
        },
        tooltip: {
          callbacks: {
            title: function (context) {
              return context[0].label;
            },
          },
        },
      },
    },
  });
}

function initializeModals() {
  debug("INIT", "Khởi tạo các modal và trình xử lý sự kiện");
  
  // Modal thông báo
  const notificationModal = document.getElementById('notificationModal');
  const candidateModal = document.getElementById('candidateModal');
  const confirmationModal = document.getElementById('confirmationModal');
  
  // Xử lý đóng modal
  document.querySelectorAll('.close, .cancel-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      if (notificationModal) notificationModal.style.display = 'none';
      if (candidateModal) candidateModal.style.display = 'none';
      if (confirmationModal) confirmationModal.style.display = 'none';
    });
  });
  
  // Xử lý click bên ngoài modal
  window.addEventListener('click', (e) => {
    if (e.target === notificationModal) notificationModal.style.display = 'none';
    if (e.target === candidateModal) candidateModal.style.display = 'none';
    if (e.target === confirmationModal) confirmationModal.style.display = 'none';
  });
  
  // Xử lý checkbox lưu mẫu thông báo
  const saveAsTemplateCheckbox = document.getElementById('saveAsTemplate');
  const templateNameGroup = document.querySelector('.template-name-group');
  
  if (saveAsTemplateCheckbox && templateNameGroup) {
    saveAsTemplateCheckbox.addEventListener('change', () => {
      templateNameGroup.style.display = saveAsTemplateCheckbox.checked ? 'block' : 'none';
      debug("EVENT", "Thay đổi checkbox lưu mẫu", {checked: saveAsTemplateCheckbox.checked});
    });
  }
  
  // Xử lý xem trước nội dung
  const titleInput = document.getElementById('notificationTitle');
  const contentInput = document.getElementById('notificationContent');
  
  // Xử lý chọn mẫu thông báo
  const templateSelector = document.getElementById('templateSelector');
  if (templateSelector) {
    templateSelector.addEventListener('change', () => {
      const selectedTemplateId = templateSelector.value;
      if (selectedTemplateId) {
        loadTemplateContent(selectedTemplateId);
      }
    });
  }
}

function initializeEventHandlers() {
  debug("INIT", "Khởi tạo trình xử lý sự kiện");
  
  // Nút xem ứng viên
  document.addEventListener("click", (e) => {
    if (e.target.closest(".view-candidates")) {
      const button = e.target.closest(".view-candidates");
      const stageId = button.getAttribute("data-stage-id");
      const stageName = button.getAttribute("data-stage-name");
      debug("EVENT", "Click nút xem ứng viên", {stageId, stageName});
      showCandidatesModal(stageId, stageName);
    }
  });

  // Nút gửi thông báo
  document.addEventListener('click', (e) => {
    if (e.target.closest(".show-notification-modal-btn")) {
      const button = e.target.closest(".show-notification-modal-btn");
      const stageId = button.getAttribute("data-stage-id");
      const stageName = button.getAttribute("data-stage-name");
      debug("EVENT", "Click nút gửi thông báo", {stageId, stageName});
      
      openNotificationModal(stageId, stageName);
    }
  });
  
  // Bộ lọc trạng thái ứng viên
  document.addEventListener('click', (e) => {
    if (e.target.closest('.filter-btn')) {
      const button = e.target.closest('.filter-btn');
      const status = button.getAttribute('data-filter');
      
      // Xóa active từ tất cả các nút lọc
      document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
      });
      
      // Thêm active cho nút được click
      button.classList.add('active');
      
      // Áp dụng bộ lọc
      filterRecipients(status);
    }
  });
}

// Khởi tạo trình xử lý panel thông báo
function initializeNotificationPanel() {
  debug("INIT", "Khởi tạo panel thông báo");
  
  // Chọn tất cả người nhận
  const selectAllCheckbox = document.getElementById('selectAllRecipients');
  if (selectAllCheckbox) {
    selectAllCheckbox.addEventListener('change', function() {
      toggleAllRecipients(this.checked);
    });
  }
  
  // Nút gửi thông báo
  const sendBtn = document.getElementById('sendNotificationBtn');
  if (sendBtn) {
    sendBtn.addEventListener('click', sendBulkNotification);
  }
  
  // Cập nhật số lượng đã chọn khi thay đổi checkbox
  document.addEventListener('change', function(e) {
    if (e.target.classList.contains('recipient-checkbox')) {
      updateSelectedCount();
    }
  });
}

// Lọc người nhận theo trạng thái
function filterRecipients(status) {
  debug("UI", "Lọc người nhận theo trạng thái", {status});
  
  const recipientItems = document.querySelectorAll('.recipient-item');
  if (!recipientItems || recipientItems.length === 0) return;
  
  recipientItems.forEach(item => {
    const recipientStatus = item.getAttribute('data-status') || '';
    
    if (!status || status === '' || recipientStatus.toLowerCase() === status.toLowerCase()) {
      item.style.display = 'flex';
    } else {
      item.style.display = 'none';
    }
  });
  
  // Cập nhật số lượng được hiển thị
  updateSelectedCount();
}

// Chọn/bỏ chọn tất cả người nhận đang hiển thị
function toggleAllRecipients(checked) {
  const visibleCheckboxes = document.querySelectorAll('.recipient-item:not([style*="display: none"]) .recipient-checkbox');
  visibleCheckboxes.forEach(checkbox => {
    checkbox.checked = checked;
  });
  updateSelectedCount();
}

// Cập nhật số lượng đã chọn
function updateSelectedCount() {
  const selectedCheckboxes = document.querySelectorAll('.recipient-checkbox:checked');
  const count = selectedCheckboxes.length;
  const countSpan = document.getElementById('selectedCount');
  const sendBtn = document.getElementById('sendNotificationBtn');
  
  if (countSpan) countSpan.textContent = count;
  if (sendBtn) sendBtn.disabled = count === 0;
}

// Gửi thông báo hàng loạt
function sendBulkNotification() {
  const selectedRecipients = Array.from(document.querySelectorAll('.recipient-checkbox:checked'));
  const title = document.getElementById('notificationTitle').value;
  const content = document.getElementById('notificationContent').value;
  const saveAsTemplate = document.getElementById('saveAsTemplate').checked;
  const templateName = document.getElementById('templateName').value;
  
  if (!title || !content) {
    showToast('Vui lòng nhập đầy đủ tiêu đề và nội dung thông báo', 'error');
    return;
  }
  
  if (selectedRecipients.length === 0) {
    showToast('Vui lòng chọn ít nhất một ứng viên', 'error');
    return;
  }
  
  // Hiển thị đang tải
  const sendBtn = document.getElementById('sendNotificationBtn');
  const originalText = sendBtn.innerHTML;
  sendBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang gửi...';
  sendBtn.disabled = true;
  
  // Chuẩn bị dữ liệu thông báo
  const notificationData = {
    title: title,
    content: content,
    saveAsTemplate: saveAsTemplate,
    templateName: templateName,
    recipients: selectedRecipients.map(checkbox => ({
      applicationId: checkbox.dataset.applicationId,
      userId: checkbox.dataset.userId,
      stageId: checkbox.dataset.stageId
    }))
  };
  
  // Gửi thông báo
  fetch(`${contextPath}/api/sendBulkNotification`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(notificationData)
  })
  .then(response => response.json())
  .then(data => {
    if (data.success) {
      showToast(`Đã gửi thông báo thành công tới ${data.sentCount} ứng viên`, 'success');
      // Đặt lại form
      document.getElementById('notificationTitle').value = '';
      document.getElementById('notificationContent').value = '';
      document.getElementById('saveAsTemplate').checked = false;
      document.getElementById('templateName').value = '';
      document.querySelector('.template-name-group').style.display = 'none';
      toggleAllRecipients(false);
      
      // Đóng modal
      const notificationModal = document.getElementById('notificationModal');
      if (notificationModal) {
        notificationModal.style.display = 'none';
      }
    } else {
      showToast('Lỗi: ' + data.message, 'error');
    }
  })
  .catch(error => {
    console.error('Error sending notification:', error);
    showToast('Có lỗi xảy ra khi gửi thông báo', 'error');
  })
  .finally(() => {
    sendBtn.innerHTML = originalText;
    sendBtn.disabled = false;
  });
}

// Tải danh sách người nhận cho panel thông báo
function loadRecipientsForNotification(stageId, stageType) {
  const recipientsList = document.getElementById('recipientsList');
  if (!recipientsList) return;
  
  // Hiển thị đang tải
  recipientsList.innerHTML = '<div class="loading"><i class="fas fa-spinner fa-spin"></i> Đang tải danh sách ứng viên...</div>';
  
  // Đảm bảo stageType được gửi đúng định dạng (không lowercase, giữ nguyên tên gốc)
  const stageTypeParam = stageType.toString();
  debug("API", `Gọi API stageCandidates với campaignId=${window.campaignData.campaignId}, stageType=${stageTypeParam}`);
  
  // Lấy danh sách ứng viên cho vòng này
  fetch(`${contextPath}/api/stageCandidates?campaignId=${window.campaignData.campaignId}&stageType=${stageTypeParam}`)
    .then(response => response.json())
    .then(candidates => {
      debug("DATA", "Nhận danh sách ứng viên", candidates);
      
      // Kiểm tra nếu có lỗi từ API
      if (candidates && candidates.success === false) {
        recipientsList.innerHTML = `<div class="no-recipients"><p>Lỗi: ${candidates.message || 'Không thể tải danh sách ứng viên'}</p></div>`;
        return;
      }
      
      if (!candidates || candidates.length === 0) {
        recipientsList.innerHTML = '<div class="no-recipients"><p>Không có ứng viên nào trong vòng này</p></div>';
        return;
      }
      
      // Lọc ứng viên theo trạng thái nếu cần
      // Đảm bảo candidates là một mảng trước khi dùng filter
      const candidatesArray = Array.isArray(candidates) ? candidates : Object.values(candidates);
      const filteredCandidates = candidatesArray;
      
      try {
        // Tạo HTML cho danh sách người nhận
        const recipientsHTML = filteredCandidates.map(candidate => {
          // Kiểm tra candidate và candidate.status tồn tại
          if (!candidate) {
            return '';
          }
          
          // Xác định trạng thái và kiểm tra giá trị hợp lệ
          const status = candidate.status || 'PENDING';
          const statusLower = status.toLowerCase();
          const statusText = status === 'APPROVED' ? 'Đã duyệt' : 
                            status === 'REJECTED' ? 'Từ chối' : 'Chờ xử lý';
          
          return `
        <div class="recipient-item" data-status="${statusLower}">
          <div class="recipient-info">
            <input type="checkbox" class="recipient-checkbox" 
                   data-application-id="${candidate.applicationId || ''}"
                   data-user-id="${candidate.userId || ''}"
                   data-stage-id="${stageId}">
            <div>
              <div class="recipient-name">${candidate.userName || 'Không có tên'}</div>
              <div class="recipient-email">${candidate.email || 'N/A'}</div>
            </div>
          </div>
          <div class="recipient-status">
            <span class="recipient-status-badge ${statusLower}">
              ${statusText}
            </span>
          </div>
          <div class="recipient-action">
            <button class="btn btn-outline btn-sm" onclick="viewCandidateDetail(${candidate.applicationId || 0})">
              <i class="fas fa-eye"></i> Xem
            </button>
          </div>
        </div>
        `;
        }).join('');
        
        recipientsList.innerHTML = recipientsHTML;
      } catch (error) {
        console.error('Lỗi khi tạo HTML cho danh sách người nhận:', error);
        recipientsList.innerHTML = '<div class="error-state"><p>Có lỗi xảy ra khi tải danh sách ứng viên</p></div>';
        return;
      }
      
      recipientsList.innerHTML = recipientsHTML;
      
      // Đặt lại số lượng đã chọn
      updateSelectedCount();
    })
    .catch(error => {
      console.error('Lỗi khi tải danh sách người nhận:', error);
      recipientsList.innerHTML = '<div class="no-recipients"><p>Có lỗi xảy ra khi tải danh sách ứng viên</p></div>';
    });
}

// Xem chi tiết vòng và tải danh sách người nhận
function viewStageDetails(stageId, stageName) {
  // Cuộn đến panel thông báo
  const notificationPanel = document.querySelector('.notification-management-panel');
  if (notificationPanel) {
    notificationPanel.scrollIntoView({ behavior: 'smooth' });
  }
  
  // Tải danh sách người nhận cho vòng này
  loadRecipientsForNotification(stageId, stageName.toLowerCase());
  
  // Cập nhật placeholder cho tiêu đề thông báo
  const titleInput = document.getElementById('notificationTitle');
  const contentInput = document.getElementById('notificationContent');
  
  if (titleInput && contentInput) {
    const stageNameVi = getStageNameInVietnamese(stageName);
    titleInput.placeholder = `Thông báo về ${stageNameVi}`;
    contentInput.placeholder = `Xin chào {Tên ứng viên},\n\nChúng tôi xin thông báo về kết quả ${stageNameVi}...\n\nTrân trọng,\nBan tổ chức`;
  }
}

function loadCandidatesForActiveTab() {
  const activeTab = document.querySelector('.tab-btn.active');
  if (!activeTab) return;
  
  const tabId = activeTab.getAttribute('data-tab');
  
  if (tabId === 'overviewTab') {
    // Không cần làm gì trong tab tổng quan vì dữ liệu đã được tải
  } else if (tabId === 'candidatesTab') {
    // Tải danh sách ứng viên cho tab ứng viên
    loadAllCandidates();
  } else if (tabId === 'notificationsTab') {
    // Tải lịch sử thông báo cho tab thông báo
    loadNotificationHistory();
  }
}

function loadAllCandidates() {
  const candidatesList = document.getElementById('candidatesList');
  if (!candidatesList) return;
  
  candidatesList.innerHTML = '<div class="loading"><i class="fas fa-spinner fa-spin"></i> Đang tải danh sách ứng viên...</div>';
  
  fetch(`${contextPath}/api/campaignCandidates?campaignId=${window.campaignData.campaignId}`)
    .then(response => response.json())
    .then(candidates => {
      debug("DATA", "Tải tất cả ứng viên", candidates);
      
      if (!candidates || candidates.length === 0) {
        candidatesList.innerHTML = '<div class="empty-state"><p>Chưa có ứng viên nào đăng ký</p></div>';
        return;
      }
      
      const candidatesHTML = candidates.map(candidate => `
        <div class="candidate-card">
          <div class="candidate-header">
            <h3>${candidate.userName || 'Không có tên'}</h3>
            <span class="badge ${candidate.currentStage.status.toLowerCase()}">
              ${getStatusText(candidate.currentStage.status)}
            </span>
          </div>
          <div class="candidate-body">
            <div class="candidate-info">
              <div><i class="fas fa-envelope"></i> ${candidate.email || 'N/A'}</div>
              <div><i class="fas fa-phone"></i> ${candidate.phone || 'N/A'}</div>
            </div>
            <div class="candidate-status">
              <div>Vòng hiện tại: <strong>${getStageNameInVietnamese(candidate.currentStage.stageName)}</strong></div>
              <div>Ngày đăng ký: <strong>${formatDate(candidate.applicationDate)}</strong></div>
            </div>
          </div>
          <div class="candidate-footer">
            <button class="btn btn-primary btn-sm" onclick="viewCandidateDetail(${candidate.applicationId})">
              <i class="fas fa-eye"></i> Xem chi tiết
            </button>
          </div>
        </div>
      `).join('');
      
      candidatesList.innerHTML = candidatesHTML;
    })
    .catch(error => {
      console.error('Lỗi khi tải danh sách ứng viên:', error);
      candidatesList.innerHTML = '<div class="error-state"><p>Có lỗi xảy ra khi tải danh sách ứng viên</p></div>';
    });
}

function loadNotificationHistory() {
  const notificationList = document.getElementById('notificationHistory');
  if (!notificationList) return;
  
  notificationList.innerHTML = '<div class="loading"><i class="fas fa-spinner fa-spin"></i> Đang tải lịch sử thông báo...</div>';
  
  fetch(`${contextPath}/api/notificationHistory?campaignId=${window.campaignData.campaignId}`)
    .then(response => response.json())
    .then(notifications => {
      debug("DATA", "Tải lịch sử thông báo", notifications);
      
      if (!notifications || notifications.length === 0) {
        notificationList.innerHTML = '<div class="empty-state"><p>Chưa có thông báo nào được gửi</p></div>';
        return;
      }
      
      const notificationsHTML = notifications.map(notification => `
        <div class="notification-item">
          <div class="notification-header">
            <h3>${notification.title}</h3>
            <span class="notification-date">${formatDate(notification.sentDate)}</span>
          </div>
          <div class="notification-body">
            <p>${notification.content.substring(0, 100)}${notification.content.length > 100 ? '...' : ''}</p>
          </div>
          <div class="notification-footer">
            <span class="notification-recipients">Đã gửi tới ${notification.recipientCount} người</span>
            <button class="btn btn-outline btn-sm" onclick="viewNotificationDetail(${notification.id})">
              <i class="fas fa-eye"></i> Xem chi tiết
            </button>
          </div>
        </div>
      `).join('');
      
      notificationList.innerHTML = notificationsHTML;
    })
    .catch(error => {
      console.error('Lỗi khi tải lịch sử thông báo:', error);
      notificationList.innerHTML = '<div class="error-state"><p>Có lỗi xảy ra khi tải lịch sử thông báo</p></div>';
    });
}

function loadNotificationTemplates() {
  const templateSelector = document.getElementById('templateSelector');
  if (!templateSelector) return;
  
  fetch(`${contextPath}/api/notificationTemplates?clubId=${window.campaignData.clubId}`)
    .then(response => response.json())
    .then(templates => {
      debug("DATA", "Tải mẫu thông báo", templates);
      
      if (!templates || templates.length === 0) {
        const option = document.createElement('option');
        option.value = '';
        option.textContent = 'Không có mẫu thông báo';
        templateSelector.appendChild(option);
        return;
      }
      
      // Thêm tùy chọn mặc định
      const defaultOption = document.createElement('option');
      defaultOption.value = '';
      defaultOption.textContent = 'Chọn mẫu thông báo';
      templateSelector.appendChild(defaultOption);
      
      // Thêm các mẫu thông báo
      templates.forEach(template => {
        const option = document.createElement('option');
        option.value = template.id;
        option.textContent = template.name;
        templateSelector.appendChild(option);
      });
    })
    .catch(error => {
      console.error('Lỗi khi tải mẫu thông báo:', error);
      const option = document.createElement('option');
      option.value = '';
      option.textContent = 'Lỗi tải mẫu thông báo';
      templateSelector.appendChild(option);
    });
}

function loadTemplateContent(templateId) {
  fetch(`${contextPath}/api/notificationTemplate?templateId=${templateId}`)
    .then(response => response.json())
    .then(template => {
      debug("DATA", "Tải nội dung mẫu thông báo", template);
      
      const titleInput = document.getElementById('notificationTitle');
      const contentInput = document.getElementById('notificationContent');
      
      if (titleInput && contentInput && template) {
        titleInput.value = template.title;
        contentInput.value = template.content;
      }
    })
    .catch(error => {
      console.error('Lỗi khi tải nội dung mẫu thông báo:', error);
      showToast('Có lỗi xảy ra khi tải nội dung mẫu thông báo', 'error');
    });
}

function showCandidatesModal(stageId, stageName) {
  // Tải danh sách ứng viên cho modal
  const candidatesList = document.getElementById('modalCandidatesList');
  const candidateModal = document.getElementById('candidateModal');
  
  if (!candidatesList || !candidateModal) return;
  
  // Cập nhật tiêu đề modal
  const modalTitle = candidateModal.querySelector('.modal-title');
  if (modalTitle) {
    modalTitle.textContent = `Danh sách ứng viên - ${getStageNameInVietnamese(stageName)}`;
  }
  
  // Hiển thị đang tải
  candidatesList.innerHTML = '<div class="loading"><i class="fas fa-spinner fa-spin"></i> Đang tải danh sách ứng viên...</div>';
  
  // Hiển thị modal
  candidateModal.style.display = 'flex';
  
  // Tải danh sách ứng viên
  fetch(`${contextPath}/api/stageCandidates?campaignId=${window.campaignData.campaignId}&stageType=${stageName.toLowerCase()}`)
    .then(response => response.json())
    .then(candidates => {
      debug("DATA", "Tải ứng viên cho modal", candidates);
      
      if (!candidates || candidates.length === 0) {
        candidatesList.innerHTML = '<div class="empty-state"><p>Không có ứng viên nào trong vòng này</p></div>';
        return;
      }
      
      const candidatesHTML = candidates.map(candidate => `
        <div class="modal-candidate-item">
          <div class="modal-candidate-info">
            <div class="modal-candidate-name">${candidate.userName || 'Không có tên'}</div>
            <div class="modal-candidate-email">${candidate.email || 'N/A'}</div>
            <div class="modal-candidate-date">Đăng ký: ${formatDate(candidate.applicationDate)}</div>
          </div>
          <div class="modal-candidate-status">
            <span class="badge ${candidate.status.toLowerCase()}">
              ${getStatusText(candidate.status)}
            </span>
          </div>
          <div class="modal-candidate-actions">
            <button class="btn btn-primary btn-sm" onclick="viewCandidateDetail(${candidate.applicationId})">
              <i class="fas fa-eye"></i> Chi tiết
            </button>
          </div>
        </div>
      `).join('');
      
      candidatesList.innerHTML = candidatesHTML;
    })
    .catch(error => {
      console.error('Lỗi khi tải danh sách ứng viên cho modal:', error);
      candidatesList.innerHTML = '<div class="error-state"><p>Có lỗi xảy ra khi tải danh sách ứng viên</p></div>';
    });
}

function openNotificationModal(stageId, stageName) {
  debug("FUNCTION", "Mở modal thông báo", {stageId, stageName});
  
  const notificationModal = document.getElementById('notificationModal');
  if (!notificationModal) {
    debug("ERROR", "Không tìm thấy modal thông báo");
    return;
  }
  
  // Cập nhật tiêu đề modal
  const modalTitle = notificationModal.querySelector('.modal-header h3');
  if (modalTitle) {
    // Kiểm tra stageName có tồn tại không trước khi chuyển đổi
    let displayName = "Gửi thông báo";
    if (stageName) {
      displayName += ` - ${getStageNameInVietnamese(stageName)}`;
    }
    modalTitle.textContent = displayName;
  }
  
  // Lưu stageId và stageName vào modal để sử dụng sau này
  notificationModal.dataset.stageId = stageId || '';
  notificationModal.dataset.stageName = stageName || '';
  
  // Reset form
  const form = document.getElementById('notificationForm');
  if (form) form.reset();
  
  // Hiển thị modal
  notificationModal.style.display = 'flex';
  
  // Tải danh sách người nhận
  if (stageId && stageName) {
    loadRecipientsForNotification(stageId, stageName);
  } else {
    const recipientsList = document.getElementById('recipientsList');
    if (recipientsList) {
      recipientsList.innerHTML = '<div class="no-recipients"><p>Không thể tải danh sách người nhận. Thông tin vòng không hợp lệ.</p></div>';
    }
  }
}

// Xem chi tiết đơn đăng ký
function viewCandidateDetail(applicationId) {
  if (!applicationId) {
    showToast('Không tìm thấy thông tin đơn đăng ký', 'error');
    return;
  }
  
  debug("FUNCTION", "Xem chi tiết đơn đăng ký", {applicationId});
  
  // Mở trang chi tiết đơn đăng ký trong tab mới
  window.open(`${contextPath}/application?id=${applicationId}`, '_blank');
}

// Xem chi tiết thông báo
function viewNotificationDetail(notificationId) {
  if (!notificationId) {
    showToast('Không tìm thấy thông tin thông báo', 'error');
    return;
  }
  
  debug("FUNCTION", "Xem chi tiết thông báo", {notificationId});
  
  // Mở trang chi tiết thông báo trong tab mới hoặc hiện modal
  // Tùy thuộc vào thiết kế của ứng dụng
  window.open(`${contextPath}/notification?id=${notificationId}`, '_blank');
}

function viewFormResponses(stageType) {
  const campaignId = window.campaignData?.campaignId;
  const templateId = window.campaignData?.templateId;
  const clubId = window.campaignData?.clubId;
  
  if (!templateId || !clubId) {
    showToast('Không tìm thấy thông tin form', 'error');
    return;
  }
  
  const url = `${contextPath}/formResponses?templateId=${templateId}&clubId=${clubId}&formType=member&stageType=${stageType}`;
  window.open(url, '_blank');
}

// Các hàm trợ giúp
function getStageNameInVietnamese(stageName) {
  switch (stageName) {
    case 'APPLICATION': return 'Vòng nộp đơn';
    case 'INTERVIEW': return 'Vòng phỏng vấn';
    case 'CHALLENGE': return 'Vòng thử thách';
    default: return stageName;
  }
}

function getStatusText(status) {
  switch (status) {
    case 'PENDING': return 'Chờ xử lý';
    case 'APPROVED': return 'Đã duyệt';
    case 'REJECTED': return 'Từ chối';
    default: return status;
  }
}

function formatDate(dateString) {
  if (!dateString) return '-';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN');
}

function showToast(message, type = 'info') {
  debug("UI", "Hiển thị toast", {message, type});
  
  // Tạo phần tử toast
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `
    <i class="fas fa-${type === 'success' ? 'check' : type === 'error' ? 'exclamation' : 'info'}-circle"></i>
    <span>${message}</span>
    <button class="toast-close">&times;</button>
  `;
  
  // Thêm vào container
  const container = document.getElementById('toastContainer');
  if (!container) {
    debug("ERROR", "Không tìm thấy toast container");
    console.error("Không tìm thấy toast container");
    return;
  }
  
  // Thêm nút đóng
  const closeBtn = toast.querySelector('.toast-close');
  if (closeBtn) {
    closeBtn.addEventListener('click', function() {
      container.removeChild(toast);
    });
  }
  
  container.appendChild(toast);
  
  // Hiển thị toast
  setTimeout(() => {
    toast.classList.add('show');
    debug("UI", "Toast hiển thị");
  }, 100);
  
  // Ẩn và xóa toast
  setTimeout(() => {
    toast.classList.remove('show');
    debug("UI", "Toast ẩn");
    
    setTimeout(() => {
      // Check if toast still exists before removing
      if (toast.parentNode === container) {
        container.removeChild(toast);
        debug("UI", "Toast đã xóa");
      }
    }, 300);
  }, 3000);
}

// Công khai hàm để sử dụng toàn cục
window.viewStageDetails = viewStageDetails;
window.viewFormResponses = viewFormResponses;
window.filterRecipients = filterRecipients;
window.viewCandidateDetail = viewCandidateDetail;
window.viewNotificationDetail = viewNotificationDetail;
