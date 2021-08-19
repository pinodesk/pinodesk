package toska.desktop.utility;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringUtils {

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
