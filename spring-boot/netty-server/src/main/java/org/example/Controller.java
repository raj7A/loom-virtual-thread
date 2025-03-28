package org.example;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;

@RestController
//@RequestMapping("/")
public class Controller {

    @GetMapping("/sleep/{time}")
    public Mono<String> getHello(@PathVariable Integer time) {
        return sleep(time)
                .map(slept -> slept + " for " + time + " ms");
    }

    @GetMapping("/process")
    public Mono<String> sleep() {
        return Mono.just(100)
                .flatMap(Controller::sleep)
                .subscribeOn(Schedulers.boundedElastic())
                .map(slept -> slept + " for " + 100 + " ms");
    }

    private static Mono<String> sleep(Integer sleep) {
        try {
            Instant start = Instant.now();
            Thread.sleep(sleep);
//            System.out.println(Thread.currentThread().getName() + "::Sleep time in ms " + Duration.between(start, Instant.now()).toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Mono.just("Slept");
    }
}