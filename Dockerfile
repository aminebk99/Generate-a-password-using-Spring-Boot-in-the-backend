# Stage 1: Build the application
FROM maven:latest as build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download the dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src /app/src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built jar file from the previous stage
COPY --from=build /app/target/password-generation-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8082

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
