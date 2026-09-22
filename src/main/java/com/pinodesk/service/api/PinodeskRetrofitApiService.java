package com.pinodesk.service.api;

import org.springframework.stereotype.Service;

import com.pinodesk.apimodel.RegisterInstallationRequest;
import com.pinodesk.apimodel.RegisterInstallationResponse;
import com.pinodesk.apimodel.RequestInstallationCodeRequest;
import com.pinodesk.apimodel.RequestInstallationCodeResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PinodeskRetrofitApiService extends PinodeskRetrofitBaseService {

    public RequestInstallationCodeResponse requestInstallationCode(String email) {
        RequestInstallationCodeRequest request = new RequestInstallationCodeRequest();
        request.setEmail(email);
        log.info("Requesting installation code for email: {}", email);
        return executeCall(apiInterface.requestInstallationCode(request));
    }

    public RegisterInstallationResponse registerInstallation(RegisterInstallationRequest req) {
        log.info("Registering installation for email: {}", req.getEmail());
        RegisterInstallationResponse response = executeCall(apiInterface.registerInstallation(req));

        // Set the installation token for subsequent requests
        if (response != null && response.getInstallationToken() != null) {
            setInstallationToken(response.getInstallationToken());
            log.info("Installation token received and set for subsequent requests");
        }

        return response;
    }
}