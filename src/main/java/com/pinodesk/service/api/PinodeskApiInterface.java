package com.pinodesk.service.api;

import com.pinodesk.apimodel.PinodeskApiResponse;
import com.pinodesk.apimodel.RequestInstallationCodeRequest;
import com.pinodesk.apimodel.RequestInstallationCodeResponse;
import com.pinodesk.apimodel.RegisterInstallationRequest;
import com.pinodesk.apimodel.RegisterInstallationResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PinodeskApiInterface {

    @POST("api/v1/installations/code")
    Call<PinodeskApiResponse<RequestInstallationCodeResponse>> requestInstallationCode(
            @Body RequestInstallationCodeRequest request);

    @POST("api/v1/installations")
    Call<PinodeskApiResponse<RegisterInstallationResponse>> registerInstallation(
            @Body RegisterInstallationRequest request);
}