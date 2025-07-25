document.addEventListener("DOMContentLoaded", function () {
  // Lấy context path từ URL
  const contextPath = window.location.pathname.substring(
    0,
    window.location.pathname.indexOf("/", 2)
  );

  // Kiểm tra xem có lỗi từ server không
  const errorMessageElem = document.getElementById("errorMessageData");
  if (errorMessageElem && errorMessageElem.value) {
    showToast("error", "Lỗi", errorMessageElem.value);
  }

  const urlParams = new URLSearchParams(window.location.search);
  const formId = urlParams.get("formId");
  const urlClubId = urlParams.get("clubId");
  const urlFormType = urlParams.get("formType");

  // Lấy formType từ URL hoặc từ trường ẩn trong HTML
  const formTypeInput = document.getElementById("formTypeInput");
  const inputFormType = formTypeInput ? formTypeInput.value : "not found";

  let formType = (urlFormType || inputFormType || "club").toLowerCase();

  if (formType !== "event" && formType !== "club") {
    formType = "club"; // Mặc định là club nếu giá trị không hợp lệ
  }

  // Lấy clubId từ input hidden hoặc URL
  const clubIdInput = document.getElementById("clubIdInput");
  const clubId =
    clubIdInput && clubIdInput.value ? clubIdInput.value : urlClubId;

  if (!formId || !clubId) {
    console.error("Không tìm thấy formId hoặc clubId");
    showToast("error", "Lỗi", "Không tìm thấy thông tin form");
    return;
  }
  // Lấy các phần tử DOM
  const responsesList = document.getElementById("responsesList");
  const searchInput = document.getElementById("responseSearchInput");
  const modal = document.getElementById("responseModal");
  const closeModal = document.querySelector(".close-modal");
  const filterTabs = document.querySelectorAll(".tab-btn");

  // Làm sạch thanh tìm kiếm khi tải trang
  if (searchInput) {
    searchInput.value = "";
    // Thêm nút xóa tìm kiếm vào form-response-search-box
    const searchBox = searchInput.closest(".form-response-search-box");
    if (
      searchBox &&
      !searchBox.querySelector(".form-response-search-clear-btn")
    ) {
      const clearButton = document.createElement("button");
      clearButton.className = "form-response-search-clear-btn";
      clearButton.innerHTML = '<i class="fas fa-times"></i>';
      clearButton.style.display = "none";
      clearButton.title = "Xóa từ khóa tìm kiếm";
      searchBox.appendChild(clearButton);
      // Thêm sự kiện cho nút xóa
      clearButton.addEventListener("click", function () {
        searchInput.value = "";
        searchTerm = "";
        this.style.display = "none";
        searchInput.focus();
        filterAndRenderResponses();
      });
      // Hiển thị/ẩn nút xóa dựa trên nội dung thanh tìm kiếm
      searchInput.addEventListener("input", function () {
        clearButton.style.display = this.value ? "flex" : "none";
      });
    }
  }

  // Kiểm tra xem có dữ liệu từ server không
  let serverData = null;
  const applicationsDataElem = document.getElementById("applicationsData");

  if (applicationsDataElem && applicationsDataElem.value) {
    try {
      serverData = JSON.parse(applicationsDataElem.value);
    } catch (e) {
      console.error("Error parsing server data:", e);
    }
  }

  // Chuyển đổi dữ liệu từ server thành định dạng cho frontend
  let responses = [];
  if (serverData && Array.isArray(serverData)) {
    console.log("[DEBUG - ReviewNote] Server data received:", serverData);
    responses = serverData.map((app) => {
      // Parse responses JSON nếu chưa được parse
      let answers = [];

      if (app.responses && typeof app.responses === "string") {
        try {
          console.log("Parsing response string:", app.responses);
          answers = JSON.parse(app.responses);
        } catch (e) {
          console.error("Error parsing responses JSON:", e);
          // Try to handle empty or partial JSON
          if (app.responses === "[]" || app.responses === "{}") {
            answers = [];
          }
        }
      } else if (Array.isArray(app.responses)) {
        answers = app.responses;
      } else {
        console.log("No valid responses found for ID:", app.responseId);
      }

      // Convert status từ database thành frontend format
      let status = app.responseStatus
        ? app.responseStatus.toLowerCase()
        : "waiting";
      if (status === "pending") status = "waiting";

      // Đảm bảo các giá trị không bị null/undefined và có thể tìm kiếm được
      const responseObj = {
        id: app.responseId ? app.responseId.toString() : "",
        name: app.fullName || "Người dùng",
        email: app.email || "",
        userId: app.userId || "",
        submittedAt: app.submitDate || new Date().toISOString(),
        status: status,
        answers: answers,
        reviewNote: app.reviewNote || "",
      };

      if (!responseObj.name || !responseObj.email || !responseObj.userId) {
        console.log(
          "Chú ý: Dữ liệu không đầy đủ cho response ID:",
          responseObj.id
        );
      }

      return responseObj;
    });
  } else {
    console.log("Không có dữ liệu từ server");
  }
  let currentFilter = "all";
  let searchTerm = "";

  updateStatistics(responses);

  // Hiển thị responses ban đầu
  renderResponses(responses);
  if (!searchInput) {
    console.error(
      "[DEBUG SEARCH] Không tìm thấy phần tử searchInput. Kiểm tra ID trong HTML."
    );
  } else {
    searchInput.addEventListener("input", function (event) {
      const oldSearchTerm = searchTerm;
      searchTerm = this.value.toLowerCase().trim();
      setTimeout(() => {
        filterAndRenderResponses();
      }, 10);
    });

    // Đảm bảo searchTerm được đồng bộ với giá trị ban đầu của input
    searchTerm = searchInput.value.toLowerCase().trim();
  }

  // Xử lý filter tabs
  filterTabs.forEach((tab) => {
    tab.addEventListener("click", function () {
      filterTabs.forEach((t) => t.classList.remove("active"));
      this.classList.add("active");
      currentFilter = this.dataset.filter;
      filterAndRenderResponses();
    });
  });

  closeModal.addEventListener("click", function () {
    modal.style.display = "none";
  });

  // Đóng modal khi click bên ngoài modal
  window.addEventListener("click", function (event) {
    if (event.target === modal) {
      modal.style.display = "none";
    }
  });

  //chuyển đổi status code thành text hiển thị
  function getStatusText(status) {
    switch (status) {
      case "waiting":
        return "Chờ duyệt";
      case "approved":
        return "Đã duyệt";
      case "rejected":
        return "Từ chối";
      case "candidate":
        return "Ứng viên";
      case "collaborator":
        return "Cộng tác viên";
      case "all":
        return "Tất cả";
      default:
        return status;
    }
  } 
  // Xử lý lọc và render responses - chi tiết hóa log để debug
  function filterAndRenderResponses() {
    let filteredResponses = [...responses]; // Tạo bản sao để tránh thay đổi mảng gốc

    // Lọc theo tab trước
    if (currentFilter !== "all") {
      const beforeCount = filteredResponses.length;

      filteredResponses = filteredResponses.filter(
        (response) => response.status === currentFilter
      );
    }

    // Lọc theo tìm kiếm
    if (searchTerm && searchTerm.length > 0) {
      try {
        // Chia từ khóa tìm kiếm thành các từ riêng biệt để tìm kiếm linh hoạt hơn
        const searchWords = searchTerm
          .split(" ")
          .filter((word) => word.length > 0);

        if (searchWords.length > 0) {
          const beforeCount = filteredResponses.length;

          filteredResponses = filteredResponses.filter((response) => {
            if (!response) {
              return false;
            }

            const name = (response.name || "").toLowerCase();
            const email = (response.email || "").toLowerCase();
            const userId = (response.userId || "").toLowerCase();

            // Kiểm tra xem có ít nhất một từ khóa tìm kiếm khớp với bất kỳ trường nào
            const matches = searchWords.some((word) => {
              const nameMatch = name.includes(word);
              const emailMatch = email.includes(word);
              const userIdMatch = userId.includes(word);

              return nameMatch || emailMatch || userIdMatch;
            });

            if (matches) {
              console.log(
                `[DEBUG FILTER] Tìm thấy kết quả khớp: ${response.name} (${response.email})`
              );
            }

            return matches;
          });
        }
      } catch (error) {
        console.error("[DEBUG FILTER] Lỗi khi thực hiện tìm kiếm:", error);
        // Giữ nguyên kết quả hiện tại nếu có lỗi
      }
    }

    renderResponses(filteredResponses);
  }
  // Render danh sách responses
  function renderResponses(dataToRender) {
    // Xóa nội dung hiện tại
    responsesList.innerHTML = "";

    // Kiểm tra nếu không có dữ liệu hoặc dữ liệu rỗng
    if (!dataToRender || dataToRender.length === 0) {
      let message = "";
      let iconClass = "fa-inbox";

      // Nếu đang tìm kiếm, hiển thị thông báo không tìm thấy kết quả tìm kiếm
      if (searchTerm && searchTerm.length > 0) {
        message = `Không tìm thấy đơn đăng ký nào phù hợp với từ khóa "${searchTerm}"`;
        if (currentFilter !== "all") {
          message += ` trong danh mục "${getStatusText(currentFilter)}"`;
        }
        iconClass = "fa-search";
      } else {
        // Nếu không tìm kiếm, hiển thị thông báo không có đơn trong danh mục
        message = `Không có đơn đăng ký nào ${
          currentFilter !== "all" ? "trong danh mục này" : ""
        }`;
        console.log(
          "[DEBUG RENDER] Hiển thị danh mục trống cho tab:",
          currentFilter
        );
      }

      const emptyStateElement = document.createElement("div");
      emptyStateElement.className = "no-responses-message";
      emptyStateElement.innerHTML = `
                <i class="fas ${iconClass}"></i>
                <p>${message}</p>
                ${
                  searchTerm
                    ? '<p style="font-size:0.9em;opacity:0.8;margin-top:10px;">Vui lòng thử từ khóa khác hoặc xóa bộ lọc tìm kiếm</p>'
                    : ""
                }
                ${
                  searchTerm
                    ? '<button id="clearSearchBtn" class="form-response-clear-search-btn"><i class="fas fa-times"></i> Xóa tìm kiếm</button>'
                    : ""
                }
            `;

      responsesList.appendChild(emptyStateElement);

      // Thêm sự kiện cho nút xóa tìm kiếm
      const clearSearchBtn = document.getElementById("clearSearchBtn");
      if (clearSearchBtn) {
        clearSearchBtn.addEventListener("click", function () {
          console.log("[DEBUG RENDER] Người dùng nhấn nút xóa tìm kiếm");
          if (searchInput) {
            searchInput.value = "";
            searchTerm = "";
            filterAndRenderResponses();
          }
        });
      }

      return;
    }

    responsesList.innerHTML = "";

    dataToRender.forEach((response) => {
      const responseItem = document.createElement("div");
      responseItem.className = "response-item";
      responseItem.dataset.id = response.id;

      let statusClass = "";
      let statusText = "";

      switch (response.status) {
        case "waiting":
          statusClass = "status-waiting";
          statusText = "Chờ duyệt";
          break;
        case "approved":
          statusClass = "status-approved";
          statusText = "Đã duyệt";
          break;
        case "rejected":
          statusClass = "status-rejected";
          statusText = "Từ chối";
          break;
        case "candidate":
          statusClass = "status-candidate";
          statusText = "Ứng viên";
          break;
        case "collaborator":
          statusClass = "status-collaborator";
          statusText = "Cộng tác viên";
          break;
      }

      let actionButtons = "";

      // Tạo nút hành động dựa trên trạng thái
      actionButtons = "";

      // Nút từ chối xuất hiện trong mọi trạng thái trừ "từ chối" và "đã duyệt"
      if (response.status !== "rejected" && response.status !== "approved") {
        actionButtons += `
                    <button class="action-btn btn-reject" data-action="reject" data-id="${response.id}">
                        <i class="fas fa-times"></i> Từ chối
                    </button>
                `;
      }

      // Phân biệt xử lý dựa trên loại form
      if (formType === "event") {
        // Đơn sự kiện: chỉ có nút duyệt ở trạng thái "chờ duyệt"
        if (response.status === "waiting") {
          actionButtons += `
                        <button class="action-btn btn-approve" data-action="approve" data-id="${response.id}">
                            <i class="fas fa-check"></i> Duyệt
                        </button>
                    `;
        }
      } else {
        // Đơn club: có các nút duyệt theo trình tự
        if (response.status === "waiting") {
          actionButtons += `
                        <button class="action-btn btn-approve" data-action="approve" data-id="${response.id}">
                            <i class="fas fa-check"></i> Duyệt
                        </button>
                    `;
        } else if (response.status === "candidate") {
          actionButtons += `
                        <button class="action-btn btn-promote" data-action="approve" data-id="${response.id}">
                            <i class="fas fa-user-plus"></i> Nâng cấp
                        </button>
                    `;
        } else if (response.status === "collaborator") {
          actionButtons += `
                        <button class="action-btn btn-promote" data-action="approve" data-id="${response.id}">
                            <i class="fas fa-user-plus"></i> Nâng cấp
                        </button>
                    `;
        }
      }

      actionButtons += `
                <button class="action-btn btn-details" data-action="details" data-id="${response.id}">
                    <i class="fas fa-eye"></i> Chi tiết
                </button>
            `;

      responseItem.innerHTML = `
                <div class="response-header">
                    <div class="user-info">
                        <div class="user-avatar">
                            <img src="${contextPath}/img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg" alt="User Avatar">
                        </div>
                        <div class="user-details">
                            <div class="user-name">${response.name}</div>
                            <div class="user-email">${response.email}</div>
                            <div class="user-id">ID: ${response.userId}</div>
                        </div>
                    </div>
                    <div class="response-date">
                        Ngày gửi: ${formatDate(response.submittedAt)}
                    </div>
                </div>
                <div class="response-body">
                    <div class="response-content">
                        ${
                          response.shortContent ||
                          "Sinh viên đã nộp đơn đăng ký tham gia " +
                            (formType === "club" ? "câu lạc bộ" : "sự kiện")
                        }
                    </div>
                </div>
                <div class="response-footer">
                    <span class="response-status ${statusClass}">${statusText}</span>
                    <div class="response-actions">
                        ${actionButtons}
                    </div>
                </div>
            `;

      responsesList.appendChild(responseItem);
    });

    // Thêm event listeners cho các nút hành động
    document.querySelectorAll(".action-btn").forEach((button) => {
      button.addEventListener("click", function (event) {
        event.stopPropagation();
        const action = this.dataset.action;
        const id = this.dataset.id;

        switch (action) {
          case "details":
            showResponseDetails(id);
            break;
          case "approve":
            approveResponse(id);
            break;
          case "reject":
            rejectResponse(id);
            break;
        }
      });
    });

    // Thêm sự kiện click vào response item để mở modal chi tiết
    document.querySelectorAll(".response-item").forEach((item) => {
      item.addEventListener("click", function () {
        const id = this.dataset.id;
        showResponseDetails(id);
      });
    });
  }

  // Hàm cập nhật thống kê
  function updateStatistics(data) {
    if (!data) return;

    // Tính toán số lượng theo từng trạng thái
    const stats = {
      total: data.length,
      waiting: data.filter((r) => r.status === "waiting").length,
      approved: data.filter((r) => r.status === "approved").length,
      rejected: data.filter((r) => r.status === "rejected").length,
      candidate: data.filter((r) => r.status === "candidate").length,
      collaborator: data.filter((r) => r.status === "collaborator").length,
    };

    // Cập nhật các con số thống kê
    document.getElementById("totalCount").textContent = stats.total;
    document.getElementById("waitingCount").textContent = stats.waiting;
    document.getElementById("approvedCount").textContent = stats.approved;
    document.getElementById("rejectedCount").textContent = stats.rejected;

    // Cập nhật tabs
    document.getElementById("allCount").textContent = stats.total;
    document.getElementById("waitingTabCount").textContent = stats.waiting;
    document.getElementById("approvedTabCount").textContent = stats.approved;
    document.getElementById("rejectedTabCount").textContent = stats.rejected;

    // Cập nhật thêm cho loại club
    if (formType === "club") {
      if (document.getElementById("candidateCount")) {
        document.getElementById("candidateCount").textContent = stats.candidate;
      }
      if (document.getElementById("collaboratorCount")) {
        document.getElementById("collaboratorCount").textContent =
          stats.collaborator;
      }
      if (document.getElementById("candidateTabCount")) {
        document.getElementById("candidateTabCount").textContent =
          stats.candidate;
      }
      if (document.getElementById("collaboratorTabCount")) {
        document.getElementById("collaboratorTabCount").textContent =
          stats.collaborator;
      }
    }
  }

  // Hiển thị chi tiết phản hồi
  function showResponseDetails(id) {
    console.log("Showing response details for ID:", id);
    const response = responses.find((r) => r.id === id);

    if (!response) {
      showToast("error", "Lỗi", "Không tìm thấy thông tin đơn đăng ký");
      return;
    }

    console.log("Found response:", response);
    console.log("Response answers type:", typeof response.answers);
    console.log("Response answers:", response.answers);

    // Đảm bảo answers luôn là một array
    if (!response.answers) {
      response.answers = [];
    } else if (
      typeof response.answers === "string" &&
      response.answers.trim() === ""
    ) {
      response.answers = [];
    }

    document.getElementById(
      "responseModalTitle"
    ).textContent = `Chi tiết đơn đăng ký - ${response.name}`;

    // Thiết lập nút lưu đánh giá
    const saveReviewBtn = document.getElementById("saveReviewBtn");
    const reviewNoteInput = document.getElementById("reviewNoteInput");

    console.log(
      "[DEBUG - ReviewNote] In showResponseDetails - response:",
      id,
      "reviewNote:",
      response.reviewNote
    );

    // Hiển thị đánh giá hiện tại (nếu có)
    if (response.reviewNote) {
      console.log(
        "[DEBUG - ReviewNote] Setting reviewNote input value to:",
        response.reviewNote
      );
      reviewNoteInput.value = response.reviewNote;
    } else {
      console.log(
        "[DEBUG - ReviewNote] No reviewNote found, setting empty value"
      );
      reviewNoteInput.value = "";
    }

    // Gỡ bỏ event listener cũ (nếu có)
    const newSaveReviewBtn = saveReviewBtn.cloneNode(true);
    saveReviewBtn.parentNode.replaceChild(newSaveReviewBtn, saveReviewBtn);

    // Thêm event listener mới
    newSaveReviewBtn.addEventListener("click", function () {
      saveReviewNote(id, reviewNoteInput.value);
    });

    // Tạo nội dung cho modal
    let modalContent = `
            <div class="modal-section">
                <div class="user-header">
                    <div class="user-avatar-large">
                        <img src="${contextPath}/img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg" alt="User Avatar">
                    </div>
                    <div class="user-info-large">
                        <div class="user-name-large">${response.name}</div>
                        <div class="user-meta">
                            <div class="meta-item">
                                <i class="fas fa-envelope"></i>
                                ${response.email}
                            </div>
                            <div class="meta-item">
                                <i class="fas fa-id-card"></i>
                                ID: ${response.userId}
                            </div>
                            <div class="meta-item">
                                <i class="fas fa-clock"></i>
                                Thời gian gửi: ${formatDate(
                                  response.submittedAt
                                )}
                            </div>
                            <div class="meta-item">
                                <i class="fas fa-tag"></i>
                                Trạng thái: ${getStatusText(response.status)}
                            </div>
                        </div>
                    </div>
                </div>
                
                ${
                  response.reviewNote
                    ? `
                <div class="review-note-display">
                    <h4><i class="fas fa-clipboard-check"></i> Đánh giá</h4>
                    <p>${response.reviewNote}</p>
                </div>
                `
                    : ""
                }
            </div>
            
            <div class="modal-section">
                <h3 class="section-title">Các câu trả lời</h3>
                <div id="questionAnswersContainer">                <p class="form-description">Những câu trả lời dưới đây được nộp bởi người dùng. Câu hỏi về ban/phòng ban được <span class="highlight-text">đánh dấu màu cam</span>.</p>
                ${renderQuestionAnswers(response.answers)}
            </div>
            
            <script>
                // Kiểm tra xem có nội dung trong phần câu trả lời không
                const qaContainer = document.getElementById('questionAnswersContainer');
                if (qaContainer && (
                    qaContainer.innerHTML.trim() === '<p>Không có câu trả lời nào.</p>' || 
                    qaContainer.innerHTML.includes('Không có câu trả lời nào hoặc dữ liệu không đúng định dạng')
                )) {
                    // Thêm thông báo chi tiết hơn
                    qaContainer.innerHTML = 
                        '<div class="no-answers-message">' +
                            '<i class="fas fa-exclamation-circle"></i>' +
                            '<p>Không tìm thấy câu trả lời nào hoặc dữ liệu câu trả lời không đúng định dạng.</p>' +
                            '<p>Có thể form không được thiết kế đúng hoặc người dùng chưa trả lời câu hỏi nào.</p>' +
                        '</div>';
                }
            </script>
            </div>
        `;

    // Thêm phần footer với các nút hành động
    let modalFooter = '<div class="modal-footer">';

    console.log(
      "Rendering modal footer. Form type:",
      formType,
      "Status:",
      response.status
    );

    // Thêm nút từ chối cho tất cả các trạng thái trừ đã từ chối và đã duyệt
    if (response.status !== "rejected" && response.status !== "approved") {
      modalFooter += `
                <button class="action-btn btn-reject" id="modalRejectBtn" data-id="${response.id}">
                    <i class="fas fa-times"></i> Từ chối
                </button>
            `;
    }

    // Phân biệt xử lý dựa trên loại form
    if (formType === "event") {
      // Form sự kiện: chỉ có nút duyệt ở trạng thái chờ duyệt
      if (response.status === "waiting") {
        modalFooter += `
                    <button class="action-btn btn-approve" id="modalApproveBtn" data-id="${response.id}">
                        <i class="fas fa-check"></i> Duyệt đăng ký
                    </button>
                `;
      }
    } else {
      // Form club: có các nút theo quy trình thăng cấp
      if (response.status === "waiting") {
        modalFooter += `
                    <button class="action-btn btn-approve" id="modalApproveBtn" data-id="${response.id}">
                        <i class="fas fa-check"></i> Duyệt thành ứng viên
                    </button>
                `;
      } else if (response.status === "candidate") {
        modalFooter += `
                    <button class="action-btn btn-promote" id="modalApproveBtn" data-id="${response.id}">
                        <i class="fas fa-user-plus"></i> Nâng cấp thành cộng tác viên
                    </button>
                `;
      } else if (response.status === "collaborator") {
        modalFooter += `
                    <button class="action-btn btn-promote" id="modalApproveBtn" data-id="${response.id}">
                        <i class="fas fa-user-plus"></i> Nâng cấp thành thành viên chính thức
                    </button>
                `;
      }
    }

    modalFooter += `</div>`;
    modalContent += modalFooter;

    document.getElementById("responseModalContent").innerHTML = modalContent;

    // Thêm event listeners cho các nút trong modal
    const modalApproveBtn = document.getElementById("modalApproveBtn");
    const modalRejectBtn = document.getElementById("modalRejectBtn");

    if (modalApproveBtn) {
      modalApproveBtn.addEventListener("click", function () {
        approveResponse(this.dataset.id);
        modal.style.display = "none";
      });
    }

    if (modalRejectBtn) {
      modalRejectBtn.addEventListener("click", function () {
        rejectResponse(this.dataset.id);
        modal.style.display = "none";
      });
    }

    modal.style.display = "block";
  }

  // Render câu hỏi và câu trả lời
  function renderQuestionAnswers(answers) {
    console.log("Rendering answers:", answers);

    if (!answers) {
      return "<p>Không có câu trả lời nào.</p>";
    }

    // Nếu answers là string JSON, parse nó
    if (typeof answers === "string") {
      try {
        answers = JSON.parse(answers);
        console.log("Parsed string answers to:", answers);
      } catch (e) {
        console.error(
          "Error parsing answers JSON in renderQuestionAnswers:",
          e
        );
        return '<div class="no-answers">Lỗi hiển thị câu trả lời</div>';
      }
    }

    let html = "";

    // Xử lý trường hợp answers là object (không phải array)
    if (
      !Array.isArray(answers) &&
      typeof answers === "object" &&
      answers !== null
    ) {
      console.log("Answers is an object, converting to array format");

      const answersArray = [];

      // Duyệt qua tất cả các thuộc tính của object
      for (const key in answers) {
        if (answers.hasOwnProperty(key)) {
          const item = answers[key];

          // Trường hợp giá trị đơn giản (string, number)
          if (typeof item === "string" || typeof item === "number") {
            answersArray.push({
              question: getQuestionNameFromId(key),
              answer: item,
            });
          }
          // Trường hợp là object
          else if (typeof item === "object" && item !== null) {
            let answer = "";

            if (item.type === "radio" || item.type === "text") {
              answer = item.value || "";
            } else if (item.type === "checkbox" && Array.isArray(item.values)) {
              answer = item.values.join(", ");
            } else if (item.type === "date") {
              answer = item.value || "";
            } else if (item.value !== undefined) {
              answer = item.value;
            } else {
              // Nếu không xác định được cấu trúc, hiển thị dưới dạng JSON
              try {
                answer = JSON.stringify(item);
              } catch (e) {
                answer = "Không thể hiển thị giá trị";
              }
            }

            answersArray.push({
              question: getQuestionNameFromId(item.fieldId || key),
              answer: answer,
              fieldId: item.fieldId || key,
            });
          }
        }
      }

      answers = answersArray;
      console.log("Converted to array:", answers);
    }

    if (!Array.isArray(answers)) {
      console.error("Answers could not be converted to an array:", answers);
      return '<div class="no-answers">Không có câu trả lời nào hoặc dữ liệu không đúng định dạng.</div>';
    }

    if (answers.length === 0) {
      return "<p>Không có câu trả lời nào.</p>";
    }

    answers.forEach((qa, index) => {
      console.log("Processing QA item:", qa, "Type:", typeof qa);

      let question = "";
      let answer = "";

      if (typeof qa === "object" && qa !== null) {
        // Object với cặp key-value
        if (qa.question !== undefined) {
          question = qa.question;
        } else if (qa.questionText !== undefined) {
          question = qa.questionText;
        } else if (qa.label !== undefined) {
          question = qa.label;
        }

        if (qa.answer !== undefined) {
          answer = qa.answer;
        } else if (qa.answerText !== undefined) {
          answer = qa.answerText;
        } else if (qa.value !== undefined) {
          answer = qa.value;
        }

        // Nếu không tìm thấy thông tin theo cấu trúc chuẩn, thử duyệt các key
        if (question === "" && answer === "") {
          for (let key in qa) {
            if (
              key.toLowerCase().includes("question") ||
              key.toLowerCase().includes("label")
            ) {
              question = qa[key];
            } else if (
              key.toLowerCase().includes("answer") ||
              key.toLowerCase().includes("value")
            ) {
              answer = qa[key];
            }
          }
        }
      }

      // Nếu vẫn không tìm được câu hỏi và câu trả lời, hiển thị thông tin raw
      if (question === "" && answer === "") {
        if (typeof qa === "string") {
          // Nếu là string, hiển thị như câu trả lời với câu hỏi mặc định
          question = "Câu trả lời";
          answer = qa;
        } else if (qa !== null) {
          question = "Dữ liệu câu hỏi";
          try {
            answer = JSON.stringify(qa);
          } catch (e) {
            answer = "Không thể hiển thị";
          }
        }
      }

      // Kiểm tra nếu đây là câu hỏi về department/ban
      let highlightClass = "";
      if (
        formType === "club" &&
        ((question &&
          (question.toLowerCase().includes("ban") ||
            question.toLowerCase().includes("department") ||
            question.toLowerCase().includes("phòng ban"))) ||
          qa.fieldId === "40" || // ID cho câu hỏi ban
          (answer &&
            typeof answer === "string" &&
            (answer.toLowerCase().includes("ban") ||
              answer.toLowerCase().includes("phòng"))))
      ) {
        highlightClass = "department-question";
      }

      // Chỉ hiển thị nếu có câu hỏi hoặc câu trả lời
      if (question || answer) {
        html += `
                    <div class="question-item ${highlightClass}">
                        <div class="question-label">${
                          question || "Câu hỏi không xác định"
                        }</div>
                        <div class="question-answer ${
                          answer && answer.length > 100
                            ? "multiline-answer"
                            : ""
                        }">${answer || "Không có câu trả lời"}</div>
                    </div>
                `;
      }
    });

    return html;
  }

  // Hàm xử lý duyệt đơn
  function approveResponse(id) {
    // Gửi AJAX request đến server
    fetch(`${contextPath}/formResponses`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: `action=approve&responseId=${id}&clubId=${clubId}`,
    })
      .then((response) => response.json())
      .then((data) => {
        console.log("Server response:", data);

        if (data.success) {
          // Cập nhật trạng thái local
          const index = responses.findIndex((r) => r.id === id);
          if (index !== -1) {
            responses[index].status = data.status || "approved";
            showToast(
              "success",
              "Thành công",
              data.message || "Đã duyệt đơn thành công"
            );
            updateStatistics(responses);
            filterAndRenderResponses();
          } else {
            // Nếu không tìm thấy trong mảng responses, reload trang để lấy dữ liệu mới
            showToast(
              "success",
              "Thành công",
              data.message || "Đã duyệt đơn thành công"
            );
            setTimeout(() => location.reload(), 1500);
          }
        } else {
          showToast(
            "error",
            "Lỗi",
            data.message || "Không thể duyệt đơn đăng ký"
          );
        }
      })
      .catch((error) => {
        console.error("Error approving response:", error);
        showToast(
          "error",
          "Lỗi kết nối",
          "Không thể kết nối với máy chủ. Vui lòng thử lại sau."
        );
      });
  }

  // Hàm xử lý từ chối đơn
  function rejectResponse(id) {
    console.log("Từ chối đơn:", id, "clubId:", clubId);

    // Hiển thị thông báo đang xử lý
    showToast("info", "Đang xử lý", "Đang gửi yêu cầu từ chối đơn...");

    // Gửi AJAX request đến server
    fetch(`${contextPath}/formResponses`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: `action=reject&responseId=${id}&clubId=${clubId}`,
    })
      .then((response) => response.json())
      .then((data) => {
        console.log("Server response:", data);

        if (data.success) {
          // Cập nhật trạng thái local
          const index = responses.findIndex((r) => r.id === id);
          if (index !== -1) {
            responses[index].status = "rejected";
            showToast(
              "error",
              "Đã từ chối",
              data.message || "Đơn đăng ký đã bị từ chối"
            );
            updateStatistics(responses);
            filterAndRenderResponses();
          } else {
            // Nếu không tìm thấy trong mảng responses, reload trang để lấy dữ liệu mới
            showToast(
              "error",
              "Đã từ chối",
              data.message || "Đơn đăng ký đã bị từ chối"
            );
            setTimeout(() => location.reload(), 1500);
          }
        } else {
          showToast(
            "error",
            "Lỗi",
            data.message || "Không thể từ chối đơn đăng ký"
          );
        }
      })
      .catch((error) => {
        console.error("Error rejecting response:", error);
        showToast(
          "error",
          "Lỗi kết nối",
          "Không thể kết nối với máy chủ. Vui lòng thử lại sau."
        );
      });
  }

  // Hiển thị toast thông báo
  function showToast(type, title, message) {
    const toastContainer = document.getElementById("toast-container");
    const toastId = "toast-" + Date.now();

    const toast = document.createElement("div");
    toast.id = toastId;
    toast.className = `toast toast-${type}`;

    let icon = "";
    switch (type) {
      case "success":
        icon = "check-circle";
        break;
      case "error":
        icon = "times-circle";
        break;
      case "info":
        icon = "info-circle";
        break;
      case "warning":
        icon = "exclamation-circle";
        break;
    }

    toast.innerHTML = `
            <div class="toast-icon">
                <i class="fas fa-${icon}"></i>
            </div>
            <div class="toast-content">
                <div class="toast-title">${title}</div>
                <div class="toast-message">${message}</div>
            </div>
            <div class="toast-close" onclick="document.getElementById('${toastId}').remove()">
                <i class="fas fa-times"></i>
            </div>
        `;

    toastContainer.appendChild(toast);

    // Tự động tắt sau 5 giây
    setTimeout(() => {
      if (document.getElementById(toastId)) {
        document.getElementById(toastId).classList.add("hide");

        setTimeout(() => {
          if (document.getElementById(toastId)) {
            document.getElementById(toastId).remove();
          }
        }, 300);
      }
    }, 5000);
  }

  // Hàm lưu đánh giá
  function saveReviewNote(responseId, reviewNote) {
    if (!responseId) {
      showToast("error", "Lỗi", "Không tìm thấy ID phản hồi");
      return;
    }

    // Hiển thị thông báo đang xử lý
    showToast("info", "Đang xử lý", "Đang lưu đánh giá...");

    // Gửi AJAX request đến server để lưu đánh giá
    fetch(`${contextPath}/formResponses`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: `action=saveReview&responseId=${responseId}&clubId=${clubId}&reviewNote=${encodeURIComponent(
        reviewNote
      )}`,
    })
      .then((response) => response.json())
      .then((data) => {
        console.log("Server response for saveReview:", data);

        if (data.success) {
          // Cập nhật trạng thái local
          const index = responses.findIndex((r) => r.id === responseId);
          if (index !== -1) {
            responses[index].reviewNote = reviewNote;
            showToast("success", "Thành công", "Đã lưu đánh giá");
          } else {
            showToast("success", "Thành công", "Đã lưu đánh giá");
          }
        } else {
          showToast("error", "Lỗi", data.message || "Không thể lưu đánh giá");
        }
      })
      .catch((error) => {
        console.error("Error saving review note:", error);
        showToast("error", "Lỗi", "Đã xảy ra lỗi khi lưu đánh giá");
      });
  }

  // Hàm định dạng ngày tháng
  function formatDate(dateString) {
    if (!dateString) return "N/A";

    const date = new Date(dateString);
    return date.toLocaleString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  }

  // Lấy text cho trạng thái
  function getStatusText(status) {
    switch (status) {
      case "waiting":
        return "Chờ duyệt";
      case "approved":
        return "Đã duyệt";
      case "rejected":
        return "Từ chối";
      case "candidate":
        return "Ứng viên";
      case "collaborator":
        return "Cộng tác viên";
      default:
        return status;
    }
  }

  // Hàm cố gắng lấy tên câu hỏi từ fieldId
  function getQuestionNameFromId(fieldId) {
    // Đây là mapping giữa fieldId và tên câu hỏi thường gặp
    const commonQuestions = {
      1: "Bạn muốn tham gia ban nào?",
    };

    return commonQuestions[fieldId] || `Câu hỏi ${fieldId}`;
  }
});
