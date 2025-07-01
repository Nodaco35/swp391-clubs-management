document.addEventListener('DOMContentLoaded', function() {
    // Xử lý chuyển tab
    const tabButtons = document.querySelectorAll('.tab-button');
    const campaignItems = document.querySelectorAll('.campaign-item');
    
    // Xử lý click tab
    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            // Đánh dấu tab đang active
            tabButtons.forEach(btn => btn.classList.remove('active', 'text-blue-600', 'border-b-2', 'border-blue-600'));
            tabButtons.forEach(btn => btn.classList.add('text-gray-600'));
            button.classList.add('active', 'text-blue-600', 'border-b-2', 'border-blue-600');
            
            const selectedTab = button.getAttribute('data-tab');
            
            // Lọc các chiến dịch theo tab
            campaignItems.forEach(item => {
                const status = item.getAttribute('data-status');
                if (selectedTab === 'all' || status === selectedTab) {
                    item.classList.remove('hidden');
                } else {
                    item.classList.add('hidden');
                }
            });
        });
    });
    
    // Xử lý tìm kiếm
    const searchInput = document.getElementById('searchCampaign');
    if (searchInput) {
        searchInput.addEventListener('input', () => {
            const searchTerm = searchInput.value.toLowerCase();
            
            campaignItems.forEach(item => {
                const title = item.querySelector('.font-medium').textContent.toLowerCase();
                const description = item.querySelector('.text-sm.text-gray-500').textContent.toLowerCase();
                
                if (title.includes(searchTerm) || description.includes(searchTerm)) {
                    item.classList.remove('hidden');
                } else {
                    item.classList.add('hidden');
                }
            });
        });
    }
    
    // Xử lý xóa chiến dịch
    const deleteButtons = document.querySelectorAll('.delete-campaign');
    const deleteModal = document.getElementById('deleteModal');
    const cancelDelete = document.getElementById('cancelDelete');
    const confirmDelete = document.getElementById('confirmDelete');
    
    let campaignIdToDelete = null;
    
    deleteButtons.forEach(button => {
        button.addEventListener('click', () => {
            campaignIdToDelete = button.getAttribute('data-id');
            deleteModal.classList.remove('hidden');
        });
    });
    
    cancelDelete.addEventListener('click', () => {
        deleteModal.classList.add('hidden');
    });
    
    confirmDelete.addEventListener('click', () => {
        if (campaignIdToDelete) {
            // Gửi yêu cầu xóa chiến dịch
            const xhr = new XMLHttpRequest();
            xhr.open('POST', `${window.location.origin}/recruitment/delete`, true);
            xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            xhr.onreadystatechange = function() {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        const response = JSON.parse(xhr.responseText);
                        if (response.success) {
                            // Xóa thành công, reload trang
                            window.location.reload();
                        } else {
                            alert('Lỗi khi xóa chiến dịch: ' + response.message);
                        }
                    } else {
                        alert('Đã xảy ra lỗi khi xử lý yêu cầu.');
                    }
                    deleteModal.classList.add('hidden');
                }
            };
            xhr.send(`recruitmentId=${campaignIdToDelete}`);
        }
    });
});