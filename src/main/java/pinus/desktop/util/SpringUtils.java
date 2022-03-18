package pinus.desktop.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class SpringUtils {

    private SpringUtils() {
    }

    private static ApplicationContext applicationContext;

    public static void init(Class<?> configurationClass) {
        if (applicationContext == null) {
            applicationContext = new AnnotationConfigApplicationContext(configurationClass);
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> type) {
        return applicationContext.getBean(type);
    }

}
