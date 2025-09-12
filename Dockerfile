FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/Portfolio-0.0.1-SNAPSHOT.jar Portfolio.jar
ENTRYPOINT ["java","-jar","/Portfolio.jar"]
