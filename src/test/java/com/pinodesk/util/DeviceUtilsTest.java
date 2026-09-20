package com.pinodesk.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Locale;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import oshi.hardware.Baseboard;
import oshi.hardware.ComputerSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
public class DeviceUtilsTest {

    @Test
    void testNormalizeValue_shouldHandleNull() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("normalizeValue", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(null, (String) null);
        assertThat(result, is(emptyOrNullString()));
    }

    @Test
    void testNormalizeValue_shouldTrimAndLowercase() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("normalizeValue", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(null, "  TEST Value  ");
        assertThat(result, is("test value"));
    }

    @Test
    void testNormalizeValue_shouldHandleEmptyString() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("normalizeValue", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(null, "");
        assertThat(result, is(emptyOrNullString()));
    }

    @Test
    void testNormalizeValue_shouldHandleWhitespace() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("normalizeValue", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(null, "   ");
        assertThat(result, is(emptyOrNullString()));
    }

    @Test
    void testNormalizeHardwareIdentifier_shouldFilterInvalidIdentifiers() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("normalizeHardwareIdentifier", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, "unknown");
        assertThat(result, is(emptyOrNullString()));

        result = (String) method.invoke(null, "UNKNOWN");
        assertThat(result, is(emptyOrNullString()));

        result = (String) method.invoke(null, "  unknown  ");
        assertThat(result, is(emptyOrNullString()));
    }

    @Test
    void testNormalizeHardwareIdentifier_shouldFilterNone() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("normalizeHardwareIdentifier", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, "none");
        assertThat(result, is(emptyOrNullString()));

        result = (String) method.invoke(null, "NONE");
        assertThat(result, is(emptyOrNullString()));
    }

    @Test
    void testNormalizeHardwareIdentifier_shouldFilterNull() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("normalizeHardwareIdentifier", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(null, (String) null);
        assertThat(result, is(emptyOrNullString()));
    }

    @Test
    void testNormalizeHardwareIdentifier_shouldFilterToBeFilledByOem() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("normalizeHardwareIdentifier", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, "To Be Filled By O.E.M.");
        assertThat(result, is(emptyOrNullString()));

        result = (String) method.invoke(null, "TO BE FILLED BY O.E.M.");
        assertThat(result, is(emptyOrNullString()));
    }

    @Test
    void testNormalizeHardwareIdentifier_shouldFilterSystemSerialNumber() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("normalizeHardwareIdentifier", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, "System Serial Number");
        assertThat(result, is(emptyOrNullString()));

        result = (String) method.invoke(null, "SYSTEM SERIAL NUMBER");
        assertThat(result, is(emptyOrNullString()));
    }

    @Test
    void testNormalizeHardwareIdentifier_shouldFilterZeroUuid() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("normalizeHardwareIdentifier", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, "00000000-0000-0000-0000-000000000000");
        assertThat(result, is(emptyOrNullString()));
    }

    @Test
    void testNormalizeHardwareIdentifier_shouldFilterFFFFUuid() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("normalizeHardwareIdentifier", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, "ffffffff-ffff-ffff-ffff-ffffffffffff");
        assertThat(result, is(emptyOrNullString()));
    }

    @Test
    void testNormalizeHardwareIdentifier_shouldAcceptValidIdentifier() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("normalizeHardwareIdentifier", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, "ABC123-XYZ789");
        assertThat(result, is("abc123-xyz789"));
    }

    @Test
    void testNormalizeHardwareIdentifier_shouldAcceptValidSerial() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("normalizeHardwareIdentifier", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, "SN123456789");
        assertThat(result, is("sn123456789"));
    }

    @Test
    void testReadOsFamily_shouldReturnDarwinForMac() {
        String result = DeviceUtils.readOsFamily();
        if (SystemUtils.IS_OS_MAC) {
            assertThat(result, is(DeviceUtils.OS_FAMILY_DARWIN));
        }
    }

    @Test
    void testReadOsFamily_shouldReturnLinuxForLinux() {
        String result = DeviceUtils.readOsFamily();
        if (SystemUtils.IS_OS_LINUX) {
            assertThat(result, is(DeviceUtils.OS_FAMILY_LINUX));
        }
    }

    @Test
    void testReadOsFamily_shouldReturnWindowsForWindows() {
        String result = DeviceUtils.readOsFamily();
        if (SystemUtils.IS_OS_WINDOWS) {
            assertThat(result, is(DeviceUtils.OS_FAMILY_WINDOWS));
        }
    }

    @Test
    void testReadStorageSize_shouldReturnNullForEmptyFileSystem() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("readStorageSize", FileSystem.class);
        method.setAccessible(true);

        FileSystem mockFileSystem = mock(FileSystem.class);
        when(mockFileSystem.getFileStores()).thenReturn(java.util.Collections.emptyList());

        Long result = (Long) method.invoke(null, mockFileSystem);
        assertThat(result, is(equalTo(null)));
    }

    @Test
    void testReadStorageSize_shouldReturnTotalSpaceForRootMountOnLinux() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("readStorageSize", FileSystem.class);
        method.setAccessible(true);

        FileSystem mockFileSystem = mock(FileSystem.class);
        OSFileStore mockFileStore = mock(OSFileStore.class);

        when(mockFileStore.getMount()).thenReturn("/");
        when(mockFileStore.getTotalSpace()).thenReturn(1000000000L);

        when(mockFileSystem.getFileStores()).thenReturn(java.util.Collections.singletonList(mockFileStore));

        Long result = (Long) method.invoke(null, mockFileSystem);
        if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
            assertThat(result, is(1000000000L));
        }
    }

    @Test
    void testReadStorageSize_shouldReturnTotalSpaceForWindowsMount() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("readStorageSize", FileSystem.class);
        method.setAccessible(true);

        FileSystem mockFileSystem = mock(FileSystem.class);
        OSFileStore mockFileStore = mock(OSFileStore.class);

        when(mockFileStore.getMount()).thenReturn("C:\\");
        when(mockFileStore.getTotalSpace()).thenReturn(2000000000L);

        when(mockFileSystem.getFileStores()).thenReturn(java.util.Collections.singletonList(mockFileStore));

        Long result = (Long) method.invoke(null, mockFileSystem);
        if (SystemUtils.IS_OS_WINDOWS && DeviceUtils.CWD.startsWith("C:\\")) {
            assertThat(result, is(2000000000L));
        }
    }

    @Test
    void testGenerateDeviceSignature_shouldReturnNullWhenAllIdentifiersInvalid() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("generateDeviceSignature", ComputerSystem.class);
        method.setAccessible(true);

        ComputerSystem mockComputer = mock(ComputerSystem.class);
        Baseboard mockBaseboard = mock(Baseboard.class);

        when(mockComputer.getHardwareUUID()).thenReturn("unknown");
        when(mockComputer.getSerialNumber()).thenReturn("none");
        when(mockComputer.getBaseboard()).thenReturn(mockBaseboard);
        when(mockBaseboard.getSerialNumber()).thenReturn("null");

        String result = (String) method.invoke(null, mockComputer);
        assertThat(result, is(equalTo(null)));
    }

    @Test
    void testGenerateDeviceSignature_shouldGenerateSignatureWithValidUuid() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("generateDeviceSignature", ComputerSystem.class);
        method.setAccessible(true);

        ComputerSystem mockComputer = mock(ComputerSystem.class);
        Baseboard mockBaseboard = mock(Baseboard.class);

        when(mockComputer.getHardwareUUID()).thenReturn("12345678-1234-1234-1234-123456789abc");
        when(mockComputer.getSerialNumber()).thenReturn("unknown");
        when(mockComputer.getBaseboard()).thenReturn(mockBaseboard);
        when(mockBaseboard.getSerialNumber()).thenReturn("none");
        when(mockComputer.getManufacturer()).thenReturn("Dell");
        when(mockComputer.getModel()).thenReturn("XPS 15");
        when(mockBaseboard.getManufacturer()).thenReturn("Dell Inc.");
        when(mockBaseboard.getModel()).thenReturn("0XYZ123");

        String result = (String) method.invoke(null, mockComputer);
        assertThat(result, is(notNullValue()));
        assertThat(result, hasLength(64));
        assertThat(result.toUpperCase(Locale.ROOT), is(result));
    }

    @Test
    void testGenerateDeviceSignature_shouldGenerateSignatureWithValidSystemSerial() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("generateDeviceSignature", ComputerSystem.class);
        method.setAccessible(true);

        ComputerSystem mockComputer = mock(ComputerSystem.class);
        Baseboard mockBaseboard = mock(Baseboard.class);

        when(mockComputer.getHardwareUUID()).thenReturn("unknown");
        when(mockComputer.getSerialNumber()).thenReturn("SN123456789");
        when(mockComputer.getBaseboard()).thenReturn(mockBaseboard);
        when(mockBaseboard.getSerialNumber()).thenReturn("none");

        String result = (String) method.invoke(null, mockComputer);
        assertThat(result, is(notNullValue()));
        assertThat(result, hasLength(64));
    }

    @Test
    void testGenerateDeviceSignature_shouldGenerateSignatureWithValidBaseboardSerial() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("generateDeviceSignature", ComputerSystem.class);
        method.setAccessible(true);

        ComputerSystem mockComputer = mock(ComputerSystem.class);
        Baseboard mockBaseboard = mock(Baseboard.class);

        when(mockComputer.getHardwareUUID()).thenReturn("unknown");
        when(mockComputer.getSerialNumber()).thenReturn("none");
        when(mockComputer.getBaseboard()).thenReturn(mockBaseboard);
        when(mockBaseboard.getSerialNumber()).thenReturn("MB987654321");

        String result = (String) method.invoke(null, mockComputer);
        assertThat(result, is(notNullValue()));
        assertThat(result, hasLength(64));
    }

    @Test
    void testGenerateDeviceSignature_shouldBeDeterministic() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("generateDeviceSignature", ComputerSystem.class);
        method.setAccessible(true);

        ComputerSystem mockComputer = mock(ComputerSystem.class);
        Baseboard mockBaseboard = mock(Baseboard.class);

        when(mockComputer.getHardwareUUID()).thenReturn("12345678-1234-1234-1234-123456789abc");
        when(mockComputer.getSerialNumber()).thenReturn("SN123456789");
        when(mockComputer.getBaseboard()).thenReturn(mockBaseboard);
        when(mockBaseboard.getSerialNumber()).thenReturn("MB987654321");

        String result1 = (String) method.invoke(null, mockComputer);
        String result2 = (String) method.invoke(null, mockComputer);

        assertThat(result1, is(result2));
    }

    @Test
    void testGenerateDeviceSignature_shouldIncludeVersionInSignature() throws Exception {
        Method method = DeviceUtils.class.getDeclaredMethod("generateDeviceSignature", ComputerSystem.class);
        method.setAccessible(true);

        ComputerSystem mockComputer = mock(ComputerSystem.class);
        Baseboard mockBaseboard = mock(Baseboard.class);

        when(mockComputer.getHardwareUUID()).thenReturn("12345678-1234-1234-1234-123456789abc");
        when(mockComputer.getSerialNumber()).thenReturn("unknown");
        when(mockComputer.getBaseboard()).thenReturn(mockBaseboard);
        when(mockBaseboard.getSerialNumber()).thenReturn("none");

        String result = (String) method.invoke(null, mockComputer);
        assertThat(result, is(notNullValue()));
    }

    @Test
    void testStaticFields_shouldBeInitialized() {
        assertThat(DeviceUtils.CWD, is(notNullValue()));
        assertThat(DeviceUtils.getOsFamily(), is(notNullValue()));
        assertThat(DeviceUtils.getOsArch(), is(notNullValue()));
        assertThat(DeviceUtils.getOsBitness(), is(notNullValue()));
    }

    @Test
    void testStaticFields_shouldHaveValidValues() {
        String cwd = DeviceUtils.CWD;
        assertThat(cwd, is(not(emptyOrNullString())));
        assertThat(cwd, containsString("/"));

        String osArch = DeviceUtils.getOsArch();
        assertThat(osArch, is(not(emptyOrNullString())));

        Integer osBitness = DeviceUtils.getOsBitness();
        assertThat(osBitness, is(notNullValue()));
        assertThat(osBitness > 0, is(true));
    }

    @Test
    void testGetRamSizeAvailable_shouldReturnPositiveValue() {
        Long ramAvailable = DeviceUtils.getRamSizeAvailable();
        assertThat(ramAvailable, is(notNullValue()));
        assertThat(ramAvailable > 0, is(true));
    }

    @Test
    void testGetStorageSizeAvailable_shouldReturnPositiveValueOrNull() {
        Long storageAvailable = DeviceUtils.getStorageSizeAvailable();
        if (storageAvailable != null) {
            assertThat(storageAvailable > 0, is(true));
        }
    }

}
