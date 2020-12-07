package com.gitlab.muhammadkholidb.bianglala.service;

import com.gitlab.muhammadkholidb.bianglala.domain.Configuration;
import com.gitlab.muhammadkholidb.bianglala.repository.ConfigurationRepository;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.sql.Where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService extends BaseService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Cacheable("configurationByCode")
    public String getConfiguration(String code) {
        return configurationRepository.readOne(new Where().equals(Configuration.C_CODE, code))
                .map(Configuration::getValue).orElse(null);
    }

}
