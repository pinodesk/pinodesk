package com.gitlab.muhammadkholidb.bianglala.utility;

import org.springframework.context.ApplicationContext;

public class ApplicationContextHolder {
    
    private ApplicationContextHolder() {}

    private static ApplicationContext applicationContext;

    public static void init(ApplicationContext ctx) {
        applicationContext = ctx;
    }

    public static ApplicationContext get() {
        return applicationContext;
    }

}
