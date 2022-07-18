package pinus.desktop.util;

import java.util.Locale;
import java.util.ResourceBundle;

import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.ConfigurationConstants;
import pinus.desktop.service.ConfigurationService;

public final class ResourceBundleUtils {

    private ResourceBundleUtils() {
    }

    public static ResourceBundle getDefaultResourceBundle() {
        if (SpringUtils.isInitialized()) {
            ConfigurationService configurationService = SpringUtils.getBean(ConfigurationService.class);
            String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
            return ResourceBundle.getBundle(CommonConstants.RESOURCE_BUNDLE_PACKAGE, new Locale(language));
        }
        return ResourceBundle.getBundle(CommonConstants.RESOURCE_BUNDLE_PACKAGE, Locale.ENGLISH);
    }

}
