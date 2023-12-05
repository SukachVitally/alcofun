FROM amazoncorretto:17.0.5

WORKDIR /app

COPY target/searchapp-0.0.1-SNAPSHOT.jar /app/searchapp.jar

EXPOSE 7080

# Your application-specific commands
CMD ["java", "-jar", "/app/searchapp.jar"]