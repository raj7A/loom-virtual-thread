FROM openjdk:22

EXPOSE 6076

COPY target /target

WORKDIR /target

ENTRYPOINT exec java -jar tomcat-server-0.0.1-SNAPSHOT.jar
