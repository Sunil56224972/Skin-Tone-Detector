# STAGE 1: Build Environment
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy dependency configuration
COPY pom.xml .

# Pre-fetch dependencies (optimized caching)
RUN mvn dependency:go-offline -B

# Copy source code and Bayesian data
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# STAGE 2: Runtime Environment
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built artifact from the build stage
COPY --from=build /app/target/*.jar app.jar

# Configuration for Bayesian data fallback
ENV APP_SKIN_BAYESIAN_DATA_PATH=classpath:image_knowledge.dat

# Expose standardized port
EXPOSE 8080

# Run as non-root for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Entry Point
ENTRYPOINT ["java", "-jar", "app.jar"]
