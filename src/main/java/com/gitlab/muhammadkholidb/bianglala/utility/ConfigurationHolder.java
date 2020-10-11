package com.gitlab.muhammadkholidb.bianglala.utility;

import java.util.Properties;

import com.gitlab.muhammadkholidb.bianglala.service.ConfigurationService;

public class ConfigurationHolder {
    
    private ConfigurationHolder() {}

    private static Properties configurationProperties;

    private static ConfigurationService configurationService;

    public static void init() {
        if (configurationProperties != null) {
            throw new IllegalStateException("Initialized already");
        }
        configurationService = ApplicationContextHolder.getApplicationContext().getBean(ConfigurationService.class);
        reload();
    }

    public static Properties getConfigurationProperties() {
        return configurationProperties;
    }

    public static void reload() {
        configurationProperties = configurationService.getConfigurationAsProperties();
    }

    public static String getConfiguration(String code) {
        return configurationProperties.getProperty(code);
    }

}
