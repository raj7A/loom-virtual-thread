package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@RestController
public class Controller {

    @Autowired
    @Qualifier("applicationTaskExecutor")
    private AsyncTaskExecutor asyncTaskExecutor;


    @GetMapping("/biProcess")
    public CompletableFuture<String> biSleep() {
        return biProcessSleep();
//        return triProcessSleep();
    }

    @GetMapping("/process")
    public String sleep() {
        return sleep(50);
    }

    private CompletableFuture<String> biProcessSleep() {
        return asyncTaskExecutor.submitCompletable(() -> sleep(50))
                .thenCombine(
                        asyncTaskExecutor.submitCompletable(() -> sleep(50)),
                        (sleep1, sleep2) -> sleep1 + "::" + sleep2);
    }

    private CompletableFuture<String> triProcessSleep() {
        return asyncTaskExecutor.submitCompletable(() -> sleep(50))
                .thenCombine(
                        asyncTaskExecutor.submitCompletable(() -> sleep(50)),
                        (sleep1, sleep2) -> sleep1 + "::" + sleep2)
                .thenCompose(result -> asyncTaskExecutor.submitCompletable(() -> sleep(50)))
                .thenApply(slept -> slept);
    }

    private static String sleep(Integer sleep) {
        try {
            Instant start = Instant.now();
            Thread.sleep(sleep);
//            System.out.println(Thread.currentThread() + "::Sleep time in ms " + Duration.between(start, Instant.now()).toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "Slept";
    }
}