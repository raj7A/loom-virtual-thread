# Virtual Thread vs Platform Thread

A simple POC (in vanilla java & springboot) to explore the new virtual thread (support in Java 21 & above  - Project loom) , and compare its performance with traditional platform thread.

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
    1. spring-boot -> Setup for experimenting netty server(reactive threads) vs tomcat server(virtual threads)
    2. vanilla-java based -> Setup for experimenting virtual thread vs platform thread

### Pre-req :
    1. Java 22
    2. Container runtimes like Docker (Docket desktop/colima)

### Run :
This entire setup is containerised (with fixed cpu), and which will give us a consistent result on each run.
Hence, the benchmarking result will be easier to derive, and be more accurate.
1. SpringBoot based - [ReadMe.md](spring-boot%2FReadMe.md)
2. Vanilla java based - [ReadMe.md](vanilla-java%2FReadMe.md)

### References
1. https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html#GUID-2DDA5807-5BD5-4ABC-B62A-A1230F0566E0
2. https://blog.rockthejvm.com/ultimate-guide-to-java-virtual-threads/ - To understand the internals of Virtual Thread
