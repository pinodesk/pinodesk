package toscabox.desktop.repository;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import toscabox.desktop.domain.Configuration;

public interface ConfigurationRepository extends CommonRepository<Configuration> {

    Integer updateConfigurationByCode(String code, String value);

}
