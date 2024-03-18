FROM openjdk:17-jdk-alpine

LABEL authors="dell"

COPY build/libs/video-sharing-api-0.0.1-SNAPSHOT.war video-sharing-api-0.0.1-SNAPSHOT.war

ENTRYPOINT ["java", "-jar", "/video-sharing-api-0.0.1-SNAPSHOT.war"]