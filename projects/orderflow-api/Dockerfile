FROM gradle:8.10.2-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN gradle test bootJar --no-daemon
FROM eclipse-temurin:21-jre
COPY --from=build /workspace/build/libs/orderflow-api-1.0.0.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
