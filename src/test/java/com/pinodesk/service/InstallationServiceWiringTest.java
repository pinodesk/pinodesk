package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinodesk.properties.ApplicationProperties;
import com.pinodesk.service.api.PinodeskRetrofitApiService;
import com.pinodesk.toolbox.jackson.ObjectConverter;

class InstallationServiceWiringTest extends BaseServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void springInitializesInstallationAndApiWithoutCircularConstruction() {
        Path installationFile = tempDir.resolve("installation.json");
        ApplicationProperties properties = mock(ApplicationProperties.class);
        when(properties.getInstallationFile()).thenReturn(installationFile);
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().getPropertySources()
                    .addFirst(new MapPropertySource("test", Map.of("pinodesk.api.base_url", "http://127.0.0.1/")));
            context.registerBean(ApplicationProperties.class, () -> properties);
            context.registerBean(ObjectMapper.class, () -> objectMapper);
            context.registerBean(ObjectConverter.class, () -> objectConverter);
            context.registerBean(Validator.class, () -> mock(Validator.class));
            context.register(InstallationService.class, PinodeskRetrofitApiService.class);

            context.refresh();

            InstallationService installation = context.getBean(InstallationService.class);
            assertNotNull(context.getBean(PinodeskRetrofitApiService.class));
            assertTrue(Files.isRegularFile(installationFile));
            String instanceId = installation.getInstallationData().orElseThrow().getInstanceId();
            assertNotNull(instanceId);
            assertEquals(instanceId, installation.ensureInstallationDataExists().getInstanceId());
        }
    }
}
