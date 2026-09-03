package com.pinodesk.util;

import java.nio.file.Path;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.SystemUtils;

import lombok.Getter;
import oshi.SystemInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

public final class DeviceUtils {

    private DeviceUtils() {
    }

    /**
     * Current working directory
     */
    public static final String CWD = Path.of(".").toAbsolutePath().toString();

    public static final String OS_FAMILY_WINDOWS = "windows";
    public static final String OS_FAMILY_DARWIN = "darwin";
    public static final String OS_FAMILY_LINUX = "linux";

    @Getter
    private static String deviceSignature;
    @Getter
    private static String deviceManufacturer;
    @Getter
    private static String deviceModel;
    @Getter
    private static String osName;
    @Getter
    private static String osVersion;
    @Getter
    private static String osFamily;
    @Getter
    private static String osArch;
    @Getter
    private static Integer osBitness;
    @Getter
    private static String cpuName;
    @Getter
    private static String cpuFamily;
    @Getter
    private static String cpuVendor;
    @Getter
    private static Long ramSize;
    @Getter
    private static Long storageSize;

    static {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem operatingSystem = si.getOperatingSystem();
        CentralProcessor processor = hal.getProcessor();
        ComputerSystem computer = hal.getComputerSystem();
        OperatingSystem.OSVersionInfo osVersionInfo = operatingSystem.getVersionInfo();
        GlobalMemory memory = hal.getMemory();
        FileSystem fileSystem = operatingSystem.getFileSystem();
        CentralProcessor.ProcessorIdentifier processorIdentifier = processor.getProcessorIdentifier();
        deviceManufacturer = computer.getManufacturer(); // Apple Inc.
        deviceModel = computer.getModel();// Mac14,7
        osName = operatingSystem.getFamily(); // macOS
        osVersion = osVersionInfo.getVersion(); // 14.1
        osFamily = readOsFamily();
        osArch = System.getProperty("os.arch"); // x86_64
        osBitness = operatingSystem.getBitness(); // 64, 32
        cpuName = processorIdentifier.getName();// Apple M2
        cpuFamily = processorIdentifier.getMicroarchitecture(); // ARM64 SoC: Avalanche + Blizzard
        cpuVendor = processorIdentifier.getVendor();// Apple Inc.
        ramSize = memory.getTotal();
        storageSize = readStorageSize(fileSystem);
        deviceSignature = generateDeviceSignature(deviceManufacturer, deviceModel, computer.getBaseboard());
    }

    private static String generateDeviceSignature(String deviceManufacturer, String deviceModel, Baseboard baseboard) {
        StringBuilder sb = new StringBuilder();
        sb.append(deviceManufacturer);
        sb.append(deviceModel);
        sb.append(baseboard.getManufacturer());
        sb.append(baseboard.getModel());
        sb.append(baseboard.getVersion());
        sb.append(baseboard.getSerialNumber());
        return DigestUtils.sha256Hex(sb.toString()).toUpperCase();
    }

    public static String readOsFamily() {
        if (SystemUtils.IS_OS_MAC) {
            return OS_FAMILY_DARWIN;
        }
        if (SystemUtils.IS_OS_LINUX) {
            return OS_FAMILY_LINUX;
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            return OS_FAMILY_WINDOWS;
        }
        return null;
    }

    public static Long readStorageSize(FileSystem fileSystem) {
        for (OSFileStore fs : fileSystem.getFileStores()) {
            String mount = fs.getMount();
            if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
                if (mount.equals("/")) {
                    return fs.getTotalSpace();
                }
            } else if (SystemUtils.IS_OS_WINDOWS && CWD.startsWith(mount)) {
                return fs.getTotalSpace();
            }
        }
        return null;
    }

    public static Long getRamSizeAvailable() {
        return new SystemInfo().getHardware().getMemory().getAvailable();
    }

    public static Long getStorageSizeAvailable() {
        FileSystem fileSystem = new SystemInfo().getOperatingSystem().getFileSystem();
        for (OSFileStore fs : fileSystem.getFileStores()) {
            String mount = fs.getMount();
            if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
                if (mount.equals("/")) {
                    return fs.getFreeSpace();
                }
            } else if (SystemUtils.IS_OS_WINDOWS && CWD.startsWith(mount)) {
                return fs.getFreeSpace();
            }
        }
        return null;
    }

}
