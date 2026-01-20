FROM eclipse-temurin:25-jre-jammy

WORKDIR /app
COPY target/AmazonScraper-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]