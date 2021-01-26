package com.getkembang.kembangdesktop.repository;

import com.getkembang.kembangdesktop.domain.Configuration;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

@Repository
public class ConfigurationRepositoryImpl extends AbstractRepository<Configuration> implements ConfigurationRepository {
    
}
