
document.addEventListener('DOMContentLoaded', function() {
    // Mobile menu toggle
    const mobileMenuBtn = document.querySelector('.mobile-menu-btn');
    const mainNav = document.querySelector('.main-nav');
    
    if (mobileMenuBtn && mainNav) {
        mobileMenuBtn.addEventListener('click', function() {
            mainNav.style.display = mainNav.style.display === 'flex' ? 'none' : 'flex';
        });
    }
    
    // Filter clubs
    const filterOptions = document.querySelectorAll('.filter-option');
    const clubCards = document.querySelectorAll('.club-card');
    
    if (filterOptions.length > 0) {
        filterOptions.forEach(option => {
            option.addEventListener('click', function(e) {
                e.preventDefault();
                
                // Remove active class from all options
                filterOptions.forEach(opt => opt.classList.remove('active'));
                
                // Add active class to clicked option
                this.classList.add('active');
                
                const category = this.getAttribute('data-category');
                
                // Show/hide club cards based on category
                clubCards.forEach(card => {
                    if (category === 'all' || card.getAttribute('data-category') === category) {
                        card.style.display = 'block';
                    } else {
                        card.style.display = 'none';
                    }
                });
            });
        });
    }
    
    // Search functionality
    const searchInput = document.getElementById('club-search');
    
    if (searchInput && clubCards.length > 0) {
        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            
            clubCards.forEach(card => {
                const clubName = card.querySelector('.club-title').textContent.toLowerCase();
                const clubCategory = card.querySelector('.club-category').textContent.toLowerCase();
                const clubDescription = card.querySelector('.club-description').textContent.toLowerCase();
                
                if (clubName.includes(searchTerm) || clubCategory.includes(searchTerm) || clubDescription.includes(searchTerm)) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    }
});

function changeSort(value) {
    window.location.href = `events-page?key=${currentKeyword}&publicFilter=${currentPublicFilter}&sortByDate=${value}`;
}

