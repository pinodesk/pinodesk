package com.gitlab.muhammadkholidb.bianglala.utility;

import org.apache.commons.lang3.StringUtils;

public class ExceptionUtils {
    
    public static String getMessage(Exception ex, String defaultMessage) {
        String message = ex.getMessage();
        return StringUtils.isBlank(message) ? defaultMessage : message;
    }

    public static String getMessage(Exception ex) {
        return getMessage(ex, ex.toString());
    }

}
