# Project Management API

Backend REST API for a simple project and task management system built
with Spring Boot.

The application supports user authentication, project creation, task
management, comments, and activity logs.

## Tech Stack

-   Java 21
-   Spring Boot
-   Spring Security
-   Spring Data JPA
-   PostgreSQL
-   Flyway
-   JWT (jjwt)
-   Swagger / OpenAPI
-   JUnit 5
-   Testcontainers
-   Docker Compose
-   Maven

## Features

-   User registration and login with JWT authentication
-   Secure API endpoints with Spring Security
-   Create and manage projects
-   Create and manage tasks
-   Add comments to tasks
-   Activity logging
-   Database migrations with Flyway
-   Integration tests with Testcontainers

## Project Structure

src/main/java/com.richards.projectmanagement

-   auth -- authentication and JWT
-   project -- project management
-   task -- task management
-   comment -- task comments
-   activity -- activity logging
-   user -- user domain
-   config -- security configuration
-   common -- shared exceptions and utilities

## Running the Project

### Prerequisites

-   Java 21
-   Maven
-   Docker

### Start the database

The project uses PostgreSQL via Docker Compose.

``` bash
docker compose up -d
```

Database configuration:

database: project_management\
user: app\
password: app\
port: 5432

### Run the application

``` bash
./mvnw spring-boot:run
```

Or run the main class from your IDE.

## API Documentation

Swagger UI:

http://localhost:8080/swagger-ui/index.html

## Running Tests

``` bash
./mvnw test
```

## Example Workflow

1.  Register a user
2.  Login and get a JWT token
3.  Create a project
4.  Add tasks to the project
5.  Add comments to tasks
6.  View activity logs

