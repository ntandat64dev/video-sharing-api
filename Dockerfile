FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
COPY gradle/ ./gradle/
COPY src/ ./src/

RUN ./gradlew build

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/video-sharing-api-0.0.1-SNAPSHOT.war .

ENTRYPOINT ["java","-jar","video-sharing-api-0.0.1-SNAPSHOT.war"]

EXPOSE 8080