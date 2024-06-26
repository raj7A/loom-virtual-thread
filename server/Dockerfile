FROM openjdk:22

EXPOSE 8099

COPY target /target

WORKDIR /target

ENTRYPOINT exec java -jar server-1.0-SNAPSHOT.jar 8099
