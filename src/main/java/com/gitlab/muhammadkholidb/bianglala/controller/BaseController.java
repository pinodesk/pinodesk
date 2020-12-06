package com.gitlab.muhammadkholidb.bianglala.controller;

import com.gitlab.muhammadkholidb.bianglala.utility.ApplicationContextHolder;

import org.springframework.context.ApplicationContext;

import javafx.fxml.FXML;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseController {

    @FXML
    void initialize() {
        setDefaultUncaughtExceptionHandler();
        initServices(ApplicationContextHolder.getApplicationContext());
        initControls();
    }

    protected abstract void initServices(ApplicationContext ctx);

    protected abstract void initControls();

    // https://stackoverflow.com/questions/12409638/java-exception-handling-catching-superclass-exception
    private static void setDefaultUncaughtExceptionHandler() {
        try {
            if (Thread.getDefaultUncaughtExceptionHandler() == null) {
                Thread.setDefaultUncaughtExceptionHandler(
                        (t, e) -> log.error("Uncaught Exception detected in thread: " + t, e));
            }
        } catch (SecurityException e) {
            log.error("Could not set the Default Uncaught Exception Handler", e);
        }
    }
}
