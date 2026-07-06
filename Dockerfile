FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
COPY --from=build /target/Portfolio-0.0.1-SNAPSHOT.jar Portfolio.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","Portfolio.jar"]

