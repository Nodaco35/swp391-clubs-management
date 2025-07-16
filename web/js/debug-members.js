// File debug này đã bị tắt để không hiển thị nút debug trong production
// Giữ file này trong project để tham khảo khi cần debug trong tương lai

/*
console.log('Department Members JS Debug - Starting...');

// Debug function to check elements
function debugMembersPage() {
    console.log('=== DEBUGGING MEMBERS PAGE ===');
    
    // Check if elements exist
    const statusFilter = document.getElementById('statusFilter');
    const sortOrder = document.getElementById('sortOrder');
    const tbody = document.querySelector('.table tbody');
    const memberRows = document.querySelectorAll('.member-row');
    
    console.log('Status Filter element:', statusFilter);
    console.log('Sort Order element:', sortOrder);
    console.log('Table body element:', tbody);
    console.log('Member rows found:', memberRows.length);
    
    if (memberRows.length > 0) {
        console.log('First member row:', memberRows[0]);
        console.log('Member row data-active:', memberRows[0].getAttribute('data-active'));
        console.log('Member row display style:', memberRows[0].style.display);
        console.log('Member row computed display:', window.getComputedStyle(memberRows[0]).display);
    }
    
    // Check if any filters are applied
    if (statusFilter) {
        console.log('Status filter value:', statusFilter.value);
    }
    
    if (sortOrder) {
        console.log('Sort order value:', sortOrder.value);
    }
    
    // Check table visibility
    const table = document.querySelector('.table');
    const tableContainer = document.querySelector('.table-responsive');
    console.log('Table element:', table);
    console.log('Table container:', tableContainer);
    
    if (tableContainer) {
        console.log('Table container display:', tableContainer.style.display);
        console.log('Table container computed display:', window.getComputedStyle(tableContainer).display);
    }
    
    // Check empty state
    const emptyState = document.querySelector('.empty-state');
    const dynamicEmpty = document.querySelector('.dynamic-empty');
    console.log('Empty state element:', emptyState);
    console.log('Dynamic empty state element:', dynamicEmpty);
    
    if (emptyState) {
        console.log('Empty state display:', emptyState.style.display);
        console.log('Empty state computed display:', window.getComputedStyle(emptyState).display);
    }
    
    if (dynamicEmpty) {
        console.log('Dynamic empty state display:', dynamicEmpty.style.display);
        console.log('Dynamic empty state computed display:', window.getComputedStyle(dynamicEmpty).display);
    }
}

// Add debug button to page
function addDebugButton() {
    const debugBtn = document.createElement('button');
    debugBtn.textContent = 'Debug Members';
    debugBtn.className = 'btn btn-warning position-fixed';
    debugBtn.style.cssText = 'top: 10px; right: 10px; z-index: 9999;';
    debugBtn.onclick = debugMembersPage;
    document.body.appendChild(debugBtn);
}

// Run debug when page loads
$(document).ready(function() {
    console.log('Debug script loaded');
    addDebugButton();
    
    // Debug after 1 second to let everything load
    setTimeout(() => {
        debugMembersPage();
    }, 1000);
});
*/
