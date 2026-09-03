package com.pinodesk.service.api;

import org.springframework.stereotype.Service;

import com.pinodesk.apimodel.ActivateReleaseRequest;
import com.pinodesk.apimodel.ActivateReleaseResponse;
import com.pinodesk.apimodel.CreateIssueRequest;
import com.pinodesk.apimodel.CreateIssueResponse;

@Service
public class PinodeskApiService extends PinodeskBaseApiService {

    private static final String PATH_ACTIVATE_RELEASE = "/v1/activation-devices";
    private static final String PATH_CREATE_ISSUES = "/v1/issues";

    public ActivateReleaseResponse activateRelease(ActivateReleaseRequest req) {
        return post(PATH_ACTIVATE_RELEASE, req, ActivateReleaseResponse.class);
    }

    public CreateIssueResponse createIssue(CreateIssueRequest req) {
        return post(PATH_CREATE_ISSUES, req, CreateIssueResponse.class);
    }

}
