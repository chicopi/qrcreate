FROM openjdk:11

# Setting up work directory
WORKDIR /app

# Copy the jar file into our app
COPY ./target/qrcreate.jar /app

# Exposing port 8080
EXPOSE 8080

# Starting the application
CMD ["java", "-jar", "qrcreate.jar"]