FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/stats-service-*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]