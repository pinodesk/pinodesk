package com.getkembang.kembangdesktop.utility;

import com.getkembang.kembangdesktop.SpringConfig;
import com.gitlab.muhammadkholidb.sequel.config.SequelConfig;

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
        applicationContext = new AnnotationConfigApplicationContext(SpringConfig.class, SequelConfig.class);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
