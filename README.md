# Sanyark Leave Management System – Backend

This repository contains the **Spring Boot backend API** for the Sanyark Leave Management System.

The system allows employees to apply for leave, managers to review and approve requests, and provides dashboards, analytics, notifications, and calendar views.

---

# Technology Stack

Backend Framework:
- Java 17
- Spring Boot 3.3.5

Security:
- Spring Security
- JWT Authentication

Database:
- MySQL
- Flyway (Database migrations)

Persistence:
- Spring Data JPA
- Hibernate

Build Tool:
- Maven

Other Libraries:
- Lombok
- Bean Validation (Jakarta Validation)

---

# Project Architecture

The backend follows a **layered architecture** to maintain clean separation of concerns.

controller → service → repository → database


### Main Layers

**Controller**
Handles REST API endpoints.

**Service**
Contains business logic.

**Repository**
Handles database interaction using Spring Data JPA.

**Entity**
JPA entities mapped to database tables.

**DTO**
Data transfer objects used for request/response separation.

**Security**
JWT authentication and request filtering.

**Exception**
Global exception handling and custom exceptions.

---

# Main Features

### Authentication
- JWT based authentication
- Employee and manager login

### Leave Management
Employees can:
- Apply for leave
- View leave history
- Cancel pending leave

Managers can:
- Approve leave
- Reject leave with comments
- View team leave requests

### Leave Balance System
Tracks:
- Total leaves
- Used leaves
- Pending leaves
- Remaining leaves

### Holiday Management
Supports:
- Public holidays
- Company optional holidays
- Company events

### Calendar System
Provides:
- Company calendar
- Personal leave calendar
- Team leave calendar

### Dashboards

Employee dashboard shows:
- Leave balance
- Upcoming holidays
- Upcoming approved leaves

Manager dashboard shows:
- Total employees
- Pending approvals
- Employees currently on leave
- Leave statistics

### Notifications
In-app notification system for:
- Leave applied
- Leave approved
- Leave rejected
- Leave cancelled

### Analytics
Manager analytics include:
- Monthly leave trends
- Top leave takers
- Employees currently on leave

---

# Database Migrations

The project uses **Flyway** for database migrations.

Migration files are located at:
src/main/resources/db/migration

Example migrations:
V1__initial_schema.sql
V2__add_indexes.sql
V3__seed_indian_holidays.sql


Hibernate schema generation is disabled:
spring.jpa.hibernate.ddl-auto=validate

This ensures Flyway remains the **single source of truth for schema changes**.

---

# Setup Instructions

## 1. Clone the Repository
git clone https://github.com/<your-username>/sanyark-leave-management-backend.git
cd sanyark-leave-management-backend

---

## 2. Install Dependencies

Ensure the following are installed:

- Java 17+
- Maven
- MySQL

---

## 3. Create Database

Create a database in MySQL:
CREATE DATABASE leave_management;

---

## 4. Configure Environment

The project uses Spring profiles.

### Development Profile

File:
application-dev.properties

Example local configuration:
spring.datasource.url=jdbc:mysql://localhost:3306/leave_management
spring.datasource.username=root
spring.datasource.password=your_password

---

## 5. Run the Application

Using Maven wrapper:
./mvnw spring-boot:run

The server will start at:
http://localhost:8080

---

# Production Configuration

Production uses environment variables.

Required variables:
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:mysql://<host>:3306/leave_management
DB_USERNAME=<db_user>
DB_PASSWORD=<db_password>
APP_JWT_SECRET=<jwt_secret>

Optional:
APP_JWT_EXPIRATION=86400000
SERVER_PORT=8080

---

# Design Decisions and Assumptions

### Flyway over Hibernate schema generation
Database schema is controlled via Flyway migrations instead of Hibernate auto updates to ensure consistency across environments.

### JWT Authentication
JWT tokens are used to maintain stateless authentication between frontend and backend services.

### Layered Architecture
The application follows a layered architecture to improve maintainability and separation of concerns.

### DTO Usage
DTOs are used instead of exposing entities directly through APIs to maintain API stability and security.

### Role-based Access
Two main roles are supported:

- EMPLOYEE
- MANAGER

Certain endpoints are restricted based on role using Spring Security.

---

# Future Improvements

Possible enhancements:

- Email notifications
- Integration with FullCalendar for advanced calendar UI
- Audit logging
- Admin panel
- Docker containerization

---

# Author

Harsh Khondalay