// API Base URL - Update this to your backend URL
const API_BASE_URL = 'http://localhost:8081/api';

// ===== Authentication Functions =====
async function loginUser(email, password) {
    try {
        console.log('Attempting login to:', `${API_BASE_URL}/auth/login`);
        console.log('Email:', email);
        
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password })
        });

        console.log('Response status:', response.status);
        const data = await response.json();
        console.log('Response data:', data);
        
        if (response.ok && data.success) {
            localStorage.setItem('user', JSON.stringify(data));
            localStorage.setItem('token', data.token);
            localStorage.setItem('userId', data.userId);
            window.location.href = 'dashboard.html';
        } else {
            alert(data.message || 'Login failed. Please check your credentials.');
        }
    } catch (error) {
        console.error('Login error:', error);
        alert('An error occurred during login. Make sure the backend is running on port 8081.');
    }
}

async function registerUser(formData) {
    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                name: formData.name,
                email: formData.email,
                password: formData.password,
                userType: formData.userType
            })
        });

        const data = await response.json();
        
        if (response.ok && data.success !== false) {
            localStorage.setItem('user', JSON.stringify(data));
            localStorage.setItem('token', data.token);
            window.location.href = 'dashboard.html';
        } else {
            // Clean up error message
            let errorMsg = data.message || 'Registration failed';
            if (errorMsg.includes('SQL') || errorMsg.includes('ERROR') || errorMsg.includes('column')) {
                errorMsg = 'Server configuration error. Please contact support or try again later.';
            } else if (errorMsg.includes('already') || errorMsg.includes('exists') || errorMsg.includes('duplicate')) {
                errorMsg = 'This email is already registered. Please use a different email or login.';
            }
            
            if (typeof showError === 'function') {
                showError('email', errorMsg);
            } else {
                console.error('Registration failed:', data.message);
            }
        }
    } catch (error) {
        console.error('Registration error:', error);
        if (typeof showError === 'function') {
            showError('email', 'Unable to connect to server. Please try again.');
        }
    }
}

function logout() {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    window.location.href = 'login.html';
}

// ===== Organization Authentication Functions =====
async function loginOrganization(email, password) {
    try {
        console.log('Attempting org login to:', `${API_BASE_URL}/org/auth/login`);
        
        const response = await fetch(`${API_BASE_URL}/org/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();
        
        if (response.ok && data.success) {
            localStorage.setItem('organization', JSON.stringify(data));
            localStorage.setItem('orgToken', data.token);
            localStorage.setItem('orgId', data.orgId);
            window.location.href = 'org-dashboard.html';
        } else {
            alert(data.message || 'Login failed. Please check your credentials.');
        }
    } catch (error) {
        console.error('Organization login error:', error);
        // Demo fallback - allow login with demo credentials
        if (email === 'org@actify.app' && password === 'org123') {
            const demoOrg = {
                success: true,
                orgId: 1,
                name: 'Green Earth Foundation',
                email: 'org@actify.app',
                token: 'demo-org-token'
            };
            localStorage.setItem('organization', JSON.stringify(demoOrg));
            localStorage.setItem('orgToken', 'demo-org-token');
            localStorage.setItem('orgId', '1');
            window.location.href = 'org-dashboard.html';
        } else {
            alert('An error occurred during login. Make sure the backend is running.');
        }
    }
}

async function registerOrganization(orgData) {
    try {
        const response = await fetch(`${API_BASE_URL}/org/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(orgData)
        });

        const data = await response.json();
        
        if (response.ok && data.success !== false) {
            localStorage.setItem('organization', JSON.stringify(data));
            localStorage.setItem('orgToken', data.token);
            localStorage.setItem('orgId', data.orgId);
            window.location.href = 'org-dashboard.html';
        } else {
            // Clean up error message - remove SQL/technical details
            let errorMsg = data.message || 'Registration failed';
            if (errorMsg.includes('SQL') || errorMsg.includes('ERROR') || errorMsg.includes('column')) {
                errorMsg = 'Server configuration error. Please contact support or try again later.';
            } else if (errorMsg.includes('already') || errorMsg.includes('exists') || errorMsg.includes('duplicate')) {
                errorMsg = 'This email is already registered. Please use a different email or login.';
            }
            
            if (typeof showError === 'function') {
                showError('email', errorMsg);
            } else {
                console.error('Registration failed:', data.message);
            }
        }
    } catch (error) {
        console.error('Organization registration error:', error);
        // Demo fallback - register locally when server is unavailable
        const demoOrg = {
            success: true,
            orgId: Date.now(),
            name: orgData.name,
            email: orgData.email,
            token: 'demo-org-token-' + Date.now()
        };
        localStorage.setItem('organization', JSON.stringify(demoOrg));
        localStorage.setItem('orgToken', demoOrg.token);
        localStorage.setItem('orgId', demoOrg.orgId.toString());
        window.location.href = 'org-dashboard.html';
    }
}

function logoutOrganization() {
    localStorage.removeItem('organization');
    localStorage.removeItem('orgToken');
    localStorage.removeItem('orgId');
    window.location.href = 'login.html';
}

// ===== Organization API Helper Functions =====
async function fetchOrgWithAuth(url, options = {}) {
    const token = localStorage.getItem('orgToken');
    
    if (!token) {
        throw new Error('No organization authentication token');
    }
    
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
        'Authorization': `Bearer ${token}`
    };

    const response = await fetch(url, {
        ...options,
        headers,
    });

    if (response.status === 401) {
        logoutOrganization();
        throw new Error('Unauthorized');
    }

    return response;
}

function checkAuth() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
    }
    return token;
}

// ===== API Helper Functions =====
async function fetchWithAuth(url, options = {}) {
    const token = localStorage.getItem('token');
    
    // No demo mode - always use real API
    if (!token) {
        throw new Error('No authentication token');
    }
    
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(url, {
        ...options,
        headers,
    });

    if (response.status === 401) {
        logout();
        throw new Error('Unauthorized');
    }

    return response;
}

// ===== Dashboard Functions =====
async function loadDashboardData() {
    try {
        checkAuth();
        
        // Fetch fresh user profile data from backend
        const profileResponse = await fetchWithAuth(`${API_BASE_URL}/users/profile`);
        if (profileResponse.ok) {
            const profileData = await profileResponse.json();
            
            // Update localStorage with fresh data
            const updatedUser = {
                id: profileData.id,
                firstName: profileData.firstName,
                lastName: profileData.lastName,
                name: `${profileData.firstName} ${profileData.lastName}`,
                email: profileData.email,
                volunteerPoints: profileData.volunteerPoints,
                eventsCompleted: profileData.eventsCompleted,
                volunteerHours: profileData.volunteerHours,
                phone: profileData.phone,
                country: profileData.country,
                city: profileData.city,
                neighborhood: profileData.neighborhood,
                interests: profileData.interests
            };
            localStorage.setItem('user', JSON.stringify(updatedUser));
            
            // Update dynamic greeting with first name only
            const userNameEl = document.getElementById('userName');
            if (userNameEl) {
                userNameEl.textContent = profileData.firstName || 'User';
            }
            
            await updateDashboardUI(profileData);
        } else {
            // Fallback to localStorage data if API fails
            const user = JSON.parse(localStorage.getItem('user') || '{}');
            const userNameEl = document.getElementById('userName');
            if (userNameEl) {
                const firstName = user.firstName || user.name?.split(' ')[0] || user.email?.split('@')[0] || 'User';
                userNameEl.textContent = firstName;
            }
            
            // Use mock data if API fails
            await updateDashboardUI({
                volunteerPoints: user.volunteerPoints || 2850,
                eventsCompleted: user.eventsCompleted || 12,
                volunteerHours: user.volunteerHours || 48,
                badges: ["First Steps", "Community Hero", "Green Guardian"]
            });
        }
    } catch (error) {
        console.error('Error loading dashboard:', error);
        // Use mock data
        await updateDashboardUI({
            volunteerPoints: 3450,
            eventsCompleted: 18,
            hoursVolunteered: 72,
            badges: ["First Steps", "Community Hero", "Green Guardian"]
        });
    }
}

async function updateDashboardUI(data) {
    // Update points
    const pointsEl = document.getElementById('userPoints');
    if (pointsEl) {
        pointsEl.textContent = data.volunteerPoints?.toLocaleString() || '0';
    }

    // Update events completed
    const eventsEl = document.getElementById('eventsCompleted');
    if (eventsEl) {
        eventsEl.textContent = data.eventsCompleted || '0';
    }

    // Update hours volunteered
    const hoursEl = document.getElementById('hoursVolunteered');
    if (hoursEl) {
        hoursEl.textContent = data.hoursVolunteered || '0';
    }

    // Update badges if present
    if (data.badges && data.badges.length > 0) {
        const badgesContainer = document.getElementById('badgesContainer');
        if (badgesContainer) {
            const badgeEmojis = { 
                "First Steps": "🏆",
                "Community Hero": "🦸",
                "Green Guardian": "🌱",
                "Helper": "🤝",
                "Champion": "🏅"
            };
            
            badgesContainer.innerHTML = data.badges.map(badge => `
                <div class="badge-item">
                    <div class="badge-icon">${badgeEmojis[badge] || '⭐'}</div>
                    <p class="badge-name">${badge}</p>
                </div>
            `).join('');
        }
    }
    
    // Load and display upcoming events
    await loadUpcomingEvents();
}

// Load and display upcoming events on dashboard
async function loadUpcomingEvents() {
    try {
        const response = await fetch(`${API_BASE_URL}/events`);
        if (response.ok) {
            const events = await response.json();
            // Filter only active events and sort by date
            const activeEvents = events
                .filter(event => event.status === 'active')
                .sort((a, b) => new Date(a.date) - new Date(b.date))
                .slice(0, 5); // Show max 5 upcoming events
            
            const upcomingEventsEl = document.getElementById('upcomingEvents');
            if (upcomingEventsEl) {
                if (activeEvents.length === 0) {
                    upcomingEventsEl.innerHTML = `
                        <div class="no-events" style="text-align: center; padding: 2rem; color: var(--text-muted);">
                            <i data-lucide="calendar-x" style="width: 48px; height: 48px; margin-bottom: 1rem;"></i>
                            <p>No upcoming events available</p>
                            <a href="events.html" class="btn btn-sm btn-outline" style="margin-top: 1rem;">Browse Events</a>
                        </div>
                    `;
                } else {
                    upcomingEventsEl.innerHTML = activeEvents.map(event => `
                        <div class="event-list-item">
                            <div class="event-info">
                                <h4 class="event-name">${event.title}</h4>
                                <p class="event-details">
                                    <i data-lucide="calendar" class="icon-sm"></i>
                                    ${formatEventDate(event.date)}${event.time ? ' • ' + event.time : ''}
                                </p>
                                <p class="event-details">
                                    <i data-lucide="map-pin" class="icon-sm"></i>
                                    ${event.location}${event.city ? ', ' + event.city : ''}
                                </p>
                            </div>
                            <div class="event-reward">
                                <i data-lucide="zap" class="icon-sm"></i>
                                <span>${event.pointsReward || 100} coins</span>
                            </div>
                        </div>
                    `).join('');
                }
                lucide.createIcons();
            }
        }
    } catch (error) {
        console.error('Error loading upcoming events:', error);
    }
}

// Format event date for display
function formatEventDate(dateStr) {
    if (!dateStr) return 'TBD';
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

// ===== Events Functions =====
async function loadEvents() {
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/events`);
        if (response.ok) {
            const events = await response.json();
            return events;
        }
    } catch (error) {
        console.error('Error loading events:', error);
    }
    return [];
}

async function registerForEvent(eventId) {
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/events/${eventId}/register`, {
            method: 'POST'
        });

        const data = await response.json();
        
        if (response.ok && data.success) {
            alert(data.message || 'Successfully registered for event!');
            return true;
        } else {
            alert(data.message || 'Failed to register for event.');
            return false;
        }
    } catch (error) {
        console.error('Error registering for event:', error);
        alert('An error occurred while registering. Please make sure you are logged in.');
        return false;
    }
}

// ===== Leaderboard Functions =====
async function loadLeaderboard() {
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/leaderboard`);
        if (response.ok) {
            const leaderboard = await response.json();
            return leaderboard;
        }
    } catch (error) {
        console.error('Error loading leaderboard:', error);
    }
    return [];
}

// ===== Utility Functions =====
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
    });
}

function getCurrentUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
}

function updateUserCoins() {
    const user = getCurrentUser();
    const coinElements = document.querySelectorAll('.coin-value');
    // Show 0 if user has no coin data
    const coins = (user && user.volunteerPoints) ? user.volunteerPoints : 0;
    coinElements.forEach(el => {
        el.textContent = coins.toLocaleString();
    });
}

function updateUserAvatar() {
    const user = getCurrentUser();
    const avatarElements = document.querySelectorAll('.user-avatar');
    if (user && user.name) {
        avatarElements.forEach(el => {
            el.textContent = user.name.charAt(0).toUpperCase();
        });
    }
}

// ===== Profile Page Functions =====
async function loadProfileData() {
    try {
        checkAuth();
        
        // Fetch fresh user profile data from backend (same as dashboard)
        const profileResponse = await fetchWithAuth(`${API_BASE_URL}/users/profile`);
        if (profileResponse.ok) {
            const profileData = await profileResponse.json();
            
            // Update localStorage with fresh data
            const updatedUser = {
                id: profileData.id,
                firstName: profileData.firstName,
                lastName: profileData.lastName,
                name: `${profileData.firstName} ${profileData.lastName}`,
                email: profileData.email,
                volunteerPoints: profileData.volunteerPoints,
                eventsCompleted: profileData.eventsCompleted,
                volunteerHours: profileData.volunteerHours,
                phone: profileData.phone,
                country: profileData.country,
                city: profileData.city,
                neighborhood: profileData.neighborhood,
                interests: profileData.interests
            };
            localStorage.setItem('user', JSON.stringify(updatedUser));
            
            // Update profile page elements with real data
            const profileNameEl = document.getElementById('profileNameNew');
            const profileEmailEl = document.getElementById('profileEmailNew');
            
            if (profileNameEl && profileData.firstName) {
                profileNameEl.textContent = `Welcome, ${profileData.firstName}!`;
            }
            if (profileEmailEl) {
                profileEmailEl.textContent = profileData.email;
            }
            
            // Update avatar with first letter
            const avatarLetter = profileData.firstName.charAt(0).toUpperCase();
            const avatarEls = document.querySelectorAll('#profileAvatarNew, #navAvatar');
            avatarEls.forEach(el => {
                if (el) el.textContent = avatarLetter;
            });
            
            // Update stats
            const pointsEl = document.getElementById('pointsDisplay');
            const eventsEl = document.getElementById('eventsDisplay');
            const hoursEl = document.getElementById('hoursDisplay');
            const coinEl = document.getElementById('coinValue');
            
            if (pointsEl) pointsEl.textContent = profileData.volunteerPoints.toLocaleString();
            if (eventsEl) eventsEl.textContent = profileData.eventsCompleted;
            if (hoursEl) hoursEl.textContent = profileData.volunteerHours;
            if (coinEl) coinEl.textContent = profileData.volunteerPoints.toLocaleString();
            
            updateProfileUI(profileData);
        } else {
            // API failed - redirect to login
            console.error('Failed to load profile data from API');
            localStorage.removeItem('user');
            localStorage.removeItem('token');
            window.location.href = 'login.html';
        }
    } catch (error) {
        console.error('Error loading profile:', error);
        // Use mock data
        updateProfileUI({
            volunteerPoints: 3450,
            eventsCompleted: 18,
            hoursVolunteered: 72,
            badges: 5
        });
    }
}

function updateProfileUI(data) {
    // Update stats
    const elements = {
        totalPoints: document.getElementById('totalPoints'),
        eventsCompleted: document.getElementById('eventsCompleted'),
        eventsAttended: document.getElementById('eventsAttended'),
        hoursVolunteered: document.getElementById('hoursVolunteered'),
        hoursContributed: document.getElementById('hoursContributed'),
        badgesEarned: document.getElementById('badgesEarned')
    };
    
    if (elements.totalPoints) elements.totalPoints.textContent = data.volunteerPoints?.toLocaleString() || '0';
    if (elements.eventsCompleted) elements.eventsCompleted.textContent = data.eventsCompleted || '0';
    if (elements.eventsAttended) elements.eventsAttended.textContent = data.eventsCompleted || '0';
    if (elements.hoursVolunteered) elements.hoursVolunteered.textContent = data.hoursVolunteered || '0';
    if (elements.hoursContributed) elements.hoursContributed.textContent = data.hoursVolunteered || '0';
    if (elements.badgesEarned) elements.badgesEarned.textContent = data.badges || '0';
    
    // Update progress bars
    const eventProgress = Math.min((data.eventsCompleted || 0) / 10 * 100, 100);
    const timeProgress = Math.min((data.hoursVolunteered || 0) / 50 * 100, 100);
    const badgeProgress = Math.min((data.badges || 0) / 15 * 100, 100);
    
    const eventProgressBar = document.getElementById('eventProgressBar');
    const timeProgressBar = document.getElementById('timeProgressBar');
    const badgeProgressBar = document.getElementById('badgeProgressBar');
    const eventProgressText = document.getElementById('eventProgress');
    const timeProgressText = document.getElementById('timeProgress');
    const badgeProgressText = document.getElementById('badgeProgress');
    
    if (eventProgressBar) eventProgressBar.style.width = eventProgress + '%';
    if (timeProgressBar) timeProgressBar.style.width = timeProgress + '%';
    if (badgeProgressBar) badgeProgressBar.style.width = badgeProgress + '%';
    if (eventProgressText) eventProgressText.textContent = `${data.eventsCompleted || 0}/10 events`;
    if (timeProgressText) timeProgressText.textContent = `${data.hoursVolunteered || 0}/50 hours`;
    if (badgeProgressText) badgeProgressText.textContent = `${data.badges || 0}/15 badges`;
}

// Initialize user data on page load
document.addEventListener('DOMContentLoaded', () => {
    updateUserCoins();
    updateUserAvatar();
});
