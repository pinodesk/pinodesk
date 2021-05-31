package com.getkembang.kembangdesktop.repository;

import com.getkembang.kembangdesktop.domain.Configuration;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.stereotype.Repository;

@Repository
public class ConfigurationRepositoryImpl extends AbstractRepository<Configuration> implements ConfigurationRepository {

    @Override
    public Integer updateConfigurationByCode(String code, String value) {
        return update(new String[] { Configuration.C_VALUE }, new Object[] { value },
                new Where().equals(Configuration.C_CODE, code));
    }

}
