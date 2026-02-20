# Use the official Maven image to build the application
FROM maven:3.8-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and the source code
COPY METAR_DECODER/pom.xml .
COPY METAR_DECODER .

# Build the application
RUN mvn clean package

# Use a smaller image for the final application
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Specify the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
