package com.pinodesk.service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinodesk.apimodel.PinodeskApiError;
import com.pinodesk.apimodel.PinodeskApiResponse;
import com.pinodesk.apimodel.RegisterInstallationRequest;
import com.pinodesk.apimodel.RegisterInstallationResponse;
import com.pinodesk.apimodel.RequestInstallationCodeRequest;
import com.pinodesk.apimodel.RequestInstallationCodeResponse;
import com.pinodesk.exception.PinodeskApiException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
class PinodeskRetrofitApiServiceTest {

    @Mock
    private PinodeskApiInterface mockApiInterface;

    @Mock
    private ObjectMapper mockObjectMapper;

    @Mock
    private Call<PinodeskApiResponse<RequestInstallationCodeResponse>> mockCodeCall;

    @Mock
    private Call<PinodeskApiResponse<RegisterInstallationResponse>> mockRegisterCall;

    private PinodeskRetrofitApiService retrofitApiService;

    @BeforeEach
    void setUp() {
        retrofitApiService = new PinodeskRetrofitApiService();
        ReflectionTestUtils.setField(retrofitApiService, "apiInterface", mockApiInterface);
        ReflectionTestUtils.setField(retrofitApiService, "mapper", mockObjectMapper);
        ReflectionTestUtils.setField(retrofitApiService, "baseURL", "https://api.pinodesk.com");
    }

    @Test
    void testRequestInstallationCode_Success() throws Exception {
        // Arrange
        String email = "test@example.com";
        String expectedRequestId = "test-request-id-123";

        RequestInstallationCodeResponse expectedResponse = new RequestInstallationCodeResponse();
        expectedResponse.setInstallationRequestId(expectedRequestId);

        PinodeskApiResponse<RequestInstallationCodeResponse> apiResponse = new PinodeskApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setData(expectedResponse);

        when(mockApiInterface.requestInstallationCode(any(RequestInstallationCodeRequest.class)))
                .thenReturn(mockCodeCall);
        when(mockCodeCall.execute()).thenReturn(Response.success(apiResponse));

        // Act
        RequestInstallationCodeResponse result = retrofitApiService.requestInstallationCode(email);

        // Assert
        assertNotNull(result);
        assertEquals(expectedRequestId, result.getInstallationRequestId());
        verify(mockApiInterface).requestInstallationCode(any(RequestInstallationCodeRequest.class));
        verify(mockCodeCall).execute();
    }

    @Test
    void testRequestInstallationCode_ApiError() throws Exception {
        // Arrange
        String email = "test@example.com";

        PinodeskApiResponse<RequestInstallationCodeResponse> apiResponse = new PinodeskApiResponse<>();
        apiResponse.setSuccess(false);

        PinodeskApiError error = new PinodeskApiError();
        error.setCode("INVALID_EMAIL");
        error.setMessage("Invalid email address");
        apiResponse.setError(error);

        when(mockApiInterface.requestInstallationCode(any(RequestInstallationCodeRequest.class)))
                .thenReturn(mockCodeCall);
        when(mockCodeCall.execute()).thenReturn(Response.success(apiResponse));

        // Act & Assert
        assertThrows(PinodeskApiException.class, () -> retrofitApiService.requestInstallationCode(email));
    }

    @Test
    void testRequestInstallationCode_HttpError() throws Exception {
        // Arrange
        String email = "test@example.com";

        when(mockApiInterface.requestInstallationCode(any(RequestInstallationCodeRequest.class)))
                .thenReturn(mockCodeCall);
        when(mockCodeCall.execute()).thenReturn(
                Response.error(500, ResponseBody.create(MediaType.parse("application/json"), "Internal Server Error")));

        // Act & Assert
        assertThrows(PinodeskApiException.class, () -> retrofitApiService.requestInstallationCode(email));
    }

    @Test
    void testRegisterInstallation_Success() throws Exception {
        // Arrange
        RegisterInstallationRequest request = new RegisterInstallationRequest();
        request.setEmail("test@example.com");
        request.setInstallationRequestId("request-id-123");
        request.setInstallationCode("CODE123");
        request.setReleasePlatform("windows");
        request.setReleaseVersion("1.0.0");
        request.setDeviceSignature("device-signature-abc");

        String expectedToken = "installation-token-xyz";
        String expectedInstallationId = "installation-id-456";

        RegisterInstallationResponse expectedResponse = new RegisterInstallationResponse();
        expectedResponse.setInstallationId(expectedInstallationId);
        expectedResponse.setInstallationToken(expectedToken);
        expectedResponse.setDeviceSignature("device-signature-abc");
        expectedResponse.setRegisteredAt(LocalDateTime.now());

        PinodeskApiResponse<RegisterInstallationResponse> apiResponse = new PinodeskApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setData(expectedResponse);

        when(mockApiInterface.registerInstallation(any(RegisterInstallationRequest.class)))
                .thenReturn(mockRegisterCall);
        when(mockRegisterCall.execute()).thenReturn(Response.success(apiResponse));

        // Act
        RegisterInstallationResponse result = retrofitApiService.registerInstallation(request);

        // Assert
        assertNotNull(result);
        assertEquals(expectedInstallationId, result.getInstallationId());
        assertEquals(expectedToken, result.getInstallationToken());
        verify(mockApiInterface).registerInstallation(any(RegisterInstallationRequest.class));
        verify(mockRegisterCall).execute();

        // Verify that the token was set for subsequent requests
        assertEquals(expectedToken, ReflectionTestUtils.getField(retrofitApiService, "currentInstallationToken"));
    }

    @Test
    void testRegisterInstallation_ApiError() throws Exception {
        // Arrange
        RegisterInstallationRequest request = new RegisterInstallationRequest();
        request.setEmail("test@example.com");
        request.setInstallationRequestId("request-id-123");
        request.setInstallationCode("INVALID_CODE");

        PinodeskApiResponse<RegisterInstallationResponse> apiResponse = new PinodeskApiResponse<>();
        apiResponse.setSuccess(false);

        PinodeskApiError error = new PinodeskApiError();
        error.setCode("INVALID_CODE");
        error.setMessage("Invalid installation code");
        apiResponse.setError(error);

        when(mockApiInterface.registerInstallation(any(RegisterInstallationRequest.class)))
                .thenReturn(mockRegisterCall);
        when(mockRegisterCall.execute()).thenReturn(Response.success(apiResponse));

        // Act & Assert
        assertThrows(PinodeskApiException.class, () -> retrofitApiService.registerInstallation(request));
    }

    @Test
    void testRegisterInstallation_HttpError() throws Exception {
        // Arrange
        RegisterInstallationRequest request = new RegisterInstallationRequest();
        request.setEmail("test@example.com");

        when(mockApiInterface.registerInstallation(any(RegisterInstallationRequest.class)))
                .thenReturn(mockRegisterCall);
        when(mockRegisterCall.execute()).thenReturn(
                Response.error(400, ResponseBody.create(MediaType.parse("application/json"), "Bad Request")));

        // Act & Assert
        assertThrows(PinodeskApiException.class, () -> retrofitApiService.registerInstallation(request));
    }

    @Test
    void testSetInstallationToken() {
        // Arrange
        String testToken = "test-token-123";

        // Act
        retrofitApiService.setInstallationToken(testToken);

        // Assert
        assertEquals(testToken, ReflectionTestUtils.getField(retrofitApiService, "currentInstallationToken"));
    }

    @Test
    void testClearInstallationToken() {
        // Arrange
        retrofitApiService.setInstallationToken("test-token-123");

        // Act
        retrofitApiService.clearInstallationToken();

        // Assert
        assertNull(ReflectionTestUtils.getField(retrofitApiService, "currentInstallationToken"));
    }

    @Test
    @SuppressWarnings("deprecation")
    void testDeprecatedMethods() {
        // Arrange
        com.pinodesk.apimodel.ActivateReleaseRequest activateRequest = new com.pinodesk.apimodel.ActivateReleaseRequest();
        com.pinodesk.apimodel.CreateIssueRequest issueRequest = new com.pinodesk.apimodel.CreateIssueRequest();

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> retrofitApiService.activateRelease(activateRequest));

        assertThrows(UnsupportedOperationException.class, () -> retrofitApiService.createIssue(issueRequest));
    }
}