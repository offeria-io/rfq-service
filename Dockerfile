# Use Java 21 JRE for the runtime image
FROM eclipse-temurin:21-jre-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file into the container
# Assumes 'mvn clean package' has been run and generated the jar in the target folder
COPY target/*.jar app.jar

# Expose the application's port
EXPOSE 8080

# Environment variables with default values
ENV DB_URL=jdbc:postgresql://localhost:5432/rfq_db
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=postgres
ENV KAFKA_SERVERS=localhost:9092

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
