# Actify Database Setup

PostgreSQL database schema and configuration for the Actify volunteer management platform.

## Prerequisites

- PostgreSQL 17
- pgAdmin 4

## Database Information

- **Database Name**: `actify_db`
- **Default Port**: `5432`
- **Encoding**: `UTF8`

## Quick Setup

### Option 1: Using psql Command Line

```bash
# Navigate to database folder
cd database

# Run the initialization script
psql -U postgres -f init.sql

# Or run specific scripts in order
psql -U postgres -f 01_create_database.sql
psql -U postgres -d actify_db -f 02_create_tables.sql
psql -U postgres -d actify_db -f 03_seed_data.sql
```

### Option 2: Using pgAdmin 4

1. Open pgAdmin 4
2. Connect to your PostgreSQL 17 server
3. Right-click on "Databases" → Create → Database
4. Name it `actify_db`
5. Right-click on `actify_db` → Query Tool
6. Open and execute `init.sql` or run scripts in order:
   - `01_create_database.sql`
   - `02_create_tables.sql`
   - `03_seed_data.sql`

## Database Schema

### Tables

#### users
- User accounts and profiles
- Tracks volunteer points, events completed, and hours
- Supports both volunteers and organizations

#### events
- Volunteer events and opportunities
- Includes location, cause, and points reward

#### event_registrations
- Links users to events they've registered for
- Tracks registration status

#### badges
- Achievement badges earned by users
- Tracks when badges were earned

#### organizations
- Non-profit organizations posting events
- Organization profiles and contact info

## Default Users

The database comes with demo users:

| Email | Password | Points | Events |
|-------|----------|--------|--------|
| demo@actify.com | demo123 | 3200 | 52 |
| alex.johnson@actify.com | demo123 | 1450 | 28 |
| sarah.chen@actify.com | demo123 | 2450 | 45 |
| emma.wilson@actify.com | demo123 | 2100 | 38 |

**Note**: All passwords are hashed with BCrypt. Raw password: `demo123`

## Sample Events

The seed data includes 5 sample events across different causes:
- Park Cleanup Drive (Environment)
- Community Teaching Workshop (Education)
- Tree Planting Initiative (Environment)
- Senior Center Support (Elderly Care)
- Beach Cleanup & Restoration (Environment)

## Maintenance Scripts

### Backup Database
```bash
pg_dump -U postgres actify_db > actify_backup.sql
```

### Restore Database
```bash
psql -U postgres actify_db < actify_backup.sql
```

### Reset Database
```bash
psql -U postgres -f reset_database.sql
```

## Configuration

Update your Spring Boot `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/actify_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

## Troubleshooting

**Connection refused?**
- Check PostgreSQL service is running
- Verify port 5432 is not blocked
- Ensure `pg_hba.conf` allows local connections

**Permission denied?**
- Make sure you're using the correct PostgreSQL user
- Grant appropriate privileges: `GRANT ALL PRIVILEGES ON DATABASE actify_db TO your_user;`

**Database already exists?**
- Drop and recreate: `DROP DATABASE IF EXISTS actify_db;`
- Or use the reset script: `psql -U postgres -f reset_database.sql`
