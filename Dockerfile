# Start from a base image with Java 17
FROM eclipse-temurin:17-jdk AS build

# Set working directory
WORKDIR /app

# Copy the Maven wrapper and pom.xml
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Download Maven dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the application
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Use a smaller runtime image for final container
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port (default Spring Boot port)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
