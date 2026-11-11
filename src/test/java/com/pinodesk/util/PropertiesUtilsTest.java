package com.pinodesk.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class PropertiesUtilsTest {

    @Test
    void logsReadFailuresAndReturnsEmptyProperties() {
        InputStream brokenInput = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("read failed");
            }
        };

        Properties properties = PropertiesUtils.loadApplicationProperties(brokenInput);

        assertTrue(properties.isEmpty());
    }
}
