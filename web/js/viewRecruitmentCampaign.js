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

  
  // Đảm bảo biến contextPath có sẵn toàn cục
  window.contextPath = contextPath;
  
  // Sử dụng dữ liệu từ biến toàn cục
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
  initializeTabs();
  initializeChart();
  initializeEventHandlers();
  initializeModals();
  initializeNotificationPanel();

  // Tải dữ liệu ban đầu
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
  debug("CHART", "Initializing chart");
  const ctx = document.getElementById("stageProgressChart");
  if (!ctx) {
    debug("CHART", "Chart canvas not found");
    return;
  }
  if (!window.campaignData) {
    debug("CHART", "Campaign data not available");
    return;
  }

  // Debug campaign data
  debug("CHART", "Campaign data:", window.campaignData);

  const stages = window.campaignData.stages;
  debug("CHART", "Stages data for chart:", stages);

  // Map data for chart
  const labels = stages.map((stage) => {
    // Chuyển đổi tên stage sang tiếng Việt cho chart
    switch(stage.stageName) {
      case 'APPLICATION': return 'Vòng nộp đơn';
      case 'INTERVIEW': return 'Vòng phỏng vấn';
      case 'CHALLENGE': return 'Vòng thử thách';
      default: return stage.stageName;
    }
  });
  
  // Debug data points
  const totalData = stages.map((stage) => {
    debug("CHART", `Stage ${stage.stageName} total:`, stage.stats.total);
    return stage.stats.total || 0;
  });
  const pendingData = stages.map((stage) => stage.stats.pending || 0);
  const approvedData = stages.map((stage) => stage.stats.approved || 0);
  const rejectedData = stages.map((stage) => stage.stats.rejected || 0);
  
  debug("CHART", "Chart data points:", {
    labels,
    totalData,
    pendingData, 
    approvedData,
    rejectedData
  });

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
      if (notificationModal) {
        notificationModal.style.display = 'none';
        // Ẩn thông báo lỗi khi đóng modal
        const errorContainer = document.getElementById('notificationFormError');
        if (errorContainer) {
          errorContainer.textContent = '';
          errorContainer.style.display = 'none';
        }
      }
      if (candidateModal) candidateModal.style.display = 'none';
      if (confirmationModal) confirmationModal.style.display = 'none';
    });
  });
  
  // Xử lý nút xác nhận gửi trong modal xác nhận
  const confirmSendBtn = document.getElementById('confirmSendBtn');
  if (confirmSendBtn) {
    confirmSendBtn.addEventListener('click', () => {
      // Ẩn modal xác nhận
      if (confirmationModal) confirmationModal.style.display = 'none';
      // Gọi hàm gửi thông báo
      sendBulkNotification();
    });
  }
  
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
    });
  }
  
  // Xử lý xem trước nội dung
  const titleInput = document.getElementById('notificationTitle');
  const contentInput = document.getElementById('notificationContent');
  
  if (titleInput && contentInput) {
    // Cập nhật xem trước khi nhập
    titleInput.addEventListener('input', updatePreview);
    contentInput.addEventListener('input', updatePreview);
    
    // Hàm cập nhật xem trước
    function updatePreview() {
      const previewTitle = document.querySelector('#notificationPreview h5');
      const previewContent = document.querySelector('#notificationPreview p');
      
      if (previewTitle && previewContent) {
        previewTitle.textContent = titleInput.value || 'Tiêu đề thông báo sẽ hiển thị ở đây';
        previewContent.textContent = contentInput.value || 'Nội dung thông báo sẽ hiển thị ở đây...';
      }
    }
  }
  
  // Xử lý chọn mẫu thông báo
  const templateSelector = document.getElementById('templateSelector');
  if (templateSelector) {
    templateSelector.addEventListener('change', () => {
      const selectedTemplateId = templateSelector.value;
      if (selectedTemplateId) {
        loadTemplateContent(selectedTemplateId);
        
        // Ẩn thông báo lỗi khi chọn mẫu thông báo
        const errorContainer = document.getElementById('notificationFormError');
        if (errorContainer) {
          errorContainer.textContent = '';
          errorContainer.style.display = 'none';
        }
      }
    });
  }
}

function initializeEventHandlers() {
  
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
  
  // Nút gửi thông báo - xóa onclick handler vì đã có xử lý trong HTML
  const sendBtn = document.getElementById('sendNotificationBtn');
  
  // Event listener cho confirmSendBtn đã được thiết lập trong initializeModals()
  
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
}
//Hiển thị modal xác nhận gửi thông báo
function confirmSendNotification() {
  debug("FUNCTION", "Hiển thị modal xác nhận gửi thông báo");
  
  // Kiểm tra các trường bắt buộc
  const title = document.getElementById('notificationTitle').value;
  const content = document.getElementById('notificationContent').value;
  const selectedRecipients = document.querySelectorAll('.recipient-checkbox:checked');
  const saveAsTemplate = document.getElementById('saveAsTemplate').checked;
  const templateName = document.getElementById('templateName').value;
  
  // Kiểm tra các điều kiện trước khi gửi
  if (!title || !content) {
    showToast('Vui lòng nhập đầy đủ tiêu đề và nội dung thông báo', 'error');
    
    // Hiển thị thông báo lỗi trong form
    const notificationError = document.getElementById('notificationFormError');
    if (notificationError) {
      notificationError.textContent = 'Vui lòng nhập đầy đủ tiêu đề và nội dung thông báo';
      notificationError.style.display = 'block';
    }
    return;
  }
  
  if (selectedRecipients.length === 0) {
    showToast('Vui lòng chọn ít nhất một ứng viên', 'error');
    
    // Hiển thị thông báo lỗi trong form
    const notificationError = document.getElementById('notificationFormError');
    if (notificationError) {
      notificationError.textContent = 'Vui lòng chọn ít nhất một ứng viên để gửi thông báo';
      notificationError.style.display = 'block';
    }
    return;
  }
  
  // Kiểm tra nếu người dùng muốn lưu mẫu nhưng chưa nhập tên mẫu
  if (saveAsTemplate && !templateName.trim()) {
    showToast('Vui lòng nhập tên mẫu thông báo', 'error');
    
    // Hiển thị thông báo lỗi trong form
    const notificationError = document.getElementById('notificationFormError');
    if (notificationError) {
      notificationError.textContent = 'Vui lòng nhập tên mẫu thông báo';
      notificationError.style.display = 'block';
    }
    return;
  }
  
  // Lấy thông tin vòng tuyển từ modal
  const notificationModal = document.getElementById('notificationModal');
  let stageName = "Không xác định";
  
  if (notificationModal && notificationModal.dataset.stageName) {
    stageName = getStageNameInVietnamese(notificationModal.dataset.stageName);
  }
  
  // Cập nhật thông tin vào modal xác nhận
  document.getElementById('confirmStage').textContent = stageName;
  document.getElementById('confirmTitle').textContent = title;
  document.getElementById('confirmCount').textContent = selectedRecipients.length;
  
  // Hiển thị modal xác nhận
  const confirmationModal = document.getElementById('confirmationModal');
  if (confirmationModal) {
    confirmationModal.style.display = 'flex';
  } else {
    debug("ERROR", "Không tìm thấy modal xác nhận");
  }
}

// Gửi thông báo hàng loạt
function sendBulkNotification() {
  const selectedRecipients = Array.from(document.querySelectorAll('.recipient-checkbox:checked'));
  const title = document.getElementById('notificationTitle').value;
  const content = document.getElementById('notificationContent').value;
  const saveAsTemplate = document.getElementById('saveAsTemplate').checked;
  const templateName = document.getElementById('templateName').value;
  
  // Kiểm tra các điều kiện bắt buộc
  if (!title || !content) {
    showToast('Vui lòng nhập đầy đủ tiêu đề và nội dung thông báo', 'error');
    return;
  }
  
  if (selectedRecipients.length === 0) {
    showToast('Vui lòng chọn ít nhất một ứng viên', 'error');
    return;
  }
  
  // Kiểm tra điều kiện lưu mẫu
  if (saveAsTemplate && !templateName.trim()) {
    showToast('Vui lòng nhập tên mẫu thông báo nếu bạn muốn lưu', 'error');
    return;
  }
  
  // Hiển thị đang tải
  const sendBtn = document.getElementById('sendNotificationBtn');
  const originalText = sendBtn.innerHTML;
  sendBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang gửi...';
  sendBtn.disabled = true;
  
  // Lấy stageId từ người nhận đầu tiên (tất cả người nhận phải thuộc cùng một vòng)
  const firstRecipient = selectedRecipients[0];
  const stageId = firstRecipient ? parseInt(firstRecipient.dataset.stageId) : 0;
  
  // Chuẩn bị dữ liệu thông báo
  const recipientsData = selectedRecipients.map(checkbox => ({
    applicationId: parseInt(checkbox.dataset.applicationId),
    userId: checkbox.dataset.userId,
    stageId: parseInt(checkbox.dataset.stageId)
  }));

  
  const notificationData = {
    title: title,
    content: content,
    saveAsTemplate: saveAsTemplate,
    templateName: templateName,
    stageId: stageId,
    clubId: window.campaignData.clubId,
    recipients: recipientsData
  };
  
  // Gửi thông báo
  // Log dữ liệu trước khi gửi
  console.log("Dữ liệu gửi thông báo:", notificationData);
  console.log("JSON data:", JSON.stringify(notificationData));

  fetch(`${contextPath}/api/sendBulkNotification`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(notificationData)
  })
  .then(response => {
    console.log("Phản hồi server:", response.status);
    return response.json();
  })
  .then(data => {
    console.log("Dữ liệu phản hồi:", data);
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
    .then(response => {
      debug("RECIPIENTS", "Phản hồi API nhận được, status:", response.status);
      return response.json();
    })
    .then(candidates => {
      
      // Kiểm tra nếu có lỗi từ API
      if (candidates && candidates.success === false) {
        recipientsList.innerHTML = `<div class="no-recipients"><p>Lỗi: ${candidates.message || 'Không thể tải danh sách ứng viên'}</p></div>`;
        return;
      }
      
      if (!candidates || candidates.length === 0) {
        recipientsList.innerHTML = '<div class="no-recipients"><p>Không có ứng viên nào trong vòng này</p></div>';
        return;
      }
      
      // Debug dữ liệu ứng viên nhận được
      debug("RECIPIENTS", "Raw candidates data:", candidates);
      
      // Lọc ứng viên theo trạng thái
      // Đảm bảo candidates là một mảng trước khi dùng filter
      const candidatesArray = Array.isArray(candidates) ? candidates : Object.values(candidates);
      
      const filteredCandidates = candidatesArray;
      
      try {
        debug("RECIPIENTS", "Bắt đầu tạo HTML cho danh sách người nhận...");
        
        // Tạo HTML cho danh sách người nhận
        let htmlContent = '';
        
        // Lặp qua danh sách ứng viên để tạo nội dung HTML
        filteredCandidates.forEach(candidate => {
          // Kiểm tra candidate tồn tại
          if (!candidate) {
            return; // Skip nếu không có dữ liệu
          }
          
          // Xác định trạng thái và kiểm tra giá trị hợp lệ
          const status = candidate.status || 'PENDING';
          const statusLower = status.toLowerCase();
          const statusText = status === 'APPROVED' ? 'Đã duyệt' : 
                            status === 'REJECTED' ? 'Từ chối' : 'Chờ xử lý';
          
          
          // Thêm vào chuỗi HTML
          htmlContent += `
        <div class="recipient-item" data-status="${statusLower}">
          <div class="recipient-info">
            <input type="checkbox" class="recipient-checkbox" 
                   data-application-id="${candidate.applicationId || ''}"
                   data-user-id="${candidate.userId || ''}"
                   data-stage-id="${stageId}">
            <div>
              <div class="recipient-name">${candidate.userName || 'Không có tên'}</div>
              <div class="recipient-email">${candidate.email || 'N/A'}</div>
              <div class="recipient-userid small text-muted">(ID: ${candidate.userId || 'N/A'})</div>
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
        });
        
        debug("RECIPIENTS", "HTML được tạo thành công, độ dài:", htmlContent.length);
        
        // Gán HTML vào container
        recipientsList.innerHTML = htmlContent;
      } catch (error) {
        recipientsList.innerHTML = '<div class="error-state"><p>Có lỗi xảy ra khi tải danh sách ứng viên</p></div>';
        return;
      }
      
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
  debug("CANDIDATES", "Loading candidates for active tab");
  
  const activeTab = document.querySelector('.tab-btn.active');
  if (!activeTab) {
    debug("CANDIDATES", "No active tab found");
    return;
  }
  
  const tabId = activeTab.getAttribute('data-tab');
  debug("CANDIDATES", "Active tab:", tabId);
  
  if (tabId === 'overview') {
    //dữ liệu đã được tải
    debug("CANDIDATES", "Overview tab active - no additional data loading needed");
  } else if (tabId === 'application') {
    // Tải danh sách ứng viên cho vòng nộp đơn
    debug("CANDIDATES", "Loading APPLICATION stage candidates");
    loadStageCandidates('application-candidates', 'APPLICATION');
  } else if (tabId === 'interview') {
    // Tải danh sách ứng viên cho vòng phỏng vấn
    debug("CANDIDATES", "Loading INTERVIEW stage candidates");
    loadStageCandidates('interview-candidates', 'INTERVIEW');
  } else if (tabId === 'challenge') {
    // Tải danh sách ứng viên cho vòng thử thách
    debug("CANDIDATES", "Loading CHALLENGE stage candidates");
    loadStageCandidates('challenge-candidates', 'CHALLENGE');
  }
}
function loadNotificationTemplates() {
  console.log("loadNotificationTemplates() called");
  
  const templateSelector = document.getElementById('templateSelector');
  if (!templateSelector) {
    console.log("No templateSelector found, exiting function");
    return;
  }
  
  // Debug thông tin campaignData
  console.log("window.campaignData:", window.campaignData);
  
  // Kiểm tra và xử lý clubId
  let clubId = window.campaignData?.clubId;
  console.log("Debug - Extracted clubId:", clubId);
  
  if (!clubId || isNaN(clubId) || clubId <= 0) {
    console.warn("Invalid clubId detected");
  }
  const apiUrl = `${contextPath}/api/notificationTemplates?clubId=${clubId}`;
  console.log("API URL:", apiUrl);
  // Hiển thị trạng thái đang tải
  templateSelector.innerHTML = '<option>Đang tải mẫu thông báo...</option>';
  
  fetch(apiUrl)
    .then(response => {
      console.log("Template API response status:", response.status);
      if (!response.ok) {
        console.error("API response not OK:", response);
        throw new Error(`API responded with status ${response.status}`);
      }
      return response.json();
    })
    .then(templates => {
      console.log("Template API response data:", templates);
      // Lưu templates vào biến toàn cục để sử dụng sau
      window.templates = templates;
      
      // Xóa tất cả các option hiện tại
      templateSelector.innerHTML = '';
      
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
        option.value = template.templateId;
        option.textContent = template.templateName;
        templateSelector.appendChild(option);
      });
    })
    .catch(error => {
      console.error("Error loading templates:", error);
      templateSelector.innerHTML = '';
      const option = document.createElement('option');
      option.value = '';
      option.textContent = 'Lỗi tải mẫu thông báo';
      templateSelector.appendChild(option);
      showToast("Lỗi khi tải mẫu thông báo");
    });
}

function loadTemplateContent(templateId) {
  // Lưu trữ templates đã tải vào biến toàn cục khi loadNotificationTemplates
  if (window.templates && window.templates.length > 0) {
    // Tìm template với ID tương ứng
    const template = window.templates.find(t => t.templateId === parseInt(templateId));
    if (template) {
      
      const titleInput = document.getElementById('notificationTitle');
      const contentInput = document.getElementById('notificationContent');
      
      if (titleInput && contentInput) {
        titleInput.value = template.title;
        contentInput.value = template.content;
      }
      return;
    }
  }
  
  // Nếu không tìm thấy trong cache, sẽ tải lại từ server
  showToast('Đang tải mẫu thông báo...', 'info');
  fetch(`${contextPath}/api/notificationTemplates?clubId=${window.campaignData.clubId}`)
    .then(response => response.json())
    .then(templates => {
      window.templates = templates; // Lưu vào cache
      
      const template = templates.find(t => t.templateId === parseInt(templateId));
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
      showToast('Có lỗi xảy ra khi tải nội dung mẫu thông báo');
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
  const notificationModal = document.getElementById('notificationModal');
  if (!notificationModal) {
    console.error("Không tìm thấy modal thông báo");
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
  
  // Ẩn thông báo lỗi
  const errorContainer = document.getElementById('notificationFormError');
  if (errorContainer) {
    errorContainer.textContent = '';
    errorContainer.style.display = 'none';
  }
  
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
  // Mở trang chi tiết đơn đăng ký trong tab mới
  window.open(`${contextPath}/application?id=${applicationId}`, '_blank');
}

// Xem chi tiết thông báo
function viewNotificationDetail(notificationId) {
  if (!notificationId) {
    showToast('Không tìm thấy thông tin thông báo', 'error');
    return;
  }
  // Mở trang chi tiết thông báo trong tab mới hoặc hiện modal
  // Tùy thuộc vào thiết kế của ứng dụng
  window.open(`${contextPath}/notification?id=${notificationId}`, '_blank');
}

function viewFormResponses(stageType) {
  const campaignId = window.campaignData?.campaignId;
  const templateId = window.campaignData?.templateId;
  const clubId = window.campaignData && window.campaignData.clubId ? window.campaignData.clubId : 1;
  
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
  
  // Thêm vào container
  const container = document.getElementById('toastContainer');
  if (!container) {
    console.error("Không tìm thấy toast container");
    return;
  }
  
  // Kiểm tra xem có toast với cùng nội dung đang hiển thị không
  const existingToasts = Array.from(container.querySelectorAll('.toast'));
  for (const existingToast of existingToasts) {
    const existingMessage = existingToast.querySelector('span')?.textContent;
    if (existingMessage === message) {
      return;
    }
  }
  
  // Tạo phần tử toast
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `
    <i class="fas fa-${type === 'success' ? 'check' : type === 'error' ? 'exclamation' : 'info'}-circle"></i>
    <span>${message}</span>
    <button class="toast-close">&times;</button>
  `;
  
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
  }, 100);
  
  // Ẩn và xóa toast
  setTimeout(() => {
    toast.classList.remove('show');
    setTimeout(() => {
      if (toast.parentNode === container) {
        container.removeChild(toast);
        debug("UI", "Toast đã xóa");
      }
    }, 300);
  }, 3000);
}
function loadStageCandidates(targetElementId, stageType) {  
  const candidatesContainer = document.getElementById(targetElementId);
  if (!candidatesContainer) {
    debug("STAGE_CANDIDATES", `Target element #${targetElementId} not found`);
    return;
  }
  
  // Hiển thị trạng thái đang tải
  candidatesContainer.innerHTML = '<tr><td colspan="6" class="text-center"><div class="loading"><i class="fas fa-spinner fa-spin"></i> Đang tải danh sách ứng viên...</div></td></tr>';
  
  // Gọi API để lấy danh sách ứng viên
  fetch(`${contextPath}/api/stageCandidates?campaignId=${window.campaignData.campaignId}&stageType=${stageType}`)
    .then(response => {
      debug("STAGE_CANDIDATES", `API Response status: ${response.status}`);
      return response.json();
    })
    .then(candidates => {
      // Kiểm tra dữ liệu trả về
      if (!candidates || candidates.length === 0) {
        candidatesContainer.innerHTML = '<tr><td colspan="6" class="text-center">Không có ứng viên nào trong vòng này</td></tr>';
        return;
      }
      
      // Xử lý và hiển thị danh sách ứng viên
      try {
        const candidatesHtml = candidates.map((candidate, index) => {
          // Format date
          const submitDate = candidate.submitDate ? new Date(candidate.submitDate).toLocaleDateString('vi-VN') : 'N/A';
          
          // Status badge class and text
          const statusClass = getStatusClass(candidate.status);
          const statusText = getStatusText(candidate.status);
          
          return `
            <tr>
              <td>${index + 1}</td>
              <td>${escapeHtml(candidate.userName || 'Không có tên')}</td>
              <td>${escapeHtml(candidate.email || 'N/A')}</td>
              <td>${submitDate}</td>
              <td>
                <span class="status-badge ${statusClass}">${statusText}</span>
              </td>
            </tr>
          `;
        }).join('');
        
        candidatesContainer.innerHTML = candidatesHtml;
      } catch (error) {
        candidatesContainer.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Có lỗi xảy ra khi hiển thị danh sách ứng viên</td></tr>';
      }
    })
    .catch(error => {
      debug("STAGE_CANDIDATES", `API Error: ${error.message}`, error);
      candidatesContainer.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Không thể tải dữ liệu từ server</td></tr>';
    });
}

/**
 * Helper function to get CSS class for status badge
 */
function getStatusClass(status) {
  switch(status) {
    case 'APPROVED': return 'approved';
    case 'REJECTED': return 'rejected';
    case 'PENDING':
    default: return 'pending';
  }
}

/**
 * Helper function to escape HTML special chars
 */
function escapeHtml(unsafe) {
  if (!unsafe) return '';
  return unsafe
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

// Công khai hàm để sử dụng toàn cục
window.viewStageDetails = viewStageDetails;
window.viewFormResponses = viewFormResponses;
window.filterRecipients = filterRecipients;
window.viewCandidateDetail = viewCandidateDetail;
window.viewNotificationDetail = viewNotificationDetail;
