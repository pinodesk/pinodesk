package pinodesk.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import kong.unirest.core.HeaderNames;
import kong.unirest.core.HttpMethod;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import pinodesk.apimodel.PinodeskApiError;
import pinodesk.apimodel.PinodeskApiResponse;
import pinodesk.constant.MessageCode;
import pinodesk.exception.DefaultRuntimeException;
import pinodesk.exception.PinodeskApiException;

public class PinodeskBaseApiService extends BaseApiService {

    @Value("${pinodesk.api.base_url}")
    private String baseURL;

    @Value("${pinodesk.api.key}")
    private String apiKey;

    @Autowired
    private ObjectMapper mapper;

    protected <T> T post(String path, Object req, Class<T> dataClass) {
        return request(HttpMethod.POST, path, req, dataClass);
    }

    private <T> T request(HttpMethod method, String path, Object req, Class<T> dataClass) {
        try {
            String response = Unirest.request(method.toString(), baseURL + path)
                    .header(HeaderNames.CONTENT_TYPE, "application/json")
                    .header(HeaderNames.AUTHORIZATION, "Bearer " + apiKey).body(req).asString().getBody();
            PinodeskApiResponse<T> pinodeskResponse = parseResponse(response, dataClass);
            if (!pinodeskResponse.isSuccess()) {
                PinodeskApiError error = pinodeskResponse.getError();
                throw new PinodeskApiException(error.getCode(), error.getMessage(), null);
            }
            return pinodeskResponse.getData();
        } catch (JsonProcessingException e) {
            // This is unexpected error (bug), need to check and fix the parsing class
            throw new DefaultRuntimeException(e);
        } catch (UnirestException e) {
            throw new PinodeskApiException(null, null, MessageCode.ERROR_REQUEST_PINODESK);
        }
    }

    private <T> PinodeskApiResponse<T> parseResponse(String response, Class<T> dataClass)
            throws JsonProcessingException {
        JavaType type = mapper.getTypeFactory().constructParametricType(PinodeskApiResponse.class, dataClass);
        return mapper.readValue(response, type);
    }

}
