package tosca.desktop.repository;

import java.util.List;

import tosca.desktop.domain.Unit;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface UnitRepository extends CommonRepository<Unit> {
    
    List<Unit> filter(String keyword, int limit);

}
