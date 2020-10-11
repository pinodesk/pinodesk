package com.gitlab.muhammadkholidb.bianglala.service;

import java.util.Properties;

import com.gitlab.muhammadkholidb.bianglala.data.repository.ConfigurationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    public Properties getConfigurationAsProperties() {
        Properties prop = new Properties();
        configurationRepository.read().forEach(config -> prop.setProperty(config.getCode(), config.getValue()));
        return prop;
    }

}
