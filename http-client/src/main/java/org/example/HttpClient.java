package org.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LongSummaryStatistics;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@SpringBootApplication
public class HttpClient implements CommandLineRunner {

    static WebClient nettyWebClient = WebClient.builder().build();
    static WebClient tomcatWebClient = WebClient.builder().build();

    public static void main(String[] args) {
        SpringApplication.run(HttpClient.class, args);
    }

    public void run(String... args) throws Exception {
        System.out.println("starting");
//        Flux.fromIterable(IntStream.range(0, 100).mapToObj(i -> i).toList())
//                .parallel().flatMap(i -> invoke(nettyWebClient, "http://nettyserver:6066/process")).subscribe();
        invoke(nettyWebClient, "http://nettyserver:6066/process").subscribe();
        invoke(tomcatWebClient, "http://tomcatserver:6076/process").subscribe();
//        LongSummaryStatistics send = send(Executors.newVirtualThreadPerTaskExecutor());
//        System.out.println(send.getAverage());
    }

    private static Mono<Long> invoke(WebClient webClient, String url) {
        Instant start = Instant.now();
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(System.out::println)
                .map(val -> Duration.between(start, Instant.now()).get(ChronoUnit.SECONDS))
                .doOnNext(System.out::println);
    }

    private static LongSummaryStatistics send(ExecutorService executor) {
        return IntStream.range(0, 100).mapToObj(i -> executor.submit(() -> {
                    try {
                        return invoke(nettyWebClient, "http://nettyserver:6066/process");
                    } catch (Exception e) {
                        System.out.println(e);
                        throw new RuntimeException(e);
                    }
                }))
                .toList()
                .stream()
                .mapToLong(longFuture -> {
                    try {
                        return longFuture.get().block();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .summaryStatistics();
    }

}
