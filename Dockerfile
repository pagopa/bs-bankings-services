ARG APP_FOLDER=/home/build
ARG MVN_REPO_FOLDER=/home/.m2

FROM maven:3.8.7-amazoncorretto-8@sha256:ec86ad98267c73eae1b7fb1bd0142723e4c9f05cc52f3466a548ce6f6f471e26 AS build
ARG APP_FOLDER
ARG MVN_REPO_FOLDER
COPY . ${APP_FOLDER}
RUN mkdir ${MVN_REPO_FOLDER}
WORKDIR ${MVN_REPO_FOLDER}
RUN --mount=type=cache,target=/root/.m2 mvn -f ${APP_FOLDER}/pom.xml -s ${APP_FOLDER}/settings.xml clean package

FROM openjdk:8-jdk-alpine
ARG APP_FOLDER
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=bs-banking-services-web/target/*.jar
COPY --from=build ${APP_FOLDER}/${JAR_FILE} bs.jar
EXPOSE 5005:5005
EXPOSE 8080:8080
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-jar", "/bs.jar"] # includes debug agent attach
