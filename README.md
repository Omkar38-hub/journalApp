# JournalApp Backend

A robust Spring Boot backend for the JournalApp project, providing RESTful APIs, authentication, user management, journaling features, and integration with external services.

## 🚀 Live Deployment

- **Backend API**: [https://journal-backend-5vli.onrender.com/app/](https://journal-backend-5vli.onrender.com/app/)
- **API Documentation**: [https://journal-backend-5vli.onrender.com/app/swagger-ui/index.html](https://journal-backend-5vli.onrender.com/app/swagger-ui/index.html)

## ✨ Features

- 🔐 **JWT Authentication** with User/Admin roles
- 🌐 **Google OAuth Integration** for seamless login
- 📝 **Journal Entry Management** with CRUD operations
- 👤 **User Profile & Password Management**
- 🧠 **Sentiment Analysis** for journal entries
- 📧 **Email Notifications** and reminders
- 🛡️ **Admin Dashboard** with user management
- ⚡ **Redis Caching** for improved performance
- 📊 **Scheduled Tasks** and background processing
- 📚 **Comprehensive API Documentation** with Swagger

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Build Tool**: Maven
- **Database**: MongoDB
- **Cache**: Redis
- **Authentication**: JWT + Google OAuth
- **Documentation**: Swagger/OpenAPI 3
- **Deployment**: Render (Cloud Platform)

## 📋 Requirements

- Java 17 or higher
- Maven 3.6+
- Redis server (for caching)
- MongoDB database

## 🚀 Local Development Setup

### 1. Clone the Repository
```bash
git clone <your-repo-url>
cd journalApp/journalApp
```

### 2. Configure Environment Variables
For production deployment, the following environment variables need to be configured:

#### Database Configuration
- `MONGODB_URI`: MongoDB connection string
- `REDIS_HOST`: Redis server host
- `REDIS_PASSWORD`: Redis server password

#### Authentication Configuration
- `GOOGLE_CLIENT_ID`: Google OAuth client ID
- `GOOGLE_CLIENT_SECRET`: Google OAuth client secret

#### Email Configuration
- `JAVA_EMAIL`: Gmail address for sending emails
- `JAVA_EMAIL_PASSWORD`: Gmail app password

#### Application Configuration
- `FRONTEND_HOST`: Frontend application URL (for CORS)
- `BACKEND_HOST`: Backend application URL (for Google OAuth redirect)
- `SERVER_PORT`: Server port (default: 8080)
- `WEATHER_API_KEY`: Weather API key for weather integration

#### Local Development Configuration
For local development, you can create an `application-dev.yaml` file:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/journal
      database: journal
      auto-index-creation: true
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: your_google_client_id
            client-secret: your_google_client_secret
  mail:
    host: smtp.gmail.com
    port: 587
    username: your_email@gmail.com
    password: your_app_password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
app:
  cors:
    allowed-origin: http://localhost:3000
google:
  redirect-uri: http://localhost:8080
server:
  port: 8080
  servlet:
    context-path: /app
weather:
  api:
    key: your_weather_api_key
```

### 3. Build the Project
```bash
./mvnw clean install
```

### 4. Run the Application
```bash
./mvnw spring-boot:run
```

The backend will start on [http://localhost:8080](http://localhost:8080) by default.

## 📚 API Documentation

- **Local Development**: [http://localhost:8080/app/docs](http://localhost:8080/app/docs)
- **Production**: [https://journal-backend-5vli.onrender.com/app/swagger-ui/index.html](https://journal-backend-5vli.onrender.com/app/swagger-ui/index.html)

## 📁 Project Structure

```
src/main/java/net/project/journalApp/
├── controller/          # REST API controllers
│   ├── AdminController.java
│   ├── GoogleAuthController.java
│   ├── JournalController.java
│   ├── PublicController.java
│   └── UserController.java
├── service/            # Business logic and integrations
│   ├── JournalEntryService.java
│   ├── UserService.java
│   ├── SentimentAnalysisService.java
│   ├── EmailServiceImpl.java
│   └── WeatherService.java
├── entity/             # JPA entities
│   ├── JournalEntry.java
│   ├── UserEntry.java
│   └── EmailDetails.java
├── repository/         # Data access layer
│   ├── JournalRepository.java
│   ├── UserRepository.java
│   └── ExternalApiRepository.java
├── config/            # Configuration classes
│   ├── SpringSecurity.java
│   ├── CorsConfig.java
│   ├── RedisConfig.java
│   └── SwaggerConfig.java
├── scheduler/         # Scheduled tasks
│   └── Scheduler.java
├── utils/            # Utility classes
│   ├── JwtUtil.java
│   └── tokenUtils.js
└── filter/           # JWT filter
    └── JwtFilter.java
```

## 🧪 Testing

Run all tests:
```bash
./mvnw test
```

Run specific test classes:
```bash
./mvnw test -Dtest=UserServiceTest
```

## 🐳 Docker Deployment

Build and run with Docker:
```bash
# Build the image
docker build -t journalapp-backend .

# Run the container
docker run -p 8080:8080 journalapp-backend
```

## 🔧 Configuration

### Environment Variables
- `MONGODB_URI`: MongoDB connection string
- `REDIS_HOST`: Redis server host
- `REDIS_PASSWORD`: Redis server password
- `GOOGLE_CLIENT_ID`: Google OAuth client ID
- `GOOGLE_CLIENT_SECRET`: Google OAuth client secret
- `JAVA_EMAIL`: Gmail address for sending emails
- `JAVA_EMAIL_PASSWORD`: Gmail app password
- `FRONTEND_HOST`: Frontend application URL (for CORS)
- `BACKEND_HOST`: Backend application URL (for Google OAuth redirect)
- `SERVER_PORT`: Server port (default: 8080)
- `WEATHER_API_KEY`: Weather API key for weather integration

### Production Deployment
The application is configured for deployment on Render with automatic scaling and SSL termination.

## 🛡️ Keep Backend Awake on Render

Render free services spin down after 15 minutes of inactivity, causing a delay on the first request. To keep the backend responsive, we use **cron-job.org** to ping the health check endpoint every 10 minutes.

### ✅ Setup Summary

**Service Used**: [cron-job.org](https://cron-job.org)

**Ping URL**: `https://journal-backend-5vli.onrender.com/app/public/healthcheck`

**Interval**: Every 10 minutes

**Purpose**: Prevent backend from sleeping due to inactivity

### 🔧 How to Set Up

1. **Visit cron-job.org**
   - Go to [https://cron-job.org](https://cron-job.org)
   - Create a free account

2. **Create a New Cronjob**
   - Click "Create cronjob"
   - Set the following parameters:
     - **Title**: `JournalApp Backend Keep Alive`
     - **URL**: `https://journal-backend-5vli.onrender.com/app/public/healthcheck`
     - **Schedule**: `Every 10 minutes`
     - **Timezone**: Your preferred timezone

3. **Save and Activate**
   - Click "Create" to save the cronjob
   - The service will start pinging your backend automatically

### 📊 Monitoring

- **Status**: Monitor the cronjob status in your cron-job.org dashboard
- **Logs**: Check ping success/failure logs
- **Uptime**: Ensure your backend stays responsive

### 🎯 Benefits

- ⚡ **Instant Response**: No cold start delays
- 💰 **Cost Effective**: Free service for keeping backend alive
- 🔄 **Automatic**: No manual intervention required
- 📈 **Reliable**: Consistent uptime for users

### 🚨 Alternative Solutions

If cron-job.org is unavailable, consider these alternatives:
- **UptimeRobot**: Free uptime monitoring with 5-minute intervals
- **Pingdom**: Website monitoring service
- **Custom Script**: Self-hosted solution using cron jobs

## 📝 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/signup` - User registration
- `GET /api/auth/google` - Google OAuth login
- `POST /api/auth/refresh` - Refresh JWT token

### Journal Management
- `GET /api/journal/entries` - Get user's journal entries
- `POST /api/journal/entries` - Create new journal entry
- `PUT /api/journal/entries/{id}` - Update journal entry
- `DELETE /api/journal/entries/{id}` - Delete journal entry

### User Management
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile
- `PUT /api/users/password` - Change password

### Admin Endpoints
- `GET /api/admin/users` - Get all users (Admin only)
- `PUT /api/admin/users/{id}/role` - Update user role (Admin only)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 🆘 Support

For support and questions:
- Check the [API Documentation](https://journal-backend-5vli.onrender.com/app/swagger-ui/index.html)
- Review the logs in the application
- Create an issue in the repository

---