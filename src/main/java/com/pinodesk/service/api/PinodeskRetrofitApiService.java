package com.pinodesk.service.api;

import com.pinodesk.apimodel.ActivateReleaseRequest;
import com.pinodesk.apimodel.ActivateReleaseResponse;
import com.pinodesk.apimodel.CreateIssueRequest;
import com.pinodesk.apimodel.CreateIssueResponse;
import com.pinodesk.apimodel.RequestInstallationCodeRequest;
import com.pinodesk.apimodel.RequestInstallationCodeResponse;
import com.pinodesk.apimodel.RegisterInstallationRequest;
import com.pinodesk.apimodel.RegisterInstallationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PinodeskRetrofitApiService extends PinodeskRetrofitBaseService {

    // Original methods - kept for reference but no longer used
    @Deprecated
    public ActivateReleaseResponse activateRelease(ActivateReleaseRequest req) {
        throw new UnsupportedOperationException("This method is deprecated and no longer used");
    }

    @Deprecated
    public CreateIssueResponse createIssue(CreateIssueRequest req) {
        throw new UnsupportedOperationException("This method is deprecated and no longer used");
    }

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