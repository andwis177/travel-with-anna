FROM maven:3.9.9-amazoncorretto-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve clean install -DskipTests
COPY src ./src
RUN mvn clean install -DskipTests

FROM openjdk:21
RUN groupadd -r app && useradd --no-log-init -r -g app app
WORKDIR /app
USER app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]


