FROM maven:3.9-eclipse-temurin-11 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM tomcat:9.0-jdk11-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /app/target/unstop-clone.war /usr/local/tomcat/webapps/unstop-clone.war

EXPOSE 8080

CMD ["catalina.sh", "run"]