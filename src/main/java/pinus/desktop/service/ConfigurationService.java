package pinus.desktop.service;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.annotation.ForActivity;
import pinus.desktop.constant.Activity;
import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.domain.Configuration;
import pinus.desktop.domain.User;
import pinus.desktop.repository.ConfigurationRepository;
import pinus.desktop.repository.UserRepository;
import pinus.desktop.util.PasswordUtils;
import pinus.desktop.viewmodel.UserAddVM;

@Service
public class ConfigurationService extends BaseService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private UserRepository userRepository;

    @ForActivity(Activity.GET_CONFIGURATION_BY_CODE)
    @Cacheable(CacheNameConstants.CONFIGURATION_BY_CODE)
    public String getConfiguration(String code) {
        return configurationRepository.findByCodeAndDeletedAtIsNull(code).map(Configuration::getValue).orElse(null);
    }

    @ForActivity(Activity.GET_CONFIGURATION_MAP)
    @Cacheable(CacheNameConstants.CONFIGURATION_MAP)
    public Map<String, String> getConfigurationMap() {
        return configurationRepository.findByDeletedAtIsNull().stream()
                .collect(Collectors.toMap(Configuration::getCode, Configuration::getValue));
    }

    @CacheEvict(value = { CacheNameConstants.CONFIGURATION_BY_CODE }, allEntries = true)
    @Transactional
    public void saveIntialSetup(Map<String, String> configurationMap, UserAddVM userAdd) {
        updateConfiguration(configurationMap);
        User user = new User();
        user.setFullName(userAdd.getFullName());
        user.setUsername(userAdd.getUsername());
        user.setStatus(userAdd.getStatus().toString());
        user.setUserGroupId(userAdd.getUserGroupId());
        user.setPasswordHash(PasswordUtils.encrypt(userAdd.getPassword()));
        userRepository.save(user);
    }

    @ForActivity(Activity.UPDATE_CONFIGURATION)
    @Transactional
    public void updateConfiguration(Map<String, String> configurationMap) {
        configurationMap.entrySet()
                .forEach(entry -> configurationRepository.updateValueByCode(entry.getKey(), entry.getValue()));
        evictAllCaches();
    }

    private void evictAllCaches() {
        cacheManager.getCacheNames().stream().forEach(name -> cacheManager.getCache(name).clear());
    }

}
