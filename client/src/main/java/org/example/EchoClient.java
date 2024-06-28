package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class EchoClient {
    static int totalIterations = 25000; // may not go beyond 28K due to port limitation
    static int sleepTimeInSecs = 1; // the same is configured on the serve side
    static String tpHost = "loomservertp";
    static String vtHost = "loomservervt";
    static int tpPort = 8098;
    static int vtPort = 8099;

    public static void main(String[] args) {
        //org.openjdk.jmh.Main.main(args);
        if (Objects.equals(args[0], "TP")) {
            threadPoolFlow();
        } else if (Objects.equals(args[0], "VT")) {
            virtualThreadFlow();
        }
    }

    public static void warmUp(String host, int port) {
        IntStream.range(0, 5).forEach(i -> {
            try {
                acquireConnectionAndSend(host, port, i);
            } catch (IOException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
        });
    }

    public static void threadPoolFlow() {
        warmUp(tpHost, tpPort);
        Instant start = Instant.now();
        try (var executor = Executors.newCachedThreadPool()) {
            LongSummaryStatistics summaryStatistics = send(tpHost, tpPort, executor, start);
            logResult(summaryStatistics, start);
        }
    }

    public static void virtualThreadFlow() {
        warmUp(vtHost, vtPort);
        Instant start = Instant.now();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            LongSummaryStatistics summaryStatistics = send(vtHost, vtPort, executor, start);
            logResult(summaryStatistics, start);
        }
    }

    private static LongSummaryStatistics send(String host, int port, ExecutorService executor, Instant start) {
        return IntStream.range(0, totalIterations).mapToObj(i -> executor.submit(() -> {
                    try {
                        return acquireConnectionAndSend(host, port, i);
                    } catch (IOException e) {
                        System.out.println(e);
                        throw new RuntimeException(e);
                    }
                }))
                .toList()
                .stream()
                .mapToLong(longFuture -> {
                    try {
                        return longFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .summaryStatistics();
    }

    private static long acquireConnectionAndSend(String host, Integer port, Integer input) throws IOException {
        Socket echoSocket = new Socket(host, port);
        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

        Instant start = Instant.now();

        out.println(input);
        in.readLine();
        echoSocket.close();

        return Duration.between(start, Instant.now()).getSeconds();
    }

    private static void logResult(LongSummaryStatistics summaryStatistics, Instant start) {
        System.out.println("Completed " + totalIterations + " iterations in " + Duration.between(start, Instant.now()).getSeconds() +
                " seconds with each iteration configured for sleep time of " + sleepTimeInSecs + " seconds" +
                " with average response time of " + Math.round(summaryStatistics.getAverage()) + " seconds");
    }
}
