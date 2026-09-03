package com.pinodesk.util;

import java.util.Locale;
import java.util.ResourceBundle;

import com.pinodesk.constant.CommonConstants;
import com.pinodesk.constant.ConfigurationConstants;
import com.pinodesk.service.ConfigurationService;

public final class ResourceBundleUtils {

    private ResourceBundleUtils() {
    }

    public static ResourceBundle getDefaultResourceBundle() {
        if (SpringUtils.isInitialized()) {
            ConfigurationService configurationService = SpringUtils.getBean(ConfigurationService.class);
            String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
            return ResourceBundle.getBundle(CommonConstants.RESOURCE_BUNDLE_PACKAGE, Locale.forLanguageTag(language));
        }
        return ResourceBundle.getBundle(CommonConstants.RESOURCE_BUNDLE_PACKAGE, Locale.ENGLISH);
    }

}
