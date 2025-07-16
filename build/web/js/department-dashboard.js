/**
 * Department Dashboard JavaScript
 * Handles dashboard UI interactions, charts, time updates, and responsive behavior
 */

// Global variables
let taskProgressChart = null;
let memberActivityChart = null;

// Initialize when document is ready
document.addEventListener('DOMContentLoaded', function() {
    initializeDashboard();
    addCardClickHandlers();
});

/**
 * Initialize dashboard components
 */
function initializeDashboard() {
    // Start time updates
    updateTime();
    setInterval(updateTime, 1000);
    
    // Initialize charts
    initializeCharts();
    
    // Setup event listeners
    setupEventListeners();
}

/**
 * Setup all event listeners
 */
function setupEventListeners() {
    // Menu active state
    document.querySelectorAll('.menu-link').forEach(link => {
        link.addEventListener('click', function(e) {
            document.querySelectorAll('.menu-item').forEach(item => {
                item.classList.remove('active');
            });
            this.parentElement.classList.add('active');
        });
    });

    // Close sidebar when clicking outside on mobile
    document.addEventListener('click', function(e) {
        const sidebar = document.getElementById('sidebar');
        const toggle = document.querySelector('.mobile-menu-toggle');
        
        if (window.innerWidth <= 768 && 
            !sidebar.contains(e.target) && 
            !toggle.contains(e.target) && 
            sidebar.classList.contains('active')) {
            sidebar.classList.remove('active');
        }
    });    // Responsive chart resize
    window.addEventListener('resize', function() {
        if (taskProgressChart && typeof taskProgressChart.resize === 'function') {
            taskProgressChart.resize();
        }
        if (memberActivityChart && typeof memberActivityChart.resize === 'function') {
            memberActivityChart.resize();
        }
    });
}

/**
 * Update current time display
 */
function updateTime() {
    const now = new Date();
    const timeString = now.toLocaleTimeString('vi-VN', { 
        hour: '2-digit', 
        minute: '2-digit',
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    });
    
    const currentTimeElement = document.getElementById('currentTime');
    const lastUpdateElement = document.getElementById('lastUpdate');
    
    if (currentTimeElement) {
        currentTimeElement.textContent = timeString;
    }
    
    if (lastUpdateElement) {
        lastUpdateElement.textContent = now.toLocaleTimeString('vi-VN');
    }
}

/**
 * Initialize all charts
 */
function initializeCharts() {
    initializeTaskProgressChart();
    initializeMemberActivityChart();
}

/**
 * Initialize task progress chart
 */
function initializeTaskProgressChart() {
    const taskCtx = document.getElementById('taskProgressChart');
    if (!taskCtx) return;
    
    // Get data from global variables (set by JSP)
    const chartData = window.dashboardData || {};
    
    taskProgressChart = new Chart(taskCtx.getContext('2d'), {
        type: 'doughnut',
        data: {
            labels: ['Hoàn thành', 'Đang thực hiện', 'To Do'],
            datasets: [{
                data: [
                    chartData.doneTasks || 0,
                    chartData.inProgressTasks || 0,
                    chartData.todoTasks || 0
                ],
                backgroundColor: [
                    '#10b981', // Green for completed
                    '#3b82f6', // Blue for in progress  
                    '#ef4444'  // Red for todo
                ],
                borderWidth: 0,
                hoverOffset: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = total > 0 ? Math.round((value / total) * 100) : 0;
                            return `${label}: ${value} (${percentage}%)`;
                        }
                    }
                }
            },
            cutout: '70%',
            animation: {
                animateRotate: true,
                duration: 1000
            }
        }
    });
}

/**
 * Initialize member activity chart
 */
function initializeMemberActivityChart() {
    const memberCtx = document.getElementById('memberActivityChart');
    if (!memberCtx) return;
    
    // Get data from global variables (set by JSP)
    const chartData = window.dashboardData || {};
    
    memberActivityChart = new Chart(memberCtx.getContext('2d'), {
        type: 'doughnut',
        data: {
            labels: ['Hoạt động', 'Không hoạt động'],
            datasets: [{
                data: [
                    chartData.activeMembers || 0,
                    (chartData.totalMembers || 0) - (chartData.activeMembers || 0)
                ],
                backgroundColor: [
                    '#10b981', // Green for active
                    '#f59e0b'  // Orange for inactive
                ],
                borderWidth: 0,
                hoverOffset: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = total > 0 ? Math.round((value / total) * 100) : 0;
                            return `${label}: ${value} (${percentage}%)`;
                        }
                    }
                }
            },
            cutout: '70%',
            animation: {
                animateRotate: true,
                duration: 1000
            }
        }
    });
}

/**
 * Mobile sidebar toggle
 */
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) {
        sidebar.classList.toggle('active');
    }
}

// Make toggleSidebar globally accessible
window.toggleSidebar = toggleSidebar;

/**
 * Refresh dashboard data
 */
function refreshDashboard() {
    // Show loading state
    showLoadingState();
    
    // Reload the page to get fresh data
    window.location.reload();
}

/**
 * Show loading state for dashboard refresh
 */
function showLoadingState() {
    const cards = document.querySelectorAll('.stat-number');
    cards.forEach(card => {
        card.style.opacity = '0.5';
    });
}

/**
 * Update chart data dynamically (for future AJAX updates)
 */
function updateChartData(newData) {
    if (taskProgressChart && newData.tasks) {
        taskProgressChart.data.datasets[0].data = [
            newData.tasks.done || 0,
            newData.tasks.inProgress || 0,
            newData.tasks.todo || 0
        ];
        taskProgressChart.update();
    }
    
    if (memberActivityChart && newData.members) {
        memberActivityChart.data.datasets[0].data = [
            newData.members.active || 0,
            (newData.members.total || 0) - (newData.members.active || 0)
        ];
        memberActivityChart.update();
    }
}

/**
 * Navigate to specific section
 */
function navigateToMembers() {
    window.location.href = 'department-members';
}

function navigateToTasks() {
    window.location.href = 'department-tasks';
}

function navigateToEvents() {
    window.location.href = 'department-events';
}

/**
 * Utility function to format numbers
 */
function formatNumber(num) {
    if (num >= 1000000) {
        return (num / 1000000).toFixed(1) + 'M';
    } else if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'K';
    }
    return num.toString();
}

/**
 * Add click handlers to stat cards for navigation
 */
function addCardClickHandlers() {
    // Find cards by their icons using a more compatible approach
    const allCards = document.querySelectorAll('.stats-card');
    
    allCards.forEach(card => {
        const icon = card.querySelector('i');
        if (!icon) return;
        
        const iconClasses = icon.className;
        card.style.cursor = 'pointer';
        
        if (iconClasses.includes('fa-users')) {
            // Members card
            card.addEventListener('click', navigateToMembers);
        } else if (iconClasses.includes('fa-tasks')) {
            // Tasks card
            card.addEventListener('click', navigateToTasks);
        } else if (iconClasses.includes('fa-calendar-check')) {
            // Events card
            card.addEventListener('click', navigateToEvents);
        }
    });
}

/**
 * Export functions for global access
 */
window.DashboardManager = {
    toggleSidebar,
    refreshDashboard,
    updateChartData,
    navigateToMembers,
    navigateToTasks,
    navigateToEvents
};
