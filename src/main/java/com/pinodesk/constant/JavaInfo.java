package com.pinodesk.constant;

public interface JavaInfo {

    /**
     * Java version string (ex: 17.0.12)
     */
    String VERSION = System.getProperty("java.version");

    /**
     * Java vendor string (ex: Eclipse Adoptium)
     */
    String VENDOR = System.getProperty("java.vendor");

    /**
     * Java runtime name (ex: OpenJDK Runtime Environment)
     */
    String RUNTIME_NAME = System.getProperty("java.runtime.name");

    /**
     * Java runtime version (ex: 17.0.12+7)
     */
    String RUNTIME_VERSION = System.getProperty("java.runtime.version");

    /**
     * Java VM name (ex: OpenJDK 64-Bit Server VM)
     */
    String VM_NAME = System.getProperty("java.vm.name");

    /**
     * Java VM vendor (ex: Eclipse Adoptium)
     */
    String VM_VENDOR = System.getProperty("java.vm.vendor");

    /**
     * Java VM version (ex: 17.0.12+7)
     */
    String VM_VERSION = System.getProperty("java.vm.version");

}
