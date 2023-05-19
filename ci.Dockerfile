# Stage 1: Build with Maven
FROM maven:3.8.3-openjdk-11-slim AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
COPY sonar-project.properties /usr/src/app/
RUN mvn -f /usr/src/app/pom.xml clean package

# Stage 2: Run with OpenJDK
FROM openjdk:11-jre-slim
COPY --from=build /usr/src/app/target/*.jar /app/
ENTRYPOINT ["java","-jar","/app/artwork-1.0.jar"]
