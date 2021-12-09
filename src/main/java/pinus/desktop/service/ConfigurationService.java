package pinus.desktop.service;

import java.util.Map;
import java.util.stream.Collectors;

import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.domain.Configuration;
import pinus.desktop.repository.ConfigurationRepository;

@Service
public class ConfigurationService extends BaseService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Cacheable(CacheNameConstants.CONFIGURATION_BY_CODE)
    public String getConfiguration(String code) {
        return configurationRepository.readOne(new Where().equals(Configuration.C_CODE, code))
                .map(Configuration::getValue).orElse(null);
    }

    @Cacheable(CacheNameConstants.CONFIGURATION_MAP)
    public Map<String, String> getConfigurationMap() {
        return configurationRepository.read().stream()
                .collect(Collectors.toMap(Configuration::getCode, Configuration::getValue));
    }

    @Transactional
    public void updateConfiguration(Map<String, String> configurationMap) {
        configurationMap.entrySet()
                .forEach(entry -> configurationRepository.updateConfigurationByCode(entry.getKey(), entry.getValue()));
        evictAllCaches();
    }

    private void evictAllCaches() {
        cacheManager.getCacheNames().stream().forEach(name -> cacheManager.getCache(name).clear());
    }

}
