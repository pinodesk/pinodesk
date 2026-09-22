package com.pinodesk.properties;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class ApplicationProperties {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @Value("${app.home}")
    private String appHome;

    @Value("${release.platform}")
    private String releasePlatform;

    public Path getAppHomePath() {
        return Path.of(appHome);
    }

    public Path getDatabasePath() {
        return getAppHomePath().resolve("db").resolve("pinodesk");
    }

    public Path getInstallationFile() {
        return getAppHomePath().resolve("installation.json");
    }
}
