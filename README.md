# Actify - Volunteer Management Platform

A comprehensive volunteer management system with points, badges, and rewards.

## Project Structure

```
actify/
├── backend/                 # Spring Boot Java backend
│   ├── src/main/java/      # Java source code
│   └── pom.xml            # Maven configuration
├── database/               # PostgreSQL database scripts
│   ├── init.sql           # Complete setup script
│   ├── 01_create_database.sql
│   ├── 02_create_tables.sql
│   ├── 03_seed_data.sql
│   ├── reset_database.sql
│   └── queries.sql        # Useful queries
├── frontend/               # Production frontend (HTML/CSS/JS)
│   ├── *.html             # All page templates
│   ├── css/               # Modular stylesheets
│   ├── app.js             # Core JavaScript
│   └── components.js      # Reusable components
├── nextjs-version/        # Next.js/React alternative (dev)
└── styles/                # Shared style assets
```

## Technology Stack

### Frontend (Production)
- **Vanilla HTML/CSS/JavaScript** - No build process required
- **Lucide Icons** - Icon library via CDN
- **Leaflet.js** - Interactive maps

### Frontend (Alternative)
- **Next.js 16.0.0** - React framework with Turbopack
- **React 19.2.0** - UI library
- **TypeScript** - Type safety
- **Tailwind CSS** - Utility-first styling
- **shadcn/ui** - Component library

### Backend
- **Spring Boot** - Java REST API
- **PostgreSQL** - Database
- **JWT** - Authentication

## Getting Started

### 1. Database Setup

**Install PostgreSQL 17 and pgAdmin 4**, then set up the database:

```bash
# Navigate to database folder
cd database

# Option A: Run complete setup (creates DB, tables, and seed data)
psql -U postgres -f init.sql

# Option B: Run scripts individually
psql -U postgres -f 01_create_database.sql
psql -U postgres -d actify_db -f 02_create_tables.sql
psql -U postgres -d actify_db -f 03_seed_data.sql
```

Or use **pgAdmin 4**:
1. Create database `actify_db`
2. Open Query Tool
3. Run `init.sql` or individual scripts

See `database/README.md` for detailed instructions.

### 2. Backend Setup

1. **Configure database connection:**
Edit `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/actify_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

2. **Start the backend:**
```bash
cd backend
mvn spring-boot:run
```

Backend runs on `http://localhost:8080`

### 3. Frontend Setup

### 3. Frontend Setup

Simply open `frontend/index.html` in your browser, or use a local server:

```bash
cd frontend
python -m http.server 8000
# or
npx serve
```

**Demo Login:**
- Email: `demo@actify.app`
- Password: `demo123`

### Alternative: Next.js Version

1. **Install dependencies:**
```bash
cd nextjs-version
pnpm install --legacy-peer-deps
```

2. **Run development server:**
```bash
pnpm dev
```

3. **Open browser:**
Navigate to [http://localhost:3000](http://localhost:3000)

## Features

- 🎯 **Event Management** - Browse and register for volunteer opportunities
- 🏆 **Points System** - Earn points for participation
- 🎖️ **Badges** - Unlock achievements and milestones
- 🎁 **Rewards** - Redeem points for rewards
- 📊 **Leaderboard** - Compare your impact with others
- 📍 **Map Integration** - Find events near you
- 👤 **Profile** - Track your volunteer journey

## API Endpoints

Backend runs on `http://localhost:8080/api`

- `POST /auth/login` - User authentication
- `POST /auth/register` - User registration
- `GET /events` - List all events
- `POST /events/{id}/register` - Register for event
- `GET /user/dashboard` - User statistics
- `GET /leaderboard` - Top volunteers

## Configuration

### Database
Edit `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/actify_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=validate
```

See `database/README.md` for backup, restore, and maintenance commands.

### Frontend
Edit `frontend/app.js`:
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

## Database Schema

The database includes these tables:
- **users** - Volunteer profiles and stats
- **organizations** - Non-profit organizations
- **events** - Volunteer opportunities
- **event_registrations** - User event signups
- **badges** - Achievement badges
- **rewards** - Redeemable rewards
- **reward_redemptions** - Redemption history
- **notifications** - User notifications

Run `database/queries.sql` for useful analytics and reporting queries.

## License

MIT License - feel free to use for your projects!
