# ----------- BUILD STAGE -------------
FROM eclipse-temurin:17-jdk AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Fix permission issue with mvnw
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the full source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# ----------- RUNTIME STAGE -------------
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy only the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (Spring Boot default or configured)
EXPOSE 8081

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]