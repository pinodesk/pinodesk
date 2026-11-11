package com.pinodesk.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.pinodesk.constant.DomainError;
import com.pinodesk.exception.DomainException;

import javafx.stage.Stage;

class BaseControllerErrorHandlingTest {

    @Test
    void dispatchesDomainErrorsThroughConfiguredHandler() {
        TestController controller = new TestController();

        controller.handleException(new DomainException(DomainError.PAYMENT_METHOD_INVALID));

        assertTrue(controller.domainErrorHandled);
    }

    private static class TestController extends BaseController {
        private boolean domainErrorHandled;

        @Override
        protected void initServices() {
            throw new AssertionError("Not used by this test");
        }

        @Override
        protected void initControlActions() {
            throw new AssertionError("Not used by this test");
        }

        @Override
        protected void initControlValues() {
            throw new AssertionError("Not used by this test");
        }

        @Override
        protected Stage getCurrentStage() {
            throw new AssertionError("Not used by this test");
        }

        @Override
        protected void handleDomainException(DomainException exception) {
            domainErrorHandled = true;
        }
    }
}
