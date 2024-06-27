package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class EchoClient {
    static int totalIterations = 25000;
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
        try (var executor = Executors.newFixedThreadPool(5000)) {
            IntStream.range(0, totalIterations).forEach(i -> executor.submit(() -> {
                try {
                    acquireConnectionAndSend(tpHost, tpPort, i);
                } catch (IOException e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
            }));
        }
        System.out.println("Completed " + totalIterations + " iterations in " + Duration.between(start, Instant.now()).getSeconds() + " seconds");
    }

    public static void virtualThreadFlow() {
        warmUp(vtHost, vtPort);
        Instant start = Instant.now();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, totalIterations).forEach(i -> executor.submit(() -> {
                try {
                    acquireConnectionAndSend(vtHost, vtPort, i);
                } catch (IOException e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
            }));
        }
        System.out.println("Completed " + totalIterations + " iterations in " + Duration.between(start, Instant.now()).getSeconds() + " seconds");
    }

    private static void acquireConnectionAndSend(String host, Integer port, Integer input) throws IOException {
        Socket echoSocket = new Socket(host, port);
        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        //System.out.println("Sending : " + input);
        out.println(input);
        //System.out.println("Received : " + in.readLine());
        in.readLine();
        echoSocket.close();
    }
}
