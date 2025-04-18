FROM openjdk:17-jdk-slim

WORKDIR /app
COPY . .

# 로그 찍기 - jar 파일 위치 확인
RUN ls -al build/libs || echo "📁 build/libs not found"
RUN find . -name "*.jar" || echo "❗ No .jar files found"

COPY build/libs/smartlineup-*.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]