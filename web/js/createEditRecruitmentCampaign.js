/**
 * CreateEditRecruitmentCampaign - Module xử lý tạo mới và chỉnh sửa hoạt động tuyển quân
 */
// Khai báo biến global cho script
let isEdit = false;
let contextPath = "";

const criticalFields = [
  "formId",
  "applicationStageStart",
  "applicationStageEnd",
  "interviewStageStart",
  "interviewStageEnd",
  "challengeStageStart",
  "challengeStageEnd",
  "startDate",
  "endDate",
  "title",
  "gen",
];

// Hàm hiển thị thông báo lỗi khi cần thiết
function showDebugInfo(message, data = null) {
  const debugPanel = document.getElementById("debugPanel");
  const debugContent = document.getElementById("debugContent");

  if (debugPanel && debugContent) {
    let content = `<p><strong>${message}</strong></p>`;

    if (data) {
      if (typeof data === "object") {
        content += "<pre>" + JSON.stringify(data, null, 2) + "</pre>";
      } else {
        content += `<p>${data}</p>`;
      }
    }

    debugContent.innerHTML = content;
    debugPanel.style.display = "block";

    // Tạo style cho debug panel khi cần hiển thị
    debugPanel.style.marginBottom = "15px";
    debugPanel.style.padding = "10px";
    debugPanel.style.border = "1px solid #f5c6cb";
    debugPanel.style.backgroundColor = "#f8d7da";
    debugPanel.style.color = "#721c24";
    debugPanel.style.borderRadius = "5px";
  }
}

document.addEventListener("DOMContentLoaded", function () {
  // Xác định mode tạo mới hay chỉnh sửa từ form data
  const form = document.getElementById("recruitmentForm");

  // Xác định mode chính xác dựa trên data-mode và kiểm tra thêm input recruitmentId
  if (form) {
    const hasValidId = document.querySelector('input[name="recruitmentId"]');
    isEdit = form.dataset.mode === "edit" && hasValidId !== null;
    
    // **Khai báo biến fields toàn cục cho form**
    window.formFields = form.querySelectorAll('input, textarea, select');
    
    // **Lưu trữ và khôi phục dữ liệu form từ Local Storage**
    // Khôi phục dữ liệu từ Local Storage khi trang load
    window.formFields.forEach(field => {
      if (field.id) { // Chỉ xử lý các trường có id
        const savedValue = localStorage.getItem(field.id);
        if (savedValue && !isEdit) { // Chỉ khôi phục khi tạo mới, không áp dụng khi chỉnh sửa
          field.value = savedValue;
        }
      }
    });

    // Lưu trữ dữ liệu vào Local Storage khi người dùng nhập
    window.formFields.forEach(field => {
      if (field.id) { // Chỉ xử lý các trường có id
        field.addEventListener('input', function() {
          localStorage.setItem(this.id, this.value);
        });
      }
    });

    // **Giới hạn ký tự cho tiêu đề**
    const titleInput = document.getElementById('title');
    if (titleInput) {
      const titleCharCount = document.getElementById('titleCharCount');
      function updateTitleCharCount() {
        const maxLength = titleInput.getAttribute('maxlength') || 100;
        const currentLength = titleInput.value.length;
        if (titleCharCount) {
          titleCharCount.textContent = `${currentLength}/${maxLength} ký tự`;
        }
      }
      titleInput.addEventListener('input', updateTitleCharCount);
      updateTitleCharCount(); // Cập nhật ban đầu
    }

    // **Giới hạn ký tự cho mô tả**
    const descriptionInput = document.getElementById('description');
    if (descriptionInput) {
      const descriptionCharCount = document.getElementById('descriptionCharCount');
      function updateDescriptionCharCount() {
        const maxLength = descriptionInput.getAttribute('maxlength') || 500;
        const currentLength = descriptionInput.value.length;
        if (descriptionCharCount) {
          descriptionCharCount.textContent = `${currentLength}/${maxLength} ký tự`;
        }
      }
      descriptionInput.addEventListener('input', updateDescriptionCharCount);
      updateDescriptionCharCount(); // Cập nhật ban đầu
    }
  } else {
    isEdit = false;
  }

  contextPath = getContextPath();

  // Khởi tạo các bước trong wizard
  initializeWizard();

  // Khởi tạo datepickers và validation
  initializeDateConstraints();

  // Thiết lập form submission
  setupFormSubmission();

  // Thiết lập các trường không thể chỉnh sửa nếu cần thiết
  setupFieldRestrictions();
});

/**
 * Khởi tạo wizard với các bước
 */
function initializeWizard() {
  // Lấy các elements
  const step1 = document.getElementById("step1");
  const step2 = document.getElementById("step2");
  const step3 = document.getElementById("step3");

  const step1NextBtn = document.getElementById("step1NextBtn");
  const step2PrevBtn = document.getElementById("step2PrevBtn");
  const step2NextBtn = document.getElementById("step2NextBtn");
  const step3PrevBtn = document.getElementById("step3PrevBtn");

  // Stepper indicators
  const step1_2Line = document.getElementById("step1-2-line");
  const step2_3Line = document.getElementById("step2-3-line");
  const step2Circle = document.getElementById("step2-circle");
  const step3Circle = document.getElementById("step3-circle");
  const step2Text = document.getElementById("step2-text");
  const step3Text = document.getElementById("step3-text");

  // Button events
  if (step1NextBtn) {
    step1NextBtn.addEventListener("click", function () {
      if (validateStep1()) {
        step1.classList.remove("active");
        step2.classList.add("active");

        // Update stepper
        step1_2Line.classList.remove("inactive");
        step1_2Line.classList.add("active");
        step2Circle.classList.remove("inactive");
        step2Circle.classList.add("active");
        step2Text.classList.remove("inactive");
        step2Text.classList.add("active");
      }
    });
  }

  if (step2PrevBtn) {
    step2PrevBtn.addEventListener("click", function () {
      step2.classList.remove("active");
      step1.classList.add("active");

      // Update stepper
      step1_2Line.classList.remove("active");
      step1_2Line.classList.add("inactive");
      step2Circle.classList.remove("active");
      step2Circle.classList.add("inactive");
      step2Text.classList.remove("active");
      step2Text.classList.add("inactive");
    });
  }

  if (step2NextBtn) {
    step2NextBtn.addEventListener("click", function () {
      if (validateStep2()) {
        step2.classList.remove("active");
        step3.classList.add("active");

        // Update stepper
        step2_3Line.classList.remove("inactive");
        step2_3Line.classList.add("active");
        step3Circle.classList.remove("inactive");
        step3Circle.classList.add("active");
        step3Text.classList.remove("inactive");
        step3Text.classList.add("active");

        // Populate confirmation step
        populateConfirmationStep();
      }
    });
  }

  if (step3PrevBtn) {
    step3PrevBtn.addEventListener("click", function () {
      step3.classList.remove("active");
      step2.classList.add("active");

      // Update stepper
      step2_3Line.classList.remove("active");
      step2_3Line.classList.add("inactive");
      step3Circle.classList.remove("active");
      step3Circle.classList.add("inactive");
      step3Text.classList.remove("active");
      step3Text.classList.add("inactive");
    });
  }

  // Prefill dates nếu đang ở chế độ edit
  const form = document.getElementById("recruitmentForm");
  if (form && form.dataset.mode === "edit") {
    prefillStageData();
  }
}

/**
 * Validate thông tin ở step 1
 */
function validateStep1() {
  const requiredFields = document.querySelectorAll("#step1 [required]");
  let isValid = true;

  // Kiểm tra tất cả các trường required
  requiredFields.forEach((field) => {
    if (!field.value.trim()) {
      isValid = false;
      markFieldInvalid(field, "Trường này là bắt buộc");
    } else {
      markFieldValid(field);
    }
  });

  // Kiểm tra cụ thể trường formId (form đăng ký)
  const formIdField = document.getElementById("formId");
  if (!formIdField.value || formIdField.value.trim() === "") {
    isValid = false;
    markFieldInvalid(formIdField, "Vui lòng chọn form đăng ký");
  }

  // Kiểm tra ngày bắt đầu < ngày kết thúc
  const startDate = new Date(document.getElementById("startDate").value);
  const endDate = new Date(document.getElementById("endDate").value);

  if (isNaN(startDate.getTime())) {
    isValid = false;
    markFieldInvalid(
      document.getElementById("startDate"),
      "Vui lòng chọn ngày bắt đầu"
    );
  }

  if (isNaN(endDate.getTime())) {
    isValid = false;
    markFieldInvalid(
      document.getElementById("endDate"),
      "Vui lòng chọn ngày kết thúc"
    );
  }

  if (
    !isNaN(startDate.getTime()) &&
    !isNaN(endDate.getTime()) &&
    startDate >= endDate
  ) {
    isValid = false;
    markFieldInvalid(
      document.getElementById("endDate"),
      "Ngày kết thúc phải sau ngày bắt đầu"
    );
  }

  return isValid;
}

/**
 * Validate thông tin ở step 2
 */
function validateStep2() {
  const requiredFields = document.querySelectorAll("#step2 [required]");
  let isValid = true;

  requiredFields.forEach((field) => {
    if (!field.value.trim()) {
      isValid = false;
      markFieldInvalid(field, "Trường này là bắt buộc");
    } else {
      markFieldValid(field);
    }
  });

  // Lấy thông tin các ngày
  const campaignStartDate = new Date(
    document.getElementById("startDate").value
  );
  const campaignEndDate = new Date(document.getElementById("endDate").value);

  const applicationStartDate = new Date(
    document.getElementById("applicationStageStart").value
  );
  const applicationEndDate = new Date(
    document.getElementById("applicationStageEnd").value
  );

  const interviewStartDate = new Date(
    document.getElementById("interviewStageStart").value
  );
  const interviewEndDate = new Date(
    document.getElementById("interviewStageEnd").value
  );

  const challengeStartDate = new Date(
    document.getElementById("challengeStageStart").value
  );
  const challengeEndDate = new Date(
    document.getElementById("challengeStageEnd").value
  );

  // Kiểm tra các ngày có hợp lệ không
  if (
    isNaN(applicationStartDate.getTime()) ||
    isNaN(applicationEndDate.getTime()) ||
    isNaN(interviewStartDate.getTime()) ||
    isNaN(interviewEndDate.getTime()) ||
    isNaN(challengeStartDate.getTime()) ||
    isNaN(challengeEndDate.getTime())
  ) {
    isValid = false;
    showToast("Vui lòng điền đầy đủ ngày tháng cho các giai đoạn", "error");
    return isValid;
  }

  // 1. Kiểm tra từng giai đoạn - ngày bắt đầu phải trước ngày kết thúc
  if (applicationStartDate >= applicationEndDate) {
    isValid = false;
    markFieldInvalid(
      document.getElementById("applicationStageEnd"),
      "Ngày kết thúc phải sau ngày bắt đầu"
    );
  }

  if (interviewStartDate >= interviewEndDate) {
    isValid = false;
    markFieldInvalid(
      document.getElementById("interviewStageEnd"),
      "Ngày kết thúc phải sau ngày bắt đầu"
    );
  }

  if (challengeStartDate >= challengeEndDate) {
    isValid = false;
    markFieldInvalid(
      document.getElementById("challengeStageEnd"),
      "Ngày kết thúc phải sau ngày bắt đầu"
    );
  }

  // 2. Kiểm tra các giai đoạn nằm trong thời gian hoạt động
  if (
    applicationStartDate < campaignStartDate ||
    applicationEndDate > campaignEndDate
  ) {
    isValid = false;
    showToast(
      "Thời gian vòng nộp đơn phải nằm trong thời gian hoạt động",
      "error"
    );
  }

  if (
    interviewStartDate < campaignStartDate ||
    interviewEndDate > campaignEndDate
  ) {
    isValid = false;
    showToast(
      "Thời gian vòng phỏng vấn phải nằm trong thời gian hoạt động",
      "error"
    );
  }

  if (
    challengeStartDate < campaignStartDate ||
    challengeEndDate > campaignEndDate
  ) {
    isValid = false;
    showToast(
      "Thời gian vòng thử thách phải nằm trong thời gian hoạt động",
      "error"
    );
  }

  // 3. Kiểm tra chồng lấn giữa các giai đoạn
  if (
    doPeriodsOverlap(
      applicationStartDate,
      applicationEndDate,
      interviewStartDate,
      interviewEndDate
    )
  ) {
    isValid = false;
    showToast(
      "Thời gian vòng nộp đơn và vòng phỏng vấn không được chồng lấn nhau",
      "error"
    );
  }

  if (
    doPeriodsOverlap(
      applicationStartDate,
      applicationEndDate,
      challengeStartDate,
      challengeEndDate
    )
  ) {
    isValid = false;
    showToast(
      "Thời gian vòng nộp đơn và vòng thử thách không được chồng lấn nhau",
      "error"
    );
  }

  if (
    doPeriodsOverlap(
      interviewStartDate,
      interviewEndDate,
      challengeStartDate,
      challengeEndDate
    )
  ) {
    isValid = false;
    showToast(
      "Thời gian vòng phỏng vấn và vòng thử thách không được chồng lấn nhau",
      "error"
    );
  }

  //Kiểm tra thứ tự các giai đoạn - nếu muốn bắt buộc theo thứ tự
  if (applicationEndDate > interviewStartDate) {
    isValid = false;
    showToast(
      "Vòng nộp đơn phải kết thúc trước khi vòng phỏng vấn bắt đầu",
      "error"
    );
  }

  if (interviewEndDate > challengeStartDate) {
    isValid = false;
    showToast(
      "Vòng phỏng vấn phải kết thúc trước khi vòng thử thách bắt đầu",
      "error"
    );
  }

  return isValid;
}

/**
 * Kiểm tra hai khoảng thời gian có chồng lấn nhau không
 * @param {Date} start1 - Ngày bắt đầu khoảng 1
 * @param {Date} end1 - Ngày kết thúc khoảng 1
 * @param {Date} start2 - Ngày bắt đầu khoảng 2
 * @param {Date} end2 - Ngày kết thúc khoảng 2
 * @returns {boolean} - true nếu chồng lấn, false nếu không
 */
function doPeriodsOverlap(start1, end1, start2, end2) {
  // Trường hợp không chồng lấn:
  // 1. Khoảng 1 kết thúc trước khi khoảng 2 bắt đầu HOẶC
  // 2. Khoảng 2 kết thúc trước khi khoảng 1 bắt đầu
  if (end1 < start2 || end2 < start1) {
    return false;
  }
  // Các trường hợp còn lại là chồng lấn
  return true;
}

/**
 * Đánh dấu trường không hợp lệ
 */
function markFieldInvalid(field, message) {
  field.classList.add("field-error");

  // Tạo thông báo lỗi nếu chưa có
  const errorId = `error-${field.id}`;
  if (!document.getElementById(errorId)) {
    const errorMsg = document.createElement("p");
    errorMsg.id = errorId;
    errorMsg.className = "form-error-message";
    errorMsg.textContent = message;
    field.parentNode.appendChild(errorMsg);
  }
}

/**
 * Đánh dấu trường hợp lệ
 */
function markFieldValid(field) {
  field.classList.remove("field-error");

  const errorId = `error-${field.id}`;
  const errorElement = document.getElementById(errorId);
  if (errorElement) {
    errorElement.remove();
  }
}

/**
 * Thiết lập ràng buộc về ngày tháng
 */
function initializeDateConstraints() {
  // Set min date cho tất cả date inputs là ngày hiện tại
  const today = new Date();
  const todayString = today.toISOString().split("T")[0]; // Format YYYY-MM-DD

  // Campaign dates
  const startDateInput = document.getElementById("startDate");
  const endDateInput = document.getElementById("endDate");

  if (startDateInput && endDateInput) {
    // Nếu không phải ở chế độ edit, set min date
    if (document.getElementById("recruitmentForm").dataset.mode !== "edit") {
      startDateInput.min = todayString;
      endDateInput.min = todayString;
    }

    // Khi thay đổi ngày bắt đầu, cập nhật min của ngày kết thúc
    startDateInput.addEventListener("change", function () {
      endDateInput.min = this.value;

      if (
        endDateInput.value &&
        new Date(endDateInput.value) < new Date(this.value)
      ) {
        endDateInput.value = this.value;
      }

      // Cập nhật min cho các date inputs của các giai đoạn
      updateStagesDateConstraints();
    });

    // Khi thay đổi ngày kết thúc, cập nhật max của ngày bắt đầu
    endDateInput.addEventListener("change", function () {
      // Không cần cập nhật max cho startDate vì có thể hoạt động đã bắt đầu

      // Cập nhật max cho các date inputs của các giai đoạn
      updateStagesDateConstraints();
    });
  }

  // Stage dates
  updateStagesDateConstraints();
}

/**
 * Cập nhật ràng buộc ngày tháng cho các giai đoạn
 */
function updateStagesDateConstraints() {
  const campaignStartDate = document.getElementById("startDate").value;
  const campaignEndDate = document.getElementById("endDate").value;

  if (!campaignStartDate || !campaignEndDate) return;

  // Application stage
  const applicationStageStart = document.getElementById(
    "applicationStageStart"
  );
  const applicationStageEnd = document.getElementById("applicationStageEnd");

  if (applicationStageStart && applicationStageEnd) {
    applicationStageStart.min = campaignStartDate;
    applicationStageStart.max = campaignEndDate;
    applicationStageEnd.min = applicationStageStart.value || campaignStartDate;
    applicationStageEnd.max = campaignEndDate;

    applicationStageStart.addEventListener("change", function () {
      applicationStageEnd.min = this.value;

      if (
        applicationStageEnd.value &&
        new Date(applicationStageEnd.value) < new Date(this.value)
      ) {
        applicationStageEnd.value = this.value;
      }
    });
  }

  // Interview stage
  const interviewStageStart = document.getElementById("interviewStageStart");
  const interviewStageEnd = document.getElementById("interviewStageEnd");

  if (interviewStageStart && interviewStageEnd) {
    interviewStageStart.min = campaignStartDate;
    interviewStageStart.max = campaignEndDate;
    interviewStageEnd.min = interviewStageStart.value || campaignStartDate;
    interviewStageEnd.max = campaignEndDate;

    interviewStageStart.addEventListener("change", function () {
      interviewStageEnd.min = this.value;

      if (
        interviewStageEnd.value &&
        new Date(interviewStageEnd.value) < new Date(this.value)
      ) {
        interviewStageEnd.value = this.value;
      }
    });
  }

  // Challenge stage
  const challengeStageStart = document.getElementById("challengeStageStart");
  const challengeStageEnd = document.getElementById("challengeStageEnd");

  if (challengeStageStart && challengeStageEnd) {
    challengeStageStart.min = campaignStartDate;
    challengeStageStart.max = campaignEndDate;
    challengeStageEnd.min = challengeStageStart.value || campaignStartDate;
    challengeStageEnd.max = campaignEndDate;

    challengeStageStart.addEventListener("change", function () {
      challengeStageEnd.min = this.value;

      if (
        challengeStageEnd.value &&
        new Date(challengeStageEnd.value) < new Date(this.value)
      ) {
        challengeStageEnd.value = this.value;
      }
    });
  }
}

/**
 * Điền sẵn dữ liệu giai đoạn nếu ở chế độ chỉnh sửa
 */
function prefillStageData() {
  // Hiển thị debug về mode hiện tại
  showDebugInfo("Đang kiểm tra dữ liệu vòng tuyển", {
    isEdit: isEdit,
    mode: isEdit ? "Chỉnh sửa" : "Tạo mới",
    hasServerData:
      typeof stagesFromServer !== "undefined" &&
      Array.isArray(stagesFromServer) &&
      stagesFromServer.length > 0,
  });

  // Kiểm tra nếu có biến stagesFromServer đã được định nghĩa từ JSP
  if (
    typeof stagesFromServer !== "undefined" &&
    Array.isArray(stagesFromServer) &&
    stagesFromServer.length > 0
  ) {
    // Điền dữ liệu giai đoạn vào form - Sử dụng ENUM values
    stagesFromServer.forEach((stage) => {
      const stageName = String(stage.stageName).toUpperCase().trim();
      if (
        stageName === "APPLICATION" ||
        stageName.includes("APPLICATION") ||
        stageName.includes("NỘP ĐƠN")
      ) {
        document.getElementById("applicationStageStart").value =
          formatDateForInput(new Date(stage.startDate));
        document.getElementById("applicationStageEnd").value =
          formatDateForInput(new Date(stage.endDate));
      } else if (
        stageName === "INTERVIEW" ||
        stageName.includes("INTERVIEW") ||
        stageName.includes("PHỎNG VẤN")
      ) {
        document.getElementById("interviewStageStart").value =
          formatDateForInput(new Date(stage.startDate));
        document.getElementById("interviewStageEnd").value = formatDateForInput(
          new Date(stage.endDate)
        );
        if (stage.locationId) {
          document.getElementById("interviewLocationId").value =
            stage.locationId;
        }
      } else if (
        stageName === "CHALLENGE" ||
        stageName.includes("CHALLENGE") ||
        stageName.includes("THỬ THÁCH")
      ) {
        document.getElementById("challengeStageStart").value =
          formatDateForInput(new Date(stage.startDate));
        document.getElementById("challengeStageEnd").value = formatDateForInput(
          new Date(stage.endDate)
        );
      } else if (
        stageName === "INTERVIEW" ||
        stageName.includes("INTERVIEW") ||
        stageName.includes("PHỎNG VẤN")
      ) {
        document.getElementById("interviewStageStart").value =
          formatDateForInput(new Date(stage.startDate));
        document.getElementById("interviewStageEnd").value = formatDateForInput(
          new Date(stage.endDate)
        );
        if (stage.locationId) {
          document.getElementById("interviewLocationId").value =
            stage.locationId;
        }
      } else if (
        stageName === "CHALLENGE" ||
        stageName.includes("CHALLENGE") ||
        stageName.includes("THỬ THÁCH")
      ) {
        document.getElementById("challengeStageStart").value =
          formatDateForInput(new Date(stage.startDate));
        document.getElementById("challengeStageEnd").value = formatDateForInput(
          new Date(stage.endDate)
        );
        // Đã xóa xử lý mô tả của kỳ thử thách theo yêu cầu
      } else {
        console.warn(
          "[DEBUG] Không nhận diện được loại vòng tuyển:",
          stage.stageName,
          "(đã chuyển đổi thành:",
          stageName,
          ")"
        );
      }
    });

    // Hiển thị thông tin debug về dữ liệu đã nhận
    showDebugInfo(
      "Đã điền dữ liệu từ " + stagesFromServer.length + " vòng tuyển",
      stagesFromServer
    );
    return;
  }

  // Phương án dự phòng nếu không có dữ liệu từ server
  const recruitmentId = document.querySelector(
    'input[name="recruitmentId"]'
  )?.value;

  if (!recruitmentId) {
    console.error(
      "[DEBUG] Không tìm thấy ID hoạt động tuyển quân để lấy dữ liệu giai đoạn"
    );
    showDebugInfo(
      "Lỗi",
      "Không tìm thấy ID hoạt động tuyển quân để lấy dữ liệu giai đoạn"
    );
    return;
  }
  showDebugInfo("Đang tải dữ liệu vòng tuyển qua API...", {
    recruitmentId: recruitmentId,
  });

  // Lấy dữ liệu các giai đoạn từ server qua API
  fetch(`${getContextPath()}/recruitment/stages?recruitmentId=${recruitmentId}`)
    .then((response) => {
      if (!response.ok) {
        throw new Error("Lỗi khi lấy dữ liệu giai đoạn");
      }
      return response.json();
    })
    .then((data) => {
      if (data && data.success && data.stages && Array.isArray(data.stages)) {
        // Điền dữ liệu giai đoạn vào form - Sử dụng ENUM values
        data.stages.forEach((stage) => {
          const stageName = String(stage.stageName).toUpperCase().trim();
          if (
            stageName === "APPLICATION" ||
            stageName.includes("APPLICATION") ||
            stageName.includes("NỘP ĐƠN")
          ) {
            document.getElementById("applicationStageStart").value =
              formatDateForInput(new Date(stage.startDate));
            document.getElementById("applicationStageEnd").value =
              formatDateForInput(new Date(stage.endDate));
          } else if (
            stageName === "INTERVIEW" ||
            stageName.includes("INTERVIEW") ||
            stageName.includes("PHỎNG VẤN")
          ) {
            document.getElementById("interviewStageStart").value =
              formatDateForInput(new Date(stage.startDate));
            document.getElementById("interviewStageEnd").value =
              formatDateForInput(new Date(stage.endDate));
            if (stage.locationID || stage.locationId) {
              document.getElementById("interviewLocationId").value =
                stage.locationID || stage.locationId;
            }
          } else if (
            stageName === "CHALLENGE" ||
            stageName.includes("CHALLENGE") ||
            stageName.includes("THỬ THÁCH")
          ) {
            document.getElementById("challengeStageStart").value =
              formatDateForInput(new Date(stage.startDate));
            document.getElementById("challengeStageEnd").value =
              formatDateForInput(new Date(stage.endDate));
          } else {
            console.warn(
              "[DEBUG] API - Không nhận diện được loại vòng tuyển:",
              stage.stageName,
              "(đã chuyển đổi thành:",
              stageName,
              ")"
            );
          }
        });

        // Hiển thị thông tin debug
        showDebugInfo(
          "Đã lấy dữ liệu từ API: " + data.stages.length + " vòng tuyển",
          data
        );
      } else {
        console.error("Dữ liệu giai đoạn không hợp lệ:", data);
        showDebugInfo(
          "Lỗi dữ liệu: Không lấy được dữ liệu vòng tuyển từ API",
          data
        );
      }
    })
    .catch((error) => {
      console.error("Lỗi khi lấy dữ liệu giai đoạn:", error);
      showDebugInfo("Lỗi khi lấy dữ liệu giai đoạn: " + error.message);
    });
}

/**
 * Format date để hiển thị trong input type="date"
 */
function formatDateForInput(date) {
  if (!date || isNaN(date.getTime())) return "";
  return date.toISOString().split("T")[0]; // Format YYYY-MM-DD
}

/**
 * Điền thông tin vào màn hình xác nhận
 */
function populateConfirmationStep() {
  try {
    // Hàm bổ trợ kiểm tra phần tử an toàn
    function setConfirmText(elementId, value) {
      const element = document.getElementById(elementId);
      if (element) {
        element.textContent = value || "";
      } else {
        console.error(
          `Không tìm thấy phần tử '${elementId}' để hiển thị kết quả`
        );
      }
    }

    // Hàm lấy giá trị an toàn từ phần tử input
    function getSafeElementValue(id) {
      const element = document.getElementById(id);
      return element && element.value ? element.value : null;
    }

    // Thông tin cơ bản
    const genValue = getSafeElementValue("gen");
    if (genValue) {
      setConfirmText("confirmGen", genValue);
    } else {
      console.error("Không tìm thấy phần tử 'gen' hoặc không có giá trị");
      setConfirmText("confirmGen", "Không có thông tin");
    }

    const startDateValue = getSafeElementValue("startDate");
    const endDateValue = getSafeElementValue("endDate");
    if (startDateValue && endDateValue) {
      const startDate = new Date(startDateValue);
      const endDate = new Date(endDateValue);
      if (!isNaN(startDate.getTime()) && !isNaN(endDate.getTime())) {
        setConfirmText(
          "confirmTime",
          `${formatDate(startDate)} - ${formatDate(endDate)}`
        );
      } else {
        setConfirmText("confirmTime", "Ngày không hợp lệ");
      }
    } else {
      console.error(
        "Không tìm thấy phần tử 'startDate' hoặc 'endDate', hoặc chúng không có giá trị"
      );
      setConfirmText("confirmTime", "Không có thông tin");
    }

    const formSelect = document.getElementById("formId");
    if (
      formSelect &&
      formSelect.selectedIndex >= 0 &&
      formSelect.options &&
      formSelect.selectedIndex < formSelect.options.length
    ) {
      const selectedForm = formSelect.options[formSelect.selectedIndex].text;
      setConfirmText("confirmForm", selectedForm);
    } else {
      console.error("Không tìm thấy phần tử 'formId' hoặc chưa chọn mẫu đơn");
      setConfirmText("confirmForm", "Không có thông tin");
    }

    // Vòng nộp đơn - APPLICATION
    const appStartValue = getSafeElementValue("applicationStageStart");
    const appEndValue = getSafeElementValue("applicationStageEnd");
    if (appStartValue && appEndValue) {
      const appStartDate = new Date(appStartValue);
      const appEndDate = new Date(appEndValue);
      if (!isNaN(appStartDate.getTime()) && !isNaN(appEndDate.getTime())) {
        setConfirmText(
          "confirmApplicationStage",
          `${formatDate(appStartDate)} - ${formatDate(appEndDate)}`
        );
      } else {
        setConfirmText("confirmApplicationStage", "Ngày không hợp lệ");
      }
    } else {
      console.error(
        "Không tìm thấy phần tử 'applicationStageStart' hoặc 'applicationStageEnd', hoặc chúng không có giá trị"
      );
      setConfirmText("confirmApplicationStage", "Không có thông tin");
    }

    // Vòng phỏng vấn - INTERVIEW
    const intStartValue = getSafeElementValue("interviewStageStart");
    const intEndValue = getSafeElementValue("interviewStageEnd");
    const locationSelect = document.getElementById("interviewLocationId");

    if (intStartValue && intEndValue) {
      const intStartDate = new Date(intStartValue);
      const intEndDate = new Date(intEndValue);
      if (!isNaN(intStartDate.getTime()) && !isNaN(intEndDate.getTime())) {
        let locationText = "";

        if (
          locationSelect &&
          locationSelect.selectedIndex > 0 &&
          locationSelect.options &&
          locationSelect.selectedIndex < locationSelect.options.length
        ) {
          locationText = ` (${
            locationSelect.options[locationSelect.selectedIndex].text
          })`;
        }

        setConfirmText(
          "confirmInterviewStage",
          `${formatDate(intStartDate)} - ${formatDate(
            intEndDate
          )}${locationText}`
        );
      } else {
        setConfirmText("confirmInterviewStage", "Ngày không hợp lệ");
      }
    } else {
      console.error(
        "Không tìm thấy phần tử 'interviewStageStart' hoặc 'interviewStageEnd', hoặc chúng không có giá trị"
      );
      setConfirmText("confirmInterviewStage", "Không có thông tin");
    }

    // Vòng thử thách - CHALLENGE
    const chalStartValue = getSafeElementValue("challengeStageStart");
    const chalEndValue = getSafeElementValue("challengeStageEnd");

    if (chalStartValue && chalEndValue) {
      const chalStartDate = new Date(chalStartValue);
      const chalEndDate = new Date(chalEndValue);
      if (!isNaN(chalStartDate.getTime()) && !isNaN(chalEndDate.getTime())) {
        let challengeText = `${formatDate(chalStartDate)} - ${formatDate(
          chalEndDate
        )}`;

        setConfirmText("confirmChallengeStage", challengeText);
      } else {
        setConfirmText("confirmChallengeStage", "Ngày không hợp lệ");
      }
    } else {
      console.error(
        "Không tìm thấy phần tử 'challengeStageStart' hoặc 'challengeStageEnd', hoặc chúng không có giá trị"
      );
      setConfirmText("confirmChallengeStage", "Không có thông tin (tùy chọn)");
    }
  } catch (error) {
    console.error("Lỗi khi điền thông tin vào màn hình xác nhận:", error);

    // Hiển thị debug panel với thông tin lỗi
    showDebugInfo("Lỗi khi điền thông tin xác nhận", {
      error: error.message,
      stack: error.stack,
      stageData: collectStageData(),
    });
  }
}

// Hàm bổ sung để thu thập dữ liệu vòng tuyển
function collectStageData() {
  const stageData = {
    application: {
      start: document.getElementById("applicationStageStart")?.value || null,
      end: document.getElementById("applicationStageEnd")?.value || null,
      stageName: "APPLICATION", // Đảm bảo sử dụng đúng ENUM value
    },
    interview: {
      start: document.getElementById("interviewStageStart")?.value || null,
      end: document.getElementById("interviewStageEnd")?.value || null,
      locationId: document.getElementById("interviewLocationId")?.value || null,
      stageName: "INTERVIEW", // Đảm bảo sử dụng đúng ENUM value
    },
    challenge: {
      start: document.getElementById("challengeStageStart")?.value || null,
      end: document.getElementById("challengeStageEnd")?.value || null,
      stageName: "CHALLENGE", // Đảm bảo sử dụng đúng ENUM value
    },
  };
  return stageData;
}

/**
 * Format date từ ISO string sang định dạng ngày/tháng/năm
 */
function formatDate(date) {
  return `${date.getDate().toString().padStart(2, "0")}/${(date.getMonth() + 1)
    .toString()
    .padStart(2, "0")}/${date.getFullYear()}`;
}

/**
 * Thiết lập xử lý khi submit form
 */
function setupFormSubmission() {
  const form = document.getElementById("recruitmentForm");

  if (!form) {
    console.error("Không tìm thấy form với id 'recruitmentForm'");
    return;
  }

  form.addEventListener("submit", function (e) {
    e.preventDefault();
    // Xác thực form trước khi gửi
    if (!validateEntireForm()) {
      showToast("Vui lòng điền đầy đủ thông tin cần thiết", "error");
      console.error("Validation thất bại");
      return;
    }

    // Đảm bảo clubId từ URL luôn được thêm vào form trước khi lấy dữ liệu
    const formUrlParams = new URLSearchParams(window.location.search);
    const formUrlClubId = formUrlParams.get("clubId");
    if (formUrlClubId) {
      // Thêm input ẩn nếu chưa có
      let hiddenClubIdInput = document.querySelector('input[name="clubId"]');
      if (!hiddenClubIdInput) {
        hiddenClubIdInput = document.createElement("input");
        hiddenClubIdInput.type = "hidden";
        hiddenClubIdInput.name = "clubId";
        form.appendChild(hiddenClubIdInput);
      }
      hiddenClubIdInput.value = formUrlClubId;
    }

    const formData = new FormData(form);

    // Kiểm tra xem có clubId không, và nếu không có thì thử lấy từ các nguồn khác
    if (!formData.has("clubId") || formData.get("clubId") === "") {
      // Lấy từ URL
      const urlParams = new URLSearchParams(window.location.search);
      const urlClubId = urlParams.get("clubId");
      if (urlClubId) {
        formData.set("clubId", urlClubId);
      }
    }

    // Kiểm tra các field quan trọng
    const requiredParams = [
      "clubId",
      "gen",
      "formId",
      "startDate",
      "endDate",
      "title",
      "applicationStageStart",
      "applicationStageEnd",
      "interviewStageStart",
      "interviewStageEnd",
      "challengeStageStart",
      "challengeStageEnd",
    ];
    let missingParams = [];

    requiredParams.forEach((param) => {
      const value = formData.get(param);
      if (!value || value.trim() === "") {
        missingParams.push(param);
        console.error(`Thiếu tham số: ${param}`);
      }
    });

    if (missingParams.length > 0) {
      const errorMsg = "Thiếu thông tin bắt buộc: " + missingParams.join(", ");
      showToast(errorMsg, "error");

      // Hiển thị thông tin lỗi khi thiếu dữ liệu
      if (missingParams.includes("clubId")) {
        showDebugInfo("Lỗi thiếu thông tin CLB", {
          missingParams: missingParams,
        });
      }
      return;
    }

    // URL endpoint tùy thuộc vào mode tạo mới hay chỉnh sửa
    const url = isEdit
      ? `${contextPath}/recruitmentForm/update`
      : `${contextPath}/recruitmentForm/create`;

    // Đảm bảo clubId tồn tại trong formData
    const submitUrlParams = new URLSearchParams(window.location.search);
    const submitUrlClubId = submitUrlParams.get("clubId");

    // Kiểm tra và bổ sung clubId từ URL nếu không có trong form
    if (
      (!formData.get("clubId") || formData.get("clubId") === "") &&
      submitUrlClubId
    ) {
      formData.set("clubId", submitUrlClubId);
    }

    // Tạo URLSearchParams từ formData để gửi lên server
    const requestParams = new URLSearchParams();

    // Thêm tất cả các tham số từ formData vào requestParams
    for (let pair of formData.entries()) {
      requestParams.append(pair[0], pair[1]);
    }

    // Xử lý đặc biệt cho formId để tránh trùng lặp
    const formSelect = document.getElementById("formId");
    if (formSelect) {
      // Đảm bảo trạng thái nhất quán trước khi lấy giá trị
      manageFormIdConsistency();

      // Xóa tất cả entry formId hiện có trong requestParams trước
      requestParams.delete("formId");

      if (formSelect.disabled) {
        // Nếu disabled, lấy từ hidden input
        const hiddenFormInput = formSelect.parentNode.querySelector(
          'input[type="hidden"][name="formId"]'
        );
        if (hiddenFormInput && hiddenFormInput.value) {
          requestParams.set("formId", hiddenFormInput.value);
        } else if (formSelect.value) {
          // Fallback nếu không có hidden input
          requestParams.set("formId", formSelect.value);
        }
      } else {
        // Nếu enabled, lấy từ select element
        if (formSelect.value) {
          requestParams.set("formId", formSelect.value);
        }
      }
    }

    // Thu thập tất cả các hidden inputs để đảm bảo chúng được đưa vào request (trừ formId đã xử lý)
    const hiddenInputs = document.querySelectorAll('input[type="hidden"]');
    hiddenInputs.forEach((input) => {
      if (input.name && input.value && input.name !== "formId") {
        requestParams.set(input.name, input.value);
      }
    });

    // Đảm bảo các trường bị disabled vẫn được thêm vào trong requestParams (trừ formId đã xử lý)
    criticalFields.forEach((fieldId) => {
      const field = document.getElementById(fieldId);
      if (field && field.disabled && field.value && fieldId !== "formId") {
        requestParams.set(fieldId, field.value);
      }
    });

    // Kiểm tra các tham số cần thiết trước khi gửi
    const checkRequiredParams = [
      "applicationStageStart",
      "applicationStageEnd",
      "interviewStageStart",
      "interviewStageEnd",
      "challengeStageStart",
      "challengeStageEnd",
    ];

    checkRequiredParams.forEach((param) => {
      // Tìm cả hidden input và field thông thường
      const field = document.getElementById(param);
      const hiddenInput = document.querySelector(
        `input[type="hidden"][name="${param}"]`
      );

      // Nếu field tồn tại và có giá trị, thêm vào request
      if (field && field.value && !requestParams.has(param)) {
        requestParams.set(param, field.value);
      }
      // Nếu hidden input tồn tại và có giá trị, thêm vào request
      else if (hiddenInput && hiddenInput.value && !requestParams.has(param)) {
        requestParams.set(param, hiddenInput.value);
      }
    });
    // Kiểm tra cuối cùng formId
    if (!requestParams.has("formId") || !requestParams.get("formId")) {
      console.error("[ERROR] formId bị thiếu trong requestParams!");
      showToast("Thiếu thông tin mẫu đơn đăng ký", "error");
      return;
    } else {
      console.log(
        `[DEBUG] formId cuối cùng sẽ gửi: ${requestParams.get("formId")}`
      );
    }

    // Nếu đang chỉnh sửa, kiểm tra ID
    if (isEdit) {
      const recruitmentIdInput = document.querySelector(
        'input[name="recruitmentId"]'
      );
      if (
        !recruitmentIdInput ||
        !recruitmentIdInput.value ||
        recruitmentIdInput.value === "0"
      ) {
        console.error("Thiếu recruitmentId trong form khi chỉnh sửa");
        showToast("Không tìm thấy ID hoạt động để cập nhật", "error");

        // Hiển thị thông tin debug
        showDebugInfo("Lỗi: Thiếu recruitmentId", {
          recruitmentIdInput: recruitmentIdInput
            ? recruitmentIdInput.value
            : "không tìm thấy input",
          formMode: "edit",
          allHiddenInputs: Array.from(
            document.querySelectorAll('input[type="hidden"]')
          ).map((input) => ({
            name: input.name,
            value: input.value,
          })),
        });
        return;
      }

      // Đảm bảo recruitmentId được thêm vào requestParams
      requestParams.set("recruitmentId", recruitmentIdInput.value);
    }

    // Đảm bảo URL endpoint chính xác và tham số
    const hasValidId =
      requestParams.get("recruitmentId") &&
      requestParams.get("recruitmentId") !== "0";
    const finalUrl =
      isEdit && hasValidId
        ? `${contextPath}/recruitmentForm/update`
        : `${contextPath}/recruitmentForm/create`;

    // Chỉ gửi recruitmentId khi cập nhật để tránh trùng lặp tham số với id
    if (isEdit && hasValidId) {
      const recruitmentIdValue = requestParams.get("recruitmentId");
      if (recruitmentIdValue) {
        // Loại bỏ tham số "id" vì servlet RecruitmentFormServlet.handleUpdateCampaign đã kiểm tra và ưu tiên recruitmentId
        requestParams.delete("id");
      }
    }

    // Double check: đảm bảo các trường quan trọng luôn được thêm vào dù có bị disabled hay không
    criticalFields.forEach((fieldName) => {
      const field = document.getElementById(fieldName);
      if (field && field.value && !requestParams.has(fieldName)) {
        requestParams.set(fieldName, field.value);
      } else if (field && field.value) {
      }
    });

    // Submit form data bằng fetch API với timeout
    const fetchPromise = fetch(finalUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: requestParams.toString(),
    });

    // Disable submit button to prevent double submission
    document.getElementById("submitBtn").disabled = true;
    document.getElementById("submitBtn").innerHTML = isEdit
      ? '<i class="fas fa-spinner fa-spin"></i> Đang cập nhật...'
      : '<i class="fas fa-spinner fa-spin"></i> Đang tạo...';

    // Thêm timeout để tránh trường hợp request bị treo
    const timeoutPromise = new Promise((_, reject) =>
      setTimeout(() => reject(new Error("Request timeout sau 10 giây")), 10000)
    );

    Promise.race([fetchPromise, timeoutPromise])
      .then((response) => {
        // Kiểm tra response status trước khi parse JSON
        if (!response.ok) {
          // Lấy text của response để hiểu rõ lỗi
          return response.text().then((text) => {
            console.error(`[ERROR] Server response: ${text}`);
            throw new Error(
              `HTTP error! Status: ${
                response.status
              }, Message: ${text.substring(0, 100)}...`
            );
          });
        }

        // Thêm kiểm tra content-type
        const contentType = response.headers.get("content-type");
        if (!contentType || !contentType.includes("application/json")) {
          return response.text().then((text) => {
            console.error(`[ERROR] Non-JSON response: ${text}`);
            throw new Error("Phản hồi không phải dạng JSON");
          });
        }

        // Parse JSON
        return response.json();
      })
      .then((data) => {
        // Re-enable the button in any case
        document.getElementById("submitBtn").disabled = false;
        document.getElementById("submitBtn").innerHTML = isEdit
          ? "Cập nhật hoạt động"
          : "Tạo hoạt động";

        if (data.success === true) {
          showToast(data.message || "Thao tác thành công!", "success");

          // Xóa dữ liệu Local Storage sau khi submit thành công
          window.formFields.forEach(field => {
            if (field.id) {
              localStorage.removeItem(field.id);
            }
          });
          setTimeout(() => {
            // Chuyển hướng về trang quản lý hoạt động tuyển quân
            const clubId = submitUrlClubId || data.clubId;
            // Kiểm tra clubId có hợp lệ không
            if (!clubId || clubId === "undefined" || clubId === "null") {
              console.error("ClubId không hợp lệ, chuyển hướng về trang chính");
              window.location.href = contextPath
                ? `${contextPath}/recruitment`
                : "/swp391-clubs-management/recruitment";
              return;
            }

            if (!contextPath || contextPath === "") {
              window.location.href = `/swp391-clubs-management/recruitment/list?clubId=${clubId}`;
            } else {
              window.location.href = `${contextPath}/recruitment/list?clubId=${clubId}`;
            }
          }, 1500);
        } else {
          // Show error message
          showToast(data.message || "Đã có lỗi xảy ra!", "error");

          // Enable detailed logging when error occurs
          if (data.errors) {
            showDebugInfo("Chi tiết lỗi", data.errors);
          }
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        document.getElementById("submitBtn").disabled = false;
        document.getElementById("submitBtn").innerHTML = isEdit
          ? "Cập nhật hoạt động"
          : "Tạo hoạt động";

        // Xử lý các loại lỗi khác nhau
        let errorMessage = error.message;

        if (errorMessage.includes("timeout")) {
          errorMessage = "Hết thời gian phản hồi từ server. Vui lòng thử lại.";
        } else if (
          errorMessage.includes("không thể thay đổi vì đã có người nộp đơn")
        ) {
          errorMessage = "Mẫu đơn không thể thay đổi vì đã có người nộp đơn.";
        } else if (errorMessage.includes("trùng lịch")) {
          errorMessage =
            "Không thể cập nhật hoạt động do trùng lịch với hoạt động khác.";
        } else if (errorMessage.includes("đang diễn ra")) {
          errorMessage =
            "Không thể thay đổi thời gian của hoạt động đang diễn ra.";
        } else if (errorMessage.includes("Network response was not ok")) {
          errorMessage =
            "Lỗi kết nối mạng. Vui lòng kiểm tra kết nối internet.";
        } else if (errorMessage.includes("JSON")) {
          errorMessage = "Lỗi xử lý dữ liệu từ server. Vui lòng thử lại.";
        }

        showToast("Đã có lỗi xảy ra: " + errorMessage, "error");
      });
  });

  // Reset form validation khi nhập lại
  const inputs = form.querySelectorAll("input, textarea, select");
  inputs.forEach((input) => {
    input.addEventListener("input", function () {
      markFieldValid(this);
    });
  });
}

/**
 * Kiểm tra tất cả các trường dữ liệu trong form
 * @returns {boolean} true nếu form hợp lệ
 */
function validateEntireForm() {
  return validateStep1() && validateStep2();
}

/**
 * Tự động chọn form đăng ký nếu chỉ có một lựa chọn
 */
function autoSelectSingleform() {
  const formSelect = document.getElementById("formId");
  if (formSelect && formSelect.options.length === 2) {
    // Có 1 option thật + 1 option placeholder "Chọn form đăng ký"
    formSelect.selectedIndex = 1; // Chọn option thứ 2 (index = 1)
  }
}

/**
 * Kiểm tra xem một vòng tuyển đã bắt đầu chưa dựa trên ngày hiện tại
 * @param {Date} startDate Ngày bắt đầu của vòng tuyển
 * @returns {boolean} True nếu vòng tuyển đã bắt đầu, ngược lại là false
 */
function hasStageStarted(startDate) {
  const currentDate = new Date();
  // Xóa phần thời gian để so sánh ngày chính xác
  currentDate.setHours(0, 0, 0, 0);

  // Tạo một đối tượng ngày mới từ startDate để tránh các vấn đề tham chiếu
  const stageStartDate = new Date(startDate);
  stageStartDate.setHours(0, 0, 0, 0);

  // Nếu ngày bắt đầu vòng tuyển là hôm nay hoặc trước đó, thì nó đã bắt đầu
  return stageStartDate <= currentDate;
}

/**
 * Kiểm tra xem một chiến dịch đã có đơn đăng ký nào được nộp chưa
 */
function hasCampaignApplications() {
  const hasApplicationsInput = document.querySelector(
    'input[name="hasApplications"]'
  );
  if (!hasApplicationsInput) {
    console.warn(
      "[DEBUG] Không tìm thấy input hasApplications, mặc định không có đơn đăng ký."
    );
    return false;
  }

  const hasApplications = hasApplicationsInput.value === "true";
  return hasApplications;
}

/**
 * Vô hiệu hóa các trường không được phép thay đổi dựa trên trạng thái chiến dịch
 */
function setupFieldRestrictions() {
  if (!isEdit) {
    return; // Không có hạn chế nào trong chế độ tạo mới
  }


  // 0. Kiểm tra ngày bắt đầu chiến dịch - vô hiệu hóa nếu chiến dịch đang diễn ra
  const campaignStartDateField = document.getElementById("startDate");
  if (campaignStartDateField && campaignStartDateField.value) {
    const campaignStartDate = new Date(campaignStartDateField.value);
    const currentDate = new Date();

    // Xóa phần thời gian để so sánh ngày chính xác
    currentDate.setHours(0, 0, 0, 0);
    campaignStartDate.setHours(0, 0, 0, 0);

    if (campaignStartDate <= currentDate) {

      // Lưu lại giá trị ban đầu
      const originalStartDateValue = campaignStartDateField.value;

      // Xóa hidden input và warning message nếu đã tồn tại trước đó
      const existingHiddenInput =
        campaignStartDateField.parentNode.querySelector(
          'input[type="hidden"][name="startDate"]'
        );
      if (existingHiddenInput) {
        campaignStartDateField.parentNode.removeChild(existingHiddenInput);
      }

      const existingWarning =
        campaignStartDateField.parentNode.querySelector(".text-warning");
      if (existingWarning) {
        campaignStartDateField.parentNode.removeChild(existingWarning);
      }

      campaignStartDateField.disabled = true;
      // Thêm ghi chú giải thích
      const noteElement = document.createElement("div");
      noteElement.className = "text-warning mt-1";
      noteElement.innerHTML =
        '<small><i class="fas fa-info-circle"></i> Ngày bắt đầu không thể thay đổi khi hoạt động đã bắt đầu.</small>';
      campaignStartDateField.parentNode.appendChild(noteElement);

      // Thêm hidden input để đảm bảo giá trị vẫn được gửi đi
      const hiddenInput = document.createElement("input");
      hiddenInput.type = "hidden";
      hiddenInput.name = "startDate";
      hiddenInput.value = originalStartDateValue;
      campaignStartDateField.parentNode.appendChild(hiddenInput);
    }
  }

  // 1. Vô hiệu hóa lựa chọn mẫu đơn nếu đã có người đăng ký
  const formSelect = document.getElementById("formId");
  if (formSelect) {
    // Kiểm tra đúng trạng thái của đơn đăng ký
    const hasApplications = hasCampaignApplications();

    // Lưu lại giá trị ban đầu để đảm bảo không bị mất
    const originalformValue = formSelect.value;

    // Xóa hidden input và warning message nếu đã tồn tại trước đó
    const existingHiddenInput = formSelect.parentNode.querySelector(
      'input[type="hidden"][name="formId"]'
    );
    if (existingHiddenInput) {
      formSelect.parentNode.removeChild(existingHiddenInput);
    }

    const existingWarning =
      formSelect.parentNode.querySelector(".text-warning");
    if (existingWarning) {
      formSelect.parentNode.removeChild(existingWarning);
    }

    if (hasApplications) {
      formSelect.disabled = true;
      // Thêm ghi chú giải thích tại sao nó bị vô hiệu hóa
      const noteElement = document.createElement("div");
      noteElement.className = "text-warning mt-1";
      noteElement.innerHTML =
        '<small><i class="fas fa-info-circle"></i> Mẫu đơn không thể thay đổi khi đã có người nộp đơn.</small>';
      formSelect.parentNode.appendChild(noteElement);

      // Chỉ thêm hidden input khi disabled
      const hiddenformInput = document.createElement("input");
      hiddenformInput.type = "hidden";
      hiddenformInput.name = "formId";
      hiddenformInput.value = originalformValue;
      formSelect.parentNode.appendChild(hiddenformInput);

      console.log(
        `[DEBUG] Đã thêm hidden input cho formId với giá trị: ${originalformValue} (disabled)`
      );
    } else {
      console.log("Mẫu đơn có thể thay đổi vì chưa có đơn đăng ký");
      formSelect.disabled = false;
    }

    // Thêm event listener để xử lý khi người dùng thay đổi form (cho cả enabled và disabled)
    formSelect.addEventListener("change", function () {
      console.log(`[DEBUG] form đã thay đổi thành: ${this.value}`);
      // Sử dụng hàm helper để quản lý nhất quán
      manageFormIdConsistency();
    });

    // Gọi hàm helper để đảm bảo trạng thái ban đầu nhất quán
    manageFormIdConsistency();
  }

  // 2. Nếu vòng tuyển đã bắt đầu, vô hiệu hóa trường ngày bắt đầu
  if (
    typeof stagesFromServer !== "undefined" &&
    Array.isArray(stagesFromServer) &&
    stagesFromServer.length > 0
  ) {
    console.log("Checking stage dates for restrictions");

    // Tạo một bản sao của stagesFromServer để tránh các vấn đề tham chiếu
    const stages = [...stagesFromServer];

    stages.forEach((stage) => {
      const stageName = String(stage.stageName || "").toUpperCase();
      const stageStartDate = new Date(stage.startDate);

      // Nếu vòng tuyển đã bắt đầu, vô hiệu hóa trường ngày bắt đầu
      if (hasStageStarted(stageStartDate)) {
        console.log(
          `Vòng ${stageName} đã bắt đầu, vô hiệu hóa trường ngày bắt đầu`
        );

        let startDateFieldId;
        switch (stageName) {
          case "APPLICATION":
            startDateFieldId = "applicationStageStart";
            break;
          case "INTERVIEW":
            startDateFieldId = "interviewStageStart";
            break;
          case "CHALLENGE":
            startDateFieldId = "challengeStageStart";
            break;
          default:
            console.log(`Tên vòng tuyển không xác định: ${stageName}`);
            return;
        }

        const startDateField = document.getElementById(startDateFieldId);
        if (startDateField) {
          // Lưu lại giá trị ban đầu
          const originalDateValue = startDateField.value;

          // Xóa hidden input và warning message nếu đã tồn tại trước đó
          const existingHiddenInput = startDateField.parentNode.querySelector(
            `input[type="hidden"][name="${startDateFieldId}"]`
          );
          if (existingHiddenInput) {
            startDateField.parentNode.removeChild(existingHiddenInput);
          }

          const existingWarning =
            startDateField.parentNode.querySelector(".text-warning");
          if (existingWarning) {
            startDateField.parentNode.removeChild(existingWarning);
          }

          startDateField.disabled = true;
          // Thêm ghi chú giải thích tại sao nó bị vô hiệu hóa
          const noteElement = document.createElement("div");
          noteElement.className = "text-warning mt-1";
          noteElement.innerHTML =
            '<small><i class="fas fa-info-circle"></i> Ngày bắt đầu không thể thay đổi khi vòng đã diễn ra.</small>';
          startDateField.parentNode.appendChild(noteElement);

          // Thêm hidden input để đảm bảo giá trị vẫn được gửi đi
          const hiddenInput = document.createElement("input");
          hiddenInput.type = "hidden";
          hiddenInput.name = startDateFieldId;
          hiddenInput.value = originalDateValue;
          startDateField.parentNode.appendChild(hiddenInput);

          console.log(
            `[DEBUG] Đã thêm hidden input cho ${startDateFieldId} với giá trị: ${originalDateValue}`
          );
        }
      }
    });
  } else {
    console.log("Không có dữ liệu về các vòng tuyển từ server");
  }
}

/**
 * Debug function to check all disabled fields and their values
 * This helps identify which fields need to be manually included in form submission
 */
function debugDisabledFields() {
  console.log("[DEBUG] Checking disabled fields:");

  // Check all form elements
  const formElements = document.querySelectorAll(
    "#recruitmentForm input, #recruitmentForm select, #recruitmentForm textarea"
  );

  let disabledFields = [];

  formElements.forEach((field) => {
    if (field.disabled) {
      disabledFields.push({
        name: field.name,
        id: field.id,
        value: field.value,
        type: field.type,
        tagName: field.tagName,
      });
      console.log(
        `[DEBUG] Found disabled field: ${field.name || field.id}, value: ${
          field.value
        }`
      );

      // Check if there's a hidden input with the same name
      const hiddenInput = field.parentNode.querySelector(
        `input[type="hidden"][name="${field.name}"]`
      );
      if (hiddenInput) {
        console.log(
          `[DEBUG] Hidden input found for ${field.name}: ${hiddenInput.value}`
        );
      } else {
        console.log(`[DEBUG] No hidden input found for ${field.name}`);
      }
    }
  });

  if (disabledFields.length === 0) {
    console.log("[DEBUG] No disabled fields found.");
  }

  // Check critical fields
  console.log("[DEBUG] Checking critical fields:");
  criticalFields.forEach((fieldName) => {
    const field = document.getElementById(fieldName);
    if (field) {
      console.log(
        `[DEBUG] Critical field ${fieldName}: value=${field.value}, disabled=${field.disabled}`
      );
    } else {
      console.log(`[DEBUG] Critical field ${fieldName} not found in DOM`);
    }
  });

  return disabledFields;
}

/**
 * Helper function để quản lý formId một cách nhất quán
 * Đảm bảo không có xung đột giữa select element và hidden input
 */
function manageFormIdConsistency() {
  const formSelect = document.getElementById("formId");
  if (!formSelect) return;

  const hiddenformInput = formSelect.parentNode.querySelector(
    'input[type="hidden"][name="formId"]'
  );

  if (formSelect.disabled) {
    // Nếu select bị disabled, đảm bảo có hidden input với giá trị đúng
    if (!hiddenformInput) {
      const newHiddenInput = document.createElement("input");
      newHiddenInput.type = "hidden";
      newHiddenInput.name = "formId";
      newHiddenInput.value = formSelect.value;
      formSelect.parentNode.appendChild(newHiddenInput);
    } else {
      // Cập nhật giá trị hidden input
      hiddenformInput.value = formSelect.value;
    }
  } else {
    // Nếu select không bị disabled, loại bỏ hidden input nếu có
    if (hiddenformInput) {
      formSelect.parentNode.removeChild(hiddenformInput);
    }
  }
}
