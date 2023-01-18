package pospino.desktop.util;

import java.util.Locale;
import java.util.ResourceBundle;

import pospino.desktop.constant.CommonConstants;
import pospino.desktop.constant.ConfigurationConstants;
import pospino.desktop.service.ConfigurationService;

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
