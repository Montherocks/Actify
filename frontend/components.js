// ===== Component Functions =====

// Cause-based default images using Unsplash
const CAUSE_IMAGES = {
    environment: 'https://images.unsplash.com/photo-1532996122724-e3c354a0b15b?w=400&h=200&fit=crop',
    education: 'https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=400&h=200&fit=crop',
    health: 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1f?w=400&h=200&fit=crop',
    charity: 'https://images.unsplash.com/photo-1593113598332-cd288d649433?w=400&h=200&fit=crop',
    community: 'https://images.unsplash.com/photo-1559027615-cd4628902d4a?w=400&h=200&fit=crop',
    animals: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=400&h=200&fit=crop',
    elderly: 'https://images.unsplash.com/photo-1581579438747-1dc8d17bbce4?w=400&h=200&fit=crop',
    youth: 'https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?w=400&h=200&fit=crop',
    'disaster-relief': 'https://images.unsplash.com/photo-1469571486292-0ba58a3f068b?w=400&h=200&fit=crop',
    other: 'https://images.unsplash.com/photo-1559027615-cd4628902d4a?w=400&h=200&fit=crop'
};

// Get image URL based on cause
function getEventImage(event) {
    // Prefer imageUrl if it exists and is valid
    if (event.imageUrl && event.imageUrl !== '' && event.imageUrl !== '/placeholder.svg' && event.imageUrl !== 'null') {
        return event.imageUrl;
    }
    // Fallback to image field
    if (event.image && event.image !== '/placeholder.svg' && event.image !== '' && event.image !== 'null') {
        return event.image;
    }
    // Default to cause-based image
    const cause = (event.cause || 'other').toLowerCase().replace(' ', '-');
    return CAUSE_IMAGES[cause] || CAUSE_IMAGES.other;
}

// Create Event Card Component
function createEventCard(event) {
    const imageUrl = getEventImage(event);
    return `
        <div class="event-card">
            <div class="event-image">
                <img src="${imageUrl}" alt="${event.title}" onerror="this.src='${CAUSE_IMAGES.other}'" />
            </div>
            <div class="event-content">
                <div class="event-header">
                    <div>
                        <h3 class="event-title">${event.title}</h3>
                        <p class="event-organizer">${event.organizer}</p>
                    </div>
                    <span class="event-cause">${event.cause}</span>
                </div>
                
                <p class="event-description">${event.description}</p>
                
                <div class="event-meta">
                    <div class="event-meta-item">
                        <i data-lucide="calendar" class="icon-sm"></i>
                        ${formatDate(event.date)} at ${event.time}
                    </div>
                    <div class="event-meta-item">
                        <i data-lucide="map-pin" class="icon-sm"></i>
                        ${event.location}
                    </div>
                    <div class="event-meta-item">
                        <i data-lucide="users" class="icon-sm"></i>
                        ${event.spots} spots available (${event.volunteers} registered)
                    </div>
                </div>
                
                <div class="event-footer">
                    <div class="event-coins">
                        <i data-lucide="zap" class="icon-sm"></i>
                        <span class="coins-amount">${event.coinsReward}</span>
                        <span class="coins-text">coins</span>
                    </div>
                    <button 
                        class="btn btn-outline btn-sm" 
                        onclick="handleEventRegistration(${event.id})"
                        data-event-id="${event.id}"
                    >
                        Register
                    </button>
                </div>
            </div>
        </div>
    `;
}

// Handle event registration
async function handleEventRegistration(eventId) {
    const button = document.querySelector(`button[data-event-id="${eventId}"]`);
    if (!button) return;

    const originalText = button.textContent;
    button.textContent = 'Registering...';
    button.disabled = true;

    const success = await registerForEvent(eventId);

    if (success) {
        button.textContent = 'Registered';
        button.classList.remove('btn-outline');
        button.classList.add('btn-primary');
    } else {
        button.textContent = originalText;
        button.disabled = false;
    }
}

// Create Badge Component
function createBadge(badgeName) {
    const badgeEmojis = {
        "First Steps": "🏆",
        "Community Hero": "🦸",
        "Green Guardian": "🌱",
        "Helper": "🤝",
        "Champion": "🏅",
        "Earth Saver": "🌍",
        "Teacher": "📚",
        "Donor": "❤️"
    };

    return `
        <div class="badge-item">
            <div class="badge-icon">${badgeEmojis[badgeName] || '⭐'}</div>
            <p class="badge-name">${badgeName}</p>
        </div>
    `;
}

// Create Coin Display Component
function createCoinDisplay(coins) {
    return `
        <div class="coin-display-card">
            <div class="card-header">
                <div class="card-icon primary-icon">
                    <i data-lucide="trophy" class="icon-filled"></i>
                </div>
                <h3 class="card-title-lg">Volunteer Points</h3>
            </div>
            <div class="coin-value-lg">${coins.toLocaleString()}</div>
            <p class="card-subtitle">Keep volunteering to earn more</p>
        </div>
    `;
}

// Create Upcoming Event Item
function createUpcomingEventItem(event) {
    return `
        <div class="event-list-item">
            <div class="event-info">
                <h4 class="event-name">${event.title}</h4>
                <p class="event-details">
                    <i data-lucide="calendar" class="icon-sm"></i>
                    ${formatDate(event.date)} • ${event.time}
                </p>
                <p class="event-details">
                    <i data-lucide="map-pin" class="icon-sm"></i>
                    ${event.location}
                </p>
            </div>
            <div class="event-reward">
                <i data-lucide="zap" class="icon-sm"></i>
                <span>${event.coinsReward} coins</span>
            </div>
        </div>
    `;
}

// Create Navigation Component
function createNavigation(currentPage = '') {
    const user = getCurrentUser();
    const userName = user?.name || 'User';
    const userCoins = user?.volunteerPoints || 0;
    const userInitial = userName.charAt(0).toUpperCase();

    return `
        <nav class="nav-logged-in">
            <div class="nav-container">
                <a href="dashboard.html" class="nav-brand">
                    <div class="brand-icon-small">
                        <i data-lucide="heart" class="icon-filled"></i>
                    </div>
                    <span class="brand-name">Actify</span>
                </a>

                <div class="nav-links-main">
                    <a href="dashboard.html" class="nav-link ${currentPage === 'dashboard' ? 'active' : ''}">
                        <i data-lucide="heart"></i>
                        Dashboard
                    </a>
                    <a href="events.html" class="nav-link ${currentPage === 'events' ? 'active' : ''}">
                        <i data-lucide="map-pin"></i>
                        Events
                    </a>
                    <a href="rewards.html" class="nav-link ${currentPage === 'rewards' ? 'active' : ''}">
                        <i data-lucide="gift"></i>
                        Rewards
                    </a>
                    <a href="leaderboard.html" class="nav-link ${currentPage === 'leaderboard' ? 'active' : ''}">
                        <i data-lucide="trophy"></i>
                        Leaderboard
                    </a>
                </div>

                <div class="nav-user">
                    <div class="user-coins">
                        <span class="coin-value">${userCoins.toLocaleString()}</span>
                        <span class="coin-label">coins</span>
                    </div>
                    <div class="user-avatar" onclick="logout()">${userInitial}</div>
                </div>
            </div>

            <!-- Mobile Navigation -->
            <div class="nav-mobile">
                <a href="dashboard.html" class="nav-mobile-item ${currentPage === 'dashboard' ? 'active' : ''}">
                    <i data-lucide="heart"></i>
                    <span>Dashboard</span>
                </a>
                <a href="events.html" class="nav-mobile-item ${currentPage === 'events' ? 'active' : ''}">
                    <i data-lucide="map-pin"></i>
                    <span>Events</span>
                </a>
                <a href="rewards.html" class="nav-mobile-item ${currentPage === 'rewards' ? 'active' : ''}">
                    <i data-lucide="gift"></i>
                    <span>Rewards</span>
                </a>
                <a href="leaderboard.html" class="nav-mobile-item ${currentPage === 'leaderboard' ? 'active' : ''}">
                    <i data-lucide="trophy"></i>
                    <span>Leaderboard</span>
                </a>
            </div>
        </nav>
    `;
}

// Create Leaderboard Row
function createLeaderboardRow(user, currentUserId = null) {
    const isCurrentUser = user.id === currentUserId || user.isCurrentUser;

    return `
        <div class="leaderboard-row ${isCurrentUser ? 'current-user' : ''}">
            <div class="lb-col-rank">
                <span class="rank-badge ${user.rank <= 3 ? 'rank-top' : ''}">#${user.rank}</span>
            </div>
            <div class="lb-col-name">
                <div class="user-info">
                    <div class="user-avatar-sm">${user.name.charAt(0)}</div>
                    <span>${user.name}</span>
                    ${isCurrentUser ? '<span class="you-badge">You</span>' : ''}
                </div>
            </div>
            <div class="lb-col-points">
                <span class="points-value">${user.points.toLocaleString()}</span>
            </div>
            <div class="lb-col-events">${user.events}</div>
        </div>
    `;
}

// Format date helper
function formatDate(dateString) {
    const date = new Date(dateString);
    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    return date.toLocaleDateString('en-US', options);
}

// Show loading spinner
function showLoading(containerId) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = '<div class="text-center" style="padding: 2rem;">Loading...</div>';
    }
}

// Show error message
function showError(containerId, message) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `<div class="text-center" style="padding: 2rem; color: var(--text-muted);">${message}</div>`;
    }
}

// Debounce utility
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Export functions to global scope
if (typeof window !== 'undefined') {
    window.createEventCard = createEventCard;
    window.handleEventRegistration = handleEventRegistration;
    window.createBadge = createBadge;
    window.createCoinDisplay = createCoinDisplay;
    window.createUpcomingEventItem = createUpcomingEventItem;
    window.createNavigation = createNavigation;
    window.createLeaderboardRow = createLeaderboardRow;
    window.showLoading = showLoading;
    window.showError = showError;
    window.debounce = debounce;
}
