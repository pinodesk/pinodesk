package pinus.desktop.util;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

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
        queue.add(runnable);
        while (!queue.isEmpty()) {
            future.thenRunAsync(queue.poll()).exceptionally(e -> null);
        }
    }
}
