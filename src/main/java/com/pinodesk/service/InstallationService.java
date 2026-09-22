package com.pinodesk.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinodesk.apimodel.RegisterInstallationRequest;
import com.pinodesk.apimodel.RegisterInstallationResponse;
import com.pinodesk.apimodel.RequestInstallationCodeResponse;
import com.pinodesk.exception.DefaultRuntimeException;
import com.pinodesk.model.InstallationData;
import com.pinodesk.properties.ApplicationProperties;
import com.pinodesk.service.api.PinodeskRetrofitApiService;
import com.pinodesk.util.DeviceUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InstallationService extends BaseService {

    @Autowired
    private PinodeskRetrofitApiService pinodeskRetrofitApiService;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        ensureInstallationDataExists();
    }

    public synchronized InstallationData ensureInstallationDataExists() {
        File installationFile = applicationProperties.getInstallationFile().toFile();
        if (installationFile.exists() && installationFile.isFile()) {
            try {
                InstallationData data = objectMapper.readValue(installationFile, InstallationData.class);
                if (data != null && data.getInstanceId() != null && !data.getInstanceId().trim().isEmpty()) {
                    log.info(
                            "Loaded installation data from {}: instanceId={}",
                            installationFile.getAbsolutePath(),
                            data.getInstanceId());
                    return data;
                }
            } catch (IOException e) {
                log.warn("Failed to read installation file at {}", installationFile.getAbsolutePath(), e);
            }
        }

        String newInstanceId = UUID.randomUUID().toString();
        Instant firstRunAt = Instant.now();
        InstallationData data = new InstallationData();
        data.setInstanceId(newInstanceId);
        data.setFirstRunAt(firstRunAt);
        saveInstallationData(data, installationFile);
        log.info("Generated new installation data: instanceId={}", newInstanceId);
        return data;
    }

    public RequestInstallationCodeResponse requestInstallationCode(String email) {
        return pinodeskRetrofitApiService.requestInstallationCode(email);
    }

    public RegisterInstallationResponse registerInstallation(
            String email,
            String installationRequestId,
            String installationCode) {
        InstallationData installationData = ensureInstallationDataExists();

        RegisterInstallationRequest req = new RegisterInstallationRequest();
        req.setEmail(email);
        req.setInstallationRequestId(installationRequestId);
        req.setInstallationCode(installationCode);
        req.setReleasePlatform(applicationProperties.getReleasePlatform());
        req.setReleaseVersion(applicationProperties.getAppVersion());
        req.setInstanceId(installationData.getInstanceId());
        req.setFirstRunAt(installationData.getFirstRunAt());
        req.setDeviceManufacturer(defaultNullUnknown(DeviceUtils.getDeviceManufacturer()));
        req.setDeviceModel(defaultNullUnknown(DeviceUtils.getDeviceModel()));
        req.setOsName(defaultNullUnknown(DeviceUtils.getOsName()));
        req.setOsVersion(defaultNullUnknown(DeviceUtils.getOsVersion()));
        req.setOsFamily(defaultNullUnknown(DeviceUtils.getOsFamily()));
        req.setOsArch(defaultNullUnknown(DeviceUtils.getOsArch()));
        req.setOsBitness(DeviceUtils.getOsBitness());
        req.setCpuName(defaultNullUnknown(DeviceUtils.getCpuName()));
        req.setCpuVendor(defaultNullUnknown(DeviceUtils.getCpuVendor()));
        req.setCpuFamily(defaultNullUnknown(DeviceUtils.getCpuFamily()));
        req.setRamSize(DeviceUtils.getRamSize());
        req.setStorageSize(DeviceUtils.getStorageSize());

        RegisterInstallationResponse response = pinodeskRetrofitApiService.registerInstallation(req);
        if (response != null) {
            InstallationData data = convertResponseToData(response);
            saveInstallationData(data);
        }
        return response;
    }

    public InstallationData getInstallationData() {
        File installationFile = applicationProperties.getInstallationFile().toFile();
        if (installationFile.exists() && installationFile.isFile()) {
            try {
                return objectMapper.readValue(installationFile, InstallationData.class);
            } catch (IOException e) {
                log.error("Failed to read installation data", e);
            }
        }
        return null;
    }

    public boolean isRegistered() {
        File installationFile = applicationProperties.getInstallationFile().toFile();
        if (!installationFile.exists() || !installationFile.isFile()) {
            return false;
        }
        try {
            JsonNode root = objectMapper.readTree(installationFile);
            if (root != null && root.hasNonNull("installation_id")) {
                String installationId = root.get("installation_id").asText();
                return !installationId.trim().isEmpty();
            }
        } catch (Exception e) {
            log.error("Failed to check installation registration status", e);
        }
        return false;
    }

    private void saveInstallationData(InstallationData data) {
        File installationFile = applicationProperties.getInstallationFile().toFile();
        saveInstallationData(data, installationFile);
    }

    private void saveInstallationData(InstallationData data, File installationFile) {
        try {
            if (installationFile.getParentFile() != null) {
                Files.createDirectories(installationFile.getParentFile().toPath());
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(installationFile, data);
            log.info("Installation data saved to: {}", installationFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to save installation data to file", e);
            throw new DefaultRuntimeException(e);
        }
    }

    private String defaultNullUnknown(String val) {
        return "unknown".equalsIgnoreCase(val) ? null : val;
    }

    private InstallationData convertResponseToData(RegisterInstallationResponse response) {
        InstallationData data = new InstallationData();
        data.setInstanceId(response.getInstanceId());
        data.setInstallationId(response.getInstallationId());
        data.setInstallationToken(response.getInstallationToken());
        data.setEmail(response.getEmail());
        data.setRegisteredAt(response.getRegisteredAt());
        data.setFirstRunAt(response.getFirstRunAt());
        return data;
    }

}
