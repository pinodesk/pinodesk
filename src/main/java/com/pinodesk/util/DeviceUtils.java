package com.pinodesk.util;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

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

    /**
     * Increment this only when the device signature algorithm changes.
     */
    public static final int DEVICE_SIGNATURE_VERSION = 1;

    private static final Set<String> INVALID_HARDWARE_IDENTIFIERS = Set.of(
            "",
            "unknown",
            "none",
            "null",
            "not specified",
            "not applicable",
            "default string",
            "to be filled by o.e.m.",
            "system serial number",
            "base board serial number",
            "00000000-0000-0000-0000-000000000000",
            "ffffffff-ffff-ffff-ffff-ffffffffffff",
            "03000200-0400-0500-0006-000700080009");

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
        GlobalMemory memory = hal.getMemory();
        FileSystem fileSystem = operatingSystem.getFileSystem();

        OperatingSystem.OSVersionInfo osVersionInfo = operatingSystem.getVersionInfo();

        CentralProcessor.ProcessorIdentifier processorIdentifier = processor.getProcessorIdentifier();

        deviceManufacturer = computer.getManufacturer();
        deviceModel = computer.getModel();

        osName = operatingSystem.getFamily();
        osVersion = osVersionInfo.getVersion();
        osFamily = readOsFamily();
        osArch = System.getProperty("os.arch");
        osBitness = operatingSystem.getBitness();

        cpuName = processorIdentifier.getName();
        cpuFamily = processorIdentifier.getMicroarchitecture();
        cpuVendor = processorIdentifier.getVendor();

        ramSize = memory.getTotal();
        storageSize = readStorageSize(fileSystem);

        deviceSignature = generateDeviceSignature(computer);
    }

    private static String generateDeviceSignature(ComputerSystem computer) {

        Baseboard baseboard = computer.getBaseboard();

        String hardwareUuid = normalizeHardwareIdentifier(computer.getHardwareUUID());

        String systemSerial = normalizeHardwareIdentifier(computer.getSerialNumber());

        String baseboardSerial = normalizeHardwareIdentifier(baseboard.getSerialNumber());

        /*
         * Do not pretend that manufacturer/model alone uniquely identifies a computer.
         */
        if (hardwareUuid.isEmpty() && systemSerial.isEmpty() && baseboardSerial.isEmpty()) {
            return null;
        }

        String raw = String.join(
                "|",
                "v" + DEVICE_SIGNATURE_VERSION,
                encodeSignatureField("manufacturer", normalizeValue(computer.getManufacturer())),
                encodeSignatureField("model", normalizeValue(computer.getModel())),
                encodeSignatureField("uuid", hardwareUuid),
                encodeSignatureField("system-serial", systemSerial),
                encodeSignatureField("baseboard-manufacturer", normalizeValue(baseboard.getManufacturer())),
                encodeSignatureField("baseboard-model", normalizeValue(baseboard.getModel())),
                encodeSignatureField("baseboard-serial", baseboardSerial));

        return DigestUtils.sha256Hex(raw).toUpperCase(Locale.ROOT);
    }

    private static String encodeSignatureField(String name, String value) {
        return name + "=" + value.length() + ":" + value;
    }

    private static String normalizeHardwareIdentifier(String value) {
        String normalized = normalizeValue(value);

        if (INVALID_HARDWARE_IDENTIFIERS.contains(normalized)) {
            return "";
        }

        return normalized;
    }

    private static String normalizeValue(String value) {
        if (value == null) {
            return "";
        }

        return value.trim().toLowerCase(Locale.ROOT);
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
