package pinodesk.service.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;

import org.springframework.beans.factory.annotation.Value;

import com.mudiatech.toolbox.jackson.JSON;

import lombok.extern.slf4j.Slf4j;
import pinodesk.apimodel.PinodeskApiError;
import pinodesk.apimodel.PinodeskApiResponse;
import pinodesk.constant.MessageCode;
import pinodesk.exception.DefaultRuntimeException;
import pinodesk.exception.PinodeskApiException;

@Slf4j
public class PinodeskBaseApiService extends BaseApiService {

    @Value("${pinodesk.api.base_url}")
    private String baseURL;

    @Value("${pinodesk.api.key}")
    private String apiKey;

    private HttpClient httpClient = createHttpClient();

    private HttpClient createHttpClient() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, new SecureRandom());
            HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext).build();
            return httpClient;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("Error creating HttpClient", e);
            throw new DefaultRuntimeException(e);
        }
    }

    protected <T> T post(String path, Object req, Class<T> dataClass) {
        return request("POST", path, req, dataClass);
    }

    private <T> T request(String method, String path, Object body, Class<T> dataClass) {
        String url = baseURL + path;
        try {
            String json = JSON.stringify(body);
            log.debug("JSON body: {}", json);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json").header("Authorization", "Bearer " + apiKey)
                    .method(method, BodyPublishers.ofString(json)).build();
            log.debug("Sending data to: {}", url);
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            log.debug("Response body: {}", response.body());
            PinodeskApiResponse<T> pinodeskResponse = JSON.parse(response.body(), PinodeskApiResponse.class, dataClass);
            if (response.statusCode() != 200) {
                log.error("Response status code: {}", response.statusCode());
                PinodeskApiError error = pinodeskResponse.getError();
                throw new PinodeskApiException(error.getCode(), error.getMessage(), null);
            }
            return pinodeskResponse.getData();
        } catch (IOException | InterruptedException | IllegalArgumentException | SecurityException e) {
            log.error(String.format("Error on the request: %s %s", method, url), e);
            throw new PinodeskApiException(null, null, MessageCode.ERROR_REQUEST_PINODESK);
        }
    }

}
