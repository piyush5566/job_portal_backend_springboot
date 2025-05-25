# Job Portal Backend (Spring Boot)

This is a secure, modular backend for a Job Portal application built with Spring Boot, Spring Security (JWT), JPA, and H2 database. It supports user registration, authentication, job management, application management, and Azure Blob Storage for profile pictures.

## Features
- **User Registration & Login** (JWT-based authentication)
- **Profile Management** (view, update, upload profile picture)
- **Job Management** (CRUD for jobs)
- **Application Management** (apply to jobs, view applications)
- **H2 Database** (in file mode, for development)
- **Azure Blob Storage** integration for file uploads
- **Centralized CORS configuration** for frontend integration
- **Secure endpoints** with role-based access

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Security (JWT)
- Spring Data JPA
- H2 Database
- Azure Blob Storage SDK
- Maven

## Getting Started

### Prerequisites
- Java 17+
- Maven 4+

### Configuration
Edit `src/main/resources/application.properties` for DB, Azure, and upload settings. Example:
```
spring.datasource.url=jdbc:h2:file:./data/jobportaldb
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
azure.storage.connection-string=... # your Azure connection string
```

### Build & Run
```bash
mvn clean package
mvn spring-boot:run
```

### API Endpoints
#### Auth
- `POST /auth/login` — Login, returns JWT

#### Users
- `POST /users` — Register
- `GET /users/profile` — Get current user profile (JWT required)
- `PUT /users/profile` — Update profile (JWT required)
- `POST /users/profile/picture` — Upload profile picture (JWT required)

#### Jobs
- `GET /jobs` — List jobs
- `POST /jobs` — Create job

#### Applications
- `GET /applications` — List applications
- `POST /applications` — Apply to job

#### H2 Console
- `GET /h2-console` — In-browser DB console (dev only)

## Security
- JWT authentication for all protected endpoints
- Passwords are hashed with BCrypt
- CORS is enabled for local frontend ports
- H2 console is only open in dev

## Development Notes
- Update `SECRET_KEY` in code for production
- Adjust CORS origins in `WebConfig` as needed
- For production, use a persistent DB (not H2)

## License
MIT
