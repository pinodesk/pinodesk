package pospino.desktop.util;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AsyncQueueProcessor {
    private CompletableFuture<Void> future;
    private Queue<Runnable> queue;

    public AsyncQueueProcessor() {
        future = CompletableFuture.runAsync(() -> {
        });
        queue = new LinkedList<>();
    }

    public void process(Runnable runnable) {
        log.debug("queue: " + queue);
        log.debug("future: " + future);
        queue.add(runnable);
        while (!queue.isEmpty()) {
            future.thenRunAsync(queue.poll()).exceptionally(e -> {
                log.error("Process queue error!", e);
                return null;
            });
        }
    }
}
