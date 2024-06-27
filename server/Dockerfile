FROM openjdk:22

EXPOSE $PORT

COPY target /target

WORKDIR /target

ENTRYPOINT exec java -jar server-1.0-SNAPSHOT.jar $PORT $FLOW
