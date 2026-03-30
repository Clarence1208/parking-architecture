FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src/ src/

EXPOSE 8080

ENTRYPOINT ["./gradlew", "bootRun", "--no-daemon"]
