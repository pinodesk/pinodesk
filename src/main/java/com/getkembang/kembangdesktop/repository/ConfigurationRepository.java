package com.getkembang.kembangdesktop.repository;

import com.getkembang.kembangdesktop.domain.Configuration;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface ConfigurationRepository extends CommonRepository<Configuration> {
    
    Integer updateConfigurationByCode(String code, String value);

}
