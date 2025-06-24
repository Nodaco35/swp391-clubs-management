console.log('=== JavaScript Debug Test ===');

// Test 1: Check if jQuery is loaded
console.log('jQuery loaded:', typeof $ !== 'undefined');

// Test 2: Check if Chart.js is loaded  
console.log('Chart.js loaded:', typeof Chart !== 'undefined');

// Test 3: Check if Bootstrap is loaded
console.log('Bootstrap loaded:', typeof bootstrap !== 'undefined');

// Test 4: Check dashboard data
console.log('Dashboard data:', window.dashboardData);

// Test 5: Check if canvas elements exist
setTimeout(() => {
    const taskChart = document.getElementById('taskProgressChart');
    const memberChart = document.getElementById('memberActivityChart');
    console.log('Task chart element:', taskChart ? 'Found' : 'Not found');
    console.log('Member chart element:', memberChart ? 'Found' : 'Not found');
    
    // Test 6: Check if functions are exposed
    console.log('toggleSidebar function:', typeof window.toggleSidebar);
    console.log('searchMembers function:', typeof window.searchMembers);
    console.log('DashboardManager:', typeof window.DashboardManager);
    
    // Test 7: Check member rows
    const memberRows = document.querySelectorAll('.member-row');
    console.log('Member rows found:', memberRows.length);
    
    // Test 8: Check if modal can be initialized
    const modalElement = document.getElementById('addMemberModal');
    if (modalElement && typeof bootstrap !== 'undefined') {
        try {
            const modal = new bootstrap.Modal(modalElement);
            console.log('Modal initialization: Success');
        } catch (e) {
            console.log('Modal initialization error:', e.message);
        }
    }
    
}, 1000);

// Test DOM elements after page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM Content Loaded');
    
    // Check common elements
    const elements = {
        'searchInput': document.getElementById('searchInput'),
        'statusFilter': document.getElementById('statusFilter'), 
        'sidebar': document.getElementById('sidebar'),
        'currentTime': document.getElementById('currentTime'),
        'toastContainer': document.getElementById('toastContainer') || document.querySelector('.toast-container')
    };
    
    Object.keys(elements).forEach(key => {
        console.log(`${key} element:`, elements[key] ? 'Found' : 'Not found');
    });
});

// Test error handling
window.addEventListener('error', function(e) {
    console.error('JavaScript Error:', e.message, 'at', e.filename, ':', e.lineno);
});

console.log('=== Debug Test Complete ===');
