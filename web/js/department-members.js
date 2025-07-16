/**
 * Department Members Management JavaScript
 * Handles member search, filtering, CRUD operations, and UI interactions
 */

// Global variables
let selectedUserId = null;
let searchTimeout = null;
let addMemberModal = null;
// Lấy contextPath từ đường dẫn hiện tại - sửa để đảm bảo xử lý đúng trường hợp không có context path
const contextPath = function() {
    const path = window.location.pathname;
    const firstSlashIndex = path.indexOf('/', 1);
    
    // Nếu không tìm thấy dấu / thứ 2 hoặc đường dẫn bắt đầu bằng /view, /img, ...
    if (firstSlashIndex === -1) {
        return '';
    }
    
    // Lấy phần context path
    return path.substring(0, firstSlashIndex);
}();

// Initialize when document is ready
$(document).ready(function() {
    initializePage();
});

/**
 * Initialize page components
 */
function initializePage() {
    // Initialize modal
    addMemberModal = new bootstrap.Modal(document.getElementById('addMemberModal'));
    updateMemberCounts();
    
    // Initialize filters based on URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const keyword = urlParams.get('keyword');
    if (keyword) {
        document.getElementById('searchInput').value = keyword;
    }
      // Setup event listeners
    setupEventListeners();
    
    // Apply initial filter state if any
    if (document.getElementById('statusFilter')) {
        filterMembers();
    }
    
    // Set default sort order without applying sort
    if (document.getElementById('sortOrder')) {
        document.getElementById('sortOrder').value = 'newest';
    }
}

/**
 * Setup all event listeners
 */
function setupEventListeners() {
    // Enter key search
    document.getElementById('searchInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchMembers();
        }
    });
    
    // Student search with debouncing
    document.getElementById('studentSearchInput').addEventListener('input', function() {
        const keyword = this.value.trim();
        
        if (searchTimeout) {
            clearTimeout(searchTimeout);
        }
        
        if (keyword.length < 2) {
            document.getElementById('studentSearchResults').innerHTML = '';
            return;
        }
        
        searchTimeout = setTimeout(() => {
            searchStudents(keyword);
        }, 300);
    });
    
    // Reset form when modal is hidden
    document.getElementById('addMemberModal').addEventListener('hidden.bs.modal', function() {
        resetModalForm();
    });
}

/**
 * Search members functionality
 */
function searchMembers() {
    const keyword = document.getElementById('searchInput').value.trim();
    const url = new URL(window.location);
    
    console.log('Searching for:', keyword);
    
    if (keyword === '') {
        // If empty keyword, go to list mode
        url.searchParams.delete('action');
        url.searchParams.delete('keyword');
        url.searchParams.delete('page');
    } else {
        url.searchParams.set('action', 'search');
        url.searchParams.set('keyword', keyword);
        url.searchParams.set('page', '1');
    }
    
    console.log('Redirecting to:', url.toString());
    window.location.href = url.toString();
}

/**
 * Clear search functionality
 */
function clearSearch() {
    const url = new URL(window.location);
    url.searchParams.delete('action');
    url.searchParams.delete('keyword');
    url.searchParams.delete('page');
    window.location.href = url.toString();
}

/**
 * Filter members by status
 */
function filterMembers() {
    const statusFilter = document.getElementById('statusFilter').value;
    const memberRows = document.querySelectorAll('.member-row');
    let visibleCount = 0;
    let activeCount = 0;
    let inactiveCount = 0;
    
    console.log('Filtering by status:', statusFilter);
    console.log('Total member rows found:', memberRows.length);
    
    memberRows.forEach(row => {
        const isActive = row.dataset.active === 'true';
        let showRow = false;
        
        console.log(`Row ${row.dataset.memberId} - Active: ${isActive}`);
        
        switch(statusFilter) {
            case '':
                showRow = true; // Show all
                break;
            case 'active':
                showRow = isActive;
                break;
            case 'inactive':
                showRow = !isActive;
                break;
        }
        
        if (showRow) {
            row.style.display = '';
            visibleCount++;
            if (isActive) {
                activeCount++;
            } else {
                inactiveCount++;
            }
        } else {
            row.style.display = 'none';
        }
    });
    
    console.log('Visible rows after filter:', visibleCount);
    console.log('Active in visible rows:', activeCount, 'Inactive in visible rows:', inactiveCount);
    
    // Update count displays based on current filter
    updateFilteredCounts(activeCount, inactiveCount, statusFilter);
    
    // Update empty state visibility
    updateEmptyState(visibleCount, statusFilter);
}

/**
 * Update empty state based on visible count and filter type
 */
function updateEmptyState(visibleCount, statusFilter) {
    const emptyState = document.querySelector('.empty-state');
    const tableContainer = document.querySelector('.table-responsive');
    
    console.log('updateEmptyState called with:', { visibleCount, statusFilter });
    console.log('emptyState element:', emptyState);
    console.log('tableContainer element:', tableContainer);
    
    if (visibleCount === 0) {
        console.log('No visible members - showing empty state');
        
        if (tableContainer) {
            tableContainer.style.display = 'none';
        }
        
        // Hide original empty state first
        if (emptyState) {
            emptyState.style.display = 'none';
        }
        
        // Remove any existing dynamic empty state
        const existingDynamic = document.querySelector('.dynamic-empty');
        if (existingDynamic) {
            existingDynamic.remove();
        }
        
        // Create new dynamic empty state
        const cardBody = document.querySelector('.card-body');
        if (cardBody) {
            const dynamicEmptyState = document.createElement('div');
            dynamicEmptyState.className = 'text-center py-5 empty-state dynamic-empty';
            
            // Customize message based on filter type
            let message = 'Không có thành viên phù hợp';
            let description = 'Thử thay đổi bộ lọc để xem kết quả khác.';
            let icon = 'fa-filter';
            
            if (statusFilter === 'active') {
                message = 'Không có thành viên hoạt động';
                description = 'Hiện tại không có thành viên nào đang hoạt động.';
                icon = 'fa-user-check';
            } else if (statusFilter === 'inactive') {
                message = 'Không có thành viên không hoạt động';
                description = 'Tất cả thành viên đều đang hoạt động.';
                icon = 'fa-user-clock';
            }
            
            dynamicEmptyState.innerHTML = `
                <div class="mb-3">
                    <i class="fas ${icon} fa-3x text-muted"></i>
                </div>
                <h5 class="text-muted">${message}</h5>
                <p class="text-muted">${description}</p>
                <button class="btn btn-outline-primary" onclick="resetFilters()">
                    <i class="fas fa-refresh me-2"></i>Đặt lại bộ lọc
                </button>
            `;
            cardBody.appendChild(dynamicEmptyState);
            console.log('Dynamic empty state created');
        }
    } else {
        console.log('Has visible members - showing table');
        
        if (tableContainer) {
            tableContainer.style.display = 'block';
        }
        
        // Hide original empty state
        if (emptyState) {
            emptyState.style.display = 'none';
        }
        
        // Remove dynamic empty state
        const dynamicEmpty = document.querySelector('.dynamic-empty');
        if (dynamicEmpty) {
            dynamicEmpty.remove();
            console.log('Dynamic empty state removed');
        }
    }
}

/**
 * Reset all filters
 */
function resetFilters() {
    document.getElementById('statusFilter').value = '';
    document.getElementById('searchInput').value = '';
    
    // Reset sort icons
    document.querySelectorAll('.sortable i').forEach(icon => {
        icon.className = 'fas fa-sort ms-1';
    });
    document.querySelectorAll('.sortable').forEach(th => {
        th.classList.remove('sort-active');
    });
    
    filterMembers();
    // Restore original counts
    updateMemberCounts();
}

/**
 * Sort members by selected criteria
 */
function sortMembers() {
    const sortOrder = document.getElementById('sortOrder').value;
    const tbody = document.querySelector('.table tbody');
    
    if (!tbody) {
        console.log('No table body found for sorting');
        return;
    }
    
    const rows = Array.from(tbody.querySelectorAll('.member-row'));
    
    if (rows.length === 0) {
        console.log('No member rows found for sorting');
        return;
    }
    
    rows.sort((a, b) => {
        try {
            switch (sortOrder) {
                case 'newest':
                    // Sort by join date - newest first
                    const dateTextA = a.querySelector('td:nth-child(3) div')?.textContent?.trim() || '';
                    const dateTextB = b.querySelector('td:nth-child(3) div')?.textContent?.trim() || '';
                    const dateA = parseDateString(dateTextA);
                    const dateB = parseDateString(dateTextB);
                    return dateB - dateA;
                    
                case 'oldest':
                    // Sort by join date - oldest first
                    const dateTextA2 = a.querySelector('td:nth-child(3) div')?.textContent?.trim() || '';
                    const dateTextB2 = b.querySelector('td:nth-child(3) div')?.textContent?.trim() || '';
                    const dateA2 = parseDateString(dateTextA2);
                    const dateB2 = parseDateString(dateTextB2);
                    return dateA2 - dateB2;
                    
                case 'name':
                    // Sort by name alphabetically
                    const nameA = a.querySelector('h6.fw-semibold')?.textContent?.trim() || '';
                    const nameB = b.querySelector('h6.fw-semibold')?.textContent?.trim() || '';
                    return nameA.localeCompare(nameB, 'vi');
                    
                case 'role':                    // Sort by role - leaders first, then members (chỉ có 2 vai trò)
                    const roleA = a.querySelector('td:nth-child(2) .badge')?.textContent?.trim() || 'Thành viên';
                    const roleB = b.querySelector('td:nth-child(2) .badge')?.textContent?.trim() || 'Thành viên';
                    
                    // Priority order: Trưởng ban > Thành viên
                    const getRolePriority = (role) => {
                        if (role === 'Trưởng ban') return 0;
                        return 1; // Tất cả vai trò khác đều là thành viên
                    };
                    
                    const priorityA = getRolePriority(roleA);
                    const priorityB = getRolePriority(roleB);
                    
                    return priorityA - priorityB;
                    
                default:
                    return 0;
            }
        } catch (error) {
            console.error('Error sorting members:', error);
            return 0;
        }
    });    // Re-append sorted rows to tbody
    rows.forEach(row => tbody.appendChild(row));
    
    console.log('Members sorted by:', sortOrder, '- Total rows:', rows.length);
}

/**
 * Parse date string in dd/MM/yyyy format to Date object
 */
function parseDateString(dateStr) {
    if (!dateStr) return new Date(0);
    const parts = dateStr.split('/');
    if (parts.length !== 3) return new Date(0);
    // parts[0] = day, parts[1] = month, parts[2] = year
    return new Date(parts[2], parts[1] - 1, parts[0]);
}

/**
 * Update filtered member counts in the statistics cards
 */
function updateFilteredCounts(activeCount, inactiveCount, statusFilter) {
    const activeElement = document.getElementById('activeCount');
    const inactiveElement = document.getElementById('inactiveCount');
    
    if (statusFilter === '') {
        // Show all counts when no filter is applied
        if (activeElement) activeElement.textContent = activeCount;
        if (inactiveElement) inactiveElement.textContent = inactiveCount;
    } else if (statusFilter === 'active') {
        // Show only active count when filtering by active
        if (activeElement) activeElement.textContent = activeCount;
        if (inactiveElement) inactiveElement.textContent = 0;
    } else if (statusFilter === 'inactive') {
        // Show only inactive count when filtering by inactive
        if (activeElement) activeElement.textContent = 0;
        if (inactiveElement) inactiveElement.textContent = inactiveCount;
    }
}

/**
 * Update member status - Chức năng đã bị tắt (chỉ xem trạng thái)
 * Function này được giữ lại nhưng không còn được sử dụng trong UI
 */
function updateMemberStatus(userId, isActive) {
    console.log('Chức năng updateMemberStatus đã bị tắt trong UI');
    /* 
    // Đoạn code này được giữ lại nhưng không còn sử dụng
    $.ajax({
        url: 'department-members',
        type: 'POST',
        data: {
            action: 'updateStatus',
            userID: userId,
            isActive: isActive,
            status: isActive ? 'Active' : 'Inactive'
        },
        success: function(response) {
            if (response.success) {
                // Update UI
                const row = $(`tr[data-member-id="${userId}"]`);
                const statusBadge = row.find('.badge');
                
                // Update row data attribute
                row.attr('data-active', isActive);
                
                if (isActive) {
                    row.removeClass('table-secondary').addClass('table-light');
                    statusBadge.removeClass('bg-secondary').addClass('bg-success').text('Hoạt động');
                } else {
                    row.removeClass('table-light').addClass('table-secondary');
                    statusBadge.removeClass('bg-success').addClass('bg-secondary').text('Không hoạt động');
                }
                showNotification('Cập nhật trạng thái thành công!', 'success');
                updateMemberCounts();
                // Reapply current filter to update display
                const currentFilter = document.getElementById('statusFilter').value;
                if (currentFilter) {
                    filterMembers();
                }
            } else {
                showNotification('Không thể cập nhật trạng thái!', 'error');
            }
        },
        error: function() {
            showNotification('Có lỗi xảy ra!', 'error');
        }
    });
    */
}

/**
 * Confirm and remove member
 */
function confirmRemoveMember(userId, fullName) {
    if (confirm(`Bạn có chắc chắn muốn xóa "${fullName}" khỏi ban?`)) {
        removeMember(userId);
    }
}

/**
 * Remove member from department
 */
function removeMember(userId) {
    $.ajax({
        url: contextPath + '/department-members',
        type: 'POST',
        data: {
            action: 'removeMember',
            userID: userId
        },
        success: function(response) {
            if (response.success) {
                $(`tr[data-member-id="${userId}"]`).fadeOut(300, function() {
                    $(this).remove();
                    updateMemberCounts();
                });
                showNotification('Xóa thành viên thành công!', 'success');
            } else {
                showNotification('Không thể xóa thành viên!', 'error');
            }
        },
        error: function() {
            showNotification('Có lỗi xảy ra!', 'error');
        }
    });
}

/**
 * Modal management functions
 */
function showAddMemberModal() {
    addMemberModal.show();
    document.getElementById('studentSearchInput').focus();
}

function closeAddMemberModal() {
    addMemberModal.hide();
    resetModalForm();
}

function resetModalForm() {
    document.getElementById('studentSearchInput').value = '';
    document.getElementById('studentSearchResults').innerHTML = '';
    document.getElementById('selectedStudent').style.display = 'none';
    selectedUserId = null;
    document.getElementById('addMemberBtn').disabled = true;
}

/**
 * Search students for adding to department
 */
function searchStudents(keyword) {    $.ajax({
        url: contextPath + '/department-members',
        type: 'GET',
        data: {
            action: 'searchStudents',
            keyword: keyword
        },
        success: function(students) {
            displaySearchResults(students);
        },
        error: function() {
            document.getElementById('studentSearchResults').innerHTML = 
                '<div class="alert alert-danger">Có lỗi khi tìm kiếm sinh viên</div>';
        }
    });
}

/**
 * Display search results in modal
 */
function displaySearchResults(students) {
    const resultsContainer = document.getElementById('studentSearchResults');
    
    if (students.length === 0) {
        resultsContainer.innerHTML = '<div class="text-muted text-center py-2">Không tìm thấy sinh viên nào</div>';
        return;
    }
    
    let html = '<div class="list-group">';
    students.forEach(student => {
        const avatarSrc = student.avatar || 'Hinh-anh-dai-dien-mac-dinh-Facebook.jpg';
        html += `
            <a href="#" class="list-group-item list-group-item-action" 
               onclick="selectStudent('${student.userID}', '${escapeHtml(student.fullName)}', '${escapeHtml(student.email)}', '${avatarSrc}')">
                <div class="d-flex align-items-center">
                    <img src="img/${avatarSrc}" 
                         alt="Avatar" class="rounded-circle me-3" style="width: 40px; height: 40px; object-fit: cover;">
                    <div>
                        <div class="fw-semibold">${escapeHtml(student.fullName)}</div>
                        <small class="text-muted">${escapeHtml(student.email)}</small>
                    </div>
                </div>
            </a>
        `;
    });
    html += '</div>';
    
    resultsContainer.innerHTML = html;
}

/**
 * Select student for adding to department
 */
function selectStudent(userId, fullName, email, avatar) {
    selectedUserId = userId;
    
    const avatarSrc = avatar || 'Hinh-anh-dai-dien-mac-dinh-Facebook.jpg';
    const selectedStudentDiv = document.getElementById('selectedStudent');
    selectedStudentDiv.querySelector('.student-info').innerHTML = `
        <div class="d-flex align-items-center">
            <img src="img/${avatarSrc}" 
                 alt="Avatar" class="rounded-circle me-3" style="width: 50px; height: 50px; object-fit: cover;">
            <div>
                <div class="fw-semibold">${escapeHtml(fullName)}</div>
                <small class="text-muted">${escapeHtml(email)}</small>
            </div>
        </div>
    `;
    
    selectedStudentDiv.style.display = 'block';
    document.getElementById('studentSearchResults').innerHTML = '';
    document.getElementById('studentSearchInput').value = fullName;
    document.getElementById('addMemberBtn').disabled = false;
}

/**
 * Add member to department
 */
function addMember() {
    if (!selectedUserId) {
        showNotification('Vui lòng chọn sinh viên!', 'error');
        return;
    }
    
    const roleId = document.getElementById('memberRole').value;
      $.ajax({
        url: contextPath + '/department-members',
        type: 'POST',
        data: {
            action: 'addMember',
            userID: selectedUserId,
            roleID: roleId
        },
        success: function(response) {
            if (response.success) {
                showNotification('Thêm thành viên thành công!', 'success');
                closeAddMemberModal();
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                showNotification('Không thể thêm thành viên!', 'error');
            }
        },
        error: function() {
            showNotification('Có lỗi xảy ra!', 'error');
        }
    });
}

/**
 * Sort table by clicking on column headers
 */
function sortByColumn(element) {
    const sortType = element.dataset.sort;
    const currentIcon = element.querySelector('i');
    let sortDirection = 'asc';
    
    // Reset all other column header icons
    document.querySelectorAll('.sortable i').forEach(icon => {
        if (icon !== currentIcon) {
            icon.className = 'fas fa-sort ms-1';
        }
    });
    
    document.querySelectorAll('.sortable').forEach(th => {
        if (th !== element) {
            th.classList.remove('sort-active');
        }
    });
    
    // Toggle sort direction
    if (currentIcon.classList.contains('fa-sort') || currentIcon.classList.contains('fa-sort-down')) {
        currentIcon.className = 'fas fa-sort-up ms-1';
        sortDirection = 'asc';
    } else {
        currentIcon.className = 'fas fa-sort-down ms-1';
        sortDirection = 'desc';
    }
    
    // Add active class to the sorted column
    element.classList.add('sort-active');
    
    // Perform the actual sorting
    const tbody = document.querySelector('.table tbody');
    if (!tbody) return;
    
    const rows = Array.from(tbody.querySelectorAll('.member-row'));
    if (rows.length === 0) return;
    
    rows.sort((a, b) => {
        let valueA, valueB;
        
        try {
            switch (sortType) {
                case 'name':
                    valueA = a.querySelector('h6.fw-semibold')?.textContent?.trim() || '';
                    valueB = b.querySelector('h6.fw-semibold')?.textContent?.trim() || '';
                    return sortDirection === 'asc' 
                        ? valueA.localeCompare(valueB, 'vi') 
                        : valueB.localeCompare(valueA, 'vi');
                
                case 'role':
                    valueA = a.querySelector('td:nth-child(2) .badge')?.textContent?.trim() || 'Thành viên';
                    valueB = b.querySelector('td:nth-child(2) .badge')?.textContent?.trim() || 'Thành viên';
                      // Priority order: Trưởng ban > Thành viên (chỉ có 2 vai trò)
                    const getRolePriority = (role) => {
                        if (role === 'Trưởng ban') return 0;
                        return 1; // Tất cả vai trò khác đều là thành viên
                    };
                    
                    const priorityA = getRolePriority(valueA);
                    const priorityB = getRolePriority(valueB);
                    
                    return sortDirection === 'asc' 
                        ? priorityA - priorityB 
                        : priorityB - priorityA;
                
                case 'date':
                    valueA = a.querySelector('td:nth-child(3) div')?.textContent?.trim() || '';
                    valueB = b.querySelector('td:nth-child(3) div')?.textContent?.trim() || '';
                    const dateA = parseDateString(valueA);
                    const dateB = parseDateString(valueB);
                    return sortDirection === 'asc' 
                        ? dateA - dateB 
                        : dateB - dateA;
                    
                case 'status':
                    valueA = a.dataset.active === 'true';
                    valueB = b.dataset.active === 'true';
                    return sortDirection === 'asc' 
                        ? (valueA === valueB ? 0 : valueA ? -1 : 1)
                        : (valueA === valueB ? 0 : valueA ? 1 : -1);
                    
                default:
                    return 0;
            }
        } catch (error) {
            console.error('Error sorting by column:', error);
            return 0;
        }
    });
    
    // Re-append sorted rows to tbody
    rows.forEach(row => tbody.appendChild(row));
    
    console.log(`Sorted by ${sortType} in ${sortDirection} order`);
}

/**
 * Utility functions
 */
function updateMemberCounts() {
    // Count all members regardless of filter state
    const memberRows = document.querySelectorAll('.member-row');
    let activeCount = 0;
    let inactiveCount = 0;
    
    memberRows.forEach(row => {
        if (row.dataset.active === 'true') {
            activeCount++;
        } else {
            inactiveCount++;
        }
    });
    
    // Update count displays with original counts
    const activeElement = document.getElementById('activeCount');
    const inactiveElement = document.getElementById('inactiveCount');
    
    if (activeElement) activeElement.textContent = activeCount;
    if (inactiveElement) inactiveElement.textContent = inactiveCount;
}

function showNotification(message, type) {
    // Create notification with Bootstrap toast
    const toastHtml = `
        <div class="toast align-items-center text-white bg-${type === 'success' ? 'success' : 'danger'} border-0" 
             role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="fas ${getNotificationIcon(type)} me-2"></i>${escapeHtml(message)}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" 
                        data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;
    
    // Create toast container if not exists
    if (!$('.toast-container').length) {
        $('body').append('<div class="toast-container position-fixed top-0 end-0 p-3"></div>');
    }
    
    const $toast = $(toastHtml);
    $('.toast-container').append($toast);
    
    const toast = new bootstrap.Toast($toast[0]);
    toast.show();
    
    // Remove toast element after it's hidden
    $toast.on('hidden.bs.toast', function() {
        $(this).remove();
    });
}

function getNotificationIcon(type) {
    switch(type) {
        case 'success': return 'fa-check-circle';
        case 'error': return 'fa-exclamation-circle';
        case 'warning': return 'fa-exclamation-triangle';
        case 'info': return 'fa-info-circle';
        default: return 'fa-bell';
    }
}

function viewMemberDetail(userId) {
    if (!userId) {
        showNotification('Không thể xem chi tiết: Thiếu ID thành viên', 'error');
        return;
    }
    
    // Show modal with loading state
    const memberDetailModalElement = document.getElementById('memberDetailModal');
    memberDetailModalElement.setAttribute('data-user-id', userId);
    const memberDetailModal = new bootstrap.Modal(memberDetailModalElement);
    memberDetailModal.show();
    
    const loadingElement = document.getElementById('memberDetailLoading');
    const contentElement = document.getElementById('memberDetailContent');
    
    loadingElement.style.display = 'block';
    contentElement.style.display = 'none';
      // Reset previous content
    document.getElementById('memberDetailTasksBody').innerHTML = '';
      // Fetch member details from the server
    const requestUrl = contextPath + '/department-members';
    console.log('Debug viewMemberDetail - Request URL:', requestUrl);
    console.log('Debug viewMemberDetail - User ID:', userId);
    
    $.ajax({
        url: requestUrl,
        type: 'GET',
        data: {
            action: 'getMemberDetail',
            userID: userId
        },
        success: function(response) {
            // Hide loading
            loadingElement.style.display = 'none';
            
            console.log('Debug viewMemberDetail - Response received:', response);
            
            const member = response.member;
            const tasks = response.tasks || [];
            
            if (!member) {
                handleMemberDetailError('Không thể tìm thấy thông tin thành viên');
                return;
            }
            
            // Show content with fade-in animation
            contentElement.style.opacity = '0';
            contentElement.style.display = 'block';
            setTimeout(() => {
                contentElement.style.opacity = '1';
                contentElement.style.transition = 'opacity 0.3s ease';
            }, 50);
            
            try {
                // Fill member profile information
                document.getElementById('memberDetailName').textContent = member.fullName || 'Không có tên';
                document.getElementById('memberDetailEmail').textContent = member.email || '(Không có email)';
                document.getElementById('memberDetailPhone').textContent = member.phone || 'Chưa cập nhật';
                document.getElementById('memberDetailStudentCode').textContent = member.studentCode || 'Chưa cập nhật';
                document.getElementById('memberDetailMajor').textContent = member.major || 'Chưa cập nhật';
                
                // Format and set joined date
                const joinedDate = new Date(member.joinedDate);
                const isValidDate = !isNaN(joinedDate.getTime());
                document.getElementById('memberDetailJoinDate').textContent = isValidDate ? 
                    joinedDate.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' }) : 
                    'Không xác định';
                  // Set avatar with fade in effect
                const avatarImg = document.getElementById('memberDetailAvatar');
                const avatarPath = member.avatar || 'Hinh-anh-dai-dien-mac-dinh-Facebook.jpg';
                avatarImg.style.opacity = '0';
                // Sử dụng contextPath đã khai báo ở trên để tạo URL tuyệt đối
                avatarImg.src = contextPath + `/img/${avatarPath}`;
                avatarImg.onload = function() {
                    avatarImg.style.opacity = '1';
                    avatarImg.style.transition = 'opacity 0.5s ease';
                };                    // Set role badge with appropriate styling
                const roleBadge = document.getElementById('memberDetailRole');
                roleBadge.className = 'role-badge';
                
                // Chỉ giữ lại 2 vai trò: Trưởng ban và Thành viên
                if(member.roleName === 'Trưởng ban') {
                    roleBadge.innerHTML = '<span class="badge bg-danger text-white"><i class="fas fa-crown me-1"></i>Trưởng ban</span>';
                } else {
                    roleBadge.innerHTML = '<span class="badge bg-secondary"><i class="fas fa-user me-1"></i>Thành viên</span>';
                }
                
                // Set status badge with appropriate styling
                const statusBadge = document.getElementById('memberDetailStatus');
                if (member.active) {
                    statusBadge.className = 'badge bg-success';
                    statusBadge.innerHTML = '<i class="fas fa-check-circle me-1"></i>Hoạt động';
                } else {
                    statusBadge.className = 'badge bg-secondary';
                    statusBadge.innerHTML = '<i class="fas fa-times-circle me-1"></i>Không hoạt động';
                }
                
                // Set task statistics with animation
                const assignedTasksEl = document.getElementById('memberDetailAssignedTasks');
                const completedTasksEl = document.getElementById('memberDetailCompletedTasks');
                const assignedTasks = member.assignedTasks || 0;
                const completedTasks = member.completedTasks || 0;
                
                // Animate counting up
                animateCounter(assignedTasksEl, 0, assignedTasks);
                animateCounter(completedTasksEl, 0, completedTasks);
                
                // Calculate task completion percentage with animation
                let completionPercentage = 0;
                if (assignedTasks > 0) {
                    completionPercentage = Math.round((completedTasks / assignedTasks) * 100);
                }
                
                const progressBar = document.getElementById('memberDetailProgress');
                const progressText = document.getElementById('memberDetailProgressText');
                
                progressBar.style.width = '0%';
                progressText.textContent = '0% hoàn thành';
                
                setTimeout(() => {
                    progressBar.style.width = `${completionPercentage}%`;
                    progressBar.style.transition = 'width 1s ease-in-out';
                    progressText.textContent = `${completionPercentage}% hoàn thành`;
                }, 300);
                
                // Set progress bar color based on completion
                if (completionPercentage < 30) {
                    progressBar.className = 'progress-bar bg-danger';
                } else if (completionPercentage < 70) {
                    progressBar.className = 'progress-bar bg-warning';
                } else {
                    progressBar.className = 'progress-bar bg-success';
                }
                
                // Set last activity with nice formatting
                const lastActivityEl = document.getElementById('memberDetailLastActivity');
                
                if (member.lastActivity) {
                    const lastActivity = new Date(member.lastActivity);
                    const isValidLastActivity = !isNaN(lastActivity.getTime());
                    
                    if (isValidLastActivity) {
                        // Calculate time ago
                        const now = new Date();
                        const diffMs = now - lastActivity;
                        const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
                        
                        let timeAgoText = '';
                        if (diffDays === 0) {
                            timeAgoText = 'Hôm nay';
                        } else if (diffDays === 1) {
                            timeAgoText = 'Hôm qua';
                        } else if (diffDays < 7) {
                            timeAgoText = `${diffDays} ngày trước`;
                        }
                        
                        lastActivityEl.innerHTML = `
                            <i class="fas fa-history me-1"></i>
                            Hoạt động gần nhất: ${lastActivity.toLocaleDateString('vi-VN', { 
                                day: '2-digit', month: '2-digit', year: 'numeric'
                            })} ${lastActivity.toLocaleTimeString('vi-VN', { 
                                hour: '2-digit', minute: '2-digit' 
                            })}
                            ${timeAgoText ? `<span class="ms-1 badge bg-light text-dark">${timeAgoText}</span>` : ''}
                        `;
                    } else {
                        lastActivityEl.innerHTML = `<i class="fas fa-history me-1"></i> Chưa có hoạt động nào`;
                    }
                } else {
                    lastActivityEl.innerHTML = `<i class="fas fa-history me-1"></i> Chưa có hoạt động nào`;
                }
                
                // Populate tasks table with animation
                const tasksBody = document.getElementById('memberDetailTasksBody');
                tasksBody.innerHTML = '';
                
                if (tasks && tasks.length > 0) {
                    document.getElementById('memberDetailEmptyTasks').style.display = 'none';
                    document.getElementById('memberDetailTaskList').style.display = 'block';
                    
                    tasks.forEach((task, index) => {
                        const row = document.createElement('tr');
                          // Format due date (now using endDate instead of dueDate)
                        let dueDateFormatted = '-';
                        if (task.endDate) {
                            const dueDate = new Date(task.endDate);
                            if (!isNaN(dueDate.getTime())) {
                                dueDateFormatted = dueDate.toLocaleDateString('vi-VN', {
                                    day: '2-digit', month: '2-digit', year: 'numeric'
                                });
                                
                                // Add warning badge if due date is near (within 2 days)
                                const now = new Date();
                                const diffDays = Math.floor((dueDate - now) / (1000 * 60 * 60 * 24));
                                if (diffDays >= 0 && diffDays <= 2 && task.status !== 'Done' && task.status !== 'Completed' && task.status !== 'Đã hoàn thành') {
                                    dueDateFormatted += ' <span class="badge bg-warning text-dark ms-1">Sắp đến hạn</span>';
                                }
                            }
                        }
                        
                        // Status badge
                        let statusBadge = '';
                        switch (task.status) {
                            case 'Completed':
                            case 'Đã hoàn thành':
                                statusBadge = '<span class="badge bg-success"><i class="fas fa-check-circle me-1"></i>Đã hoàn thành</span>';
                                break;
                            case 'In Progress':
                            case 'Đang thực hiện':
                                statusBadge = '<span class="badge bg-primary"><i class="fas fa-spinner me-1"></i>Đang thực hiện</span>';
                                break;
                            case 'Not Started':
                            case 'Chưa bắt đầu':
                                statusBadge = '<span class="badge bg-secondary"><i class="fas fa-clock me-1"></i>Chưa bắt đầu</span>';
                                break;
                            case 'Overdue':
                            case 'Quá hạn':
                                statusBadge = '<span class="badge bg-danger"><i class="fas fa-exclamation-circle me-1"></i>Quá hạn</span>';
                                break;
                            default:
                                statusBadge = `<span class="badge bg-info"><i class="fas fa-info-circle me-1"></i>${task.status}</span>`;
                        }
                        
                        // Priority badge
                        let priorityBadge = '';
                        switch (task.priority) {
                            case 'High':
                            case 'Cao':
                                priorityBadge = '<span class="badge bg-danger"><i class="fas fa-arrow-up me-1"></i>Cao</span>';
                                break;
                            case 'Medium':
                            case 'Trung bình':
                                priorityBadge = '<span class="badge bg-warning text-dark"><i class="fas fa-equals me-1"></i>Trung bình</span>';
                                break;
                            case 'Low':
                            case 'Thấp':
                                priorityBadge = '<span class="badge bg-info"><i class="fas fa-arrow-down me-1"></i>Thấp</span>';
                                break;
                            default:
                                priorityBadge = `<span class="badge bg-secondary"><i class="fas fa-minus me-1"></i>${task.priority || 'Không xác định'}</span>`;
                        }
                          // Hiển thị title từ task.title
                        // Hiển thị createdBy.fullName từ task.createdBy nếu có
                        const creatorName = task.createdBy && task.createdBy.fullName ? task.createdBy.fullName : 'Không xác định';
                        
                        row.innerHTML = `
                            <td title="${escapeHtml(task.title)}">${escapeHtml(task.title)}</td>
                            <td>${statusBadge}</td>
                            <td>${priorityBadge}</td>
                            <td>${dueDateFormatted}</td>
                        `;
                        
                        // Add animation delay based on index
                        row.style.opacity = '0';
                        row.style.transform = 'translateY(10px)';
                        
                        tasksBody.appendChild(row);
                        
                        setTimeout(() => {
                            row.style.opacity = '1';
                            row.style.transform = 'translateY(0)';
                            row.style.transition = 'all 0.3s ease';
                        }, 50 * (index + 1));
                    });
                } else {
                    document.getElementById('memberDetailEmptyTasks').style.display = 'block';
                    document.getElementById('memberDetailTaskList').style.display = 'none';
                }
            } catch (e) {
                console.error('Error rendering member details:', e);
                handleMemberDetailError('Lỗi hiển thị thông tin thành viên');
            }
        },        error: function(xhr, status, error) {
            console.error('Error fetching member details:', error);
            console.error('XHR Status:', status);
            console.error('XHR Response:', xhr.responseText);
            console.error('XHR Status Code:', xhr.status);
            
            // Chi tiết thêm về lỗi
            let errorDetails = '';
            try {
                if (xhr.responseText) {
                    if (xhr.responseText.startsWith('{')) {
                        const errorResponse = JSON.parse(xhr.responseText);
                        errorDetails = errorResponse.message || '';
                    } else {
                        // Có thể là lỗi HTML từ Tomcat
                        const errorMatch = xhr.responseText.match(/<p><b>message<\/b>(.+?)<\/p>/);
                        if (errorMatch && errorMatch.length > 1) {
                            errorDetails = errorMatch[1].trim();
                        } else if (xhr.responseText.includes('Servlet execution threw an exception')) {
                            errorDetails = 'Lỗi thực thi servlet';
                        } else {
                            errorDetails = 'Lỗi server';
                        }
                    }
                }
            } catch (e) {
                console.error('Error parsing error response:', e);
                errorDetails = xhr.responseText || '';
            }
            
            let errorMessage;
            if (xhr.status === 404) {
                errorMessage = 'URL không tồn tại (404). Kiểm tra lại URL servlet trong web.xml';
            } else if (xhr.status === 500) {
                errorMessage = `Lỗi server (500): ${errorDetails || 'Kiểm tra log server để biết thêm chi tiết'}`;
            } else if (xhr.status === 403) {
                errorMessage = `Truy cập bị từ chối (403): ${errorDetails}`;
            } else {
                errorMessage = errorDetails ? `Lỗi: ${errorDetails}` : `Không thể tải thông tin (${xhr.status || 'unknown'})`;
            }
            
            handleMemberDetailError(errorMessage);
            showNotification('Không thể tải thông tin chi tiết thành viên', 'error');
        }
    });
}

/**
 * Hàm thử lại để tải lại thông tin chi tiết của thành viên
 */
function reloadMemberDetail() {
    // Lấy userId từ attribute data của popup
    const userId = document.getElementById('memberDetailModal').getAttribute('data-user-id');
    
    console.log('Thử lại tải thông tin thành viên ID:', userId);
    
    if (userId) {
        viewMemberDetail(userId);
    } else {
        showNotification('Không thể xác định ID thành viên để tải lại', 'error');
    }
}

// Helper function to animate counting
function animateCounter(element, start, end) {
    if (!element) return;
    
    let current = start;
    const increment = end > start ? 1 : -1;
    const duration = 1000; // ms
    const steps = Math.abs(end - start);
    const stepTime = steps > 0 ? duration / steps : duration;
    
    const timer = setInterval(() => {
        current += increment;
        element.textContent = current;
        
        if ((increment > 0 && current >= end) || (increment < 0 && current <= end)) {
            element.textContent = end;
            clearInterval(timer);
        }
    }, stepTime);
}

// Helper function to handle errors in member detail modal
function handleMemberDetailError(message) {
    document.getElementById('memberDetailLoading').style.display = 'none';
    document.getElementById('memberDetailContent').style.display = 'block';
    
    // Thử lấy thông tin thành viên từ bảng hiển thị
    const userId = document.getElementById('memberDetailModal').getAttribute('data-user-id');
    const memberRow = document.querySelector(`tr[data-member-id="${userId}"]`);
    
    if (memberRow) {
        console.log('Thử lấy thông tin từ bảng hiển thị');
        try {
            // Lấy thông tin từ bảng
            const name = memberRow.querySelector('.member-name')?.textContent?.trim() || 'Không có tên';
            const email = memberRow.querySelector('.member-email')?.textContent?.trim() || '';
            const roleBadge = memberRow.querySelector('.badge')?.cloneNode(true) || null;
            const isActive = memberRow.dataset.active === 'true';
            const joinDateText = memberRow.querySelector('td:nth-child(3) div')?.textContent?.trim() || '';
            
            // Điền thông tin cơ bản
            document.getElementById('memberDetailName').textContent = name;
            document.getElementById('memberDetailEmail').textContent = email || '(Không có email)';
            document.getElementById('memberDetailPhone').textContent = 'Không có dữ liệu';
            document.getElementById('memberDetailStudentCode').textContent = 'Không có dữ liệu';
            document.getElementById('memberDetailMajor').textContent = 'Không có dữ liệu';
            document.getElementById('memberDetailJoinDate').textContent = joinDateText || 'Không xác định';
            
            // Xử lý avatar
            document.getElementById('memberDetailAvatar').src = contextPath + '/img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg';
            
            // Xử lý badge role
            const roleBadgeContainer = document.getElementById('memberDetailRole');
            roleBadgeContainer.innerHTML = '';
            if (roleBadge) {
                roleBadgeContainer.appendChild(roleBadge);
            } else {
                roleBadgeContainer.innerHTML = '<span class="badge bg-secondary"><i class="fas fa-user me-1"></i>Thành viên</span>';
            }
            
            // Xử lý badge status
            const statusBadge = document.getElementById('memberDetailStatus');
            if (isActive) {
                statusBadge.className = 'badge bg-success';
                statusBadge.innerHTML = '<i class="fas fa-check-circle me-1"></i>Hoạt động';
            } else {
                statusBadge.className = 'badge bg-secondary';
                statusBadge.innerHTML = '<i class="fas fa-times-circle me-1"></i>Không hoạt động';
            }
            
            // Thống kê tasks
            document.getElementById('memberDetailAssignedTasks').textContent = '?';
            document.getElementById('memberDetailCompletedTasks').textContent = '?';
            document.getElementById('memberDetailProgress').style.width = '0%';
            document.getElementById('memberDetailProgressText').textContent = 'Không có dữ liệu';
            document.getElementById('memberDetailLastActivity').innerHTML = '<i class="fas fa-history me-1"></i>Không có dữ liệu';
            
            // Hiển thị thông báo không có công việc
            document.getElementById('memberDetailEmptyTasks').style.display = 'block';
            document.getElementById('memberDetailTaskList').style.display = 'none';
            
            console.log('Đã hiển thị thông tin cơ bản từ bảng');
            
            // Thêm thông báo lỗi ở dưới
            const warningAlert = document.createElement('div');
            warningAlert.className = 'alert alert-warning mt-3';
            warningAlert.innerHTML = `
                <i class="fas fa-exclamation-triangle me-2"></i>
                Không thể tải đầy đủ thông tin thành viên. Đang hiển thị thông tin hạn chế.
                <div class="mt-2">
                    <small class="text-muted">${message}</small>
                </div>
                <div class="mt-2">
                    <button class="btn btn-sm btn-outline-primary" onclick="reloadMemberDetail()">
                        <i class="fas fa-sync-alt me-1"></i>Thử lại
                    </button>
                </div>
            `;
            document.getElementById('memberDetailContent').appendChild(warningAlert);
            
            return;
        } catch (error) {
            console.error('Error extracting member info from table:', error);
        }
    }
    
    // Nếu không thể lấy từ bảng hoặc có lỗi, hiển thị thông báo lỗi
    document.getElementById('memberDetailContent').innerHTML = `
        <div class="text-center py-5">
            <i class="fas fa-exclamation-triangle text-warning fa-3x mb-3"></i>
            <h5>Không thể tải thông tin</h5>
            <p class="text-muted">${message || 'Có lỗi xảy ra khi tải thông tin chi tiết thành viên.'}</p>
            <button class="btn btn-outline-primary mt-3" onclick="reloadMemberDetail()">
                <i class="fas fa-sync-alt me-2"></i>Thử lại
            </button>
            <button class="btn btn-outline-secondary mt-3 ms-2" data-bs-dismiss="modal">
                <i class="fas fa-arrow-left me-2"></i>Quay lại
            </button>
        </div>
    `;
}

/**
 * Hàm thử lại để tải lại thông tin chi tiết của thành viên
 */
function reloadMemberDetail() {
    // Lấy userId từ attribute data của popup
    const userId = document.getElementById('memberDetailModal').getAttribute('data-user-id');
    
    console.log('Thử lại tải thông tin thành viên ID:', userId);
    
    if (userId) {
        // Reset trạng thái và tải lại
        document.getElementById('memberDetailLoading').style.display = 'block';
        document.getElementById('memberDetailContent').style.display = 'none';
        
        // Gọi lại hàm tải thông tin
        viewMemberDetail(userId);
    } else {
        showNotification('Không thể tải lại: Thiếu ID thành viên', 'error');
    }
}

// Make helper functions globally accessible
window.handleMemberDetailError = handleMemberDetailError;
window.reloadMemberDetail = reloadMemberDetail;

// Make functions globally accessible
window.searchMembers = searchMembers;
window.clearSearch = clearSearch;
window.filterMembers = filterMembers;
window.sortMembers = sortMembers;
window.sortByColumn = sortByColumn;
window.resetFilters = resetFilters;
window.showAddMemberModal = showAddMemberModal;
window.confirmRemoveMember = confirmRemoveMember;
window.addMember = addMember;
window.updateMemberStatus = updateMemberStatus;
window.viewMemberDetail = viewMemberDetail;
window.showNotification = showNotification;
window.animateCounter = animateCounter;
