# Virtual Thread vs Platform Thread

A simple POC (springboot framework based) to explore the new virtual thread (introduced in Java 22 - Project loom) , and compare its performance with traditional platform thread.

### Platform Thread (https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html) :
    1. Heavy thread, hence generally pooled
    2. Costlier
    3. Thread is pooled to achieve optimal scalability
    4. May or may not preserve the thread-per-request model, depends on asynchronous/synchronous model

### Virtual Thread (https://openjdk.org/jeps/444) :
    1. Lightweight thread, hence create as you need
    2. Cheap
    3. Thread should NOT be pooled, yet achieves optimal scalability better than platform thread
    4. Preserves the thread-per-request model
    5. Virtual threads are not faster threads — they do not run code any faster than platform threads. They exist to provide scale (higher throughput), not speed (lower latency)

### About the modules :
    1. netty-server  - Spring boot based netty server that uses webflux - sleep induced delay
    2. tomcat-server - Spring boot based tomcat server that uses java native/completableFuture - sleep induced delay
    3. http-client   - A simple client to test the servers - not for load testing

### Pre-req :
    1. Java 22
    2. Container runtimes like Docker (Docket desktop/colima)

### Build :
``` bash
./mvnw clean install
docker build -t tomcat-server tomcat-server
docker build -t netty-server netty-server
docker build -t http-client http-client
```

### Run :
_1. Run the servers :_
``` bash
docker compose up nettyserver tomcatserver
```
_2. Run the client :_
``` bash
docker compose up httpclient
```
_3. Use the jmeter to trigger the load :_
```bash
brew install jmeter
```
To run via UI - below command opens the jmeter UI
```bash
jmeter
```
To run via CLI
```bash
jmeter -n -t netty_and_tomcat.jmx -l output.jtl
```

### Sample Result :
Netty Server :
![netty.png](..%2Fimages%2Fnetty.png)

Tomcat Server :
![tomcat.png](..%2Fimages%2Ftomcat.png)

### Conclusion
    tomcat-server(virtual thread) provides 4x throughput compared to netty-server(reactor webflux), without comprimising the latency

### References
1. https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html#GUID-2DDA5807-5BD5-4ABC-B62A-A1230F0566E0
2. https://blog.rockthejvm.com/ultimate-guide-to-java-virtual-threads/ - To understand the internals of Virtual Thread