package pospino.desktop.util;

import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TaskUtils {

    private TaskUtils() {
    }

    public static void runTask(String name, Runnable runnable) {
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
            }
        };
        new Thread(task).start();
    }

}
