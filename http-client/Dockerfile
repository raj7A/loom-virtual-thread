FROM openjdk:22

EXPOSE 6000

COPY target /target

WORKDIR /target

ENTRYPOINT exec java -jar http-client-0.0.1-SNAPSHOT.jar
