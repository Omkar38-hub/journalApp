# JournalApp Backend

A robust Spring Boot backend for the JournalApp project, providing RESTful APIs, authentication, user management, journaling features, and integration with external services.

## Features
- JWT authentication (User/Admin roles)
- Google OAuth integration
- Journal entry management
- User profile and password management
- Sentiment analysis
- Email notifications
- Admin dashboard and user management
- Redis caching and scheduled tasks
- API documentation with Swagger

## Requirements
- Java 17+
- Maven
- Redis (for caching)

## Setup

1. **Clone the repository:**
   ```bash
   git clone <your-repo-url>
   cd journalApp/journalApp
   ```

2. **Configure environment variables:**
   - Edit `src/main/resources/application.yaml` (or `application-prod.yaml` for production) to set your database, Redis, and other secrets.
   - Example variables:
     - `spring.datasource.url`
     - `spring.datasource.username`
     - `spring.datasource.password`
     - `jwt.secret`
     - `google.clientId`, `google.clientSecret`

3. **Build the project:**
   ```bash
   ./mvnw clean install
   ```

4. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   The backend will start on [http://localhost:8080](http://localhost:8080) by default.

## API Documentation
- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Project Structure
- `controller/` - REST API controllers
- `service/` - Business logic and integrations
- `entity/` - JPA entities
- `repository/` - Data access layer
- `config/` - Security, CORS, Swagger, Redis configs
- `scheduler/` - Scheduled/background tasks
- `utils/` - Utility classes (JWT, etc.)

## Testing
- Run all tests:
  ```bash
  ./mvnw test
  ```

---