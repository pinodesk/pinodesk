package com.pinodesk.constant;

import java.io.File;

public interface SystemConstants {
    String USER_HOME_DIR = System.getProperty("user.home");
    String FILE_SEPARATOR = File.separator;
    String LINE_BREAK = System.lineSeparator();
}
