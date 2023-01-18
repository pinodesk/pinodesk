package pospino.desktop.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class ApplicationProperties {

    @Value("${app.name}")
    private String name;

    @Value("${app.version}")
    private String version;
}
