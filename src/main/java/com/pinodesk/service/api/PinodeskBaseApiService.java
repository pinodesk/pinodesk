package com.pinodesk.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinodesk.apimodel.PinodeskApiError;
import com.pinodesk.apimodel.PinodeskApiResponse;
import com.pinodesk.constant.MessageCode;
import com.pinodesk.exception.DefaultRuntimeException;
import com.pinodesk.exception.PinodeskApiException;
import com.pinodesk.util.DeviceUtils;

import kong.unirest.core.HeaderNames;
import kong.unirest.core.HttpMethod;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PinodeskBaseApiService extends BaseApiService {

    @Value("${pinodesk.api.base_url}")
    private String baseURL;

    @Value("${pinodesk.api.key}")
    private String apiKey;

    @Autowired
    private ObjectMapper mapper;

    protected static final String HEADER_PINODESK_API_KEY = "X-Pinodesk-Api-Key";
    protected static final String HEADER_PINODESK_DEVICE_SIGNATURE = "X-Pinodesk-Device-Signature";

    protected <T> T post(String path, Object req, Class<T> dataClass) {
        return request(HttpMethod.POST, path, req, dataClass);
    }

    private <T> T request(HttpMethod method, String path, Object req, Class<T> dataClass) {
        String url = baseURL + path;
        try {
            String response = Unirest.request(method.toString(), url)
                    .header(HeaderNames.CONTENT_TYPE, "application/json").header(HEADER_PINODESK_API_KEY, apiKey)
                    .header(HEADER_PINODESK_DEVICE_SIGNATURE, DeviceUtils.getDeviceSignature()).body(req).asString()
                    .getBody();
            PinodeskApiResponse<T> pinodeskResponse = parseResponse(response, dataClass);
            if (!pinodeskResponse.isSuccess()) {
                PinodeskApiError error = pinodeskResponse.getError();
                throw new PinodeskApiException(error.getCode(), error.getMessage(), null);
            }
            return pinodeskResponse.getData();
        } catch (JsonProcessingException e) {
            log.error("Error processing response as JSON", e);
            // This is unexpected error (bug), need to check and fix the parsing class
            throw new DefaultRuntimeException(e);
        } catch (UnirestException e) {
            log.error(String.format("Error on the request: %s %s", method, url), e);
            throw new PinodeskApiException(null, null, MessageCode.ERROR_REQUEST_PINODESK);
        }
    }

    private <T> PinodeskApiResponse<T> parseResponse(String response, Class<T> dataClass)
            throws JsonProcessingException {
        JavaType type = mapper.getTypeFactory().constructParametricType(PinodeskApiResponse.class, dataClass);
        return mapper.readValue(response, type);
    }

}
