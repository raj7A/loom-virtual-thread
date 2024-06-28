# Virtual Thread vs Platform Thread

A simple POC to explore the new virtual thread (introduced in Java 22) , and compare its performance with traditional platform thread.

### Platform Thread (https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html) :
    1. Heavy thread, hence generally pooled
    2. Costlier
    3. Thread is pooled to achieve optimal scalability
    4. May or may not preserve the thread-per-request model

### Virtual Thread (https://openjdk.org/jeps/444) :
    1. Lightweight thread, hence create as you go
    2. Cheap
    3. Thread should NOT be pooled, yet achieves optimal scalability better than platform thread
    4. Preserves the thread-per-request model

### About the modules :
    1. loop   - A simple Non-IO based looping logic to has implementation for both Platform Thread & Virtual Thread
    2. server - A socket based IO server that has implementation for both Platform Thread & Virtual Thread
    3. client - A socket based IO client that has implementation for both Platform Thread & Virtual Thread, which invokes the server respectovely

### Pre-req :
    1. Java 22
    2. Container runtimes like Docker desktop

### Build :
``` bash
./mvnw clean install
docker build -t loom-server server
docker build -t loom-client client
docker build -t loop loop
```
### Run :
_1. Run the IO based server & client to test Thread Pool implementation :_
``` bash
docker compose up looptp 
```
_2. Run the IO based server & client to test Virtual Thread implementation :_
``` bash
docker compose up loopvt
```
_3. Run the IO based server & client to test Platform Thread/Thread Pool implementation :_
``` bash
docker compose up loomservertp loomclienttp
```
_4. Run the IO based server & client to test Virtual Thread implementation :_
``` bash
docker compose up loomservervt loomclientvt 
```
_5. Run all at once :_
``` bash
docker compose up
```

### Sample Result :
![img.png](perf_numbers.png)
### Conclusion
    Results are defintely better compared to platform thread, But still play around it yourself with different number of iterations, thread pool executors, thread counts etc.



https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html#GUID-2DDA5807-5BD5-4ABC-B62A-A1230F0566E0