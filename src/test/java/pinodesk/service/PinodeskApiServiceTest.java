package pinodesk.service;

import org.junit.jupiter.api.Test;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PinodeskApiServiceTest extends BaseServiceTest {

    @Test
    void testGetSystemInfo() {
        try {
            HttpResponse<String> response = Unirest.get("https://api.pinodesk.com/v1/system/info").asString();
            log.debug("response.getBody() : {}", response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
