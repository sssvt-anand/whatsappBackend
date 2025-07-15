# Build stage with Maven and Java 17
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /workspace/app

# Copy the POM file first (for better layer caching)
COPY whatsappbackend/pom.xml .

# Download dependencies (this will cache them unless POM changes)
RUN mvn dependency:go-offline

# Copy the rest of the source files
COPY whatsappbackend/src ./src

# Build the application
RUN mvn package -DskipTests

# Runtime stage with just JRE
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /workspace/app/whatsappbackend/target/backend-0.0.1-SNAPSHOT.jar ./app.jar

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]