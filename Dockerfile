FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=bs-banking-services-web/target/*.jar
COPY ${JAR_FILE} bs.jar
EXPOSE 5005:5005
EXPOSE 8080:8080
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-jar", "/bs.jar"]