package com.getkembang.kembangdesktop.service;

import java.util.Map;
import java.util.stream.Collectors;

import com.getkembang.kembangdesktop.constant.CacheName;
import com.getkembang.kembangdesktop.domain.Configuration;
import com.getkembang.kembangdesktop.repository.ConfigurationRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigurationService extends BaseService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Cacheable(CacheName.Keys.CONFIGURATION_BY_CODE)
    public String getConfiguration(String code) {
        return configurationRepository.readOne(new Where().equals(Configuration.C_CODE, code))
                .map(Configuration::getValue).orElse(null);
    }

    @Cacheable(CacheName.Keys.CONFIGURATION_MAP)
    public Map<String, String> getConfigurationMap() {
        return configurationRepository.read().stream()
                .collect(Collectors.toMap(Configuration::getCode, Configuration::getValue));
    }

    @CacheEvict(value = { CacheName.Keys.CONFIGURATION_MAP, CacheName.Keys.CONFIGURATION_BY_CODE }, allEntries = true)
    @Transactional
    public void updateConfiguration(Map<String, String> configurationMap) {
        configurationMap.entrySet()
                .forEach(entry -> configurationRepository.updateConfigurationByCode(entry.getKey(), entry.getValue()));
    }

}
