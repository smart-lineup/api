FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/smartlineup-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]