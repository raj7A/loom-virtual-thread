package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @GetMapping("/scProcess")
    public String processSc() {
        return structuredConcurrency();
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

    // This structured concurrency functionality is still in preview mode
    private String structuredConcurrency() {
        Instant start = Instant.now();
        Callable<String> task1 = () -> sleep(100);
        Callable<String> task2 = () -> sleep(100);
        StringBuilder output = new StringBuilder();

        try (var scope = new CollectingScope<String>()) {
            scope.fork(task1);
            scope.fork(task2);
            scope.join();
            scope.successfulTasks()
                    .map(StructuredTaskScope.Subtask::get)
                    .map(val -> output.append(val + "::"))
                    .collect(Collectors.toList());
//            System.out.println(Thread.currentThread() + "::structuredConcurrency completed time in ms " + Duration.between(start, Instant.now()).toMillis());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output.toString();
    }


    public class CollectingScope<T> extends StructuredTaskScope<T> {
        private final Queue<Subtask<? extends T>> successSubtasks = new LinkedTransferQueue<>();
        private final Queue<Subtask<? extends T>> failedSubtasks = new LinkedTransferQueue<>();

        @Override
        protected void handleComplete(Subtask<? extends T> subtask) {
            if (subtask.state() == Subtask.State.SUCCESS) {
                successSubtasks.add(subtask);
            } else if (subtask.state() == Subtask.State.FAILED) {
                failedSubtasks.add(subtask);
            }
        }

        @Override
        public CollectingScope<T> join() throws InterruptedException {
            super.join();
            return this;
        }

        public Stream<Subtask<? extends T>> successfulTasks() {
            super.ensureOwnerAndJoined();
            return successSubtasks.stream();
        }

        public Stream<Subtask<? extends T>> failedTasks() {
            super.ensureOwnerAndJoined();
            return failedSubtasks.stream();
        }
    }
}
