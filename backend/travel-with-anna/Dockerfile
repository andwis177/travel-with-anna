FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
USER app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

#FROM openjdk:17-jdk-alpine
#VOLUME /app
#EXPOSE 8080
#ARG JAR_FILE=target/travel-with-anna-0.0.1-SNAPSHOT.jar
#ADD ${JAR_FILE} app.jar
#ENTRYPOINT ["java", "-jar", "/app.jar"]