# ============================================================
# Stage 1: Build the Spring Boot application
# ============================================================
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy the backend project
COPY . .

# Make Maven Wrapper executable
RUN chmod +x mvnw

# Build Spring Boot application
RUN ./mvnw clean package -DskipTests

# Select the Spring Boot executable JAR
RUN JAR_FILE=$(find target -maxdepth 1 -type f -name "*.jar" ! -name "*-plain.jar" | head -n 1) \
    && cp "$JAR_FILE" target/app.jar


# ============================================================
# Stage 2: Run the application
# ============================================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the executable JAR from build stage
COPY --from=builder /app/target/app.jar app.jar

# Spring Boot runs on port 8081
EXPOSE 8081

# Start Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
