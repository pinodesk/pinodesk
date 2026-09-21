package com.pinodesk.service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinodesk.apimodel.PinodeskApiError;
import com.pinodesk.apimodel.PinodeskApiResponse;
import com.pinodesk.constant.MessageCode;
import com.pinodesk.exception.PinodeskApiException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PinodeskRetrofitBaseService {

    @Value("${pinodesk.api.base_url}")
    private String baseURL;

    @Autowired
    private ObjectMapper mapper;

    protected static final String HEADER_PINODESK_INSTALLATION_TOKEN = "X-Pinodesk-Installation-Token";

    protected PinodeskApiInterface apiInterface;
    protected String currentInstallationToken;

    @PostConstruct
    protected void init() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
                message -> log.debug("HTTP Request: {}", message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request.Builder requestBuilder = original.newBuilder()
                            .header("Content-Type", "application/json");

                    // Add installation token if available
                    if (currentInstallationToken != null && !currentInstallationToken.isEmpty()) {
                        requestBuilder.header(HEADER_PINODESK_INSTALLATION_TOKEN, currentInstallationToken);
                    }

                    okhttp3.Request request = requestBuilder.method(original.method(), original.body()).build();
                    return chain.proceed(request);
                }).connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseURL).client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(mapper)).build();

        apiInterface = retrofit.create(PinodeskApiInterface.class);
    }

    protected void setInstallationToken(String token) {
        this.currentInstallationToken = token;
        log.info("Installation token set for subsequent requests");
    }

    protected void clearInstallationToken() {
        this.currentInstallationToken = null;
        log.info("Installation token cleared");
    }

    protected <T> T executeCall(Call<PinodeskApiResponse<T>> call) {
        try {
            Response<PinodeskApiResponse<T>> response = call.execute();
            if (!response.isSuccessful()) {
                log.error("HTTP error: {} {}", response.code(), response.message());
                throw new PinodeskApiException(null, null, MessageCode.ERROR_REQUEST_PINODESK);
            }

            PinodeskApiResponse<T> pinodeskResponse = response.body();
            if (pinodeskResponse == null) {
                log.error("Empty response body");
                throw new PinodeskApiException(null, null, MessageCode.ERROR_REQUEST_PINODESK);
            }

            if (!pinodeskResponse.isSuccess()) {
                PinodeskApiError error = pinodeskResponse.getError();
                throw new PinodeskApiException(error.getCode(), error.getMessage(), null);
            }

            return pinodeskResponse.getData();
        } catch (IOException e) {
            log.error("IO error executing API call", e);
            throw new PinodeskApiException(null, null, MessageCode.ERROR_REQUEST_PINODESK);
        } catch (Exception e) {
            if (e instanceof PinodeskApiException) {
                throw e;
            }
            log.error("Error executing API call", e);
            throw new PinodeskApiException(null, null, MessageCode.ERROR_REQUEST_PINODESK);
        }
    }
}