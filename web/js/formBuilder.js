let optionTemplateHtml;
let questionTemplateHtml;
document.addEventListener("DOMContentLoaded", () => {
  let questionCount = 0;
  const MAX_QUESTIONS = 20;
  const MAX_TITLE_LENGTH = 20;
  let activeTab = "edit";

  const formTitleInput = document.getElementById("formTitle");
  const formTypeSelect = document.getElementById("formType");
  const questionsList = document.getElementById("questionsList");
  const addQuestionBtn = document.getElementById("addQuestionBtn");
  const addInfoBtn = document.getElementById("addInfoBtn");
  const saveBtn = document.getElementById("saveBtn");
  const publishBtn = document.getElementById("publishBtn");
  const publishDialog = document.getElementById("publishDialog");
  const confirmPublishBtn = document.getElementById("confirmPublishBtn");
  const saveSuccessAlert = document.getElementById("saveSuccessAlert");
  const saveErrorAlert = document.getElementById("saveErrorAlert");
  const maxQuestionsAlert = document.getElementById("maxQuestionsAlert");
  const questionCountElement = document.getElementById("questionCount");
  const tabItems = document.querySelectorAll(".tab-item");
  const tabContents = document.querySelectorAll(".tab-content");
  const previewTitle = document.getElementById("previewTitle");
  const previewForm = document.getElementById("previewForm");

  const formBuilderForm = document.getElementById("formBuilderForm");
  const actionInput = document.getElementById("action");
  const formTitleHidden = document.getElementById("formTitleHidden");
  const formTypeHidden = document.getElementById("formTypeHidden");
  const questionsHidden = document.getElementById("questionsHidden");
  const editingTemplateIdInput = document.querySelector(
    'input[name="editingTemplateId"]'
  );
  const editingFormId = document.querySelector('input[name="editingFormId"]');

  questionTemplateHtml = document.getElementById("questionTemplate")?.innerHTML;
  optionTemplateHtml = document.getElementById("optionTemplate")?.innerHTML;
  if (
    window.existingQuestions &&
    Array.isArray(window.existingQuestions) &&
    window.existingQuestions.length > 0
  ) {
    if (questionsList) {
      while (questionsList.firstChild) {
        questionsList.removeChild(questionsList.firstChild);
      }
    } else {
      console.error("formBuilder.js: Element #questionsList không tìm thấy.");
      return;
    }

    questionCount = 0;
    window.existingQuestions.forEach((qData, index) => {
      try {
        const questionCard = createQuestionCard(qData, index + 1);
        if (questionCard) {
          questionsList.appendChild(questionCard);
          addQuestionEventListeners(questionCard);
          questionCount++;
        } else {
          console.error(
            "formBuilder.js: createQuestionCard trả về null cho dữ liệu:",
            JSON.parse(JSON.stringify(qData))
          );
        }
      } catch (e) {
        console.error(
          "formBuilder.js: Lỗi khi tạo/thêm card cho câu hỏi đã có:",
          JSON.parse(JSON.stringify(qData)),
          e
        );
      }
    });
  } else {
    if (questionsList) {
      const defaultQuestionCards =
        questionsList.querySelectorAll(".question-card");
      questionCount = defaultQuestionCards.length;
      defaultQuestionCards.forEach((card, index) => {
        if (!card.dataset.id) card.dataset.id = `qDefault${index + 1}`;
        addQuestionEventListeners(card);
        const numberEl = card.querySelector(".question-number");
        if (numberEl) numberEl.textContent = index + 1;
      });
      console.log(
        `formBuilder.js: Khởi tạo với ${questionCount} câu hỏi mặc định từ HTML.`
      );
    }
  }

  updateQuestionCountDisplay();
  checkQuestionLimit();
  updateQuestionNumbers();
  if (typeof window.updatePreview === "function") {
    // Check if updatePreview is defined
    window.updatePreview();
  }
  if (
    saveSuccessAlert &&
    new URLSearchParams(window.location.search).has("success")
  ) {
    saveSuccessAlert.style.display = "flex";
    setTimeout(() => {
      saveSuccessAlert.style.display = "none";
      const url = new URL(window.location.href);
      url.searchParams.delete("success");
      url.searchParams.delete("error");
      url.searchParams.delete("action");
      url.searchParams.delete("message");
      // Không xóa formId và clubId để duy trì trạng thái chỉnh sửa
      window.history.replaceState({}, document.title, url.toString());
    }, 5000);
  }
  if (
    saveErrorAlert &&
    new URLSearchParams(window.location.search).has("error")
  ) {
    saveErrorAlert.style.display = "flex";
    setTimeout(() => {
      saveErrorAlert.style.display = "none";
      const url = new URL(window.location.href);
      url.searchParams.delete("success");
      url.searchParams.delete("error");
      url.searchParams.delete("action");
      url.searchParams.delete("message");
      // Không xóa formId và clubId để duy trì trạng thái chỉnh sửa
      window.history.replaceState({}, document.title, url.toString());
    }, 7000);
  }

  const Sortable = window.Sortable;
  if (Sortable && questionsList) {
    new Sortable(questionsList, {
      animation: 150,
      handle: ".drag-handle",
      onEnd: () => {
        updateQuestionNumbers();
        if (typeof window.updatePreview === "function") window.updatePreview();
      },
    });
  }

    if (formTitleInput) {
        formTitleInput.addEventListener("input", () => {
            if (formTitleInput.value.length > MAX_TITLE_LENGTH) {
                formTitleInput.value = formTitleInput.value.substring(0, MAX_TITLE_LENGTH);
                alert("Tiêu đề form không được vượt quá " + MAX_TITLE_LENGTH + " kí tự");
            }
            if (previewTitle) {
                previewTitle.textContent = formTitleInput.value || "Đơn đăng ký";
            };
        });
    }
    if (formTypeSelect) {
    formTypeSelect.addEventListener("change", function () {
      const type = this.value;
      if (
        formTitleInput.value === "Đơn đăng ký mới" ||
        formTitleInput.value === "Đơn đăng ký thành viên" ||
        formTitleInput.value === "Đơn đăng ký tham gia sự kiện"
      ) {
        const newTitle =
          type === "member"
            ? "Đơn đăng ký thành viên"
            : type === "event"
            ? "Đơn đăng ký tham gia sự kiện"
            : "Đơn đăng ký mới";
        formTitleInput.value = newTitle;
        if (previewTitle) previewTitle.textContent = newTitle;
      }
      
      // Tìm câu hỏi chọn ban dựa trên nội dung và loại câu hỏi
      const allCards = Array.from(questionsList.querySelectorAll(".question-card"));
      let departmentQuestionCard = null;
      
      // Tìm câu hỏi chọn ban dựa vào nội dung
      for (const card of allCards) {
        const labelInput = card.querySelector(".question-label");
        const typeSelect = card.querySelector(".question-type");
        if (labelInput && typeSelect && 
            labelInput.value.includes("Chọn ban") && 
            typeSelect.value === "radio") {
          departmentQuestionCard = card;
          // Đảm bảo thuộc tính data-required-type được thiết lập
          card.dataset.requiredType = "department";
          break;
        }
      }
      
      if (type === "member") {
        if (!departmentQuestionCard) {
          addDepartmentQuestion();
        } else {
          // Đảm bảo câu hỏi chọn ban được bảo vệ
          departmentQuestionCard.classList.add("required-question");
          departmentQuestionCard.dataset.requiredType = "department";
        }
      } else {
        if (departmentQuestionCard) {
          departmentQuestionCard.remove();
          questionCount--;
          updateQuestionCountDisplay();
          checkQuestionLimit();
          updateQuestionNumbers();
        }
      }

      // Gọi protectRequiredQuestions sau khi xử lý câu hỏi chọn ban
      if (typeof protectRequiredQuestions === "function") {
        protectRequiredQuestions();
      }

      if (typeof window.updatePreview === "function") window.updatePreview();
    });
  }

  if (addQuestionBtn)
    addQuestionBtn.addEventListener("click", () => addNewQuestion("text"));
  if (addInfoBtn)
    addInfoBtn.addEventListener("click", () => addNewQuestion("info"));
  if (saveBtn) saveBtn.addEventListener("click", () => submitForm("save"));
  if (publishBtn && publishDialog)
    publishBtn.addEventListener("click", () =>
      publishDialog.classList.add("open")
    );

  if (publishDialog) {
    publishDialog
      .querySelector(".dialog-overlay")
      ?.addEventListener("click", () => publishDialog.classList.remove("open"));
    publishDialog
      .querySelector(".dialog-close")
      ?.addEventListener("click", () => publishDialog.classList.remove("open"));
    publishDialog
      .querySelector(".dialog-cancel")
      ?.addEventListener("click", () => publishDialog.classList.remove("open"));
  }
  if (confirmPublishBtn && publishDialog) {
    confirmPublishBtn.addEventListener("click", () => {
      submitForm("publish");
      publishDialog.classList.remove("open");
    });
  }

  tabItems.forEach((tab) => {
    tab.addEventListener("click", function () {
      const tabId = this.dataset.tab;
      tabItems.forEach((t) => t.classList.remove("active"));
      this.classList.add("active");
      tabContents.forEach((content) => {
        content.classList.remove("active");
        if (content.id === `${tabId}Tab`) content.classList.add("active");
      });
      activeTab = tabId;
      if (tabId === "preview" && typeof window.updatePreview === "function")
        window.updatePreview();
    });
  });

  function updateQuestionCountDisplay() {
    if (questionCountElement) questionCountElement.textContent = questionCount;
  }
  function checkQuestionLimit() {
    const limitReached = questionCount >= MAX_QUESTIONS;
    if (addQuestionBtn) addQuestionBtn.disabled = limitReached;
    if (addInfoBtn) addInfoBtn.disabled = limitReached;
    if (maxQuestionsAlert)
      maxQuestionsAlert.style.display = limitReached ? "flex" : "none";
  }
  function showAlert(alertElement, duration = 3000) {
    if (alertElement) {
      alertElement.style.display = "flex";
      setTimeout(() => {
        alertElement.style.display = "none";
      }, duration);
    }
  }

  function submitForm(actionType) {
    if (
      !formBuilderForm ||
      !formTitleInput ||
      !formTypeSelect ||
      !actionInput ||
      !formTitleHidden ||
      !formTypeHidden ||
      !questionsHidden
    ) {
      alert("Đã có lỗi xảy ra, không thể gửi form. Vui lòng thử lại.");
      return;
    }
    if (formTitleInput.value.trim() === "") {
            alert("Tiêu đề form không được để trống!");
            formTitleInput.focus();
            return;
        }
        const formTypeVal = formTypeSelect.value;
        if (formTypeVal === "") {
            const formTypeError = document.getElementById("formTypeError");
            if (formTypeError)
                formTypeError.style.display = "block";
            alert("Vui lòng chọn loại form!");
            formTypeSelect.focus();
            return;
        } else {
      const formTypeError = document.getElementById("formTypeError");
      if (formTypeError) formTypeError.style.display = "none";
    }

    actionInput.value = actionType;
    formTitleHidden.value = formTitleInput.value;
    formTypeHidden.value = formTypeVal;
    const formData = getFormData();
    if (formData.length === 0) {
      alert("Form phải có ít nhất một câu hỏi!");
      return;
    }
    questionsHidden.value = JSON.stringify(formData);

    console.log(
      "formBuilder.js: Gửi form. Action:",
      actionType,
      "Tiêu đề:",
      formTitleHidden.value,
      "Loại Form:",
      formTypeHidden.value
    );
    formBuilderForm.submit();
  }

  function addNewQuestion(type = "text") {
    if (questionCount >= MAX_QUESTIONS) {
      showAlert(maxQuestionsAlert);
      return;
    }
    questionCount++;
    updateQuestionCountDisplay();
    checkQuestionLimit();

    const domId = "qNew" + Date.now();
    const html = questionTemplateHtml
      .replace(/{{id}}/g, domId)
      .replace(/{{number}}/g, questionCount);
    const tempDiv = document.createElement("div");
    tempDiv.innerHTML = html;
    const newQuestionCard = tempDiv.firstElementChild;
    if (!newQuestionCard) {
      questionCount--;
      updateQuestionCountDisplay();
      checkQuestionLimit();
      return;
    }
    newQuestionCard.dataset.id = domId;

    const typeSelect = newQuestionCard.querySelector(".question-type");
    if (typeSelect) typeSelect.value = type;
    newQuestionCard
      .querySelectorAll(
        ".question-actions button, .question-type, .question-required"
      )
      .forEach((el) => (el.disabled = false));
    toggleQuestionFields(newQuestionCard, type);
    questionsList.appendChild(newQuestionCard);
    addQuestionEventListeners(newQuestionCard);
    updateQuestionNumbers();
    if (typeof window.updatePreview === "function") window.updatePreview();
    newQuestionCard.querySelector(".question-label")?.focus();
  }
  function createQuestionCard(qData, number) {
    const domId = "qLoaded" + qData.id;
    const html = questionTemplateHtml
      .replace(/{{id}}/g, domId)
      .replace(/{{number}}/g, number);
    const tempDiv = document.createElement("div");
    tempDiv.innerHTML = html;
    const questionCard = tempDiv.firstElementChild;
    if (!questionCard) {
      console.error(
        "formBuilder.js: Không thể tạo questionCard từ template cho dữ liệu:",
        JSON.parse(JSON.stringify(qData))
      );
      return null;
    }
    questionCard.dataset.id = domId;
    questionCard.dataset.originalId = qData.id;

    // Kiểm tra xem câu hỏi có phải là câu hỏi bắt buộc không
    const requiredQuestionIds = getRequiredQuestionIds();
    const isFixedRequiredQuestion =
      requiredQuestionIds.includes(qData.id) ||
      (qData.label.includes("Chọn ban") && qData.type === "radio");

    if (isFixedRequiredQuestion) {
      if (qData.label.includes("Chọn ban")) {
        questionCard.dataset.requiredType = "department";
      }

      questionCard.classList.add("required-question");

      // Thêm badge "Cố định" cho câu hỏi bắt buộc
      const questionHeader = questionCard.querySelector(".question-header");
      if (questionHeader && !questionHeader.querySelector(".required-badge")) {
        const requiredBadge = document.createElement("span");
        requiredBadge.classList.add("required-badge");
        requiredBadge.textContent = "Cố định";
        questionHeader.appendChild(requiredBadge);
      }
    }

    const labelInput = questionCard.querySelector(".question-label");
    if (labelInput) {
      labelInput.value = qData.label || "Câu hỏi";
    }

    const typeSelect = questionCard.querySelector(".question-type");
    if (typeSelect) {
      typeSelect.value = qData.type || "text";
      if (isFixedRequiredQuestion) {
        typeSelect.disabled = true;
      }
    }

    const requiredCheckbox = questionCard.querySelector(".question-required");
    if (requiredCheckbox) {
      requiredCheckbox.checked =
        qData.required || isFixedRequiredQuestion || false;
      requiredCheckbox.id = `required-${domId}`;
      if (isFixedRequiredQuestion) {
        requiredCheckbox.disabled = true;
      }
      const labelForRequired = questionCard.querySelector(
        `label[for="required-{{id}}"]`
      );
      if (labelForRequired)
        labelForRequired.setAttribute("for", `required-${domId}`);
    }

    const placeholderInput = questionCard.querySelector(
      ".question-placeholder"
    );
    if (placeholderInput && qData.placeholder)
      placeholderInput.value = qData.placeholder;
    if (qData.type === "info") {
      if (qData.options && qData.options !== "null") {
        try {
          const infoData = JSON.parse(qData.options);
          const contentTextarea =
            questionCard.querySelector(".question-content");
          if (contentTextarea && infoData.content)
            contentTextarea.value = infoData.content;
          if (infoData.image) {
            questionCard.dataset.imageData = infoData.image;
            const imagePreview = questionCard.querySelector(
              ".uploaded-image-preview"
            );
            const previewContainer = questionCard.querySelector(
              ".image-preview-container"
            );
            const uploadPlaceholder = questionCard.querySelector(
              ".upload-placeholder"
            );
            if (imagePreview) imagePreview.src = infoData.image;
            if (previewContainer) previewContainer.style.display = "block";
            if (uploadPlaceholder) uploadPlaceholder.style.display = "none";
          }
        } catch (e) {
          console.error(
            "formBuilder.js: Lỗi parse JSON options cho INFO:",
            qData.options,
            e
          );
          const contentTextarea =
            questionCard.querySelector(".question-content");
          if (contentTextarea && typeof qData.options === "string")
            contentTextarea.value = qData.options;
        }
      }
    } else if (["radio", "checkbox", "select"].includes(qData.type)) {
      if (
        qData.options &&
        qData.options !== "null" &&
        typeof qData.options === "string"
      ) {
        const optionValues = qData.options.split(";;");
        optionValues.forEach((optValue) => {
          if (optValue.trim() !== "") {
            addOption(questionCard, optValue.trim());
          }
        });
      } else {
        console.log(
          `formBuilder.js: Không có options hoặc options không phải chuỗi cho ${qData.type}:`,
          qData.options
        );
      }
    }

    questionCard
      .querySelectorAll(
        ".question-actions button, .question-type, .question-required"
      )
      .forEach((el) => (el.disabled = false));
    toggleQuestionFields(questionCard, qData.type || "text");
    return questionCard;
  }

  function toggleQuestionFields(questionCard, type) {
    const placeholderGroup = questionCard.querySelector(".placeholder-group");
    const infoGroup = questionCard.querySelector(".info-group");
    const optionsContainer = questionCard.querySelector(".options-container");

    if (placeholderGroup) placeholderGroup.style.display = "none";
    if (infoGroup) infoGroup.style.display = "none";
    if (optionsContainer) optionsContainer.style.display = "none";

    if (type === "info" && infoGroup) {
      infoGroup.style.display = "block";
    } else if (
      ["radio", "checkbox", "select"].includes(type) &&
      optionsContainer
    ) {
      optionsContainer.style.display = "block";
      const optionsList = optionsContainer.querySelector(".options-list");
      const isNewQuestion =
        questionCard.dataset.id &&
        (questionCard.dataset.id.startsWith("qNew") ||
          questionCard.dataset.id.startsWith("qDup"));
      // Chỉ thêm option mặc định nếu là câu hỏi mới VÀ chưa có option nào (trường hợp load từ DB đã có options thì không thêm)
      if (
        optionsList &&
        optionsList.children.length === 0 &&
        isNewQuestion &&
        !questionCard.dataset.originalId
      ) {
        addOption(questionCard, "Tùy chọn 1");
      }
    } else if (
      placeholderGroup &&
      !["info", "radio", "checkbox", "select"].includes(type)
    ) {
      placeholderGroup.style.display = "block";
    }
  }
  function addQuestionEventListeners(questionCard) {
    const isHardcodedRequiredQuestion =
      questionCard.classList.contains("required-question");
    // Kiểm tra loại câu hỏi bắt buộc
    const requiredType = questionCard.dataset.requiredType || "";
    const labelInput = questionCard.querySelector(".question-label");
    const typeSelect = questionCard.querySelector(".question-type");

    if (
      isHardcodedRequiredQuestion &&
      !requiredType &&
      labelInput &&
      typeSelect
    ) {
      if (
        labelInput.value.includes("Chọn ban") &&
        typeSelect.value === "radio"
      ) {
        questionCard.dataset.requiredType = "department";
      }
    }

    if (typeSelect) {
      typeSelect.addEventListener("change", function () {
        toggleQuestionFields(questionCard, this.value);
        if (typeof window.updatePreview === "function") window.updatePreview();
      });
      if (isHardcodedRequiredQuestion) {
        typeSelect.disabled = true;
      }
    }

    // Đánh dấu và vô hiệu hóa checkbox required nếu là câu hỏi bắt buộc
    const requiredCheckbox = questionCard.querySelector(".question-required");
    if (requiredCheckbox && isHardcodedRequiredQuestion) {
      requiredCheckbox.checked = true;
      requiredCheckbox.disabled = true;
    }

    setupImageUpload(questionCard);
    const addOptionBtn = questionCard.querySelector(".add-option");
    if (addOptionBtn) {
      addOptionBtn.addEventListener("click", (e) => {
        e.preventDefault();
        const optionsList = questionCard.querySelector(".options-list");
        const optionCount = optionsList ? optionsList.children.length + 1 : 1;
        addOption(questionCard, `Tùy chọn ${optionCount}`);
        if (typeof window.updatePreview === "function") window.updatePreview();
      });
    }
    const moveUpBtn = questionCard.querySelector(".move-up"),
      moveDownBtn = questionCard.querySelector(".move-down"),
      deleteBtn = questionCard.querySelector(".delete"),
      duplicateBtn = questionCard.querySelector(".duplicate");

    // Xác định nếu câu hỏi là "Chọn ban"
    const isDepartmentQuestion =
      questionCard.dataset.requiredType === "department" ||
      (questionCard
        .querySelector(".question-label")
        ?.value.includes("Chọn ban") &&
        questionCard.querySelector(".question-type")?.value === "radio");

    if (isDepartmentQuestion) {
      const addOptionBtn = questionCard.querySelector(".add-option");
      if (addOptionBtn) {
        addOptionBtn.disabled = true;
        addOptionBtn.style.opacity = "0.5";
      }

      // Vô hiệu hóa nút xóa tùy chọn
      questionCard.querySelectorAll(".delete-option").forEach((btn) => {
        btn.disabled = true;
        btn.style.opacity = "0.5";
      });

      // Đặt trường input thành chỉ đọc
      questionCard.querySelectorAll(".option-value").forEach((input) => {
        input.readOnly = true;
      });
    }

    if (isHardcodedRequiredQuestion) {
      if (moveUpBtn) moveUpBtn.disabled = true;
      if (moveDownBtn) moveDownBtn.disabled = true;
      if (duplicateBtn) duplicateBtn.disabled = true;
      // Vô hiệu hóa nút xóa cho câu hỏi bắt buộc
      if (deleteBtn) deleteBtn.disabled = true;
    } else {
      if (moveUpBtn) moveUpBtn.disabled = false;
      if (moveDownBtn) moveDownBtn.disabled = false;
      if (deleteBtn) deleteBtn.disabled = false;
    }
    if (moveUpBtn)
      moveUpBtn.addEventListener("click", () => {
        const prev = questionCard.previousElementSibling;
        if (
          prev &&
          (!isHardcodedRequiredQuestion ||
            !prev.classList.contains("required-question"))
        ) {
          questionsList.insertBefore(questionCard, prev);
          updateQuestionNumbers();
          if (typeof window.updatePreview === "function")
            window.updatePreview();
        }
      });
    if (moveDownBtn)
      moveDownBtn.addEventListener("click", () => {
        const next = questionCard.nextElementSibling;
        if (next) {
          questionsList.insertBefore(next, questionCard);
          updateQuestionNumbers();
          if (typeof window.updatePreview === "function")
            window.updatePreview();
        }
      });
    if (deleteBtn)
      deleteBtn.addEventListener("click", () => {
        const isDepartmentQuestion =
          questionCard.dataset.requiredType === "department" ||
          (questionCard
            .querySelector(".question-label")
            ?.value.includes("Chọn ban") &&
            questionCard.querySelector(".question-type")?.value === "radio");

        // Không cho phép xóa câu hỏi chọn ban nếu form là loại member
        if (
          isDepartmentQuestion &&
          document.getElementById("formType")?.value === "member"
        ) {
          alert(
            "Không thể xóa câu hỏi chọn ban trong form đăng ký thành viên."
          );
          return;
        }

        questionCard.remove();
        questionCount--;
        updateQuestionCountDisplay();
        checkQuestionLimit();
        updateQuestionNumbers();
        if (typeof window.updatePreview === "function") window.updatePreview();
      });

    if (duplicateBtn) {
      duplicateBtn.addEventListener("click", () => {
        if (questionCount >= MAX_QUESTIONS) {
          showAlert(maxQuestionsAlert);
          return;
        }
        questionCount++;
        updateQuestionCountDisplay();
        checkQuestionLimit();
        const clone = questionCard.cloneNode(true);
        const newId = "qDup" + Date.now();
        clone.dataset.id = newId;
        clone.removeAttribute("data-original-id");
        clone.classList.remove("required-question");
        const requiredBadge = clone.querySelector(".required-badge");
        if (requiredBadge) requiredBadge.remove();
        clone
          .querySelectorAll(
            ".question-actions button, .question-type, .question-required"
          )
          .forEach((el) => (el.disabled = false));
        const clonedRequiredCheckbox =
          clone.querySelector(".question-required");
        if (clonedRequiredCheckbox) {
          clonedRequiredCheckbox.id = `required-${newId}`;
          const labelForCloned = clone.querySelector(`label[for^="required-"]`);
          if (labelForCloned)
            labelForCloned.setAttribute("for", `required-${newId}`);
        }
        questionsList.appendChild(clone);
        addQuestionEventListeners(clone);
        updateQuestionNumbers();
        if (typeof window.updatePreview === "function") window.updatePreview();
      });
    }

    questionCard
      .querySelectorAll("input, select, textarea")
      .forEach((input) => {
        input.addEventListener("change", () => {
          if (typeof window.updatePreview === "function")
            window.updatePreview();
        });
        input.addEventListener("input", () => {
          if (typeof window.updatePreview === "function")
            window.updatePreview();
        });
      });
  }

  function setupImageUpload(questionCard) {
    const imageInput = questionCard.querySelector(".question-image-file"),
      imageUploadArea = questionCard.querySelector(".image-upload-area"),
      imagePreviewContainer = questionCard.querySelector(
        ".image-preview-container"
      ),
      imagePreview = questionCard.querySelector(".uploaded-image-preview"),
      uploadPlaceholder = questionCard.querySelector(".upload-placeholder"),
      changeImageBtn = questionCard.querySelector(".change-image"),
      removeImageBtn = questionCard.querySelector(".remove-image");
    if (
      !imageInput ||
      !imageUploadArea ||
      !imagePreviewContainer ||
      !imagePreview ||
      !uploadPlaceholder
    )
      return;
    const showPreview = (imageData) => {
      imagePreview.src = imageData;
      imagePreviewContainer.style.display = "block";
      uploadPlaceholder.style.display = "none";
      questionCard.dataset.imageData = imageData;
      if (typeof window.updatePreview === "function") window.updatePreview();
    };
    const clearPreview = () => {
      imageInput.value = "";
      imagePreview.src = "/placeholder.svg?height=100&width=100";
      imagePreviewContainer.style.display = "none";
      uploadPlaceholder.style.display = "block";
      delete questionCard.dataset.imageData;
      if (typeof window.updatePreview === "function") window.updatePreview();
    };
    imageInput.addEventListener("change", (e) => {
      const file = e.target.files[0];
      if (file) handleImageFile(file, showPreview);
    });
    imageUploadArea.addEventListener("dragover", (e) => {
      e.preventDefault();
      imageUploadArea.classList.add("dragover");
    });
    imageUploadArea.addEventListener("dragleave", () =>
      imageUploadArea.classList.remove("dragover")
    );
    imageUploadArea.addEventListener("drop", (e) => {
      e.preventDefault();
      imageUploadArea.classList.remove("dragover");
      const file = e.dataTransfer.files[0];
      if (file) handleImageFile(file, showPreview);
    });
    if (changeImageBtn)
      changeImageBtn.addEventListener("click", (e) => {
        e.preventDefault();
        imageInput.click();
      });
    if (removeImageBtn)
      removeImageBtn.addEventListener("click", (e) => {
        e.preventDefault();
        clearPreview();
      });
  }

  function handleImageFile(file, callback) {
    const validTypes = ["image/jpeg", "image/png", "image/gif"];
    if (!validTypes.includes(file.type)) {
      alert("Vui lòng chọn file ảnh có định dạng JPG, PNG hoặc GIF.");
      return;
    }
    const maxSize = 5 * 1024 * 1024;
    if (file.size > maxSize) {
      alert("Kích thước file tối đa là 5MB.");
      return;
    }
    const reader = new FileReader();
    reader.onload = (e) => callback(e.target.result);
    reader.readAsDataURL(file);
  }

  function addOption(questionCard, value = "") {
    const optionsList = questionCard.querySelector(".options-list");
    if (!optionsList) return;
    const optionCount = optionsList.children.length + 1;
    const optionId =
      "opt" +
      Date.now() +
      "-" +
      optionCount +
      Math.random().toString(16).slice(2);
    const html = optionTemplateHtml
      .replace(/{{id}}/g, optionId)
      .replace(/{{number}}/g, optionCount)
      .replace(/{{value}}/g, value.replace(/"/g, "&quot;"));
    const tempDiv = document.createElement("div");
    tempDiv.innerHTML = html;
    const newOption = tempDiv.firstElementChild;
    optionsList.appendChild(newOption);
    const deleteBtn = newOption.querySelector(".delete-option");
    if (deleteBtn) {
      deleteBtn.addEventListener("click", (e) => {
        e.preventDefault();
        const questionType =
          questionCard.querySelector(".question-type")?.value;
        if (
          optionsList.children.length <= 1 &&
          (questionType === "radio" || questionType === "select")
        ) {
          alert("Phải có ít nhất một tùy chọn cho loại câu hỏi này.");
          return;
        }
        newOption.remove();
        Array.from(optionsList.children).forEach((opt, index) => {
          const numEl = opt.querySelector(".option-number");
          if (numEl) numEl.textContent = `${index + 1}.`;
        });
        if (typeof window.updatePreview === "function") window.updatePreview();
      });
    }
    newOption.querySelector(".option-value")?.addEventListener("input", () => {
      if (typeof window.updatePreview === "function") window.updatePreview();
    });
  }

  function updateQuestionNumbers() {
    if (!questionsList) return;
    Array.from(questionsList.children).forEach((question, index) => {
      const numberEl = question.querySelector(".question-number");
      if (numberEl) numberEl.textContent = index + 1;
    });
    console.log("formBuilder.js: Đã cập nhật số thứ tự câu hỏi.");
  }

  function getFormData() {
    const questions = [];
    if (!questionsList) return questions;
    // Lấy tất cả question cards theo thứ tự DOM hiện tại sau khi kéo thả
    const cards = Array.from(questionsList.querySelectorAll(".question-card"));

    // Lặp qua tất cả cards và thu thập dữ liệu
    cards.forEach((card, index) => {
      const typeSelect = card.querySelector(".question-type"),
        labelInput = card.querySelector(".question-label"),
        requiredCheckbox = card.querySelector(".question-required");
      if (!typeSelect || !labelInput || !requiredCheckbox) {
        console.warn(
          `formBuilder.js: Thiếu trường trong question card (index ${index}), data-id: ${card.dataset.id}. Bỏ qua.`
        );
        return;
      }

      const questionData = {
        id: card.dataset.originalId || card.dataset.id,
        type: typeSelect.value,
        label: labelInput.value,
        required: requiredCheckbox.checked,
        placeholder: card.querySelector(".question-placeholder")?.value || "",
        displayOrder: index, // Lưu thứ tự hiển thị hiện tại
      };

      if (questionData.type === "info") {
        questionData.options = JSON.stringify({
          content: card.querySelector(".question-content")?.value || "",
          image: card.dataset.imageData || "",
        });
      } else if (["radio", "checkbox", "select"].includes(questionData.type)) {
        questionData.options = [];
        card.querySelectorAll(".option-item").forEach((optItem) => {
          questionData.options.push({
            id: optItem.dataset.id,
            value: optItem.querySelector(".option-value")?.value || "",
          });
        });
      }
      questions.push(questionData);
    });
    console.log(
      "formBuilder.js: getFormData thu thập được:",
      JSON.parse(JSON.stringify(questions))
    );
    return questions;
  }

  window.updatePreview = function updatePreview() {
    if (activeTab !== "preview" || !previewForm || !formTitleInput) return;
    console.log("formBuilder.js: Bắt đầu updatePreview.");
    if (previewTitle)
      previewTitle.textContent = formTitleInput.value || "Đơn đăng ký";
    previewForm.innerHTML = "";
    const questions = getFormData();
    questions.forEach((q) => {
      const fieldset = document.createElement("fieldset");
      fieldset.className = "form-group-preview";
      const legend = document.createElement("label");
      legend.className = "form-label-preview";
      legend.innerHTML =
        q.label + (q.required ? ' <span style="color: red;">*</span>' : "");
      fieldset.appendChild(legend);
      const inputElement = createPreviewInputElement(q);
      if (inputElement) fieldset.appendChild(inputElement);
      previewForm.appendChild(fieldset);
    });
    console.log("formBuilder.js: Hoàn thành updatePreview.");
  };

  function createPreviewInputElement(q) {
    let input;
    switch (q.type) {
      case "text":
      case "email":
      case "number":
      case "date":
        input = document.createElement("input");
        input.type = q.type;
        input.className = "form-input-preview";
        if (q.placeholder) input.placeholder = q.placeholder;
        break;
      case "textarea":
        input = document.createElement("textarea");
        input.className = "form-textarea-preview";
        input.rows = 3;
        if (q.placeholder) input.placeholder = q.placeholder;
        break;
      case "info":
        input = document.createElement("div");
        input.className = "info-preview-content";
        if (q.options) {
          try {
            const infoData = JSON.parse(q.options);
            if (infoData.content) {
              const p = document.createElement("p");
              p.textContent = infoData.content;
              input.appendChild(p);
            }
            if (infoData.image) {
              const img = document.createElement("img");
              img.src = infoData.image;
              img.alt = "Hình ảnh thông tin";
              input.appendChild(img);
            }
          } catch (e) {
            console.error(
              "formBuilder.js: Lỗi parse JSON options cho INFO preview:",
              q.options,
              e
            );
            // Fallback for non-JSON string content if necessary
            if (typeof q.options === "string") {
              const p = document.createElement("p");
              p.textContent = q.options;
              input.appendChild(p);
            }
          }
        }
        break;
      case "radio":
      case "checkbox":
        input = document.createElement("div");
        input.className = "form-options-preview";
        (q.options || []).forEach((opt, index) => {
          const wrapper = document.createElement("div");
          wrapper.className = "form-option-item-preview";

          const element = document.createElement("input");
          element.type = q.type;
          element.name = `preview-${q.type}-${q.id}-${index}`; // Ensure unique name for radio groups per question
          element.id = `preview-${q.type}-${q.id}-${opt.id || index}`;
          element.value = opt.value;
          const label = document.createElement("label");
          label.setAttribute("for", element.id);
          label.textContent = opt.value;

          wrapper.appendChild(element);
          wrapper.appendChild(label);
          input.appendChild(wrapper);
        });
        break;
    }
    return input;
  }

  //Hàm tạo câu hỏi chọn ban
  function addDepartmentQuestion() {
    if (questionCount >= MAX_QUESTIONS) {
      showAlert(maxQuestionsAlert);
      return;
    }
    questionCount++;
    updateQuestionCountDisplay();
    checkQuestionLimit();

    console.log("addDepartmentQuestion: Thêm câu hỏi chọn ban mới");
    
    const domId = "qDepartment" + Date.now();
    const html = questionTemplateHtml
      .replace(/{{id}}/g, domId)
      .replace(/{{number}}/g, questionCount);
    const tempDiv = document.createElement("div");
    tempDiv.innerHTML = html;
    const newQuestionCard = tempDiv.firstElementChild;
    newQuestionCard.dataset.id = domId;
    newQuestionCard.dataset.requiredType = "department";
    newQuestionCard.classList.add("required-question");

    const labelInput = newQuestionCard.querySelector(".question-label");
    if (labelInput) labelInput.value = "Chọn ban muốn vào CLB";

    const typeSelect = newQuestionCard.querySelector(".question-type");
    if (typeSelect) {
      typeSelect.value = "radio";
      typeSelect.disabled = true;
    }

    const requiredCheckbox =
      newQuestionCard.querySelector(".question-required");
    if (requiredCheckbox) {
      requiredCheckbox.checked = true;
      requiredCheckbox.disabled = true;
    }

    const optionsList = newQuestionCard.querySelector(".options-list");
    if (optionsList && window.clubDepartments) {
      console.log("addDepartmentQuestion: Thêm options từ clubDepartments:", window.clubDepartments);
      if (window.clubDepartments.length === 0) {
        console.warn("addDepartmentQuestion: window.clubDepartments rỗng hoặc không hợp lệ");
      }
      
      window.clubDepartments.forEach((dept, index) => {
        const optionId = "opt-dept-" + dept.id;
        const html = optionTemplateHtml
          .replace(/{{id}}/g, optionId)
          .replace(/{{number}}/g, index + 1)
          .replace(/{{value}}/g, dept.name);
        const tempDiv = document.createElement("div");
        tempDiv.innerHTML = html;
        //append tùy chọn vào optionsList
        optionsList.appendChild(tempDiv.firstElementChild);
      });
      
      // Hiển thị các tùy chọn
      toggleQuestionFields(newQuestionCard, "radio");
    } else {
      console.error("addDepartmentQuestion: Không tìm thấy optionsList hoặc window.clubDepartments");
    }

    const questionHeader = newQuestionCard.querySelector(".question-header");
    if (questionHeader) {
      const requiredBadge = document.createElement("span");
      requiredBadge.classList.add("required-badge");
      requiredBadge.textContent = "Cố định";
      questionHeader.appendChild(requiredBadge);
    }

    const moveUpBtn = newQuestionCard.querySelector(".move-up");
    const moveDownBtn = newQuestionCard.querySelector(".move-down");
    const duplicateBtn = newQuestionCard.querySelector(".duplicate");
    if (moveUpBtn) moveUpBtn.disabled = true;
    if (moveDownBtn) moveDownBtn.disabled = true;
    if (duplicateBtn) duplicateBtn.disabled = true;

    questionsList.insertBefore(newQuestionCard, questionsList.firstChild); // Thêm vào đầu danh sách
    addQuestionEventListeners(newQuestionCard);
    updateQuestionNumbers();
    if (typeof window.updatePreview === "function") window.updatePreview();
    //debug danh sách ban(options)
    console.log(window.clubDepartments);
  }
});

function toggleEventField(selectElement) {
  const eventNameField = document.getElementById("eventNameField");
  if (eventNameField)
    eventNameField.style.display =
      selectElement.value === "event" ? "block" : "none";
}

// Hàm lấy ID của các câu hỏi bắt buộc (từ DB hoặc từ dữ liệu ban đầu)
function getRequiredQuestionIds() {
  if (!window.existingQuestions || !Array.isArray(window.existingQuestions)) {
    return [];
  }

  return window.existingQuestions
    .filter((q) => q.label.includes("Chọn ban") && q.type === "radio")
    .map((q) => q.id);
}
// Hàm bảo vệ các câu hỏi bắt buộc
function protectRequiredQuestions() {
  if (!questionsList) return;

  // Lấy loại form hiện tại - sửa lỗi formTypeSelect undefined
  const formTypeElement = document.getElementById("formType");
  const formType = formTypeElement ? formTypeElement.value : "member";

  const allCards = Array.from(questionsList.querySelectorAll(".question-card"));
  console.log("protectRequiredQuestions: Số câu hỏi tìm được:", allCards.length);

  allCards.forEach((card) => {
    const labelInput = card.querySelector(".question-label");
    const typeSelect = card.querySelector(".question-type");

    if (!labelInput || !typeSelect) return;
    const isDepartmentQuestion =
      labelInput.value.includes("Chọn ban") && typeSelect.value === "radio";

    // Nếu là câu hỏi chọn ban
    if (isDepartmentQuestion) {
      if (formType === "member") {
        card.classList.add("required-question");
        card.dataset.requiredType = "department";
        
        // Kiểm tra các options để đảm bảo danh sách ban đã được thêm vào
        const optionsList = card.querySelector(".options-list");
        if (optionsList && window.clubDepartments && optionsList.children.length === 0) {
          console.log("Thêm options cho câu hỏi chọn ban trong protectRequiredQuestions");
          window.clubDepartments.forEach((dept, index) => {
            const optionId = "opt-dept-" + dept.id;
            const html = optionTemplateHtml
              .replace(/{{id}}/g, optionId)
              .replace(/{{number}}/g, index + 1)
              .replace(/{{value}}/g, dept.name);
            const tempDiv = document.createElement("div");
            tempDiv.innerHTML = html;
            optionsList.appendChild(tempDiv.firstElementChild);
          });
        }

        const questionHeader = card.querySelector(".question-header");
        if (
          questionHeader &&
          !questionHeader.querySelector(".required-badge")
        ) {
          const requiredBadge = document.createElement("span");
          requiredBadge.classList.add("required-badge");
          requiredBadge.textContent = "Cố định";
          questionHeader.appendChild(requiredBadge);
        }
        // Vô hiệu hóa nút di chuyển, nhân bản và xóa
        const moveUpBtn = card.querySelector(".move-up");
        const moveDownBtn = card.querySelector(".move-down");
        const duplicateBtn = card.querySelector(".duplicate");
        const deleteBtn = card.querySelector(".delete");
        const addOptionBtn = card.querySelector(".add-option");

        if (moveUpBtn) moveUpBtn.disabled = true;
        if (moveDownBtn) moveDownBtn.disabled = true;
        if (duplicateBtn) duplicateBtn.disabled = true;
        if (deleteBtn) deleteBtn.disabled = true;

        // Vô hiệu hóa nút thêm tùy chọn
        if (addOptionBtn) {
          addOptionBtn.disabled = true;
          addOptionBtn.style.opacity = "0.5";
        }

        // Vô hiệu hóa các nút xóa tùy chọn và đặt input thành chỉ đọc
        card.querySelectorAll(".delete-option").forEach((btn) => {
          btn.disabled = true;
          btn.style.opacity = "0.5";
        });

        card.querySelectorAll(".option-value").forEach((input) => {
          input.readOnly = true;
        });

        // Vô hiệu hóa loại câu hỏi
        typeSelect.disabled = true;

        const requiredCheckbox = card.querySelector(".question-required");
        if (requiredCheckbox) {
          requiredCheckbox.checked = true;
          requiredCheckbox.disabled = true;
        }
      }
    }
  });
}

// Gọi hàm bảo vệ câu hỏi bắt buộc sau khi load form
if (
  window.existingQuestions &&
  Array.isArray(window.existingQuestions) &&
  window.existingQuestions.length > 0
) {
  setTimeout(protectRequiredQuestions, 500); // Đợi 500ms để đảm bảo DOM đã được tạo hoàn chỉnh
}
