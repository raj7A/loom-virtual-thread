package org.example;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;

@RestController
public class Controller {

//    @GetMapping("/sleep/{time}")
//    public Mono<String> sleepTime(@PathVariable Integer time) {
//        return sleep(time)
//                .map(slept -> slept + " for " + time + " ms");
//    }

    @GetMapping("/process")
    public Mono<String> sleep() {
        return sleepAsynch()
                .map(slept -> slept + " for " + 50 + " ms");
    }

    @GetMapping("/biProcess")
    public Mono<String> biSleep() {
        return biProcessSleep();
    }

    private static Mono<String> biProcessSleep() {
        return Mono.zip(sleepAsynch(), sleepAsynch())
                .map(tuple -> {
//                    System.out.println(Thread.currentThread());
                    return tuple.getT1() + "::" + tuple.getT2();
                });
    }

    private static Mono<String> sleepAsynch() {
        return Mono.just(50)
                .flatMap(Controller::sleep)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private static Mono<String> sleep(Integer sleep) {
        try {
            Instant start = Instant.now();
            Thread.sleep(sleep);
//            System.out.println(Thread.currentThread() + "::Sleep time in ms " + Duration.between(start, Instant.now()).toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Mono.just("Slept");
    }
}