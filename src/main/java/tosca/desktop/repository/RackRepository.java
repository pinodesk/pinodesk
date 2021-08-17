package tosca.desktop.repository;

import java.util.List;

import tosca.desktop.domain.Rack;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface RackRepository extends CommonRepository<Rack> {
    
    List<Rack> filter(String keyword, int limit);

}
