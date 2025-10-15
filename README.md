# Spring Boot User Management System

A comprehensive user management system built with Spring Boot, featuring JWT authentication, role-based authorization, and PostgreSQL database.

## 🚀 Features

- **JWT Authentication** - Secure token-based authentication
- **Role-based Authorization** - ADMIN and USER roles with different permissions
- **CRUD Operations** - Complete user management functionality
- **Input Validation** - Comprehensive validation using Bean Validation
- **Global Exception Handling** - Consistent error responses
- **Soft Delete** - Users are marked as inactive instead of hard deletion
- **Bulk Operations** - Fork/Join framework for parallel user creation
- **Database Migrations** - Liquibase for database schema management
- **Spring Profiles** - Support for dev, test, and prod environments

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3.x**
- **Spring Security**
- **Spring Data JPA**
- **PostgreSQL**
- **Liquibase**
- **JWT (JJWT)**
- **Lombok**
- **MapStruct**
- **Maven**

## 🏗️ Architecture

- **Layered Architecture** - Controller, Service, Repository layers
- **DTO Pattern** - Separate DTOs for request/response
- **Exception Handling** - Global exception handler with custom exceptions
- **Validation** - Input validation with proper error messages
- **Security** - JWT-based authentication with method-level security

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL 12+

### Installation
1. Clone the repository
2. Configure PostgreSQL database
3. Copy template files and update with your credentials:
   ```bash
   cp src/main/resources/application-template.properties src/main/resources/application.properties
   cp src/main/resources/application-dev-template.properties src/main/resources/application-dev.properties
   ```
4. Update the following in your properties files:
   - Database URL, username, and password
   - JWT secret (base64 encoded)
   - Admin username and password
5. Run `mvn spring-boot:run`

## 📚 API Documentation

### Authentication
- `POST /api/v1/auth/login` - User login

### User Management
- `GET /api/v1/users` - Get all users (ADMIN only)
- `GET /api/v1/users/{id}` - Get user by ID
- `POST /api/v1/users` - Create user (ADMIN only)
- `PUT /api/v1/users/{id}` - Update user (ADMIN only)
- `PATCH /api/v1/users/{id}` - Partial update
- `DELETE /api/v1/users/{id}` - Delete user (ADMIN only)

## 🔐 Security

- JWT tokens with configurable expiration
- Role-based access control
- Method-level security with `@PreAuthorize`
- Password encryption using BCrypt


