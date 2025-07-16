document.addEventListener("DOMContentLoaded", () => {
  // Tab switching
  const tabButtons = document.querySelectorAll(".tab-btn")
  const tabContents = document.querySelectorAll(".tab-content")

  tabButtons.forEach((button) => {
    button.addEventListener("click", () => {
      // Remove active class from all buttons and contents
      tabButtons.forEach((btn) => btn.classList.remove("active"))
      tabContents.forEach((content) => content.classList.remove("active"))

      // Add active class to clicked button and corresponding content
      button.classList.add("active")
      const tabId = button.getAttribute("data-tab")
      document.getElementById(`${tabId}-tab`).classList.add("active")

      // Re-apply filter after tab switch
      if (formTypeFilter) {
        filterForms()
      }
    })
  })
  // Search and filter functionality
  const formTypeFilter = document.getElementById("formTypeFilter")
  
  // Định nghĩa hàm filterForms ngoài event listener để tránh lỗi
  function filterForms() {
    if (!formTypeFilter) return; // Kiểm tra tồn tại của formTypeFilter
    
    const selectedType = formTypeFilter.value
    const formCards = document.querySelectorAll(".form-card")
    const formsGrids = document.querySelectorAll(".forms-grid")
    
    console.log("Filtering forms by type:", selectedType);

    // Apply or remove filtered class to the form grids
    formsGrids.forEach(grid => {
      if (selectedType) {
        grid.classList.add('filtered')
      } else {
        grid.classList.remove('filtered')
      }
    })
    
    let visibleCount = 0
    // Áp dụng bộ lọc cho từng thẻ form
    formCards.forEach((card) => {
      const type = card.getAttribute("data-form-type")
      // Kiểm tra nếu loại form khớp với bộ lọc hoặc không có bộ lọc nào được chọn
      const shouldShow = !selectedType || type === selectedType
      
      if (shouldShow) {
        card.style.display = "block"
        card.classList.add('show')
        visibleCount++
      } else {
        card.style.display = "none"
        card.classList.remove('show')
      }
    })
    
    // Show a message if no matching forms
    const currentTab = document.querySelector('.tab-content.active')
    let emptyMessage = currentTab.querySelector('.filter-empty-state')
    
    if (visibleCount === 0 && selectedType) {
      if (!emptyMessage) {
        emptyMessage = document.createElement('div')
        emptyMessage.className = 'empty-state filter-empty-state'
        emptyMessage.innerHTML = `
          <i class="fas fa-filter"></i>
          <h3>Không có form loại "${selectedType === 'Club' ? 'Đăng ký thành viên' : 'Đăng ký sự kiện'}"</h3>
          <p>Thử chọn loại form khác hoặc tạo form mới.</p>
        `
        currentTab.querySelector('.forms-grid').appendChild(emptyMessage)
      }
    } else if (emptyMessage) {
      emptyMessage.remove()
    }
  }
  
  // Thêm sự kiện change cho bộ lọc
  if (formTypeFilter) {
    formTypeFilter.addEventListener("change", filterForms)
    // Gọi filterForms ngay lập tức để áp dụng bất kỳ bộ lọc nào được chọn sẵn
    filterForms()
  }

  // Get context path for proper URL construction
  const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2)) || ""
  console.log("Context path:", contextPath)
  // Edit form
  const editButtons = document.querySelectorAll(".edit-form")
  editButtons.forEach((button) => {
    button.addEventListener("click", () => {
      const templateId = button.getAttribute("data-template-id")
      // Get clubId from URL parameters
      const urlParams = new URLSearchParams(window.location.search)
      const clubId = urlParams.get('clubId')
      window.location.href = `${contextPath}/formBuilder?templateId=${templateId}&clubId=${clubId}`
    })
  })
  // Publish form
  const publishButtons = document.querySelectorAll(".publish-form")
  publishButtons.forEach((button) => {
    button.addEventListener("click", () => {
      const templateId = button.getAttribute("data-template-id")
      performAction("publish", templateId, "Đang xuất bản form...")
    })
  })

  // Unpublish form
  const unpublishButtons = document.querySelectorAll(".unpublish-form")
  const unpublishModal = document.getElementById("unpublishModal")
  const unpublishFormTitle = document.getElementById("unpublishFormTitle")
  let currentTemplateId = null
  let currentFormTitle = ""

  unpublishButtons.forEach((button) => {
    button.addEventListener("click", () => {
      currentTemplateId = button.getAttribute("data-template-id")
      currentFormTitle = button.getAttribute("data-form-title")
      unpublishFormTitle.textContent = currentFormTitle
      unpublishModal.style.display = "block"
    })
  })

  // Delete form
  const deleteButtons = document.querySelectorAll(".delete-form")
  const deleteModal = document.getElementById("deleteModal")
  const deleteFormTitle = document.getElementById("deleteFormTitle")

  deleteButtons.forEach((button) => {
    button.addEventListener("click", () => {
      currentTemplateId = button.getAttribute("data-template-id")
      currentFormTitle = button.getAttribute("data-form-title")
      deleteFormTitle.textContent = currentFormTitle
      deleteModal.style.display = "block"
    })
  })
  // View responses
  const viewResponsesButtons = document.querySelectorAll(".view-responses")
  viewResponsesButtons.forEach((button) => {
    button.addEventListener("click", () => {
      const templateId = button.getAttribute("data-template-id")
      const clubId = button.getAttribute("data-club-id")
      const formType = button.getAttribute("data-form-type")
      window.location.href = `${contextPath}/formResponses?templateId=${templateId}&clubId=${clubId}&formType=${formType}`
    })
  })

  // Copy link
  const copyLinkButtons = document.querySelectorAll(".copy-link")
  copyLinkButtons.forEach((button) => {
    button.addEventListener("click", () => {
      const templateId = button.getAttribute("data-template-id")
      const publicLink = `${window.location.origin}${contextPath}/applicationForm?templateId=${templateId}`

      navigator.clipboard
          .writeText(publicLink)
          .then(() => {
            showToast("Link đã được sao chép vào clipboard!", "success")
          })
          .catch((err) => {
            console.error("Could not copy text: ", err)
            showToast("Không thể sao chép link", "error")
          })
    })
  })
  // Modal event handlers
  function setupModalHandlers() {
    // Delete modal
    const confirmDelete = document.querySelector(".confirm-delete")
    const cancelDelete = document.querySelector(".cancel-delete")

    if (confirmDelete) {
      confirmDelete.addEventListener("click", () => {
        if (currentTemplateId) {
          performAction("delete", currentTemplateId, "Đang xóa form...")
          deleteModal.style.display = "none"
        }
      })
    }

    if (cancelDelete) {
      cancelDelete.addEventListener("click", () => {
        deleteModal.style.display = "none"
      })
    }

    // Unpublish modal
    const confirmUnpublish = document.querySelector(".confirm-unpublish")
    const cancelUnpublish = document.querySelector(".cancel-unpublish")

    if (confirmUnpublish) {
      confirmUnpublish.addEventListener("click", () => {
        if (currentTemplateId) {
          performAction("unpublish", currentTemplateId, "Đang hủy xuất bản form...")
          unpublishModal.style.display = "none"
        }
      })
    }

    if (cancelUnpublish) {
      cancelUnpublish.addEventListener("click", () => {
        unpublishModal.style.display = "none"
      })
    }

    // Close modals with X button
    document.querySelectorAll(".close").forEach((closeBtn) => {
      closeBtn.addEventListener("click", () => {
        closeBtn.closest(".modal").style.display = "none"
      })
    })

    // Close modals when clicking outside
    window.addEventListener("click", (event) => {
      if (event.target === deleteModal) deleteModal.style.display = "none"
      if (event.target === unpublishModal) unpublishModal.style.display = "none"
    })
  }

  setupModalHandlers()

  // Helper function to perform actions
  function performAction(action, templateId, loadingMessage) {
    console.log(`Performing action: ${action} for templateId: ${templateId}`)
    showLoading(loadingMessage)

    const url = `${contextPath}/formManagement`
    console.log(`Making request to: ${url}`)

    fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: `action=${action}&templateId=${templateId}`,
    })
        .then((response) => {
          console.log(`Response status: ${response.status}`)
          console.log(`Response headers:`, response.headers)

          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`)
          }

          // Check if response is JSON
          const contentType = response.headers.get("content-type")
          if (!contentType || !contentType.includes("application/json")) {
            throw new Error("Response is not JSON")
          }

          return response.json()
        })
        .then((data) => {
          console.log("Response data:", data)
          hideLoading()

          if (data.success) {
            showToast(data.message, "success")
            setTimeout(() => {
              window.location.reload()
            }, 1500)
          } else {
            showToast(data.message || "Có lỗi xảy ra", "error")
          }
        })
        .catch((error) => {
          hideLoading()
          console.error("Error details:", error)

          if (error.message.includes("HTTP error! status: 404")) {
            showToast("Không tìm thấy servlet. Kiểm tra URL mapping.", "error")
          } else if (error.message.includes("Response is not JSON")) {
            showToast("Server trả về HTML thay vì JSON. Kiểm tra servlet.", "error")
          } else {
            showToast("Có lỗi xảy ra khi thực hiện hành động: " + error.message, "error")
          }
        })
  }

  function showToast(message, type) {
    console.log(`Toast: ${type} - ${message}`)

    // Fallback nếu không có toast element
    const toast = document.getElementById(type + "Toast")
    if (toast) {
      const messageElement = toast.querySelector(".toast-message")
      if (messageElement) {
        messageElement.textContent = message
        toast.classList.add("show")

        setTimeout(() => {
          toast.classList.remove("show")
        }, 3000)
      }
    } else {
      // Fallback với alert nếu không có toast
      alert(`${type.toUpperCase()}: ${message}`)
    }
  }

  function showLoading(message = "Đang xử lý...") {
    console.log("Loading:", message)
    document.body.style.cursor = "wait"
    // Có thể thêm loading spinner ở đây
  }

  function hideLoading() {
    console.log("Loading finished")
    document.body.style.cursor = "default"
  }
})
