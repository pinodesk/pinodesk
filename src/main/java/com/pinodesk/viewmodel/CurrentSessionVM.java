package com.pinodesk.viewmodel;

import java.util.List;
import java.util.Map;

import com.pinodesk.constant.ConfigurationConstants;
import com.pinodesk.constant.SimpleStatus;

import lombok.Data;

@Data
public class CurrentSessionVM {
    private SessionVM session;
    private UserVM user;
    private UserGroupVM userGroup;
    private List<UserGroupMenuVM> userGroupMenus;
    private Map<String, String> configurationMap;

    public boolean isPharmacyFeatureEnabled() {
        return SimpleStatus.YES.toString()
                .equals(configurationMap.get(ConfigurationConstants.PHARMACY_FEATURES_ENABLED));
    }
}
