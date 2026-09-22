package com.pinodesk.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinodesk.apimodel.RegisterInstallationRequest;
import com.pinodesk.apimodel.RegisterInstallationResponse;
import com.pinodesk.apimodel.RequestInstallationCodeResponse;
import com.pinodesk.exception.DefaultRuntimeException;
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
        loadOrCreateInstanceId();
    }

    public synchronized String loadOrCreateInstanceId() {
        File installationFile = applicationProperties.getInstallationFile().toFile();
        if (installationFile.exists() && installationFile.isFile()) {
            try {
                JsonNode root = objectMapper.readTree(installationFile);
                if (root != null && root.hasNonNull("instance_id")) {
                    String instanceId = root.get("instance_id").asText();
                    if (!instanceId.trim().isEmpty()) {
                        log.info("Loaded instance ID from {}: {}", installationFile.getAbsolutePath(), instanceId);
                        return instanceId;
                    }
                }
            } catch (IOException e) {
                log.warn("Failed to read installation file at {}", installationFile.getAbsolutePath(), e);
            }
        }

        String newInstanceId = UUID.randomUUID().toString();
        saveInstanceIdToFile(newInstanceId, installationFile);
        log.info("Generated new instance ID: {}", newInstanceId);
        return newInstanceId;
    }

    public synchronized String getInstanceId() {
        return loadOrCreateInstanceId();
    }

    public RequestInstallationCodeResponse requestInstallationCode(String email) {
        return pinodeskRetrofitApiService.requestInstallationCode(email);
    }

    public RegisterInstallationResponse registerInstallation(
            String email,
            String installationRequestId,
            String installationCode) {
        RegisterInstallationRequest req = new RegisterInstallationRequest();
        req.setEmail(email);
        req.setInstallationRequestId(installationRequestId);
        req.setInstallationCode(installationCode);
        req.setReleasePlatform(applicationProperties.getReleasePlatform());
        req.setReleaseVersion(applicationProperties.getAppVersion());
        req.setInstanceId(loadOrCreateInstanceId());
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
            updateInstallationData(response);
        }
        return response;
    }

    public synchronized void updateInstallationData(RegisterInstallationResponse response) {
        File installationFile = applicationProperties.getInstallationFile().toFile();
        try {
            if (installationFile.getParentFile() != null) {
                Files.createDirectories(installationFile.getParentFile().toPath());
            }
            if (response.getInstanceId() == null || response.getInstanceId().isBlank()) {
                response.setInstanceId(loadOrCreateInstanceId());
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(installationFile, response);
            log.info("Installation data saved to: {}", installationFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to save installation data to file", e);
            throw new DefaultRuntimeException(e);
        }
    }

    public RegisterInstallationResponse getInstallationData() {
        File installationFile = applicationProperties.getInstallationFile().toFile();
        if (installationFile.exists() && installationFile.isFile()) {
            try {
                return objectMapper.readValue(installationFile, RegisterInstallationResponse.class);
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

    private void saveInstanceIdToFile(String instanceId, File installationFile) {
        try {
            if (installationFile.getParentFile() != null) {
                Files.createDirectories(installationFile.getParentFile().toPath());
            }
            Map<String, Object> data = new LinkedHashMap<>();
            if (installationFile.exists() && installationFile.isFile()) {
                try {
                    data = objectMapper.readValue(installationFile, new TypeReference<Map<String, Object>>() {
                    });
                } catch (Exception e) {
                    log.warn("Could not parse existing installation file to map, creating fresh map", e);
                }
            }
            data.put("instance_id", instanceId);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(installationFile, data);
            log.info("Instance ID saved to: {}", installationFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to save instance ID to file", e);
            throw new DefaultRuntimeException(e);
        }
    }

    private String defaultNullUnknown(String val) {
        return "unknown".equalsIgnoreCase(val) ? null : val;
    }

}
