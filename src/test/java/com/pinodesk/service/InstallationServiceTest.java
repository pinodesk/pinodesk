package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.pinodesk.apimodel.RegisterInstallationRequest;
import com.pinodesk.apimodel.RegisterInstallationResponse;
import com.pinodesk.apimodel.RequestInstallationCodeResponse;
import com.pinodesk.properties.ApplicationProperties;
import com.pinodesk.service.api.PinodeskRetrofitApiService;

class InstallationServiceTest extends BaseServiceTest {

    @Mock
    private PinodeskRetrofitApiService pinodeskRetrofitApiService;

    @Mock
    private ApplicationProperties applicationProperties;

    @InjectMocks
    private InstallationService installationService;

    @TempDir
    Path tempDir;

    private Path installationFilePath;

    @BeforeEach
    void setUp() {
        installationFilePath = tempDir.resolve("installation.json");
        when(applicationProperties.getInstallationFile()).thenReturn(installationFilePath);
    }

    @Test
    void testLoadOrCreateInstanceId_generatesNewAndStoresInFileWhenNotExists() throws IOException {
        String instanceId = installationService.loadOrCreateInstanceId();

        assertNotNull(instanceId);
        assertFalse(instanceId.isBlank());
        assertTrue(Files.exists(installationFilePath));

        JsonNode root = objectMapper.readTree(installationFilePath.toFile());
        assertTrue(root.hasNonNull("instance_id"));
        assertEquals(instanceId, root.get("instance_id").asText());
    }

    @Test
    void testLoadOrCreateInstanceId_readsExistingFromFile() throws IOException {
        String existingId = "existing-instance-uuid-123";
        String content = "{\"instance_id\":\"" + existingId + "\"}";
        Files.writeString(installationFilePath, content);

        String instanceId = installationService.loadOrCreateInstanceId();

        assertEquals(existingId, instanceId);
    }

    @Test
    void testIsRegistered_returnsFalseWhenFileNotExists() {
        assertFalse(installationService.isRegistered());
    }

    @Test
    void testIsRegistered_returnsFalseWhenOnlyInstanceIdPresent() {
        installationService.loadOrCreateInstanceId();
        assertFalse(installationService.isRegistered());
    }

    @Test
    void testIsRegistered_returnsTrueWhenInstallationIdPresent() throws IOException {
        String content = "{\"instance_id\":\"inst-1\",\"installation_id\":\"install-456\"}";
        Files.writeString(installationFilePath, content);

        assertTrue(installationService.isRegistered());
    }

    @Test
    void testRequestInstallationCode_delegatesToRetrofitService() {
        String email = "test@example.com";
        RequestInstallationCodeResponse expectedResponse = new RequestInstallationCodeResponse();
        expectedResponse.setInstallationRequestId("req-123");

        when(pinodeskRetrofitApiService.requestInstallationCode(email)).thenReturn(expectedResponse);

        RequestInstallationCodeResponse response = installationService.requestInstallationCode(email);

        assertNotNull(response);
        assertEquals("req-123", response.getInstallationRequestId());
        verify(pinodeskRetrofitApiService).requestInstallationCode(email);
    }

    @Test
    void testRegisterInstallation_buildsRequestAndUpdatesInstallationFile() throws IOException {
        when(applicationProperties.getReleasePlatform()).thenReturn("linux");
        when(applicationProperties.getAppVersion()).thenReturn("1.8.0");

        String expectedInstanceId = "my-instance-id";
        Files.writeString(installationFilePath, "{\"instance_id\":\"" + expectedInstanceId + "\"}");

        RegisterInstallationResponse mockResponse = new RegisterInstallationResponse();
        mockResponse.setInstanceId(expectedInstanceId);
        mockResponse.setInstallationId("inst-id-789");
        mockResponse.setInstallationToken("token-xyz");
        mockResponse.setEmail("user@example.com");
        mockResponse.setRegisteredAt(ZonedDateTime.now());

        when(pinodeskRetrofitApiService.registerInstallation(any(RegisterInstallationRequest.class)))
                .thenReturn(mockResponse);

        RegisterInstallationResponse result = installationService
                .registerInstallation("user@example.com", "req-123", "CODE-999");

        assertNotNull(result);
        assertEquals("inst-id-789", result.getInstallationId());

        ArgumentCaptor<RegisterInstallationRequest> captor = ArgumentCaptor.forClass(RegisterInstallationRequest.class);
        verify(pinodeskRetrofitApiService).registerInstallation(captor.capture());
        RegisterInstallationRequest sentRequest = captor.getValue();

        assertEquals("user@example.com", sentRequest.getEmail());
        assertEquals("req-123", sentRequest.getInstallationRequestId());
        assertEquals("CODE-999", sentRequest.getInstallationCode());
        assertEquals("linux", sentRequest.getReleasePlatform());
        assertEquals("1.8.0", sentRequest.getReleaseVersion());
        assertEquals(expectedInstanceId, sentRequest.getInstanceId());

        JsonNode root = objectMapper.readTree(installationFilePath.toFile());
        assertEquals(expectedInstanceId, root.get("instance_id").asText());
        assertEquals("inst-id-789", root.get("installation_id").asText());
        assertEquals("token-xyz", root.get("installation_token").asText());
        assertEquals("user@example.com", root.get("email").asText());
        assertTrue(root.hasNonNull("registered_at"));
        assertTrue(root.get("registered_at").isTextual());
        assertFalse(root.get("registered_at").isArray());
        assertTrue(installationService.isRegistered());

        RegisterInstallationResponse data = installationService.getInstallationData();
        assertNotNull(data);
        assertEquals("user@example.com", data.getEmail());
        assertEquals("inst-id-789", data.getInstallationId());
        assertEquals(expectedInstanceId, data.getInstanceId());
    }

}
