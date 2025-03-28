# Netty vs Tomcat

A simple POC (springboot framework based) to explore the new virtual thread (introduced in Java 22 - Project loom) , and compare its performance with project reactor (asynch framework).

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
To run via UI - below command opens the jmeter UI (import netty_and_tomcat.jmx and tweak as per your requirement)
```bash
jmeter
```
To run via CLI 
```bash
jmeter -n -t netty_and_tomcat.jmx -l output.jtl
```

### Observations :
1. /biProcess endpoint(that does 2 concurrent backend IO sleep induced call with 50ms each) takes 55ms/request to 60ms/request .
2. Netty handles upto 70tps without breaching p95 response time of 60ms
3. Tomcat handles upto 400tps without breaching p95 response time of 60ms
4. tomcat-server(virtual thread) provides huge throughput compared to netty-server(reactor webflux), without compromising the latency.

Netty metrics :

![netty.png](..%2Fimages%2Fnetty.png)

Tomcat metrics :

![tomcat.png](..%2Fimages%2Ftomcat.png)

### Disclaimer
1. This repo provides the basic setup handy for you to do the POC for yourself, and intentionally skipping the detailed benchmarking numbers.

### References
1. https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html#GUID-2DDA5807-5BD5-4ABC-B62A-A1230F0566E0
2. https://blog.rockthejvm.com/ultimate-guide-to-java-virtual-threads/ - To understand the internals of Virtual Thread