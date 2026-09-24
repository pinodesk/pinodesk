package com.pinodesk;

import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Base class for JavaFX UI tests using TestFX with Monocle for headless
 * testing. This enables JavaFX tests to run in CI environments without a
 * display.
 */
@ExtendWith(ApplicationExtension.class)
public abstract class JavaFXTestBase extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Override if needed for test-specific stage setup
    }
}