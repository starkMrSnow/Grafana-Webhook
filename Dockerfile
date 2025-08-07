FROM eclipse-temurin:24-jdk

WORKDIR /app

COPY target/Grafana.webhook-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]