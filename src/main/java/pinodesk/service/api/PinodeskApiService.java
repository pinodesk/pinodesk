package pinodesk.service.api;

import org.springframework.stereotype.Service;

import pinodesk.apimodel.ActivateReleaseRequest;
import pinodesk.apimodel.ActivateReleaseResponse;
import pinodesk.apimodel.CreateIssueRequest;
import pinodesk.apimodel.CreateIssueResponse;

@Service
public class PinodeskApiService extends PinodeskBaseApiService {

    private static final String PATH_ACTIVATE_RELEASE = "/v1/releases/activate";
    private static final String PATH_CREATE_ISSUES = "/v1/issues";

    public ActivateReleaseResponse activateRelease(ActivateReleaseRequest req) {
        return post(PATH_ACTIVATE_RELEASE, req, ActivateReleaseResponse.class);
    }

    public CreateIssueResponse createIssue(CreateIssueRequest req) {
        return post(PATH_CREATE_ISSUES, req, CreateIssueResponse.class);
    }

}
