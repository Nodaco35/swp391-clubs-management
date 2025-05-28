document.addEventListener("DOMContentLoaded", () => {
  // Initialize variables
  let questionCount = 3
  const REQUIRED_QUESTIONS_COUNT = 3
  let activeTab = "edit"

  // DOM Elements
  const formTitle = document.getElementById("formTitle")
  const formType = document.getElementById("formType")
  const questionsList = document.getElementById("questionsList")
  const addQuestionBtn = document.getElementById("addQuestionBtn")
  const addInfoBtn = document.getElementById("addInfoBtn")
  const saveBtn = document.getElementById("saveBtn")
  const publishBtn = document.getElementById("publishBtn")
  const publishDialog = document.getElementById("publishDialog")
  const confirmPublishBtn = document.getElementById("confirmPublishBtn")
  const saveSuccessAlert = document.getElementById("saveSuccessAlert")
  const maxQuestionsAlert = document.getElementById("maxQuestionsAlert")
  const questionCountElement = document.getElementById("questionCount")
  const tabItems = document.querySelectorAll(".tab-item")
  const tabContents = document.querySelectorAll(".tab-content")
  const previewTitle = document.getElementById("previewTitle")
  const previewForm = document.getElementById("previewForm")

  // Form elements
  const formBuilderForm = document.getElementById("formBuilderForm")
  const actionInput = document.getElementById("action")
  const formTitleHidden = document.getElementById("formTitleHidden")
  const formTypeHidden = document.getElementById("formTypeHidden")
  const questionsHidden = document.getElementById("questionsHidden")

  // Templates
  const questionTemplate = document.getElementById("questionTemplate").innerHTML
  const optionTemplate = document.getElementById("optionTemplate").innerHTML

  // Show success alerts on page load
  if (saveSuccessAlert) {
    saveSuccessAlert.style.display = "flex"
    setTimeout(() => {
      saveSuccessAlert.style.display = "none"
      // Clean URL
      const url = new URL(window.location)
      url.searchParams.delete("success")
      url.searchParams.delete("error")
      url.searchParams.delete("action")
      window.history.replaceState({}, document.title, url)
    }, 5000)
  }

  // Initialize SortableJS
  const Sortable = window.Sortable // Declare the Sortable variable
  if (Sortable) {
    new Sortable(questionsList, {
      animation: 150,
      handle: ".drag-handle",
      onEnd: () => updateQuestionNumbers(),
    })
  }

  // Event Listeners
  if (formTitle) {
    formTitle.addEventListener("input", function () {
      previewTitle.textContent = this.value
    })
  }

  if (formType) {
    formType.addEventListener("change", function () {
      const type = this.value
      const newTitle = type === "member" ? "Đơn đăng ký thành viên" : "Đơn đăng ký tham gia sự kiện"
      formTitle.value = newTitle
      previewTitle.textContent = newTitle
      updatePreview()
    })
  }

  // Add question buttons
  if (addQuestionBtn) {
    addQuestionBtn.addEventListener("click", () => {
      if (questionCount >= 20) {
        showAlert(maxQuestionsAlert)
        return
      }
      addNewQuestion("text")
    })
  }

  if (addInfoBtn) {
    addInfoBtn.addEventListener("click", () => {
      if (questionCount >= 20) {
        showAlert(maxQuestionsAlert)
        return
      }
      addNewQuestion("info")
    })
  }

  // Save and publish buttons
  if (saveBtn) {
    saveBtn.addEventListener("click", () => submitForm("save"))
  }

  if (publishBtn) {
    publishBtn.addEventListener("click", () => {
      if (publishDialog) {
        publishDialog.classList.add("open")
      }
    })
  }

  // Dialog handlers
  if (publishDialog) {
    publishDialog.querySelector(".dialog-overlay")?.addEventListener("click", () => {
      publishDialog.classList.remove("open")
    })

    publishDialog.querySelector(".dialog-close")?.addEventListener("click", () => {
      publishDialog.classList.remove("open")
    })

    publishDialog.querySelector(".dialog-cancel")?.addEventListener("click", () => {
      publishDialog.classList.remove("open")
    })
  }

  if (confirmPublishBtn) {
    confirmPublishBtn.addEventListener("click", () => {
      submitForm("publish")
      if (publishDialog) {
        publishDialog.classList.remove("open")
      }
    })
  }

  // Tab switching
  tabItems.forEach((tab) => {
    tab.addEventListener("click", function () {
      const tabId = this.getAttribute("data-tab")

      tabItems.forEach((t) => t.classList.remove("active"))
      this.classList.add("active")

      tabContents.forEach((content) => {
        content.classList.remove("active")
        if (content.id === tabId + "Tab") {
          content.classList.add("active")
        }
      })

      activeTab = tabId
      if (tabId === "preview") {
        updatePreview()
      }
    })
  })

  // Functions
  function showAlert(alertElement) {
    if (alertElement) {
      alertElement.style.display = "flex"
      setTimeout(() => {
        alertElement.style.display = "none"
      }, 3000)
    }
  }
  function submitForm(action) {
    if (!formBuilderForm) return
    actionInput.value = action
    formTitleHidden.value = formTitle.value
    formTypeHidden.value = formType.value
    questionsHidden.value = JSON.stringify(getFormData())
    if (formTitleHidden.value.trim() === "") {
      alert("Tiêu đề không được để trống!");
      return;
    }
    formBuilderForm.submit()
  }

  function addNewQuestion(type = "text") {
    questionCount++
    questionCountElement.textContent = questionCount

    const id = "q" + Date.now()
    const html = questionTemplate.replace(/{{id}}/g, id).replace(/{{number}}/g, questionCount)

    const tempDiv = document.createElement("div")
    tempDiv.innerHTML = html
    const newQuestion = tempDiv.firstElementChild

    const typeSelect = newQuestion.querySelector(".question-type")
    typeSelect.value = type

    toggleQuestionFields(newQuestion, type)
    questionsList.appendChild(newQuestion)
    addQuestionEventListeners(newQuestion)

    if (questionCount >= 20) {
      addQuestionBtn.disabled = true
      addInfoBtn.disabled = true
    }

    updatePreview()
  }

  function toggleQuestionFields(questionCard, type) {
    const placeholderGroup = questionCard.querySelector(".placeholder-group")
    const infoGroup = questionCard.querySelector(".info-group")
    const optionsContainer = questionCard.querySelector(".options-container")

    // Hide all groups first
    if (placeholderGroup) placeholderGroup.style.display = "none"
    if (infoGroup) infoGroup.style.display = "none"
    if (optionsContainer) optionsContainer.style.display = "none"

    if (type === "info" && infoGroup) {
      infoGroup.style.display = "block"
    } else if (["radio", "checkbox", "select"].includes(type) && optionsContainer) {
      optionsContainer.style.display = "block"
      const optionsList = optionsContainer.querySelector(".options-list")
      if (optionsList && optionsList.children.length === 0) {
        addOption(questionCard, "Tùy chọn 1")
        addOption(questionCard, "Tùy chọn 2")
      }
    } else if (placeholderGroup) {
      placeholderGroup.style.display = "block"
    }
  }

  function addQuestionEventListeners(questionCard) {
    const isRequiredQuestion = questionCard.classList.contains("required-question")

    // Question type change
    const typeSelect = questionCard.querySelector(".question-type")
    if (typeSelect) {
      typeSelect.addEventListener("change", function () {
        toggleQuestionFields(questionCard, this.value)
        updatePreview()
      })
    }

    // Image upload handling
    setupImageUpload(questionCard)

    // Add option button
    const addOptionBtn = questionCard.querySelector(".add-option")
    if (addOptionBtn) {
      addOptionBtn.addEventListener("click", () => {
        const optionsList = questionCard.querySelector(".options-list")
        const optionCount = optionsList.children.length + 1
        addOption(questionCard, `Tùy chọn ${optionCount}`)
        updatePreview()
      })
    }

    // Move and delete buttons (only for non-required questions)
    if (!isRequiredQuestion) {
      const moveUpBtn = questionCard.querySelector(".move-up")
      const moveDownBtn = questionCard.querySelector(".move-down")
      const deleteBtn = questionCard.querySelector(".delete")

      if (moveUpBtn) {
        moveUpBtn.addEventListener("click", () => {
          const prevQuestion = questionCard.previousElementSibling
          if (prevQuestion && !prevQuestion.classList.contains("required-question")) {
            questionsList.insertBefore(questionCard, prevQuestion)
            updateQuestionNumbers()
            updatePreview()
          }
        })
      }

      if (moveDownBtn) {
        moveDownBtn.addEventListener("click", () => {
          const nextQuestion = questionCard.nextElementSibling
          if (nextQuestion) {
            questionsList.insertBefore(nextQuestion, questionCard)
            updateQuestionNumbers()
            updatePreview()
          }
        })
      }

      if (deleteBtn) {
        deleteBtn.addEventListener("click", () => {
          if (questionCount <= REQUIRED_QUESTIONS_COUNT) return

          questionCard.remove()
          questionCount--
          questionCountElement.textContent = questionCount

          if (questionCount < 20) {
            addQuestionBtn.disabled = false
            addInfoBtn.disabled = false
          }

          updateQuestionNumbers()
          updatePreview()
        })
      }
    }

    // Duplicate button
    const duplicateBtn = questionCard.querySelector(".duplicate")
    if (duplicateBtn) {
      duplicateBtn.addEventListener("click", () => {
        if (questionCount >= 20) {
          showAlert(maxQuestionsAlert)
          return
        }

        questionCount++
        questionCountElement.textContent = questionCount

        const clone = questionCard.cloneNode(true)
        const newId = "q" + Date.now()
        clone.setAttribute("data-id", newId)

        // Remove required-question class and badges
        clone.classList.remove("required-question")
        const requiredBadge = clone.querySelector(".required-badge")
        if (requiredBadge) requiredBadge.remove()

        // Update IDs
        const requiredCheckbox = clone.querySelector(".question-required")
        if (requiredCheckbox) {
          requiredCheckbox.id = "required-" + newId
          const label = requiredCheckbox.nextElementSibling
          if (label) label.setAttribute("for", "required-" + newId)
        }

        questionsList.appendChild(clone)
        addQuestionEventListeners(clone)
        updateQuestionNumbers()

        if (questionCount >= 20) {
          addQuestionBtn.disabled = true
          addInfoBtn.disabled = true
        }

        updatePreview()
      })
    }

    // Input changes
    questionCard.querySelectorAll("input, select, textarea").forEach((input) => {
      input.addEventListener("change", updatePreview)
      input.addEventListener("input", updatePreview)
    })
  }

  function setupImageUpload(questionCard) {
    const imageInput = questionCard.querySelector(".question-image-file")
    const imageUploadArea = questionCard.querySelector(".image-upload-area")
    const imagePreviewContainer = questionCard.querySelector(".image-preview-container")
    const imagePreview = questionCard.querySelector(".uploaded-image-preview")
    const uploadPlaceholder = questionCard.querySelector(".upload-placeholder")
    const changeImageBtn = questionCard.querySelector(".change-image")
    const removeImageBtn = questionCard.querySelector(".remove-image")

    if (!imageInput) return

    imageInput.addEventListener("change", (e) => {
      const file = e.target.files[0]
      if (file) {
        handleImageUpload(file, imagePreviewContainer, imagePreview, uploadPlaceholder, questionCard)
      }
    })

    if (imageUploadArea) {
      imageUploadArea.addEventListener("dragover", (e) => {
        e.preventDefault()
        imageUploadArea.classList.add("dragover")
      })

      imageUploadArea.addEventListener("dragleave", () => {
        imageUploadArea.classList.remove("dragover")
      })

      imageUploadArea.addEventListener("drop", (e) => {
        e.preventDefault()
        imageUploadArea.classList.remove("dragover")
        const file = e.dataTransfer.files[0]
        if (file) {
          handleImageUpload(file, imagePreviewContainer, imagePreview, uploadPlaceholder, questionCard)
        }
      })
    }

    if (changeImageBtn) {
      changeImageBtn.addEventListener("click", () => imageInput.click())
    }

    if (removeImageBtn) {
      removeImageBtn.addEventListener("click", () => {
        imageInput.value = ""
        if (imagePreviewContainer) imagePreviewContainer.style.display = "none"
        if (uploadPlaceholder) uploadPlaceholder.style.display = "block"
        if (imagePreview) imagePreview.src = ""
        questionCard.dataset.imageData = ""
        updatePreview()
      })
    }
  }

  function handleImageUpload(file, imagePreviewContainer, imagePreview, uploadPlaceholder, questionCard) {
    const validTypes = ["image/jpeg", "image/png", "image/gif"]
    if (!validTypes.includes(file.type)) {
      alert("Vui lòng chọn file ảnh có định dạng JPG, PNG hoặc GIF.")
      return
    }

    const maxSize = 5 * 1024 * 1024 // 5MB
    if (file.size > maxSize) {
      alert("Kích thước file tối đa là 5MB.")
      return
    }

    const reader = new FileReader()
    reader.onload = (e) => {
      const imageData = e.target.result
      if (imagePreview) imagePreview.src = imageData
      if (imagePreviewContainer) imagePreviewContainer.style.display = "block"
      if (uploadPlaceholder) uploadPlaceholder.style.display = "none"
      questionCard.dataset.imageData = imageData
      updatePreview()
    }
    reader.readAsDataURL(file)
  }

  function addOption(questionCard, value) {
    const optionsList = questionCard.querySelector(".options-list")
    if (!optionsList) return

    const optionCount = optionsList.children.length + 1
    const optionId = "opt" + Date.now() + "-" + optionCount

    const html = optionTemplate
        .replace(/{{id}}/g, optionId)
        .replace(/{{number}}/g, optionCount)
        .replace(/{{value}}/g, value)

    const tempDiv = document.createElement("div")
    tempDiv.innerHTML = html
    const newOption = tempDiv.firstElementChild

    optionsList.appendChild(newOption)

    // Delete option button
    const deleteBtn = newOption.querySelector(".delete-option")
    if (deleteBtn) {
      deleteBtn.addEventListener("click", () => {
        if (optionsList.children.length <= 1) return

        newOption.remove()
        Array.from(optionsList.children).forEach((option, index) => {
          const numberEl = option.querySelector(".option-number")
          if (numberEl) numberEl.textContent = index + 1 + "."
        })
        updatePreview()
      })
    }

    // Option value change
    const valueInput = newOption.querySelector(".option-value")
    if (valueInput) {
      valueInput.addEventListener("input", updatePreview)
    }
  }

  function updateQuestionNumbers() {
    Array.from(questionsList.children).forEach((question, index) => {
      const numberEl = question.querySelector(".question-number")
      if (numberEl) numberEl.textContent = index + 1
    })
  }

  function getFormData() {
    const questions = [];
    document.querySelectorAll(".question-card").forEach(questionCard => {
      const question = {
        id: questionCard.getAttribute("data-id"),
        type: questionCard.querySelector(".question-type").value,
        label: questionCard.querySelector(".question-label").value,
        required: questionCard.querySelector(".question-required").checked,
        placeholder: questionCard.querySelector(".question-placeholder")?.value || ""
      };
      if (question.type === "info") {
        const infoContent = questionCard.querySelector(".question-content")?.value || ""; // Lấy nội dung từ textarea
        const imageData = questionCard.dataset.imageData || ""; // Lấy dữ liệu ảnh từ dataset
        question.options = JSON.stringify({
          content: infoContent,
          image: imageData
        });
      } else if (["radio", "checkbox", "select"].includes(question.type)) {
        question.options = [];
        questionCard.querySelectorAll(".option-item").forEach(optionItem => {
          question.options.push({
            id: optionItem.getAttribute("data-id"),
            value: optionItem.querySelector(".option-value").value
          });
        });
      }
      questions.push(question);
    });
    return questions;
  }

  function updatePreview() {
    if (activeTab !== "preview" || !previewForm) return;

    if (previewTitle && formTitle) {
      previewTitle.textContent = formTitle.value || "Tiêu đề mặc định";
    }

    previewForm.innerHTML = "";
    const questions = getFormData();

    questions.forEach((question) => {
      const fieldset = document.createElement("fieldset");
      fieldset.className = "form-group";

      const legend = document.createElement("label");
      legend.className = "form-label";
      legend.innerHTML = question.label;
      if (question.required) {
        legend.innerHTML += ' <span style="color: red;">*</span>';
      }
      fieldset.appendChild(legend);

      if (question.type === "info") {
        const infoDiv = document.createElement("div");
        infoDiv.className = "info-preview";
        if (question.options) {
          try {
            const infoData = JSON.parse(question.options);
            if (infoData.content) {
              const contentP = document.createElement("p");
              contentP.textContent = infoData.content;
              infoDiv.appendChild(contentP);
            }
            if (infoData.image) {
              const img = document.createElement("img");
              img.src = infoData.image;
              img.style.maxWidth = "100%";
              img.alt = "Thông tin hình ảnh";
              img.onerror = function () {
                this.style.display = "none";
                console.error("Lỗi tải hình ảnh:", this.src);
              };
              infoDiv.appendChild(img);
            }
          } catch (e) {
            console.error("Lỗi parse JSON trong options của câu hỏi Info:", e, question.options);
          }
        }
        fieldset.appendChild(infoDiv);
      } else {
        const input = createInputElement(question);
        if (input) {
          fieldset.appendChild(input);
        }
      }

      previewForm.appendChild(fieldset);
    });
  }

  function createInputElement(question) {
    let input;

    switch (question.type) {
      case "text":
      case "email":
      case "tel":
      case "number":
      case "date":
        input = document.createElement("input");
        input.type = question.type;
        input.className = "form-input";
        if (question.placeholder) input.placeholder = question.placeholder;
        break;

      case "textarea":
        input = document.createElement("textarea");
        input.className = "form-textarea";
        input.rows = 4;
        if (question.placeholder) input.placeholder = question.placeholder;
        break;

      case "radio":
        input = document.createElement("div");
        input.className = "radio-group";
        question.options?.forEach((option) => {
          const wrapper = document.createElement("div");
          wrapper.className = "radio-wrapper";

          const radio = document.createElement("input");
          radio.type = "radio";
          radio.name = `radio-${question.id}`;
          radio.id = `radio-${question.id}-${option.id}`;
          radio.value = option.id;

          const label = document.createElement("label");
          label.setAttribute("for", `radio-${question.id}-${option.id}`);
          label.textContent = option.value;

          wrapper.appendChild(radio);
          wrapper.appendChild(label);
          input.appendChild(wrapper);
        });
        break;

      case "checkbox":
        input = document.createElement("div");
        input.className = "checkbox-group";
        question.options?.forEach((option) => {
          const wrapper = document.createElement("div");
          wrapper.className = "checkbox-wrapper";

          const checkbox = document.createElement("input");
          checkbox.type = "checkbox";
          checkbox.id = `checkbox-${question.id}-${option.id}`;
          checkbox.value = option.id;

          const label = document.createElement("label");
          label.setAttribute("for", `checkbox-${question.id}-${option.id}`);
          label.textContent = option.value;

          wrapper.appendChild(checkbox);
          wrapper.appendChild(label);
          input.appendChild(wrapper);
        });
        break;

      case "select":
        input = document.createElement("select");
        input.className = "form-select";

        const placeholder = document.createElement("option");
        placeholder.value = "";
        placeholder.textContent = "Chọn một tùy chọn";
        placeholder.disabled = true;
        placeholder.selected = true;
        input.appendChild(placeholder);

        question.options?.forEach((option) => {
          const optionEl = document.createElement("option");
          optionEl.value = option.id;
          optionEl.textContent = option.value;
          input.appendChild(optionEl);
        });
        break;

      case "info":
        input = document.createElement("div");
        input.className = "info-field";
        if (question.options) {
          try {
            const infoData = JSON.parse(question.options);
            if (infoData.content) {
              const contentDiv = document.createElement("div");
              contentDiv.className = "info-content";
              contentDiv.textContent = infoData.content;
              input.appendChild(contentDiv);
            }
            if (infoData.image) {
              const img = document.createElement("img");
              img.src = infoData.image;
              img.alt = "Thông tin hình ảnh";
              img.className = "info-image";
              img.onerror = function () {
                this.style.display = "none";
              };
              input.appendChild(img);
            }
          } catch (e) {
            console.error("Lỗi parse JSON trong options của câu hỏi Info:", e);
          }
        }
        break;
    }

    return input;
  }

  // Add event listeners to existing questions
  document.querySelectorAll(".question-card").forEach((question) => {
    addQuestionEventListeners(question)
  })

  // Initial preview update
  updatePreview()
})

// Global function for event field toggle
function toggleEventField(selectElement) {
  const eventNameField = document.getElementById("eventNameField")
  if (eventNameField) {
    eventNameField.style.display = selectElement.value === "event" ? "block" : "none"
  }
}
