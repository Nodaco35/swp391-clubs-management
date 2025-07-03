/**
 * RecruitmentActivitiesManagement - Quản lý danh sách hoạt động tuyển quân
 * Phụ thuộc: recruitmentCommon.js
 */
document.addEventListener('DOMContentLoaded', function() {
    // Khởi tạo các dropdown và tab
    initializeDropdowns();
    initializeTabs();
    initializeSearchFilter();
    
    // Khởi tạo các modal và buttons
    initializeModals();
    
    // Phát hiện và cập nhật trạng thái hoạt động
    updateCampaignStatus();
});

// Xử lý dropdown menu
function initializeDropdowns() {
    // Xử lý click vào button dropdown
    document.addEventListener('click', function(e) {
        const button = e.target.closest('.dropdown button');
        if (button) {
            const dropdown = button.parentElement;
            const menu = dropdown.querySelector('.dropdown-menu');
            
            // Toggle dropdown menu
            menu.classList.toggle('hidden');
            
            // Đóng các dropdown khác
            document.querySelectorAll('.dropdown-menu:not(.hidden)').forEach(otherMenu => {
                if (otherMenu !== menu) {
                    otherMenu.classList.add('hidden');
                }
            });
            
            e.stopPropagation();
        } else if (!e.target.closest('.dropdown-menu')) {
            // Đóng tất cả dropdown menu khi click bên ngoài
            document.querySelectorAll('.dropdown-menu:not(.hidden)').forEach(menu => {
                menu.classList.add('hidden');
            });
        }
    });
    
    // Xử lý các nút xóa và kết thúc hoạt động
    document.querySelectorAll('.delete-campaign').forEach(button => {
        button.addEventListener('click', function() {
            const campaignId = this.dataset.id;
            showDeleteModal(campaignId);
        });
    });
    
    document.querySelectorAll('.close-campaign').forEach(button => {
        button.addEventListener('click', function() {
            const campaignId = this.dataset.id;
            showCloseModal(campaignId);
        });
    });
}

// Xử lý tabs
function initializeTabs() {
    const tabs = document.querySelectorAll('.tab-button');
    
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            // Remove active class from all tabs
            tabs.forEach(t => t.classList.remove('active', 'text-blue-600', 'border-b-2', 'border-blue-600'));
            tabs.forEach(t => t.classList.add('text-gray-600'));
            
            // Add active class to current tab
            this.classList.add('active', 'text-blue-600', 'border-b-2', 'border-blue-600');
            this.classList.remove('text-gray-600');
            
            // Filter campaigns
            const tabFilter = this.dataset.tab.toLowerCase();
            filterCampaigns(tabFilter);
        });
    });
}

// Hàm lọc hoạt động theo tab
function filterCampaigns(tabFilter) {
    const campaigns = document.querySelectorAll('.campaign-item');
    
    campaigns.forEach(campaign => {
        const status = campaign.dataset.status;
        
        if (tabFilter === 'all' || status === tabFilter) {
            campaign.style.display = '';
        } else {
            campaign.style.display = 'none';
        }
    });
}

// Hàm khởi tạo search filter
function initializeSearchFilter() {
    const searchInput = document.getElementById('searchCampaign');
    
    if (searchInput) {
        searchInput.addEventListener('keyup', function() {
            const searchTerm = this.value.toLowerCase();
            const campaigns = document.querySelectorAll('.campaign-item');
            
            campaigns.forEach(campaign => {
                const title = campaign.querySelector('.font-medium.text-gray-900').innerText.toLowerCase();
                const description = campaign.querySelector('.text-sm.text-gray-500').innerText.toLowerCase();
                
                if (title.includes(searchTerm) || description.includes(searchTerm)) {
                    campaign.style.display = '';
                } else {
                    campaign.style.display = 'none';
                }
            });
        });
    }
}

// Hàm khởi tạo modal
function initializeModals() {
    // Delete modal
    const deleteModal = document.getElementById('deleteModal');
    const cancelDelete = document.getElementById('cancelDelete');
    const confirmDelete = document.getElementById('confirmDelete');
    
    if (cancelDelete) {
        cancelDelete.addEventListener('click', function() {
            deleteModal.classList.add('hidden');
        });
    }
    
    // Close campaign modal
    const closeModal = document.getElementById('closeModal');
    const cancelClose = document.getElementById('cancelClose');
    const confirmClose = document.getElementById('confirmClose');
    
    if (cancelClose) {
        cancelClose.addEventListener('click', function() {
            closeModal.classList.add('hidden');
        });
    }
}

// Hiển thị modal xác nhận xóa
function showDeleteModal(campaignId) {
    const deleteModal = document.getElementById('deleteModal');
    const confirmDelete = document.getElementById('confirmDelete');
    
    deleteModal.classList.remove('hidden');
    
    // Cập nhật event listener cho nút xác nhận xóa
    const newConfirmDelete = confirmDelete.cloneNode(true);
    confirmDelete.parentNode.replaceChild(newConfirmDelete, confirmDelete);
    
    newConfirmDelete.addEventListener('click', function() {
        deleteCampaign(campaignId);
    });
}

// Hiển thị modal xác nhận kết thúc
function showCloseModal(campaignId) {
    const closeModal = document.getElementById('closeModal');
    const confirmClose = document.getElementById('confirmClose');
    
    closeModal.classList.remove('hidden');
    
    // Cập nhật event listener cho nút xác nhận kết thúc
    const newConfirmClose = confirmClose.cloneNode(true);
    confirmClose.parentNode.replaceChild(newConfirmClose, confirmClose);
    
    newConfirmClose.addEventListener('click', function() {
        closeCampaign(campaignId);
    });
}

// Hàm xóa hoạt động
function deleteCampaign(campaignId) {
    const contextPath = window.location.pathname.split('/recruitment')[0];
    
    fetch(`${contextPath}/recruitment/delete?recruitmentId=${campaignId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        const deleteModal = document.getElementById('deleteModal');
        deleteModal.classList.add('hidden');
        
        if (data.success) {
            showToast('Xóa hoạt động thành công', 'success');
            // Reload trang sau 1.5 giây
            setTimeout(() => {
                window.location.reload();
            }, 1500);
        } else {
            showToast(data.message || 'Không thể xóa hoạt động', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('Đã xảy ra lỗi khi xóa hoạt động', 'error');
    });
}

// Hàm kết thúc hoạt động
function closeCampaign(campaignId) {
    const contextPath = window.location.pathname.split('/recruitment')[0];
    
    fetch(`${contextPath}/recruitment/close?recruitmentId=${campaignId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        const closeModal = document.getElementById('closeModal');
        closeModal.classList.add('hidden');
        
        if (data.success) {
            showToast('Kết thúc hoạt động thành công', 'success');
            // Reload trang sau 1.5 giây
            setTimeout(() => {
                window.location.reload();
            }, 1500);
        } else {
            showToast(data.message || 'Không thể kết thúc hoạt động', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('Đã xảy ra lỗi khi kết thúc hoạt động', 'error');
    });
}

// Hàm cập nhật trạng thái hoạt động (có thể được gọi định kỳ)
function updateCampaignStatus() {
    // Hàm này có thể được mở rộng trong tương lai để cập nhật trạng thái tự động
    console.log('Campaign status updated');
}
