FROM maven:3.8.3-openjdk-11-slim
VOLUME /tmp
COPY target/artwork-1.0.jar artwork-1.0.jar
EXPOSE 8002
ENTRYPOINT ["java","-jar","/artwork-1.0.jar"]
