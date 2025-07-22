window.addEventListener('error', function (event) {
    console.error('Lỗi JS toàn cục:', event.error);
});
// Parse dữ liệu JSON
function safeParseJson(jsonString, defaultValue = {}) {
  try {
    if (!jsonString || typeof jsonString !== "string") {
      return defaultValue;
    }
    return JSON.parse(jsonString);
  } catch (e) {
    console.error("Lỗi phân tích JSON:", e, "Chuỗi gốc:", jsonString);
    return defaultValue;
  }
}

function logDebug(...args) {
  console.log("[Debug]", ...args);
}

function logError(...args) {
  console.error("[Error]", ...args);
}

document.addEventListener("DOMContentLoaded", function () {
  logDebug("DOM Content Loaded - Bắt đầu khởi tạo viewFeedback.js");

  const chartColors = {
    ratingVeryGood: "#2e7d32",
    ratingGood: "#4caf50",
    ratingNeutral: "#ffc107",
    ratingBad: "#ff5252",
    ratingVeryBad: "#b71c1c",
    gridColor: "#e5e5e5",
  };

  // Nhãn phân loại đánh giá
  const ratingLabels = {
    5: "Rất hài lòng",
    4: "Hài lòng",
    3: "Bình thường",
    2: "Không hài lòng",
    1: "Rất không hài lòng",
  };

  function showUsersWithRating(stars) {
    // Lấy tất cả người dùng đã đánh giá số sao này
    const userElements = document.querySelectorAll(
      `.feedback-item[data-rating="${stars}"] .user-name`
    );
    const userNames = [];

    userElements.forEach((el) => {
      const nameText = el.innerText.trim();
      userNames.push(nameText);
    });

    // Tạo và hiển thị một modal/popup với danh sách người dùng
    if (userNames.length > 0) {
      const filteredNames = userNames.filter(
        (name) => !name.includes("ẩn danh")
      );

      // Tạo lớp phủ modal
      const modalOverlay = document.createElement("div");
      modalOverlay.style.position = "fixed";
      modalOverlay.style.top = "0";
      modalOverlay.style.left = "0";
      modalOverlay.style.width = "100%";
      modalOverlay.style.height = "100%";
      modalOverlay.style.backgroundColor = "rgba(0,0,0,0.5)";
      modalOverlay.style.zIndex = "9999";
      modalOverlay.style.display = "flex";
      modalOverlay.style.justifyContent = "center";
      modalOverlay.style.alignItems = "center";

      // Tạo nội dung modal
      const modalContent = document.createElement("div");
      modalContent.style.backgroundColor = "white";
      modalContent.style.borderRadius = "8px";
      modalContent.style.padding = "20px";
      modalContent.style.maxWidth = "600px";
      modalContent.style.width = "80%";
      modalContent.style.maxHeight = "80vh";
      modalContent.style.overflowY = "auto";
      modalContent.style.boxShadow = "0 4px 12px rgba(0,0,0,0.15)";

      let ratingColor;
      switch (stars) {
        case 5:
          ratingColor = chartColors.ratingVeryGood;
          break;
        case 4:
          ratingColor = chartColors.ratingGood;
          break;
        case 3:
          ratingColor = chartColors.ratingNeutral;
          break;
        case 2:
          ratingColor = chartColors.ratingBad;
          break;
        case 1:
          ratingColor = chartColors.ratingVeryBad;
          break;
        default:
          ratingColor = "#333";
      }

      // Tạo tiêu đề với cùng bảng màu như tooltip
      modalContent.innerHTML = `
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
                    <h3 style="margin: 0; color: ${ratingColor};">${stars} sao - ${ratingLabels[stars]}</h3>
                    <button id="closeModal" style="background: none; border: none; cursor: pointer; font-size: 20px;">✖</button>
                </div>
                <p>Danh sách người đánh giá ${stars} sao (${userNames.length} người):</p>
            `;

      // Tạo danh sách người dùng
      const userList = document.createElement("ul");
      userList.style.listStyleType = "none";
      userList.style.padding = "0";
      userList.style.margin = "0";

      userNames.forEach((name) => {
        const listItem = document.createElement("li");
        listItem.style.padding = "8px 0";
        listItem.style.borderBottom = "1px solid #eee";
        listItem.innerHTML = name;
        userList.appendChild(listItem);
      });

      modalContent.appendChild(userList);
      modalOverlay.appendChild(modalContent);
      document.body.appendChild(modalOverlay);

      document
        .getElementById("closeModal")
        .addEventListener("click", function () {
          document.body.removeChild(modalOverlay);
        });

      modalOverlay.addEventListener("click", function (e) {
        if (e.target === modalOverlay) {
          document.body.removeChild(modalOverlay);
        }
      });
    } else {
      alert(`Không có người dùng nào đánh giá ${stars} sao.`);
    }
  }

  // Làm hàm có sẵn toàn cục để Chart.js có thể sử dụng
  window.showUsersWithRating = showUsersWithRating;

  const criteriaDescriptions = {
    "Mức độ tổ chức": "Mức độ tổ chức và chuẩn bị của hoạt động",
    "Sự rõ ràng": "Sự rõ ràng trong thông tin truyền đạt trước khi tham gia",
    "Thái độ hỗ trợ": "Thái độ và sự hỗ trợ của ban tổ chức",
    "Sự phù hợp": "Sự phù hợp giữa nội dung và mong đợi",
    "Chào đón và hòa nhập": "Mức độ bạn cảm thấy được chào đón và hòa nhập",
    "Giá trị hoạt động": "Giá trị hoặc kiến thức bạn nhận được từ hoạt động",
    "Sự hợp lý": "Sự hợp lý về thời gian, thời lượng, lịch trình",
    "Cơ hội tham gia": "Cơ hội để bạn tham gia, đóng góp, thể hiện",
    "Mức độ tiếp tục":
      "Mức độ bạn muốn tiếp tục tham gia các hoạt động của CLB trong tương lai",
  };

  // Biến toàn cục để lưu trữ instance của biểu đồ chi tiết
  let detailedRatingChartInstance = null;

  // Biểu đồ đánh giá chi tiết theo tiêu chí
  const renderDetailedRatingChart = () => {
    const detailedRatingChartEl = document.getElementById("detailedRatingChart");
    if (!detailedRatingChartEl) return;

    // Hủy biểu đồ cũ nếu tồn tại
    if (detailedRatingChartInstance) {
      detailedRatingChartInstance.destroy();
      detailedRatingChartInstance = null;
    }

    // Lấy dữ liệu từ script type="application/json"
    let criteriaData = {};
    try {
      // Lấy dữ liệu từ script JSON
      const jsonScriptElement = document.getElementById("detailedRatingData");
      if (!jsonScriptElement) {
        logDebug("Không tìm thấy phần tử script#detailedRatingData");
        throw new Error("Không có script JSON chứa dữ liệu tiêu chí");
      }

      criteriaData = JSON.parse(jsonScriptElement.textContent.trim());
    } catch (error) {
      logError("Lỗi phân tích dữ liệu tiêu chí:", error);

      // Hiển thị thông báo lỗi
      const errorDiv = document.createElement("div");
      errorDiv.className = "chart-error";
      errorDiv.innerHTML = `<div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i> Lỗi khi hiển thị biểu đồ chi tiết: ${error.message}
            </div>`;
      detailedRatingChartEl.parentNode.appendChild(errorDiv);

      // Tạo một đối tượng dữ liệu trống
      criteriaData = {
        q1Organization: 0,
        q2Communication: 0,
        q3Support: 0,
        q4Relevance: 0,
        q5Welcoming: 0,
        q6Value: 0,
        q7Timing: 0,
        q8Participation: 0,
        q9WillingnessToReturn: 0,
      };
    }

    const criteriaLabels = [
      "Mức độ tổ chức",
      "Sự rõ ràng",
      "Thái độ hỗ trợ",
      "Sự phù hợp",
      "Chào đón và hòa nhập",
      "Giá trị hoạt động",
      "Sự hợp lý",
      "Cơ hội tham gia",
      "Mức độ tiếp tục",
    ];

    const criteriaValues = [
      criteriaData.q1Organization || 0,
      criteriaData.q2Communication || 0,
      criteriaData.q3Support || 0,
      criteriaData.q4Relevance || 0,
      criteriaData.q5Welcoming || 0,
      criteriaData.q6Value || 0,
      criteriaData.q7Timing || 0,
      criteriaData.q8Participation || 0,
      criteriaData.q9WillingnessToReturn || 0,
    ];
    const detailedData = criteriaLabels.map((label, index) => {
      return {
        name: label,
        avgRating: criteriaValues[index],
      };
    });

    const detailedChartData = {
      labels: criteriaLabels,
      datasets: [
        {
          label: "Điểm đánh giá trung bình",
          data: criteriaValues,
          backgroundColor: criteriaValues.map((value) => {
            // Màu sắc dựa trên giá trị trung bình
            if (value >= 4.5)
              return chartColors.ratingVeryGood; // Rất hài lòng (4.5-5)
            else if (value >= 3.5)
              return chartColors.ratingGood; // Hài lòng (3.5-4.5)
            else if (value >= 2.5)
              return chartColors.ratingNeutral; // Bình thường (2.5-3.5)
            else if (value >= 1.5)
              return chartColors.ratingBad; // Không hài lòng (1.5-2.5)
            else return chartColors.ratingVeryBad; // Rất không hài lòng (0-1.5)
          }),
          borderWidth: 0,
          borderRadius: 2,
          borderSkipped: false,
          maxBarThickness: 40,
        },
      ],
    };
    // Tạo biểu đồ đánh giá chi tiết
    detailedRatingChartInstance = new Chart(detailedRatingChartEl, {
      type: "bar",
      data: detailedChartData,
      options: {
        responsive: true,
        maintainAspectRatio: false,
        layout: {
          padding: 10,
        },
        scales: {
          x: {
            grid: {
              display: false,
            },
            ticks: {
              color: "#666",
              font: {
                size: 11,
              },
              maxRotation: 45,
              minRotation: 45,
            },
          },
          y: {
            beginAtZero: true,
            min: 0,
            max: 5, // Hiển thị thang điểm từ 0-5
            grace: "5%",
            grid: {
              color: chartColors.gridColor,
              drawTicks: false,
            },
            ticks: {
              color: "#888",
              font: {
                size: 12,
              },
              stepSize: 1,
              // Đảm bảo hiển thị tất cả các giá trị thang điểm từ 0-5
              callback: function (value) {
                // Luôn hiển thị các giá trị 0, 1, 2, 3, 4, 5
                return [0, 1, 2, 3, 4, 5].includes(value) ? value : null;
              },
            },
          },
        },
        plugins: {
          legend: {
            display: false, // Chú thích được tạo bên ngoài trong HTML
          },
          tooltip: {
            enabled: true, // Sử dụng tooltip tích hợp
            backgroundColor: "white",
            titleColor: "#333",
            bodyColor: "#333",
            borderColor: "#ddd",
            borderWidth: 1,
            padding: 12,
            cornerRadius: 8,
            displayColors: false,
            titleFont: {
              size: 14,
              weight: "bold",
            },
            callbacks: {
              title: function (tooltipItems) {
                const index = tooltipItems[0].dataIndex;
                const criteriaName = criteriaLabels[index];
                // Trả về tiêu đề là tên tiêu chí
                return criteriaName;
              },
              label: function (context) {
                const index = context.dataIndex;
                const criteriaName = criteriaLabels[index];
                const value = context.raw;
                const roundedValue = Math.round(value * 100) / 100; // Làm tròn đến 2 chữ số thập phân

                // Xác định mức đánh giá dựa trên giá trị
                let ratingText;
                if (value >= 4.5) ratingText = "Rất hài lòng";
                else if (value >= 3.5) ratingText = "Hài lòng";
                else if (value >= 2.5) ratingText = "Bình thường";
                else if (value >= 1.5) ratingText = "Không hài lòng";
                else ratingText = "Rất không hài lòng";
                const description = criteriaDescriptions[criteriaName];

                // Trả về mảng các dòng để hiển thị trong tooltip
                return [
                  description ? description : criteriaName, // Hiển thị mô tả đầy đủ hoặc tên tiêu chí nếu không có mô tả
                  `Điểm trung bình: ${roundedValue}/5`,
                  `Mức đánh giá: ${ratingText}`,
                ];
              },
            },
          },
        },
        // Tùy chọn bổ sung để làm cho biểu đồ tương tự hơn với thiết kế mẫu
        barPercentage: 0.8,
        categoryPercentage: 0.9,
      },
    });
  };
  // Gọi hàm để render biểu đồ chi tiết
  function cleanupCharts() {
    logDebug("Chart cleanup called");

    if (detailedRatingChartInstance) {
      logDebug("Destroying detailed rating chart instance");
      detailedRatingChartInstance.destroy();
      detailedRatingChartInstance = null;
    }
  }

  // Thêm sự kiện để dọn dẹp biểu đồ khi đóng tab hoặc tải lại trang
  window.addEventListener("beforeunload", cleanupCharts);

  setTimeout(function () {
    renderDetailedRatingChart();
  }, 300);


  const viewDetailButtons = document.querySelectorAll(".view-detail-btn");
  if (viewDetailButtons && viewDetailButtons.length > 0) {
    viewDetailButtons.forEach((button) => {
      button.addEventListener("click", function (e) {
        e.preventDefault();
        const feedbackId = this.getAttribute("data-feedback");
        const detailsSection = document.getElementById(
          `feedback-details-${feedbackId}`
        );

        if (detailsSection) {

          const isVisible = detailsSection.style.display === "block";

          document
            .querySelectorAll(".feedback-detailed-ratings")
            .forEach((section) => {
              section.style.display = "none";
            });

          document.querySelectorAll(".view-detail-btn").forEach((btn) => {
            btn.innerHTML = '<i class="fas fa-chevron-down"></i> Xem chi tiết';
          });


          if (!isVisible) {
            detailsSection.style.display = "block";
            this.innerHTML = '<i class="fas fa-chevron-up"></i> Ẩn chi tiết';

            // Scroll đến phần chi tiết
            setTimeout(() => {
              detailsSection.scrollIntoView({
                behavior: "smooth",
                block: "center",
              });
            }, 100);
          }
        }
      });
    });
  }

  // Xử lý lọc đánh giá
  const filterButtons = document.querySelectorAll(".filter-btn");
  const feedbackItems = document.querySelectorAll(".feedback-item");

  if (filterButtons.length > 0 && feedbackItems.length > 0) {
    filterButtons.forEach((button) => {
      button.addEventListener("click", function () {
        const filter = this.getAttribute("data-filter");

        filterButtons.forEach((btn) => btn.classList.remove("active"));

        this.classList.add("active");

        // Lọc các feedback items
        let visibleCount = 0;
        feedbackItems.forEach((item) => {
          const rating = item.getAttribute("data-rating");

          if (filter === "all" || rating === filter) {
            item.style.display = "block";
            visibleCount++;
          } else {
            item.style.display = "none";
          }
        });

        // Hiển thị thông báo nếu không có đánh giá nào khớp với bộ lọc
        const noMatchingFilters = document.querySelector(
          ".no-matching-filters"
        );
        if (noMatchingFilters) {
          if (visibleCount === 0) {
            noMatchingFilters.style.display = "block";
          } else {
            noMatchingFilters.style.display = "none";
          }
        }
      });
    });
  }
});
