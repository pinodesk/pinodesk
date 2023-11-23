package pospino.desktop.service.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kong.unirest.core.GenericType;
import kong.unirest.core.HeaderNames;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import kong.unirest.core.UnirestParsingException;
import lombok.extern.slf4j.Slf4j;
import pospino.desktop.apimodel.ActivateReleaseRequest;
import pospino.desktop.apimodel.ActivateReleaseResponse;
import pospino.desktop.apimodel.PospinoApiError;
import pospino.desktop.apimodel.PospinoApiResponse;
import pospino.desktop.exception.PospinoApiException;

@Slf4j
@Service
public class PospinoApiService extends BaseApiService {

    @Value("${pospino.api.base_url}")
    private String baseURL;

    @Value("${pospino.api.key}")
    private String apiKey;

    private static final String PATH_ACTIVATE_RELEASE = "/v1/releases/activate";

    public ActivateReleaseResponse activateRelease(ActivateReleaseRequest req) {
        String url = baseURL + PATH_ACTIVATE_RELEASE;
        try {
            PospinoApiResponse<ActivateReleaseResponse> pospinoResponse = Unirest.post(url)
                    .header(HeaderNames.CONTENT_TYPE, "application/json")
                    .header(HeaderNames.AUTHORIZATION, "Bearer " + apiKey).body(req)
                    .asObject(new GenericType<PospinoApiResponse<ActivateReleaseResponse>>() {
                    }).ifFailure(r -> r.getParsingError().ifPresent(e -> {
                        // Parsing errors are not thrown (won't come in to the catch clause), we need to
                        // handle this exception manually
                        throw e;
                    })).getBody();
            if (!pospinoResponse.isSuccess()) {
                PospinoApiError error = pospinoResponse.getError();
                throw new PospinoApiException(error.getCode(), error.getMessage());
            }
            return pospinoResponse.getData();
        } catch (UnirestParsingException e) {
            throw e; // This is unexpected error (bug), need to check and fix the parsing class
        } catch (UnirestException e) {
            throw new PospinoApiException(null, "Failed to connect to Pospino server! Please try again later");
        }
    }

}
