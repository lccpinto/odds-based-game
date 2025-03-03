FROM gradle:8.12.1-jdk21 as builder
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle clean build --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar game.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "game.jar"]
