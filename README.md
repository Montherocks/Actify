<p align="center">
  <img src="https://img.shields.io/badge/Actify-Volunteer%20Management%20Platform%20%7C%20Spring%20Boot%20%7C%20Modern%20Frontend-10b981?style=for-the-badge" alt="Actify"/>
</p>

<h1 align="center">🌟 Actify</h1>

<p align="center">
  <img src="https://img.icons8.com/color/96/000000/heart-with-pulse.png" width="80"/>
</p>

<p align="center">
  <b>🚀 Empowering communities through volunteer engagement with gamified experiences, real-time tracking, and meaningful rewards</b>
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
│   ├── src/main/java/com/actify/   # Core application logic
│   │   ├── controller/             # REST API endpoints
│   │   ├── model/                  # JPA entities (User, Event, etc.)
│   │   ├── repository/             # Database repositories
│   │   ├── security/               # JWT authentication & security
│   │   └── config/                 # Configuration classes
│   └── pom.xml                     # Maven dependencies
├── 🗄️  database/                   # PostgreSQL database scripts
│   ├── init.sql                    # Complete database setup
│   ├── 01_create_database.sql      # Database creation
│   ├── 02_create_tables.sql        # Table definitions
│   ├── 03_seed_data.sql            # Sample data insertion
│   └── queries.sql                 # Analytics queries
├── 🌐 frontend/                    # Modern HTML/CSS/JS frontend
│   ├── *.html                      # Page templates
│   ├── css/                        # Modular stylesheets
│   ├── app.js                      # Core JavaScript logic
│   └── components.js               # Reusable UI components
├── ⚛️  nextjs-version/             # Next.js/React alternative
│   ├── app/                        # Next.js 15 app directory
│   ├── components/                 # React components
│   └── lib/                        # Utilities
└── 🎨 styles/                      # Global style assets
```

## 📦 Tech Stack

<p align="center">
  <img src="https://img.shields.io/badge/Frontend-HTML%20%7C%20CSS%20%7C%20JavaScript-orange?style=for-the-badge&logo=html5" alt="Frontend"/>
  <img src="https://img.shields.io/badge/Backend-Spring%20Boot%20%7C%20Java%2021-green?style=for-the-badge&logo=spring" alt="Backend"/>
  <img src="https://img.shields.io/badge/Database-PostgreSQL-blue?style=for-the-badge&logo=postgresql" alt="Database"/>
</p>

| Layer | Technology | Purpose |
|-------|------------|----------|
| 🖼️ **Frontend** | HTML5, CSS3, JavaScript | Modern, responsive UI with no build process |
| 🔧 **Icons** | Lucide Icons | Beautiful, consistent iconography |
| 🗺️ **Maps** | Leaflet.js | Interactive event location mapping |
| ⚛️ **Alternative** | Next.js 16 + React 19 + TypeScript | Modern development experience |
| 🎨 **Styling** | Tailwind CSS + shadcn/ui | Utility-first styling with components |
| 🚀 **Backend** | Spring Boot + Java 21 | Robust REST API with modern Java |
| 🗄️ **Database** | PostgreSQL 17 | Reliable, scalable data storage |
| 🔐 **Auth** | JWT + Spring Security | Secure authentication & authorization |
| 📦 **Build** | Maven | Dependency management & packaging |

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

**Install PostgreSQL 17 and pgAdmin 4**, then set up the database:

```bash
# 🚀 Navigate to database folder
cd database

# 🎯 Option A: Complete setup (one command!)
psql -U postgres -f init.sql

# 🔧 Option B: Step by step
psql -U postgres -f 01_create_database.sql
psql -U postgres -d actify_db -f 02_create_tables.sql
psql -U postgres -d actify_db -f 03_seed_data.sql
```

**🖥️ Using pgAdmin 4:**
1. ➕ Create database `actify_db`
2. 🔧 Open Query Tool  
3. 📂 Run `init.sql` or individual scripts

> 📖 See `database/README.md` for detailed instructions and troubleshooting!

### 🚀 2. Backend Setup (Spring Boot)

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-red?style=flat-square&logo=openjdk" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-green?style=flat-square&logo=spring" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Maven-Build-blue?style=flat-square&logo=apache-maven" alt="Maven"/>
</p>

**1. 🔧 Configure database connection:**

Edit `backend/src/main/resources/application.properties`:
```properties
# 🗄️ Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/actify_db
spring.datasource.username=postgres
spring.datasource.password=your_password

# 🚀 Server Configuration
server.port=8081
```

**2. 🎯 Start the backend:**
```bash
cd backend
mvn spring-boot:run
```

✅ Backend runs at `http://localhost:8081` with API at `/api/*`

### 🌐 3. Frontend Setup (No Build Required!)

<p align="center">
  <img src="https://img.shields.io/badge/No%20Build-Process-success?style=flat-square" alt="No Build"/>
  <img src="https://img.shields.io/badge/Pure-HTML%2FCSS%2FJS-orange?style=flat-square" alt="Pure Frontend"/>
</p>

**🎯 Option A: Direct file access**
```bash
# Simply open in browser!
open frontend/index.html
# or double-click index.html
```

**🚀 Option B: Local server (recommended)**
```bash
cd frontend

# Python server
python -m http.server 5500

# Node.js server
npx serve .

# Live Server (VS Code extension)
# Right-click index.html → "Open with Live Server"
```

🌟 **Frontend runs at:** `http://localhost:5500`

### ⚛️ Alternative: Next.js Version (Modern Development)

<p align="center">
  <img src="https://img.shields.io/badge/Next.js-16-black?style=flat-square&logo=next.js" alt="Next.js"/>
  <img src="https://img.shields.io/badge/React-19-blue?style=flat-square&logo=react" alt="React"/>
  <img src="https://img.shields.io/badge/TypeScript-5-blue?style=flat-square&logo=typescript" alt="TypeScript"/>
</p>

**1. 📦 Install dependencies:**
```bash
cd nextjs-version
pnpm install --legacy-peer-deps
```

**2. 🚀 Run development server:**
```bash
pnpm dev
```

**3. 🌐 Open browser:**
Navigate to [http://localhost:3000](http://localhost:3000)

> 💡 **Features**: Hot reload, TypeScript, Tailwind CSS, shadcn/ui components!

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
