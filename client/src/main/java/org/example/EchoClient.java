package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class EchoClient {
    static int maxCount = 10000;

    public static void main(String[] args) throws IOException {
        //org.openjdk.jmh.Main.main(args);
        Instant start = Instant.now();

        virtualSend();
        //executorSend();

        System.out.println("Duration is : " + Duration.between(start, Instant.now()).getSeconds());
    }

    public static void warmUp() throws IOException {
        IntStream.range(0, 5).forEach(i -> {
            try {
                acquireConnectionAndSend(i);
            } catch (IOException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
        });
    }

    public static void executorSend() {
        try (var executor = Executors.newFixedThreadPool(maxCount)) {
            IntStream.range(0, maxCount).forEach(i -> executor.submit(() -> {
                try {
                    acquireConnectionAndSend(i);
                } catch (IOException e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
            }));
        }
    }

    public static void virtualSend() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, maxCount).forEach(i -> executor.submit(() -> {
                try {
                    acquireConnectionAndSend(i);
                } catch (IOException e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
            }));
        }
    }

    private static void acquireConnectionAndSend(Integer input) throws IOException {
        Socket echoSocket = new Socket("loomserver", 8099);
        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        //System.out.println("Sending : " + input);
        out.println(input);
        //System.out.println("Received : " + in.readLine());
        in.readLine();
        echoSocket.close();
    }
}
