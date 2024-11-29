# Use an OpenJDK image as a base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built application jar file into the container
COPY target/password-generation-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8082

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
