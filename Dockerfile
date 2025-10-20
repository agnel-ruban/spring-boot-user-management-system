# syntax=docker/dockerfile:1

# --- Stage 1: Build the application ---
FROM eclipse-temurin:17-jdk AS builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper and project files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Ensure Unix line endings and executable permissions for mvnw
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Download dependencies (cacheable layer)
RUN ./mvnw -B -q dependency:go-offline

# Copy source code and build the application
COPY src src
RUN ./mvnw -B -DskipTests clean package

# --- Stage 2: Create the final production image ---
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
