package org.example;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;

public class Main {

    static int maxCount = 100000;
    public static void main(String[] args) {
        Instant start = Instant.now();

//        executorSend();
        virtualSend();

        System.out.println("Duration is : " + Duration.between(start, Instant.now()).getSeconds());
    }

    public static void executorSend() {
        try (var executor = Executors.newFixedThreadPool(10000)) {
            IntStream.range(0, maxCount).forEach(i -> executor.submit(() -> {
                try {
                    sleep(Duration.ofSeconds(1));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
    }

    public static void virtualSend() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, maxCount).forEach(i -> executor.submit(() -> {
                try {
                    sleep(Duration.ofSeconds(1));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
    }
}