package pinodesk.service.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kong.unirest.core.GenericType;
import kong.unirest.core.HeaderNames;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import kong.unirest.core.UnirestParsingException;
import lombok.extern.slf4j.Slf4j;
import pinodesk.apimodel.ActivateReleaseResponse;
import pinodesk.apimodel.PinodeskApiError;
import pinodesk.apimodel.PinodeskApiResponse;
import pinodesk.exception.PinodeskApiException;
import pinodesk.apimodel.ActivateReleaseRequest;

@Slf4j
@Service
public class PinodeskApiService extends BaseApiService {

    @Value("${pinodesk.api.base_url}")
    private String baseURL;

    @Value("${pinodesk.api.key}")
    private String apiKey;

    private static final String PATH_ACTIVATE_RELEASE = "/v1/releases/activate";

    public ActivateReleaseResponse activateRelease(ActivateReleaseRequest req) {
        String url = baseURL + PATH_ACTIVATE_RELEASE;
        try {
            PinodeskApiResponse<ActivateReleaseResponse> pinodeskResponse = Unirest.post(url)
                    .header(HeaderNames.CONTENT_TYPE, "application/json")
                    .header(HeaderNames.AUTHORIZATION, "Bearer " + apiKey).body(req)
                    .asObject(new GenericType<PinodeskApiResponse<ActivateReleaseResponse>>() {
                    }).ifFailure(r -> r.getParsingError().ifPresent(e -> {
                        // Parsing errors are not thrown (won't come in to the catch clause), we need to
                        // handle this exception manually
                        throw e;
                    })).getBody();
            if (!pinodeskResponse.isSuccess()) {
                PinodeskApiError error = pinodeskResponse.getError();
                throw new PinodeskApiException(error.getCode(), error.getMessage());
            }
            return pinodeskResponse.getData();
        } catch (UnirestParsingException e) {
            throw e; // This is unexpected error (bug), need to check and fix the parsing class
        } catch (UnirestException e) {
            throw new PinodeskApiException(null, "Failed to connect to Pinodesk server! Please try again later");
        }
    }

}
