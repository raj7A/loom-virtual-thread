package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@RestController
public class Controller {

    @Autowired
    @Qualifier("applicationTaskExecutor")
    private AsyncTaskExecutor asyncTaskExecutor;

    @GetMapping("/processAsynch")
    public CompletableFuture<String> sleepAsync() {
        return asyncTaskExecutor.submitCompletable(() -> sleep(100))
                .thenApply(slept -> slept + " for 100 ms");
    }

    @GetMapping("/process")
    public String sleep() {
        return sleep(100);
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