FROM gradle:7.6-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]