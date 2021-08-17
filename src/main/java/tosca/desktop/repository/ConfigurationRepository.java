package tosca.desktop.repository;

import tosca.desktop.domain.Configuration;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface ConfigurationRepository extends CommonRepository<Configuration> {
    
    Integer updateConfigurationByCode(String code, String value);

}
