package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Properties;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.springframework.test.util.ReflectionTestUtils;

import com.pinodesk.entity.Configuration;
import com.pinodesk.repository.ConfigurationRepository;

class ConfigurationServiceTest extends BaseServiceTest {

    @Mock
    private ConfigurationRepository configurationRepository;

    @InjectMocks
    private ConfigurationService configurationService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(configurationRepository);
    }

    @Test
    void testGetConfiguration_shouldReturnValue() {
        Configuration configuration = new Configuration();
        configuration.setValue("value");
        when(configurationRepository.findByCodeAndDeletedAtIsNull(anyString())).thenReturn(Optional.of(configuration));
        String value = configurationService.getConfiguration("code");
        assertNotNull(value);
        assertEquals("value", value);
        verify(configurationRepository).findByCodeAndDeletedAtIsNull(anyString());
    }

    @Test
    void testGetConfiguration_notFound_shouldReturnNull() {
        when(configurationRepository.findByCodeAndDeletedAtIsNull(anyString())).thenReturn(Optional.empty());
        String value = configurationService.getConfiguration("code");
        assertNull(value);
        verify(configurationRepository).findByCodeAndDeletedAtIsNull(anyString());
    }

    @Test
    void createBackupProperties_shouldWriteMetadata() throws IOException {
        ReflectionTestUtils.setField(configurationService, "appName", "Pinodesk");
        ReflectionTestUtils.setField(configurationService, "appVersion", "1.0");
        Path path = tempDir.resolve("backup.properties");
        long before = System.currentTimeMillis();

        File result = ReflectionTestUtils.invokeMethod(configurationService, "createBackupProperties", path.toString());

        assertEquals(path.toFile(), result);
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(path)) {
            properties.load(reader);
        }
        assertEquals("Pinodesk", properties.getProperty("app.name"));
        assertEquals("1.0", properties.getProperty("app.version"));
        long timestamp = Long.parseLong(properties.getProperty("timestamp"));
        assertTrue(timestamp >= before && timestamp <= System.currentTimeMillis());
    }

    @Test
    void createBackupProperties_whenFlushFails_shouldCloseWriterAndPreserveFailure() throws IOException {
        ReflectionTestUtils.setField(configurationService, "appName", "Pinodesk");
        ReflectionTestUtils.setField(configurationService, "appVersion", "1.0");
        IOException failure = new IOException("Disk full");
        try (MockedConstruction<FileWriter> writers = mockConstruction(
                FileWriter.class,
                (writer, context) -> doThrow(failure).when(writer).flush())) {
            UndeclaredThrowableException thrown = assertThrows(
                    UndeclaredThrowableException.class,
                    () -> ReflectionTestUtils.invokeMethod(
                            configurationService,
                            "createBackupProperties",
                            tempDir.resolve("backup.properties").toString()));

            assertSame(failure, thrown.getCause());
            assertEquals(1, writers.constructed().size());
            verify(writers.constructed().get(0)).close();
        }
    }

}
