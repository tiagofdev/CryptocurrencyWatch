# Use an official Amazon Corretto JDK runtime as a parent image
FROM amazoncorretto:17

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from your build context to the container
COPY target/GLAbackend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port that the application will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
