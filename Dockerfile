# Alpine Linux with OpenJDK JRE

FROM openjdk:8-jre-alpine
# copy fat WAR
COPY target/fileupload-streaming-1.0.0.jar /app.jar
ADD target/classes/application.properties /app/application.properties
ADD target/classes/logging.properties /app/logging.properties
# runs application
CMD ["/usr/bin/java", "-jar", "-Dspring.profiles.active=default --spring.config.location=classpath:file:/app/application-properties", "-Djava.util.logging.config.file=classpath:file:/app/logging.properties",  "/app.jar"]