FROM openjdk:22

EXPOSE 8098

COPY target /target

WORKDIR /target

ENTRYPOINT exec java -jar client-1.0-SNAPSHOT.jar $FLOW
