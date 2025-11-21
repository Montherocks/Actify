<p align="center">
  <img src="https://img.shields.io/badge/Actify-Volunteer%20Management%20Platform%20%7C%20Spring%20Boot%20%7C%20Modern%20Frontend-10b981?style=for-the-badge" alt="Actify"/>
</p>

<h1 align="center">🌟 Actify</h1>

<p align="center">
  <img src="https://img.icons8.com/color/96/000000/leaf.png" width="80"/>
</p>

<p align="center">
  <b>🌱 Empowering communities through volunteer engagement with gamified experiences and meaningful rewards</b>
</p>

---

## 🚀 Features

- 🎯 **Event Management**: Browse and register for volunteer opportunities with interactive maps
- 🏆 **Gamified Points System**: Earn points for participation and track your volunteer impact
- 🎖️ **Achievement Badges**: Unlock milestones and showcase your volunteer journey
- 🎁 **Rewards Store**: Redeem points for meaningful rewards and recognition
- 📊 **Live Leaderboard**: Compare your impact with other volunteers in your community
- 📍 **Location-Based Events**: Find volunteer opportunities near you with Leaflet maps
- 👤 **Dynamic Profiles**: Personalized dashboard with real-time statistics and progress
- 🔔 **Smart Notifications**: Stay updated on events, achievements, and community news
- 📱 **Responsive Design**: Beautiful UI that works seamlessly on all devices
- 🔐 **Secure Authentication**: JWT-based login with role management

---

## 🏗️ Project Architecture

```text
Actify/
├── 🖥️  backend/                    # Spring Boot Java backend
│   ├── src/main/java/              # Application source code
│   └── pom.xml                     # Maven configuration
├── 🗄️  database/                   # Database setup scripts
│   ├── init.sql                    # Complete database setup
│   └── *.sql                       # Table creation & sample data
├── 🌐 frontend/                    # Modern web interface
│   ├── *.html                      # Application pages
│   ├── css/                        # Stylesheets
│   └── *.js                        # JavaScript logic
└── 📱 nextjs-version/              # Modern React interface
    ├── app/                        # Application pages
    └── components/                 # UI components
```

## 📦 Tech Stack

<p align="center">
  <img src="https://img.shields.io/badge/Frontend-HTML%20%7C%20CSS%20%7C%20JavaScript-orange?style=for-the-badge&logo=html5" alt="Frontend"/>
  <img src="https://img.shields.io/badge/Backend-Spring%20Boot%20%7C%20Java%2021-green?style=for-the-badge&logo=spring" alt="Backend"/>
  <img src="https://img.shields.io/badge/Database-PostgreSQL-blue?style=for-the-badge&logo=postgresql" alt="Database"/>
</p>

| Layer | Technology | Purpose |
|-------|------------|----------|
| 🌐 **Frontend** | HTML5, CSS3, JavaScript | Clean, responsive web interface |
| 🎨 **Icons** | Lucide Icons | Beautiful iconography |
| 🗺️ **Maps** | Leaflet.js | Interactive location mapping |
| 🚀 **Backend** | Spring Boot + Java | Robust REST API |
| 🗄️ **Database** | PostgreSQL | Reliable data storage |
| 🔐 **Security** | JWT Authentication | Secure user sessions |
| 📦 **Build** | Maven | Project management |

---

## 🌐 Live Demo

<p align="center">
  <img src="https://img.shields.io/badge/🚀-Live%20Demo%20Available-success?style=for-the-badge" alt="Live Demo"/>
</p>

**Demo Credentials:**
- 📧 Email: `john.doe@example.com`
- 🔑 Password: `password123`

> 💡 **Quick Test**: Use our API test page at `frontend/test-api.html` to register and login with the backend!

---

## ⚡ Quick Start

### 🗄️ 1. Database Setup

<p align="center">
  <img src="https://img.shields.io/badge/PostgreSQL-17-blue?style=flat-square&logo=postgresql" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/pgAdmin-4-orange?style=flat-square" alt="pgAdmin"/>
</p>


### 🚀 2. Backend Setup (Spring Boot)

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-red?style=flat-square&logo=openjdk" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-green?style=flat-square&logo=spring" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Maven-Build-blue?style=flat-square&logo=apache-maven" alt="Maven"/>
</p>


### 🌐 3. Frontend Setup (No Build Required!)

<p align="center">
  <img src="https://img.shields.io/badge/No%20Build-Process-success?style=flat-square" alt="No Build"/>
  <img src="https://img.shields.io/badge/Pure-HTML%2FCSS%2FJS-orange?style=flat-square" alt="Pure Frontend"/>
</p>


> 💡 **Enhanced UI** with modern React components and styling!

## 🔌 API Endpoints

<p align="center">
  <img src="https://img.shields.io/badge/REST-API-green?style=flat-square" alt="REST API"/>
  <img src="https://img.shields.io/badge/Base%20URL-localhost%3A8081%2Fapi-blue?style=flat-square" alt="Base URL"/>
</p>

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| 🔐 `POST` | `/auth/login` | User authentication | ❌ No |
| 📝 `POST` | `/auth/register` | User registration | ❌ No |
| 🎯 `GET` | `/events` | List all events | ✅ Yes |
| ➕ `POST` | `/events/{id}/register` | Register for event | ✅ Yes |
| 👤 `GET` | `/users/profile` | Get user profile | ✅ Yes |
| 📊 `GET` | `/leaderboard` | Top volunteers | ✅ Yes |

---

## ⚙️ Configuration

### 🗄️ Database Configuration
Edit `backend/src/main/resources/application.properties`:
```properties
# PostgreSQL Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/actify_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=validate

# Server Settings
server.port=8081

# JWT Configuration  
jwt.secret=your-secret-key
jwt.expiration=86400000
```

> 📖 See `database/README.md` for backup, restore, and maintenance commands.

### 🌐 Frontend Configuration
Edit `frontend/app.js`:
```javascript
// API Base URL Configuration
const API_BASE_URL = 'http://localhost:8081/api';

// Map Configuration
const MAP_CENTER = [40.7128, -74.0060]; // New York City
const MAP_ZOOM = 12;
```

## 🏗️ Database Schema

<p align="center">
  <img src="https://img.shields.io/badge/Tables-8-blue?style=flat-square" alt="Tables"/>
  <img src="https://img.shields.io/badge/Relations-Fully%20Normalized-green?style=flat-square" alt="Relations"/>
</p>

| Table | Purpose | Key Fields |
|-------|---------|------------|
| 👥 **users** | Volunteer profiles & stats | `firstName`, `lastName`, `volunteerPoints`, `eventsCompleted` |
| 🏢 **organizations** | Non-profit organizations | `name`, `description`, `contactInfo` |
| 🎯 **events** | Volunteer opportunities | `title`, `description`, `location`, `rewardPoints` |
| ✅ **event_registrations** | User event signups | `userId`, `eventId`, `registrationDate`, `status` |
| 🏆 **badges** | Achievement badges | `name`, `description`, `criteria`, `icon` |
| 🎁 **rewards** | Redeemable rewards | `name`, `pointsCost`, `description`, `availability` |
| 💰 **reward_redemptions** | Redemption history | `userId`, `rewardId`, `redemptionDate`, `status` |
| 🔔 **notifications** | User notifications | `userId`, `message`, `type`, `readStatus` |

> 📊 Run `database/queries.sql` for useful analytics and reporting queries!

---

## 🤝 Contributing

<p align="center">
  <img src="https://img.shields.io/badge/Contributions-Welcome-brightgreen?style=for-the-badge" alt="Contributions Welcome"/>
</p>

1. 🍴 Fork the repository
2. 🌿 Create a feature branch (`git checkout -b feature/amazing-feature`)
3. 💡 Make your changes
4. ✅ Commit your changes (`git commit -m 'Add amazing feature'`)
5. 📤 Push to the branch (`git push origin feature/amazing-feature`)
6. 🔄 Open a Pull Request

---

## 👥 Team

<p align="center">
  <img src="https://img.icons8.com/color/48/000000/group.png" width="60"/>
</p>

<table align="center">
  <tr>
    <td align="center">
      <img src="https://img.shields.io/badge/👨‍💻-Developer%20&%20Designer-blue?style=for-the-badge" alt="Developer"/>
    </td>
  </tr>
  <tr>
    <td align="center">
      <b>Building communities through technology</b>
    </td>
  </tr>
</table>

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<p align="center">
  <img src="https://img.shields.io/badge/Made%20with-💚%20for%20Volunteers-10b981?style=for-the-badge"/>
</p>

<p align="center">
  <b>🌟 Star this repo if you find it helpful! 🌟</b>
</p>
