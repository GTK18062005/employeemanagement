# Employee Management System (EMS)

A full-stack employee management application with role-based access control, built with Java/Spring Boot backend and React frontend.

## Features

### Admin
- **Users** — Create, view, enable/disable system user accounts
- **Employees** — Full CRUD for employee records (personal info, department, designation)
- **Projects** — Create, edit, assign managers, update status
- **Attendance** — View all employee attendance records, filter by date
- **Leave** — Review, approve, and reject leave requests
- **Salary** — Create and manage salary records for employees
- **Parking** — Manage parking slots and allocate/release parking for employees

### Project Manager
- **Projects** — View assigned projects and project details
- **Team** — Team overview (per-project team assignments)
- **Attendance** — Monitor team attendance records
- **Leave** — Approve/reject team leave requests
- **Parking** — View team parking allocations

### Employee
- **Profile** — View account info, change password
- **Projects** — View assigned projects and details
- **Attendance** — Mark daily attendance, lookup by date, view history
- **Leave** — Apply for leave, view status, cancel pending requests
- **Salary** — View personal salary history
- **Parking** — View personal parking allocation

## Technology Stack

### Backend
| Technology | Version |
|-----------|---------|
| Java | 21 |
| Spring Boot | 4.1.0 |
| Spring Security | JWT-based authentication |
| Spring Data JPA | Hibernate ORM |
| PostgreSQL | Database |
| Maven | Build tool |

### Frontend
| Technology | Version |
|-----------|---------|
| React | 19 |
| Vite | 8 |
| Axios | HTTP client |
| React Router | Client-side routing |
| Lucide React | SVG icons |

## Project Structure

```
employee-management-system/
├── backend/                    # Spring Boot application
│   ├── src/main/java/com/ems/
│   │   ├── config/             # Security, CORS, JWT config
│   │   ├── controller/         # REST API controllers
│   │   ├── dto/                # Request/response DTOs
│   │   ├── entity/             # JPA entities
│   │   ├── repository/         # Data access layer
│   │   └── service/            # Business logic
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── pom.xml
│   └── mvnw.cmd
│
├── frontend/                   # React + Vite application
│   ├── src/
│   │   ├── components/         # Reusable UI components
│   │   ├── constants/          # Routes, roles, navigation
│   │   ├── context/            # Auth & toast context
│   │   ├── hooks/              # Custom React hooks
│   │   ├── pages/              # Page components (admin/manager/employee)
│   │   ├── routes/             # Router & route guards
│   │   ├── services/           # API service layer
│   │   └── utils/              # Formatters, error handling
│   ├── package.json
│   └── vite.config.js
│
├── .gitignore
└── README.md
```

## Prerequisites

- **Java 21** (JDK)
- **Node.js 18+** (with npm)
- **PostgreSQL** (running instance)

## Local Setup

### 1. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE employee_management;
```

### 2. Backend Setup

```bash
cd backend

# Configure database credentials (or use defaults)
# Edit src/main/resources/application.properties
# Or set environment variables: DB_URL, DB_USERNAME, DB_PASSWORD

# Run the application
.\mvnw.cmd spring-boot:run
```

The backend starts on **port 8081** by default.

On first startup, an admin user is automatically bootstrapped (if `admin.bootstrap.enabled=true`).

### 3. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Create environment file
cp .env.example .env
# Edit .env if backend runs on a different port

# Start development server
npm run dev
```

The frontend starts on **port 5173** by default.

## Environment Variables

### Backend (`application.properties` / system env)

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/employee_management` | PostgreSQL JDBC URL |
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | `1235` | Database password |
| `JWT_SECRET` | (base64 dev key) | JWT signing secret — **must be changed for production** |
| `JWT_EXPIRATION` | `3600000` (1 hour) | JWT token expiration in milliseconds |
| `ADMIN_BOOTSTRAP_ENABLED` | `true` | Create default admin on startup |
| `ADMIN_USERNAME` | `admin` | Bootstrap admin username |
| `ADMIN_PASSWORD` | (empty) | Bootstrap admin password |

### Frontend (`.env`)

| Variable | Default | Description |
|----------|---------|-------------|
| `VITE_API_BASE_URL` | `http://localhost:8081/api` | Backend API base URL |

> **Security Note**: All `VITE_*` variables are publicly exposed in the built frontend bundle. Never put secrets in frontend environment variables.

## Build

### Frontend Production Build

```bash
cd frontend
npm run build
```

Output is generated in `frontend/dist/`.

### Backend Compile

```bash
cd backend
.\mvnw.cmd clean compile
```

### Backend Package (JAR)

```bash
cd backend
.\mvnw.cmd clean package -DskipTests
```

Output JAR is generated in `backend/target/`.

## API Base URL

- **Development**: `http://localhost:8081/api` (configured in `.env`)
- **Production**: Set `VITE_API_BASE_URL` to your production backend URL before building

## Security Notes

- **Never commit `.env` files** with real credentials (`.gitignore` already excludes them)
- **Change `JWT_SECRET`** in production — the default is for development only
- **Change database credentials** in production
- JWT tokens are stored in `sessionStorage` (cleared on tab close)
- 401 responses automatically clear the session and redirect to login
- Role-based route guards enforce access on both frontend and backend

## Development Ports

| Service | Port |
|---------|------|
| Backend API | 8081 |
| Frontend Dev Server | 5173 |

## CORS Configuration

The backend CORS configuration allows the frontend origin (`http://localhost:5173` in development). For production, update the allowed origin to match your frontend deployment URL.

Supported methods: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS`
