FROM tomcat:9.0-jdk11-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

COPY target/unstop-clone.war /usr/local/tomcat/webapps/unstop-clone.war

EXPOSE 8080

CMD ["catalina.sh", "run"]