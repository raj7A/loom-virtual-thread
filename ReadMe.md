####
```
./mvnw clean install
docker build -t loom-server server
docker build -t loom-client client
docker compose up
```

https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html#GUID-2DDA5807-5BD5-4ABC-B62A-A1230F0566E0