package org.example;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;

public class Loop {

    public static int totalIterations = 100000;
    public static int sleepTimeInSecs = 1;


    public static void main(String[] args) {
        trigger(args);
    }

    public static void trigger(String[] args) {
        System.out.println("Loop process is up with CPU count : " + Runtime.getRuntime().availableProcessors());
        Instant start = Instant.now();

        if (Objects.equals(args[0], "TP")) {
            threadPoolFlow();
        } else if (Objects.equals(args[0], "VT")) {
            virtualThreadFlow();
        }

//        System.out.println("Used memory in MB : " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000);
        System.out.println("Completed " + totalIterations + " iterations in " + Duration.between(start, Instant.now()).getSeconds() + " seconds with each iteration configured for sleep time of " + sleepTimeInSecs + " seconds");
    }

    public static int[] threadPoolFlow() {
        //System.out.println("Flow::ThreadPool");
//        try (var executor = Executors.newFixedThreadPool(10000)) {
        try (var executor = Executors.newCachedThreadPool()) {
            return IntStream.range(0, totalIterations)
                    .map(i -> {
                        executor.submit(() -> {
                            try {
//                                System.out.println((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000);
//                                System.out.println(Thread.currentThread().getName() + "" + Thread.currentThread().threadId());
                                sleep(Duration.ofSeconds(sleepTimeInSecs));
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        return i;
                    }).toArray();
        }
    }

    public static int[] virtualThreadFlow() {
        //System.out.println("Flow::VirtualThread");
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            return IntStream.range(0, totalIterations)
                    .map(i -> {
                        executor.submit(() -> {
                            try {
//                                System.out.println((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000);
//                                System.out.println(Thread.currentThread().getName() + "" + Thread.currentThread().threadId());
                                sleep(Duration.ofSeconds(sleepTimeInSecs));
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        return i;
                    }).toArray();
        }
    }
}