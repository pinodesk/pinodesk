package com.pinodesk.util;

import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public final class TaskUtils {

    private TaskUtils() {
    }

    public static void runTask(String name, Runnable runnable) {
        runTask(name, runnable, null);
    }

    public static void runTask(String name, Runnable runnable, Consumer<Throwable> onFailed) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (name != null) {
                    updateTitle(name);
                }
                runnable.run();
                return null;
            }

            @Override
            protected void failed() {
                log.error("Failed to run task: " + getTitle(), getException());
                if (onFailed != null) {
                    onFailed.accept(getException());
                }
            }
        };
        new Thread(task).start();
    }

}
