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

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('user', JSON.stringify(data));
            localStorage.setItem('token', data.token);
            window.location.href = 'dashboard.html';
        } else {
            alert('Registration failed. Please try again.');
        }
    } catch (error) {
        console.error('Registration error:', error);
        alert('An error occurred during registration.');
    }
}

function logout() {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    window.location.href = 'login.html';
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
    
    // Handle demo mode
    if (token === 'demo-token-12345') {
        // Return mock response for demo mode
        const mockUser = JSON.parse(localStorage.getItem('user') || '{}');
        return {
            ok: true,
            json: async () => mockUser,
            status: 200
        };
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
            
            // Update dynamic greeting with full name
            const userNameEl = document.getElementById('userName');
            if (userNameEl) {
                userNameEl.textContent = `${profileData.firstName} ${profileData.lastName}`;
            }
            
            updateDashboardUI(profileData);
        } else {
            // Fallback to localStorage data if API fails
            const user = JSON.parse(localStorage.getItem('user') || '{}');
            const userNameEl = document.getElementById('userName');
            if (userNameEl) {
                userNameEl.textContent = user.name || user.firstName + ' ' + user.lastName || user.email?.split('@')[0] || 'User';
            }
            
            // Use mock data if API fails
            updateDashboardUI({
                volunteerPoints: user.volunteerPoints || 2850,
                eventsCompleted: user.eventsCompleted || 12,
                volunteerHours: user.volunteerHours || 48,
                badges: ["First Steps", "Community Hero", "Green Guardian"]
            });
        }
    } catch (error) {
        console.error('Error loading dashboard:', error);
        // Use mock data
        updateDashboardUI({
            volunteerPoints: 3450,
            eventsCompleted: 18,
            hoursVolunteered: 72,
            badges: ["First Steps", "Community Hero", "Green Guardian"]
        });
    }
}

function updateDashboardUI(data) {
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

        if (response.ok) {
            alert('Successfully registered for event!');
            return true;
        } else {
            alert('Failed to register for event.');
            return false;
        }
    } catch (error) {
        console.error('Error registering for event:', error);
        alert('An error occurred while registering.');
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
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        
        // Update profile name and email
        const profileNameEl = document.getElementById('profileName');
        const profileEmailEl = document.getElementById('profileEmail');
        
        if (profileNameEl) {
            profileNameEl.textContent = user.name || 'User';
        }
        if (profileEmailEl) {
            profileEmailEl.textContent = user.email || 'user@actify.app';
        }
        
        // Fetch user profile data
        const profileResponse = await fetchWithAuth(`${API_BASE_URL}/users/profile`);
        if (profileResponse.ok) {
            const profileData = await profileResponse.json();
            updateProfileUI(profileData);
        } else {
            // Use mock data if API fails
            updateProfileUI({
                volunteerPoints: 3450,
                eventsCompleted: 18,
                hoursVolunteered: 72,
                badges: 5
            });
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
