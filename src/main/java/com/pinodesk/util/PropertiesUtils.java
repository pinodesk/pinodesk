package com.pinodesk.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertiesUtils {

    public static Properties getApplicationProperties() {
        return loadApplicationProperties(
                PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties"));
    }

    static Properties loadApplicationProperties(InputStream is) {
        Properties prop = new Properties();
        try (InputStream input = is) {
            if (input != null) {
                prop.load(input);
            }
        } catch (IOException e) {
            log.error("Failed to load application properties", e);
        }
        return prop;
    }

}
