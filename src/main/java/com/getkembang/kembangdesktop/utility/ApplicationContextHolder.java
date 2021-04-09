package com.getkembang.kembangdesktop.utility;

import com.getkembang.kembangdesktop.KembangConfig;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationContextHolder {

    private ApplicationContextHolder() {
    }

    private static ApplicationContext applicationContext;

    public static void init() {
        if (applicationContext != null) {
            throw new UnsupportedOperationException("Initialized already");
        }
        applicationContext = new AnnotationConfigApplicationContext(KembangConfig.class);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
