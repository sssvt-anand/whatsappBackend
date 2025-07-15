# Build stage
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /workspace/app

# Copy only the POM file first (better caching)
COPY whatsappbackend/pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source files
COPY whatsappbackend/src ./src

# Build the application
RUN mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built JAR
COPY --from=build /workspace/app/whatsappbackend/target/backend-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]