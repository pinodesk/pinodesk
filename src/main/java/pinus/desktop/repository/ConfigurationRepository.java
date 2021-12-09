package pinus.desktop.repository;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import pinus.desktop.domain.Configuration;

public interface ConfigurationRepository extends CommonRepository<Configuration> {

    Integer updateConfigurationByCode(String code, String value);

}
